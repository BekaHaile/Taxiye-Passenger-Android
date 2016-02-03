package product.clicklabs.jugnoo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppPackage;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 16/12/15.
 */
public class FetchAppDataService extends IntentService implements Constants {

	public FetchAppDataService(){
		this("FetchAppDataService");
	}
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public FetchAppDataService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (intent.hasExtra(KEY_ACCESS_TOKEN)) {
			String accessToken = intent.getStringExtra(KEY_ACCESS_TOKEN);
			long timeToSave = intent.getLongExtra(KEY_APP_MONITORING_TIME_TO_SAVE, (System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
//			long timeToSave = intent.getLongExtra(KEY_APP_MONITORING_TIME_TO_SAVE, (0));
			fetchAppList(this, accessToken, timeToSave);
		}

	}

	public void fetchAppList(final Context context, final String accessToken, final long timeToSave) {
		try {
			if (AppStatus.getInstance(context).isOnline(context)) {

				HashMap<String, String> params = new HashMap<>();
				params.put(KEY_ACCESS_TOKEN, accessToken);
				Response response = RestClient.getApiServices().getActiveAppList(params);
				if(response != null){
					try {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							ArrayList<AppPackage> appPackageList = new ArrayList<>();
							JSONArray jsonArray = jObj.getJSONArray("app_list");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								AppPackage appPackage = new AppPackage(jsonObject.getInt("app_id"),
										jsonObject.getString("package_name"));
								appPackageList.add(appPackage);
							}
							Utils.checkAppsArrayInstall(context, appPackageList);
							Gson gson = new Gson();
							String arr = gson.toJson(appPackageList);
							Log.e("appPackageList", "=" + arr);
							returnAppList(context, accessToken, arr, timeToSave);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void returnAppList(final Context context, String accessToken, String appPackagesStr, final long timeToSave) {
		try {
			if (AppStatus.getInstance(context).isOnline(context)) {

				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", accessToken);
				params.put("app_data", appPackagesStr);
				Log.i("112", accessToken);
				Log.i("113",appPackagesStr);

				Response response = RestClient.getApiServices().updateUserInstalledApp(params);
				if(response != null){
					try {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");

						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							Prefs.with(context).save(SPLabels.APP_MONITORING_TRIGGER_TIME, timeToSave);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
