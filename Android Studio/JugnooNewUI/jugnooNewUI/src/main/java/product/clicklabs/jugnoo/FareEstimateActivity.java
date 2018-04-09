package product.clicklabs.jugnoo;

import android.content.Intent;
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
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.utils.Utils;


public class FareEstimateActivity extends BaseAppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SearchListAdapter.SearchListActionsHandler, Constants, GAAction, GACategory {

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

    private int isPooled = 0, rideType = RideTypeValue.NORMAL.getOrdinal();
    private LatLng pickupLatLng;
    private SearchResult searchResultGlobal;
    private Region region;
    private PromoCoupon promoCoupon;
    private GoogleApiClient mGoogleApiClient;
    private TextView tvCouponApplied;

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
            Gson gson = new Gson();
            region = gson.fromJson(getIntent().getStringExtra(Constants.KEY_REGION), Region.class);
            rideType = getIntent().getIntExtra(Constants.KEY_RIDE_TYPE, RideTypeValue.NORMAL.getOrdinal());
            if (rideType == RideTypeValue.POOL.getOrdinal()) {
                isPooled = 1;
            }
            if(getIntent().hasExtra(Constants.KEY_COUPON_SELECTED)){
                promoCoupon = (PromoCoupon) getIntent().getSerializableExtra(Constants.KEY_COUPON_SELECTED);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double latitude = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.autoData.getPickupLatLng().latitude);
            double longitude = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.autoData.getPickupLatLng().longitude);
            pickupLatLng = new LatLng(latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                pickupLatLng = Data.autoData.getPickupLatLng();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

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
        tvCouponApplied = (TextView) findViewById(R.id.tv_coupon_applied);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        tvCouponApplied.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        if(promoCoupon!=null && promoCoupon.getId()!=-1 ){
            tvCouponApplied.setVisibility(View.VISIBLE);
            tvCouponApplied.setText(getString(R.string.coupon_applied_format, promoCoupon.getTitle()));
        }else{
            tvCouponApplied.setVisibility(View.GONE);

        }

        linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
        linearLayoutContainer.setVisibility(View.VISIBLE);

        relativeLayoutFareEstimateDetails = (RelativeLayout) findViewById(R.id.relativeLayoutFareEstimateDetails);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapLite = googleMap;
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
            }
        });


        textViewPickupLocation = (TextView) findViewById(R.id.textViewPickupLocation);
        textViewPickupLocation.setTypeface(Fonts.mavenLight(this));
        textViewDropLocation = (TextView) findViewById(R.id.textViewDropLocation);
        textViewDropLocation.setTypeface(Fonts.mavenLight(this));
        textViewEstimateTime = (TextView) findViewById(R.id.textViewEstimateTime);
        textViewEstimateTime.setTypeface(Fonts.mavenMedium(this));
        textViewEstimateDistance = (TextView) findViewById(R.id.textViewEstimateDistance);
        textViewEstimateDistance.setTypeface(Fonts.mavenMedium(this));
        textViewEstimateFare = (TextView) findViewById(R.id.textViewEstimateFare);
        textViewEstimateFare.setTypeface(Fonts.mavenMedium(this));
        textViewConvenienceCharge = (TextView) findViewById(R.id.textViewConvenienceCharge);
        textViewConvenienceCharge.setTypeface(Fonts.mavenLight(this));
        textViewConvenienceCharge.setText("");
        textViewEstimateFareNote = (TextView) findViewById(R.id.textViewEstimateFareNote);
        textViewEstimateFareNote.setTypeface(Fonts.mavenLight(this));
        buttonOk = (Button) findViewById(R.id.buttonOk);
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

        buttonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent intent = new Intent();
                    if (searchResultGlobal != null) {
                        String str = (new Gson()).toJson(searchResultGlobal);
                        intent.putExtra(Constants.KEY_SEARCH_RESULT, str);
                    }
                    setResult(RESULT_OK, intent);
                    GAUtils.event(RIDES, GAAction.FARE_ESTIMATE, GET+RIDE+CLICKED);
                    performBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (rideType != RideTypeValue.POOL.getOrdinal() && Data.autoData.getDropLatLng() != null) {
				getDirectionsAndComputeFare(Data.autoData.getPickupLatLng(), Data.autoData.getDropLatLng());
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

    private void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng) {
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
                                    assl, R.drawable.pin_ball_start)));
                            mapLite.addMarker(markerOptionsS);

                            MarkerOptions markerOptionsE = new MarkerOptions();
                            markerOptionsE.title(getString(R.string.start));
                            markerOptionsE.position(destLatLng);
                            markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                    assl, R.drawable.pin_ball_end)));
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
            }).getDirectionsAndComputeFare(sourceLatLng, destLatLng, isPooled, true, region,promoCoupon);

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
            intent.putExtra(Constants.KEY_AVOID_RIDE_ACTION,true);
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
            Data.autoData.setDropLatLng(searchResult.getLatLng());
            Data.autoData.setDropAddress(searchResult.getAddress());
            Data.autoData.setDropAddressId(searchResult.getId());
            getDirectionsAndComputeFare(Data.autoData.getPickupLatLng(), searchResult.getLatLng());
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
    }
}
