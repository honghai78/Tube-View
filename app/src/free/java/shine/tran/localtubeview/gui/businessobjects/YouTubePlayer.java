package shine.tran.localtubeview.gui.businessobjects;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shine.app.tubeviewmultitask.Constants;
import shine.app.tubeviewmultitask.PlayerService;
import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.YouTubeVideo;
import shine.tran.localtubeview.gui.activities.YouTubePlayerActivity;

/**
 * Launches YouTube player.
 */
public class YouTubePlayer {
	private static final String TAG = YouTubePlayer.class.getSimpleName();
	public static int OVERLAY_PERMISSION_REQ = 1234;
	/**
	 * Launches the YouTube player so that the user can view the selected video.
	 *
	 * @param youTubeVideo Video to be viewed.
	 */
	public static void launch(YouTubeVideo youTubeVideo, Context context) {
		if (youTubeVideo != null) {
			// if the user has selected to play the videos using the official YouTube player
			// (in the preferences/settings) ...
			if (useOfficialYouTubePlayer(context)) {
				launchOfficialYouTubePlayer(youTubeVideo.getId(), context);
			} else {
//				launchOfficialYouTubePlayer(youTubeVideo.getId(), context);
				launchCustomYouTubePlayer(youTubeVideo, context);
			}
		}
	}


	/**
	 * Read the user's preferences and determine if the user wants to use the official YouTube video
	 * player or not.
	 *
	 * @return True if the user wants to use the official player; false otherwise.
	 */
	private static boolean useOfficialYouTubePlayer(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getBoolean(context.getString(R.string.pref_key_use_offical_player), false);
	}


	/**
	 * Launch the official (i.e. non-free) YouTube player.
	 */
	private static void launchOfficialYouTubePlayer(String videoId, Context context) {
		try {
			// try to start the YouTube standalone player
			Intent intent = com.google.android.youtube.player.YouTubeStandalonePlayer.createVideoIntent((Activity) context, context.getString(R.string.API_KEY), videoId);
			context.startActivity(intent);
		} catch (Exception e) {
			String errorMsg = context.getString(R.string.launch_offical_player_error);

			// log the error
			Log.e(TAG, errorMsg, e);

			// display the error in an AlertDialog
			new AlertDialog.Builder(context)
					.setTitle(R.string.error)
					.setMessage(errorMsg)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNeutralButton(android.R.string.ok, null)
					.show();
		}
	}


	/**
	 * Launch the custom-made YouTube player.
	 */
	private static void launchCustomYouTubePlayer(YouTubeVideo youTubeVideo, final Context context) {
//		Intent i = new Intent(context, YouTubePlayerActivity.class);
//		i.putExtra(YouTubePlayerActivity.YOUTUBE_VIDEO_OBJ, youTubeVideo);
//		context.startActivity(i);
		final String VID;
				String PID;
		String url = youTubeVideo.getUrl().replace("www","m");
		if (String.valueOf(url).contains("http://m.youtube.com/watch?") ||
				String.valueOf(url).contains("https://m.youtube.com/watch?") ||  String.valueOf(url).contains("https://www.youtube.com/watch?")
				|| String.valueOf(url).contains("http://www.youtube.com/watch?")) {
			Log.d(TAG + "Yay Catches!!!! ", url);
			url = String.valueOf(url);
			//Video Id
			VID = url.substring(url.indexOf("?v=") + 3, url.length());
			Log.d(TAG + "VID ", VID);
			//Playlist Id
			final String listID = url.substring(url.indexOf("?list=") + 6, url.length());
			Pattern pattern = Pattern.compile(
					"([A-Za-z0-9_-]+)&[\\w]+=.*",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(listID.toString());
			Log.d(TAG + "ListID", listID);
			PID = "";
			if (matcher.matches()) {
				PID = matcher.group(1);
			}
			if (listID.contains("m.youtube.com")) {
				Log.d(TAG + "Not a ", "Playlist.");
				PID = null;
			} else {
				Constants.linkType = 1;
				Log.d(TAG + "PlaylistID ", PID);
			}
			Handler handler = new Handler(context.getMainLooper());
			final String finalPID = PID;
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (isServiceRunning(PlayerService.class, context)) {
						Log.d(TAG + "Service : ", "Already Running!");
						PlayerService.startVid(VID, finalPID);
					} else {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
							Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
									Uri.parse("package:" + context.getPackageName()));
							((Activity)context).startActivityForResult(i, OVERLAY_PERMISSION_REQ);
						} else {
							Log.e("CHECL-----", VID);
							Intent i = new Intent(context, PlayerService.class);
							i.putExtra("VID_ID", VID);
							i.putExtra("PLAYLIST_ID", finalPID);
							i.putExtra("LAYOUT_VIEW", true);
							i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
							context.startService(i);
						}

//                                    Intent i = new Intent(MainActivity.this, PlayerService.class);
//                                    i.putExtra("VID_ID", VID);
//                                    i.putExtra("PLAYLIST_ID", finalPID);
//                                    i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
//                                    startService(i);
					}

				}
			});
		}

	}
	public static boolean isServiceRunning(Class<PlayerService> playerServiceClass, Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (playerServiceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
