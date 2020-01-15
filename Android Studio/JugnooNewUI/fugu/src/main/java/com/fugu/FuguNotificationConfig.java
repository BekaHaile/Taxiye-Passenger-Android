package com.fugu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import com.fugu.activity.FuguChannelsActivity;
import com.fugu.activity.FuguChatActivity;
import com.fugu.agent.AgentChatActivity;
import com.fugu.agent.AgentListActivity;
import com.fugu.agent.Util.FragmentType;
import com.fugu.agent.database.AgentCommonData;
import com.fugu.agent.model.getConversationResponse.Conversation;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.model.UnreadCountModel;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.CommonResponse;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.service.FuguPushIntentService;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 19/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguNotificationConfig implements FuguAppConstant {

    private static final String TAG = FuguNotificationConfig.class.getSimpleName();
    public static String fuguDeviceToken = "";
    public static final String CHANNEL_ONE_ID = "com.fugu.ONE";
    public static Long pushChannelId = -1L;
    public static Long pushLabelId = -1L;
    public static Long agentPushChannelId = -1L;
    public static boolean isChannelActivityOnPause = false;
    public static final String CHANNEL_ONE_NAME = "Default notification";

    public void setNotificationSoundEnabled(boolean notificationSoundEnabled) {
        this.notificationSoundEnabled = notificationSoundEnabled;
    }

    public void setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
    }

    public void setLargeIcon(int largeIcon) {
        this.largeIcon = largeIcon;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    private boolean notificationSoundEnabled = true;
    private int smallIcon = -1, largeIcon;
    private int priority;

    private static NotificationManager notificationManager;

    public static void updateFcmRegistrationToken(String deviceToken) {
        fuguDeviceToken = deviceToken;
    }

    public boolean isFuguNotification(final Map<String, String> data) {

        if (data.containsKey("push_source") && data.get("push_source").equalsIgnoreCase("FUGU"))
            return true;
        else
            return false;
    }

    public static void handleFuguPushNotification(final Context context, final Bundle bundle) {
        /*try {
            FuguLog.e(TAG, "bundle = "+bundle.getString(FuguAppConstant.CONVERSATION));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if (bundle != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (AgentCommonData.isAgentFlow()) {
                        Conversation conversation = new Gson().fromJson(bundle.getString(FuguAppConstant.CONVERSATION), Conversation.class);
                        if (conversation != null && FuguConfig.getInstance() != null && !FuguConfig.getInstance().isDataCleared()) {
                            Intent conversationIntent = new Intent(context, AgentChatActivity.class);
                            if (conversation.getChannelId() < 0) {
                                conversationIntent = new Intent(context, AgentListActivity.class);
                            }
                            conversationIntent.putExtra(FuguAppConstant.FRAGMENT_TYPE, FragmentType.MY_CHAT.getOrdinal());
                            conversationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, Conversation.class));
                            context.startActivity(conversationIntent);
                        }
                    } else {
                        FuguConversation conversation = new Gson().fromJson(bundle.getString(FuguAppConstant.CONVERSATION), FuguConversation.class);
                        if (conversation != null && conversation.isStartChannelsActivity() && FuguConfig.getInstance() != null &&
                                !FuguConfig.getInstance().isDataCleared()) {
                            FuguLog.e(TAG, "conversation: " + new Gson().toJson(conversation));
                            Intent conversationIntent = new Intent(context, FuguChatActivity.class);
                            if (conversation.getChannelId() < 0 && conversation.getLabelId() < 0) {
                                conversationIntent = new Intent(context, FuguChannelsActivity.class);
                            }
                            conversationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                            context.startActivity(conversationIntent);
                        }
                    }
                }
            }, 1000);

        } else
            return;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public void showNotification(final Context context, final Map<String, String> data) {
        if (AgentCommonData.isAgentFlow()) {
            showAgentNotification(context, data);
        } else {
            showUserNotification(context, data);
        }
    }

    private void showAgentNotification(final Context context, final Map<String, String> data) {
        FuguLog.e(TAG, "Init time: " + new Date());
        Paper.init(context);
        try {
            JSONObject messageJson = new JSONObject(data.get("message"));
            FuguLog.e(TAG, "Push message: " + data.get("message"));

            try {
                if(messageJson.optInt("notification_type") == 14) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent mIntent = new Intent(NOTIFICATION_INTENT);
            Bundle dataBundle = new Bundle();
            for (String key : data.keySet()) {
                dataBundle.putString(key, data.get(key));
            }
            mIntent.putExtras(dataBundle);
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

            int disableReply = messageJson.optInt("disable_reply", 0);
            Long channelId = messageJson.optLong("channel_id", -1);
            String label = messageJson.optString("label", "");

            if (channelId > 0 && agentPushChannelId.compareTo(channelId) == 0) {
                if (!isChannelActivityOnPause)
                    return;
            }

            Intent notificationIntent = new Intent(context, FuguPushIntentService.class);
            notificationIntent.putExtra("channelId", channelId);
            notificationIntent.putExtra("label", label);
            notificationIntent.putExtra("userId", AgentCommonData.getUserData().getUserId());
            notificationIntent.putExtra("is_from_push", true);
            notificationIntent.putExtra("disable_reply", disableReply);

            Bundle mBundle = new Bundle();
            for (String key : data.keySet()) {
                mBundle.putString(key, data.get(key));
            }
            notificationIntent.putExtra("data", mBundle);
            if (CommonData.getPushFlags() != -1)
                notificationIntent.setFlags(CommonData.getPushFlags());
            PendingIntent pi = PendingIntent.getService(context, channelId.intValue()
                    , notificationIntent, 0);

            int notificationDefaults = Notification.DEFAULT_ALL;
            if (!notificationSoundEnabled)
                notificationDefaults = Notification.DEFAULT_LIGHTS;

//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ONE_ID)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageJson.getString("new_message")))
//                    .setSmallIcon(smallIcon == -1 ? R.drawable.hippo_default_notif_icon : smallIcon)
//                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
//                    .setContentTitle(messageJson.getString("title"))
//                    .setContentText(messageJson.getString("new_message"))
//                    .setContentIntent(pi)
//                    .setDefaults(notificationDefaults)
//                    .setPriority(priority)
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setAutoCancel(true);
//            mBuilder.setChannelId(CHANNEL_ONE_ID);

            String dateTime = messageJson.optString("date_time", "");

            String channelOneId = CHANNEL_ONE_ID + "." + channelId;
            String channelOneName = CHANNEL_ONE_NAME + "" + channelId;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelOneId);
            builder.setAutoCancel(true);
            builder.setContentTitle(messageJson.getString("title"));
            builder.setContentIntent(pi);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageJson.getString("new_message")));
            if (!TextUtils.isEmpty(dateTime))
                builder.setWhen(getTimeMilliSec(DateUtils.getInstance().convertToLocal(dateTime)));

            builder.setSmallIcon(smallIcon == -1 ? R.drawable.hippo_default_notif_icon : smallIcon);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
            builder.setContentText(messageJson.getString("new_message"));
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(channelOneId, channelOneName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                builder.setChannelId(channelOneId);
                notificationManager.createNotificationChannel(notificationChannel);
            }


            Notification notification = builder.build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", "android");

                if (smallIconViewId != 0) {

                    if (notification.headsUpContentView != null)
                        notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                    if (notification.bigContentView != null)
                        notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                }
            }

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            int id = (int) System.currentTimeMillis();
            notificationManager.notify(id, notification);
            AgentCommonData.saveNotificationId(channelId, id);

            try {
                if (channelId != 0 && FuguNotificationConfig.agentPushChannelId != channelId) {
                    if (!TextUtils.isEmpty(messageJson.optString("user_unique_key", "")))
                        UnreadCountHelper.getInstance().pushUpdateCount(messageJson.optString("user_unique_key", ""), true);

                    if (FuguConfig.getInstance() != null && !FuguConfig.getInstance().isChannelActivity()) {
                        UnreadCountHelper.getInstance().addTotalPushUnread(messageJson.getInt("channel_id"));
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Creating an Audio Attribute
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        FuguLog.e(TAG, "Creation at: " + new Date());
    }

    /**
     * @param data notification data
     */
    private void showUserNotification(final Context context, final Map<String, String> data) {
        Paper.init(context);
        CommonData.setPushBoolean(true);
        if (CommonData.getConversationList() != null && CommonData.getConversationList().size() <= 0)
            CommonData.setNotificationFirstClick(true);
        try {
            JSONObject messageJson = new JSONObject(data.get("message"));

            try {
                if(messageJson.optInt("notification_type") == 14) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent mIntent = new Intent(NOTIFICATION_INTENT);
            Bundle dataBundle = new Bundle();
            for (String key : data.keySet()) {
                dataBundle.putString(key, data.get(key));
            }
            mIntent.putExtras(dataBundle);
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

            Paper.init(context);

            long channelId = -1;
            long labelId = -1;
            int disableReply = messageJson.optInt("disable_reply", 0);
            String label = messageJson.optString("label", "");
            String title = messageJson.optString("title", "");
            String message = messageJson.optString("new_message", "");

            if (messageJson.has("channel_id"))
                channelId = messageJson.optLong("channel_id", -1);
            if (messageJson.has("label_id"))
                labelId = messageJson.optLong("label_id", -1);

//            FuguLog.e(TAG, "messageJson **** " + messageJson);
//            FuguLog.e(TAG, "disableReply **** " + disableReply);
//            FuguLog.e(TAG, "label **** " + label);
//            FuguLog.e(TAG, "title **** " + title);
//            FuguLog.e(TAG, "message **** " + message);
//            FuguLog.e(TAG, "channelId **** " + channelId);
//            FuguLog.e(TAG, "labelId **** " + labelId);


            if (pushChannelId != null && channelId > 0 && pushChannelId.compareTo(channelId) == 0) {
                return;
            } else if (pushLabelId != null && labelId > 0 && pushLabelId.compareTo(labelId) == 0) {
                return;
            }

            Intent notificationIntent = new Intent(context, FuguPushIntentService.class);
            notificationIntent.putExtra("channelId", channelId);
            notificationIntent.putExtra("en_user_id", CommonData.getUserDetails().getData().getEn_user_id());
            notificationIntent.putExtra("userId", CommonData.getUserDetails().getData().getUserId());
            notificationIntent.putExtra("labelId", labelId);
            notificationIntent.putExtra("label", label);
            notificationIntent.putExtra("disable_reply", disableReply);

            Bundle mBundle = new Bundle();
            for (String key : data.keySet()) {
                mBundle.putString(key, data.get(key));
            }
            notificationIntent.putExtra("data", mBundle);
            if (CommonData.getPushFlags() != -1)
                notificationIntent.setFlags(CommonData.getPushFlags());
            PendingIntent pi = PendingIntent.getService(context, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)
                    , notificationIntent, 0);

            int notificationDefaults = Notification.DEFAULT_ALL;
            if (!notificationSoundEnabled)
                notificationDefaults = Notification.DEFAULT_LIGHTS;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(smallIcon == -1 ? R.drawable.hippo_default_notif_icon : smallIcon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setDefaults(notificationDefaults)
                    .setPriority(priority)
                    .setAutoCancel(true);
            mBuilder.setChannelId(CHANNEL_ONE_ID);
            Notification notification = mBuilder.build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", "android");
                if (smallIconViewId != 0) {
                    if (notification.headsUpContentView != null)
                        notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                    if (notification.bigContentView != null)
                        notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                }
            }

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notification);

            try {
                if (FuguConfig.getInstance() != null && !FuguConfig.getInstance().isChannelActivity()) {
                    /*if (FuguNotificationConfig.pushChannelId.compareTo(messageJson.getLong("channel_id")) != 0) {
                        try {
                            final long channelid = messageJson.getLong("channel_id");
                            final long labelid = labelId;
                            HandlerThread thread = new HandlerThread("UpdateThread");
                            thread.start();
                            new Handler(thread.getLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    addUnreadCount(channelid, labelid);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
                    if ((messageJson.has("channel_id") && FuguNotificationConfig.pushChannelId.compareTo(messageJson.getLong("channel_id")) != 0)
                            || (messageJson.has("label_id") && FuguNotificationConfig.pushLabelId.compareTo(messageJson.getLong("label_id")) != 0)) {
                        try {
                            final long channelid = channelId;
                            final long labelid = labelId;
                            addUnreadCount(channelid, labelid);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }

                }
            } catch (Exception e) {
                //e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Creating an Audio Attribute
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void addUnreadCount(final Long channelId, final Long labelId) {
        try {
            FuguLog.e(TAG, "In count");
            if (!FuguConfig.getInstance().isChannelActivity()) {
                ArrayList<UnreadCountModel> unreadCountModels;
                int index = -1;
                if (channelId > 0) {
                    index = CommonData.getUnreadCountModel().indexOf(new UnreadCountModel(channelId));
                } else if (labelId > 0) {
                    if (CommonData.getUnreadCountModel() != null) {
                        ArrayList<UnreadCountModel> unreadCountModel = CommonData.getUnreadCountModel();
                        for (int i = 0; i < unreadCountModel.size(); i++) {
                            if (unreadCountModel.get(i).getLabelId().compareTo(labelId) == 0) {
                                index = i;
                                break;
                            }
                        }
                    }
                }
                FuguLog.v(TAG, "index = " + index);
                if (index > -1) {
                    unreadCountModels = CommonData.getUnreadCountModel();
                    FuguLog.v(TAG, "unreadCountModels = " + unreadCountModels.size());
                    FuguLog.v(TAG, "unreadCountModels.get(index).getCount() = " + unreadCountModels.get(index).getCount());
                    int channelCount = unreadCountModels.get(index).getCount() + 1;
                    FuguLog.v(TAG, "channelCount = " + channelCount);
                    unreadCountModels.get(index).setCount(channelCount);
                    CommonData.setUnreadCount(new ArrayList<UnreadCountModel>());
                    CommonData.setUnreadCount(unreadCountModels);
                } else {
                    int channelCount = 1;
                    UnreadCountModel countModel = new UnreadCountModel(channelId, labelId, channelCount);
                    unreadCountModels = CommonData.getUnreadCountModel();
                    FuguLog.v(TAG, "unreadCountModels = " + unreadCountModels.size());
                    unreadCountModels.add(countModel);
                    CommonData.setUnreadCount(unreadCountModels);
                }

                int count = 0;
                for (int i = 0; i < unreadCountModels.size(); i++) {
                    count = count + unreadCountModels.get(i).getCount();
                }

                FuguLog.v(TAG, "count = " + count);

                if (FuguConfig.getInstance().getCallbackListener() != null) {
                    FuguConfig.getInstance().getCallbackListener().count(count);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context, ArrayList<Integer> ids) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (ids != null && ids.size() > 0) {
            for (Integer integer : ids) {
                notificationManager.cancel(integer);
            }
        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            if (!TextUtils.isEmpty(timeStamp)) {
                Date date = format.parse(timeStamp);
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void sendAck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommonParams commonParams = new CommonParams.Builder()
                            .add("", "")
                            .build();

                    RestClient.getApiInterface().sendAckToServer(commonParams.getMap())
                            .enqueue(new ResponseResolver<CommonResponse>() {
                                @Override
                                public void success(CommonResponse commonResponse) {

                                }

                                @Override
                                public void failure(APIError error) {

                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
