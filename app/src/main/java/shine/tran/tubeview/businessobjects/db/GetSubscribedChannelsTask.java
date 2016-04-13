package shine.tran.tubeview.businessobjects.db;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

//import shine.tran.tubeview.R;
import shine.tran.tubeview.R;
import shine.tran.tubeview.businessobjects.YouTubeChannel;
import shine.tran.tubeview.gui.app.TubeViewApp;
import shine.tran.tubeview.gui.businessobjects.SubsAdapter;

/**
 * Gets a list of channels that the user is subscribed to and then passes the channels list to
 * the given {@link SubsAdapter}.
 */
public class GetSubscribedChannelsTask extends AsyncTask<Void, Void, List<YouTubeChannel>> {

    private SubsAdapter adapter;

    private static final String TAG = GetSubscribedChannelsTask.class.getSimpleName();


    public GetSubscribedChannelsTask(SubsAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    protected List<YouTubeChannel> doInBackground(Void... params) {
        List<YouTubeChannel> subbedChannelsList = null;

        try {
            subbedChannelsList = TubeViewApp.getSubscriptionsDb().getSubscribedChannels();
        } catch (Throwable tr) {
            Log.e(TAG, "An error has occurred while getting subbed channels", tr);
        }

        return subbedChannelsList;
    }


    @Override
    protected void onPostExecute(List<YouTubeChannel> subbedChannelsList) {
        if (subbedChannelsList == null) {
            Toast.makeText(adapter.getContext(), R.string.error_get_subbed_channels, Toast.LENGTH_LONG).show();
        } else {
            adapter.appendList(subbedChannelsList);
        }
    }

}
