package com.fugu.agent.listeners;

import com.fugu.agent.model.GetConversationResponse;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public interface AgentManagerListener {

    void updateUI();

    void getConversationResponse(GetConversationResponse getConversationResponse, boolean isPagenation, int fragmentType);
}
