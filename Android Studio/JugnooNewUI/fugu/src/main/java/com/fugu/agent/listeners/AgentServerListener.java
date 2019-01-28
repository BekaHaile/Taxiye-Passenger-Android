package com.fugu.agent.listeners;

import com.fugu.agent.model.GetConversationResponse;
import com.fugu.retrofit.APIError;

import java.util.ArrayList;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public interface AgentServerListener extends AgentListener {

    void conversationList(GetConversationResponse getConversationResponse, boolean isPagination, int fragmentType, int endPage);

    void onError(APIError error, int fragmentType);

    void offlineConversationList(int fragmentType, ArrayList<Object> arrayList);

}
