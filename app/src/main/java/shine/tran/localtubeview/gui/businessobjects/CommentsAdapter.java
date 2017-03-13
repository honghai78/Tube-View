package shine.tran.localtubeview.gui.businessobjects;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.GetCommentThreads;
import shine.tran.localtubeview.businessobjects.YouTubeComment;
import shine.tran.localtubeview.businessobjects.YouTubeCommentThread;
import shine.tran.localtubeview.gui.app.TubeViewApp;

/**
 * An adapter that will display comments in an {@link ExpandableListView}.
 */
public class CommentsAdapter extends BaseExpandableListAdapter {

	private String						videoId;
	private List<YouTubeCommentThread>	commentThreadsList = new ArrayList<>();
	private GetCommentsTask				getCommentsTask = null;
	private GetCommentThreads			getCommentThreads = null;
	private ExpandableListView			expandableListView;
	private View						commentsProgressBar;
	private View						noVideoCommentsView;
	private LayoutInflater				layoutInflater;

	private static final String TAG = CommentsAdapter.class.getSimpleName();


	public CommentsAdapter(String videoId, ExpandableListView expandableListView, View commentsProgressBar, View noVideoCommentsView) {
		this.videoId = videoId;
		this.expandableListView = expandableListView;
		this.expandableListView.setAdapter(this);
		this.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		this.commentsProgressBar = commentsProgressBar;
		this.noVideoCommentsView = noVideoCommentsView;
		this.layoutInflater = LayoutInflater.from(expandableListView.getContext());
		this.getCommentsTask = new GetCommentsTask(videoId);
		this.getCommentsTask.execute();
	}

	@Override
	public int getGroupCount() {
		return commentThreadsList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return commentThreadsList.get(groupPosition).getTotalReplies();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return commentThreadsList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return commentThreadsList.get(groupPosition).getRepliesList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return (groupPosition * 1024) + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getParentOrChildView(true, groupPosition, 0 /*ignore this*/, convertView, parent);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		return getParentOrChildView(false, groupPosition, childPosition, convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}


	private View getParentOrChildView(boolean getParentView, int groupPosition, int childPosition, View convertView, ViewGroup parent) {
		View row;
		CommentViewHolder viewHolder;

		if (convertView == null) {
			row = layoutInflater.inflate(R.layout.comment, parent, false);
			viewHolder = new CommentViewHolder(row);
			row.setTag(viewHolder);
		} else {
			row = convertView;
			viewHolder = (CommentViewHolder) row.getTag();
		}

		if (viewHolder != null) {
			YouTubeComment comment;

			if (getParentView)
				comment = ((YouTubeCommentThread)getGroup(groupPosition)).getTopLevelComment();
			else
				comment = (YouTubeComment)getChild(groupPosition, childPosition);

			viewHolder.updateInfo(comment, getParentView, groupPosition);
		}

		// if it reached the bottom of the list, then try to get the next page of videos
		if (getParentView  &&  groupPosition == getGroupCount() - 1) {
			if (this.getCommentsTask == null) {
				Log.w(TAG, "Getting next page of comments...");
				this.getCommentsTask = new GetCommentsTask(this.videoId);
				this.getCommentsTask.execute();
			}
		}

		return row;
	}


	////////////

	private class CommentViewHolder {
		private View		commentView,
							paddingView;
		private TextView	authorTextView,
							commentTextView,
							dateTextView,
							upvotesTextView,
							viewRepliesTextView;
		private InternetImageView	thumbnailImageView;

		protected CommentViewHolder(View commentView) {
			this.commentView = commentView;
			paddingView		= commentView.findViewById(R.id.comment_padding_view);
			authorTextView	= (TextView) commentView.findViewById(R.id.author_text_view);
			commentTextView	= (TextView) commentView.findViewById(R.id.comment_text_view);
			dateTextView	= (TextView) commentView.findViewById(R.id.comment_date_text_view);
			upvotesTextView	= (TextView) commentView.findViewById(R.id.comment_upvotes_text_view);
			viewRepliesTextView	= (TextView) commentView.findViewById(R.id.view_all_replies_text_view);
			thumbnailImageView	= (InternetImageView) commentView.findViewById(R.id.comment_thumbnail_image_view);
		}


		protected void updateInfo(YouTubeComment comment, boolean isTopLevelComment, final int groupPosition) {
			paddingView.setVisibility(isTopLevelComment ? View.GONE : View.VISIBLE);
			authorTextView.setText(comment.getAuthor());
			commentTextView.setText(comment.getComment());
			dateTextView.setText(comment.getDatePublished());
			upvotesTextView.setText(comment.getLikeCount());

			// change the width dimensions depending on whether the comment is a top level or a child
			ViewGroup.LayoutParams lp = thumbnailImageView.getLayoutParams();
			lp.width = (int) TubeViewApp.getDimension(isTopLevelComment  ?  R.dimen.top_level_comment_thumbnail_width  :  R.dimen.child_comment_thumbnail_width);

			if (isTopLevelComment  &&  getChildrenCount(groupPosition) > 0) {
				viewRepliesTextView.setVisibility(View.VISIBLE);

				// on click, hide/show the comment replies
				commentView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View viewReplies) {
						if (expandableListView.isGroupExpanded(groupPosition)) {
							viewRepliesTextView.setText(R.string.view_replies);
							expandableListView.collapseGroup(groupPosition);
						} else {
							viewRepliesTextView.setText(R.string.hide_replies);
							expandableListView.expandGroup(groupPosition);
						}
					}
				});
			} else {
				viewRepliesTextView.setVisibility(View.GONE);
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////

	private class GetCommentsTask extends AsyncTask<Void, Void, List<YouTubeCommentThread>> {

		protected GetCommentsTask(String videoId) {
			if (getCommentThreads == null) {
				getCommentThreads = new GetCommentThreads();
				try {
					getCommentThreads.init(videoId);
				} catch (Throwable tr) {
					Toast.makeText(expandableListView.getContext(), R.string.error_get_comments, Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			commentsProgressBar.setVisibility(View.VISIBLE);
			noVideoCommentsView.setVisibility(View.GONE);
		}

		@Override
		protected  List<YouTubeCommentThread> doInBackground(Void... params) {
			return getCommentThreads.get();
		}

		@Override
		protected void onPostExecute(List<YouTubeCommentThread> commentThreadsList) {
			if (commentThreadsList != null) {
				if (commentThreadsList.size() > 0) {
					CommentsAdapter.this.commentThreadsList.addAll(commentThreadsList);
					CommentsAdapter.this.notifyDataSetChanged();
				} else {
					noVideoCommentsView.setVisibility(View.VISIBLE);
				}
			}

			commentsProgressBar.setVisibility(View.GONE);
			getCommentsTask = null;
		}

	}

}
