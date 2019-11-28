package product.clicklabs.jugnoo;

import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fugu.FuguNotificationConfig;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.Map;

import product.clicklabs.jugnoo.apis.ApiTrackPush;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.LocationUpdateService;
import product.clicklabs.jugnoo.home.SyncIntentService;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.promotion.models.Promo;
import product.clicklabs.jugnoo.utils.CallActivity;
import product.clicklabs.jugnoo.utils.FbEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Foreground;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.EventsHolder;

public class GCMIntentService extends FirebaseMessagingService implements Constants, GAAction {

	private final String TAG = GCMIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    public static final int PROMOTION_NOTIFICATION_ID = 1212;
	private FuguNotificationConfig fuguNotificationConfig = new FuguNotificationConfig();
	private String deliveryId;

    public GCMIntentService() {
    }


    private void notificationManager(Context context, String title, String message, int playSound) {
		clearNotifications(context);
        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = getNotificationManager(this, Constants.NOTIF_CHANNEL_DEFAULT);

			Intent notificationIntent;
			if(HomeActivity.appInterruptHandler != null){
				notificationIntent = new Intent(context, HomeActivity.class);
			} else{
				notificationIntent = new Intent(context, SplashNewActivity.class);
			}

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Constants.NOTIF_CHANNEL_DEFAULT);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
            builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);
			setPlaySound(builder, playSound);

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setSmallIcon(R.mipmap.notification_icon);
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

            Notification notification = builder.build();
			hideSmallIcon(notification);
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

	private void notificationManagerCustomID(Context context, String title, String message, int notificationId, int deepindex,
											 Bitmap bitmap, String url, int playSound, int showDialog, int showPush, int tabIndex, int flag){
		notificationManagerCustomID(context, title, message, notificationId, deepindex, bitmap, url, playSound, showDialog, showPush, tabIndex, flag,
				0, ProductType.AUTO.getOrdinal(), 0, -1, -1);
	}


	// 0, ProductType.AUTO.getOrdinal(), 0, -1
    private void notificationManagerCustomID(Context context, String title, String message, int notificationId, int deepindex,
											 Bitmap bitmap, String url, int playSound, int showDialog, int showPush, int tabIndex, int flag,
											 int orderId, int productType, int campaignId, int postId, int postNotificationId) {

        try {
			if(TextUtils.isEmpty(message)){
				return;
			}

            long when = System.currentTimeMillis();

            NotificationManager notificationManager = getNotificationManager(this, Constants.NOTIF_CHANNEL_DEFAULT);

            Intent notificationIntent = new Intent();
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			if("".equalsIgnoreCase(url)){
				notificationIntent.setClass(context, SplashNewActivity.class);

				deepindex = showDialog == 1 ? -1 : deepindex;
				notificationIntent.setData(Uri.parse("jungooautos://app?deepindex=" + deepindex));
				notificationIntent.putExtra(Constants.KEY_PUSH_CLICKED, "1");
				notificationIntent.putExtra(Constants.KEY_TAB_INDEX, tabIndex);
				notificationIntent.putExtra(Constants.KEY_ORDER_ID, orderId);
				notificationIntent.putExtra(Constants.KEY_PRODUCT_TYPE, productType);
				notificationIntent.putExtra(Constants.KEY_CAMPAIGN_ID, campaignId);
				notificationIntent.putExtra(Constants.KEY_POST_ID, postId);
				notificationIntent.putExtra(Constants.KEY_POST_NOTIFICATION_ID, postNotificationId);
				if(deliveryId!=null && !deliveryId.isEmpty()) {
					notificationIntent.putExtra(KEY_DELIVERY_ID,deliveryId);
				}

			} else{
				notificationIntent.setData(Uri.parse(url));
			}

			if(HomeActivity.appInterruptHandler != null && showDialog == 1){
				HomeActivity.appInterruptHandler.onShowDialogPush();
			}

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Constants.NOTIF_CHANNEL_DEFAULT);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);

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

            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

            builder.setSmallIcon(R.mipmap.notification_icon);
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

			if(showDialog == 1 && showPush == 0){

			} else{
				Notification notification = builder.build();
				hideSmallIcon(notification);
				notificationManager.notify(notificationId, notification);

				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
			}

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static NotificationManager getNotificationManager(final Context context, String channel){

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// The user-visible name of the channel.
			CharSequence name = context.getString(R.string.notification_channel_default);
			// The user-visible description of the channel.
			String description = context.getString(R.string.notification_channel_description_default);
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
			// Configure the notification channel.
			mChannel.setDescription(description);
			notificationManager.createNotificationChannel(mChannel);
		}
        return notificationManager;
	}

	public static void setSilentNotificationChannel(final Context context, String channel){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			CharSequence name = context.getString(R.string.notification_channel_silent);
			String description = context.getString(R.string.notification_channel_description_default);
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
			// Configure the notification channel.
			mChannel.setDescription(description);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(mChannel);
			}
		}
	}


	private void generateNotificationForCall(Context context, String title, String message, int notificationId,
											 String callNumber, String eta, int playSound, String clientID) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = getNotificationManager(this, Constants.NOTIF_CHANNEL_DEFAULT);

			Intent notificationIntent = new Intent();
			Log.d("clientID", "clientID = "+clientID);
			if(Constants.KEY_RIDE_ACCEPTED.equalsIgnoreCase(clientID)){
				notificationIntent.putExtra(Constants.KEY_EVENT, Constants.KEY_RIDE_ACCEPTED);
				clientID = "";
			}
			if(TextUtils.isEmpty(clientID)) {
				if (HomeActivity.appInterruptHandler != null) {
					notificationIntent.setClass(context, HomeActivity.class);
				} else {
					notificationIntent.setClass(context, SplashNewActivity.class);
				}
			} else {
//				if(!TextUtils.isEmpty(Data.currentActivity)) {
//					notificationIntent = new Intent(context, FreshActivity.class);
//				} else {
					notificationIntent.setClass(context, SplashNewActivity.class);
//				}
				notificationIntent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientID);
			}

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Constants.NOTIF_CHANNEL_DEFAULT);
			if(TextUtils.isEmpty(clientID)) {
				builder.setAutoCancel(false);
			} else {
				builder.setAutoCancel(true);
			}
			builder.setContentTitle(title);
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setTicker(message);
			setPlaySound(builder, playSound);
			builder.setWhen(when);

			if(eta != null){
				Drawable drawable = context.getResources().getDrawable(R.drawable.circle_theme_size);
				builder.setLargeIcon(drawableToBitmapPlusText(context, drawable, eta, 16));
			} else{
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
			}

//			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.mipmap.notification_icon);

			Intent intentCall = new Intent(context, CallActivity.class);
			intentCall.putExtra(context.getResources().getString(R.string.call_number), callNumber);
			PendingIntent pendingIntentCall = PendingIntent.getActivity(this, 123411, intentCall, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.addAction(android.R.drawable.sym_action_call, context.getResources().getString(R.string.call_driver), pendingIntentCall);

			builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= 16){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}

			Notification notification = builder.build();
			hideSmallIcon(notification);
			notificationManager.notify(notificationId, notification);

			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
			wl.acquire(15000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    private void notificationManagerCustomIDAnotherApp(Context context, String title, String message,
													   int notificationId, String packageName, int playSound) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = getNotificationManager(this, Constants.NOTIF_CHANNEL_DEFAULT);

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

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Constants.NOTIF_CHANNEL_DEFAULT);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);
			setPlaySound(builder, playSound);
            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setSmallIcon(R.mipmap.notification_icon);
            builder.setContentIntent(intent);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
				builder.setPriority(Notification.PRIORITY_HIGH);
			}



            Notification notification = builder.build();

			hideSmallIcon(notification);

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

	public static void clearNotification(Context context, int notifId) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notifId);
	}


	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		try {
			if (fuguNotificationConfig.isFuguNotification(remoteMessage.getData())) {
				fuguNotificationConfig.setLargeIcon(R.mipmap.ic_launcher);
                fuguNotificationConfig.setSmallIcon(R.mipmap.notification_icon);


                if(Build.VERSION.SDK_INT >= 16){
                    fuguNotificationConfig.setPriority(Notification.PRIORITY_HIGH);
                }
                fuguNotificationConfig.showNotification(getApplicationContext(), remoteMessage.getData());
				return;
			}

			if (remoteMessage.getData().size() > 0) {
				Bundle extras = new Bundle();
				for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
					extras.putString(entry.getKey(), entry.getValue());
				}

			}
		} catch (Throwable t) {
			//Logger.logFine("Error parsing FCM message", t);
		}
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
					String url = jObj.optString(KEY_URL, "");
					int showDialog = jObj.optInt(Constants.KEY_SHOW_DIALOG, 0);
					int showPush = jObj.optInt(Constants.KEY_SHOW_PUSH, 1);


					if (PushFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						Prefs.with(this).save(SP_CURRENT_ENGAGEMENT_ID, jObj.optString(KEY_ENGAGEMENT_ID));
						Prefs.with(this).save(SP_CURRENT_STATE, PassengerScreenMode.P_REQUEST_FINAL.getOrdinal());

						if(Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_ACCEPT, 0) == 1){
							SoundMediaPlayer.INSTANCE.startSound(this, R.raw.ride_status_update, 1, false);
							playSound = 0;
						}

						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.rideRequestAcceptedInterrupt(jObj);
						} else{
							startLocationUpdateService();
						}

						int pushCallDriver = jObj.optInt(KEY_PUSH_CALL_DRIVER, 0);
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
						if(pushCallDriver == 1 && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, null, playSound, Constants.KEY_RIDE_ACCEPTED);
						} else{
							notificationManager(this, title, message1, playSound);
						}

					}else if (PushFlags.UNLOCK_BLE_DEVICE.getOrdinal() == flag){
						Log.e(TAG,"push arrived");
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, message);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					} else if (PushFlags.DRIVER_ARRIVED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						String driverArrivedMessage = jObj.getString(KEY_MESSAGE);
						Prefs.with(this).save(SP_CURRENT_ENGAGEMENT_ID, jObj.optString(KEY_ENGAGEMENT_ID, Prefs.with(this).getString(Constants.SP_CURRENT_ENGAGEMENT_ID, "")));
						Prefs.with(this).save(SP_CURRENT_STATE, PassengerScreenMode.P_DRIVER_ARRIVED.getOrdinal());

						if(Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_ARRIVED, 0) == 1){
							SoundMediaPlayer.INSTANCE.startSound(this, R.raw.ride_status_update, 1, false);
							playSound = 0;
						}

						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onDriverArrived(driverArrivedMessage);
						} else{
							startLocationUpdateService();
						}

						int pushCallDriver = jObj.optInt(KEY_PUSH_CALL_DRIVER, 0);
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						if(pushCallDriver == 1 && !"".equalsIgnoreCase(phoneNo)){
							generateNotificationForCall(this, title, driverArrivedMessage, NOTIFICATION_ID, phoneNo,
									null, playSound, "");
						} else{
							notificationManager(this, title, driverArrivedMessage, playSound);
						}
						if(Prefs.with(this).getInt(Constants.KEY_CUSTOMER_ARRIVED_BEEP_ENABLED, 0) == 1) {
							SoundMediaPlayer.INSTANCE.startSound(GCMIntentService.this, R.raw.arrived_beep, 4, true);
						}

					} else if (PushFlags.RIDE_STARTED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						message1 = jObj.optString(KEY_MESSAGE, getString(R.string.your_ride_has_started));
						Prefs.with(this).save(SP_CURRENT_ENGAGEMENT_ID, jObj.optString(KEY_ENGAGEMENT_ID, Prefs.with(this).getString(Constants.SP_CURRENT_ENGAGEMENT_ID, "")));
						Prefs.with(this).save(SP_CURRENT_STATE, PassengerScreenMode.P_IN_RIDE.getOrdinal());

						if(Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_START, 0) == 1){
							SoundMediaPlayer.INSTANCE.startSound(this, R.raw.ride_status_update, 1, false);
							playSound = 0;
						}

						Prefs.with(this).save(SP_CHAT_CLOSE, true);
						Intent intent1 = new Intent(Constants.INTENT_ACTION_CHAT_CLOSE);
						intent1.putExtra(Constants.KEY_FLAG, flag);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

						if (HomeActivity.appInterruptHandler != null) {
							PlaceOrderResponse.ReferralPopupContent referralPopupContent = null;
							try {
								JSONObject jReferralPopupContent = jObj.optJSONObject(KEY_REFERRAL_POPUP_CONTENT);
								referralPopupContent = new Gson().fromJson(jReferralPopupContent.toString(), PlaceOrderResponse.ReferralPopupContent.class);
							} catch (Exception e){}
							HomeActivity.appInterruptHandler.startRideForCustomer(0, message1, referralPopupContent);
						} else {
							String SHARED_PREF_NAME = "myPref",
									SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
									P_IN_RIDE = "P_IN_RIDE";
							SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
							Editor editor = pref.edit();
							editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
							editor.apply();
							startLocationUpdateService();
						}
						notificationManager(this, title, message1, playSound);

						FbEvents.logEvent(this, FB_EVENT_RIDE_STARTED);

					} else if (PushFlags.RIDE_ENDED.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						message1 = jObj.optString(KEY_MESSAGE, getString(R.string.your_ride_has_ended));
						String engagementId = jObj.getString("engagement_id");
						JSONObject coupon = jObj.getJSONObject("coupon");
						Promo promo = null;

						if(coupon.has("coupon_card_type") && coupon.has("is_scratched") && coupon.optInt("is_scratched", 0) != 1) {
							CouponInfo couponInfo = new CouponInfo(coupon.optInt("account_id", 0), coupon.optString("title", ""));
//							couponInfo.setCouponId(coupon.getInt("coupon_id"));
							promo = new Promo(coupon.optString("name", ""), coupon.optString("clientId", ""),
									couponInfo, 0, 0, coupon.optInt("is_scratched", 0) == 1, coupon.getInt("coupon_card_type"));

						}
						if (HomeActivity.appInterruptHandler != null) {
							if (PassengerScreenMode.P_IN_RIDE == HomeActivity.passengerScreenMode) {
								HomeActivity.appInterruptHandler.customerEndRideInterrupt(engagementId, promo);
							}
						}
						if(Prefs.with(this).getInt(KEY_CUSTOMER_PLAY_SOUND_RIDE_END, 0) == 1){
							SoundMediaPlayer.INSTANCE.startSound(this, R.raw.ride_status_update, 1, false);
							playSound = 0;
						}
						notificationManager(this, title, message1, playSound);
						stopLocationUpdateService(PassengerScreenMode.P_RIDE_END.getOrdinal());

					} else if (PushFlags.RIDE_REJECTED_BY_DRIVER.getOrdinal() == flag) {
						//Prefs.with(this).save(KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.ride_cancelled_by_driver));
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.startRideForCustomer(1, message1, null);
						}
						notificationManager(this, title, message1, playSound);
						stopLocationUpdateService(PassengerScreenMode.P_INITIAL.getOrdinal());
						Intent intentBr = new Intent(Constants.ACTION_FINISH_ACTIVITY);
						intentBr.putExtra(Constants.KEY_FINISH_ACTIVITY, 1);
						sendBroadcast(intentBr);

					} else if (PushFlags.WAITING_STARTED.getOrdinal() == flag
							|| PushFlags.WAITING_ENDED.getOrdinal() == flag) {
						message1 = jObj.getString("message");
						notificationManager(this, title, "" + message1, playSound);

					} else if (PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag) {
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						String log = jObj.getString("log");
						int requestType = jObj.optInt("request_type", -1);
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onNoDriversAvailablePushRecieved(log, requestType);
						}
					} else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {
						Prefs.with(this).save(KEY_STATE_RESTORE_NEEDED, 1);
						String logMessage = jObj.getString("message");
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.onChangeStatePushReceived();
						}
						notificationManager(this, title, logMessage, playSound);

					} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag
							|| PushFlags.PROS_STATUS_SILENT.getOrdinal() == flag || PushFlags.SHOW_NOTIFICATION_WITH_DEEPLINK.getOrdinal()==flag) {
						if (jObj.has("client_id") && PushFlags.SHOW_NOTIFICATION_WITH_DEEPLINK.getOrdinal()!=flag) {
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
						} else {
							String picture = jObj.optString(KEY_PICTURE, "");
							int campaignId = jObj.optInt(Constants.KEY_CAMPAIGN_ID, 0);
							int postId = jObj.optInt(Constants.KEY_POST_ID, -1);
							int postNotificationId = jObj.optInt(Constants.KEY_NOTIFICATION_ID, -1);
							int jobId = jObj.optInt(Constants.KEY_JOB_ID, -1);
							int isFeedbackPending = jObj.optInt(Constants.KEY_IS_FEEDBACK_PENDING, 0);

							if(Data.activityResumed && deepindex == AppLinkIndex.FEED_PAGE.getOrdinal() && postId > 0){
								return;
							}



							if("".equalsIgnoreCase(picture)){
								picture = jObj.optString(KEY_IMAGE, "");
							}

							int restaurantId = jObj.optInt(KEY_RESTAURANT_ID, -1);
							int feedbackId = jObj.optInt(KEY_FEEDBACK_ID, -1);


							// Push dialog content saved if showDialog flag is 1
							if(showDialog == 1) {
								Prefs.with(this).save(SP_PUSH_DIALOG_CONTENT, message);
							}else{
								// deep link to restaurant page
								Prefs.with(this).save(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, ""+restaurantId);

								// deep link to restaurant review page particular feedback
								Prefs.with(this).save(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK, feedbackId);

							}


							if(PushFlags.PROS_STATUS_SILENT.getOrdinal() != flag) {
								// if picture is not empty first fetch picture via Picasso loading and then
								// display bitmap along push else display push directly
								if (!"".equalsIgnoreCase(picture)) {
									deepindex = jObj.optInt(KEY_DEEPINDEX, AppLinkIndex.NOTIFICATION_CENTER.getOrdinal());
									bigImageNotifAsync(title, message1, deepindex, picture, url, playSound, showDialog, showPush,
											tabIndex, flag, campaignId, postId, postNotificationId);
								} else {
									deepindex = jObj.optInt(KEY_DEEPINDEX, -1);
									notificationManagerCustomID(this, title, message1, PROMOTION_NOTIFICATION_ID, deepindex,
											null, url, playSound, showDialog, showPush, tabIndex, flag,
											0, ProductType.AUTO.getOrdinal(), campaignId, postId, postNotificationId);
								}
							}

                            if(PushFlags.SHOW_NOTIFICATION_WITH_DEEPLINK.getOrdinal()!=flag){
                                // for sending broadcast to FreshActivity for tab index action
                                Intent broadcastIntent = new Intent(Data.LOCAL_BROADCAST);
                                // if push content has post_id and deepindex 22(FEED) only then hit this broadcast
                                if(deepindex == AppLinkIndex.FEED_PAGE.getOrdinal() && postId != -1){
                                    broadcastIntent.putExtra(Constants.KEY_POST_ID, postId);
                                    broadcastIntent.putExtra(Constants.KEY_POST_NOTIFICATION_ID, postNotificationId);
                                }
                                else if(deepindex == AppLinkIndex.MENUS_PAGE.getOrdinal() && restaurantId > 0 && feedbackId > 0){
                                    broadcastIntent.putExtra(Constants.KEY_RESTAURANT_ID, restaurantId);
                                    broadcastIntent.putExtra(Constants.KEY_FEEDBACK_ID, feedbackId);
                                }
                                else if (deepindex == AppLinkIndex.PROS_PAGE.getOrdinal() && jobId > 0){
                                    if(isFeedbackPending == 1) {
                                        Prefs.with(this).save(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, jobId);
                                    }

                                    broadcastIntent.putExtra(Constants.KEY_JOB_ID, jobId);
                                    broadcastIntent.putExtra(Constants.KEY_IS_FEEDBACK_PENDING, isFeedbackPending);
                                }
                                else if("".equalsIgnoreCase(url)){
                                    deepindex = showDialog == 1 ? -1 : deepindex;
                                    broadcastIntent.putExtra(Constants.KEY_PUSH_CLICKED, "1");
                                    broadcastIntent.putExtra(Constants.KEY_TAB_INDEX, tabIndex);
                                }
                                broadcastIntent.putExtra(Constants.KEY_DEEPINDEX, deepindex);
                                broadcastIntent.putExtra(Constants.KEY_FLAG, flag);

                                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                            }



							// updating last push received time
							Prefs.with(this).save(SP_LAST_PUSH_RECEIVED_TIME, System.currentTimeMillis());


							// CAMPAIGN TRACK PUSH
							// if push content has campaign_id it hit /track_push api on server
							// for that campaign id with status received
							try {if (campaignId > 0) {
									new ApiTrackPush().hit(GCMIntentService.this, campaignId, ApiTrackPush.Status.RECEIVED);
								}} catch (Exception e) {}

						}

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
						notificationManagerCustomID(this, title, getString(R.string.your_account_has_been_verified), NOTIFICATION_ID, -1,
								null, "", playSound, 0, 1, 0, flag);

					}
					else if (PushFlags.CLEAR_ALL_MESSAGE.getOrdinal() == flag) {
						MyApplication.getInstance().getDatabase2().deleteNotificationTable();
						notifyActivityOnPush();
					}
					else if (PushFlags.DELETE_NOTIFICATION_ID.getOrdinal() == flag) {
						if(jObj.has(KEY_NOTIFICATION_ID)) {
							int id = jObj.optInt(KEY_NOTIFICATION_ID, -1);
							if(id != -1) {
								MyApplication.getInstance().getDatabase2().deleteNotification(id);
								notifyActivityOnPush();
							}
						}
					}
					else if (PushFlags.UPLOAD_CONTACTS_ERROR.getOrdinal() == flag) {
						if(HomeActivity.appInterruptHandler != null){ //&& FeedUtils.isForeground(this)){
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
						if(PermissionCommon.isGranted(android.Manifest.permission.ACCESS_FINE_LOCATION,this)) {
							if (!Utils.isServiceRunning(this, LocationUpdateService.class.getName())) {
								Intent intent = new Intent(this, LocationUpdateService.class);
								intent.putExtra(KEY_ONE_SHOT, true);
								startService(intent);
							} else {
								Intent intent1 = new Intent();
								intent1.setAction(ACTION_LOCATION_UPDATE);
								intent1.putExtra(KEY_EMERGENCY_LOC, true);
								sendBroadcast(intent1);
							}
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
					} else if (PushFlags.STATUS_CHANGED.getOrdinal() == flag) {

						String clientId = jObj.optString(KEY_CLIENT_ID, "");
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
						int orderId = jObj.optInt(KEY_ORDER_ID, 0);
						int orderStatus = jObj.optInt(Constants.ORDER_STATUS,Constants.NO_VALID_STATUS);
						if(!TextUtils.isEmpty(phoneNo)){
							generateNotificationForCall(this, title, message1, NOTIFICATION_ID, phoneNo, null, playSound, clientId);
						} else{
							notificationManager(this, title, message1, playSound);
						}
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_ORDER_ID, orderId);
						intent.putExtra(Constants.KEY_MESSAGE, message1);
						intent.putExtra(KEY_CLIENT_ID, clientId);
						if(orderStatus!=Constants.NO_VALID_STATUS){
							intent.putExtra(Constants.ICICI_ORDER_STATUS, orderStatus);
						}
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					}
					else if (PushFlags.MENUS_STATUS.getOrdinal() == flag
							|| PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag) {
						String clientId = jObj.optString(KEY_CLIENT_ID, "");
						int orderId = jObj.optInt(KEY_ORDER_ID, 0);
						int closeTracking = jObj.optInt(KEY_CLOSE_TRACKING, 0);
						int productType = jObj.optInt(KEY_PRODUCT_TYPE, ProductType.AUTO.getOrdinal());
						int orderStatus = jObj.optInt(Constants.ORDER_STATUS,Constants.NO_VALID_STATUS);
						message1 = jObj.optString(KEY_MESSAGE, "");
						if(!TextUtils.isEmpty(message1)) {
							notificationManagerCustomID(this, title, message1, PROMOTION_NOTIFICATION_ID, deepindex,
									null, url, playSound, showDialog, showPush, tabIndex, flag,
									orderId, productType, 0, -1, -1);
						}
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, message1);
						intent.putExtra(Constants.KEY_ORDER_ID, orderId);
						intent.putExtra(Constants.KEY_CLOSE_TRACKING, closeTracking);
						intent.putExtra(KEY_CLIENT_ID, clientId);
						if(orderStatus!=Constants.NO_VALID_STATUS){
							intent.putExtra(Constants.ICICI_ORDER_STATUS, orderStatus);
						}
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					}
					else if(PushFlags.CHAT_MESSAGE.getOrdinal() == flag) {
						String chatMessage = jObj.getJSONObject(KEY_MESSAGE).optString("chat_message", "");
						deliveryId = jObj.optString(KEY_DELIVERY_ID);
						notificationManagerCustomID(this, title, chatMessage, PROMOTION_NOTIFICATION_ID, AppLinkIndex.CHAT_PAGE.getOrdinal(),
								null, "", playSound, 0, 1, tabIndex, flag);
						Prefs.with(this).save(KEY_CHAT_COUNT , Prefs.with(this).getInt(KEY_CHAT_COUNT, 0) + 1);
//
					}
					else if(PushFlags.CHAT_MESSAGE.getOrdinal() == flag){
						String clientId = jObj.optString(KEY_CLIENT_ID, "");
						String phoneNo = jObj.optString(KEY_PHONE_NO, "");
						//message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
						String name = "";//FeedUtils.getActivityName(this);


						if(!name.equalsIgnoreCase(this.getPackageName()) ||
								Data.context == null){
							String chatMessage = jObj.getJSONObject(KEY_MESSAGE).optString("chat_message", "");
							notificationManagerCustomID(this, title, chatMessage, PROMOTION_NOTIFICATION_ID, AppLinkIndex.CHAT_PAGE.getOrdinal(),
									null, "", playSound, 0, 1, tabIndex, flag);
							Prefs.with(this).save(KEY_CHAT_COUNT , Prefs.with(this).getInt(KEY_CHAT_COUNT, 0) + 1);
							Intent intent = new Intent(Data.LOCAL_BROADCAST);
							intent.putExtra(Constants.KEY_FLAG, flag);
							intent.putExtra(Constants.KEY_CHAT_DELIVERY,message);
							LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
						} else {
							// Nothing
							clearNotification(this, PROMOTION_NOTIFICATION_ID);
						}
					} else if (PushFlags.REFRESH_PAY_DATA.getOrdinal() == flag) {
						message1 = jObj.optString(KEY_MESSAGE, "");
						notificationManagerCustomID(this, title, message1, PROMOTION_NOTIFICATION_ID, deepindex,
								null, url, playSound, showDialog, showPush, tabIndex, flag);
						Intent intent = new Intent(Constants.INTENT_ACTION_PAY_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, message);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					} else if(PushFlags.SHOW_NOTIFICATION_WITH_DEEPLINK.getOrdinal()==flag){
                        String clientId = jObj.optString(KEY_CLIENT_ID, "");
                        String phoneNo = jObj.optString(KEY_PHONE_NO, "");
                        message1 = jObj.optString(KEY_MESSAGE, getResources().getString(R.string.request_accepted_message));
                        notificationManager(this, title, message1, playSound);


                    } else if(PushFlags.BID_RECEIVED.getOrdinal()==flag){
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, message);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    }
					else if(PushFlags.MPESA_PAYMENT_SUCCESS.getOrdinal()==flag){
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, jObj.getString(KEY_MESSAGE));
						intent.putExtra(Constants.TO_PAY, jObj.getString(Constants.TO_PAY));
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					}
					else if(PushFlags.MPESA_PAYMENT_FAILURE.getOrdinal()==flag){
						Intent intent = new Intent(Data.LOCAL_BROADCAST);
						intent.putExtra(Constants.KEY_FLAG, flag);
						intent.putExtra(Constants.KEY_MESSAGE, message);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

					} else if(PushFlags.NO_DRIVER_FOUND_HELP.getOrdinal() == flag) {
						if(HomeActivity.appInterruptHandler != null && Foreground.get(MyApplication.getInstance()).isForeground()) {
							HomeActivity.appInterruptHandler.onNoDriverHelpPushReceived(new JSONObject(message));
						} else {
							notificationManager(this, title, message1, 0);
							Prefs.with(this).save(KEY_PUSH_NO_DRIVER_FOUND_HELP, message);
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

	private void startLocationUpdateService(){
		if(PermissionCommon.isGranted(android.Manifest.permission.ACCESS_FINE_LOCATION,this)) {
			if (!"".equalsIgnoreCase(Prefs.with(this).getString(SP_CURRENT_ENGAGEMENT_ID, ""))
					&& Prefs.with(this).getLong(KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL,
					LOCATION_UPDATE_INTERVAL) > 0
					&& !Utils.isServiceRunning(this, LocationUpdateService.class.getName())) {
				Intent intent = new Intent(this, LocationUpdateService.class);
				intent.putExtra(KEY_ONE_SHOT, false);
				startService(intent);
			}
		}
	}

	private void stopLocationUpdateService(int mode){
		Prefs.with(this).save(SP_CURRENT_STATE, mode);
		Prefs.with(this).save(SP_CURRENT_ENGAGEMENT_ID, "");
		Intent intent = new Intent(this, LocationUpdateService.class);
		intent.putExtra(STOP_FOREGROUND, 1);
		startService(intent);
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
								   final int showDialog, final int showPush, final int tabIndex, final int flag,
								   final int campaignId, final int postId, final int postNotificationId){
		try {
			RequestCreator requestCreator = Picasso.with(GCMIntentService.this).load(picture);
			Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
					try {
						notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID,
								deepindex, bitmap, url, playSound, showDialog, showPush, tabIndex, flag,
								0, ProductType.AUTO.getOrdinal(), campaignId, postId, postNotificationId);
					} catch (Exception e) {
						e.printStackTrace();
						notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID, deepindex,
								null, url, playSound, showDialog, showPush, tabIndex, flag,
								0, ProductType.AUTO.getOrdinal(), campaignId, postId, postNotificationId);
					}
				}

				@Override
				public void onBitmapFailed(Drawable drawable) {

				}

				@Override
				public void onPrepareLoad(Drawable drawable) {

				}
			};
			PicassoTools.into(requestCreator, target);
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
		textView2.setText(R.string.min);
		textView2.setTextSize(fontSize-4);
		textView2.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);

		final Rect boundsText2 = new Rect();

		final Paint paint2 = textView2.getPaint();
		paint2.getTextBounds(context.getString(R.string.min), 0, textView2.length(), boundsText2);
		paint2.setTextAlign(Paint.Align.CENTER);
		paint2.setColor(Color.WHITE);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		canvas.drawText(text, canvas.getWidth() / 2, Utils.dpToPx(context, 24), paint1);
		canvas.drawText(context.getString(R.string.min), canvas.getWidth() / 2, Utils.dpToPx(context, 26) + boundsText1.height(), paint2);

		return bitmap;
	}

	private void hideSmallIcon(Notification notification){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			int smallIconViewId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

			if (smallIconViewId != 0) {
				if (notification.contentIntent != null)
					notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

				if (notification.headsUpContentView != null)
					notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

				if (notification.bigContentView != null)
					notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
			}
		}
	}

}
