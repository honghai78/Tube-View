package shine.tran.tubeview.businessobjects;

import android.util.Log;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelBrandingSettings;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelStatistics;
import com.google.api.services.youtube.model.ThumbnailDetails;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.app.TubeViewApp;

/**
 * Represents a YouTube Channel.
 * <p>
 * <p>This class has the ability to query channel info by using the given channel ID.</p>
 */
public class YouTubeChannel implements Serializable {

    private String id;
    private String title;
    private String description;
    private String thumbnailHdUrl;
    private String thumbnailNormalUrl;
    private String bannerUrl;
    private String totalSubscribers;
    private boolean isUserSubscribed;
    private long lastVisitTime;

    private static final String TAG = YouTubeChannel.class.getSimpleName();


    /**
     * Initialise this object.  This should not be called from the main thread.
     *
     * @param channelId Channel ID
     */
    public void init(String channelId) throws IOException {
        init(channelId, false);
    }


    /**
     * Initialise this object.  This should not be called from the main thread.
     *
     * @param channelId        Channel ID
     * @param isUserSubscribed if set to true, then it means the user is subscribed to this channel;
     *                         otherwise it means that we currently do not know if the user is
     *                         subbed or not (hence we need to check).
     * @throws IOException
     */
    public void init(String channelId, boolean isUserSubscribed) throws IOException {
        YouTube youtube = YouTubeAPI.create();
        YouTube.Channels.List channelInfo = youtube.channels().list("snippet, statistics, brandingSettings");
        channelInfo.setFields("items(id, snippet/title, snippet/description, snippet/thumbnails/high, snippet/thumbnails/default," +
                "statistics/subscriberCount, brandingSettings/image/bannerTabletHdImageUrl)," +
                "nextPageToken");
        channelInfo.setKey(TubeViewApp.getStr(R.string.API_KEY));
        channelInfo.setId(channelId);

        // get this channel's info from the remote YouTube server
        if (getChannelInfo(channelInfo)) {
            this.id = channelId;

            // get any channel info that is stored in the database
            getChannelInfoFromDB(isUserSubscribed);
        }
    }


    /**
     * Get this channel's info from the remote YouTube server.
     *
     * @param channelInfo
     * @return True if successful; false otherwise.
     */
    private boolean getChannelInfo(YouTube.Channels.List channelInfo) {
        List<Channel> channelList = null;
        boolean successful = false;

        try {
            // communicate with YouTube
            ChannelListResponse response = channelInfo.execute();

            // get channel
            channelList = response.getItems();
        } catch (IOException e) {
            Log.e(TAG, "Error has occurred while getting Featured Videos.", e);
        }


        if (channelList.size() <= 0)
            Log.e(TAG, "channelList is empty");
        else {
            parse(channelList.get(0));
            successful = true;
        }

        return successful;
    }


    private void parse(Channel channel) {
        ChannelSnippet snippet = channel.getSnippet();
        if (snippet != null) {
            this.title = snippet.getTitle();
            this.description = snippet.getDescription();

            ThumbnailDetails thumbnail = snippet.getThumbnails();
            if (thumbnail != null) {
                this.thumbnailHdUrl = snippet.getThumbnails().getHigh().getUrl();
                this.thumbnailNormalUrl = snippet.getThumbnails().getDefault().getUrl();
            }
        }

        ChannelBrandingSettings branding = channel.getBrandingSettings();
        if (branding != null)
            this.bannerUrl = branding.getImage().getBannerTabletHdImageUrl();

        ChannelStatistics statistics = channel.getStatistics();
        if (statistics != null) {
            this.totalSubscribers = String.format(TubeViewApp.getStr(R.string.total_subscribers),
                    statistics.getSubscriberCount());
        }
    }


    /**
     * Get any channel info that is stored in the database (locally).
     *
     * @param isUserSubscribed if set to true, then it means the user is subscribed to this channel;
     *                         otherwise it means that we currently do not know if the user is
     *                         subbed or not (hence we need to check).
     */
    private void getChannelInfoFromDB(boolean isUserSubscribed) {
        // check if the user is subscribed to this channel or not
        if (!isUserSubscribed) {
            try {
                this.isUserSubscribed = TubeViewApp.getSubscriptionsDb().isUserSubscribedToChannel(id);
            } catch (Throwable tr) {
                Log.e(TAG, "Unable to check if user has subscribed to channel id=" + id, tr);
                this.isUserSubscribed = false;
            }
        } else {
            this.isUserSubscribed = true;
        }

        // get the last time the user has visited this channel
        this.lastVisitTime = TubeViewApp.getSubscriptionsDb().getLastVisitTime(id);
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailHdUrl() {
        return thumbnailHdUrl;
    }

    public String getThumbnailNormalUrl() {
        return thumbnailNormalUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getTotalSubscribers() {
        return totalSubscribers;
    }

    public boolean isUserSubscribed() {
        return isUserSubscribed;
    }

    public void updateLastVisitTime() {
        lastVisitTime = TubeViewApp.getSubscriptionsDb().updateLastVisitTime(id);

        if (lastVisitTime < 0) {
            Log.e(TAG, "Unable to update channel's last visit time.  ChannelID=" + id);
        }
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }

}
