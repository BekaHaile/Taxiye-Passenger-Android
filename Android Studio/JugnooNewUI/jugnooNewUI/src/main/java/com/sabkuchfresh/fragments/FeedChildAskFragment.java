package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public class FeedChildAskFragment extends ImageSelectFragment {


    private EditText etContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_feed_review, container, false);
        activity = ((FreshActivity) getActivity());
        etContent = (EditText) rootView.findViewById(R.id.etContent);
        etContent.setHint(R.string.looking_for_something);
        displayImagesRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_photos);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        return rootView;


    }


    public static FeedChildAskFragment newInstance() {
        return new FeedChildAskFragment();
    }

    @Override
    public boolean canSubmit() {
        if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
            Toast.makeText(activity, R.string.please_enter_something, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    @Override
    public String getText() {
        return etContent.getText().toString().trim();
    }

    @Override
    public Integer getRestaurantId() {
        return FeedAddPostFragment.NOT_APPLICABLE;
    }

    @Override
    protected Integer getScore() {
        return FeedAddPostFragment.NOT_APPLICABLE;
    }

    @Override
    public boolean cameraEnableState() {
        return (imageSelected == null || imageSelected.size() < 5);

    }
}
