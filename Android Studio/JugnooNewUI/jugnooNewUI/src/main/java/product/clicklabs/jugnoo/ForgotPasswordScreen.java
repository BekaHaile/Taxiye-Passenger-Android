package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ForgotPasswordScreen extends BaseActivity  {

    TextView textViewTitle;
    ImageView imageViewBack;

    TextView textViewForgotPasswordHelp;
    EditText editTextEmail;
    Button buttonSendEmail;

    LinearLayout relative;

    static String emailAlready = "";

    // *****************************Used for flurry work***************//
    @Override
    protected void onStart() {
        super.onStart();
//        FlurryAgent.init(this, Config.getFlurryKey());
//        FlurryAgent.onStartSession(this, Config.getFlurryKey());
    }

    @Override
    protected void onStop() {
        super.onStop();
//        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(ForgotPasswordScreen.this, relative, 1134, 720, false);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewForgotPasswordHelp = (TextView) findViewById(R.id.textViewForgotPasswordHelp);
        textViewForgotPasswordHelp.setTypeface(Fonts.mavenLight(this));

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.setTypeface(Fonts.mavenMedium(this));

        buttonSendEmail = (Button) findViewById(R.id.buttonSendEmail);
        buttonSendEmail.setTypeface(Fonts.mavenRegular(this));

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(getLoginIntent(emailAlready));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();
            }
        });


        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextEmail.setError(null);
            }
        });


        buttonSendEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();

                if ("".equalsIgnoreCase(email)) {
                    editTextEmail.requestFocus();
                    editTextEmail.setError(getString(R.string.please_enter_email_or_phone));
                } else {
                    boolean onlyDigits = Utils.checkIfOnlyDigits(email);
                    if (onlyDigits) {
                        email = Utils.retrievePhoneNumberTenChars(email);
                        if (!Utils.validPhoneNumber(email)) {
                            editTextEmail.requestFocus();
                            editTextEmail.setError(getString(R.string.please_enter_valid_phone));
                        } else {
                            email = "+91" + email;
                            forgotPasswordAsync(ForgotPasswordScreen.this, email, true);
                        }
                    } else {
                        if (isEmailValid(email)) {
                            forgotPasswordAsync(ForgotPasswordScreen.this, email, false);
                        } else {
                            editTextEmail.requestFocus();
                            editTextEmail.setError(getString(R.string.please_enter_valid_email_id));
                        }
                    }

                }

            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Utils.checkIfOnlyDigits(s.toString())) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(10);
                    editTextEmail.setFilters(fArray);
                } else {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(1000);
                    editTextEmail.setFilters(fArray);
                }
            }
        });


        editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonSendEmail.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });

        editTextEmail.setText(emailAlready);
        editTextEmail.setSelection(editTextEmail.getText().toString().length());


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }


    /**
     * ASync for register from server
     */
    public void forgotPasswordAsync(final Activity activity, final String email, boolean isPhoneNumber) {
        if (MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

            HashMap<String, String> params = new HashMap<>();

            if(isPhoneNumber){
                params.put("phone_no", email);
            }
            else{
                params.put("email", email);
            }

            Log.i("params", "=" + params);

//            AsyncHttpClient client = Data.getClient();
//            client.post(Config.getServerUrl() + "/forgot_password", params,
//                new CustomAsyncHttpResponseHandler() {
//                    private JSONObject jObj;
//
//                    @Override
//                    public void onSuccess(String response) {
//                        Log.v("Server response", "response = " + response);
//
//                        try {
//                            jObj = new JSONObject(response);
//
//                            if (!jObj.isNull("error")) {
//                                String errorMessage = jObj.getString("error");
//                                DialogPopup.alertPopup(activity, "", errorMessage);
//                            } else {
//                                DialogPopup.alertPopupWithListener(activity, "", jObj.getString("log"), new View.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = getLoginIntent(email);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                                        finish();
//                                    }
//                                });
//                            }
//                        } catch (Exception exception) {
//                            exception.printStackTrace();
//                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
//                        }
//                        DialogPopup.dismissLoadingDialog();
//                    }
//
//
//                    @Override
//                    public void onFailure(Throwable arg3) {
//                        Log.e("request fail", arg3.toString());
//                        DialogPopup.dismissLoadingDialog();
//                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
//                    }
//
//                });

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().forgotPassword(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.v("Server response", "response = " + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);

                        if (!jObj.isNull("error")) {
                            String errorMessage = jObj.getString("error");
                            DialogPopup.alertPopup(activity, "", errorMessage);
                        } else {
                            DialogPopup.alertPopupWithListener(activity, "", jObj.getString("log"), new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = getLoginIntent(email);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    finish();
                                }
                            });
                        }
                    } catch (Exception exception) {
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

        } else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private Intent getLoginIntent(String email){
        Intent intent = new Intent(ForgotPasswordScreen.this, SplashNewActivity.class);
        intent.putExtra(Constants.KEY_SPLASH_STATE, SplashNewActivity.State.LOGIN.getOrdinal());
        intent.putExtra(Constants.KEY_FORGOT_LOGIN_EMAIL, email);
        return intent;
    }

    @Override
    public void onBackPressed() {
        startActivity(getLoginIntent(emailAlready));
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        ASSL.closeActivity(relative);

        System.gc();
    }

}
