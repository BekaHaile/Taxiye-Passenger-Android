package sabkuchfresh.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URL;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import sabkuchfresh.utils.Constants;

public class GCMIntentService extends GcmListenerService implements Constants {

	private final String TAG = GCMIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    public static final int PROMOTION_NOTIFICATION_ID = 1212;

    public GCMIntentService() {
    }

	private void setPlaySound(NotificationCompat.Builder builder, int playSound){
		if(playSound == 1) {
			builder.setDefaults(Notification.DEFAULT_ALL);
		} else{
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
		}
	}

	@SuppressWarnings("deprecation")
	private void notificationManagerCustomIDWithBitmap(Context context, String title, String message, int notificationId,
													   Bitmap bitmap, String url, int playSound, int deepIndex, int tabIndex) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent = new Intent();
			notificationIntent.setAction(Intent.ACTION_VIEW); // jungooautos://app?deepindex=0
			//deepIndex = showDialog == 1 ? -1 : deepIndex;
			notificationIntent.putExtra(Constants.KEY_DEEP_INDEX, deepIndex);
			notificationIntent.putExtra(Constants.KEY_TAB_INDEX, tabIndex);
			notificationIntent.putExtra(Constants.KEY_PUSH_CLICKED, "1");
			if("".equalsIgnoreCase(url)){
				notificationIntent.putExtra(KEY_PUSH_CLICKED, "1");
				notificationIntent.setClass(context, SplashNewActivity.class);
			} else{
				notificationIntent.setData(Uri.parse(url));
			}

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);



			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle(title);
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			setPlaySound(builder, playSound);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notification_icon);
			builder.setContentIntent(intent);
			if(bitmap != null) {
				builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(title).setSummaryText(message));
			}
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

					String message1 = jObj.optString(KEY_MESSAGE, "");
					int playSound = jObj.optInt(KEY_PLAY_SOUND, 1);

					if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
							String picture = jObj.optString("picture", "");
							if("".equalsIgnoreCase(picture)){
								picture = jObj.optString("image", "");
							}
							String url = jObj.optString(KEY_URL, "");
							int deepIndex = jObj.optInt("deepindex", -1);
							int tabIndex = jObj.optInt("tab_index", 0);
							if(!"".equalsIgnoreCase(picture)){
								new BigImageNotifAsync(title, message1, picture, url, playSound, deepIndex, tabIndex).execute();
							}
							else{
								notificationManagerCustomIDWithBitmap(this, title, message1, PROMOTION_NOTIFICATION_ID,
										null, url, playSound, deepIndex, tabIndex);
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }


    private class BigImageNotifAsync extends AsyncTask<String, String, Bitmap> {

        private Bitmap bitmap = null;
        private String title, message, picture, url;
        private int playSound, deepIndex, tabIndex;

        public BigImageNotifAsync(String title, String message, String picture, String url, int playSound, int deepIndex,
								  int tabIndex){
            this.picture = picture;
			this.title = title;
            this.message = message;
			this.url = url;
			this.playSound = playSound;
			this.deepIndex = deepIndex;
			this.tabIndex = tabIndex;
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
                notificationManagerCustomIDWithBitmap(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID,
						result, url, playSound, deepIndex, tabIndex);
            }catch (Exception e){
                e.printStackTrace();
				notificationManagerCustomIDWithBitmap(GCMIntentService.this, title, message, PROMOTION_NOTIFICATION_ID,
						null, url, playSound, deepIndex, tabIndex);
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
