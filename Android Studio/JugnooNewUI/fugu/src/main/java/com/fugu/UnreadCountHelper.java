package com.fugu;

import android.text.TextUtils;

import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.listeners.UnreadListener;
import com.fugu.agent.model.LoginModel.UserData;
import com.fugu.agent.model.UnreadCountData;
import com.fugu.agent.model.unreadResponse.UnreadCountResponse;
import com.fugu.agent.model.unreadResponse.UserUnreadCount;
import com.fugu.constant.FuguAppConstant;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gurmail on 27/06/18.
 *
 * @author gurmail
 */

public class UnreadCountHelper implements UnreadListener {
    private static final String TAG = UnreadCountHelper.class.getSimpleName();
    private static UnreadCountHelper instance;

    private UnreadCountHelper() {

    }

    protected static UnreadCountHelper getInstance() {
        if(instance == null) {
            synchronized (UnreadCountHelper.class) {
                if(instance == null) {
                    instance = new UnreadCountHelper();
                    instance.setHelperListener();
                }
            }
        }
        instance.checkHelperListener();
        return instance;
    }

    private void setHelperListener() {
        FuguConfig.getInstance().addUIListener(UnreadListener.class, this);
    }

    private void checkHelperListener() {
        FuguConfig.getInstance().addOrUpdateUIListener(UnreadListener.class, this);
    }

    private static Type listType = new TypeToken<ArrayList<String>>() {
    }.getType();

    @Override
    public void pushUpdateCount(String userUniqueKey, boolean isArrayStr) {
        if(TextUtils.isEmpty(userUniqueKey))
            return;
        if(isArrayStr) {
            ArrayList<String> userUniqueKeyArr = new Gson().fromJson(userUniqueKey, listType);
            for(String string : userUniqueKeyArr) {
                UnreadCountData countData = AgentCommonData.getUnreadCount(string);
                if(countData == null || TextUtils.isEmpty(countData.getUserUniqueKey()))
                    continue;

                int count = countData.getCount();
                count = count + 1;
                countData.setCount(count);
                AgentCommonData.addUnreadCount(string, countData);
            }
        } else {
            UnreadCountData countData = AgentCommonData.getUnreadCount(userUniqueKey);
            if(countData == null || TextUtils.isEmpty(countData.getUserUniqueKey()))
                return;

            int count = countData.getCount();
            count = count + 1;
            countData.setCount(count);
            AgentCommonData.addUnreadCount(userUniqueKey, countData);
        }

        HashMap<String, UnreadCountData> dataHashMap = AgentCommonData.getUnreadCount();

        HashMap<String, Integer> hashMap = new HashMap<>();
        for (Map.Entry map : dataHashMap.entrySet()) {
            UnreadCountData data = (UnreadCountData) map.getValue();
            hashMap.put((String) map.getKey(), data.getCount());
        }

        String jsonData = new Gson().toJson(hashMap);
        if(FuguConfig.getInstance().getAgentCountListener() != null)
            FuguConfig.getInstance().getAgentCountListener().unreadCount(jsonData);
    }

    @Override
    public void updateOpenChannelCount(ArrayList<String> userUniqueKeyArr, int value) {
        for(String userUniqueKey : userUniqueKeyArr) {
            UnreadCountData countData = AgentCommonData.getUnreadCount(userUniqueKey);
            if (countData == null || TextUtils.isEmpty(countData.getUserUniqueKey()))
                continue;
            int count = countData.getCount();
            count = count - value;
            if (count < 0)
                count = 0;
            countData.setCount(count);
            AgentCommonData.addUnreadCount(userUniqueKey, countData);
        }
        HashMap<String, UnreadCountData> dataHashMap = AgentCommonData.getUnreadCount();
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (Map.Entry map : dataHashMap.entrySet()) {
            UnreadCountData data = (UnreadCountData) map.getValue();
            hashMap.put((String) map.getKey(), data.getCount());
        }

        String jsonData = new Gson().toJson(hashMap);
        if(FuguConfig.getInstance().getAgentCountListener() != null)
            FuguConfig.getInstance().getAgentCountListener().unreadCount(jsonData);
    }

    @Override
    public void getUnreadCount() {
        HashMap<String, UnreadCountData> countHashMap = AgentCommonData.getUnreadCount();
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (Map.Entry map : countHashMap.entrySet()) {
            UnreadCountData data = (UnreadCountData) map.getValue();
            hashMap.put((String) map.getKey(), data.getCount());
        }

        String jsonData = new Gson().toJson(hashMap);
        if(FuguConfig.getInstance().getAgentCountListener() != null)
            FuguConfig.getInstance().getAgentCountListener().unreadCount(jsonData);
    }

    @Override
    public void getUpdatedUnreadCount(ArrayList<String> strings) {
        //FuguLog.i(TAG, "getUIListeners = "+FuguConfig.getInstance().getUIListeners(UnreadListener.class).size());
        if(strings == null)
            strings = AgentCommonData.getAgentUniqueKey();
        if(FuguConfig.getInstance().getAgentCountListener() != null)
            getUpdatedUnreadCount(strings, FuguConfig.getInstance().getAgentCountListener());

    }

    @Override
    public void getUpdatedUnreadCount(ArrayList<String> strings, final AgentUnreadCountListener countListener) {
        UserData userData = AgentCommonData.getUserData();
        if(strings == null)
            strings = AgentCommonData.getAgentUniqueKey();
        if(userData == null || strings == null)
            return;
        CommonParams commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.ACCESS_TOKEN, userData.getAccessToken())
                .add(FuguAppConstant.USER_UNIQUE_KEY, new Gson().toJson(strings))
                .add(FuguAppConstant.RESPONSE_TYPE, 1)
                .build();

        RestClient.getAgentApiInterface().getUnreadCount(commonParams.getMap())
                .enqueue(new ResponseResolver<UnreadCountResponse>() {
                    @Override
                    public void success(UnreadCountResponse unreadCountResponse) {
                        try {
                            AgentCommonData.clearUnreadCount();
                            HashMap<String, UnreadCountData> countHashMap = new HashMap<>();

                            for(UserUnreadCount unreadCount : unreadCountResponse.getData().getUserUnreadCount()) {
                                UnreadCountData data = new UnreadCountData();
                                data.setUserUniqueKey(unreadCount.getUserUniqueKey());
                                data.setHippoUserId(unreadCount.getUserId());
                                data.setCount(unreadCount.getUnreadCount());

                                countHashMap.put(unreadCount.getUserUniqueKey(), data);
                            }
                            AgentCommonData.addAllUnreadCount(countHashMap);
                            HashMap<String, Integer> hashMap = new HashMap<>();
                            for (Map.Entry map : countHashMap.entrySet()) {
                                UnreadCountData data = (UnreadCountData) map.getValue();
                                hashMap.put((String) map.getKey(), data.getCount());
                            }
                            String jsonData = new Gson().toJson(hashMap);
                            if(FuguConfig.getInstance().getAgentCountListener() != null)
                                FuguConfig.getInstance().getAgentCountListener().unreadCount(jsonData);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void failure(APIError error) {

                    }
                });
    }

    @Override
    public void addTotalPushUnread(Integer channelId) {
        try {
            Integer count = AgentCommonData.getTotalUnreadCount(channelId);
            if(count == null)
                count = 0;

            count += 1;
            HashMap<Integer, Integer> totalUnreadCount = AgentCommonData.addTotalUnreadCount(channelId, count);
            int totalCount = 0;
            for(Integer value : totalUnreadCount.values()) {
                totalCount = totalCount + value;
            }
            if (FuguConfig.getInstance().getCallbackListener() != null) {
                FuguConfig.getInstance().getCallbackListener().count(totalCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTotalUnreadCount() {
        HashMap<Integer, Integer> totalUnreadCount = AgentCommonData.getTotalUnreadCount();
        FuguLog.i(TAG, "totalUnreadCount: "+new Gson().toJson(totalUnreadCount));
        int totalCount = 0;
        for(Integer value : totalUnreadCount.values()) {
            totalCount = totalCount + value;
        }
        if (FuguConfig.getInstance().getCallbackListener() != null) {
            FuguConfig.getInstance().getCallbackListener().count(totalCount);
        }
    }
}
