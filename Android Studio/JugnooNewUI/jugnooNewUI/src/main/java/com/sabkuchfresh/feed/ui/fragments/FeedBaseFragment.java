package com.sabkuchfresh.feed.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabkuchfresh.home.FreshActivity;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public class FeedBaseFragment extends Fragment {

    public FreshActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (FreshActivity) context;
        activity.fragmentUISetup(this);
    }


}
