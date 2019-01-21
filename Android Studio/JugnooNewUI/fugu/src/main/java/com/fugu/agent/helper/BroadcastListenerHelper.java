package com.fugu.agent.helper;

import com.fugu.agent.listeners.BroadcastListener;
import com.fugu.agent.model.broadcastResponse.BroadcastModel;
import com.fugu.agent.model.broadcastStatus.BroadcastResponseModel;
import com.fugu.agent.model.createConversation.CreateConversation;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;

import java.util.HashMap;

/**
 * Created by gurmail on 20/07/18.
 *
 * @author gurmail
 */

public class BroadcastListenerHelper implements BroadcastListener {

    @Override
    public void getGroupingList(HashMap<String, Object> hashMap, final BroadcastResponse broadcastResponse) {
        RestClient.getAgentApiInterface().getGroupingTag(hashMap).enqueue(new ResponseResolver<BroadcastModel>() {
            @Override
            public void success(BroadcastModel broadcastModel) {
                broadcastResponse.groupingResponse(broadcastModel);
            }

            @Override
            public void failure(APIError error) {
                broadcastResponse.onFailure(1, error.getMessage());
            }
        });
    }

    @Override
    public void sendBroadcastMessage(HashMap<String, Object> hashMap, final BroadcastResponse broadcastResponse) {
        RestClient.getAgentApiInterface().sendBroadcastMessage(hashMap).enqueue(new ResponseResolver<BroadcastResponseModel>() {
            @Override
            public void success(BroadcastResponseModel responseModel) {
                broadcastResponse.sendBroadcastResponse(responseModel);
            }

            @Override
            public void failure(APIError error) {
                broadcastResponse.onFailure(2, error.getMessage());
            }
        });
    }

    @Override
    public void getBroadcastList(HashMap<String, Object> hashMap, final BroadcastResponse broadcastResponse) {
        RestClient.getAgentApiInterface().getBroadcastList(hashMap).enqueue(new ResponseResolver<BroadcastResponseModel>() {
            @Override
            public void success(BroadcastResponseModel broadcastResponseModel) {
                broadcastResponse.broadcastListResponse(broadcastResponseModel);
            }

            @Override
            public void failure(APIError error) {
                broadcastResponse.onFailure(3, error.getMessage());
            }
        });
    }
}
