package shine.tran.tubeview.gui.businessobjects;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RemoteViews;

import shine.tran.tubeview.R;

/**
 * The (channel) subscribe button.
 */
@RemoteViews.RemoteView
public class SubscribeButton extends Button {

	/** Is user subscribed to a channel? */
	private boolean isUserSubscribed = false;


	public SubscribeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public boolean isUserSubscribed() {
		return isUserSubscribed;
	}


	/**
	 * Set the button's state to subscribe (i.e. once clicked, the user indicates that he wants to
	 * subscribe).
	 */
	public void setSubscribeState() {
		setText(R.string.subscribe);
		isUserSubscribed = false;	// the user is currently NOT subscribed
	}


	/**
	 * Set the button's state to unsubscribe (i.e. once clicked, the user indicates that he wants to
	 * unsubscribe).
	 */
	public void setUnsubscribeState() {
		setText(R.string.unsubscribe);
		isUserSubscribed = true;
	}

}
