package shine.tran.tubeview.businessobjects;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.VideoStream.ParseStreamMetaData;
import shine.tran.tubeview.businessobjects.VideoStream.StreamMetaDataList;
import shine.tran.tubeview.gui.app.TubeViewApp;

/**
 * Represents a YouTube video.
 */
public class YouTubeVideo implements Serializable {

    /**
     * YouTube video ID.
     */
    private String id;
    /**
     * Video title.
     */
    private String title;
    /**
     * Channel ID.
     */
    private String channelId;
    /**
     * Channel name.
     */
    private String channelName;
    /**
     * The total number of 'likes'.
     */
    private String likeCount;
    /**
     * The total number of 'dislikes'.
     */
    private String dislikeCount;
    /**
     * The percentage of people that thumbs-up this video (format:  "<percentage>%").
     */
    private String thumbsUpPercentageStr;
    private int thumbsUpPercentage;
    /**
     * Video duration string (e.g. "5:15").
     */
    private String duration;
    /**
     * Total views count.  This can be <b>null</b> if the video does not allow the user to
     * like/dislike it.
     */
    private String viewsCount;
    /**
     * The date/time of when this video was published (e.g. "7 hours ago").
     */
    private String publishDate;
    /**
     * Thumbnail URL string.
     */
    private String thumbnailUrl;
    /**
     * The language of this video.  (This tends to be ISO 639-1).
     */
    private String language;
    /**
     * The description of the video (set by the YouTuber/Owner).
     */
    private String description;

    private String url;

    private static final Set<String> defaultPrefLanguages = new HashSet<>(Arrays.asList(TubeViewApp.getStr(R.string.lang_en)));

    private static final String TAG = YouTubeVideo.class.getSimpleName();


    public YouTubeVideo(Video video) {
        this.id = video.getId();

        if (video.getSnippet() != null) {
            this.title = video.getSnippet().getTitle();
            this.channelId = video.getSnippet().getChannelId();
            this.channelName = video.getSnippet().getChannelTitle();
            this.url = "https://www.youtube.com/watch?v=" + id;
            setPublishDate(video.getSnippet().getPublishedAt());

            if (video.getSnippet().getThumbnails() != null) {
                Thumbnail thumbnail = video.getSnippet().getThumbnails().getHigh();
                if (thumbnail != null)
                    this.thumbnailUrl = thumbnail.getUrl();
            }

            this.language = video.getSnippet().getDefaultAudioLanguage() != null ? video.getSnippet().getDefaultAudioLanguage()
                    : (video.getSnippet().getDefaultLanguage() != null ? video.getSnippet().getDefaultLanguage() : null);

            this.description = video.getSnippet().getDescription();
        }

        if (video.getContentDetails() != null) {
            setDuration(video.getContentDetails().getDuration());
        }

        if (video.getStatistics() != null) {
            BigInteger likeCount = video.getStatistics().getLikeCount(),
                    dislikeCount = video.getStatistics().getDislikeCount();

            setThumbsUpPercentage(likeCount, dislikeCount);

            this.viewsCount = String.format(TubeViewApp.getStr(R.string.views),
                    video.getStatistics().getViewCount());

            if (likeCount != null)
                this.likeCount = String.format("%,d", video.getStatistics().getLikeCount());

            if (dislikeCount != null)
                this.dislikeCount = String.format("%,d", video.getStatistics().getDislikeCount());
        }
    }


    /**
     * Returns a list of video/stream meta-data that is supported by this app (with respect to this
     * video).
     *
     * @return A list of {@link StreamMetaDataList}.
     */
    public StreamMetaDataList getVideoStreamList() {
        ParseStreamMetaData ex = new ParseStreamMetaData(id);
        StreamMetaDataList streamMetaDataList;

        try {
            streamMetaDataList = ex.getStreamMetaDataList();
        } catch (Exception e) {
            Log.e(TAG, "An error has occurred while getting video metadata/streams for video with id=" + id, e);
            streamMetaDataList = null;
        }

        return streamMetaDataList;
    }


    /**
     * Sets the {@link #thumbsUpPercentageStr}, i.e. the percentage of people that thumbs-up this video
     * (format:  "<percentage>%").
     *
     * @param likedCountInt    Total number of "likes".
     * @param dislikedCountInt Total number of "dislikes".
     */
    private void setThumbsUpPercentage(BigInteger likedCountInt, BigInteger dislikedCountInt) {
        String fullPercentageStr = null;
        int percentageInt = -1;

        // some videos do not allow users to like/dislike them:  hence likedCountInt / dislikedCountInt
        // might be null in those cases
        if (likedCountInt != null && dislikedCountInt != null) {
            BigDecimal likedCount = new BigDecimal(likedCountInt),
                    dislikedCount = new BigDecimal(dislikedCountInt),
                    totalVoteCount = likedCount.add(dislikedCount),    // liked and disliked counts
                    likedPercentage = null;

            if (totalVoteCount.compareTo(BigDecimal.ZERO) != 0) {
                likedPercentage = (likedCount.divide(totalVoteCount, MathContext.DECIMAL128)).multiply(new BigDecimal(100));

                // round the liked percentage to 0 decimal places and convert it to string
                String percentageStr = likedPercentage.setScale(0, RoundingMode.HALF_UP).toString();
                fullPercentageStr = percentageStr + "%";
                percentageInt = Integer.parseInt(percentageStr);
            }
        }

        this.thumbsUpPercentageStr = fullPercentageStr;
        this.thumbsUpPercentage = percentageInt;
    }


    /**
     * Sets the {@link #duration} by converts ISO 8601 duration to human readable string.
     *
     * @param duration ISO 8601 duration.
     */
    private void setDuration(String duration) {
        this.duration = VideoDuration.toHumanReadableString(duration);
    }


    /**
     * Sets the {@link #publishDate} by converting the given video's publish date into a pretty
     * string.
     *
     * @param publishDateTime {@link DateTime} of when the video was published.
     */
    private void setPublishDate(DateTime publishDateTime) {
        this.publishDate = (publishDateTime != null)
                ? new PrettyTimeEx().format(publishDateTime)
                : "???";
    }


    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    /**
     * @return True if the video allows the users to like/dislike it.
     */
    public boolean isThumbsUpPercentageSet() {
        return (thumbsUpPercentageStr != null);
    }

    /**
     * @return The thumbs up percentage (as an integer).  Can return <b>-1</b> if the video does not
     * allow the users to like/dislike it.  Refer to {@link #isThumbsUpPercentageSet}.
     */
    public int getThumbsUpPercentage() {
        return thumbsUpPercentage;
    }

    /**
     * @return The thumbs up percentage (format:  "«percentage»%").  Can return <b>null</b> if the
     * video does not allow the users to like/dislike it.  Refer to {@link #isThumbsUpPercentageSet}.
     */
    public String getThumbsUpPercentageStr() {
        return thumbsUpPercentageStr;
    }

    /**
     * @return The total number of 'likes'.  Can return <b>null</b> if the video does not allow the
     * users to like/dislike it.  Refer to {@link #isThumbsUpPercentageSet}.
     */
    public String getLikeCount() {
        return likeCount;
    }

    /**
     * @return The total number of 'dislikes'.  Can return <b>null</b> if the video does not allow the
     * users to like/dislike it.  Refer to {@link #isThumbsUpPercentageSet}.
     */
    public String getDislikeCount() {
        return dislikeCount;
    }

    public String getDuration() {
        return duration;
    }

    public String getViewsCount() {
        return viewsCount;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Return true if this video does not meet the preferred language criteria;  false otherwise.
     * Many YouTube videos do not set the language, hence this method will not be accurate.
     *
     * @return True to filter out the video; false otherwise.
     */
    public boolean filterVideoByLanguage() {
        Set<String> preferredLanguages = getPreferredLanguages();

        // if the video's language is not defined (i.e. null)
        //    OR if there is no linguistic content to the video (zxx)
        //    OR if the language is undefined (und)
        // then we are NOT going to filter this video
        if (getLanguage() == null || getLanguage().equalsIgnoreCase("zxx") || getLanguage().equalsIgnoreCase("und"))
            return false;

        // if there are no preferred languages, then it means we must not filter this video
        if (preferredLanguages.isEmpty())
            return false;

        // if this video's language is equal to the user's preferred one... then do NOT filter it out
        for (String prefLanguage : preferredLanguages) {
            if (getLanguage().matches(prefLanguage))
                return false;
        }

        // this video is undesirable, hence we are going to filter it
        Log.i("FILTERING Video", getTitle() + "[" + getLanguage() + "]");
        return true;
    }


    private Set<String> getPreferredLanguages() {
        return TubeViewApp.getPreferenceManager().getStringSet(TubeViewApp.getStr(R.string.pref_key_preferred_languages), defaultPrefLanguages);
    }

}
