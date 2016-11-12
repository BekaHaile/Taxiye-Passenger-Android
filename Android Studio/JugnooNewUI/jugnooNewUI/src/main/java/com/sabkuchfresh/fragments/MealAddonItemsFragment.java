package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.Utils;
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
public class MealAddonItemsFragment extends Fragment implements FlurryEventNames, MealAdapter.Callback {
    private final String TAG = MealAddonItemsFragment.class.getSimpleName();

    private RelativeLayout linearLayoutRoot;
    private AddOnItemsAdapter addOnItemsAdapter;
    private NonScrollListView listViewAddonItems;
    private RelativeLayout relativeLayoutProceed;
    private TextView textViewProceed;

    private RelativeLayout relativeLayoutCartTop;
    private TextView textViewCartItems, textViewCartTotalUndiscount, textViewCartTotal;
    private ImageView imageViewCartArrow, imageViewDeleteCart, imageViewCartSep;
    private LinearLayout linearLayoutCartDetails, linearLayoutCartExpansion;
    private NonScrollListView listViewCart;
    private FreshCartItemsAdapter freshCartItemsAdapter;

    private Bus mBus;

    private View rootView;
    private FreshActivity activity;

    private ArrayList<SubItem> mealsAddonData = new ArrayList<>();
    private int addOnSelectedCount = 0;


    public MealAddonItemsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addon_items, container, false);

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

        updateAddonsListCount();


        ViewGroup header = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.list_item_addon_header, listViewAddonItems, false);
        header.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 124));
        ASSL.DoMagic(header);
        listViewAddonItems.addHeaderView(header, null, false);
        ((TextView)header.findViewById(R.id.textViewCompleteMeal)).setTypeface(Fonts.mavenMedium(activity));

        addOnItemsAdapter = new AddOnItemsAdapter(activity, mealsAddonData, this);
        listViewAddonItems.setAdapter(addOnItemsAdapter);

        relativeLayoutProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openCheckoutMergedFragment(activity, activity.getRelativeLayoutContainer());
            }
        });

        setSkipOnCLickListener();



        updateCartItemsList();

        relativeLayoutCartTop = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCartTop);
        textViewCartItems = (TextView) rootView.findViewById(R.id.textViewCartItems); textViewCartItems.setTypeface(Fonts.mavenMedium(activity));
        textViewCartTotalUndiscount = (TextView) rootView.findViewById(R.id.textViewCartTotalUndiscount);
        textViewCartTotalUndiscount.setVisibility(View.GONE);
        textViewCartTotal = (TextView) rootView.findViewById(R.id.textViewCartTotal); textViewCartTotal.setTypeface(Fonts.mavenMedium(activity));
        imageViewCartArrow = (ImageView) rootView.findViewById(R.id.imageViewCartArrow);
        imageViewDeleteCart = (ImageView) rootView.findViewById(R.id.imageViewDeleteCart);
        imageViewCartSep = (ImageView) rootView.findViewById(R.id.imageViewCartSep);
        imageViewCartSep.setVisibility(View.GONE);
        linearLayoutCartExpansion = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartExpansion);
        linearLayoutCartDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartDetails);
        linearLayoutCartDetails.setVisibility(View.GONE);
        listViewCart = (NonScrollListView) rootView.findViewById(R.id.listViewCart);
        freshCartItemsAdapter = new FreshCartItemsAdapter(activity, activity.subItemsInCart, FlurryEventNames.REVIEW_CART, false,
                new FreshCartItemsAdapter.Callback() {
                    @Override
                    public void onPlusClicked(int position, SubItem subItem) {
                        updateCartDataView();
                        addOnItemsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onMinusClicked(int position, SubItem subItem) {
                        updateCartDataView();
                        if(subItem.getSubItemQuantitySelected() == 0){
                            activity.subItemsInCart.remove(position);
                            checkIfEmpty();
                        }
                        addOnItemsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public boolean checkForMinus(int position, SubItem subItem) {
                        return activity.checkForMinus(position, subItem);
                    }

                    @Override
                    public void minusNotDone(int position, SubItem subItem) {
                        activity.clearMealsCartIfNoMainMeal();
                        addOnItemsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public PromoCoupon getSelectedCoupon() {
                        return null;
                    }

                    @Override
                    public void removeCoupon() {

                    }
                });
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
                }
            }
        });

        imageViewDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteCart();
            }
        });

        updateCartDataView();

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            setSkipOnCLickListener();
            updateCartDataView();
            updateCartItemsList();
            updateAddonsListCount();
        }
    }


    private void setSkipOnCLickListener(){
        activity.getTopBar().textViewSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutProceed.performClick();
            }
        });
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
        updateCartTopBarView(activity.updateCartValuesGetTotalPrice());
        addOnSelectedCount++;
        updateBottomBar();
        updateCartItemsList();
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        updateCartTopBarView(activity.updateCartValuesGetTotalPrice());
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

    private void updateCartTopBarView(Pair<Double, Integer> pair){
        textViewCartItems.setText(activity.getString(R.string.cart_items_format, String.valueOf(pair.second)));
        textViewCartTotal.setText(activity.getString(R.string.rupees_value_format_without_space,
                Utils.getMoneyDecimalFormatWithoutFloat().format(pair.first)));
    }

    private void updateCartDataView(){
        try {
            updateCartTopBarView(activity.updateCartValuesGetTotalPrice());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIfEmpty(){
        if(activity.subItemsInCart.size() == 0){
            activity.performBackPressed();
        }
    }


    private void updateCartItemsList(){
        if(activity.subItemsInCart == null) {
            activity.subItemsInCart = new ArrayList<>();
        }
        activity.subItemsInCart.clear();
        if(activity.getProductsResponse() != null
                && activity.getProductsResponse().getCategories() != null) {
            for (Category category : activity.getProductsResponse().getCategories()) {
                for (SubItem subItem : category.getSubItems()) {
                    if (subItem.getSubItemQuantitySelected() > 0) {
                        activity.subItemsInCart.add(subItem);
                    }
                }
            }
        }
        if(freshCartItemsAdapter != null){
            freshCartItemsAdapter.notifyDataSetChanged();
        }
    }

    public void deleteCart() {
        for(SubItem subItem : activity.subItemsInCart){
            subItem.setSubItemQuantitySelected(0);
        }
        updateCartDataView();
        activity.subItemsInCart.clear();
        freshCartItemsAdapter.notifyDataSetChanged();
        checkIfEmpty();

    }

    private void updateAddonsListCount(){
        try {
            addOnSelectedCount = 0;
            mealsAddonData.clear();
            mealsAddonData.addAll(activity.getProductsResponse().getCategories().get(1).getSubItems());
            for(SubItem subItem : mealsAddonData){
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
