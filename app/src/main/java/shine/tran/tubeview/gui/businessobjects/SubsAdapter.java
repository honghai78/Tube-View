package shine.tran.tubeview.gui.businessobjects;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.YouTubeChannel;
import shine.tran.tubeview.businessobjects.db.GetSubscribedChannelsTask;
import shine.tran.tubeview.gui.activities.ChannelBrowserActivity;

/**
 * Channel subscriptions adapter.
 */
public class SubsAdapter extends BaseAdapterEx<YouTubeChannel> {

	private static SubsAdapter subsAdapter = null;
	private static final String TAG = SubsAdapter.class.getSimpleName();

	private SubsAdapter(Context context) {
		super(context);
		new GetSubscribedChannelsTask(this).executeInParallel();
	}


	public static SubsAdapter get(Context context) {
		if (subsAdapter == null) {
			subsAdapter = new SubsAdapter(context);
		}

		return subsAdapter;
	}


	/**
	 * Append channel to this adapter.
	 *
	 * @param channel Channel to append.
	 */
	public void appendChannel(YouTubeChannel channel) {
		append(channel);
	}


	/**
	 * Remove channel from this adapter.
	 *
	 * @param channel Channel to remove.
	 */
	public void removeChannel(YouTubeChannel channel) {
		removeChannel(channel.getId());
	}


	/**
	 * Remove channel from this adapter.
	 *
	 * @param channelId Channel to remove.
	 */
	public void removeChannel(String channelId) {
		int size = getCount();

		for (int i = 0;  i < size;  i++) {
			if (get(i).getId().equalsIgnoreCase(channelId)) {
				remove(i);
				return;
			}
		}

		Log.e(TAG, "Channel not removed from adapter:  id="+channelId);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		SubChannelViewHolder viewHolder;

		if (convertView == null) {
			rowView = getLayoutInflater().inflate(R.layout.sub_channel, parent, false);
			viewHolder = new SubChannelViewHolder(rowView);
			rowView.setTag(viewHolder);
		} else {
			rowView = convertView;
			viewHolder = (SubChannelViewHolder) rowView.getTag();
		}

		if (viewHolder != null) {
			viewHolder.updateInfo(get(position));
		}

		return rowView;
	}


	/////////////////////

	private class SubChannelViewHolder {

		private InternetImageView	thumbnailImageView;
		private TextView			channelNameTextView;
		private YouTubeChannel		channel = null;

		public SubChannelViewHolder(View rowView) {
			thumbnailImageView  = (InternetImageView) rowView.findViewById(R.id.sub_channel_thumbnail_image_view);
			channelNameTextView = (TextView) rowView.findViewById(R.id.sub_channel_name_text_view);
			channel = null;

			rowView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getContext(), ChannelBrowserActivity.class);
					i.putExtra(ChannelBrowserActivity.CHANNEL_OBJ, channel);
					getContext().startActivity(i);
				}
			});
		}

		public void updateInfo(YouTubeChannel channel) {
			thumbnailImageView.setImageAsync(channel.getThumbnailNormalUrl());
			channelNameTextView.setText(channel.getTitle());
			this.channel = channel;
		}

	}

}
