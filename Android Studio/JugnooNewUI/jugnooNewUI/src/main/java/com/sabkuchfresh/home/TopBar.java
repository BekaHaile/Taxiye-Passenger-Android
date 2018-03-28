package com.sabkuchfresh.home;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.fragments.FreshHomeFragment;
import com.sabkuchfresh.fragments.MealFragment;
import com.sabkuchfresh.fragments.MenusFragment;
import com.sabkuchfresh.utils.AppConstant;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 4/8/16.
 */
public class TopBar implements GAAction, GACategory {

    Activity activity;
    DrawerLayout drawerLayout;

    public RelativeLayout topRl;
    public ImageView imageViewMenu;
    public TextView title;

    public ImageView imageViewBack, ivDeliveryAddressCross, imageViewDelete ;
    RelativeLayout  imgVwFatafatTutorial;
    public EditText editTextDeliveryAddress;
    public TextView tvDeliveryAddress;
    public ProgressWheel progressWheelDeliveryAddressPin;

    public LinearLayout llSearchContainer;
    public EditText etSearch;
    public ProgressWheel pbSearch;
    public ImageView ivSearchCross, ivSearch, ivFilterApplied;
    public RelativeLayout rlSearch;
    public RelativeLayout rlFilter;

    private RelativeLayout llSearchCartContainer;
    private LinearLayout llSearchCart;
    private LinearLayout llTopBarDeliveryAddress;
    private Animation searchBarAnimation;
    private Animation searchBarCloseAnimation;
    public ImageView ivFreshSort;
    public ImageView ivFreshCall;

    private TextView tvTopBarDeliveryAddressLocation;
    private TextView tvAddressLayoutTitle;

    public RelativeLayout rlFreshSort;
    public RelativeLayout rlFreshCall;



    public TopBar(Activity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        setupTopBar();
    }

    private void setupTopBar() {
        topRl = (RelativeLayout) drawerLayout.findViewById(R.id.topRl);
        imageViewMenu = (ImageView) drawerLayout.findViewById(R.id.imageViewMenu);
        title = (TextView) drawerLayout.findViewById(R.id.title);
        title.setTypeface(Fonts.avenirNext(activity));

        llSearchCartContainer = (RelativeLayout) drawerLayout.findViewById(R.id.llSearchCartContainer);
        llSearchCart = (LinearLayout) drawerLayout.findViewById(R.id.llSearchCart);
        llTopBarDeliveryAddress = (LinearLayout) drawerLayout.findViewById(R.id.llTopBarDeliveryAddress);
        tvTopBarDeliveryAddressLocation = (TextView) drawerLayout.findViewById(R.id.tvTopBarDeliveryAddressLocation);
        tvAddressLayoutTitle = (TextView) drawerLayout.findViewById(R.id.tvAddressLayoutTitle);

        imageViewBack = (ImageView) drawerLayout.findViewById(R.id.imageViewBack);
        imgVwFatafatTutorial=(RelativeLayout)topRl.findViewById(R.id.ivTutorial);
        editTextDeliveryAddress = (EditText) drawerLayout.findViewById(R.id.editTextDeliveryAddress);
        editTextDeliveryAddress.setTypeface(Fonts.mavenLight(activity));
        tvDeliveryAddress = (TextView) drawerLayout.findViewById(R.id.tvDeliveryAddress);
        tvDeliveryAddress.setVisibility(View.GONE);
        ivDeliveryAddressCross = (ImageView) drawerLayout.findViewById(R.id.ivDeliveryAddressCross);
        imageViewDelete = (ImageView) drawerLayout.findViewById(R.id.imageViewDelete);
        progressWheelDeliveryAddressPin = (ProgressWheel) drawerLayout.findViewById(R.id.progressWheelDeliveryAddressPin);
        progressWheelDeliveryAddressPin.setVisibility(View.GONE);

        llSearchContainer = (LinearLayout) drawerLayout.findViewById(R.id.llSearchContainer);
        etSearch = (EditText) drawerLayout.findViewById(R.id.etSearch);
        etSearch.setTypeface(Fonts.mavenMedium(activity));
        ivSearch = (ImageView) drawerLayout.findViewById(R.id.ivSearch);
        rlSearch=(RelativeLayout)drawerLayout.findViewById(R.id.rlSearch);
        pbSearch = (ProgressWheel) drawerLayout.findViewById(R.id.pbSearch);
        rlFilter = (RelativeLayout) drawerLayout.findViewById(R.id.rlFilter);
        ivFilterApplied = (ImageView) drawerLayout.findViewById(R.id.ivFilterApplied);
        ivSearchCross = (ImageView) drawerLayout.findViewById(R.id.ivSearchCross);
        ivFilterApplied.setVisibility(View.GONE);
        ivFreshSort = (ImageView) drawerLayout.findViewById(R.id.ivFreshSort);
        ivFreshCall = (ImageView) drawerLayout.findViewById(R.id.ivFreshCall);
        rlFreshSort=(RelativeLayout)drawerLayout.findViewById(R.id.rlFreshSort);
        rlFreshCall = (RelativeLayout)drawerLayout.findViewById(R.id.rlFreshCall);
        //setSearchVisibility(View.GONE);

        topRl.setOnClickListener(topBarOnClickListener);
        imageViewMenu.setOnClickListener(topBarOnClickListener);
        //ivSearch.setOnClickListener(topBarOnClickListener);
        rlSearch.setOnClickListener(topBarOnClickListener);
        rlFilter.setOnClickListener(topBarOnClickListener);
        imageViewBack.setOnClickListener(topBarOnClickListener);
        rlFreshSort.setOnClickListener(topBarOnClickListener);
        rlFreshCall.setOnClickListener(topBarOnClickListener);
        imgVwFatafatTutorial.setOnClickListener(topBarOnClickListener);
        llTopBarDeliveryAddress.setOnClickListener(topBarOnClickListener);
        title.setOnClickListener(topBarOnClickListener);




    }

    public void animateSearchBar(boolean open) {


        if (llSearchContainer == null)
            return;


        if (open) {
            if (searchBarAnimation == null)
                searchBarAnimation = AnimationUtils.loadAnimation(activity, R.anim.search_open_anim);


            llSearchContainer.startAnimation(searchBarAnimation);
        } else {
            if (searchBarCloseAnimation == null) {
                searchBarCloseAnimation = AnimationUtils.loadAnimation(activity, R.anim.search_close_anim);
                searchBarCloseAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        llSearchContainer.setVisibility(View.GONE);
                        llSearchContainer.invalidate();
                        if (activity instanceof FreshActivity
                                && ((FreshActivity) activity).getTopFragment() != null
                                && ((FreshActivity) activity).getTopFragment() instanceof MenusFragment) {

//                            title.setAlpha(0.0f);
                            llTopBarDeliveryAddress.setAlpha(0.0f);


                            rlSearch.setAlpha(0.0f);
//                            title.setVisibility(View.VISIBLE);
                            llTopBarDeliveryAddress.setVisibility(View.VISIBLE);
                            rlSearch.setVisibility(View.VISIBLE);


                            if(activity instanceof FreshActivity && ((FreshActivity) activity).getMenusFragment().iSChildCategoryOpen()){
                                imageViewBack.setVisibility(View.VISIBLE);
                                imageViewBack.invalidate();
                                imageViewMenu.setVisibility(View.GONE);
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                            }else{
                                imageViewBack.setVisibility(View.GONE);
                                imageViewBack.invalidate();
                                imageViewMenu.setVisibility(View.VISIBLE);
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
                            }


//                            title.animate().alpha(1.0f).setDuration(200);

                            llTopBarDeliveryAddress.animate().alpha(1.0f).setDuration(200);

                            rlSearch.animate().alpha(1.0f).setDuration(200);

                            imageViewMenu.animate().alpha(1.0f).setDuration(200);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }


            llSearchContainer.startAnimation(searchBarCloseAnimation);

        }


    }


    public LinearLayout getLlSearchContainer() {
        return llSearchContainer;
    }

    public LinearLayout getLlSearchCart() {
        return llSearchCart;
    }

    public ImageView getIvSearch() {
        return ivSearch;
    }

    public RelativeLayout getRlSearch() {return rlSearch;}

    public RelativeLayout getLlSearchCartContainer() {
        return llSearchCartContainer;
    }

    public View.OnClickListener topBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.imageViewMenu:
                    drawerLayout.openDrawer(GravityCompat.START);
                    Utils.hideSoftKeyboard(activity, etSearch);
                    if(activity instanceof FreshActivity){
                        int appType = ((FreshActivity)activity).getAppType();
                        if(appType == AppConstant.ApplicationType.FRESH){
                            GAUtils.event(JUGNOO, FRESH+HOME, LEFT_MENU_ICON+CLICKED);
                        } else if(appType == AppConstant.ApplicationType.MEALS){
                            GAUtils.event(JUGNOO, MEALS+HOME, LEFT_MENU_ICON+CLICKED);
                        } else if(appType == AppConstant.ApplicationType.MENUS){
                            GAUtils.event(JUGNOO, GAAction.MENUS+HOME, LEFT_MENU_ICON+CLICKED);
                        } else if(appType == AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                            GAUtils.event(JUGNOO, GACategory.DELIVERY_CUSTOMER+HOME, LEFT_MENU_ICON+CLICKED);
                        } else if(appType == AppConstant.ApplicationType.FEED){
                            GAUtils.event(JUGNOO, FEED+HOME, LEFT_MENU_ICON+CLICKED);
                        } else if(appType == AppConstant.ApplicationType.PROS){
                            GAUtils.event(JUGNOO, PROS+HOME, LEFT_MENU_ICON+CLICKED);
                        }
                    }

                    break;



                case R.id.imageViewBack:
                    if (activity instanceof FreshActivity) {
                        ((FreshActivity) activity).performBackPressed(true);
                    }
                    break;

                case R.id.rlSearch:
                    if (activity instanceof FreshActivity) {
                        ((FreshActivity) activity).searchItem();
                    }
                    break;

                case R.id.rlFilter:
                    if (activity instanceof FreshActivity) {
                        Utils.hideSoftKeyboard(activity, etSearch);
                        ((FreshActivity) activity).openMenusFilter();
                    }
                    break;

                case R.id.rlFreshSort:
                    if(activity instanceof FreshActivity){
                        ((FreshActivity)activity).openFreshSortDialog();
                    }
                    break;

                case R.id.rlFreshCall:
                    if(activity instanceof FreshActivity){
                        ((FreshActivity)activity).callVendor();
                    }
                    break;

                case R.id.ivTutorial:
                    if(activity instanceof FreshActivity){
                        ((FreshActivity)activity).showFatafatTutorial();
                    }
                case R.id.llTopBarDeliveryAddress:
                    if(activity instanceof FreshActivity){
                        Fragment topFragment = ((FreshActivity) activity).getTopFragment();
                        if(topFragment!=null&& (topFragment instanceof MenusFragment || topFragment instanceof MealFragment || topFragment instanceof FreshHomeFragment)){
                            ((FreshActivity)activity).onChangeLocalityClick();

                        }
                    }
                    break;

                case R.id.title:
                    if(activity instanceof FreshActivity){
                        ((FreshActivity)activity).onTitleClicked();
                    }
                    break;

            }
        }
    };


    public void setSearchVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            llSearchContainer.setBackgroundResource(R.drawable.capsule_white_stroke);
        } else {
            //llSearchContainer.setBackgroundResource(R.drawable.background_transparent);
        }
    }

    public void setPBSearchVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            pbSearch.spin();
        } else {
            pbSearch.stopSpinning();
        }
        pbSearch.setVisibility(visibility);
    }


    public TextView getTvTopBarDeliveryAddressLocation() {
        return tvTopBarDeliveryAddressLocation;
    }

    public LinearLayout getLlTopBarDeliveryAddress() {
        return llTopBarDeliveryAddress;
    }

    public TextView getTvAddressLayoutTitle() {
        return tvAddressLayoutTitle;
    }
}





