package com.fugu;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fugu.activity.FuguBaseActivity;
import com.fugu.activity.FuguChannelsActivityNew;
import com.fugu.activity.FuguChatActivity;
import com.fugu.agent.AgentBroadcastActivity;
import com.fugu.agent.AgentListActivity;
import com.fugu.agent.Util.ConversationMode;
import com.fugu.agent.Util.SPLabels;
import com.fugu.agent.Util.permissionHelper.PermissionsManager;
import com.fugu.agent.Util.permissionHelper.PermissionsResultAction;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.listeners.AgentListener;
import com.fugu.agent.model.ApiResponseFlags;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.LoginResponse;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.apis.ApiPutUserDetails;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.datastructure.ChatType;
import com.fugu.model.CustomAttributes;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguCreateConversationParams;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.receiver.FuguNetworkStateReceiver;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.CommonResponse;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.support.HippoSupportActivity;
import com.fugu.utils.CustomAlertDialog;
import com.fugu.utils.FuguLog;
import com.fugu.utils.StringUtil;
import com.fugu.utils.UniqueIMEIID;
import com.fugu.utils.UnreadCountApi;
import com.fugu.utils.loadingBox.LoadingBox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

    private String TAG = FuguConfig.class.getName();
    private static FuguConfig fuguConfig;
    HashMap<String, Object> commonParamsMAp;
    //private String serverUrl = "https://beta-api.fuguchat.com:3001"; //test
    //private String serverUrl = "https://alpha-api.fuguchat.com:3000"; // dev
    private String serverUrl = "";
    private String agentServerUrl = "";
    private String themeColor = "";
    private int homeUpIndicatorDrawableId = R.drawable.fugu_ic_arrow_back;
    private int videoCallNotificationDrawable = R.drawable.hippo_default_notif_icon;
    private int videoCallDrawableId = -1;//R.drawable.hippo_ic_info;
    private int audioCallDrawableId = -1;
    public String appKey = "";
    private String appType = "1";
    private int READ_PHONE_PERMISSION = 101;
    private static String mResellerToken;
    private static int mReferenceId = -1;

    private CaptureUserData userData;
    private String agentAuthToken = "";
    protected Context context;
    private Activity activity;
    private boolean isDataCleared = true;
    private long lastClickTime = 0;

    private boolean isChannelActivity;
    private static boolean isUnreadRequired;
    private int chatInfoDrawable = -1;
    private int homeIconDrawable = -1;
    public static boolean DEBUG = false;
    private int broadcastDrawable = -1;
    public int getInfoIcon() {
        return chatInfoDrawable;
    }

    public void setInfoIcon(int chatInfoDrawable) {
        this.chatInfoDrawable = chatInfoDrawable;
    }

    public int getHomeIconDrawable() {
        return homeIconDrawable;
    }

    public void setHomeIconDrawable(int homeIconDrawable) {
        this.homeIconDrawable = homeIconDrawable;
    }

    public void setBroadcastDrawable(int broadcastDrawable) {
        this.broadcastDrawable = broadcastDrawable;
    }

    public int getBroadcastDrawable() {
        return broadcastDrawable;
    }

    public boolean isChannelActivity() {
        return isChannelActivity;
    }

    public void setChannelActivity(boolean channelActivity) {
        isChannelActivity = channelActivity;
    }

    public void setFontConfig(FuguFontConfig fuguFontConfig) {
        CommonData.setFontConfig(fuguFontConfig);
    }

    public void setNewPeerChatCreated(){
        CommonData.setNewPeerChatCreated(true);
    }


    // Initial Meta FuguMessage
    private static MetaMessage meta = new MetaMessage();
    // Initinal FayeClient
    private static FayeClient mClient;

    /**
     * @deprecated
     * @return
     */
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
                mClient = new FayeClient("https://hippo-api-dev.fuguchat.com:3012/faye", meta);
            } else if(CommonData.getServerUrl().equals(AGENT_BETA_SERVER)) {
                mClient = new FayeClient("https://beta-hippo.fuguchat.com:3001/faye", meta);
            } else {
                mClient = new FayeClient(LIVE_SERVER + ":3002/faye", meta);
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

    public void setStringConfig(FuguStringConfig fuguStringConfig) {
        CommonData.setStringConfig(fuguStringConfig);
    }
    private void setFuguConfig(@NonNull String appType, @NonNull String appKey, Activity activity,
                               String environment, CaptureUserData userData, String resellerToken, int referenceId) {
        //FuguConfig.getInstance().serverUrl = serverUrl;
        //if (!FuguConfig.getInstance().serverUrl.isEmpty())
        FuguLog.v("inside setFuguConfig", "inside setFuguConfig");
        retrofit = null;
        AgentCommonData.setAgentLoginInit(false);
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

        registerNetworkListener(activity);

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

    public static void setPushNotificationFlags(int flags) {
        CommonData.setPushFlags(flags);
    }

    public static void init(String appType, String appKey, Activity activity, String environment, String provider) {
        init(appType, appKey, activity, environment, null, provider);
    }

    public static void init(String appType, String appKey, Activity activity, CaptureUserData userData, String provider) {
        init(appType, appKey, activity, null, userData, provider);
    }

    public static void init(String appType, String appKey, Activity activity, String provider) {
        init(appType, appKey, activity, null, null, provider);
    }

    public static void initReseller(String resellerToken, int referenceId, String appType, Activity activity, String environment, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, environment, null, provider);
    }

    public static void initReseller(String resellerToken, int referenceId, String appType, Activity activity, CaptureUserData userData, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, null, userData, provider);
    }

    public static void initReseller(String resellerToken, int referenceId, String appType, Activity activity, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, null, null, provider);
    }

    public static void init(String appType, String appKey, final Activity activity, String environment, CaptureUserData userData, String provider) {
        init(appType, appKey, activity, environment, userData, provider, false);
    }

    public static FuguConfig init(String appType, String appKey, final Activity activity, String environment,
                                  CaptureUserData userData, String provider, boolean unreadCount) {
        isUnreadRequired = unreadCount;
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

    public static void initReseller(String resellerToken, int referenceId, String appType, final Activity activity,
                                    String environment, CaptureUserData userData, String provider) {
        initReseller(resellerToken, referenceId, appType, activity, environment, userData, provider, false);
    }

    public static FuguConfig initReseller(String resellerToken, int referenceId, String appType, final Activity activity,
                                          String environment, CaptureUserData userData, String provider, boolean unreadCount) {
        FuguLog.v("inside initReseller", "initReseller");
        isUnreadRequired = unreadCount;
        Paper.init(activity);
        fuguConfig = new FuguConfig();
        mResellerToken = resellerToken;
        mReferenceId = referenceId;
        CommonData.saveResellerData(resellerToken, referenceId);
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

    public void setCallNotificationDrawable(int videoCallNotificationDrawable) {
        FuguConfig.getInstance().videoCallNotificationDrawable = videoCallNotificationDrawable;
    }

    public void setVideoCallDrawableId(int videoCallDrawableId) {
        FuguConfig.getInstance().videoCallDrawableId = videoCallDrawableId;
    }

    public void setAudioCallDrawableId(int audioCallDrawableId) {
        FuguConfig.getInstance().audioCallDrawableId = audioCallDrawableId;
    }

    public void showConversations(final Activity activity, final String title) {
        if(AgentCommonData.isAgentFlow()) {
            Log.e(TAG, "Can't call this method with manager flow");
            return;
        }
        CommonData.setChatTitle(title);
        if (CommonData.isFirstTimeWithNotification() || (CommonData.getConversationList() != null && CommonData.getConversationList().size() <= 0)) {
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    CommonData.setNotificationFirstClick(false);
                    if (CommonData.getConversationList().size() == 0) {
                        caseOne(title);
                    } else {
                        caseElse(title);
                    }
                }

                @Override
                public void onFailure() {
                    CommonData.setNotificationFirstClick(false);
                }
            }).sendUserDetails(FuguConfig.getmResellerToken(), FuguConfig.getmReferenceId());
        } else {
            if (CommonData.getConversationList().size() == 0) {
                caseOne(title);
            } else {
                caseElse(title);
            }
        }
        /*else if (CommonData.getConversationList() != null && CommonData.getConversationList().size() == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    caseTwo(title);
                }
            }, 1000);
        } else {
            caseElse(title);
        }*/
    }

    /**
     * Open Support menu
     * @param FuguTicketAttributes
     */
    public void showFAQSupport(FuguTicketAttributes FuguTicketAttributes) {
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }
        if(FuguTicketAttributes != null) {
            openSupportScreen(FuguTicketAttributes.getmFaqName(), FuguTicketAttributes.getmTransactionId());
        } else {
            openSupportScreen(null, null);
        }
        lastClickTime = SystemClock.elapsedRealtime();
    }

    private void openSupportScreen(final String categoryId, final String transactionId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openFuguSupportActivity(categoryId, transactionId);
            }
        }, 100);
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
        Intent conversationsIntent = new Intent(activity.getApplicationContext(), FuguChannelsActivityNew.class);
        conversationsIntent.putExtra("userData", getUserData());
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

    private void openFuguSupportActivity(String faqName, String transactionId) {
        Intent intent = new Intent(activity.getApplicationContext(), HippoSupportActivity.class);
        intent.putExtra(FuguAppConstant.SUPPORT_ID, faqName);
        intent.putExtra(FuguAppConstant.SUPPORT_TRANSACTION_ID, transactionId);
        intent.putExtra("userData", getUserData());
        activity.startActivity(intent);
    }

    public void openChat(Activity activity, Long messageChannelId) {
        if(AgentCommonData.isAgentFlow()) {
            Log.e(TAG, "Can't call this method with manager flow");
            return;
        }
        FuguLog.v("In openChat", "userData --->" + FuguConfig.getInstance().getUserData());
        openChat(activity, messageChannelId, null);
    }

    public void openChat(final Activity activity, final Long messageChannelId, final String titleString) {
        if(AgentCommonData.isAgentFlow()) {
            Log.e(TAG, "Can't call this method with manager flow");
            return;
        }
        if (FuguConfig.getInstance().getUserData() == null || getUserData().getUserId().compareTo(-1l) == 0) {
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
            showGroupChat(transactionId, userUniqueKey, null, channelName, tags,
                    ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), userMessage, null, null, null);
        } else {
            new CustomAlertDialog.Builder(context)
                    .setMessage("Message cannot be null")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    /**
     * To create ticket for InAppSupport
     * @param transactionId
     * @param userUniqueKey
     * @param channelName
     * @param tags
     * @param userMessage
     * @param isSupportTicket
     * @param customAttributes
     */
    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName, ArrayList<String> tags,
                                        String[] userMessage, String isSupportTicket, CustomAttributes customAttributes) {
        showGroupChat(transactionId, userUniqueKey, null, channelName, tags,
                ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), userMessage, null, isSupportTicket, customAttributes);
    }

    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName,
                                        ArrayList<String> tags) {
        showGroupChat(transactionId, userUniqueKey, null, channelName, tags,
                ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), null, null, null);
    }

    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName,
                                        ArrayList<String> tags, ArrayList<String> groupingTags) {
        showGroupChat(transactionId, userUniqueKey, null, channelName, null,
                ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), null, groupingTags, null);
    }

    public void openChatByTransactionId(String transactionId, String userUniqueKey, String channelName) {
        showGroupChat(transactionId, userUniqueKey, null, channelName, null,
                ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal(), null, null, null);
    }

    public void showGroupChat(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, null,
                null, ChatType.GROUP_CHAT.getOrdinal(), null, null, null);
    }

    public void showGroupChat(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys, ArrayList<String> tags) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, null, tags, ChatType.GROUP_CHAT.getOrdinal(),
                null, null, null);
    }

    public void showGroupChat(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys, String channelName) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, null, ChatType.GROUP_CHAT.getOrdinal(),
                null, null, null);
    }


    public void showGroupChat(final String transactionId, final String userUniqueKey, final ArrayList<String> otherUserUniqueKeys,
                              final String channelName, final ArrayList<String> tags, final int chatType, final String[] message,
                              final ArrayList<String> groupingTags, final String isSupportTicket) {
        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType, message, groupingTags,
                isSupportTicket, null);
    }

    public void showGroupChat(final String transactionId, final String userUniqueKey, final ArrayList<String> otherUserUniqueKeys,
                              final String channelName, final ArrayList<String> tags, final int chatType, final String[] message,
            final ArrayList<String> groupingTags, final String isSupportTicket, final CustomAttributes customAttributes) {
        FuguLog.i("showGroupChat", "In ShowGroupChat");
        if (FuguConfig.getInstance().getUserData() == null || getUserData().getUserId().compareTo(-1l) == 0) {
            new ApiPutUserDetails(activity, new ApiPutUserDetails.Callback() {
                @Override
                public void onSuccess() {
                    if (message != null && message.length > 0 && !TextUtils.isEmpty(message[0])) {
                        showGroupChats(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType, message,
                                groupingTags, isSupportTicket, customAttributes);
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
            showGroupChats(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, chatType,
                    message, groupingTags, isSupportTicket, customAttributes);
        }

    }

    private void showGroupChats(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys,
                String channelName, ArrayList<String> tags, int chatType, String[] message,
                ArrayList<String> groupingTags, String isSupportTicket, CustomAttributes customAttributes) {
        FuguLog.i("showGroupChat", "userData not null");
        Intent chatIntent = new Intent(activity.getApplicationContext(), FuguChatActivity.class);
        FuguLog.d("userName in SDK", "HippoConfig showGroupChat" + FuguConfig.getInstance().getUserData().getUserId());
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

        FuguCreateConversationParams fuguPeerChatParams;
        if (chatType == ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal()) {
            if (message != null && !TextUtils.isEmpty(message[0])) {
                fuguPeerChatParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey()
                        , -1l, transactionId, FuguConfig.getInstance().getUserData().getUserId(), channelName, tagsArray, message,
                        FuguConfig.getInstance().getUserData().getEnUserId());
                if(groupingTags != null && groupingTags.size()>0) {
                    fuguPeerChatParams.setGroupingTags(groupingTags);
                }
                chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));
            } else {
                fuguPeerChatParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey(),-1l, transactionId,
                        FuguConfig.getInstance().getUserData().getUserId(), channelName, tagsArray, FuguConfig.getInstance().getUserData().getEnUserId());
                if(groupingTags != null && groupingTags.size()>0) {
                    fuguPeerChatParams.setGroupingTags(groupingTags);
                }
                chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));
            }
        } else {
            fuguPeerChatParams = new FuguCreateConversationParams(FuguConfig.getInstance().getAppKey()
                    , -1l, transactionId, userUniqueKey, otherUsersArray, channelName, tagsArray,
                    FuguConfig.getInstance().getUserData().getEnUserId());
            if(groupingTags != null && groupingTags.size()>0) {
                fuguPeerChatParams.setGroupingTags(groupingTags);
            }
            chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));
        }
        try {
            if(customAttributes != null)
                fuguPeerChatParams.setCustomAttributes(customAttributes);

            if(!TextUtils.isEmpty(isSupportTicket) && Integer.parseInt(isSupportTicket) == 1)
                fuguPeerChatParams.setIsSupportTicket(Integer.parseInt(isSupportTicket));

        } catch (Exception e) {
            e.printStackTrace();
        }
        chatIntent.putExtra(FuguAppConstant.PEER_CHAT_PARAMS, new Gson().toJson(fuguPeerChatParams, FuguCreateConversationParams.class));

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

        showGroupChat(transactionId, userUniqueKey, otherUserUniqueKeys, channelName, tags, ChatType.GROUP_CHAT.getOrdinal(),
                null, null, null);
    }

    public CaptureUserData getUserData() {
        if(userData == null)
            userData = CommonData.getUserData();
        return userData;
    }

    public Context getContext() {
        return context;
    }

    private int getAppVersion() {
        try {
            return FuguConfig.getInstance().context.getPackageManager().getPackageInfo(FuguConfig.getInstance()
                    .context.getPackageName(), 0).versionCode;
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

    public int getVideoCallNotificationDrawable() {
        return FuguConfig.getInstance().videoCallNotificationDrawable;
    }

    public int getVideoCallDrawableId() {
        return FuguConfig.getInstance().videoCallDrawableId;
    }

    public int getAudioCallDrawableId() {
        return FuguConfig.getInstance().audioCallDrawableId;
    }

    public String getAppKey() {
        return FuguConfig.getInstance().appKey;
    }

    public static String getmResellerToken() {
        if(TextUtils.isEmpty(mResellerToken))
            mResellerToken = CommonData.getResellerToken();
        return mResellerToken;
    }

    public static int getmReferenceId() {
        if(mReferenceId == -1)
            mReferenceId = CommonData.getReferenceId();
        return mReferenceId;
    }

    public String getAppType() {
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
        CommonData.setIsDataCleared(false);
        FuguConfig.getInstance().activity = activity;
        FuguConfig.getInstance().context = activity;

        FuguConfig.getInstance().userData = new CaptureUserData();
        CommonData.saveUserData(FuguConfig.getInstance().userData);
        sendUserDetails(activity, mResellerToken, mReferenceId);

//        if (isPermissionGranted(FuguConfig.getInstance().context, Manifest.permission.READ_PHONE_STATE)) {
//            FuguConfig.getInstance().userData = new CaptureUserData();
//            CommonData.saveUserData(FuguConfig.getInstance().userData);
//            sendUserDetails(activity, mResellerToken, mReferenceId);
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_PERMISSION);
//            }
//        }
    }

    public void updateUserDetails(Activity activity, CaptureUserData userData, String resellerToken, int referenceId) {
        FuguConfig.getInstance().isDataCleared = false;
        CommonData.setIsDataCleared(false);
        FuguConfig.getInstance().activity = activity;
        FuguConfig.getInstance().context = activity;
        FuguConfig.getInstance().userData = userData;
        CommonData.saveUserData(userData);

        sendUserDetails(activity, resellerToken, referenceId);

//        if (isPermissionGranted(FuguConfig.getInstance().context, Manifest.permission.READ_PHONE_STATE)) {
//            FuguLog.v("permissionGranted", "permissionGranted");
//            sendUserDetails(activity, resellerToken, referenceId);
//        } else {
//            FuguLog.v("permission not Granted", "permission not Granted");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_PERMISSION);
//            } else {
//                FuguLog.v("below M", "below M");
//            }
//        }
    }

    public static void clearFuguData(Activity activity) {
        if (AgentCommonData.isAgentFlow()) {
            logoutAgent(activity);
        } else {
            logOutUser(activity);
        }
        fuguConfig.clearLocalData();
    }

    private void clearLocalData() {
        FuguConfig.getInstance().isDataCleared = true;
        CommonData.setIsDataCleared(true);
        try {
            CommonData.clearData();
            AgentCommonData.clearAgentData();
            userData = null;
        } catch (Exception e) {

        }
    }

    public boolean isDataCleared() {
        return isDataCleared;
        //return CommonData.getIsDataCleared();
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
        commonParamsMAp.put(APP_VERSION_CODE, BuildConfig.VERSION_CODE);
        commonParamsMAp.put(DEVICE_DETAILS, CommonData.deviceDetails(activity));
        if (getUserData() != null) {
            if (!getUserData().getUserUniqueKey().trim().isEmpty())
                commonParamsMAp.put(USER_UNIQUE_KEY, getUserData().getUserUniqueKey());

            if (!getUserData().getFullName().trim().isEmpty())
                commonParamsMAp.put(FULL_NAME, getUserData().getFullName());

            if (!getUserData().getEmail().trim().isEmpty())
                commonParamsMAp.put(EMAIL, getUserData().getEmail());

            if (!getUserData().getPhoneNumber().trim().isEmpty())
                commonParamsMAp.put(PHONE_NUMBER, getUserData().getPhoneNumber());

            JSONObject attJson = new JSONObject();
            JSONObject addressJson = new JSONObject();
            try {
                if (!getUserData().getAddressLine1().trim().isEmpty()) {
                    addressJson.put("address_line1", getUserData().getAddressLine1());
                }
                if (!getUserData().getAddressLine2().trim().isEmpty()) {
                    addressJson.put("address_line2", getUserData().getAddressLine2());
                }
                if (!getUserData().getCity().trim().isEmpty()) {
                    addressJson.put("city", getUserData().getCity());
                }
                if (!getUserData().getRegion().trim().isEmpty()) {
                    addressJson.put("region", getUserData().getRegion());
                }
                if (!getUserData().getCountry().trim().isEmpty()) {
                    addressJson.put("country", getUserData().getCountry());
                }
                if (!getUserData().getZipCode().trim().isEmpty()) {
                    addressJson.put("zip_code", getUserData().getZipCode());
                }
                if (getUserData().getLatitude() != 0 && getUserData().getLongitude() != 0) {
                    attJson.put(LAT_LONG, String.valueOf(getUserData().getLatitude() + "," + getUserData().getLongitude()));
                }
                attJson.put("ip_address", CommonData.getLocalIpAddress());
                attJson.put(ADDRESS, addressJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commonParamsMAp.put(ATTRIBUTES, attJson);
            if (!getUserData().getCustom_attributes().isEmpty()) {
                commonParamsMAp.put(CUSTOM_ATTRIBUTES, new JSONObject(getUserData().getCustom_attributes()));
            }
        }

        if (!FuguNotificationConfig.fuguDeviceToken.isEmpty()) {
            commonParamsMAp.put(DEVICE_TOKEN, FuguNotificationConfig.fuguDeviceToken);
        }

        if (!getUserData().getTags().isEmpty()) {
            ArrayList<GroupingTag> groupingTags = new ArrayList<>();
            for (GroupingTag tag : getUserData().getTags()) {
                GroupingTag groupingTag = new GroupingTag();
                if (!TextUtils.isEmpty(tag.getTagName()))
                    groupingTag.setTagName(tag.getTagName());
                if (tag.getTeamId() != null && tag.getTeamId()>0)
                    groupingTag.setTeamId(tag.getTeamId());

                if (!TextUtils.isEmpty(tag.getTagName()) || (tag.getTeamId() != null && tag.getTeamId()>0)) {
                    groupingTags.add(groupingTag);
                }
            }
            commonParamsMAp.put(GROUPING_TAGS, new Gson().toJson(groupingTags));
        } else {
            commonParamsMAp.put(GROUPING_TAGS, "[]");
        }

        FuguLog.e("Fugu Config sendUserDetails map", "==" + commonParamsMAp.toString());
        if (resellerToken != null) {
            apiPutUserDetailReseller(commonParamsMAp);
        } else {
            apiPutUserDetail(commonParamsMAp);
        }
    }

    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
        CommonParams params = new CommonParams.Builder()
                .putMap(commonParams)
                .build();
        RestClient.getApiInterface().putUserDetails(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, false, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {

                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        if(FuguConfig.getInstance().userData == null)
                            FuguConfig.getInstance().userData = getUserData();
                        FuguConfig.getInstance().userData.setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        FuguConfig.getInstance().userData.setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                        CommonData.saveUserData(FuguConfig.getInstance().userData);
                        if (isUnreadRequired)
                            getUnreadCount(activity, fuguPutUserDetailsResponse.getData().getEn_user_id());
                    }

                    @Override
                    public void failure(APIError error) {
                    }
                });
    }

    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
        CommonParams params = new CommonParams.Builder()
                .putMap(commonParams)
                .build();
        RestClient.getApiInterface().putUserDetailsReseller(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(activity, false, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        if(FuguConfig.getInstance().userData == null)
                            FuguConfig.getInstance().userData = getUserData();

                        FuguConfig.getInstance().userData.setUserId(fuguPutUserDetailsResponse.getData().getUserId());
                        FuguConfig.getInstance().userData.setEnUserId(fuguPutUserDetailsResponse.getData().getEn_user_id());
                        CommonData.saveUserData(FuguConfig.getInstance().userData);
                        if (fuguPutUserDetailsResponse.getData().getAppSecretKey() != null &&
                                !TextUtils.isEmpty(fuguPutUserDetailsResponse.getData().getAppSecretKey())) {
                            FuguConfig.getInstance().appKey = fuguPutUserDetailsResponse.getData().getAppSecretKey();
                            CommonData.setAppSecretKey(fuguPutUserDetailsResponse.getData().getAppSecretKey());
                        }

                        if (isUnreadRequired)
                            getUnreadCount(activity, fuguPutUserDetailsResponse.getData().getEn_user_id());
                    }

                    @Override
                    public void failure(APIError error) {
                    }
                });
    }

    private static void logoutAgent(Activity activity) {
        if (AgentCommonData.getUserData() != null) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(FuguAppConstant.ACCESS_TOKEN, AgentCommonData.getUserData().getAccessToken())
                    .add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(activity))
                    .add(DEVICE_TYPE, ANDROID_USER)
                    .add(APP_VERSION, BuildConfig.VERSION_CODE)
                    .add(APP_SOURCE_TYPE, String.valueOf(1))
                    .build();

            RestClient.getAgentApiInterface().logout(commonParams.getMap())
                    .enqueue(new ResponseResolver<LoginResponse>() {
                        @Override
                        public void success(LoginResponse loginResponse) {

                        }

                        @Override
                        public void failure(APIError error) {

                        }
                    });
        }
    }

    private static void logOutUser(Activity activity) {
        if (FuguConfig.getInstance().getUserData() != null) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                    .add(EN_USER_ID, FuguConfig.getInstance().getUserData().getEnUserId())
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add("device_id", UniqueIMEIID.getUniqueIMEIId(activity))
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
        dest.writeInt(this.chatInfoDrawable);
        dest.writeString(this.appKey);
        dest.writeString(this.appType);
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
        this.chatInfoDrawable = in.readInt();
        this.appKey = in.readString();
        this.appType = in.readString();
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

    private UnreadCount callbackListener;
    private AgentUnreadCountListener countListener;

    public UnreadCount getCallbackListener() {
        return callbackListener;
    }

    public AgentUnreadCountListener getAgentCountListener() {
        return countListener;
    }

    public void setCountForCallbackListener(AgentUnreadCountListener countListener) {
        this.countListener = countListener;
    }

    public void setCallbackListener(UnreadCount callbackListener) {
        this.callbackListener = callbackListener;
    }

    public void getUnreadCount() {
        getUnreadCount(activity, FuguConfig.getInstance().getUserData().getEnUserId());
    }

    private void getUnreadCount(Activity activity, String enUserId) {
        new UnreadCountApi().getConversations(activity, enUserId, new UnreadCountApi.CountUnread() {
            @Override
            public void countValue(int count) {
                if (FuguConfig.getInstance().getCallbackListener() != null) {
                    FuguConfig.getInstance().getCallbackListener().count(count);
                }
            }
        });
    }

    // For Agent sdk flow;


    private boolean openAgentScreen;
    private boolean apiInProgress;
    private String chatTitle = "Chat";
    private Queue<String> objectQueue = new LinkedList();

    public void openChatConversation(Activity activity, String title) {
        if (TextUtils.isEmpty(chatTitle) || !TextUtils.isEmpty(title)) {
            this.chatTitle = title;
            AgentCommonData.setMainTitle(title);
        }
        if (AgentCommonData.getUserData() == null || apiInProgress) {
            objectQueue.add("open_agent_screen");
            LoadingBox.showOn(activity);
            if (!apiInProgress) { // Check apiInProgress set false before opening class
                updateAgentDetails(activity, fuguConfig.agentAuthToken);
            }
        } else {
            Intent intent = new Intent(activity.getApplicationContext(), AgentListActivity.class);
            intent.putExtra("title", chatTitle);
            activity.startActivity(intent);
        }
    }

    public static void initManager(String accessToken, final Activity activity, String appType, String provider, String packageName) {
        initManager(accessToken, activity, appType, null, "live", provider, packageName);
    }

//    public static void initManager(String accessToken, final Activity activity, String appType,
//                                   HashMap<String, Object> customAttributes, String provider, String packageName) {
//        initManager(accessToken, activity, appType, customAttributes, "live", provider, packageName);
//    }

    public static void updateAgentDetails(final Activity activity, String authToken) {
        updateAgentDetails(activity, authToken, null);
    }

    public void reversePager(boolean orderFlag) {
        AgentCommonData.setPagerOrder(orderFlag);
    }

    public static void updateAgentDetails(final Activity activity, String authToken, HashMap<String, Object> customAttributes) {
        if (fuguConfig == null)
            fuguConfig = new FuguConfig();
        FuguConfig.getInstance().activity = activity;
        FuguConfig.getInstance().context = activity;
        FuguConfig.getInstance().isDataCleared = false;
        CommonParams commonParams1 = new CommonParams.Builder()
                .add(FuguAppConstant.AUTH_TOKEN, authToken)
                .build();
        fuguConfig.agentTokenVerfy(activity, commonParams1.getMap(), customAttributes);
    }

    public static void initPaper(Context context) {
        Paper.init(context);
    }

    /**
     * @param authToken
     * @param activity
     * @param appType
     * @param customAttributes
     * @param environment
     * @param provider
     * @return
     */
    public static FuguConfig initManager(String authToken, final Activity activity, final String appType,
                                       HashMap<String, Object> customAttributes, String environment,
                                         String provider, String packageName) {
        FuguLog.v("inside initAgent", "initAgent");
        Paper.init(activity);
//        isUnreadRequired = unreadCount;

        fuguConfig = new FuguConfig();
        fuguConfig.agentListeners = new HashMap<>();

        if(TextUtils.isEmpty(packageName.trim())) {
            new CustomAlertDialog.Builder(activity)
                    .setMessage("Package name can't be empty")
                    .setPositiveButton("Ok", new CustomAlertDialog.CustomDialogInterface.OnClickListener() {
                        @Override
                        public void onClick() {
                            activity.finish();
                        }
                    })
                    .show();
            return fuguConfig;
        }
        AgentCommonData.savePackageName(packageName);


        if (TextUtils.isEmpty(authToken)) {
            new CustomAlertDialog.Builder(activity)
                    .setMessage("Token cannot be null")
                    .setPositiveButton("Ok", new CustomAlertDialog.CustomDialogInterface.OnClickListener() {
                        @Override
                        public void onClick() {
                            activity.finish();
                        }
                    })
                    .show();
        } else if (TextUtils.isEmpty(provider)) {
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

            FuguConfig.getInstance().activity = activity;
            FuguConfig.getInstance().context = activity;
            fuguConfig.agentAuthToken = authToken;
            fuguConfig.setFuguAgentConfig(authToken, activity, appType, customAttributes, environment);
            //fuguConfig.checkAgentPermissionCheck(authToken, activity, appType, environment);
        }
        return fuguConfig;
    }


    private void checkAgentPermissionCheck(String authToken, final Activity activity, final String appType, String environment) {
        FuguConfig.getInstance().activity = activity;
        FuguConfig.getInstance().context = activity;

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity,
                new String[]{Manifest.permission.READ_PHONE_STATE}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });

    }

    private void setFuguAgentConfig(String authToken, Activity activity, String appType,
                                    HashMap<String, Object> customAttributes, String environment) {
        FuguLog.v("inside setFuguAgentConfig", "inside setFuguAgentConfig");
        retrofit = null;
        AgentCommonData.setAgentLoginInit(true);
        if (environment != null && environment.equalsIgnoreCase("test")) {
            FuguConfig.getInstance().agentServerUrl = AGENT_TEST_SERVER;
            CommonData.setServerUrl(AGENT_TEST_SERVER);
        } else if (environment != null && environment.equalsIgnoreCase("beta-live")) {
            FuguConfig.getInstance().agentServerUrl = AGENT_BETA_LIVE_SERVER; //test server
            CommonData.setServerUrl(AGENT_BETA_LIVE_SERVER);
        } else if (environment != null && environment.equalsIgnoreCase("beta-test")) {
            FuguConfig.getInstance().agentServerUrl = AGENT_BETA_SERVER; //test Beta server
            CommonData.setServerUrl(AGENT_BETA_SERVER);
        } else {
            FuguConfig.getInstance().agentServerUrl = AGENT_LIVE_SERVER; // live server
            CommonData.setServerUrl(AGENT_LIVE_SERVER);
        }

        registerNetworkListener(activity);

        CommonParams commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.AUTH_TOKEN, authToken)
                .add(FuguAppConstant.DEVICE_TYPE, 1)
                .add(FuguAppConstant.DEVICE_TOKEN, FuguNotificationConfig.fuguDeviceToken)
                .add("device_id", UniqueIMEIID.getUniqueIMEIId(activity))
                .add(DEVICE_DETAILS, CommonData.deviceDetails(activity))
                .add(APP_VERSION, BuildConfig.VERSION_CODE)
                .add(APP_SOURCE, String.valueOf(2))
                .add(APP_TYPE, appType)
                .build();
        agentTokenVerfy(activity, commonParams.getMap(), customAttributes);
    }

    private void agentTokenVerfy(final Activity activity, final Map<String, Object> stringMap, final HashMap<String, Object> customAttributes) {
        apiInProgress = true;
        RestClient.getAgentApiInterface().verifyAuthToken(stringMap)
                .enqueue(new ResponseResolver<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse) {
                        CommonParams.Builder builder = new CommonParams.Builder();
                        builder.add(FuguAppConstant.ACCESS_TOKEN, loginResponse.getUserData().getAccessToken());
                        builder.add(FuguAppConstant.DEVICE_TYPE, 1);
                        builder.add(FuguAppConstant.DEVICE_TOKEN, FuguNotificationConfig.fuguDeviceToken);
                        //builder.add(FuguAppConstant.APP_VERSION, 237);
                        builder.add("device_id", UniqueIMEIID.getUniqueIMEIId(activity));
                        builder.add(DEVICE_DETAILS, CommonData.deviceDetails(activity));
                        builder.add(APP_SOURCE, String.valueOf(2));
                        builder.add(APP_VERSION, BuildConfig.VERSION_CODE);
                        builder.add(APP_TYPE, stringMap.get(APP_TYPE));
                        if (!customAttributes.isEmpty()) {
                            builder.add(CUSTOM_ATTRIBUTES, new JSONObject(customAttributes));
                        }

                        CommonParams commonParams = builder.build();
                        agentTokenLogin(activity, commonParams.getMap());
                    }

                    @Override
                    public void failure(APIError error) {
                        LoadingBox.hide();
                        Log.e(TAG, "Token not verified: " + error.getMessage());
                        clearLocalData();
                        apiInProgress = false;
                    }
                });
    }


    private void agentTokenLogin(final Activity activity, Map<String, Object> stringMap) {
        RestClient.getAgentApiInterface().login(stringMap)
                .enqueue(new ResponseResolver<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse) {
                        try {
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == loginResponse.getStatusCode()) {

                                Paper.book().delete(SPLabels.USER_DATA);
                                Paper.book().delete(SPLabels.TAGS);
                                AgentCommonData.saveUserData(loginResponse.getUserData());
                                AgentCommonData.saveTags(loginResponse.getUserData().getTags());
                                apiInProgress = false;
                                FuguConfig.getInstance().isDataCleared = false;
                                checkSavedQueue();

                            } else {
                                Toast.makeText(activity, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        apiInProgress = false;
                        LoadingBox.hide();
                    }

                    @Override
                    public void failure(APIError error) {
                        apiInProgress = false;
                        clearLocalData();
                        Log.e(TAG, "Agent Login failed: " + error.getMessage());
                        LoadingBox.hide();
                        //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkSavedQueue() {
        for (String str : objectQueue) {
            switch (str) {
                case "open_agent_screen":
                    Intent intent = new Intent(activity.getApplicationContext(), AgentListActivity.class);
                    intent.putExtra("title", chatTitle);
                    activity.startActivity(intent);
                    break;
                case "get_agent_count":
                    UnreadCountHelper.getInstance().getUpdatedUnreadCount(strings, unreadCount);
                    break;
                case "start_conversation":
                    Intent intent1 = new Intent(activity.getApplicationContext(), AgentListActivity.class);
                    intent1.putExtra("title", title);
                    intent1.putExtra("user_unique_key", userUniqueKey);
                    activity.startActivity(intent1);
                    break;
                case "agent_total_count":
                    getTotalUnreadCount();
                    break;
                case "send_broadcast_message":
                    Intent intent2 = new Intent(activity.getApplicationContext(), AgentBroadcastActivity.class);
                    intent2.putExtra("title", title);
                    activity.startActivity(intent2);
                    break;
                default:

                    break;
            }
        }
        objectQueue.clear();
    }

    private void sendBroadcastMessage(Activity activity, String title) {
        if(AgentCommonData.isAgentFlow()) {
            if (AgentCommonData.getUserData() == null || apiInProgress) {
                this.title = title;
                objectQueue.add("send_broadcast_message");
                LoadingBox.showOn(activity);
                if (!apiInProgress) { // Check apiInProgress set false before opening class
                    updateAgentDetails(activity, fuguConfig.agentAuthToken);
                }
            } else {
                Intent intent = new Intent(activity.getApplicationContext(), AgentBroadcastActivity.class);
                intent.putExtra("title", title);
                activity.startActivity(intent);
            }
        } else {
            Log.e(TAG, "Can't call this method in Client flow");
        }
    }

    String title = "";
    String userUniqueKey = "";

    public void openConversationFor(Activity activity, String otherUserUniqueKey, String title) {
        if (AgentCommonData.getUserData() == null || apiInProgress) {
            this.title = title;
            this.userUniqueKey = otherUserUniqueKey;
            objectQueue.add("start_conversation");
            LoadingBox.showOn(activity);
            if (!apiInProgress) { // Check apiInProgress set false before opening class
                updateAgentDetails(activity, fuguConfig.agentAuthToken);
            }
        } else {
            Intent intent = new Intent(activity.getApplicationContext(), AgentListActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("user_unique_key", otherUserUniqueKey);
            activity.startActivity(intent);
        }
    }

    private ArrayList<String> strings;

    public void setUnreadCount(AgentUnreadCountListener unreadCount) {
        this.unreadCount = unreadCount;
    }

    private AgentUnreadCountListener unreadCount;

    public void getUnreadCountFor(ArrayList<String> strings) {
        AgentCommonData.setAgentUniqueKey(strings);
        if (AgentCommonData.getUserData() == null || apiInProgress) {
            this.strings = strings;
            objectQueue.add("get_agent_count");
            //LoadingBox.showOn(activity);
            if (!apiInProgress) { // Check apiInProgress set false before opening class
                updateAgentDetails(activity, fuguConfig.agentAuthToken);
            }
        } else {
            UnreadCountHelper.getInstance().getUpdatedUnreadCount(strings);
        }
    }

    public void getTotalUnreadCount() {
        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
            if (taskList.get(0).topActivity.getClassName().equals("com.fugu.agent.AgentChatActivity")) {
                UnreadCountHelper.getInstance().sendTotalUnreadCount();
                return;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        getTotalUnreadCountData();
    }


    private void getTotalUnreadCountData() {
        if (FuguConfig.getInstance().getCallbackListener() == null) {
            Log.e(TAG, "Set CallbacListener first");
            return;
        }
        if (AgentCommonData.getUserData() == null || apiInProgress) {
            objectQueue.add("agent_total_count");
            //LoadingBox.showOn(activity);
            if (!apiInProgress) { // Check apiInProgress set false before opening class
                updateAgentDetails(activity, fuguConfig.agentAuthToken);
            }
        } else {
            int[] typeIntArray = new int[]{ConversationMode.ALL.getOrdinal(), ConversationMode.DEFAULT.getOrdinal()};
            UserData userData = AgentCommonData.getUserData();
            String userID = String.valueOf(userData.getEnUserId());
            String accessToken = userData.getAccessToken();
            HashMap<String, Object> params = new HashMap<>();
            params.put(FuguAppConstant.EN_USER_ID, userID);
            params.put(FuguAppConstant.ACCESS_TOKEN, accessToken);
            params.put(FuguAppConstant.STATUS, "[1]");
            params.put(FuguAppConstant.DEVICE_TYPE, 1);
            params.put(FuguAppConstant.TYPE, Arrays.toString(typeIntArray));

            RestClient.getAgentApiInterface().getConversation(params).enqueue(new ResponseResolver<GetConversationResponse>() {
                @Override
                public void success(GetConversationResponse getConversationResponse) {
                    try {
                        if (getConversationResponse != null) { // Null handled in try catch
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == getConversationResponse.getStatusCode()) {
                                HashMap<Integer, Integer> hashMap = AgentCommonData.getTotalUnreadCount();
                                int totalCount = 0;
                                for (Conversation conversation : getConversationResponse.getData().getConversation()) {
                                    hashMap.put(conversation.getChannelId().intValue(), conversation.getUnreadCount());
                                    totalCount += conversation.getUnreadCount();
                                }
                                AgentCommonData.addTotalUnreadCount(hashMap);

                                UnreadCountHelper.getInstance().sendTotalUnreadCount();
                                if (FuguConfig.getInstance().getCallbackListener() != null) {
                                    FuguConfig.getInstance().getCallbackListener().count(totalCount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(APIError error) {

                }
            });
        }
    }

    private Map<Class<? extends AgentListener>, Collection<? extends AgentListener>> agentListeners;

    @SuppressWarnings("unchecked")
    private <T extends AgentListener> Collection<T> getOrCreateUIListeners(Class<T> cls) {
        if(agentListeners == null)
            agentListeners = new HashMap<>();
        Collection<T> collection = (Collection<T>) agentListeners.get(cls);
        if (collection == null) {
            collection = new ArrayList<T>();
            agentListeners.put(cls, collection);
        }
        return collection;
    }

    /**
     * @param cls Requested class of listeners.
     * @return List of registered UI listeners.
     */
    public <T extends AgentListener> Collection<T> getUIListeners(Class<T> cls) {
//        if (closed) {
//            return Collections.emptyList();
//        }
        return Collections.unmodifiableCollection(getOrCreateUIListeners(cls));
    }

    /**
     * Register new listener.
     * <p/>
     * Should be called from {@link Activity#onResume()}.
     */
    public <T extends AgentListener> void addUIListener(Class<T> cls, T listener) {
        getOrCreateUIListeners(cls).add(listener);
    }

    public <T extends AgentListener> void addOrUpdateUIListener(Class<T> cls, T listener) {
        try {
            if(getUIListeners(cls).size() == 0) {
                getOrCreateUIListeners(cls).add(listener);
            }
        } catch (Exception e) {

        }
    }



    /**
     * Unregister listener.
     * <p/>
     * Should be called from {@link Activity#onPause()}.
     */
    public <T extends AgentListener> void removeUIListener(Class<T> cls, T listener) {
        getOrCreateUIListeners(cls).remove(listener);
    }

    private void registerNetworkListener(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                context.registerReceiver(new FuguNetworkStateReceiver(),
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                FuguLog.e(TAG, "Error in broadcasting");
            }
        }
    }


    /*public void unregisterNetworkListener(Context context, BroadcastReceiver broadcastReceiver) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                context.unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                FuguLog.e(TAG, "Error in broadcasting");
            }
        }
    }*/

    private HashMap<String, Long> channelIds = new HashMap<>();

    public void setChannelIds(String transactionId, Long channelId) {
        channelIds.put(transactionId, channelId);
    }

    public Long getChannelId(String transactionId) {
        return channelIds.get(transactionId);
    }
}