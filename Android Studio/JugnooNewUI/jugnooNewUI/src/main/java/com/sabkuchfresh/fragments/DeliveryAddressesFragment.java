package com.sabkuchfresh.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.FreshAddressAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 14/09/16.
 */
public class DeliveryAddressesFragment extends Fragment implements FreshAddressAdapter.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private View rootView;
    private FreshActivity activity;
    protected Bus mBus;
    private FreshAddressAdapter addressFragment;
    private RecyclerView recyclerView;
    private LinearLayout linearLayoutAddFav, linearLayoutChooseOnMap, linearLayoutCurrentLocation, linearLayoutSearch;
    private RelativeLayout linearLayoutMain, relativeLayoutAddHome, relativeLayoutAddWork;
    private TextView textViewAddHome, textViewAddHomeValue, textViewAddWork, textViewAddWorkValue;
    private ImageView imageViewSep;
    private GoogleApiClient mGoogleApiClient;
    private SearchListAdapter searchListAdapter;
    private ScrollView scrollViewSearch;
    private NonScrollListView listViewSearch;
    private List<DeliveryAddress> deliveryAddresses = new ArrayList<DeliveryAddress>();


    public DeliveryAddressesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_delivery_addresses, container, false);

        activity = (FreshActivity) getActivity();
        mBus = (activity).getBus();

        activity.fragmentUISetup(this);

        linearLayoutMain = (RelativeLayout) rootView.findViewById(R.id.linearLayoutMain);

        new ASSL(activity, linearLayoutMain, 1134, 720, false);

        ((TextView)rootView.findViewById(R.id.textViewCurrentLocation)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewChooseOnMap)).setTypeface(Fonts.mavenMedium(activity));
        linearLayoutAddFav = (LinearLayout)rootView.findViewById(R.id.linearLayoutAddFav);
        linearLayoutCurrentLocation = (LinearLayout)rootView.findViewById(R.id.linearLayoutCurrentLocation);
        linearLayoutChooseOnMap = (LinearLayout)rootView.findViewById(R.id.linearLayoutChooseOnMap);
        relativeLayoutAddHome = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddHome);
        relativeLayoutAddWork = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddWork);
        textViewAddHome = (TextView)rootView.findViewById(R.id.textViewAddHome);
        textViewAddHomeValue = (TextView)rootView.findViewById(R.id.textViewAddHomeValue);
        textViewAddWork = (TextView)rootView.findViewById(R.id.textViewAddWork);
        textViewAddWorkValue = (TextView)rootView.findViewById(R.id.textViewAddWorkValue);
        imageViewSep = (ImageView)rootView.findViewById(R.id.imageViewSep);
        scrollViewSearch = (ScrollView) rootView.findViewById(R.id.scrollViewSearch);
        linearLayoutSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutSearch);
        scrollViewSearch.setVisibility(View.GONE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliveryAddress);
        recyclerView.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(false);
        recyclerView.setVisibility(View.VISIBLE);

        setSavePlaces();
        deliveryAddresses.addAll(activity.getUserCheckoutResponse().getCheckoutData().getDeliveryAddresses());
        for(int i=0; i<deliveryAddresses.size(); i++){
            if(activity.getSelectedAddress().equalsIgnoreCase(deliveryAddresses.get(i).getLastAddress())){
                deliveryAddresses.remove(i);
            }
        }
        addressFragment = new FreshAddressAdapter(activity, deliveryAddresses, this);
        recyclerView.setAdapter(addressFragment);


        mGoogleApiClient = new GoogleApiClient
                .Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        relativeLayoutAddHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
                    final SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(homeString);
                    getLatLngFromPlaceId(searchResult.getPlaceId(), new GetLatLngFromPlaceId() {
                        @Override
                        public void onLatLngReceived(LatLng latLng) {
                            if(latLng != null) {
                                /*Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(latLng.latitude));
                                Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(latLng.longitude));
                                Prefs.with(activity).save(activity.getResources().getString(R.string.pref_local_address), "" + searchResult.getAddress());

                                activity.setSelectedAddress("" + searchResult.getAddress());
                                mBus.post(new AddressAdded(true));
                                activity.performBackPressed();*/
                                fillAddressDetails(latLng);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        relativeLayoutAddWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
                    final SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(workString);
                    getLatLngFromPlaceId(searchResult.getPlaceId(), new GetLatLngFromPlaceId() {
                        @Override
                        public void onLatLngReceived(LatLng latLng) {
                            if(latLng != null) {
                                /*Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(latLng.latitude));
                                Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(latLng.longitude));
                                Prefs.with(activity).save(activity.getResources().getString(R.string.pref_local_address), "" + searchResult.getAddress());

                                activity.setSelectedAddress("" + searchResult.getAddress());
                                mBus.post(new AddressAdded(true));
                                activity.performBackPressed();*/
                                fillAddressDetails(latLng);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        linearLayoutCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddressAsync(new LatLng(Data.latitude, Data.longitude), new GetAddressFromLatLng() {
                    @Override
                    public void onAddressReceived(String address) {
                        if(address != null) {
                           /* Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(Data.latitude));
                            Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(Data.longitude));
                            Prefs.with(activity).save(activity.getResources().getString(R.string.pref_local_address), "" + address);

                            activity.setSelectedAddress("" + address);
                            mBus.post(new AddressAdded(true));
                            activity.performBackPressed();*/
                            //activity.openAddToAddressBook();
                            fillAddressDetails(new LatLng(Data.latitude, Data.longitude));
                        }
                    }
                });

            }
        });

        linearLayoutChooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.performBackPressed();
                activity.openMapAddress();
            }
        });

        activity.getTopBar().editTextDeliveryAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    scrollViewSearch.setVisibility(View.VISIBLE);
                } else{
                    scrollViewSearch.setVisibility(View.GONE);
                }
            }
        });

        searchListAdapter = new SearchListAdapter(activity, activity.getTopBar().editTextDeliveryAddress, new LatLng(30.75, 76.78), mGoogleApiClient,
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
                        //progressBarSearch.setVisibility(View.VISIBLE);
//                        searchListActionsHandler.onSearchPre();
                    }

                    @Override
                    public void onSearchPost() {
                        //progressBarSearch.setVisibility(View.GONE);
//                        searchListActionsHandler.onSearchPost();
                    }

                    @Override
                    public void onPlaceClick(SearchResult autoCompleteSearchResult) {
//                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                    }

                    @Override
                    public void onPlaceSearchPre() {
                        //progressBarSearch.setVisibility(View.VISIBLE);
//                        searchListActionsHandler.onPlaceSearchPre();
                    }

                    @Override
                    public void onPlaceSearchPost(SearchResult searchResult) {
                        //progressBarSearch.setVisibility(View.GONE);
//                        searchAddress.setText(searchResult.name);
                        activity.getTopBar().editTextDeliveryAddress.setText("");
                        scrollViewSearch.setVisibility(View.GONE);
//                        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_lati), String.valueOf(searchResult.getLatLng().latitude));
//                        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_longi), String.valueOf(searchResult.getLatLng().longitude));
//                        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_local_address), "" + searchResult.getAddress());
//                        activity.setSelectedAddress("" + searchResult.getAddress());
//                        mBus.post(new AddressAdded(true));
                        fillAddressDetails(searchResult.getLatLng());

                        //activity.performBackPressed();
                        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.getLatLng(), MAX_ZOOM), MAP_ANIMATE_DURATION, null);

//                        searchListActionsHandler.onPlaceSearchPost(searchResult);
//                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onPlaceSearchError() {
                        //progressBarSearch.setVisibility(View.GONE);
//                        searchListActionsHandler.onPlaceSearchError();
                    }

                    @Override
                    public void onPlaceSaved() {
                    }

                }, true);

        listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
        listViewSearch.setAdapter(searchListAdapter);

        return rootView;
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            activity.fragmentUISetup(this);
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

    private void getAddressAsync(final LatLng currentLatLng, final GetAddressFromLatLng getAddressFromLatLng){
        try {
            RestClient.getGoogleApiServices().geocode(currentLatLng.latitude + "," + currentLatLng.longitude,
                    "en", false, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            try {
                                String resp = new String(((TypedByteArray) response.getBody()).getBytes());
                                GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(resp);
                                String address = gapiAddress.getSearchableAddress();
                                getAddressFromLatLng.onAddressReceived(address);

                            } catch (Exception e) {
                                e.printStackTrace();
                                getAddressFromLatLng.onAddressReceived(null);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            getAddressFromLatLng.onAddressReceived(null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            getAddressFromLatLng.onAddressReceived(null);
        }

    }

    private void fillAddressDetails(final LatLng latLng) {
        try {
            if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {
                DialogPopup.showLoadingDialog(getActivity(), "Loading...");
                final Map<String, String> params = new HashMap<String, String>(6);

                params.put(Data.LATLNG, latLng.latitude + "," + latLng.longitude);
                params.put("language", Locale.getDefault().getCountry());
                params.put("sensor", "false");

                RestClient.getGoogleApiServices().getMyAddress(params, new Callback<GoogleGeocodeResponse>() {
                    @Override
                    public void success(GoogleGeocodeResponse geocodeResponse, Response response) {
                        try {

                            String addressText = "" + geocodeResponse.results.get(0).getLocality();

                            activity.current_latitude = latLng.latitude;
                            activity.current_longitude = latLng.longitude;

                            activity.current_street = ""+geocodeResponse.results.get(0).getStreetNumber();
                            activity.current_route = ""+geocodeResponse.results.get(0).getRoute();
                            activity.current_area = "" + geocodeResponse.results.get(0).getLocality();
                            activity.current_city = "" + geocodeResponse.results.get(0).getCity();
                            activity.current_pincode = "" + geocodeResponse.results.get(0).getPin();
                            String streetNum = activity.current_street;
                            if(activity.current_street.length()>0)
                                streetNum = geocodeResponse.results.get(0).getStreetNumber()+", ";

                            String route = activity.current_route;
                            if(route.length()>0)
                                route = geocodeResponse.results.get(0).getRoute() + ", ";

                            //activity.performBackPressed();
                            activity.openAddToAddressBook();

                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(getActivity(), "", Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("DeliveryAddressFragment", "error=" + error.toString());
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

    private void setSavePlaces() {
        if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
            String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
            SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(homeString);
            textViewAddHome.setText(getResources().getString(R.string.home));
            textViewAddHomeValue.setVisibility(View.VISIBLE);
            textViewAddHomeValue.setText(searchResult.getAddress());
        } else{
            relativeLayoutAddHome.setVisibility(View.GONE);
            textViewAddHome.setText(getResources().getString(R.string.add_home));
            textViewAddHomeValue.setVisibility(View.GONE);
            imageViewSep.setVisibility(View.GONE);
        }

        if (!Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
            String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
            SearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(workString);
            textViewAddWork.setText(getResources().getString(R.string.work));
            textViewAddWorkValue.setVisibility(View.VISIBLE);
            textViewAddWorkValue.setText(searchResult.getAddress());
        } else{
            relativeLayoutAddWork.setVisibility(View.GONE);
            textViewAddWork.setText(getResources().getString(R.string.add_work));
            textViewAddWorkValue.setVisibility(View.GONE);
            imageViewSep.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSlotSelected(int position, DeliveryAddress slot) {
        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_lati), slot.getDeliveryLatitude());
        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_longi), slot.getDeliveryLongitude());
        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_local_address), "" + slot.getLastAddress());

        activity.setSelectedAddress("" + slot.getLastAddress());
        //FlurryEventLogger.event(Address_Screen, CHANGE_ADDRESS, ""+position);

        mBus.post(new AddressAdded(true));
//        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_address_selected), 3);

        activity.performBackPressed();
    }

    @Override
    public void onAddAddress() {

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

}
