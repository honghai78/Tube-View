package shine.tran.tubeview.businessobjects.db;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.app.TubeViewApp;
import shine.tran.tubeview.gui.businessobjects.SubscribeButton;

/**
 * A task that checks if a user is subscribed to a particular YouTube channel.
 */
public class CheckIfUserSubbedToChannelTask extends AsyncTask<Void, Void, Boolean> {

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
