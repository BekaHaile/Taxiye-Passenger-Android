package com.sabkuchfresh.home;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 23/03/17.
 */

public class RazorpayCallbackService extends IntentService {

	public RazorpayCallbackService(){
		this("RazorpayCallbackService");
	}


	public RazorpayCallbackService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {

		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Constants.KEY_ACCESS_TOKEN, intent.getStringExtra(Constants.KEY_ACCESS_TOKEN));
			map.put(Constants.KEY_RAZORPAY_PAYMENT_ID, intent.getStringExtra(Constants.KEY_RAZORPAY_PAYMENT_ID));
			map.put(Constants.KEY_RAZORPAY_SIGNATURE, intent.getStringExtra(Constants.KEY_RAZORPAY_SIGNATURE));
			map.put(Constants.KEY_ORDER_ID, String.valueOf(intent.getIntExtra(Constants.KEY_ORDER_ID, 0)));
			map.put(Constants.KEY_AUTH_ORDER_ID, String.valueOf(intent.getIntExtra(Constants.KEY_AUTH_ORDER_ID, 0)));


			Response response = null;
			new HomeUtil().putDefaultParams(map);
			String clientId = Config.getLastOpenedClientId(this);
			map.put(Constants.KEY_CLIENT_ID, clientId);
			if(clientId.equalsIgnoreCase(Config.getMenusClientId())
					|| clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId())){
				response = RestClient.getMenusApiService().razorpayPlaceOrderCallback(map);
			} else if (clientId.equalsIgnoreCase(Config.getFreshClientId())
					|| clientId.equalsIgnoreCase(Config.getMealsClientId())){
				response = RestClient.getFreshApiService().razorpayPlaceOrderCallback(map);
			} else if (clientId.equalsIgnoreCase(Config.getAutosClientId())){
				response = RestClient.getApiService().razorpayPlaceOrderCallback(map);
			}

			if(response != null) {
				String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
				JSONObject jObject1 = new JSONObject(responseStr);
				backToActivity(jObject1);
			} else {
				backToActivity(null);
			}
		} catch (Exception e){
			e.printStackTrace();
			backToActivity(null);
		}
	}

	private void backToActivity(JSONObject jObject1){
		Intent intent = new Intent(Constants.INTENT_ACTION_RAZOR_PAY_CALLBACK);
		if(jObject1 != null) {
			intent.putExtra(Constants.KEY_RESPONSE, jObject1.toString());
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

}
