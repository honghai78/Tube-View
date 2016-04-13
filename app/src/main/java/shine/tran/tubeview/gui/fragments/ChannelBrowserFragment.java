package shine.tran.tubeview.gui.fragments;

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

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.VideoCategory;
import shine.tran.tubeview.businessobjects.YouTubeChannel;
import shine.tran.tubeview.businessobjects.db.SubscribeToChannelTask;
import shine.tran.tubeview.gui.activities.ChannelBrowserActivity;
import shine.tran.tubeview.gui.businessobjects.FragmentEx;
import shine.tran.tubeview.gui.businessobjects.InternetImageView;
import shine.tran.tubeview.gui.businessobjects.SubscribeButton;
import shine.tran.tubeview.gui.businessobjects.VideoGridAdapter;

/**
 * A Fragment that displays information about a channel.
 */
public class ChannelBrowserFragment extends FragmentEx {

	private YouTubeChannel	channel = null;
	private GridView		gridView;
	private VideoGridAdapter videoGridAdapter;

	private InternetImageView	channelThumbnailImage = null;
	private InternetImageView	channelBannerImage = null;
	private TextView			channelSubscribersTextView = null;
	private SubscribeButton		channelSubscribeButton = null;
	private GetChannelInfoTask	task = null;

	private static final String TAG = ChannelBrowserActivity.class.getSimpleName();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final String channelId;
		Bundle bundle = getActivity().getIntent().getExtras();

		// we need to create a YouTubeChannel object:  this can be done by either:
		//   (1) the object is passed to this Fragment
		//   (2) passing the channel ID... a task is then created to create a YouTubeChannel
		//       instance using the given channel ID
		if (bundle != null  &&  bundle.getSerializable(ChannelBrowserActivity.CHANNEL_OBJ) != null) {
			this.channel = (YouTubeChannel) bundle.getSerializable(ChannelBrowserActivity.CHANNEL_OBJ);
			channelId = channel.getId();
		} else {
			channelId = getActivity().getIntent().getStringExtra(ChannelBrowserActivity.CHANNEL_ID);
		}

		// inflate the layout for this fragment
		View fragment = inflater.inflate(R.layout.fragment_channel_browser, container, false);

		channelBannerImage = (InternetImageView) fragment.findViewById(R.id.channel_banner_image_view);
		channelThumbnailImage = (InternetImageView) fragment.findViewById(R.id.channel_thumbnail_image_view);
		channelSubscribersTextView = (TextView) fragment.findViewById(R.id.channel_subs_text_view);
		channelSubscribeButton = (SubscribeButton) fragment.findViewById(R.id.channel_subscribe_button);
		channelSubscribeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// subscribe / unsubscribe to this video's channel
				new SubscribeToChannelTask(channelSubscribeButton, channel).execute();
			}
		});

		if (channel == null) {
			if (task == null) {
				task = new GetChannelInfoTask();
				task.execute(channelId);
			}
		} else {
			initViews();
			channel.updateLastVisitTime();
		}

		gridView = (GridView) fragment.findViewById(R.id.grid_view);

		if (videoGridAdapter == null) {
			videoGridAdapter = new VideoGridAdapter(getActivity(), false /*hide channel name*/);
			videoGridAdapter.setVideoCategory(VideoCategory.CHANNEL_VIDEOS, channelId);
		}

		this.gridView.setAdapter(this.videoGridAdapter);

		return fragment;
	}


	/**
	 * Initialise views that are related to {@link #channel}.
	 */
	private void initViews() {
		if (channel != null) {
			channelThumbnailImage.setImageAsync(channel.getThumbnailNormalUrl());
			channelBannerImage.setImageAsync(channel.getBannerUrl());
			channelSubscribersTextView.setText(channel.getTotalSubscribers());

			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(channel.getTitle());
			}

			// if the user has subscribed to this channel, then change the state of the
			// subscribe button
			if (channel.isUserSubscribed()) {
				channelSubscribeButton.setUnsubscribeState();
			} else {
				channelSubscribeButton.setSubscribeState();
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
				// initialise the channel
				chn.init(channelId[0]);

				// the user is visiting the channel, so we need to update the last visit time
				chn.updateLastVisitTime();
			} catch (IOException e) {
				Log.e(TAG, "Unable to get channel info.  ChannelID=" + channelId[0], e);
				chn = null;
			}

			return chn;
		}

		@Override
		protected void onPostExecute(YouTubeChannel youTubeChannel) {
			ChannelBrowserFragment.this.channel = youTubeChannel;
			initViews();
		}

	}

}
