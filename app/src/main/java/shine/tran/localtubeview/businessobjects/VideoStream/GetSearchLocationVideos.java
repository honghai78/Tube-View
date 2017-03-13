package shine.tran.localtubeview.businessobjects.VideoStream;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.IOException;

import shine.tran.localtubeview.businessobjects.GetYouTubeVideoBySearch;
import shine.tran.localtubeview.gui.activities.MainActivity;

/**
 * Created by Administrator on 04/08/2016.
 * The class return list videos when search with mLocation
 * @{@link GetYouTubeVideoBySearch}
 */
public class GetSearchLocationVideos extends GetYouTubeVideoBySearch{

        @Override
        public void init() throws IOException {
            super.init();
            videosList.setOrder("date");
        }


        /**
         * Set the channel id.
         *
         * @param query Channel ID.
         */
        @Override
        public void setQuery(String query) {
            if (videosList != null) {
                videosList.setQ(query);
                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(MainActivity.ACTIVITY);
                    MainActivity.setInfoLocation(MainActivity.ACTIVITY);
                    videosList.setLocation(MainActivity.LATITUDE + "," + MainActivity.LONGITUDE);
                    videosList.setLocationRadius(MainActivity.RADIUS+"km");
                    if(MainActivity.COUNTRY_NAME.length()>0) Toast.makeText(MainActivity.ACTIVITY, "Search in Location:\n" + MainActivity.COUNTRY_NAME+"\nRadius: "+MainActivity.RADIUS+"km", Toast.LENGTH_LONG).show();
                    else
                    {
                        Toast.makeText(MainActivity.ACTIVITY, "Sorry! Your Location is unable.", Toast.LENGTH_LONG).show();
                        MainActivity.TEST = false;
                    }
            }
        }
}
