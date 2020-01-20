package com.sabkuchfresh.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.AddOnItemsAdapter;
import com.sabkuchfresh.adapters.FreshCartItemsAdapter;
import com.sabkuchfresh.adapters.MealAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;

/**
 * Created by shankar on 10/10/16.
 */
public class MealAddonItemsFragment extends Fragment implements GACategory, GAAction, MealAdapter.Callback {
    private final String TAG = "Meals Addon Screen";

    private RelativeLayout linearLayoutRoot;
    private AddOnItemsAdapter addOnItemsAdapter;
    private NonScrollListView listViewAddonItems;
    private RelativeLayout relativeLayoutProceed;
    private TextView textViewProceed;

    private RelativeLayout relativeLayoutCartTop;
    private TextView textViewCartItems, textViewCartTotalUndiscount;
    private ImageView imageViewCartArrow, imageViewDeleteCart, imageViewCartSep;
    private LinearLayout linearLayoutCartExpansion;
    private NonScrollListView listViewCart, listViewCharges;
    private FreshCartItemsAdapter freshCartItemsAdapter;

    private Bus mBus;

    private View rootView;
    private FreshActivity activity;

    private ArrayList<SubItem> mealsAddonSubItems = new ArrayList<>();
    private int addOnSelectedCount = 0;
    public ArrayList<SubItem> subItemsInCart;


    public MealAddonItemsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addon_items, container, false);

        GAUtils.trackScreenView(MEALS+ADD_ONS);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        mBus = activity.getBus();

        linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listViewAddonItems = (NonScrollListView) rootView.findViewById(R.id.listViewAddonItems);

        relativeLayoutProceed = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutProceed);
        textViewProceed = (TextView) rootView.findViewById(R.id.textViewProceed); textViewProceed.setTypeface(Fonts.mavenMedium(activity));
        ViewGroup.LayoutParams layoutParams = relativeLayoutProceed.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_54);
        relativeLayoutProceed.setLayoutParams(layoutParams);

        updateAddonsListCount();


        ViewGroup header = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.list_item_addon_header, listViewAddonItems, false);
        header.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 124));
        ASSL.DoMagic(header);
        listViewAddonItems.addHeaderView(header, null, false);
        ((TextView)header.findViewById(R.id.textViewCompleteMeal)).setTypeface(Fonts.mavenMedium(activity));

        addOnItemsAdapter = new AddOnItemsAdapter(activity, mealsAddonSubItems, this, activity.getResources().getString(R.string.default_currency), activity.getResources().getString(R.string.default_currency));
        listViewAddonItems.setAdapter(addOnItemsAdapter);

        relativeLayoutProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openCheckoutMergedFragment(activity, activity.getRelativeLayoutContainer());
                GAUtils.event(activity.getGaCategory(), ADD_ONS, SKIP_AND_PROCEED);
            }
        });

        updateCartItemsList();

        relativeLayoutCartTop = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCartTop);
        textViewCartItems = (TextView) rootView.findViewById(R.id.textViewCartItems); textViewCartItems.setTypeface(Fonts.mavenMedium(activity));
        textViewCartTotalUndiscount = (TextView) rootView.findViewById(R.id.textViewCartTotalUndiscount);
        textViewCartTotalUndiscount.setVisibility(View.GONE);
        imageViewCartArrow = (ImageView) rootView.findViewById(R.id.imageViewCartArrow);
        imageViewDeleteCart = (ImageView) rootView.findViewById(R.id.imageViewDeleteCart);
        imageViewCartSep = (ImageView) rootView.findViewById(R.id.imageViewCartSep);
        imageViewCartSep.setVisibility(View.GONE);
        linearLayoutCartExpansion = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartExpansion);
        listViewCharges = (NonScrollListView) rootView.findViewById(R.id.listViewCharges);
        listViewCharges.setVisibility(View.GONE);
        listViewCart = (NonScrollListView) rootView.findViewById(R.id.listViewCart);
        freshCartItemsAdapter = new FreshCartItemsAdapter(activity, subItemsInCart, false,
                new FreshCartItemsAdapter.Callback() {
                    @Override
                    public void onPlusClicked(int position, SubItem subItem) {
                        activity.saveSubItemToDeliveryStoreCart(subItem);
                        activity.updateItemListFromSPDB();
                        updateCartDataView();
                        updateAddonsListCount();
                        GAUtils.event(activity.getGaCategory(), ADD_ONS, CART+EXISTING+ITEM+INCREASED);
                    }

                    @Override
                    public void onMinusClicked(int position, SubItem subItem) {
                        activity.saveSubItemToDeliveryStoreCart(subItem);
                        if(subItem.getSubItemQuantitySelected() == 0){
                            subItemsInCart.remove(position);
                        }
                        activity.updateItemListFromSPDB();
                        updateCartDataView();
                        updateAddonsListCount();
                        checkIfEmpty();
                        GAUtils.event(activity.getGaCategory(), ADD_ONS, CART+EXISTING+ITEM+DECREASED);
                    }

                    @Override
                    public boolean checkForMinus(int position, SubItem subItem) {
                        return activity.checkForMinus(position, subItem);
                    }

                    @Override
                    public void minusNotDone(int position, SubItem subItem) {
                        activity.clearMealsCartIfNoMainMeal();
                        updateAddonsListCount();
                    }

                    @Override
                    public void deleteStarSubscription() {
                        freshCartItemsAdapter.setResults(subItemsInCart, null, activity.getResources().getString(R.string.default_currency), activity.getResources().getString(R.string.default_currency));
                    }

                    @Override
                    public PromoCoupon getSelectedCoupon() {
                        return null;
                    }

                    @Override
                    public void removeCoupon() {

                    }
                },this, activity.getResources().getString(R.string.default_currency), activity.getResources().getString(R.string.default_currency));
        listViewCart.setAdapter(freshCartItemsAdapter);

        linearLayoutCartExpansion.setVisibility(View.GONE);
        imageViewDeleteCart.setVisibility(View.GONE);
        imageViewCartArrow.setRotation(180f);


        relativeLayoutCartTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutCartExpansion.getVisibility() == View.VISIBLE){
                    linearLayoutCartExpansion.setVisibility(View.GONE);
                    imageViewDeleteCart.setVisibility(View.GONE);
                    imageViewCartArrow.setRotation(180f);
                } else {
                    linearLayoutCartExpansion.setVisibility(View.VISIBLE);
                    imageViewDeleteCart.setVisibility(View.VISIBLE);
                    imageViewCartArrow.setRotation(0f);
                    GAUtils.event(activity.getGaCategory(), ADD_ONS, CART+VIEW_EXPANDED);
                }
            }
        });

        imageViewDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteCart();
                GAUtils.event(activity.getGaCategory(), ADD_ONS, CART+EMPTIED);
            }
        });

        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCartDataView();
            }
        }, 100);


        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);

            if(activity.getCartChangedAtCheckout()){
                activity.updateItemListFromSPDB();
                freshCartItemsAdapter.notifyDataSetChanged();
                addOnItemsAdapter.notifyDataSetChanged();
                activity.updateCartValuesGetTotalPrice();
            }
            activity.setCartChangedAtCheckout(false);
            updateCartDataView();
            updateCartItemsList();
            updateAddonsListCount();
        }
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
    public void onSlotSelected(int position, SubItem slot) {

    }

    @Override
    public void onPlusClicked(int position, SubItem subItem) {
        activity.saveSubItemToDeliveryStoreCart(subItem);
        updateCartTopBarView(activity.updateCartValuesGetTotalPriceFMG(subItem));
        addOnSelectedCount++;
        updateBottomBar();
        updateCartItemsList();
        GAUtils.event(activity.getGaCategory(), ADD_ONS, ITEM+ADDED_TO_CART);
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        activity.saveSubItemToDeliveryStoreCart(subItem);
        updateCartTopBarView(activity.updateCartValuesGetTotalPriceFMG(subItem));
        addOnSelectedCount--;
        updateBottomBar();
        updateCartItemsList();
    }

    private void updateBottomBar(){
        textViewProceed.setText((addOnSelectedCount > 0) ? R.string.proceed : R.string.skip_and_proceed);
    }

    @Override
    public boolean checkForMinus(int position, SubItem subItem) {
        return true;
    }

    @Override
    public void minusNotDone(int position, SubItem subItem) {

    }

    @Override
    public boolean onLikeClicked(SubItem subItem, int pos) {
        return false;
    }

    @Override
    public boolean canAddItem(final SubItem subItem, final MealAdapter.CallbackCheckForAdd callbackCheckForAdd) {
        return false;
    }

    @Override
    public boolean isMealsCartEmpty() {
        return false;
    }


    private void updateCartTopBarView(Pair<Double, Integer> pair){
        textViewCartItems.setText(activity.getString(R.string.cart_items_format, String.valueOf(pair.second)));
    }

    private void updateCartDataView(){
        try {
            updateCartTopBarView(activity.updateCartValuesGetTotalPrice());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIfEmpty(){
        if(subItemsInCart.size() == 0){
            activity.performBackPressed(false);
        }
    }


    private void updateCartItemsList(){
        subItemsInCart = activity.fetchCartList();
        if(freshCartItemsAdapter != null){
            freshCartItemsAdapter.setResults(subItemsInCart, null, activity.getResources().getString(R.string.default_currency), activity.getResources().getString(R.string.default_currency));
        }
    }

    public void deleteCart() {
        for(SubItem subItem : subItemsInCart){
            subItem.setSubItemQuantitySelected(0);
            activity.saveSubItemToDeliveryStoreCart(subItem);
        }
        activity.updateItemListFromSPDB();
        updateCartDataView();
        subItemsInCart.clear();
        activity.setCartChangedAtCheckout(true);
        freshCartItemsAdapter.notifyDataSetChanged();
        checkIfEmpty();
    }

    private void updateAddonsListCount(){
        try {
            addOnSelectedCount = 0;
            mealsAddonSubItems.clear();
            mealsAddonSubItems.addAll(activity.getProductsResponse().getCategories().get(1).getSubItems());
            for(SubItem subItem : mealsAddonSubItems){
                addOnSelectedCount = addOnSelectedCount + subItem.getSubItemQuantitySelected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateBottomBar();
        if(addOnItemsAdapter != null){
            addOnItemsAdapter.notifyDataSetChanged();
        }
    }
}
