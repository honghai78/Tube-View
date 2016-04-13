package shine.tran.tubeview.gui.businessobjects;

import android.content.Context;
import android.content.Intent;

import shine.tran.tubeview.businessobjects.YouTubeVideo;
import shine.tran.tubeview.gui.activities.YouTubePlayerActivity;

/**
 * Launches YouTube player.
 */
public class YouTubePlayer {

	private static final String TAG = YouTubePlayer.class.getSimpleName();

	/**
	 * Launches the custom-made YouTube player so that the user can view the selected video.
	 *
	 * @param youTubeVideo Video to be viewed.
	 */
	public static void launch(YouTubeVideo youTubeVideo, Context context) {
		Intent i = new Intent(context, YouTubePlayerActivity.class);
		i.putExtra(YouTubePlayerActivity.YOUTUBE_VIDEO_OBJ, youTubeVideo);
		context.startActivity(i);
	}

}
