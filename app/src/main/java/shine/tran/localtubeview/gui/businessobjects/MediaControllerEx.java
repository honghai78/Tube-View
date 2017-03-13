package shine.tran.localtubeview.gui.businessobjects;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import shine.tran.localtubeview.R;

/**
 * A {@link MediaController} that handles the back button events.
 * <p/>
 * <p>When a user has pressed the back button (of the Navigation Bar), then this controller will
 * stop the video and closes the calling activity.</p>
 */
public class MediaControllerEx extends MediaController {

    private VideoView videoView;
    private Context context;
    private Activity activity;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    /**
     * Initialises this object.  It also attaches the supplied VideoView with this MediaController.
     *
     * @param activity  Activity where this controller will run on.
     * @param videoView VideoView that this controller will control.
     */
    public MediaControllerEx(Activity activity, VideoView videoView) {
        super(activity);
        this.activity = activity;
        context = activity.getApplicationContext();
        this.videoView = videoView;
        this.videoView.setMediaController(this);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // if the user has pressed the BACK button (of the Navigation Bar), then ...
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                        videoView.stopPlayback();
                        ((Activity) getContext()).finish();
                    } else {
                        Toast.makeText(activity.getBaseContext(), "Press once again to home!",
                                Toast.LENGTH_SHORT).show();
                    }
                    back_pressed = System.currentTimeMillis();
            }

            // true means we handles the event
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        //ImageButton searchButton = new ImageButton(context);
        ImageView rotateImageView = new ImageView(context);
        rotateImageView.setImageResource(R.drawable.media);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        rotateImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        addView(rotateImageView, params);
    }
}
