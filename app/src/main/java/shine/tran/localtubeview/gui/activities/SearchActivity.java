package shine.tran.localtubeview.gui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.db.SubscriptionsDb;
import shine.tran.localtubeview.gui.businessobjects.ArrayAdapterSearchView;
import shine.tran.localtubeview.gui.businessobjects.BackActivity;
import shine.tran.localtubeview.gui.fragments.SearchVideoGridFragment;

/**
 * Activity that will display videos that meet the search criteria supplied by the user.
 * This activity holds {@link shine.tran.localtubeview.gui.fragments.VideosGridFragment}.
 */
public class SearchActivity extends BackActivity {
    private List array;
    private SubscriptionsDb subscriptionsDb;
    private AdView avBanner;
    private AdRequest adRequest;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
        avBanner =(AdView)findViewById(R.id.av_banner);
        adRequest = new AdRequest.Builder().build();
        avBanner.loadAd(adRequest);
        subscriptionsDb= new SubscriptionsDb(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
        array = subscriptionsDb.getStringDataSearch();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.autocomplete_item, array);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);
		// setup the SearchView (actionbar)
		final MenuItem searchItem = menu.findItem(R.id.menu_search);
		final ArrayAdapterSearchView searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search_videos));
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchView.setQuery(adapter.getItem(position).toString(), false);

            }

        });
        searchView.setAdapter(adapter);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array = subscriptionsDb.getStringDataSearch();
                searchView.setQuery(getSupportActionBar().getTitle(), false);
            }
        });
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
                if(!subscriptionsDb.checkStringDataSearch(array, query)) subscriptionsDb.stringSearch(query);
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
