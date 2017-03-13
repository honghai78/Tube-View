package shine.tran.localtubeview.businessobjects.db;

import android.util.Log;
import android.widget.Toast;

import shine.tran.localtubeview.R;
import shine.tran.localtubeview.businessobjects.AsyncTaskParallel;
import shine.tran.localtubeview.gui.app.TubeViewApp;
import shine.tran.localtubeview.gui.businessobjects.SubscribeButton;

/**
 * A task that checks if a user is subscribed to a particular YouTube channel.
 */
public class CheckIfUserSubbedToChannelTask extends AsyncTaskParallel<Void, Void, Boolean> {

    private SubscribeButton subscribeButton;
    private String channelId;

    private static String TAG = CheckIfUserSubbedToChannelTask.class.getSimpleName();


    /**
     * Constructor.
     *
     * @param subscribeButton The subscribe button that the user has just clicked.
     * @param channelId       The channel ID the user wants to subscribe / unsubscribe.
     */
    public CheckIfUserSubbedToChannelTask(SubscribeButton subscribeButton, String channelId) {
        this.subscribeButton = subscribeButton;
        this.channelId = channelId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean isUserSubbed;

        try {
            isUserSubbed = TubeViewApp.getSubscriptionsDb().isUserSubscribedToChannel(channelId);
        } catch (Throwable tr) {
            Log.e(TAG, "Unable to check if user has subscribed to channel id=" + channelId, tr);
            isUserSubbed = null;
        }

        return isUserSubbed;
    }

    @Override
    protected void onPostExecute(Boolean isUserSubbed) {
        if (isUserSubbed == null) {
            String err = String.format(TubeViewApp.getStr(R.string.error_check_if_user_has_subbed), channelId);
            Toast.makeText(subscribeButton.getContext(), err, Toast.LENGTH_LONG).show();
        } else if (isUserSubbed) {
            subscribeButton.setUnsubscribeState();
        } else {
            subscribeButton.setSubscribeState();
        }
    }

}
