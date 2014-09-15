package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

public class JSONParser {

	public JSONParser(){
		
	}
	
	
	public void parseLoginData(Context context, String response) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"), userData.getString("id"));
		
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
		editor.putString(Data.SP_ID_KEY, Data.userData.id);
		editor.commit();
		
		try{
			int currentUserStatus = userData.getInt("current_user_status");
			if(currentUserStatus == 1){
				HomeActivity.userMode = UserMode.DRIVER;
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				
				try {
					int excepInt = userData.getInt("exceptional_driver");
					if(1 == excepInt){
						HomeActivity.exceptionalDriver = ExceptionalDriver.YES;
					}
					else{
						HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
					}
				} catch (Exception e) {
					HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
					e.printStackTrace();
				}
			}
			else if(currentUserStatus == 2){
				HomeActivity.userMode = UserMode.PASSENGER;
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
				HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			}
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.userMode = UserMode.PASSENGER;
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
			HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
		}
		
		
		
//		{
//		    "user_data": {
//		        "access_token": "c274d8d70e77850511df24a6255dab48",
//		        "user_name": "Shankar Bhagwati",
//		        "user_image": "http://graph.facebook.com/717496164959213/picture?width=160&height=160",
//		        "id": 1,
//		        "current_user_status": 1,
//		        "fare_details": [
//		            {
//		                "fare_fixed": 30,
//		                "fare_per_km": 10,
//		                "fare_threshold_distance": 2
//		            }
//		        ]
//		    },
//		    "popup": 0
//		}
		
		
		try{
			JSONArray fareDetailsArr = userData.getJSONArray("fare_details");
			JSONObject fareDetails0 = fareDetailsArr.getJSONObject(0);
			HomeActivity.fareFixed = fareDetails0.getInt("fare_fixed");
			HomeActivity.farePerKm = fareDetails0.getInt("fare_per_km");
			HomeActivity.fareThresholdDistance = fareDetails0.getInt("fare_threshold_distance");
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.fareFixed = 30;
			HomeActivity.farePerKm = 10;
			HomeActivity.fareThresholdDistance = 2;
		}
		
		
	}
	
	
	
	
	
	public void parseAccessTokenLoginData(Context context, String response, String accessToken, String id) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(accessToken, userData.getString("user_name"), 
				userData.getString("user_image"), id);
		
		
		try{
			JSONArray fareDetailsArr = userData.getJSONArray("fare_details");
			JSONObject fareDetails0 = fareDetailsArr.getJSONObject(0);
			HomeActivity.fareFixed = fareDetails0.getInt("fare_fixed");
			HomeActivity.farePerKm = fareDetails0.getInt("fare_per_km");
			HomeActivity.fareThresholdDistance = fareDetails0.getInt("fare_threshold_distance");
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.fareFixed = 30;
			HomeActivity.farePerKm = 10;
			HomeActivity.fareThresholdDistance = 2;
		}
		
		
		//current_user_status = 1 driver or 2 user
		
		int currentUserStatus = userData.getInt("current_user_status");
		if(currentUserStatus == 1){
			try{
				int excepInt = userData.getInt("exceptional_driver");
				if(1 == excepInt){
					HomeActivity.exceptionalDriver = ExceptionalDriver.YES;
				}
				else{
					HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
				}
			} catch(Exception e){
				e.printStackTrace();
				HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			}
		}
		
		
		getUserStatus(context, accessToken, currentUserStatus);
		
	}
	
	
	
	public static void getUserStatus(Context context, String accessToken, int currentUserStatus){
		
		if(currentUserStatus == 1){ // for driver
			
			HomeActivity.userMode = UserMode.DRIVER;
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			
			String screenMode = pref.getString(Data.SP_DRIVER_SCREEN_MODE, "");
			
			int engagementStatus = -1;
			String engagementId = "", userId = "", latitude = "", longitude = "", customerName = "", customerImage = "", customerPhone = "", customerRating = "";
			
			//TODO
			try{
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
					
					SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/user_status", nameValuePairs);
					
					Log.e("result of = user_status", "="+result);
					if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
						
					}
					else{
						try{
							JSONObject jObject1 = new JSONObject(result);
//							{
//							    "last_engagement_info": [
//							        {
//							            "user_id": 207,
//							            "pickup_latitude": 30.718836,
//							            "pickup_longitude": 76.810133,
//							            "engagement_id": 2522,
//							            "status": 4,
//							            "user_name": "Shankar Bhagwati",
//							            "phone_no": "+919780298413",
//							            "user_image": "http://graph.facebook.com/717496164959213/picture?width=160&height=160",
//							            "rating": 4.833333333333333
//							        }
//							    ]
//							}
							
							
							JSONArray lastEngInfoArr = jObject1.getJSONArray("last_engagement_info");
							JSONObject jObject = lastEngInfoArr.getJSONObject(0);
							
							engagementStatus = jObject.getInt("status");
							
							if((1 == engagementStatus) || (2 == engagementStatus)){
								engagementId = jObject.getString("engagement_id");
								userId = jObject.getString("user_id");
								latitude = jObject.getString("pickup_latitude");
								longitude = jObject.getString("pickup_longitude");
								customerName = jObject.getString("user_name");
								customerImage = jObject.getString("user_image");
								customerPhone = jObject.getString("phone_no");
								customerRating = jObject.getString("rating");
							}
							else{
								
							}
							
						} catch(Exception e){
							e.printStackTrace();
						}
					}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			// 0 for request, 1 for accepted,2 for started,3 for ended, 4 for rejected by driver, 5 for rejected by user,6 for timeout, 7 for nullified by chrone
			if(engagementStatus == 1){
				screenMode = Data.D_START_RIDE;
			}
			else if(engagementStatus == 2){
				screenMode = Data.D_IN_RIDE;
			}
			else{
				screenMode = "";
			}
			
			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				clearSPData(context);
			}
			else{
				
				if(Data.D_START_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.driverScreenMode = DriverScreenMode.D_START_RIDE;
					
					Data.dEngagementId = engagementId;
					Data.dCustomerId = userId;
					
					String lat = latitude;
					String lng = longitude;
					
					Data.dCustLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
					String name = customerName;
					String image = customerImage;
					String phone = customerPhone;
					String rating = customerRating;
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating);
				}
				else if(Data.D_IN_RIDE.equalsIgnoreCase(screenMode)){
					
					HomeActivity.driverScreenMode = DriverScreenMode.D_IN_RIDE;
					
					Data.dEngagementId = engagementId;
					Data.dCustomerId = userId;
					
					String lat = latitude;
					String lng = longitude;
					
					Data.dCustLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
					String name = customerName;
					String image = customerImage;
					String phone = customerPhone;
					String rating = customerRating;
					
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating);
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					HomeActivity.previousWaitTime = Double.parseDouble(pref.getString(Data.SP_WAIT_TIME, "0"));
					HomeActivity.previousRideTime = Double.parseDouble(pref.getString(Data.SP_RIDE_TIME, "0"));
					
					HomeActivity.waitStart = 2;
					
					if(-1 == HomeActivity.totalDistance){
						Data.startRidePreviousLatLng = Data.dCustLatLng;
						HomeActivity.totalDistance = 0;
					}
					else{
						String lat1 = pref.getString(Data.SP_LAST_LATITUDE, "0");
						String lng1 = pref.getString(Data.SP_LAST_LONGITUDE, "0");
						
						Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
					}
					
				}
				else{
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
				
			}
			
			
		}
		else{ // for customer
			
			HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			
			HomeActivity.userMode = UserMode.PASSENGER;
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			
			
			String screenMode = pref.getString(Data.SP_CUSTOMER_SCREEN_MODE, "");
			

			int engagementStatus = -1;
			String engagementId = "", sessionId = "",  userId = "", latitude = "", longitude = "", 
					driverName = "", driverImage = "", driverCarImage = "", driverPhone = "", driverRating = "", 
					pickupLatitude = "", pickupLongitude = "";
			
			
			//TODO
			try{
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
					
					SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/user_status", nameValuePairs);
					
					Log.e("result of = user_status", "="+result);
					if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
						
					}
					else{
						try{
							JSONObject jObject1 = new JSONObject(result);
							
//							{
//							    "last_engagement_info": [
//							        {
//							            "driver_id": 208,
//							            "pickup_latitude": 30.718836,
//							            "pickup_longitude": 76.810133,
//							            "engagement_id": 2522,
//							            "status": 4,
//							            "session_id": 641,
//							            "user_name": "Chaman Laal",
//							            "phone_no": "+919780298413",
//							            "user_image": "http://graph.facebook.com/1411907995761545/picture?width=160&height=160",
//							            "driver_car_image": "",
//							            "current_location_latitude": 0,
//							            "current_location_longitude": 0,
//							            "rating": 5
//							        }
//							    ]
//							}
							
							JSONArray lastEngInfoArr = jObject1.getJSONArray("last_engagement_info");
							JSONObject jObject = lastEngInfoArr.getJSONObject(0);

							engagementStatus = jObject.getInt("status");
							
							if((1 == engagementStatus) || (2 == engagementStatus)){
								engagementId = jObject.getString("engagement_id");
								sessionId = jObject.getString("session_id");
								userId = jObject.getString("driver_id");
								latitude = jObject.getString("current_location_latitude");
								longitude = jObject.getString("current_location_longitude");
								driverName = jObject.getString("user_name");
								driverImage = jObject.getString("user_image");
								driverCarImage = jObject.getString("driver_car_image");
								driverPhone = jObject.getString("phone_no");
								driverRating = jObject.getString("rating");
								pickupLatitude = jObject.getString("pickup_latitude");
								pickupLongitude = jObject.getString("pickup_longitude");
							}
							else{
								
							}
							
						} catch(Exception e){
							e.printStackTrace();
						}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			if(engagementStatus == 1){
				screenMode = Data.P_REQUEST_FINAL;
			}
			else if(engagementStatus == 2){
				screenMode = Data.P_IN_RIDE;
			}
			else if(engagementStatus == 3){
				screenMode = "";
			}
			else{
				screenMode = "";
			}
			
			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
				clearSPData(context);
			}
			else{
				
				Data.cSessionId = sessionId;
				Data.cEngagementId = engagementId;
				Data.cDriverId = userId;
				
				double dLatitude = Double.parseDouble(latitude);
				double dLongitude = Double.parseDouble(longitude);
				
				String SP_C_DRIVER_DISTANCE = pref.getString(Data.SP_C_DRIVER_DISTANCE, "");
				String SP_C_DRIVER_DURATION = pref.getString(Data.SP_C_DRIVER_DURATION, "");
				
				Data.assignedDriverInfo = new DriverInfo(userId, dLatitude, dLongitude, driverName, 
						driverImage, driverCarImage, driverPhone, driverRating);
				Log.e("Data.assignedDriverInfo on login","="+Data.assignedDriverInfo.latLng);
				Data.assignedDriverInfo.distanceToReach = SP_C_DRIVER_DISTANCE;
				Data.assignedDriverInfo.durationToReach = SP_C_DRIVER_DURATION;
				
				
				if(Data.P_REQUEST_FINAL.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
				}
				else if(Data.P_IN_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					
					if(-1 == HomeActivity.totalDistance){
						Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude));
						HomeActivity.totalDistance = 0;
					}
					else{
						String lat1 = pref.getString(Data.SP_LAST_LATITUDE, "0");
						String lng1 = pref.getString(Data.SP_LAST_LONGITUDE, "0");
						
						Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
					}
					
				}
				else if(Data.P_RIDE_END.equalsIgnoreCase(screenMode)){
					
					String SP_C_TOTAL_DISTANCE = pref.getString(Data.SP_C_TOTAL_DISTANCE, "0");
					String SP_C_TOTAL_FARE = pref.getString(Data.SP_C_TOTAL_FARE, "0");
					String SP_C_WAIT_TIME = pref.getString(Data.SP_C_WAIT_TIME, "0");
					
					Data.totalDistance = Double.parseDouble(SP_C_TOTAL_DISTANCE);
					Data.totalFare = Double.parseDouble(SP_C_TOTAL_FARE);
					Data.waitTime = SP_C_WAIT_TIME;
					
					
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
					
				}
				
			}
		}
		
	}
	
	
	
	
	
	
	public static void clearSPData(final Context context){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
        	
	        	SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
	    		Editor editor = pref.edit();
        		
        		editor.putString(Data.SP_DRIVER_SCREEN_MODE, "");
        		
        		editor.putString(Data.SP_D_ENGAGEMENT_ID, "");
        		editor.putString(Data.SP_D_CUSTOMER_ID, "");
        		editor.putString(Data.SP_D_LATITUDE, "0");
        		editor.putString(Data.SP_D_LONGITUDE, "0");
        		editor.putString(Data.SP_D_CUSTOMER_NAME, "");
        		editor.putString(Data.SP_D_CUSTOMER_IMAGE, "");
        		editor.putString(Data.SP_D_CUSTOMER_PHONE, "");
        		editor.putString(Data.SP_D_CUSTOMER_RATING, "");
        		
        		
        		
        		
        		editor.putString(Data.SP_TOTAL_DISTANCE, "0");
        		editor.putString(Data.SP_WAIT_TIME, "0");
        		editor.putString(Data.SP_RIDE_TIME, "0");
        		editor.putString(Data.SP_LAST_LATITUDE, "0");
        		editor.putString(Data.SP_LAST_LONGITUDE, "0");
        		
        		
        		
        		
        		editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, "");

        		editor.putString(Data.SP_C_SESSION_ID, "");
        		editor.putString(Data.SP_C_ENGAGEMENT_ID, "");
        		editor.putString(Data.SP_C_DRIVER_ID, "");
        		editor.putString(Data.SP_C_LATITUDE, "0");
        		editor.putString(Data.SP_C_LONGITUDE, "0");
        		editor.putString(Data.SP_C_DRIVER_NAME, "");
        		editor.putString(Data.SP_C_DRIVER_IMAGE, "");
        		editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, "");
        		editor.putString(Data.SP_C_DRIVER_PHONE, "");
				editor.putString(Data.SP_C_DRIVER_RATING, "");
        		editor.putString(Data.SP_C_DRIVER_DISTANCE, "0");
        		editor.putString(Data.SP_C_DRIVER_DURATION, "0");
        		
        		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
        		editor.putString(Data.SP_C_TOTAL_FARE, "0");
        		editor.putString(Data.SP_C_WAIT_TIME, "0");
        		
        	
	        	editor.commit();
	    		
	        	
	        	Database database = new Database(context);
				database.deleteSavedPath();
				database.close();
        	
		
			}
		}).start();
	}
	
	
	
	
}
