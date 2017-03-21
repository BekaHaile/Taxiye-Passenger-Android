package product.clicklabs.jugnoo.apis;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For updating current open client id
 *
 * Created by shankar
 */
public class ApiUpdateClientId {

	public void updateClientId(String clientIdOpened, LatLng latLng) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_UPDATED_CLIENT_ID, clientIdOpened);
			if(latLng != null) {
				params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
			}
			Log.i("params", "=" + params.toString());

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().updateClientId(params, new retrofit.Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
				}

				@Override
				public void failure(RetrofitError error) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
