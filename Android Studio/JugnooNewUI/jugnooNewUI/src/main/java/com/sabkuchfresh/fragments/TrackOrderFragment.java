package com.sabkuchfresh.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import product.clicklabs.jugnoo.ChatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.base.BaseFragment;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.MapsApiSources;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.directions.JungleApisImpl;
import product.clicklabs.jugnoo.retrofit.RestClient;
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

import static product.clicklabs.jugnoo.ChatActivity.KEY_DELIVERY_ID;
import static product.clicklabs.jugnoo.ChatActivity.KEY_ORDER_TYPE;
import static product.clicklabs.jugnoo.Constants.KEY_CHAT_COUNT;
import static product.clicklabs.jugnoo.Constants.KEY_DRIVER_PHONE_NO;

/**
 * Created by shankar on 29/05/17.
 */

public class TrackOrderFragment extends BaseFragment implements GACategory, GAAction {


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
	private int initialHeight, rootHeight;

	private View rootView;
	private Activity activity;
	private boolean expanded;
	private float padding = 50f;
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
		bundle.putString(KEY_DRIVER_PHONE_NO, driverPhoneNo);
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


		rootHeight = -1;

		accessToken = getArguments().getString(Constants.KEY_ACCESS_TOKEN, "");
		orderId = getArguments().getInt(Constants.KEY_ORDER_ID, 0);
		deliveryId = getArguments().getInt(Constants.KEY_DELIVERY_ID, 0);
		pickupLatLng = new LatLng(getArguments().getDouble(Constants.KEY_PICKUP_LATITUDE, 0d),
				getArguments().getDouble(Constants.KEY_PICKUP_LONGITUDE, 0d));
		deliveryLatLng = new LatLng(getArguments().getDouble(Constants.KEY_DELIVERY_LATITUDE, 0d),
				getArguments().getDouble(Constants.KEY_DELIVERY_LONGITUDE, 0d));
		showDeliveryRoute = getArguments().getInt(Constants.KEY_SHOW_DELIVERY_ROUTE, 0);
		driverPhoneNo = getArguments().getString(KEY_DRIVER_PHONE_NO, "");
		initialHeight = getArguments().getInt("initialHeight", ViewGroup.LayoutParams.MATCH_PARENT);
		tiltState = getArguments().getBoolean("tiltState", false);

		rlMapContainer = (RelativeLayout) rootView.findViewById(R.id.rlMapContainer);
		rlMapContainer.post(new Runnable() {
			@Override
			public void run() {
				rootHeight = rlMapContainer.getMeasuredHeight();
				loadMap();

//				setMapPaddingAndMoveCamera();
			}
		});

		tvTrackingInfo = (TextView) rootView.findViewById(R.id.tvTrackingInfo);
		tvTrackingInfo.setVisibility(View.GONE);
		tvETA = (TextView) rootView.findViewById(R.id.tvETA);
		tvETA.setVisibility(View.GONE);
		bMyLocation = (ImageView) rootView.findViewById(R.id.bMyLocation);
		bCallDriver = (LinearLayout) rootView.findViewById(R.id.bCallDriver);
		bCallDriver.setVisibility(View.GONE);

rootView.findViewById(R.id.bChatDriver).setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View view) {
openChatScreen();
	}
});

		bMyLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestLocationPermissionExplicit();
			}
		});

		LocalBroadcastManager.getInstance(activity).registerReceiver(orderUpdateBroadcast, new IntentFilter(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE));

		return rootView;
	}

	public void openChatScreen() {
		Prefs.with(getActivity()).save(KEY_CHAT_COUNT, 0);
//		tvChatCount.setVisibility(View.GONE);
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_ORDER_TYPE, ChatActivity.ORDER_TYPE_DELIVERY);
		bundle.putString(KEY_DRIVER_PHONE_NO, driverPhoneNo);
//		intent.putExtra(KEY_ORDER_TYPE,ChatActivity.ORDER_TYPE_DELIVERY);
		bundle.putString(KEY_DELIVERY_ID, String.valueOf(deliveryId));

		intent.putExtras(bundle);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
		GAUtils.event(RIDES, DRIVER_ENROUTE, CHAT + GAAction.BUTTON + CLICKED);
	}
	@Override
	public void permissionGranted(int requestCode) {
		if(requestCode == REQUEST_CODE_PERMISSION_LOCATION){
			super.permissionGranted(requestCode);
			zoomToDriverAndDrop(null, 0);
		}
	}

	private void loadMap() {
		// if fragment not added completely and activity somehow finishes
		if(!isAdded()){
			return;
		}
		((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				TrackOrderFragment.this.googleMap = googleMap;
				if (googleMap != null) {
					// Customise the styling of the base map using a JSON object defined
					// in a raw resource file.
					boolean success = googleMap.setMapStyle(
							MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style_json));

					if (!success) {
						Log.e(TAG, "Style parsing failed.");
						return;
					}

					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					enableMapMyLocation(googleMap);
					googleMap.getUiSettings().setCompassEnabled(false);

					googleMap.addMarker(getMarkerOptionsForResource(pickupLatLng, R.drawable.restaurant_map_marker, 40f, 40f, 0.5f, 0.5f, 0));
//					googleMap.addMarker(getMarkerOptionsForResource(deliveryLatLng, R.drawable.delivery_map_marker, 71f, 83f, 0.15f, 1.0f, 0));

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
						}

						@Override
						public void onMapReleased() {

						}

						@Override
						public void onMapUnsettled() {

						}

						@Override
						public void moveMap() {

						}

						@Override
						public void onMapSettled() {

						}

						@Override
						public void onCameraPositionChanged(CameraPosition cameraPosition) {
						}
					};

					try {
						initEtaMarker(null,null);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (rootHeight>0) {
						if(expanded){
                            setPaddingZero();
                        }else{
                            setPaddingSome(true);
                        }
					}
//					setMapPaddingAndMoveCamera();


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
	}

	public void setMapPaddingAndMoveCamera(){
		if(googleMap != null && rootHeight > 0) {
			expanded = !tiltState;
			if(expanded){
				padding = 140f;
			}else{
				padding = 50f;
			}
			googleMap.setPadding(0, 0, 0, expanded ? 0 : (rootHeight - initialHeight));

			try {
				if (MapUtils.distance(pickupLatLng, deliveryLatLng) > 10) {
					LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
					llbBuilder.include(pickupLatLng).include(deliveryLatLng);
					googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(llbBuilder), (int) (120f * ASSL.minRatio())));
				} else {
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliveryLatLng, 15));
				}
			} catch (Exception e) {
				e.printStackTrace();
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliveryLatLng, 15));
			}
			zoomToDriverAndDrop(null, 0);
		}
	}

	private void zoomToDriverAndDrop(LatLng latLngDriverAnim, int zoomDuration){
		try {
			if (googleMap != null) {
				LatLngBounds.Builder llbBuilder = getLatLongBuilderForZoomLevel(latLngDriverAnim);

				int duration = zoomDuration > 0 ? zoomDuration : (!zoomedFirstTime?500:500);
				googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(llbBuilder), (int) (padding * ASSL.minRatio())), duration, null);
			} else {
				Utils.showToast(activity, getString(R.string.waiting_for_location));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@NonNull
	private LatLngBounds.Builder getLatLongBuilderForZoomLevel(LatLng latLngDriverAnim) {
		LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
		llbBuilder.include(deliveryLatLng);
		if(markerDriver != null) {
            if(latLngDriverAnim == null) {
                llbBuilder.include(markerDriver.getPosition());
            }
        } else {
            llbBuilder.include(pickupLatLng);
        }

		if(latLngDriverAnim != null) {
            llbBuilder.include(latLngDriverAnim);
        }

		if(polylinePath2 != null) {
            for (LatLng latLng : polylinePath2.getPoints()) {
                llbBuilder.include(latLng);
            }
        }
		return llbBuilder;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		cancelTimer();
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
							activity.finish();
							activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									Intent intent1 = new Intent(Data.LOCAL_BROADCAST);
									intent1.putExtra(Constants.KEY_FLAG, flag);
									LocalBroadcastManager.getInstance(activity).sendBroadcast(intent1);
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
	public void onResume() {
		super.onResume();
		if(googleMap != null){
			scheduleTimer();
			enableMapMyLocation(googleMap);
		}
		getLocationFetcher().connect(this, 10000);
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

							ArrayList<LatLng> latLngsWayPoints = new ArrayList<>();
							if(latLngCurr == null){
								latLngCurr = latLngDriver;
							} else {
								latLngsWayPoints.add(latLngCurr);
							}
							latLngsWayPoints.add(latLngDriver);
							JungleApisImpl.DirectionsResult directionsResult = JungleApisImpl.INSTANCE.getDirectionsWaypointsPathSync(pickupLatLng, deliveryLatLng, latLngsWayPoints, MapsApiSources.CUSTOMER_TRACK_ORDER, false, false);
							final List<LatLng> list = directionsResult.getLatLngs();
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if(list != null && list.size() > 0) {
										try {
											try {
												if (status == EngagementStatus.STARTED.getOrdinal()) {
													latLngsDriverAnim.clear();
													if (markerDriver == null) {
														markerDriver = googleMap.addMarker(getMarkerOptionsForResource(latLngDriver,
																R.drawable.ic_bike_track_order_marker, 66f, 86f, 0.5f, 0.5f, 2));
														markerDriver.setRotation(Prefs.with(activity).getFloat(Constants.SP_TRACKING_LAST_BEARING, (float) bearing));
													}
													if (!zoomedFirstTime) {
														zoomToDriverAndDrop(null, 0);
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
																	if (activity instanceof RideTransactionsActivity) {
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


											int positionNearNew = 0, positionNearCurr = 0;
											double distanceNearNew = Double.MAX_VALUE, distanceNearCurr = Double.MAX_VALUE;
											for (int i = 0; i < list.size(); i++) {
												double distI = MapUtils.distance(latLngDriver, list.get(i));
												if (distI < distanceNearNew) {
													distanceNearNew = distI;
													positionNearNew = i;
												}
												double distC = MapUtils.distance(latLngCurr, list.get(i));
												if (distC < distanceNearCurr) {
													distanceNearCurr = distC;
													positionNearCurr = i;
												}
											}

											//hard coding
//											positionNearCurr = 0; positionNearNew = list.size()-1;

											polylineOptions1.add(pickupLatLng);
											for (int j = 0; j <= positionNearCurr; j++) {
												polylineOptions1.add(list.get(j));
											}

											for (int k = positionNearNew; k < list.size(); k++) {
												polylineOptions2.add(list.get(k));
											}
											polylineOptions2.add(deliveryLatLng);

											// show delivery route fixed to 0 for not showing any path
//											showDeliveryRoute = 0;

											//to animate driver between curr and new points
											if (positionNearNew > positionNearCurr) {
												List<LatLng> latLngsAnimateDriver = new ArrayList<LatLng>();
												double distanceToAnimate = 0;
												LatLng pointer = list.get(positionNearCurr),
														source = list.get(positionNearCurr),
														dest = list.get(positionNearNew);
												for (int l = positionNearCurr; l <= positionNearNew; l++) {
													latLngsAnimateDriver.add(list.get(l));
													distanceToAnimate = distanceToAnimate + MapUtils.distance(pointer, list.get(l));
													pointer = list.get(l);
												}
												boolean fastDuration = false;
												double displacement = MapUtils.distance(source, dest);
												//maximum distance for marker animation on google path should be less than 1.8times displacement between source and destination
												if(displacement < MarkerAnimation.MIN_DISTANCE
														|| displacement > MarkerAnimation.MAX_DISTANCE
														|| distanceToAnimate > displacement * MarkerAnimation.MAX_DISTANCE_FACTOR_GAPI){
													latLngsAnimateDriver.clear();
													latLngsAnimateDriver.add(source);
													latLngsAnimateDriver.add(dest);
													fastDuration = true;
												}


												int pathColor = ContextCompat.getColor(activity, R.color.theme_color);
												int untrackedPathColor = ContextCompat.getColor(activity, R.color.text_color_30alpha);
												float pathWidth = ASSL.Xscale() * 7f;
												if (showDeliveryRoute != 1) {
													pathColor = Color.TRANSPARENT;
													untrackedPathColor = Color.TRANSPARENT;
												}
												for (LatLng latLng : latLngsAnimateDriver) {
													polylineOptions1.add(latLng);
												}

												MarkerAnimation.animateMarkerOnList(markerDriver, latLngsAnimateDriver,
														new LatLngInterpolator.LinearFixed(), false, googleMap,
														pathColor,
														untrackedPathColor,
														pathWidth, callbackAnim, fastDuration, 9000);
												latLngsDriverAnim.clear();
												latLngsDriverAnim.addAll(latLngsAnimateDriver);
											} else {
												MarkerAnimation.clearPolylines();
											}


											if (showDeliveryRoute == 1 && list.size() > 0) {
												polylinePath1 = googleMap.addPolyline(polylineOptions1);
												polylinePath2 = googleMap.addPolyline(polylineOptions2);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										zoomedFirstTime = true;
										latLngCurr = latLngDriver;
									} else {
										MarkerAnimation.clearPolylines();
									}
								}
							});

							try {
								if (TextUtils.isEmpty(eta)) {
									double distance = MapUtils.distance(latLngDriver, deliveryLatLng) * 1.5D;
									double timeInMin = (distance / 1000.0D) * 2;

									setEtaText(String.valueOf((int)(timeInMin)),getString(R.string.min));
								} else {
									setEtaText(eta,getString(R.string.min));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return timerTask;
	}

	private MarkerAnimation.CallbackAnim callbackAnim = new MarkerAnimation.CallbackAnim() {
		@Override
		public void onPathFound(List<LatLng> latLngs) {

		}

		@Override
		public void onTranslate(LatLng latLng, double duration) {

			if (zoomedFirstTime && expanded) {
				zoomToDriverAndDrop(latLng, (int)(0.7d*duration));
			}
		}

		@Override
		public void onAnimComplete() {
		}

		@Override
		public void onAnimNotDone() {
		}
	};

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
						.createMarkerBitmapForResource(activity, resId, width, height)));
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
			getTimer().scheduleAtFixedRate(getTimerTask(), 0, 30 * Constants.SECOND_MILLIS);
			timerStarted = true;
		}
	}


	private Marker etaMarker;
	private Bitmap etaMarkerBitmap;

	private void setEtaText(final String value, final String suffix) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(value!=null){
                        if (etaMarker == null) {
                            initEtaMarker(value,suffix);
                        } else {
                            etaMarker.setIcon(BitmapDescriptorFactory
                                    .fromBitmap(getEtaIconBitmap(value,suffix)));
                        }
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void initEtaMarker(String  value, String suffix) {
		if(googleMap!=null){
            etaMarker = googleMap.addMarker(getStartPickupLocMarkerOptions(deliveryLatLng, value,suffix));

        }
	}


	private LatLngBounds getMapLatLngBounds(LatLngBounds.Builder builder){
		return MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 100);
	}



	public void setPaddingZero(){
		if(googleMap != null) {
			padding = 140f;
			expanded = true;
			googleMap.setPadding(0, 0, 0, 0);
			tiltState = false;
			zoomToDriverAndDrop(null,0);

//            googleMap.animateCamera(CameraUpdateFactory.scrollBy(0.0f,-initialHeight));
//			zoomToDriverAndDrop(null,0);
			/*zoomToDriverAndDrop(null, 0);
			setEtaText(lastEta);*/
		}
	}

	public void setPaddingSome(boolean isMapJustInitialised){
		if(googleMap != null) {
			padding = 50f;
			expanded = false;
			googleMap.setPadding(15, 15, 15, (rootHeight - initialHeight));
			tiltState = true;

			if(isMapJustInitialised){
				googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getMapLatLngBounds(getLatLongBuilderForZoomLevel(null)), (int) (padding * ASSL.minRatio())));

			}else{
				zoomToDriverAndDrop(null,0);

			}

//			googleMap.animateCamera(CameraUpdateFactory.scrollBy(0.0f,initialHeight));
//			zoomToDriverAndDrop(null,0);

			/*zoomToDriverAndDrop(null, 0);
			tvETA.setVisibility(View.GONE);*/
		}
	}
	private final float HOME_MARKER_ZINDEX = 2.0f;
	public MarkerOptions getStartPickupLocMarkerOptions(LatLng latLng, String value,String suffix){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(getString(R.string.pickup_location));
		markerOptions.snippet("");
		markerOptions.position(latLng);
		markerOptions.zIndex(HOME_MARKER_ZINDEX);

		markerOptions.icon(BitmapDescriptorFactory
				.fromBitmap(getEtaIconBitmap(value,suffix)));
		return markerOptions;
	}

	private Bitmap getEtaIconBitmap(String value,String suffix) {
		if(etaMarkerBitmap!=null) {
			etaMarkerBitmap.recycle();
		}

		etaMarkerBitmap = CustomMapMarkerCreator
				.getTextBitmap(getActivity(), value,suffix, getResources().getDimensionPixelSize(R.dimen.text_size_22));

		return etaMarkerBitmap;

	}

	private void enableMapMyLocation(GoogleMap googleMap) {
		if(googleMap != null && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			googleMap.setMyLocationEnabled(true);
		}
	}

}
