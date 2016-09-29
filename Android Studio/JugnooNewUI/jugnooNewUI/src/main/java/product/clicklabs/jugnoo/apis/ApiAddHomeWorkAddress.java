package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.Constants.KEY_ID;
import static product.clicklabs.jugnoo.Constants.TYPE_HOME;
import static product.clicklabs.jugnoo.Data.latitude;
import static product.clicklabs.jugnoo.Data.longitude;

/**
 * For saving home, work and other addresses
 *
 * Created by shankar on 3/2/16.
 */
public class ApiAddHomeWorkAddress {

	private final String TAG = ApiAddHomeWorkAddress.class.getSimpleName();

	private Activity activity;
	private Callback callback;
	private Gson gson;

	public ApiAddHomeWorkAddress(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
		this.gson = new Gson();
	}

	public void addHomeAndWorkAddress(final SearchResult searchResult, final boolean deleteAddress, final int matchedWithOtherId,
									  final boolean editThisAddress, final int placeRequestCode) {
		try {
			if(AppStatus.getInstance(activity).isOnline(activity)) {

				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				if(matchedWithOtherId > 0){
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put(Constants.KEY_ADDRESS, "");
					params.put(Constants.KEY_GOOGLE_PLACE_ID, "");

					params.put(Constants.KEY_ADDRESS_ID, String.valueOf(matchedWithOtherId));
					params.put(Constants.KEY_DELETE_FLAG, "1");
					params.put(Constants.KEY_IS_CONFIRMED, "1");
				}
				else{
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put(Constants.KEY_ADDRESS, searchResult.getAddress());
					params.put(Constants.KEY_GOOGLE_PLACE_ID, searchResult.getPlaceId());
					params.put(Constants.KEY_TYPE, searchResult.getName());

					params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
					params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));

					if(editThisAddress){
						params.put(Constants.KEY_ADDRESS_ID, String.valueOf(searchResult.getId()));
						if(deleteAddress) {
							params.put(Constants.KEY_DELETE_FLAG, "1");
						}
					}
					params.put(Constants.KEY_IS_CONFIRMED, "1");
				}
				Log.i(TAG, "addHomeAndWorkAddress params=" + params.toString());

				RestClient.getApiServices().addHomeAndWorkAddress(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "addHomeAndWorkAddress response = " + responseStr);
						DialogPopup.dismissLoadingDialog();

						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.optInt("", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){

								if(matchedWithOtherId > 0){
									if(searchResult.getName().equalsIgnoreCase(TYPE_HOME)){
										Prefs.with(activity).save(SPLabels.ADD_WORK, "");
									} else if(searchResult.getName().equalsIgnoreCase(Constants.TYPE_WORK)){
										Prefs.with(activity).save(SPLabels.ADD_HOME, "");
									}
									addHomeAndWorkAddress(searchResult, deleteAddress, 0, editThisAddress, placeRequestCode);
								}
								else{
									int id = 0;
									if(searchResult.getId() != null){
										id = searchResult.getId();
									}
									searchResult.setId(jObj.optInt(KEY_ID, id));
									searchResult.setIsConfirmed(1);

									String strResult = gson.toJson(searchResult, SearchResult.class);
									if(deleteAddress){
										strResult = "";
									}
									if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
										Prefs.with(activity).save(SPLabels.ADD_HOME, strResult);
									} else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
										Prefs.with(activity).save(SPLabels.ADD_WORK, strResult);
									} else {
										if(deleteAddress){
											Data.userData.getSearchResults().remove(searchResult);
										} else {
											Data.userData.getSearchResults().add(searchResult);
										}
									}
									callback.onSuccess(searchResult, strResult);
								}

							} else{
								DialogPopup.alertPopup(activity, "", message);
							}
						}  catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "addHomeAndWorkAddress error" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
						callback.onFailure();
					}
				});
			}
			else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						callback.onRetry(view);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
						callback.onNoRetry(view);
					}
				});
	}


	public interface Callback{
		void onSuccess(SearchResult searchResult, String strResult);
		void onFailure();
		void onRetry(View view);
		void onNoRetry(View view);
	}

}
