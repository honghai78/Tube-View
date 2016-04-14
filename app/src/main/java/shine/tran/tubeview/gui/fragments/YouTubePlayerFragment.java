package shine.tran.tubeview.gui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ShareActionProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.GetVideoDescription;
import shine.tran.tubeview.businessobjects.GetVideosDetailsByIDs;
import shine.tran.tubeview.businessobjects.VideoCategory;
import shine.tran.tubeview.businessobjects.VideoStream.StreamMetaData;
import shine.tran.tubeview.businessobjects.VideoStream.StreamMetaDataList;
import shine.tran.tubeview.businessobjects.YouTubeChannel;
import shine.tran.tubeview.businessobjects.YouTubeVideo;
import shine.tran.tubeview.businessobjects.db.CheckIfUserSubbedToChannelTask;
import shine.tran.tubeview.businessobjects.db.SubscribeToChannelTask;
import shine.tran.tubeview.gui.activities.MainActivity;
import shine.tran.tubeview.gui.activities.YouTubePlayerActivity;
import shine.tran.tubeview.gui.app.TubeViewApp;
import shine.tran.tubeview.gui.businessobjects.CommentsAdapter;
import shine.tran.tubeview.gui.businessobjects.FragmentEx;
import shine.tran.tubeview.gui.businessobjects.MediaControllerEx;
import shine.tran.tubeview.gui.businessobjects.OnSwipeTouchListener;
import shine.tran.tubeview.gui.businessobjects.SubscribeButton;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.SlidingDrawer;
import shine.tran.tubeview.gui.businessobjects.VideoGridAdapter;


/**
 * A fragment that holds a standalone YouTube player.
 */
public class YouTubePlayerFragment extends FragmentEx implements MediaPlayer.OnPreparedListener{

    public static Drawable      BACK_GROUND_DRAWABLE = null;
	private YouTubeVideo        mYouTubeVideo = null;
	private YouTubeChannel      mYouTubeChannel = null;
	private android.support.v4.widget.DrawerLayout 		drawerLayout = null;

	private VideoView           mVideoView = null;
	private MediaControllerEx   mMediaController = null;
	private TextView            mVideoDescTitleTextView = null;
	private TextView            mVideoDescChannelTextView = null;
	private SubscribeButton     mVideoDescSubscribeButton = null;
	private TextView            mVideoDescViewsTextView = null;
	private TextView            mVideoDescLikesTextView = null;
	private TextView            mVideoDescDislikesTextView = null;
	private TextView            mVideoDescPublishDateTextView = null;
	private TextView            mVideoDescriptionTextView = null;
	private ProgressBar         mVideoDescLikesBar = null;
	private View                mVoidView = null;
	private View                mLoadingVideoView = null;

	private SlidingDrawer       mVideoDescriptionDrawer = null;
	private SlidingDrawer       mCommentsDrawer = null;
	private View                mCommentsProgressBar = null,
								mNoVideoCommentsView = null;
	private CommentsAdapter     mCommentsAdapter = null;
	private ExpandableListView  mCommentsExpandableListView = null;


	private SlidingDrawer       mVideoDrawer = null;
	private View                mVideoProgressBar = null;

	private GridView            mGridView = null;

	private Handler             mTimerHandler = null;

	private VideoGridAdapter     mVideoGridAdapter = null;
	private static final int     HUD_VISIBILITY_TIMEOUT = 7000;
    public static Activity       ACTIVITY = null;
	private static final String  TAG = YouTubePlayerFragment.class.getSimpleName();
    private int posi =0;
    private Uri uri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_youtube_player, container, false);
		// indicate that this fragment has an action bar menu
		setHasOptionsMenu(true);

		if (mYouTubeVideo == null) {
			mLoadingVideoView = view.findViewById(R.id.loadingVideoView);

			mVideoView = (VideoView) view.findViewById(R.id.video_view);
			// play the video once its loaded
			mVideoView.setOnPreparedListener(this);
            if(Build.VERSION.SDK_INT>=16) mVideoView.setBackground(BACK_GROUND_DRAWABLE);
			// setup the media controller (will control the video playing/pausing)
			mMediaController = new MediaControllerEx(getActivity(), mVideoView);

			//===============================================================================
			//Set VideosDescri
			mVideoDescriptionDrawer = (SlidingDrawer) view.findViewById(R.id.des_drawer);
			mVideoDescTitleTextView = (TextView) view.findViewById(R.id.video_desc_title);
			mVideoDescChannelTextView = (TextView) view.findViewById(R.id.video_desc_channel);
			mVideoDescViewsTextView = (TextView) view.findViewById(R.id.video_desc_views);
			mVideoDescLikesTextView = (TextView) view.findViewById(R.id.video_desc_likes);
			mVideoDescDislikesTextView = (TextView) view.findViewById(R.id.video_desc_dislikes);
			mVideoDescPublishDateTextView = (TextView) view.findViewById(R.id.video_desc_publish_date);
			mVideoDescriptionTextView = (TextView) view.findViewById(R.id.video_desc_description);
			mVideoDescLikesBar = (ProgressBar) view.findViewById(R.id.video_desc_likes_bar);
			mVideoDescSubscribeButton = (SubscribeButton) view.findViewById(R.id.video_desc_subscribe_button);
			mVideoDescSubscribeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// subscribe / unsubscribe to this video's channel
					new SubscribeToChannelTask(mVideoDescSubscribeButton, mYouTubeChannel).execute();
				}
			});

			//=======================================================================================
			//set commentVideos

			mCommentsExpandableListView = (ExpandableListView) view.findViewById(R.id.commentsExpandableListView);
			mCommentsProgressBar = view.findViewById(R.id.comments_progress_bar);
			mNoVideoCommentsView = view.findViewById(R.id.no_video_comments_text_view);
			mCommentsDrawer = (SlidingDrawer) view.findViewById(R.id.comments_drawer);
			mCommentsDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
				@Override
				public void onDrawerOpened() {
					if (mCommentsAdapter == null) {
						mCommentsAdapter = new CommentsAdapter(mYouTubeVideo.getId(), mCommentsExpandableListView, mCommentsProgressBar, mNoVideoCommentsView);
					}
				}
			});


			//================================================================================
			drawerLayout = (android.support.v4.widget.DrawerLayout) view.findViewById(R.id.comment_drawer_layout);
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, null, 0, 0)
			{
				@Override
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					mCommentsDrawer.open();
				}
			};


			drawerLayout.addDrawerListener(drawerToggle);
			mVoidView = view.findViewById(R.id.void_view);

			//Creat Swipe
			mVoidView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
				public void onSwipeTop() {
					if (isHudVisible())
						hideHud();
					else
						mVideoDescriptionDrawer.open();
				}

				public void onSwipeRight() {
					if (mVideoDrawer.isOpened())
						mVideoDrawer.close();
					else
						drawerLayout.openDrawer(Gravity.LEFT);
				}

				public void onSwipeLeft() {
					if (drawerLayout.isDrawerOpen(Gravity.LEFT))
						drawerLayout.closeDrawer(Gravity.LEFT);
					else
						mVideoDrawer.open();
				}

				public void onSwipeBottom() {
					if (mVideoDescriptionDrawer.isOpened())
						mVideoDescriptionDrawer.close();
					else
						showOrHideHud();
				}

				public void onClick() {
					showOrHideHud();
				}

			});

			//======================================================================================
			mGridView = (GridView) view.findViewById(R.id.commentsExpandableListView1);
			mVideoProgressBar = view.findViewById(R.id.comments_progress_bar1);
			//noVideoCommentsView1 = view.findViewById(R.id.no_video_comments_text_view1);
			mVideoDrawer = (SlidingDrawer) view.findViewById(R.id.video_drawer);
			mVideoDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
				@Override
				public void onDrawerOpened() {
					if (mVideoGridAdapter == null) {
						mVideoGridAdapter = new VideoGridAdapter(getActivity());

						String searchQuery = mYouTubeVideo.getTitle();
						if (searchQuery != null) {
							// set the video category (if the user wants to search)... otherwise it will be set-
							// up by the VideoGridFragment
							mVideoGridAdapter.setVideoCategory(VideoCategory.SEARCH_QUERY, searchQuery);

							// set the action bar's title
							ActionBar actionBar = getSupportActionBar();
							if (actionBar != null)
								actionBar.setTitle(searchQuery);
						}
					}
					mGridView.setAdapter(mVideoGridAdapter);
					mVideoProgressBar.getLayoutParams().height = 0;
				}
			});

			// hide action bar
			getSupportActionBar().hide();

			// get which video we need to play...
			Bundle bundle = getActivity().getIntent().getExtras();
			if (bundle != null  &&  bundle.getSerializable(YouTubePlayerActivity.YOUTUBE_VIDEO_OBJ) != null) {
				// ... either the video details are passed through the previous activity
				mYouTubeVideo = (YouTubeVideo) bundle.getSerializable(YouTubePlayerActivity.YOUTUBE_VIDEO_OBJ);
				setUpHUDAndPlayVideo();

				getVideoInfoTasks();
			} else {
				// ... or the video URL is passed to Tube View via another Android app
				GetVideoDetailsTask getVideoDetailsTask = new GetVideoDetailsTask();
				getVideoDetailsTask.execute();
			}
		}

        //================================
        ACTIVITY = getActivity();

        //=============================
        return view;
	}



	private void getVideoInfoTasks() {
		// get Channel info (e.g. avatar...etc) task
		new GetYouTubeChannelInfoTask().execute(mYouTubeVideo.getChannelId());

		// check if the user has subscribed to a channel... if he has, then change the state of
		// the subscribe button
		new CheckIfUserSubbedToChannelTask(mVideoDescSubscribeButton, mYouTubeVideo.getChannelId()).execute();
	}


	/**
	 * Will setup the HUD's details according to the contents of {@link #mYouTubeVideo}.  Then it
	 * will try to load and play the video.
	 */
	private void setUpHUDAndPlayVideo() {
		mVideoDescTitleTextView.setText(mYouTubeVideo.getTitle());
		mVideoDescChannelTextView.setText(mYouTubeVideo.getChannelName());
		mVideoDescViewsTextView.setText(mYouTubeVideo.getViewsCount());

		if (mYouTubeVideo.isThumbsUpPercentageSet()) {
			mVideoDescLikesTextView.setText(mYouTubeVideo.getLikeCount());
			mVideoDescDislikesTextView.setText(mYouTubeVideo.getDislikeCount());
			mVideoDescPublishDateTextView.setText(mYouTubeVideo.getPublishDate());

			mVideoDescLikesBar.setProgress(mYouTubeVideo.getThumbsUpPercentage());
			//mVideoDescLikesBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.video_desc_like_bar), PorterDuff.Mode.SRC_IN);
		}

		// load the video
		loadVideo();
	}



	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mLoadingVideoView.setVisibility(View.GONE);
		mVideoView.start();
		showHud();
	}



	/**
	 * @return True if the HUD is visible (provided that this Fragment is also visible).
	 */
	private boolean isHudVisible() {
		return isVisible()  &&  (mMediaController.isShowing()  ||  getSupportActionBar().isShowing());
	}



	/**
	 * Hide or display the HUD depending if the HUD is currently visible or not.
	 */
	private void showOrHideHud() {
		if (isHudVisible())
			hideHud();
		else
			showHud();
	}



	/**
	 * Show the HUD (head-up display), i.e. the Action Bar and Media Controller.
	 */
	private void showHud() {
		if (!isHudVisible()) {
			getSupportActionBar().show();
			getSupportActionBar().setTitle(mYouTubeVideo.getTitle());
			mMediaController.show(0);
			mVideoDescriptionDrawer.close();
			mVideoDescriptionDrawer.setVisibility(View.INVISIBLE);
			mCommentsDrawer.close();
			mCommentsDrawer.setVisibility(View.INVISIBLE);

			// hide UI after a certain timeout (defined in UI_VISIBILITY_TIMEOUT)
			mTimerHandler = new Handler();
			mTimerHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					hideHud();
					mTimerHandler = null;
				}
			}, HUD_VISIBILITY_TIMEOUT);
		}
	}



	/**
	 * Hide the HUD.
	 */
	private void hideHud() {
		if (isHudVisible()) {
			getSupportActionBar().hide();
			mMediaController.hide();

			mVideoDescriptionDrawer.setVisibility(View.VISIBLE);
			mCommentsDrawer.setVisibility(View.VISIBLE);

			// If there is a mTimerHandler running, then cancel it (stop if from running).  This way,
			// if the HUD was hidden on the 5th second, and the user reopens the HUD, this code will
			// prevent the HUD to re-disappear 2 seconds after it was displayed (assuming that
			// UI_VISIBILITY_TIMEOUT = 7 seconds).
			if (mTimerHandler != null) {
				mTimerHandler.removeCallbacksAndMessages(null);
				mTimerHandler = null;
			}
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_youtube_player, menu);
		//getActivity().getMenuInflater().inflate(R.menu.menu_youtube_player, menu);
		// Set up ShareActionProvider's default share intent
		MenuItem shareItem = menu.findItem(R.id.action_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);
		Intent myIntent = new Intent();
		myIntent.setAction(Intent.ACTION_SEND);
		if (mYouTubeVideo !=null){
			myIntent.putExtra(Intent.EXTRA_TEXT, mYouTubeVideo.getUrl());
		}
//		myIntent.putExtra(Intent.EXTRA_TEXT, mYouTubeVideo.getUrl());
		myIntent.setType("text/*");

		myShareActionProvider.setShareIntent(myIntent);
		//return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_reload_video:
				loadVideo();
				return true;
			case R.id.menu_open_video_with:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + mYouTubeVideo.getId()));
				startActivity(browserIntent);
				mVideoView.pause();
				return true;
			case R.id.menu_landscape:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				return true;
			case R.id.menu_portrait:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				return true;
			case R.id.action_share:
				Toast.makeText(getActivity(), "OK", Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onPause() {
		boolean checkScreenOn = false;
		PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        if(Build.VERSION.SDK_INT<21) checkScreenOn = pm.isScreenOn();
        else checkScreenOn = pm.isInteractive();

		if(checkScreenOn) loadVideo();
      //  mVideoView.setOnPreparedListener(PreparedListener);
        super.onPause();
    }

  /*  @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("Position", mVideoView.getCurrentPosition());
        mVideoView.pause();
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        try {
            posi = savedInstanceState.getInt("Position");
            mVideoView.seekTo(posi);
        }
        catch (NullPointerException n){n.printStackTrace();}
        super.onViewStateRestored(savedInstanceState);
    }
*/
    /**
	 * Loads the video specified in {@link #mYouTubeVideo}.
	 */
	private void loadVideo() {
		// get the video's steam
		new GetStreamTask(mYouTubeVideo, true).execute();
		// get the video description
		new GetVideoDescriptionTask().execute();
	}



    ////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Given a YouTubeVideo, it will asynchronously get a list of streams (supplied by YouTube) and
	 * then it asks the mVideoView to start playing a stream.
	 */
	private class GetStreamTask extends AsyncTask<Void, Exception, StreamMetaDataList> {

		/** YouTube Video */
		private YouTubeVideo	youTubeVideo;
		/** The current video position (i.e. time).  Set to -1 if we are not interested in reloading
		 *  the video. */
		private	int				currentVideoPosition = -1;


		public GetStreamTask(YouTubeVideo youTubeVideo) {

            this(youTubeVideo, false);
		}

        @Override
        protected void onPreExecute() {
            if(Build.VERSION.SDK_INT>=16) mVideoView.setBackground(BACK_GROUND_DRAWABLE);
            super.onPreExecute();
        }

        /**
		 * Returns a stream for the given video.  If reloadVideo is set to true, then it will stop
		 * the current video, get a NEW stream and then resume playing.
		 *
		 * @param youTubeVideo	YouTube video
		 * @param reloadVideo	Set to true to reload a video
		 */
		public GetStreamTask(YouTubeVideo youTubeVideo, boolean reloadVideo) {
			this.youTubeVideo = youTubeVideo;

			if (reloadVideo) {
				boolean isVideoPlaying = mVideoView.isPlaying();
				mVideoView.pause();
				this.currentVideoPosition = isVideoPlaying ? mVideoView.getCurrentPosition() : 0;
                //==================================
                posi = currentVideoPosition;
                //======================================
				mVideoView.stopPlayback();
				mLoadingVideoView.setVisibility(View.VISIBLE);
			}
		}


		@Override
		protected StreamMetaDataList doInBackground(Void... param) {
			if(youTubeVideo!=null) return youTubeVideo.getVideoStreamList();
			else return null;
		}


		@Override
		protected void onPostExecute(StreamMetaDataList streamMetaDataList) {
			if (streamMetaDataList == null) {
				// if the stream list is null, then it means an error has occurred
				Toast.makeText(YouTubePlayerFragment.this.getActivity(),
						String.format(getActivity().getString(R.string.error_get_video_streams), youTubeVideo.getId()),
						Toast.LENGTH_LONG).show();
			} else if (streamMetaDataList.size() <= 0) {
				// if steam list if empty, then it means something went wrong...
				Toast.makeText(YouTubePlayerFragment.this.getActivity(),
						String.format(getActivity().getString(R.string.error_video_streams_empty), youTubeVideo.getId()),
						Toast.LENGTH_LONG).show();
			} else {
				Log.i(TAG, streamMetaDataList.toString());

				// get the desired stream based on user preferences
				StreamMetaData desiredStream = streamMetaDataList.getDesiredStream();

				// play the video
				Log.i(TAG, ">> PLAYING: " + desiredStream);
                uri = desiredStream.getUri();
				mVideoView.setVideoURI(desiredStream.getUri());

				// if we are reloading a video... then seek the correct position
				if (currentVideoPosition >= 0) {
					mVideoView.seekTo(currentVideoPosition);
				}
			}
            if(Build.VERSION.SDK_INT>=16)  mVideoView.setBackground(null);
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Get the video's description and set the appropriate text view.
	 */
	private class GetVideoDescriptionTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			GetVideoDescription getVideoDescription = new GetVideoDescription();
			String description = TubeViewApp.getStr(R.string.error_get_video_desc);

			try {
                getVideoDescription.init(mYouTubeVideo.getId());
				List<YouTubeVideo> list = getVideoDescription.getNextVideos();

				if (list.size() > 0) {
					description = list.get(0).getDescription();
				}
			} catch (IOException e) {
				Log.e(TAG, description + " - id=" + mYouTubeVideo.getId(), e);
			}
            catch (NullPointerException n)
            {
                getActivity().finish();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }

			return description;
		}

		@Override
		protected void onPostExecute(String description) {
			mVideoDescriptionTextView.setText(description);
		}

	}


	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * This task will, from the given video URL, get the details of the video (e.g. video name,
	 * likes ...etc).
	 */
	private class GetVideoDetailsTask extends AsyncTask<Void, Void, YouTubeVideo> {

		private String videoUrl = null;


		@Override
		protected void onPreExecute() {
			videoUrl = getUrlFromIntent(getActivity().getIntent());
		}


		/**
		 * Returns an instance of {@link YouTubeVideo} from the given {@link #videoUrl}.
		 *
		 * @return {@link YouTubeVideo}; null if an error has occurred.
		 */
		@Override
		protected YouTubeVideo doInBackground(Void... params) {
			String videoId = getYouTubeIdFromUrl(videoUrl);
			YouTubeVideo youTubeVideo = null;

			if (videoId != null) {
				try {
					GetVideosDetailsByIDs getVideo = new GetVideosDetailsByIDs();
					getVideo.init(videoId);
					List<YouTubeVideo> youTubeVideos = getVideo.getNextVideos();

					if (youTubeVideos.size() > 0)
						youTubeVideo = youTubeVideos.get(0);
				} catch (IOException ex) {
					Log.e(TAG, "Unable to get video details, where id="+videoId, ex);
				}
			}

			return youTubeVideo;
		}


		@Override
		protected void onPostExecute(YouTubeVideo youTubeVideo) {
			if (youTubeVideo == null) {
				String err = String.format(getString(R.string.error_invalid_url), videoUrl);
				Toast.makeText(getActivity(), err, Toast.LENGTH_LONG).show();
				Log.e(TAG, err);
				getActivity().finish();
			} else {
				YouTubePlayerFragment.this.mYouTubeVideo = youTubeVideo;
				setUpHUDAndPlayVideo();	// setup the HUD and play the video

				getVideoInfoTasks();
			}
		}


		/**
		 * The video URL is passed to Tube View via another Android app (i.e. via an intent).
		 *
		 * @return The URL of the YouTube video the user wants to play.
		 */
		private String getUrlFromIntent(final Intent intent) {
			String url = null;

			if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
				url = intent.getData().toString();
			}

			return url;
		}


		/**
		 * Extracts the video ID from the given video URL.
		 *
		 * @param url	YouTube video URL.
		 * @return ID if everything went as planned; null otherwise.
		 */
		private String getYouTubeIdFromUrl(String url) {
			if (url == null)
				return null;

			final String pattern = "(?<=v=|/videos/|embed/|youtu\\.be/|/v/|/e/)[^#&\\?]*";
			Pattern compiledPattern = Pattern.compile(pattern);
			Matcher matcher = compiledPattern.matcher(url);

			return matcher.find() ? matcher.group() /*video id*/ : null;
		}

	}


	////////////////////////////////////////////////////////////////////////////////////////////////


	private class GetYouTubeChannelInfoTask extends AsyncTask<String, Void, YouTubeChannel> {

		private final String TAG = GetYouTubeChannelInfoTask.class.getSimpleName();

		@Override
		protected YouTubeChannel doInBackground(String... channelId) {
			YouTubeChannel chn = new YouTubeChannel();

			try {
				chn.init(channelId[0]);
			} catch (IOException e) {
				Log.e(TAG, "Unable to get channel info.  ChannelID=" + channelId[0], e);
				chn = null;
			}

			return chn;
		}

		@Override
		protected void onPostExecute(YouTubeChannel youTubeChannel) {
			YouTubePlayerFragment.this.mYouTubeChannel = youTubeChannel;
		}

	}

}