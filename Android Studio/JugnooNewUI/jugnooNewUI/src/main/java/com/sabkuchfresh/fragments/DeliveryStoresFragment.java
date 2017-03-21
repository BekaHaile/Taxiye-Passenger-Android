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
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.DeliveryStoresAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.DeliveryStore;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;

/**
 * Created by ankit on 17/03/17.
 */

public class DeliveryStoresFragment extends Fragment {

    private View rootView;
    private FreshActivity activity;
    private RelativeLayout llRoot;
    private RecyclerView rvDeliveryStores;
    private DeliveryStoresAdapter deliveryStoresAdapter;

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


        deliveryStoresAdapter = new DeliveryStoresAdapter(activity, activity.getProductsResponse().getDeliveryStores(),
                rvDeliveryStores, new DeliveryStoresAdapter.Callback() {
            @Override
            public void onStoreSelected(final int position, final DeliveryStore deliveryStore) {

                if(activity.getOpenedVendorId() != 0
                        && !deliveryStore.getVendorId().equals(activity.getOpenedVendorId())
                        && activity.getCart().getCartItems(activity.getOpenedVendorId()).size() > 0) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                            activity.getString(R.string.you_have_selected_cart_from_this_vendor, activity.getOpenedVendorName()),
                            activity.getString(R.string.checkout),
                            activity.getString(R.string.change_store),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.openCart(activity.getAppType());
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    TODO clear cart for previous selected activity.getCart().getDeliveryStoreCart(activity.getOpenedVendorId()).getSubItemHashMap().clear();
                                    selectStore(position, deliveryStore);
                                }
                            }, false, false);
                } else if(!deliveryStore.getVendorId().equals(activity.getOpenedVendorId())) {
                    selectStore(position, deliveryStore);
                }
            }
        });


        rvDeliveryStores.setAdapter(deliveryStoresAdapter);

        return rootView;
    }


    private void selectStore(int position, final DeliveryStore deliveryStore){
        for(int i=0; i<activity.getProductsResponse().getDeliveryStores().size(); i++){
            activity.getProductsResponse().getDeliveryStores().get(i).setIsSelected(0);
        }
        activity.getProductsResponse().getDeliveryStores().get(position).setIsSelected(1);
        deliveryStoresAdapter.notifyDataSetChanged();

        activity.setOpenedVendorIdName(deliveryStore.getVendorId(), deliveryStore.getVendorName());
        activity.setRefreshCart(true);
        activity.performBackPressed(false);
    }

}
