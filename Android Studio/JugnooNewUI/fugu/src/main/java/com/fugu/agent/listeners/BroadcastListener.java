package com.fugu.agent.listeners;

import com.fugu.agent.model.broadcastResponse.BroadcastModel;
import com.fugu.agent.model.broadcastStatus.BroadcastResponseModel;

import java.util.HashMap;

/**
 * Created by gurmail on 20/07/18.
 *
 * @author gurmail
 */

public interface BroadcastListener extends AgentListener {

    interface BroadcastResponse {
        void groupingResponse(BroadcastModel broadcastModel);
        void sendBroadcastResponse(BroadcastResponseModel responseModel);
        void broadcastListResponse(BroadcastResponseModel responseModel);
        void onFailure(int type, String errorMessage);
    }

    void getGroupingList(HashMap<String, Object> hashMap, BroadcastResponse broadcastResponse);

    void sendBroadcastMessage(HashMap<String, Object> hashMap, BroadcastResponse broadcastResponse);

    void getBroadcastList(HashMap<String, Object> hashMap, BroadcastResponse broadcastResponse);
}
