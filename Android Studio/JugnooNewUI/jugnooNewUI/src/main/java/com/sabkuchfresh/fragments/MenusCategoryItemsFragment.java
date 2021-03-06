package com.sabkuchfresh.fragments;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sabkuchfresh.adapters.MenusCategoryItemsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.SwipeCheckout;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;


@SuppressLint("ValidFragment")
public class MenusCategoryItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout llRoot;

    private RecyclerView recyclerViewCategoryItems;
    private MenusCategoryItemsAdapter menusCategoryItemsAdapter;

    private View rootView;
    private FreshActivity activity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView noMealsView;
    private LinearLayoutManager linearLayoutManager;


    protected Bus mBus;

    public static MenusCategoryItemsFragment newInstance(int position, int isVendorMenu) {
        MenusCategoryItemsFragment frag = new MenusCategoryItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_CATEGORY_POSITION, position);
        bundle.putInt(Constants.KEY_IS_VENDOR_MENU, isVendorMenu);
        frag.setArguments(bundle);
        return frag;
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

    /**
     * Scrolls to direct vendor sub category / item position
     */
    public void scrollToDirectVendorSearchIndex(){
        if(menusCategoryItemsAdapter!=null){

            int scrollToPos = -1;

            if(menusCategoryItemsAdapter.getVendorDirectSearchItemIndex()!=0){
               scrollToPos = menusCategoryItemsAdapter.getVendorDirectSearchItemIndex();
            }else if(menusCategoryItemsAdapter.getVendorDirectSearchSubCatIndex()!=0){
                scrollToPos = menusCategoryItemsAdapter.getVendorDirectSearchSubCatIndex();
            }
            if(scrollToPos!=-1){
                linearLayoutManager.scrollToPositionWithOffset(scrollToPos,0);
                final int finalScrollToPos = scrollToPos;
                recyclerViewCategoryItems.post(new Runnable() {
                    @Override
                    public void run() {

                        final View view = linearLayoutManager.findViewByPosition(finalScrollToPos);
                        final String backgroundColorProperty = "backgroundColor";
                        // highlight the row
                        ObjectAnimator animFadeIn = ObjectAnimator.ofObject(view, backgroundColorProperty,
                                new ArgbEvaluator(), Color.WHITE, ContextCompat.getColor(activity,R.color.grey_light_more));
                        animFadeIn.setDuration(800);
                        animFadeIn.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(final Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(final Animator animation) {

                                ObjectAnimator animFadeOut = ObjectAnimator.ofObject(view, backgroundColorProperty,
                                        new ArgbEvaluator(), ContextCompat.getColor(activity,R.color.grey_light_more)
                                        ,Color.WHITE);
                                animFadeOut.setDuration(800);
                                animFadeOut.start();
                            }

                            @Override
                            public void onAnimationCancel(final Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(final Animator animation) {

                            }
                        });
                        animFadeIn.start();
                    }
                });

            }

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);

        try {
            Bundle bundle = getArguments();
            if (bundle.containsKey(Constants.KEY_CATEGORY_POSITION)) {
                int position = bundle.getInt(Constants.KEY_CATEGORY_POSITION);

                activity = (FreshActivity) getActivity();
                mBus = (activity).getBus();
                llRoot = (LinearLayout) rootView.findViewById(R.id.llRoot);
                try {
                    if (llRoot != null) {
                        new ASSL(activity, llRoot, 1134, 720, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                noMealsView = (ImageView) rootView.findViewById(R.id.noMealsView);
                noMealsView.setVisibility(View.GONE);

                recyclerViewCategoryItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
                linearLayoutManager  = new LinearLayoutManager(activity);
                recyclerViewCategoryItems.setLayoutManager(linearLayoutManager);
                recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
                recyclerViewCategoryItems.setHasFixedSize(false);
                mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
                mSwipeRefreshLayout.setOnRefreshListener(this);
                mSwipeRefreshLayout.setColorSchemeResources(R.color.white);
                mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.grey_icon_color);
                mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

                int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                if (activity.isMenusOrDeliveryOpen()) {
                    mSwipeRefreshLayout.setEnabled(false);
                } else {
                    mSwipeRefreshLayout.setEnabled(true);
                }

                menusCategoryItemsAdapter = new MenusCategoryItemsAdapter(activity, position,
                        activity.getMenuProductsResponse().getCategories().get(position),
                        new MenusCategoryItemsAdapter.Callback() {
                            @Override
                            public boolean checkForAdd(int position, Item item, MenusCategoryItemsAdapter.CallbackCheckForAdd callbackCheckForAdd) {
                                return activity.checkForAdd(position, item, callbackCheckForAdd);
                            }

                            @Override
                            public void onPlusClicked(int position, Item item, boolean isNewItemAdded) {
                                //This method is only called when item is not customisable
                                if (activity.getTotalPrice() <= 0) {
                                    activity.saveDeliveryAddressModel();
                                }
                                activity.updateCartValuesGetTotalPrice();
                                if (activity.isMenusOrDeliveryOpen()) {
                                    if (isNewItemAdded)
                                        GAUtils.event(activity.getGaCategory(), GAAction.RESTAURANT_HOME, GAAction.ITEM + GAAction.ADDED);
                                    else
                                        GAUtils.event(activity.getGaCategory(), GAAction.RESTAURANT_HOME, GAAction.ITEM + GAAction.INCREASED);
                                }

                            }

                            @Override
                            public void onMinusClicked(int position, Item item) {
                                activity.updateCartValuesGetTotalPrice();
                                if (activity.isMenusOrDeliveryOpen()) {

                                    GAUtils.event(activity.getGaCategory(), GAAction.RESTAURANT_HOME, GAAction.ITEM + GAAction.DECREASED);
                                }
                            }

                            @Override
                            public void onMinusFailed(int position, Item item) {
                                DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                                        activity.getString(R.string.you_have_to_decrease_quantity_from_checkout),
                                        activity.getString(R.string.view_cart), activity.getString(R.string.cancel),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                activity.getLlCheckoutBar().performClick();
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        }, true, false);
                            }

                            @Override
                            public MenusResponse.Vendor getVendorOpened() {
                                return activity.getVendorOpened();
                            }

                            @Override
                            public void onCategoryClick(Category category) {

                            }
                        },activity.getMenuProductsResponse().getCurrencyCode(), activity.getMenuProductsResponse().getCurrency(), new MenusCategoryItemsAdapter.ItemAdded() {
                    @Override
                    public void onItemAdded(int position) {
                        scrollToDirectVendorSearchIndex();
                    }

                    @Override
                    public void onItemFound(final int position, final int itemId) {
                        recyclerViewCategoryItems.scrollToPosition(position);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setSelectedItem(itemId);
                            }
                        }, 200);
                    }
                });
                recyclerViewCategoryItems.setAdapter(menusCategoryItemsAdapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(llRoot);
        System.gc();
    }

    /**
     * Method used to update list data
     */
    public void updateDetail(boolean setData) {
        if(setData){
            menusCategoryItemsAdapter.setSubItems(true);
        } else {
            menusCategoryItemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        mBus.post(new SwipeCheckout(0));
    }

    @Subscribe
    public void updateSwipe(SwipeCheckout swipe) {
        if (swipe.flag == 1) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setSelectedItem(int itemId) {
        if (menusCategoryItemsAdapter != null) {
            menusCategoryItemsAdapter.setSelectedItemId(itemId);
        }
    }
}
