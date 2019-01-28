package com.fugu.agent.listeners;

import java.util.HashMap;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public interface ConversationListerner {

    void getAgentConversation(HashMap<String, Object> params, boolean isPagination, int fragmentType, int endPage);

    void getOfflineData(int fragmentType);
}
