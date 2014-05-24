package product.clicklabs.jugnoo;

import java.util.ArrayList;

import com.androidquery.callback.ImageOptions;
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
	
	public static ArrayList<FavoriteLocation> favoriteLocations = new ArrayList<FavoriteLocation>();
	
	public static ArrayList<FriendInfo> friendInfos = new ArrayList<FriendInfo>();
	public static ArrayList<FriendInfo> friendInfosDuplicate = new ArrayList<FriendInfo>();
	
	public static LatLng getChandigarhLatLng(){
		if(chandigarhLatLng == null){
			chandigarhLatLng = new LatLng(30.7500, 76.7800);
		}
		return chandigarhLatLng;
	}
	
	
	
	
	public static ImageOptions imageOptionsRound(){
		ImageOptions options = new ImageOptions();
		 options.round = 10;
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	public static ImageOptions imageOptionsFullRound(){
		ImageOptions options = new ImageOptions();
		 options.round = 500;
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	public static ImageOptions imageOptions(){
		ImageOptions options = new ImageOptions();
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	
}
