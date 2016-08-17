package com.sabkuchfresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.StoreListingAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Fonts;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;

/**
 * Created by gurmail on 13/07/16.
 */
public class HomeFragment extends Fragment {


    private View rootView;
    private FreshActivity activity;
//    private Button fatafat, meals;
    private LinearLayout linearLayoutRoot;
    private RecyclerView recyclerViewStore;
    private StoreListingAdapter listingAdapter;
    private TextView toptextview;
//    private ArrayList<Store> stores;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if(linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toptextview = (TextView) rootView.findViewById(R.id.toptextview);
        toptextview.setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);

//        stores = new ArrayList<>();
//
//        Store store = new Store();
//        store.setTitle("FATAFAT");
//        store.setDescription("ABCLKhadf");
//
//        Store store1 = new Store();
//        store1.setTitle("MEALS");
//        store1.setDescription("ABCLKhadf");
//        stores.add(store);
//        stores.add(store1);


//        fatafat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addFreshFragment();
//            }
//        });
//
//        meals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        recyclerViewStore = (RecyclerView) rootView.findViewById(R.id.store_list);
        recyclerViewStore.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewStore.setHasFixedSize(false);


        listingAdapter = new StoreListingAdapter(activity, Data.userData.getFatafatUserData().stores, new StoreListingAdapter.Callback() {
            @Override
            public void onSlotSelected(int storeId) {
                if(storeId == AppConstant.ApplicationType.FRESH) {
                    addFreshFragment();
                } else {
                    addMealFragment();
                }
            }
        });

        recyclerViewStore.setAdapter(listingAdapter);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    public void addFreshFragment() {
        activity.addFreshFragment1(this, false);
    }

    public void addMealFragment() {
        activity.addMealFragment(this, false);
    }
}
