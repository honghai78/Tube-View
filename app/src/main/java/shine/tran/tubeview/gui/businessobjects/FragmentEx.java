package shine.tran.tubeview.gui.businessobjects;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * An extension of {@link Fragment} in which a fragment instance is retained across Activity
 * re-creation (e.g. after device rotation EditText content is not lost).
 */
public class FragmentEx extends Fragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
	}


	protected AppCompatActivity getAppCompatActivity() {
		return (AppCompatActivity) getActivity();
	}


	/**
	 * @return Instance of {@link ActionBar}.
	 */
	protected ActionBar getSupportActionBar() {
		// The Fragment might not always get completely destroyed after Activity.finish(), hence
		// this code might get called after the hosting activity is destroyed.  Therefore we need
		// to handle getActivity() properly.  Refer to:  http://stackoverflow.com/a/21886594/3132935
		AppCompatActivity activity = getAppCompatActivity();
		return (activity != null ? activity.getSupportActionBar() : null);
	}


	/**
	 * Set a {@link Toolbar} to act as the {@link ActionBar}.
	 *
	 * @param toolbar	Toolbar to set as the Activity's action bar, or null to clear it.
	 */
	protected void setSupportActionBar(Toolbar toolbar) {
		AppCompatActivity activity = getAppCompatActivity();

		if (activity != null) {
			activity.setSupportActionBar(toolbar);
		}
	}

}
