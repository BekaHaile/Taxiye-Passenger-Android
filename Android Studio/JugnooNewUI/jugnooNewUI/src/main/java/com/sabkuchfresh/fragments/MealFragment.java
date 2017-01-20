package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.MealAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshSortingDialog;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SubItemCompareAtoZ;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceLowToHigh;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceHighToLow;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by gurmail on 15/07/16.
 */
public class MealFragment extends Fragment implements FlurryEventNames, SwipeRefreshLayout.OnRefreshListener, MealAdapter.Callback {
    private final String TAG = MealFragment.class.getSimpleName();

    private RelativeLayout linearLayoutRoot;
    private MealAdapter mealAdapter;
    private RecyclerView recyclerViewCategoryItems;

    private Bus mBus;
    private ImageView noMealsView;
    private View rootView;
    private FreshActivity activity;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout noFreshsView;
    private TextView swipe_text;

    private FreshSortingDialog freshSortingDialog;
    private ArrayList<RecentOrder> recentOrder = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<SubItem> mealsData = new ArrayList<>();
    private ArrayList<SortResponseModel> slots = new ArrayList<>();
    private boolean resumed = false;
    private RelativeLayout relativeLayoutNoMenus;
    private TextView textViewNothingFound;

    public MealFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        mBus = activity.getBus();
        Data.AppType = AppConstant.ApplicationType.MEALS;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);

        linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        linearLayoutRoot.setBackgroundColor(activity.getResources().getColor(R.color.menu_item_selector_color));

        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView)rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        textViewNothingFound = (TextView)rootView.findViewById(R.id.textViewNothingFound); textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);

        mealAdapter = new MealAdapter(activity, mealsData, recentOrder, status, this);

        recyclerViewCategoryItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
        recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCategoryItems.setHasFixedSize(false);

        noMealsView = (ImageView) rootView.findViewById(R.id.noMealsView);
        noMealsView.setVisibility(View.GONE);

        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView); noFreshsView.setVisibility(View.GONE);
        swipe_text = (TextView) rootView.findViewById(R.id.swipe_text);
        swipe_text.setTypeface(Fonts.mavenRegular(activity));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setEnabled(true);

        recyclerViewCategoryItems.setAdapter(mealAdapter);

        setSortingList();

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MEALS);

        try {
            if(Data.getMealsData() != null && Data.getMealsData().getPendingFeedback() == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.openFeedback();
                    }
                }, 300);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isHidden() && resumed) {
            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MEALS);
            activity.setRefreshCart(false);
        }
        resumed = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            mealAdapter.notifyDataSetChanged();
            activity.resumeMethod();
            if(relativeLayoutNoMenus.getVisibility() == View.VISIBLE){
                activity.showBottomBar(false);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(activity.isRefreshCart()){
                        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MEALS);
                    }
                    activity.setRefreshCart(false);
                }
            }, 300);
        }
    }


    private void showPopup() {
        //123
        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
            @Override
            public void onDismiss() {
                //activity.orderComplete();
            }
        }).show();

    }

    private void setSortingList() {
        slots.clear();
        slots.add(new SortResponseModel(0, "A-Z", false));
        slots.add(new SortResponseModel(1, "Popularity", false));
        slots.add(new SortResponseModel(2, "Price: Low to High", false));
        slots.add(new SortResponseModel(3, "Price: High to Low", false));


    }

    public FreshSortingDialog getFreshSortingDialog() {

        if (freshSortingDialog == null) {
            freshSortingDialog = new FreshSortingDialog(activity, slots,
                    new FreshSortingDialog.FreshDeliverySortDialogCallback() {
                        @Override
                        public void onOkClicked(int position) {
                            //setSelectedSlotToView();
//                            activity.sortArray(position);
                            activity.mealSort = position;
                            onSortEvent(position);
//                            mBus.post(new SortSelection(position));
                        }
                    });
        }
        return freshSortingDialog;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
    }

    @Override
    public void onRefresh() {
        getAllProducts(false, activity.getSelectedLatLng());
    }

    public void getAllProducts(final boolean loader, final LatLng latLng) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                ProgressDialog progressDialog = null;
                if (loader)
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
                params.put(Constants.STORE_ID, "" + 2);
                params.put(Constants.KEY_CLIENT_ID, Config.getMealsClientId());
                params.put(Constants.INTERATED, "1");
                Log.i(TAG, "getAllProducts params=" + params.toString());

                new HomeUtil().putDefaultParams(params);
                final ProgressDialog finalProgressDialog = progressDialog;
                RestClient.getFreshApiService().getAllProducts(params, new Callback<ProductsResponse>() {
                    @Override
                    public void success(ProductsResponse productsResponse, Response response) {
                        noFreshsView.setVisibility(View.GONE);
                        relativeLayoutNoMenus.setVisibility(View.GONE);
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            if(!isHidden()) {
                                activity.showBottomBar(true);
                            } else {
                                Fragment fragment = activity.getTopFragment();
                                if(fragment != null && fragment instanceof MealFragment) {
                                    activity.showBottomBar(false);
                                }
                            }

                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if(flag == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()){
                                    relativeLayoutNoMenus.setVisibility(View.VISIBLE);
                                    mSwipeRefreshLayout.setVisibility(View.GONE);
                                    noMealsView.setVisibility(View.GONE);
                                    activity.showBottomBar(false);
                                    textViewNothingFound.setText(!TextUtils.isEmpty(productsResponse.getMessage()) ?
                                            productsResponse.getMessage() : getString(R.string.nothing_found_near_you));
                                }
                                else {
                                    int sortedBy = jObj.optInt(Constants.SORTED_BY);
                                    mealsData.clear();
                                    mealsData.addAll(productsResponse.getCategories().get(0).getSubItems());
                                    recentOrder.clear();
                                    recentOrder.addAll(productsResponse.getRecentOrders());
                                    status.clear();
                                    status.addAll(productsResponse.getRecentOrdersPossibleStatus());
                                    activity.setProductsResponse(productsResponse);
                                    activity.setMenuRefreshLatLng(new LatLng(latLng.latitude, latLng.longitude));
                                    setSortingList();
                                    if (activity.mealSort == -1) {
                                        slots.get(sortedBy).setCheck(true);
                                        activity.mealSort = sortedBy;
                                    } else {
                                        slots.get(activity.mealSort).setCheck(true);
                                        onSortEvent(activity.mealSort);
                                    }


                                    activity.canOrder = false;
                                    int size = productsResponse.getCategories().get(0).getSubItems().size();
                                    for (int i = 0; i < size; i++) {
                                        if (productsResponse.getCategories().get(0).getSubItems().get(i).getcanOrder() == 1) {
                                            activity.canOrder = true;
                                            break;
                                        }

                                    }

                                    mealAdapter.setList(mealsData);
                                    recyclerViewCategoryItems.smoothScrollToPosition(0);

                                    if(mealsData.size()+recentOrder.size()>0) {
                                        noMealsView.setVisibility(View.GONE);
                                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                        activity.showBottomBar(true);
                                    } else {
                                        noMealsView.setVisibility(View.VISIBLE);
                                        //mSwipeRefreshLayout.setVisibility(View.GONE);
                                        activity.showBottomBar(false);
                                    }

                                    if (activity.getProductsResponse() != null
                                            && activity.getProductsResponse().getCategories() != null) {
                                        activity.updateCartFromSP();
                                        activity.updateCartValuesGetTotalPrice();
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        noFreshsView.setVisibility(View.GONE);
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        try {
                            if(finalProgressDialog != null)
                            finalProgressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        retryDialog(DialogErrorType.CONNECTION_LOST);

                    }
                });
            } else {

                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        activity.showBottomBar(false);
        mealsData.clear();
        mealAdapter.setList(mealsData);

        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllProducts(true, activity.getSelectedLatLng());
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    @Override
    public void onSlotSelected(int position, SubItem slot) {

    }

    @Override
    public void onPlusClicked(int position, SubItem subItem) {
        activity.updateCartValuesGetTotalPrice();
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        activity.updateCartValuesGetTotalPrice();
    }

    @Override
    public boolean checkForMinus(int position, SubItem subItem) {
        return activity.checkForMinus(position, subItem);
    }

    @Override
    public void minusNotDone(int position, SubItem subItem) {
        activity.clearMealsCartIfNoMainMeal();
        mealAdapter.notifyDataSetChanged();
    }

    public void onSortEvent(int postion) {
        switch (postion) {
            case 0:
                Collections.sort(mealsData, new SubItemCompareAtoZ());
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.A_Z);
                mealAdapter = null;
                mealAdapter = new MealAdapter(activity, mealsData, recentOrder, status, this);
                recyclerViewCategoryItems.setAdapter(mealAdapter);
                break;
            case 1:
                Collections.sort(mealsData, new Comparator<SubItem>() {
                    @Override
                    public int compare(SubItem p1, SubItem p2) {
                        return p1.getPriorityId() - p2.getPriorityId();
                    }

                });
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.POPULARITY);
                mealAdapter = null;
                mealAdapter = new MealAdapter(activity, mealsData, recentOrder, status, this);
                recyclerViewCategoryItems.setAdapter(mealAdapter);
                break;
            case 2:
                Collections.sort(mealsData, new SubItemComparePriceLowToHigh());
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
                //mealAdapter.notifyDataSetChanged();
                mealAdapter = null;
                mealAdapter = new MealAdapter(activity, mealsData, recentOrder, status, this);
                recyclerViewCategoryItems.setAdapter(mealAdapter);
                break;
            case 3:
                Collections.sort(mealsData, new SubItemComparePriceHighToLow());
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
                mealAdapter = null;
                mealAdapter = new MealAdapter(activity, mealsData, recentOrder, status, this);
                recyclerViewCategoryItems.setAdapter(mealAdapter);
                break;
            default:
                // should not happened
                break;
        }
    }

    public MealAdapter getMealAdapter(){
        return mealAdapter;
    }

}
