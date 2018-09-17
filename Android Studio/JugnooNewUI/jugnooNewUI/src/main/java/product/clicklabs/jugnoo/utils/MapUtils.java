package product.clicklabs.jugnoo.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.MyApplication;
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
	
	
	public static GAPIAddress parseGAPIIAddress(String responseStr){
		GAPIAddress fullAddress = new GAPIAddress(new ArrayList<String>(), "Unnamed", "", "", "", "", "", "not_found");
		try {
			JSONObject jsonObj = new JSONObject(responseStr);
			//http://maps.googleapis.com/maps/api/geocode/json?latlng=30.75,76.75

			String status = jsonObj.getString("status");
			if (status.equalsIgnoreCase("OK")) {
				JSONArray Results = jsonObj.getJSONArray("results");
				JSONObject zero = Results.getJSONObject(0);

				String streetNumber = "", route = "", subLocality2 = "", subLocality1 = "", locality = "", administrativeArea2 = "", administrativeArea1 = "", country = "", postalCode = "";
				String subLocality = "", administrativeArea = "";

				if (zero.has("address_components")) {
					try {
						ArrayList<String> selectedAddressComponentsArr = new ArrayList<String>();
						
						JSONArray addressComponents = zero.getJSONArray("address_components");

						for (int i = 0; i < addressComponents.length(); i++) {

							JSONObject iObj = addressComponents.getJSONObject(i);
							JSONArray jArr = iObj.getJSONArray("types");

							ArrayList<String> addressTypes = new ArrayList<String>();
							for (int j = 0; j < jArr.length(); j++) {
								addressTypes.add(jArr.getString(j));
							}

							if ("".equalsIgnoreCase(streetNumber) && addressTypes.contains("street_number")) {
								streetNumber = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(streetNumber) && !selectedAddressComponentsArr.toString().contains(streetNumber)) {
									selectedAddressComponentsArr.add(streetNumber);
								}
							}
							if ("".equalsIgnoreCase(route) && addressTypes.contains("route")) {
								route = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(route) && !selectedAddressComponentsArr.toString().contains(route)) {
									selectedAddressComponentsArr.add(route);
								}
							}
							if ("".equalsIgnoreCase(subLocality2) && addressTypes.contains("sublocality_level_2")) {
								subLocality2 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(subLocality2) && !selectedAddressComponentsArr.toString().contains(subLocality2)) {
									selectedAddressComponentsArr.add(subLocality2);
								}
							}
							if ("".equalsIgnoreCase(subLocality1) && addressTypes.contains("sublocality_level_1")) {
								subLocality1 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(subLocality1) && !selectedAddressComponentsArr.toString().contains(subLocality1)) {
									selectedAddressComponentsArr.add(subLocality1);
								}
							}
							if ("".equalsIgnoreCase(locality) && addressTypes.contains("locality")) {
								locality = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(locality) && !selectedAddressComponentsArr.toString().contains(locality)) {
									selectedAddressComponentsArr.add(locality);
								}
							}
							if ("".equalsIgnoreCase(administrativeArea2) && addressTypes.contains("administrative_area_level_2")) {
								administrativeArea2 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(administrativeArea2) && !selectedAddressComponentsArr.toString().contains(administrativeArea2)) {
									selectedAddressComponentsArr.add(administrativeArea2);
								}
							}
							if ("".equalsIgnoreCase(administrativeArea1) && addressTypes.contains("administrative_area_level_1")) {
								administrativeArea1 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(administrativeArea1) && !selectedAddressComponentsArr.toString().contains(administrativeArea1)) {
									selectedAddressComponentsArr.add(administrativeArea1);
								}
							}
							if ("".equalsIgnoreCase(country) && addressTypes.contains("country")) {
								country = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(country) && !selectedAddressComponentsArr.toString().contains(country)) {
									selectedAddressComponentsArr.add(country);
								}
							}
							if ("".equalsIgnoreCase(postalCode) && addressTypes.contains("postal_code")) {
								postalCode = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(postalCode) && !selectedAddressComponentsArr.toString().contains(postalCode)) {
									selectedAddressComponentsArr.add(postalCode);
								}
							}
						}

						subLocality = subLocality1;
						administrativeArea = administrativeArea2;
						if(!administrativeArea.toLowerCase().contains(administrativeArea1.toLowerCase())) {
							if ("".equalsIgnoreCase(administrativeArea)) {
								administrativeArea = administrativeArea1;
							} else {
								administrativeArea = administrativeArea + ", " + administrativeArea1;
							}
						}



						fullAddress = new GAPIAddress(selectedAddressComponentsArr, zero.getString("formatted_address"),
								streetNumber, subLocality, locality, administrativeArea, country, postalCode);
						if (fullAddress.addressComponents.size() > 0) {
							String lessRedundantformattedAddress = "";
							for (int i = 0; i < fullAddress.addressComponents.size(); i++) {
								if (i < fullAddress.addressComponents.size() - 1) {
									lessRedundantformattedAddress = lessRedundantformattedAddress + fullAddress.addressComponents.get(i) + ", ";
								} else {
									lessRedundantformattedAddress = lessRedundantformattedAddress + fullAddress.addressComponents.get(i);
								}
							}
							fullAddress.formattedAddress = lessRedundantformattedAddress;
						}
					} catch (Exception e) {
						e.printStackTrace();
						fullAddress.formattedAddress = zero.getString("formatted_address");
					}
				}
				else{
					fullAddress.formattedAddress = zero.getString("formatted_address");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if("Unnamed".equalsIgnoreCase(fullAddress.formattedAddress)){
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
			Response response = GoogleRestApis.getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
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
