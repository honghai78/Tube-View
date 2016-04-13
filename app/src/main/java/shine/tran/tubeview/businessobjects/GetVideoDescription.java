package shine.tran.tubeview.businessobjects;

import java.io.IOException;

/**
 * Queries YouTube and return the description of a video.  The description is set by the YouTuber
 * who uploaded the video.
 */
public class GetVideoDescription extends GetVideosDetailsByIDs {

	/**
	 * Initialise object.
	 *
	 * @param videoId		The video ID to query about.
	 * @throws IOException
	 */
	public void init(String videoId) throws IOException {
		super.init(videoId);
		super.videosList.setFields("items(snippet/description)");
	}

}
