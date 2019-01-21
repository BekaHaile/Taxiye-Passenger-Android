package com.fugu.support.callback;

import com.fugu.support.model.callbackModel.SendQueryChat;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public interface SupportDetailPresenter {

    void sendQuery(SendQueryChat queryChat);

    void onDestroy();
}
