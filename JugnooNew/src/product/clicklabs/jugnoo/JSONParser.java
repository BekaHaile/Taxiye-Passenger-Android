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
		
		try{
			Data.termsAgreed = userData.getInt("terms_agreed");
			Data.termsAgreed = 1;
		} catch(Exception e){
			Data.termsAgreed = 1;
		}
		Data.termsAgreed = 1;
		
		Data.userData = new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"), userData.getString("id"), userData.getString("referral_code"));
		
		if(Data.termsAgreed == 1){
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			Editor editor = pref.edit();
			editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
			editor.putString(Data.SP_ID_KEY, Data.userData.id);
			editor.commit();
		}
		
		try{
			int currentUserStatus = userData.getInt("current_user_status");
			
			if(currentUserStatus == 1){
				new DriverServiceOperations().startDriverService(context);
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
				new DriverServiceOperations().stopService(context);
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
		
		parseFareDetails(userData);
		
	}
	
	
	public void parseFareDetails(JSONObject userData){
		try{
			JSONArray fareDetailsArr = userData.getJSONArray("fare_details");
			JSONObject fareDetails0 = fareDetailsArr.getJSONObject(0);
			double farePerMin = 0;
			double freeMinutes = 0;
			if(fareDetails0.has("fare_per_min")){
				farePerMin = fareDetails0.getDouble("fare_per_min");
			}
			if(fareDetails0.has("fare_threshold_time")){
				freeMinutes = fareDetails0.getDouble("fare_threshold_time");
			}
			Data.fareStructure = new FareStructure(fareDetails0.getDouble("fare_fixed"), 
					fareDetails0.getDouble("fare_threshold_distance"), 
					fareDetails0.getDouble("fare_per_km"), 
					farePerMin, freeMinutes);
		} catch(Exception e){
			e.printStackTrace();
			Data.fareStructure = new FareStructure(25, 2, 6, 1, 6);
		}
	}
	
	
	public String parseAccessTokenLoginData(Context context, String response, String accessToken, String id) throws Exception{
		
		JSONObject jObj = new JSONObject(response);
		
		//Fetching login data
		JSONObject jLoginObject = jObj.getJSONObject("login");
		JSONObject userData = jLoginObject.getJSONObject("user_data");
		
		Data.userData = new UserData(accessToken, userData.getString("user_name"), 
				userData.getString("user_image"), id, userData.getString("referral_code"));
		
		parseFareDetails(userData);
		
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
		else{
			HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			
			
			//Fetching drivers info
			JSONObject jDriversObject = jObj.getJSONObject("drivers");
			parseDriversToShow(jDriversObject, "data");
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
			String engagementId = "", userId = "", latitude = "", longitude = "", customerName = "", customerImage = "", customerPhone = "", customerRating = "";
			
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
									
									Database2.getInstance(context).deleteAllDriverRequests();
									for(int i=0; i<jActiveRequests.length(); i++){
										JSONObject jActiveRequest = jActiveRequests.getJSONObject(i);
										 String requestEngagementId = jActiveRequest.getString("engagement_id");
		    	    					 String requestUserId = jActiveRequest.getString("user_id");
		    	    					 double requestLatitude = jActiveRequest.getDouble("pickup_latitude");
		    	    					 double requestLongitude = jActiveRequest.getDouble("pickup_longitude");
		    	    					 String requestAddress = jActiveRequest.getString("pickup_location_address");
		    	    					 String requestStartTime = new DateOperations().getSixtySecAfterCurrentTime();
		    	    					 
		    	    					 Database2.getInstance(context).insertDriverRequest(requestEngagementId, requestUserId, 
		    	    							 ""+requestLatitude, ""+requestLongitude, requestStartTime, requestAddress);
		    	    					 
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
										customerRating = jObject.getString("rating");
									}
									else{
										
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
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating);
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
					
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating);
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					HomeActivity.previousWaitTime = Double.parseDouble(pref.getString(Data.SP_WAIT_TIME, "0"));
					HomeActivity.previousRideTime = Double.parseDouble(pref.getString(Data.SP_RIDE_TIME, "0"));
					
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
					
					if(Utils.compareDouble(HomeActivity.totalDistance, -1.0) == 0){
						HomeActivity.totalDistance = -1;
					}
					
				}
				else{
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
				
			}
			
			
		}
		else{ // TODO for customer
			
			String screenMode = "";

			int engagementStatus = -1;
			String engagementId = "", sessionId = "",  userId = "", latitude = "", longitude = "", 
					driverName = "", driverImage = "", driverCarImage = "", driverPhone = "", driverRating = "", 
					pickupLatitude = "", pickupLongitude = "";
			
			try{
							
							if(jObject1.has("error")){
								returnResponse = HttpRequester.SERVER_TIMEOUT;
								return returnResponse;
							}
							else{
							
							
//							response = {
//									"log": "Assigning driver", 
//									"flag": constants.responseFlags.ASSIGNING_DRIVERS,
//									"session_id": 2020
//							};

							
							
//							"flag": constants.responseFlags.ENGAGEMENT_DATA, 
//							"last_engagement_info":[
//							{
//								Òdriver_idÓ, 
//								Òpickup_latitudeÓ, 
//								Òpickup_longitudeÓ, 
//								Òengagement_idÓ, 
//								ÒstatusÓ, 
//								Òsession_idÓ,
//								Òuser_nameÓ, 
//								Òphone_noÓ, 
//								Òuser_imageÓ, 
//								Òdriver_car_imageÓ, 
//								Òcurrent_location_latitudeÓ, 
//								Òcurrent_location_longitudeÓ, 
//								ÒratingÓ
//								}
//							]

							
								int flag = jObject1.getInt("flag");
								
								if(ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag){
									sessionId = jObject1.getString("session_id");
									engagementStatus = EngagementStatus.REQUESTED.getOrdinal();
								}
								else if(ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag){
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
								}
							
							}
			} catch(Exception e){
				e.printStackTrace();
				engagementStatus = -1;
				returnResponse = HttpRequester.SERVER_TIMEOUT;
				return returnResponse;
			}
			
			HomeActivity.userMode = UserMode.PASSENGER;
			
			if(EngagementStatus.REQUESTED.getOrdinal() == engagementStatus){
				screenMode = Data.P_ASSIGNING;
			}
			else if(EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus){
				screenMode = Data.P_REQUEST_FINAL;
			}
			else if(EngagementStatus.STARTED.getOrdinal() == engagementStatus){
				screenMode = Data.P_IN_RIDE;
			}
			else if(EngagementStatus.ENDED.getOrdinal() == engagementStatus){
				screenMode = "";
			}
			else{
				screenMode = "";
			}
			
			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
				clearSPData(context);
			}
			else if(Data.P_ASSIGNING.equalsIgnoreCase(screenMode)){
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
				Data.cSessionId = sessionId;
				
				clearSPData(context);
			}
			else{
				
				SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				
				Data.cSessionId = sessionId;
				Data.cEngagementId = engagementId;
				Data.cDriverId = userId;
				
				Data.pickupLatLng = new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude));
				
				double dLatitude = Double.parseDouble(latitude);
				double dLongitude = Double.parseDouble(longitude);
				
				String SP_C_DRIVER_DISTANCE = pref.getString(Data.SP_C_DRIVER_DISTANCE, "0");
				String SP_C_DRIVER_DURATION = pref.getString(Data.SP_C_DRIVER_DURATION, "");
				
				Data.assignedDriverInfo = new DriverInfo(userId, dLatitude, dLongitude, driverName, 
						driverImage, driverCarImage, driverPhone, driverRating);
				Log.e("Data.assignedDriverInfo on login","="+Data.assignedDriverInfo.latLng);
				Data.assignedDriverInfo.distanceToReach = SP_C_DRIVER_DISTANCE;
				Data.assignedDriverInfo.durationToReach = SP_C_DRIVER_DURATION;
				
				Log.e("Data.assignedDriverInfo.durationToReach in get_current_user_status", "="+Data.assignedDriverInfo.durationToReach);
				
				
				if(Data.P_REQUEST_FINAL.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
				}
				else if(Data.P_IN_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					
					if(Utils.compareDouble(HomeActivity.totalDistance, -1.0) == 0){
						Data.startRidePreviousLatLng = Data.pickupLatLng;
						HomeActivity.totalDistance = -1;
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
					String SP_C_RIDE_TIME = pref.getString(Data.SP_C_RIDE_TIME, "0");
					
					Data.totalDistance = Double.parseDouble(SP_C_TOTAL_DISTANCE);
					Data.totalFare = Double.parseDouble(SP_C_TOTAL_FARE);
					Data.waitTime = SP_C_WAIT_TIME;
					Data.rideTime = SP_C_RIDE_TIME;
					
					
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
					
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
