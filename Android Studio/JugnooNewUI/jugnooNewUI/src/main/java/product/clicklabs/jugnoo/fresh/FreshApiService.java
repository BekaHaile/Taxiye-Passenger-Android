package product.clicklabs.jugnoo.fresh;

import java.util.Map;

import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by shankar on 4/7/16.
 */
public interface FreshApiService {

	@FormUrlEncoded
	@POST("/get_all_products")
	void getAllProducts(@FieldMap Map<String, String> params,
						Callback<ProductsResponse> callback);

}
