package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class PhoneNoOTPConfirmScreen extends BaseActivity{
	
	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewOtpNumber;
	ImageView imageViewSep, imageViewChangePhoneNumber;
	EditText editTextOTP;

	LinearLayout linearLayoutWaiting;
	TextView textViewCounter;
	ImageView imageViewYellowLoadingBar;

	Button buttonVerify, buttonOtpViaCall;
	LinearLayout linearLayoutGiveAMissedCall;
	TextView textViewOr;


	RelativeLayout relative;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

	
	String phoneNoToVerify = "";

	public static String OTP_SCREEN_OPEN = null;
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {

		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");
				String[] arr = message.split("Your\\ One\\ Time\\ Password\\ is\\ ");
				otp = arr[1];
				otp = otp.replaceAll("\\.", "");
			} else if(intent.hasExtra("otp")){
				otp = intent.getStringExtra("otp");
			}

			if(Utils.checkIfOnlyDigits(otp)){
				if(!"".equalsIgnoreCase(otp)) {
					editTextOTP.setText(otp);
					editTextOTP.setSelection(editTextOTP.getText().length());
					buttonVerify.performClick();
				}
			}

		} catch(Exception e){
			e.printStackTrace();
		}

		super.onNewIntent(intent);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_no_otp);

		Utils.enableSMSReceiver(this);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(PhoneNoOTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.mavenLight(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		imageViewSep = (ImageView) findViewById(R.id.imageViewSep); imageViewSep.setVisibility(View.GONE);
		imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber); imageViewChangePhoneNumber.setVisibility(View.GONE);

		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		((TextView)findViewById(R.id.textViewWaiting)).setTypeface(Fonts.mavenLight(this));
		textViewCounter = (TextView) findViewById(R.id.textViewCounter); textViewCounter.setTypeface(Fonts.mavenLight(this));
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);

		editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.mavenMedium(this));

		buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Fonts.mavenRegular(this));
		buttonOtpViaCall = (Button) findViewById(R.id.buttonOtpViaCall); buttonOtpViaCall.setTypeface(Fonts.mavenRegular(this));
		textViewOr = (TextView) findViewById(R.id.textViewOr); textViewOr.setTypeface(Fonts.mavenLight(this));
		linearLayoutGiveAMissedCall = (LinearLayout) findViewById(R.id.linearLayoutGiveAMissedCall);
		((TextView) findViewById(R.id.textViewGiveAMissedCall)).setTypeface(Fonts.mavenLight(this));


		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

		textViewTitle.measure(0, 0);
		int mWidth = textViewTitle.getMeasuredWidth();
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, mWidth));


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		editTextOTP.setOnFocusChangeListener(onFocusChangeListener);
		editTextOTP.setOnClickListener(onClickListener);
		
		buttonVerify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String otpCode = editTextOTP.getText().toString().trim();
				if (otpCode.length() > 0) {
					verifyOtpPhoneNoChange(PhoneNoOTPConfirmScreen.this, phoneNoToVerify, otpCode);
				} else {
					editTextOTP.requestFocus();
					editTextOTP.setError("Code can't be empty");
				}
			}
		});


		editTextOTP.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonVerify.performClick();
						break;

					case EditorInfo.IME_ACTION_NEXT:
						break;

					default:
				}
				return true;
			}
		});

		buttonOtpViaCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (1 == Data.otpViaCallEnabled) {
						initiateOTPCallAsync(PhoneNoOTPConfirmScreen.this, phoneNoToVerify);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		linearLayoutGiveAMissedCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (!"".equalsIgnoreCase(Data.knowlarityMissedCallNumber)) {
						DialogPopup.alertPopupTwoButtonsWithListeners(PhoneNoOTPConfirmScreen.this, "",
								getResources().getString(R.string.give_missed_call_dialog_text),
								getResources().getString(R.string.call_us),
								getResources().getString(R.string.cancel),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Utils.openCallIntent(PhoneNoOTPConfirmScreen.this, Data.knowlarityMissedCallNumber);
									}
								},
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								}, false, false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		try {
			if(getIntent().hasExtra("phone_no_verify")){
				phoneNoToVerify = getIntent().getStringExtra("phone_no_verify");
				textViewOtpNumber.setText(phoneNoToVerify);
			}
			else {
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}

		linearLayoutWaiting.setVisibility(View.GONE);

		try{
			if(!"".equalsIgnoreCase(Data.knowlarityMissedCallNumber)) {
				linearLayoutGiveAMissedCall.setVisibility(View.VISIBLE);
			}
			else{
				linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			}

			if(1 == Data.otpViaCallEnabled) {
				buttonOtpViaCall.setVisibility(View.VISIBLE);
			}
			else{
				buttonOtpViaCall.setVisibility(View.GONE);
			}
			if(linearLayoutGiveAMissedCall.getVisibility() == View.VISIBLE
					&& buttonOtpViaCall.getVisibility() == View.VISIBLE){
				textViewOr.setVisibility(View.VISIBLE);
			} else{
				textViewOr.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
			linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			buttonOtpViaCall.setVisibility(View.GONE);
			textViewOr.setVisibility(View.GONE);
		}


		OTP_SCREEN_OPEN = "yes";


	}


	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, buttonVerify.getTop());
					}
				}, 200);
			} else {
				try {
					((EditText)v).setError(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				((EditText)v).setError(null);
			}
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					scrollView.smoothScrollTo(0, buttonVerify.getTop());
				}
			}, 200);
			try {
				if(v.getId() == R.id.editTextEmail) {
					((AutoCompleteTextView) v).showDropDown();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	

	public void verifyOtpPhoneNoChange(final Activity activity, String phoneNo, String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			HashMap<String, String> params = new HashMap<>();
		
			params.put("client_id", Config.getClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("phone_no", phoneNo);
			params.put("verification_token", otp);
			
			Log.i("params", ">" + params);
		
//			AsyncHttpClient client = Data.getClient();
//			client.post(Config.getServerUrl() + "/verify_my_contact_number", params,
//					new CustomAsyncHttpResponseHandler() {
//					private JSONObject jObj;
//
//						@Override
//						public void onFailure(Throwable arg3) {
//							Log.e("request fail", arg3.toString());
//							DialogPopup.dismissLoadingDialog();
//							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//						}
//
//						@Override
//						public void onSuccess(String response) {
//							Log.i("Server response", "response = " + response);
//							try {
//								jObj = new JSONObject(response);
//								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
//									int flag = jObj.getInt("flag");
//									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
//										String error = jObj.getString("error");
//										DialogPopup.dialogBanner(activity, error);
//									}
//									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
//										String message = jObj.getString("message");
//										DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
//											@Override
//											public void onClick(View v) {
//												performBackPressed();
//											}
//										});
//									}
//									else{
//										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//									}
//								}
//							}  catch (Exception exception) {
//								exception.printStackTrace();
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//							}
//							DialogPopup.dismissLoadingDialog();
//						}
//					});

			RestClient.getApiServices().verifyMyContactNumber(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i("Server response", "response = " + response);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
							int flag = jObj.getInt("flag");
							if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
								String error = jObj.getString("error");
								DialogPopup.dialogBanner(activity, error);
							}
							else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
								String message = jObj.getString("message");
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										performBackPressed();
									}
								});
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
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	/**
	 * ASync for initiating OTP Call from server
	 */
	public void initiateOTPCallAsync(final Activity activity, String phoneNo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			HashMap<String, String> params = new HashMap<>();
		
			params.put("client_id", Config.getClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("phone_no", phoneNo);
			Log.i("params", ">"+params);
		
//			AsyncHttpClient client = Data.getClient();
//			client.post(Config.getServerUrl() + "/send_new_number_otp_via_call", params,
//					new CustomAsyncHttpResponseHandler() {
//					private JSONObject jObj;
//
//						@Override
//						public void onFailure(Throwable arg3) {
//							Log.e("request fail", arg3.toString());
//							DialogPopup.dismissLoadingDialog();
//							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//						}
//
//						@Override
//						public void onSuccess(String response) {
//							Log.i("Server response", "response = " + response);
//							try {
//								jObj = new JSONObject(response);
//								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
//									int flag = jObj.getInt("flag");
//									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
//										String error = jObj.getString("error");
//										DialogPopup.dialogBanner(activity, error);
//									}
//									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
//										String message = jObj.getString("message");
//										DialogPopup.dialogBanner(activity, message);
//									}
//									else{
//										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//									}
//								}
//							}  catch (Exception exception) {
//								exception.printStackTrace();
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//							}
//							DialogPopup.dismissLoadingDialog();
//						}
//					});


			RestClient.getApiServices().sendNewNumberOtpViaCall(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i("Server response", "response = " + response);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
							int flag = jObj.getInt("flag");
							if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
								String error = jObj.getString("error");
								DialogPopup.dialogBanner(activity, error);
							}
							else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
								String message = jObj.getString("message");
								DialogPopup.dialogBanner(activity, message);
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
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	@Override
	protected void onDestroy() {
		Utils.disableSMSReceiver(this);
		OTP_SCREEN_OPEN = null;
		ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}