package shine.tran.tubeview.gui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.VideoCategory;
import shine.tran.tubeview.gui.activities.MainActivity;
import shine.tran.tubeview.gui.businessobjects.FragmentEx;
import shine.tran.tubeview.gui.businessobjects.VideoGridAdapter;

/**
 * Fragment that will hold a list of videos corresponding to the user's query.
 */
public class SearchVideoGridFragment extends FragmentEx {

    public static GridView mGridView;
    protected VideoGridAdapter mVideoGridAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.videos_gridview, container, false);

        this.mGridView = (GridView) view.findViewById(R.id.grid_view);

        if (mVideoGridAdapter == null) {
            this.mVideoGridAdapter = new VideoGridAdapter(getActivity());

            String searchQuery = getSearchQuery();
            if (searchQuery != null) {
                // set the video category (if the user wants to search)... otherwise it will be set-
                // up by the VideoGridFragment
                if (MainActivity.TEST && MainActivity.COUNTRY_NAME != null) {
                    this.mVideoGridAdapter.setVideoCategory(VideoCategory.SEARCH_QUERY_LOCATION, searchQuery);
                } else
                    this.mVideoGridAdapter.setVideoCategory(VideoCategory.SEARCH_QUERY, searchQuery);

                // set the action bar's title
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null)
                    actionBar.setTitle(searchQuery);
            }
        }

        this.mGridView.setAdapter(this.mVideoGridAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        // setup the SearchView (actionbar)
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_videos));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // collapse the action-bar's search view
                searchView.setQuery("", false);
                searchView.setIconified(true);
                menu.findItem(R.id.menu_search).collapseActionView();

                // run the search activity

                return true;
            }
        });
    }

    private String getSearchQuery() {
        String searchQuery = null;
        Intent intent = getActivity().getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(Intent.ACTION_SEARCH);
            Log.d("SEARCH", "Query=" + searchQuery);
        }

        return searchQuery;
    }

}
