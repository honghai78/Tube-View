package shine.tran.localtubeview.gui.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import shine.tran.localtubeview.businessobjects.db.SubscriptionsDb;

/**
 * TubeView application.
 */
public class TubeViewApp extends Application {

	/** TubeView Application instance. */
	private static TubeViewApp TubeViewApp = null;
	private static volatile SubscriptionsDb subscriptionsDb = null;


	@Override
	public void onCreate() {
		super.onCreate();
		TubeViewApp = this;
	}


	/**
	 * Returns a localised string.
	 *
	 * @param stringResId	String resource id (e.g. R.string.my_string)
	 * @return	Localised string, from the strings XML file.
	 */
	public static String getStr(int stringResId) {
		return TubeViewApp.getString(stringResId);
	}


	/**
	 * Returns the App's {@link SharedPreferences}.
	 *
	 * @return {@link SharedPreferences}
	 */
	public static SharedPreferences getPreferenceManager() {
		return PreferenceManager.getDefaultSharedPreferences(TubeViewApp);
	}


	/**
	 * Returns the dimension value that is specified in R.dimens.*.  This value is NOT converted into
	 * pixels, but rather it is kept as it was originally written (e.g. dp).
	 *
	 * @return The dimension value.
	 */
	public static float getDimension(int dimensionId) {
		return TubeViewApp.getResources().getDimension(dimensionId);
	}


	public static synchronized SubscriptionsDb getSubscriptionsDb() {
		if (subscriptionsDb == null) {
			subscriptionsDb = new SubscriptionsDb(TubeViewApp.getBaseContext());
		}

		return subscriptionsDb;
	}

}
