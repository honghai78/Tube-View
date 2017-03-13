package shine.tran.localtubeview.businessobjects;

import com.google.api.client.util.DateTime;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

/**
 * An extension of {@link PrettyTime}.
 */
public class PrettyTimeEx extends PrettyTime {

	/**
	 * It will convert {@link DateTime} to a pretty string.
	 *
	 * @see PrettyTime#format(Date)
	 */
	public String format(DateTime dateTime) {
		Long unixEpoch = dateTime.getValue();
		Date date = new Date(unixEpoch);
		return format(date);
	}

}
