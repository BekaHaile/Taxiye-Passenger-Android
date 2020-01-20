package com.sabkuchfresh.feed.ui.fragments;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sabkuchfresh.home.FreshActivity;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public  class FeedBaseFragment extends Fragment {

    public FreshActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (FreshActivity) context;
        activity.fragmentUISetup(this);
    }



}
