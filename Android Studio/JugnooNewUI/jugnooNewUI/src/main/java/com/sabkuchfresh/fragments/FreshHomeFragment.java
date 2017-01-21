package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sabkuchfresh.adapters.FreshSuperCategoriesAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.AppConstant;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 19/01/17.
 */

public class FreshHomeFragment extends Fragment {

    private View rootView;
    private RelativeLayout relative;
    private FreshActivity activity;
    RecyclerView rvFreshSuper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_home, container, false);
        relative = (RelativeLayout) rootView.findViewById(R.id.relative);
        try {
            activity = (FreshActivity) getActivity();
            if (relative != null) {
                new ASSL(activity, relative, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.fragmentUISetup(this);
        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FRESH);

        rvFreshSuper = (RecyclerView) rootView.findViewById(R.id.rvFreshSuper);
        rvFreshSuper.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.HORIZONTAL, false);
        rvFreshSuper.setLayoutManager(gridLayoutManager);
        FreshSuperCategoriesAdapter adapter = new FreshSuperCategoriesAdapter(activity, new FreshSuperCategoriesAdapter.Callback() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(activity, "Position =" + pos, Toast.LENGTH_SHORT).show();
                activity.getTransactionUtils().addFreshFragment(activity, activity.getRelativeLayoutContainer());
            }
        });

        rvFreshSuper.setAdapter(adapter);


        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            activity.fragmentUISetup(this);
        }
    }
}
