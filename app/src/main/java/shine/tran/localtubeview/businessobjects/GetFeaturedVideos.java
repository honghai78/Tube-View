package shine.tran.localtubeview.businessobjects;

import android.util.Log;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.List;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.gui.app.TubeViewApp;

/**
 * Get today's featured YouTube videos.
 */
public class GetFeaturedVideos extends GetYouTubeVideos {

	protected YouTube.Videos.List videosList = null;
	private String nextPageToken = null;
	private boolean noMoreVideoPages = false;

	private static final String	TAG = GetFeaturedVideos.class.getSimpleName();
	private static final Long	MAX_RESULTS = 50L;
    public static boolean TEST=false;

	@Override
	public void init() throws IOException {
		videosList = YouTubeAPI.create().videos().list("snippet, statistics, contentDetails");
		videosList.setFields("items(id, snippet/defaultAudioLanguage, snippet/defaultLanguage, snippet/publishedAt, snippet/title, snippet/channelId, snippet/channelTitle," +
				"snippet/thumbnails/high, contentDetails/duration, statistics)," +
				"nextPageToken");
		videosList.setKey(TubeViewApp.getStr(R.string.API_KEY));
		videosList.setChart("mostPopular");
		//videosList.setLocale("10.76262,106.66017");
        if(TEST) videosList.setRegionCode(getPreferredRegion());
				videosList.setMaxResults(MAX_RESULTS);
		nextPageToken = null;
	}


	@Override
	public List<YouTubeVideo> getNextVideos() {
		List<Video> searchResultList = null;

	//
		//videosList.setLocale("10.76262,106.66017");
		if (!noMoreVideoPages()) {
			try {
				// set the page token/id to retrieve
				videosList.setPageToken(nextPageToken);

				// communicate with YouTube
				VideoListResponse response = videosList.execute();

				// get videos
				searchResultList = response.getItems();

				// set the next page token
				nextPageToken = response.getNextPageToken();

				// if nextPageToken is null, it means that there are no more videos
				if (nextPageToken == null)
					noMoreVideoPages = true;
			} catch (IOException e) {
				Log.e(TAG, "Error has occurred while getting Featured Videos.", e);
			}
		}

		return toYouTubeVideoList(searchResultList);
	}


	@Override
	public boolean noMoreVideoPages() {
		return noMoreVideoPages;
	}

}
