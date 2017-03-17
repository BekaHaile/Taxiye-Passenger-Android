package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 17/03/17.
 */

public class DeliveryStoresFragment extends Fragment {

    private View rootView;
    private FreshActivity activity;
    private RelativeLayout llRoot;

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

        return rootView;
    }
}
