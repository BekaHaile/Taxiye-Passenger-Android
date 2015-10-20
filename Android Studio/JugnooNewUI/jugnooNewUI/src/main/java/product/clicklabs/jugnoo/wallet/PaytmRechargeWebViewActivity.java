package product.clicklabs.jugnoo.wallet;

/**
 * Created by socomo30 on 10/9/15.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;

public class PaytmRechargeWebViewActivity extends FragmentActivity {

    private WebView webView;
    boolean cancelTransaction = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState!=null){
            super.onCreate(null);
            finish();//call activity u want to as activity is being destroyed it is restarted
        }else {
            super.onCreate(savedInstanceState);
        }


        setContentView(R.layout.activity_paytm_recharge_webview);

        webView = (WebView) findViewById(com.payu.sdk.R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        progressBar = (ProgressBar) findViewById(com.payu.sdk.R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {});
        webView.setWebViewClient(new MyAppWebViewClient());

        String postDataQuery = getIntent().getExtras().getString("postData");
        String postData;
        try {
            postData = URLEncoder.encode(postDataQuery, "UTF-8");
            Log.e("postData", "postData = "+postData);
            webView.postUrl(Config.getTXN_URL() + "paytm/wallet/add_money", postData.getBytes());

        } catch(Exception e) {
            Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show();
            webView.postUrl(Config.getTXN_URL() + "paytm/wallet/add_money", EncodingUtils.getBytes(postDataQuery, "utf-8"));
        }
    }



	@Override
    public void onBackPressed() {
        boolean disableBack = false;
        if(cancelTransaction){
            cancelTransaction = false;
            super.onBackPressed();
            return;
        }
        try {
            Bundle bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            disableBack = bundle.containsKey("payu_disable_back") && bundle.getBoolean("payu_disable_back");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!disableBack) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);;
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Do you really want to cancel the transaction ?");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelTransaction = true;
                    dialog.dismiss();
                    onBackPressed();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
        webView.clearHistory();
        webView.destroy();
    }

    class MyAppWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.e("onLoadResource url", "==" + url);
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("on page started", "on page started");
            progressBar.setVisibility(View.VISIBLE);
			urlRedirectionCallback(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Log.e("paymentWebview.onPageFinished", "url = " + url);
            if (webView.getProgress() > 50) {
                progressBar.setVisibility(View.GONE);
            }

//			if(url.equalsIgnoreCase("https://test.jugnoo.in:7000/paytm/wallet/add_money_cb")){
//				Toast.makeText(PaymentWebViewActivity.this, "Transaction complete", Toast.LENGTH_SHORT).show();
//				new Handler().postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						finish();
//					}
//				}, 3000);
//			}
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("shouldOverride url", "== " + url);
            return false;
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("onReceivedError url", "== errorCode="+errorCode+" description="+description+" failingUrl="+failingUrl);
            String excepUrl = "payu.in";
            if(!failingUrl.contains(excepUrl)){
                finish();
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }


	private void urlRedirectionCallback(String url){
		try {
			//https://jugnoo.in/paytm/wallet/success.php
			//https://jugnoo.in/paytm/wallet/failure.php
			if("https://jugnoo.in/paytm/wallet/success.php".equalsIgnoreCase(url)){
				Toast.makeText(PaytmRechargeWebViewActivity.this, "Transaction complete", Toast.LENGTH_LONG).show();
				finish();
			} else if("https://jugnoo.in/paytm/wallet/failure.php".equalsIgnoreCase(url)){
				Toast.makeText(PaytmRechargeWebViewActivity.this, "Transaction failed", Toast.LENGTH_LONG).show();
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
