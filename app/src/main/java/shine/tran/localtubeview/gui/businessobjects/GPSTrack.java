package shine.tran.localtubeview.gui.businessobjects;

/**
 * Created by Administrator on 04/05/2016.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;

public class GPSTrack extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // mLocation
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTrack(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get mLocation from Network Provider
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                    }
                }
            }


        }
        catch (SecurityException r)
        {

        }
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(GPSTrack.this);
            }
        }
        catch (SecurityException e){e.printStackTrace();}
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public boolean test=false;
    public boolean showSettingsAlert(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                test = true;
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                test = false;
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        return test;
    }

    /**
     * Function to return region code
     * @return String
     */
    public String getRegionCode(Context context)
    {
        Geocoder gcd = new Geocoder(context);
        String code = null;

        try {
            code = gcd.getFromLocation(getLatitude(), getLongitude(), 1).get(0).getCountryCode();
        }
        catch (IOException i)
        {
            return null;
        }
        catch (IndexOutOfBoundsException i)
        {
            return null;
        }
        return code;
    }


    /**
     * Function to return region name
     * @return String
     */
    public String getRegionName(Context context)
    {
        Geocoder gcd = new Geocoder(context);
        String code = null;


        try {
            Address temp = gcd.getFromLocation(getLatitude(), getLongitude(), 1).get(0);
            String featureName = "";
            String subAdminArea = "";
            String adminArea = "";
            if(temp.getFeatureName()!=null) featureName = temp.getFeatureName()  +", ";
            if(temp.getSubAdminArea()!=null) subAdminArea = temp.getSubAdminArea() + ", ";
            if(temp.getAdminArea()!=null) adminArea = temp.getAdminArea()+ ", ";

            code = featureName + subAdminArea + adminArea + temp.getCountryName();

            if(code==null || code.length()<1)
            {
                if(location!=null)
                    code = getLatitude()+", "+getLongitude();
            }

        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
        catch (IndexOutOfBoundsException i)
        {
            return null;
        }
        return code;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
      //  MainActivity.TEST = false;
    }

    @Override
    public void onProviderEnabled(String provider) {
       // if(PreferencesFragment.mLocation.isChecked())
         //   MainActivity.TEST = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

