package product.clicklabs.jugnoo;

import org.apache.http.Header;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FareInfoActivity extends FragmentActivity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	ProgressBar progressBar;
	TextView textViewInfo;
	WebView webview;
	
	AsyncHttpClient fetchHelpDataClient;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fare_info_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(FareInfoActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.regularFont(getApplicationContext()));
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Data.regularFont(getApplicationContext()));
		webview = (WebView) findViewById(R.id.webview);
		
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getFareDetailsAsync(FareInfoActivity.this);
			}
		});
		
	}
	
	
	public void openHelpData(String data, boolean errorOccured) {
		if (errorOccured) {
			textViewInfo.setVisibility(View.VISIBLE);
			textViewInfo.setText(data);
			webview.setVisibility(View.GONE);
		} else {
			textViewInfo.setVisibility(View.GONE);
			webview.setVisibility(View.VISIBLE);
			loadHTMLContent(data);
		}
	}
	
	public void loadHTMLContent(String data){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webview.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}
	
	
	/**
	 * ASync for get rides from server
	 */
	public void getFareDetailsAsync(final Activity activity) {
		if(fetchHelpDataClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				
				progressBar.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);
				webview.setVisibility(View.GONE);
				loadHTMLContent("");
				
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				
				fetchHelpDataClient = Data.getClient();
				fetchHelpDataClient.post(Data.SERVER_URL + "/get_fare_details", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
	
							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Log.e("request fail", arg3.toString());
								progressBar.setVisibility(View.GONE);
								openHelpData("Some error occured. Tap to retry.", true);
							}
	
							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								String response = new String(arg2);
								Log.i("Server response faq ", "response = " + response);
								try {
									jObj = new JSONObject(response);
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											openHelpData("Some error occured. Tap to retry.", true);
										}
									}
									else{
										String data = jObj.getString("data");
										openHelpData(data, false);
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									openHelpData("Some error occured. Tap to retry.", true);
								}
								progressBar.setVisibility(View.GONE);
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
								fetchHelpDataClient = null;
							}
						});
			}
			else {
				openHelpData("No internet connection. Tap to retry.", true);
			}
		}
	}
	
	
	public void performBackPressed(){
		try {
			if(fetchHelpDataClient != null){
				fetchHelpDataClient.cancelAllRequests(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(fetchHelpDataClient != null){
				fetchHelpDataClient.cancelAllRequests(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        ASSL.closeActivity(relative);
        System.gc();
	}

}

