package shine.tran.tubeview.gui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.GetFeaturedVideos;
import shine.tran.tubeview.businessobjects.VideoCategory;
import shine.tran.tubeview.gui.businessobjects.FragmentEx;
import shine.tran.tubeview.gui.businessobjects.SubsAdapter;
import shine.tran.tubeview.gui.businessobjects.VideoGridAdapter;

/**
 * A fragment that will hold a {@link GridView} full of YouTube videos.
 */
@SuppressWarnings("deprecation")
public class VideosGridFragment extends FragmentEx implements ActionBar.OnNavigationListener {

	public static GridView mGridView;
	public static VideoGridAdapter mVideoGridAdapter;
	private ListView               mSubsListView = null;
	private SubsAdapter            mSubsAdapter = null;
	private ActionBarDrawerToggle  mSubsDrawerToggle;
	public static VideoCategory mVideoCategory = null;

	private ProgressDialog mProgressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_videos_grid, container, false);

		mProgressBar = new ProgressDialog(VideosGridFragment.this.getActivity());
		mProgressBar.setCancelable(true);
		mProgressBar.setMessage("Loading.....");
		mProgressBar.show();
		// setup the video grid view
		this.mGridView = (GridView) view.findViewById(R.id.grid_view);

		if(mVideoGridAdapter ==null)
			mVideoGridAdapter = new VideoGridAdapter(VideosGridFragment.this.getActivity());
		this.mGridView.setAdapter(this.mVideoGridAdapter);

		// setup the toolbar / actionbar
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.activity_main_toolbar);
		setSupportActionBar(toolbar);

		// indicate that this fragment has an action bar menu
		setHasOptionsMenu(true);

		DrawerLayout subsDrawerLayout = (DrawerLayout) view.findViewById(R.id.subs_drawer_layout);
		mSubsDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				subsDrawerLayout,
				R.string.app_name,
				R.string.app_name
		);
		mSubsDrawerToggle.setDrawerIndicatorEnabled(true);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}
		this.mSubsListView = (ListView) view.findViewById(R.id.subs_drawer);
		if(mSubsAdapter ==null) mSubsAdapter = SubsAdapter.get(VideosGridFragment.this.getActivity());
		this.mSubsListView.setAdapter(this.mSubsAdapter);
		mProgressBar.dismiss();
		mProgressBar.cancel();
		return view;

	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			SpinnerAdapter spinnerAdapter =
					ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.video_categories,
							android.R.layout.simple_spinner_dropdown_item);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setListNavigationCallbacks(spinnerAdapter, this);
		}

		mSubsDrawerToggle.syncState();
	}


	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// scroll to the top
		mGridView.setSelection(0);
		// set/change the video category
		mVideoCategory = VideoCategory.getVideoCategory(itemPosition);
		GetFeaturedVideos.TEST=true;
		mVideoGridAdapter.setVideoCategory(VideoCategory.getVideoCategory(itemPosition));
		return true;	// true means event was handled
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app
		// icon touch event
		if (mSubsDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
		//return false;
	}

}
