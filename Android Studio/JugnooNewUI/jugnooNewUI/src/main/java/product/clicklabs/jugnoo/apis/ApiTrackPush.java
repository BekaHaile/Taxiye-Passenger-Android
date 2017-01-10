package product.clicklabs.jugnoo.apis;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.util.HashMap;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For logging campaign pushes
 *
 * Created by shankar
 */
public class ApiTrackPush {

	public void hit(Context context, int campaignId, ApiTrackPush.Status status) {
		try {
			Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(context);
			if(!TextUtils.isEmpty(pair.first)) {
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, pair.first);
				params.put(Constants.KEY_CAMPAIGN_ID, String.valueOf(campaignId));
				params.put(Constants.KEY_STATUS, String.valueOf(status.getOrdinal()));
				Log.i("params", "=" + params.toString());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().pushTracking(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
					}

					@Override
					public void failure(RetrofitError error) {
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum Status{
		RECEIVED(0),
		OPENED(1)
		;

		private int ordinal;
		Status(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

}
