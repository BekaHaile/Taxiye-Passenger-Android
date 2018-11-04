package product.clicklabs.jugnoo.apis;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import product.clicklabs.jugnoo.utils.GoogleRestApis;
import product.clicklabs.jugnoo.utils.MapUtils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 5/31/16.
 */
public class ApiGoogleDirectionWaypoints extends AsyncTask<String, Integer, String> {

	private String strOrigin = "", strDestination = "", strWaypoints = "";
	private LatLng latLngInit;
	private Callback callback;

	public ApiGoogleDirectionWaypoints(){

	}

	public ApiGoogleDirectionWaypoints(ArrayList<LatLng> latLngs, boolean sortArray, Callback callback){
		setData(latLngs, sortArray);
		this.callback = callback;
	}

	public ApiGoogleDirectionWaypoints setData(ArrayList<LatLng> latLngs, boolean sortArray){
		latLngInit = latLngs.get(0);
		if(sortArray) {
			Collections.sort(latLngs, new Comparator<LatLng>() {
				@Override
				public int compare(LatLng lhs, LatLng rhs) {
					if (latLngInit != null) {
						double distanceLhs = MapUtils.distance(latLngInit, lhs);
						double distanceRhs = MapUtils.distance(latLngInit, rhs);
						return (int) (distanceLhs - distanceRhs);
					}
					return 0;
				}
			});
		}

		StringBuilder sb = new StringBuilder();
		for(int i=0; i<latLngs.size(); i++){
			if(i == 0){
				strOrigin = latLngs.get(i).latitude+","+latLngs.get(i).longitude;
			} else if(i == latLngs.size()-1){
				strDestination = latLngs.get(i).latitude+","+latLngs.get(i).longitude;
			} else{
				sb.append("via:")
						.append(latLngs.get(i).latitude)
						.append("%2C")
						.append(latLngs.get(i).longitude)
						.append("%7C");
			}
		}
		strWaypoints = sb.toString();
		return this;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(callback != null) {
			callback.onPre();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			Response response = GoogleRestApis.INSTANCE.getDirectionsWaypoints(strOrigin, strDestination, strWaypoints);
			return new String(((TypedByteArray)response.getBody()).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		List<LatLng> list = null;
		if(s != null) {
			list = MapUtils.getLatLngListFromPath(s);
		}
		if(callback != null) {
			callback.onFinish(list);
		}
	}

	public Pair<List<LatLng>, String> syncHit(){
		List<LatLng> list = new ArrayList<>();
		String s = null;
		try {
			Response response = GoogleRestApis.INSTANCE.getDirectionsWaypoints(strOrigin, strDestination, strWaypoints);
			s = new String(((TypedByteArray)response.getBody()).getBytes());
			list = MapUtils.getLatLngListFromPath(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Pair<>(list, s);
	}


	public interface Callback{
		void onPre();
		void onFinish(List<LatLng> list);
	}

	//https://maps.googleapis.com/maps/api/directions/json?origin=30.7178599,76.8091295&destination=30.731916,76.747618&waypoints=via:30.759040%2C76.775368%7Cvia:%2030.720925%2C76.774135

}
