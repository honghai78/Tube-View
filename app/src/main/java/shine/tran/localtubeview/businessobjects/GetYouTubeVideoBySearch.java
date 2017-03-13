package shine.tran.localtubeview.businessobjects;

import android.util.Log;
import android.widget.Toast;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.gui.activities.MainActivity;
import shine.tran.localtubeview.gui.app.TubeViewApp;

/**
 * Get videos corresponding to the user's query (refer to {@link #setQuery(String)}).
 */
public class GetYouTubeVideoBySearch extends GetYouTubeVideos {

    protected YouTube.Search.List videosList = null;
    private String nextPageToken = null;
    private boolean noMoreVideoPages = false;

    private static final String TAG = GetYouTubeVideoBySearch.class.getSimpleName();
    protected static final Long MAX_RESULTS = 45L;


    @Override
    public void init() throws IOException {
        videosList = YouTubeAPI.create().search().list("id");
        videosList.setFields("items(id/videoId), nextPageToken");
        videosList.setKey(TubeViewApp.getStr(R.string.API_KEY));
        //videosList.setLocation("10.76262,106.66017");
        //videosList.setLocationRadius("10000km");
        videosList.setType("video");
//		videosList.setRegionCode(getPreferredRegion());
        videosList.setSafeSearch("none");
        videosList.setMaxResults(MAX_RESULTS);
        nextPageToken = null;
    }

    /**
     * Set the user's query.
     *
     * @param query User's query.
     */
    @Override
    public void setQuery(String query) {
        if (videosList != null)
            videosList.setQ(query);
        Toast.makeText(MainActivity.ACTIVITY, "Search All Worldwide", Toast.LENGTH_LONG).show();
    }


    @Override
    public List<YouTubeVideo> getNextVideos() {
        List<YouTubeVideo> videosList = null;

        if (!noMoreVideoPages()) {
            try {
                // set the page token/id to retrieve
                this.videosList.setPageToken(nextPageToken);

                // communicate with YouTube
                SearchListResponse searchResponse = this.videosList.execute();

                // get videos
                List<SearchResult> searchResultList = searchResponse.getItems();
                if (searchResultList != null) {
                    videosList = getVideosList(searchResultList);
                }

                // set the next page token
                nextPageToken = searchResponse.getNextPageToken();

                // if nextPageToken is null, it means that there are no more videos
                if (nextPageToken == null)
                    noMoreVideoPages = true;
            } catch (IOException ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }

        return videosList;
    }


    /**
     * YouTube's search functionality (i.e. {@link SearchResult} does not return enough information
     * about the YouTube videos.
     * <p>
     * <p>Hence, we need to submit the video IDs to YouTube to retrieve more information about the
     * given video list.</p>
     *
     * @param searchResultList Search results
     * @return List of {@link YouTubeVideo}s.
     * @throws IOException
     */
    private List<YouTubeVideo> getVideosList(List<SearchResult> searchResultList) throws IOException {
        StringBuilder videoIds = new StringBuilder();

        // append the video IDs into a strings (CSV)
        for (SearchResult res : searchResultList) {
            videoIds.append(res.getId().getVideoId());
            videoIds.append(',');
        }

        // get video details by supplying the videos IDs
        GetVideosDetailsByIDs getVideo = new GetVideosDetailsByIDs();
        getVideo.init(videoIds.toString());

        return getVideo.getNextVideos();
    }


    @Override
    public boolean noMoreVideoPages() {
        return noMoreVideoPages;
    }

}
