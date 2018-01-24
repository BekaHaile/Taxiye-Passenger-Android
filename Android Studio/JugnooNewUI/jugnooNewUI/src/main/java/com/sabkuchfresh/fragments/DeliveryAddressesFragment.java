package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.widgets.UserLockBottomSheetBehavior;
import com.squareup.otto.Bus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.apis.ApiFetchUserAddress;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ankit on 14/09/16.
 */
public class DeliveryAddressesFragment extends Fragment implements GAAction,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GACategory{

    private View rootView;
    private Activity activity;
    protected Bus mBus;
    private NonScrollListView listViewRecentAddresses, listViewSavedLocations;
    private TextView textViewSavedPlaces, textViewRecentAddresses;
    private CoordinatorLayout linearLayoutMain;
    private GoogleApiClient mGoogleApiClient;
    private SearchListAdapter searchListAdapter;
    private ScrollView scrollViewSearch;
    private NonScrollListView listViewSearch;
    private CardView cardViewSearch;
    private EditText editTextDeliveryAddress;
    private ProgressWheel progressWheelDeliveryAddressPin;
    private TextView tvDeliveryAddress;
    private SavedPlacesAdapter savedPlacesAdapter, savedPlacesAdapterRecent;
    private NestedScrollView scrollViewSuggestions;
    @Bind(R.id.rlMarkerPin)
    RelativeLayout rlMarkerPin;
    @Bind(R.id.bNext)
    Button bNext;
    @Bind(R.id.rlMain)
    RelativeLayout rlMain;
    @Bind(R.id.llLocationsContainer)
    LinearLayout llLocationsContainer;
    @Bind(R.id.llSetAnywhere)
    LinearLayout llSetAnywhere;
    @Bind(R.id.ll_set_location_on_map)
    LinearLayout llSetLocationOnMap;


    public double current_latitude = 0.0;
    public double current_longitude = 0.0;
    public String current_street = "";
    public String current_route = "";
    public String current_area = "";
    public String current_city = "";
    public String current_pincode = "";

    private SearchResult searchResultNearPin = null;


    private BottomSheetBehavior bottomSheetBehavior;
    private UserLockBottomSheetBehavior bottomSheetBehaviorLocked;
    private GoogleMap googleMap;
    private boolean autoCompleteResultClicked = false;
    private boolean canProceedWithUnsavedAddressMode;
    public static final String KEY_ARGS_PROCEED_WITHOUT_UNSAVED_ADDRESS = "key_args_proceed_without_unsaved_address";


    @OnClick(R.id.bMyLocation)
    void zoomToCurrentLocation(){
        try {
            if(googleMap != null
                    && MapUtils.distance(googleMap.getCameraPosition().target,
                    getCurrentLatLng()) > 10){
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), 14), 300, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LatLng getCurrentLatLng(){
        return new LatLng(Data.latitude, Data.longitude);
    }


   public static DeliveryAddressesFragment newInstance(boolean canProceedWithUnsavedAddressMode) {

        Bundle args = new Bundle();
        args.putBoolean(KEY_ARGS_PROCEED_WITHOUT_UNSAVED_ADDRESS,canProceedWithUnsavedAddressMode);
        DeliveryAddressesFragment fragment = new DeliveryAddressesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null && getArguments().containsKey(KEY_ARGS_PROCEED_WITHOUT_UNSAVED_ADDRESS)){
            canProceedWithUnsavedAddressMode = getArguments().getBoolean(KEY_ARGS_PROCEED_WITHOUT_UNSAVED_ADDRESS,canProceedWithUnsavedAddressMode);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_delivery_addresses, container, false);
        ButterKnife.bind(this, rootView);

        activity = getActivity();
        mBus = MyApplication.getInstance().getBus();

        if(activity instanceof FreshActivity) {
            ((FreshActivity)activity).fragmentUISetup(this);
            editTextDeliveryAddress = ((FreshActivity)activity).getTopBar().editTextDeliveryAddress;
            tvDeliveryAddress = ((FreshActivity)activity).getTopBar().tvDeliveryAddress;
            GAUtils.trackScreenView(((FreshActivity)activity).getGaCategory()+DELIVERY_ADDRESS);
            if(((FreshActivity)activity).getAppType() == AppConstant.ApplicationType.FEED){
                editTextDeliveryAddress.setHint(R.string.type_address);
                tvDeliveryAddress.setHint(R.string.type_address);
            } else if(((FreshActivity)activity).getAppType() == AppConstant.ApplicationType.PROS){
                editTextDeliveryAddress.setHint(R.string.type_service_address);
                tvDeliveryAddress.setHint(R.string.type_service_address);
            } else {
                editTextDeliveryAddress.setHint(R.string.type_delivery_address);
                tvDeliveryAddress.setHint(R.string.type_delivery_address);
            }
            progressWheelDeliveryAddressPin = ((FreshActivity)activity).getTopBar().progressWheelDeliveryAddressPin;

        } else if(activity instanceof AddPlaceActivity){
            editTextDeliveryAddress = ((AddPlaceActivity)activity).getEditTextDeliveryAddress();
            progressWheelDeliveryAddressPin = ((AddPlaceActivity)activity).getProgressWheelDeliveryAddressPin();
            tvDeliveryAddress = ((AddPlaceActivity)activity).getTvDeliveryAddress();
        }

        if(editTextDeliveryAddress != null){
            editTextDeliveryAddress.setText("");
        }
        if(tvDeliveryAddress != null){
            tvDeliveryAddress.setText("");
        }


        linearLayoutMain = (CoordinatorLayout) rootView.findViewById(R.id.linearLayoutMain);

        new ASSL(activity, linearLayoutMain, 1134, 720, false);

        scrollViewSearch = (ScrollView) rootView.findViewById(R.id.scrollViewSearch);
        scrollViewSearch.setVisibility(View.GONE);
        cardViewSearch = (CardView) rootView.findViewById(R.id.cardViewSearch);
        listViewRecentAddresses = (NonScrollListView) rootView.findViewById(R.id.listViewRecentAddresses);
        textViewSavedPlaces = (TextView) rootView.findViewById(R.id.textViewSavedPlaces); textViewSavedPlaces.setTypeface(Fonts.mavenMedium(activity));
        textViewRecentAddresses = (TextView) rootView.findViewById(R.id.textViewRecentAddresses); textViewRecentAddresses.setTypeface(Fonts.mavenMedium(activity));
        textViewSavedPlaces.setVisibility(View.GONE);
        textViewRecentAddresses.setVisibility(View.GONE);
        scrollViewSuggestions = (NestedScrollView) rootView.findViewById(R.id.scrollViewSuggestions);
        scrollViewSuggestions.setVisibility(View.VISIBLE);

        listViewSavedLocations = (NonScrollListView) rootView.findViewById(R.id.listViewSavedLocations);

        if(activity instanceof FreshActivity) {
            scrollViewSuggestions.setVisibility(View.VISIBLE);
            try {
                savedPlacesAdapter = new SavedPlacesAdapter(activity, homeUtil.getSavedPlacesWithHomeWork(activity), new SavedPlacesAdapter.Callback() {
                    @Override
                    public void onItemClick(SearchResult searchResult) {
                        savedAddressSelected(searchResult);
                    }

                    @Override
                    public void onDeleteClick(SearchResult searchResult) {
                    }
                }, true, true, false);
                listViewSavedLocations.setAdapter(savedPlacesAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                listViewSavedLocations.setVisibility(View.VISIBLE);
                listViewRecentAddresses.setVisibility(View.VISIBLE);
                savedPlacesAdapterRecent = new SavedPlacesAdapter(activity, Data.userData.getSearchResultsRecent(), new SavedPlacesAdapter.Callback() {
					@Override
					public void onItemClick(SearchResult searchResult) {

					    if(canProceedWithUnsavedAddressMode){
                            if(activity instanceof  FreshActivity && ((FreshActivity)activity).getAnywhereHomeFragment() != null){
                                ((FreshActivity) activity).getAnywhereHomeFragment().setRequestedAddress(searchResult);

                            }else{
                                onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                                        searchResult.getAddress(), searchResult.getId(), searchResult.getName(),searchResult);
                                if(activity instanceof FreshActivity) {
                                    GAUtils.event(((FreshActivity) activity).getGaCategory(), DELIVERY_ADDRESS, SUGGESTED_PLACES + SELECTED);
                                }
                            }
                            if(activity instanceof FreshActivity){
                                ((FreshActivity) activity).getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName()
                                        , FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }

                        }
                        else {
                            if(searchResult.getIsConfirmed() == 1){
                                onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                                        searchResult.getAddress(), searchResult.getId(), searchResult.getName(),searchResult);
                                if(activity instanceof FreshActivity) {
                                    GAUtils.event(((FreshActivity) activity).getGaCategory(), DELIVERY_ADDRESS, SUGGESTED_PLACES + SELECTED);
                                }
                            } else {
                                goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_NEW_LOCATION, false, false);
                            }
                        }
                        }


					@Override
					public void onDeleteClick(SearchResult searchResult) {
					}
				}, true, true, false);

                listViewRecentAddresses.setAdapter(savedPlacesAdapterRecent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(activity instanceof AddPlaceActivity){
            listViewRecentAddresses.setVisibility(View.GONE);
            listViewSavedLocations.setVisibility(View.GONE);
            scrollViewSuggestions.setVisibility(View.GONE);
        }



        mGoogleApiClient = new GoogleApiClient
                .Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        boolean showSavedPlaces = !(activity instanceof AddPlaceActivity);
        searchListAdapter = new SearchListAdapter(activity, editTextDeliveryAddress, new LatLng(30.75, 76.78), mGoogleApiClient,
                PlaceSearchListFragment.PlaceSearchMode.PICKUP.getOrdinal(),
                new SearchListAdapter.SearchListActionsHandler() {

                    @Override
                    public void onTextChange(String text) {
                        try {
                            if(text.length() > 0){
                                scrollViewSearch.setVisibility(View.VISIBLE);
                            }
                            if(activity instanceof AddPlaceActivity) {
                                AddPlaceActivity addPlaceActivity = ((AddPlaceActivity)activity);
                                if (text.length() > 0) {
                                    addPlaceActivity.getImageViewSearchCross().setVisibility(View.VISIBLE);
                                } else {
                                    addPlaceActivity.getImageViewSearchCross().setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSearchPre() {
                        showProgressWheelDeliveryPin();
                    }

                    @Override
                    public void onSearchPost() {
                        progressWheelDeliveryAddressPin.setVisibility(View.GONE);
                        if(activity instanceof FreshActivity) {
                            GAUtils.event(((FreshActivity)activity).getGaCategory(), DELIVERY_ADDRESS, ADDRESS_BOX + ENTERED);
                        }
                    }

                    @Override
                    public void onPlaceClick(SearchResult autoCompleteSearchResult) {

                    }

                    @Override
                    public void onPlaceSearchPre() {
                        showProgressWheelDeliveryPin();
                        DialogPopup.showLoadingDialog(activity, "");
                    }

                    @Override
                    public void onPlaceSearchPost(SearchResult searchResult) {
                        try {
                            progressWheelDeliveryAddressPin.setVisibility(View.GONE);
                            editTextDeliveryAddress.setText("");
                            scrollViewSearch.setVisibility(View.GONE);




                            if(canProceedWithUnsavedAddressMode){
                                if(activity instanceof  FreshActivity && ((FreshActivity)activity).getAnywhereHomeFragment() != null){
                                    ((FreshActivity) activity).getAnywhereHomeFragment().setRequestedAddress(searchResult);

                                }else{
                                    onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                                            searchResult.getAddress(), searchResult.getId(), searchResult.getName(),searchResult);
                                    if(activity instanceof FreshActivity) {
                                        GAUtils.event(((FreshActivity) activity).getGaCategory(), DELIVERY_ADDRESS, SUGGESTED_PLACES + SELECTED);
                                    }
                                }

                                if(activity instanceof FreshActivity){
                                    ((FreshActivity) activity).getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName()
                                            , FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }

                            }
                            else {
                                goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_NEW_LOCATION, false, true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void onPlaceSearchError() {
                        progressWheelDeliveryAddressPin.setVisibility(View.GONE);
                        Utils.showToast(activity, getString(R.string.could_not_find_address));
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void onPlaceSaved() {
                    }

                    @Override
                    public void onNotifyDataSetChanged(int count) {
                        if(count > 0){
                            cardViewSearch.setVisibility(View.VISIBLE);
                        } else {
                            cardViewSearch.setVisibility(View.GONE);
                        }
                    }
                }, showSavedPlaces);

        listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
        listViewSearch.setAdapter(searchListAdapter);


        setBottomSheetBehavior();
        setSavedPlaces();
        if(!Data.isRecentAddressesFetched()) {
            getApiFetchUserAddress().hit(false);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                getApiCallback().onFinish();
            }
        });

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                DeliveryAddressesFragment.this.googleMap = googleMap;
                if (googleMap != null) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    setupMapAndButtonMargins();
                    moveCameraToCurrent();



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
                            mapSettledCanForward = false;
                            searchResultNearPin = null;
                        }

                        @Override
                        public void onMapSettled() {
                            autoCompleteResultClicked = false;
                            if(scrollViewSuggestions.getVisibility()== View.GONE || getBottomSheetBehaviour()==null || getBottomSheetBehaviour().getState()==BottomSheetBehavior.STATE_COLLAPSED){
                                fillAddressDetails(DeliveryAddressesFragment.this.googleMap.getCameraPosition().target);

                            }

                        }

                        @Override
                        public void onCameraPositionChanged(CameraPosition cameraPosition) {
                        }
                    };

                }
            }
        });


        tvDeliveryAddressSetVisibility(View.VISIBLE);
        tvDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDeliveryAddressSetVisibility(View.GONE);
                editTextDeliveryAddress.setText("");
                editTextDeliveryAddress.setSelection(editTextDeliveryAddress.getText().length());
                editTextDeliveryAddress.requestFocus();
                Utils.showSoftKeyboard(activity, editTextDeliveryAddress);
            }
        });


        if(canProceedWithUnsavedAddressMode){
            rootView.findViewById(R.id.divider_above_map).setVisibility(View.GONE);
            if(((FreshActivity)activity).getAnywhereHomeFragment()!=null && ((FreshActivity)activity).getAnywhereHomeFragment().isPickUpAddressRequested()){
                llSetAnywhere.setVisibility(View.VISIBLE);
                llSetAnywhere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity instanceof FreshActivity){
                            ((FreshActivity) activity).getAnywhereHomeFragment().setRequestedAddress(null);
                            ((FreshActivity)activity).performBackPressed(false);
                        }

                    }
                });

                rootView.findViewById(R.id.divider_above_map).setVisibility(View.VISIBLE);

            }

            getBottomSheetBehaviour().setPeekHeight(0);
            getBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_EXPANDED);
            llSetLocationOnMap.setVisibility(View.VISIBLE);
            llSetLocationOnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_COLLAPSED);
                    try {
                        fillAddressDetails(DeliveryAddressesFragment.this.googleMap.getCameraPosition().target);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

            getBottomSheetBehaviour().setBottomSheetCallback(null);

        } else {
            rootView.findViewById(R.id.divider_above_map).setVisibility(View.GONE);
            rootView.findViewById(R.id.divider_below_map).setVisibility(View.GONE);
            llSetAnywhere.setVisibility(View.GONE);
            llSetLocationOnMap.setVisibility(View.GONE);

        }


        return rootView;
    }

    private void showProgressWheelDeliveryPin() {
        if(activity instanceof AddPlaceActivity && ((AddPlaceActivity) activity).getTopFragment() instanceof DeliveryAddressesFragment
                || (activity instanceof FreshActivity && ((FreshActivity) activity).getTopFragment() instanceof DeliveryAddressesFragment)){
            progressWheelDeliveryAddressPin.setVisibility(View.VISIBLE);

        }

    }

    private void moveCameraToCurrent(){
        if(getView() != null && googleMap != null) {
            if (activity instanceof AddPlaceActivity
                    && ((AddPlaceActivity) activity).isEditThisAddress()
                    && ((AddPlaceActivity) activity).getSearchResult() != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((AddPlaceActivity) activity).getSearchResult().getLatLng(), 14));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), 14));
                zoomToCurrentLocation();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(editTextDeliveryAddress != null) {
            Utils.hideSoftKeyboard(activity, editTextDeliveryAddress);
        }
    }

    private void setupMapAndButtonMargins() {
        if(getView() != null) {

            if(canProceedWithUnsavedAddressMode){
                RelativeLayout.LayoutParams paramsB = (RelativeLayout.LayoutParams) bNext.getLayoutParams();
                RelativeLayout.LayoutParams paramsRL = (RelativeLayout.LayoutParams) rlMarkerPin.getLayoutParams();
                int height = activity.getResources().getDimensionPixelSize(R.dimen.dp_20);
                paramsB.bottomMargin = height;
                bNext.setLayoutParams(paramsB);
                if (googleMap != null) {
                    googleMap.setPadding(0, 0, 0, scrollViewSuggestions.getVisibility() == View.VISIBLE ?
                            height : 0);
                }
                paramsRL.setMargins(0, 0, 0, 0);
                rlMarkerPin.setLayoutParams(paramsRL);
            }else{
                RelativeLayout.LayoutParams paramsRL = (RelativeLayout.LayoutParams) rlMarkerPin.getLayoutParams();
                RelativeLayout.LayoutParams paramsB = (RelativeLayout.LayoutParams) bNext.getLayoutParams();
                int height = activity.getResources().getDimensionPixelSize(R.dimen.dp_162);
//        if (savedPlacesAdapter.getCount() + savedPlacesAdapterRecent.getCount() <= 5) {
//            ViewGroup.LayoutParams layoutParams = scrollViewSuggestions.getLayoutParams();
//            layoutParams.height = llLocationsContainer.getMeasuredHeight() + activity.getResources().getDimensionPixelSize(R.dimen.dp_8);
//            scrollViewSuggestions.setLayoutParams(layoutParams);
//            height = layoutParams.height;
//        }
                if (scrollViewSuggestions.getVisibility() == View.VISIBLE) {
                    if (savedPlacesAdapter.getCount() >= 3) {
                        height = activity.getResources().getDimensionPixelSize(R.dimen.dp_280);
                        paramsRL.setMargins(0, 0, 0, height);
                        paramsB.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_300));
                    } else {
                        paramsRL.setMargins(0, 0, 0, height);
                        paramsB.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_186));
                    }
                } else {
                    paramsRL.setMargins(0, 0, 0, 0);
                    paramsB.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_20));
                }
                rlMarkerPin.setLayoutParams(paramsRL);
                bNext.setLayoutParams(paramsB);
                if (googleMap != null) {
                    googleMap.setPadding(0, 0, 0, scrollViewSuggestions.getVisibility() == View.VISIBLE ?
                            height : 0);
                }
                if (getBottomSheetBehaviour() != null ) {
                    getBottomSheetBehaviour().setPeekHeight(height);
                }
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden && (activity instanceof FreshActivity)) {
            ((FreshActivity) activity).fragmentUISetup(this);
//            setSavedPlaces();
            tvDeliveryAddressSetVisibility(View.VISIBLE);
            ((FreshActivity)activity).getTopBar().imageViewDelete.setVisibility(View.GONE);
        } else if(!hidden && (activity instanceof AddPlaceActivity)){
            AddPlaceActivity addPlaceActivity = (AddPlaceActivity)activity;
            addPlaceActivity.getTextViewTitle().setVisibility(View.GONE);
            addPlaceActivity.getRelativeLayoutSearch().setVisibility(View.VISIBLE);
            tvDeliveryAddressSetVisibility(View.VISIBLE);
            ((AddPlaceActivity)activity).getImageViewDelete().setVisibility(View.GONE);
        } else if(hidden){
            progressWheelDeliveryAddressPin.setVisibility(View.GONE);
            tvDeliveryAddressSetVisibility(View.GONE);
        }
        scrollViewSuggestions.scrollTo(0, 0);
    }

    private interface GetLatLngFromPlaceId{
        void onLatLngReceived(LatLng latLng);
    }

    private interface GetAddressFromLatLng{
        void onAddressReceived(String address);
    }

    private void getLatLngFromPlaceId(String placeId, final GetLatLngFromPlaceId getLatLngFromPlaceId){
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        try {
                            Log.e("SearchListAdapter", "getPlaceById response=" + places);
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                getLatLngFromPlaceId.onLatLngReceived(myPlace.getLatLng());
                            }else{
                                getLatLngFromPlaceId.onLatLngReceived(null);
                            }
                            places.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                            getLatLngFromPlaceId.onLatLngReceived(null);
                        }
                    }
                });
    }


    private void fillAddressDetails(final LatLng latLng) {
        try {
            // This will happen only in FreshActivity case
            // to check if some saved address is lying near to pin point, So that SearchResult object is hashed
            // in searchResultNearPin to direct back to offering fragment with address selected same as clicking on
            // saved addresses list
            if (activity instanceof FreshActivity) {
                searchResultNearPin = homeUtil.getNearBySavedAddress(activity, latLng,
                        Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
                if (searchResultNearPin != null) {
                    setFetchedAddressToTextView(searchResultNearPin.getName());
                    return;
                }
            }

            // we need to check if the autoCompleteResult clicked latLng is near some saved place,
            // if yes this case will also behave like map pan near saved location case
            if(autoCompleteResultClicked) {
                mapSettledCanForward = true;
                return;
            }

            if(isVisible() && !isRemoving()) {
                showProgressWheelDeliveryPin();
            }
            final Map<String, String> params = new HashMap<String, String>(6);

            params.put(Data.LATLNG, latLng.latitude + "," + latLng.longitude);
            params.put("language", Locale.getDefault().getCountry());
            params.put("sensor", "false");
//            params.put("key", activity.getString(R.string.google_maps_api_server_key));


            RestClient.getGoogleApiService().getMyAddress(params, new Callback<GoogleGeocodeResponse>() {
                @Override
                public void success(GoogleGeocodeResponse geocodeResponse, Response response) {
                    try {
                        if(geocodeResponse.results != null && geocodeResponse.results.size() > 0){
                            current_latitude = latLng.latitude;
                            current_longitude = latLng.longitude;

                            current_street = geocodeResponse.results.get(0).getStreetNumber();
                            current_route = geocodeResponse.results.get(0).getRoute();
                            current_area = geocodeResponse.results.get(0).getLocality();
                            current_city = geocodeResponse.results.get(0).getCity();
                            current_pincode = geocodeResponse.results.get(0).getCountry();

                            setFetchedAddressToTextView(current_street + (current_street.length()>0?", ":"")
                                    + current_route + (current_route.length()>0?", ":"")
                                    + geocodeResponse.results.get(0).getAddAddress()
                                    + ", " + current_city);
                        } else {
                            Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
                            tvDeliveryAddress.setText("");
                        }

                    } catch (Exception e) {
                        android.util.Log.e(TAG, "success: "+ geocodeResponse.getErrorMessage());
                        e.printStackTrace();
                        Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
                        tvDeliveryAddress.setText("");
                    }
                    progressWheelDeliveryAddressPin.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("DeliveryAddressFragment", "error=" + error.toString());
                    Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
                    progressWheelDeliveryAddressPin.setVisibility(View.GONE);
                    tvDeliveryAddress.setText("");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFetchedAddressToTextView(String address){
        tvDeliveryAddress.setText(address);
        if (isVisible() && scrollViewSearch.getVisibility() == View.GONE) {
            tvDeliveryAddressSetVisibility(View.VISIBLE);
        }
        mapSettledCanForward = true;
    }


    private void goToPredefinedSearchResultConfirmation(SearchResult searchResult, int placeRequestCode, boolean editThisAddress, boolean zoomToMap){
        try {
            if(activity instanceof FreshActivity){
				FreshActivity freshActivity = (FreshActivity) activity;
				freshActivity.setPlaceRequestCode(placeRequestCode);
				freshActivity.setSearchResult(searchResult);
                freshActivity.setEditThisAddress(editThisAddress);
                freshActivity.setDeliveryAddressToEdit(null);
			}
            setAddressToBundleAndOpenAddressForm(searchResult.getAddress(), searchResult.getLatitude(), searchResult.getLongitude(), searchResult.getPlaceId(), zoomToMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAddressToBundleAndOpenAddressForm(String addressRes, double latitude, double longitude, String placeId, boolean zoomToMap){
        try {
            current_street = "";
            current_route = "";
            current_area = "";
            current_city = "";
            current_pincode = "";

            current_latitude = latitude;
            current_longitude = longitude;

            String[] address = addressRes.split(",");
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
            //fillAddressDetails(searchResult.getLatLng());

            // if zoomToMap is true then only zooming map to the provided location and set provided address
            if(zoomToMap){
                setFetchedAddressToTextView(addressRes);
                autoCompleteResultClicked = true;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14), 300, null);
                getBottomSheetBehaviour().setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            // else do normally, direct to AddToAddressBook Fragment
            else {
                if (activity instanceof FreshActivity) {
                    ((FreshActivity) activity).openAddToAddressBook(createAddressBundle(placeId));
                } else if (activity instanceof AddPlaceActivity) {
                    ((AddPlaceActivity) activity).openAddToAddressBook(createAddressBundle(placeId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setSavedPlaces() {
        if(getView() != null) {
            if (savedPlacesAdapter != null) {
                savedPlacesAdapter.setList(homeUtil.getSavedPlacesWithHomeWork(activity));
                if (savedPlacesAdapter.getCount() > 0) {
                    textViewSavedPlaces.setVisibility(View.VISIBLE);
                    listViewSavedLocations.setVisibility(View.VISIBLE);
                    textViewSavedPlaces.setText(savedPlacesAdapter.getCount() == 1 ? R.string.saved_location : R.string.saved_locations);
                } else {
                    textViewSavedPlaces.setVisibility(View.GONE);
                    listViewSavedLocations.setVisibility(View.GONE);
                }
            }

            if (savedPlacesAdapterRecent != null) {
                savedPlacesAdapterRecent.notifyDataSetChanged();
                if (savedPlacesAdapterRecent.getCount() > 0) {
                    textViewRecentAddresses.setVisibility(View.VISIBLE);
                    listViewRecentAddresses.setVisibility(View.VISIBLE);
                    textViewRecentAddresses.setText(savedPlacesAdapterRecent.getCount() == 1 ? R.string.recent_location : R.string.recent_locations);
                } else {
                    textViewRecentAddresses.setVisibility(View.GONE);
                    listViewRecentAddresses.setVisibility(View.GONE);
                }
            }

            scrollViewSuggestions.setVisibility((listViewSavedLocations.getVisibility() == View.GONE
                    && listViewRecentAddresses.getVisibility() == View.GONE) ? View.GONE : View.VISIBLE);
            setupMapAndButtonMargins();
        }
    }

    private void onAddressSelected(String latitude, String longitude, String address, int addressId, String type,SearchResult searchResult){
        if(activity instanceof FreshActivity) {
            if(((FreshActivity) activity).getAnywhereHomeFragment()!=null){
                ((FreshActivity) activity).getAnywhereHomeFragment().setRequestedAddress(searchResult);


                if(((FreshActivity) activity).currentOpenClientIdForFab().equals(Config.FEED_CLIENT_ID)){
                    //If feed is opened as a Seperate offering
                    ((FreshActivity)activity).setSelectedAddress(address);
                    ((FreshActivity)activity).setSelectedLatLng(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                    ((FreshActivity)activity).setSelectedAddressId(addressId);
                    ((FreshActivity)activity).setSelectedAddressType(type);
                    mBus.post(new AddressAdded(true));
                }

            }else{
                ((FreshActivity)activity).setSelectedAddress(address);
                ((FreshActivity)activity).setSelectedLatLng(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                ((FreshActivity)activity).setSelectedAddressId(addressId);
                ((FreshActivity)activity).setSelectedAddressType(type);
                mBus.post(new AddressAdded(true));
            }
            ((FreshActivity)activity).performBackPressed(false);

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




    private ApiFetchUserAddress apiFetchUserAddress;
    private ApiFetchUserAddress.Callback apiCallback;
    private ApiFetchUserAddress getApiFetchUserAddress(){
        if(apiFetchUserAddress == null){

            apiFetchUserAddress = new ApiFetchUserAddress(activity, getApiCallback());
        }
        return apiFetchUserAddress;
    }

    private ApiFetchUserAddress.Callback getApiCallback(){
        if(apiCallback == null){
            apiCallback = new ApiFetchUserAddress.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {

                }

                @Override
                public void onNoRetry(View view) {

                }

                @Override
                public void onFinish() {
                    setSavedPlaces();
                    moveCameraToCurrent();
                }
            };
        }
        return apiCallback;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            progressWheelDeliveryAddressPin.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButterKnife.unbind(this);
    }


    private void currentLocationTap(){
        fillAddressDetails(getCurrentLatLng());
        if(activity instanceof FreshActivity) {
            GAUtils.event(((FreshActivity)activity).getGaCategory(), DELIVERY_ADDRESS, CURRENT_LOCATION+SELECTED);
        }
    }

    private void editHome(){
        try {
            String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
            final SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
            goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_HOME, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editWork(){
        try {
            String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
            final SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
            goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_WORK, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler();

    @OnClick(R.id.bNext)
    void goToAddAddressFragment(){
        if(mapSettledCanForward) {
            if(tvDeliveryAddress.getVisibility() == View.GONE){
                tvDeliveryAddressSetVisibility(View.VISIBLE);
                Utils.hideSoftKeyboard(activity, editTextDeliveryAddress);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goToAddAddressFragment();
                    }
                }, 600);
            } else {
                if (activity instanceof FreshActivity) {
                    // If searchResultNearPin is null, directing user to save this location to address book
                    if(searchResultNearPin == null) {

                        if(canProceedWithUnsavedAddressMode){
                            StringBuilder addressBuilder = new StringBuilder();
                            addressBuilder.append(current_street)
                                    .append(TextUtils.isEmpty(current_street)?"":", ")
                                    .append(current_route)
                                    .append(TextUtils.isEmpty(current_route)?"":", ")
                                    .append(current_area)
                                    .append(TextUtils.isEmpty(current_area)?"":", ")
                                    .append(current_city)
                                    .append(TextUtils.isEmpty(current_city)?"":", ")
                                    .append(current_pincode);

                            SearchResult searchResult = new SearchResult("",addressBuilder.toString()
                                    ,"",current_latitude,current_longitude);
                            onAddressSelected(String.valueOf(current_latitude),String.valueOf(current_longitude),addressBuilder.toString(),-1,"",searchResult);
                            ((FreshActivity) activity).getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } else {
                            FreshActivity freshActivity = (FreshActivity) activity;
                            freshActivity.setPlaceRequestCode(Constants.REQUEST_CODE_ADD_NEW_LOCATION);
                            freshActivity.setSearchResult(null);
                            freshActivity.setEditThisAddress(false);
                            freshActivity.openAddToAddressBook(createAddressBundle(""));
                        }
                    }
                    // Else use searchResultNearPin and perform saved addresses list click functionality
                    else {
                        savedAddressSelected(searchResultNearPin);
                    }
                } else if (activity instanceof AddPlaceActivity) {
                    ((AddPlaceActivity) activity).openAddToAddressBook(createAddressBundle(""));
                }
            }

            GAUtils.event(JUGNOO, DELIVERY_ADDRESS, NEXT+CLICKED);
        } else {
            Utils.showToast(activity, "Please wait...");
        }
    }

    private boolean mapSettledCanForward = false;
    private HomeUtil homeUtil = new HomeUtil();

    /**
     * @return returns boolean true if back was consumed by fragment or false otherwise
     */
    public boolean backWasConsumed(){
        if(scrollViewSearch.getVisibility() == View.VISIBLE){
            tvDeliveryAddressSetVisibility(View.VISIBLE);
            Utils.hideKeyboard(activity);
            return true;
        }
        return false;
    }

    private void tvDeliveryAddressSetVisibility(int visibility){
        tvDeliveryAddress.setVisibility(visibility);
        scrollViewSearch.setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void savedAddressSelected(SearchResult searchResult){
        if(searchResult.getIsConfirmed() == 1){
            onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                    searchResult.getAddress(), searchResult.getId(), searchResult.getName(),searchResult);
            if(activity instanceof FreshActivity) {
                GAUtils.event(((FreshActivity)activity).getGaCategory(), DELIVERY_ADDRESS, SAVED_PLACES+SELECTED);
            }
        } else {
            goToPredefinedSearchResultConfirmation(searchResult, searchResult.getPlaceRequestCode(), true, false);
        }
    }

    private BottomSheetBehavior getBottomSheetBehaviour(){
        if(canProceedWithUnsavedAddressMode){
            return bottomSheetBehaviorLocked;
        }else{
           return  bottomSheetBehavior;
        }
    }

    private void setBottomSheetBehavior(){
        if(canProceedWithUnsavedAddressMode){
            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) scrollViewSuggestions.getLayoutParams();
            params.setBehavior(new UserLockBottomSheetBehavior());
            scrollViewSuggestions.requestLayout();
            bottomSheetBehaviorLocked = (UserLockBottomSheetBehavior) UserLockBottomSheetBehavior.from(scrollViewSuggestions);
        }else{
            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) scrollViewSuggestions.getLayoutParams();
            params.setBehavior(new BottomSheetBehavior());
            scrollViewSuggestions.requestLayout();
            bottomSheetBehavior = BottomSheetBehavior.from(scrollViewSuggestions);
        }
    }


}
