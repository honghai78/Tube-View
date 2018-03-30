package shine.tran.localtubeview.gui.businessobjects;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.GetYouTubeVideos;
import shine.tran.localtubeview.businessobjects.GetYouTubeVideosTask;
import shine.tran.localtubeview.businessobjects.VideoCategory;
import shine.tran.localtubeview.businessobjects.YouTubeVideo;

/**
 * An adapter that will display videos in a {@link android.widget.GridView}.
 */
public class VideoGridAdapter extends BaseAdapterEx<YouTubeVideo> {

	/** Class used to get YouTube videos from the web. */
	private GetYouTubeVideos	getYouTubeVideos;
	private boolean				showChannelInfo = true;
	private Activity activity = null;
	private static final String TAG = VideoGridAdapter.class.getSimpleName();


	/**
	 * @see #VideoGridAdapter(Context, boolean)
	 */
	public VideoGridAdapter(Activity context) {
		this(context, true);
		activity = context;
	}


	/**
	 * Constructor.
	 *
	 * @param context			Context
	 * @param showChannelInfo	True to display channel information (e.g. channel name) and allows
	 *                          user to open and browse the channel; false to hide such information.
	 */
	public VideoGridAdapter(Context context, boolean showChannelInfo) {
		super(context);
		this.getYouTubeVideos = null;
		this.showChannelInfo = showChannelInfo;
	}


	/**
	 * Set the video category.  Upon set, the adapter will download the videos of the specified
	 * category asynchronously.
	 *
	 * @see #setVideoCategory(VideoCategory, String)
	 */
	public void setVideoCategory(VideoCategory videoCategory) {
		setVideoCategory(videoCategory, null);
	}


	/**
	 * Set the video category.  Upon set, the adapter will download the videos of the specified
	 * category asynchronously.
	 *
	 * @param videoCategory	The video category you want to change to.
	 * @param searchQuery	The search query.  Should only be set if mVideoCategory is equal to
	 *                      SEARCH_QUERY.
	 */
	public void setVideoCategory(VideoCategory videoCategory, String searchQuery) {
		try {
			Log.i(TAG, videoCategory.toString());

			// clear all previous items in this adapter
			this.clearList();

			// create a new instance of GetYouTubeVideos
			this.getYouTubeVideos = videoCategory.createGetYouTubeVideos();
			this.getYouTubeVideos.init();

			// set the query
			if (searchQuery != null) {
				getYouTubeVideos.setQuery(searchQuery);
			}

			// get the videos from the web asynchronously
			new GetYouTubeVideosTask(getYouTubeVideos, this, activity).executeInParallel();
		} catch (IOException e) {
			Log.e(TAG, "Could not init " + videoCategory, e);
			Toast.makeText(getContext(),
					String.format(getContext().getString(R.string.could_not_get_videos), videoCategory.toString()),
					Toast.LENGTH_LONG).show();
		}
	}

	public void reLoad()
	{
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;
		GridViewHolder viewHolder;

		if (convertView == null) {
			row = getLayoutInflater().inflate(R.layout.video_cell, parent, false);
			viewHolder = new GridViewHolder(row, activity);
			row.setTag(viewHolder);
		} else {
			row = convertView;
			viewHolder = (GridViewHolder) row.getTag();
		}

		if (viewHolder != null) {
			viewHolder.updateInfo(get(position), getContext(), showChannelInfo);
		}

		// if it reached the bottom of the list, then try to get the next page of videos
		if (position == getCount() - 1) {
			Log.w(TAG, "BOTTOM REACHED!!!");
			new GetYouTubeVideosTask(getYouTubeVideos, this, activity).executeInParallel();
		}

		return row;
	}

}
