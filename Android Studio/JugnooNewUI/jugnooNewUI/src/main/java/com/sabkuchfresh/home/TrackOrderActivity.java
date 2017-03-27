package com.sabkuchfresh.home;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.MapUtils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 26/03/17.
 */

public class TrackOrderActivity extends AppCompatActivity {

	private final String TAG = TrackOrderActivity.class.getSimpleName();

	private String accessToken;
	private int orderId, deliveryId;
	private LatLng pickupLatLng, deliveryLatLng;

	private TextView tvTitle;
	private ImageView ivBack;
	private GoogleMap googleMap;
	private TextView tvTrackingInfo, tvETA;
	private ASSL assl;

	private Marker markerDriver;
	private Polyline polylinePath;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_order);

		assl = new ASSL(this, 1134, 720, false);

		accessToken = getIntent().hasExtra(Constants.KEY_ACCESS_TOKEN) ? getIntent().getStringExtra(Constants.KEY_ACCESS_TOKEN) : "";
		orderId = getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0);
		deliveryId = getIntent().getIntExtra(Constants.KEY_DELIVERY_ID, 0);
		pickupLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, 0d),
				getIntent().getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, 0d));
		deliveryLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_DELIVERY_LATITUDE, 0d),
				getIntent().getDoubleExtra(Constants.KEY_DELIVERY_LONGITUDE, 0d));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvTrackingInfo = (TextView) findViewById(R.id.tvTrackingInfo);
		tvTrackingInfo.setVisibility(View.GONE);
		tvETA = (TextView) findViewById(R.id.tvETA);
		tvETA.setVisibility(View.GONE);

		tvTitle.setText(getString(R.string.order_id_format, String.valueOf(orderId)));


		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				TrackOrderActivity.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMyLocationEnabled(true);
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
					llbBuilder.include(pickupLatLng).include(deliveryLatLng);
					LatLngBounds latLngBounds = llbBuilder.build();

					googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

					googleMap.addMarker(getMarkerOptionsForResource(pickupLatLng, R.drawable.restaurant_map_marker, 87f, 147f));
					googleMap.addMarker(getMarkerOptionsForResource(deliveryLatLng, R.drawable.delivery_map_marker, 87f, 147f));

					googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							return true;
						}
					});

					scheduleTimer();

				}
			}
		});

		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(googleMap != null){
			scheduleTimer();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelTimer();
	}

	Timer timer = new Timer();
	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			try {
				if (MyApplication.getInstance().isOnline()) {
					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
					params.put(Constants.KEY_DELIVERY_ID, String.valueOf(deliveryId));
					MyApplication.getInstance().getHomeUtil().putDefaultParams(params);
					Response response = RestClient.getApiService().menusLiveTracking(params);
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					JSONObject jObj = new JSONObject(responseStr);
					int flag = jObj.optInt(Constants.KEY_FLAG, 0);
					if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
						final double latitude = jObj.optDouble(Constants.KEY_LATITUDE, 0d);
						final double longitude = jObj.optDouble(Constants.KEY_LONGITUDE, 0d);
						final String eta = jObj.optString(Constants.KEY_ETA, "");
						final String trackingInfo = jObj.optString(Constants.KEY_TRACKING_INFO, "");

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (markerDriver == null) {
									markerDriver = googleMap.addMarker(getMarkerOptionsForResource(new LatLng(latitude, longitude),
											R.drawable.ic_auto_marker, 49f, 62f));
								} else {
									markerDriver.setPosition(new LatLng(latitude, longitude));
								}
								if(!TextUtils.isEmpty(trackingInfo)){
									tvTrackingInfo.setVisibility(View.VISIBLE);
									tvTrackingInfo.setText(trackingInfo);
								} else {
									tvTrackingInfo.setVisibility(View.GONE);
								}
							}
						});

						response = RestClient.getGoogleApiService().getDirections(latitude + "," + longitude,
								deliveryLatLng.latitude + "," + deliveryLatLng.longitude, false, "driving", false);
						final String result = new String(((TypedByteArray) response.getBody()).getBytes());
						final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
						if (list.size() > 0) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										if(TextUtils.isEmpty(eta)) {
											JSONObject jObj1 = new JSONObject(result);
											double durationInSec = jObj1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value");
											long mins = (long) (durationInSec / 60d);
											tvETA.setText(mins + "\n" + (mins > 1 ? "mins" : "min"));
										} else {
											long etaLong = 2;
											try {etaLong = Long.getLong(eta);} catch (Exception e) {}
											tvETA.setText(eta + "\n" + (etaLong > 1 ? "mins" : "min"));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										if (polylinePath != null) {
											polylinePath.remove();
										}
										PolylineOptions polylineOptions = new PolylineOptions();
										polylineOptions.width(ASSL.Xscale() * 7)
												.color(ContextCompat.getColor(TrackOrderActivity.this,
														R.color.google_path_polyline_color)).geodesic(true);
										for (int z = 0; z < list.size(); z++) {
											polylineOptions.add(list.get(z));
										}
										polylinePath = googleMap.addPolyline(polylineOptions);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	private MarkerOptions getMarkerOptionsForResource(LatLng latLng, int resId, float width, float height) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title("");
		markerOptions.snippet("");
		markerOptions.position(latLng);
		markerOptions.icon(BitmapDescriptorFactory
				.fromBitmap(CustomMapMarkerCreator
						.createMarkerBitmapForResource(this, assl, resId, width, height)));
		return markerOptions;
	}

	private void cancelTimer(){
		timerTask.cancel();
		timer.cancel();
		timer.purge();
		timerStarted = false;
	}


	private boolean timerStarted = false;
	private void scheduleTimer(){
		if(!timerStarted) {
			timer.scheduleAtFixedRate(timerTask, 0, 15 * Constants.SECOND_MILLIS);
			timerStarted = true;
		}
	}

}
