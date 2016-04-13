package shine.tran.tubeview.gui.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.GetFeaturedVideos;
import shine.tran.tubeview.businessobjects.VideoCategory;
import shine.tran.tubeview.gui.app.TubeViewApp;
import shine.tran.tubeview.gui.businessobjects.FragmentEx;
import shine.tran.tubeview.gui.businessobjects.GPSTrack;
import shine.tran.tubeview.gui.businessobjects.SubsAdapter;
import shine.tran.tubeview.gui.businessobjects.VideoGridAdapter;

/**
 * A fragment that will hold a {@link GridView} full of YouTube videos.
 */
@SuppressWarnings("deprecation")
public class VideosGridFragment extends FragmentEx implements ActionBar.OnNavigationListener {

	public static GridView				gridView;
	public static VideoGridAdapter	videoGridAdapter;
	private ListView				subsListView = null;
	private SubsAdapter				subsAdapter = null;
	private ActionBarDrawerToggle	subsDrawerToggle;
	public static VideoCategory videoCategory = null;

	ProgressDialog progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_videos_grid, container, false);

		progressBar = new ProgressDialog(VideosGridFragment.this.getActivity());
		progressBar.setCancelable(true);
		progressBar.setMessage("Loading.....");
		progressBar.show();
		// setup the video grid view
		this.gridView = (GridView) view.findViewById(R.id.grid_view);

		if(videoGridAdapter==null)
			videoGridAdapter = new VideoGridAdapter(VideosGridFragment.this.getActivity());
		this.gridView.setAdapter(this.videoGridAdapter);

		// setup the toolbar / actionbar
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.activity_main_toolbar);
		setSupportActionBar(toolbar);

		// indicate that this fragment has an action bar menu
		setHasOptionsMenu(true);

		DrawerLayout subsDrawerLayout = (DrawerLayout) view.findViewById(R.id.subs_drawer_layout);
		subsDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),
				subsDrawerLayout,
				R.string.app_name,
				R.string.app_name
		);
		subsDrawerToggle.setDrawerIndicatorEnabled(true);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}
		this.subsListView = (ListView) view.findViewById(R.id.subs_drawer);
		if(subsAdapter==null) subsAdapter = SubsAdapter.get(VideosGridFragment.this.getActivity());
		this.subsListView.setAdapter(this.subsAdapter);
		progressBar.dismiss();
		progressBar.cancel();
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

		subsDrawerToggle.syncState();
	}


	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// scroll to the top
		gridView.setSelection(0);
		// set/change the video category
		videoCategory = VideoCategory.getVideoCategory(itemPosition);
		GetFeaturedVideos.TEST=true;
		videoGridAdapter.setVideoCategory(VideoCategory.getVideoCategory(itemPosition));
		return true;	// true means event was handled
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app
		// icon touch event
		if (subsDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
		//return false;
	}

}
