package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.GoogleRegisterData;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.GoogleSigninActivity;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


public class RegisterScreen extends BaseActivity implements LocationUpdate, FlurryEventNames, Constants {

    private final String TAG = RegisterScreen.class.getSimpleName();

    RelativeLayout topBar;
    TextView textViewTitle;
    ImageView imageViewBack;

	LinearLayout linearLayoutSocialSignup;
    ImageView imageViewFacebookSignup, imageViewGoogleSignup;
	RelativeLayout orDivRl;
    TextView orText;

    EditText editTextUserName, editTextEmail, editTextPhone, editTextPassword, editTextReferralCode;
    LinearLayout linearLayoutPhoneEditText;
    TextView textViewPolicy;
    Button buttonEmailSignup;

    TextView textViewScroll;
    ScrollView scrollView;
    LinearLayout linearLayoutMain;

    LinearLayout relative;

    String name = "", referralCode = "", emailId = "", phoneNo = "", password = "", accessToken = "";

	private static final int GOOGLE_SIGNIN_REQ_CODE = 1124;

    public static RegisterationType registerationType = RegisterationType.EMAIL;
    boolean sendToOtpScreen = false;

    public static JSONObject multipleCaseJSON;

    public void resetFlags() {
        sendToOtpScreen = false;
    }

    // *****************************Used for flurry work***************//
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());
        FlurryAgent.onEvent("Register started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }


    CallbackManager callbackManager;
    FacebookLoginHelper facebookLoginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(RegisterScreen.this, relative, 1134, 720, false);

        topBar = (RelativeLayout) findViewById(R.id.topBar);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

		linearLayoutSocialSignup = (LinearLayout) findViewById(R.id.linearLayoutSocialSignup);
		imageViewFacebookSignup = (ImageView) findViewById(R.id.imageViewFacebookLogin);
		imageViewGoogleSignup = (ImageView) findViewById(R.id.imageViewGoogleLogin);
		orDivRl = (RelativeLayout) findViewById(R.id.orDivRl);
        orText = (TextView) findViewById(R.id.orText);
        orText.setTypeface(Fonts.latoRegular(getApplicationContext()));

        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserName.setTypeface(Fonts.latoRegular(this));
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.setTypeface(Fonts.latoRegular(this));
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPhone.setTypeface(Fonts.latoRegular(this));
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword.setTypeface(Fonts.latoRegular(this));
        editTextReferralCode = (EditText) findViewById(R.id.editTextReferralCode);
        editTextReferralCode.setTypeface(Fonts.latoRegular(this));

        linearLayoutPhoneEditText = (LinearLayout) findViewById(R.id.linearLayoutPhoneEditText);


        ((TextView)findViewById(R.id.textViewPhone91)).setTypeface(Fonts.latoRegular(this));

        textViewPolicy = (TextView) findViewById(R.id.textViewPolicy);
        textViewPolicy.setTypeface(Fonts.latoLight(this), Typeface.BOLD);

        buttonEmailSignup = (Button) findViewById(R.id.buttonEmailSignup);
        buttonEmailSignup.setTypeface(Fonts.latoRegular(this));

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textViewScroll = (TextView) findViewById(R.id.textViewScroll);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);


        linearLayoutMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);
            }
        });
        topBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);
            }
        });


		imageViewFacebookSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(AppStatus.getInstance(RegisterScreen.this).isOnline(RegisterScreen.this)) {
                    FlurryEventLogger.event(SIGNUP_VIA_FACEBOOK);
                    Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);
                    facebookLoginHelper.openFacebookSession();
                } else{
                    DialogPopup.alertPopup(RegisterScreen.this, "", Data.CHECK_INTERNET_MSG);
                }
            }
        });

		imageViewGoogleSignup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                if(AppStatus.getInstance(RegisterScreen.this).isOnline(RegisterScreen.this)) {
                    FlurryEventLogger.event(SIGNUP_VIA_GOOGLE);
                    Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);
                    startActivityForResult(new Intent(RegisterScreen.this, GoogleSigninActivity.class), GOOGLE_SIGNIN_REQ_CODE);
                } else{
                    DialogPopup.alertPopup(RegisterScreen.this, "", Data.CHECK_INTERNET_MSG);
                }
			}
		});



        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);
                performBackPressed();
            }
        });

        editTextUserName.setLongClickable(false);

        editTextUserName.setOnFocusChangeListener(onFocusChangeListener);
        editTextEmail.setOnFocusChangeListener(onFocusChangeListener);
        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, linearLayoutPhoneEditText.getTop());
                        }
                    }, 200);
                } else {
                    try {
                        ((EditText)v).setError(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        editTextPassword.setOnFocusChangeListener(onFocusChangeListener);
        editTextReferralCode.setOnFocusChangeListener(onFocusChangeListener);

        editTextUserName.setOnClickListener(onClickListener);
        editTextEmail.setOnClickListener(onClickListener);
        editTextPhone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, linearLayoutPhoneEditText.getTop());
                    }
                }, 200);
            }
        });
        editTextPassword.setOnClickListener(onClickListener);
        editTextReferralCode.setOnClickListener(onClickListener);

        editTextUserName.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextEmail.requestFocus();
                return true;
            }
        });

        editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextPhone.requestFocus();
                return true;
            }
        });

        editTextPhone.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextPassword.requestFocus();
                return true;
            }
        });

        editTextPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextReferralCode.requestFocus();
                return true;
            }
        });




        buttonEmailSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);

                String name = editTextUserName.getText().toString().trim();
                if (name.length() > 0) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                }
                String referralCode = editTextReferralCode.getText().toString().trim();
                String emailId = editTextEmail.getText().toString().trim();
                boolean noFbEmail = false;

                if (RegisterationType.FACEBOOK == registerationType && emailId.equalsIgnoreCase("")) {
                    emailId = "n@n.c";
                    noFbEmail = true;
                }


                String phoneNo = editTextPhone.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if ("".equalsIgnoreCase(name)) {
                    editTextUserName.requestFocus();
                    editTextUserName.setError("Please enter a name");
                } else if (!Utils.hasAlphabets(name)) {
                    editTextUserName.requestFocus();
                    editTextUserName.setError("Please enter at least one alphabet");
                } else {
                    if ("".equalsIgnoreCase(emailId)) {
                        editTextEmail.requestFocus();
                        editTextEmail.setError("Please enter email id");
                    } else {
                        if ("".equalsIgnoreCase(phoneNo)) {
                            editTextPhone.requestFocus();
                            editTextPhone.setError("Please enter phone number");
                        } else {
                            phoneNo = Utils.retrievePhoneNumberTenChars(phoneNo);
                            if (!Utils.validPhoneNumber(phoneNo)) {
                                editTextPhone.requestFocus();
                                editTextPhone.setError("Please enter valid phone number");
                            } else {
                                phoneNo = "+91" + phoneNo;
                                if ("".equalsIgnoreCase(password)) {
                                    editTextPassword.requestFocus();
                                    editTextPassword.setError("Please enter password");
                                } else {
                                    if (Utils.isEmailValid(emailId)) {
                                        if (password.length() >= 6) {

                                            if (RegisterationType.FACEBOOK == registerationType) {
                                                if (noFbEmail) {
                                                    emailId = "";
                                                }
                                                sendFacebookSignupValues(RegisterScreen.this, referralCode, phoneNo, password);
                                            }
											else if(RegisterationType.GOOGLE == registerationType){
												sendGoogleSignupValues(RegisterScreen.this, referralCode, phoneNo, password);
											}
											else {
                                                sendSignupValues(RegisterScreen.this, name, referralCode, emailId, phoneNo, password);
                                            }
                                            FlurryEventLogger.event(SIGNUP_FINAL);
                                        } else {
                                            editTextPassword.requestFocus();
                                            editTextPassword.setError("Password must be of atleast six characters");
                                        }

                                    } else {
                                        editTextEmail.requestFocus();
                                        editTextEmail.setError("Please enter valid email id");
                                    }

                                }
                            }
                        }
                    }
                }

            }
        });



        editTextReferralCode.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonEmailSignup.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });

        callbackManager = CallbackManager.Factory.create();

        facebookLoginHelper = new FacebookLoginHelper(this, callbackManager, new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone(FacebookUserData facebookUserData) {
                Data.facebookUserData = facebookUserData;
                fillSocialAccountInfo(RegisterationType.FACEBOOK);
                FlurryEventLogger.registerViaFBClicked(Data.facebookUserData.fbId);
            }

            @Override
            public void facebookLoginError(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });





        SpannableString sstr = new SpannableString("Terms and Conditions");
        final ForegroundColorSpan clrs = new ForegroundColorSpan(getResources().getColor(R.color.yellow_orange));
        sstr.setSpan(clrs, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewPolicy.setText("");
        textViewPolicy.append("By signing up, you agree to \n");
        textViewPolicy.append(sstr);
        textViewPolicy.append(" of Jugnoo");

        textViewPolicy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(RegisterScreen.this, editTextUserName);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jugnoo.in/terms_of_use"));
                startActivity(browserIntent);
            }
        });



        fillSocialAccountInfo(RegisterScreen.registerationType);


        try {
            if (getIntent().hasExtra("back_from_otp")) {
                if (RegisterationType.FACEBOOK == registerationType) {
                    editTextReferralCode.setText(OTPConfirmScreen.facebookRegisterData.referralCode);
                    editTextPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.facebookRegisterData.phoneNo));
                    editTextPassword.setText(OTPConfirmScreen.facebookRegisterData.password);
                }
				else if(RegisterationType.GOOGLE == registerationType){
					editTextReferralCode.setText(OTPConfirmScreen.googleRegisterData.referralCode);
					editTextPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.googleRegisterData.phoneNo));
					editTextPassword.setText(OTPConfirmScreen.googleRegisterData.password);
				}
				else {
                    editTextUserName.setText(OTPConfirmScreen.emailRegisterData.name);
                    editTextEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
                    editTextReferralCode.setText(OTPConfirmScreen.emailRegisterData.referralCode);
                    editTextPhone.setText(Utils.retrievePhoneNumberTenChars(OTPConfirmScreen.emailRegisterData.phoneNo));
                    editTextPassword.setText(OTPConfirmScreen.emailRegisterData.password);
                }

                editTextUserName.setSelection(editTextUserName.getText().length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		try{
			if(getIntent().hasExtra("referral_code")){
				String referralCode = getIntent().getStringExtra("referral_code");
				editTextReferralCode.setText(referralCode);
			}
		} catch(Exception e){
			e.printStackTrace();
		}


//        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
//            @Override
//            public void keyboardOpened() {
//
//            }
//
//            @Override
//            public void keyBoardClosed() {
//
//            }
//        }));


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new DeviceTokenGenerator().generateDeviceToken(this, new IDeviceTokenReceiver() {

            @Override
            public void deviceTokenReceived(final String regId) {
                Data.deviceToken = regId;
                Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
            }
        });


//		editTextUserName.setText("Support Meals 31");
//		editTextEmail.setText("support+m31@jugnoo.in");
//		editTextPhone.setText("+919000000231");
//		editTextPassword.setText("jugnoopass");


        if (Data.previousAccountInfoList == null) {
            Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        }
        Data.previousAccountInfoList.clear();


        new ReadSMSAsync().execute();
    }


    private void fillSocialAccountInfo(RegisterationType registerationType){
        try {
            RegisterScreen.registerationType = registerationType;
            if (RegisterationType.FACEBOOK == RegisterScreen.registerationType) {
                editTextUserName.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
                editTextEmail.setText(Data.facebookUserData.userEmail);

                if(Data.facebookUserData.firstName != null && !Data.facebookUserData.firstName.equalsIgnoreCase("")){
                    editTextUserName.setEnabled(false);
                } else{
                    editTextUserName.setEnabled(true);
                }
                if(Data.facebookUserData.userEmail != null && !Data.facebookUserData.userEmail.equalsIgnoreCase("")) {
                    editTextEmail.setEnabled(false);
                } else {

                    editTextEmail.setEnabled(true);
                }
            }
            else if(RegisterationType.GOOGLE == RegisterScreen.registerationType){
                editTextUserName.setText(Data.googleSignInAccount.getDisplayName());
                editTextEmail.setText(Data.googleSignInAccount.getEmail());

                if(Data.googleSignInAccount.getDisplayName() != null && !Data.googleSignInAccount.getDisplayName().equalsIgnoreCase("")){
                    editTextUserName.setEnabled(false);
                } else{
                    editTextUserName.setEnabled(true);
                }
                if(Data.googleSignInAccount.getEmail() != null && !Data.googleSignInAccount.getEmail().equalsIgnoreCase("")) {
                    editTextEmail.setEnabled(false);
                } else {

                    editTextEmail.setEnabled(true);
                }
            }
            if(RegisterationType.EMAIL == RegisterScreen.registerationType){
                linearLayoutSocialSignup.setVisibility(View.VISIBLE);
                orDivRl.setVisibility(View.VISIBLE);
            }
            else{
                linearLayoutSocialSignup.setVisibility(View.GONE);
                orDivRl.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ReadSMSAsync extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String referralCode = getSmsFindReferralCode(1*60*60*1000);
            return referralCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (editTextReferralCode.getText().toString().equalsIgnoreCase("")) {
                editTextReferralCode.setText(s);
            }
        }
    }

	private String getSmsFindReferralCode(long diff) {
        String referralCode = "";
        try {
            Uri uri = Uri.parse("content://sms/inbox");
            long now = System.currentTimeMillis();
            long last1 = now - diff;    //in millis
            String[] selectionArgs = new String[]{Long.toString(last1)};
            String selection = "date" + ">?";
            Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
//                    String number = cursor.getString(cursor.getColumnIndexOrThrow("address"));
//                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//                    Date smsDayTime = new Date(Long.valueOf(date));
//                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
//                    String typeOfSMS = null;
//                    switch (Integer.parseInt(type)) {
//                        case 1:
//                            typeOfSMS = "INBOX";
//                            break;
//
//                        case 2:
//                            typeOfSMS = "SENT";
//                            break;
//
//                        case 3:
//                            typeOfSMS = "DRAFT";
//                            break;
//                    }
                    Log.e("body", "="+body);
                    try {
                        if(body.contains("Jugnoo")){
                            String[] codeArr = body.split("code ");
                            String[] spaceArr = codeArr[1].split(" ");
                            String rCode = spaceArr[0];
                            if(!"".equalsIgnoreCase(rCode)){
                                referralCode = rCode;
                                break;
                            }
                        }
                    } catch (Exception e) {}
//                    stringBuffer.append("\nPhone Number:--- " + number + " \nMessage Type:--- "
//                            + typeOfSMS + " \nMessage Date:--- " + smsDayTime
//                            + " \nMessage Body:--- " + body);
//                    stringBuffer.append("\n----------------------------------");
                    cursor.moveToNext();
                }
//                DialogPopup.alertPopup(this, "", stringBuffer.toString());
            }
            if (cursor != null){
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return referralCode;
	}




    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {
            if (hasFocus) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, v.getTop());
                    }
                }, 200);
            } else {
                try {
                    ((EditText)v).setError(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, v.getTop());
                }
            }, 200);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
			if(RESULT_OK == resultCode){
				if(requestCode == GOOGLE_SIGNIN_REQ_CODE){
					Data.googleSignInAccount = data.getParcelableExtra(KEY_GOOGLE_PARCEL);
                    fillSocialAccountInfo(RegisterationType.GOOGLE);
				}
				else{
					callbackManager.onActivityResult(requestCode, resultCode, data);
				}
			}
			else{
				callbackManager.onActivityResult(requestCode, resultCode, data);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    protected void onResume() {
        super.onResume();

        if (Data.locationFetcher == null) {
            Data.locationFetcher = new LocationFetcher(RegisterScreen.this, 1000, 1);
        }
        HomeActivity.checkForAccessTokenChange(this);

    }

    @Override
    protected void onPause() {
        try {
            if (Data.locationFetcher != null) {
                Data.locationFetcher.destroy();
                Data.locationFetcher = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPause();


    }


    /**
     * ASync for register from server
     */
    public void sendSignupValues(final Activity activity, final String name, final String referralCode, final String emailId, final String phoneNo, final String password) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            if (Data.locationFetcher != null) {
                Data.loginLatitude = Data.locationFetcher.getLatitude();
                Data.loginLongitude = Data.locationFetcher.getLongitude();
            }

            params.put("user_name", name);
            params.put("phone_no", phoneNo);
            params.put("email", emailId);
            params.put("password", password);
            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);

            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);

            params.put("client_id", Config.getClientId());
            params.put("referral_code", referralCode);

            params.put("device_token", Data.getDeviceToken());
            params.put("unique_device_id", Data.uniqueDeviceId);



			if(Utils.isDeviceRooted()){
				params.put("device_rooted", "1");
			}
			else{
				params.put("device_rooted", "0");
			}


            Log.i("register_using_email params", params.toString());


            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/register_using_email", params,
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
                        Log.i("Server response register_using_email", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                        RegisterScreen.this.name = name;
                                        RegisterScreen.this.emailId = emailId;
                                        RegisterScreen.this.phoneNo = jObj.getString("phone_no");
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = jObj.getString("access_token");
										Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
                                        Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
                                        sendToOtpScreen = true;
                                    } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
                                        RegisterScreen.this.name = name;
                                        RegisterScreen.this.emailId = emailId;
                                        RegisterScreen.this.phoneNo = phoneNo;
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = "";
                                        parseDataSendToMultipleAccountsScreen(activity, jObj);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                }
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


    /**
     * ASync for login from server
     */
    public void sendFacebookSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            if (Data.locationFetcher != null) {
                Data.loginLatitude = Data.locationFetcher.getLatitude();
                Data.loginLongitude = Data.locationFetcher.getLongitude();
            }

            params.put("user_fb_id", Data.facebookUserData.fbId);
            params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
            params.put("fb_access_token", Data.facebookUserData.accessToken);
            params.put("fb_mail", Data.facebookUserData.userEmail);
            params.put("username", Data.facebookUserData.userName);

            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put("referral_code", referralCode);

            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);
            params.put("device_token", Data.getDeviceToken());
            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("client_id", Config.getClientId());


			if(Utils.isDeviceRooted()){
				params.put("device_rooted", "1");
			}
			else{
				params.put("device_rooted", "0");
			}


            Log.e("register_using_facebook params", params.toString());


            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/register_using_facebook", params,
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
                        Log.i("Server response register_using_facebook", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                        RegisterScreen.this.phoneNo = jObj.getString("phone_no");
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = jObj.getString("access_token");
										Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
                                        Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
                                        sendToOtpScreen = true;
                                    } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
                                        RegisterScreen.this.phoneNo = phoneNo;
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = "";
                                        parseDataSendToMultipleAccountsScreen(activity, jObj);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                }
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


	/**
	 * ASync for login from server
	 */
	public void sendGoogleSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");

			RequestParams params = new RequestParams();

			if (Data.locationFetcher != null) {
				Data.loginLatitude = Data.locationFetcher.getLatitude();
				Data.loginLongitude = Data.locationFetcher.getLongitude();
			}

            params.put("google_access_token", Data.googleSignInAccount.getIdToken());

			params.put("phone_no", phoneNo);
			params.put("password", password);
			params.put("referral_code", referralCode);

			params.put("latitude", "" + Data.loginLatitude);
			params.put("longitude", "" + Data.loginLongitude);
			params.put("device_token", Data.getDeviceToken());
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("client_id", Config.getClientId());


			if(Utils.isDeviceRooted()){
				params.put("device_rooted", "1");
			}
			else{
				params.put("device_rooted", "0");
			}


			Log.e("register_using_facebook params", params.toString());


			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/register_using_google", params,
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
							Log.i("Server response register_using_google", "response = " + response);

							try {
								jObj = new JSONObject(response);

								if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
										int flag = jObj.getInt("flag");
										if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
											String error = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", error);
										} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
											String error = jObj.getString("error");
                                            DialogPopup.alertPopupWithListener(activity, "", error, onClickListenerAlreadyRegistered);
										} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
											RegisterScreen.this.phoneNo = jObj.getString("phone_no");
											RegisterScreen.this.password = password;
											RegisterScreen.this.referralCode = referralCode;
											RegisterScreen.this.accessToken = jObj.getString("access_token");
											Data.knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
                                            Data.otpViaCallEnabled = jObj.optInt(KEY_OTP_VIA_CALL_ENABLED, 1);
											sendToOtpScreen = true;
										} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
											RegisterScreen.this.phoneNo = phoneNo;
											RegisterScreen.this.password = password;
											RegisterScreen.this.referralCode = referralCode;
											RegisterScreen.this.accessToken = "";
											parseDataSendToMultipleAccountsScreen(activity, jObj);
										} else {
											DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
										}
										DialogPopup.dismissLoadingDialog();
									}
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


	private void generateOTPRegisterData(){
		if(RegisterationType.FACEBOOK == RegisterScreen.registerationType){
			OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
					Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
					Data.facebookUserData.accessToken,
					Data.facebookUserData.userEmail,
					Data.facebookUserData.userName,
					phoneNo, password, referralCode, accessToken);
		}
		else if(RegisterationType.GOOGLE == RegisterScreen.registerationType){
			OTPConfirmScreen.googleRegisterData = new GoogleRegisterData(Data.googleSignInAccount.getId(),
					Data.googleSignInAccount.getDisplayName(),
					Data.googleSignInAccount.getEmail(),
                    "",
					phoneNo, password, referralCode, accessToken);
		}
		else{
			OTPConfirmScreen.intentFromRegister = true;
			OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode, accessToken);
		}
	}

    /**
     * Send intent to otp screen by making required data objects
     */
    public void sendIntentToOtpScreen() {
		OTPConfirmScreen.intentFromRegister = true;
		generateOTPRegisterData();
		Intent intent = new Intent(RegisterScreen.this, OTPConfirmScreen.class);
		intent.putExtra("show_timer", 1);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    public void parseDataSendToMultipleAccountsScreen(Activity activity, JSONObject jObj) {
		generateOTPRegisterData();
        RegisterScreen.multipleCaseJSON = jObj;
        if (Data.previousAccountInfoList == null) {
            Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        }
        Data.previousAccountInfoList.clear();
        Data.previousAccountInfoList.addAll(JSONParser.parsePreviousAccounts(jObj));
        startActivity(new Intent(activity, MultipleAccountsActivity.class));
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && sendToOtpScreen) {
            sendIntentToOtpScreen();
        }

    }


    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    public void performBackPressed() {
        FacebookLoginHelper.logoutFacebook();
        Intent intent = new Intent(RegisterScreen.this, SplashNewActivity.class);
        intent.putExtra("no_anim", "yes");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    private OnClickListener onClickListenerAlreadyRegistered = new OnClickListener() {
        @Override
        public void onClick(View v) {
            goToLoginScreen();
        }
    };
    private void goToLoginScreen(){
        Intent intent = new Intent(this, SplashLogin.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
    }


    @Override
    public void onLocationChanged(Location location, int priority) {
        Data.loginLatitude = location.getLatitude();
        Data.loginLongitude = location.getLongitude();
    }


	public enum RegisterationType{
		EMAIL, FACEBOOK, GOOGLE
	}

}
