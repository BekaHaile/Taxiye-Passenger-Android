package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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


	RelativeLayout relative;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

	LinearLayout llEditText;
	private TextView tvResendCode, tvCallMe;


	String phoneNoToVerify = "", countryCode = "";
	private PinEditTextLayout pinEditTextLayout;

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

			setOTPToEditText(otp);

		} catch(Exception e){
			e.printStackTrace();
		}

		super.onNewIntent(intent);
	}

	private void setOTPToEditText(String otp) {
		if(Utils.checkIfOnlyDigits(otp)){
			if(!"".equalsIgnoreCase(otp)) {
				pinEditTextLayout.setOTPDirectly(otp);
			}
		}
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

		findViewById(R.id.ivTopBanner).setVisibility(View.INVISIBLE);

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
		tvResendCode = (TextView) findViewById(R.id.tvResendCode);
		tvCallMe = (TextView) findViewById(R.id.tvCallMe);

		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		tvCallMe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (1 == Prefs.with(PhoneNoOTPConfirmScreen.this).getInt(Constants.SP_OTP_VIA_CALL_ENABLED, 1)) {
						initiateOTPCallAsync(PhoneNoOTPConfirmScreen.this, phoneNoToVerify);
						startTimerForRetry();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});



		tvResendCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					apiGenerateLoginOtp(PhoneNoOTPConfirmScreen.this, Utils.retrievePhoneNumberTenChars(phoneNoToVerify, countryCode));
					startTimerForRetry();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		startSMSListener();
		try {
			if(getIntent().hasExtra("phone_no_verify")){
				phoneNoToVerify = getIntent().getStringExtra("phone_no_verify");
				textViewOtpNumber.setText(phoneNoToVerify);
				if(getIntent().hasExtra(Constants.KEY_COUNTRY_CODE)){
					countryCode = getIntent().getStringExtra(Constants.KEY_COUNTRY_CODE);
				}
			}
			else {
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}

			try {
				if (1 == Prefs.with(this).getInt(Constants.SP_OTP_VIA_CALL_ENABLED, 1)) {
					tvCallMe.setVisibility(View.VISIBLE);
				} else {
					tvCallMe.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				tvCallMe.setVisibility(View.GONE);
			}

		startTimerForRetry();

	}

	private void verifyClick(String otp, EditText editText){
		if (otp.length() > 0) {
			verifyOtpPhoneNoChange(PhoneNoOTPConfirmScreen.this, phoneNoToVerify, otp);
		} else {
			editText.requestFocus();
			editText.setError(getString(R.string.otp_cant_be_empty));
		}
	}


	@Override
	protected void onResume() {
		super.onResume();

		Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, PhoneNoOTPConfirmScreen.class.getName());

		HomeActivity.checkForAccessTokenChange(this);
	}

	@Override
	protected void onPause() {
		Prefs.with(this).save(Constants.SP_OTP_SCREEN_OPEN, "");
		super.onPause();
	}


	public void verifyOtpPhoneNoChange(final Activity activity, String phoneNo, String otp) {
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();

			params.put("client_id", Config.getAutosClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("phone_no", phoneNo);
			params.put(Constants.KEY_COUNTRY_CODE, countryCode);
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
								DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
							}
						}
					}  catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
		}

	}


	/**
	 * ASync for initiating OTP Call from server
	 */
	public void initiateOTPCallAsync(final Activity activity, String phoneNo) {
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

			HashMap<String, String> params = new HashMap<>();

			params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
			params.put(Constants.KEY_PHONE_NO, phoneNo);
			params.put(Constants.KEY_COUNTRY_CODE, countryCode);
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
								DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
							}
						}
					}  catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
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
		unregisterReceiver(smsReceiver);
		ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}


	private void apiGenerateLoginOtp(final Activity activity, final String phoneNumber){
		if(MyApplication.getInstance().isOnline()){
			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();

			params.put("phone_no", countryCode+phoneNumber);
			params.put(Constants.KEY_COUNTRY_CODE, countryCode);
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
					DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
				}
			});
		} else{
			DialogPopup.alertPopup(this, "", activity.getString(R.string.connection_lost_desc));
		}
	}


	private Handler handler = new Handler();
	private Runnable runnableRetryBlock;
	private int secondsLeftForRetry = 15;
	private void startTimerForRetry(){
		if(runnableRetryBlock == null) {
			runnableRetryBlock = new Runnable() {
				@Override
				public void run() {
					if(secondsLeftForRetry > 0){
						tvResendCode.setText(getString(R.string.resend_code_in, (secondsLeftForRetry > 9?"0:":"0:0")+secondsLeftForRetry));
						tvCallMe.setText(getString(R.string.call_me_in, (secondsLeftForRetry > 9?"0:":"0:0")+secondsLeftForRetry));
						tvResendCode.setEnabled(false);
						tvCallMe.setEnabled(false);
						secondsLeftForRetry--;
						handler.postDelayed(runnableRetryBlock, 1000);
					} else {
						tvResendCode.setText(R.string.resend_code);
						tvCallMe.setText(R.string.call_me);
						tvResendCode.setEnabled(true);
						tvCallMe.setEnabled(true);
						handler.removeCallbacks(runnableRetryBlock);
					}
				}
			};
		}
		secondsLeftForRetry = 15;
		handler.post(runnableRetryBlock);
	}

	private void startSMSListener(){
		SmsRetrieverClient client = SmsRetriever.getClient(this);
		Task<Void> task = client.startSmsRetriever();
		task.addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				registerReceiver(smsReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
			}
		});
		task.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {

			}
		});

	}

	private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getExtras() != null
					&& SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
				Bundle extras = intent.getExtras();
				Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
				if (status != null)
					switch (status.getStatusCode()) {
						case CommonStatusCodes.SUCCESS:
							String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
							if (message != null) {
								setOTPToEditText(Utils.retrieveOTPFromSMS(message));
							}
							break;

						case CommonStatusCodes.TIMEOUT:
							break;
					}
			}
		}
	};

}