package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.adapters.FreshAddressAdapter;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DeliveryAddress;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 14/09/16.
 */
public class DeliveryAddressesFragment extends Fragment implements FreshAddressAdapter.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private View rootView;
    private Activity activity;
    protected Bus mBus;
    private FreshAddressAdapter addressFragment;
    private NonScrollListView listViewDeliveryAddress, listViewSavedLocations;
    private LinearLayout linearLayoutFav, linearLayoutChooseOnMap, linearLayoutCurrentLocation, linearLayoutSearch;
    private RelativeLayout linearLayoutMain, relativeLayoutAddHome, relativeLayoutAddWork;
    private TextView textViewAddHome, textViewAddHomeValue, textViewAddWork, textViewAddWorkValue;
    private ImageView imageViewSep;
    private GoogleApiClient mGoogleApiClient;
    private SearchListAdapter searchListAdapter;
    private ScrollView scrollViewSearch;
    private NonScrollListView listViewSearch;
    private List<DeliveryAddress> deliveryAddresses = new ArrayList<DeliveryAddress>();
    private EditText editTextDeliveryAddress;
    private SavedPlacesAdapter savedPlacesAdapter;


    public double current_latitude = 0.0;
    public double current_longitude = 0.0;
    public String current_street = "";
    public String current_route = "";
    public String current_area = "";
    public String current_city = "";
    public String current_pincode = "";


    public DeliveryAddressesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_delivery_addresses, container, false);

        activity = getActivity();
        mBus = MyApplication.getInstance().getBus();

        if(activity instanceof FreshActivity) {
            ((FreshActivity)activity).fragmentUISetup(this);
            editTextDeliveryAddress = ((FreshActivity)activity).getTopBar().editTextDeliveryAddress;
        }else if(activity instanceof AddPlaceActivity){
            editTextDeliveryAddress = ((AddPlaceActivity)activity).getEditTextDeliveryAddress();
        }

        linearLayoutMain = (RelativeLayout) rootView.findViewById(R.id.linearLayoutMain);

        new ASSL(activity, linearLayoutMain, 1134, 720, false);

        ((TextView)rootView.findViewById(R.id.textViewCurrentLocation)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewChooseOnMap)).setTypeface(Fonts.mavenMedium(activity));
        linearLayoutFav = (LinearLayout)rootView.findViewById(R.id.linearLayoutFav);
        linearLayoutCurrentLocation = (LinearLayout)rootView.findViewById(R.id.linearLayoutCurrentLocation);
        linearLayoutChooseOnMap = (LinearLayout)rootView.findViewById(R.id.linearLayoutChooseOnMap);
        relativeLayoutAddHome = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddHome);
        relativeLayoutAddWork = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddWork);
        textViewAddHome = (TextView)rootView.findViewById(R.id.textViewAddHome); textViewAddHome.setTypeface(Fonts.mavenMedium(activity));
        textViewAddHomeValue = (TextView)rootView.findViewById(R.id.textViewAddHomeValue); textViewAddHomeValue.setTypeface(Fonts.mavenMedium(activity));
        textViewAddWork = (TextView)rootView.findViewById(R.id.textViewAddWork); textViewAddWork.setTypeface(Fonts.mavenMedium(activity));
        textViewAddWorkValue = (TextView)rootView.findViewById(R.id.textViewAddWorkValue); textViewAddWorkValue.setTypeface(Fonts.mavenMedium(activity));
        imageViewSep = (ImageView)rootView.findViewById(R.id.imageViewSep);
        scrollViewSearch = (ScrollView) rootView.findViewById(R.id.scrollViewSearch);
        linearLayoutSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutSearch);
        scrollViewSearch.setVisibility(View.GONE);
        listViewDeliveryAddress = (NonScrollListView) rootView.findViewById(R.id.listViewDeliveryAddress);
        linearLayoutFav.setVisibility(View.GONE);

        listViewSavedLocations = (NonScrollListView) rootView.findViewById(R.id.listViewSavedLocations);
        try {
            savedPlacesAdapter = new SavedPlacesAdapter(activity, Data.userData.getSearchResults(), new SavedPlacesAdapter.Callback() {
                @Override
                public void onItemClick(SearchResult searchResult) {
                    if(searchResult.getIsConfirmed() == 1){
                        onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                                searchResult.getAddress(), searchResult.getId());
                    } else {
                        goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_NEW_LOCATION, true);
                    }
                }
            }, false, true);
            listViewSavedLocations.setAdapter(savedPlacesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(activity instanceof FreshActivity) {
            try {
                linearLayoutFav.setVisibility(View.VISIBLE);
                setSavePlaces();
                deliveryAddresses.addAll(((FreshActivity)activity).getUserCheckoutResponse().getCheckoutData().getDeliveryAddresses());
                for (int i = 0; i < deliveryAddresses.size(); i++) {
                    if (((FreshActivity)activity).getSelectedAddress().equalsIgnoreCase(deliveryAddresses.get(i).getLastAddress())) {
                        deliveryAddresses.remove(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listViewDeliveryAddress.setVisibility(View.VISIBLE);
            addressFragment = new FreshAddressAdapter(((FreshActivity)activity), deliveryAddresses, this);
            listViewDeliveryAddress.setAdapter(addressFragment);
        }
        else if(activity instanceof AddPlaceActivity){
            listViewDeliveryAddress.setVisibility(View.GONE);
            linearLayoutFav.setVisibility(View.GONE);
        }


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
                    final SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
                    if(searchResult.getIsConfirmed() == 1){
                        onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                                searchResult.getAddress(), searchResult.getId());
                    } else {
                        goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_HOME, true);
                    }
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
                    final SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
                    if(searchResult.getIsConfirmed() == 1){
                        onAddressSelected(String.valueOf(searchResult.getLatitude()), String.valueOf(searchResult.getLongitude()),
                                searchResult.getAddress(), searchResult.getId());
                    } else {
                        goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_WORK, true);
                    }
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
                            fillAddressDetails(new LatLng(Data.latitude, Data.longitude));
                        }
                    }
                });

            }
        });

        linearLayoutChooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof FreshActivity) {
                    ((FreshActivity)activity).openMapAddress(createAddressBundle());
                }
                else if(activity instanceof AddPlaceActivity) {
                    ((AddPlaceActivity)activity).openMapAddress(createAddressBundle());
                }
            }
        });

        searchListAdapter = new SearchListAdapter(activity, editTextDeliveryAddress, new LatLng(30.75, 76.78), mGoogleApiClient,
                PlaceSearchListFragment.PlaceSearchMode.PICKUP.getOrdinal(),
                new SearchListAdapter.SearchListActionsHandler() {

                    @Override
                    public void onTextChange(String text) {
                        try {
                            if(text.length() > 0){
                                scrollViewSearch.setVisibility(View.VISIBLE);
                            } else{
                                scrollViewSearch.setVisibility(View.GONE);
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
                        try {
                            editTextDeliveryAddress.setText("");
                            scrollViewSearch.setVisibility(View.GONE);

                            goToPredefinedSearchResultConfirmation(searchResult, Constants.REQUEST_CODE_ADD_NEW_LOCATION, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPlaceSearchError() {
                        //progressBarSearch.setVisibility(View.GONE);
//                        searchListActionsHandler.onPlaceSearchError();
                        Toast.makeText(activity, R.string.could_not_find_address, Toast.LENGTH_SHORT).show();
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
        if(!hidden && (activity instanceof FreshActivity)) {
            ((FreshActivity) activity).fragmentUISetup(this);
        } else if(!hidden && (activity instanceof AddPlaceActivity)){
            AddPlaceActivity addPlaceActivity = (AddPlaceActivity)activity;
            addPlaceActivity.getTextViewTitle().setVisibility(View.GONE);
            addPlaceActivity.getRelativeLayoutSearch().setVisibility(View.VISIBLE);
        }
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
            DialogPopup.showLoadingDialog(getActivity(), "Loading...");
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
                            DialogPopup.dismissLoadingDialog();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            getAddressFromLatLng.onAddressReceived(null);
            DialogPopup.dismissLoadingDialog();
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

                            current_latitude = latLng.latitude;
                            current_longitude = latLng.longitude;

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

                            if(activity instanceof FreshActivity) {
                                ((FreshActivity)activity).openAddToAddressBook(createAddressBundle());
                            }else if(activity instanceof AddPlaceActivity){
                                ((AddPlaceActivity)activity).openAddToAddressBook(createAddressBundle());
                            }

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

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void goToPredefinedSearchResultConfirmation(SearchResult searchResult, int placeRequestCode, boolean editThisAddress){
        try {
            if(activity instanceof FreshActivity){
				FreshActivity freshActivity = (FreshActivity) activity;
				freshActivity.setPlaceRequestCode(placeRequestCode);
				freshActivity.setSearchResult(searchResult);
                freshActivity.setEditThisAddress(editThisAddress);
			}
            setAddressToBundle(searchResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAddressToBundle(SearchResult searchResult){
        try {
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
            //fillAddressDetails(searchResult.getLatLng());
            if(activity instanceof FreshActivity) {
                ((FreshActivity)activity).openAddToAddressBook(createAddressBundle());
            }else if(activity instanceof AddPlaceActivity){
                ((AddPlaceActivity)activity).openAddToAddressBook(createAddressBundle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setSavePlaces() {
        if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
            String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
            SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
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
            SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
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
        onAddressSelected(slot.getDeliveryLatitude(), slot.getDeliveryLongitude(), slot.getLastAddress(), 0);
    }

    private void onAddressSelected(String latitude, String longitude, String address, int addressId){
        if(activity instanceof FreshActivity) {
            ((FreshActivity)activity).setSelectedAddress(address);
            ((FreshActivity)activity).setSelectedLatLng(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
            ((FreshActivity)activity).setSelectedAddressId(addressId);
            mBus.post(new AddressAdded(true));
            ((FreshActivity)activity).performBackPressed();
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

    private Bundle createAddressBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("current_street", current_street);
        bundle.putString("current_street", current_street);
        bundle.putString("current_route", current_route);
        bundle.putString("current_area", current_area);
        bundle.putString("current_city", current_city);
        bundle.putString("current_pincode", current_pincode);
        bundle.putDouble("current_latitude", current_latitude);
        bundle.putDouble("current_longitude", current_longitude);
        return bundle;
    }



}
