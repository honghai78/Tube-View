package shine.app.tubeviewmultitask;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

/**
 * Created by Shine Tran on 15/3/16.
 */
public class WebPlayer {

    static WebView player;
    static Context context;
    static WebPlayer webPlayer;
    public WebPlayer(Context context) {
        this.player = new WebView(context);
        this.context = context;
    }

    public static WebPlayer getWebViewPlayer(Context c){
        if(webPlayer == null){
            webPlayer = new WebPlayer(c);
        }
        return webPlayer;
    }

    public void setupPlayer() {
        player.getSettings().setJavaScriptEnabled(true);

//         For debugging using chrome on PC
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                player.setWebContentsDebuggingEnabled(true);
            }

        player.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            player.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        player.getSettings().setUserAgentString("Android");

        //----------------------------To get Player Id-------------------------------------------

        player.addJavascriptInterface(new JavaScriptInterface((PlayerService) context), "Interface");
        player.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return true;
                                    }

                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        PlayerService.addStateChangeListener();
                                        player.loadUrl(JavaScript.playVideoScript());
                                    }
                                }
        );
    }

    public static void loadScript(String s) {
        player.loadUrl(s);
    }

    public static WebView getPlayer() {
        return player;
    }

    public void destroy() {
        player.destroy();
        webPlayer = null;
    }

    public void loadDataWithUrl(String baseUrl, String videoHTML, String mimeType, String encoding, String historyUrl) {
        player.loadDataWithBaseURL(baseUrl, videoHTML, mimeType, encoding, historyUrl);
    }
}