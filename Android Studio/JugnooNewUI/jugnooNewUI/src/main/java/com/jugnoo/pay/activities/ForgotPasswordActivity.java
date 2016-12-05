package com.jugnoo.pay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AppStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 7/8/16.
 */
public class ForgotPasswordActivity extends BaseActivity {
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

    @Bind(R.id.send_otp_btn)
    Button sendBtn;

    @OnClick(R.id.send_otp_btn)
    void sendBtnClicked() {
        Validator validator = new Validator();
        String phone = phoneET.getText().toString();
        if (phone.length() == 10 && !phone.isEmpty()) {
            callingForgotPswdApi();
        } else {
            phoneET.setError("Please enter your phone number.");
            phoneET.requestFocus();
            phoneET.setHovered(true);
        }
//        else if(phone.length()<12)
//        {
//            phoneET.setError("Please enter your 10 digit phone number.");
//            phoneET.requestFocus();
//            phoneET.setHovered(true);
//        }


    }

    @Bind(R.id.phone_et)
    EditText phoneET;

    public static ForgotPasswordActivity forgotPswdClassObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pswd);
        ButterKnife.bind(this);
        forgotPswdClassObject = this;
        toolbarTitleTxt.setText(R.string.forgot_password_screen);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        setupParent(mainLayout);

        phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (start != phoneET.getText().length()) {
                    if ((start == 2) || (start == 6)) {
                        phoneET.getEditableText().append(' ');
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable text) {
                // TODO Auto-generated method stub


            }
        });


    }


    private void callingForgotPswdApi() {
        if(AppStatus.getInstance(ForgotPasswordActivity.this).isOnline(ForgotPasswordActivity.this)) {
            CallProgressWheel.showLoadingDialog(ForgotPasswordActivity.this, AppConstant.PLEASE);
            String p = phoneET.getText().toString().trim();
            final String phone = p.replace(" ", "");
            HashMap<String, String> params = new HashMap<>();

            params.put("phone_no", p);

            RestClient.getPayApiService().forgotPassword(params, new Callback<CommonResponse>() {
                @Override
                public void success(CommonResponse commonResponse, Response response) {
                    CallProgressWheel.dismissLoadingDialog();
                    try {
                        if (commonResponse != null) {
                            int flag = commonResponse.getFlag();
                            if (flag == ApiResponseFlags.PASSWORD_RESET_SUCCESS.getOrdinal()) {
                                startActivity(new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                finish();
                            } else {
                                CommonMethods.callingBadToken(ForgotPasswordActivity.this, flag, commonResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    CallProgressWheel.dismissLoadingDialog();
                    try {
                        if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(HomeActivity.this,"No Internet Connection", "Ok");
                            showAlertNoInternet(ForgotPasswordActivity.this);
                        } else {
                            String json = new String(((TypedByteArray) error.getResponse()
                                    .getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(json);
                            SingleButtonAlert.showAlert(ForgotPasswordActivity.this, jsonObject.getString("message"), AppConstant.OK);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
