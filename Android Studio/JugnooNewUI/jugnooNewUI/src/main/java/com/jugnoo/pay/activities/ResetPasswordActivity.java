package com.jugnoo.pay.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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
 * Created by ankit on 29/09/16.
 */
public class ResetPasswordActivity extends BaseActivity{

    @Bind(R.id.editTextOTP)
    EditText editTextOTP;
    @Bind(R.id.editTextNewPass)
    EditText editTextNewPass;
    @Bind(R.id.editTextConfirmPass)
    EditText editTextConfirmPass;
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

    @OnClick(R.id.buttonResetPassword)
    void sendBtnClicked() {
        Validator validator = new Validator();
        if(editTextOTP.getText().length() > 0
                && (validator.validateSamePassword(editTextNewPass.getText().toString(), editTextNewPass, editTextConfirmPass.getText().toString(), editTextConfirmPass))){
            // Api call
            resetPswdApi();
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pswd);
        ButterKnife.bind(this);
        toolbarTitleTxt.setText(R.string.reset_password_screen);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        setupParent(mainLayout);
    }

    private void resetPswdApi() {
        if(AppStatus.getInstance(ResetPasswordActivity.this).isOnline(ResetPasswordActivity.this)) {
            CallProgressWheel.showLoadingDialog(ResetPasswordActivity.this, AppConstant.PLEASE);
            String p = editTextNewPass.getText().toString().trim();
            HashMap<String, String> params = new HashMap<>();

            params.put("otp", editTextOTP.getText().toString());
            params.put("password", p);

            RestClient.getPayApiService().resetPassword(params, new Callback<CommonResponse>() {
                @Override
                public void success(CommonResponse commonResponse, Response response) {
                    CallProgressWheel.dismissLoadingDialog();
                    if (commonResponse != null) {
                        int flag = commonResponse.getFlag();
                        if (flag == ApiResponseFlags.PASSWORD_RESET_SUCCESS.getOrdinal()) {
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            finish();
                        } else {
                            CommonMethods.callingBadToken(ResetPasswordActivity.this, flag, commonResponse.getMessage());
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    CallProgressWheel.dismissLoadingDialog();
                    try {
                        if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(HomeActivity.this,"No Internet Connection", "Ok");
                            showAlertNoInternet(ResetPasswordActivity.this);
                        } else {
                            String json = new String(((TypedByteArray) error.getResponse()
                                    .getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(json);
                            SingleButtonAlert.showAlert(ResetPasswordActivity.this, jsonObject.getString("message"), AppConstant.OK);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
