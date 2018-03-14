package com.sabkuchfresh.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-01 on 3/7/18.
 */

public class TabbedSearchResultFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rvSearch;
    private FreshActivity activity;
    private DeliveryHomeAdapter deliveryHomeAdapter;
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> statusMeals = new ArrayList<>();
    private ArrayList<String> statusFatafat = new ArrayList<>();
    private LinearLayout llSuggestions;
    private TextView tvSuggestionsHeader,tvSuggestionValue;
    private ImageView imgVwArrow;
    private List<MenusResponse.SearchSuggestions> searchSuggestions = new ArrayList<>();
    private boolean apiInProgress = false;
    private boolean switchedToSuggestions = false;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.fragment_tabbed_search_result,container,false);
        initView(main);

        // set initial data if coming
        if(getArguments()!=null && getArguments().containsKey(MenusResponse.class.getSimpleName())){
            MenusResponse menusResponse = new Gson().fromJson(getArguments().getString
                    (MenusResponse.class.getSimpleName()),MenusResponse.class);

            if(menusResponse.getSuggestionsList()!=null){
                // suggestions response
                setSearchSuggestions(menusResponse);
            }

            if(menusResponse.getVendors()!=null){
                // stores response
                setStoreSearchResponse(menusResponse);
            }
        }

        return main;
    }

    private void initView(final View main) {
        llSuggestions = (LinearLayout) main.findViewById(R.id.llSuggestions);
        tvSuggestionsHeader = (TextView) main.findViewById(R.id.tvSuggestionHeader);
        tvSuggestionValue = (TextView) main.findViewById(R.id.tvSuggestionValue);
        imgVwArrow = (ImageView) main.findViewById(R.id.imgVwArrow);

        // initially hide the suggestions
        llSuggestions.setVisibility(View.GONE);

        rvSearch = (RecyclerView) main.findViewById(R.id.rvSearchResults);
        rvSearch.setLayoutManager(new LinearLayoutManager(activity));

        tvSuggestionsHeader.setOnClickListener(this);

        deliveryHomeAdapter = new DeliveryHomeAdapter(activity, new DeliveryHomeAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int vendorId) {
                activity.fetchRestaurantMenuAPI(vendorId, false, null, null, -1, null);
                Utils.hideKeyboard(activity);
            }

            @Override
            public void onBannerInfoDeepIndexClick(int deepIndex) {
                Data.deepLinkIndex = deepIndex;
                if (activity != null) {
                    activity.openDeepIndex();
                }
            }

            @Override
            public void openCategory(MenusResponse.Category category) {
                // NA here
            }

            @Override
            public void onVendorDirectSearchClicked(final MenusResponse.VendorDirectSearch vendorDirectSearch) {
                activity.setDirectVendorSearchObject(vendorDirectSearch);
                //fetch menu and redirect to vendor fragment
                activity.fetchRestaurantMenuAPI((vendorDirectSearch.getVendorId()),
                        false, null, null, -1, null,vendorDirectSearch);
                Utils.hideKeyboard(activity);
            }

            @Override
            public void apiRecommendRestaurant(int categoryId, String restaurantName, String locality, String telephone) {
               // NA here
            }

            @Override
            public boolean showDirectVendorSuggestions() {
                return true;
            }

            @Override
            public boolean showSuggestions() {
                return true;
            }

            @Override
            public void onSuggestionClicked(final MenusResponse.SearchSuggestions searchSuggestion) {
                // get the items response from the suggestion
                switchedToSuggestions = false;
                getSuggestions(false, activity.getSelectedLatLng(), true
                        , activity.getCategoryOpened(),searchSuggestion.getId());

                // show suggestion layout and set text
                llSuggestions.setVisibility(View.VISIBLE);
                tvSuggestionsHeader.setVisibility(View.VISIBLE);
                imgVwArrow.setVisibility(View.VISIBLE);
                tvSuggestionValue.setVisibility(View.VISIBLE);
                tvSuggestionValue.setText(searchSuggestion.getText());
            }
        }, rvSearch, status,statusMeals,statusFatafat);

        rvSearch.setAdapter(deliveryHomeAdapter);
    }

    public void setStoreSearchResponse(MenusResponse response){

        llSuggestions.setVisibility(View.GONE);
        response.setSuggestionsList(null);
        response.setDirectSearchVendors(null);
        // send hasMorePages as true to avoid adding the suggest store layout
        deliveryHomeAdapter.setList(response, false, true);
    }

    public void setItemSearchResponse(MenusResponse response){

        response.setVendors(null);
        response.setSuggestionsList(null);
        // send hasMorePages as true to avoid adding the suggest store layout
        deliveryHomeAdapter.setList(response, false, true);
    }

    public void setSearchSuggestions(MenusResponse response){

        searchSuggestions = response.getSuggestionsList();

        // show suggestions layout
        llSuggestions.setVisibility(View.VISIBLE);
        imgVwArrow.setVisibility(View.GONE);
        tvSuggestionValue.setVisibility(View.GONE);

        response.setDirectSearchVendors(null);
        response.setVendors(null);
        // send hasMorePages as true to avoid adding the suggest store layout
        deliveryHomeAdapter.setList(response, false, true);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        activity = (FreshActivity)context;
    }

    @Override
    public void onClick(final View v) {
        if(v.getId()==R.id.tvSuggestionHeader){
            //hide items and show last suggestions again
            MenusResponse menusResponse = new MenusResponse();
            menusResponse.setSuggestionsList(searchSuggestions);
            setSearchSuggestions(menusResponse);
            switchedToSuggestions = true;
        }
    }

    public void getSuggestions(final boolean loader, final LatLng latLng, final boolean scrollToTop
            , final MenusResponse.Category categoryObject, final int suggestionId) {

        if (apiInProgress)
            return;

        int categoryId = categoryObject == null ? -1 : categoryObject.getId();
        try {
            if (!MyApplication.getInstance().isOnline()) {
                retryDialog(DialogErrorType.NO_NET, latLng, loader, false, scrollToTop, categoryObject,suggestionId);
                return;
            }

            if (loader)
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
            params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
            params.put(Constants.INTERATED, "1");
            params.put(Constants.KEY_SHOW_ALL_RESULT,String.valueOf(1));
            params.put(Constants.PAGE_NO, String.valueOf(0));
            if (categoryId > 0) {
                params.put(Constants.KEY_MERCHANT_CATEGORY_ID, String.valueOf(categoryId));
            }

            new HomeUtil().putDefaultParams(params);

            Callback<MenusResponse> callback = new Callback<MenusResponse>() {
                @Override
                public void success(final MenusResponse menusResponse, Response response) {

                    if (activity!=null && !activity.isFinishing()) {
                        activity.getTopBar().setPBSearchVisibility(View.GONE);

                        if(!switchedToSuggestions) {

                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            try {
                                JSONObject jObj = new JSONObject(responseStr);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()) {

                                        //set the direct vendor response
                                        MenusResponse itemsMenusResponse = new MenusResponse();
                                        if (menusResponse.getDirectSearchVendors() != null) {
                                            itemsMenusResponse.setSuggestionsList(menusResponse.getSuggestionsList());
                                        } else {
                                            itemsMenusResponse.setDirectSearchVendors(new ArrayList<MenusResponse.VendorDirectSearch>());
                                        }
                                        setItemSearchResponse(itemsMenusResponse);
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                retryDialog(DialogErrorType.SERVER_ERROR, latLng, loader, false
                                        , scrollToTop, categoryObject,suggestionId);
                            }
                        }
                        apiInProgress = false;
                        DialogPopup.dismissLoadingDialog();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    activity.getTopBar().setPBSearchVisibility(View.GONE);
                    apiInProgress = false;
                    DialogPopup.dismissLoadingDialog();
                    if(!switchedToSuggestions) {
                        retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader, false
                                , scrollToTop, categoryObject,suggestionId);
                    }


                }

            };

            activity.getTopBar().setPBSearchVisibility(View.VISIBLE);
            apiInProgress = true;
            RestClient.getMenusApiService().fetchRestaurantViaSearchV2(params, callback);


        } catch (Exception e) {
            e.printStackTrace();
            apiInProgress = false;
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader, final boolean isPagination
            , final boolean scrollToTop, final MenusResponse.Category category, final int suggestionId) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if (isPagination) {
                            // NA here
                        } else {
                            getSuggestions(loader, latLng, scrollToTop, category,suggestionId);
                        }
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                }, Data.getCurrentIciciUpiTransaction(activity.getAppType()) == null);
    }
}
