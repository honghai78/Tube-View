package shine.tran.tubeview.businessobjects.VideoStream;


import android.util.Log;

import org.json.JSONObject;
import org.jsoup.parser.Parser;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses stream/video meta-data and returns
 */
public class ParseStreamMetaData {

	public String pageUrl;
	private String pageContents;
	private JSONObject jsonObj;
	private JSONObject playerArgs;

	private static final String TAG = ParseStreamMetaData.class.getSimpleName();
	private static final String DECRYPTION_FUNC_NAME="decrypt";

	// cached values
	private static volatile String decryptionCode = "";


	/**
	 * Initialise the object.
	 *
	 * @param videoId	The ID of the video we are going to get its streams.
	 */
	public ParseStreamMetaData(String videoId) {
		setPageUrl(videoId);

		//attempt to load the youtube js player JSON arguments
		try {
			pageContents = HttpDownloader.download(pageUrl);
			String jsonString = matchGroup1("ytplayer.config\\s*=\\s*(\\{.*?\\});", pageContents);
			jsonObj = new JSONObject(jsonString);
			playerArgs = jsonObj.getJSONObject("args");
		} catch (Exception e) {
			// if this fails, the video is most likely not available
			Log.d(TAG, "Could not load JSON data for Youtube video \""+pageUrl+"\". This most likely means the video is unavailable");
		}

		// load and parse description code, if it isn't already initialised
		if (decryptionCode.isEmpty()) {
			try {
				// The Youtube service needs to be initialized by downloading the
				// js-Youtube-player. This is done in order to get the algorithm
				// for decrypting cryptic signatures inside certain stream urls.
				JSONObject ytAssets = jsonObj.getJSONObject("assets");
				String playerUrl = ytAssets.getString("js");

				if (playerUrl.startsWith("//")) {
					playerUrl = "https:" + playerUrl;
				}
				decryptionCode = loadDecryptionCode(playerUrl);
			} catch (Exception e){
				Log.d(TAG, "Could not load decryption code for the Youtube service.");
				e.printStackTrace();
			}
		}
	}


	/**
	 * Returns a list of video/stream meta-data that is supported by this app.
	 *
	 * @return List of {@link StreamMetaData}.
	 */
	public StreamMetaDataList getStreamMetaDataList() throws Exception {
		StreamMetaDataList	streamMetaDataList = new StreamMetaDataList();
		String				encodedUrlMap = playerArgs.getString("url_encoded_fmt_stream_map");
		StreamMetaData		streamMetaData;

		for (String url_data_str : encodedUrlMap.split(",")) {
			Map<String, String> tags = new HashMap<>();

			for (String raw_tag : Parser.unescapeEntities(url_data_str, true).split("&")) {
				String[] split_tag = raw_tag.split("=");
				tags.put(split_tag[0], split_tag[1]);
			}

			int itag = Integer.parseInt(tags.get("itag"));
			String streamUrl = URLDecoder.decode(tags.get("url"), "UTF-8");

			// if video has a signature: decrypt it and add it to the url
			if (tags.get("s") != null) {
				streamUrl = streamUrl + "&signature=" + decryptSignature(tags.get("s"), decryptionCode);
			}

			// contruct the meta-data of the video and add it to the list if it is supported
			streamMetaData = new StreamMetaData(streamUrl, itag);
			if (streamMetaData.getFormat() != MediaFormat.UNKNOWN) {
				streamMetaDataList.add(streamMetaData);
			}
		}

		return streamMetaDataList;
	}



	/**
	 * Given video ID it will set the video's page URL.
	 *
	 * @param videoId	The ID of the video.
	 */
	private void setPageUrl(String videoId) {
		this.pageUrl = "https://www.youtube.com/watch?v=" + videoId;
	}



	private String loadDecryptionCode(String playerUrl) throws Exception {
		String playerCode = HttpDownloader.download(playerUrl);
		String decryptionFuncName = "";
		String decryptionFunc = "";
		String helperObjectName;
		String helperObject = "";
		String callerFunc = "function " + DECRYPTION_FUNC_NAME + "(a){return %%(a);}";
		String decryptionCode;

		try {
			decryptionFuncName = matchGroup1("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(", playerCode);

			String functionPattern = "(var "+  decryptionFuncName.replace("$", "\\$") +"=function\\([a-zA-Z0-9_]*\\)\\{.+?\\})";
			decryptionFunc = matchGroup1(functionPattern, playerCode);
			decryptionFunc += ";";

			helperObjectName = matchGroup1(";([A-Za-z0-9_\\$]{2})\\...\\(", decryptionFunc);

			String helperPattern = "(var " + helperObjectName.replace("$", "\\$") + "=\\{.+?\\}\\};)";
			helperObject = matchGroup1(helperPattern, playerCode);

		} catch (Throwable tr) {
			Log.e(TAG, "loadDecryptionCode error", tr);
		}

		callerFunc = callerFunc.replace("%%", decryptionFuncName);
		decryptionCode = helperObject + decryptionFunc + callerFunc;

		return decryptionCode;
	}


	private String decryptSignature(String encryptedSig, String decryptionCode) {
		Context context = Context.enter();
		context.setOptimizationLevel(-1);
		Object result = null;
		try {
			ScriptableObject scope = context.initStandardObjects();
			context.evaluateString(scope, decryptionCode, "decryptionCode", 1, null);
			Function decryptionFunc = (Function) scope.get("decrypt", scope);
			result = decryptionFunc.call(context, scope, scope, new Object[]{encryptedSig});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Context.exit();
		return result.toString();
	}



	private String matchGroup1(String pattern, String input) {
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher(input);
		boolean foundMatch = mat.find();
		if (foundMatch) {
			return mat.group(1);
		}
		else {
			Log.w(TAG, "failed to find pattern \""+pattern+"\" inside of \""+input+"\"");
			new Exception("failed to find pattern \""+pattern+"\"").printStackTrace();
			return "";
		}
	}
}
