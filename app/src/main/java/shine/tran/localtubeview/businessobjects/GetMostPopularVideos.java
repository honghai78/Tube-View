package shine.tran.localtubeview.businessobjects;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.Calendar;

/**
 * Get today's most popular YouTube videos.
 */
public class GetMostPopularVideos extends GetYouTubeVideoBySearch {

	private static final String	TAG = GetMostPopularVideos.class.getSimpleName();

	@Override
	public void init() throws IOException {
		super.init();
		videosList.setPublishedAfter(getOneDayBefore());
		videosList.setOrder("viewCount");
	}


	/**
	 * Returns a date that is 24 hours in the past.
	 *
	 * @return 24 hours ago ({@link DateTime})
	 */
	private DateTime getOneDayBefore() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);

		String dateRFC3339 = String.format("%d-%02d-%02dT%02d:%02d:%02dZ",
												calendar.get(Calendar.YEAR),
												calendar.get(Calendar.MONTH)+1,
												calendar.get(Calendar.DAY_OF_MONTH),
												calendar.get(Calendar.HOUR_OF_DAY),
												calendar.get(Calendar.MINUTE),
												calendar.get(Calendar.SECOND));
		Log.d(TAG, "24 Hours before: " + dateRFC3339);
		return new DateTime(dateRFC3339);
	}

}
