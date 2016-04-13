package shine.tran.tubeview.gui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.widget.Toast;

import shine.tran.tubeview.BuildConfig;
import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.VideoStream.VideoResolution;
import shine.tran.tubeview.gui.activities.MainActivity;
import shine.tran.tubeview.gui.businessobjects.GPSTrack;

/**
 * A fragment that allows the user to change the settings of this app.  This fragment is called by
 * {@link shine.tran.tubeview.gui.activities.PreferencesActivity}
 */
public class PreferencesFragment extends PreferenceFragment {

    private GPSTrack mGps = null;
    public static CheckBoxPreference mLocation = null;
    public static boolean RE_LOAD = false;
    private String mRegion = "";
    private ListPreference mRegionPref;
    private static final String TAG = PreferencesFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference resolutionPref = (ListPreference) findPreference(getString(R.string.pref_key_preferred_res));
        resolutionPref.setEntries(VideoResolution.getAllVideoResolutionsNames());
        resolutionPref.setEntryValues(VideoResolution.getAllVideoResolutionsIds());
        //Save mRegion when setting is enable
        mRegionPref = (ListPreference) findPreference(getString(R.string.pref_key_preferred_region));
        // if the user clicks on the author, then open the display the actual author
        Preference authorPref = findPreference(getString(R.string.pref_key_author));
        authorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayAppAuthor();
                return true;
            }
        });

        mLocation = (CheckBoxPreference) findPreference(getString(R.string.pref_key_use_location));

        if (Build.VERSION.SDK_INT > 22) {
            mLocation.setChecked(false);
            mLocation.setEnabled(false);
            Toast.makeText(MainActivity.ACTIVITY, "SORRY: \nThis version of the Tube View , we do not support GPS on Android 6.0 Marshmallow", Toast.LENGTH_LONG).show();
        } else {
            mLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mGps = new GPSTrack(getActivity());
                    if (mLocation.isChecked()) {

                        // check if GPS enabled
                        if (mGps.canGetLocation()) {

                            //  double latitude = mGps.getLatitude();
                            //double longitude = mGps.getLongitude();
                            mLocation.setChecked(true);
                            // \n is for new line
                            String countryName = mGps.getRegionName(getActivity());
                            MainActivity.COUNTRY_NAME = countryName;
                            MainActivity.LATITUDE = mGps.getLatitude();
                            MainActivity.LONGITUDE = mGps.getLongitude();
                            MainActivity.TEST = true;
                            mGps.stopUsingGPS();
                            if (countryName != null)
                                Toast.makeText(getActivity().getApplicationContext(), "Your Location is:\n" + countryName, Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getActivity().getApplicationContext(), "Unable to determine your mLocation", Toast.LENGTH_LONG).show();
                        } else {
                            // can't get mLocation
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                            // Setting Dialog Title
                            alertDialog.setTitle("GPS is settings");

                            // Setting Dialog Message
                            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

                            // On pressing Settings button
                            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mLocation.setChecked(true);
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    getActivity().startActivity(intent);
                                }
                            });

                            // on pressing cancel button
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mLocation.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                            // Showing Alert Message
                            alertDialog.show();
                            //======================
                        }

                    } else {
                        MainActivity.TEST = false;
                        mGps.stopUsingGPS();
                    }

                    return false;
                }
            });
        }
        // if the user clicks on the license, then open the display the actual license
        Preference licensePref = findPreference(getString(R.string.pref_key_license));
        licensePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayAppLicense();
                return true;
            }
        });

        // remove the 'use official player' checkbox if we are running an OSS version
        if (BuildConfig.FLAVOR.equals("oss")) {
            PreferenceCategory videoPlayerCategory = (PreferenceCategory) findPreference(getString(R.string.pref_key_video_player_category));
            Preference useOfficialPlayer = findPreference(getString(R.string.pref_key_use_offical_player));
            videoPlayerCategory.removePreference(useOfficialPlayer);
        }

        // set the app's version number
        Preference versionPref = findPreference(getString(R.string.pref_key_version));
        versionPref.setSummary(BuildConfig.VERSION_NAME);
    }


    /**
     * Displays the app's license in an AlertDialog.
     */
    private void displayAppLicense() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.app_license)
                .setNeutralButton(R.string.i_agree, null)
                .setCancelable(false)    // do not allow the user to click outside the dialog or press the back button
                .show();
    }

    /**
     * Displays the app's author in an AlertDialog.
     */
    private void displayAppAuthor() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.app_author)
                .setNeutralButton(R.string.i_agree, null)
                .setCancelable(false)    // do not allow the user to click outside the dialog or press the back button
                .show();
    }

    @Override
    public void onStart() {
        mRegion = mRegionPref.getValue();
        if (Build.VERSION.SDK_INT < 23) {
            mGps = new GPSTrack(getActivity());
            if (mGps.canGetLocation()) {
                Toast.makeText(getActivity(), "GPS is enabled", Toast.LENGTH_LONG).show();
                MainActivity.TEST = true;
                mGps.stopUsingGPS();
                //mLocation.setChecked(true);
            } else {
                Toast.makeText(getActivity(), "GPS is NOT enabled", Toast.LENGTH_LONG).show();
                mLocation.setChecked(false);
                MainActivity.TEST = false;
            }
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        if (!mRegionPref.getValue().equals(mRegion))
            RE_LOAD = true;
        super.onPause();
    }
}
