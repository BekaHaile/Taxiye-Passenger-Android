package product.clicklabs.jugnoo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.apis.ApiFareEstimate;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.Utils;


public class FareEstimateActivity extends BaseAppCompatActivity implements
        SearchListAdapter.SearchListActionsHandler, Constants, GAAction, GACategory {

    RelativeLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    LinearLayout linearLayoutContainer;

    RelativeLayout relativeLayoutFareEstimateDetails;
    GoogleMap mapLite;
    TextView textViewPickupLocation, textViewDropLocation, textViewEstimateTime, textViewEstimateDistance,
            textViewEstimateFare, textViewConvenienceCharge, textViewEstimateFareNote;
    Button buttonOk;

    public ASSL assl;

    private int isPooled = 0, rideType = RideTypeValue.NORMAL.getOrdinal();
    private SearchResult searchResultGlobal;
    private Region region;
    private PromoCoupon promoCoupon;
    private GeoDataClient geoDataClient;
    private LatLng pickupLatLng, dropLatLng;
    private String pickupAddress, dropAddress;
    private boolean isScheduleRide;
    private String finalDateTime;
    private int selectedRegionID;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_estimate);
        try {
            pickupLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, Data.latitude),
                    getIntent().getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, Data.longitude));
            pickupAddress = getIntent().getStringExtra(Constants.KEY_PICKUP_LOCATION_ADDRESS);
            if (getIntent().hasExtra(Constants.KEY_DROP_LATITUDE)) {
                dropLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_DROP_LATITUDE, Data.latitude),
                        getIntent().getDoubleExtra(Constants.KEY_DROP_LONGITUDE, Data.longitude));
                dropAddress = getIntent().getStringExtra(Constants.KEY_DROP_LOCATION_ADDRESS);
            }
            Gson gson = new Gson();
            region = gson.fromJson(getIntent().getStringExtra(Constants.KEY_REGION), Region.class);
            rideType = getIntent().getIntExtra(Constants.KEY_RIDE_TYPE, RideTypeValue.NORMAL.getOrdinal());
            if (rideType == RideTypeValue.POOL.getOrdinal()) {
                isPooled = 1;
            }
            if (getIntent().hasExtra(Constants.KEY_COUPON_SELECTED)) {
                promoCoupon = (PromoCoupon) getIntent().getSerializableExtra(Constants.KEY_COUPON_SELECTED);

            }
            if (getIntent().hasExtra(Constants.KEY_SCHEDULE_RIDE)) {
                isScheduleRide = getIntent().getBooleanExtra(Constants.KEY_SCHEDULE_RIDE, false);

            }
            if (getIntent().hasExtra(Constants.KEY_SCHEDULE_RIDE_FORMATED_DATE_TIME)) {
                finalDateTime = getIntent().getStringExtra(Constants.KEY_SCHEDULE_RIDE_FORMATED_DATE_TIME);

            }
            if (getIntent().hasExtra(Constants.KEY_SCHEDULE_RIDE_SELECTED_REGION_ID)) {
                selectedRegionID = getIntent().getIntExtra(Constants.KEY_SCHEDULE_RIDE_SELECTED_REGION_ID,0);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        geoDataClient = Places.getGeoDataClient(this, null);

        relative = findViewById(R.id.relative);
        assl = new ASSL(this, relative, 1134, 720, false);

        textViewTitle = findViewById(R.id.textViewTitle);
        TextView tvCouponApplied = findViewById(R.id.tv_coupon_applied);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        tvCouponApplied.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = findViewById(R.id.imageViewBack);
        if (promoCoupon != null && promoCoupon.getId() != -1) {
            tvCouponApplied.setVisibility(View.VISIBLE);
            tvCouponApplied.setText(getString(R.string.coupon_applied_format, promoCoupon.getTitle()));
        } else {
            tvCouponApplied.setVisibility(View.GONE);

        }

        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
        linearLayoutContainer.setVisibility(View.VISIBLE);

        relativeLayoutFareEstimateDetails = findViewById(R.id.relativeLayoutFareEstimateDetails);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapLite = googleMap;
                if (mapLite != null) {
                    mapLite.getUiSettings().setAllGesturesEnabled(false);
                    mapLite.getUiSettings().setZoomGesturesEnabled(false);
                    mapLite.getUiSettings().setZoomControlsEnabled(false);
                    if (ActivityCompat.checkSelfPermission(FareEstimateActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mapLite.setMyLocationEnabled(true);
                    }
                    mapLite.getUiSettings().setTiltGesturesEnabled(false);
                    mapLite.getUiSettings().setMyLocationButtonEnabled(false);
                    mapLite.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    if (pickupLatLng != null) {
                        mapLite.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 12));
                    }

                    mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            return true;
                        }
                    });

                }
            }
        });


        textViewPickupLocation = findViewById(R.id.textViewPickupLocation);
        textViewPickupLocation.setTypeface(Fonts.mavenLight(this));
        textViewDropLocation = findViewById(R.id.textViewDropLocation);
        textViewDropLocation.setTypeface(Fonts.mavenLight(this));
        textViewEstimateTime = findViewById(R.id.textViewEstimateTime);
        textViewEstimateTime.setTypeface(Fonts.mavenMedium(this));
        textViewEstimateDistance = findViewById(R.id.textViewEstimateDistance);
        textViewEstimateDistance.setTypeface(Fonts.mavenMedium(this));
        textViewEstimateFare = findViewById(R.id.textViewEstimateFare);
        textViewEstimateFare.setTypeface(Fonts.mavenMedium(this));
        textViewConvenienceCharge = findViewById(R.id.textViewConvenienceCharge);
        textViewConvenienceCharge.setTypeface(Fonts.mavenLight(this));
        textViewConvenienceCharge.setText("");
        textViewEstimateFareNote = findViewById(R.id.textViewEstimateFareNote);
        textViewEstimateFareNote.setTypeface(Fonts.mavenLight(this));
        buttonOk = findViewById(R.id.buttonOk);
        buttonOk.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutFareEstimateDetails.setVisibility(View.GONE);

        ((TextView) findViewById(R.id.textViewStart)).setTypeface(Fonts.mavenMedium(this));
        ((TextView) findViewById(R.id.textViewEnd)).setTypeface(Fonts.mavenMedium(this));
        ((TextView) findViewById(R.id.textViewEstimateDistanceText)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewEstimateRideTimeText)).setTypeface(Fonts.mavenLight(this));

        // Get the TextView width and height in pixels
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (isScheduleRide) {
            buttonOk.setText(getString(R.string.confirm_ride));
        } else {
            buttonOk.setText(getString(R.string.get_ride));
        }
        buttonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isScheduleRide) {
                        scheduleRide(selectedRegionID, finalDateTime);
                    } else {
                        Intent intent = new Intent();
                        if (searchResultGlobal != null) {
                            String str = (new Gson()).toJson(searchResultGlobal);
                            intent.putExtra(Constants.KEY_SEARCH_RESULT, str);
                        }
                        intent.putExtra(Constants.KEY_SCHEDULE_RIDE, false);
                        setResult(RESULT_OK, intent);
                        GAUtils.event(RIDES, GAAction.FARE_ESTIMATE, GET + RIDE + CLICKED);
                        performBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (dropLatLng != null) {
                getDirectionsAndComputeFare(pickupLatLng, pickupAddress, dropLatLng, dropAddress);
            } else {

                Bundle bundle = new Bundle();
                bundle.putString(KEY_SEARCH_FIELD_TEXT, "");
                bundle.putString(KEY_SEARCH_FIELD_HINT, getString(R.string.assigning_state_edit_text_hint));
                bundle.putInt(KEY_SEARCH_MODE, PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal());

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.linearLayoutContainer, PlaceSearchListFragment.newInstance(bundle), PlaceSearchListFragment.class.getSimpleName())
                        .commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getDirectionsAndComputeFare(final LatLng sourceLatLng, final String sourceAddress, final LatLng destLatLng, final String destAddress) {
        try {
            new ApiFareEstimate(this, new ApiFareEstimate.Callback() {
                @Override
                public void onSuccess(List<LatLng> list, String startAddress, String endAddress, String distanceText, String timeText, double distanceValue, double timeValue) {
                    try {

                        Fragment frag = getSupportFragmentManager()
                                .findFragmentByTag(PlaceSearchListFragment.class.getSimpleName());
                        if (frag != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .remove(frag)
                                    .commit();
                        }

                        linearLayoutContainer.setVisibility(View.GONE);
                        relativeLayoutFareEstimateDetails.setVisibility(View.VISIBLE);


                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.width(ASSL.Xscale() * 5).color(getResources().getColor(R.color.google_path_polyline_color)).geodesic(true);
                        for (int z = 0; z < list.size(); z++) {
                            polylineOptions.add(list.get(z));
                            builder.include(list.get(z));
                        }

                        final LatLngBounds latLngBounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 100);

                        if (mapLite != null) {
                            mapLite.clear();
                            mapLite.addPolyline(polylineOptions);


                            MarkerOptions markerOptionsS = new MarkerOptions();
                            markerOptionsS.title(getString(R.string.start));
                            markerOptionsS.position(sourceLatLng);
                            markerOptionsS.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                    R.drawable.pin_ball_start)));
                            mapLite.addMarker(markerOptionsS);

                            MarkerOptions markerOptionsE = new MarkerOptions();
                            markerOptionsE.title(getString(R.string.start));
                            markerOptionsE.position(destLatLng);
                            markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                    R.drawable.pin_ball_end)));
                            mapLite.addMarker(markerOptionsE);


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                        mapLite.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,
                                                (int) (660f * minRatio), (int) (240f * minRatio),
                                                (int) (minRatio * 60)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);
                        }

                        if (!TextUtils.isEmpty(sourceAddress)) {
                            startAddress = sourceAddress;
                        }
                        if (!TextUtils.isEmpty(destAddress)) {
                            endAddress = destAddress;
                        }
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


                    } catch (Exception e) {
                        e.printStackTrace();
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void onFareEstimateSuccess(String currency, String minFare, String maxFare, double convenienceCharge) {

                    textViewEstimateFare.setText(Utils.formatCurrencyValue(currency, minFare) + " - " +
                            Utils.formatCurrencyValue(currency, maxFare));

                    if (convenienceCharge > 0) {
                        textViewConvenienceCharge.setText(getString(R.string.convenience_charge_colon) + " " + Utils.formatCurrencyValue(currency, convenienceCharge));
                    } else {
                        textViewConvenienceCharge.setText("");
                    }
                }

                @Override
                public void onPoolSuccess(String currency, double fare, double rideDistance, String rideDistanceUnit,
                                          double rideTime, String rideTimeUnit, int poolFareId, double convenienceCharge,
                                          String text) {
                    textViewEstimateFare.setText(Utils.formatCurrencyValue(currency, fare));

                    if (convenienceCharge > 0) {
                        textViewConvenienceCharge.setText(getString(R.string.convenience_charge_colon) + " " + Utils.formatCurrencyValue(currency, convenienceCharge));
                    } else {
                        textViewConvenienceCharge.setText("");
                    }
                }

                @Override
                public void onNoRetry() {
                    onBackPressed();
                }

                @Override
                public void onRetry() {
                }

                @Override
                public void onFareEstimateFailure() {

                }

                @Override
                public void onDirectionsFailure() {

                }
            }).getDirectionsAndComputeFare(sourceLatLng, destLatLng, isPooled, true, region, promoCoupon);

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


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        // pass the selected search string back along with a flag to avoid perform the get a ride action
        if (searchResultGlobal != null) {
            Intent intent = new Intent();
            String str = (new Gson()).toJson(searchResultGlobal);
            intent.putExtra(Constants.KEY_SEARCH_RESULT, str);
            intent.putExtra(Constants.KEY_AVOID_RIDE_ACTION, true);
            setResult(RESULT_OK, intent);
        }
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
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
    public void onPlaceClick(SearchResult autoCompleteSearchResult) {

    }

    @Override
    public void onPlaceSearchPre() {
        DialogPopup.showLoadingDialog(FareEstimateActivity.this, getString(R.string.loading));
    }

    @Override
    public void onPlaceSearchPost(SearchResult searchResult) {
        try {
            if (Data.autoData != null) {
                Data.autoData.setDropLatLng(searchResult.getLatLng());
                Data.autoData.setDropAddress(searchResult.getAddress());
                Data.autoData.setDropAddressId(searchResult.getId());
            }
            getDirectionsAndComputeFare(pickupLatLng, pickupAddress, searchResult.getLatLng(), searchResult.getAddress());
            searchResultGlobal = searchResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaceSearchError() {
        DialogPopup.dismissLoadingDialog();
    }

    @Override
    public void onPlaceSaved() {
    }

    @Override
    public void onNotifyDataSetChanged(int count) {

    }

    public GeoDataClient getGeoDataClient(){
        return geoDataClient;
    }

    private void scheduleRide(int regionId, String finalDateTime) {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_REGION_ID, regionId + "");
        params.put(Constants.KEY_PICKUP_TIME, DateOperations.localToUTC(finalDateTime));
        params.put(Constants.KEY_LATITUDE, String.valueOf(pickupLatLng.latitude));
        params.put(Constants.KEY_LONGITUDE, String.valueOf(pickupLatLng.longitude));
        params.put(Constants.KEY_PICKUP_LOCATION_ADDRESS,pickupAddress);
        params.put(Constants.KEY_DROP_LOCATION_ADDRESS, dropAddress);
        params.put("op_drop_latitude", String.valueOf(dropLatLng.latitude));
        params.put("op_drop_longitude", String.valueOf(dropLatLng.longitude));
        params.put("vehicle_type", String.valueOf(region.getVehicleType()));
        params.put(Constants.KEY_PREFERRED_PAYMENT_MODE, "" + Data.autoData.getPickupPaymentOption());


        new ApiCommon<>(this).showLoader(true).execute(params, ApiName.SCHEDULE_RIDE,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {

                        DialogPopup.alertPopupWithListener(FareEstimateActivity.this,"",
                                getString(R.string.booking_scheduled_successfully), new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent();
                                        if (searchResultGlobal != null) {
                                            String str = (new Gson()).toJson(searchResultGlobal);
                                            intent.putExtra(Constants.KEY_SEARCH_RESULT, str);
                                        }
                                        intent.putExtra(Constants.KEY_SCHEDULE_RIDE, true);
                                        setResult(RESULT_OK, intent);
                                        GAUtils.event(RIDES, GAAction.FARE_ESTIMATE, GET + RIDE + CLICKED);
                                        performBackPressed();
                                    }
                                });





                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });


    }

}
