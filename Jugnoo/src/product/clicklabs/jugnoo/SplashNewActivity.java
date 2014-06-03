package product.clicklabs.jugnoo;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SplashNewActivity extends Activity{
	
	
	LinearLayout relative;
	
	ImageView jugnooImg;
	ImageView jugnooTextImg;
	
	boolean loginDataFetched = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_new);
		
		loginDataFetched = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);
		
		
		jugnooImg = (ImageView) findViewById(R.id.jugnooImg);
		jugnooTextImg = (ImageView) findViewById(R.id.jugnooTextImg);
		jugnooTextImg.setVisibility(View.GONE);
		
		
		int initialTime = 1000;
		int blinkInterval = 100;
		int waitBeforeMove = 200;
		int waitAfterMove = 100;
		
		final float moveDist = 60.0f;
		
		//First blink
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_02);
			}
		}, initialTime);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_03);
			}
		}, initialTime + blinkInterval);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_01);
			}
		}, initialTime + (blinkInterval * 2));
		initialTime = initialTime + (blinkInterval * 2);
		
		
		// first move
		initialTime = initialTime + waitBeforeMove;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				layoutParams.setMargins(0, (int)(moveDist * ASSL.Yscale()), 0, 0);
				jugnooImg.setLayoutParams(layoutParams);
			}
		}, initialTime);
		initialTime = initialTime + waitAfterMove;
		
		
		
		// second blink
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_02);
			}
		}, initialTime);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_03);
			}
		}, initialTime + blinkInterval);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_01);
			}
		}, initialTime + (blinkInterval * 2));
		initialTime = initialTime + (blinkInterval * 2);
		
		// second move
		initialTime = initialTime + waitBeforeMove;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				layoutParams.setMargins(0, (int) (moveDist * 2 * ASSL.Yscale()), 0, 0);
				jugnooImg.setLayoutParams(layoutParams);
			}
		}, initialTime);
		initialTime = initialTime + waitAfterMove;
		
		
		
		
		
		// third blink
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_02);
			}
		}, initialTime);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_03);
			}
		}, initialTime + blinkInterval);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_01);
			}
		}, initialTime + (blinkInterval * 2));
		initialTime = initialTime + (blinkInterval * 2);

		// third move
		initialTime = initialTime + waitBeforeMove;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				layoutParams.setMargins(0, (int) (moveDist * 3 * ASSL.Yscale()), 0, 0);
				jugnooImg.setLayoutParams(layoutParams);
			}
		}, initialTime);
		initialTime = initialTime + waitAfterMove;
		
		
		// fourth blink
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_02);
			}
		}, initialTime);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_03);
			}
		}, initialTime + blinkInterval);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_01);
			}
		}, initialTime + (blinkInterval * 2));
		initialTime = initialTime + (blinkInterval * 2);
		
		
		// fourth move
		initialTime = initialTime + waitBeforeMove;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				layoutParams.setMargins(0, (int) (moveDist * 4 * ASSL.Yscale()), 0, 0);
				jugnooImg.setLayoutParams(layoutParams);
			}
		}, initialTime);
		initialTime = initialTime + waitAfterMove;
		
		
		// fifth blink
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_02);
			}
		}, initialTime);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_03);
			}
		}, initialTime + blinkInterval);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooImg.setImageResource(R.drawable.img_01);
			}
		}, initialTime + (blinkInterval * 2));
		initialTime = initialTime + (blinkInterval * 2);
		
		
		// fifth show
		initialTime = initialTime + waitBeforeMove;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jugnooTextImg.setVisibility(View.VISIBLE);
			}
		}, initialTime);
		initialTime = initialTime + waitBeforeMove;
		
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				accessTokenLogin(SplashNewActivity.this);
			}
		}, initialTime);
		
		
	}
	
	
	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		final String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		if(!"".equalsIgnoreCase(accessToken)){
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);

				Log.i("accessToken", "=" + accessToken);
			
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(Data.SERVER_TIMEOUT);
				client.post(Data.SERVER_URL + "/access_token", params,
						new AsyncHttpResponseHandler() {
						private JSONObject jObj;
		
							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										
//										{"error":"some parameter missing","flag":0}//error
//										{"error":"invalid access token","flag":1}//error

										int flag = jObj.getInt("flag");	
										String errorMessage = jObj.getString("error");
										
										if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else if(1 == flag){ // {"error":"email not  registered","flag":1}//error
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
										
										new JSONParser().parseAccessTokenLoginData(activity, response, accessToken);
										
										loginDataFetched = true;
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
		
							@Override
							public void onFailure(Throwable arg0) {
								Log.e("request fail", arg0.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
		else{
			startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}

	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			startActivity(new Intent(SplashNewActivity.this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	@Override
	public void onBackPressed() {
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}
