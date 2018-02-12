package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshSuperCategoriesAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCurrentStatusIciciUpi;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.utils.AppConstant;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 19/01/17.
 */

public class FreshHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction, GACategory{

    private View rootView;
    private LinearLayout llRoot;
    private RelativeLayout relativeLayoutNoMenus;
    private FreshActivity activity;
    private RecyclerView rvFreshSuper;
    private FreshSuperCategoriesAdapter superCategoriesAdapter;
    private TextView textViewNothingFound;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_home, container, false);
        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            activity = (FreshActivity) getActivity();
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
            GAUtils.trackScreenView(FRESH+HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.fragmentUISetup(this);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.white);
        swipeContainer.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
        swipeContainer.setSize(SwipeRefreshLayout.DEFAULT);
        swipeContainer.setEnabled(true);
        //swipeContainer.setVisibility(View.GONE);

        relativeLayoutNoMenus = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoMenus);
        ((TextView)rootView.findViewById(R.id.textViewOhSnap)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);

        textViewNothingFound = (TextView)rootView.findViewById(R.id.textViewNothingFound); textViewNothingFound.setTypeface(Fonts.mavenMedium(activity));
        relativeLayoutNoMenus.setVisibility(View.GONE);
        rvFreshSuper = (RecyclerView) rootView.findViewById(R.id.rvFreshSuper);
        rvFreshSuper.setItemAnimator(new DefaultItemAnimator());
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                switch (superCategoriesAdapter.getItemViewType(position)){
                    case FreshSuperCategoriesAdapter.SINGLE_ITEM:
                        return 2;
                    case FreshSuperCategoriesAdapter.PAGER:
                        return 3;
                    case FreshSuperCategoriesAdapter.MAIN_ITEM:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        rvFreshSuper.setLayoutManager(gridLayoutManager);
        superCategoriesAdapter = new FreshSuperCategoriesAdapter(activity, new FreshSuperCategoriesAdapter.Callback() {
            @Override
            public void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory) {
                try {
                    if(superCategory.getIsBulkOrder()){
                        Utils.openCallIntent(activity,superCategory.getBulkOrderCellNumber());
                    }else if(superCategory.getSuperCategoryId() != null) {



                            if (superCategory.getIsEnabled() == 0) {
                                Utils.showToast(activity, getString(R.string.coming_soon_to_your_city));
                            } else {
                                activity.getTransactionUtils().addFreshFragment(activity, activity.getRelativeLayoutContainer(), superCategory);
                                activity.getFabViewTest().hideJeanieHelpInSession();
                            }
                            try {
                                GAUtils.event(FRESH, HOME + SUPER + CATEGORY + CLICKED, superCategory.getSuperCategoryName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }



					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rvFreshSuper.setAdapter(superCategoriesAdapter);

        relativeLayoutNoMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);

        try {
            if(Data.getFreshData() != null && Data.getFreshData().getPendingFeedback() == 1) {
                activity.getHandler().postDelayed(new Runnable() {
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
        try {
            if(!hidden){
				superCategoriesAdapter.notifyDataSetChanged();
                if(!activity.isOrderJustCompleted()) {
                    activity.setAddressTextToLocationPlaceHolder();
                }
				activity.fragmentUISetup(this);
				if(activity.getCartChangedAtCheckout()){
					activity.updateItemListFromSPDB();
					activity.updateCartValuesGetTotalPrice();
				}
				activity.setCartChangedAtCheckout(false);
				activity.getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(activity.isRefreshCart() || activity.refreshCart2){
							activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);
						}
						activity.setRefreshCart(false);
						activity.refreshCart2 = false;
					}
				}, 300);
			}

            if(relativeLayoutNoMenus.getVisibility() == View.VISIBLE){
				activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
				activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
				activity.llCheckoutBarSetVisibilityDirect(View.GONE);
			}
        } catch (Exception e) {
        }
    }


    public void getSuperCategoriesAPI(boolean showDialog) {
        try {
            if(MyApplication.getInstance().isOnline()) {
                ProgressDialog progressDialog = null;
                if(showDialog) {
                    progressDialog = DialogPopup.showLoadingDialogNewInstance(activity, activity.getResources().getString(R.string.loading));
                }
                final ProgressDialog finalProgressDialog = progressDialog;

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getFreshClientId());
                params.put(Constants.INTERATED, "1");

                params.put(Constants.KEY_VENDOR_ID, String.valueOf(activity.getLastCartVendorId()));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFreshApiService().getSuperCategories(params, new Callback<SuperCategoriesData>() {
                    @Override
                    public void success(final SuperCategoriesData superCategoriesData, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        if(finalProgressDialog != null){
                            finalProgressDialog.dismiss();
                        }
                        try {
                            if(superCategoriesData.getFlag() == ApiResponseFlags.FRESH_NOT_AVAILABLE.getOrdinal()){
                                oSnapNotAvailableCase(superCategoriesData.getMessage());
                            } else if(superCategoriesData.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                if(activity.getCartCityId() == -1){
                                    activity.setCartCityId(superCategoriesData.getDeliveryInfo().getCityId());
                                }

                                if(!activity.checkForCityChange(superCategoriesData.getDeliveryInfo().getCityId(),
                                        new FreshActivity.CityChangeCallback() {
                                            @Override
                                            public void onYesClick() {
                                                setSuperCategoriesDataToUI(superCategoriesData);
                                            }

                                            @Override
                                            public void onNoClick() {

                                            }
                                        })){
                                    setSuperCategoriesDataToUI(superCategoriesData);
                                }

                            } else {
                                DialogPopup.alertPopup(activity, "", superCategoriesData.getMessage());
                                stopOhSnap();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogSuperCategoriesAPI(DialogErrorType.SERVER_ERROR);
                            stopOhSnap();
                        }
                        swipeContainer.setRefreshing(false);
                        checkPendingTransaction();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(finalProgressDialog != null){
                            finalProgressDialog.dismiss();
                        }
                        retryDialogSuperCategoriesAPI(DialogErrorType.CONNECTION_LOST);
                        stopOhSnap();
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
            else {
                retryDialogSuperCategoriesAPI(DialogErrorType.NO_NET);
                swipeContainer.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            swipeContainer.setRefreshing(false);
        }
    }


    private void setSuperCategoriesDataToUI(SuperCategoriesData superCategoriesData){
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.setSuperCategoriesData(superCategoriesData);
        activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);

        activity.updateCartValuesGetTotalPrice();
        stopOhSnap();
        rvFreshSuper.smoothScrollToPosition(0);
        superCategoriesAdapter.setList(superCategoriesData.getSuperCategories(), superCategoriesData.getAds());
    }


    private void retryDialogSuperCategoriesAPI(DialogErrorType dialogErrorType){
        swipeContainer.setVisibility(View.VISIBLE);
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getSuperCategoriesAPI(true);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                },Data.getCurrentIciciUpiTransaction(activity.getAppType())==null);
    }

    public void oSnapNotAvailableCase(String message){
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
        relativeLayoutNoMenus.setVisibility(View.VISIBLE);
        textViewNothingFound.setText(!TextUtils.isEmpty(message) ? message : getString(R.string.nothing_found_near_you));
        activity.llCheckoutBarSetVisibilityDirect(View.GONE);
        activity.setTitleAlignment(false);
    }

    public void stopOhSnap(){
        relativeLayoutNoMenus.setVisibility(View.GONE);
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
        activity.llCheckoutBarSetVisibilityDirect(View.VISIBLE);
        activity.setTitleAlignment(false);
    }

    @Override
    public void onRefresh() {
        getSuperCategoriesAPI(false);
    }

    private boolean resumed = false;
    @Override
    public void onResume() {
        super.onResume();
        if(!isHidden() && resumed) {
            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);
        }
        resumed = true;
    }

    public RecyclerView getRvFreshSuper(){
        return rvFreshSuper;
    }

    private void checkPendingTransaction() {

        if(Data.getCurrentIciciUpiTransaction(activity.getAppType())!=null){
            activity.setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(activity.getAppType()));
            ApiCurrentStatusIciciUpi.checkIciciPaymentStatusApi(activity, false, new ApiCurrentStatusIciciUpi.ApiCurrentStatusListener() {
                @Override
                public void onGoToCheckout(IciciPaymentOrderStatus iciciPaymentOrderStatus) {
                    activity.openCart(AppConstant.ApplicationType.FRESH, true);
                }
            }, AppConstant.ApplicationType.FRESH);
        }

    }
}
