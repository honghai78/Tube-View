package shine.tran.tubeview.gui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.businessobjects.BackActivity;
import shine.tran.tubeview.gui.fragments.SearchVideoGridFragment;

/**
 * Activity that will display videos that meet the search criteria supplied by the user.
 * This activity holds {@link shine.tran.tubeview.gui.fragments.VideosGridFragment}.
 */
public class SearchActivity extends BackActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);


	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
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
                Intent i = new Intent(SearchActivity.this, SearchActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(Intent.ACTION_SEARCH, query);
                //SearchVideoGridFragment.
                startActivity(i);
                onBackPressed();
                return true;
            }
        });

		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_reload_list:
                SearchVideoGridFragment.mGridView.setSelection(0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
