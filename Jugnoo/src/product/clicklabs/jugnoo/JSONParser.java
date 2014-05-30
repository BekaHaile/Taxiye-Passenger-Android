package product.clicklabs.jugnoo;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class JSONParser {

	public JSONParser(){
		
	}
	
	
	public void parseLoginData(Context context, String response) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"));
		
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
		editor.commit();
		
		HomeActivity.userMode = UserMode.PASSENGER;
		
		HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
		
		
	}
	
	
	public void parseAccessTokenLoginData(Context context, String response, String accessToken) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(accessToken, userData.getString("user_name"), 
				userData.getString("user_image"));
		
		//current_user_status = 1 driver or 2 user
		
		int currentUserStatus = userData.getInt("current_user_status");
		
		if(currentUserStatus == 1){
			HomeActivity.userMode = UserMode.DRIVER;
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			
			String screenMode = pref.getString(Data.SP_DRIVER_SCREEN_MODE, "");
			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
			}
			else{
				
				if(Data.D_START_RIDE.equalsIgnoreCase(screenMode)){
					
					HomeActivity.driverScreenMode = DriverScreenMode.D_START_RIDE;
					
					Data.dEngagementId = pref.getString(Data.SP_D_ENGAGEMENT_ID, "");
					Data.dCustomerId = pref.getString(Data.SP_D_CUSTOMER_ID, "");
					
					String lat = pref.getString(Data.SP_D_LATITUDE, "0");
					String lng = pref.getString(Data.SP_D_LONGITUDE, "0");
					
					Data.dCustLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
					String name = pref.getString(Data.SP_D_CUSTOMER_NAME, "");
					String image = pref.getString(Data.SP_D_CUSTOMER_IMAGE, "");
					String phone = pref.getString(Data.SP_D_CUSTOMER_PHONE, "");
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone);
					
				}
				else if(Data.D_IN_RIDE.equalsIgnoreCase(screenMode)){
					
					HomeActivity.driverScreenMode = DriverScreenMode.D_IN_RIDE;
					
					Data.dEngagementId = pref.getString(Data.SP_D_ENGAGEMENT_ID, "");
					Data.dCustomerId = pref.getString(Data.SP_D_CUSTOMER_ID, "");
					
					String name = pref.getString(Data.SP_D_CUSTOMER_NAME, "");
					String image = pref.getString(Data.SP_D_CUSTOMER_IMAGE, "");
					String phone = pref.getString(Data.SP_D_CUSTOMER_PHONE, "");
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone);
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "0"));
					HomeActivity.previousWaitTime = Double.parseDouble(pref.getString(Data.SP_WAIT_TIME, "0"));
					
					String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
					String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
					
					Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
				}
				else{
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
				
			}
			
			
		}
		else{
			HomeActivity.userMode = UserMode.PASSENGER;
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
		}
		
		
	}
	
	
	
	
	
}
