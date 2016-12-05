package com.jugnoo.pay.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.sabkuchfresh.utils.AppConstant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;


/**
 * Created by ankit on 6/18/16.
 */
public class WebActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @Bind(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }

    private WebView webView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        if(getIntent().hasExtra(AppConstant.URL)){
            url = getIntent().getStringExtra(AppConstant.URL);
        }

        CallProgressWheel.showLoadingDialog(WebActivity.this, AppConstant.PLEASE);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.setWebViewClient(new MyAppWebViewClient());
        try {
            webView.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class MyAppWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //imageViewProgressBar.setVisibility(View.VISIBLE);
            CallProgressWheel.dismissLoadingDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (webView.getProgress() > 70) {
                //imageViewProgressBar.setVisibility(View.GONE);
                CallProgressWheel.dismissLoadingDialog();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //imageViewProgressBar.setVisibility(View.GONE);
            CallProgressWheel.dismissLoadingDialog();
            SingleButtonAlert.showAlertGps(WebActivity.this, description, AppConstant.OK, new SingleButtonAlert.OnAlertOkClickListener() {
                @Override
                public void onOkButtonClicked() {
                    finish();
                }
            });
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri;
            try {
                uri = Uri.parse(url);
            } catch (NullPointerException e) {
                return true;
            }

            String host = uri.getHost(); //Host is null when user clicked on email, phone number, ...
            if (host != null && host.equals("jugnoo.in")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            else {
                Log.i("WebActivity", "url="+url);
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs or anything else (email, phone number, ...)
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
