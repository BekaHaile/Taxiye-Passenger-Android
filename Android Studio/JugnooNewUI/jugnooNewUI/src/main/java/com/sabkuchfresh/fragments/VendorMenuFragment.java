package com.sabkuchfresh.fragments;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CompoundButton;
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
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
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

    private TextView tvSwitchVegToggle;
    private SwitchCompat switchVegToggle;

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
        tvOfferTitle = (TextView)  viewPromoTitle.findViewById(R.id.tv_offer_title);
        ibArrow = (ImageButton) viewPromoTitle.findViewById(R.id.ib_arrow);
        ibArrow.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(activity, R.color.theme_color), PorterDuff.Mode.SRC_IN));
        //ifHasAnyOffers

        RelativeLayout rlSelectedStore = (RelativeLayout) rootView.findViewById(R.id.rlSelectedStore);
        rlSelectedStore.setVisibility(View.GONE);

        mBus = (activity).getBus();

        activity.fragmentUISetup(this);
        llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
        try {
            if (llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GAUtils.trackScreenView(activity.getGaCategory()+RESTAURANT_HOME);
        GAUtils.trackScreenView(activity.getGaCategory()+RESTAURANT_HOME+V2);


        noFreshsView = (LinearLayout) rootView.findViewById(R.id.noFreshsView);

        viewPromoTitle.setVisibility(View.VISIBLE);
        tvSwitchVegToggle = (TextView) viewPromoTitle.findViewById(R.id.tvSwitchVegToggle);
        switchVegToggle = (SwitchCompat) viewPromoTitle.findViewById(R.id.switchVegToggle);

        int isVegToggle = Prefs.with(activity).getInt(Constants.KEY_SP_IS_VEG_TOGGLE, 0);
        switchVegToggle.setChecked(isVegToggle == 1);

        switchVegToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int isVegToggle = Prefs.with(activity).getInt(Constants.KEY_SP_IS_VEG_TOGGLE, 0);
                Prefs.with(activity).save(Constants.KEY_SP_IS_VEG_TOGGLE,
                        isVegToggle == 0 ? 1 : 0);
                onUpdateListEvent(new UpdateMainList(true, true));
            }
        });

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
                    GAUtils.event(activity.getGaCategory(), GAAction.RESTAURANT_HOME , GAAction.TABS + GAAction.SLIDED);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        try {
            rootView.findViewById(R.id.swipe_container).setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        activity.tvCollapRestaurantDeliveryTime.setText("");
        activity.clearRestaurantRatingStars(activity.llCollapRatingStars, activity.tvCollapRestaurantRating, null);

        activity.setSortingList(this);

        success();

        // check if we have category / subCategory coming in arguments, if yes we need to switch tabs accordingly
        if(getArguments()!=null){
            int categoryId = -1,subCategoryId = -1;
            if(getArguments().containsKey(Constants.ITEM_CATEGORY_ID)){
                categoryId = getArguments().getInt(Constants.ITEM_CATEGORY_ID);
            }
            if(getArguments().containsKey(Constants.ITEM_SUB_CATEGORY_ID)){
                subCategoryId = getArguments().getInt(Constants.ITEM_SUB_CATEGORY_ID);
            }

            if(menusCategoryFragmentsAdapter!=null && categoryId!= -1){
                int tabPosition = menusCategoryFragmentsAdapter.getCategoryPosition(categoryId);
                viewPager.setCurrentItem(tabPosition);

                if(subCategoryId!=-1){
                    MenusCategoryItemsFragment itemsFragment = (MenusCategoryItemsFragment) getChildFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.viewPager + ":" +tabPosition );
                }
            }


        }


        return rootView;
    }

    private void setUpPromoDisplayView(final String promoText, final String TandC) {
        recyclerViewOffers = (RecyclerView) rootView.findViewById(R.id.recycler_view_offers);
        View.OnClickListener expandClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(recyclerViewOffers.getVisibility()==View.VISIBLE) {
//                    recyclerViewOffers.setVisibility(View.GONE);
//                    ibArrow.animate().rotationBy(-180).translationYBy(-10).setDuration(0).start();
//                } else{
//                    recyclerViewOffers.setVisibility(View.VISIBLE);
//                    ibArrow.animate().rotationBy(180).translationYBy(10).setDuration(0).start();
//                }
//
//                if(handler == null){
//                    handler = activity.getHandler();
//                }
//                if(startEnableStateRunnable ==null){
//                    startEnableStateRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            tvOfferTitle.setEnabled(true);
//                            ibArrow.setEnabled(true);
//                        }
//                    };
//                    GAUtils.event(GAAction.MENUS, GAAction.RESTAURANT_HOME , GAAction.OFFER + GAAction.EXPANDED);
//                }
//                tvOfferTitle.setEnabled(false);
//                ibArrow.setEnabled(false);
//                handler.postDelayed(startEnableStateRunnable,activity.getResources().getInteger(R.integer.anim_time_promo_recycler));
				DialogPopup.alertPopupLeftOriented(activity, promoText, TandC, true, true, true, true,
						R.color.theme_color, 16, 13, Fonts.mavenMedium(activity));
            }
        };

        tvOfferTitle.setOnClickListener(expandClickListener);
        ibArrow.setOnClickListener(expandClickListener);
        recyclerViewOffers.setAdapter(new DisplayOffersAdapter(activity,true,TandC));
        recyclerViewOffers.setLayoutManager(new LinearLayoutManager(activity));
        tvOfferTitle.setVisibility(View.VISIBLE);
        ibArrow.setVisibility(View.GONE);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
				activity.fragmentUISetup(this);
				menusCategoryFragmentsAdapter.notifyDataSetChanged();
				tabs.notifyDataSetChanged();
				activity.resumeMethod();
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if(!activity.isOrderJustCompleted()){
                                activity.setMinOrderAmountText(VendorMenuFragment.this);
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 200);
                if(activity.getScrollToCategoryId() != -1){
                    int positionInPager = menusCategoryFragmentsAdapter.getCategoryPosition(activity.getScrollToCategoryId());
                    if(positionInPager > -1) {
                        viewPager.setCurrentItem(positionInPager);
                    }
                    activity.setScrollToCategoryId(-1);
                }
			} else {
				if(recyclerViewOffers!=null && recyclerViewOffers.getVisibility()==View.VISIBLE) {
					ibArrow.performClick();
				}
			}
        } catch (Exception e) {}
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
                    // fix for category switching bug when previous category is a complete non-veg category
                    // now we return to the last selected category on toggle
                    int previousCatId = menusCategoryFragmentsAdapter.getCategories().get(viewPager.getCurrentItem()).getCategoryId();
					int currentPos = menusCategoryFragmentsAdapter.filterCategoriesAccIsVeg(activity.getMenuProductsResponse().getCategories(), previousCatId);
                    tabs.notifyDataSetChanged();
                    if(currentPos > -1) {
                        viewPager.setCurrentItem(currentPos);
                    }
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

    void success() {
        try {
            noFreshsView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            if (activity.getMenuProductsResponse() != null) {

                if(activity.getMenuProductsResponse().getCategories()!=null){
                    tabs.setVisibility(View.VISIBLE);
                    ivShadowAboveTab.setVisibility(View.VISIBLE);
                    ivShadowBelowTab.setVisibility(View.VISIBLE);

                    menusCategoryFragmentsAdapter.filterCategoriesAccIsVeg(activity.getMenuProductsResponse().getCategories(), -1);
                    tabs.setViewPager(viewPager);
                    viewPager.setCurrentItem(Data.tabLinkIndex);
                    Data.tabLinkIndex = 0;
                    tabs.setBackgroundColor(activity.getResources().getColor(R.color.white_light_grey));
                    tabs.notifyDataSetChanged();

                    if (activity.updateCart) {
                        activity.updateCart = false;
                        activity.openCart();
                    }
                    if(activity.menusSort == -1){
                        activity.menusSort = 0;
                    }
                    activity.getBus().post(new SortSelection(activity.menusSort));

                    if(activity.getMenuProductsResponse().getMenusPromotionInfo()!=null && activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText()!=null) {
                        setUpPromoDisplayView(activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoText(),activity.getMenuProductsResponse().getMenusPromotionInfo().getPromoTC());
                    } else {
                        tvOfferTitle.setVisibility(View.GONE);
                        ibArrow.setVisibility(View.GONE);
                        rootView.findViewById(R.id.ivShadowBelowOffer).setVisibility(View.VISIBLE);
                    }

                    if(activity.collapsingToolBarEnabled(this)){
                        setUpCollapseToolbarData();

                    }

                    tvSwitchVegToggle.setVisibility(activity.getVendorOpened().getPureVegetarian() == 1 ? View.GONE : View.VISIBLE);
                    switchVegToggle.setVisibility(activity.getVendorOpened().getPureVegetarian() == 1 ? View.GONE : View.VISIBLE);

                    if(switchVegToggle.getVisibility() == View.GONE && tvOfferTitle.getVisibility() == View.GONE){
                        viewPromoTitle.setVisibility(View.GONE);
                        rootView.findViewById(R.id.ivShadowBelowOffer).setVisibility(View.GONE);
                    }
                    activity.setMinOrderAmountText(VendorMenuFragment.this);
                    activity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if(Prefs.with(activity).getInt(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK, -1) > 0){
                                activity.openRestaurantReviewsListFragment();
                            }
                        }
                    });
                    activity.getSlots().get(activity.menusSort).setCheck(true);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void setUpCollapseToolbarData() {
        if (activity.getVendorOpened() != null) {
            activity.tvCollapRestaurantName.setVisibility(View.VISIBLE);
            activity.llCollapseRating.setVisibility(View.VISIBLE);
            activity.tvCollapRestaurantName.setText(activity.getVendorOpened().getName().toUpperCase());

            if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                Picasso.with(activity).load(activity.getVendorOpened().getImage()).
                        fit().centerCrop().placeholder(R.drawable.ic_fresh_item_placeholder)
                        .into(activity.ivCollapseRestImage);
            } else {
                activity.ivCollapseRestImage.setImageDrawable(null);
            }

            int visibility = activity.setVendorDeliveryTimeAndDrawableColorToTextView(activity.getVendorOpened(), activity.tvCollapRestaurantDeliveryTime, R.color.white, true);
			activity.tvCollapRestaurantDeliveryTime.setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);

            if (activity.getVendorOpened().getRating() != null && activity.getVendorOpened().getRating() >= 1d) {
                activity.llCollapRatingStars.setVisibility(View.VISIBLE);
                activity.setRestaurantRatingStarsToLL(activity.llCollapRatingStars, activity.tvCollapRestaurantRating,
                        activity.getVendorOpened().getRating(),
                        R.drawable.ic_half_star_green_white, R.drawable.ic_star_white, null, 0);
            } else {
                activity.llCollapRatingStars.setVisibility(View.INVISIBLE);
            }

        }
    }

}
