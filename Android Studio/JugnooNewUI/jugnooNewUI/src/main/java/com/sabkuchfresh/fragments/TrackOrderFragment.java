package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import product.clicklabs.jugnoo.RideTransactionsActivity;
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
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 29/05/17.
 */

public class TrackOrderFragment extends Fragment implements GACategory, GAAction {


	private String accessToken, driverPhoneNo;
	private int orderId, deliveryId, showDeliveryRoute;
	private LatLng pickupLatLng, deliveryLatLng;

	private GoogleMap googleMap;
	private TextView tvTrackingInfo, tvETA;
	private ImageView bMyLocation;
	private ASSL assl;
	private LinearLayout bCallDriver;
	private RelativeLayout rlMapContainer;

	private Marker markerDriver;
	private Polyline polylinePath1, polylinePath2;
	private final int MAP_ANIMATE_DURATION = 1000;
	private boolean mapTouchedOnce, zoomSetManually;
	private float zoomInitial;
	private int initialHeight, rootHeight;

	private View rootView;
	private Activity activity;
	private boolean expanded;
	private long lastEta;
	private float padding = 140f;
	private boolean tiltState = false;


	public static TrackOrderFragment newInstance(String accessToken, int orderId, int deliveryId,
												 double pickupLatitude, double pickupLongitude,
												 double deliveryLatitude, double deliveryLongitude,
												 int showDeliveryRoute, String driverPhoneNo, int initialHeight,
												 boolean tiltState){
		TrackOrderFragment fragment = new TrackOrderFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.KEY_ACCESS_TOKEN, accessToken);
		bundle.putInt(Constants.KEY_ORDER_ID, orderId);
		bundle.putInt(Constants.KEY_DELIVERY_ID, deliveryId);
		bundle.putDouble(Constants.KEY_PICKUP_LATITUDE, pickupLatitude);
		bundle.putDouble(Constants.KEY_PICKUP_LONGITUDE, pickupLongitude);
		bundle.putDouble(Constants.KEY_DELIVERY_LATITUDE, deliveryLatitude);
		bundle.putDouble(Constants.KEY_DELIVERY_LONGITUDE, deliveryLongitude);
		bundle.putInt(Constants.KEY_SHOW_DELIVERY_ROUTE, showDeliveryRoute);
		bundle.putString(Constants.KEY_DRIVER_PHONE_NO, driverPhoneNo);
		bundle.putInt("initialHeight", initialHeight);
		bundle.putBoolean("tiltState", tiltState);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_track_order, container, false);

		activity = getActivity();

		GAUtils.trackScreenView(TRACK_DELIVERY);
		
		assl = new ASSL(activity, 1134, 720, false);



		mapTouchedOnce = false;
		zoomSetManually = false;
		zoomInitial = -1;

		accessToken = getArguments().getString(Constants.KEY_ACCESS_TOKEN, "");
		orderId = getArguments().getInt(Constants.KEY_ORDER_ID, 0);
		deliveryId = getArguments().getInt(Constants.KEY_DELIVERY_ID, 0);
		pickupLatLng = new LatLng(getArguments().getDouble(Constants.KEY_PICKUP_LATITUDE, 0d),
				getArguments().getDouble(Constants.KEY_PICKUP_LONGITUDE, 0d));
		deliveryLatLng = new LatLng(getArguments().getDouble(Constants.KEY_DELIVERY_LATITUDE, 0d),
				getArguments().getDouble(Constants.KEY_DELIVERY_LONGITUDE, 0d));
		showDeliveryRoute = getArguments().getInt(Constants.KEY_SHOW_DELIVERY_ROUTE, 0);
		driverPhoneNo = getArguments().getString(Constants.KEY_DRIVER_PHONE_NO, "");
		initialHeight = getArguments().getInt("initialHeight", ViewGroup.LayoutParams.MATCH_PARENT);
		tiltState = getArguments().getBoolean("tiltState", false);

		rlMapContainer = (RelativeLayout) rootView.findViewById(R.id.rlMapContainer);
		rlMapContainer.post(new Runnable() {
			@Override
			public void run() {
				rootHeight = rlMapContainer.getMeasuredHeight();
				setMapPaddingAndMoveCamera();
			}
		});

		tvTrackingInfo = (TextView) rootView.findViewById(R.id.tvTrackingInfo);
		tvTrackingInfo.setVisibility(View.GONE);
		tvETA = (TextView) rootView.findViewById(R.id.tvETA);
		tvETA.setVisibility(View.GONE);
		bMyLocation = (ImageView) rootView.findViewById(R.id.bMyLocation);
		bCallDriver = (LinearLayout) rootView.findViewById(R.id.bCallDriver);
		bCallDriver.setVisibility(View.GONE);

		((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				TrackOrderFragment.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap.setMyLocationEnabled(false);
					googleMap.getUiSettings().setCompassEnabled(false);

					googleMap.addMarker(getMarkerOptionsForResource(pickupLatLng, R.drawable.restaurant_map_marker, 40f, 40f, 0.5f, 0.5f, 0));
					googleMap.addMarker(getMarkerOptionsForResource(deliveryLatLng, R.drawable.delivery_map_marker, 71f, 83f, 0.15f, 1.0f, 0));

					googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							return true;
						}
					});

					scheduleTimer();

					TouchableMapFragment mapFragment = ((TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap));
					new MapStateListener(googleMap, mapFragment, activity) {

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

					setMapPaddingAndMoveCamera();

					if (!TextUtils.isEmpty(driverPhoneNo)) {
						bCallDriver.setVisibility(View.VISIBLE);
						bCallDriver.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Utils.openCallIntent(activity, driverPhoneNo);
							}
						});
					}
				}
			}
		});

		bMyLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				zoomToDriverAndDrop();
			}
		});

		LocalBroadcastManager.getInstance(activity).registerReceiver(orderUpdateBroadcast, new IntentFilter(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE));

		return rootView;
	}

	public void setMapPaddingAndMoveCamera(){
		if(googleMap != null && rootHeight > 0) {
			expanded = false;
			googleMap.setPadding(0, 0, 0, rootHeight - initialHeight);

			try {
				if (MapUtils.distance(pickupLatLng, deliveryLatLng) > 10) {
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
			zoomToDriverAndDrop();
		}
	}

	private void zoomToDriverAndDrop(){
		try {
			if (googleMap != null) {
				LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
				if (!zoomedFirstTime) {
					llbBuilder.include(pickupLatLng);
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (googleMap != null) {
								zoomInitial = googleMap.getCameraPosition().zoom;
							}
						}
					}, MAP_ANIMATE_DURATION + 50);
				}
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
					if(tiltState) {
						LatLngBounds latLngBounds = getMapLatLngBounds(llbBuilder);
						CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(MapLatLngBoundsCreator.move(latLngBounds.getCenter(), -1500, 0))
								.zoom(12)
								.tilt(40)
								.build();
						googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), MAP_ANIMATE_DURATION, null);
					} else {
						googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(llbBuilder), (int) (padding * ASSL.minRatio())), MAP_ANIMATE_DURATION, null);
					}
				} else {
					googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(MapLatLngBoundsCreator
							.createBoundsWithMinDiagonal(llbBuilder, 140), (int) (padding * ASSL.minRatio())),
							MAP_ANIMATE_DURATION, null);
				}
			} else {
				Utils.showToast(activity, getString(R.string.waiting_for_location));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Prefs.with(activity).save(Constants.SP_TRACKING_LAST_BEARING, markerDriver != null ? markerDriver.getRotation() : 0f);
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(orderUpdateBroadcast);
	}


	private BroadcastReceiver orderUpdateBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						final int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
						final int orderId = intent.getIntExtra(Constants.KEY_ORDER_ID, -1);
						final int closeTracking = intent.getIntExtra(Constants.KEY_CLOSE_TRACKING, 0);
						if(orderId == TrackOrderFragment.this.orderId
								&& closeTracking == 1
								&& (PushFlags.STATUS_CHANGED.getOrdinal() == flag
								|| PushFlags.MENUS_STATUS.getOrdinal() == flag
								|| PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag)) {
							if(activity instanceof RideTransactionsActivity) {
								((RideTransactionsActivity) activity).performBackPressed();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										Intent intent1 = new Intent(Data.LOCAL_BROADCAST);
										intent1.putExtra(Constants.KEY_FLAG, flag);
										LocalBroadcastManager.getInstance(activity).sendBroadcast(intent1);
									}
								}, 200);
							} else {
								activity.onBackPressed();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}
	};

	@Override
	public void onResume() {
		super.onResume();
		if(googleMap != null){
			scheduleTimer();
		}
	}

	@Override
	public void onPause() {
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
	LatLng latLngCurr;
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
							final double bearing = jObj.optDouble(Constants.KEY_BEARING, 0);
							final String eta = jObj.optString(Constants.KEY_ETA, "");
							final String trackingInfo = jObj.optString(Constants.KEY_TRACKING_INFO, "");
							final int status = jObj.optInt(Constants.KEY_STATUS, EngagementStatus.STARTED.getOrdinal());
							final String message = jObj.optString(Constants.KEY_MESSAGE, getString(R.string.some_error_occured_try_again));
							final LatLng latLngDriver = new LatLng(latitude, longitude);

							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										if(status == EngagementStatus.STARTED.getOrdinal()) {
											latLngsDriverAnim.clear();
											latLngCurr = markerDriver != null ? markerDriver.getPosition() : latLngDriver;
											if (markerDriver == null) {
												markerDriver = googleMap.addMarker(getMarkerOptionsForResource(latLngDriver,
														R.drawable.ic_bike_track_order_marker, 38f, 94f, 0.5f, 0.5f, 2));
												markerDriver.setRotation(Prefs.with(activity).getFloat(Constants.SP_TRACKING_LAST_BEARING, (float)bearing));
											} else {
												MarkerAnimation.animateMarkerToICS("-1", markerDriver,
														latLngDriver, new LatLngInterpolator.LinearFixed(),
														callbackAnim, true, googleMap, ContextCompat.getColor(activity, R.color.theme_color));
											}
											if (!zoomedFirstTime) {
												zoomToDriverAndDrop();
											}

											if (!TextUtils.isEmpty(trackingInfo)) {
												tvTrackingInfo.setVisibility(View.VISIBLE);
												tvTrackingInfo.setText(trackingInfo);
											} else {
												tvTrackingInfo.setVisibility(View.GONE);
											}
										} else {
											cancelTimer();
											DialogPopup.alertPopupWithListener(activity, "", message,
													new View.OnClickListener() {
														@Override
														public void onClick(View v) {
															if(activity instanceof RideTransactionsActivity) {
																((RideTransactionsActivity) activity).performBackPressed();
															} else {
																activity.onBackPressed();
															}
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
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										if (polylinePath1 != null) {
											polylinePath1.remove();
										}
										PolylineOptions polylineOptions1 = new PolylineOptions();
										polylineOptions1.width(ASSL.Xscale() * 7f)
												.color(ContextCompat.getColor(activity,
														R.color.theme_color)).geodesic(true);

										if (polylinePath2 != null) {
											polylinePath2.remove();
										}
										PolylineOptions polylineOptions2 = new PolylineOptions();
										polylineOptions2.width(ASSL.Xscale() * 7f)
												.color(ContextCompat.getColor(activity,
														R.color.text_color_30alpha)).geodesic(true);

										LatLngBounds.Builder builder = new LatLngBounds.Builder();
										builder.include(deliveryLatLng).include(latLngDriver);
										for(LatLng latLng : latLngsDriverAnim){builder.include(latLng);}

										int positionNearPrev = 0, positionNearCurr = 0;
										double distanceNearPrev = Double.MAX_VALUE, distanceNearCurr = Double.MAX_VALUE;
										for(int i=0; i<list.size(); i++){
											double distI = MapUtils.distance(latLngDriver, list.get(i));
											if(distI < distanceNearPrev){
												distanceNearPrev = distI;
												positionNearPrev = i;
											}
											double distC = MapUtils.distance(latLngCurr, list.get(i));
											if(distC < distanceNearCurr){
												distanceNearCurr = distC;
												positionNearCurr = i;
											}
										}
										polylineOptions1.add(pickupLatLng);
										for(int j=0; j<positionNearCurr; j++){
											polylineOptions1.add(list.get(j));
										}
										polylineOptions1.add(latLngCurr);
										polylineOptions2.add(latLngDriver);
										for (int k = positionNearPrev+1; k < list.size(); k++) {
											polylineOptions2.add(list.get(k));
											builder.include(list.get(k));
										}
										polylineOptions2.add(deliveryLatLng);
										if(showDeliveryRoute == 1 && list.size() > 0) {
											polylinePath1 = googleMap.addPolyline(polylineOptions1);
											polylinePath2 = googleMap.addPolyline(polylineOptions2);
										}
										if (zoomedFirstTime && !zoomSetManually) {
											zoomToDriverAndDrop();
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
						.createMarkerBitmapForResource(activity, assl, resId, width, height)));
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


	private void setEtaText(long etaLong) {
		lastEta = etaLong;
		tvETA.setText(String.valueOf(etaLong) + "\n");
		SpannableString spannableString = new SpannableString(etaLong > 1 ? "mins" : "min");
		spannableString.setSpan(new RelativeSizeSpan(0.6f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvETA.append(spannableString);
		tvETA.setVisibility((expanded && etaLong > 0) ? View.VISIBLE : View.GONE);
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

	public void setPaddingZero(){
		if(googleMap != null) {
			expanded = true;
			googleMap.setPadding(0, 0, 0, 0);
			tiltState = false;
			zoomToDriverAndDrop();
			setEtaText(lastEta);
		}
	}

	public void setPaddingSome(){
		if(googleMap != null) {
			expanded = false;
			googleMap.setPadding(0, 0, 0, rootHeight - initialHeight);
			tiltState = true;
			zoomToDriverAndDrop();
			tvETA.setVisibility(View.GONE);
		}
	}
}
