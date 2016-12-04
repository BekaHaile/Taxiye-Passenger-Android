package com.jugnoo.pay.services;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.jugnoo.pay.R;
import com.jugnoo.pay.utils.PushInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by ashutosh on 3/1/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static String TAG = "MyGcmListenerService";
    private  String pushMessage,bookingId;
    private  String spId,notificationType,spImageUrl,spName;
    private static PushInterface pushInterface;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        String message = data.getString("message");
        Log.d(TAG, message);

        try {
            JSONObject json = new JSONObject(message);
//            statusPush = json.getString("status");
            pushMessage = json.getString("notificationMessage");
            bookingId = Integer.toString(json.getInt("id"));
            notificationType = json.getString("notificationType");
           try {
               spId = json.getString("spId");
               spName = json.getString("spName");
               spImageUrl = json.getString("spImage");
           }
           catch(Exception e)
           {
               spId = null;
               spName = null;
               spImageUrl = null;
           }
//            valetName = json.getString("firstName")+" "+json.getString("LastName");
//            valetImageUrl = json.getJSONObject("profilePicURL").getString("original");
//            Log.d(TAG, statusPush);
        } catch (JSONException e) {
            pushMessage = "";
            bookingId = "";
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendNotification(pushMessage, notificationType,bookingId,spId,spName,spImageUrl);

    }


    public static void setPushListener(PushInterface pushListener) {
        pushInterface = pushListener;
    }


    private void sendNotification(String message, String status,String bookingId,String spId,String name,String imageUrl) {
//        Intent intent=null;
        int newNum = generateRandom();
//         if(notificationType.equalsIgnoreCase(AppConstants.CANCELED_BOOKING)||notificationType.equalsIgnoreCase(AppConstants.ACCEPTED_BOOKING)||notificationType.equalsIgnoreCase(AppConstants.STARTED_BOOKING)||notificationType.equalsIgnoreCase(AppConstants.ARRIVED_BOOKING)||notificationType.equalsIgnoreCase(AppConstants.EXPIRED_BOOKING))
//
//        {
//            ActivityManager am = (ActivityManager)this.getSystemService(this.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//            ActivityManager.RunningTaskInfo task = tasks.get(0); // get current task
//            ComponentName rootActivity = task.topActivity;
//
//             if(rootActivity.getPackageName().equalsIgnoreCase("com.jugnoo.pay")){
//
//                 intent = new Intent();
//                 intent.setComponent(rootActivity);
//
//                 intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK );
//            }
//            else
//            {
//                intent = new Intent(this, BookingDetailsActivity.class);
//                if(bookingId!=null)
//                    if(!bookingId.isEmpty())
//                        intent.putExtra(AppConstants.BOOKING_ID,Integer.parseInt(bookingId));
//            }
//
//
//        }
//        else  if(status.equalsIgnoreCase(AppConstants.COMPLETED_BOOKING))
//        {
//            if(spId!=null&&spName!=null) {
////                intent = new Intent(this, FeedbackActivity.class);
//                intent = new Intent(this, RatingActivity.class);
//                intent.putExtra(AppConstants.BOOKING_ID,bookingId);
//                intent.putExtra(AppConstants.SP_NAME,spName);
//                if(spImageUrl!=null)
//                intent.putExtra(AppConstants.SP_PROFILE,spImageUrl);
//                intent.putExtra(AppConstants.SP_ID,spId);
//            }
//            else
//            intent = new Intent(this, MainActivity.class);
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
//        else {
//             intent = new Intent(this, MainActivity.class);
//             intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//         }
//
//
//        Log.d("Gcm", "internet");
//
//        PendingIntent pendingIntent = null;
//             pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
           Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(icon).setColor(getNotificationIcon())
                .setSmallIcon( R.mipmap.ic_launcher)
                .setContentTitle("Shinrai CP")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
//                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(newNum /* ID of notification */, notificationBuilder.build());
//
//
//        if (pushInterface != null) {
//            Log.d("REFRESH", "" + pushInterface);
//
//
//         if (status.equalsIgnoreCase("Completed") ) {
//                Log.d("TaskCompleted", "booking asign gcm" + pushInterface);
//             System.out.println("notify num== "+newNum);
//                pushInterface.showTwoButtonAlert(message,status,bookingId,name,imageUrl,spId,newNum);
//
//            }
//            else
//         {
//             Log.d("Status== ", "booking asign gcm" + status);
//             pushInterface.showTwoButtonAlert(message,status,bookingId,name,imageUrl,spId,newNum);
//         }
//        }


    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? getResources().getColor(R.color.appBackgroundColor) : getResources().getColor(R.color.whiteTxtColor);
    }
    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }

}




