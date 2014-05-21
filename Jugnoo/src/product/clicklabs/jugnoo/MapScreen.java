////package product.clicklabs.jugnoo;
////
////import java.io.BufferedReader;
////import java.io.IOException;
////import java.io.InputStream;
////import java.io.InputStreamReader;
////import java.io.UnsupportedEncodingException;
////import java.text.DecimalFormat;
////import java.util.ArrayList;
////import java.util.List;
////
////import org.apache.http.HttpEntity;
////import org.apache.http.HttpResponse;
////import org.apache.http.client.ClientProtocolException;
////import org.apache.http.client.methods.HttpPost;
////import org.apache.http.impl.client.DefaultHttpClient;
////import org.json.JSONArray;
////import org.json.JSONObject;
////
////import rmn.androidscreenlibrary.ASSL;
////import android.annotation.SuppressLint;
////import android.content.Intent;
////import android.graphics.Color;
////import android.location.Location;
////import android.net.Uri;
////import android.os.AsyncTask;
////import android.os.Bundle;
////import android.os.Handler;
////import android.support.v4.app.FragmentActivity;
////import android.util.Log;
////import android.view.View;
////import android.view.ViewGroup.LayoutParams;
////import android.view.WindowManager;
////import android.widget.FrameLayout;
////import android.widget.ImageView;
////import android.widget.LinearLayout;
////import android.widget.ListView;
////import android.widget.ProgressBar;
////import android.widget.TextView;
////
////import com.flurry.android.FlurryAgent;
////import com.google.android.gms.common.ConnectionResult;
////import com.google.android.gms.common.GooglePlayServicesUtil;
////import com.google.android.gms.maps.CameraUpdateFactory;
////import com.google.android.gms.maps.GoogleMap;
////import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
////import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
////import com.google.android.gms.maps.SupportMapFragment;
////import com.google.android.gms.maps.model.LatLng;
////import com.google.android.gms.maps.model.LatLngBounds;
////import com.google.android.gms.maps.model.Marker;
////import com.google.android.gms.maps.model.MarkerOptions;
////import com.google.android.gms.maps.model.Polyline;
////import com.google.android.gms.maps.model.PolylineOptions;
////
/////**
//// * Class for showing map to user to search and pin point to his address so to check whether address is in range of delivery or not.
//// * @author shankar
//// *
//// */
////@SuppressLint("DefaultLocale")
////public class MapScreen extends FragmentActivity {
////
////	ImageView back, getDirections;
////	TextView title;
////	ProgressBar progress;
////
////	FrameLayout frameLayout;
////
////
////
////	GoogleMap map;
////	
////
////	CustomIW infoWindow;
////	
////	BrandLocation nearestLocation;
////	
////	static ArrayList<BrandLocation> offerLocations = new ArrayList<BrandLocation>();
////	
////	Location myLocation;
////	
////	LatLng source, destination;
////	
////	ArrayList<Polyline> polyLinesAL = new ArrayList<Polyline>();
////	
////	CreatePathAsyncTask createPath;
////
////	@Override
////	protected void onCreate(Bundle savedInstanceState) {
////		super.onCreate(savedInstanceState);
////		setContentView(R.layout.map_screen);
////
////
////		frameLayout = (FrameLayout) findViewById(R.id.rv);
////		new ASSL(this, frameLayout, 1184, 720, true);
////
////		SoundEffects.getInstance().init(this);
////		
////		polyLinesAL = new ArrayList<Polyline>();
////
////		back = (ImageView) findViewById(R.id.back);															// views initialized from layout 
////		getDirections = (ImageView) findViewById(R.id.getDirections);
////		
////		title = (TextView) findViewById(R.id.title);
////		title.setTypeface(Data.mediumFont(getApplicationContext()));
////
////
////		progress = (ProgressBar) findViewById(R.id.progress);
////		progress.setVisibility(View.VISIBLE);
////
////
////		map = ((SupportMapFragment) getSupportFragmentManager()
////				.findFragmentById(R.id.map)).getMap();																// map object initialized
////		
////		map.getUiSettings().setZoomControlsEnabled(false);
////		map.setMyLocationEnabled(true);
////		
////		map.setOnMyLocationChangeListener(myLocationChangeListener);
////		
//////		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
//////			
//////			@Override
//////			public boolean onMyLocationButtonClick() {
//////				Toast.makeText(getApplicationContext(), "Hello", 100).show();
//////				return false;
//////			}
//////		});
////		
////		try{
////			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.getChandigarhLatLng(), 8));
////		} catch(Exception e){
////			Log.e("e","="+e.toString());
////		}
////		 
////
////		back.setOnClickListener(new View.OnClickListener() {
////
////			@Override
////			public void onClick(View view) {
////				SoundEffects.getInstance().playSound(SoundEffects.BUTTON_TAP);
////				finish();
////				overridePendingTransition(R.anim.left_in, R.anim.left_out);
////
////			}
////		});
////		
////		
////		getDirections.setOnClickListener(new View.OnClickListener() {
////			
////			@Override
////			public void onClick(View view) {
////				try{
////				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
////					    Uri.parse("http://maps.google.com/maps?saddr="+source.latitude+","+source.longitude
////					    		+"&daddr="+destination.latitude+","+destination.longitude+""));
////					startActivity(intent);
////				} catch(Exception e){
////					e.printStackTrace();
////				}
////				
////			}
////		});
////
////
////		getDirections.setEnabled(false);
////
////
////		getWindow().setSoftInputMode(
////				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);										// hide keyboard
////
////		Handler handler = new Handler();
////		handler.postDelayed(new Runnable() {
////			
////			@Override
////			public void run() {
////				myLocation = map.getMyLocation();
////				
////				map.clear();
////				
////				LatLng userLoc = new LatLng(Data.latitude, Data.longitude);
////				
////				// Move the camera instantly to restaurant position with a zoom of 15.
////				map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 8));
////				// Zoom in, animating the camera.
////				map.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);
////				
////				Log.e("offerLocations.size()","="+offerLocations.size());
////				
////				double distance = 99999999;
////				
////				Log.e("Data.latitude","="+Data.latitude);
////				Log.e("Data.longitude","="+Data.longitude);
////				
////				
////				
////				for(int i=0; i<offerLocations.size(); i++){
////						LatLng restaurantLatLng = new LatLng(offerLocations.get(i).latitude, offerLocations.get(i).longitude);
////						double distanceFrom = distance(userLoc, restaurantLatLng);
////						if(distanceFrom < distance){
////							distance = distanceFrom;
////							nearestLocation = offerLocations.get(i);
////						}
////						
////						MarkerOptions mo1 = new MarkerOptions();
////						mo1.title(offerLocations.get(i).locationId);
////						mo1.snippet(offerLocations.get(i).address);
////						mo1.position(restaurantLatLng);
////						map.addMarker(mo1);
////				}
////				
////				
////				if(nearestLocation != null){
////					infoWindow = new CustomIW(nearestLocation, "");
////					map.setInfoWindowAdapter(infoWindow);
////				
////					if(myLocation == null){
////						source = new LatLng(Data.latitude, Data.longitude);
////					}
////					else{
////						source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
////					}
////					destination = new LatLng(nearestLocation.latitude, nearestLocation.longitude);
////					
////					createPath = new CreatePathAsyncTask(makeURL(source, destination), nearestLocation);
////					createPath.execute();
////				}
////				
////				getDirections.setEnabled(true);
////				
////			}
////		}, 2000);
////		
////		 
////		 
////		 map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
////				
////				@Override
////				public boolean onMarkerClick(Marker arg0) {
////					try{
////					if(myLocation ==  null){
////
////						
////						Log.e("arg0.getId()", "="+arg0.getId());
////						DecimalFormat decimalFormat = new DecimalFormat("#.##");
////						for(int i=0; i<offerLocations.size(); i++) {
////							if(offerLocations.get(i).locationId.toLowerCase().equalsIgnoreCase(""+arg0.getTitle().toLowerCase())){
////								infoWindow = new CustomIW(offerLocations.get(i), 
////										"Distance from your location: "+decimalFormat.format(distance(new LatLng(Data.latitude, Data.longitude), 
////												new LatLng(offerLocations.get(i).latitude, offerLocations.get(i).longitude))) +" kms.");
////								 map.setInfoWindowAdapter(infoWindow);
////								 
////								 source = new LatLng(Data.latitude, Data.longitude);
////								 destination = new LatLng(offerLocations.get(i).latitude, offerLocations.get(i).longitude);
////									
////								 if(!createPath.position.locationId.equalsIgnoreCase(offerLocations.get(i).locationId)){
////									 createPath = new CreatePathAsyncTask(makeURL(source, destination), offerLocations.get(i));
////										createPath.execute();
////								 }
////								 
////								break;
////							}
////						}
////						
////						
////					}
////					else{
////						
////					Log.e("arg0.getId()", "="+arg0.getId());
////					DecimalFormat decimalFormat = new DecimalFormat("#.##");
////					for(int i=0; i<offerLocations.size(); i++) {
////						if(offerLocations.get(i).locationId.toLowerCase().equalsIgnoreCase(""+arg0.getTitle().toLowerCase())){
////							infoWindow = new CustomIW(offerLocations.get(i), 
////									"Distance from your location: "+decimalFormat.format(distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 
////											new LatLng(offerLocations.get(i).latitude, offerLocations.get(i).longitude))) +" kms.");
////							 map.setInfoWindowAdapter(infoWindow);
////							 
////							 source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
////							 destination = new LatLng(offerLocations.get(i).latitude, offerLocations.get(i).longitude);
////								
////							 if(!createPath.position.locationId.equalsIgnoreCase(offerLocations.get(i).locationId)){
////								 createPath = new CreatePathAsyncTask(makeURL(source, destination), offerLocations.get(i));
////									createPath.execute();
////							 }
////							 
////							break;
////						}
////					}
////					
////					} 
////					} catch(Exception e){
////						e.printStackTrace();
////					}
////					
////					return false;
////				}
////			});
////		
////		
////		 
////	}
////
////	@Override
////	protected void onResume() {
////		super.onResume();
////		
////		if(Data.gps == null){
////			int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapScreen.this);
////			if(resp == ConnectionResult.SUCCESS){ 
////				Data.gps = new LocationFetcher(MapScreen.this);
////			}
////			else{																						// google play services not working
////				Log.e("Google Play Service Error ","="+resp);
////				Data.gps = new GPSTracker(MapScreen.this);
////			}
////			Log.e("onresume ","gps new");
////		}
////		
////		if(Data.gps == null){
////			SplashActivity.showSettingsAlert(MapScreen.this);
////		}
////		else{
////        if(Data.gps.isLocationEnabled(MapScreen.this)){
////        	Data.gps.start(MapScreen.this);
////		
////		Handler handler = new Handler();
////		handler.postDelayed(new Runnable() {
////			
////			@Override
////			public void run() {
////				if(Data.gps != null){
////				Data.latitude = Data.gps.getLatitude();
////	        	Data.longitude = Data.gps.getLongitude();
////	        	
////	        	Log.e("location ", "Your Location is - \nLat: " + Data.latitude + "\nLng: " + Data.longitude);
////				}
////				
////			}
////		}, 2000);
////		
////    				
////        	
////        }else{
////        	// can't get location
////        	// GPS or Network is not enabled
////        	// Ask user to enable GPS/network in settings
////        	Data.gps.showSettingsAlert(MapScreen.this);
////        }
////		}
////	}
////	
////
////	OnMyLocationChangeListener myLocationChangeListener = new OnMyLocationChangeListener() {
////		
////		@Override
////		public void onMyLocationChange(Location arg0) {
////			myLocation = arg0;
////		}
////	};
////	
////	
////	/**
////	 * Function to calculate Line Of Sight distance between two locations 
////	 * @param start start location
////	 * @param end end location
////	 * @return double distance in Kilometers
////	 */
////	double distance(LatLng start, LatLng end) {
////		try {
////			Location location1 = new Location("locationA");
////			location1.setLatitude(start.latitude);
////			location1.setLongitude(start.longitude);
////			Location location2 = new Location("locationA");
////			location2.setLatitude(end.latitude);
////			location2.setLongitude(end.longitude);
////
////			double distance = location1.distanceTo(location2);
////			distance = distance / 1000.0;
////			return distance;
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////		return 0;
////
////	}
////
////
////	
////
////	public String makeURL (LatLng source, LatLng destination){
////        StringBuilder urlString = new StringBuilder();
////        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
////        urlString.append("?origin=");// from
////        urlString.append(Double.toString(source.latitude));
////        urlString.append(",");
////        urlString
////                .append(Double.toString(source.longitude));
////        urlString.append("&destination=");// to
////        urlString
////                .append(Double.toString(destination.latitude));
////        urlString.append(",");
////        urlString.append(Double.toString(destination.longitude));
////        urlString.append("&sensor=false&mode=driving&alternatives=true");
////        return urlString.toString();
////	}
////	
////	public void drawPath(String  result) {
////
////	    try {
////	            //Tranform the string into a json object
////	           final JSONObject json = new JSONObject(result);
////	           JSONArray routeArray = json.getJSONArray("routes");
////	           JSONObject routes = routeArray.getJSONObject(0);
////	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
////	           String encodedString = overviewPolylines.getString("points");
////	           List<LatLng> list = decodePoly(encodedString);
////
////	           for(Polyline polyline : polyLinesAL){
////	        	   polyline.remove();
////	           }
////	           
////	           polyLinesAL.clear();
////	           
////	           for(int z = 0; z<list.size()-1;z++){
////	                LatLng src= list.get(z);
////	                LatLng dest= list.get(z+1);
////	                Polyline line = map.addPolyline(new PolylineOptions()
////	                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
////	                .width(5)
////	                .color(Color.RED).geodesic(true));
////
////	                polyLinesAL.add(line);
////	            }
////	           
////
////	    } 
////	    catch (Exception e) {
////	    	e.printStackTrace();
////	    }
////	} 
////	
////	
////	private List<LatLng> decodePoly(String encoded) {
////
////	    List<LatLng> poly = new ArrayList<LatLng>();
////	    int index = 0, len = encoded.length();
////	    int lat = 0, lng = 0;
////
////	    while (index < len) {
////	        int bInt, shift = 0, result = 0;
////	        do {
////	            bInt = encoded.charAt(index++) - 63;
////	            result |= (bInt & 0x1f) << shift;
////	            shift += 5;
////	        } while (bInt >= 0x20);
////	        int dlat = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
////	        lat += dlat;
////
////	        shift = 0;
////	        result = 0;
////	        do {
////	            bInt = encoded.charAt(index++) - 63;
////	            result |= (bInt & 0x1f) << shift;
////	            shift += 5;
////	        } while (bInt >= 0x20);
////	        int dlng = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
////	        lng += dlng;
////
////	        LatLng pLatLng = new LatLng( (((double) lat / 1E5)),
////	                 (((double) lng / 1E5) ));
////	        poly.add(pLatLng);
////	    }
////
////	    return poly;
////	}
////	
////	
////	class CreatePathAsyncTask extends AsyncTask<Void, Void, String>{
////	    String url;
////	    BrandLocation position;
////	    CreatePathAsyncTask(String urlPass, BrandLocation posit){
////	        url = urlPass;
////	        position = posit;
////	    }
////	    @Override
////	    protected void onPreExecute() {
////	    	progress.setVisibility(View.VISIBLE); 
////	        super.onPreExecute();
////	    }
////	    @Override
////	    protected String doInBackground(Void... params) {
////	        JSONParser jParser = new JSONParser();
////	        return jParser.getJSONFromUrl(url);
////	    }
////	    @Override
////	    protected void onPostExecute(String result) {
////	        super.onPostExecute(result);   
////	        progress.setVisibility(View.GONE);       
////	        if(result!=null){
////	            drawPath(result);
////	            
////	            LatLng bound0 = new LatLng(source.latitude-0.01, destination.longitude-0.01);
////	            LatLng bound1 = new LatLng(source.latitude-0.01, source.longitude+0.01);
////	            LatLng bound2 = new LatLng(destination.latitude+0.01, destination.longitude-0.01);
////	            LatLng bound3 = new LatLng(destination.latitude-0.01, source.longitude+0.01);
////	            
////	            LatLngBounds bounds = new LatLngBounds.Builder().include(bound0).include(bound1).include(bound2).include(bound3).build();
////
////	            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50), 2000, null);
//////	            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
////	            
////	        }
////	    }
////	}
////	
////
////
////	/**
////	 * Class for displaying custom info window for marker
////	 * @author shankar
////	 *
////	 */
////	class CustomIW implements InfoWindowAdapter {
////		View mymarkerview;
////		String titleStr;
////		String snippetStr;
////		
////		BrandLocation liTODisplay;
////
////		CustomIW(BrandLocation liTODisplay, String snippet) {
////			mymarkerview = getLayoutInflater().inflate(
////					R.layout.custom_info_window, null);
////			this.titleStr = liTODisplay.address;
////			this.snippetStr = snippet;
////		}
////
////		@Override
////		public View getInfoWindow(Marker marker) {
////
////			render(marker, mymarkerview);
////			return mymarkerview;
////		}
////
////		@Override
////		public View getInfoContents(Marker marker) {
////			return null;
////		}
////
////		void render(Marker marker, View view) {
////
////			LinearLayout relative = (LinearLayout) view
////					.findViewById(R.id.relative);
////
////			TextView title = (TextView) view.findViewById(R.id.title);
////			title.setText("" + titleStr);
////
////			TextView snippet = (TextView) view.findViewById(R.id.snippet);
////			snippet.setText("" + snippetStr);
////
////			relative.setLayoutParams(new ListView.LayoutParams(500,
////					LayoutParams.WRAP_CONTENT));
////			ASSL.DoMagic(relative);
////
////		}
////	}
////
////
////
////	@Override
////	public void onBackPressed() { 
////		SoundEffects.getInstance().playSound(SoundEffects.BUTTON_TAP);
////		finish();
////		overridePendingTransition(R.anim.left_in, R.anim.left_out);
////	}
////
////
////
////	@Override
////	protected void onDestroy() {																	// clearing memory
////		super.onDestroy();
////		ASSL.closeActivity(frameLayout);
////		System.gc();
////	}
////
////	//*****************************Used for flurry work***************//
////	protected void onStart() {
////	    super.onStart();
////	    FlurryAgent.onStartSession(this, Data.flurryKey); 
////	    FlurryAgent.onEvent("Application started");
////	}
////	@Override
////	protected void onStop() {
////	    super.onStop();
////	    FlurryAgent.onEndSession(this);
////	}
////
////	
////}
////
////
////class JSONParser {
////
////    static InputStream inputStream = null;
////    static JSONObject jObj = null;
////    static String json = "";
////    // constructor
////    public JSONParser() {
////    }
////    public String getJSONFromUrl(String url) {
////
////        // Making HTTP request
////        try {
////            // defaultHttpClient
////            DefaultHttpClient httpClient = new DefaultHttpClient();
////            HttpPost httpPost = new HttpPost(url);
////
////            HttpResponse httpResponse = httpClient.execute(httpPost);
////            HttpEntity httpEntity = httpResponse.getEntity();
////            inputStream = httpEntity.getContent();           
////
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        } catch (ClientProtocolException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        try {
////            BufferedReader reader = new BufferedReader(new InputStreamReader(
////            		inputStream, "iso-8859-1"), 8);
////            StringBuilder stringBuilder = new StringBuilder();
////            String line = null;
////            while ((line = reader.readLine()) != null) {
////                stringBuilder.append(line + "\n");
////            }
////
////            json = stringBuilder.toString();
////            inputStream.close();
////        } catch (Exception e) {
////            Log.e("Buffer Error", "Error converting result " + e.toString());
////        }
////        return json;
////
////    }
////}
//
//
//
//package product.clicklabs.jugnoo;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
//import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
//import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//public class MapScreen extends FragmentActivity {
//	static final LatLng elante = new LatLng(30.704892, 76.800918); // >30.704892  76.800918
//	GoogleMap map;
//
//	LinearLayout searchBarLayout, searchListLayout;
//	EditText searchET;
//	Button goBtn;
//	ListView searchList;
//	ProgressBar progress;
//	
//	ArrayList<LocationInfo> locationsAL = new ArrayList<LocationInfo>();
//	
//	BaseAdapterSearch adap;
//	
//	double curLatitude = 0, curLongitude = 0;
//	
//	MarkerOptions markerOptions;
//	LatLng latLng;
//
//	Yourcustominfowindowadpater infoWindow;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.maps_activity);
//
//		searchBarLayout = (LinearLayout) findViewById(R.id.searchBarLayout);
//		searchListLayout = (LinearLayout) findViewById(R.id.searchListLayout);
//
//		searchET = (EditText) findViewById(R.id.searchET);
//		goBtn = (Button) findViewById(R.id.goBtn);
//
//		searchList = (ListView) findViewById(R.id.searchList);
//		
//		progress = (ProgressBar) findViewById(R.id.progress);
//		progress.setVisibility(View.GONE);
//		
//		locationsAL.clear();
//		
//		adap = new BaseAdapterSearch();
//		
//		searchList.setAdapter(adap);
//
//		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//				.getMap();
//		Marker hamburg = map.addMarker(new MarkerOptions().position(elante)
//				.title("elante"));
//		
//		map.setInfoWindowAdapter(infoWindow);
//
////		Marker kiel = map.addMarker(new MarkerOptions()
////				.position(KIEL)
////				.title("Kiel")
////				.snippet("Kiel is cool")
////				.icon(BitmapDescriptorFactory
////						.fromResource(R.drawable.ic_launcher)));
//
//		// Move the camera instantly to hamburg with a zoom of 15.
//		map.moveCamera(CameraUpdateFactory.newLatLngZoom(elante, 15));
//
//		// Zoom in, animating the camera.
//		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//		
//		
//		// Setting a click event handler for the map
//		map.setOnMapClickListener(new OnMapClickListener() {
//			
// 
//            @Override
//            public void onMapClick(LatLng latLng1) {
//            	try {
//            		
//            		latLng = latLng1;
//            		
////                // Creating a marker
////                markerOptions = new MarkerOptions();
//// 
////                // Setting the position for the marker
////                markerOptions.position(latLng);
//// 
////                // Setting the title for the marker.
////                // This will be displayed on taping the marker
////                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
////                
////                markerOptions.snippet(""+distance(elante, latLng));
//// 
////                // Clears the previously touched position
////                map.clear();
//// 
////                // Animating to the touched position
////                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//////                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//////                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//// 
////                // Placing a marker on the touched position
////                map.addMarker(markerOptions);
//                
//                curLatitude = latLng.latitude;
//                curLongitude = latLng.longitude;
//                
//                
//                GetDataAsync gda = new GetDataAsync();
//                gda.execute();
//                
//                } catch (Exception e) {
//					e.printStackTrace();
//				}
//            }
//        });
//		
//		
//		goBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				locationsAL.clear();
//				adap.notifyDataSetChanged();
//				searchListLayout.setVisibility(View.VISIBLE);
//				google_places();
//			}
//		});
//	}
//
//	String distance(LatLng start, LatLng end) {
//		try {
//			Location location1 = new Location("locationA");
//			location1.setLatitude(start.latitude);
//			location1.setLongitude(start.longitude);
//			Location location2 = new Location("locationA");
//			location2.setLatitude(end.latitude);
//			location2.setLongitude(end.longitude);
//
//			double distance = location1.distanceTo(location2);
//			distance = distance/1000.0;
//			return "" + distance;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//
//	}
//	
//	/**
//	 * To search addresses related to particular address available on google
//	 */
//	public void google_places() {
//		
//		final ProgressDialog dialog = ProgressDialog.show(MapScreen.this, "","Loading... ", true);
//		
//		RequestParams params = new RequestParams();
//
//		String ignr2 = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="
//				+ ""
//				+ ","
//				+ ""
//				+ "&radius=50000"
//				+ "&query="
//				+ searchET.getText().toString()
//				+ "&sensor=true&key=AIzaSyAK1SuJqpBKZygmM_4rl-t29Hcm7t4nE38";
//		// "https://maps.googleapis.com/maps/api/place/textsearch/json?location=%f,%f&radius=2bb0000&query=%@&sensor=true&key=%@";
//
//		ignr2 = ignr2.replaceAll(" ", "%20");
//
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.post(ignr2, params, new AsyncHttpResponseHandler() {
//			private AlertDialog alertDialog;
//
//			@Override
//			public void onSuccess(String response) {
//				Log.i("request result", response);
//				try {
//					JSONArray info = null;
//					JSONObject jj = new JSONObject(response);
//					info = jj.getJSONArray("results");
//					Log.v("converted", info + "");
//					for (int a = 0; a < info.length(); a++) {
//						JSONObject first = info.getJSONObject(a);
//						Log.e("first" + a, "" + first);
//					}
//					Log.v("info.len....", "" + info.length());
//					locationsAL.clear();
//					for (int i = 0; i < info.length(); i++) {
//						// printing the values to the logcat
//						try {
//							LocationInfo li = new LocationInfo(info.getJSONObject(i).getString("name"), info.getJSONObject(i).getString("formatted_address"), 
//									info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"), 
//									info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
//							
//							locationsAL.add(li);
//							
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//					for (int i = 0; i < locationsAL.size(); i++) {
//						Log.i("Results name : ....", "" + locationsAL.get(i).name);
//					}
//					adap.notifyDataSetChanged();
//					Toast.makeText(getApplicationContext(), ""+locationsAL.size() + " results found.", Toast.LENGTH_LONG).show();
//					
//				} catch (JSONException e) {
//					e.printStackTrace();
//					Log.e("errorr at response", "" + e.toString());
//				}
//				
//				dialog.dismiss();
//			}
//
//			@Override
//			public void onFailure(Throwable arg0) {
//				try {
//					Log.e("request fail", arg0.getMessage().toString());
//				} catch (Exception e) {
//					Log.e("moving from", e.toString());
//				}
//				dialog.dismiss();
//			}
//		});
//	}
//	
//	
//	class LocationInfo{
//		String name;
//		String address;
//		double latitude;
//		double longitude;
//		
//		public LocationInfo(String name, String address, double latitude, double longitude){
//			this.name = name;
//			this.address = address;
//			this.latitude = latitude;
//			this.longitude = longitude;
//		}
//	}
//	
//	class BaseAdapterSearch extends BaseAdapter {
//		LayoutInflater mInflater;
//		ViewHolderSearch holder;
//
//		public BaseAdapterSearch() {
//			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		}
//
//		@Override
//		public int getCount() {
//			return locationsAL.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//
//			
//			if (convertView == null) {
//				
//				holder = new ViewHolderSearch();
//				convertView = mInflater.inflate(R.layout.list_item, null);
//				
//				holder.name = (TextView) convertView.findViewById(R.id.addressName); 
//				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
//				
//				holder.relative.setTag(holder);
//				
//				holder.relative.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						holder = (ViewHolderSearch) v.getTag();
//						
//						searchListLayout.setVisibility(View.GONE);
//						
//						LocationInfo li = locationsAL.get(holder.id);
//						
//						LatLng ll = new LatLng(li.latitude, li.longitude);
//						
//						Log.e("li.latitude, li.longitude ",">"+li.latitude + "  " + li.longitude);
//						
//						map.clear();
//						map.addMarker(new MarkerOptions().position(ll).title(""+li.name).snippet(""+li.address));
//						
//						map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
//						map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//						
//					}
//				});
//
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolderSearch) convertView.getTag();
//			}
//			
//			
//			holder.id = position;
//			
//			holder.name.setText(""+locationsAL.get(position).name + " " + locationsAL.get(position).address);
//			
//			return convertView;
//		}
//
//	}
//
//	
//	
//	
//	class GetDataAsync extends AsyncTask<String, Integer, String> {
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			progress.setVisibility(View.VISIBLE);
//		}
//
//		@Override
//		protected void onPostExecute(final String result) {
//			super.onPostExecute(result);
//			progress.setVisibility(View.GONE);
//			try {
//                markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
////                markerOptions.title(""+result);
////                markerOptions.snippet("Distance from elante: "+distance(elante, latLng) +" kms.");
//                map.clear();
//                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//                map.addMarker(markerOptions);
//                infoWindow = new Yourcustominfowindowadpater(result, "Distance from elante: "+distance(elante, latLng) +" kms.");
//                map.setInfoWindowAdapter(infoWindow);
//                
//                map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
//
//					@Override
//					public void onInfoWindowClick(Marker arg0) {
//						Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
//					}  
//                	
//                });
//                
//                } catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			
//			String fullAddress = getAddress();
//			Log.e("fullAddress",">"+fullAddress);
//			
//			return fullAddress;
//		}
//	}
//	
//	
//	
//	 JSONObject getJSONfromURL(String url) {
//
//	        // initialize
//	        InputStream is = null;
//	        String result = "";
//	        JSONObject jObject = null;
//
//	        // http post
//	        try {
//	            HttpClient httpclient = new DefaultHttpClient();
//	            HttpPost httppost = new HttpPost(url);
//	            HttpResponse response = httpclient.execute(httppost);
//	            HttpEntity entity = response.getEntity();
//	            is = entity.getContent();
//
//	        } catch (Exception e) {
//	            Log.e("log_tag", "Error in http connection " + e.toString());
//	        }
//
//	        // convert response to string
//	        try {
//	            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//	            StringBuilder sb = new StringBuilder();
//	            String line = null;
//	            while ((line = reader.readLine()) != null) {
//	                sb.append(line + "\n");
//	            }
//	            is.close();
//	            result = sb.toString();
//	        } catch (Exception e) {
//	            Log.e("log_tag", "Error converting result " + e.toString());
//	        }
//
//	        // try parse the string to a JSON object
//	        try {
//	            jObject = new JSONObject(result);
//	        } catch (JSONException e) {
//	            Log.e("log_tag", "Error parsing data " + e.toString());
//	        }
//
//	        return jObject;
//	    }
//	 
//	 String getAddress() {
//	    	String fullAddress = "";
//
//	        try {
//
//	        	
//	        	Log.i("curLatitude ",">"+curLatitude);
//	        	Log.i("curLongitude ",">"+curLongitude);
//	        	
//	            JSONObject jsonObj = getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + curLatitude + ","
//	                    + curLongitude + "&sensor=true");
//	            String Status = jsonObj.getString("status");
//	            if (Status.equalsIgnoreCase("OK")) {
//	                JSONArray Results = jsonObj.getJSONArray("results");
//	                for(int i=0; i<Results.length(); i++){
//	                	JSONObject zero = Results.getJSONObject(i);
//	                	Log.i("zero ==","="+zero.getString("formatted_address"));
//	                }
//	                JSONObject zero = Results.getJSONObject(0);
//	                String fullAddress1 = zero.getString("formatted_address");
//	                fullAddress = fullAddress1;
//
//	                Log.e("Results.length() ==","="+Results.length());
//	            }
//
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//
//	        return fullAddress;
//	    }
//
//	 
//	class Yourcustominfowindowadpater implements InfoWindowAdapter {
//		View mymarkerview;
//		String titleStr;
//		String snippetStr;
//
//		Yourcustominfowindowadpater(String title, String snippet) {
//			mymarkerview = getLayoutInflater().inflate(R.layout.custom_info_window, null);
//			this.titleStr = title;
//			this.snippetStr = snippet;
//		}
//
//		@Override
//		public View getInfoWindow(Marker marker) {
//
//			render(marker, mymarkerview);
//			return mymarkerview;
//		}
//
//		@Override
//		public View getInfoContents(Marker marker) {
//			return null;
//		}
//
//		void render(Marker marker, View view) {
////			Add the code to set the required values for each element in your custominfowindow layout file
//			
//			LinearLayout relative = (LinearLayout) view.findViewById(R.id.relative);
//			
//			TextView title = (TextView) view.findViewById(R.id.title);
//			title.setText(""+titleStr);
//			
//			TextView snippet = (TextView) view.findViewById(R.id.snippet);
//			snippet.setText(""+snippetStr);
//			
//			relative.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Toast.makeText(getApplicationContext(), ""+titleStr, Toast.LENGTH_SHORT).show();
//				}
//			});
//			
//			
//			
//			
//		}
//	}
//	 
//}
//
