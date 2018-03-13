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

import com.google.gson.Gson;
import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;

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
            public void onSuggestionClicked(final MenusResponse.SearchSuggestions searchSuggestions) {
                // todo get the items response from the suggestion
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
        }
    }
}
