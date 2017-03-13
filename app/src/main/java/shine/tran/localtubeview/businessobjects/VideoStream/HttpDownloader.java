package shine.tran.localtubeview.businessobjects.VideoStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Downloads HTTP content.
 */
public class HttpDownloader {

	/** Mimic the Mozilla user agent */
	private static final String USER_AGENT = "Mozilla/5.0";


	/**
	 * Download (via HTTP) the text file located at the supplied URL, and return its contents.
	 * Primarily intended for downloading web pages.
	 *
	 * @param siteUrl	The URL of the text file to download.
	 *
	 * @return	The contents of the specified text file.
	 */
	public static String download(String siteUrl) throws Exception {
		URL					url = new URL(siteUrl);
		HttpURLConnection	con = (HttpURLConnection) url.openConnection();
		StringBuffer		response = new StringBuffer();
		BufferedReader		in = null;

		try {
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} finally {
			if (in != null)
				in.close();

			if (con != null)
				con.disconnect();
		}

		return response.toString();
	}

}
