package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 1/30/17.
 */

public class NewFeedbackFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_rate_experience, container, false);
        return rootView;
    }
}
