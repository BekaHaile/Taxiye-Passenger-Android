package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class WalletWebviewActivity extends BaseActivity{
	
	LinearLayout relative;
	
	WebView paymentWebview;
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	String amount = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_webview);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(WalletWebviewActivity.this, relative, 1134, 720, false);
		
		
		paymentWebview = (WebView) findViewById(R.id.paymentWebview);
		WebSettings webSettings = paymentWebview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		
		try {
			if(AppStatus.getInstance(this).isOnline(this)){
				amount = "";
				if(getIntent().hasExtra("amount")){
					amount = getIntent().getStringExtra("amount");
					
					String urlStart = "https://www.dev.jugnoo.in/jugnoo-phpfiles/wallet/payments.php";
					
					if(Config.getServerUrl().contains(Config.getLiveServerUrl().substring(0, Config.getLiveServerUrl().length()-5))){
						urlStart = "https://www.dev.jugnoo.in/jugnoo-phpfiles/wallet/payments.php";
					}
					else{
						urlStart = "http://www.test.jugnoo.in/jugnoo/wallet/payments.php";
					}
					
					//https://www.dev.jugnoo.in/jugnoo-phpfiles/wallet/payments.php
					
//					Toast.makeText(this, urlStart, Toast.LENGTH_LONG).show();
					
					//access_token=%@&is_access_token_new=1&amount=%@&client_id=%@
					String postData = "access_token=" + Data.userData.accessToken 
							+ "&is_access_token_new=1"
							+ "&amount=" + amount
							+ "&client_id=" + Config.getClientId();
					
					Log.e("urlStart", "="+urlStart);
					Log.e("postData", "="+postData);
					
					
					paymentWebview.setWebViewClient(new MyAppWebViewClient());
					
					paymentWebview.postUrl(urlStart, EncodingUtils.getBytes(postData, "BASE64"));
					
				}
				else{
					Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
			else{
				DialogPopup.alertPopup(this, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent(WalletWebviewActivity.this, WalletAddPaymentActivity.class);
			intent.putExtra("payment", "failure");
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(WalletWebviewActivity.this, WalletAddPaymentActivity.class);
		intent.putExtra("payment", "failure");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	class MyAppWebViewClient extends WebViewClient {
        
		@Override
		public void onLoadResource(WebView view, String url) {
			Log.i("onLoadResource url", "=="+url);
        	
        	Log.i("shouldOverrideUrlLoading url", "=="+url);
        	
        	String[] arrUrl = url.split("\\/");
        	
        	if("success.php".equalsIgnoreCase(arrUrl[arrUrl.length-1])){
        		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						Intent intent = new Intent(WalletWebviewActivity.this, WalletActivity.class);
						intent.putExtra("payment", "success");
						intent.putExtra("amount", amount);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.left_in, R.anim.left_out);
					}
				}, 5000);
        	}
        	else if("failure.php".equalsIgnoreCase(arrUrl[arrUrl.length-1])){
        		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						Intent intent = new Intent(WalletWebviewActivity.this, WalletAddPaymentActivity.class);
						intent.putExtra("payment", "failure");
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.left_in, R.anim.left_out);
					}
				}, 5000);
        	}
        	
			
			super.onLoadResource(view, url);
		}
		
		
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	Log.i("shouldOverrideUrlLoading url", "=="+url);
            return false;
        }
        
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	
        	Log.e("onReceivedError url", "== errorCode="+errorCode+" description="+description+" failingUrl="+failingUrl);
        	//== errorCode=-6 description=net::ERR_CONNECTION_REFUSED failingUrl=https://www.test.jugnoo.in/jugnoo/wallet/payments.php
        	
        	String excepUrl = "payu.in";
        	
        	if(!failingUrl.contains(excepUrl)){
        		Intent intent = new Intent(WalletWebviewActivity.this, WalletAddPaymentActivity.class);
				intent.putExtra("payment", "failure");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
        	}
        	
        	super.onReceivedError(view, errorCode, description, failingUrl);
        }
        
    }
	
	
}
