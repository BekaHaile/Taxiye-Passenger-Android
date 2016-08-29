package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sabkuchfresh.bus.AddressSearch;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class AddAddressMapFragment extends Fragment implements LocationUpdate,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "AddAddressMapFragment";
    public static final double MAP_PAN_DISTANCE_CHECK = 50; // in meters
    public static final double MIN_DISTANCE_FOR_REFRESH = 50; // in meters

    public static final float MAX_ZOOM = 15;
    private static final int MAP_ANIMATE_DURATION = 300;

    public static final double FIX_ZOOM_DIAGONAL = 408;

    Button getMyLocation;
    TextView buttonAddLocation;
    String PIN;
    String City;
    String Address1;
    LatLng latlng;
    String mCategoryId;
    boolean zoomedToMyLoc = false;
    MarkerOptions marker;
    Location currentLocation;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private String addressText = "";

    View rootView;
    public FreshActivity homeActivity;

    RelativeLayout relativeLayoutSearchBarText, relative;
    LinearLayout layoutAddLocation;

    private TextView mSelectedLoc, textVeiwSearch;
    private TextView mAddressName;

    private EditText editTextSearch;

    private RelativeLayout searchAddress;

    public GoogleMap googleMap;
    TouchableMapFragment mapFragment;
    public MapStateListener mapStateListener;
    boolean mapTouched = false;
    private LinearLayout linearLayoutSearch;

//    private ImageView imageViewSearchCross;
    private ProgressBar progressWheel, progressBarSearch;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Bus mBus;
    private GoogleApiClient mGoogleApiClient;
//    private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;
    private SearchListAdapter searchListAdapter;
    private NonScrollListView listViewSearch;
    private boolean unsatflag = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_address, container, false);
        homeActivity = (FreshActivity) getActivity();
        homeActivity.fragmentUISetup(this);
        zoomedToMyLoc = false;
        mBus = (homeActivity).getBus();
        relative = (RelativeLayout) rootView.findViewById(R.id.root);
        new ASSL(homeActivity, relative, 1134, 720, false);
        setupUI(rootView.findViewById(R.id.root));

        mSelectedLoc = (TextView) rootView.findViewById(R.id.selected_loc);
        mAddressName = (TextView) rootView.findViewById(R.id.address_name);
        textVeiwSearch = (TextView) rootView.findViewById(R.id.textVeiwSearch);


        mSelectedLoc.setTypeface(Fonts.mavenRegular(homeActivity), Typeface.BOLD);
        mAddressName.setTypeface(Fonts.mavenRegular(homeActivity));
        textVeiwSearch.setTypeface(Fonts.mavenRegular(homeActivity));

        progressWheel = (ProgressBar) rootView.findViewById(R.id.progress_wheel);
        progressBarSearch = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);

        getMyLocation = (Button) rootView.findViewById(R.id.getMyLocation);
        editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);

        layoutAddLocation = (LinearLayout) rootView.findViewById(R.id.layoutAddLocation);
        linearLayoutSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutSearch);
        linearLayoutSearch.setVisibility(View.GONE);
        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextSearch.getText().toString().length() < 1) {
                    linearLayoutSearch.setVisibility(View.GONE);
                    homeActivity.locationSearchShown = false;
                }
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Map Layout
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                moveCamera();
                initializeMap();

            }
        });
        mapFragment = ((TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView));
        mapTouched = false;

        searchAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSearchBarText);

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        searchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutSearch.setVisibility(View.VISIBLE);
                editTextSearch.requestFocus();
                try {
                    InputMethodManager imm = (InputMethodManager) homeActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                homeActivity.locationSearchShown = true;
            }
        });

        layoutAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(unsatflag) {
                    homeActivity.openAddToAddressBook();
                } else {
                    Toast.makeText(homeActivity, "Please wait...", Toast.LENGTH_SHORT).show();
                }

            }
        });


        searchListAdapter = new SearchListAdapter(homeActivity, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient,
                PlaceSearchListFragment.PlaceSearchMode.PICKUP.getOrdinal(),
                new SearchListAdapter.SearchListActionsHandler() {

                    @Override
                    public void onTextChange(String text) {
                        try {
                            if (text.length() > 0) {
//                                imageViewSearchCross.setVisibility(View.VISIBLE);
//                                hideSearchLayout();
                            } else {
//                                imageViewSearchCross.setVisibility(View.GONE);
//                                showSearchLayout();
                            }
//                            searchListActionsHandler.onTextChange(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSearchPre() {
                        progressBarSearch.setVisibility(View.VISIBLE);
//                        searchListActionsHandler.onSearchPre();
                    }

                    @Override
                    public void onSearchPost() {
                        progressBarSearch.setVisibility(View.GONE);
//                        searchListActionsHandler.onSearchPost();
                    }

                    @Override
                    public void onPlaceClick(SearchResult autoCompleteSearchResult) {
//                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                    }

                    @Override
                    public void onPlaceSearchPre() {
                        progressBarSearch.setVisibility(View.VISIBLE);
//                        searchListActionsHandler.onPlaceSearchPre();
                    }

                    @Override
                    public void onPlaceSearchPost(SearchResult searchResult) {
                        progressBarSearch.setVisibility(View.GONE);
//                        searchAddress.setText(searchResult.name);
                        editTextSearch.setText("");
                        linearLayoutSearch.setVisibility(View.GONE);
                        homeActivity.locationSearchShown = false;
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.getLatLng(), MAX_ZOOM), MAP_ANIMATE_DURATION, null);

//                        searchListActionsHandler.onPlaceSearchPost(searchResult);
//                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onPlaceSearchError() {
                        progressBarSearch.setVisibility(View.GONE);
//                        searchListActionsHandler.onPlaceSearchError();
                    }

                    @Override
                    public void onPlaceSaved() {
                    }

                }, true);

        listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
        listViewSearch.setAdapter(searchListAdapter);





        getMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocation != null && googleMap != null) {
//                    FlurryEventLogger.event(FlurryText.checkout_add_pin_loc);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(14).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    //Toast.makeText(homeActivity, Data.WAITING_FOR_LOCATION, Toast.LENGTH_SHORT).show();

//                    if(Utils.checkIfGooglePlayServicesAvailable(homeActivity)){
//                        LocationInit.showLocationAlertDialog(homeActivity);
//                    }

                }
            }
        });

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            homeActivity.fragmentUISetup(this);
        }
    }

    public void closeLayout() {
        linearLayoutSearch.setVisibility(View.GONE);
        homeActivity.locationSearchShown = false;
    }

    public void myLocation() {

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                currentLocation = location;
                getMyLocation.performClick();
            }
        });


    }


    private void moveCamera() {
        CameraPosition cameraPosition;
        try {
            cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(Data.locationFetcher.getLatitude(), Data.locationFetcher.getLongitude())).zoom(14).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeMap() {

        if (googleMap != null) {

            try {

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        currentLocation = location;
                    }
                });
            } catch (Exception e) {
                try {
                    googleMap.getUiSettings().setZoomGesturesEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    googleMap.getUiSettings().setTiltGesturesEnabled(false);
                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            currentLocation = location;
                        }
                    });

                } catch (Exception e1) {
                    e.printStackTrace();

                }
            }

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(homeActivity,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            mapStateListener = new MapStateListener(googleMap, mapFragment, getActivity()) {
                @Override
                public void onMapTouched() {
                    // Map touched
                    mapTouched = true;
                }

                @Override
                public void onMapReleased() {
                    // Map released
                }

                @Override
                public void onMapUnsettled() {
                    // Map unsettled
                    mSelectedLoc.setVisibility(View.GONE);
                    mAddressName.setVisibility(View.GONE);
                    progressWheel.setVisibility(View.VISIBLE);
//                    progressWheel.spin();
                    unsatflag = false;
                }

                @Override
                public void onMapSettled() {
                    // Map settled
//                    getAddress(map.getCameraPosition().target);
                    //unsatflag = true;
                    fillAddressDetails(googleMap.getCameraPosition().target);

                }
            };

        }
    }


    @Override
    public void onDestroyView() {


        super.onDestroyView();
        destroyMap();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        mBus.unregister(this);
    }

    public void destroyMap() {

        try {
            SupportMapFragment suMapFrag = (SupportMapFragment) homeActivity.getSupportFragmentManager().findFragmentById(R.id.mapView);
            if (suMapFrag != null)
                homeActivity.getSupportFragmentManager().beginTransaction().remove(suMapFrag).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void hideSoftKeyboard() {
        try {
            if (homeActivity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(homeActivity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Data.latitude = location.getLatitude();
        Data.longitude = location.getLongitude();
    }


    /**
     * Method used to hide keyboard if outside touched.
     *
     * @param view
     */
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }

            });
        }
        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    private void fillAddressDetails(final LatLng latLng) {


        try {
            if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
//                DialogPopup.showLoadingDialog(getActivity(), "Loading...");
                final Map<String, String> params = new HashMap<String, String>(6);

                params.put(Data.LATLNG, latLng.latitude + "," + latLng.longitude);
                params.put("language", Locale.getDefault().getCountry());
                params.put("sensor", "false");

                RestClient.getGoogleApiServices().getMyAddress(params, new Callback<GoogleGeocodeResponse>() {
                    @Override
                    public void success(GoogleGeocodeResponse geocodeResponse, Response response) {
//                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//                        Logger.i(TAG, "Location Address response = " + responseStr);
                        try {
                            unsatflag = true;
//                            Toast.makeText(getActivity(), "" + geocodeResponse.results.get(0).formatted_address, Toast.LENGTH_SHORT).show();
//                            mAddressName.setText("For\n" + geocodeResponse.results.get(0).getAddAddress());
//                            textVeiwSearch.setText("" + geocodeResponse.results.get(0).getAddAddress());

                            mSelectedLoc.setVisibility(View.VISIBLE);
                            mAddressName.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.GONE);
//                            progressWheel.stopSpinning();

                            latitude = latLng.latitude;
                            longitude = latLng.longitude;
                            addressText = "" + geocodeResponse.results.get(0).getLocality();

                            homeActivity.current_latitude = latLng.latitude;
                            homeActivity.current_longitude = latLng.longitude;

                            homeActivity.current_area = "" + geocodeResponse.results.get(0).getLocality();
                            homeActivity.current_city = "" + geocodeResponse.results.get(0).getCity();
                            homeActivity.current_pincode = "" + geocodeResponse.results.get(0).getPin();
                            mAddressName.setText("For\n" + geocodeResponse.results.get(0).getAddAddress()+", "+homeActivity.current_city);
                            textVeiwSearch.setText("" + geocodeResponse.results.get(0).getAddAddress()+", "+homeActivity.current_city);

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(getActivity(), "", Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmDeletePaytm error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(getActivity(), "", Data.SERVER_ERROR_MSG);
                    }
                });
            } else {

//                new MaterialDialog.Builder(getActivity())
//                        .title(Data.CHECK_INTERNET_TITLE)
//                        .content(Data.CHECK_INTERNET_MSG)
//                        .positiveText("OK")
//                        .positiveColor(getResources().getColor(R.color.colorPrimary))
//                        .negativeColor(getResources().getColor(R.color.colorPrimary))
//                        .callback(new MaterialDialog.ButtonCallback() {
//                            @Override
//                            public void onPositive(MaterialDialog dialog) {
//                                super.onPositive(dialog);
//
//
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Subscribe
    public void onAddressClose(AddressSearch addressSearch) {
        if(addressSearch.selection == 1) {
            editTextSearch.setText("");
            linearLayoutSearch.setVisibility(View.GONE);
            homeActivity.locationSearchShown = false;
        }
    }
}
