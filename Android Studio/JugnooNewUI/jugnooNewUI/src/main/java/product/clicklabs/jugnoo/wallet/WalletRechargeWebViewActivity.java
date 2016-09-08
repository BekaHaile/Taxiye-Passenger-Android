package product.clicklabs.jugnoo.wallet;

/**
 * Created by socomo30 on 10/9/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        // Enable javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        // Create database folder
        String databasePath = getDir("databases", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setDatabasePath(databasePath);

        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSaveFormData(true);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);



        webView.setWebViewClient(new MyAppWebViewClient());

        walletType = getIntent().getIntExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());

        try {
            if(walletType == PaymentOption.PAYTM.getOrdinal()){
				String postDataQuery = getIntent().getStringExtra(Constants.POST_DATA);
                loadHTMLContent(postDataQuery);
			}
			else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()){
				String url = getIntent().getStringExtra(Constants.KEY_URL);
				webView.loadUrl(url);
			}
            else if(walletType == PaymentOption.FREECHARGE.getOrdinal()){
                String url = getIntent().getStringExtra(Constants.KEY_URL);
                String data = getIntent().getStringExtra(Constants.POST_DATA);
                loadDataFreecharge(url, data);
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
//            Log.e("onLoadResource url", "=" + url);
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



    private void loadDataFreecharge(final String url, final String data){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String respStr = "";
                try {
//                    OkHttpClient client = new OkHttpClient();
//                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//                    RequestBody body = RequestBody.create(mediaType, data);
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .post(body)
//                            .addHeader("cache-control", "no-cache")
//                            .addHeader("postman-token", "163a261c-1ddf-04cb-8f16-0a747d5118b8")
//                            .addHeader("content-type", "application/x-www-form-urlencoded")
//                            .build();
//                    Response response = client.newCall(request).execute();
//                            .url("https://checkout-sandbox.freecharge.in/api/v1/co/oauth/wallet/add")
//                    RequestBody body = RequestBody.create(mediaType, "amount=1&channel=ANDROID&metadata=22&merchantId=TpIysmf2ri3KJY&callbackUrl=https%3A%2F%2Ftest.jugnoo.in%3A8017%2Ffreecharge%2Fadd_money_callback&loginToken=072610d5cef4d694c65a5ab1ab53cc31be84c932ec8e7e5f52c0e48e7123f9e5eb69bc26b7f8921cc4991613682ed636576db7b5b336ba2baf1c272623a31bbc354d1069d170a30005d2223e7a18e2ef8517387154d92f5a1012d91ec40e79963766507b778e9ffcf5048fe03a2bd4f879a0c44a17338c8cdfadda4e890cfcc8d81e426cce4a48f51da16df50c679fa1&checksum=70b5cb56383e45b02b3389af7210d553827222de01eddd299f10062c2eda0a5a");

                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, data);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("cache-control", "no-cache")
                            .addHeader("postman-token", "163a261c-1ddf-04cb-8f16-0a747d5118b8")
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = client.newCall(request).execute();

                    respStr = new String(response.body().bytes());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                final String respStrFinal = respStr;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            webView.loadDataWithBaseURL("", respStrFinal, "text/html", "utf-8", null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

}
