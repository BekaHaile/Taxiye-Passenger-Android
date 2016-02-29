package product.clicklabs.jugnoo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.RSA;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 2/5/16.
 */
public class FetchAndSendMessages extends AsyncTask<String, Integer, ArrayList<FetchAndSendMessages.MSenderBody>>{

	private final String TAG = FetchAndSendMessages.class.getSimpleName();

	private final String KEYWORD_UBER = "uber",
			KEYWORD_PAYTM = "paytm",
			KEYWORD_OLA = "ola",
			KEYWORD_SAY_OLA = "say ola",
			KEYWORD_TFS = "tfs",
			KEYWORD_BOOKING = "booking",
			KEYWORD_AUTO = "auto";

	private final long DAY_MILLIS = 24 * 60 * 60 * 1000;
	private final long THREE_DAYS_MILLIS = 3 * DAY_MILLIS;

	private Context context;
	private String accessToken;

	public FetchAndSendMessages(Context context, String accessToken){
		this.context = context;
		this.accessToken = accessToken;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ArrayList<MSenderBody> doInBackground(String... params) {
		try {
			long defaultTime = System.currentTimeMillis() - THREE_DAYS_MILLIS;
			long currentTime = System.currentTimeMillis();
			long lastTime = Prefs.with(context).getLong(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME, defaultTime);
			long currentMinusLast = (currentTime - lastTime);

			if(currentMinusLast >= DAY_MILLIS){
				return fetchMessages(defaultTime);
			} else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<MSenderBody> s) {
		super.onPostExecute(s);
		try {
			if(s != null && s.size() > 0){
				if(AppStatus.getInstance(context).isOnline(context)){
					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
					JSONArray jArray = new JSONArray();
					for(MSenderBody message : s){
						JSONObject jObj = new JSONObject();
						jObj.put("sender", message.getSender());
						jObj.put("body", message.getBody());
						jArray.put(jObj);
					}
					params.put(Constants.KEY_ANALYTICS_SMS_LIST, jArray.toString());

					Log.i(TAG, "params before api=" + params);

					String encStr = RSA.encryptWithPublicKey(params.toString());
					Log.i(TAG, "params before api enc=" + encStr);

					HashMap<String, String> hash = new HashMap<>();
					hash.put("data", encStr);

					RestClient.getApiServices().uploadAnalyticsMessages(hash, new Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							try {
								String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
								Log.i(TAG, "uploadAnalytics responseStr"+responseStr);
								JSONObject jObj = new JSONObject(responseStr);
								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
//									Prefs.with(context).save(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME, System.currentTimeMillis());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "uploadAnalytics error="+error);
						}
					});

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<MSenderBody> fetchMessages(long lastTime) {
		ArrayList<MSenderBody> messages = new ArrayList<>();
		try {
			Uri uri = Uri.parse("content://sms/inbox");
			String[] selectionArgs = new String[]{Long.toString(lastTime)};
			String selection = "date" + ">?";
			Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);

			if (cursor != null) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
					String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
					try {
						if(body.toLowerCase().contains(KEYWORD_PAYTM) && body.toLowerCase().contains(KEYWORD_UBER)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_TFS) && body.toLowerCase().contains(KEYWORD_BOOKING)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_OLA) && body.toLowerCase().contains(KEYWORD_BOOKING)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_OLA) && body.toLowerCase().contains(KEYWORD_AUTO)){
							messages.add(new MSenderBody(sender, body));
						}
						else if(body.toLowerCase().contains(KEYWORD_SAY_OLA)){
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
	}
}