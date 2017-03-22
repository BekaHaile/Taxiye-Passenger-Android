package com.sabkuchfresh.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshSuperCategoriesAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

/**
 * Created by ankit on 19/01/17.
 */

public class FreshHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GAAction, GACategory{

    private View rootView;
    private LinearLayout llRoot;
    private RelativeLayout relativeLayoutNoMenus;
    private FreshActivity activity;
    private RecyclerView rvFreshSuper;
    private FreshSuperCategoriesAdapter adapter;
    private TextView textViewNothingFound;
    private SwipeRefreshLayout swipeContainer;
    private ViewPager mImageViewPager;
    private CustomPagerAdapter mCustomPagerAdapter;
    private ArrayList<String> mResources = new ArrayList<>();
//    private Scr scrollView;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.fragmentUISetup(this);
        activity.setDeliveryAddressView(rootView);

        for(int j=0; j<3; j++) {
            mResources.add("https://visitorinvictoria.ca/wp-content/uploads/2017/03/groceries.jpg");
        }
      /*  scrollView = (NestedScrollView) rootView.findViewById (R.id.nest_scrollview);
        scrollView.setFillViewport (true);*/
//        mImageViewPager = (ViewPager) rootView.findViewById(R.id.pager);
//        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabDots);
//        tabLayout.setupWithViewPager(mImageViewPager, true);
//        mCustomPagerAdapter = new CustomPagerAdapter(activity);
//
//        mImageViewPager.setAdapter(mCustomPagerAdapter);
//
//        for(int i=0; i < tabLayout.getTabCount(); i++) {
//            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
//            p.setMargins(20, 0, 0, 0);
//            tab.requestLayout();
//        }

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.white);
        swipeContainer.setProgressBackgroundColorSchemeResource(R.color.theme_color);
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
                switch (adapter.getItemViewType(position)){
                    case FreshSuperCategoriesAdapter.SINGLE_ITEM:
                        return 2;
                    case FreshSuperCategoriesAdapter.MAIN_ITEM:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        rvFreshSuper.setLayoutManager(gridLayoutManager);
        adapter = new FreshSuperCategoriesAdapter(activity, new FreshSuperCategoriesAdapter.Callback() {
            @Override
            public void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory) {
                if(superCategory.getIsEnabled() == 0){
                    Utils.showToast(activity, getString(R.string.coming_soon_to_your_city));
                } else {
                    activity.getTransactionUtils().addFreshFragment(activity, activity.getRelativeLayoutContainer(), superCategory);
                    activity.getFabViewTest().hideJeanieHelpInSession();
                }
                try {
                    GAUtils.event(FRESH, HOME+SUPER+CATEGORY+CLICKED, superCategory.getSuperCategoryName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rvFreshSuper.setAdapter(adapter);

        relativeLayoutNoMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);

        try {
            if(Data.getFreshData() != null && Data.getFreshData().pendingFeedback == 1) {
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

        GAUtils.trackScreenView(FRESH_SCREEN);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            adapter.notifyDataSetChanged();
            activity.setAddressTextToLocationPlaceHolder();
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
        }
    }


    public void getSuperCategoriesAPI(boolean showDialog) {
        try {
            if(MyApplication.getInstance().isOnline()) {
                if(showDialog) {
                    DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                }

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                params.put(Constants.KEY_CLIENT_ID, Config.getFreshClientId());
                params.put(Constants.INTERATED, "1");

                new HomeUtil().putDefaultParams(params);
                RestClient.getFreshApiService().getSuperCategories(params, new Callback<SuperCategoriesData>() {
                    @Override
                    public void success(final SuperCategoriesData superCategoriesData, Response response) {
                        DialogPopup.dismissLoadingDialog();
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
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
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
        activity.saveDeliveryAddressModel();
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.setSuperCategoriesData(superCategoriesData);
        adapter.setList(superCategoriesData.getSuperCategories());
        activity.updateCartValuesGetTotalPrice();
        stopOhSnap();
        rvFreshSuper.smoothScrollToPosition(0);
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
                });
    }

    public void oSnapNotAvailableCase(String message){
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.GONE);
        relativeLayoutNoMenus.setVisibility(View.VISIBLE);
        textViewNothingFound.setText(!TextUtils.isEmpty(message) ? message : getString(R.string.nothing_found_near_you));
    }

    public void stopOhSnap(){
        relativeLayoutNoMenus.setVisibility(View.GONE);
        activity.getTopBar().getLlSearchCartContainer().setVisibility(View.VISIBLE);
        activity.getTopBar().getLlSearchCart().setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        getSuperCategoriesAPI(false);
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_pager_promo, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.ivPromo);
            Picasso.with(mContext).load(mResources.get(position))
                    .placeholder(R.drawable.ic_fresh_new_placeholder)
                    .error(R.drawable.ic_fresh_new_placeholder)
                    .fit()
                    .into(imageView);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
