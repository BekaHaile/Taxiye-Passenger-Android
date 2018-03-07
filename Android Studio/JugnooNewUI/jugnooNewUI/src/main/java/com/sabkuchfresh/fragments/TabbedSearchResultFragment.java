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

import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-01 on 3/7/18.
 */

public class TabbedSearchResultFragment extends Fragment {

    private RecyclerView rvSearch;
    private FreshActivity activity;
    private DeliveryHomeAdapter deliveryHomeAdapter;
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> statusMeals = new ArrayList<>();
    private ArrayList<String> statusFatafat = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.fragment_tabbed_search_result,container,false);
        initView(main);
        return main;
    }

    private void initView(final View main) {
        rvSearch = (RecyclerView) main.findViewById(R.id.rvSearchResults);
        rvSearch.setLayoutManager(new LinearLayoutManager(activity));

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
            public void apiRecommendRestaurant(int categoryId, String restaurantName, String locality, String telephone) {
               // NA here
            }
        }, rvSearch, status,statusMeals,statusFatafat);

        rvSearch.setAdapter(deliveryHomeAdapter);
    }

    public void setStoreSearchResponse(MenusResponse response){
        // send hasMorePages as true to avoid adding the suggest store layout
        deliveryHomeAdapter.setList(response, false, true);
    }

    public void setItemSearchResponse(MenusResponse response){
        // send hasMorePages as true to avoid adding the suggest store layout
        deliveryHomeAdapter.setList(response, false, true);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        activity = (FreshActivity)context;
    }
}
