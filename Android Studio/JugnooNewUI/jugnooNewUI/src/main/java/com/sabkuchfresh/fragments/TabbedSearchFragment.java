package com.sabkuchfresh.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.DeliveryHomeAdapter;
import com.sabkuchfresh.adapters.TabbedPagerAdaptor;
import com.sabkuchfresh.datastructure.SearchSuggestion;
import com.sabkuchfresh.datastructure.VendorDirectSearch;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-01 on 3/7/18.
 */

public class TabbedSearchFragment extends Fragment {

    private TabLayout tabLayoutSearch;
    private ViewPager viewPagerSearch;
    private TabbedSearchResultFragment storeSearchResultFragment, itemSearchResultFragment;
    private FreshActivity activity;
    private String searchText = "";
    private boolean isMenusApiInProgress = false;
    private boolean searchOpened = false;
    private LinearLayout llRecentSearch, llSearchResults;
    private RecyclerView rvRecentSearches;
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> statusMeals = new ArrayList<>();
    private ArrayList<String> statusFatafat = new ArrayList<>();
    private TextView tvNoResults;
    private final int TAB_ITEMS = 0, TAB_STORES = 1;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.fragment_tabbed_search, container, false);
        initView(main);
        setData();
        return main;
    }

    private void initView(final View main) {
        tabLayoutSearch = (TabLayout) main.findViewById(R.id.tabSearch);
        tabLayoutSearch.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPagerSearch = (ViewPager) main.findViewById(R.id.vpSearch);
        llRecentSearch = (LinearLayout) main.findViewById(R.id.llRecentSearch);
        llSearchResults = (LinearLayout) main.findViewById(R.id.llSearchResults);
        tvNoResults = (TextView) main.findViewById(R.id.tvNoResults);
        tvNoResults.setVisibility(View.GONE);
        rvRecentSearches = (RecyclerView) main.findViewById(R.id.rvRecentSearch);
        rvRecentSearches.setLayoutManager(new LinearLayoutManager(activity));
        setUpViewForFresh();
        // initially hide the cross button
        activity.getTopBar().ivSearchCross.setVisibility(View.GONE);
        activity.getTopBar().etSearch.setText("");
        activity.getTopBar().etSearch.post(new Runnable() {
            @Override
            public void run() {
                activity.getTopBar().etSearch.requestFocus();
                Utils.showSoftKeyboard(activity, activity.getTopBar().etSearch);
            }
        });

    }

    /**
     * Controls which elements to show/hide for fresh topBar
     */
    private void setUpViewForFresh(){

        activity.fragmentUISetup(this);
        activity.getTopBar().imageViewMenu.setVisibility(View.GONE);
        activity.getTopBar().imageViewBack.setVisibility(View.VISIBLE);
        activity.getTopBar().title.setVisibility(View.GONE);
        activity.getTopBar().llSearchContainer.setVisibility(View.VISIBLE);
        activity.getTopBar().setSearchVisibility(View.VISIBLE);
        activity.getTopBar().rlSearch.setVisibility(View.GONE);
        activity.getTopBar().getLlTopBarDeliveryAddress().setVisibility(View.GONE);
        activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
        searchOpened = true;
    }

    @Override
    public void onHiddenChanged(final boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setUpViewForFresh();
        }
    }

    public boolean isSearchOpened() {
        return searchOpened;
    }

    private void setData() {

        DeliveryHomeAdapter deliveryHomeAdapter = new DeliveryHomeAdapter(activity, new DeliveryHomeAdapter.Callback() {
            @Override
            public void onRestaurantSelected(int vendorId, final boolean shouldOpenMerchantInfo) {
                // NA here
            }

            @Override
            public void onBannerInfoDeepIndexClick(int deepIndex) {
                // NA here
            }

            @Override
            public void openCategory(MenusResponse.Category category) {
                // NA here
            }

            @Override
            public void onVendorDirectSearchClicked(final VendorDirectSearch vendorDirectSearch) {
                // NA
            }

            @Override
            public void apiRecommendRestaurant(int categoryId, String restaurantName, String locality, String telephone) {
                // NA here
            }

            @Override
            public boolean showDirectVendorSuggestions() {
                return false;
            }

            @Override
            public boolean showSuggestions() {
                return true;
            }

            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                // set the suggestion on etSearch
                activity.getTopBar().etSearch.setText(searchSuggestion.getText());
                activity.getTopBar().etSearch.setSelection(searchSuggestion.getText().length());

                // switch to items tab
                viewPagerSearch.setCurrentItem(TAB_ITEMS);

                Utils.hideKeyboard(activity);

            }
        }, rvRecentSearches, status, statusMeals, statusFatafat);

        rvRecentSearches.setAdapter(deliveryHomeAdapter);

        // set tabs up
        String[] titles = activity.getResources().getStringArray(R.array.search_tab_names);
        ArrayList<Fragment> fragments = new ArrayList<>();
        storeSearchResultFragment = new TabbedSearchResultFragment();
        itemSearchResultFragment = new TabbedSearchResultFragment();

        fragments.add(itemSearchResultFragment);
        fragments.add(storeSearchResultFragment);
        TabbedPagerAdaptor pagerAdaptor = new TabbedPagerAdaptor(getChildFragmentManager(), fragments, titles);
        viewPagerSearch.setAdapter(pagerAdaptor);
        tabLayoutSearch.setupWithViewPager(viewPagerSearch);
        changeFontInViewGroup(tabLayoutSearch);

        // initially hide the search results layout and show recent searches
        showRecentSearches();

        MenusResponse menusResponse = new MenusResponse();
        menusResponse.setVendors(null);
        menusResponse.setSuggestionsList(Data.getDeliveryCustomerData().getRecentSearches());

        // send hasMorePages as true to avoid adding the suggest store layout
        deliveryHomeAdapter.setList(menusResponse, false, true);

    }

    private void showRecentSearches(){
        llSearchResults.setVisibility(View.GONE);
        llRecentSearch.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        activity = (FreshActivity) context;
    }

    /**
     * Change font for a viewgroup
     *
     * @param viewGroup the viewGroup
     */
    void changeFontInViewGroup(ViewGroup viewGroup) {
        Typeface typeface = Fonts.mavenRegular(activity);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {
                ((TextView) child).setTypeface(typeface, Typeface.BOLD);
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }

    public void doSearch(String searchString) {

        if (searchOpened) {
            int oldLength = searchText.length();
            searchText = searchString;
            if (searchText.length() > 2) {
                // hide recent searches
                llRecentSearch.setVisibility(View.GONE);
                llSearchResults.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);
                getAllMenus(false, activity.getSelectedLatLng(), true, activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_SEARCH);

            } else {
                if (oldLength > searchString.length() && oldLength >= 1) {
                    // empty show recent suggestions
                    showRecentSearches();

                    activity.getTopBar().etSearch.post(new Runnable() {
                        @Override
                        public void run() {
                            activity.getTopBar().etSearch.requestFocus();
                        }
                    });

                    // clear the past search results in both tabs
                    if(storeSearchResultFragment!=null && itemSearchResultFragment !=null){
                        MenusResponse response = new MenusResponse();
                        response.setVendors(null);
                        response.setDirectSearchVendors(null);
                        response.setSuggestionsList(null);
                        storeSearchResultFragment.setStoreSearchResponse(response);
                        itemSearchResultFragment.setSearchSuggestions(response);
                    }

                }

            }
        }

    }

    public void searchClosed() {
        searchOpened = false;
        activity.getTopBar().etSearch.setText("");
    }


    public void getAllMenus(final boolean loader, final LatLng latLng, final boolean scrollToTop, final MenusResponse.Category categoryObject, final int typeApi) {

        if (isMenusApiInProgress)
            return;

        int categoryId = categoryObject == null ? -1 : categoryObject.getId();
        final String searchTextCurr = searchText;
        try {
            if (!MyApplication.getInstance().isOnline()) {
                retryDialog(DialogErrorType.NO_NET, latLng, loader, false, scrollToTop, categoryObject, typeApi);
                return;
            }

            // do not hit if recentSearch is visible
            if(llRecentSearch.getVisibility()==View.VISIBLE){
                return;
            }

            if (loader)
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

            HashMap<String, String> params = getMenusApiHashMap(latLng, categoryId);
            Callback<MenusResponse> callback = new Callback<MenusResponse>() {
                @Override
                public void success(final MenusResponse menusResponse, Response response) {

                    if (searchOpened) {
                        boolean shouldRecallSearchAPI = false;
                        activity.getTopBar().setPBSearchVisibility(View.GONE);
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == menusResponse.getFlag()) {

                                    // do not load results if recentSearch is visible
                                    if(llRecentSearch.getVisibility()!=View.VISIBLE) {

                                        //set the response to store and item fragments
                                        MenusResponse storeMenusResponse = new MenusResponse();
                                        storeMenusResponse.setVendors(menusResponse.getVendors());
                                        storeMenusResponse.setDirectSearchVendors(null);
                                        storeSearchResultFragment.setStoreSearchResponse(storeMenusResponse);

                                        MenusResponse itemsMenusResponse = new MenusResponse();
                                        if (menusResponse.getSuggestionsList() != null) {
                                            itemsMenusResponse.setSuggestionsList(menusResponse.getSuggestionsList());
                                        } else {
                                            itemsMenusResponse.setSuggestionsList(new ArrayList<SearchSuggestion>());
                                        }
                                        itemSearchResultFragment.setSearchSuggestions(itemsMenusResponse);

                                        shouldRecallSearchAPI = true;
                                        activity.getTopBar().etSearch.requestFocus();

                                        // decide whether to show no results found
                                        if ((menusResponse.getSuggestionsList() != null && menusResponse.getSuggestionsList().size() == 0)
                                                && (menusResponse.getVendors() != null && menusResponse.getVendors().size() == 0)) {
                                            tvNoResults.setVisibility(View.VISIBLE);
                                            llSearchResults.setVisibility(View.GONE);
                                        } else {
                                            tvNoResults.setVisibility(View.GONE);
                                            llSearchResults.setVisibility(View.VISIBLE);

                                            // switch to stores tab if items are empty
                                            if(menusResponse.getSuggestionsList().size()==0){
                                                viewPagerSearch.setCurrentItem(TAB_STORES);
                                            }
                                            else {
                                                viewPagerSearch.setCurrentItem(TAB_ITEMS);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR, latLng, loader, false, scrollToTop, categoryObject, typeApi);
                        }
                        isMenusApiInProgress = false;
                        if (shouldRecallSearchAPI) {
                            recallSearch(searchTextCurr);

                        }
                        DialogPopup.dismissLoadingDialog();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    activity.getTopBar().setPBSearchVisibility(View.GONE);
                    isMenusApiInProgress = false;
                    DialogPopup.dismissLoadingDialog();
                    retryDialog(DialogErrorType.CONNECTION_LOST, latLng, loader, false, scrollToTop, categoryObject, typeApi);


                }

            };

            activity.getTopBar().setPBSearchVisibility(View.VISIBLE);
            isMenusApiInProgress = true;
            params.put(Constants.KEY_SEARCH_TEXT, searchText);
            RestClient.getMenusApiService().fetchRestaurantViaSearchV2(params, callback);

        } catch (Exception e) {
            e.printStackTrace();
            isMenusApiInProgress = false;
        }
    }


    @NonNull
    private HashMap<String, String> getMenusApiHashMap(LatLng latLng, int categoryId) {
        HashMap<String, String> params = new HashMap<>();

        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
        params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
        params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
        params.put(Constants.INTERATED, "1");
        params.put(Constants.KEY_SHOW_ALL_RESULT, String.valueOf(1));
        params.put(Constants.PAGE_NO, String.valueOf(0));
        if (categoryId > 0) {
            params.put(Constants.KEY_MERCHANT_CATEGORY_ID, String.valueOf(categoryId));
        }

        new HomeUtil().putDefaultParams(params);
        return params;
    }

    private void recallSearch(String previousSearchText) {
        if (searchOpened && !searchText.trim().equalsIgnoreCase(previousSearchText)) {
            getAllMenus(false, activity.getSelectedLatLng(), true, activity.getCategoryOpened(), MenusFragment.TYPE_API_MENUS_SEARCH);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final LatLng latLng, final boolean loader, final boolean isPagination, final boolean scrollToTop, final MenusResponse.Category category, final int typeApi) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if (isPagination) {
                            // NA here
                        } else {
                            getAllMenus(loader, latLng, scrollToTop, category, typeApi);

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
