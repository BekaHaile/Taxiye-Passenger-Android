package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.MealAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshDeliverySlotsDialog;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.SortResponseModel;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.SubItemCompare;
import com.sabkuchfresh.retrofit.model.SubItemComparePrice;
import com.sabkuchfresh.retrofit.model.SubItemComparePriceRev;
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
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
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

    private FreshDeliverySlotsDialog freshDeliverySlotsDialog;
    private ArrayList<SubItem> mealsData = new ArrayList<>();

    private ArrayList<SortResponseModel> slots = new ArrayList<>();

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

        mealAdapter = new MealAdapter(activity, mealsData, this);

        recyclerViewCategoryItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
        recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCategoryItems.setHasFixedSize(false);

        noMealsView = (ImageView) rootView.findViewById(R.id.noMealsView);
        noMealsView.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setEnabled(true);

        recyclerViewCategoryItems.setAdapter(mealAdapter);


        setSortingList();
        getAllProducts(true);

        try {
            if(Data.getMealsData() != null && Data.getMealsData().getPendingFeedback() == 1) {
                //TODO meals feedback fragment open here
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            mealAdapter.notifyDataSetChanged();
            activity.resumeMethod();
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

    public FreshDeliverySlotsDialog getFreshDeliverySlotsDialog() {

        if (freshDeliverySlotsDialog == null) {
            freshDeliverySlotsDialog = new FreshDeliverySlotsDialog(activity, slots,
                    new FreshDeliverySlotsDialog.FreshDeliverySortDialogCallback() {
                        @Override
                        public void onOkClicked(int position) {
                            //setSelectedSlotToView();
//                            activity.sortArray(position);
                            onSortEvent(position);
//                            mBus.post(new SortSelection(position));
                        }
                    });
        }
        return freshDeliverySlotsDialog;
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
        getAllProducts(false);
    }

    public void getAllProducts(final boolean loader) {
        try {

            if (AppStatus.getInstance(activity).isOnline(activity)) {
                if (loader)
                    DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
                params.put(Constants.STORE_ID, "" + 2);
                params.put(Constants.KEY_CLIENT_ID, Config.getMealsClientId());
                params.put(Constants.INTERATED, "1");
                Log.i(TAG, "getAllProducts params=" + params.toString());

                RestClient.getFreshApiService().getAllProducts(params, new Callback<ProductsResponse>() {
                    @Override
                    public void success(ProductsResponse productsResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                int sortedBy = jObj.getInt(Constants.SORTED_BY);
                                //Prefs.with(activity).save(Constants.SORTED_BY, sortedBy);
                                setSortingList();
                                slots.get(sortedBy).setCheck(true);
                                activity.setProductsResponse(productsResponse);

                                activity.canOrder = false;
                                int size = productsResponse.getCategories().get(0).getSubItems().size();
                                for (int i = 0; i < size; i++) {
                                    if (productsResponse.getCategories().get(0).getSubItems().get(i).getcanOrder() == 1) {
                                        activity.canOrder = true;
                                        break;
                                    }

                                }

                                mealsData.clear();
                                mealsData.addAll(productsResponse.getCategories().get(0).getSubItems());
                                mealAdapter.setList(mealsData);

                                if(mealsData.size()>0) {
                                    noMealsView.setVisibility(View.GONE);
                                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                    activity.hideBottomBar(true);
                                } else {
                                    noMealsView.setVisibility(View.VISIBLE);
                                    //mSwipeRefreshLayout.setVisibility(View.GONE);
                                    activity.hideBottomBar(false);
                                }

                                if (activity.getProductsResponse() != null
                                        && activity.getProductsResponse().getCategories() != null) {
                                    activity.updateCartFromSP();
                                    activity.updateCartValuesGetTotalPrice();

                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
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

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getAllProducts(true);
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


    public void onSortEvent(int postion) {
        switch (postion) {
            case 0:
                Collections.sort(mealsData, new SubItemCompare());
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.A_Z);
                mealAdapter.notifyDataSetChanged();
                break;
            case 1:
                Collections.sort(mealsData, new Comparator<SubItem>() {
                    @Override
                    public int compare(SubItem p1, SubItem p2) {
                        return p1.getPriorityId() - p2.getPriorityId();
                    }

                });
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.POPULARITY);
                mealAdapter.notifyDataSetChanged();
                break;
            case 2:
                Collections.sort(mealsData, new SubItemComparePrice());
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
                mealAdapter.notifyDataSetChanged();
                break;
            case 3:
                Collections.sort(mealsData, new SubItemComparePriceRev());
                FlurryEventLogger.event(FlurryEventNames.HOME_SCREEN, FlurryEventNames.SORT, FlurryEventNames.PRICE_LOW_TO_HIGH);
                mealAdapter.notifyDataSetChanged();
                break;
            default:
                // should not happened
                break;
        }
    }
}
