package shine.tran.localtubeview.businessobjects.db;

import android.widget.Toast;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.AsyncTaskParallel;
import shine.tran.localtubeview.businessobjects.YouTubeChannel;
import shine.tran.localtubeview.gui.app.TubeViewApp;
import shine.tran.localtubeview.gui.businessobjects.SubsAdapter;
import shine.tran.localtubeview.gui.businessobjects.SubscribeButton;

/**
 * A task that subscribes / unsubscribes to a YouTube channel.
 */
public class SubscribeToChannelTask extends AsyncTaskParallel<Void, Void, Boolean> {

    /**
     * Set to true if the user wants to subscribe to a youtube channel;  false if the user wants to
     * unsubscribe.
     */
    private boolean subscribeToChannel;
    private SubscribeButton subscribeButton;
    private YouTubeChannel channel;

    private static String TAG = SubscribeToChannelTask.class.getSimpleName();


    /**
     * Constructor.
     *
     * @param subscribeButton The subscribe button that the user has just clicked.
     * @param channel         The channel the user wants to subscribe / unsubscribe.
     */
    public SubscribeToChannelTask(SubscribeButton subscribeButton, YouTubeChannel channel) {
        this.subscribeToChannel = !subscribeButton.isUserSubscribed();
        this.subscribeButton = subscribeButton;
        this.channel = channel;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        if (subscribeToChannel) {
            return TubeViewApp.getSubscriptionsDb().subscribe(channel.getId());
        } else {
            return TubeViewApp.getSubscriptionsDb().unsubscribe(channel.getId());
        }
    }


    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            SubsAdapter adapter = SubsAdapter.get(subscribeButton.getContext());

            if (subscribeToChannel) {
                // change the state of the button
                subscribeButton.setUnsubscribeState();

                // append the channel to the SubsAdapter (i.e. the channels subscriptions list/drawer)
                adapter.appendChannel(channel);

                Toast.makeText(subscribeButton.getContext(), R.string.subscribed, Toast.LENGTH_LONG).show();
            } else {
                // change the state of the button
                subscribeButton.setSubscribeState();

                // remove the channel from the SubsAdapter (i.e. the channels subscriptions list/drawer)
                adapter.removeChannel(channel);

                Toast.makeText(subscribeButton.getContext(), R.string.unsubscribed, Toast.LENGTH_LONG).show();
            }
        } else {
            String err = String.format(TubeViewApp.getStr(R.string.error_unable_to_subscribe), channel.getId());
            Toast.makeText(subscribeButton.getContext(), err, Toast.LENGTH_LONG).show();
        }
    }

}
