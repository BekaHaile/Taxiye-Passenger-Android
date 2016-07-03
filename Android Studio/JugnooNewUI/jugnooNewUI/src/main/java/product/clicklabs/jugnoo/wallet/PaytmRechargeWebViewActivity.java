package product.clicklabs.jugnoo.wallet;

/**
 * Created by socomo30 on 10/9/15.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.wallet.models.WalletAddMoneyState;
import product.clicklabs.jugnoo.utils.Log;

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

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {});
        webView.setWebViewClient(new MyAppWebViewClient());

        String postDataQuery = getIntent().getStringExtra(Constants.POST_DATA);
        try {
			loadHTMLContent(postDataQuery);
        } catch(Exception e) {
            Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show();
			loadHTMLContent(postDataQuery);
        }
    }

	public void loadHTMLContent(String data) {
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		webView.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}



	@Override
    public void onBackPressed() {
        boolean disableBack = false;
        if(cancelTransaction){
            cancelTransaction = false;
			Intent returnIntent = new Intent();
			setResult(-1, returnIntent);
			finish();
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
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
			urlRedirectionCallback(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

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
            return false;
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("onReceivedError url", "== errorCode=" + errorCode + " description=" + description + " failingUrl=" + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }


	private void urlRedirectionCallback(String url){
		try {
			//https://jugnoo.in/paytm/wallet/success.php
			//https://jugnoo.in/paytm/wallet/failure.php
			Log.e("url", "=" + url);
			if("https://jugnoo.in/paytm/wallet/success.php".equalsIgnoreCase(url)){
				Intent returnIntent = new Intent();
				setResult(WalletAddMoneyState.SUCCESS.getOrdinal(), returnIntent);
				finish();
			} else if("https://jugnoo.in/paytm/wallet/failure.php".equalsIgnoreCase(url)){
				Intent returnIntent = new Intent();
				setResult(WalletAddMoneyState.FAILURE.getOrdinal(), returnIntent);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
