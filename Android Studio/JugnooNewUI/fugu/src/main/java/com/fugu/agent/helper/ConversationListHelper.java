package com.fugu.agent.helper;

import com.fugu.FuguConfig;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.listeners.AgentServerListener;
import com.fugu.agent.listeners.ConversationListerner;
import com.fugu.agent.model.GetConversationResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class ConversationListHelper implements ConversationListerner {

    @Override
    public void getAgentConversation(HashMap<String, Object> params, final boolean isPagination, final int fragmentType, final int endPage) {
        RestClient.getAgentApiInterface().getConversation(params).enqueue(new ResponseResolver<GetConversationResponse>() {
            @Override
            public void success(GetConversationResponse getConversationResponse) {
                for(AgentServerListener listener : FuguConfig.getInstance().getUIListeners(AgentServerListener.class))
                    listener.conversationList(getConversationResponse, isPagination, fragmentType, endPage);
            }

            @Override
            public void failure(APIError error) {
                for(AgentServerListener listener : FuguConfig.getInstance().getUIListeners(AgentServerListener.class))
                    listener.onError(error, fragmentType);
            }
        });
    }

    @Override
    public void getOfflineData(int fragmentType) {
        ArrayList<Object> conversationList = AgentCommonData.getAgentConversationList(fragmentType);
        for(AgentServerListener listener : FuguConfig.getInstance().getUIListeners(AgentServerListener.class))
            listener.offlineConversationList(fragmentType, conversationList);
    }

}
