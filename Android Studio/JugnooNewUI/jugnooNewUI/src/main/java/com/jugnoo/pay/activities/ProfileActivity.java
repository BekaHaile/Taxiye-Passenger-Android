package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jugnoo.pay.models.AccessTokenRequest;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.SendMoneyResponse;
import com.jugnoo.pay.models.SetMPINResponse;
import com.jugnoo.pay.services.GCMIntentService;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.jugnoo.pay.utils.TwoButtonAlert;
import com.jugnoo.pay.utils.Validator;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.yesbank.SetMpin;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 9/23/16.
 */
public class ProfileActivity extends BaseActivity {
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

    @Bind(R.id.first_name_et)
    EditText fNameET;
    @Bind(R.id.email_et)
    EditText emailET;

    @Bind(R.id.phone_txt)
    TextView phoneTxt;

    @Bind(R.id.old_password_et)
    EditText oldPswdET;

    @Bind(R.id.new_password_et)
    EditText newPswdET;
    @Bind(R.id.confirm_password_et)
    EditText confirmPswdET;

    @Bind(R.id.name_char)
    TextView nameChar;

    @OnClick(R.id.logout_layout)
    void logoutBtnClicked() {
        TwoButtonAlert.showAlert(ProfileActivity.this, "Are you sure you want to logout?", AppConstants.CANCEL, AppConstants.LOG_OUT, new TwoButtonAlert.OnAlertOkCancelClickListener() {
            @Override
            public void onOkButtonClicked() {
                //callingLogOutUserApi();
                logoutAsync(ProfileActivity.this);
            }

            @Override
            public void onCancelButtonClicked() {

            }
        });
    }

    @OnClick(R.id.textViewChangeMPIN)
    void textViewChangeMPINClicked(){
        changeMPINApi();
    }

    @Bind(R.id.pswd_layout)
    LinearLayout pswdLayout;

    @OnClick(R.id.pswd_edit_layout)
    void editPswdClicked() {
        if (!editPswdStatus) {
            pswdLayout.setVisibility(View.VISIBLE);
            editPswdStatus = true;
            downArrwBtn.setImageResource(R.drawable.save_normal);
            divider.setVisibility(View.GONE);
            oldPswdET.setError(null);
        } else {
            downArrwBtn.setImageResource(R.drawable.icon_edit);
            editPswdStatus = false;
            pswdLayout.setVisibility(View.GONE);
            divider.setVisibility(View.VISIBLE);
        }
    }

    @Bind(R.id.edit_info_btn)
    ImageView editInfoBtn;

    @OnClick(R.id.edit_info_btn)
    void editInfoClicked() {
        if (!editInfoStatus) {
            setEditTextsEnable(true);
            editInfoStatus = true;
            editInfoBtn.setImageResource(R.drawable.save_normal);
        } else {

            String fname = fNameET.getText().toString().trim();
            String email = emailET.getText().toString().trim();
            if (fname.length() == 0 || fname.isEmpty()) {
                fNameET.setHovered(true);
                fNameET.requestFocus();
                Toast.makeText(getApplicationContext(), "Please fill your full name", Toast.LENGTH_LONG).show();
//            return;
            } else if (email.length() == 0 || email.isEmpty()) {
                emailET.setHovered(true);
                emailET.requestFocus();
                Toast.makeText(getApplicationContext(), "Please fill your email id", Toast.LENGTH_LONG).show();
//            return;
            } else {
                TwoButtonAlert.showAlert(ProfileActivity.this, "Do you want to save changes?", "Cancel", "Save", new TwoButtonAlert.OnAlertOkCancelClickListener() {
                    @Override
                    public void onOkButtonClicked() {
                        //callUpdateProfileApi(false);
                        updateUserProfileAPI(false);
                    }

                    @Override
                    public void onCancelButtonClicked() {

                    }
                });
            }


        }
    }

    private String accessToken;
    private CommonResponse userDetails;
    private boolean editInfoStatus = false, editPswdStatus = false;
    private ImageView downArrwBtn;
    private View divider;
    private ImageView imageViewProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        toolbarTitleTxt.setText(R.string.profile_screen);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        downArrwBtn = (ImageView)findViewById(R.id.down_arrow);
        divider = (View)findViewById(R.id.divider);
        accessToken = Data.userData.accessToken;
        userDetails = Prefs.with(ProfileActivity.this).getObject(SharedPreferencesName.APP_USER, CommonResponse.class);
        (findViewById(R.id.first_name_et)).getBackground().mutate().setColorFilter(getResources().getColor(R.color.services_name_txt_color), PorterDuff.Mode.SRC_ATOP);
        (findViewById(R.id.email_et)).getBackground().mutate().setColorFilter(getResources().getColor(R.color.services_name_txt_color), PorterDuff.Mode.SRC_ATOP);
        imageViewProfile = (ImageView)findViewById(R.id.profile_image);

        try {
            fNameET.setText(Data.userData.userName);
            emailET.setText(Data.userData.userEmail);
            phoneTxt.setText(Data.userData.phoneNo);
            nameChar.setText(Data.userData.userName.substring(0, 1).toUpperCase());
            if(!"".equalsIgnoreCase(Data.userData.userImage)) {
                Picasso.with(ProfileActivity.this).load(Data.userData.userImage).transform(new CircleTransform())
                        .into(imageViewProfile);
            }
            setEditTextsEnable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        downArrwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editPswdStatus) {
                    pswdLayout.setVisibility(View.VISIBLE);
                    editPswdStatus = true;
                    downArrwBtn.setImageResource(R.drawable.save_normal);
                    divider.setVisibility(View.GONE);
                } else {
                    Validator validator = new Validator();
                    if (validator.validateNotSamePassword(oldPswdET.getText().toString(), oldPswdET, newPswdET.getText().toString(), newPswdET)) {
                        if (validator.validateSamePassword(newPswdET.getText().toString(), newPswdET, confirmPswdET.getText().toString(), confirmPswdET)) {
                            TwoButtonAlert.showAlert(ProfileActivity.this, "Do you want to save Changes?", "Cancel", "Save", new TwoButtonAlert.OnAlertOkCancelClickListener() {
                                @Override
                                public void onOkButtonClicked() {
                                    //callUpdateProfileApi(true);
                                    updateUserProfileAPI(true);
                                }

                                @Override
                                public void onCancelButtonClicked() {

                                }
                            });
                        }
                    }
                }
            }
        });

    }

    // to sign out user from app
    private void callingLogOutUserApi() {
        CallProgressWheel.showLoadingDialog(ProfileActivity.this, "Please wait..");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccess_token(accessToken);

        RestClient.getPayApiService().logout(accessTokenRequest, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse commonResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("ProfileActivity.success");

                if (commonResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = commonResponse.getFlag();
                    if (flag == 409 || flag == 101) {
                        Prefs.with(ProfileActivity.this).removeAll();
                        // startActivity(new Intent(ProfileActivity.this, SignInActivity.class));
                        startActivity(new Intent(ProfileActivity.this, SplashNewActivity.class));
                        finish();
                    } else {
                        CommonMethods.callingBadToken(ProfileActivity.this, flag, commonResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("ProfileActivity.failure");
                try {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(MainActivity.this,"No Internet Connection", "Ok");
                        showAlertNoInternet(ProfileActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(ProfileActivity.this, jsonObject.getString("message"), AppConstants.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    void logoutAsync(final Activity activity) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            CallProgressWheel.showLoadingDialog(activity, "Please Wait ...");

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

            Log.i("access_token", "=" + Data.userData.accessToken);

            RestClient.getApiServices().logoutUser(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.v("Profile", "logoutUser response = " + responseStr);

                    try {
                        JSONObject jObj = new JSONObject(responseStr);

                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.AUTH_LOGOUT_FAILURE.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.AUTH_LOGOUT_SUCCESSFUL.getOrdinal() == flag) {
                                logoutFunc(activity, null);
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                    CallProgressWheel.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("Profile", "logoutUser error=" + error.toString());
                    CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }

    private void logoutFunc(Activity activity, String message){

        FacebookLoginHelper.logoutFacebook();

        GCMIntentService.clearNotifications(activity);

        Data.clearDataOnLogout(activity);

        ActivityCompat.finishAffinity(activity);
        Intent intent = new Intent(activity, SplashNewActivity.class);
        if(message != null){
            intent.putExtra(Constants.KEY_LOGGED_OUT, 1);
            intent.putExtra(Constants.KEY_MESSAGE, message);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    // to update user profile
    private void callUpdateProfileApi(final boolean changePswd) {
        CallProgressWheel.showLoadingDialog(ProfileActivity.this, "Please wait..");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccess_token(accessToken);
        if (changePswd) {
            accessTokenRequest.setOld_password(oldPswdET.getText().toString());
            accessTokenRequest.setNew_password(newPswdET.getText().toString());
        } else {
            accessTokenRequest.setUpdated_user_email(emailET.getText().toString());
            accessTokenRequest.setUpdated_user_name(fNameET.getText().toString());
        }
        RestClient.getPayApiService().updateProfile(accessTokenRequest, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse commonResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("ProfileActivity.success");

                if (commonResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = commonResponse.getFlag();
                    if (flag == 143) {
                        if (changePswd) {
                            pswdLayout.setVisibility(View.GONE);
                            editPswdStatus = false;
                            downArrwBtn.setImageResource(R.drawable.arrow);

                        } else {
                            setEditTextsEnable(false);
                            editInfoStatus = false;
                            editInfoBtn.setImageResource(R.drawable.icon_edit);
                        }
                    } else {
                        CommonMethods.callingBadToken(ProfileActivity.this, flag, commonResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("ProfileActivity.failure");
                try {
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
//                        SingleButtonAlert.showAlert(MainActivity.this,"No Internet Connection", "Ok");
                        showAlertNoInternet(ProfileActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(ProfileActivity.this, jsonObject.getString("message"), AppConstants.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void updateUserProfileAPI(final boolean changePswd) {
        if(AppStatus.getInstance(ProfileActivity.this).isOnline(ProfileActivity.this)) {

            CallProgressWheel.showLoadingDialog(ProfileActivity.this, "Updating...");

            HashMap<String, String> params = new HashMap<>();

            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put("latitude", "" + Data.loginLatitude);
            params.put("longitude", "" + Data.loginLongitude);
            if (changePswd) {
                params.put("old_password", oldPswdET.getText().toString());
                params.put("new_password", newPswdET.getText().toString());
            } else {
                params.put("updated_user_email", emailET.getText().toString());
                params.put("updated_user_name", fNameET.getText().toString());
            }

            RestClient.getApiServices().updateUserProfile(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i("Profile", "updateUserProfile response = " + responseStr);
                    CallProgressWheel.dismissLoadingDialog();
                    try {

                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(ProfileActivity.this, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(ProfileActivity.this, "", error);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                if (changePswd) {
                                    pswdLayout.setVisibility(View.GONE);
                                    editPswdStatus = false;
                                    downArrwBtn.setImageResource(R.drawable.arrow);

                                    // logoutAsync(ProfileActivity.this);
                                    logoutFunc(ProfileActivity.this, null);

                                } else {
                                    setEditTextsEnable(false);
                                    editInfoStatus = false;
                                    editInfoBtn.setImageResource(R.drawable.icon_edit);
                                    Data.userData.userName = fNameET.getText().toString();
                                    Data.userData.phoneNo = phoneTxt.getText().toString();
                                }
                            } else {
                                DialogPopup.alertPopup(ProfileActivity.this, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(ProfileActivity.this, "", Data.SERVER_ERROR_MSG);
                        CallProgressWheel.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("Profile", "updateUserProfile error="+error.toString());
                    CallProgressWheel.dismissLoadingDialog();
                    DialogPopup.alertPopup(ProfileActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        }
        else {
            DialogPopup.alertPopup(ProfileActivity.this, "", Data.CHECK_INTERNET_MSG);
        }

    }


    void setEditTextsEnable(boolean status) {
        fNameET.setEnabled(status);
        emailET.setEnabled(status);
    }

    // used to send the  money
    private void changeMPINApi() {
        CallProgressWheel.showLoadingDialog(ProfileActivity.this, AppConstants.PLEASE);
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token",  accessToken);

        RestClient.getPayApiService().setMPIN(params, new Callback<SendMoneyResponse>() {
            @Override
            public void success(SendMoneyResponse sendMoneyResponse, Response response) {
                System.out.println("SendMoneyActivity.success22222222");
                CallProgressWheel.dismissLoadingDialog();

                if (sendMoneyResponse != null) {
                    int flag = sendMoneyResponse.getFlag();
                    if (flag == ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
                        callBankSetMPINApi(sendMoneyResponse.getTxnDetails());
                    } else if(flag == ApiResponseFlags.TXN_ALREADY_EXISTS.getOrdinal()){
                        Toast.makeText(ProfileActivity.this, sendMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else{
                        CommonMethods.callingBadToken(ProfileActivity.this, flag, sendMoneyResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SendMoneyActivity.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        showAlertNoInternet(ProfileActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(ProfileActivity.this, jsonObject.getString("message"), AppConstants.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }

            }
        });


    }

    void callBankSetMPINApi(SendMoneyResponse.TxnDetails txnDetails) {
        Bundle bundle = new Bundle();
        bundle.putString("mid", txnDetails.getMid());
        bundle.putString("merchantKey", txnDetails.getMkey());// b0222ce704ebc0c1f4dc24360751f9f6
        bundle.putString("merchantTxnID", Integer.toString(txnDetails.getOrderId())); // 11
        bundle.putString("virtualAddress", txnDetails.getVpa()); // P2P

        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");

        Log.v("mid == ", txnDetails.getMid());
        Log.v("merchantKey == ", txnDetails.getMkey());
        Log.v("merchantTxnID== ", Integer.toString(txnDetails.getOrderId()));
        Log.v("virtualAddress== ", txnDetails.getVpa());

        Intent intent = new Intent(getApplicationContext(), SetMpin.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null) {
            Bundle bundle= data.getExtras();

            String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
            String yblRefId = bundle.getString("yblRefId");
            String virtualAddress = bundle.getString("virtualAddress");
            String status= bundle.getString("status");
            String statusdesc = bundle.getString("statusdesc");
            String date = bundle.getString("date");

            // new code - added on 21-11-2016
            String accountNo = bundle.getString("accountNo");
            String ifsc = bundle.getString("ifsc");
            String accName = bundle.getString("accName");
            //----------------------

            String add1 = bundle.getString("add1");
            String add2 = bundle.getString("add2");
            String add3 = bundle.getString("add3");
            String add4 = bundle.getString("add4");
            String add5 = bundle.getString("add5");
            String add6 = bundle.getString("add6");
            String add7 = bundle.getString("add7");
            String add8 = bundle.getString("add8");
            String add9 = bundle.getString("add9");
            String add10 = bundle.getString("add10");

            Log.v("pgMeTrnRefNo== ", pgMeTrnRefNo);
            Log.v("yblRefId== ", yblRefId);
            Log.v("virtualAddress== ", virtualAddress);
            Log.v("status== ", status);
            Log.v("statusdesc== ", statusdesc);
            Log.v("date== ", date);

            SetMPINResponse setMPINResponse = new SetMPINResponse();
            setMPINResponse.setPgMeTrnRefNo(pgMeTrnRefNo);
            setMPINResponse.setYblRefId(yblRefId);
            setMPINResponse.setVirtualAddress(virtualAddress);
            setMPINResponse.setStatus(status);
            setMPINResponse.setStatusdesc(statusdesc);
            setMPINResponse.setDate(date);

            // new code - added on 21-11-2016
            setMPINResponse.setAccountNo(accountNo);
            setMPINResponse.setIfsc(ifsc);
            setMPINResponse.setAccName(accName);
            //--------------------

            changeMPINCallbackApi(setMPINResponse);
        }
        else{
            if(data == null){
                Log.e("call failed","call failed");
            }
        }
    }

    private void changeMPINCallbackApi(SetMPINResponse setMPINResponse) {
        CallProgressWheel.showLoadingDialog(ProfileActivity.this, AppConstants.PLEASE);
        HashMap<String, String> params = new HashMap<>();

        params.put("access_token",  accessToken);
        params.put("message", setMPINResponse.toString());

        RestClient.getPayApiService().setMPINCallback(params, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse commonResponse, Response response) {
                System.out.println("Change MPIN Callback.success22222222");
                CallProgressWheel.dismissLoadingDialog();

                if (commonResponse != null) {
                    int flag = commonResponse.getFlag();
                    if (flag == ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
                        //callBankSetMPINApi(sendMoneyResponse.getTxnDetails());

                    } else {
                        CommonMethods.callingBadToken(ProfileActivity.this, flag, commonResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("change MPIN Callback.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        showAlertNoInternet(ProfileActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(ProfileActivity.this, jsonObject.getString("message"), AppConstants.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }

            }
        });


    }

}
