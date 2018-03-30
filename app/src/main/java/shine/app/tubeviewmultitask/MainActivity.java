package shine.app.tubeviewmultitask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import shine.app.tubeviewmultitask.CustomViews.CustomSwipeRefresh;
import shine.app.tubeviewmultitask.CustomVolley.AppController;
import shine.tran.localtubeview.R;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static String TAG = "MAIN";
    Activity mainAct;
    WebView youtubeView;
    String currUrl;
    boolean onrotaion = true;
    boolean doubleClickToExit = false;
    //For Result Activity
    public static int OVERLAY_PERMISSION_REQ = 1234;
    String VID, PID;
    //SearchView
    SearchView searchView;
    //Swipe Refresh
    CustomSwipeRefresh swipeRefreshLayout;
    boolean exit = false;

    Button retry, changeSettings, exitApp;

    ViewStub viewStub;
    SharedPreferences sharedPref;
    boolean autoFloating = false;
    BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mainAct = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);
        ButterKnife.bind(this);
        viewStub = (ViewStub) findViewById(R.id.view_stub);
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.FileName), Context.MODE_PRIVATE);
        autoFloating = sharedPref.getBoolean(getString(R.string.autoFloating), true);
        if (isInternetAvailable(mainAct)) {

            viewStub.setLayoutResource(R.layout.content_main);
            viewStub.inflate();

            exit = false;

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //Swipe Refresh WebView
            swipeRefreshLayout = (CustomSwipeRefresh) findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            youtubeView.loadUrl(youtubeView.getUrl());
                        }
                    });
                }
            });


            // after initialization
            swipeRefreshLayout.setCanChildScrollUpCallback(new CustomSwipeRefresh.CanChildScrollUpCallback() {
                @Override
                public boolean canSwipeRefreshChildScrollUp() {
                    return youtubeView.getScrollY() > 0;
                }
            });

            youtubeView = (WebView) findViewById(R.id.youtube_view);
            youtubeView.getSettings().setJavaScriptEnabled(true);
            youtubeView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String str, Bitmap bitmap) {
                    super.onPageStarted(view, str, bitmap);
                    Log.d(TAG + "Main Page Loading ", str);
                    swipeRefreshLayout.setRefreshing(true);

                    currUrl = str;
                }

                @Override
                public void onPageFinished(WebView view, String str) {
                    super.onPageFinished(view, str);
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG + "Main Page Finished", str);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        String url = String.valueOf(str);
                        selectionOpenTypeMode(url);
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains("?app=desktop") && !url.contains("signin?app=desktop")) {
                        Log.d("Url stopped to load : ", url);
                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
                        final Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Desktop View Unavailable", Snackbar.LENGTH_LONG);
                        //Changing Text Color
                        View snkBar = snackbar.getView();
                        TextView tv = (TextView) snkBar.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#e52d27"));
                        snackbar.show();
                        return true;
                    }
                    return false;
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String url = String.valueOf(request.getUrl());
                        selectionOpenTypeMode(url);
                    }
                    return super.shouldInterceptRequest(view, request);
                }
            });
            youtubeView.canGoBack();
            currUrl = "https://m.youtube.com/";
            youtubeView.loadUrl(currUrl);
        } else {

            viewStub.setLayoutResource(R.layout.content_main_no_internet);
            viewStub.inflate();

            exit = true;
            retry = (Button) findViewById(R.id.retry_internet);
            changeSettings = (Button) findViewById(R.id.change_settings);
            exitApp = (Button) findViewById(R.id.exit_app);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainAct.recreate();
                }
            });
            changeSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                }
            });
            exitApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(Constants.MESSAGE);
               Log.e("TAG", s);

                if (!autoFloating) {
                    Fragment fr = VideoPlayerFragment.newInstance();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, fr, Constants.TAG_FR);
                    fragmentTransaction.commit();
                }
            }
        };
    }

    public boolean isServiceRunning(Class<PlayerService> playerServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (playerServiceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void stopService(){
        stopService(new Intent(this, PlayerService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    needPermissionDialog(requestCode);
                } else {
                    Intent i = new Intent(this, PlayerService.class);
                    i.putExtra("VID_ID", VID);
                    i.putExtra("PLAYLIST_ID", PID);
                    Log.e("TAG", VID + "----" + PID);
                    i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
                    startService(i);
                }
            }
        } else if (requestCode == 0) {
            mainAct.recreate();
        }
    }

    private void needPermissionDialog(final int requestCode) {
        if (requestCode == OVERLAY_PERMISSION_REQ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to grant the permission.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(i, OVERLAY_PERMISSION_REQ);
                        }
                    });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG + "Settings", "Act");
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            return;
        }
        Log.d(TAG + "Curr Url", currUrl);
        if (currUrl.equals("https://m.youtube.com/")) {
            if (doubleClickToExit) {
                super.onBackPressed();
                return;
            }

            this.doubleClickToExit = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleClickToExit = false;
                }
            }, 2000);
        } else {
            youtubeView.goBack();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG + "Network Test", "no internet connection");
            return false;
        } else {
            if (info.isConnected()) {
                Log.d(TAG + "Network Test", " internet connection available...");
                return true;
            } else {
                Log.d(TAG + "Network Test", " internet connection");
                return true;
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        youtubeView.loadUrl("http://m.youtube.com/results?q=" + query);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            newText = newText.replace(" ", "+");
            String url = "http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&client=firefox&q="
                    + newText;
            JsonArrayRequest req = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONArray jsonArraySuggestion = (JSONArray) response.get(1);
                                String[] suggestions = new String[10];
                                for (int i = 0; i < 10; i++) {
                                    if (!jsonArraySuggestion.isNull(i)) {
                                        suggestions[i] = jsonArraySuggestion.get(i).toString();
                                    }
                                }
                                Log.d(TAG + "Suggestions", Arrays.toString(suggestions));
                                //Cursor Adaptor
                                String[] columnNames = {"_id", "suggestion"};
                                MatrixCursor cursor = new MatrixCursor(columnNames);
                                String[] temp = new String[2];
                                int id = 0;
                                for (String item : suggestions) {
                                    if (item != null) {
                                        temp[0] = Integer.toString(id++);
                                        temp[1] = item;
                                        cursor.addRow(temp);
                                    }
                                }
                                CursorAdapter cursorAdapter = new CursorAdapter(getApplicationContext(), cursor, false) {
                                    @Override
                                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                        return LayoutInflater.from(context).inflate(R.layout.search_suggestion_list_item, parent, false);
                                    }

                                    @Override
                                    public void bindView(View view, Context context, Cursor cursor) {
                                        final TextView suggest = (TextView) view.findViewById(R.id.suggest);
                                        ImageView putInSearchBox = (ImageView) view.findViewById(R.id.put_in_search_box);
                                        String body = cursor.getString(cursor.getColumnIndexOrThrow("suggestion"));
                                        suggest.setText(body);
                                        suggest.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                searchView.setQuery(suggest.getText(), true);
                                                searchView.clearFocus();
                                            }
                                        });
                                        putInSearchBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                searchView.setQuery(suggest.getText(), false);
                                            }
                                        });
                                    }
                                };
                                searchView.setSuggestionsAdapter(cursorAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG + "Tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(req);

        }
        return true;
    }

    public void selectionOpenTypeMode(String url) {
        autoFloating = sharedPref.getBoolean(getString(R.string.autoFloating), true);
        openViewPlayer(url, autoFloating);
    }

    private void openViewPlayer(String url, final boolean view) {
        if (String.valueOf(url).contains("http://m.youtube.com/watch?") ||
                String.valueOf(url).contains("https://m.youtube.com/watch?")) {
            Log.d(TAG + "Yay Catches!!!! ", url);
            url = String.valueOf(url);
            //Video Id
            VID = url.substring(url.indexOf("&v=") + 3, url.length());
            Log.d(TAG + "VID ", VID);
            //Playlist Id
            final String listID = url.substring(url.indexOf("&list=") + 6, url.length());
            Pattern pattern = Pattern.compile(
                    "([A-Za-z0-9_-]+)&[\\w]+=.*",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(listID.toString());
            Log.d(TAG + "ListID", listID);
            PID = "";
            if (matcher.matches()) {
                PID = matcher.group(1);
            }
            if (listID.contains("m.youtube.com")) {
                Log.d(TAG + "Not a ", "Playlist.");
                PID = null;
            } else {
                Constants.linkType = 1;
                Log.d(TAG + "PlaylistID ", PID);
            }
            Handler handler = new Handler(getMainLooper());
            final String finalPID = PID;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    youtubeView.stopLoading();
                    youtubeView.goBack();
                    if (isServiceRunning(PlayerService.class)) {
                        Log.d(TAG + "Service : ", "Already Running!");
                        PlayerService.startVid(VID, finalPID);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(i, OVERLAY_PERMISSION_REQ);
                        } else {
                            Intent i = new Intent(MainActivity.this, PlayerService.class);
                            i.putExtra("VID_ID", VID);
                            i.putExtra("PLAYLIST_ID", finalPID);
                            i.putExtra("LAYOUT_VIEW", view);
                            i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
                            startService(i);
                        }

//                                    Intent i = new Intent(MainActivity.this, PlayerService.class);
//                                    i.putExtra("VID_ID", VID);
//                                    i.putExtra("PLAYLIST_ID", finalPID);
//                                    i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
//                                    startService(i);
                    }

                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    public void removeFragmentUrlCommit(String VID){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_FR);
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        this.VID = VID;
        autoFloating = sharedPref.getBoolean(getString(R.string.autoFloating), true);
        Intent i = new Intent(MainActivity.this, PlayerService.class);
        i.putExtra("VID_ID", this.VID);
        i.putExtra("PLAYLIST_ID", PID);
        i.putExtra("LAYOUT_VIEW", autoFloating);
        i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
        startService(i);
    }
}