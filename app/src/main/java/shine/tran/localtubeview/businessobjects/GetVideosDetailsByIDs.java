package shine.tran.localtubeview.businessobjects;

import java.io.IOException;

/**
 * A class that is able to query YouTube and returns information regarding the supplied videos IDs.
 */
public class GetVideosDetailsByIDs extends GetFeaturedVideos {

	/**
	 * Initialise object.
	 *
	 * @param videoIds		Comma separated videos IDs.
	 * @throws IOException
	 */
	public void init(String videoIds) throws IOException {
		super.init();
		super.videosList.setId(videoIds);
		super.videosList.setChart(null);
	}

}
