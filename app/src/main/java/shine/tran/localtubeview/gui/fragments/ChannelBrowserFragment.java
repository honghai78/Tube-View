package shine.tran.localtubeview.gui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.io.IOException;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.VideoCategory;
import shine.tran.localtubeview.businessobjects.YouTubeChannel;
import shine.tran.localtubeview.businessobjects.db.SubscribeToChannelTask;
import shine.tran.localtubeview.gui.activities.ChannelBrowserActivity;
import shine.tran.localtubeview.gui.businessobjects.FragmentEx;
import shine.tran.localtubeview.gui.businessobjects.InternetImageView;
import shine.tran.localtubeview.gui.businessobjects.SubscribeButton;
import shine.tran.localtubeview.gui.businessobjects.VideoGridAdapter;

/**
 * A Fragment that displays information about a mChannel.
 */
public class ChannelBrowserFragment extends FragmentEx {

	private YouTubeChannel mChannel = null;
	private GridView       mGridView;
	private VideoGridAdapter mVideoGridAdapter;

	private InternetImageView mChannelThumbnailImage = null;
	private InternetImageView mChannelBannerImage = null;
	private TextView          mChannelSubscribersTextView = null;
	private SubscribeButton   mChannelSubscribeButton = null;
	private GetChannelInfoTask mTask = null;

	private static final String TAG = ChannelBrowserActivity.class.getSimpleName();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final String channelId;
		Bundle bundle = getActivity().getIntent().getExtras();

		// we need to create a YouTubeChannel object:  this can be done by either:
		//   (1) the object is passed to this Fragment
		//   (2) passing the mChannel ID... a mTask is then created to create a YouTubeChannel
		//       instance using the given mChannel ID
		if (bundle != null  &&  bundle.getSerializable(ChannelBrowserActivity.CHANNEL_OBJ) != null) {
			this.mChannel = (YouTubeChannel) bundle.getSerializable(ChannelBrowserActivity.CHANNEL_OBJ);
			channelId = mChannel.getId();
		} else {
			channelId = getActivity().getIntent().getStringExtra(ChannelBrowserActivity.CHANNEL_ID);
		}

		// inflate the layout for this fragment
		View fragment = inflater.inflate(R.layout.fragment_channel_browser, container, false);

		mChannelBannerImage = (InternetImageView) fragment.findViewById(R.id.channel_banner_image_view);
		mChannelThumbnailImage = (InternetImageView) fragment.findViewById(R.id.channel_thumbnail_image_view);
		mChannelSubscribersTextView = (TextView) fragment.findViewById(R.id.channel_subs_text_view);
		mChannelSubscribeButton = (SubscribeButton) fragment.findViewById(R.id.channel_subscribe_button);
		mChannelSubscribeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// subscribe / unsubscribe to this video's mChannel
				new SubscribeToChannelTask(mChannelSubscribeButton, mChannel).executeInParallel();
			}
		});

		if (mChannel == null) {
			if (mTask == null) {
				mTask = new GetChannelInfoTask();
				mTask.execute(channelId);
			}
		} else {
			initViews();
			mChannel.updateLastVisitTime();
		}

		mGridView = (GridView) fragment.findViewById(R.id.grid_view);

		if (mVideoGridAdapter == null) {
			mVideoGridAdapter = new VideoGridAdapter(getActivity(), false /*hide mChannel name*/);
			mVideoGridAdapter.setVideoCategory(VideoCategory.CHANNEL_VIDEOS, channelId);
		}

		this.mGridView.setAdapter(this.mVideoGridAdapter);

		return fragment;
	}


	/**
	 * Initialise views that are related to {@link #mChannel}.
	 */
	private void initViews() {
		if (mChannel != null) {
			mChannelThumbnailImage.setImageAsync(mChannel.getThumbnailNormalUrl());
			mChannelBannerImage.setImageAsync(mChannel.getBannerUrl());
			mChannelSubscribersTextView.setText(mChannel.getTotalSubscribers());

			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(mChannel.getTitle());
			}

			// if the user has subscribed to this mChannel, then change the state of the
			// subscribe button
			if (mChannel.isUserSubscribed()) {
				mChannelSubscribeButton.setUnsubscribeState();
			} else {
				mChannelSubscribeButton.setSubscribeState();
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////

	private class GetChannelInfoTask extends AsyncTask<String, Void, YouTubeChannel> {

		private final String TAG = GetChannelInfoTask.class.getSimpleName();

		@Override
		protected YouTubeChannel doInBackground(String... channelId) {
			YouTubeChannel chn = new YouTubeChannel();

			try {
				// initialise the mChannel
				chn.init(channelId[0]);

				// the user is visiting the mChannel, so we need to update the last visit time
				chn.updateLastVisitTime();
			} catch (IOException e) {
				Log.e(TAG, "Unable to get mChannel info.  ChannelID=" + channelId[0], e);
				chn = null;
			}

			return chn;
		}

		@Override
		protected void onPostExecute(YouTubeChannel youTubeChannel) {
			ChannelBrowserFragment.this.mChannel = youTubeChannel;
			initViews();
		}

	}

}
