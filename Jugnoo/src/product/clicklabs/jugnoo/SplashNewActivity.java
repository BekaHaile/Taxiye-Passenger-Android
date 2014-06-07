package product.clicklabs.jugnoo;

import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashNewActivity extends Activity{
	
	
	LinearLayout relative;
	
	ImageView jugnooImg;
	ImageView jugnooTextImg;
	
	boolean loginDataFetched = false, loginFailed = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.splash_new);
		
		loginDataFetched = false;
		loginFailed = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);
		
		
		jugnooImg = (ImageView) findViewById(R.id.jugnooImg);
		jugnooTextImg = (ImageView) findViewById(R.id.jugnooTextImg);
		jugnooTextImg.setVisibility(View.GONE);
		
		
		Animation animation = new TranslateAnimation(0, 0, 0, (int)(550*ASSL.Yscale()));
		animation.setFillAfter(false);
		animation.setDuration(650);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setAnimationListener(new ShowAnimListener());
		jugnooImg.startAnimation(animation);
		
		
		
		
	}
	
	
	
	class ShowAnimListener implements AnimationListener{

		
		public ShowAnimListener(){
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
			jugnooImg.clearAnimation();
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(jugnooImg.getLayoutParams());
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams.setMargins(0, (int)(262 * ASSL.Yscale()), 0, 0);
			jugnooImg.setLayoutParams(layoutParams);
			
			jugnooTextImg.setVisibility(View.VISIBLE);
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					accessTokenLogin(SplashNewActivity.this);
				}
			}, 500);
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
	
	
	/**
	 * ASync for access token login from server
	 */
	public void accessTokenLogin(final Activity activity) {
		
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		final String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		final String id = pref.getString(Data.SP_ID_KEY, "");
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
										else if(8 == flag){ // {"error":"email not  registered","flag":1}//error
//											new DialogPopup().alertPopup(activity, "", errorMessage);
											loginFailed = true;
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
										
										new JSONParser().parseAccessTokenLoginData(activity, response, accessToken, id);
										
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
		else if(hasFocus && loginFailed){
			loginFailed = false;
			startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
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
