package shine.app.tubeviewmultitask;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import shine.tran.localtubeview.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.layout_player)
    RelativeLayout layoutPlayer;
    Unbinder unbinder;
    private WebPlayer webPlayer;
    private WindowManager.LayoutParams parWebView;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void init() {
        webPlayer = WebPlayer.getWebViewPlayer(getActivity());
        //Web Player Params
        parWebView = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        View playerViewTemp = webPlayer.getPlayer();
        ViewGroup par = (ViewGroup) playerViewTemp.getParent();
        if (par != null)
            par.removeView(playerViewTemp);
        layoutPlayer.addView(playerViewTemp, parWebView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fullScreen();
    }

    private void fullScreen() {
        if (!FullscreenWebPlayer.active && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            webPlayer.loadScript(JavaScript.pauseVideoScript());
            Intent fullScreenIntent = new Intent(getActivity(), FullscreenWebPlayer.class);
            fullScreenIntent.putExtra(Constants.SEND_BACK_PLAYER, true);
            fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(fullScreenIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        layoutPlayer.removeAllViews();
        init();
    }

    @OnClick(R.id.fullscreen)
    public void fullScreenOnClick(View v) {
        if (!FullscreenWebPlayer.active) {
            webPlayer.loadScript(JavaScript.pauseVideoScript());
            Intent fullScreenIntent = new Intent(getActivity(), FullscreenWebPlayer.class);
            fullScreenIntent.putExtra(Constants.SEND_BACK_PLAYER, true);
            fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(fullScreenIntent);
        }
    }

    @OnClick(R.id.entire_width)
    public void floating(View v) {
        Intent i = new Intent(getActivity(), PlayerService.class);
        i.putExtra("VID_ID", ConstantStrings.VID);
        i.putExtra("PLAYLIST_ID", ConstantStrings.PLIST);
        i.putExtra("LAYOUT_VIEW", true);
        i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
        getActivity().startService(i);
    }
}
