package com.sabkuchfresh.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sabkuchfresh.TokenGenerator.HomeUtil;
import com.sabkuchfresh.config.Config;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.SettleUserDebt;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppStatus;
import product.clicklabs.jugnoo.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Log;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ChangePasswordActivity extends BaseActivity {

	private final String TAG = ChangePasswordActivity.class.getSimpleName();

	LinearLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	EditText editTextOldPassword, editTextNewPassword, editTextRetypeNewPassword;
	
	Button buttonChangePassword;
	
	ScrollView scrollView;
	
	TextView textViewScroll, textViewOld, textViewNew, textViewRetype;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ChangePasswordActivity.this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword); editTextOldPassword.setTypeface(Fonts.latoRegular(this));
		editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword); editTextNewPassword.setTypeface(Fonts.latoRegular(this));
		editTextRetypeNewPassword = (EditText) findViewById(R.id.editTextRetypeNewPassword); editTextRetypeNewPassword.setTypeface(Fonts.latoRegular(this));

		buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword); buttonChangePassword.setTypeface(Fonts.mavenRegular(this));
		textViewOld = (TextView)findViewById(R.id.textViewOld); textViewOld.setTypeface(Fonts.mavenLight(this));
		textViewNew = (TextView)findViewById(R.id.textViewNew); textViewNew.setTypeface(Fonts.mavenLight(this));
		textViewRetype = (TextView)findViewById(R.id.textViewRetype); textViewRetype.setTypeface(Fonts.mavenLight(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		
		
		
		
		buttonChangePassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String oldPassword = editTextOldPassword.getText().toString().trim();
				String newPassword = editTextNewPassword.getText().toString().trim();
				String retypeNewPassword = editTextRetypeNewPassword.getText().toString().trim();
				
				if("".equalsIgnoreCase(oldPassword)){
					editTextOldPassword.requestFocus();
					editTextOldPassword.setError("Please enter old password");
				}
				else{
					if("".equalsIgnoreCase(newPassword)){
						editTextNewPassword.requestFocus();
						editTextNewPassword.setError("Please enter new password");
					}
					else{
						if("".equalsIgnoreCase(retypeNewPassword)){
							editTextRetypeNewPassword.requestFocus();
							editTextRetypeNewPassword.setError("Please retype new password");
						}
						else{
							if(!newPassword.equalsIgnoreCase(retypeNewPassword)){
								editTextNewPassword.requestFocus();
								editTextNewPassword.setError("Passwords not matched");
							}
							else{
								if(newPassword.equalsIgnoreCase(oldPassword)){
									editTextNewPassword.requestFocus();
									editTextNewPassword.setError("New Password is same as the old one");
								}
								else{
									if(newPassword.length() >= 6){
										updateUserProfileChangePasswordAPI(ChangePasswordActivity.this, oldPassword, newPassword);
									}
									else{
										editTextNewPassword.requestFocus();
										editTextNewPassword.setError("New Password must be of atleast 6 characters");
									}
								}
							}
						}
					}
				}
			}
		});

		textViewOld.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextOldPassword.requestFocus();
			}
		});

		textViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextNewPassword.requestFocus();
			}
		});

		textViewRetype.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextRetypeNewPassword.requestFocus();
			}
		});
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		editTextOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextOldPassword.getBottom());
				}
				editTextOldPassword.setError(null);
			}
		});
		
		editTextNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextNewPassword.getBottom());
				}
				editTextNewPassword.setError(null);
			}
		});
		
		editTextRetypeNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextRetypeNewPassword.getBottom());
				}
				editTextRetypeNewPassword.setError(null);
			}
		});
		
		editTextRetypeNewPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonChangePassword.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});


		editTextOldPassword.requestFocus();
		

		
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeUtil.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
	public void updateUserProfileChangePasswordAPI(final Activity activity, final String oldPassword, final String newPassword) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Updating...");
			
			HashMap<String, String> params = new HashMap<>();
			params.put("client_id", Config.getClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("old_password", oldPassword);
			params.put("new_password", newPassword);

			RestClient.getApiServices().updateUserProfile(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "updateUserProfile response = " + responseStr);
					DialogPopup.dismissLoadingDialog();
					try {
						JSONObject jObj = new JSONObject(responseStr);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								DialogPopup.alertPopup(activity, "", error);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								String message = jObj.getString("message");
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										performBackPressed();
									}
								});
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "updateUserProfile error="+error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
}
