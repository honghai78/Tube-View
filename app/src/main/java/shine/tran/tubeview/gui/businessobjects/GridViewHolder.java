package shine.tran.tubeview.gui.businessobjects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.google.android.youtube.player.YouTubePlayer;

import shine.tran.tubeview.BuildConfig;
import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.YouTubeVideo;
import shine.tran.tubeview.gui.activities.ChannelBrowserActivity;
import shine.tran.tubeview.gui.activities.YouTubePlayerActivity;
import shine.tran.tubeview.gui.fragments.YouTubePlayerFragment;

/**
 *
 */
public class GridViewHolder {
    private TextView titleTextView,
            channelTextView,
            thumbsUpPercentageTextView,
            videoDurationTextView,
            viewsTextView,
            publishDateTextView;
    private InternetImageView thumbnailImageView;
    private RelativeLayout bottomLayout;
    public static Drawable drawable = null;
    /**
     * YouTube video
     */
    private YouTubeVideo youTubeVideo = null;
    private Context context = null;

    private static final String TAG = GridViewHolder.class.getSimpleName();

    protected GridViewHolder(View row) {
        titleTextView = (TextView) row.findViewById(R.id.title_text_view);
        channelTextView = (TextView) row.findViewById(R.id.channel_text_view);
        thumbsUpPercentageTextView = (TextView) row.findViewById(R.id.thumbs_up_text_view);
        videoDurationTextView = (TextView) row.findViewById(R.id.video_duration_text_view);
        viewsTextView = (TextView) row.findViewById(R.id.views_text_view);
        publishDateTextView = (TextView) row.findViewById(R.id.publish_date_text_view);
        thumbnailImageView = (InternetImageView) row.findViewById(R.id.thumbnail_image_view);
        bottomLayout = (RelativeLayout) row.findViewById(R.id.cell_bottom_layout);
    }


    /**
     * Updates the contents of this ViewHold such that the data of these views is equal to the
     * given youTubeVideo.
     *
     * @param youTubeVideo    {@link YouTubeVideo} instance.
     * @param showChannelInfo True to display channel information (e.g. channel name) and allows
     *                        user to open and browse the channel; false to hide such information.
     */
    protected void updateInfo(YouTubeVideo youTubeVideo, Context context, boolean showChannelInfo) {
        this.youTubeVideo = youTubeVideo;
        this.context = context;
        updateViewsData(this.youTubeVideo, showChannelInfo);
    }


    /**
     * This method will update the {@link View}s of this object reflecting the supplied video.
     *
     * @param video           {@link YouTubeVideo} instance.
     * @param showChannelInfo True to display channel information (e.g. channel name); false to
     *                        hide such information.
     */
    private void updateViewsData(YouTubeVideo video, boolean showChannelInfo) {
        titleTextView.setText(video.getTitle());
        channelTextView.setText(showChannelInfo ? video.getChannelName() : "");
        publishDateTextView.setText(video.getPublishDate());
        videoDurationTextView.setText(video.getDuration());
        viewsTextView.setText(video.getViewsCount());
        thumbnailImageView.setImageAsync(video.getThumbnailUrl());

        if (video.getThumbsUpPercentageStr() != null) {
            thumbsUpPercentageTextView.setVisibility(View.VISIBLE);
            thumbsUpPercentageTextView.setText(video.getThumbsUpPercentageStr());
        } else {
            thumbsUpPercentageTextView.setVisibility(View.INVISIBLE);
        }
        setupThumbnailOnClickListener();
        setupChannelOnClickListener(showChannelInfo);
    }


    private void setupThumbnailOnClickListener() {
        thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View thumbnailView) {
                if (youTubeVideo != null) {
                    YouTubePlayerFragment.BACK_GROUND_DRAWABLE = thumbnailImageView.getDrawable();
                    YouTubePlayer.launch(youTubeVideo, context);
                }
            }
        });
    }


    private void setupChannelOnClickListener(boolean openChannelOnClick) {
        View.OnClickListener channelListener = null;

        if (openChannelOnClick) {
            channelListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ChannelBrowserActivity.class);
                    i.putExtra(ChannelBrowserActivity.CHANNEL_ID, youTubeVideo.getChannelId());
                    context.startActivity(i);
                }
            };
        }

        channelTextView.setOnClickListener(channelListener);
        bottomLayout.setOnClickListener(channelListener);
    }

}
