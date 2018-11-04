package product.clicklabs.jugnoo.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MapUtils {

	private final String TAG = MapUtils.class.getSimpleName();
	
	public static double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);
//
//			double distance = location1.distanceTo(location2);
//			NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
//			DecimalFormat formatter = (DecimalFormat)nf;
//			formatter.applyPattern("#.#");
//			double distanceFormated = Double.parseDouble(formatter.format(distance));


			double distance = location1.distanceTo(location2);
			DecimalFormat decimalFormat = new DecimalFormat("#.#",new DecimalFormatSymbols(Locale.ENGLISH));
			double distanceFormated = Double.parseDouble(decimalFormat.format(distance));

			return distanceFormated;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	public static double getBearing(LatLng source, LatLng dest){
		//Source
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(source.latitude);
			location1.setLongitude(source.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(dest.latitude);
			location2.setLongitude(dest.longitude);
			double brng = location1.bearingTo(location2);
			return brng;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void rotateMarker1(final Marker marker, final float toRotation) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final float startRotation = marker.getRotation();
		final long duration = 300;

		final Interpolator interpolator = new LinearInterpolator();

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed / duration);

				float rot = t * toRotation + (1 - t) * startRotation;

				marker.setRotation(-rot > 180 ? rot / 2 : rot);
				if (t < 1.0) {
					// Post again 16ms later.
					handler.postDelayed(this, 16);
				}
			}
		});
	}
	
	
	//http://maps.googleapis.com/maps/api/directions/json?origin=30.7342187,76.78088307&destination=30.74571777,76.78635478&sensor=false&mode=driving&alternatives=false
	public static String makeDirectionsURL(LatLng source, LatLng destination) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(source.latitude));
		urlString.append(",");
		urlString.append(Double.toString(source.longitude));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destination.latitude));
		urlString.append(",");
		urlString.append(Double.toString(destination.longitude));
		urlString.append("&sensor=false&mode=driving&alternatives=false");
		return urlString.toString();
	}
	
	
	//https://maps.googleapis.com/maps/api/distancematrix/json?origins=30.75,76.78&destinations=30.78,76.79&language=EN&sensor=false
	
	public static String makeDistanceMatrixURL(LatLng source, LatLng destination){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?origins=");// from
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(source.longitude));
        urlString.append("&destinations=");// to
        urlString
                .append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&language=EN&sensor=false&alternatives=false");
        return urlString.toString();
	}
	
	
	
	public static List<LatLng> decodeDirectionsPolyline(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int bInt, shift = 0, result = 0;
	        do {
	            bInt = encoded.charAt(index++) - 63;
	            result |= (bInt & 0x1f) << shift;
	            shift += 5;
	        } while (bInt >= 0x20);
	        int dlat = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            bInt = encoded.charAt(index++) - 63;
	            result |= (bInt & 0x1f) << shift;
	            shift += 5;
	        } while (bInt >= 0x20);
	        int dlng = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
	        lng += dlng;

	        LatLng pLatLng = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(pLatLng);
	    }
	    return poly;
	}
	
	
	public static GAPIAddress parseGAPIIAddress(GoogleGeocodeResponse googleGeocodeResponse){
		GAPIAddress fullAddress = new GAPIAddress("Unnamed");
		try {
			String formatStr = MyApplication.getInstance().getString(R.string.geocode_address_format);
			StringBuilder addressSB = new StringBuilder();
			if(googleGeocodeResponse.getStatus().equalsIgnoreCase("OK")
					&& googleGeocodeResponse.results != null && googleGeocodeResponse.results.size() > 0) {
				String addressComparator = "";
				for (int i = googleGeocodeResponse.results.get(0).addressComponents.size()-1; i >= 0; i--) {
					GoogleGeocodeResponse.AddressComponent addressComponent = googleGeocodeResponse.results.get(0).addressComponents.get(i);
					if(addressComparator.contains(addressComponent.longName)){
						addressComponent.redundant = true;
					} else {
						addressComparator = addressComparator + addressComponent.longName + ",";
					}
				}

				String address = "";
				Log.e("MapUtils", "getPlaceById from poi response="+googleGeocodeResponse.results.get(0).getPlaceName());
				if(!TextUtils.isEmpty(googleGeocodeResponse.results.get(0).getPlaceName())){
					addressSB.append(googleGeocodeResponse.results.get(0).getPlaceName()).append(", ");
				}
				if(!TextUtils.isEmpty(formatStr) && formatStr.contains(",")) {
					String format[] = formatStr.split(",");
					for (String formatI : format) {
						for (GoogleGeocodeResponse.AddressComponent addressComponent : googleGeocodeResponse.results.get(0).addressComponents) {
							if(addressComponent.redundant){
								continue;
							}
							for(String type : addressComponent.types){
								if (type.contains(formatI) && !addressSB.toString().contains(addressComponent.longName)) {
									addressSB.append(addressComponent.longName).append(", ");
									break;
								}
							}
						}
					}
					if(addressSB.length() > 2 && googleGeocodeResponse.results.get(0).addressComponents.size() > 4) {
						address = addressSB.toString().substring(0, addressSB.length() - 2);
					}
				}
				if(TextUtils.isEmpty(address)){
					address = googleGeocodeResponse.results.get(0).formatted_address;
				}
				Log.e("addressSB", "===="+address);

				fullAddress = new GAPIAddress(address);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullAddress;
	}


	
	public static List<LatLng> getLatLngListFromPath(String result){
		try {
	    	 final JSONObject json = new JSONObject(result);
		     JSONArray routeArray = json.getJSONArray("routes");
		     JSONObject routes = routeArray.getJSONObject(0);
		     JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		     String encodedString = overviewPolylines.getString("points");
		     List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
		     return list;
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return new ArrayList<LatLng>();
	    }
	}

	public static void drawPathFromGoogle(Activity activity, final GoogleMap map, final LatLng sourceLatLng, final LatLng destinationLatLng){
		if (MyApplication.getInstance().isOnline()) {
			Response response = GoogleRestApis.INSTANCE.getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
					destinationLatLng.latitude + "," + destinationLatLng.longitude, false, "driving", false, "metric");
			String result = new String(((TypedByteArray)response.getBody()).getBytes());
			Log.i("result", "=" + result);
			if (result != null) {
				final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
				if (list.size() > 0) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								PolylineOptions polylineOptions = new PolylineOptions();
								polylineOptions.width(ASSL.Xscale() * 5).color(Color.BLUE).geodesic(true);
								for (int z = 0; z < list.size(); z++) {
									polylineOptions.add(list.get(z));
								}
								map.addPolyline(polylineOptions);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		}
	}





	private static ValueAnimator markerRotationValueAnimator;
	public static void rotateMarker(final Marker marker, float bearing) {
		if (marker != null) {
			if(markerRotationValueAnimator != null){
				markerRotationValueAnimator.cancel();
			}
			markerRotationValueAnimator = new ValueAnimator();
			final float start = getPositiveRotation(marker.getRotation());
			final float end = getPositiveRotation(bearing);
			final float rotationAngle = getRotationAngle(start, end);

			markerRotationValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					try {
						if (marker == null) {
							return;
						}
						float v = animation.getAnimatedFraction();
						float rotation = start + v * rotationAngle;
						marker.setRotation(getRotationUnder360(rotation));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			markerRotationValueAnimator.setFloatValues(0, 1);
			markerRotationValueAnimator.setDuration(600);
			markerRotationValueAnimator.start();
		}
	}


	private static float getPositiveRotation(float rotation){
		if(rotation < 0){
			rotation = -(-180 - (rotation + 180));
		}
		return rotation;
	}

	private static float getRotationUnder360(float rotation){
		if(rotation > 360){
			rotation = rotation - ((int)(rotation/360))*360;
		}
		return rotation;
	}

	private static float getRotationAngle(float start, float end){
		float rotationTo = 0;
		if(start <= end){
			rotationTo = end - start;
		} else {
			rotationTo = 360+end - start;
		}

		if(rotationTo > 180){
			rotationTo = -(180 - (rotationTo - 180));
		}
		return rotationTo;
	}


}
