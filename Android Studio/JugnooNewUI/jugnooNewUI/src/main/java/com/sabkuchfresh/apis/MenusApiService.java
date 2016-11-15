package com.sabkuchfresh.apis;

import com.sabkuchfresh.retrofit.model.MenusResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by shankar on 4/7/16.
 */
public interface MenusApiService {

	@FormUrlEncoded
	@POST("/nearby_restaurants")
	void nearbyRestaurants(@FieldMap Map<String, String> params,
						   Callback<MenusResponse> callback);



}
