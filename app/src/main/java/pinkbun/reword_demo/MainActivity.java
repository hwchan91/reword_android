package pinkbun.reword_demo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends FragmentActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      These lines makes the status bar transparent and the website will take up the whole length of the display, Work for Android versions post-KitKat; uncomment if needed
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        WebViewClient webViewClient = new MyWebViewClient();
        mWebView.setWebViewClient(webViewClient);

        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U;` Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        loadCorrectUrl();

        mWebView.addJavascriptInterface(new Object()
        {
            @JavascriptInterface
            public void redirect()
            {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadCorrectUrl();

                    }
                });
            }
        }, "browser");

        if (getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void loadCorrectUrl() {
        if (!DetectConnection.checkInternetConnection(this)) {
            mWebView.loadUrl("file:///android_asset/landing.html");
        } else {
            mWebView.loadUrl("http://rewordgame.herokuapp.com"); //change
        }
    }

    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Uri uri = Uri.parse(url);
            return handleUri(uri);
        }

        @TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return handleUri(uri);
        }

        public boolean handleUri(final Uri uri) {
            final String host = uri.getHost();
            final String scheme = uri.getScheme();
            if (host.endsWith("herokuapp.com")) { //change the host url to match your website
                return false;
            } else {
                final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
        int currIndex = mWebBackForwardList.getCurrentIndex();
        WebHistoryItem item = mWebBackForwardList.getItemAtIndex(currIndex - 1);
        String previousUrl = item.getUrl();
        if (currIndex > 0 && !previousUrl.equals("file:///android_asset/landing.html")) {
            mWebView.goBack();
        } else {
            moveTaskToBack(true);
        }
    }

}
