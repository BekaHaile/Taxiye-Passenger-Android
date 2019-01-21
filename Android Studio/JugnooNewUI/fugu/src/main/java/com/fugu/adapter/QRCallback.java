package com.fugu.adapter;

import com.fugu.model.Message;

/**
 * Created by gurmail on 26/04/18.
 *
 * @author gurmail
 */

public interface QRCallback {


    void onFormClickListener(int id, Message currentFormMsg);

    void DataFormCallback();

    void onClickListener(Message message, int pos, FuguMessageAdapter.QuickReplyViewHolder viewHolder);
}
