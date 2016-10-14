package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.bus.AddressSearch;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.MyApplication;
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
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class AddAddressMapFragment extends Fragment implements LocationUpdate,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "AddAddressMapFragment";

    public static final float MAX_ZOOM = 15;
    private static final int MAP_ANIMATE_DURATION = 300;
    private final double MAP_PAN_DISTANCE = 100;

    Button getMyLocation, buttonOk;
    boolean zoomedToMyLoc = false;
    Location currentLocation;

    View rootView;
    public FragmentActivity activity;

    RelativeLayout relative;
    LinearLayout layoutAddLocation;
    private ImageView centerPivot, locationPointer;
    private TextView mSelectedLoc, textVeiwSearch;
    private TextView mAddressName;

    private EditText editTextSearch;

    private RelativeLayout searchAddress;

    public GoogleMap googleMap;
    TouchableMapFragment mapFragment;
    public MapStateListener mapStateListener;
    boolean mapTouched = false;
    private ScrollView scrollViewSearch;
    private LinearLayout linearLayoutSearch;

    private ProgressBar progressWheel, progressBarSearch;
    private Bus mBus;
    private GoogleApiClient mGoogleApiClient;
    private SearchListAdapter searchListAdapter;
    private NonScrollListView listViewSearch;
    private boolean unsatflag = false, locationUpdate = false;

    //Location Error layout
    RelativeLayout relativeLayoutLocationError;
    RelativeLayout relativeLayoutLocationErrorSearchBar;

    public double current_latitude = 0.0;
    public double current_longitude = 0.0;
    public String current_street = "";
    public String current_route = "";
    public String current_area = "";
    public String current_city = "";
    public String current_pincode = "";
    public String placeId = "";
    private boolean editThisAddress = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_address, container, false);
        activity = getActivity();

        if(activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);
            editThisAddress = ((FreshActivity) activity).isEditThisAddress();
        } else if(activity instanceof AddPlaceActivity) {
            AddPlaceActivity addPlaceActivity = (AddPlaceActivity) activity;
            addPlaceActivity.getTextViewTitle().setVisibility(View.VISIBLE);
            addPlaceActivity.getTextViewTitle().setText(R.string.choose_your_address);
            addPlaceActivity.getRelativeLayoutSearch().setVisibility(View.GONE);
            editThisAddress = addPlaceActivity.isEditThisAddress();
        }
        zoomedToMyLoc = false;
        mBus = MyApplication.getInstance().getBus();
        relative = (RelativeLayout) rootView.findViewById(R.id.root);
        new ASSL(activity, relative, 1134, 720, false);
        hideKeyboardOnOutsideTouch(rootView.findViewById(R.id.root));

        fetchAddressBundle();

        mSelectedLoc = (TextView) rootView.findViewById(R.id.selected_loc);
        mAddressName = (TextView) rootView.findViewById(R.id.address_name);
        textVeiwSearch = (TextView) rootView.findViewById(R.id.textVeiwSearch);


        mSelectedLoc.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        mAddressName.setTypeface(Fonts.mavenRegular(activity));
        textVeiwSearch.setTypeface(Fonts.mavenRegular(activity));

        progressWheel = (ProgressBar) rootView.findViewById(R.id.progress_wheel);
        progressBarSearch = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);

        getMyLocation = (Button) rootView.findViewById(R.id.getMyLocation);
        editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);

        layoutAddLocation = (LinearLayout) rootView.findViewById(R.id.layoutAddLocation);
        scrollViewSearch = (ScrollView) rootView.findViewById(R.id.scrollViewSearch);
        linearLayoutSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutSearch);
        scrollViewSearch.setVisibility(View.GONE);
        centerPivot = (ImageView) rootView.findViewById(R.id.centerPivot);
        locationPointer = (ImageView) rootView.findViewById(R.id.locationPointer);
        buttonOk = (Button)rootView.findViewById(R.id.buttonOk);

        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextSearch.getText().toString().length() < 1) {
                    scrollViewSearch.setVisibility(View.GONE);
                    layoutAddLocation.setVisibility(View.GONE);
                    centerPivot.setVisibility(View.VISIBLE);
                    locationPointer.setVisibility(View.VISIBLE);
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

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unsatflag) {
                    if(activity instanceof FreshActivity) {
                        FreshActivity freshActivity = (FreshActivity) activity;
                        freshActivity.setPlaceRequestCode(Constants.REQUEST_CODE_ADD_NEW_LOCATION);
                        freshActivity.setSearchResult(null);
                        freshActivity.setEditThisAddress(false);
                        freshActivity.openAddToAddressBook(createAddressBundle(placeId));
                    } else if(activity instanceof AddPlaceActivity){
                        ((AddPlaceActivity) activity).openAddToAddressBook(createAddressBundle(placeId));
                    }
                } else {
                    Utils.showToast(activity, "Please wait...");
                }
            }
        });


        searchListAdapter = new SearchListAdapter(activity, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient,
                PlaceSearchListFragment.PlaceSearchMode.PICKUP.getOrdinal(),
                new SearchListAdapter.SearchListActionsHandler() {

                    @Override
                    public void onTextChange(String text) {
                        try {
                            if (text.length() > 0) {
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSearchPre() {
                        progressBarSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSearchPost() {
                        progressBarSearch.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPlaceClick(SearchResult autoCompleteSearchResult) {
                    }

                    @Override
                    public void onPlaceSearchPre() {
                        progressBarSearch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPlaceSearchPost(SearchResult searchResult) {
                        progressBarSearch.setVisibility(View.GONE);
                        locationUpdate = true;
                        editTextSearch.setText("");
                        scrollViewSearch.setVisibility(View.GONE);
                        layoutAddLocation.setVisibility(View.GONE);
                        centerPivot.setVisibility(View.VISIBLE);
                        locationPointer.setVisibility(View.VISIBLE);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.getLatLng(), MAX_ZOOM), MAP_ANIMATE_DURATION, null);
                        textVeiwSearch.setText(searchResult.getAddress());
                        mAddressName.setText(searchResult.getAddress());
                        mSelectedLoc.setVisibility(View.VISIBLE);
                        mAddressName.setVisibility(View.VISIBLE);
                        progressWheel.setVisibility(View.GONE);

                        current_street = "";
                        current_route = "";
                        current_area = "";
                        current_city = "";
                        current_pincode = "";

                        current_latitude = searchResult.getLatLng().latitude;
                        current_longitude = searchResult.getLatLng().longitude;

                        String[] address = searchResult.getAddress().split(",");
                        List<String> addressArray = Arrays.asList(address);
                        Collections.reverse(addressArray);
                        address = (String[]) addressArray.toArray();

                        if(address.length > 0 && (!TextUtils.isEmpty(address[0].trim())))
                            current_pincode = "" + address[0].trim();
                        if(address.length > 1 && (!TextUtils.isEmpty(address[1].trim())))
                            current_city = "" + address[1].trim();
                        if(address.length > 2 && (!TextUtils.isEmpty(address[2].trim())))
                            current_area = "" + address[2].trim();

                        if(address.length > 3 && (!TextUtils.isEmpty(address[3].trim())))
                            current_route = "" + address[3].trim();

                        if(address.length > 4 && (!TextUtils.isEmpty(address[4].trim())))
                            current_street = "" + address[4].trim();

                        if(address.length > 5){
                            current_street = "";
                            for(int i=address.length-1; i > 3; i--){
                                if(current_street.equalsIgnoreCase("")){
                                    current_street = address[i].trim();
                                } else{
                                    current_street = current_street+", "+address[i].trim();
                                }
                            }
                        }
                    }

                    @Override
                    public void onPlaceSearchError() {
                        progressBarSearch.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPlaceSaved() {
                    }

                }, true);

        listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
        listViewSearch.setAdapter(searchListAdapter);


        relativeLayoutLocationError = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLocationError);
        relativeLayoutLocationErrorSearchBar = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLocationErrorSearchBar);
        ((TextView) rootView.findViewById(R.id.textViewLocationErrorSearch)).setTypeface(Fonts.mavenMedium(activity));

        //Location error layout
        relativeLayoutLocationError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationGotNow();
            }
        });

        relativeLayoutLocationErrorSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddress.performClick();
                relativeLayoutLocationError.setVisibility(View.GONE);
                searchAddress.setVisibility(View.VISIBLE);
                getMyLocation.setVisibility(View.VISIBLE);
            }
        });

        if(!editThisAddress && Data.locationAddressSettingsNoPressed){
            Data.locationAddressSettingsNoPressed = false;
            relativeLayoutLocationError.setVisibility(View.VISIBLE);
            searchAddress.setVisibility(View.GONE);
            layoutAddLocation.setVisibility(View.GONE);
            centerPivot.setVisibility(View.GONE);
            locationPointer.setVisibility(View.GONE);
            getMyLocation.setVisibility(View.GONE);
        } else {
            locationGotNow();
        }

        getMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocation != null && googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude())), 500, null);
                } else {
                    //Toast.makeText(homeActivity, Data.WAITING_FOR_LOCATION, Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.hideSoftKeyboard(activity, editTextSearch);
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);



        return rootView;
    }

    private void locationGotNow() {
        relativeLayoutLocationError.setVisibility(View.GONE);
        searchAddress.setVisibility(View.VISIBLE);
        layoutAddLocation.setVisibility(View.GONE);
        centerPivot.setVisibility(View.VISIBLE);
        locationPointer.setVisibility(View.VISIBLE);
        getMyLocation.setVisibility(View.VISIBLE);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && (activity instanceof FreshActivity)) {
            ((FreshActivity) activity).fragmentUISetup(this);
        }
    }

    public void closeLayout() {
        scrollViewSearch.setVisibility(View.GONE);
        layoutAddLocation.setVisibility(View.GONE);
        centerPivot.setVisibility(View.VISIBLE);
        locationPointer.setVisibility(View.VISIBLE);
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
        try {
            if (editThisAddress) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_latitude, current_longitude), MAX_ZOOM));
            } else if ((Data.locationAddressSettingsNoPressed)
                    || (Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0)) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.971723, 78.754263), 5));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), MAX_ZOOM));
            }
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

            mapStateListener = new MapStateListener(googleMap, mapFragment, getActivity()) {
                @Override
                public void onMapTouched() {
                    mapTouched = true;
                }

                @Override
                public void onMapReleased() {
                }

                @Override
                public void onMapUnsettled() {
                    mSelectedLoc.setVisibility(View.GONE);
                    mAddressName.setVisibility(View.GONE);
                    progressWheel.setVisibility(View.VISIBLE);
                    unsatflag = false;
                }

                @Override
                public void onMapSettled() {
                    if(!locationUpdate) {
                        if(editThisAddress
                                && MapUtils.distance(googleMap.getCameraPosition().target,
                                new LatLng(current_latitude, current_longitude)) < MAP_PAN_DISTANCE){
                            setNewAddressLatLng(googleMap.getCameraPosition().target);
                        } else {
                            fillAddressDetails(googleMap.getCameraPosition().target);
                        }
                    } else {
                        mSelectedLoc.setVisibility(View.VISIBLE);
                        mAddressName.setVisibility(View.VISIBLE);
                        progressWheel.setVisibility(View.GONE);
                        unsatflag = true;
                    }
                    locationUpdate = false;
                }
            };

        }
    }


    @Override
    public void onDestroyView() {
        destroyMap();
        super.onDestroyView();
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
            SupportMapFragment suMapFrag = (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapView);
            if (suMapFrag != null)
                activity.getSupportFragmentManager().beginTransaction().remove(suMapFrag).commit();

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
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
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
    public void hideKeyboardOnOutsideTouch(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }

            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboardOnOutsideTouch(innerView);
            }
        }
    }


    private void fillAddressDetails(final LatLng latLng) {
        try {
            if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
                final Map<String, String> params = new HashMap<String, String>(6);

                params.put(Data.LATLNG, latLng.latitude + "," + latLng.longitude);
                params.put("language", Locale.getDefault().getCountry());
                params.put("sensor", "false");

                RestClient.getGoogleApiServices().getMyAddress(params, new Callback<GoogleGeocodeResponse>() {
                    @Override
                    public void success(GoogleGeocodeResponse geocodeResponse, Response response) {
                        try {
                            setNewAddressLatLng(latLng);

                            current_street = ""+geocodeResponse.results.get(0).getStreetNumber();
                            current_route = ""+geocodeResponse.results.get(0).getRoute();
                            current_area = "" + geocodeResponse.results.get(0).getLocality();
                            current_city = "" + geocodeResponse.results.get(0).getCity();
                            current_pincode = "" + geocodeResponse.results.get(0).getPin();
                            String streetNum = current_street;
                            if(current_street.length()>0)
                                streetNum = geocodeResponse.results.get(0).getStreetNumber()+", ";

                            String route = current_route;
                            if(route.length()>0)
                                route = geocodeResponse.results.get(0).getRoute() + ", ";
                            route = current_route;

                            mAddressName.setText("For\n"+streetNum + route + geocodeResponse.results.get(0).getAddAddress()+", "+current_city);
                            textVeiwSearch.setText(""+streetNum + route + geocodeResponse.results.get(0).getAddAddress()+", "+current_city);

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(getActivity(), "", Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "RetrofitError error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(getActivity(), "", Data.SERVER_ERROR_MSG);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setNewAddressLatLng(LatLng latLng){
        unsatflag = true;

        mSelectedLoc.setVisibility(View.VISIBLE);
        mAddressName.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.GONE);

        current_latitude = latLng.latitude;
        current_longitude = latLng.longitude;
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
            scrollViewSearch.setVisibility(View.GONE);
            layoutAddLocation.setVisibility(View.GONE);
            centerPivot.setVisibility(View.VISIBLE);
            locationPointer.setVisibility(View.VISIBLE);
        }
    }

    private Bundle createAddressBundle(String placeId){
        Bundle bundle = new Bundle();
        bundle.putString("current_street", current_street);
        bundle.putString("current_route", current_route);
        bundle.putString("current_area", current_area);
        bundle.putString("current_city", current_city);
        bundle.putString("current_pincode", current_pincode);
        bundle.putDouble("current_latitude", current_latitude);
        bundle.putDouble("current_longitude", current_longitude);
        bundle.putString(Constants.KEY_PLACEID, placeId);
        return bundle;
    }

    private void fetchAddressBundle(){
        Bundle bundle = getArguments();
        current_street = bundle.getString("current_street", current_street);
        current_route = bundle.getString("current_route", current_route);
        current_area = bundle.getString("current_area", current_area);
        current_city = bundle.getString("current_city", current_city);
        current_pincode = bundle.getString("current_pincode", current_pincode);
        current_latitude = bundle.getDouble("current_latitude", current_latitude);
        current_longitude = bundle.getDouble("current_longitude", current_longitude);
        placeId = bundle.getString(Constants.KEY_PLACEID, placeId);
    }

}
