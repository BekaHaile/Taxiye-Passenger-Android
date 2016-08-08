package product.clicklabs.jugnoo.wallet;

/**
 * Created by socomo30 on 10/9/15.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.wallet.models.WalletAddMoneyState;

public class WalletRechargeWebViewActivity extends FragmentActivity {

    private WebView webView;
    boolean cancelTransaction = false;
    private ProgressBar progressBar;
    int walletType;

    private final String PAYTM_SUCCESS_URL = "https://jugnoo.in/paytm/wallet/success.php";
    private final String PAYTM_FAILURE_URL = "https://jugnoo.in/paytm/wallet/failure.php";

    private final String MOBIKWIK_SUCCESS_URL = "https://jugnoo.in/mobikwik/wallet/success.php";
    private final String MOBIKWIK_FAILURE_URL = "https://jugnoo.in/mobikwik/wallet/failure.php";

    private final String FREECHARGE_SUCCESS_URL = "https://jugnoo.in/freecharge/wallet/success.php";
    private final String FREECHARGE_FAILURE_URL = "https://jugnoo.in/freecharge/wallet/failure.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState!=null){
            super.onCreate(null);
            finish();//call activity u want to as activity is being destroyed it is restarted
        }else {
            super.onCreate(savedInstanceState);
        }


        setContentView(R.layout.activity_wallet_recharge_webview);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
        });
        webView.setWebViewClient(new MyAppWebViewClient());

        walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());

        try {
            if(walletType == PaymentOption.PAYTM.getOrdinal()){
				String postDataQuery = getIntent().getStringExtra(Constants.POST_DATA);
				try {
					loadHTMLContent(postDataQuery);
				} catch(Exception e) {
					Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show();
					loadHTMLContent(postDataQuery);
				}
			}
			else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()
                    || walletType == PaymentOption.FREECHARGE.getOrdinal()){
				String url = getIntent().getStringExtra(Constants.KEY_URL);
				webView.loadUrl(url);
			}
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show();
        }
    }

	public void loadHTMLContent(String data) {
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		webView.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}



	@Override
    public void onBackPressed() {
        if (cancelTransaction) {
            cancelTransaction = false;
            Intent returnIntent = new Intent();
            setResult(-1, returnIntent);
            finish();
            return;
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setMessage(getResources().getString(R.string.wallet_abort_transaction_alert));
            alertDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelTransaction = true;
                    dialog.dismiss();
                    onBackPressed();
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
			Log.e("url", "=" + url);

            if(walletType == PaymentOption.PAYTM.getOrdinal()) {
                if (PAYTM_SUCCESS_URL.equalsIgnoreCase(url)) {
                    Intent returnIntent = new Intent();
                    setResult(WalletAddMoneyState.SUCCESS.getOrdinal(), returnIntent);
                    finish();
                } else if (PAYTM_FAILURE_URL.equalsIgnoreCase(url)) {
                    Intent returnIntent = new Intent();
                    setResult(WalletAddMoneyState.FAILURE.getOrdinal(), returnIntent);
                    finish();
                }
            } else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()){
                if (MOBIKWIK_SUCCESS_URL.equalsIgnoreCase(url)) {
                    Intent returnIntent = new Intent();
                    setResult(WalletAddMoneyState.SUCCESS.getOrdinal(), returnIntent);
                    finish();
                } else if (MOBIKWIK_FAILURE_URL.equalsIgnoreCase(url)) {
                    Intent returnIntent = new Intent();
                    setResult(WalletAddMoneyState.FAILURE.getOrdinal(), returnIntent);
                    finish();
                }
            } else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
                if (FREECHARGE_SUCCESS_URL.equalsIgnoreCase(url)) {
                    Intent returnIntent = new Intent();
                    setResult(WalletAddMoneyState.SUCCESS.getOrdinal(), returnIntent);
                    finish();
                } else if (FREECHARGE_FAILURE_URL.equalsIgnoreCase(url)) {
                    Intent returnIntent = new Intent();
                    setResult(WalletAddMoneyState.FAILURE.getOrdinal(), returnIntent);
                    finish();
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
