package me.hasan.smsapplication;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private WebView webView;
    private ConnectionDetector detector;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipe;
    private static String url = "https://soft.smartupworld.com/Codeigniter/ekattor//";
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        webInit();

    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    public void webInit() {
        webView = (WebView) findViewById(R.id.webView);
        detector = new ConnectionDetector(this);
        final WebSettings webSettings = webView.getSettings();

        webView.setWebViewClient(new myWebViewClient());
        webView.setWebChromeClient(new GoogleClient());
        webView.loadUrl(url);
        detector.connection();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String urlName, String userAgent, String contentDescription, String mimeType, long length) {

                String urlNew = webView.getUrl();
                String fileName = URLUtil.guessFileName(urlName, contentDescription,mimeType);
                String cookies = CookieManager.getInstance().getCookie(urlName);
                DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(urlName));
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                downloadRequest.setMimeType(mimeType);
                downloadRequest.addRequestHeader("cookie",cookies);
                downloadRequest.setDescription("Downloading ...");
                downloadRequest.setDestinationInExternalFilesDir(Home.this,Environment.DIRECTORY_DOWNLOADS,".pdf");
//                downloadRequest.setTitle(URLUtil.guessFileName(urlName,contentDescription,mimeType));
                downloadRequest.allowScanningByMediaScanner();
                downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);

                downloadManager.enqueue(downloadRequest);
                Toast.makeText(Home.this, "Downloading ....", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class myWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
            //Toast.makeText(Home.this, ""+webView.getUrl(), Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //webView.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
            swipe.setRefreshing(false);
        }

    }

    class GoogleClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == event.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
