package shine.tran.tubeview.businessobjects.VideoStream;

import android.util.Log;

import java.util.ArrayList;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.app.TubeViewApp;

/**
 * A list of {@link StreamMetaData}.
 */
public class StreamMetaDataList extends ArrayList<StreamMetaData> {

	private static final String TAG = StreamMetaDataList.class.getSimpleName();

	/**
	 * Returns the stream desired by the user (if possible).  The desired stream is defined in the
	 * app preferences.  If the video does NOT contain the desired stream, then it tries to return
	 * a stream with a slighter lower resolution.
	 *
	 * @return The desired {@link StreamMetaData}.
	 */
	public StreamMetaData getDesiredStream() {
		VideoResolution desiredVideoRes = getDesiredVideoResolution();
		Log.d(TAG, "Desired Video Res:  " + desiredVideoRes);
		return getDesiredStream(desiredVideoRes);
	}



	/**
	 * Gets the desired stream recursively.
	 *
	 * @param desiredVideoRes	The desired video resolution (as defined in the app preferences).
	 *
	 * @return The desired {@link StreamMetaData}.
	 */
	private StreamMetaData getDesiredStream(VideoResolution desiredVideoRes) {
		if (desiredVideoRes == VideoResolution.RES_UNKNOWN) {
			Log.w(TAG, "No video with the following res could be found: " + desiredVideoRes);
			return get(0);
		}

		for (StreamMetaData streamMetaData : this) {
			if (streamMetaData.getResolution() == desiredVideoRes)
				return streamMetaData;
		}

		return getDesiredStream(desiredVideoRes.getLowerVideoResolution());
	}



	/**
	 * Gets the desired video resolution as defined by the user in the app preferences.
	 *
	 * @return Desired {@link VideoResolution}.
	 */
	private VideoResolution getDesiredVideoResolution() {
		String resIdValue = TubeViewApp.getPreferenceManager()
							.getString(TubeViewApp.getStr(R.string.pref_key_preferred_res),
										Integer.toString(VideoResolution.DEFAULT_VIDEO_RES_ID));

		return VideoResolution.videoResIdToVideoResolution(resIdValue);
	}



	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		for (StreamMetaData streamMetaData : this) {
			out.append(streamMetaData.toString());
			out.append('\n');
		}

		return out.toString();
	}
}
