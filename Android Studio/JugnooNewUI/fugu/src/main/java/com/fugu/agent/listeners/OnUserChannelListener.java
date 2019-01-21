package com.fugu.agent.listeners;

import org.json.JSONObject;

/**
 * Created by gurmail on 20/06/18.
 *
 * @author gurmail
 */

public interface OnUserChannelListener extends AgentListener {

    void onAssignChat(JSONObject jsonObject);

    void onControlChannelData(JSONObject jsonObject);

    void onRefreshData();

    void onReadAll(JSONObject jsonObject);

    void updateCount(Long channelId);
}
