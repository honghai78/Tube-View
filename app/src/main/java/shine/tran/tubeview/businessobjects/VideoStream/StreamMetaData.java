package shine.tran.tubeview.businessobjects.VideoStream;


import android.net.Uri;

/**
 * Represents the meta-data of a YouTube video stream.
 */
 public class StreamMetaData {

	/** URL of the stream */
	private Uri uri;
	/** Video resolution (e.g. 1080p) */
	private VideoResolution resolution;
	/** Video format (e.g. MPEG-4) */
	private MediaFormat format;

	private static final String TAG = StreamMetaData.class.getSimpleName();


	public StreamMetaData(String url, int itag) {
		setUri(url);
		setMediaFormat(itag);
		setResolution(itag);
	}


	private void setUri(String url) {
		this.uri = Uri.parse(url);
	}


	/**
	 * Converts the given itag into {@link MediaFormat}.
	 */
	private void setMediaFormat(int itag) {
		this.format = MediaFormat.itagToMediaFormat(itag);
	}


	/**
	 * Converts the given itag into {@link VideoResolution}.
	 */
	private void setResolution(int itag) {
		this.resolution = VideoResolution.itagToVideoResolution(itag);
	}


	public Uri getUri() {
		return uri;
	}

	public VideoResolution getResolution() {
		return resolution;
	}

	public MediaFormat getFormat() {
		return format;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("URI:  ");
		str.append(uri);
		str.append('\n');

		str.append("FORMAT:  ");
		str.append(format);
		str.append('\n');

		str.append("RESOLUTION:  ");
		str.append(resolution);

		return str.toString();
	}

}
