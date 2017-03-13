package shine.tran.localtubeview.businessobjects;

import com.google.api.services.youtube.model.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.gui.activities.MainActivity;
import shine.tran.localtubeview.gui.app.TubeViewApp;

/**
 * Returns a list of YouTube videos.
 *
 * <p>Do not run this directly, but rather use {@link GetYouTubeVideosTask}.</p>
 */
public abstract class GetYouTubeVideos {

	/**
	 * Initialise this object.
	 *
	 * @throws IOException
	 */
	public abstract void init() throws IOException;


	/**
	 * Sets user's query. [optional]
	 */
	public void setQuery(String query) {
	}


	/**
	 * Gets the next page of videos.
	 *
	 * @return List of {@link YouTubeVideo}s.
	 */
	public abstract List<YouTubeVideo> getNextVideos();


	/**
	 * @return True if YouTube states that there will be no more video pages; false otherwise.
	 */
	public abstract boolean noMoreVideoPages();


	/**
	 * Converts {@link List} of {@link Video} to {@link List} of {@link YouTubeVideo}.
	 *
	 * @param videoList {@link List} of {@link Video}.
	 * @return {@link List} of {@link YouTubeVideo}.
	 */
	protected List<YouTubeVideo> toYouTubeVideoList(List<Video> videoList) {
		List<YouTubeVideo> youTubeVideoList = new ArrayList<>();

		if (videoList != null) {
			YouTubeVideo youTubeVideo;

			for (Video video : videoList) {
				youTubeVideo = new YouTubeVideo(video);
				if (!youTubeVideo.filterVideoByLanguage())
					youTubeVideoList.add(youTubeVideo);
			}
		}

		return youTubeVideoList;
	}


	protected String getPreferredRegion() {
		String region = TubeViewApp.getPreferenceManager()
				.getString(TubeViewApp.getStr(R.string.pref_key_preferred_region), "").trim();
		if(region.equals("AUTO")) {
			return MainActivity.COUNTRY_CODE_VALUE;
		}
        else if(region.equals("ALL"))
            return null;
		else
		return (region.isEmpty() ? MainActivity.COUNTRY_CODE_VALUE : region);
	}

}
