package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.DeliveryStoresAdapter;
import com.sabkuchfresh.datastructure.Stores;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.Store;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 17/03/17.
 */

public class DeliveryStoresFragment extends Fragment {

    private View rootView;
    private FreshActivity activity;
    private RelativeLayout llRoot;
    private RecyclerView rvDeliveryStores;
    private DeliveryStoresAdapter deliveryStoresAdapter;
    private ArrayList<Stores.DeliveryStore> stores = new ArrayList<>();

    public static DeliveryStoresFragment newInstance() {
        Bundle args = new Bundle();
        DeliveryStoresFragment fragment = new DeliveryStoresFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_delivery_stores, container, false);
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        llRoot = (RelativeLayout) rootView.findViewById(R.id.llRoot);
        try {
            if(llRoot != null) {
                new ASSL(activity, llRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rvDeliveryStores = (RecyclerView) rootView.findViewById(R.id.rvDeliveryStores);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDeliveryStores.setLayoutManager(layoutManager);
        rvDeliveryStores.setItemAnimator(new DefaultItemAnimator());
        rvDeliveryStores.setHasFixedSize(false);

        for(int i=0; i<3; i++){
            Stores.DeliveryStore store = new Stores.DeliveryStore();
            store.setVendorName("Exo Fresh");
            if(i == 0) {
                store.setIsSelected(1);
            }
            stores.add(store);
        }

        deliveryStoresAdapter = new DeliveryStoresAdapter(activity, stores, rvDeliveryStores, new DeliveryStoresAdapter.Callback() {
            @Override
            public void onStoreSelected(int position, Slot slot) {

            }
        });

        rvDeliveryStores.setAdapter(deliveryStoresAdapter);

        return rootView;
    }
}
