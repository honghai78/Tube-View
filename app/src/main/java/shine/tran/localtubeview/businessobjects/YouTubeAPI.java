package shine.tran.localtubeview.businessobjects;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;


/**
 * Represents YouTube API service.
 */
public class YouTubeAPI {

	/**
	 * Returns a new instance of {@link YouTube}.
	 *
	 * @return {@link YouTube}
	 */
	public static YouTube create() {
		HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
		JsonFactory jsonFactory = com.google.api.client.extensions.android.json.AndroidJsonFactory.getDefaultInstance();
		return new YouTube.Builder(httpTransport, jsonFactory, null)
							.setApplicationName("+")
							.build();
	}

}
