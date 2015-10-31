package product.clicklabs.jugnoo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

import java.net.URL;

import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.EventsHolder;

public class GCMIntentService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 1;
    public static final int PROMOTION_NOTIFICATION_ID = 1212;

    public GCMIntentService() {
    }


    @SuppressWarnings("deprecation")
    private void notificationManager(Context context, String message, boolean ring) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent notificationIntent = new Intent(context, SplashNewActivity.class);


            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Autos");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);


            if (ring) {
                builder.setLights(Color.GREEN, 500, 500);
            } else {
                builder.setDefaults(Notification.DEFAULT_ALL);
            }

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);


            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID, notification);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire(15000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("deprecation")
    private void notificationManagerResume(Context context, String message, boolean ring) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent notificationIntent = new Intent(context, HomeActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Autos");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);

            if (ring) {
                builder.setLights(Color.GREEN, 500, 500);
            } else {
                builder.setDefaults(Notification.DEFAULT_ALL);
            }

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);


            Notification notification = builder.build();
            notificationManager.notify(NOTIFICATION_ID, notification);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire(15000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @SuppressWarnings("deprecation")
    private void notificationManagerCustomID(Context context, String message, int notificationId, int deepindex) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent notificationIntent = new Intent(context, SplashNewActivity.class);
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			notificationIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Autos");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);

            Notification notification = builder.build();
            notificationManager.notify(notificationId, notification);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire(15000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("deprecation")
    private void notificationManagerCustomIDAnotherApp(Context context, String message, int notificationId, String packageName) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent appOpenIntent;
            try {
                PackageManager manager = context.getPackageManager();
                appOpenIntent = manager.getLaunchIntentForPackage(packageName);
                if (appOpenIntent == null) {
                    throw new PackageManager.NameNotFoundException();
                }
                appOpenIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            } catch (Exception e) {
                appOpenIntent = new Intent(Intent.ACTION_VIEW);
                appOpenIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            }

            appOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, appOpenIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Autos");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);

            Notification notification = builder.build();
            notificationManager.notify(notificationId, notification);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire(15000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	@SuppressWarnings("deprecation")
	private void notificationManagerCustomIDWithBitmap(Context context, String message, int notificationId, int deepindex, Bitmap bitmap) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Log.v("message", "," + message);

			Intent notificationIntent = new Intent(context, SplashNewActivity.class);
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			notificationIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);



			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle("Autos");
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notification_icon);
			builder.setContentIntent(intent);
			builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle("Autos").setSummaryText(message));
			builder.setContentText(message);
			builder.setTicker(message);

			Notification notification = builder.build();
			notificationManager.notify(notificationId, notification);

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
	public void onMessageReceived(String from, Bundle data) {
        Log.e("Recieved a gcm message arg1...", "," + data);

		try {
			Log.e("Recieved a gcm message arg1...", "," + data);

			if (!"".equalsIgnoreCase(data.getString("message", ""))) {

				String message = data.getString("message");

				try {
					JSONObject jObj = new JSONObject(message);

					int flag = jObj.getInt("flag");

					if (PushFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.rideRequestAcceptedInterrupt(jObj);
							notificationManagerResume(this, "Your request has been accepted", false);
						} else {
							notificationManager(this, "Your request has been accepted", false);
						}
					} else if (PushFlags.RIDE_STARTED.getOrdinal() == flag) {

						if (HomeActivity.appInterruptHandler != null) {
							notificationManagerResume(this, "Your ride has started.", false);
							HomeActivity.appInterruptHandler.startRideForCustomer(0);
						} else {
							String SHARED_PREF_NAME = "myPref",
									SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
									P_IN_RIDE = "P_IN_RIDE";
							SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
							Editor editor = pref.edit();
							editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
							editor.commit();

							notificationManager(this, "Your ride has started.", false);
						}
					} else if (PushFlags.DRIVER_ARRIVED.getOrdinal() == flag) {

						String driverArrivedMessage = jObj.getString("message");

						if (HomeActivity.appInterruptHandler != null) {
							notificationManagerResume(this, driverArrivedMessage, false);
							HomeActivity.appInterruptHandler.onDriverArrived(driverArrivedMessage);
						} else {
							notificationManager(this, driverArrivedMessage, false);
						}

					} else if (PushFlags.RIDE_ENDED.getOrdinal() == flag) {
						String engagementId = jObj.getString("engagement_id");

						if (HomeActivity.appInterruptHandler != null) {
							if (PassengerScreenMode.P_IN_RIDE == HomeActivity.passengerScreenMode) {
								notificationManagerResume(this, "Your ride has ended.", false);
								HomeActivity.appInterruptHandler.customerEndRideInterrupt(engagementId);
							}
						} else {
							notificationManager(this, "Your ride has ended.", false);
						}
					} else if (PushFlags.RIDE_REJECTED_BY_DRIVER.getOrdinal() == flag) {
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.startRideForCustomer(1);
							notificationManagerResume(this, "Your ride has been cancelled due to an unexpected issue", false);
						} else {
							notificationManager(this, "Your ride has been cancelled due to an unexpected issue", false);
						}
					} else if (PushFlags.WAITING_STARTED.getOrdinal() == flag || PushFlags.WAITING_ENDED.getOrdinal() == flag) {
						String message1 = jObj.getString("message");
						if (HomeActivity.activity == null) {
							notificationManager(this, "" + message1, false);
						} else {
							notificationManagerResume(this, "" + message1, false);
						}
					} else if (PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag) {
						String log = jObj.getString("log");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onNoDriversAvailablePushRecieved(log);
						}
					} else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {
						String logMessage = jObj.getString("message");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onChangeStatePushReceived();
							notificationManagerResume(this, logMessage, false);
						} else {
							notificationManager(this, logMessage, false);
						}
					} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
						String message1 = jObj.getString("message");

						if (jObj.has("client_id")) {
							Log.e("jObj=", "=" + jObj);
							String clientId = jObj.getString("client_id");
							if (AccessTokenGenerator.MEALS_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, message1, PROMOTION_NOTIFICATION_ID, AccessTokenGenerator.MEALS_PACKAGE);
							} else if (AccessTokenGenerator.FATAFAT_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, message1, PROMOTION_NOTIFICATION_ID, AccessTokenGenerator.FATAFAT_PACKAGE);
							} else {
								notificationManagerCustomIDAnotherApp(this, message1, PROMOTION_NOTIFICATION_ID, AccessTokenGenerator.AUTOS_PACKAGE);
							}
						}
						else {
							String picture = jObj.optString("picture", "");
							if("".equalsIgnoreCase(picture)){
								picture = jObj.optString("image", "");
							}

							if(!"".equalsIgnoreCase(picture)){
								int deepindex = jObj.optInt("deepindex", AppLinkIndex.NOTIFICATION_CENTER.getOrdinal());
								new BigImageNotifAsync(message1, deepindex, picture).execute();
							}
							else{
								int deepindex = jObj.optInt("deepindex", -1);
								notificationManagerCustomID(this, message1, PROMOTION_NOTIFICATION_ID, deepindex);
							}
						}

						try {
							String picture = jObj.optString("image", "");
							if("".equalsIgnoreCase(picture)){
								picture = jObj.optString("picture", "");
							}
							// store push in database for notificaion center screen...
							String pushArrived = DateOperations.getCurrentTimeInUTC();
							if(jObj.has("timeToDisplay") && jObj.has("timeTillDisplay")) {
								Database2.getInstance(this).insertNotification(pushArrived, message1, "0", jObj.getString("timeToDisplay"), jObj.getString("timeTillDisplay"), picture);
								Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
							} else if(jObj.has("timeToDisplay")){
								Database2.getInstance(this).insertNotification(pushArrived, message1, "0", jObj.getString("timeToDisplay"), "", picture);
								Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
							} else if(jObj.has("timeTillDisplay")){
								Database2.getInstance(this).insertNotification(pushArrived, message1, "0", "0", jObj.getString("timeTillDisplay"), picture);
								Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0)+1));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}


					} else if (PushFlags.PAYMENT_RECEIVED.getOrdinal() == flag) {
						String message1 = jObj.getString("message");
						double balance = jObj.getDouble("balance");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onJugnooCashAddedByDriver(balance, message1);
							notificationManagerResume(this, message1, false);
						} else {
							notificationManager(this, message1, false);
						}
					} else if (PushFlags.EMERGENCY_CONTACT_VERIFIED.getOrdinal() == flag) {
						String message1 = jObj.getString("message");
						int emergencyContactId = jObj.getInt("id");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onEmergencyContactVerified(emergencyContactId);
							notificationManagerResume(this, message1, false);
						} else {
							notificationManager(this, message1, false);
						}
					} else if (PushFlags.OTP_VERIFIED_BY_CALL.getOrdinal() == flag) {
						String otp = jObj.getString("message");
						if(OTPConfirmScreen.OTP_SCREEN_OPEN != null) {
							Intent otpConfirmScreen = new Intent(this, OTPConfirmScreen.class);
							otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							otpConfirmScreen.putExtra("otp", otp);
							startActivity(otpConfirmScreen);
						}
						else if(PhoneNoOTPConfirmScreen.OTP_SCREEN_OPEN != null){
							Intent otpConfirmScreen = new Intent(this, PhoneNoOTPConfirmScreen.class);
							otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							otpConfirmScreen.putExtra("otp", otp);
							startActivity(otpConfirmScreen);
						}
						notificationManagerCustomID(this, "Your account has been verified", NOTIFICATION_ID, -1);

					} else if (PushFlags.CLEAR_ALL_MESSAGE.getOrdinal() == flag) {
						Database2.getInstance(this).deleteNotificationTable();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			Log.e("Recieved exception message arg1...", "," + data);
			Log.e("exception", "," + e);
		}
    }

    private class BigImageNotifAsync extends AsyncTask<String, String, Bitmap> {

        private Bitmap bitmap = null;
        private String message, picture;
        private int deepindex;

        public BigImageNotifAsync(String message, int deepindex, String picture){
            this.deepindex = deepindex;
            this.picture = picture;
            this.message = message;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(picture);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }catch (Exception e){
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // execution of result of Long time consuming operation
            try {
                notificationManagerCustomIDWithBitmap(GCMIntentService.this, message, PROMOTION_NOTIFICATION_ID, deepindex, result);
				if(EventsHolder.displayPushHandler != null){
					EventsHolder.displayPushHandler.onDisplayMessagePushReceived();
				}
            }catch (Exception e){
                e.printStackTrace();
                notificationManagerCustomID(GCMIntentService.this, message, PROMOTION_NOTIFICATION_ID, deepindex);
            }

        }

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }
    }



}
