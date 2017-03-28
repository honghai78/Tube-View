package shine.tran.localtubeview.businessobjects;

import android.app.Activity;
import android.app.ProgressDialog;

import java.util.List;

import shine.tran.localtubeview.gui.businessobjects.VideoGridAdapter;
import shine.tran.localtubeview.gui.fragments.VideosGridFragment;

/**
 * An asynchronous task that will retrieve YouTube videos and displays them in the supplied Adapter.
 */
public class GetYouTubeVideosTask extends AsyncTaskParallel<Void, Void, List<YouTubeVideo>> {

	/** Object used to retrieve the desired YouTube videos. */
	private GetYouTubeVideos	getYouTubeVideos;

	/** The Adapter where the retrieved videos will be displayed. */
	private VideoGridAdapter videoGridAdapter;

	/** Class tag. */
	private static final String TAG = GetYouTubeVideosTask.class.getSimpleName();
	private ProgressDialog mProgressBar;
	private Activity activity;
	public GetYouTubeVideosTask(GetYouTubeVideos getYouTubeVideos, VideoGridAdapter videoGridAdapter, Activity ac) {
		this.getYouTubeVideos = getYouTubeVideos;
		this.videoGridAdapter = videoGridAdapter;
		activity = ac;
		mProgressBar = new ProgressDialog(activity);
		mProgressBar.setCancelable(false);
		mProgressBar.setMessage("Loading.....");
		mProgressBar.show();
	}
	@Override
	protected List<YouTubeVideo> doInBackground(Void... params) {
		List<YouTubeVideo> videos = null;

		if (!isCancelled()) {
			videos = getYouTubeVideos.getNextVideos();
		}

		return videos;
	}


	@Override
	protected void onPostExecute(List<YouTubeVideo> videosList) {
		videoGridAdapter.appendList(videosList);
		mProgressBar.cancel();
	}

}
