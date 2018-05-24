package product.clicklabs.jugnoo.retrofit;

import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;

import java.util.Map;

import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public interface GoogleAPIServices {

	// sensor=false&mode=driving&alternatives=false
	@GET("/directions/json")
	Response getDirections(@Query("origin") String originLatLng,
						   @Query("destination") String destLatLng,
						   @Query("sensor") Boolean sensor,
						   @Query("mode") String mode,
						   @Query("alternatives") Boolean alternatives);

	@GET("/directions/json")
	void getDirections(@Query("origin") String originLatLng,
					   @Query("destination") String destLatLng,
					   @Query("sensor") Boolean sensor,
					   @Query("mode") String mode,
					   @Query("alternatives") Boolean alternatives,
					   @Query("units") String units,
					   Callback<SettleUserDebt> callback);

	// language=EN&sensor=false&alternatives=false
	@GET("/distancematrix/json")
	Response getDistanceMatrix(@Query("origins") String originLatLng,
							   @Query("destinations") String destLatLng,
							   @Query("language") String language,
							   @Query("sensor") Boolean sensor,
							   @Query("alternatives") Boolean alternatives);


	@GET("/geocode/json")
	void geocode(@Query("latlng") String latLng,
				 @Query("language") String language,
				 @Query("sensor") Boolean sensor, Callback<SettleUserDebt> callback);

	@GET("/geocode/json")
	void getMyAddress(@QueryMap Map<String, String> params, Callback<GoogleGeocodeResponse> cb);

	@GET("/directions/json")
	Response getDirectionsWaypoints(@Query("origin") String originLatLng,
									@Query("destination") String destLatLng,
									@Query(value = "waypoints", encodeValue = false) String waypoints);

}
