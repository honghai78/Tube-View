package shine.tran.localtubeview.businessobjects;

import android.util.Log;

import shine.tran.localtubeview.businessobjects.VideoStream.GetChannelVideos;
import shine.tran.localtubeview.businessobjects.VideoStream.GetSearchLocationVideos;

/**
 * Represents a video category/group.
 */
public enum VideoCategory {
    /**
     * Featured videos
     */
    FEATURED(0),

    /**
     * Most popular videos
     */
    MOST_POPULAR(1),

    /**
     * Videos related to a search query
     */
    SEARCH_QUERY(2),

    /**
     * Videos that are owned by a channel
     */
    CHANNEL_VIDEOS(3),

    /**
     * Videos related to a search query by mLocation
     */
    SEARCH_QUERY_LOCATION(4);

    // *****************
    // DON'T FORGET to update getVideoCategory() and createGetYouTubeVideos() methods...
    // *****************

    private final int id;
    private static final String TAG = VideoCategory.class.getSimpleName();


    VideoCategory(int id) {
        this.id = id;
    }


    /**
     * Convert the given id integer number to {@link VideoCategory}.
     *
     * @param id ID number representing the position of the item in video_categories array (see
     *           the respective strings XML file).
     * @return A new instance of {@link VideoCategory}.
     */
    public static VideoCategory getVideoCategory(int id) {
        if (id < FEATURED.id || id > CHANNEL_VIDEOS.id) {
            Log.e(TAG, "ILLEGAL ID VALUE=" + id);
            Log.e(TAG, "Do NOT forget to update VideoCategories enum.");
            id = FEATURED.id;
        }

        return VideoCategory.values()[id];
    }


    /**
     * Creates a new instance of {@link GetFeaturedVideos} or {@link GetMostPopularVideos} depending
     * on the video category.
     *
     * @return New instance of {@link GetYouTubeVideos}.
     */
    public GetYouTubeVideos createGetYouTubeVideos() {
        if (id == FEATURED.id)
            return new GetFeaturedVideos();
        else if (id == MOST_POPULAR.id)
            return new GetMostPopularVideos();
        else if (id == SEARCH_QUERY.id)
            return new GetYouTubeVideoBySearch();
        else if (id == CHANNEL_VIDEOS.id)
            return new GetChannelVideos();
        else if (id == SEARCH_QUERY_LOCATION.id)
            return new GetSearchLocationVideos();

        return null;
    }

}
