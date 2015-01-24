package com.example.androidgpsmetering;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GpsDistanceCalculator {
	
	private static GpsDistanceCalculator instance;
	
	private static final long LOCATION_UPDATE_INTERVAL = 10000; // in milliseconds
	public static final double MAX_DISPLACEMENT_THRESHOLD = 200; //in meters
	
	double totalDistance;
	Location lastLocation;
	
	GPSForegroundLocationFetcher gpsForegroundLocationFetcher;
	GPSLocationUpdate gpsLocationUpdate;
	GpsDistanceUpdater gpsDistanceUpdater;
	Activity activity;
	
	public ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList<LatLngPair>();
	ArrayList<DistanceMatrixAsyncTask> distanceMatrixAsyncTasks = new ArrayList<DistanceMatrixAsyncTask>();
	
	private String LOCATION_SP = "location_sp",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng",
			TOTAL_DISTANCE = "total_distance",
			TRACKING = "tracking";
	
	private GpsDistanceCalculator(Activity activity, GpsDistanceUpdater gpsDistanceUpdater, double totalDistance){
		this.activity = activity;
		this.totalDistance = totalDistance;
		this.lastLocation = null;
		this.gpsDistanceUpdater = gpsDistanceUpdater;
		this.deltaLatLngPairs = new ArrayList<LatLngPair>();
		this.distanceMatrixAsyncTasks = new ArrayList<DistanceMatrixAsyncTask>();
		
		this.gpsLocationUpdate = new GPSLocationUpdate() {
			
			@Override
			public void onGPSLocationChanged(Location location) {
				if((Utils.compareDouble(location.getLatitude(), 0.0) != 0) && (Utils.compareDouble(location.getLongitude(), 0.0) != 0)){
					drawLocationChanged(location);
				}
				GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistance(GpsDistanceCalculator.this.totalDistance);
			}
		};
		
		disconnectGPSListener();
		this.gpsForegroundLocationFetcher = null;
		initializeGPSForegroundLocationFetcher(activity);
		
		Log.i("initiliazed", "=done");
	}
	
	public static GpsDistanceCalculator getInstance(Activity activity, GpsDistanceUpdater gpsDistanceUpdater, double totalDistance){
		if(instance == null){
			instance = new GpsDistanceCalculator(activity, gpsDistanceUpdater, totalDistance);
		}
		instance.gpsDistanceUpdater = gpsDistanceUpdater;
		instance.totalDistance = totalDistance;
		return instance;
	}
	
	
	public void start(){
		if(1 != getTrackingFromSP(activity)){
			saveTotalDistanceToSP(activity, -1);
			connectGPSListener(activity);
			saveTrackingToSP(activity, 1);
		}
	}
	
	public void resume(){
		if(1 == getTrackingFromSP(activity)){
			connectGPSListener(activity);
		}
	}
	
	public void pause(){
		if(1 == getTrackingFromSP(activity)){
			saveData(activity, lastLocation, totalDistance);
			disconnectGPSListener();
		}
	}
	
	public void stop(){
		saveLatLngToSP(activity, 0, 0);
		saveTotalDistanceToSP(activity, -1);
		disconnectGPSListener();
		saveTrackingToSP(activity, 0);
		instance = null;
	}
	
	public void initializeGPSForegroundLocationFetcher(Context context){
		if(gpsForegroundLocationFetcher == null){
			gpsForegroundLocationFetcher = new GPSForegroundLocationFetcher(context, gpsLocationUpdate, LOCATION_UPDATE_INTERVAL);
		}
	}
	
	public void connectGPSListener(Context context){
		disconnectGPSListener();
		try {
			initializeGPSForegroundLocationFetcher(context);
			gpsForegroundLocationFetcher.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectGPSListener(){
		try {
			if(gpsForegroundLocationFetcher != null){
				gpsForegroundLocationFetcher.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public synchronized void drawLocationChanged(Location location){
		try {

			final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

			if (Utils.compareDouble(totalDistance, -1.0) == 0) {
				lastLocation = null;
				Log.i("lastLocation made null", "=" + lastLocation);
			}

			Log.i("lastLocation", "=" + lastLocation);
			Log.i("totalDistance", "=" + totalDistance);

			if (lastLocation != null) {
				final LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
				addLatLngPathToDistance(lastLatLng, currentLatLng);
			} else {
				if (Utils.compareDouble(totalDistance, -1.0) == 0) {
					totalDistance = 0;
				}
				LatLng lastLatLng = getSavedLatLngFromSP(activity);
				if((Utils.compareDouble(lastLatLng.latitude, 0.0) != 0) && (Utils.compareDouble(lastLatLng.longitude, 0.0) != 0)){
					addLatLngPathToDistance(getSavedLatLngFromSP(activity), currentLatLng);
				}
			}

			lastLocation = location;

			saveData(activity, lastLocation, totalDistance);

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public synchronized void addLatLngPathToDistance(final LatLng lastLatLng, final LatLng currentLatLng){
		try {
			double displacement = MapUtils.distance(lastLatLng, currentLatLng);
			Log.i("displacement", "="+displacement);
			if(Utils.compareDouble(displacement, MAX_DISPLACEMENT_THRESHOLD) == -1){
				boolean validDistance = updateTotalDistance(lastLatLng, currentLatLng, displacement);
				if(validDistance){
					GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistance(totalDistance);
				}
			}
			else{
				callGoogleDistanceMatrixAPI(lastLatLng, currentLatLng, displacement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized boolean updateTotalDistance(LatLng lastLatLng, LatLng currentLatLng, double deltaDistance){
		boolean validDistance = false;
		if(deltaDistance > 0.0){
			LatLngPair latLngPair = new LatLngPair(lastLatLng, currentLatLng, deltaDistance);
			if(deltaLatLngPairs == null){
				deltaLatLngPairs = new ArrayList<LatLngPair>();
			}
			
			if(!deltaLatLngPairs.contains(latLngPair)){
				totalDistance = totalDistance + deltaDistance;
				deltaLatLngPairs.add(latLngPair);
				validDistance = true;
			}
		}
		return validDistance;
	}
	
	
	
	public synchronized void callGoogleDistanceMatrixAPI(LatLng lastLatLng, LatLng currentLatLng, double displacement){
		if(distanceMatrixAsyncTasks == null){
			distanceMatrixAsyncTasks = new ArrayList<DistanceMatrixAsyncTask>();
		}
		DistanceMatrixAsyncTask distanceMatrixAsyncTask = new DistanceMatrixAsyncTask(lastLatLng, currentLatLng, displacement);
		if(!distanceMatrixAsyncTasks.contains(distanceMatrixAsyncTask)){
			distanceMatrixAsyncTasks.add(distanceMatrixAsyncTask);
			distanceMatrixAsyncTask.execute();
		}
	}
	
	class DistanceMatrixAsyncTask extends AsyncTask<Void, Void, String>{
	    String url;
	    double displacementToCompare;
	    LatLng source, destination;
	    DistanceMatrixAsyncTask(LatLng source, LatLng destination, double displacementToCompare){
	    	this.source = source;
	    	this.destination = destination;
	        this.url = MapUtils.makeDistanceMatrixURL(source, destination);
	        this.displacementToCompare = displacementToCompare;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	return new HttpRequester().getJSONFromUrl(url);
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        if(result!=null){
	            updateGAPIDistance(result, displacementToCompare, source, destination);
	        }
	        distanceMatrixAsyncTasks.remove(this);
	    }
	    
	    
	    @Override
	    public boolean equals(Object o) {
	    	try{
	    		if((((DistanceMatrixAsyncTask)o).source == this.source) && (((DistanceMatrixAsyncTask)o).destination == this.destination)){
	    			return true;
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return false;
	    }
	}
	
	
	public synchronized void updateGAPIDistance(String result, double displacementToCompare, LatLng source, LatLng destination) {
	    try {
	    	double distanceOfPath = Double.MAX_VALUE;
	    	JSONObject jsonObject = new JSONObject(result);
	    	String status = jsonObject.getString("status");
	    	if("OK".equalsIgnoreCase(status)){
	    		JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
	    		distanceOfPath = element0.getJSONObject("distance").getDouble("value") ;
	    	}
	    	if(Utils.compareDouble(distanceOfPath, (displacementToCompare*1.8)) <= 0){														// distance would be approximately correct
		           boolean validDistance = updateTotalDistance(source, destination, distanceOfPath);
		           if(validDistance){
		        	   GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistance(totalDistance);
		           }
	    	}
	    	else{																									// displacement would be correct
	    		boolean validDistance = updateTotalDistance(source, destination, displacementToCompare);
	    		if(validDistance){
	    			GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistance(totalDistance);
	    		}
	    	}
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	} 
	
	
	private void saveData(Context context, Location location, double totalDistance){
		if(location != null){
			saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
			saveTotalDistanceToSP(context, totalDistance);
		}
	}
	
	public synchronized void saveLatLngToSP(Context context, double latitude, double longitude){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LOCATION_LAT, ""+latitude);
		editor.putString(LOCATION_LNG, ""+longitude);
		Log.e("latlng to sp", "= "+latitude+","+longitude);
		editor.commit();
	}
	
	public synchronized LatLng getSavedLatLngFromSP(Context context){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String latitude = preferences.getString(LOCATION_LAT, ""+ 0);
		String longitude = preferences.getString(LOCATION_LNG, ""+ 0);
		Log.e("latlng from sp", "= "+latitude+","+longitude);
		return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
	}
	
	
	public synchronized void saveTotalDistanceToSP(Context context, double totalDistance){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(TOTAL_DISTANCE, ""+totalDistance);
		editor.commit();
	}

	public synchronized double getSavedTotalDistanceFromSP(Context context){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String totalDistance = preferences.getString(TOTAL_DISTANCE, ""+ -1);
		return Double.parseDouble(totalDistance);
	}
	
	public synchronized void saveTrackingToSP(Context context, int tracking){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(TRACKING, ""+tracking);
		editor.commit();
	}

	public synchronized int getTrackingFromSP(Context context){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String tracking = preferences.getString(TRACKING, ""+ 0);
		return Integer.parseInt(tracking);
	}
	
}
