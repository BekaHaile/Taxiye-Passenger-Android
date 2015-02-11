package product.clicklabs.jugnoo.driver;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CouponInfo;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.FareStructure;
import product.clicklabs.jugnoo.driver.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;
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
		
		Data.termsAgreed = 1;
		
		Data.userData = parseUserData(context, userData);
		
		if(Data.termsAgreed == 1){
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			Editor editor = pref.edit();
			editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
			editor.commit();
		}
		
		try{
			int currentUserStatus = userData.getInt("current_user_status");
			
			if(currentUserStatus == 1){
				Database2.getInstance(context).updateUserMode(Database2.UM_DRIVER);
				new DriverServiceOperations().startDriverService(context);
				HomeActivity.userMode = UserMode.DRIVER;
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
			}
			else if(currentUserStatus == 2){
				Database2.getInstance(context).updateUserMode(Database2.UM_PASSENGER);
				new DriverServiceOperations().stopService(context);
				HomeActivity.userMode = UserMode.PASSENGER;
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
			}
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.userMode = UserMode.PASSENGER;
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
		}
		
	}
	
	
	public static FareStructure parseFareObject(JSONObject fareDetails){
		try{
//			{
//		        "id": 1,
//		        "fare_fixed": 25,
//		        "fare_per_km": 6,
//		        "fare_threshold_distance": 2,
//		        "fare_per_min": 1,
//		        "fare_threshold_time": 0,
//		        "type": 0,
//		        "per_ride_driver_subsidy": 20
//		    }
			
//			For testing
//			return new FareStructure(40, 1, 20, 2, 1, 0, 0);
			
			return new FareStructure(fareDetails.getDouble("fare_fixed"), 
					fareDetails.getDouble("fare_threshold_distance"), 
					fareDetails.getDouble("fare_per_km"), 
					fareDetails.getDouble("fare_per_min"), 
					fareDetails.getDouble("fare_threshold_time"), 
					fareDetails.getDouble("fare_per_waiting_min"), 
					fareDetails.getDouble("fare_threshold_waiting_time"));
		} catch(Exception e){
			e.printStackTrace();
			return new FareStructure(25, 2, 6, 1, 6, 0, 0);
		}
	}
	
	
	public static CouponInfo parseCouponInfo(JSONObject couponObject){
		try{
			
//			"coupon": {
//	        "account_id": 367,
//	        "coupon_id": 1,
//	        "title": "Free ride",
//	        "description": "Your next ride with Jugnoo upto Rs. 100 will be FREE. \n\nTerms of Use:\n1. The coupon will be applied automatically at the end of your next ride.\n2. Only one coupon will be applied in one ride.\n3. The maximum value of this coupon is Rs. 100 and you will have to pay the remaining amount at the end of the ride.\n4. Jugnoo reserves the right to discontinue the coupon at its discretion.",
//	        "discount": 100,
//	        "maximum": 100,
//	        "image": "",
//	        "type": 0,
//	        "subtitle": "upto Rs. 100"
//	    }

//			For testing
//			CouponInfo couponInfo = new CouponInfo(0, 
//					"50% off", 
//					"upto 100/-", 
//					"discount", 
//					50, 
//					100);
			
			CouponInfo couponInfo = new CouponInfo(couponObject.getInt("type"), 
					couponObject.getString("title"), 
					couponObject.getString("subtitle"), 
					couponObject.getString("description"), 
					couponObject.getDouble("discount"), 
					couponObject.getDouble("maximum"));
			return couponInfo;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public UserData parseUserData(Context context, JSONObject userData) throws Exception{
		
		int canSchedule = 0, canChangeLocation = 0, schedulingLimitMinutes = 0, isAvailable = 1, exceptionalDriver = 0, gcmIntent = 1, christmasIconEnable = 0, 
				nukkadEnable = 0, freeRideIconDisable = 1;
		String phoneNo = "";
		
		if(userData.has("can_schedule")){
			canSchedule = userData.getInt("can_schedule");
		}
		
		if(userData.has("can_change_location")){
			canChangeLocation = userData.getInt("can_change_location");
		}
		
		if(userData.has("scheduling_limit")){
			schedulingLimitMinutes = userData.getInt("scheduling_limit");
		}
		
		if(userData.has("is_available")){
			isAvailable = userData.getInt("is_available");
		}
		
		if(userData.has("exceptional_driver")){
			exceptionalDriver = userData.getInt("exceptional_driver");
		}
		
		try{
			if(userData.has("gcm_intent")){
				gcmIntent = userData.getInt("gcm_intent");
				Database2.getInstance(context).updateDriverGcmIntent(gcmIntent);
				Database2.getInstance(context).close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			if(userData.has("christmas_icon_enable")){
				christmasIconEnable = userData.getInt("christmas_icon_enable");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(userData.has("phone_no")){
			phoneNo = userData.getString("phone_no");
		}
		
		try{
			if(userData.has("nukkad_enable")){
				nukkadEnable = userData.getInt("nukkad_enable");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(userData.has("free_ride_icon_disable")){
			freeRideIconDisable = userData.getInt("free_ride_icon_disable");
		}
		
		//"gcm_intent": 0, christmas_icon_enable
		
		return new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"), userData.getString("referral_code"), phoneNo, 
				canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, christmasIconEnable, nukkadEnable, 
				freeRideIconDisable);
	}
	
	public String parseAccessTokenLoginData(Context context, String response, String accessToken) throws Exception{
		
		JSONObject jObj = new JSONObject(response);
		
		//Fetching login data
		JSONObject jLoginObject = jObj.getJSONObject("login");
		JSONObject userData = jLoginObject.getJSONObject("user_data");
		
		Data.userData = parseUserData(context, userData);
		
		//current_user_status = 1 driver or 2 user
		int currentUserStatus = userData.getInt("current_user_status");
		if(currentUserStatus == 1){
			Database2.getInstance(context).updateUserMode(Database2.UM_DRIVER);
		}
		else if(currentUserStatus == 2){
			Database2.getInstance(context).updateUserMode(Database2.UM_PASSENGER);
		}
		
		//Fetching user current status
		JSONObject jUserStatusObject = jObj.getJSONObject("status");
		String resp = parseCurrentUserStatus(context, currentUserStatus, jUserStatusObject);
				
		return resp;
	}
	
	
	
	public String getUserStatus(Context context, String accessToken, int currentUserStatus){
		String returnResponse = "";
		try{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
			HttpRequester simpleJSONParser = new HttpRequester();
			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/get_current_user_status", nameValuePairs);
			Log.e("result of = user_status", "="+result);
			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
				returnResponse = HttpRequester.SERVER_TIMEOUT;
				return returnResponse;
			}
			else{
				JSONObject jObject1 = new JSONObject(result);
				returnResponse = parseCurrentUserStatus(context, currentUserStatus, jObject1);
				return returnResponse;
			}
		} catch(Exception e){
			e.printStackTrace();
			returnResponse = HttpRequester.SERVER_TIMEOUT;
			return returnResponse;
		}
	}
	
	
	public void parseDriversToShow(JSONObject jObject, String jsonArrayKey){
		try{
			JSONArray data = jObject.getJSONArray(jsonArrayKey);
			Data.driverInfos.clear();
			for(int i=0; i<data.length(); i++){
				JSONObject dataI = data.getJSONObject(i);
				String userId = dataI.getString("user_id");
				double latitude = dataI.getDouble("latitude");
				double longitude = dataI.getDouble("longitude");
				String userName = dataI.getString("user_name");
				String userImage = dataI.getString("user_image");
				String driverCarImage = dataI.getString("driver_car_image");
				String phoneNo = dataI.getString("phone_no");
				String rating = dataI.getString("rating");
				Data.driverInfos.add(new DriverInfo(userId, latitude, longitude, userName, userImage, driverCarImage, phoneNo, rating));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public String parseCurrentUserStatus(Context context, int currentUserStatus, JSONObject jObject1){
		
		String returnResponse = "";
		
		if(currentUserStatus == 1){ // TODO for driver
			
			String screenMode = "";
			
			int engagementStatus = -1;
			String engagementId = "", userId = "", latitude = "", longitude = "", customerName = "", customerImage = "", customerPhone = "", customerRating = "4", schedulePickupTime = "";
			int freeRide = 0;
			CouponInfo couponInfo = null;
			
			try{
							
							if(jObject1.has("error")){
								returnResponse = HttpRequester.SERVER_TIMEOUT;
								return returnResponse;
							}
							else{
							
//							{
//								"flag": constants.responseFlags.ACTIVE_REQUESTS,
//								"active_requests":[
//									{
//								Òengagement_idÓ, 
//								Òuser_idÓ, 
//								Òpickup_latitudeÓ, 
//								Òpickup_longitudeÓ, 
//								Òpickup_location_addressÓ, 
//								Òcurrent_timeÓ
//								}
//								]};
							
							
							
//							{
//							"flag": constants.responseFlags.ENGAGEMENT_DATA,
//							"last_engagement_info":[
//							{
//							Òuser_idÒ,
//							Òpickup_latitudeÒ,
//							Òpickup_longitudeÒ,
//							Òengagement_idÒ,
//							ÒstatusÒ,
//							Òuser_nameÒ,
//							Òphone_noÒ,
//							Òuser_imageÒ,
//							ÒratingÒ
//							}
//							]
							
								int flag = jObject1.getInt("flag");
								
								if(ApiResponseFlags.ACTIVE_REQUESTS.getOrdinal() == flag){
									
									JSONArray jActiveRequests = jObject1.getJSONArray("active_requests");
									
									Data.driverRideRequests.clear();
									for(int i=0; i<jActiveRequests.length(); i++){
										JSONObject jActiveRequest = jActiveRequests.getJSONObject(i);
										 String requestEngagementId = jActiveRequest.getString("engagement_id");
		    	    					 String requestUserId = jActiveRequest.getString("user_id");
		    	    					 double requestLatitude = jActiveRequest.getDouble("pickup_latitude");
		    	    					 double requestLongitude = jActiveRequest.getDouble("pickup_longitude");
		    	    					 String requestAddress = jActiveRequest.getString("pickup_location_address");
		    	    					 String requestStartTime = DateOperations.getSixtySecAfterCurrentTime();
		    	    					 
		    	    					 Data.driverRideRequests.add(new DriverRideRequest(requestEngagementId, requestUserId, 
		    	    								new LatLng(requestLatitude, requestLongitude), requestStartTime, requestAddress));
		    	    					 
		    	    					 Log.i("inserter in db", "insertDriverRequest = "+requestEngagementId);
									}
									
									Database2.getInstance(context).close();
									
									if(jActiveRequests.length() == 0){
										GCMIntentService.stopRing();
									}
									
								}
								else if(ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag){
									JSONArray lastEngInfoArr = jObject1.getJSONArray("last_engagement_info");
									JSONObject jObject = lastEngInfoArr.getJSONObject(0);
									
									engagementStatus = jObject.getInt("status");
									
									if((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) || 
											(EngagementStatus.STARTED.getOrdinal() == engagementStatus)){
										engagementId = jObject.getString("engagement_id");
										userId = jObject.getString("user_id");
										latitude = jObject.getString("pickup_latitude");
										longitude = jObject.getString("pickup_longitude");
										customerName = jObject.getString("user_name");
										customerImage = jObject.getString("user_image");
										customerPhone = jObject.getString("phone_no");
										if(jObject.has("rating")){
											customerRating = jObject.getString("rating");
										}
										
										int isScheduled = 0;
										if(jObject.has("is_scheduled")){
											isScheduled = jObject.getInt("is_scheduled");
											if(isScheduled == 1 && jObject.has("pickup_time")){
												schedulePickupTime = jObject.getString("pickup_time");
											}
										}
										
										if(jObject.has("free_ride")){
											freeRide = jObject.getInt("free_ride");
										}
										
										if(jObject.has("fare_details")){
											try{
												Data.fareStructure = JSONParser.parseFareObject(jObject.getJSONObject("fare_details"));
											} catch(Exception e){
												e.printStackTrace();
											}
										}
										
										if(jObject.has("coupon")){
											try{
												couponInfo = JSONParser.parseCouponInfo(jObject.getJSONObject("coupon"));
											} catch(Exception e){
												e.printStackTrace();
											}
										}
										
									}
								}
							
							}
			} catch(Exception e){
				e.printStackTrace();
				engagementStatus = -1;
				returnResponse = HttpRequester.SERVER_TIMEOUT;
				return returnResponse;
			}
			
			
			HomeActivity.userMode = UserMode.DRIVER;
			
			// 0 for request, 1 for accepted,2 for started,3 for ended, 4 for rejected by driver, 5 for rejected by user,6 for timeout, 7 for nullified by chrone
			if(EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus){
				screenMode = Data.D_START_RIDE;
			}
			else if(EngagementStatus.STARTED.getOrdinal() == engagementStatus){
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
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating, freeRide, couponInfo);
					Data.assignedCustomerInfo.schedulePickupTime = schedulePickupTime;
					
				}
				else if(Data.D_IN_RIDE.equalsIgnoreCase(screenMode)){
					
					SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
					
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
					
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating, freeRide, couponInfo);
					Data.assignedCustomerInfo.schedulePickupTime = schedulePickupTime;
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					HomeActivity.previousWaitTime = Long.parseLong(pref.getString(Data.SP_WAIT_TIME, "0"));
					
//					double previousRideTime = Double.parseDouble(pref.getString(Data.SP_RIDE_TIME, "0"));
					long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis()));
					long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
					if(timeDiffToAdd > 0){
						HomeActivity.previousRideTime = timeDiffToAdd;
					}
					else{
						HomeActivity.previousRideTime = 0;
					}
					
					
					
					HomeActivity.waitStart = 2;
					
					String lat1 = pref.getString(Data.SP_LAST_LATITUDE, "0");
					String lng1 = pref.getString(Data.SP_LAST_LONGITUDE, "0");
					
					Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
					
					Log.e("Data on app restart", "-----");
					Log.i("HomeActivity.totalDistance", "="+HomeActivity.totalDistance);
					Log.i("Data.startRidePreviousLatLng", "="+Data.startRidePreviousLatLng);
					Log.e("----------", "-----");
					
					Log.writePathLogToFile(Data.dEngagementId, "Got from SP totalDistance = "+HomeActivity.totalDistance);
					Log.writePathLogToFile(Data.dEngagementId, "Got from SP Data.startRidePreviousLatLng = "+Data.startRidePreviousLatLng);
					
				}
				else{
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
				
			}
			
			
			
		}
		
		return returnResponse;
	}
	
	
	
	
	
	
	
	
	public void clearSPData(final Context context) {
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

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_WAIT_TIME, "0");
		editor.putString(Data.SP_RIDE_TIME, "0");
		editor.putString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis());
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
		editor.putString(Data.SP_C_DRIVER_DURATION, "");

		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
		editor.putString(Data.SP_C_TOTAL_FARE, "0");
		editor.putString(Data.SP_C_WAIT_TIME, "0");
		editor.putString(Data.SP_C_RIDE_TIME, "0");

		editor.commit();

		Database.getInstance(context).deleteSavedPath();
		Database.getInstance(context).close();

	}
	
	
	
	
}
