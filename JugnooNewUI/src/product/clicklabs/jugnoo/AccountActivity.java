package product.clicklabs.jugnoo;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.BlurTransform;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

public class AccountActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack, imageViewLogout;
	
	ImageView imageViewUserImageBlur, imageViewProfileImage;
	
	EditText editTextUserName, editTextEmail, editTextPhone;
	ImageView imageViewEditName, imageViewEditEmail, imageViewEditPhoneNo;
	
	
	RelativeLayout relativeLayoutNotTriedJugnoo;
	TextView textViewNotTriedJugnoo, textViewGetARide;
	
	Button buttonLogout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		imageViewUserImageBlur = (ImageView) findViewById(R.id.imageViewUserImageBlur);
		imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);
		
		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Data.latoRegular(this));
		editTextEmail = (EditText) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Data.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Data.latoRegular(this));
		
		imageViewEditName = (ImageView) findViewById(R.id.imageViewEditName);
		imageViewEditEmail = (ImageView) findViewById(R.id.imageViewEditEmail);
		imageViewEditPhoneNo = (ImageView) findViewById(R.id.imageViewEditPhoneNo);
		
		
		
		relativeLayoutNotTriedJugnoo = (RelativeLayout) findViewById(R.id.relativeLayoutNotTriedJugnoo);
		textViewNotTriedJugnoo = (TextView) findViewById(R.id.textViewNotTriedJugnoo); textViewNotTriedJugnoo.setTypeface(Data.latoRegular(this));
		textViewGetARide = (TextView) findViewById(R.id.textViewGetARide); textViewGetARide.setTypeface(Data.latoRegular(this));
		
		buttonLogout = (Button) findViewById(R.id.buttonLogout); buttonLogout.setTypeface(Data.latoRegular(this));
		
		
		
		editTextUserName.setEnabled(false);
		editTextEmail.setEnabled(false);
		editTextPhone.setEnabled(false);
		
		
		editTextUserName.setText(Data.userData.userName);
		editTextEmail.setText(Data.userData.userEmail);
		editTextPhone.setText(Data.userData.phoneNo);
		
		try{
			if(!"".equalsIgnoreCase(Data.userData.userImage)){
				Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform()).into(imageViewProfileImage);
				Picasso.with(this).load(Data.userData.userImage).transform(new BlurTransform()).into(imageViewUserImageBlur);
			}
		} catch(Exception e){
			e.printStackTrace();
		}	
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		
		imageViewEditName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextUserName.setEnabled(true);
				editTextUserName.setSelection(editTextUserName.getText().length());
			}
		});
		
		imageViewEditEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});

		imageViewEditPhoneNo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		relativeLayoutNotTriedJugnoo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		buttonLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				new DialogPopup().alertPopupTwoButtonsWithListeners(AccountActivity.this, "", "Are you sure you want to logout?", "Logout", "", 
						new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								logoutAsync(AccountActivity.this);
							}
						}, 
						new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
							}
						}, 
						true, false);
			
			}
		});
		
		
	}

	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}
	
	
	
	void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Please Wait ...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("client_id", Data.CLIENT_ID);

			Log.i("access_token", "="+Data.userData.accessToken);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL+"/logout_user", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.AUTH_LOGOUT_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_LOGOUT_SUCCESSFUL.getOrdinal() == flag){

										PicassoTools.clearCache(Picasso.with(activity));
										
										new FacebookLoginHelper().logoutFacebook();
										
										GCMIntentService.clearNotifications(activity);
										
										Data.clearDataOnLogout(activity);
										
										HomeActivity.userMode = UserMode.PASSENGER;
										HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
										HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
										
										ActivityCompat.finishAffinity(activity);
										Intent intent = new Intent(activity, SplashNewActivity.class);
										startActivity(intent);
										overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}
						
						@Override
						public void onRetry(int retryNo) {
							Log.e("retryNo","="+retryNo);
							super.onRetry(retryNo);
						}
						
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	

}
