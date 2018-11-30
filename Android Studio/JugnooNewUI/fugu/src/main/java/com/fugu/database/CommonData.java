package com.fugu.database;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.fugu.CaptureUserData;
import com.fugu.FuguColorConfig;
import com.fugu.FuguFontConfig;
import com.fugu.FuguStringConfig;
import com.fugu.adapter.ListItem;
import com.fugu.constant.FuguAppConstant;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguDeviceDetails;
import com.fugu.model.FuguGetMessageResponse;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.model.Message;
import com.fugu.model.UnreadCountModel;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 15/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public final class CommonData implements PaperDbConstant {

    public static List<FuguConversation> CONVERSATION_LIST = Collections.emptyList();
    public static FuguPutUserDetailsResponse USER_DETAILS = null;
    public static String SERVER_URL = "";
    public static String APP_SECRET_KEY = "";
    public static String APP_TYPE = "1";
    public static TreeMap<Long, FuguGetMessageResponse> GET_MESSAGE_RESPONSE_MAP = new TreeMap<>();
    public static TreeMap<Long, FuguGetMessageResponse> GET_LABEL_ID_RESPONSE_MAP = new TreeMap<>();
    public static TreeMap<Long, TreeMap<String, ListItem>> UNSENT_MESSAGE_MAP = new TreeMap<>();
    public static FuguColorConfig COLOR_CONFIG = new FuguColorConfig();
    public static FuguStringConfig STRING_CONFIG = new FuguStringConfig();
    public static FuguFontConfig FONT_CONFIG = new FuguFontConfig();
    public static String isNewChatKey = "IS_NEW_CHAT";
    public static String providerKey = "PROVIDER_KEY";
    public static String pushKey = "PUSH_KEY";
    public static String NOTIFICATION_FIRST_CLICK = "hippo_notification_first_click";
    public static String pushChannelKey = "PUSH_CHANNEL_KEY";
    public static String isAppOpenKey = "isAppOpen";
    public static String newPeerChatCreated = "FUGU_NEW_PEER_CHAT_CREATED";
    public static TreeMap<Long, ArrayList<ListItem>> FUGU_MESSAGE_LIST = new TreeMap<>();
    public static final String clearFuguDataKey = "clearFuguData";
    public static final String HIPPO_UNREAD_COUNT = "hippo_unread_count";
    private static String keyQuickReply = "saveQuickReplay";


    public static HashMap<Long, LinkedHashMap<String, JSONObject>> UNSENT_MESSAGE_JSON = new HashMap<>();
    public static HashMap<Long, LinkedHashMap<String, ListItem>> SENT_MESSAGES = new HashMap<>();
    public static HashMap<Long, LinkedHashMap<String, ListItem>> UNSENT_MESSAGES = new HashMap<>();

    /**
     * Empty Constructor
     * not called
     */
    private CommonData() {
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param getMessageResponse
     */

    public static void setMessageResponse(Long channelId, FuguGetMessageResponse getMessageResponse) {
        GET_MESSAGE_RESPONSE_MAP.put(channelId, getMessageResponse);
        Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, CommonData.GET_MESSAGE_RESPONSE_MAP);
    }


    /**
     * Gets PAPER_GET_MESSAGE_RESPONSE_MAP
     *
     * @return the messageResponse
     */

    public static FuguGetMessageResponse getMessageResponse(Long channelId) {
        if (GET_MESSAGE_RESPONSE_MAP.isEmpty()) {
            GET_MESSAGE_RESPONSE_MAP = Paper.book().read(PAPER_GET_MESSAGE_RESPONSE_MAP, new TreeMap<Long, FuguGetMessageResponse>());
        }
        return GET_MESSAGE_RESPONSE_MAP.get(channelId);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the conversationList
     */

    public static FuguGetMessageResponse getLabelIdResponse(Long labelId) {
        if (GET_LABEL_ID_RESPONSE_MAP.isEmpty()) {
            GET_LABEL_ID_RESPONSE_MAP = Paper.book().read(PAPER_GET_LABEL_ID_RESPONSE_MAP, new TreeMap<Long, FuguGetMessageResponse>());
        }
        return GET_LABEL_ID_RESPONSE_MAP.get(labelId);
    }


    /**
     * Save PAPER_SERVER_URL
     *
     * @param serverUrl
     */

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = serverUrl;
        Paper.book().write(PAPER_SERVER_URL, CommonData.SERVER_URL);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the serverUrl
     */

    public static String getServerUrl() {
        if (TextUtils.isEmpty(SERVER_URL.trim())) {
            SERVER_URL = Paper.book().read(PAPER_SERVER_URL, FuguAppConstant.LIVE_SERVER);
        }
        return SERVER_URL;
    }


    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appSecretKey
     */

    public static void setAppSecretKey(String appSecretKey) {
        APP_SECRET_KEY = appSecretKey;
        Paper.book().write(PAPER_APP_SECRET_KEY, CommonData.APP_SECRET_KEY);
    }

    /**
     * Gets PAPER_APP_SECRET_KEY
     *
     * @return the appSecretKey
     */

    public static String getAppSecretKey() {
        if (APP_SECRET_KEY.isEmpty()) {
            APP_SECRET_KEY = Paper.book().read(PAPER_APP_SECRET_KEY, "");
        }
        return APP_SECRET_KEY;
    }


    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appType
     */

    public static void setAppType(String appType) {
        APP_TYPE = appType;
        Paper.book().write(PAPER_APP_TYPE, CommonData.APP_TYPE);
    }

    /**
     * Gets PAPER_APP_SECRET_KEY
     *
     * @return the appSecretKey
     */

    public static String getAppType() {
        APP_TYPE = Paper.book().read(PAPER_APP_TYPE, "1");
        return APP_TYPE;
    }

    public static int getPushFlags() {
        return Paper.book().read(PAPER_PUSH_FLAGS, -1);
    }

    public static void setPushFlags(int flags) {
        Paper.book().write(PAPER_PUSH_FLAGS, flags);
    }

    /*
    * Sets cachedMessages in getMessagesResponse or getLabelIdResponse
    *
    * @param cachedMessages
    * */

    public static void setCachedMessages(boolean isOpenChat, Long mapKey, ArrayList<Message> cachedMessages) {
        if (isOpenChat && GET_LABEL_ID_RESPONSE_MAP.get(mapKey) != null &&
                GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        } else if (GET_MESSAGE_RESPONSE_MAP.get(mapKey) != null && GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        }
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param conversationList
     */

    public static void setConversationList(List<FuguConversation> conversationList) {
        CommonData.CONVERSATION_LIST = conversationList;
        Paper.book().write(PAPER_CONVERSATION_LIST, conversationList);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the conversationList
     */

    public static List<FuguConversation> getConversationList() {
        if (CONVERSATION_LIST.isEmpty())
            CONVERSATION_LIST = Paper.book().read(PAPER_CONVERSATION_LIST, Collections.<FuguConversation>emptyList());
        return CONVERSATION_LIST;
    }

    /**
     * Gets PAPER_USER_DETAILS
     *
     * @return the userDetails
     */

    public static FuguPutUserDetailsResponse getUserDetails() {
        if (USER_DETAILS == null)
            USER_DETAILS = Paper.book().read(PAPER_USER_DETAILS, null);
        return USER_DETAILS;
    }

    /**
     * Save PAPER_COLOR_CONFIG
     *
     * @param fuguColorConfig
     */
    public static void setColorConfig(FuguColorConfig fuguColorConfig) {
        CommonData.COLOR_CONFIG = fuguColorConfig;
        Paper.book().write(PAPER_COLOR_CONFIG, fuguColorConfig);
    }

    /**
     * Save PAPER_FONT_CONFIG
     *
     * @param fuguFontConfig font config
     */
    public static void setFontConfig(FuguFontConfig fuguFontConfig) {
        CommonData.FONT_CONFIG = fuguFontConfig;
        Paper.book().write(PAPER_FONT_CONFIG, fuguFontConfig);
    }

    /**
     * Gets PAPER_COLOR_CONFIG
     *
     * @return the fuguColorConfig
     */

    public static FuguColorConfig getColorConfig() {
        if (COLOR_CONFIG == null)
            COLOR_CONFIG = Paper.book().read(PAPER_COLOR_CONFIG, null);
        return COLOR_CONFIG;
    }


    /**
     * Save PAPER_STRING_CONFIG
     *
     * @param fuguStringConfig
     */
    public static void setStringConfig(FuguStringConfig fuguStringConfig) {
        CommonData.STRING_CONFIG = fuguStringConfig;
        Paper.book().write(PAPER_STRING_CONFIG, fuguStringConfig);
    }

    /**
     * Gets PAPER_COLOR_CONFIG
     *
     * @return the fuguColorConfig
     */

    public static FuguStringConfig getStringConfig() {
        if (STRING_CONFIG == null)
            STRING_CONFIG = Paper.book().read(PAPER_STRING_CONFIG, null);
        return STRING_CONFIG;
    }



    /**
     * Gets PAPER_FONT_CONFIG
     *
     * @return the fuguFontConfig
     */

    public static FuguFontConfig getFontConfig() {
        if (FONT_CONFIG == null)
            FONT_CONFIG = Paper.book().read(PAPER_FONT_CONFIG, null);
        return FONT_CONFIG;
    }

    /**
     * Save PAPER_USER_DETAILS
     *
     * @param userDetails
     */
    public static void setUserDetails(FuguPutUserDetailsResponse userDetails) {
        CommonData.USER_DETAILS = userDetails;
        Paper.book().write(PAPER_USER_DETAILS, userDetails);
    }


//    public static void setUnsentMessageMapByChannel(Long channelId, TreeMap<String, ListItem> unsentMessageMap) {
//        UNSENT_MESSAGE_MAP.put(channelId, unsentMessageMap);
//        Paper.book().write(PAPER_UNSENT_MESSAGE_MAP, CommonData.UNSENT_MESSAGE_MAP);
//    }
//
//    public static void removeUnsentMessageMapChannel(Long channelId) {
//        CommonData.UNSENT_MESSAGE_MAP.remove(channelId);
//        Paper.book().write(PAPER_UNSENT_MESSAGE_MAP, UNSENT_MESSAGE_MAP);
//    }

    /**
     * Gets PAPER_UNSENT_MESSAGE_MAP
     *
     * @return the unsentMessageMap
     */

    /*public static TreeMap<String, ListItem> getUnsentMessageMapByChannel(Long channelId) {
        if (UNSENT_MESSAGE_MAP.isEmpty()) {
            UNSENT_MESSAGE_MAP = Paper.book().read(PAPER_UNSENT_MESSAGE_MAP, new TreeMap<Long, TreeMap<String, ListItem>>());
        }
        return UNSENT_MESSAGE_MAP.get(channelId);
    }

    public static TreeMap<Long, TreeMap<String, ListItem>> getUnsentMessageMap() throws Exception {
        if (UNSENT_MESSAGE_MAP.isEmpty()) {
            UNSENT_MESSAGE_MAP = Paper.book().read(PAPER_UNSENT_MESSAGE_MAP, new TreeMap<Long, TreeMap<String, ListItem>>());
        }
        return UNSENT_MESSAGE_MAP;
    }*/


    //======================================== Clear Data ===============================================

    /**
     * Delete paper.
     */
    public static void clearData() throws Exception {
        USER_DETAILS = null;
        CONVERSATION_LIST = Collections.emptyList();
        GET_MESSAGE_RESPONSE_MAP = new TreeMap<>();
        GET_LABEL_ID_RESPONSE_MAP = new TreeMap<>();
        UNSENT_MESSAGE_MAP = new TreeMap<>();
        SERVER_URL = "";
        APP_SECRET_KEY = "";
        COLOR_CONFIG = new FuguColorConfig();
        APP_TYPE = "";
        Paper.book().destroy();
    }

    public static JsonObject deviceDetails(Context context) {
        Gson gson = new GsonBuilder().create();
        JsonObject deviceDetailsJson = null;
        try {
            deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(
                    getAppVersion(context)).getDeviceDetails()).getAsJsonObject();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return deviceDetailsJson;
    }


    public static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getPackageName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            FuguLog.e("IP Address", ex.toString());
        }
        return null;
    }

    public static void setIsNewchat(boolean isNewchat) {
        Paper.book().write(isNewChatKey, isNewchat);
    }

    public static boolean getIsNewChat() {
        return Paper.book().read(isNewChatKey);
    }

    public static void setNewPeerChatCreated(boolean isNewPeerChatCreated) {
        Paper.book().write(newPeerChatCreated, isNewPeerChatCreated);
    }

    public static boolean getNewPeerChatCreated() {
        return Paper.book().read(newPeerChatCreated,false);
    }

    public static void setProvider(String provider) {
        Paper.book().write(providerKey, provider);
    }

    public static String getProvider() {
        return Paper.book().read(providerKey);
    }

    public static void setPushBoolean(boolean push) {
        Paper.book().write(pushKey, push);
    }

    public static boolean getPushBoolean() {
        if (Paper.book().read(pushKey) == null) {
            return false;
        }
        return Paper.book().read(pushKey);
    }

    public static void setNotificationFirstClick(boolean flag) {
        Paper.book().write(NOTIFICATION_FIRST_CLICK, flag);
    }

    public static boolean isFirstTimeWithNotification() {
        return Paper.book().read(NOTIFICATION_FIRST_CLICK, false);
    }

    public static void setPushChannel(Long pushChannel) {
        Paper.book().write(pushChannelKey, pushChannel);
    }

    public static Long getPushChannel() {
        if (Paper.book().read(pushChannelKey) == null) {
            return -1l;
        }
        return Paper.book().read(pushChannelKey);
    }

    public static void clearPushChannel() {
        Paper.book().delete(pushChannelKey);
    }

    public static void setTransactionIdsMap(HashMap<String, Long> transactionIdsMap) {
        Paper.book().write("TransactionIdsMap", transactionIdsMap);
    }

    public static HashMap<String, Long> getTransactionIdsMap() {
        return Paper.book().read("TransactionIdsMap");
    }

    public static boolean getIsAppOpen() {
        if (Paper.book().read(isAppOpenKey) == null) {
            return true;
        }
        return Paper.book().read(isAppOpenKey);
    }

    public static void setIsAppOpen(boolean isAppOpen) {
        Paper.book().write(isAppOpenKey, isAppOpen);
    }

    public static void setClearFuguDataKey(boolean clearFuguData) {
        Paper.book().write(clearFuguDataKey, clearFuguData);
    }

    public static boolean getClearFuguDataKey() {
        return Paper.book().read(clearFuguDataKey, false);
    }

//    public static void setUnreadCount(int count) {
//        Paper.book().write(HIPPO_UNREAD_COUNT, count);
//    }
//
//    public static int getUnreadCount() {
//        return Paper.book().read(HIPPO_UNREAD_COUNT);
//    }

    public static void setUnreadCount(ArrayList<UnreadCountModel> unreadCount) {
        //FuguLog.v("Array", "Array = "+new Gson().toJson(unreadCount, listType));
        Paper.book().write(HIPPO_UNREAD_COUNT, unreadCount);
    }

    public static ArrayList<UnreadCountModel> getUnreadCountModel() {
        ArrayList<UnreadCountModel> countModel = Paper.book().read(HIPPO_UNREAD_COUNT, new ArrayList<UnreadCountModel>());
        return countModel;
    }

    public static Type listType = new TypeToken<List<UnreadCountModel>>() {
    }.getType();

    public static void setIsDataCleared(boolean isDataCleared) {
        Paper.book().write("isDataCleared", isDataCleared);
    }

    public static boolean getIsDataCleared() {
        return Paper.book().read("isDataCleared", false);
    }

    public static void setChatTitle(String chatTitle) {
        Paper.book().write("chat_title", chatTitle);
    }

    public static String getChatTitle() {
        return Paper.book().read("chat_title", "Support");
    }

    public static void saveResellerData(String resellerToken, int referenceId) {
        Paper.book().write("fugu_resellerToken", resellerToken);
        Paper.book().write("fugu_referenceId", referenceId);
    }

    public static void saveUserData(CaptureUserData userData) {
        Paper.book().write("fugu_userData", userData);
    }

    public static CaptureUserData getUserData() {
        return Paper.book().read("fugu_userData", new CaptureUserData());
    }

    public static int getReferenceId() {
        return Paper.book().read("fugu_referenceId", 1);
    }

    public static String getResellerToken() {
        return Paper.book().read("fugu_resellerToken", null);
    }

    // for support

    public static void setCurrentVersion(int versionCode) {
        Paper.book().write(PAPER_DB_VERSION, versionCode);
    }

    public static int getLocalVersion() {
        return Paper.book().read(PAPER_DB_VERSION, -1);
    }

    public static String getDefaultCategory() {
        return Paper.book().read(PAPER_USER_DEFAULT_CATEGORY, null);
    }

    public static void setDefaultCategory(String defaultCategory) {
        Paper.book().write(PAPER_USER_DEFAULT_CATEGORY, defaultCategory);
    }

    public static void setSupportPath(ArrayList<String> pathList) {
        Paper.book().write(PAPER_SUPPORT_PATH, pathList);
        FuguLog.d("TAG", "Path = " + new Gson().toJson(pathList));
    }

    public static ArrayList<String> getPathList() {
        return Paper.book().read(PAPER_SUPPORT_PATH, new ArrayList<String>());
    }

    public static void removeLastPath() {
        ArrayList<String> pathList = getPathList();
        pathList.remove(pathList.size() - 1);
        setSupportPath(pathList);
    }

    public static void clearPathList() {
        Paper.book().delete(PAPER_SUPPORT_PATH);
    }

    public static void saveUserUniqueKey(String userUniqueKey) {
        Paper.book().write(PAPER_USER_UNIQUE_KEY, userUniqueKey);
    }

    public static String getUserUniqueKey() {
        return Paper.book().read(PAPER_USER_UNIQUE_KEY);
    }

    public static Message getQuickReplyData() {
        return Paper.book().read(keyQuickReply);
    }

    public static void setQuickReplyData(Message quickReplyData) {
        Paper.book().write(keyQuickReply, quickReplyData);
    }

    public static void clearQuickReplyData() {
        Paper.book().delete(keyQuickReply);
    }

    // Unsent messages as JSONObject
    public static void setUnsentMessageMapByChannel(Long uniqueId, LinkedHashMap<String, JSONObject> unsentMessageMap) {
        UNSENT_MESSAGE_JSON = getUnsentMessageMap();
        if(unsentMessageMap != null && unsentMessageMap.values().size()>0) {
            UNSENT_MESSAGE_JSON.put(uniqueId, unsentMessageMap);
            Paper.book().write(PAPER_UNSENT_MESSAGE_MAP, UNSENT_MESSAGE_JSON);
        }
    }

    public static void removeUnsentMessageMapChannel(Long channelId) {
        UNSENT_MESSAGE_JSON = getUnsentMessageMap();
        UNSENT_MESSAGE_JSON.remove(channelId);
        Paper.book().write(PAPER_UNSENT_MESSAGE_MAP, UNSENT_MESSAGE_JSON);
    }

    public static LinkedHashMap<String, JSONObject> getUnsentMessageMapByChannel(Long channelId) {
        UNSENT_MESSAGE_JSON = Paper.book().read(PAPER_UNSENT_MESSAGE_MAP, new HashMap<Long, LinkedHashMap<String, JSONObject>>());
        return UNSENT_MESSAGE_JSON.get(channelId);
    }
    // Unsent messages as JSONObject ended

    public static HashMap<Long, LinkedHashMap<String, JSONObject>> getUnsentMessageMap() {
        UNSENT_MESSAGE_JSON = Paper.book().read(PAPER_UNSENT_MESSAGE_MAP, new HashMap<Long, LinkedHashMap<String, JSONObject>>());
        return UNSENT_MESSAGE_JSON;
    }
    // Unsent messages as JSONObject ended


    //Unsent messages as Object

    public static void setAllUnsentMessageByChannel(LinkedHashMap<String, ListItem> unsentMessage) {
        Paper.book().write(PAPER_UNSENT_MESSAGES, unsentMessage);
    }

    public static void setUnsentMessageByChannel(Long channelId, LinkedHashMap<String, ListItem> unsentMessage) {
        UNSENT_MESSAGES = getUnsentMessages();
        if(unsentMessage != null && unsentMessage.values().size()>0) {
            UNSENT_MESSAGES.put(channelId, unsentMessage);
            Paper.book().write(PAPER_UNSENT_MESSAGES, UNSENT_MESSAGES);
        }
    }

    public static void removeUnsentMessageChannel(Long channelId) {
        UNSENT_MESSAGES = getUnsentMessages();
        UNSENT_MESSAGES.remove(channelId);
        Paper.book().write(PAPER_UNSENT_MESSAGES, UNSENT_MESSAGES);
    }

    public static LinkedHashMap<String, ListItem> getUnsentMessageByChannel(Long channelId) {
        UNSENT_MESSAGES = Paper.book().read(PAPER_UNSENT_MESSAGES, new HashMap<Long, LinkedHashMap<String, ListItem>>());
        return UNSENT_MESSAGES.get(channelId);
    }

    public static HashMap<Long, LinkedHashMap<String, ListItem>> getUnsentMessages() {
        UNSENT_MESSAGES = Paper.book().read(PAPER_UNSENT_MESSAGES, new HashMap<Long, LinkedHashMap<String, ListItem>>());
        return UNSENT_MESSAGES;
    }
    //Unsent messages as Object ended


    //Sent messages
    public static void addExistingMessages(Long channelId, LinkedHashMap<String, ListItem> sentMessage) {
        LinkedHashMap<String, ListItem> allSentMessages = getSentMessageByChannel(channelId);
        allSentMessages.putAll(sentMessage);
        setSentMessageByChannel(channelId, allSentMessages);
    }

    public static void setSentMessageByChannel(Long channelId, LinkedHashMap<String, ListItem> sentMessage) {
        SENT_MESSAGES = getSentMessages();
        SENT_MESSAGES.put(channelId, sentMessage);
        Paper.book().write(PAPER_SENT_MESSAGES, SENT_MESSAGES);

    }

    public static void removeSentMessageChannel(Long channelId) {
        SENT_MESSAGES = getSentMessages();
        SENT_MESSAGES.remove(channelId);
        Paper.book().write(PAPER_SENT_MESSAGES, SENT_MESSAGES);
    }

    public static LinkedHashMap<String, ListItem> getSentMessageByChannel(Long channelId) {
        SENT_MESSAGES = Paper.book().read(PAPER_SENT_MESSAGES, new HashMap<Long, LinkedHashMap<String, ListItem>>());
        return SENT_MESSAGES.get(channelId);
    }

    public static HashMap<Long, LinkedHashMap<String, ListItem>> getSentMessages() {
        SENT_MESSAGES = Paper.book().read(PAPER_SENT_MESSAGES, new HashMap<Long, LinkedHashMap<String, ListItem>>());
        return SENT_MESSAGES;
    }
    //Sent messages ended
}
