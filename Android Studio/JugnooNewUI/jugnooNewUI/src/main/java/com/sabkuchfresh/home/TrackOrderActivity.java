package com.sabkuchfresh.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiGoogleDirectionWaypoints;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.GoogleDirectionWayPointsResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.LatLngInterpolator;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.MarkerAnimation;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
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
	private LinearLayout bCallDriver;

	private Marker markerDriver;
	private Polyline polylinePath1, polylinePath2;
	private final int MAP_ANIMATE_DURATION = 300;
	private boolean mapTouchedOnce, zoomSetManually;
	private float zoomInitial;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_order);

		assl = new ASSL(this, 1134, 720, false);

		GAUtils.trackScreenView(TRACK_DELIVERY);

		mapTouchedOnce = false;
		zoomSetManually = false;
		zoomInitial = -1;

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
		bCallDriver = (LinearLayout) findViewById(R.id.bCallDriver);

		tvTitle.setText(getString(R.string.order_hash_format, String.valueOf(orderId)));


		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				TrackOrderActivity.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap.setMyLocationEnabled(false);

					googleMap.addMarker(getMarkerOptionsForResource(pickupLatLng, R.drawable.restaurant_map_marker, 40f, 40f, 0.5f, 0.5f, 0));
					googleMap.addMarker(getMarkerOptionsForResource(deliveryLatLng, R.drawable.delivery_map_marker, 71f, 83f, 0.15f, 1.0f, 0));

					googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							return true;
						}
					});

					scheduleTimer();

					TouchableMapFragment mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap));
					new MapStateListener(googleMap, mapFragment, TrackOrderActivity.this) {

						@Override
						public void onMapTouched() {
							mapTouchedOnce = true;
						}

						@Override
						public void onMapReleased() {

						}

						@Override
						public void onMapUnsettled() {

						}

						@Override
						public void onMapSettled() {

						}

						@Override
						public void onCameraPositionChanged(CameraPosition cameraPosition) {
							// for detecting if zoom is manually changed by user, a boolean zoomSetManually
							// will be set to stop google directions path zoom
							if(zoomInitial == -1 && cameraPosition != null) zoomInitial = cameraPosition.zoom;
							if(mapTouchedOnce && cameraPosition != null && cameraPosition.zoom != zoomInitial){
								zoomSetManually = true;
							}
						}
					};

					try {
						if(MapUtils.distance(pickupLatLng, deliveryLatLng) > 10) {
							LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
							llbBuilder.include(pickupLatLng).include(deliveryLatLng);
							googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(llbBuilder), (int) (120f * ASSL.minRatio())));
						} else {
							googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLatLng));
						}
					} catch (Exception e) {
						e.printStackTrace();
						googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLatLng));
					}
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
			if (googleMap != null) {
				LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
				llbBuilder.include(deliveryLatLng);
				int points = 0;
				if(markerDriver != null) {
					llbBuilder.include(markerDriver.getPosition());
					points++;
				}

				for(LatLng latLng : latLngsDriverAnim){
					llbBuilder.include(latLng);
					points++;
				}

				if(polylinePath2 != null) {
					for (LatLng latLng : polylinePath2.getPoints()) {
						llbBuilder.include(latLng);
						points++;
					}
				}

				if(points > 0) {
					googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(llbBuilder), (int) (120f * ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
				} else {
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(deliveryLatLng), MAP_ANIMATE_DURATION, null);
				}
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

	private BroadcastReceiver orderUpdateBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						final int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
						final int orderId = intent.getIntExtra(Constants.KEY_ORDER_ID, -1);
						final int closeTracking = intent.getIntExtra(Constants.KEY_CLOSE_TRACKING, 0);
						if(orderId == TrackOrderActivity.this.orderId
								&& closeTracking == 1
								&& (PushFlags.STATUS_CHANGED.getOrdinal() == flag
								|| PushFlags.MENUS_STATUS.getOrdinal() == flag
								|| PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag)) {
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
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelTimer();
	}

	Timer timer;
	private Timer getTimer(){
		timer = new Timer();
		return timer;
	}


	private boolean zoomedFirstTime = false;
	private List<LatLng> latLngsDriverAnim = new ArrayList<>();
	private MarkerAnimation.CallbackAnim callbackAnim = new MarkerAnimation.CallbackAnim() {
		@Override
		public void onPathFound(List<LatLng> latLngs) {
			latLngsDriverAnim.clear();
			latLngsDriverAnim.addAll(latLngs);
		}

		@Override
		public void onAnimComplete() {

		}

		@Override
		public void onAnimNotDone() {

		}
	};

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
							final int status = jObj.optInt(Constants.KEY_STATUS, EngagementStatus.STARTED.getOrdinal());
							final String message = jObj.optString(Constants.KEY_MESSAGE, getString(R.string.some_error_occured_try_again));
							final LatLng latLngDriver = new LatLng(latitude, longitude);

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										if(status == EngagementStatus.STARTED.getOrdinal()) {
											latLngsDriverAnim.clear();
											if (markerDriver == null) {
												markerDriver = googleMap.addMarker(getMarkerOptionsForResource(latLngDriver,
														R.drawable.ic_bike_track_order_marker, 38f, 94f, 0.5f, 0.5f, 2));
												markerDriver.setRotation((float) bearing);
											} else {
												MarkerAnimation.animateMarkerToICS("-1", markerDriver,
														latLngDriver, new LatLngInterpolator.Spherical(),
														callbackAnim, true, googleMap,
														ContextCompat.getColor(TrackOrderActivity.this,
																R.color.theme_color));
											}
											if (!zoomedFirstTime) {
												LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
												llbBuilder.include(pickupLatLng).include(deliveryLatLng).include(latLngDriver);
												googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(llbBuilder), (int) (120f * ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
												handler.postDelayed(new Runnable() {
													@Override
													public void run() {
														if (googleMap != null) {
															zoomInitial = googleMap.getCameraPosition().zoom;
														}
													}
												}, MAP_ANIMATE_DURATION + 50);
											}

											if (!TextUtils.isEmpty(trackingInfo)) {
												tvTrackingInfo.setVisibility(View.VISIBLE);
												tvTrackingInfo.setText(trackingInfo);
											} else {
												tvTrackingInfo.setVisibility(View.GONE);
											}
										} else {
											cancelTimer();
											DialogPopup.alertPopupWithListener(TrackOrderActivity.this, "", message,
													new View.OnClickListener() {
														@Override
														public void onClick(View v) {
															finish();
															overridePendingTransition(R.anim.left_in, R.anim.left_out);
														}
													});
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});

							ArrayList<LatLng> latLngsWayPoints = new ArrayList<>();
							latLngsWayPoints.add(pickupLatLng);
							latLngsWayPoints.add(latLngDriver);
							latLngsWayPoints.add(deliveryLatLng);
							Pair<List<LatLng>, String> pair = apiGoogleDirectionWaypoints.setData(latLngsWayPoints, false).syncHit();
							final List<LatLng> list = pair.first;
							final String result = pair.second;
//							response = RestClient.getGoogleApiService().getDirections(latitude + "," + longitude,
//									deliveryLatLng.latitude + "," + deliveryLatLng.longitude, false, "driving", false);
//							final String result = new String(((TypedByteArray) response.getBody()).getBytes());
//							final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										if (polylinePath1 != null) {
											polylinePath1.remove();
										}
										PolylineOptions polylineOptions1 = new PolylineOptions();
										polylineOptions1.width(ASSL.Xscale() * 7f)
												.color(ContextCompat.getColor(TrackOrderActivity.this,
														R.color.theme_color)).geodesic(true);

										if (polylinePath2 != null) {
											polylinePath2.remove();
										}
										PolylineOptions polylineOptions2 = new PolylineOptions();
										polylineOptions2.width(ASSL.Xscale() * 7f)
												.color(ContextCompat.getColor(TrackOrderActivity.this,
														R.color.text_color_30alpha)).geodesic(true);

										LatLngBounds.Builder builder = new LatLngBounds.Builder();
										builder.include(deliveryLatLng).include(latLngDriver);
										for(LatLng latLng : latLngsDriverAnim){builder.include(latLng);}

										final LatLng latLngDriverLast = markerDriver != null ? markerDriver.getPosition() : latLngDriver;
										int positionCentre = 0;
										double distance = Double.MAX_VALUE;
										for(int i=0; i<list.size(); i++){
											double distI = MapUtils.distance(latLngDriverLast, list.get(i));
											if(distI < distance){
												distance = distI;
												positionCentre = i;
											}
										}
										polylineOptions1.add(pickupLatLng);
										for(int j=0; j<positionCentre; j++){
											polylineOptions1.add(list.get(j));
										}
										polylineOptions1.add(latLngDriverLast);
										polylineOptions2.add(latLngDriverLast);
										for (int k = positionCentre+1; k < list.size(); k++) {
											polylineOptions2.add(list.get(k));
											builder.include(list.get(k));
										}
										polylineOptions2.add(deliveryLatLng);
										if(showDeliveryRoute == 1 && list.size() > 0) {
											polylinePath1 = googleMap.addPolyline(polylineOptions1);
											polylinePath2 = googleMap.addPolyline(polylineOptions2);
										}
										if (zoomedFirstTime && !zoomSetManually) {
											googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(builder), (int) (120f * ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									try {
										if (TextUtils.isEmpty(eta)) {
											GoogleDirectionWayPointsResponse googleDirectionWayPointsResponse = gson.fromJson(result, GoogleDirectionWayPointsResponse.class);
											Log.i("googleDirectionWayPointsResponse","="+googleDirectionWayPointsResponse);

//											JSONObject jObj1 = new JSONObject(result);
//											double durationInSec = jObj1.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value");
//											long mins = (long) (durationInSec / 60d);
											setEtaText(getEtaFromResponse(latLngDriver, googleDirectionWayPointsResponse));
										} else {
											long etaLong = 10;
											try {
												etaLong = Long.getLong(eta);
											} catch (Exception e) {
												e.printStackTrace();
											}
											setEtaText(etaLong);
										}
									} catch (Exception e) {
										e.printStackTrace();
										tvETA.setVisibility(View.GONE);
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

	private MarkerOptions getMarkerOptionsForResource(LatLng latLng, int resId, float width, float height,
													  float xAnchor, float yAnchor, int zIndex) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title("");
		markerOptions.snippet("");
		markerOptions.position(latLng);
		markerOptions.zIndex(zIndex);
		markerOptions.anchor(xAnchor, yAnchor);
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
			getTimer().scheduleAtFixedRate(getTimerTask(), 0, 20 * Constants.SECOND_MILLIS);
			timerStarted = true;
		}
	}


	private void setEtaText(long etaLong) {
		tvETA.setText(String.valueOf(etaLong) + "\n");
		SpannableString spannableString = new SpannableString(etaLong > 1 ? "mins" : "min");
		spannableString.setSpan(new RelativeSizeSpan(0.6f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvETA.append(spannableString);
		tvETA.setVisibility(etaLong > 0 ? View.VISIBLE : View.GONE);
	}

	private Handler handler = new Handler();

	private LatLngBounds getMapLatLngBounds(LatLngBounds.Builder builder){
		return MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 140);
	}


	private ApiGoogleDirectionWaypoints apiGoogleDirectionWaypoints = new ApiGoogleDirectionWaypoints();
	private Gson gson = new Gson();

	private long getEtaFromResponse(LatLng latLng, GoogleDirectionWayPointsResponse pointsResponse){
		long eta = 0L;
		try {
			eta = (long) pointsResponse.getRoutes().get(0).getLegs().get(0).getDuration().getValue();
			double distance = Double.MAX_VALUE;
			for(GoogleDirectionWayPointsResponse.Step step : pointsResponse.getRoutes().get(0).getLegs().get(0).getSteps()){
				double distI = MapUtils.distance(latLng, step.getStartLocation().getLatLng());
				if(distI < distance){
					eta = eta - (long)step.getDuration().getValue();
					distance = distI;
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eta / 60L;
	}

}
