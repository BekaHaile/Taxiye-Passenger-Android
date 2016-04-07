package product.clicklabs.jugnoo.fresh;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by shankar on 4/7/16.
 */
public interface FreshApiService {

	@GET("/get_all_products")
	void getAllProducts(@QueryMap Map<String, String> params,
						Callback<SettleUserDebt> callback);

}
