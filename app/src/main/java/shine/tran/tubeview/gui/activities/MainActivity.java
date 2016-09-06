package shine.tran.tubeview.gui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.db.SubscriptionsDb;
import shine.tran.tubeview.gui.app.TubeViewApp;
import shine.tran.tubeview.gui.businessobjects.ArrayAdapterSearchView;
import shine.tran.tubeview.gui.businessobjects.GPSTrack;
import shine.tran.tubeview.gui.fragments.PreferencesFragment;
import shine.tran.tubeview.gui.fragments.VideosGridFragment;

/**
 * Main activity (launcher).  This activity holds {@link shine.tran.tubeview.gui.fragments.VideosGridFragment}.
 */
public class MainActivity extends AppCompatActivity {
    public static String COUNTRY_CODE_VALUE = "";
    public static boolean TEST = false;
    public static double LATITUDE = 0;
    public static double LONGITUDE = 0;
    public static Activity ACTIVITY = null;
    public static String COUNTRY_NAME = "";
    ProgressDialog progressBar;
    public static String RADIUS = "";
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    SharedPreferences sharedPref = null;
    SubscriptionsDb subscriptionsDb;
    private List array;
    private ArrayAdapterSearchView searchView;
     ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_main);
        new InternetCheck().execute(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        RADIUS = sharedPref.getString(this.getString(R.string.pref_key_use_radius), "1000");

        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Loading.....");
        progressBar.show();

        //==================================================================
        TEST = sharedPref.getBoolean(MainActivity.this.getString(R.string.pref_key_use_location), false);
        if (Build.VERSION.SDK_INT > 22) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                TEST = false;
            }
        }
        //======================================================================
        GPSTrack gps = new GPSTrack(MainActivity.this);
        if (gps.canGetLocation()) {
            COUNTRY_CODE_VALUE = gps.getRegionCode(MainActivity.this);
            gps.stopUsingGPS();
        }
        ACTIVITY = MainActivity.this;
        if (COUNTRY_CODE_VALUE == null) {
            TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(MainActivity.this.TELEPHONY_SERVICE);
            String country = tm.getNetworkCountryIso();
            //if phone no SIM then get country from mLocation phone
            if (country == null || country.length() < 2) {
                country = getResources().getConfiguration().locale.getCountry();
            }

            COUNTRY_CODE_VALUE = country.toUpperCase();

        } else

        {
            TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(MainActivity.this.TELEPHONY_SERVICE);
            String country = tm.getNetworkCountryIso();
            //if phone no SIM then get country from mLocation phone
            if (country == null || country.length() < 2) {
                country = getResources().getConfiguration().locale.getCountry();
            }
            COUNTRY_CODE_VALUE = country.toUpperCase();
        }
        showRegion();
        //===============================================================
        subscriptionsDb = new SubscriptionsDb(getBaseContext());
        array=subscriptionsDb.getStringDataSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
       // array = subscriptionsDb.getStringDataSearch();

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        // setup the SearchView (actionbar)
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
         searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(searchItem);
        // final SearchView.SearchAutoComplete = ()
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array=subscriptionsDb.getStringDataSearch();
            }
        });
        //==========================================

        //====================================================

        searchView.setQueryHint(getString(R.string.search_videos));
       adapter = new ArrayAdapter<String>
                (this, R.layout.autocomplete_item, array);

        searchView.setAdapter(adapter);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchView.setQuery(adapter.getItem(position).toString(), false);

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
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(Intent.ACTION_SEARCH, query);
                startActivity(i);

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
                showRegion();
                VideosGridFragment.mGridView.setSelection(0);
                VideosGridFragment.mVideoGridAdapter.setVideoCategory(VideosGridFragment.mVideoCategory);
                VideosGridFragment.mVideoGridAdapter.reLoad();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostResume(){
            array = subscriptionsDb.getStringDataSearch();

            if (PreferencesFragment.RE_LOAD) {
                PreferencesFragment.RE_LOAD = false;
                showRegion();
                VideosGridFragment.mGridView.setSelection(0);
                VideosGridFragment.mVideoGridAdapter.setVideoCategory(VideosGridFragment.mVideoCategory);
                VideosGridFragment.mVideoGridAdapter.reLoad();
            }
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        array = subscriptionsDb.getStringDataSearch();
      //  adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        array = subscriptionsDb.getStringDataSearch();
        //adapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        //======================
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }


    private void showRegion() {
        String region = TubeViewApp.getPreferenceManager()
                .getString(TubeViewApp.getStr(R.string.pref_key_preferred_region), "").trim();
        if ((region.length() < 1) || (region.equals("AUTO")))
            region = COUNTRY_CODE_VALUE + " (Automatic)";
        Toast.makeText(MainActivity.this, "Country code is: " + region, Toast.LENGTH_LONG).show();
    }

    public static void setInfoLocation(Activity activity) {
        GPSTrack gps = new GPSTrack(activity);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            String countryName = gps.getRegionName(activity);
            MainActivity.COUNTRY_NAME = countryName;
            MainActivity.LATITUDE = gps.getLatitude();
            MainActivity.LONGITUDE = gps.getLongitude();
            RADIUS = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.pref_key_use_radius), "1000");
            gps.stopUsingGPS();
        }

    }


    class InternetCheck extends AsyncTask<Activity, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Activity... activitys) {

            return isNetworkAvailable(activitys[0]);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.cancel();
            if (!result) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.ACTIVITY);

                alertDialog.setCancelable(false);
                // Setting Dialog Title
                alertDialog.setTitle("Network Not Connect");

                // Setting Dialog Message
                alertDialog.setMessage("Network is not enabled. Do you want to go to settings menu?");

                // On pressing Settings button
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                });

                alertDialog.setNegativeButton("Try Reload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                // on pressing cancel button
                alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        }

        public boolean isNetworkAvailable(Context context) {
            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        }

    }

}