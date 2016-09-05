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
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.Map;

import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.LocationUpdateService;
import product.clicklabs.jugnoo.home.SyncIntentService;
import product.clicklabs.jugnoo.utils.CallActivity;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.EventsHolder;

public class GCMIntentService extends FirebaseMessagingService implements Constants {

	private final String TAG = GCMIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    public static final int PROMOTION_NOTIFICATION_ID = 1212;

    public GCMIntentService() {
    }


    @SuppressWarnings("deprecation")
    private void notificationManager(Context context, String title, String message, int playSound) {
		clearNotifications(context);
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
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
			setPlaySound(builder, playSound);

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


	private void setPlaySound(NotificationCompat.Builder builder, int playSound){
		if(playSound == 1) {
			builder.setDefaults(Notification.DEFAULT_ALL);
		} else{
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
		}
	}

    @SuppressWarnings("deprecation")
    private void notificationManagerCustomID(Context context, String title, String message, int notificationId, int deepindex,
											 Bitmap bitmap, String url, int playSound, int showDialog, int showPush, int tabIndex, int flag) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent();
			Intent broadcastIntent = new Intent(Data.LOCAL_BROADCAST);
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			if("".equalsIgnoreCase(url)){
				notificationIntent.setClass(context, SplashNewActivity.class);

				deepindex = showDialog == 1 ? -1 : deepindex;
				notificationIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));
				notificationIntent.putExtra(Constants.KEY_PUSH_CLICKED, "1");
				notificationIntent.putExtra(Constants.KEY_TAB_INDEX, tabIndex);

				broadcastIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));
				broadcastIntent.putExtra(Constants.KEY_PUSH_CLICKED, "1");
				broadcastIntent.putExtra(Constants.KEY_TAB_INDEX, tabIndex);
			} else{
				notificationIntent.setData(Uri.parse(url));
				broadcastIntent.setData(Uri.parse(url));
			}
			broadcastIntent.putExtra(Constants.KEY_FLAG, flag);

			if(HomeActivity.appInterruptHandler != null && showDialog == 1){
				HomeActivity.appInterruptHandler.onShowDialogPush();
			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);

			if(bitmap == null){
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			} else{
				builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
						.setBigContentTitle(title).setSummaryText(message));
			}

            builder.setContentText(message);
            builder.setTicker(message);
			setPlaySound(builder, playSound);
            builder.setWhen(when);

            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));

            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

			if(showDialog == 1 && showPush == 0){

			} else{
				Notification notification = builder.build();
				notificationManager.notify(notificationId, notification);

				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
			}

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	@SuppressWarnings("deprecation")
	private void generateNotificationForCall(Context context, String title, String message, int notificationId,
											 String callNumber, String eta, int playSound, String clientID) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent;
			Log.d("clientID", "clientID = "+clientID);
			if(TextUtils.isEmpty(clientID)) {
				if (HomeActivity.appInterruptHandler != null) {
					notificationIntent = new Intent(context, HomeActivity.class);
				} else {
					notificationIntent = new Intent(context, SplashNewActivity.class);
				}
			} else {
//				if(!TextUtils.isEmpty(Data.currentActivity)) {
//					notificationIntent = new Intent(context, FreshActivity.class);
//				} else {
					notificationIntent = new Intent(context, SplashNewActivity.class);
//				}
				notificationIntent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientID);
			}

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			if(TextUtils.isEmpty(clientID)) {
				builder.setAutoCancel(false);
			} else {
				builder.setAutoCancel(true);
			}
			builder.setContentTitle(title);
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			setPlaySound(builder, playSound);
			builder.setWhen(when);

			if(eta != null){
				Drawable drawable = context.getResources().getDrawable(R.drawable.circle_theme_size);
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
    private void notificationManagerCustomIDAnotherApp(Context context, String title, String message,
													   int notificationId, String packageName, int playSound) {

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
			setPlaySound(builder, playSound);
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
			try {
				Prefs.with(this).save(KEY_SP_FUGU_CAMPAIGN_NAME, String.valueOf(data.get(KEY_SP_FUGU_CAMPAIGN_NAME)));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!"".equalsIgnoreCase(String.valueOf(data.get(KEY_MESSAGE)))) {

				String message = String.valueOf(data.get(KEY_MESSAGE));

				try {
					JSONObject jObj = new JSONObject(message);

					int flag = jObj.getInt(KEY_FLAG);
					String title = jObj.optString(KEY_TITLE, getResources().getString(R.string.app_name));

					int deepindex = jObj.optInt(KEY_DEEPINDEX, -1);
					String message1 = jObj.optString(KEY_MESSAGE, "");
					int tabIndex = jObj.optInt(KEY_TAB_INDEX, 0);
					int playSound = jObj.optInt(KEY_PLAY_SOUND, 1);

					if(deepindex == -1 && tabIndex > 0){
						deepindex = AppLinkIndex.FRESH_PAGE.getOrdinal();
					}


					if (PushFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.rideRequestAcceptedInterrupt(jObj);
						}
						Prefs.with(this).save(SP_CURRENT_ENGAGEMENT_ID, jObj.optString(KEY_ENGAGEMENT_ID));

						int pushCallDriver = jObj.optInt(KEY_PUSH_CALL_DRIVER, 0);
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
						if(pushCallDriver == 1 && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, null, playSound, "");
						} else{
							notificationManager(this, title, message1, playSound);
						}
						try {
							JSONObject map = new JSONObject();
							map.put(KEY_ENGAGEMENT_ID, jObj.optString(KEY_ENGAGEMENT_ID));
							NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_RIDE_ACCEPTED, map);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (PushFlags.DRIVER_ARRIVED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						String driverArrivedMessage = jObj.getString(KEY_MESSAGE);
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onDriverArrived(driverArrivedMessage);
						}

						int pushCallDriver = jObj.optInt(KEY_PUSH_CALL_DRIVER, 0);
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						if(pushCallDriver == 1 && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, driverArrivedMessage, NOTIFICATION_ID, phoneNo,
									null, playSound, "");
						} else{
							notificationManager(this, title, driverArrivedMessage, playSound);
						}

					} else if (PushFlags.RIDE_STARTED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						message1 = jObj.optString(KEY_MESSAGE, "Your ride has started");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.startRideForCustomer(0, message1);
						} else {
							String SHARED_PREF_NAME = "myPref",
									SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
									P_IN_RIDE = "P_IN_RIDE";
							SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
							Editor editor = pref.edit();
							editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
							editor.apply();
							if(!"".equalsIgnoreCase(Prefs.with(this).getString(SP_CURRENT_ENGAGEMENT_ID, ""))
									&& Prefs.with(this).getLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL,
									LOCATION_UPDATE_INTERVAL) > 0
									&& !Utils.isServiceRunning(this, LocationUpdateService.class.getName())){
								Intent intent = new Intent(this, LocationUpdateService.class);
								intent.putExtra(KEY_ONE_SHOT, false);
								startService(intent);
							}
						}
						notificationManager(this, title, message1, playSound);
						try {
							JSONObject map = new JSONObject();
							map.put(KEY_ENGAGEMENT_ID, Prefs.with(this).getString(SP_CURRENT_ENGAGEMENT_ID, ""));
							NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_RIDE_START, map);
						} catch (Exception e) {
							e.printStackTrace();
						}

						FbEvents.logEvent(this, FlurryEventNames.FB_EVENT_RIDE_STARTED);

					} else if (PushFlags.RIDE_ENDED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						message1 = jObj.optString(KEY_MESSAGE, "Your ride has ended");
						String engagementId = jObj.getString("engagement_id");

						if (HomeActivity.appInterruptHandler != null) {
							if (PassengerScreenMode.P_IN_RIDE == HomeActivity.passengerScreenMode) {
								HomeActivity.appInterruptHandler.customerEndRideInterrupt(engagementId);
							}
						}
						notificationManager(this, title, message1, playSound);
						Prefs.with(this).save(SP_CURRENT_STATE, PassengerScreenMode.P_RIDE_END.getOrdinal());
						Intent intent = new Intent(this, LocationUpdateService.class);
						stopService(intent);

					} else if (PushFlags.RIDE_REJECTED_BY_DRIVER.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.ride_cancelled_by_driver));
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.startRideForCustomer(1, message1);
						}
						notificationManager(this, title, message1, playSound);
						NudgeClient.trackEventUserId(this, FlurryEventNames.NUDGE_RIDE_CANCELLED_BY_DRIVER, null);

					} else if (PushFlags.WAITING_STARTED.getOrdinal() == flag
							|| PushFlags.WAITING_ENDED.getOrdinal() == flag) {
						message1 = jObj.getString("message");
						notificationManager(this, title, "" + message1, playSound);

					} else if (PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag) {
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						String log = jObj.getString("log");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onNoDriversAvailablePushRecieved(log);
						}
					} else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						String logMessage = jObj.getString("message");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onChangeStatePushReceived();
						}
						notificationManager(this, title, logMessage, playSound);

					} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
						if (jObj.has("client_id")) {
							String clientId = jObj.getString("client_id");
							if (AccessTokenGenerator.MEALS_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID,
										AccessTokenGenerator.MEALS_PACKAGE, playSound);
							} else if (AccessTokenGenerator.FATAFAT_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID,
										AccessTokenGenerator.FATAFAT_PACKAGE, playSound);
							} else if (AccessTokenGenerator.FATAFAT_FRESH_CLIENT_ID.equalsIgnoreCase(clientId)) {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID,
										AccessTokenGenerator.FATAFAT_FRESH_PACKAGE, playSound);
							} else {
								notificationManagerCustomIDAnotherApp(this, title, message1, PROMOTION_NOTIFICATION_ID,
										AccessTokenGenerator.AUTOS_PACKAGE, playSound);
							}
						}
						else {
							String picture = jObj.optString(KEY_PICTURE, "");
							if("".equalsIgnoreCase(picture)){
								picture = jObj.optString(KEY_IMAGE, "");
							}
							String url = jObj.optString(KEY_URL, "");
							int showDialog = jObj.optInt(Constants.KEY_SHOW_DIALOG, 0);
							int showPush = jObj.optInt(Constants.KEY_SHOW_PUSH, 1);
							if(showDialog == 1) {
								Prefs.with(this).save(SP_PUSH_DIALOG_CONTENT, message);
							}

							if(!"".equalsIgnoreCase(picture)){
								deepindex = jObj.optInt(KEY_DEEPINDEX, AppLinkIndex.NOTIFICATION_CENTER.getOrdinal());
								bigImageNotifAsync(title, message1, deepindex, picture, url, playSound, showDialog, showPush, tabIndex, flag);
							}
							else{
								deepindex = jObj.optInt(KEY_DEEPINDEX, -1);
								notificationManagerCustomID(this, title, message1, PROMOTION_NOTIFICATION_ID, deepindex,
										null, url, playSound, showDialog, showPush, tabIndex, flag);
							}

							Prefs.with(this).save(SP_LAST_PUSH_RECEIVED_TIME, System.currentTimeMillis());
						}

						if(deepindex == AppLinkIndex.INVITE_AND_EARN.getOrdinal()){
							FlurryEventLogger.eventWithSessionOpenAndCloseMap(this, FlurryEventNames.INVITE_PUSH_RECEIVED);
						}
						FlurryEventLogger.eventWithSessionOpenAndCloseMap(this, FlurryEventNames.TO_WHOM_A_PUSH_WAS_DELIVERED);
					} else if (PushFlags.PAYMENT_RECEIVED.getOrdinal() == flag) {
						double balance = jObj.getDouble("balance");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onJugnooCashAddedByDriver(balance, message1);
						}
						notificationManager(this, title, message1, playSound);

					} else if (PushFlags.EMERGENCY_CONTACT_VERIFIED.getOrdinal() == flag) {
						int emergencyContactId = jObj.getInt("id");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onEmergencyContactVerified(emergencyContactId);
						}
						notificationManager(this, title, message1, playSound);

					} else if (PushFlags.OTP_VERIFIED_BY_CALL.getOrdinal() == flag) {
						String otp = jObj.getString("message");
						if(Prefs.with(this).getString(Constants.SP_OTP_SCREEN_OPEN, "")
								.equalsIgnoreCase(OTPConfirmScreen.class.getName())) {
							Intent otpConfirmScreen = new Intent(this, OTPConfirmScreen.class);
							otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							otpConfirmScreen.putExtra("otp", otp);
							startActivity(otpConfirmScreen);
						}
						else if(Prefs.with(this).getString(Constants.SP_OTP_SCREEN_OPEN, "")
								.equalsIgnoreCase(PhoneNoOTPConfirmScreen.class.getName())){
							Intent otpConfirmScreen = new Intent(this, PhoneNoOTPConfirmScreen.class);
							otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							otpConfirmScreen.putExtra("otp", otp);
							startActivity(otpConfirmScreen);
						}
						notificationManagerCustomID(this, title, "Your account has been verified", NOTIFICATION_ID, -1,
								null, "", playSound, 0, 1, 0, flag);

					}
					else if (PushFlags.CLEAR_ALL_MESSAGE.getOrdinal() == flag) {
						Database2.getInstance(this).deleteNotificationTable();
						notifyActivityOnPush();
					}
					else if (PushFlags.DELETE_NOTIFICATION_ID.getOrdinal() == flag) {
						if(jObj.has(KEY_NOTIFICATION_ID)) {
							int id = jObj.optInt(KEY_NOTIFICATION_ID, -1);
							if(id != -1) {
								Database2.getInstance(this).deleteNotification(id);
								notifyActivityOnPush();
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
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, eta, playSound, "");
                        }
                    } else if(PushFlags.INITIATE_PAYTM_RECHARGE.getOrdinal() == flag){
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, message);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
						notificationManager(this, title, message1, playSound);

					} else if(PushFlags.SYNC_PARA.getOrdinal() == flag){
						Intent synIntent = new Intent(this, SyncIntentService.class);
						synIntent.putExtra(KEY_START_TIME, jObj.getString(KEY_START_TIME));
						synIntent.putExtra(KEY_END_TIME, jObj.getString(KEY_END_TIME));
						startService(synIntent);

					} else if(PushFlags.UPDATE_POOL_RIDE_STATUS.getOrdinal() == flag){
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onUpdatePoolRideStatus(jObj);
						}

					}else if (PushFlags.CUSTOMER_EMERGENCY_LOCATION.getOrdinal() == flag){
						if(!Utils.isServiceRunning(this, LocationUpdateService.class.getName())) {
							Intent intent = new Intent(this, LocationUpdateService.class);
							intent.putExtra(KEY_ONE_SHOT, true);
							startService(intent);
						} else{
							Intent intent1 = new Intent();
							intent1.setAction(ACTION_LOCATION_UPDATE);
							intent1.putExtra(KEY_LATITUDE, LocationFetcher.getSavedLatFromSP(this));
							intent1.putExtra(KEY_LONGITUDE, LocationFetcher.getSavedLngFromSP(this));
							intent1.putExtra(KEY_EMERGENCY_LOC, true);
							sendBroadcast(intent1);
						}
					} else if (PushFlags.ORDER_DISPATCH.getOrdinal() == flag) {

						String clientId = jObj.optString(KEY_CLIENT_ID, "");
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
						if(!TextUtils.isEmpty(phoneNo)){
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, null, playSound, clientId);
						} else{
							notificationManager(this, title, message1, playSound);
						}
					}

					incrementPushCounter(jObj, flag);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private void incrementPushCounter(JSONObject jObj, int flag){
		try {
			boolean tryToSave = false;
			if(PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag){
				tryToSave = true;
			} else if(1 == jObj.optInt(KEY_SAVE_NOTIFICATION, 0)){
				tryToSave = true;
			}

			if(tryToSave) {
				int notificationId = jObj.optInt(KEY_NOTIFICATION_ID, flag);
				if(PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag && notificationId != flag){
					FlurryEventLogger.event(this, FlurryEventNames.CAMPAIGN_+notificationId);
				}

				Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				notifyActivityOnPush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void notifyActivityOnPush(){
		if (EventsHolder.displayPushHandler != null) {
			EventsHolder.displayPushHandler.onDisplayMessagePushReceived();
		} else if(HomeActivity.appInterruptHandler != null){
			HomeActivity.appInterruptHandler.onDisplayMessagePushReceived();
		}
	}

	public void bigImageNotifAsync(final String title, final String message, final int deepindex,
								   final String picture, final String url, final int playSound,
								   final int showDialog, final int showPush, final int tabIndex, final int flag){
		try {
			RequestCreator requestCreator = Picasso.with(GCMIntentService.this).load(picture);
			Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
					try {
						notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID,
								deepindex, bitmap, url, playSound, showDialog, showPush, tabIndex, flag);
					} catch (Exception e) {
						e.printStackTrace();
						notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID, deepindex,
								null, url, playSound, showDialog, showPush, tabIndex, flag);
					}
				}

				@Override
				public void onBitmapFailed(Drawable drawable) {

				}

				@Override
				public void onPrepareLoad(Drawable drawable) {

				}
			};
//			PicassoTools.into(requestCreator, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static Bitmap drawableToBitmapPlusText(Context context, Drawable drawable, String text, float fontSize) {
		Bitmap bitmap;

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
		textView1.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);

		final Rect boundsText1 = new Rect();

		final Paint paint1 = textView1.getPaint();
		paint1.getTextBounds(text, 0, textView1.length(), boundsText1);
		paint1.setTextAlign(Paint.Align.CENTER);
		paint1.setColor(Color.WHITE);

		final TextView textView2 = new TextView(context);
		textView2.setText("min");
		textView2.setTextSize(fontSize-4);
		textView2.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);

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
