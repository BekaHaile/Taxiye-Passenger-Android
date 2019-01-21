package com.fugu.agent.listeners;

/**
 * Created by gurmail on 01/09/18.
 *
 * @author gurmail
 */

public interface AgentConnectionListener {

    void onConnectionStatus(String message, int status);
}
