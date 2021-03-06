package shine.tran.localtubeview.gui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.gui.businessobjects.BackActivity;

/**
 * An {@link Activity} that contains an instance of
 * {@link shine.tran.localtubeview.gui.fragments.YouTubePlayerFragment}.
 */
public class YouTubePlayerActivity extends BackActivity {

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public static final String YOUTUBE_VIDEO_OBJ = "YouTubePlayerActivity.yt_video_obj";
    private AdView avBanner;
    private AdRequest adRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_video_player);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to home!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
