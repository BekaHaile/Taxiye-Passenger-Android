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
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
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
			KEYWORD_TFS = "tfs",
			KEYWORD_TAXI_FOR_SURE = "taxiforsure",
			KEYWORD_TAXI_FS = "taxifs",
			KEYWORD_PNR = "PNR";

	private final String[] KEYWORDS_BODY = new String[]{ KEYWORD_OLA, KEYWORD_OLAX, KEYWORD_TFS,
			KEYWORD_TAXI_FOR_SURE, KEYWORD_TAXI_FS, KEYWORD_PNR,
			"faasos", "swiggy", "freshmenu", "bigbasketeer", "foodpanda", "yumist",
			"bigbasket.com", "grofers", "tapzo", "order", "summary", "foodie", "delivery", "delivered", "upi"};
	private final String[] KEYWORDS_FROM = new String[]{ "swiggy", "faasos", "panda", "tapzo" };

	private ArrayList<KeywordDatum> keywordData;

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

		keywordData = new ArrayList<>();
		for(String keyword : KEYWORDS_BODY){
			keywordData.add(new KeywordDatum(keyword, true));
		}
		for(String keyword : KEYWORDS_FROM){
			keywordData.add(new KeywordDatum(keyword, false));
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
				int maxSize = 180, lowSize = 100;
				HashMap<String, String> hParams = new HashMap<>();
				hParams.put(Constants.KEY_ACCESS_TOKEN, accessToken);
				JSONArray jArray = new JSONArray();
				for (MSenderBody message : mSenderBodies) {
					if(message.getBody().length()>maxSize){
						List<String> bodies = Utils.splitEqually(message.getBody(), maxSize);
						for(String body : bodies){
							try {
								JSONObject jObj = new JSONObject();
								jObj.put("s", message.getSender());
								jObj.put("b", body);
								jObj.put("t", message.getDate());
								Log.e(TAG, "jOjj length="+jObj.toString().length());
								String decr = RSA.encryptWithPublicKeyStr(jObj.toString());
								jArray.put(decr);
							} catch (Exception e) {
								e.printStackTrace();
								List<String> bodiesSub = Utils.splitEqually(body, lowSize);
								for(String bodySub : bodiesSub){
									try {
										JSONObject jObj = new JSONObject();
										jObj.put("s", message.getSender());
										jObj.put("b", bodySub);
										jObj.put("t", message.getDate());
										String decr = RSA.encryptWithPublicKeyStr(jObj.toString());
										jArray.put(decr);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}
						}
					} else{
						JSONObject jObj = new JSONObject();
						jObj.put("s", message.getSender());
						jObj.put("b", message.getBody());
						jObj.put("t", message.getDate());
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
				if (MyApplication.getInstance().isOnline()) {
					Log.i(TAG, "params before api=" + params);
					new HomeUtil().putDefaultParams(params);
					RestClient.getApiService().uploadAnalytics(params, new Callback<SettleUserDebt>() {
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
				if (MyApplication.getInstance().isOnline()) {
					Log.i(TAG, "params before sync api=" + params);
					new HomeUtil().putDefaultParams(params);
					Response response = RestClient.getApiService().uploadAnalytics(params);

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
						String date = DateOperations.getTimeStampUTCFromMillis(Long
								.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date"))), true);
						if(body.toLowerCase().contains(KEYWORD_PAYTM) && body.toLowerCase().contains(KEYWORD_UBER)){
							messages.add(new MSenderBody(sender, body, date));
						}
						for(KeywordDatum keywordDatum : keywordData){
							if(keywordDatum.isBody && body.toLowerCase().contains(keywordDatum.keyword)){
								messages.add(new MSenderBody(sender, body, date));
								break;
							} else if(!keywordDatum.isBody && sender.toLowerCase().contains(keywordDatum.keyword)){
								messages.add(new MSenderBody(sender, body, date));
								break;
							}
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
		private String date;

		public MSenderBody(String sender, String body, String date){
			this.sender = sender;
			this.body = body;
			this.date = date;
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

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
	}

	class KeywordDatum{
		private String keyword;
		private boolean isBody;

		public KeywordDatum(String keyword, boolean isBody){
			this.keyword = keyword;
			this.isBody = isBody;
		}
	}
}