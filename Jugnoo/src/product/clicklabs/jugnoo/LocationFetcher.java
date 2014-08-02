package product.clicklabs.jugnoo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class LocationFetcher {

	private Location location; // location
	double latitude = 30.7500; // latitude default chandigarh latlng
	double longitude = 76.7800; // longitude
	AlertDialog alertDialog;
	public MyLocationListener listener;
	public LocationManager locationManager;
	public String provider;
	
	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcher(Context context){
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		if(isLocationEnabled(context)){
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				provider = LocationManager.GPS_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, listener);
			}
			else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
				provider = LocationManager.NETWORK_PROVIDER;
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, listener);
			}
		}
		else{
			showSettingsAlert(context);
		}
	}
	

	
	double distance(Location start, Location end) {
		try {
			double distance = start.distanceTo(end);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
	
	/**
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
	public boolean isLocationEnabled(Context context) {
		try{
			ContentResolver contentResolver = context.getContentResolver();
			boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
			boolean netStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);
			return gpsStatus || netStatus;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showGooglePlayErrorAlert(final Activity mContext){
		try{
			if(alertDialog != null && alertDialog.isShowing()){
				alertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle("Google Play Services Error");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Google Play services not found or outdated. Please install Google Play Services?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	dialog.dismiss();
		            	Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.google.android.gms"));
						mContext.startActivity(intent);
		            }
		        });
		 
		        // on pressing cancel button
		        alertDialogPrepare.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	mContext.finish();
		            }
		        });
		 
		        alertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        alertDialog.show();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(final Context mContext){
		try{
			if(alertDialog != null && alertDialog.isShowing()){
				alertDialog.dismiss();
			}
				AlertDialog.Builder alertDialogPrepare = new AlertDialog.Builder(mContext);
		   	 
		        // Setting Dialog Title
		        alertDialogPrepare.setTitle("Loaction Settings");
		        alertDialogPrepare.setCancelable(false);
		 
		        // Setting Dialog Message
		        alertDialogPrepare.setMessage("Location is not enabled. Do you want to go to settings menu?");
		 
		        // On pressing Settings button
		        alertDialogPrepare.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		            	mContext.startActivity(intent);
		            	dialog.dismiss();
		            }
		        });
		 
		        // on pressing cancel button
		        alertDialogPrepare.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		            }
		        });
		 
		        alertDialog = alertDialogPrepare.create();
		        
		        // Showing Alert Message
		        alertDialog.show();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		try{
		if(location == null){
			location = locationManager.getLastKnownLocation(provider);
			if(location != null){
				latitude = location.getLatitude();
			}
		}else{
			latitude = location.getLatitude();
		}
		
		} catch(Exception e){Log.e("e","="+e.toString());}
		
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		try{
		if(location == null){
			location = locationManager.getLastKnownLocation(provider);
			if(location != null){
				longitude = location.getLatitude();
				Log.e("last location","="+longitude);
			}
			
		}
		else{
			longitude = location.getLongitude();
		}
		} catch(Exception e){Log.e("e","="+e.toString());}
		
		return longitude;
	}
	
	

	
	public void destroy(){
		try{
			Log.e("location","destroy");
			locationManager.removeUpdates(listener);
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}


	class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			Log.i("**************************************", "Location changed "+loc);
			LocationFetcher.this.location = loc;
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

}