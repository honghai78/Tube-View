package shine.tran.localtubeview.businessobjects.VideoStream;

import android.widget.Toast;

import java.io.IOException;

import shine.tran.localtubeview.businessobjects.GetYouTubeVideoBySearch;
import shine.tran.localtubeview.gui.activities.MainActivity;

/**
 * Returns the videos of a channel.  The channel is specified by calling {@link #setQuery(String)}.
 */
public class GetChannelVideos extends GetYouTubeVideoBySearch {

    @Override
    public void init() throws IOException {
        super.init();
        videosList.setOrder("date");
    }


    /**
     * Set the channel id.
     *
     * @param channelId Channel ID.
     */
    @Override
    public void setQuery(String channelId) {
        if (videosList != null) {
            videosList.setChannelId(channelId);
//            videosList.setLocation(null);
//            videosList.setLocationRadius(null);
            Toast.makeText(MainActivity.ACTIVITY, "All videos in channel!", Toast.LENGTH_LONG).show();
        }
    }

}
