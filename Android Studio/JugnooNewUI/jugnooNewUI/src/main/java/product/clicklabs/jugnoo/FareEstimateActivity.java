package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FareEstimateActivity extends BaseFragmentActivity implements FlurryEventNames,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SearchListAdapter.SearchListActionsHandler, Constants{

    private final String TAG = FareEstimateActivity.class.getSimpleName();

    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    LinearLayout linearLayoutContainer;

    RelativeLayout relativeLayoutFareEstimateDetails;
    GoogleMap mapLite;
    TextView textViewPickupLocation, textViewDropLocation, textViewEstimateTime, textViewEstimateDistance,
			textViewEstimateFare, textViewConvenienceCharge, textViewEstimateFareNote;
    Button buttonOk;

    public ASSL assl;

	private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_estimate);

		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

        relative = (LinearLayout) findViewById(R.id.relative);
        assl = new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


        linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);

        relativeLayoutFareEstimateDetails = (RelativeLayout) findViewById(R.id.relativeLayoutFareEstimateDetails);

        mapLite = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMap();
        if (mapLite != null) {
            mapLite.getUiSettings().setAllGesturesEnabled(false);
            mapLite.getUiSettings().setZoomGesturesEnabled(false);
            mapLite.getUiSettings().setZoomControlsEnabled(false);
            mapLite.setMyLocationEnabled(true);
            mapLite.getUiSettings().setTiltGesturesEnabled(false);
            mapLite.getUiSettings().setMyLocationButtonEnabled(false);
            mapLite.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					return true;
				}
			});

        }

        textViewPickupLocation = (TextView) findViewById(R.id.textViewPickupLocation);
        textViewPickupLocation.setTypeface(Fonts.mavenLight(this));
        textViewDropLocation = (TextView) findViewById(R.id.textViewDropLocation);
        textViewDropLocation.setTypeface(Fonts.mavenLight(this));
        textViewEstimateTime = (TextView) findViewById(R.id.textViewEstimateTime);
        textViewEstimateTime.setTypeface(Fonts.mavenRegular(this));
        textViewEstimateDistance = (TextView) findViewById(R.id.textViewEstimateDistance);
        textViewEstimateDistance.setTypeface(Fonts.mavenRegular(this));
        textViewEstimateFare = (TextView) findViewById(R.id.textViewEstimateFare);
        textViewEstimateFare.setTypeface(Fonts.mavenRegular(this));
		textViewConvenienceCharge = (TextView) findViewById(R.id.textViewConvenienceCharge);
		textViewConvenienceCharge.setTypeface(Fonts.mavenLight(this));
		textViewConvenienceCharge.setText("");
        textViewEstimateFareNote = (TextView) findViewById(R.id.textViewEstimateFareNote);
        textViewEstimateFareNote.setTypeface(Fonts.mavenLight(this));
        buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutFareEstimateDetails.setVisibility(View.GONE);

        ((TextView)findViewById(R.id.textViewStart)).setTypeface(Fonts.mavenRegular(this));
        ((TextView)findViewById(R.id.textViewEnd)).setTypeface(Fonts.mavenRegular(this));
        ((TextView)findViewById(R.id.textViewEstimateDistanceText)).setTypeface(Fonts.mavenLight(this));
        ((TextView)findViewById(R.id.textViewEstimateRideTimeText)).setTypeface(Fonts.mavenLight(this));


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        buttonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
                FlurryEventLogger.event(FARE_RECEIPT_CHECKED);
            }
        });

        PlaceSearchListFragment placeSearchListFragment = new PlaceSearchListFragment(this, mGoogleApiClient);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
        bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
        placeSearchListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.linearLayoutContainer, placeSearchListFragment, PlaceSearchListFragment.class.getSimpleName())
                .commitAllowingStateLoss();

    }

    private void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (sourceLatLng != null && destLatLng != null) {
                                Response response = RestClient.getGoogleApiServices().getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
                                        destLatLng.latitude + "," + destLatLng.longitude, false, "driving", false);
                                String result = new String(((TypedByteArray)response.getBody()).getBytes());
                                Log.i("result", "=" + result);
                                if (result != null) {
                                    JSONObject jObj = new JSONObject(result);
                                    final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
                                    if (jObj.getString("status").equalsIgnoreCase("OK") && list.size() > 0) {

                                        final String startAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address");
                                        final String endAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address");

                                        final String distanceText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                                        final String timeText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

                                        final double distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value");
                                        final double timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value");

                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {

                                                    Fragment frag = getSupportFragmentManager()
                                                            .findFragmentByTag(PlaceSearchListFragment.class.getSimpleName());
                                                    if(frag != null) {
                                                        getSupportFragmentManager().beginTransaction()
                                                                .remove(frag)
                                                                .commit();
                                                    }

                                                    linearLayoutContainer.setVisibility(View.GONE);
													relativeLayoutFareEstimateDetails.setVisibility(View.VISIBLE);


                                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                                    PolylineOptions polylineOptions = new PolylineOptions();
                                                    polylineOptions.width(ASSL.Xscale() * 5).color(Color.BLUE).geodesic(true);
                                                    for (int z = 0; z < list.size(); z++) {
                                                        polylineOptions.add(list.get(z));
                                                        builder.include(list.get(z));
                                                    }

                                                    final LatLngBounds latLngBounds = builder.build();

                                                    mapLite.clear();
                                                    mapLite.addPolyline(polylineOptions);


                                                    MarkerOptions markerOptionsS = new MarkerOptions();
                                                    markerOptionsS.title("Start");
                                                    markerOptionsS.position(sourceLatLng);
                                                    markerOptionsS.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                                        assl, R.drawable.ic_small_pin_start)));
                                                    mapLite.addMarker(markerOptionsS);

                                                    MarkerOptions markerOptionsE = new MarkerOptions();
                                                    markerOptionsE.title("Start");
                                                    markerOptionsE.position(destLatLng);
                                                    markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                                        assl, R.drawable.ic_small_pin_end)));
                                                    mapLite.addMarker(markerOptionsE);


                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                                                mapLite.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, (int)(minRatio*40)));
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, 500);


                                                    textViewPickupLocation.setText(startAddress);
                                                    String startAdd = textViewPickupLocation.getText().toString();
                                                    if (startAdd.charAt(startAdd.length() - 1) == ',') {
                                                        textViewPickupLocation.setText(startAdd.substring(0, startAdd.length() - 1));
                                                    }

                                                    textViewDropLocation.setText(endAddress);
                                                    String endAdd = textViewDropLocation.getText().toString();
                                                    if (endAdd.charAt(endAdd.length() - 1) == ',') {
                                                        textViewDropLocation.setText(endAdd.substring(0, endAdd.length() - 1));
                                                    }

                                                    textViewEstimateTime.setText(timeText);
                                                    textViewEstimateDistance.setText(distanceText);
													textViewEstimateFare.setText("");
													textViewConvenienceCharge.setText("");

													getFareEstimate(FareEstimateActivity.this, sourceLatLng, distanceValue/1000, timeValue/60);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    DialogPopup.dismissLoadingDialog();
                                                }
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                FlurryEventLogger.event(FlurryEventNames.GOOGLE_API_DIRECTIONS_FAILURE);
                                                DialogPopup.alertPopup(FareEstimateActivity.this, "", "Fare could not be estimated between the selected pickup and drop location");
                                                DialogPopup.dismissLoadingDialog();
                                            }
                                        });
                                    }
                                }
								else{
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											DialogPopup.dismissLoadingDialog();
										}
									});
								}
                            }
							else{
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										DialogPopup.dismissLoadingDialog();
									}
								});
							}
                        } catch (Exception e) {
                            e.printStackTrace();
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									DialogPopup.dismissLoadingDialog();
								}
							});
                        }

                    }
                }).start();
            } else {
                DialogPopup.dismissLoadingDialog();
                DialogPopup.alertPopup(FareEstimateActivity.this, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					DialogPopup.dismissLoadingDialog();
				}
			});
        }
    }


	/**
	 * ASync for calculating fare estimate from server
	 */
	public void getFareEstimate(final Activity activity, final LatLng sourceLatLng, final double distanceValue, final double timeValue) {
		if (!HomeActivity.checkIfUserDataNull(activity)) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("start_latitude", "" + sourceLatLng.latitude);
				params.put("start_longitude", "" + sourceLatLng.longitude);
				params.put("ride_distance", "" + distanceValue);
				params.put("ride_time", "" + timeValue);

                RestClient.getApiServices().getFareEstimate(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.e(TAG, "getFareEstimate response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);

                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    String minFare = jObj.getString("min_fare");
                                    String maxFare = jObj.getString("max_fare");
                                    double convenienceCharge = jObj.optDouble("convenience_charge", 0);

                                    textViewEstimateFare.setText(getResources().getString(R.string.rupee) + "" + minFare + " - " +
                                            getResources().getString(R.string.rupee) + "" + maxFare);

                                    if(convenienceCharge > 0){
                                        textViewConvenienceCharge.setText("Convenience Charges "
                                                +getResources().getString(R.string.rupee)+" "+Utils.getMoneyDecimalFormat().format(convenienceCharge));
                                    }
                                    else{
                                        textViewConvenienceCharge.setText("");
                                    }
                                } else {
                                    retryDialog(activity, message, sourceLatLng, distanceValue, timeValue);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(activity, Data.SERVER_ERROR_MSG, sourceLatLng, distanceValue, timeValue);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "getFareEstimate error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(activity, Data.SERVER_NOT_RESOPNDING_MSG, sourceLatLng, distanceValue, timeValue);
                    }
                });

			} else {
				retryDialog(activity, Data.CHECK_INTERNET_MSG, sourceLatLng, distanceValue, timeValue);
                DialogPopup.dismissLoadingDialog();
			}
		} else{
            DialogPopup.dismissLoadingDialog();
        }
	}

	private void retryDialog(final Activity activity, String message, final LatLng sourceLatLng, final double distanceValue, final double timeValue){
		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, "Retry", "Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFareEstimate(activity, sourceLatLng, distanceValue, timeValue);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performBackPressed();
                    }
                }, false, false);
	}


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		mGoogleApiClient.disconnect();
        super.onStop();
	}

	@Override
	public void onConnected(Bundle bundle) {
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

    @Override
    public void onTextChange(String text) {

    }

    @Override
    public void onSearchPre() {

    }

    @Override
    public void onSearchPost() {

    }

    @Override
    public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {

    }

    @Override
    public void onPlaceSearchPre() {
        DialogPopup.showLoadingDialog(FareEstimateActivity.this, "Loading...");
    }

    @Override
    public void onPlaceSearchPost(SearchResult searchResult) {
        getDirectionsAndComputeFare(Data.pickupLatLng, searchResult.latLng);
        FlurryEventLogger.event(FARE_ESTIMATE_CALCULATED);
    }

    @Override
    public void onPlaceSearchError() {
        DialogPopup.dismissLoadingDialog();
    }

    @Override
    public void onPlaceSaved() {
    }
}
