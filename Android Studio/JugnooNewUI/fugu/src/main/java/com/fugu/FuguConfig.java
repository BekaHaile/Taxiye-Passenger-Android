package com.fugu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.widget.Toast;

import com.fugu.activity.FuguBaseActivity;
import com.fugu.activity.FuguChannelsActivity;
import com.fugu.activity.FuguChatActivity;
import com.fugu.apis.ApiPutUserDetails;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.datastructure.ChatType;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguCreateConversationParams;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.CommonResponse;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.CustomAlertDialog;
import com.fugu.utils.FuguLog;
import com.fugu.utils.StringUtil;
import com.fugu.utils.UniqueIMEIID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import faye.FayeClient;
import faye.MetaMessage;
import io.paperdb.Paper;

import static com.fugu.retrofit.RestClient.retrofit;

/**
 * Created by Bhavya Rattan on 08/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguConfig extends FuguBaseActivity implements Parcelable {

    private String TAG = "FuguConfig";
    private static FuguConfig fuguConfig;
    HashMap<String, Object> commonParamsMAp;
    //private String serverUrl = "https://beta-api.fuguchat.com:3001"; //test
    //private String serverUrl = "https://alpha-api.fuguchat.com:3000"; // dev
    private String serverUrl = "";
    private String themeColor = "";
    private int homeUpIndicatorDrawableId = R.drawable.fugu_ic_arrow_back;
    public String appKey = "";
    private int appType = 1;
    private int READ_PHONE_PERMISSION = 101;
    private static String mResellerToken;
    private static int mReferenceId;

    public CaptureUserData userData;
    protected Context context;
    private Activity activity;
    private boolean isDataCleared = true;

    // Initial Meta FuguMessage
    private static MetaMessage meta = new MetaMessage();
    // Initinal FayeClient
    private static FayeClient mClient;

    public static FayeClient getClient() {
        if (mClient == null) {
            meta = new MetaMessage();
            JSONObject jsonExt = new JSONObject();
            try {
                if (FuguConfig.getInstance().getUserData() != null) {
                    jsonExt.put("user_id", FuguConfig.getInstance().getUserData().getUserId());
                    jsonExt.put("device_type", 1);
                    jsonExt.put("source", 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            meta.setAllExt(jsonExt.toString());
            if (CommonData.getServerUrl().equals(LIVE_SERVER)) {
                mClient = new FayeClient(CommonData.getServerUrl() + ":3002/faye", meta);
            } else if (CommonData.getServerUrl().equals(TEST_SERVER) || CommonData.getServerUrl().equals(BETA_LIVE_SERVER)) {
                mClient = new FayeClient(CommonData.getServerUrl() + ":3001/faye", meta);
//                mClient = new FayeClient("https://beta-api.fuguchat.com:3017/faye", meta);
            }
        }
        return mClient;
    }

    public int getTargetSDKVersion() {
        return targetSDKVersion;
    }

    private int targetSDKVersion = 0;

    private FuguConfig() {
    }

    public void setColorConfig(FuguColorConfig fuguColorConfig) {
        CommonData.setColorConfig(fuguColorConfig);
    }

    private void setFuguConfig(@NonNull int appType, @NonNull String appKey, Activity activity, String environment, CaptureUserData userData, String resellerToken, int referenceId) {
        //FuguConfig.getInstance().serverUrl = serverUrl;
        //if (!FuguConfig.getInstance().serverUrl.isEmpty())
        FuguLog.v("inside setFuguConfig", "inside setFuguConfig");
        retrofit = null;

        if (environment != null && environment.equalsIgnoreCase("test")) {
            FuguConfig.getInstance().serverUrl = TEST_SERVER;
            CommonData.setServerUrl(TEST_SERVER);
        } else if (environment != null && environment.equalsIgnoreCase("beta-live")) {
            FuguConfig.getInstance().serverUrl = BETA_LIVE_SERVER; //test server
            CommonData.setServerUrl(BETA_LIVE_SERVER);
        } else {
            FuguConfig.getInstance().serverUrl = LIVE_SERVER; // live server
            CommonData.setServerUrl(LIVE_SERVER);
        }

        FuguConfig.getInstance().appKey = appKey;
        FuguConfig.getInstance().appType = appType;
        CommonData.setAppSecretKey(appKey);
        CommonData.setAppType(appType);

        if (userData != null) {
            FuguLog.v("userData not null", "userData not null");
            updateUserDetails(activity, userData, resellerToken, referenceId);
        } else {
            FuguLog.v("userData null", "userData null");
            registerAnonymousUser(activity);
        }

    }

    /**
     * Method to capture the instance of Fugu
     *
     * @return
     */
    public static FuguConfig getInstance() {
        if (fuguConfig == null)
            fuguConfig = new FuguConfig();
        return fuguConfig;
    }


    public static void init(int appType, String appKey, Activity activity, String environment, String provider) {
        init(appType, appKey, activity, environment, null, provider);
    }

    public static void init(int appType, String appKey, Activity activity, CaptureUserData userData, String provider) {
        init(appType, appKey, activity, null, userData, provider);
    }

    public static void init(int appType, String appKey, Activity activity, String provider) {
        init(appType, appKey, activity, null, null, provider);
    }

    public static void initReseller(String resellerToken, int referenceId, int appType, Activity activity, String environment, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, environment, null, provider);
    }

    public static void initReseller(String resellerToken, int referenceId, int appType, Activity activity, CaptureUserData userData, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, null, userData, provider);
    }

    public static void initReseller(String resellerToken, int referenceId, int appType, Activity activity, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, null, null, provider);
    }

    public static FuguConfig init(int appType, String appKey, final Activity activity, String environment, CaptureUserData userData, String provider) {
        Paper.init(activity);
        if (!CommonData.getClearFuguDataKey()) {
            try {
                CommonData.clearData();
                CommonData.setClearFuguDataKey(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fuguConfig = new FuguConfig();
        fuguConfig.setFuguConfig(appType, appKey, activity, environment, userData, null, 0);
        if (TextUtils.isEmpty(provider)) {
            new CustomAlertDialog.Builder(activity)
                    .setMessage("Provider cannot be null")
                    .setPositiveButton("Ok", new CustomAlertDialog.CustomDialogInterface.OnClickListener() {
                        @Override
                        public void onClick() {
                            activity.finish();
                        }
                    })
                    .show();
        } else {
            CommonData.setProvider(provider);
        }
        return fuguConfig;
    }

    public static FuguConfig initReseller(String resellerToken, int referenceId, int appType, final Activity activity, String environment, CaptureUserData userData, String provider) {
        FuguLog.v("inside initReseller", "initReseller");
        Paper.init(activity);
        fuguConfig = new FuguConfig();
        mResellerToken = resellerToken;
        mReferenceId = referenceId;
        fuguConfig.setFuguConfig(appType, "", activity, environment, userData, resellerToken, referenceId);
        if (TextUtils.isEmpty(provider)) {
            new CustomAlertDialog.Builder(activity)
                    .setMessage("Provider cannot be null")
                    .setPositiveButton("Ok", new CustomAlertDialog.CustomDialogInterface.OnClickListener() {
                        @Override
                        public void onClick() {
                            activity.finish();
                        }
                    })
                    .show();
        } else {
            CommonData.setProvider(provider);
        }
        return fuguConfig;
    }

    public void setHomeUpIndicatorDrawableId(int homeUpIndicatorDrawableId) {
        FuguConfig.getInstance().homeUpIndicatorDrawableId = homeUpIndicatorDrawableId;
    }

    public void showConversations(final Activity activity, final String title) {
        if (CommonData.getConversationList() != null && CommonData.getConversationList().size() <= 0) {
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    if (CommonData.getConversationList().size() == 0) {
                        caseOne(title);
                    } else if (CommonData.getConversationList() != null && CommonData.getConversationList().size() == 1) {
                        FuguLog.e("Case 2", "case 2");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                caseTwo(title);
                            }
                        }, 1000);
                    } else {
                        caseElse(title);
                    }
                }

                @Override
                public void onFailure() {

                }
            }).sendUserDetails(FuguConfig.getmResellerToken(), FuguConfig.getmReferenceId());
        } else if (CommonData.getConversationList() != null && CommonData.getConversationList().size() == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    caseTwo(title);
                }
            }, 1000);
        } else {
            caseElse(title);
        }
    }

    private void caseOne(String title) {
        FuguLog.e("Case 1", "case 1");
        Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
        FuguConversation conversation = new FuguConversation();
        conversation.setBusinessName(title);
        conversation.setOpenChat(true);
        conversation.setUserName(StringUtil.toCamelCase(FuguConfig.getInstance().getUserData().getFullName()));
        conversation.setUserId(FuguConfig.getInstance().getUserData().getUserId());
        conversation.setEnUserId(FuguConfig.getInstance().getUserData().getEnUserId());
        chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
        activity.startActivity(chatIntent);
    }

    private void caseElse(String title) {
        FuguLog.e("Case else", "case else");
        Intent conversationsIntent = new Intent(activity.getApplicationContext(), FuguChannelsActivity.class);
        conversationsIntent.putExtra("userData", userData);
        conversationsIntent.putExtra("title", title);
        conversationsIntent.putExtra("appVersion", getAppVersion());
        activity.startActivity(conversationsIntent);
    }

    private void caseTwo(String title) {
        FuguLog.e("Case 2", "case 2");
        Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
        FuguConversation conversation = new FuguConversation();
        conversation.setLabelId(CommonData.getConversationList().get(0).getLabelId());
        conversation.setLabel(CommonData.getConversationList().get(0).getLabel());
        conversation.setChannelId(CommonData.getConversationList().get(0).getChannelId());
        conversation.setBusinessName(title);
        conversation.setOpenChat(true);
        conversation.setUserName(StringUtil.toCamelCase(FuguConfig.getInstance().getUserData().getFullName()));
        conversation.setUserId(FuguConfig.getInstance().getUserData().getUserId());
        conversation.setEnUserId(FuguConfig.getInstance().getUserData().getEnUserId());
        conversation.setStatus(CommonData.getConversationList().get(0).getStatus());
        chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
        activity.startActivity(chatIntent);
    }


    public void openChat(Activity activity, Long messageChannelId) {
        FuguLog.v("In openChat", "userData --->" + FuguConfig.getInstance().getUserData());
        openChat(activity, messageChannelId, null);
    }

    public void openChat(final Activity activity, final Long messageChannelId, final String titleString) {

        if (FuguConfig.getInstance().getUserData() == null || userData.getUserId().compareTo(-1l) == 0) {
            FuguLog.v("In openChat before FuguChatActivity", "userData null");
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
                    FuguConversation conversation = new FuguConversation();
                    conversation.setLabelId(messageChannelId);
                    conversation.setLabel(titleString);
                    conversation.setOpenChat(true);
                    conversation.setUserName(StringUtil.toCamelCase(FuguConfig.getInstance().getUserData().getFullName()));
                    conversation.setUserId(FuguConfig.getInstance().getUserData().getUserId());
                    conversation.setEnUserId(FuguConfig.getInstance().getUserData().getEnUserId());
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    activity.startActivity(chatIntent);
                }

                @Override
                public void onFailure() {

                }
            }).sendUserDetails(FuguConfig.getInstance().getmResellerToken(), FuguConfig.getInstance().getmReferenceId());
        } else {
            Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
            FuguConversation conversation = new FuguConversation();
            conversation.setLabelId(messageChannelId);
            conversation.setLabel(titleString);
            conversation.setOpenChat(true);
            conversation.setUserName(StringUtil.toCamelCase(FuguConfig.getInstance().getUserData().getFullName()));
            conversation.setUserId(FuguConfig.getInstance().getUserData().getUserId());
            conversation.setEnUserId(FuguConfig.getInstance().getUserData().getEnUserId());
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            activity.startActivity(chatIntent);
        }


    }

    /**
     * To send default message
     */
    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName, ArrayList<String> tags, String[] userMessage) {
        if (userMessage != null && userMessage.length > 0 && !TextUtils.isEmpty(userMessage[0])) {
            showGroupChat(transactionId, userUniqueKey, null, channelName, tags, ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), userMessage);
        } else {
            new CustomAlertDialog.Builder(context)
                    .setMessage("Message cannot be null")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName, ArrayList<String> tags) {
        showGroupChat(transactionId, userUniqueKey, null, channelName, tags, ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), null);
    }

    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName) {
        showGroupChat(transactionId, userUniqueKey, null, channelName, null, ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), null);
    }

    public void showGroupChat(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, null, null, ChatType.GROUP_CHAT.getOrdinal(), null);
    }

    public void showGroupChat(String transactionId, String userUniqueKey,
                              ArrayList<String> otherUserUniqueKeys, ArrayList<String> tags) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, null, tags, ChatType.GROUP_CHAT.getOrdinal(), null);
    }

    public void showGroupChat(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys,
                              String channelName) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, null, ChatType.GROUP_CHAT.getOrdinal(), null);
    }


    public void showGroupChat(final String transactionId, final String userUniqueKey, final ArrayList<String> otherUserUniqueKeys,
                              final String channelName, final ArrayList<String> tags, final int chatType, final String[] message) {
        FuguLog.i("showGroupChat", "In ShowGroupChat");
        if (FuguConfig.getInstance().getUserData() == null || userData.getUserId().compareTo(-1l) == 0) {
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    if (message != null && message.length > 0 && !TextUtils.isEmpty(message[0])) {
                        showGroupChats(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType, message);
                    } else {
                        new CustomAlertDialog.Builder(context)
                                .setMessage("Message Cannot be null")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                }

                @Override
                public void onFailure() {

                }
            });
        } else {
            showGroupChats(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType, message);
        }

    }

    private void showGroupChats(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys,
                                String channelName, ArrayList<String> tags, int chatType, String[] message) {
        FuguLog.i("showGroupChat", "userData not null");
        Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
        FuguLog.d("userName in SDK", "FuguConfig showGroupChat" + FuguConfig.getInstance().getUserData().getUserId());
        FuguConversation conversation = new FuguConversation();
        conversation.setLabelId(-1l);
        conversation.setLabel(CommonData.getUserDetails().getData().getBusinessName());
        conversation.setUserId(FuguConfig.getInstance().getUserData().getUserId());
        conversation.setEnUserId(FuguConfig.getInstance().getUserData().getEnUserId());
        conversation.setUserName(StringUtil.toCamelCase(FuguConfig.getInstance().getUserData().getFullName()));
        chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
        chatIntent.putExtra(CHAT_TYPE, chatType);

        Gson gson = new GsonBuilder().create();
        JsonArray otherUsersArray = null;
        JsonArray tagsArray = null;

        if (otherUserUniqueKeys != null) {
            otherUsersArray = gson.toJsonTree(otherUserUniqueKeys).getAsJsonArray();
        }

        if (tags != null) {
            tagsArray = gson.toJsonTree(tags).getAsJsonArray();
        }

        if (chatType == ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal()) {
            if (message != null && !TextUtils.isEmpty(message[0])) {
                FuguCreateConversationParams fuguPeerChatParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey()
                        , -1l, transactionId, FuguConfig.getInstance().getUserData().getUserId(), channelName, tagsArray, message,
                        FuguConfig.getInstance().getUserData().getEnUserId());
                chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));
            } else {
                FuguCreateConversationParams fuguPeerChatParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey()
                        , -1l, transactionId, FuguConfig.getInstance().getUserData().getUserId(), channelName, tagsArray,
                        FuguConfig.getInstance().getUserData().getEnUserId());
                chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));
            }
        } else {
            FuguCreateConversationParams fuguPeerChatParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey()
                    , -1l, transactionId, userUniqueKey, otherUsersArray, channelName, tagsArray,
                    FuguConfig.getInstance().getUserData().getEnUserId());
            chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));
        }

        activity.startActivity(chatIntent);
    }

    public void showPeerChat(String transactionId, String userUniqueKey, String otherUserUniqueKey) {

        showPeerChat(transactionId, userUniqueKey, otherUserUniqueKey, null, null);
    }

    public void showPeerChat(String transactionId, String userUniqueKey, String otherUserUniqueKey, String channelName) {

        showPeerChat(transactionId, userUniqueKey, otherUserUniqueKey, channelName, null);
    }


    public void showPeerChat(String transactionId, String userUniqueKey,
                             String otherUserUniqueKey, ArrayList<String> tags) {

        showPeerChat(transactionId, userUniqueKey, otherUserUniqueKey, null, tags);
    }

    public void showPeerChat(String transactionId, String userUniqueKey,
                             String otherUserUniqueKey, String channelName, ArrayList<String> tags) {

        ArrayList<String> otherUserUniqueKeys = new ArrayList<>();
        otherUserUniqueKeys.add(otherUserUniqueKey);

        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, ChatType.GROUP_CHAT.getOrdinal(), null);
    }

    public CaptureUserData getUserData() {
        return userData;
    }

    public Context getContext() {
        return context;
    }

    private int getAppVersion() {
        try {
            return FuguConfig.getInstance().context.getPackageManager().getPackageInfo(FuguConfig.getInstance().context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getServerUrl() {
        return FuguConfig.getInstance().serverUrl;
    }

    public int getHomeUpIndicatorDrawableId() {
        return FuguConfig.getInstance().homeUpIndicatorDrawableId;
    }

    public String getAppKey() {
        return FuguConfig.getInstance().appKey;
    }

    public static String getmResellerToken() {
        return mResellerToken;
    }

    public static int getmReferenceId() {
        return mReferenceId;
    }

    public int getAppType() {
        return FuguConfig.getInstance().appType;
    }

//    public String getThemeColor() {
//
//        if (themeColor.isEmpty() && !actionBarBackgroundColor.equalsIgnoreCase("#ffffff")) {
//            return actionBarBackgroundColor;
//        } else if (themeColor.isEmpty()) {
//            return THEME_COLOR_STRING;
//        }
//        return themeColor;
//    }

    private void registerAnonymousUser(Activity activity) {

        FuguConfig.getInstance().isDataCleared = false;
        FuguConfig.getInstance().activity = activity;
        FuguConfig.getInstance().context = activity;

        if (isPermissionGranted(FuguConfig.getInstance().context, Manifest.permission.READ_PHONE_STATE)) {
            FuguConfig.getInstance().userData = new CaptureUserData();
            sendUserDetails(activity, mResellerToken, mReferenceId);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_PERMISSION);
            }
        }
    }

    public void updateUserDetails(Activity activity, CaptureUserData userData, String resellerToken, int referenceId) {
        FuguConfig.getInstance().isDataCleared = false;
        FuguConfig.getInstance().activity = activity;
        FuguConfig.getInstance().context = activity;
        FuguConfig.getInstance().userData = userData;
        if (isPermissionGranted(FuguConfig.getInstance().context, Manifest.permission.READ_PHONE_STATE)) {
            FuguLog.v("permissionGranted", "permissionGranted");
            sendUserDetails(activity, resellerToken, referenceId);
        } else {
            FuguLog.v("permission not Granted", "permission not Granted");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_PERMISSION);
            } else {
                FuguLog.v("below M", "below M");
            }
        }
    }

    public static void clearFuguData(Activity activity) {
        FuguConfig.getInstance().isDataCleared = true;
        logOutUser(activity);
        try {
            CommonData.clearData();
        } catch (Exception e) {

        }
    }

    public boolean isDataCleared() {
        return isDataCleared;
    }


    private void sendUserDetails(Activity activity, String resellerToken, int referenceId) {
        // if (isNetworkAvailable()) {
        FuguLog.v("inside sendUserDetails", "inside sendUserDetails");

        commonParamsMAp = new HashMap<>();
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
        commonParamsMAp.put(DEVICE_DETAILS, CommonData.deviceDetails(activity));
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
            if (!userData.getCustom_attributes().isEmpty()) {
                commonParamsMAp.put(CUSTOM_ATTRIBUTES, new JSONObject(userData.getCustom_attributes()));
            }
        }

        if (!FuguNotificationConfig.fuguDeviceToken.isEmpty()) {
            commonParamsMAp.put(DEVICE_TOKEN, FuguNotificationConfig.fuguDeviceToken);
        }

        FuguLog.e("Fugu Config sendUserDetails map", "==" + commonParamsMAp.toString());
        if (resellerToken != null) {
            apiPutUserDetailReseller(commonParamsMAp);
        } else {
            apiPutUserDetail(commonParamsMAp);
        }
    }

    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
        RestClient.getApiInterface().putUserDetails(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, false, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {

                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());

                        FuguConfig.getInstance().userData.setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        FuguConfig.getInstance().userData.setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                    }

                    @Override
                    public void failure(APIError error) {
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

                        if (fuguPutUserDetailsResponse.getData().getAppSecretKey() != null && !TextUtils.isEmpty(fuguPutUserDetailsResponse.getData().getAppSecretKey())) {
                            FuguConfig.getInstance().appKey = fuguPutUserDetailsResponse.getData().getAppSecretKey();
                            CommonData.setAppSecretKey(fuguPutUserDetailsResponse.getData().getAppSecretKey());
                        }
                    }

                    @Override
                    public void failure(APIError error) {
                    }
                });
    }

    private static void logOutUser(Activity activity) {
        if (FuguConfig.getInstance().getUserData() != null) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                    .add(USER_ID, FuguConfig.getInstance().getUserData().getUserId())
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add(DEVICE_TYPE, 1)
                    .build();
            RestClient.getApiInterface().logOut(commonParams.getMap())
                    .enqueue(new ResponseResolver<CommonResponse>(activity, false, false) {
                        @Override
                        public void success(CommonResponse commonResponse) {

                        }

                        @Override
                        public void failure(APIError error) {

                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        FuguLog.e(getClass().getSimpleName(), "Permission callback called-------" + requestCode);

        if (requestCode == READ_PHONE_PERMISSION) {
            if (targetSDKVersion > 22 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendUserDetails(FuguConfig.getInstance().activity, mResellerToken, mReferenceId);
            } else if (targetSDKVersion <= 22 && grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                sendUserDetails(FuguConfig.getInstance().activity, mResellerToken, mReferenceId);
            } else {
                //ActivityCompat.shouldShowRequestPermissionRationale(FuguChannelsActivity.this, Manifest.permission.READ_PHONE_STATE);
                Toast.makeText(getApplicationContext(), "Go to Settings and grant permission to access phone state", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permission
     * @return
     */
    public boolean isPermissionGranted(Context activity, String permission) {

        PackageManager pm = activity.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(FuguConfig.getInstance().activity.getPackageName(), 0);
            if (applicationInfo != null) {
                targetSDKVersion = applicationInfo.targetSdkVersion;
            }
        } catch (Exception e) {

        }

        if (targetSDKVersion > 22) {
            return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return PermissionChecker.checkSelfPermission(activity, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
    }

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permission
     * @return
     */
    public boolean askUserToGrantPermission(Activity activity, String permission, String explanation, int code) {
        FuguLog.e(TAG, "permissions" + permission);
        return askUserToGrantPermission(activity, new String[]{permission}, explanation, code);
    }

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permissions
     * @param explanation
     * @param requestCode
     * @return
     */
    public boolean askUserToGrantPermission(Activity activity, String[] permissions, String explanation, int requestCode) {
        String permissionRequired = null;

        for (String permission : permissions)
            if (!isPermissionGranted(activity, permission)) {
                permissionRequired = permission;
                break;
            }

        // Check if the Permission is ALREADY GRANTED
        if (permissionRequired == null) return true;

        // Check if there is a need to show the PERMISSION DIALOG
        boolean explanationRequired = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequired);
        FuguLog.e(TAG, "askUserToGrantPermission: explanationRequired(" + explanationRequired + "): " + permissionRequired);

        // Convey the EXPLANATION if required
        if (explanationRequired) {

            if (explanation == null) explanation = "Please grant permission";
            Toast.makeText(activity, explanation, Toast.LENGTH_SHORT).show();
        } else {

            // We can request the permission, if no EXPLANATIONS required
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.serverUrl);
        dest.writeString(this.themeColor);
        dest.writeInt(this.homeUpIndicatorDrawableId);
        dest.writeString(this.appKey);
        dest.writeInt(this.appType);
        dest.writeInt(this.READ_PHONE_PERMISSION);
        dest.writeParcelable(this.userData, flags);
        dest.writeParcelable((Parcelable) this.context, flags);
        dest.writeByte(this.isDataCleared ? (byte) 1 : (byte) 0);
        dest.writeInt(this.targetSDKVersion);
    }

    protected FuguConfig(Parcel in) {
        this.serverUrl = in.readString();
        this.themeColor = in.readString();
        this.homeUpIndicatorDrawableId = in.readInt();
        this.appKey = in.readString();
        this.appType = in.readInt();
        this.READ_PHONE_PERMISSION = in.readInt();
        this.userData = in.readParcelable(CaptureUserData.class.getClassLoader());
        this.context = in.readParcelable(Context.class.getClassLoader());
        this.isDataCleared = in.readByte() != 0;
        this.targetSDKVersion = in.readInt();
    }

    public static final Creator<FuguConfig> CREATOR = new Creator<FuguConfig>() {
        @Override
        public FuguConfig createFromParcel(Parcel source) {
            return new FuguConfig(source);
        }

        @Override
        public FuguConfig[] newArray(int size) {
            return new FuguConfig[size];
        }
    };

}