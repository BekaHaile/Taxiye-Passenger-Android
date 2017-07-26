package com.sabkuchfresh.pros.api;

import com.sabkuchfresh.pros.models.CreateTaskData;
import com.sabkuchfresh.pros.models.ProsCatalogueData;
import com.sabkuchfresh.pros.models.ProsOrderStatusResponse;
import com.sabkuchfresh.pros.models.ProsProductData;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by shankar on 20/06/17.
 */

public interface ProsApi {

	@FormUrlEncoded
	@POST("/get_app_catalogue")
	void getAppCatalogue(@FieldMap Map<String, String> params,
						 Callback<ProsCatalogueData> callback);

	@FormUrlEncoded
	@POST("/get_products_for_category")
	void getProductsForCategory(@FieldMap Map<String, String> params,
						 Callback<ProsProductData> callback);

	@FormUrlEncoded
	@POST("/create_task_via_vendor")
	void createTaskViaVendor(@FieldMap Map<String, String> params,
								Callback<CreateTaskData> callback);

	@FormUrlEncoded
	@POST("/order_history")
	void orderHistory(@FieldMap Map<String, String> params,
							 Callback<ProsOrderStatusResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_booking")
	void cancelBooking(@FieldMap Map<String, String> params,
					  Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/task_rating")
	void taskRating(@FieldMap Map<String, String> params,
					   Callback<SettleUserDebt> callback);

}
