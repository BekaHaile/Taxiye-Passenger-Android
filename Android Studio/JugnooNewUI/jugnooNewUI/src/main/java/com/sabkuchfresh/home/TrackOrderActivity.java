package com.sabkuchfresh.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
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
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.LatLngInterpolator;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.MarkerAnimation;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 26/03/17.
 */

public class TrackOrderActivity extends AppCompatActivity implements GACategory, GAAction {

	private final String TAG = TrackOrderActivity.class.getSimpleName();

	private String accessToken, driverPhoneNo;
	private int orderId, deliveryId, showDeliveryRoute;
	private LatLng pickupLatLng, deliveryLatLng;

	private TextView tvTitle;
	private ImageView ivBack;
	private GoogleMap googleMap;
	private TextView tvTrackingInfo, tvETA;
	private ImageView bMyLocation;
	private ASSL assl;
	private Button bCallDriver;

	private Marker markerDriver;
	private Polyline polylinePath;
	private Location myLocation;
	private final int MAP_ANIMATE_DURATION = 300;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_order);

		assl = new ASSL(this, 1134, 720, false);

		GAUtils.trackScreenView(TRACK_DELIVERY);

		accessToken = getIntent().hasExtra(Constants.KEY_ACCESS_TOKEN) ? getIntent().getStringExtra(Constants.KEY_ACCESS_TOKEN) : "";
		orderId = getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0);
		deliveryId = getIntent().getIntExtra(Constants.KEY_DELIVERY_ID, 0);
		pickupLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, 0d),
				getIntent().getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, 0d));
		deliveryLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_DELIVERY_LATITUDE, 0d),
				getIntent().getDoubleExtra(Constants.KEY_DELIVERY_LONGITUDE, 0d));
		showDeliveryRoute = getIntent().getIntExtra(Constants.KEY_SHOW_DELIVERY_ROUTE, 0);
		driverPhoneNo = getIntent().hasExtra(Constants.KEY_DRIVER_PHONE_NO) ? getIntent().getStringExtra(Constants.KEY_DRIVER_PHONE_NO) : "";


		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvTrackingInfo = (TextView) findViewById(R.id.tvTrackingInfo);
		tvTrackingInfo.setVisibility(View.GONE);
		tvETA = (TextView) findViewById(R.id.tvETA);
		tvETA.setVisibility(View.GONE);
		bMyLocation = (ImageView) findViewById(R.id.bMyLocation);
		bCallDriver = (Button) findViewById(R.id.bCallDriver);

		tvTitle.setText(getString(R.string.order_hash_format, String.valueOf(orderId)));


		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				TrackOrderActivity.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap.setMyLocationEnabled(true);
					googleMap.getUiSettings().setMyLocationButtonEnabled(false);

					LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
					llbBuilder.include(pickupLatLng).include(deliveryLatLng);
					LatLngBounds latLngBounds = llbBuilder.build();

					googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, (int)(120f*ASSL.minRatio())));

					googleMap.addMarker(getMarkerOptionsForResource(pickupLatLng, R.drawable.restaurant_map_marker, 87f, 147f, false, 0));
					googleMap.addMarker(getMarkerOptionsForResource(deliveryLatLng, R.drawable.delivery_map_marker, 87f, 147f, false, 0));

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

		bMyLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				zoomToDriverAndDrop();
			}
		});

		if(!TextUtils.isEmpty(driverPhoneNo)){
			bCallDriver.setVisibility(View.VISIBLE);

			bCallDriver.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.openCallIntent(TrackOrderActivity.this, driverPhoneNo);
				}
			});
		}

		LocalBroadcastManager.getInstance(this).registerReceiver(orderUpdateBroadcast, new IntentFilter(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE));

	}

	private void zoomToDriverAndDrop(){
		try {
			if(googleMap != null && myLocation != null
					&& MapUtils.distance(googleMap.getCameraPosition().target,
					new LatLng(myLocation.getLatitude(), myLocation.getLongitude())) > 10){
				LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
				llbBuilder.include(deliveryLatLng);
				if(markerDriver != null) {
					llbBuilder.include(markerDriver.getPosition());
				}
				if(polylinePath != null) {
					for (LatLng latLng : polylinePath.getPoints()) {
						llbBuilder.include(latLng);
					}
				}

				LatLngBounds latLngBounds = llbBuilder.build();
				googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, (int)(120f*ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
			} else {
				Utils.showToast(TrackOrderActivity.this, getString(R.string.waiting_for_location));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUpdateBroadcast);
	}

	private LocationUpdate locationUpdate = new LocationUpdate() {
		@Override
		public void onLocationChanged(Location location) {
			myLocation = location;
		}
	};

	private BroadcastReceiver orderUpdateBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						final int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
						if(PushFlags.STATUS_CHANGED.getOrdinal() == flag
								|| PushFlags.MENUS_STATUS.getOrdinal() == flag
								|| PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag) {
							finish();
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									Intent intent1 = new Intent(Data.LOCAL_BROADCAST);
									intent1.putExtra(Constants.KEY_FLAG, flag);
									LocalBroadcastManager.getInstance(TrackOrderActivity.this).sendBroadcast(intent1);
								}
							}, 200);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}
	};

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
		MyApplication.getInstance().getLocationFetcher().connect(locationUpdate, 60000L);
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelTimer();
		MyApplication.getInstance().getLocationFetcher().destroy();
	}

	Timer timer;
	private Timer getTimer(){
		timer = new Timer();
		return timer;
	}


	private boolean zoomedFirstTime = false;
	TimerTask timerTask;
	private TimerTask getTimerTask() {
		timerTask = new TimerTask() {
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
							final double bearing = jObj.optDouble(Constants.KEY_BEARING, 0d);
							final String eta = jObj.optString(Constants.KEY_ETA, "");
							final String trackingInfo = jObj.optString(Constants.KEY_TRACKING_INFO, "");

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										if (markerDriver == null) {
											markerDriver = googleMap.addMarker(getMarkerOptionsForResource(new LatLng(latitude, longitude),
													R.drawable.ic_bike_marker, 49f, 62f, true, 2));
											markerDriver.setRotation((float) bearing);
										} else {
											MarkerAnimation.animateMarkerToICS("-1", markerDriver,
													new LatLng(latitude, longitude), new LatLngInterpolator.Spherical());
										}
										if(!zoomedFirstTime) {
											LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
											llbBuilder.include(pickupLatLng).include(deliveryLatLng).include(new LatLng(latitude, longitude));
											LatLngBounds latLngBounds = llbBuilder.build();
											googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, (int)(120f*ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
										}

										if (!TextUtils.isEmpty(trackingInfo)) {
											tvTrackingInfo.setVisibility(View.VISIBLE);
											tvTrackingInfo.setText(trackingInfo);
										} else {
											tvTrackingInfo.setVisibility(View.GONE);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});

							String resultStr = "";
							response = RestClient.getGoogleApiService().getDirections(latitude + "," + longitude,
									deliveryLatLng.latitude + "," + deliveryLatLng.longitude, false, "driving", false);
							resultStr = new String(((TypedByteArray) response.getBody()).getBytes());
							final String result = resultStr;
							final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										if (TextUtils.isEmpty(eta)) {
											JSONObject jObj1 = new JSONObject(result);
											double durationInSec = jObj1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value");
											long mins = (long) (durationInSec / 60d);
											setEtaText(String.valueOf(mins), mins);
										} else {
											long etaLong = 2;
											try {
												etaLong = Long.getLong(eta);
											} catch (Exception e) {
											}
											setEtaText(eta, etaLong);
										}
									} catch (Exception e) {
										e.printStackTrace();
										tvETA.setVisibility(View.GONE);
									}
									try {
										if (polylinePath != null) {
											polylinePath.remove();
										}
										PolylineOptions polylineOptions = new PolylineOptions();
										polylineOptions.width(ASSL.Xscale() * 7f)
												.color(ContextCompat.getColor(TrackOrderActivity.this,
														R.color.google_path_polyline_color)).geodesic(true);
										LatLngBounds.Builder builder = new LatLngBounds.Builder();
										builder.include(deliveryLatLng).include(new LatLng(latitude, longitude));
										for (int z = 0; z < list.size(); z++) {
											polylineOptions.add(list.get(z));
											builder.include(list.get(z));
										}
										if(showDeliveryRoute == 1 && list.size() > 0) {
											polylinePath = googleMap.addPolyline(polylineOptions);
										}
										if (zoomedFirstTime) {
											googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), (int) (120f * ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									zoomedFirstTime = true;
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return timerTask;
	}

	private MarkerOptions getMarkerOptionsForResource(LatLng latLng, int resId, float width, float height, boolean setMidAnchor, int zIndex) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title("");
		markerOptions.snippet("");
		markerOptions.position(latLng);
		markerOptions.zIndex(zIndex);
		if(setMidAnchor) {
			markerOptions.anchor(0.5f, 0.5f);
		}
		markerOptions.icon(BitmapDescriptorFactory
				.fromBitmap(CustomMapMarkerCreator
						.createMarkerBitmapForResource(this, assl, resId, width, height)));
		return markerOptions;
	}

	private void cancelTimer(){
		try {
			timerTask.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			timer.cancel();
			timer.purge();
		} catch (Exception e) {
			e.printStackTrace();
		}
		timerStarted = false;
	}


	private boolean timerStarted = false;
	private void scheduleTimer(){
		if(!timerStarted) {
			getTimer().scheduleAtFixedRate(getTimerTask(), 0, 18 * Constants.SECOND_MILLIS);
			timerStarted = true;
		}
	}


	private void setEtaText(String etaStr, long etaLong) {
		if(etaLong == 0){
			etaLong = 1;
			etaStr = "1";
		}
		tvETA.setText(etaStr + "\n");
		SpannableString spannableString = new SpannableString(etaLong > 1 ? "mins" : "min");
		spannableString.setSpan(new RelativeSizeSpan(0.6f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvETA.append(spannableString);
		tvETA.setVisibility(View.VISIBLE);
	}
}
