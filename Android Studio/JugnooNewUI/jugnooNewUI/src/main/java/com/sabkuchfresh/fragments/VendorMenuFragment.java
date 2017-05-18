package com.sabkuchfresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.DisplayOffersAdapter;
import com.sabkuchfresh.adapters.MenusCategoryFragmentsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.SortSelection;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.bus.UpdateMainList;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;


public class VendorMenuFragment extends Fragment implements PagerSlidingTabStrip.MyTabClickListener, GAAction {

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
    private Animation starCloseAnim;
    private Animation starOpenAnim;
    private Handler handler;
    private Runnable startEnableStateRunnable;
    private RecyclerView recyclerViewOffers;
    private TextView tvOfferTitle;
    private View viewPromoTitle;
    private ImageButton ibArrow;

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
        mainLayout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
        activity = (FreshActivity) getActivity();
        viewPromoTitle = rootView.findViewById(R.id.layout_offer_title);
        //ifHasAnyOffers

        RelativeLayout rlSelectedStore = (RelativeLayout) rootView.findViewById(R.id.rlSelectedStore);
        rlSelectedStore.setVisibility(View.GONE);

        mBus = (activity).getBus();
        activity.setSwipeAvailable(false);

        activity.fragmentUISetup(this);
        activity.appBarLayout.setExpanded(true);
        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GAUtils.trackScreenView(MENUS+RESTAURANT_HOME);


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
                    GAUtils.event(GAAction.MENUS, GAAction.RESTAURANT_HOME , GAAction.TABS + GAAction.SLIDED);
//                    FlurryEventLogger.eventGA(FlurryEventNames.INTERACTIONS, FlurryEventNames.CATEGORY_CHANGE, FlurryEventNames.SWIPE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        try {
            ((SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container)).setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        activity.tvCollapRestaurantDeliveryTime.setText("");
        activity.clearRestaurantRatingStars(activity.llCollapRatingStars, activity.tvCollapRestaurantRating);

        activity.setSortingList(this);

        success(activity.getMenuProductsResponse());


        return rootView;
    }

    private void setUpPromoDisplayView(String promoText,String TandC) {
        recyclerViewOffers = (RecyclerView) rootView.findViewById(R.id.recycler_view_offers);
        tvOfferTitle = (TextView)  viewPromoTitle.findViewById(R.id.tv_offer_title);
        ibArrow = (ImageButton) viewPromoTitle.findViewById(R.id.ib_arrow);
        View.OnClickListener expandClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewOffers.getVisibility()==View.VISIBLE)
                {
                  /*  if( starCloseAnim ==null) {
                        starCloseAnim = AnimationUtils.loadAnimation(activity, R.anim.recycler_offer_close_anim);
                    }
                    //                    recyclerViewOffers.startAnimation(starCloseAnim);*/
                    recyclerViewOffers.setVisibility(View.GONE);
                    ibArrow.animate().rotationBy(-180).translationYBy(-10).setDuration(0).start();


                } else{

                 /*   if(starOpenAnim ==null) {
                        starOpenAnim = AnimationUtils.loadAnimation(activity, R.anim.recycler_offer_open_anim);
                    }
                    //                    recyclerViewOffers.startAnimation(starOpenAnim);*/

                    recyclerViewOffers.setVisibility(View.VISIBLE);
                    ibArrow.animate().rotationBy(180).translationYBy(10).setDuration(0).start();
                }

                if(handler ==null){
                    handler = activity.getHandler();

                }
                if(startEnableStateRunnable ==null){
                    startEnableStateRunnable = new Runnable() {
                        @Override
                        public void run() {
                            viewPromoTitle.findViewById(R.id.tv_offer_title).setEnabled(true);
                            viewPromoTitle.findViewById(R.id.ib_arrow).setEnabled(true);
                        }
                    };

                  GAUtils.event(GAAction.MENUS, GAAction.RESTAURANT_HOME , GAAction.OFFER + GAAction.EXPANDED);

                }


                viewPromoTitle.findViewById(R.id.tv_offer_title).setEnabled(false);
                viewPromoTitle.findViewById(R.id.ib_arrow).setEnabled(false);
                handler.postDelayed(startEnableStateRunnable,activity.getResources().getInteger(R.integer.anim_time_promo_recycler));

            }
        };

        tvOfferTitle.setOnClickListener(expandClickListener);
        ibArrow.setOnClickListener(expandClickListener);
        recyclerViewOffers.setAdapter(new DisplayOffersAdapter(activity,true,TandC));
        recyclerViewOffers.setLayoutManager(new LinearLayoutManager(activity));
   /*     String heading = "FLAT 20% OFF";
        String subHeading="ON FIRST ORDER";*/
        viewPromoTitle.setVisibility(View.VISIBLE);
        tvOfferTitle.setVisibility(View.VISIBLE);
        tvOfferTitle.setText(setUpOfferTitle(promoText,null));
        rootView.findViewById(R.id.ivShadowBelowOffer).setVisibility(View.VISIBLE);


    }

    private SpannableString setUpOfferTitle(String heading, String subHeading) {
        if(TextUtils.isEmpty(heading))
            return null;

        tvOfferTitle.setTypeface(Fonts.mavenMedium(activity), Typeface.NORMAL);
        SpannableString spannableContent ;

        if(subHeading==null || subHeading.trim().length()<1)
            spannableContent=new SpannableString(heading);
        else
            spannableContent= new SpannableString(heading + "\n" + subHeading);

        spannableContent.setSpan(new StyleSpan(Typeface.BOLD),0,heading.length(),0);
        spannableContent.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.color_orange_menus)), 0, heading.length(), 0);
        if(subHeading!=null && subHeading.trim().length()>0)
          spannableContent.setSpan(new RelativeSizeSpan(0.6f), spannableContent.length()-subHeading.length(),spannableContent.length(), 0);
        return spannableContent;

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
        try {
            activity.setSwipeAvailable(true);
            if (!hidden) {
				activity.fragmentUISetup(this);
				menusCategoryFragmentsAdapter.notifyDataSetChanged();
				tabs.notifyDataSetChanged();
				activity.resumeMethod();
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (activity.isRefreshCart()) {

							}
                            activity.setRefreshCart(false);
                            if(!activity.isOrderJustCompleted()) {
								activity.setMinOrderAmountText(VendorMenuFragment.this);
							}
                        } catch (Exception e) {
                        }
                    }
                }, 200);
			}
			else{
				if(recyclerViewOffers!=null && recyclerViewOffers.getVisibility()==View.VISIBLE)
				{
					ibArrow.performClick();
				}
			}
        } catch (Exception e) {
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
                if(event.isVegToggle){
					menusCategoryFragmentsAdapter.filterCategoriesAccIsVeg(activity.getMenuProductsResponse().getCategories());
				}
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (int i = 0; i < viewPager.getChildCount(); i++) {
                    Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + i);
                    if (page != null) {
                        ((MenusCategoryItemsFragment) page).updateDetail(event.isVegToggle);
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
    }


    public MenusCategoryFragmentsAdapter getMenusCategoryFragmentsAdapter() {
        return menusCategoryFragmentsAdapter;
    }

    void success(VendorMenuResponse productsResponse) {
        try {
            noFreshsView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            activity.setMenuProductsResponse(productsResponse);

            if (activity.getMenuProductsResponse() != null) {

                if(activity.getMenuProductsResponse().getCategories()!=null){
                    tabs.setVisibility(View.VISIBLE);
                    ivShadowAboveTab.setVisibility(View.VISIBLE);
                    ivShadowBelowTab.setVisibility(View.VISIBLE);

                    activity.updateItemListFromSPDB();
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

                    if(activity.getMenuProductsResponse().getMenusPromotionInfo()!=null && activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText()!=null) {
                        setUpPromoDisplayView(activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText(),activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoTC());
                    }

                    setUpCollapseToolbarData();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void setUpCollapseToolbarData() {
        if (activity.getVendorOpened() != null) {
            activity.tvCollapRestaurantName.setText(activity.getVendorOpened().getName().toUpperCase());

            // TODO: 18/05/17 remove this
            int isVegToggle = Prefs.with(activity).getInt(Constants.KEY_SP_IS_VEG_TOGGLE, 0);
            activity.tvCollapRestaurantName.setTextColor(ContextCompat.getColor(activity, isVegToggle == 1 ? R.color.text_green_color : R.color.white));

            if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                Picasso.with(activity).load(activity.getVendorOpened().getImage())
                        .placeholder(R.drawable.ic_fresh_item_placeholder)
                        .into(activity.ivCollapseRestImage);
            } else {
                activity.ivCollapseRestImage.setImageDrawable(null);
            }

            int visibility = activity.setVendorDeliveryTimeAndDrawableColorToTextView(activity.getVendorOpened(), activity.tvCollapRestaurantDeliveryTime, R.color.white);
			activity.tvCollapRestaurantDeliveryTime.setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);

            if (activity.getVendorOpened().getRating() != null && activity.getVendorOpened().getRating() >= 1d) {
                activity.llCollapRatingStars.setVisibility(View.VISIBLE);
                activity.setRestaurantRatingStarsToLL(activity.llCollapRatingStars, activity.tvCollapRestaurantRating, activity.getVendorOpened().getRating());
            } else {
                activity.llCollapRatingStars.setVisibility(View.GONE);
            }
        }
    }

}
