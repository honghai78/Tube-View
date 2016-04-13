package shine.tran.tubeview.gui.activities;

import android.os.Bundle;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.businessobjects.BackActivity;

public class ChannelBrowserActivity extends BackActivity {

	public static final String CHANNEL_OBJ = "ChannelBrowserActivity.ChannelObj";
	public static final String CHANNEL_ID  = "ChannelBrowserActivity.ChannelID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_browser);
	}

}
