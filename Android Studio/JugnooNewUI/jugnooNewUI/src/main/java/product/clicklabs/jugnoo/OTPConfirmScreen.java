package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class OTPConfirmScreen extends BaseActivity implements LocationUpdate, FlurryEventNames{
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView otpHelpText;
	EditText editTextOTP;
	Button buttonVerify;
	
	RelativeLayout relativeLayoutOTPThroughCall;
	TextView textViewOTPNotReceived, textViewCallMe;

    RelativeLayout relativeLayoutChangePhone;
    TextView textViewChangePhone, textViewChange;
	
	
	LinearLayout relative;
	
	boolean loginDataFetched = false;
	
	public static boolean intentFromRegister = true;
	public static EmailRegisterData emailRegisterData;
	public static FacebookRegisterData facebookRegisterData;

	public static String OTP_SCREEN_OPEN = null;
	
	String otpHelpStr = "Please enter the One Time Password you just received via SMS at ";
	
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
			String message = intent.getStringExtra("message");
			String otp = "";
			String[] arr = message.split("Your\\ One\\ Time\\ Password\\ is\\ ");
			String str = arr[1];
			str = str.replaceAll("\\.", "");
			if(Utils.checkIfOnlyDigits(str)){
				otp = str;
			}

			if(!"".equalsIgnoreCase(otp)) {
				editTextOTP.setText(otp);
				editTextOTP.setSelection(editTextOTP.getText().length());
				buttonVerify.performClick();
				OTP_SCREEN_OPEN = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp_confrim);

		Utils.enableSMSReceiver(this);

		loginDataFetched = false;


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		
		otpHelpText = (TextView) findViewById(R.id.otpHelpText); otpHelpText.setTypeface(Fonts.latoRegular(this));
		
		editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.latoRegular(this));
		
		buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Fonts.latoRegular(this));
		
		relativeLayoutOTPThroughCall = (RelativeLayout) findViewById(R.id.relativeLayoutOTPThroughCall);
		textViewOTPNotReceived = (TextView) findViewById(R.id.textViewOTPNotReceived); textViewOTPNotReceived.setTypeface(Fonts.latoLight(this));
		textViewCallMe = (TextView) findViewById(R.id.textViewCallMe); textViewCallMe.setTypeface(Fonts.latoLight(this), Typeface.BOLD);

        relativeLayoutChangePhone = (RelativeLayout) findViewById(R.id.relativeLayoutChangePhone);
        textViewChangePhone = (TextView) findViewById(R.id.textViewChangePhone); textViewChangePhone.setTypeface(Fonts.latoLight(this));
        textViewChange = (TextView) findViewById(R.id.textViewChange); textViewChange.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		editTextOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editTextOTP.setError(null);
			}
		});
		
		
		buttonVerify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String otpCode = editTextOTP.getText().toString().trim();
				if(otpCode.length() > 0){
					if(RegisterScreen.facebookLogin){
						verifyOtpViaFB(OTPConfirmScreen.this, otpCode);
					}
					else{
						verifyOtpViaEmail(OTPConfirmScreen.this, otpCode);
					}
                    FlurryEventLogger.event(OTP_VERIFIED_WITH_SMS);
				}
				else{
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
		
		
		relativeLayoutOTPThroughCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (RegisterScreen.facebookLogin) {
					initiateOTPCallAsync(OTPConfirmScreen.this, facebookRegisterData.phoneNo);
				} else {
					initiateOTPCallAsync(OTPConfirmScreen.this, emailRegisterData.phoneNo);
				}
                FlurryEventLogger.event(CALL_ME_OTP);
			}
		});

        relativeLayoutChangePhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(CHANGE_PHONE_OTP_NOT_RECEIVED);
                startActivity(new Intent(OTPConfirmScreen.this, ChangePhoneBeforeOTPActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		try {
			if(RegisterScreen.facebookLogin){
				otpHelpText.setText(otpHelpStr + " " + facebookRegisterData.phoneNo);
			}
			else{
				otpHelpText.setText(otpHelpStr + " " + emailRegisterData.phoneNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new DeviceTokenGenerator().generateDeviceToken(this, new IDeviceTokenReceiver() {
			
			@Override
			public void deviceTokenReceived(final String regId) {
				Data.deviceToken = regId;
				Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
			}
		});

		OTP_SCREEN_OPEN = "yes";

	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(OTPConfirmScreen.this, 1000, 1);
		}
		HomeActivity.checkForAccessTokenChange(this);

        checkIfRegisterDataNull(this);

	}



    public static boolean checkIfRegisterDataNull(Activity activity){
        try {
            if(emailRegisterData == null && facebookRegisterData == null){
                activity.startActivity(new Intent(activity, SplashNewActivity.class));
                activity.finish();
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	
	@Override
	protected void onPause() {
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
	}
	
	
	
	
	/**
	 * ASync for confirming otp from server
	 */
	public void verifyOtpViaEmail(final Activity activity, String otp) {
        if(!checkIfRegisterDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                if (Data.locationFetcher != null) {
                    Data.latitude = Data.locationFetcher.getLatitude();
                    Data.longitude = Data.locationFetcher.getLongitude();
                }

                params.put("email", emailRegisterData.emailId);
                params.put("password", emailRegisterData.password);
                params.put("device_token", Data.deviceToken);
                params.put("device_type", Data.DEVICE_TYPE);
                params.put("device_name", Data.deviceName);
                params.put("app_version", "" + Data.appVersion);
                params.put("os_version", Data.osVersion);
                params.put("country", Data.country);
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.latitude);
                params.put("longitude", "" + Data.longitude);
                params.put("client_id", Config.getClientId());
                params.put("otp", otp);


                Log.i("params", "" + params.toString());


                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/verify_otp", params,
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
                            Log.i("Server response", "response = " + response);

                            try {
                                jObj = new JSONObject(response);

                                int flag = jObj.getInt("flag");

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                            new JSONParser().parseAccessTokenLoginData(activity, response);
                                            Database.getInstance(OTPConfirmScreen.this).insertEmail(emailRegisterData.emailId);
                                            Database.getInstance(OTPConfirmScreen.this).close();
                                            loginDataFetched = true;
                                        }
                                    } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                } else {
                                    DialogPopup.dismissLoadingDialog();
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                DialogPopup.dismissLoadingDialog();
                            }


                        }
                    });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }

	}
	
	
	
	

	public void verifyOtpViaFB(final Activity activity, String otp) {
        if(!checkIfRegisterDataNull(activity)) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                RequestParams params = new RequestParams();

                if (Data.locationFetcher != null) {
                    Data.latitude = Data.locationFetcher.getLatitude();
                    Data.longitude = Data.locationFetcher.getLongitude();
                }


                params.put("user_fb_id", Data.facebookUserData.fbId);
                params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
                params.put("fb_access_token", Data.facebookUserData.accessToken);
                params.put("fb_mail", Data.facebookUserData.userEmail);
                params.put("username", Data.facebookUserData.userName);

                params.put("device_token", Data.deviceToken);
                params.put("device_type", Data.DEVICE_TYPE);
                params.put("device_name", Data.deviceName);
                params.put("app_version", "" + Data.appVersion);
                params.put("os_version", Data.osVersion);
                params.put("country", Data.country);
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.latitude);
                params.put("longitude", "" + Data.longitude);
                params.put("client_id", Config.getClientId());
                params.put("otp", otp);


                Log.i("params", "" + params);


                AsyncHttpClient client = Data.getClient();
                client.post(Config.getServerUrl() + "/verify_otp", params,
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

                                int flag = jObj.getInt("flag");

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                        if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                            new JSONParser().parseAccessTokenLoginData(activity, response);
                                            loginDataFetched = true;
                                            Database.getInstance(OTPConfirmScreen.this).insertEmail(Data.facebookUserData.userEmail);
                                            Database.getInstance(OTPConfirmScreen.this).close();
                                        }
                                    } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                } else {
                                    DialogPopup.dismissLoadingDialog();
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.dismissLoadingDialog();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }
	}
	
	
	/**
	 * ASync for initiating OTP Call from server
	 */
	public void initiateOTPCallAsync(final Activity activity, String phoneNo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			params.put("phone_no", phoneNo);
			Log.i("phone_no", ">"+phoneNo);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/send_otp_via_call", params,
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
							Log.i("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);

								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									int flag = jObj.getInt("flag");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else{
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									String message = jObj.getString("message");
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										DialogPopup.alertPopup(activity, "", message);
									}
									DialogPopup.dismissLoadingDialog();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			Database2.getInstance(OTPConfirmScreen.this).updateDriverLastLocationTime();
			Database2.getInstance(OTPConfirmScreen.this).close();
			startActivity(new Intent(OTPConfirmScreen.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			ActivityCompat.finishAffinity(this);
		}
	}
	

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
		if(intentFromRegister){
			Intent intent = new Intent(OTPConfirmScreen.this, RegisterScreen.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(OTPConfirmScreen.this, SplashLogin.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		}
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		Utils.disableSMSReceiver(this);
		OTP_SCREEN_OPEN = null;
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}


	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}


	
}





