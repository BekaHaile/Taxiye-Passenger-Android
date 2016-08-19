package product.clicklabs.jugnoo.apis;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
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

	public void updateClientId(String clientIdOpened) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_UPDATED_CLIENT_ID, clientIdOpened);
			Log.i("params", "=" + params.toString());
			RestClient.getApiServices().updateClientId(params, new retrofit.Callback<SettleUserDebt>() {
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
