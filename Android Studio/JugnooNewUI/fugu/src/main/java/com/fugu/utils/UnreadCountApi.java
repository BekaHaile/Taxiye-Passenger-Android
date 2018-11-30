package com.fugu.utils;

import android.app.Activity;

import com.fugu.BuildConfig;
import com.fugu.FuguConfig;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguGetConversationsResponse;
import com.fugu.model.UnreadCountModel;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;

import java.util.ArrayList;

import static com.fugu.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.fugu.constant.FuguAppConstant.APP_VERSION;
import static com.fugu.constant.FuguAppConstant.DEVICE_TYPE;
import static com.fugu.constant.FuguAppConstant.EN_USER_ID;

/**
 * Created by gurmail on 06/06/18.
 *
 * @author gurmail
 */

public class UnreadCountApi {

    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();

    public UnreadCountApi() {

    }

    /**
     * Get conversations api hit
     */
    public void getConversations(Activity activity, String enUserId, final CountUnread countUnread) {
        {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, FuguConfig.getInstance().getAppKey())
                    .add(EN_USER_ID, enUserId)
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add(DEVICE_TYPE, 1)
                    .build();
            RestClient.getApiInterface().getConversations(commonParams.getMap())
                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(activity, false, false) {
                        @Override
                        public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
                            try {
                                CommonData.setConversationList(fuguGetConversationsResponse.getData().getFuguConversationList());
                                int count = 0;
                                fuguConversationList.clear();
                                fuguConversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
                                for (int i = 0; i < fuguConversationList.size(); i++) {
                                    count = count + fuguConversationList.get(i).getUnreadCount();
                                }
                                countUnread.countValue(count);
                                updateCount(fuguConversationList);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {

                        }
                    });
        }
    }

    public interface CountUnread {
        void countValue(int count);
    }

    private ArrayList<UnreadCountModel> unreadCountModels = new ArrayList<>();
    private void updateCount(ArrayList<FuguConversation> fuguConversationList) {
        try {
            int count = 0;
            unreadCountModels.clear();
            CommonData.setUnreadCount(unreadCountModels);
            for(int i=0;i<fuguConversationList.size();i++) {
                if(fuguConversationList.get(i).getUnreadCount()>0) {
                    UnreadCountModel countModel = new UnreadCountModel(fuguConversationList.get(i).getChannelId(), fuguConversationList.get(i).getLabelId(), fuguConversationList.get(i).getUnreadCount());
                    unreadCountModels.add(countModel);
                    count = count + fuguConversationList.get(i).getUnreadCount();
                }
            }

            CommonData.setUnreadCount(unreadCountModels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
