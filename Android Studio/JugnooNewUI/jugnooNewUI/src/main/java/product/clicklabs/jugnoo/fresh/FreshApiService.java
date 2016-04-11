package product.clicklabs.jugnoo.fresh;

import java.util.Map;

import product.clicklabs.jugnoo.fresh.models.OrderHistoryResponse;
import product.clicklabs.jugnoo.fresh.models.PlaceOrderResponse;
import product.clicklabs.jugnoo.fresh.models.ProductsResponse;
import product.clicklabs.jugnoo.fresh.models.UserCheckoutResponse;
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

	@FormUrlEncoded
	@POST("/user_checkout_data")
	void userCheckoutData(@FieldMap Map<String, String> params,
						Callback<UserCheckoutResponse> callback);

	@FormUrlEncoded
	@POST("/place_order")
	void placeOrder(@FieldMap Map<String, String> params,
						  Callback<PlaceOrderResponse> callback);

	@FormUrlEncoded
	@POST("/order_history")
	void orderHistory(@FieldMap Map<String, String> params,
					Callback<OrderHistoryResponse> callback);

}
