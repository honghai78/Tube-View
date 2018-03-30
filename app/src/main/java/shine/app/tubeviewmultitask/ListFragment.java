package shine.app.tubeviewmultitask;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import shine.tran.localtubeview.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    private String TAG = ListFragment.class.getSimpleName();
    @BindView(R.id.webview)
    WebView webView;
    Unbinder unbinder;
    ProgressDialog progressDialog;
    boolean test = true;
    String VID = "";
    String PID = "";

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        webView.loadUrl("https://m.youtube.com/watch?v=" + ConstantStrings.VID);
        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Force links and redirects to open in the WebView instead of in a browser
        webView.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("TAG", "Finnist");
                setup();
                WebPlayer.loadScript(JavaScript.playVideoScript());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String url = String.valueOf(request.getUrl());
                    if (String.valueOf(url).contains("http://m.youtube.com/watch?") ||
                            String.valueOf(url).contains("https://m.youtube.com/watch?")) {
                        Log.d(TAG + "Yay Catches!!!! ", url);
                        url = String.valueOf(url);
                        //Video Id
                        int temp = url.indexOf("&v=");
                        if(temp>0){
                            VID = url.substring( temp + 3, url.length());
                        }
                        else {
                            VID = ConstantStrings.VID;
                        }
                        Log.d(TAG + "VID ", VID);
                        //Playlist Id
                        temp = url.indexOf("&list=");
                         String listID = "";
                        if(temp>0){
                          listID = url.substring(url.indexOf("&list=") + 6, url.length());
                        }

                        Pattern pattern = Pattern.compile(
                                "([A-Za-z0-9_-]+)&[\\w]+=.*",
                                Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(listID.toString());
                        Log.d(TAG + "ListID", listID);
                        PID = "";
                        if (matcher.matches()) {
                            PID = matcher.group(1);
                        }
                        if (listID.contains("m.youtube.com")) {
                            Log.d(TAG + "Not a ", "Playlist.");
                            PID = null;
                        } else {
                            Constants.linkType = 1;
                            Log.d(TAG + "PlaylistID ", PID);
                        }
                        if(!VID.equals(ConstantStrings.VID)){
                            ((MainActivity)getActivity()).stopService();
                           ((MainActivity)getActivity()).removeFragmentUrlCommit(VID);
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                setup();
                WebPlayer.loadScript(JavaScript.playVideoScript());
            }
        });
        return view;
    }

    public void setup() {
        if(webView!=null)
        webView.loadUrl("javascript:" + "setGround();\n" +
                "                      function setGround() {\n" +
                "                                var n1 = document.getElementById('player');\n" +
                "                                if(n1!=null){\n" +
                "                                  n1.style.opacity = '0';\n" +
                "                                  n1.style.height = '0px !important';\n" +
                "                                }\n" +
                "                                else{\n" +
                "                                  console.log(\"n1\");\n" +
                "                                }\n" +
                "                                var n = document.getElementById('koya_elem_0_11');\n" +
                "                                if(n!=null){\n" +
                "                                  n.style.display = 'none';\n" +
                "                                  n.style.height = '0px !important';\n" +
                "                                }\n" +
                "                                else{\n" +
                "                                  console.log(\"n\");\n" +
                "                                }\n" +
                "                              }");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
