package com.fugu.support.callback;

import com.fugu.support.model.SupportDataList;
import com.fugu.support.model.callbackModel.OpenChatParams;

/**
 * Created by gurmail on 29/03/18.
 */

public interface HippoSupportView extends BaseInterface {

    void setData(SupportDataList supportResponse);

    void openChatSupport(OpenChatParams chatParams);

//    @Deprecated
//    void openChatSupport(String transactionId, String userUniqueKey, String channelName, ArrayList<String> tagsList, String[] data);

    void showError();
}
