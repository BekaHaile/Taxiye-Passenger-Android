package com.fugu.apis;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.fugu.BuildConfig;
import com.fugu.CaptureUserData;
import com.fugu.FuguConfig;
import com.fugu.FuguNotificationConfig;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguDeviceDetails;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;
import com.fugu.utils.UniqueIMEIID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ankit on 07/09/17.
 */

public class ApiPutUserDetails implements FuguAppConstant {

    public Activity activity;
    private Callback callback;
    private CaptureUserData userData = FuguConfig.getInstance().getUserData();

    public ApiPutUserDetails(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void sendUserDetails(String resellerToken, int referenceId) {
        FuguLog.v("inside sendUserDetails", "inside sendUserDetails");
        Gson gson = new GsonBuilder().create();
        JsonObject deviceDetailsJson = null;
        try {
            deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(getAppVersion()).getDeviceDetails()).getAsJsonObject();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> commonParamsMAp = new HashMap<>();
        if (resellerToken != null) {
            commonParamsMAp.put(RESELLER_TOKEN, resellerToken);
            commonParamsMAp.put(REFERENCE_ID, String.valueOf(referenceId));
        } else {
            commonParamsMAp.put(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey());
        }
        commonParamsMAp.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(activity));
        commonParamsMAp.put(APP_TYPE, FuguConfig.getInstance().getAppType());
        commonParamsMAp.put(DEVICE_TYPE, ANDROID_USER);
        commonParamsMAp.put(APP_VERSION, BuildConfig.VERSION_NAME);
        commonParamsMAp.put(DEVICE_DETAILS, deviceDetailsJson);
        if (userData != null) {
            if (!userData.getUserUniqueKey().trim().isEmpty())
                commonParamsMAp.put(USER_UNIQUE_KEY, userData.getUserUniqueKey());

            if (!userData.getFullName().trim().isEmpty())
                commonParamsMAp.put(FULL_NAME, userData.getFullName());

            if (!userData.getEmail().trim().isEmpty())
                commonParamsMAp.put(EMAIL, userData.getEmail());

            if (!userData.getPhoneNumber().trim().isEmpty())
                commonParamsMAp.put(PHONE_NUMBER, userData.getPhoneNumber());

            JSONObject attJson = new JSONObject();
            JSONObject addressJson = new JSONObject();
            try {
                if (!userData.getAddressLine1().trim().isEmpty()) {
                    addressJson.put("address_line1", userData.getAddressLine1());
                }
                if (!userData.getAddressLine2().trim().isEmpty()) {
                    addressJson.put("address_line2", userData.getAddressLine2());
                }
                if (!userData.getCity().trim().isEmpty()) {
                    addressJson.put("city", userData.getCity());
                }
                if (!userData.getRegion().trim().isEmpty()) {
                    addressJson.put("region", userData.getRegion());
                }
                if (!userData.getCountry().trim().isEmpty()) {
                    addressJson.put("country", userData.getCountry());
                }
                if (!userData.getZipCode().trim().isEmpty()) {
                    addressJson.put("zip_code", userData.getZipCode());
                }
                if (userData.getLatitude() != 0 && userData.getLongitude() != 0) {
                    attJson.put(LAT_LONG, String.valueOf(userData.getLatitude() + "," + userData.getLongitude()));
                }
                attJson.put("ip_address", CommonData.getLocalIpAddress());
                attJson.put(ADDRESS, addressJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commonParamsMAp.put(ATTRIBUTES, attJson);
            commonParamsMAp.put(CUSTOM_ATTRIBUTES, new JSONObject(userData.getCustom_attributes()));
        }


        if (!FuguNotificationConfig.fuguDeviceToken.isEmpty()) {
            commonParamsMAp.put(DEVICE_TOKEN, FuguNotificationConfig.fuguDeviceToken);
        }

        FuguLog.e("Fugu Config sendUserDetails maps", "==" + commonParamsMAp.toString());
        if (resellerToken != null) {
            apiPutUserDetailReseller(commonParamsMAp);
        } else {
            apiPutUserDetail(commonParamsMAp);
        }
    }

    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
        RestClient.getApiInterface().putUserDetails(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {

                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());

                        FuguConfig.getInstance().userData.setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        FuguConfig.getInstance().userData.setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                        FuguLog.e("en_user_id",fuguPutUserDetailsResponse.getData().getEn_user_id());

                        callback.onSuccess();
                    }

                    @Override
                    public void failure(APIError error) {
                        callback.onFailure();
                    }
                });
    }

    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
        RestClient.getApiInterface().putUserDetailsReseller(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, false, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        FuguConfig.getInstance().userData.setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        FuguConfig.getInstance().userData.setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                        FuguLog.e("en_user_id",fuguPutUserDetailsResponse.getData().getEn_user_id());
                        if (fuguPutUserDetailsResponse.getData().getAppSecretKey() != null && !TextUtils.isEmpty(fuguPutUserDetailsResponse.getData().getAppSecretKey())) {
                            FuguConfig.getInstance().appKey = fuguPutUserDetailsResponse.getData().getAppSecretKey();
                            CommonData.setAppSecretKey(fuguPutUserDetailsResponse.getData().getAppSecretKey());
                        }
                        callback.onSuccess();
                    }

                    @Override
                    public void failure(APIError error) {
                        callback.onFailure();
                    }
                });
    }

    private int getAppVersion() {
        try {
            return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public interface Callback {
        void onSuccess();

        void onFailure();
    }
}
