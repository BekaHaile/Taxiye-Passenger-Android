package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.AddOnItemsAdapter;
import com.sabkuchfresh.adapters.MealAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 10/10/16.
 */
public class MealAddonItemsFragment extends Fragment implements FlurryEventNames, MealAdapter.Callback {
    private final String TAG = MealAddonItemsFragment.class.getSimpleName();

    private RelativeLayout linearLayoutRoot;
    private AddOnItemsAdapter addOnItemsAdapter;
    private RecyclerView recyclerViewCategoryItems;
    private RelativeLayout relativeLayoutProceed;
    private TextView textViewProceed;

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

        recyclerViewCategoryItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewCategoryItems);
        recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCategoryItems.setHasFixedSize(false);

        relativeLayoutProceed = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutProceed);
        textViewProceed = (TextView) rootView.findViewById(R.id.textViewProceed); textViewProceed.setTypeface(Fonts.mavenMedium(activity));

        try {
            mealsAddonData.clear();
            mealsAddonData.addAll(activity.getProductsResponse().getCategories().get(1).getSubItems());
            for(SubItem subItem : mealsAddonData){
                addOnSelectedCount = addOnSelectedCount + subItem.getSubItemQuantitySelected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateBottomBar();

        addOnItemsAdapter = new AddOnItemsAdapter(activity, mealsAddonData, this);
        recyclerViewCategoryItems.setAdapter(addOnItemsAdapter);

        relativeLayoutProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openCartFragment(activity, activity.getRelativeLayoutContainer());
            }
        });


        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            addOnItemsAdapter.notifyDataSetChanged();
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
        activity.updateCartValuesGetTotalPrice();
        addOnSelectedCount++;
        updateBottomBar();
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        activity.updateCartValuesGetTotalPrice();
        addOnSelectedCount--;
        updateBottomBar();
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
}
