package product.clicklabs.jugnoo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppPackage;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by aneeshbansal on 16/12/15.
 */
public class FetchAppDataService extends IntentService {
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

		if(intent.hasExtra("access_token")){
			String accessToken = intent.getStringExtra("access_token");
			fetchAppList(this, accessToken);
		}

	}

	public void fetchAppList(final Context context, final String accessToken) {
		try {
			if (AppStatus.getInstance(context).isOnline(context)) {

				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);
				SyncHttpClient client = Data.getSyncClient();
				client.post(Config.getServerUrl() + "/fetch_application_list", params,
						new CustomAsyncHttpResponseHandler() {

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
							}
							@Override
							public void onSuccess(String response) {

								try {
									JSONObject jObj = new JSONObject(response);
									int flag = jObj.getInt("flag");

									if (ApiResponseFlags.APP_INSTALLED_INFO.getOrdinal() == flag) {

										ArrayList<AppPackage> appPackageList = new ArrayList<>();
										JSONArray jsonArray = jObj.getJSONArray("application");
										for (int i = 0; i < jsonArray.length(); i++) {
											JSONObject jsonObject = jsonArray.getJSONObject(i);
											AppPackage appPackage = new AppPackage(jsonObject.getString("package_name"));
											appPackageList.add(appPackage);
										}
										Utils.checkAppsArrayInstall(context, appPackageList);
										Gson gson = new Gson();
										String arr = gson.toJson(appPackageList);
										Log.e("appPackageList", "="+arr);
										returnAppList(context, accessToken, arr);

									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void returnAppList(final Context context, String accessToken, String appPackagesStr) {
		try {
			if (AppStatus.getInstance(context).isOnline(context)) {

				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);
				params.put("applications", appPackagesStr);

				SyncHttpClient client = Data.getSyncClient();
				client.post(Config.getServerUrl() + "/fetch_application_list", params,
					new CustomAsyncHttpResponseHandler() {

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
						}
						@Override
						public void onSuccess(String response) {

							try {
								JSONObject jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");

								if (ApiResponseFlags.APP_INSTALLED_INFO.getOrdinal() == flag) {

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
