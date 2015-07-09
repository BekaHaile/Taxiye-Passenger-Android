package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class ForgotPasswordScreen extends Activity {

    TextView textViewTitle;
    ImageView imageViewBack;

    TextView textViewForgotPasswordHelp;
    EditText editTextEmail;
    Button buttonSendEmail;

    TextView extraTextForScroll;

    LinearLayout relative;

    static String emailAlready = "";

    // *****************************Used for flurry work***************//
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


        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewForgotPasswordHelp = (TextView) findViewById(R.id.textViewForgotPasswordHelp);
        textViewForgotPasswordHelp.setTypeface(Fonts.latoRegular(this));

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.setTypeface(Fonts.latoRegular(this));

        buttonSendEmail = (Button) findViewById(R.id.buttonSendEmail);
        buttonSendEmail.setTypeface(Fonts.latoRegular(getApplicationContext()));

        extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);


        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordScreen.this, SplashLogin.class));
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
                            FlurryEventLogger.emailLoginClicked(email);
                        }
                    } else {
                        if (isEmailValid(email)) {
                            forgotPasswordAsync(ForgotPasswordScreen.this, email, false);
                            FlurryEventLogger.forgotPasswordClicked(email);
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

        final View activityRootView = findViewById(R.id.mainLinear);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    // r will be populated with the coordinates of your view
                    // that area still visible.
                    activityRootView.getWindowVisibleDisplayFrame(r);

                    int heightDiff = activityRootView.getRootView()
                        .getHeight() - (r.bottom - r.top);
                    if (heightDiff > 100) { // if more than 100 pixels, its
                        // probably a keyboard...

                        /************** Adapter for the parent List *************/

                        ViewGroup.LayoutParams params_12 = extraTextForScroll
                            .getLayoutParams();

                        params_12.height = (int) (heightDiff);

                        extraTextForScroll.setLayoutParams(params_12);
                        extraTextForScroll.requestLayout();

                    } else {

                        ViewGroup.LayoutParams params = extraTextForScroll
                            .getLayoutParams();
                        params.height = 0;
                        extraTextForScroll.setLayoutParams(params);
                        extraTextForScroll.requestLayout();

                    }
                }
            });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }


    /**
     * ASync for register from server
     */
    public void forgotPasswordAsync(final Activity activity, final String email, boolean isPhoneNumber) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            if(isPhoneNumber){
                params.put("phone_no", email);
            }
            else{
                params.put("email", email);
            }

            Log.i("params", "=" + params);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/forgot_password", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onSuccess(String response) {
                        Log.v("Server response", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if (!jObj.isNull("error")) {
                                String errorMessage = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", errorMessage);
                            } else {
                                DialogPopup.alertPopupWithListener(activity, "", jObj.getString("log"), new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ForgotPasswordScreen.this, SplashLogin.class);
                                        intent.putExtra("forgot_login_email", email);
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
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
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


    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordScreen.this, SplashLogin.class));
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
