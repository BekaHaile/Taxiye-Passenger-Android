package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.FetchPayDataResponse;
import com.jugnoo.pay.models.GenerateTokenRequest;
import com.jugnoo.pay.models.TokenGeneratedResponse;
import com.jugnoo.pay.models.VerifyRegisterResponse;
import com.jugnoo.pay.models.VerifyUserRequest;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;
import com.yesbank.Registration;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 7/7/16.
 */
public class SignUpActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @Bind(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }

//     @OnClick(R.id.terms_txt)
//    void termsTxtClicked() {
//        CommonMethods.openUrl(SignUpActivity.this, getResources().getString(R.string.terms_url_string));
//    }
//
//
//    @OnClick(R.id.conditions_txt)
//    void conditionsTxtClicked() {
//        CommonMethods.openUrl(SignUpActivity.this, getResources().getString(R.string.privacy_url_string));
//    }

    @Bind(R.id.sign_up_btn)
    Button signUpBtn;

    @OnClick(R.id.sign_up_btn)
    void signUpBtnClicked() {
        Validator validator = new Validator();
        if (validator.regValidateScreenOne(fNameET.getText().toString().trim(), fNameET, emailET.getText().toString().trim(),
                emailET, phoneET.getText().toString().trim(), phoneET, pswdET.getText().toString().trim(), pswdET)) {
            DialogPopup.alertPopupTwoButtonsWithListeners(SignUpActivity.this, "", getString(R.string.choose_mobile_number),
                    getString(R.string.register),
                    getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callRegisterApi();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, true, false);
        }
    }

    @Bind(R.id.first_name_et)
    EditText fNameET;
    @Bind(R.id.email_et)
    EditText emailET;

    @Bind(R.id.phone_et)
    EditText phoneET;

    @Bind(R.id.password_et)
    EditText pswdET;

    @OnClick(R.id.textViewTerms)
    void textViewTermsClicked(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jugnoo.in/terms-of-service"));
        startActivity(browserIntent);
    }


//    @Bind(R.id.terms_check)
//    CheckBox termsCheck;


    private String userChoosenTask = null;
    private String deviceToken, accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        toolbarTitleTxt.setText(R.string.sign_up_string);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        deviceToken = Prefs.with(SignUpActivity.this).getString(SharedPreferencesName.DEVICE_TOKEN, "");
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        setupParent(mainLayout);

        pswdET.setTypeface(fNameET.getTypeface());

//        Intent intent = getIntent();
    }


    private void callRegisterApi() {
        CallProgressWheel.showLoadingDialog(SignUpActivity.this, AppConstant.PLEASE);
        GenerateTokenRequest request = new GenerateTokenRequest();
        request.setUserEmail(emailET.getText().toString());
        request.setPhone_number(phoneET.getText().toString());
        request.setLatitude("11");
        request.setLongitude("11");
        request.setUniqueDeviceId(CommonMethods.getUniqueDeviceId(SignUpActivity.this));
        request.setDeviceToken(MyApplication.getInstance().getDeviceToken());
        request.setDeviceType("0");


        RestClient.getPayApiService().generateToken(request, new Callback<TokenGeneratedResponse>() {
            @Override
            public void success(TokenGeneratedResponse tokenGeneratedResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                if (tokenGeneratedResponse != null) {
                    accessToken = tokenGeneratedResponse.getToken();
                    int flag = tokenGeneratedResponse.getFlag();
                    if (flag == ApiResponseFlags.TOKEN_GENERATED_SUCCSSFULLY.getOrdinal()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("mid", tokenGeneratedResponse.getMid());
                        bundle.putString("merchantKey", tokenGeneratedResponse.getMkey());
                        bundle.putString("merchantTxnID", tokenGeneratedResponse.getToken());
                        bundle.putString("appName", "jugnooApp");
                        Intent intent = new Intent(getApplicationContext(), Registration.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                    } else {
                        CommonMethods.callingBadToken(SignUpActivity.this, flag, tokenGeneratedResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SelectServiceActivity.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(SelectServiceActivity.this,"No Internet Connection", "Ok");
                        showAlertNoInternet(SignUpActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        DialogPopup.alertPopup(SignUpActivity.this, "", jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }
            }
        });


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
            String yblRefId = bundle.getString("yblRefId");
            String virtualAddress = bundle.getString("virtualAddress");
            String status = bundle.getString("status");
            String statusdesc = bundle.getString("statusdesc");
            String registrationDate = bundle.getString("registrationDate");
//            System.out.println("data=== "+bundle.getString("add1"));
            System.out.println("virtual address== " + virtualAddress + " date=== " + registrationDate + "  ybl== " + yblRefId + "  pgM== " + pgMeTrnRefNo + " status== " + status + "  dsc ==" + statusdesc);

            VerifyRegisterResponse verifyRegisterResponse = new VerifyRegisterResponse();
            verifyRegisterResponse.setPgMeTrnRefNo(pgMeTrnRefNo);
            verifyRegisterResponse.setYblRefId(yblRefId);
            verifyRegisterResponse.setVirtualAddress(virtualAddress);
            verifyRegisterResponse.setStatus(status);
            verifyRegisterResponse.setStatusdesc(statusdesc);
            verifyRegisterResponse.setRegistrationDate(registrationDate);

            if (virtualAddress.length() > 0) {
                callVerifyUserApi(verifyRegisterResponse);
            }
        }
    }


    private void callVerifyUserApi(VerifyRegisterResponse verifyRegisterResponse) {
        CallProgressWheel.showLoadingDialog(SignUpActivity.this, AppConstant.PLEASE);
        String deviceToken = Prefs.with(SignUpActivity.this).getString(SharedPreferencesName.DEVICE_TOKEN, "");
//        String accessToken =   Prefs.with(SignUpActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN,"");
        VerifyUserRequest request = new VerifyUserRequest();
        request.setUserEmail(emailET.getText().toString());
        request.setDeviceToken(MyApplication.getInstance().getDeviceToken());
        request.setLatitude("11");
        request.setLongitude("11");
        request.setUniqueDeviceId(CommonMethods.getUniqueDeviceId(SignUpActivity.this));
        request.setPhone_no(phoneET.getText().toString());
        request.setToken(accessToken);
        request.setUser_name(fNameET.getText().toString());
        request.setPassword(pswdET.getText().toString());
        request.setVpa(verifyRegisterResponse.getVirtualAddress());
        request.setDeviceType("0");
        request.setMessage(verifyRegisterResponse.toString());

        RestClient.getPayApiService().verifyUser(request, new Callback<FetchPayDataResponse>() {
            @Override
            public void success(FetchPayDataResponse tokenGeneratedResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                if (tokenGeneratedResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = tokenGeneratedResponse.getFlag();
                    if (flag == 401) {
                        Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, "");
                        Prefs.with(SignUpActivity.this).save(SharedPreferencesName.APP_USER, tokenGeneratedResponse);
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    } else
                        CommonMethods.callingBadToken(SignUpActivity.this, flag, tokenGeneratedResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SelectServiceActivity.failure2222222");

                    CallProgressWheel.dismissLoadingDialog();

                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(SelectServiceActivity.this,"No Internet Connection", "Ok");
                        showAlertNoInternet(SignUpActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        DialogPopup.alertPopup(SignUpActivity.this, "", jsonObject.getString("message"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }

            }
        });


    }


}