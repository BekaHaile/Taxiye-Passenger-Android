package product.clicklabs.jugnoo;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {
	
	public static String MAPS_BROWSER_KEY = "AIzaSyAHVDCyeC13xO_GxG5zE8_wbRJolqkBg90";
	
	public static double latitude = 30.7500, longitude = 76.7800;
	
	public static LatLng chandigarhLatLng = new LatLng(30.7500, 76.7800);
	
	public static ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
	
	
	public static LatLng getChandigarhLatLng(){
		if(chandigarhLatLng == null){
			chandigarhLatLng = new LatLng(30.7500, 76.7800);
		}
		return chandigarhLatLng;
	}
	
	
}
