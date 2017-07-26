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

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.PinEditTextLayout;
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

	Button buttonVerify;
	LinearLayout buttonOtpViaCall;
	LinearLayout linearLayoutGiveAMissedCall;


	RelativeLayout relative;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

	LinearLayout llEditText;


	String phoneNoToVerify = "";
	private PinEditTextLayout pinEditTextLayout;
	LinearLayout rlResendOTP;

	@Override
	protected void onNewIntent(Intent intent) {

		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");
				otp = Utils.retrieveOTPFromSMS(message);
			} else if(intent.hasExtra("otp")){
				otp = intent.getStringExtra("otp");
			}

			if(Utils.checkIfOnlyDigits(otp)){
				if(!"".equalsIgnoreCase(otp)) {
//					editTextOTP.setText(otp);
//					editTextOTP.setSelection(editTextOTP.getText().length());
//					buttonVerify.performClick();
					pinEditTextLayout.setOTPDirectly(otp);
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

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(PhoneNoOTPConfirmScreen.this, relative, 1134, 720, false);

		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));

		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.mavenLight(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		imageViewSep = (ImageView) findViewById(R.id.imageViewSep); imageViewSep.setVisibility(View.GONE);
		imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber); imageViewChangePhoneNumber.setVisibility(View.GONE);

		editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.mavenMedium(this));

		buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Fonts.mavenRegular(this));
		buttonOtpViaCall = (LinearLayout) findViewById(R.id.buttonOtpViaCall);
		linearLayoutGiveAMissedCall = (LinearLayout) findViewById(R.id.linearLayoutGiveAMissedCall);
		((TextView) findViewById(R.id.textViewGiveAMissedCall)).setTypeface(Fonts.mavenLight(this));

		findViewById(R.id.ivTopBanner).setVisibility(View.GONE);
		findViewById(R.id.ivBottomBanner).setVisibility(View.GONE);

		rlResendOTP = (LinearLayout) findViewById(R.id.rlResendOTP);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

		llEditText = (LinearLayout) findViewById(R.id.llEditText);
		pinEditTextLayout = new PinEditTextLayout(llEditText, new PinEditTextLayout.Callback() {
			@Override
			public void onOTPComplete(String otp, EditText editText) {
				verifyClick(otp, editText);
			}
		});

		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));


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
				verifyClick(editTextOTP.getText().toString().trim(), editTextOTP);
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
					if (1 == Prefs.with(PhoneNoOTPConfirmScreen.this).getInt(Constants.SP_OTP_VIA_CALL_ENABLED, 1)) {
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
					if (!"".equalsIgnoreCase(Prefs.with(PhoneNoOTPConfirmScreen.this)
							.getString(Constants.SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
						DialogPopup.alertPopupTwoButtonsWithListeners(PhoneNoOTPConfirmScreen.this, "",
								getResources().getString(R.string.give_missed_call_dialog_text),
								getResources().getString(R.string.call_us),
								getResources().getString(R.string.cancel),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Utils.openCallIntent(PhoneNoOTPConfirmScreen.this, Prefs.with(PhoneNoOTPConfirmScreen.this)
												.getString(Constants.SP_KNOWLARITY_MISSED_CALL_NUMBER, ""));
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

		rlResendOTP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Utils.disableSMSReceiver(PhoneNoOTPConfirmScreen.this);
					editTextOTP.setError(null);
					apiGenerateLoginOtp(PhoneNoOTPConfirmScreen.this, Utils.retrievePhoneNumberTenChars(phoneNoToVerify));
				} catch(Exception e){
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


		try{
			if(!"".equalsIgnoreCase(Prefs.with(this).getString(Constants.SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
				linearLayoutGiveAMissedCall.setVisibility(View.VISIBLE);
			}
			else{
				linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			}

			if(1 == Prefs.with(this).getInt(Constants.SP_OTP_VIA_CALL_ENABLED, 1)) {
				buttonOtpViaCall.setVisibility(View.VISIBLE);
			}
			else{
				buttonOtpViaCall.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
			linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			buttonOtpViaCall.setVisibility(View.GONE);
		}



	}

	private void verifyClick(String otp, EditText editText){
		if (otp.length() > 0) {
			verifyOtpPhoneNoChange(PhoneNoOTPConfirmScreen.this, phoneNoToVerify, otp);
		} else {
			editText.requestFocus();
			editText.setError("OTP can't be empty");
		}
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

		Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, PhoneNoOTPConfirmScreen.class.getName());
		Utils.enableSMSReceiver(this);

		HomeActivity.checkForAccessTokenChange(this);
	}

	@Override
	protected void onPause() {
		Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, "");
		Utils.disableSMSReceiver(this);
		super.onPause();
	}


	public void verifyOtpPhoneNoChange(final Activity activity, String phoneNo, String otp) {
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put("client_id", Config.getAutosClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("phone_no", phoneNo);
			params.put("verification_token", otp);

			Log.i("params", ">" + params);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().verifyMyContactNumber(params, new Callback<SettleUserDebt>() {
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
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
			params.put(Constants.KEY_PHONE_NO, phoneNo);
			Log.i("params", ">"+params);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().sendNewNumberOtpViaCall(params, new Callback<SettleUserDebt>() {
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
		ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}


	private void apiGenerateLoginOtp(final Activity activity, final String phoneNumber){
		if(MyApplication.getInstance().isOnline()){
			DialogPopup.showLoadingDialog(activity, "Loading...");
			HashMap<String, String> params = new HashMap<>();

			params.put("phone_no", "+91"+phoneNumber);
			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());
			params.put("login_type", "0");

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().generateLoginOtp(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i("TAG", "generateLoginOtp response = " + responseStr);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dismissLoadingDialog();
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									Utils.enableSMSReceiver(PhoneNoOTPConfirmScreen.this);
									DialogPopup.alertPopup(activity, "", JSONParser.getServerMessage(jObj));
								}
							} else {
								DialogPopup.alertPopup(activity, "", jObj.optString("error"));
							}
							DialogPopup.dismissLoadingDialog();
						}


					} catch (Exception e){
						e.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("TAG", "loginUsingEmailOrPhoneNo error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		} else{
			DialogPopup.alertPopup(this, "", Data.CHECK_INTERNET_MSG);
		}
	}

}