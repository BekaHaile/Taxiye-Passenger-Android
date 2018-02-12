package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCurrentStatusIciciUpi;
import com.sabkuchfresh.commoncalls.ApiLikeMeal;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.DiscountInfo;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.RecentOrder;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SubItemCompareAtoZ;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceHighToLow;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceLowToHigh;
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
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
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
public class MealFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MealAdapter.Callback, GAAction, GACategory {
    private final String TAG = MealFragment.class.getSimpleName();

    private LinearLayout llRoot;
    private MealAdapter mealAdapter;
    private RecyclerView recyclerViewCategoryItems;

    private Bus mBus;
    private ImageView noMealsView;
    private View rootView;
    private FreshActivity activity;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout noFreshsView;
    private TextView swipe_text;

    private ArrayList<RecentOrder> recentOrder = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<SubItem> mealsData = new ArrayList<>();
    private ArrayList<SortResponseModel> slots = new ArrayList<>();
    private ProductsResponse.MealsBulkBanner mealsBulkBanner;
    private boolean resumed = false;
    private RelativeLayout relativeLayoutNoMenus;
    private TextView textViewNothingFound;
    private View vShadow;

    public MealFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);

        GAUtils.trackScreenView(MEALS+HOME);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
//        activity.setDeliveryAddressView(rootView);

        mBus = activity.getBus();
        Data.AppType = AppConstant.ApplicationType.MEALS;
        Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MEALS);

        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        llRoot.setBackgroundColor(activity.getResources().getColor(R.color.white));

        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView)rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

        textViewNothingFound = (TextView)rootView.findViewById(R.id.textViewNothingFound); textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);


        recyclerViewCategoryItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
        recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
//        recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCategoryItems.setHasFixedSize(false);
        ((SimpleItemAnimator) recyclerViewCategoryItems.getItemAnimator()).setSupportsChangeAnimations(false);
        mealAdapter = new MealAdapter(activity, mealsData, recentOrder, status, this,null,recyclerViewCategoryItems );

        noMealsView = (ImageView) rootView.findViewById(R.id.noMealsView);
        noMealsView.setVisibility(View.GONE);

        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView); noFreshsView.setVisibility(View.GONE);
        swipe_text = (TextView) rootView.findViewById(R.id.swipe_text);
        swipe_text.setTypeface(Fonts.mavenRegular(activity));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setEnabled(true);

        recyclerViewCategoryItems.setAdapter(mealAdapter);
        vShadow = rootView.findViewById(R.id.vShadow);
        vShadow.setVisibility(View.VISIBLE);

        setSortingList();

        recyclerViewCategoryItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    int offset = recyclerView.computeVerticalScrollOffset();
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();

                    int percentage = (int)(100.0 * offset / (float)(range - extent));

                    if(percentage > 0 && percentage % 10 == 0) {
                        GAUtils.event(MEALS, HOME + LIST_SCROLLED, percentage + "%");
                        Log.i("GA Logged", "scroll percentage: "+ percentage + "%");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MEALS);

        try {
            if(!activity.isDeliveryOpenInBackground() && Data.getMealsData() != null && Data.getMealsData().getPendingFeedback() == 1) {
                //activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.openFeedback(Config.getMealsClientId());
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
            if(!activity.isOrderJustCompleted()) {
                activity.setAddressTextToLocationPlaceHolder();
            }
            mealAdapter.notifyDataSetChanged();
            activity.resumeMethod();
            if(activity.getCartChangedAtCheckout()){
                activity.updateItemListFromSPDB();
                mealAdapter.notifyDataSetChanged();
                activity.updateCartValuesGetTotalPrice();
            }
            activity.setCartChangedAtCheckout(false);
            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(activity.isRefreshCart()){
                        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.MEALS);
                    }
                    activity.setRefreshCart(false);
                    activity.setMinOrderAmountText(MealFragment.this);
                }
            }, 300);
            activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
            if(relativeLayoutNoMenus.getVisibility() == View.VISIBLE){
                activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
                activity.llCheckoutBarSetVisibilityDirect(View.GONE);
            }

            if(noMealsView.getVisibility() == View.VISIBLE){
                activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
                activity.llCheckoutBarSetVisibilityDirect(View.GONE);
            }
        }/*else{
            activity.setRefreshCart(true);
        }*/

    }


    private void showPopup() {
        //123
        new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
            @Override
            public void onDismiss() {
                //activity.orderComplete();
            }
        }).showNoDeliveryDialog();

    }

    private void setSortingList() {
        slots.clear();
        slots.add(new SortResponseModel(0, "A-Z", false));
        slots.add(new SortResponseModel(1, "Popularity", false));
        slots.add(new SortResponseModel(2, "Price: Low to High", false));
        slots.add(new SortResponseModel(3, "Price: High to Low", false));


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(llRoot);
        System.gc();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(activity.textViewMinOrder!=null) {
            activity.textViewMinOrder.setOnClickListener(null);
        }
        if(getMealAdapter()!=null){
            getMealAdapter().removeScheduledHandler();
        }
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
            if (MyApplication.getInstance().isOnline()) {
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
                        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                        activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
                        activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {

                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if(flag == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()){
                                    activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
                                    activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
                                    activity.llCheckoutBarSetVisibilityDirect(View.GONE);
                                    relativeLayoutNoMenus.setVisibility(View.VISIBLE);
                                    mSwipeRefreshLayout.setVisibility(View.GONE);
                                    noMealsView.setVisibility(View.GONE);
                                    textViewNothingFound.setText(!TextUtils.isEmpty(productsResponse.getMessage()) ?
                                            productsResponse.getMessage() : getString(R.string.nothing_found_near_you));
                                }
                                else {
                                    if(Data.getMealsData() != null && Data.getMealsData().getPendingFeedback() == 1) {
                                        activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
                                        activity.llCheckoutBarSetVisibilityDirect(View.GONE);
                                    } else{
                                        activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
                                        activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
                                    }
                                    if(productsResponse.getCategories() == null || productsResponse.getCategories().size() == 0){
                                        noMealsView.setVisibility(View.VISIBLE);
                                        recyclerViewCategoryItems.setVisibility(View.GONE);
                                        activity.llCheckoutBarSetVisibilityDirect(View.GONE);
                                        if(finalProgressDialog != null)
                                            finalProgressDialog.dismiss();
                                        return;
                                    }
                                    recyclerViewCategoryItems.setVisibility(View.VISIBLE);
                                    int sortedBy = jObj.optInt(Constants.SORTED_BY);
                                    mealsData.clear();
                                    mealsData.addAll(productsResponse.getCategories().get(0).getSubItems());
                                    recentOrder.clear();
                                    recentOrder.addAll(productsResponse.getRecentOrders());
                                    status.clear();
                                    status.addAll(productsResponse.getRecentOrdersPossibleStatus());
                                    mealsBulkBanner = productsResponse.getMealsBulkBanner();
                                    activity.setProductsResponse(productsResponse);
                                    activity.setMenuRefreshLatLng(new LatLng(latLng.latitude, latLng.longitude));
                                    setSortingList();
                                    if (activity.mealSort == -1) {
                                        slots.get(sortedBy).setCheck(true);
                                        activity.mealSort = sortedBy;
                                        mealAdapter.setList(mealsData, recentOrder, status, mealsBulkBanner,productsResponse.getDiscountInfo() );
                                    } else {
                                        slots.get(activity.mealSort).setCheck(true);
                                        onSortEvent(activity.mealSort,productsResponse.getDiscountInfo() );
                                    }


                                    activity.canOrder = false;
                                    int size = productsResponse.getCategories().get(0).getSubItems().size();
                                    for (int i = 0; i < size; i++) {
                                        if (productsResponse.getCategories().get(0).getSubItems().get(i).getcanOrder() == 1) {
                                            activity.canOrder = true;
                                            break;
                                        }

                                    }

                                    if(mealAdapter.getItemCount()>0) {
                                        noMealsView.setVisibility(View.GONE);
                                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                        activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
                                    } else {
                                        noMealsView.setVisibility(View.VISIBLE);
                                        activity.llCheckoutBarSetVisibilityDirect(View.GONE);
                                    }
                                    activity.setTitleAlignment(false);

                                    if (activity.getProductsResponse() != null
                                            && activity.getProductsResponse().getCategories() != null) {
                                        activity.updateItemListFromSPDB(); // this is necessary
                                        activity.updateCartValuesGetTotalPrice();
                                    }
                                }
                            }
                            activity.setMinOrderAmountText(MealFragment.this);
                            checkPendingTransaction();
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
    }

    private void checkPendingTransaction() {

        if(Data.getCurrentIciciUpiTransaction(activity.getAppType())!=null){
            activity.setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(activity.getAppType()));
            ApiCurrentStatusIciciUpi.checkIciciPaymentStatusApi(activity, false, new ApiCurrentStatusIciciUpi.ApiCurrentStatusListener() {
                @Override
                public void onGoToCheckout(IciciPaymentOrderStatus iciciPaymentOrderStatus) {
                    activity.openCart(AppConstant.ApplicationType.MEALS, true);
                }
            }, AppConstant.ApplicationType.MEALS);
        }

    }

    private void retryDialog(DialogErrorType dialogErrorType) {
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
                },Data.getCurrentIciciUpiTransaction(activity.getAppType())==null);
    }

    @Override
    public void onSlotSelected(int position, SubItem slot) {

    }

    @Override
    public void onPlusClicked(int position, SubItem subItem) {
        activity.saveSubItemToDeliveryStoreCart(subItem);
        activity.updateCartValuesGetTotalPrice();
        activity.getFabViewTest().hideJeanieHelpInSession();
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        activity.saveSubItemToDeliveryStoreCart(subItem);
        activity.updateCartValuesGetTotalPrice();
        activity.getFabViewTest().hideJeanieHelpInSession();
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

    private ApiLikeMeal apiLikeMeal;
    @Override
    public boolean onLikeClicked(SubItem subItem, int position) {
        if (!mSwipeRefreshLayout.isRefreshing()) {

            if (apiLikeMeal == null)
                apiLikeMeal = new ApiLikeMeal(new ApiLikeMeal.LikeMealCallback() {
                    @Override
                    public void onSuccess(boolean isLiked, int position, SubItem subItem) {
                        if (getView() != null && mealAdapter != null) {

                            mealAdapter.notifyOnLike(position, isLiked);
                        }
                    }

                    @Override
                    public void onFailure(boolean isLiked, int posInOriginalList, SubItem subItem) {
                        if (subItem != null) {
                            subItem.setLikeAPIInProgress(false);
                        }
                    }
                });
            apiLikeMeal.likeFeed( getActivity(), !subItem.getIsLiked(), position, subItem);
//            GAUtils.event(FEED, HOME, LIKE + CLICKED);
        }


        return true;
    }

    public void onSortEvent(int position, DiscountInfo discountInfo) {
        switch (position) {
            case 0:
                Collections.sort(mealsData, new SubItemCompareAtoZ());
                break;
            case 1:
                Collections.sort(mealsData, new Comparator<SubItem>() {
                    @Override
                    public int compare(SubItem p1, SubItem p2) {
                        return p1.getPriorityId() - p2.getPriorityId();
                    }

                });
                break;
            case 2:
                Collections.sort(mealsData, new SubItemComparePriceLowToHigh());
                break;
            case 3:
                Collections.sort(mealsData, new SubItemComparePriceHighToLow());
                break;
        }
        mealAdapter.setList(mealsData, recentOrder, status, mealsBulkBanner,discountInfo);
    }

    public MealAdapter getMealAdapter(){
        return mealAdapter;
    }

    public boolean shouldShowStrip() {
        return getView()!=null && relativeLayoutNoMenus.getVisibility()!=View.VISIBLE;
    }


}
