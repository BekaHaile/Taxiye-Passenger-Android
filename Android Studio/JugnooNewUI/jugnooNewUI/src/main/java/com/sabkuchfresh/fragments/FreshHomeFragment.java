package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.AppConstant;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by ankit on 19/01/17.
 */

public class FreshHomeFragment extends Fragment {

    private View root;
    private RelativeLayout relative;
    private FreshActivity activity;
    private ImageView ivFresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_fresh_home, container, false);
        relative = (RelativeLayout) root.findViewById(R.id.relative);
        try {
            activity = (FreshActivity) getActivity();
            if (relative != null) {
                new ASSL(activity, relative, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.getTopBar().searchLayout.setVisibility(View.GONE);

        ivFresh = (ImageView) root.findViewById(R.id.ivFresh);
        activity.fragmentUISetup(this);
        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);
        ivFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().addFreshFragment(activity, activity.getRelativeLayoutContainer());
            }
        });


        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            activity.fragmentUISetup(this);
        }
    }
}
