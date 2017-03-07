package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.LinkedWalletStatus;
import product.clicklabs.jugnoo.datastructure.LoginVia;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.PinEntryEditText;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class OTPConfirmScreen extends BaseActivity implements FlurryEventNames, Constants{

	private final String TAG = "OTP screen";

	ImageView imageViewBack;

	TextView textViewOtpNumber, tvOtpPhoneNumber, textViewForgotPassword;
	ImageView imageViewChangePhoneNumber;
	EditText editTextOTP, etPhoneNew, etPasswordNew;

	LinearLayout linearLayoutWaiting;
	TextView textViewCounter, tvOtpViaCall;
	ImageView imageViewYellowLoadingBar, imageViewWalletIcon, ivResend;

	Button buttonVerify, buttonOtpViaCall;
	LinearLayout linearLayoutOtherOptions, linearLayoutGiveAMissedCall, llPasswordLogin, llOTP;
	private Animation tweenAnimation;

	RelativeLayout relative, rlOTPTimer;

	LinearLayout rlResendOTP;
	TextView textViewScroll;
	
	boolean loginDataFetched = false;
	private int linkedWallet = 0;
	//private int userVerified = 0;
	private String linkedWalletErrorMsg = "";
	private Button bEmailLoginNew;
	
	public static boolean intentFromRegister = true, backFromMissedCall;
	public static EmailRegisterData emailRegisterData;
	public static FacebookRegisterData facebookRegisterData;
	public static GoogleRegisterData googleRegisterData;
	private boolean giveAMissedCall;
	private Handler handler = new Handler();
	private String signupBy = "", email = "", password = "";
	private RelativeLayout rlProgress, llLoginNew, rlOTPContainer;
	private ProgressWheel progressBar;
	private boolean runAfterDelay;
	private TextView tvProgress;
	private boolean onlyDigits, openHomeSwitcher = false;
	public static boolean phoneNoLogin = false;
	int duration = 500, otpLength = 4;
	private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
	private View view1, view2, view3, view4, view5;
	private PinEntryEditText txtPinEntry;
	private ProgressDialog missedCallDialog;

	@Override
	protected void onStart() {
		super.onStart();
//		FlurryAgent.init(this, Config.getFlurryKey());
//		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		retrieveOTPFromSMS(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp_confrim);

		loginDataFetched = false;
		openHomeSwitcher = false;

		try {
			if(getIntent().hasExtra(LINKED_WALLET)){
                linkedWallet = getIntent().getIntExtra(LINKED_WALLET, 0);
                linkedWalletErrorMsg = getIntent().getStringExtra(LINKED_WALLET_MESSAGE);
                if((!"".equalsIgnoreCase(linkedWalletErrorMsg)) && (linkedWalletErrorMsg != null)){
                    DialogPopup.dialogBanner(OTPConfirmScreen.this, linkedWalletErrorMsg);
                }
                signupBy = getIntent().getStringExtra("signup_by");
                email = getIntent().getStringExtra("email");
                password = getIntent().getStringExtra("password");
				if(getIntent().hasExtra("otp_length")){
					otpLength = Integer.parseInt(getIntent().getStringExtra("otp_length"));
				}


                if(email.length() > 0){
                    onlyDigits = Utils.checkIfOnlyDigits(email);
                }

            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*if(getIntent().hasExtra(USER_VERIFIED)){
			userVerified = getIntent().getIntExtra(USER_VERIFIED, 0);
		}*/


		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

		((TextView)findViewById(R.id.otpHelpText)).setTypeface(Fonts.mavenRegular(this));
		textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber); textViewOtpNumber.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);

		rlOTPContainer = (RelativeLayout) findViewById(R.id.rlOTPContainer);
		imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber);
		imageViewWalletIcon = (ImageView) findViewById(R.id.imageViewWalletIcon);

		rlOTPTimer = (RelativeLayout) findViewById(R.id.rlOTPTimer);
		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		textViewCounter = (TextView) findViewById(R.id.textViewCounter); textViewCounter.setTypeface(Fonts.mavenRegular(this));
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);

		editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.mavenMedium(this));

		buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Fonts.mavenRegular(this));

		linearLayoutOtherOptions = (LinearLayout) findViewById(R.id.linearLayoutOtherOptions);
		buttonOtpViaCall = (Button) findViewById(R.id.buttonOtpViaCall); buttonOtpViaCall.setTypeface(Fonts.mavenRegular(this));
		rlResendOTP = (LinearLayout) findViewById(R.id.rlResendOTP);
		ivResend = (ImageView) findViewById(R.id.ivResend);
		tvOtpViaCall = (TextView) findViewById(R.id.tvOtpViaCall); tvOtpViaCall.setTypeface(Fonts.mavenRegular(this));
		linearLayoutGiveAMissedCall = (LinearLayout) findViewById(R.id.linearLayoutGiveAMissedCall);
		((TextView) findViewById(R.id.textViewGiveAMissedCall)).setTypeface(Fonts.mavenRegular(this));
		rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
		tvProgress = (TextView) findViewById(R.id.tvProgress); tvProgress.setTypeface(Fonts.mavenRegular(this));
		progressBar = (ProgressWheel) findViewById(R.id.progressBar);

		llLoginNew = (RelativeLayout) findViewById(R.id.llLoginNew); llLoginNew.setVisibility(View.GONE);
		txtPinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);

		llPasswordLogin = (LinearLayout) findViewById(R.id.llPasswordLogin);

		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		tvOtpPhoneNumber = (TextView) findViewById(R.id.tvOtpPhoneNumber); tvOtpPhoneNumber.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
		textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
		etPhoneNew = (EditText) findViewById(R.id.etPhoneNew); etPhoneNew.setText(email);
		etPasswordNew = (EditText) findViewById(R.id.etPasswordNew);
		bEmailLoginNew = (Button) findViewById(R.id.bEmailLoginNew);

		etOtp1 = (EditText) findViewById(R.id.etOtp1);
		etOtp2 = (EditText) findViewById(R.id.etOtp2);
		etOtp3 = (EditText) findViewById(R.id.etOtp3);
		etOtp4 = (EditText) findViewById(R.id.etOtp4);
		etOtp5 = (EditText) findViewById(R.id.etOtp5);
		etOtp6 = (EditText) findViewById(R.id.etOtp6);

		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);
		view3 = (View) findViewById(R.id.view3);
		view4 = (View) findViewById(R.id.view4);
		view5 = (View) findViewById(R.id.view5);

		etOtp1.addTextChangedListener(new CustomTextWatcher(null, etOtp1, etOtp2));
		etOtp2.addTextChangedListener(new CustomTextWatcher(etOtp1, etOtp2, etOtp3));
		etOtp3.addTextChangedListener(new CustomTextWatcher(etOtp2, etOtp3, etOtp4));
		etOtp4.addTextChangedListener(new CustomTextWatcher(etOtp3, etOtp4, etOtp5));
		etOtp5.addTextChangedListener(new CustomTextWatcher(etOtp4, etOtp5, etOtp6));
		etOtp6.addTextChangedListener(new CustomTextWatcher(etOtp5, etOtp6, null));

		etOtp1.setOnKeyListener(new CustomBackKeyListener(null, etOtp2));
		etOtp2.setOnKeyListener(new CustomBackKeyListener(etOtp1, etOtp3));
		etOtp3.setOnKeyListener(new CustomBackKeyListener(etOtp2, etOtp4));
		etOtp4.setOnKeyListener(new CustomBackKeyListener(etOtp3, etOtp5));
		etOtp5.setOnKeyListener(new CustomBackKeyListener(etOtp4, etOtp6));
		etOtp6.setOnKeyListener(new CustomBackKeyListener(etOtp5, null));


		llOTP = (LinearLayout) findViewById(R.id.llOTP);

		tweenAnimation = AnimationUtils.loadAnimation(OTPConfirmScreen.this, R.anim.tween);

		//createEditTextOtp();

		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.eventGA(ACQUISITION, TAG, "Back");
				performBackPressed();
			}
		});

		txtPinEntry.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() == 4){
					buttonVerify.performClick();
					rlOTPTimer.setVisibility(View.GONE);
				}
			}
		});


		relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextOTP.setError(null);
			}
		});

		buttonVerify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//String otpCode = editTextOTP.getText().toString().trim();
				String otpCode = txtPinEntry.getText().toString().trim();
				if (otpCode.length() > 0) {
					if(missedCallDialog != null) {
						missedCallDialog.dismiss();
					}
					//rlProgress.setVisibility(View.GONE);
					if (SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
						verifyOtpViaFB(OTPConfirmScreen.this, otpCode, linkedWallet);
						/*if(userVerified == 1){
							sendOTP(otpCode);
						} else {
							verifyOtpViaFB(OTPConfirmScreen.this, otpCode, linkedWallet);
						}*/

					} else if (SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType) {
						verifyOtpViaGoogle(OTPConfirmScreen.this, otpCode, linkedWallet);
					} else {
						//verifyOtpViaEmail(OTPConfirmScreen.this, otpCode, linkedWallet);
						apiLoginUsingOtp(OTPConfirmScreen.this, otpCode, email);
					}
					FlurryEventLogger.event(OTP_VERIFIED_WITH_SMS);
                    Bundle bundle = new Bundle();
                    MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.VERIFY_ME, bundle);
					FlurryEventLogger.eventGA(ACQUISITION, TAG, "Verify me");
				} else {
//					editTextOTP.requestFocus();
//					editTextOTP.setError("OTP can't be empty");

					txtPinEntry.requestFocus();
					txtPinEntry.setError("OTP can't be empty");
				}
			}
		});


		editTextOTP.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				buttonVerify.performClick();
				return true;
			}
		});

		txtPinEntry.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				buttonVerify.performClick();
				return true;
			}
		});

		rlResendOTP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Utils.disableSMSReceiver(OTPConfirmScreen.this);
					editTextOTP.setError(null);
					if(missedCallDialog != null) {
						missedCallDialog.dismiss();
					}
//					rlProgress.setVisibility(View.GONE);
					apiGenerateLoginOtp(OTPConfirmScreen.this, email);

				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		editTextOTP.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					editTextOTP.setTextSize(20);
				} else {
					editTextOTP.setTextSize(15);
				}

				if(s.length() == 4){
					buttonVerify.performClick();
					rlOTPTimer.setVisibility(View.GONE);
				}
			}
		});


		editTextOTP.setOnFocusChangeListener(onFocusChangeListener);
		editTextOTP.setOnClickListener(onClickListener);


		linearLayoutGiveAMissedCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Utils.disableSMSReceiver(OTPConfirmScreen.this);
					editTextOTP.setError(null);
					txtPinEntry.setError(null);
					tweenAnimation.cancel();
					if(missedCallDialog != null) {
						missedCallDialog.dismiss();
					}
//					rlProgress.setVisibility(View.GONE);
					linearLayoutGiveAMissedCall.clearAnimation();
					if(!"".equalsIgnoreCase(Prefs.with(OTPConfirmScreen.this).getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
						DialogPopup.alertPopupTwoButtonsWithListeners(OTPConfirmScreen.this, "",
								getResources().getString(R.string.give_missed_call_dialog_text),
								getResources().getString(R.string.call_us),
								getResources().getString(R.string.cancel),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										giveAMissedCall = true;
										Utils.openCallIntent(OTPConfirmScreen.this, Prefs.with(OTPConfirmScreen.this)
												.getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""));
										backFromMissedCall = true;
										FlurryEventLogger.event(GIVE_MISSED_CALL);
                                        Bundle bundle = new Bundle();
                                        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.GIVE_A_MISS_CALL, bundle);
										FlurryEventLogger.eventGA(ACQUISITION, TAG, "Give a miss call");
									}
								},
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								}, false, false
						);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		llPasswordLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.disableSMSReceiver(OTPConfirmScreen.this);
				llLoginNew.setVisibility(View.VISIBLE);
				Animation animation8 = AnimationUtils.loadAnimation(OTPConfirmScreen.this, R.anim.right_in);
				animation8.setFillAfter(true);
				animation8.setDuration(duration);
				llLoginNew.startAnimation(animation8);

				Animation animation9 = AnimationUtils.loadAnimation(OTPConfirmScreen.this, R.anim.right_out);
				animation9.setFillAfter(false);
				animation9.setDuration(duration);
				rlOTPContainer.startAnimation(animation9);
				rlOTPContainer.setVisibility(View.GONE);
			}
		});


		imageViewChangePhoneNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				editTextOTP.setError(null);
                FlurryEventLogger.event(CHANGE_PHONE_OTP_NOT_RECEIVED);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.EDIT_PHONE_NUMBER, bundle);

                FlurryEventLogger.eventGA(ACQUISITION, TAG, "Edit phone number");
				Intent intent = new Intent(OTPConfirmScreen.this, ChangePhoneBeforeOTPActivity.class);
				intent.putExtra(LINKED_WALLET, linkedWallet);
				startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

		textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(OTPConfirmScreen.this, editTextOTP);
				ForgotPasswordScreen.emailAlready = etPhoneNew.getText().toString();
				startActivity(new Intent(OTPConfirmScreen.this, ForgotPasswordScreen.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
				FlurryEventLogger.event(FORGOT_PASSWORD);
				Bundle bundle = new Bundle();
				MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+FirebaseEvents.LOGIN_PAGE+"_"+FirebaseEvents.FORGET_PASSWORD, bundle);
				FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "Login Page", "Forget password");
			}
		});

		bEmailLoginNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(OTPConfirmScreen.this, etPhoneNew);
					String email = etPhoneNew.getText().toString().trim();
					String password = etPasswordNew.getText().toString().trim();
					if ("".equalsIgnoreCase(email)) {
						etPhoneNew.requestFocus();
						etPhoneNew.setError(getResources().getString(R.string.nl_login_phone_empty_error));
					} else {
						if ("".equalsIgnoreCase(password)) {
							etPasswordNew.requestFocus();
							etPasswordNew.setError(getResources().getString(R.string.nl_login_empty_password_error));
						} else {
							boolean onlyDigits = Utils.checkIfOnlyDigits(email);
							if (onlyDigits) {
								email = Utils.retrievePhoneNumberTenChars(email);
								if (!Utils.validPhoneNumber(email)) {
									etPhoneNew.requestFocus();
									etPhoneNew.setError(getResources().getString(R.string.invalid_phone_error));
								} else {
									email = "+91" + email;
									sendLoginValues(OTPConfirmScreen.this, email, password, true, true);
									phoneNoLogin = true;
								}
							} else {
								if (Utils.isEmailValid(email)) {
									sendLoginValues(OTPConfirmScreen.this, email, password, false, true);
									phoneNoLogin = false;
								} else {
									etPhoneNew.requestFocus();
									etPhoneNew.setError("Please enter valid email");
								}
							}
                            Bundle bundle = new Bundle();
                            MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+FirebaseEvents.LOGIN_PAGE+"_"+FirebaseEvents.LOGIN, bundle);
							FlurryEventLogger.event(LOGIN_VIA_EMAIL);
						}
					}
			}
		});


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		try {
			//jungooautos-verify://app?otp=1234
			Uri data = getIntent().getData();
			String host = "app";
			Gson gson = new Gson();

			if(data != null && data.getHost().equalsIgnoreCase(host)) {
				String otp = data.getQueryParameter("otp");

				String registrationMode = Prefs.with(this).getString(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE,
						"" + SplashNewActivity.registerationType);
				String registerData = Prefs.with(this).getString(SPLabels.LOGIN_UNVERIFIED_DATA, "");

				if((""+SplashNewActivity.RegisterationType.FACEBOOK).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					facebookRegisterData = gson.fromJson(registerData, FacebookRegisterData.class);
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
				}
				else if((""+SplashNewActivity.RegisterationType.GOOGLE).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					googleRegisterData = gson.fromJson(registerData, GoogleRegisterData.class);
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
				}
				else if((""+SplashNewActivity.RegisterationType.EMAIL).equalsIgnoreCase(registrationMode)
						&& !"".equalsIgnoreCase(registerData)){
					emailRegisterData = gson.fromJson(registerData, EmailRegisterData.class);
					textViewOtpNumber.setText(emailRegisterData.phoneNo);
				}
				if(otp != null && !"".equalsIgnoreCase(otp)){
//					editTextOTP.setText(otp);
//					editTextOTP.setSelection(editTextOTP.getText().length());
//					buttonVerify.performClick();

					txtPinEntry.setText(otp);
					txtPinEntry.setSelection(txtPinEntry.getText().length());
					buttonVerify.performClick();
				}
			}
			else{
				if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
					textViewOtpNumber.setText(facebookRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, "" + SplashNewActivity.RegisterationType.FACEBOOK);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(facebookRegisterData, FacebookRegisterData.class));
				}
				else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
					textViewOtpNumber.setText(googleRegisterData.phoneNo);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+SplashNewActivity.RegisterationType.GOOGLE);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(googleRegisterData, GoogleRegisterData.class));
				}
				else{
					textViewOtpNumber.setText(email);
					tvOtpPhoneNumber.setText(email);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA_TYPE, ""+SplashNewActivity.RegisterationType.EMAIL);
					Prefs.with(this).save(SPLabels.LOGIN_UNVERIFIED_DATA, gson.toJson(emailRegisterData, EmailRegisterData.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewCounter.setText("0:10");

		/*if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
			imageViewWalletIcon.setVisibility(View.VISIBLE);
			imageViewWalletIcon.setImageResource(R.drawable.ic_paytm_big);
			ivResend.setVisibility(View.VISIBLE);
			tvOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		}
		else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
			imageViewWalletIcon.setVisibility(View.VISIBLE);
			imageViewWalletIcon.setImageResource(R.drawable.ic_mobikwik_big);
			ivResend.setVisibility(View.VISIBLE);
			tvOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		}
		else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
			imageViewWalletIcon.setVisibility(View.VISIBLE);
			imageViewWalletIcon.setImageResource(R.drawable.ic_freecharge_big);
			ivResend.setVisibility(View.VISIBLE);
			tvOtpViaCall.setText(getResources().getString(R.string.resend_otp));
		}
		else{
			imageViewWalletIcon.setVisibility(View.GONE);
			ivResend.setVisibility(View.GONE);
			tvOtpViaCall.setText(getResources().getString(R.string.receive_otp_via_call));
		}*/

		startOTPTimer();

		try{
			if(!"".equalsIgnoreCase(Prefs.with(OTPConfirmScreen.this).getString(SP_KNOWLARITY_MISSED_CALL_NUMBER, ""))) {
				linearLayoutGiveAMissedCall.setVisibility(View.VISIBLE);
			}
			else{
				linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			}

			/*if(1 == Prefs.with(OTPConfirmScreen.this).getInt(SP_OTP_VIA_CALL_ENABLED, 1)
					|| linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()
					|| linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()
					|| linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()) {
				rlResendOTP.setVisibility(View.VISIBLE);
			}
			else{
				rlResendOTP.setVisibility(View.GONE);
			}*/
		} catch(Exception e){
			e.printStackTrace();
			linearLayoutGiveAMissedCall.setVisibility(View.GONE);
			//rlResendOTP.setVisibility(View.GONE);
		}

		rlOTPTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rlOTPTimer.setVisibility(View.GONE);
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				retrieveOTPFromSMS(getIntent());
			}
		}, 100);

	}

	private void createEditTextOtp(){
		if(otpLength == 4){
			view4.setVisibility(View.GONE);
			etOtp5.setVisibility(View.GONE);
			view5.setVisibility(View.GONE);
			etOtp6.setVisibility(View.GONE);
			etOtp4.addTextChangedListener(new CustomTextWatcher(etOtp3, etOtp4, null));
		} else if(otpLength == 5){
			view5.setVisibility(View.GONE);
			etOtp6.setVisibility(View.GONE);
			etOtp5.addTextChangedListener(new CustomTextWatcher(etOtp4, etOtp5, null));
		}
	}

	private void startOTPTimer(){
		try{
			long timerDuration = 10000;
			if(getIntent().getIntExtra("show_timer", 0) == 1){
				rlOTPTimer.setVisibility(View.VISIBLE);
				linearLayoutOtherOptions.setVisibility(View.GONE);
				CustomCountDownTimer customCountDownTimer = new CustomCountDownTimer(timerDuration, 5);
				customCountDownTimer.start();
			}
			else{
				throw new Exception();
			}
		} catch(Exception e){
			rlOTPTimer.setVisibility(View.GONE);
			linearLayoutOtherOptions.setVisibility(View.VISIBLE);
		}
	}

	private String getLoggedInAccesToken(){
		if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
			return facebookRegisterData.accessToken;
		}
		else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
			return googleRegisterData.accessToken;
		}
		else{
			return emailRegisterData.accessToken;
		}
	}

	class CustomCountDownTimer extends CountDownTimer {

		private final long mMillisInFuture;
		public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mMillisInFuture = millisInFuture;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			double percent = (((double)millisUntilFinished) * 100.0) / mMillisInFuture;

			double widthToSet = percent * ((double) (ASSL.Xscale() * 530)) / 100.0;

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewYellowLoadingBar.getLayoutParams();
			params.width = (int) widthToSet;
			imageViewYellowLoadingBar.setLayoutParams(params);


			long seconds = (long) Math.ceil(((double)millisUntilFinished) / 1000.0d);
			String text = seconds < 10 ? "0:0"+seconds : "0:"+seconds;
			textViewCounter.setText(text);
		}

		@Override
		public void onFinish() {
			rlOTPTimer.setVisibility(View.GONE);
			linearLayoutOtherOptions.setVisibility(View.VISIBLE);
		}
	}


	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(missedCallDialog != null) {
							missedCallDialog.dismiss();
						}
//						rlProgress.setVisibility(View.GONE);
						/*if(rlOTPTimer.getVisibility() == View.VISIBLE){
							scrollView.smoothScrollTo(0, editTextOTP.getBottom());
						} else {
							scrollView.smoothScrollTo(0, buttonVerify.getTop());
						}*/
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
					if(missedCallDialog != null) {
						missedCallDialog.dismiss();
					}
//					rlProgress.setVisibility(View.GONE);
					/*if(rlOTPTimer.getVisibility() == View.VISIBLE){
						scrollView.smoothScrollTo(0, editTextOTP.getBottom());
					} else {
						scrollView.smoothScrollTo(0, buttonVerify.getTop());
					}*/
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

		Prefs.with(this).save(SP_OTP_SCREEN_OPEN, OTPConfirmScreen.class.getName());
		Utils.enableSMSReceiver(this);

		MyApplication.getInstance().getLocationFetcher().connect(locationUpdate, 1000);
		HomeActivity.checkForAccessTokenChange(this);

		try {
			if(giveAMissedCall) {
				giveAMissedCall = false;
				//buttonVerify.performClick();
				missedCallDialog = DialogPopup.showLoadingDialogNewInstance(OTPConfirmScreen.this, "Loading...");
				//rlProgress.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				tvProgress.setText(getResources().getString(R.string.trying_to_verify));
				progressBar.spin();
				if (signupBy.equalsIgnoreCase("email")) {
					if (onlyDigits) {
						email = "+91" + email;
						//sendLoginValues(OTPConfirmScreen.this, email, password, true, false);
						apiLoginUsingOtp(OTPConfirmScreen.this, "99999", email);
					} else {
						sendLoginValues(OTPConfirmScreen.this, email, password, false, false);
					}
				} else if (signupBy.equalsIgnoreCase("facebook")) {
					sendFacebookLoginValues(OTPConfirmScreen.this);
				} else if (signupBy.equalsIgnoreCase("google")) {
					sendGoogleLoginValues(OTPConfirmScreen.this);
				}
				// api call
				//handler.postDelayed(runnable, 5000);
			}
			if(backFromMissedCall){
				backFromMissedCall = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			runAfterDelay = true;
			if(signupBy.equalsIgnoreCase("email")){
				if(onlyDigits){
					email = "+91"+email;
					//sendLoginValues(OTPConfirmScreen.this, email, password, true, false);
					apiLoginUsingOtp(OTPConfirmScreen.this, "99999", email);
				} else{
					sendLoginValues(OTPConfirmScreen.this, email, password, false, false);
				}
			} else if(signupBy.equalsIgnoreCase("facebook")){
				sendFacebookLoginValues(OTPConfirmScreen.this);
			} else if(signupBy.equalsIgnoreCase("google")){
				sendGoogleLoginValues(OTPConfirmScreen.this);
			}
		}
	};


    public static boolean checkIfRegisterDataNull(Activity activity){
        try {
            if(emailRegisterData == null && facebookRegisterData == null && googleRegisterData == null){
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
		Prefs.with(this).save(SP_OTP_SCREEN_OPEN, "");
		Utils.disableSMSReceiver(this);
		try{
			MyApplication.getInstance().getLocationFetcher().destroy();
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
	}

	private void showErrorOnMissedCallBack(){
		if(runAfterDelay) {
			runAfterDelay = false;
			if(missedCallDialog != null){
				missedCallDialog.dismiss();
			}
			DialogPopup.alertPopup(OTPConfirmScreen.this, "", getResources().getString(R.string.we_could_not_verify));
			//rlProgress.setVisibility(View.VISIBLE);
			//progressBar.setVisibility(View.GONE);
			//tvProgress.setText(getResources().getString(R.string.we_could_not_verify));
		}
	}

	private void apiLoginUsingOtp(final Activity activity, String otp, String phoneNumber){
			if(MyApplication.getInstance().isOnline()){
				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

				params.put("phone_no", "+91"+Utils.retrievePhoneNumberTenChars(phoneNumber));
				params.put("device_token", MyApplication.getInstance().getDeviceToken());
				params.put("device_name", MyApplication.getInstance().deviceName());
				params.put("os_version", MyApplication.getInstance().osVersion());
				params.put("country", MyApplication.getInstance().country());
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);
				params.put("client_id", Config.getAutosClientId());
				params.put("login_otp", otp);
				params.put("login_type", "0");
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				} else{
					params.put("device_rooted", "0");
				}

				Log.i("params", "" + params.toString());

				new HomeUtil().putDefaultParams(params);

				RestClient.getApiService().loginUsingOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						DialogPopup.dismissLoadingDialog();
						if(missedCallDialog != null){
							missedCallDialog.dismiss();
						}
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "verifyOtp response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);

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
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.EMAIL_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										//MyApplication.getInstance().getDatabase().insertEmail(emailRegisterData.emailId);
										//MyApplication.getInstance().getDatabase().close();
										loginDataFetched = true;
										firebaseEventWalletAtSignup();
									}
								} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_ALREADY_VERIFIED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								}else {
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "loginUsingOtp error="+error.toString());
						if(missedCallDialog != null){
							missedCallDialog.dismiss();
						}
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else{
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}

	private void apiGenerateLoginOtp(final Activity activity, final String phoneNumber){
		if(MyApplication.getInstance().isOnline()){
			DialogPopup.showLoadingDialog(activity, "Loading...");
			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

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
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "=" + params);
			new HomeUtil().putDefaultParams(params);

			RestClient.getApiService().generateLoginOtp(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "generateLoginOtp response = " + responseStr);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dismissLoadingDialog();
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									Utils.enableSMSReceiver(OTPConfirmScreen.this);
									startOTPTimer();
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
					Log.e(TAG, "loginUsingEmailOrPhoneNo error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		} else{
			DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.CHECK_INTERNET_MSG);
		}
	}

	/**
	 * ASync for confirming otp from server
	 */
	public void verifyOtpViaEmail(final Activity activity, String otp, final int linkedWallet) {
        if(!checkIfRegisterDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

                params.put("email", emailRegisterData.emailId);
                params.put("password", emailRegisterData.password);
				params.put("device_token", MyApplication.getInstance().getDeviceToken());
                params.put("device_name", MyApplication.getInstance().deviceName());
                params.put("os_version", MyApplication.getInstance().osVersion());
                params.put("country", MyApplication.getInstance().country());
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getAutosClientId());
                params.put("otp", otp);
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "verifyOtp response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);

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
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.EMAIL_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										MyApplication.getInstance().getDatabase().insertEmail(emailRegisterData.emailId);
										MyApplication.getInstance().getDatabase().close();
										loginDataFetched = true;
										firebaseEventWalletAtSignup();
									}
								} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								} else if (ApiResponseFlags.AUTH_ALREADY_VERIFIED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", error);
								}else {
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "verifyOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }

	}


	public void verifyOtpViaFB(final Activity activity, String otp, final int linkedWallet) {
        if(!checkIfRegisterDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();


                params.put("user_fb_id", facebookRegisterData.fbId);
                params.put("user_fb_name", facebookRegisterData.fbName);
                params.put("fb_access_token", facebookRegisterData.accessToken);
                params.put("fb_mail", facebookRegisterData.fbUserEmail);
                params.put("username", facebookRegisterData.fbUserName);

				params.put("device_token", MyApplication.getInstance().getDeviceToken());
                params.put("device_name", MyApplication.getInstance().deviceName());
                params.put("os_version", MyApplication.getInstance().osVersion());
                params.put("country", MyApplication.getInstance().country());
                params.put("unique_device_id", Data.uniqueDeviceId);
                params.put("latitude", "" + Data.loginLatitude);
                params.put("longitude", "" + Data.loginLongitude);
                params.put("client_id", Config.getAutosClientId());
                params.put("otp", otp);
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

                Log.i("params", "" + params);


				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.v(TAG, "verifyOtp response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);

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
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.FACEBOOK_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										loginDataFetched = true;
										MyApplication.getInstance().getDatabase().insertEmail(facebookRegisterData.fbUserEmail);
										MyApplication.getInstance().getDatabase().close();
										firebaseEventWalletAtSignup();
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "verifyOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});

            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        }
	}


	public void verifyOtpViaGoogle(final Activity activity, String otp, final int linkedWallet) {
		if(!checkIfRegisterDataNull(activity)) {
			if (MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<>();

				Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
				Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

				params.put("user_google_id", googleRegisterData.id);
				params.put("email", googleRegisterData.email);
				params.put("google_access_token", googleRegisterData.accessToken);

				params.put("device_token", MyApplication.getInstance().getDeviceToken());
				params.put("device_name", MyApplication.getInstance().deviceName());
				params.put("os_version", MyApplication.getInstance().osVersion());
				params.put("country", MyApplication.getInstance().country());
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("latitude", "" + Data.loginLatitude);
				params.put("longitude", "" + Data.loginLongitude);
				params.put("client_id", Config.getAutosClientId());
				params.put("otp", otp);
				params.put("reg_wallet_type", String.valueOf(linkedWallet));

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}

				Log.i("params", "" + params);

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().verifyOtp(params, new Callback<LoginResponse>() {
					@Override
					public void success(LoginResponse loginResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.v(TAG, "verifyOtp response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);

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
										new JSONParser().parseAccessTokenLoginData(activity, responseStr,
												loginResponse, LoginVia.GOOGLE_OTP,
												new LatLng(Data.loginLatitude, Data.loginLongitude));
										loginDataFetched = true;
										MyApplication.getInstance().getDatabase().insertEmail(googleRegisterData.email);
										MyApplication.getInstance().getDatabase().close();
										firebaseEventWalletAtSignup();
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

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "verifyOtp errror="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
		if (MyApplication.getInstance().isOnline()) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			params.put("phone_no", phoneNo);
			Log.i("phone_no", ">" + phoneNo);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().sendOtpViaCall(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "sendOtpViaCall response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							int flag = jObj.getInt("flag");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", errorMessage);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
							DialogPopup.dismissLoadingDialog();
						} else {
							String message = jObj.getString("message");
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							}
							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "sendOtpViaCall error="+error.toString());
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if(hasFocus && loginDataFetched){
			loginDataFetched = false;

			MyApplication.getInstance().getAppSwitcher().switchApp(OTPConfirmScreen.this,
					Prefs.with(OTPConfirmScreen.this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()),
					Data.splashIntentUri, new LatLng(Data.loginLatitude, Data.loginLongitude), false);
//			Intent intent = new Intent(OTPConfirmScreen.this, HomeActivity.class);
//			intent.setData(Data.splashIntentUri);
//			startActivity(intent);
//			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//			ActivityCompat.finishAffinity(this);
		}
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
	}


	public void performBackPressed(){
        Bundle bundle = new Bundle();
        MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.OTP_SCREEN+"_"+ FirebaseEvents.BACK, bundle);
		if(llLoginNew.getVisibility() == View.VISIBLE){
			Animation animation4 = AnimationUtils.loadAnimation(this, R.anim.left_out);
			animation4.setFillAfter(false);
			animation4.setDuration(duration);
			llLoginNew.startAnimation(animation4);

			Animation animation5 = AnimationUtils.loadAnimation(this, R.anim.left_in);
			animation5.setFillAfter(true);
			animation5.setDuration(duration);
			rlOTPContainer.startAnimation(animation5);

			rlOTPContainer.setVisibility(View.VISIBLE);
			llLoginNew.setVisibility(View.GONE);
		} else {
			if (intentFromRegister) {
				Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
				intent.putExtra(KEY_SPLASH_STATE, SplashNewActivity.State.SIGNUP.getOrdinal());
				intent.putExtra(KEY_BACK_FROM_OTP, true);
				startActivity(intent);
			} else {
				Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
				intent.putExtra(KEY_SPLASH_STATE, SplashNewActivity.State.LOGIN.getOrdinal());
				intent.putExtra(KEY_BACK_FROM_OTP, true);
				startActivity(intent);
			}
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
	}

	@Override
	protected void onDestroy() {
		try{
			if(handler != null){
				handler.removeCallbacks(runnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}



	private void retrieveOTPFromSMS(Intent intent){
		try {
			String otp = "";
			if(intent.hasExtra("message")){
				String message = intent.getStringExtra("message");
				otp = Utils.retrieveOTPFromSMS(message);
			} else if(intent.hasExtra(KEY_OTP)){
				otp = intent.getStringExtra(KEY_OTP);
			}

			if(Utils.checkIfOnlyDigits(otp)){
				if(!"".equalsIgnoreCase(otp)) {
					//editTextOTP.setText(otp);
					//editTextOTP.setSelection(editTextOTP.getText().length());
					txtPinEntry.setText(otp);
					txtPinEntry.setSelection(txtPinEntry.getText().length());
					//buttonVerify.performClick();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void generateOTP(final String accessToken, final int linkedWallet) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(KEY_ACCESS_TOKEN, accessToken);
				params.put(KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(KEY_IS_ACCESS_TOKEN_NEW, "1");

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, linkedWallet+"walletRequestOtp response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(OTPConfirmScreen.this, message);
							} else if (ApiResponseFlags.PAYTM_INVALID_EMAIL.getOrdinal() == flag) {
								DialogPopup.alertPopup(OTPConfirmScreen.this, "", message);
							} else {
								DialogPopup.alertPopup(OTPConfirmScreen.this, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, linkedWallet+"walletRequestOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
					}
				};

				new HomeUtil().putDefaultParams(params);
				if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
					RestClient.getApiService().paytmRequestOtp(params, callback);
				}
				else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
					RestClient.getApiService().mobikwikRequestOtp(params, callback);
				}
				else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
					RestClient.getApiService().freeChargeRequestOtp(params, callback);
				}
			} else{
				DialogPopup.dialogNoInternet(OTPConfirmScreen.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								generateOTP(accessToken, linkedWallet);
							}

							@Override
							public void neutralClick(View view) {

							}

							@Override
							public void negativeClick(View view) {

							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendOTP(final String otp) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
				params.put(Constants.KEY_OTP, otp);

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "loginWithOtp response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "loginWithOtp error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
					}
				};
				new HomeUtil().putDefaultParams(params);
				if(linkedWallet == PaymentOption.PAYTM.getOrdinal()) {
					RestClient.getApiService().paytmLoginWithOtp(params, callback);
				} else if(linkedWallet == PaymentOption.MOBIKWIK.getOrdinal()){
					RestClient.getApiService().mobikwikLoginWithOtp(params, callback);
				} else if(linkedWallet == PaymentOption.FREECHARGE.getOrdinal()) {
					RestClient.getApiService().freeChargeLoginWithOtp(params, callback);
				}
			} else{
				DialogPopup.dialogNoInternet(OTPConfirmScreen.this, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								sendOTP(otp);
							}

							@Override
							public void neutralClick(View view) {

							}

							@Override
							public void negativeClick(View view) {

							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	private void firebaseEventWalletAtSignup(){
		if(linkedWallet == LinkedWalletStatus.PAYTM_WALLET_ADDED.getOrdinal()){
			MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.SIGN_UP_PAGE+"_"+FirebaseEvents.PAYTM, new Bundle());
		} else if(linkedWallet == LinkedWalletStatus.MOBIKWIK_WALLET_ADDED.getOrdinal()){
			MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.SIGN_UP_PAGE+"_"+FirebaseEvents.MOBIKWIK, new Bundle());
		} else if(linkedWallet == LinkedWalletStatus.FREECHARGE_WALLET_ADDED.getOrdinal()){
			MyApplication.getInstance().logEvent(FirebaseEvents.FB_ACQUISITION+"_"+FirebaseEvents.SIGN_UP_PAGE+"_"+FirebaseEvents.FREECHARGE, new Bundle());
		}
	}

	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, String password, final boolean isPhoneNumber, boolean showDialog) {
		if (MyApplication.getInstance().isOnline()) {
			//resetFlags();
			if(showDialog){
				DialogPopup.showLoadingDialog(OTPConfirmScreen.this, "Loading...");
			}
			//DialogPopup.showLoadingDialog(activity, "Trying to verify through missed call...");

			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

			if (isPhoneNumber) {
				params.put("phone_no", emailId);
			} else {
				params.put("email", emailId);
			}
			params.put("password", password);
			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "=" + params);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().loginUsingEmailOrPhoneNo(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "loginUsingEmailOrPhoneNo response = " + responseStr);
					try {
						JSONObject jObj = new JSONObject(responseStr);

						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								showErrorOnMissedCallBack();
								/*if (isPhoneNumber) {
									enteredEmail = jObj.getString("user_email");
								} else {
									enteredEmail = emailId;
								}*/
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Login Page", "Login");
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.EMAIL,
											new LatLng(Data.loginLatitude, Data.loginLongitude));
									MyApplication.getInstance().getDatabase().insertEmail(emailId);
									loginDataFetched = true;
									DialogPopup.showLoadingDialog(activity, "Loading...");
									DialogPopup.dismissLoadingDialog();
								}
							} else {
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								showErrorOnMissedCallBack();
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

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingEmailOrPhoneNo error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	public void sendFacebookLoginValues(final Activity activity) {
		if (MyApplication.getInstance().isOnline()) {
			//DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();


			params.put("user_fb_id", Data.facebookUserData.fbId);
			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			params.put("fb_access_token", Data.facebookUserData.accessToken);
			params.put("fb_mail", Data.facebookUserData.userEmail);
			params.put("username", Data.facebookUserData.userName);

			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "" + params);

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().loginUsingFacebook(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "loginUsingFacebook response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
							if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								linkedWallet = jObj.optInt("reg_wallet_type");
								showErrorOnMissedCallBack();
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									FlurryEventLogger.eventGA(REVENUE + SLASH + ACTIVATION + SLASH + RETENTION, "Login Page", "Login with facebook");
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.FACEBOOK,
											new LatLng(Data.loginLatitude, Data.loginLongitude));
									loginDataFetched = true;

									MyApplication.getInstance().getDatabase().insertEmail(Data.facebookUserData.userEmail);
									DialogPopup.showLoadingDialog(activity, "Loading...");
									DialogPopup.dismissLoadingDialog();
								}
							} else {
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								showErrorOnMissedCallBack();
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

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingFacebook error=" + error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	public void sendGoogleLoginValues(final Activity activity) {
		if (MyApplication.getInstance().isOnline()) {
			//DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<>();

			Data.loginLatitude = MyApplication.getInstance().getLocationFetcher().getLatitude();
			Data.loginLongitude = MyApplication.getInstance().getLocationFetcher().getLongitude();

			params.put("google_access_token", Data.googleSignInAccount.getIdToken());

			params.put("device_token", MyApplication.getInstance().getDeviceToken());
			params.put("device_name", MyApplication.getInstance().deviceName());
			params.put("os_version", MyApplication.getInstance().osVersion());
			params.put("country", MyApplication.getInstance().country());
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("client_id", Config.getAutosClientId());

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}
			params.put(KEY_SOURCE, JSONParser.getAppSource(this));
			String links = MyApplication.getInstance().getDatabase2().getSavedLinksUpToTime(Data.BRANCH_LINK_TIME_DIFF);
			if(links != null){
				if(!"[]".equalsIgnoreCase(links)) {
					params.put(KEY_BRANCH_REFERRING_LINKS, links);
				}
			}
			params.put(KEY_SP_LAST_OPENED_CLIENT_ID, Prefs.with(activity).getString(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()));

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(this, params);

			Log.i("params", "" + params);


			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().loginUsingGoogle(params, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i(TAG, "loginUsingGoogle response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						int flag = jObj.getInt("flag");

						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
							if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
								String error = jObj.getString("error");
								showErrorOnMissedCallBack();
							}
							else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
								String error = jObj.getString("error");
//								DialogPopup.alertPopup(activity, "", error);
								showErrorOnMissedCallBack();
							}
							else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
								linkedWallet = jObj.optInt("reg_wallet_type");
								showErrorOnMissedCallBack();
							}
							else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
								if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
									new JSONParser().parseAccessTokenLoginData(activity, responseStr,
											loginResponse, LoginVia.GOOGLE,
											new LatLng(Data.loginLatitude, Data.loginLongitude));
									FlurryEventLogger.eventGA(REVENUE+SLASH+ACTIVATION+SLASH+RETENTION, "Login Page", "Login with Google");
									loginDataFetched = true;

									MyApplication.getInstance().getDatabase().insertEmail(Data.googleSignInAccount.getEmail());
									DialogPopup.showLoadingDialog(activity, "Loading...");
									DialogPopup.dismissLoadingDialog();
								}
							}
							else{
//								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								showErrorOnMissedCallBack();
							}
							DialogPopup.dismissLoadingDialog();
						}
						else{
							DialogPopup.dismissLoadingDialog();
						}

					}  catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "loginUsingGoogle error="+error.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	private LocationUpdate locationUpdate = new LocationUpdate() {
		@Override
		public void onLocationChanged(Location location) {
			Data.loginLatitude = location.getLatitude();
			Data.loginLongitude = location.getLongitude();
		}
	};

	private class CustomTextWatcher implements TextWatcher{

		EditText previousEt, currentEt, nextEt;

		public CustomTextWatcher(EditText previousET, EditText currentEt, EditText nextEt) {
			this.previousEt = previousET;
			this.currentEt = currentEt;
			this.nextEt = nextEt;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			Log.v("customTextWatcher before", "---> "+s.length());
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.v("customTextWatcher onText", "---> "+s.length());
		}

		@Override
		public void afterTextChanged(Editable s) {
			Log.v("customTextWatcher afterText", "---> "+s.length());
			Log.v("currentEt length afterText", "---> "+currentEt.getText().length());
			Log.v("s length afterText", "---> "+s.length());
			if(s.length() == 2 && nextEt != null){
				currentEt.setText(s.subSequence(0, s.length()-1));
				nextEt.setText(s.subSequence(s.length()-1, s.length()));
			}
			if(s.length() > 0 && nextEt != null) {
				nextEt.requestFocus();
				nextEt.setSelection(nextEt.getText().length());
				nextEt.setEnabled(true);
			}
		}
	}

	private class CustomBackKeyListener implements View.OnKeyListener{

		EditText previousEt, nextEt;

		public CustomBackKeyListener(EditText previousEt, EditText nextEt) {
			this.previousEt = previousEt;
			this.nextEt = nextEt;
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_DEL){
				if(((EditText)v).getText().length() == 0 && previousEt != null) {
					previousEt.setEnabled(true);
					previousEt.requestFocus();
					previousEt.setSelection(previousEt.getText().length());
				}
			} /*else if(((EditText)v).getText().length() == 1 && nextEt != null){
				nextEt.requestFocus();
			} else if(((EditText)v).getText().length() == 2 && nextEt != null){
				nextEt.setText(((EditText)v).getText().subSequence(((EditText)v).getText().length()-1, ((EditText)v).getText().length()));
				((EditText)v).setText(((EditText)v).getText().subSequence(0, ((EditText)v).getText().length()-1));
				nextEt.requestFocus();
			}*/
			return false;
		}
	}

}





