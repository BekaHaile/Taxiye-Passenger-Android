package com.sabkuchfresh.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.sabkuchfresh.R;

/**
 * Created by Gurmail S. Kang on 5/18/16.
 */
public class AboutUsFragment extends Fragment {
    private WebView webView;

    private String urlLink = "https://www.jugnoo.in/about-us-fatafat.html";

    private LinearLayout mProgressWheel;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.fragment_webview, container, false);

        mProgressWheel = (LinearLayout) contentView.findViewById(R.id.progress_layout);

        webView = (WebView) contentView.findViewById(R.id.web_view);
        webView.loadUrl(urlLink);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(urlLink);


        return contentView;
    }


    class MyWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            mProgressWheel.setVisibility(View.VISIBLE);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            if (webView.getProgress() > 70) {
                mProgressWheel.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

    }
}
