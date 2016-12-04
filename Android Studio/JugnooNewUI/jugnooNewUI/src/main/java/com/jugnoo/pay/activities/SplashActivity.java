package com.jugnoo.pay.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.PermissionsRefusedListener;
import com.github.jksiezni.permissive.Permissive;
import com.github.jksiezni.permissive.PermissiveMessenger;
import com.github.jksiezni.permissive.Rationale;
import com.jugnoo.pay.R;
import com.jugnoo.pay.models.AccessTokenRequest;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.retrofit.RetrofitClient;
import com.jugnoo.pay.retrofit.WebApi;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.Constants;
import com.jugnoo.pay.utils.MyApplication;
import com.jugnoo.pay.utils.Prefs;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 7/7/16.
 */
public class SplashActivity extends BaseActivity implements Constants {


    private String deviceToken, accessToken;
    private int pageNumber = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getFacebookHashKey();
        deviceToken = Prefs.with(SplashActivity.this).getString(SharedPreferencesName.DEVICE_TOKEN, "");
        accessToken = Prefs.with(SplashActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "");
        pageNumber = Prefs.with(SplashActivity.this).getInt(SharedPreferencesName.PAGE_NUMBER, 0);
        Prefs.with(SplashActivity.this).save("activity_open", 0);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverDeviceToken,
                new IntentFilter(INTENT_ACTION_DEVICE_TOKEN_UPDATE));

    }

    private BroadcastReceiver broadcastReceiverDeviceToken = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.hasExtra(KEY_DEVICE_TOKEN)) {
                    if(!Prefs.with(SplashActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "").equalsIgnoreCase("")){
                        callingAccessTokenLoginApi();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverDeviceToken);
        super.onDestroy();
    }


    // getting hash key for facebook for live app
    void getFacebookHashKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.jugnoo.pay", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
                System.out.println("hash key=== " + something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }


    // used to verifying access token of user
    private void callingAccessTokenLoginApi() {
        CallProgressWheel.showLoadingDialog(SplashActivity.this, AppConstants.PLEASE);
        AccessTokenRequest request = new AccessTokenRequest();
        request.setDevice_token(MyApplication.getInstance().getDeviceToken());
        request.setUnique_device_id(CommonMethods.getUniqueDeviceId(SplashActivity.this));
        request.setAccess_token(accessToken);
        request.setDevice_type("0");

        WebApi mWebApi = RetrofitClient.createService(WebApi.class);
        Log.v("device token", "---> "+MyApplication.getInstance().getDeviceToken());


        mWebApi.getAccessTokenLogin(request, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse tokenGeneratedResponse, Response response) {
                System.out.println("SignInActivity.success22222222");
                CallProgressWheel.dismissLoadingDialog();
                if (tokenGeneratedResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = tokenGeneratedResponse.getFlag();
                    if (flag == ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal()) {
                        Prefs.with(SplashActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getAccessToken());
                        Prefs.with(SplashActivity.this).save(SharedPreferencesName.APP_USER, tokenGeneratedResponse);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        ActivityCompat.finishAffinity(SplashActivity.this);
                    } else if (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
                        SingleButtonAlert.showAlertGps(SplashActivity.this, tokenGeneratedResponse.getMessage(), AppConstants.OK, new SingleButtonAlert.OnAlertOkClickListener() {
                            @Override
                            public void onOkButtonClicked() {
                                // startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                                startActivity(new Intent(SplashActivity.this, SplashNewActivity.class));
                                overridePendingTransition(0, 0);
                                finish();
                            }
                        });
                    } else if (flag == ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal()){
                        SingleButtonAlert.showAlertGps(SplashActivity.this, tokenGeneratedResponse.getMessage(), AppConstants.OK, new SingleButtonAlert.OnAlertOkClickListener() {
                            @Override
                            public void onOkButtonClicked() {

                            }
                        });
                    }
                    else {
                        CommonMethods.callingBadToken(SplashActivity.this, flag, tokenGeneratedResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SignInActivity.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        showAlertNoInternet(SplashActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(SplashActivity.this, jsonObject.getString("message"), AppConstants.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }
            }
        });


    }

    // checking internet connection
    public void showAlertNoInternet(final Activity context) {
        Button btnNoInternet;
        Dialog dialog = null;

        try {
            if (dialog == null) {
                dialog = new Dialog(context, R.style.AppTheme);
                dialog.setContentView(R.layout.layout_no_internet_connection);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);


                btnNoInternet = (Button) dialog.findViewById(R.id.refresh_btn);

                final Dialog finalDialog = dialog;
                btnNoInternet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isNetworkConnected()) {

                            showAlertNoInternet(context);

                        } else {

                            if (finalDialog != null) {
                                finalDialog.dismiss();
                                onResume();
                            }
                        }


                    }
                });
            }


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        appVersionApiHit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Permissive.Request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withRationale(new Rationale() {
                            @Override
                            public void onShowRationale(Activity activity, String[] allowablePermissions, PermissiveMessenger messenger) {
                                SingleButtonAlert.showAlertGps(SplashActivity.this, "Please make sure you have granted all permission to acess Contacts, location and send sms etc.", "Ok", new SingleButtonAlert.OnAlertOkClickListener() {
                                    @Override
                                    public void onOkButtonClicked() {
                                        finish();
//                                        messenger.cancelPermissionsRequest();
                                    }
                                });
                            }
                        })
                        .whenPermissionsGranted(new PermissionsGrantedListener() {
                            @Override
                            public void onPermissionsGranted(String[] permissions) throws SecurityException {
                                if (accessToken.equalsIgnoreCase("") && (Prefs.with(SplashActivity.this).getInt("activity_open", 0) != 1)) {
                                    Prefs.with(SplashActivity.this).save("activity_open", 1);
                                    // startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                                    startActivity(new Intent(SplashActivity.this, SplashNewActivity.class));
                                    overridePendingTransition(0, 0);
                                    finish();
                                } else{
                                    if(!Prefs.with(SplashActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "").equalsIgnoreCase("")){
                                        callingAccessTokenLoginApi();
                                    }
                                }
                            }
                        })
                        .whenPermissionsRefused(new PermissionsRefusedListener() {
                            @Override
                            public void onPermissionsRefused(String[] permissions) {
                                finish();
                            }
                        })
                        .execute(SplashActivity.this);

            }
        }, 3000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
