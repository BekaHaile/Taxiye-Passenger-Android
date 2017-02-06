package com.sabkuchfresh.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.MenusCategoryFragmentsAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.utils.CustomTypeFaceSpan;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;


public class VendorMenuFragment extends Fragment implements PagerSlidingTabStrip.MyTabClickListener {

    private final String TAG = VendorMenuFragment.class.getSimpleName();
    private LinearLayout llRoot;
    private LinearLayout mainLayout;
    private LinearLayout noFreshsView;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private ImageView ivShadowBelowTab, ivShadowAboveTab;
    private MenusCategoryFragmentsAdapter menusCategoryFragmentsAdapter;
    private View rootView;
    private FreshActivity activity;
    private boolean tabClickFlag = false;

    public VendorMenuFragment() {
    }

    protected Bus mBus;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh, container, false);

        activity = (FreshActivity) getActivity();
        mBus = (activity).getBus();
        activity.setSwipeAvailable(false);

        activity.fragmentUISetup(this);
        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        menusCategoryFragmentsAdapter = new MenusCategoryFragmentsAdapter(activity, getChildFragmentManager());
        viewPager.setAdapter(menusCategoryFragmentsAdapter);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.text_color_dark_1, R.color.text_color);
        tabs.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        tabs.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        tabs.setOnMyTabClickListener(this);
        ivShadowBelowTab = (ImageView) rootView.findViewById(R.id.ivShadowBelowTab);
        ivShadowAboveTab = (ImageView) rootView.findViewById(R.id.ivShadowAboveTab);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (tabClickFlag) {
                    tabClickFlag = false;
                } else {
                    Log.d(TAG, "onPageSelected = " + position);
                    FlurryEventLogger.event(FlurryEventNames.INTERACTIONS, FlurryEventNames.CATEGORY_CHANGE, FlurryEventNames.SWIPE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        success(activity.getMenuProductsResponse());

        activity.setSortingList(this);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            activity.setRefreshCart(false);

        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        activity.setSwipeAvailable(true);
        if (!hidden) {
            menusCategoryFragmentsAdapter.notifyDataSetChanged();
            tabs.notifyDataSetChanged();
            activity.collapseToolbar();
            activity.fragmentUISetup(this);
            activity.resumeMethod();
            if (activity.isRefreshCart()) {

            }
            activity.setRefreshCart(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(llRoot);
        System.gc();
    }


    @Subscribe
    public void onUpdateListEvent(UpdateMainList event) {

        if (event.flag) {
            // Update pager adapter

            try {
                for (int i = 0; i < viewPager.getChildCount(); i++) {
                    Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + i);
                    if (page != null) {
                        ((MenusCategoryItemsFragment) page).updateDetail();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                menusCategoryFragmentsAdapter.notifyDataSetChanged();
                tabs.notifyDataSetChanged();
                activity.updateCartValuesGetTotalPrice();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Subscribe
    public void onSwipe(SwipeCheckout swipe) {
        if (swipe.flag == 0) {

        }
    }

    @Override
    public void onTabClicked(int position) {
        Log.d(TAG, "onTabClicked = " + position);
        tabClickFlag = true;
        FlurryEventLogger.event(FlurryEventNames.INTERACTIONS, FlurryEventNames.CATEGORY_CHANGE, FlurryEventNames.TAP);
    }


    public MenusCategoryFragmentsAdapter getMenusCategoryFragmentsAdapter() {
        return menusCategoryFragmentsAdapter;
    }

    void success(VendorMenuResponse productsResponse) {
        try {
            noFreshsView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            activity.setMenuProductsResponse(productsResponse);

            if (activity.getMenuProductsResponse() != null
                    && activity.getMenuProductsResponse().getCategories() != null) {
                tabs.setVisibility(View.VISIBLE);
                ivShadowAboveTab.setVisibility(View.VISIBLE);
                ivShadowBelowTab.setVisibility(View.VISIBLE);

                activity.updateCartFromSP();
                activity.updateCartValuesGetTotalPrice();
                menusCategoryFragmentsAdapter.setCategories(activity.getMenuProductsResponse().getCategories());
                tabs.setViewPager(viewPager);
                viewPager.setCurrentItem(Data.tabLinkIndex);
                Data.tabLinkIndex = 0;
                tabs.setBackgroundColor(activity.getResources().getColor(R.color.white_light_grey));
                tabs.notifyDataSetChanged();

                if (activity.updateCart) {
                    activity.updateCart = false;
                    activity.openCart();
                }
                activity.getBus().post(new SortSelection(activity.menusSort));


                if (activity.getVendorOpened() != null) {
                    activity.tvCollapRestaurantName.setText(activity.getVendorOpened().getName().toUpperCase());
                    Picasso.with(activity).load(activity.getVendorOpened().getImage()).fit().into(activity.ivCollapseRestImage);

                    activity.setVendorDeliveryTimeToTextView(activity.getVendorOpened(), activity.tvCollapRestaurantDeliveryTime);
                    setTextViewDrawableColor(activity.tvCollapRestaurantDeliveryTime, ContextCompat.getColor(activity, R.color.white));

                    if (activity.getVendorOpened().getRating() != null) {
                        activity.tvCollapRestaurantRating.setVisibility(View.VISIBLE);

                        Spannable spannable = new SpannableString(activity.getString(R.string.star_icon) + " " + activity.getVendorOpened().getRating() + "  " + "(" + activity.getVendorOpened().getReviewCount() + ")");
                        Typeface star = Typeface.createFromAsset(activity.getAssets(), "fonts/icomoon.ttf");
                        spannable.setSpan(new CustomTypeFaceSpan("", star), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        activity.tvCollapRestaurantRating.setText(spannable);
                        int ratingColor;
                        if (activity.getVendorOpened().getColorCode() != null
                                && activity.getVendorOpened().getColorCode().startsWith("#")
                                && activity.getVendorOpened().getColorCode().length() == 7)
                            ratingColor = Color.parseColor(activity.getVendorOpened().getColorCode());
                        else
                            ratingColor = Color.parseColor("#8dd061"); //default Green Color

                        setTextViewBackgroundDrawableColor(activity.tvCollapRestaurantRating, ratingColor);
                    } else {
                        activity.tvCollapRestaurantRating.setVisibility(View.GONE);
                    }


                    activity.ivCollapseRestImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.openRestaurantFragment();
                        }
                    });


                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void setTextViewBackgroundDrawableColor(TextView textView, int color) {
        if(textView.getBackground() != null){
            textView.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }
}
