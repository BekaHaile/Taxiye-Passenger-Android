package product.clicklabs.jugnoo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by ankit on 6/18/16.
 */
public class WebActivity extends BaseActivity {

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack, imageViewProgressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewProgressBar = (ImageView) findViewById(R.id.imageViewProgressBar);

        try {
            if(Data.webActivityTitle != null && !"".equalsIgnoreCase(Data.webActivityTitle)){
				textViewTitle.setText(Data.webActivityTitle);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        imageViewProgressBar.setBackgroundResource(R.drawable.loader_new_frame_anim);
        imageViewProgressBar.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) imageViewProgressBar.getBackground();
                frameAnimation.start();
            }
        });
        imageViewProgressBar.setVisibility(View.GONE);


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
            webView.loadUrl(Data.userData.getFatafatUrlLink());
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

    }

    public void performBackPressed(){
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
//        Bundle bundle = new Bundle();
//        MyApplication.getInstance().logEvent();
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    class MyAppWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            imageViewProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (webView.getProgress() > 70) {
                imageViewProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            imageViewProgressBar.setVisibility(View.GONE);
            DialogPopup.alertPopup(WebActivity.this, "", description);
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
            if (host != null && host.equals(getString(R.string.app_domain))) {
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
        ASSL.closeActivity(relative);
        System.gc();
    }
}
