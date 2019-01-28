package com.fugu.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.fugu.FuguConfig;
import com.fugu.FuguNotificationConfig;
import com.fugu.activity.FuguChannelsActivity;
import com.fugu.activity.FuguChatActivity;
import com.fugu.agent.AgentChatActivity;
import com.fugu.agent.AgentListActivity;
import com.fugu.agent.Util.MessageMode;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.google.gson.Gson;

import static com.fugu.constant.FuguAppConstant.CHAT_TYPE;
import static com.fugu.constant.FuguAppConstant.NOTIFICATION_INTENT;
import static com.fugu.constant.FuguAppConstant.NOTIFICATION_TAPPED;

/**
 * Created by Bhavya Rattan on 26/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguPushIntentService extends IntentService {

    private static final String TAG = "FuguPushIntentService";

    public FuguPushIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent notificationIntent;

        if(AgentCommonData.isAgentFlow()) {
            Long channelId = intent.getLongExtra("channelId", -1);

            Conversation conversation = new Conversation();
            conversation.setChannelId(channelId);
            conversation.setUserId(intent.getIntExtra("userId",-1));
            conversation.setLabel(intent.getStringExtra("label"));
            conversation.setStatus(MessageMode.OPEN_CHAT.getOrdinal());
            conversation.setDisableReply(intent.getIntExtra("disable_reply", 0));

            if (FuguConfig.getInstance() != null && !FuguConfig.getInstance().isDataCleared()) {
                Intent backIntent = new Intent(this, AgentListActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                notificationIntent = new Intent(this, AgentChatActivity.class);
                notificationIntent.putExtra("is_from_push", true);
                if(channelId < 0) {
                    notificationIntent = new Intent(this, AgentListActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(notificationIntent);
                } else if ((FuguNotificationConfig.agentPushChannelId == -1) ||
                        (FuguNotificationConfig.agentPushChannelId != -1 && FuguNotificationConfig.isChannelActivityOnPause)) {
                    notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    TaskStackBuilder.create(this)
//                            .addNextIntentWithParentStack(notificationIntent)
//                            .startActivities();

                    startActivity(notificationIntent);
                } else {
                    Intent mIntent = new Intent(NOTIFICATION_TAPPED);
                    mIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
                }
            } else {
                PackageManager pm = this.getPackageManager();
                notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());
                conversation.setLabel("");
                //conversation.setStartChannelsActivity(true);
                notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
                notificationIntent.putExtra("is_from_push", true);
                startActivity(notificationIntent);
            }

        } else {
            long channelId = intent.getLongExtra("channelId", -1l);
            long labelId = intent.getLongExtra("labelId", -1l);
            FuguConversation conversation = new FuguConversation();
            conversation.setChannelId(channelId);
            conversation.setEnUserId(intent.getStringExtra("en_user_id"));
            conversation.setUserId(intent.getLongExtra("userId",-1L));
            conversation.setLabel(intent.getStringExtra("label"));
            conversation.setLabelId(labelId);
            conversation.setDisableReply(intent.getIntExtra("disable_reply", 0));
            if(channelId<0 && labelId>0) {
                conversation.setOpenChat(true);
            }
            if (FuguConfig.getInstance() != null && !FuguConfig.getInstance().isDataCleared()) {
                notificationIntent = new Intent(this, FuguChatActivity.class);
                if(channelId < 0 && labelId < 0) {
                    notificationIntent = new Intent(this, FuguChannelsActivity.class);
                    notificationIntent.putExtra("title", CommonData.getChatTitle());
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(notificationIntent);
                } else if (FuguNotificationConfig.pushChannelId.compareTo(-1l) == 0) {
                    notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(notificationIntent);
                } else {
                    Intent mIntent = new Intent(NOTIFICATION_TAPPED);
                    mIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
                }
            } else {
                PackageManager pm = this.getPackageManager();
                notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());
                //conversation.setLabel("");
                conversation.setStartChannelsActivity(true);
                notificationIntent.putExtra("startChatActivity", true);
                notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
//                Bundle bundle = notificationIntent.getExtras();
//                notificationIntent.putExtras(bundle);
                startActivity(notificationIntent);

                /*PackageManager pm = this.getPackageManager();
                notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());

                conversation = new FuguConversation();
                conversation.setChannelId(intent.getLongExtra("channelId", -1l));
                conversation.setLabel("");
                //conversation.setOpenChat(true);
                conversation.setEnUserId(intent.getStringExtra("en_user_id"));
                conversation.setUserId(intent.getLongExtra("userId",-1L));
                conversation.setStartChannelsActivity(true);

                notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));

                startActivity(notificationIntent);*/

            }
        }

    }
}
