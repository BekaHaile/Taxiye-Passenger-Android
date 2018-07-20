package product.clicklabs.jugnoo.utils;

import android.util.Base64;

import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import retrofit.Callback;
import retrofit.client.Response;

public class GoogleRestApis {

	private static String MAPS_CLIENT(){
		return Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_MAPS_API_CLIENT, BuildConfig.MAPS_CLIENT);
	}
	private static String MAPS_BROWSER_KEY(){
		return Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_MAPS_API_BROWSER_KEY, BuildConfig.MAPS_BROWSER_KEY);
	}
	private static boolean MAPS_APIS_SIGN(){
		return Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_MAPS_API_SIGN, BuildConfig.MAPS_APIS_SIGN ? 1 : 0) == 1;
	}
	private static String MAPS_PRIVATE_KEY(){
		return Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_MAPS_API_PRIVATE_KEY, BuildConfig.MAPS_PRIVATE_KEY);
	}
	
	public static Response getDirections(String originLatLng, String destLatLng, Boolean sensor,
										 String mode, Boolean alternatives, String units){
		if(MAPS_APIS_SIGN()) {
			String urlToSign = "/maps/api/directions/json?" +
					"origin=" + originLatLng
					+ "&destination=" + destLatLng
					+ "&sensor=" + sensor
					+ "&mode=" + mode
					+ "&alternatives=" + alternatives
					+ "&units=" + units
					+ "&client=" + MAPS_CLIENT();
			String googleSignature = null;
			try {
				googleSignature = generateGoogleSignature(urlToSign);
			} catch (Exception ignored) {
			}

			return RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
					sensor, mode, alternatives, units, MAPS_CLIENT(), googleSignature);
		} else {
			return RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
					sensor, mode, alternatives, units, MAPS_BROWSER_KEY());
		}
	}

	public static void getDirections(String originLatLng, String destLatLng, Boolean sensor,
										 String mode, Boolean alternatives, String units, Callback<SettleUserDebt> callback){
		if(MAPS_APIS_SIGN()) {
			String urlToSign = "/maps/api/directions/json?" +
					"origin=" + originLatLng
					+ "&destination=" + destLatLng
					+ "&sensor=" + sensor
					+ "&mode=" + mode
					+ "&alternatives=" + alternatives
					+ "&units=" + units
					+ "&client=" + MAPS_CLIENT();
			String googleSignature = null;
			try {
				googleSignature = generateGoogleSignature(urlToSign);
			} catch (Exception ignored) {
			}

			RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
					sensor, mode, alternatives, units, MAPS_CLIENT(), googleSignature, callback);
		} else {
			RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
					sensor, mode, alternatives, units, MAPS_BROWSER_KEY(), callback);
		}
	}

	public static Response getDistanceMatrix(String originLatLng, String destLatLng, String language,
											 Boolean sensor, Boolean alternatives){
		if(MAPS_APIS_SIGN()) {
			String urlToSign = "/maps/api/distancematrix/json?" +
					"origins=" + originLatLng
					+ "&destinations=" + destLatLng
					+ "&language=" + language
					+ "&sensor=" + sensor
					+ "&alternatives=" + alternatives
					+ "&client=" + MAPS_CLIENT();
			String googleSignature = null;
			try {
				googleSignature = generateGoogleSignature(urlToSign);
			} catch (Exception ignored) {
			}

			return RestClient.getGoogleApiService().getDistanceMatrix(originLatLng, destLatLng, language,
					sensor, alternatives, MAPS_CLIENT(), googleSignature);
		} else {
			return RestClient.getGoogleApiService().getDistanceMatrix(originLatLng, destLatLng, language,
					sensor, alternatives, MAPS_BROWSER_KEY());
		}
	}

	public static Response geocode(String latLng, String language){
		if(MAPS_APIS_SIGN()) {
			String urlToSign = "/maps/api/geocode/json?" +
					"latlng=" + latLng
					+ "&language=" + language
					+ "&sensor=" + false
					+ "&client=" + MAPS_CLIENT();
			String googleSignature = null;
			try {
				googleSignature = generateGoogleSignature(urlToSign);
			} catch (Exception ignored) {
			}

			return RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_CLIENT(), googleSignature);
		} else {
			return RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_BROWSER_KEY());
		}
	}
	public static void geocode(String latLng, String language, Callback<GoogleGeocodeResponse> callback){
		if(MAPS_APIS_SIGN()) {
			String urlToSign = "/maps/api/geocode/json?" +
					"latlng=" + latLng
					+ "&language=" + language
					+ "&sensor=" + false
					+ "&client=" + MAPS_CLIENT();
			String googleSignature = null;
			try {
				googleSignature = generateGoogleSignature(urlToSign);
			} catch (Exception ignored) {
			}

			RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_CLIENT(), googleSignature, callback);
		} else {
			RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_BROWSER_KEY(), callback);
		}
	}


	public static Response getDirectionsWaypoints(String strOrigin, String strDestination, String strWaypoints){
		if(MAPS_APIS_SIGN()) {
			String urlToSign = "/maps/api/directions/json?" +
					"origin=" + strOrigin
					+ "&destination=" + strDestination
					+ "&waypoints=" + strWaypoints
					+ "&client=" + MAPS_CLIENT();
			String googleSignature = null;
			try {
				googleSignature = generateGoogleSignature(urlToSign);
			} catch (Exception ignored) {
			}


			return RestClient.getGoogleApiService().getDirectionsWaypoints(strOrigin, strDestination,
					strWaypoints, MAPS_CLIENT(), googleSignature);
		} else {
			return RestClient.getGoogleApiService().getDirectionsWaypoints(strOrigin, strDestination,
					strWaypoints, MAPS_BROWSER_KEY());
		}
	}

	private static String generateGoogleSignature(String urlToSign) throws NoSuchAlgorithmException,
			InvalidKeyException {

		// Convert the key from 'web safe' base 64 to binary
		String keyString = MAPS_PRIVATE_KEY();
		keyString = keyString.replace('-', '+');
		keyString = keyString.replace('_', '/');
		// Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
		byte[] key = Base64.decode(keyString, Base64.DEFAULT);


		SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

		// Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(sha1Key);

		// compute the binary signature for the request
		byte[] sigBytes = mac.doFinal(urlToSign.getBytes());

		// base 64 encode the binary signature
		// Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
		String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);

		// convert the signature to 'web safe' base 64
		signature = signature.replace('+', '-');
		signature = signature.replace('/', '_');

		return signature;
	}


}
