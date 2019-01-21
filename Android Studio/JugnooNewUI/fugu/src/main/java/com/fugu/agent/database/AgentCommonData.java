package com.fugu.agent.database;

import android.annotation.SuppressLint;

import com.fugu.FuguColorConfig;
import com.fugu.FuguStringConfig;
import com.fugu.agent.Util.FragmentType;
import com.fugu.agent.Util.SPLabels;
import com.fugu.agent.model.FuguAgentGetMessageResponse;
import com.fugu.agent.model.ListItem;
import com.fugu.agent.model.LoginModel.Tag;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.Message;
import com.fugu.agent.model.UnreadCountData;
import com.fugu.agent.model.unreadResponse.UserUnreadCount;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.model.UnreadCountModel;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import io.paperdb.Paper;

import static com.fugu.database.PaperDbConstant.PAPER_COLOR_CONFIG;
import static com.fugu.database.PaperDbConstant.PAPER_NOTIFICATION;
import static com.fugu.database.PaperDbConstant.PAPER_SENT_MESSAGES;
import static com.fugu.database.PaperDbConstant.PAPER_STRING_CONFIG;
import static com.fugu.database.PaperDbConstant.PAPER_UNSENT_MESSAGES;


/**
 * Created by Bhavya Rattan on 15/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public final class AgentCommonData implements AgentPaperDbConstant {

    public static final String PACKAGENAME = "HIPPO_PACKAGENAME";
    public static List<FuguConversation> CONVERSATION_LIST = Collections.emptyList();
    public static HashMap<Integer, FuguAgentGetMessageResponse> GET_MESSAGE_RESPONSE_MAP = new HashMap<>();
    //public static HashMap<Integer, LinkedHashMap<Integer, ListItem>> UNSENT_MESSAGE_MAP = new HashMap<>();
    public static UserData userData;
    private static HashMap<String, UnreadCountData> UNREAD_COUNT = new HashMap<>();
    private static HashMap<Integer, Integer> TOTAL_UNREAD_COUNT = new HashMap<>();

    public static HashMap<Long, LinkedHashMap<String, JSONObject>> UNSENT_MESSAGE_JSON = new HashMap<>();
    public static HashMap<Long, LinkedHashMap<String, ListItem>> SENT_MESSAGES = new HashMap<>();
    public static HashMap<Long, LinkedHashMap<String, ListItem>> UNSENT_MESSAGES = new HashMap<>();

    /**
     * Empty Constructor
     * not called
     */
    private AgentCommonData() {
    }

    public static void setMainTitle(String title) {
        Paper.book().write(SPLabels.AGENT_TITLE, title);
    }

    public static String getMainTitle() {
        return Paper.book().read(SPLabels.AGENT_TITLE, "Support Chat");
    }

    public static void saveUserData(UserData userData1) {
        userData = new UserData();
        userData = userData1;
        Paper.book().write(SPLabels.USER_DATA, userData1);
    }

    public static UserData getUserData() {
        if (userData == null) {
            userData = Paper.book().read(SPLabels.USER_DATA);
        }
        return userData;
    }

    public static void saveTags(List<Tag> tags) {
        Paper.book().write(SPLabels.TAGS, tags);
    }

    public static void setAgentLoginInit(boolean init) {
        Paper.book().write(PAPER_IS_AGENT_INIT, init);
    }

    public static boolean isAgentFlow() {
        return Paper.book().read(PAPER_IS_AGENT_INIT, false);
    }

//    /**
//     * Save PAPER_STRING_CONFIG
//     *
//     * @param fuguStringConfig
//     */
//    public static void setStringConfig(FuguStringConfig fuguStringConfig) {
//        CommonData.STRING_CONFIG = fuguStringConfig;
//        Paper.book().write(PAPER_STRING_CONFIG, fuguStringConfig);
//    }
//
//    /**
//     * Gets PAPER_COLOR_CONFIG
//     *
//     * @return the fuguColorConfig
//     */
//
//    public static FuguStringConfig getStringConfig() {
//        if (STRING_CONFIG == null)
//            STRING_CONFIG = Paper.book().read(PAPER_STRING_CONFIG, null);
//        return STRING_CONFIG;
//    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param getMessageResponse
     */

    public static void setAgentMessageResponse(Integer channelId, FuguAgentGetMessageResponse getMessageResponse) {

        GET_MESSAGE_RESPONSE_MAP.put(channelId, getMessageResponse);
        Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, AgentCommonData.GET_MESSAGE_RESPONSE_MAP);

    }

    /**
     * Save PAPER_GET_MESSAGE_RESPONSE_MAP
     *
     * @param channelId
     * @param messages
     */

    public static void setAgentMessagesToMessageMap(Integer channelId, ArrayList<Message> messages) {

        if (GET_MESSAGE_RESPONSE_MAP.get(channelId) != null && GET_MESSAGE_RESPONSE_MAP.get(channelId).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(channelId).getData().setMessages(messages);
            Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, AgentCommonData.GET_MESSAGE_RESPONSE_MAP);
        }

    }


    /**
     * Gets PAPER_GET_MESSAGE_RESPONSE_MAP
     *
     * @return the messageResponse
     */

    public static FuguAgentGetMessageResponse getAgentMessageResponse(Integer channelId) {

        if (GET_MESSAGE_RESPONSE_MAP.isEmpty()) {
            GET_MESSAGE_RESPONSE_MAP = Paper.book().read(PAPER_GET_MESSAGE_RESPONSE_MAP, new HashMap<Integer, FuguAgentGetMessageResponse>());
        }

        return GET_MESSAGE_RESPONSE_MAP.get(channelId);
    }

//    /**
//     * Save PAPER_COLOR_CONFIG
//     *
//     * @param fuguColorConfig
//     */
//    public static void setColorConfig(FuguColorConfig fuguColorConfig) {
//        CommonData.COLOR_CONFIG = fuguColorConfig;
//        Paper.book().write(PAPER_COLOR_CONFIG, fuguColorConfig);
//    }
//
//
//    public static FuguColorConfig getColorConfig() {
//
//        if (COLOR_CONFIG == null) {
//            COLOR_CONFIG = Paper.book().read(PAPER_COLOR_CONFIG, null);
//        }
//        return COLOR_CONFIG;
//    }


    /*
    * Sets cachedMessages in getMessagesResponse or getLabelIdResponse
    *
    * @param cachedMessages
    * *//*

    public static void setAgentCachedMessages(Integer mapKey, ArrayList<Message> cachedMessages) {
        if (GET_MESSAGE_RESPONSE_MAP.get(mapKey) != null && GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        }
    }*/


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param conversationChatList
     */

    public static void setAgentConversationList(Integer fragmentType, ArrayList<Object> conversationChatList) {
        Paper.book().write(PAPER_CONVERSATION_LIST+fragmentType, conversationChatList);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the conversationList
     */

    public static ArrayList<Object> getAgentConversationList(Integer fragmentType) {
        return Paper.book().read(PAPER_CONVERSATION_LIST+fragmentType, new ArrayList<>());
    }


    public static boolean getPagerOrder() {
        return Paper.book().read(SPLabels.AGENT_PAGER_ORDER, false);
    }

    public static void setPagerOrder(boolean order) {
        Paper.book().write(SPLabels.AGENT_PAGER_ORDER, order);
    }

    // Handle unread count w.r.t user unique key.
    public static HashMap<String, UnreadCountData> getUnreadCount() {
        UNREAD_COUNT = Paper.book().read(PAPER_AGENT_UNREAD_COUNT, new HashMap<String, UnreadCountData>());
        return UNREAD_COUNT;
    }

    public static void addAllUnreadCount(HashMap<String, UnreadCountData> hashMap) {
        Paper.book().write(PAPER_AGENT_UNREAD_COUNT, hashMap);
    }

    public static UnreadCountData getUnreadCount(String userUniqueKey) {
        return getUnreadCount().get(userUniqueKey);
    }


    public static HashMap<String, UnreadCountData> addUnreadCount(String userUniqueKey, UnreadCountData unreadCountData) {
        UNREAD_COUNT = getUnreadCount();
        UNREAD_COUNT.put(userUniqueKey, unreadCountData);
        Paper.book().write(PAPER_AGENT_UNREAD_COUNT, UNREAD_COUNT);
        return UNREAD_COUNT;
    }

    public static HashMap<String, UnreadCountData> removeUnreadCount(String userUniqueKey) {
        UNREAD_COUNT = getUnreadCount();
        UNREAD_COUNT.remove(userUniqueKey);
        Paper.book().write(PAPER_AGENT_UNREAD_COUNT, UNREAD_COUNT);
        return UNREAD_COUNT;
    }

    public static void clearUnreadCount() {
        Paper.book().delete(PAPER_AGENT_UNREAD_COUNT);
    }


    //For Agent Total Unread Count
    public static HashMap<Integer, Integer> getTotalUnreadCount() {
        TOTAL_UNREAD_COUNT = Paper.book().read(PAPER_AGENT_TOTAL_UNREAD_COUNT, new HashMap<Integer, Integer>());
        return TOTAL_UNREAD_COUNT;
    }

    public static void addTotalUnreadCount(HashMap<Integer, Integer> hashMap) {
        Paper.book().write(PAPER_AGENT_TOTAL_UNREAD_COUNT, hashMap);
    }

    public static Integer getTotalUnreadCount(Integer channelId) {
        return getTotalUnreadCount().get(channelId);
    }

    public static HashMap<Integer, Integer> addTotalUnreadCount(Integer channelId, Integer count) {
        TOTAL_UNREAD_COUNT = getTotalUnreadCount();
        TOTAL_UNREAD_COUNT.put(channelId, count);
        Paper.book().write(PAPER_AGENT_TOTAL_UNREAD_COUNT, TOTAL_UNREAD_COUNT);
        return TOTAL_UNREAD_COUNT;
    }

    public static HashMap<Integer, Integer> removeTotalUnreadCount(Integer channelId) {
        TOTAL_UNREAD_COUNT = getTotalUnreadCount();
        TOTAL_UNREAD_COUNT.remove(channelId);
        Paper.book().write(PAPER_AGENT_TOTAL_UNREAD_COUNT, TOTAL_UNREAD_COUNT);
        return TOTAL_UNREAD_COUNT;
    }

    public static void clearTotalUnreadCount() {
        Paper.book().delete(PAPER_AGENT_TOTAL_UNREAD_COUNT);
    }



    public static ArrayList<String> getAgentUniqueKey() {
        return Paper.book().read(SPLabels.AGENT_LIST, null);
    }

    public static void setAgentUniqueKey(ArrayList<String> strings) {
        Paper.book().write(SPLabels.AGENT_LIST, strings);
    }

    //======================================== Clear UserData ===============================================

    /**
     * Delete paper.
     */
    public static void clearAgentData() throws Exception {
        CONVERSATION_LIST = Collections.emptyList();
        GET_MESSAGE_RESPONSE_MAP = new HashMap<>();
        userData = new UserData();
        Paper.book().delete(PAPER_CONVERSATION_LIST);
        Paper.book().delete(PAPER_GET_MESSAGE_RESPONSE_MAP);
        Paper.book().delete(PAPER_UNSENT_MESSAGE_MAP);
        Paper.book().delete(PAPER_IS_AGENT_INIT);
        Paper.book().delete(SPLabels.USER_DATA);
        Paper.book().delete(SPLabels.TAGS);
        Paper.book().delete(SPLabels.AGENT_LIST);
        Paper.book().delete(SPLabels.AGENT_TITLE);
        Paper.book().delete(SPLabels.AGENT_PAGER_ORDER);
        Paper.book().delete(PAPER_AGENT_UNREAD_COUNT);
        Paper.book().destroy();
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



    public static String getPackageName() {
        return Paper.book().read(PACKAGENAME);
    }

    public static void savePackageName(String packagename) {
        Paper.book().write(PACKAGENAME, packagename);
    }

    public static HashMap<Long, ArrayList<Integer>> getAllNotificationChannels() {
        return Paper.book().read(PAPER_NOTIFICATION, new HashMap<Long, ArrayList<Integer>>());
    }

    public static ArrayList<Integer> getNotificationArray(Long channelId) {
        HashMap<Long, ArrayList<Integer>> hashMap = getAllNotificationChannels();
        return hashMap.get(channelId);
    }

    public static void saveNotificationId(Long channelId, Integer notificationChannel) {
        ArrayList<Integer> ids = getNotificationArray(channelId);
        if(ids == null || ids.size()==0)
            ids = new ArrayList<>();

        ids.add(notificationChannel);
        HashMap<Long, ArrayList<Integer>> hashMap = getAllNotificationChannels();
        hashMap.put(channelId, ids);
        Paper.book().write(PAPER_NOTIFICATION, hashMap);
    }

    public static void removeNotificationChannel(Long channelId) {
        HashMap<Long, ArrayList<Integer>> hashMap = getAllNotificationChannels();
        hashMap.remove(channelId);
        Paper.book().write(PAPER_NOTIFICATION, hashMap);
    }

    public static void removeAllNotificationChannel() {
        Paper.book().delete(PAPER_NOTIFICATION);
    }
}
