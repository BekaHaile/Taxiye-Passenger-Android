package com.fugu.agent.listeners;

import com.fugu.AgentUnreadCountListener;

import java.util.ArrayList;

/**
 * Created by gurmail on 27/06/18.
 *
 * @author gurmail
 */

public interface UnreadListener extends AgentListener {

    void getUnreadCount();

    void getUpdatedUnreadCount(ArrayList<String> strings, AgentUnreadCountListener unreadCount);

    void pushUpdateCount(String userUniqueKey, boolean isArrayStr);

    void sendTotalUnreadCount();

    void addTotalPushUnread(Integer channelId);

    void getUpdatedUnreadCount(ArrayList<String> strings);

    void updateOpenChannelCount(ArrayList<String> userUniqueKey, int value);

}
