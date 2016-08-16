package com.sabkuchfresh.home;

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

import com.sabkuchfresh.R;
import com.sabkuchfresh.SplashNewActivity;
import com.sabkuchfresh.TokenGenerator.HomeUtil;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.SettleUserDebt;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ForgotPasswordScreen extends BaseActivity implements FlurryEventNames {

    TextView textViewTitle;
    ImageView imageViewBack;

    TextView textViewForgotPasswordHelp;
    EditText editTextEmail;
    Button buttonSendEmail;

    LinearLayout relative;

    public static String emailAlready = "";


    @Override
    protected void onResume() {
        super.onResume();
        HomeUtil.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(ForgotPasswordScreen.this, relative, 1134, 720, false);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewForgotPasswordHelp = (TextView) findViewById(R.id.textViewForgotPasswordHelp);
        textViewForgotPasswordHelp.setTypeface(Fonts.mavenLight(this));

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.setTypeface(Fonts.latoRegular(this));

        buttonSendEmail = (Button) findViewById(R.id.buttonSendEmail);
        buttonSendEmail.setTypeface(Fonts.mavenRegular(this));



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
                    editTextEmail.setError("Please enter email or phone number");
                } else {
                    boolean onlyDigits = Utils.checkIfOnlyDigits(email);
                    if (onlyDigits) {
                        email = Utils.retrievePhoneNumberTenChars(email);
                        if (!Utils.validPhoneNumber(email)) {
                            editTextEmail.requestFocus();
                            editTextEmail.setError("Please enter valid phone number");
                        } else {
                            email = "+91" + email;
                            forgotPasswordAsync(ForgotPasswordScreen.this, email, true);
                        }
                    } else {
                        if (isEmailValid(email)) {
                            forgotPasswordAsync(ForgotPasswordScreen.this, email, false);
                        } else {
                            editTextEmail.requestFocus();
                            editTextEmail.setError("Please enter valid email");
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
        if (AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

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
//                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
//                        }
//                        DialogPopup.dismissLoadingDialog();
//                    }
//
//
//                    @Override
//                    public void onFailure(Throwable arg3) {
//                        Log.e("request fail", arg3.toString());
//                        DialogPopup.dismissLoadingDialog();
//                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//                    }
//
//                });

            RestClient.getApiServices().forgotPassword(params, new Callback<SettleUserDebt>() {
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

        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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
