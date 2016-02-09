package product.clicklabs.jugnoo.retrofit;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface StringAPIService {

	@FormUrlEncoded
	@POST("/paytm/add_money")
	void paytmAddMoney(@FieldMap Map<String, String> params,
					   Callback<String> callback);


}
