package shine.tran.localtubeview.businessobjects.db;

import android.util.Log;
import android.widget.Toast;

import java.util.List;

//import shine.tran.tubeview.R;
import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.AsyncTaskParallel;
import shine.tran.localtubeview.businessobjects.YouTubeChannel;
import shine.tran.localtubeview.gui.app.TubeViewApp;
import shine.tran.localtubeview.gui.businessobjects.SubsAdapter;

/**
 * Gets a list of channels that the user is subscribed to and then passes the channels list to
 * the given {@link SubsAdapter}.
 */
public class GetSubscribedChannelsTask extends AsyncTaskParallel<Void, Void, List<YouTubeChannel>> {

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
