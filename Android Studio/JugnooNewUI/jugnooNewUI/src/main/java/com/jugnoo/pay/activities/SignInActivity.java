package com.jugnoo.pay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.LoginRequest;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.jugnoo.pay.utils.TwoButtonAlert;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 7/7/16.
 */
public class SignInActivity extends BaseActivity {
    private String deviceToken;
//    @Bind(R.id.toolbar)
//    Toolbar mToolBar;
//    @Bind(R.id.toolbar_title)
//    TextView toolbarTitleTxt;
//    @Bind(R.id.back_btn)
//    ImageButton backBtn;
//
//    @OnClick(R.id.back_btn)
//    void backBtnClicked() {
//        onBackPressed();
//    }

    @Bind(R.id.sign_in_btn)
    Button signInBtn;
   @Bind(R.id.sign_up_txt)
    TextView signUpTxt;
    @Bind(R.id.forgot_pswd_txt)
    TextView forgotPswdTxt;

    @OnClick(R.id.sign_up_txt)
    void signUpTxtClicked() {
        startActivity(new Intent(this, SignUpActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @OnClick(R.id.forgot_pswd_txt)
    void forgotPswdTxtClicked() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @OnClick(R.id.sign_in_btn)
    void signInBtnClicked() {

        Validator validator = new Validator();
        if (validator.validateLoginUser(phoneET.getText().toString().trim(), phoneET, pswdET.getText().toString().trim(), pswdET)) {
            callingLoginApi();
        }
    }

    @Bind(R.id.phone_et)
    EditText phoneET;
    @Bind(R.id.password_et)
    EditText pswdET;
    private String facebookId, fName, lName, profilePicUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
//        toolbarTitleTxt.setText(R.string.sign_in_string);
//        mToolBar.setTitle("");
//        setSupportActionBar(mToolBar);
        pswdET.setTypeface(phoneET.getTypeface());
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        setupParent(mainLayout);


        deviceToken = Prefs.with(SignInActivity.this).getString(SharedPreferencesName.DEVICE_TOKEN, "");
        if (deviceToken.isEmpty() || deviceToken.length() == 0) {
            MyApplication.getInstance().getDeviceToken();
        }


    }


    // used to verifying access token of user
    private void callingLoginApi() {
        CallProgressWheel.showLoadingDialog(SignInActivity.this, AppConstant.PLEASE);
        LoginRequest request = new LoginRequest();
        request.setPhone_no(phoneET.getText().toString());
        request.setUnique_device_id(CommonMethods.getUniqueDeviceId(SignInActivity.this));
        request.setPassword(pswdET.getText().toString());
        request.setLatitude("11");
        request.setLongitude("11");
        request.setDevice_token(MyApplication.getInstance().getDeviceToken());
        request.setDeviceType("0");


        RestClient.getPayApiService().loginUser(request, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse tokenGeneratedResponse, Response response) {
                System.out.println("SignInActivity.success22222222");
                CallProgressWheel.dismissLoadingDialog();
                if (tokenGeneratedResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = tokenGeneratedResponse.getFlag();
                    if (flag == ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal()) {
                        Prefs.with(SignInActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getAccessToken());
                        Prefs.with(SignInActivity.this).save(SharedPreferencesName.APP_USER, tokenGeneratedResponse);
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    } else if(flag == ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal()){
                        TwoButtonAlert.showAlert(SignInActivity.this, tokenGeneratedResponse.getMessage(), AppConstant.CANCEL, AppConstant.REGISTER,
                                new TwoButtonAlert.OnAlertOkCancelClickListener() {
                            @Override
                            public void onOkButtonClicked() {
                                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                finish();
                            }

                            @Override
                            public void onCancelButtonClicked() {

                            }
                        });
                    }  else{
                        pswdET.getText().clear();
                        CommonMethods.callingBadToken(SignInActivity.this,flag,tokenGeneratedResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SignInActivity.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        showAlertNoInternet(SignInActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(SignInActivity.this, jsonObject.getString("message"), AppConstant.OK);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
