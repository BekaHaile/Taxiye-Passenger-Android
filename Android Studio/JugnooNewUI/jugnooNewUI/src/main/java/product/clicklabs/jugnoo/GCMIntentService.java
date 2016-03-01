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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.CallActivity;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.EventsHolder;

public class GCMIntentService extends GcmListenerService implements Constants {

	private final String TAG = GCMIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    public static final int PROMOTION_NOTIFICATION_ID = 1212;

    public GCMIntentService() {
    }


    @SuppressWarnings("deprecation")
    private void notificationManager(Context context, String title, String message, boolean ring) {
		clearNotifications(context);
        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, SplashNewActivity.class);


            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
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

    @SuppressWarnings("deprecation")
    private void notificationManagerResume(Context context, String title, String message, boolean ring) {
		clearNotifications(context);
        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, HomeActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle(title);
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




    @SuppressWarnings("deprecation")
    private void notificationManagerCustomID(Context context, String title, String message, int notificationId, int deepindex) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, SplashNewActivity.class);
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			notificationIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setWhen(when);

//			Drawable drawable = context.getResources().getDrawable(R.drawable.circle_yellow_size);
//			builder.setLargeIcon(drawableToBitmapPlusText(context, drawable, "99", 18));

            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));

            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

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
	private void generateNotificationForCall(Context context, String title, String message, int notificationId, String callNumber, String eta) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent;
			if(HomeActivity.appInterruptHandler != null){
				notificationIntent = new Intent(context, HomeActivity.class);
			} else{
				notificationIntent = new Intent(context, SplashNewActivity.class);
			}

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(false);
			builder.setContentTitle(title);
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);

			if(eta != null){
				Drawable drawable = context.getResources().getDrawable(R.drawable.circle_yellow_size);
				builder.setLargeIcon(drawableToBitmapPlusText(context, drawable, eta, 16));
			} else{
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
			}

//			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notification_icon);

			Intent intentCall = new Intent(context, CallActivity.class);
			intentCall.putExtra(context.getResources().getString(R.string.call_number), callNumber);
			PendingIntent pendingIntentCall = PendingIntent.getActivity(this, 123411, intentCall, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.addAction(android.R.drawable.sym_action_call, context.getResources().getString(R.string.call_driver), pendingIntentCall);

			builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

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
    private void notificationManagerCustomIDAnotherApp(Context context, String title, String message, int notificationId, String packageName) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

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
	private void notificationManagerCustomIDWithBitmap(Context context, String title, String message, int notificationId, int deepindex, Bitmap bitmap) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent = new Intent(context, SplashNewActivity.class);
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			notificationIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);



			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle(title);
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notification_icon);
			builder.setContentIntent(intent);
			builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(title).setSummaryText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

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
        Log.e(TAG, "onMessageReceived data=" + data);

		try {

			if (!"".equalsIgnoreCase(data.getString(KEY_MESSAGE, ""))) {

				String message = data.getString(KEY_MESSAGE);

				try {
					JSONObject jObj = new JSONObject(message);

					int flag = jObj.getInt(KEY_FLAG);
					String title = jObj.optString(KEY_TITLE, getResources().getString(R.string.app_name));

					int deepindex = jObj.optInt("deepindex", -1);
					String message1 = jObj.optString(KEY_MESSAGE, "");


					if (PushFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.rideRequestAcceptedInterrupt(jObj);
						}

						int pushCallDriver = jObj.optInt(KEY_PUSH_CALL_DRIVER, 0);
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
						if(pushCallDriver == 1 && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, null);
						} else{
							if (HomeActivity.appInterruptHandler != null) {
								notificationManagerResume(this, title, message1, false);
							} else {
								notificationManager(this, title, message1, false);
							}
						}

					} else if (PushFlags.DRIVER_ARRIVED.getOrdinal() == flag) {
						String driverArrivedMessage = jObj.getString(KEY_MESSAGE);
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onDriverArrived(driverArrivedMessage);
						}

						int pushCallDriver = jObj.optInt(KEY_PUSH_CALL_DRIVER, 0);
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						if(pushCallDriver == 1 && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, driverArrivedMessage, NOTIFICATION_ID, phoneNo, null);
						} else{
							if (HomeActivity.appInterruptHandler != null) {
								notificationManagerResume(this, title, driverArrivedMessage, false);
							} else {
								notificationManager(this, title, driverArrivedMessage, false);
							}
						}

					} else if (PushFlags.RIDE_STARTED.getOrdinal() == flag) {
						message1 = jObj.optString(KEY_MESSAGE, "Your ride has started");
						if (HomeActivity.appInterruptHandler != null) {
							notificationManagerResume(this, title, message1, false);
							HomeActivity.appInterruptHandler.startRideForCustomer(0);
						} else {
							String SHARED_PREF_NAME = "myPref",
									SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
									P_IN_RIDE = "P_IN_RIDE";
							SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
							Editor editor = pref.edit();
							editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
							editor.commit();

							notificationManager(this, title, message1, false);
						}
					} else if (PushFlags.RIDE_ENDED.getOrdinal() == flag) {
						message1 = jObj.optString(KEY_MESSAGE, "Your ride has ended");
						String engagementId = jObj.getString("engagement_id");

						if (HomeActivity.appInterruptHandler != null) {
							if (PassengerScreenMode.P_IN_RIDE == HomeActivity.passengerScreenMode) {
								notificationManagerResume(this, title, message1, false);
								HomeActivity.appInterruptHandler.customerEndRideInterrupt(engagementId);
							}
						} else {
							notificationManager(this, title, message1, false);
						}
					} else if (PushFlags.RIDE_REJECTED_BY_DRIVER.getOrdinal() == flag) {
						message1 = jObj.optString(KEY_MESSAGE, "Your ride has been cancelled due to an unexpected issue");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.startRideForCustomer(1);
							notificationManagerResume(this, title, message1, false);
						} else {
							notificationManager(this, title, message1, false);
						}
					} else if (PushFlags.WAITING_STARTED.getOrdinal() == flag || PushFlags.WAITING_ENDED.getOrdinal() == flag) {
						message1 = jObj.getString("message");
						if (HomeActivity.activity == null) {
							notificationManager(this, title, "" + message1, false);
						} else {
							notificationManagerResume(this, title, "" + message1, false);
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
							notificationManagerResume(this, title, logMessage, false);
						} else {
							notificationManager(this, title, logMessage, false);
						}
					} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {

						if (jObj.has("client_id")) {
							String clientId = jObj.getString("client_id");
							if (AccessTokenGenerator.MEALS_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID, AccessTokenGenerator.MEALS_PACKAGE);
							} else if (AccessTokenGenerator.FATAFAT_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID, AccessTokenGenerator.FATAFAT_PACKAGE);
							} else {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID, AccessTokenGenerator.AUTOS_PACKAGE);
							}
						}
						else {
							String picture = jObj.optString("picture", "");
							if("".equalsIgnoreCase(picture)){
								picture = jObj.optString("image", "");
							}

							if(!"".equalsIgnoreCase(picture)){
								deepindex = jObj.optInt("deepindex", AppLinkIndex.NOTIFICATION_CENTER.getOrdinal());
								new BigImageNotifAsync(title, message1, deepindex, picture).execute();
							}
							else{
								deepindex = jObj.optInt("deepindex", -1);
								notificationManagerCustomID(this, title, message1, PROMOTION_NOTIFICATION_ID, deepindex);
							}
						}

						if(deepindex == AppLinkIndex.INVITE_AND_EARN.getOrdinal()){
							HashMap<String, String> map = new HashMap<String, String>();
							map.put(KEY_USER_ID, Prefs.with(this).getString(SP_USER_ID, ""));
							FlurryEventLogger.eventWithSessionOpenAndClose(this, FlurryEventNames.INVITE_PUSH_RECEIVED, map);
						}
					} else if (PushFlags.PAYMENT_RECEIVED.getOrdinal() == flag) {
						double balance = jObj.getDouble("balance");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onJugnooCashAddedByDriver(balance, message1);
							notificationManagerResume(this, title, message1, false);
						} else {
							notificationManager(this, title, message1, false);
						}
					} else if (PushFlags.EMERGENCY_CONTACT_VERIFIED.getOrdinal() == flag) {
						int emergencyContactId = jObj.getInt("id");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onEmergencyContactVerified(emergencyContactId);
							notificationManagerResume(this, title, message1, false);
						} else {
							notificationManager(this, title, message1, false);
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
						notificationManagerCustomID(this, title, "Your account has been verified", NOTIFICATION_ID, -1);
					}
					else if (PushFlags.CLEAR_ALL_MESSAGE.getOrdinal() == flag) {
						Database2.getInstance(this).deleteNotificationTable();
						if (EventsHolder.displayPushHandler != null) {
							EventsHolder.displayPushHandler.onDisplayMessagePushReceived();
						}
					}
					else if (PushFlags.DELETE_NOTIFICATION_ID.getOrdinal() == flag) {
						if(jObj.has(KEY_NOTIFICATION_ID)) {
							int id = jObj.optInt(KEY_NOTIFICATION_ID, -1);
							if(id != -1) {
								Database2.getInstance(this).deleteNotification(id);
								if (EventsHolder.displayPushHandler != null) {
									EventsHolder.displayPushHandler.onDisplayMessagePushReceived();
								}
							}
						}
					}
					else if (PushFlags.UPLOAD_CONTACTS_ERROR.getOrdinal() == flag) {
						if(HomeActivity.appInterruptHandler != null && Utils.isForeground(this)){
							HomeActivity.appInterruptHandler.showDialog(message1);
						}
						else{
							Prefs.with(this).save(SPLabels.UPLOAD_CONTACTS_ERROR, message1);
						}
					}  else if(PushFlags.DRIVER_ETA.getOrdinal() == flag){
                        String eta = jObj.optString(KEY_ETA, "-1");
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
                        if(!"-1".equalsIgnoreCase(eta) && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, eta);
                        }
                    }

					savePush(jObj, flag, title, message1, deepindex);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private ArrayList<Integer> dontSavePushes = null;
	private ArrayList<Integer> getDontSavePushesArray(){
		if(dontSavePushes == null){
			dontSavePushes = new ArrayList<>();
//			dontSavePushes.add(PushFlags.WAITING_STARTED.getOrdinal());
//			dontSavePushes.add(PushFlags.WAITING_ENDED.getOrdinal());
//			dontSavePushes.add(PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal());
//			dontSavePushes.add(PushFlags.CHANGE_STATE.getOrdinal());
//			dontSavePushes.add(PushFlags.PAYMENT_RECEIVED.getOrdinal());
//			dontSavePushes.add(PushFlags.CLEAR_ALL_MESSAGE.getOrdinal());
//			dontSavePushes.add(PushFlags.DELETE_NOTIFICATION_ID.getOrdinal());
//			dontSavePushes.add(PushFlags.UPLOAD_CONTACTS_ERROR.getOrdinal());
//			dontSavePushes.add(PushFlags.DRIVER_ETA.getOrdinal());
		}
		return dontSavePushes;
	}

	private void savePush(JSONObject jObj, int flag, String title, String message1, int deepindex){
		try {
			boolean tryToSave = false;
			if(PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag){
				tryToSave = true;
			} else if(!getDontSavePushesArray().contains(flag)){
				int saveNotification = jObj.optInt(KEY_SAVE_NOTIFICATION, 0);
				if(1 == saveNotification){
					tryToSave = true;
				}
			}

			if(tryToSave && !"".equalsIgnoreCase(message1)) {
				String picture = jObj.optString("image", "");
				if ("".equalsIgnoreCase(picture)) {
					picture = jObj.optString("picture", "");
				}
//				if(PushFlags.DISPLAY_MESSAGE.getOrdinal() != flag) {
//					message1 = title + "\n" + message1;
//				}

				message1 = title + "\n" + message1;

				int notificationId = jObj.optInt(KEY_NOTIFICATION_ID, flag);

				// store push in database for notificaion center screen...
				String pushArrived = DateOperations.getCurrentTimeInUTC();
				if (jObj.has("timeToDisplay") && jObj.has("timeTillDisplay")) {
					Database2.getInstance(this).insertNotification(this, notificationId, pushArrived, message1, "" + deepindex,
							jObj.getString("timeToDisplay"), jObj.getString("timeTillDisplay"), picture);
					Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				} else if (jObj.has("timeToDisplay")) {
					Database2.getInstance(this).insertNotification(this, notificationId, pushArrived, message1, "" + deepindex,
							jObj.getString("timeToDisplay"), "", picture);
					Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				} else if (jObj.has("timeTillDisplay")) {
					Database2.getInstance(this).insertNotification(this, notificationId, pushArrived, message1, "" + deepindex,
							"0", jObj.getString("timeTillDisplay"), picture);
					Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				}
				if (EventsHolder.displayPushHandler != null) {
					EventsHolder.displayPushHandler.onDisplayMessagePushReceived();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    private class BigImageNotifAsync extends AsyncTask<String, String, Bitmap> {

        private Bitmap bitmap = null;
        private String title, message, picture;
        private int deepindex;

        public BigImageNotifAsync(String title, String message, int deepindex, String picture){
            this.deepindex = deepindex;
            this.picture = picture;
			this.title = title;
            this.message = message;
        }

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
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
                notificationManagerCustomIDWithBitmap(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID, deepindex, result);
				if(EventsHolder.displayPushHandler != null){ EventsHolder.displayPushHandler.onDisplayMessagePushReceived(); }
            }catch (Exception e){
                e.printStackTrace();
                notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID, deepindex);
            }
        }

    }



	public static Bitmap drawableToBitmapPlusText(Context context, Drawable drawable, String text, float fontSize) {
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if(bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		final TextView textView1 = new TextView(context);
		textView1.setText(text);
		textView1.setTextSize(fontSize);
		textView1.setTypeface(Fonts.latoRegular(context), Typeface.BOLD);

		final Rect boundsText1 = new Rect();

		final Paint paint1 = textView1.getPaint();
		paint1.getTextBounds(text, 0, textView1.length(), boundsText1);
		paint1.setTextAlign(Paint.Align.CENTER);
		paint1.setColor(Color.WHITE);

		final TextView textView2 = new TextView(context);
		textView2.setText("min");
		textView2.setTextSize(fontSize-4);
		textView2.setTypeface(Fonts.latoRegular(context), Typeface.BOLD);

		final Rect boundsText2 = new Rect();

		final Paint paint2 = textView2.getPaint();
		paint2.getTextBounds("min", 0, textView2.length(), boundsText2);
		paint2.setTextAlign(Paint.Align.CENTER);
		paint2.setColor(Color.WHITE);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		canvas.drawText(text, canvas.getWidth() / 2, Utils.dpToPx(context, 24), paint1);
		canvas.drawText("min", canvas.getWidth() / 2, Utils.dpToPx(context, 26) + boundsText1.height(), paint2);

		return bitmap;
	}



}
