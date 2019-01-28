package com.fugu.support.logicImplView;

import android.app.Activity;
import android.widget.Toast;

import com.fugu.model.FuguCreateConversationResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.support.Utils.CommonSupportParam;
import com.fugu.support.callback.HippoSupportDetailInter;
import com.fugu.support.model.Category;
import com.fugu.support.model.HippoSendQueryParams;
import com.fugu.support.model.SupportModelResponse;
import com.fugu.support.model.callbackModel.SendQueryChat;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public class HippoSupportDetailInterImpl implements HippoSupportDetailInter {

    private OnFinishedListener onFinishedListener;

    @Override
    public void getSupportData(Activity activity, SendQueryChat queryChat, OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
        createChat(activity, queryChat.getCategory(), queryChat.getTransactionId(), queryChat.getUserUniqueId(),
                queryChat.getSupportId(), queryChat.getPathList(), queryChat.getTextboxMsg(),
                queryChat.getSuccessMessage(), queryChat.getSubHeader());
    }

    private void createChat(final Activity activity, Category category, String transactionId,
                            String userUniqueId, int supportId, ArrayList<String> pathList,
                            String textboxMsg, final String successMsg, String subHeader) {
        HippoSendQueryParams submitQueryParams = new CommonSupportParam().getSubmitQueryParams(category.getCategoryName(),
                transactionId, userUniqueId, supportId, pathList, textboxMsg, subHeader);

        RestClient.getApiInterface().createTicket(submitQueryParams)
                .enqueue(new ResponseResolver<SupportModelResponse>(activity, true, false) {

                    @Override
                    public void success(SupportModelResponse fuguCreateConversationResponse) {
                        Toast.makeText(activity, successMsg, Toast.LENGTH_SHORT).show();
                        onFinishedListener.onSuccess();
                    }

                    @Override
                    public void failure(APIError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        onFinishedListener.onFailure();
                    }
                });
    }
}
