package shine.tran.tubeview.gui.activities;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import shine.tran.tubeview.R;
import shine.tran.tubeview.gui.businessobjects.BackActivity;

public class ChannelBrowserActivity extends BackActivity {

	public static final String CHANNEL_OBJ = "ChannelBrowserActivity.ChannelObj";
	public static final String CHANNEL_ID  = "ChannelBrowserActivity.ChannelID";
	private AdView avBanner;
	private AdRequest adRequest;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_browser);
		avBanner =(AdView)findViewById(R.id.av_banner);
		adRequest = new AdRequest.Builder().build();
		avBanner.loadAd(adRequest);
	}

}
