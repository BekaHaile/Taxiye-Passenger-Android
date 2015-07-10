package com.payu.sdk;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.apache.http.util.EncodingUtils;

public class ProcessPaymentActivity extends FragmentActivity {
    private WebView webView;
    private BroadcastReceiver mReceiver = null;
    boolean cancelTransaction = false;
    public static ClearFragment clearFrag;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState!=null){
            super.onCreate(null);
            finish();//call activity u want to as activity is being destroyed it is restarted
        }else {
            super.onCreate(savedInstanceState);
        }



        setContentView(R.layout.activity_process_payment);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        Log.e("Constants.PAYMENT_URL", "Constants.PAYMENT_URL = " + Constants.PAYMENT_URL);
        Log.e("Constants.PAYMENT_URL", "postData = " + getIntent().getExtras().getString("postData"));

        webView.setWebChromeClient(new WebChromeClient() { });
        webView.setWebViewClient(new MyAppWebViewClient());

        String postDataQuery = getIntent().getExtras().getString("postData");

        webView.postUrl(Constants.PAYMENT_URL, EncodingUtils.getBytes(postDataQuery, "BASE64"));
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
                    clearFrag.finishCalled(200);
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
            Log.i("onLoadResource url", "=="+url);

            Log.i("shouldOverride url", "=="+url);

            String[] arrUrl = url.split("\\/");

            if("success.php".equalsIgnoreCase(arrUrl[arrUrl.length-1])){
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        clearFrag.finishCalled(201);
                        finish();


                    }
                }, 5000);
            }
            else if("failure.php".equalsIgnoreCase(arrUrl[arrUrl.length-1])){
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        clearFrag.finishCalled(202);
                        finish();

                    }
                }, 5000);
            }


            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            Log.e("on page started", "on page started");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            if (webView.getProgress() > 50)
                progressBar.setVisibility(View.GONE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("shouldOverrideUrlLoading url", "== "+url);
            return false;
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            Log.e("onReceivedError url", "== errorCode="+errorCode+" description="+description+" failingUrl="+failingUrl);

            String excepUrl = "payu.in";

            if(!failingUrl.contains(excepUrl)){

                clearFrag.finishCalled(203);
                finish();

            }

            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

}