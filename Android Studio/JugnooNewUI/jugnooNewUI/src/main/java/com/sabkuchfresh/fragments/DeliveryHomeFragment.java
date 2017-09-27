package com.sabkuchfresh.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.widgets.DeliveryDisplayCategoriesView;

import product.clicklabs.jugnoo.R;


public class DeliveryHomeFragment extends Fragment{

    private FreshActivity activity;

    public DeliveryHomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FreshActivity) {
            activity = (FreshActivity) context;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      activity.fragmentUISetup(this);


        View rootView = inflater.inflate(R.layout.fragment_delivery_home, container, false);
        DeliveryDisplayCategoriesView deliveryDisplayCategoriesView = new DeliveryDisplayCategoriesView(activity, rootView.findViewById(R.id.rLCategoryDropDown), null);
        deliveryDisplayCategoriesView.setCategories(null);



        return rootView;
    }


}
