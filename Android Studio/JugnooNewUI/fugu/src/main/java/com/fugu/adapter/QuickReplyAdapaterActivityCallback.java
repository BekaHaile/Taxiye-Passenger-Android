package com.fugu.adapter;

import com.fugu.model.Message;

/**
 * Created by amit on 08/05/18.
 */

public interface QuickReplyAdapaterActivityCallback {

    void QuickReplyListener(Message message, int pos);

    void sendActionId(Message event);
}
