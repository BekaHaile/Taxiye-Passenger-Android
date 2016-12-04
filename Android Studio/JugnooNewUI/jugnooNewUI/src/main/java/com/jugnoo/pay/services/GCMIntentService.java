package com.jugnoo.pay.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jugnoo.pay.R;
import com.jugnoo.pay.activities.SplashNewActivity;

import org.json.JSONObject;

import java.util.Map;

public class GCMIntentService extends FirebaseMessagingService {

	private final String TAG = GCMIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;

    public GCMIntentService() {
    }


    @SuppressWarnings("deprecation")
    private void notificationManager(Context context, String title, String message) {
		clearNotifications(context);
        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent;
				// notificationIntent = new Intent(context, SplashActivity.class);
                notificationIntent = new Intent(context, SplashNewActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
            //builder.setSmallIcon(R.drawable.notification_icon);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.ic_notification_white);
            } else {
                builder.setSmallIcon(R.drawable.notification_icon);
            }
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID, notification);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire(15000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }


	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		try {
			String from = remoteMessage.getFrom();
			Map data = remoteMessage.getData();
			Log.e(TAG, "onMessageReceived data=" + data);

			if (!"".equalsIgnoreCase(String.valueOf(data.get("message")))) {
				String message = String.valueOf(data.get("message"));
				try {
					JSONObject jObj = new JSONObject(message);
					int flag = jObj.getInt("flag");
					String title = jObj.optString("title", getResources().getString(R.string.app_name));
					String message1 = jObj.optString("message", "");
					notificationManager(this, title, message1);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
