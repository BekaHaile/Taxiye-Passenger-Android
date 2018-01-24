package com.sabkuchfresh.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by cl-macmini-01 on 1/23/18.
 */

public class SuggestStoreFragment extends Fragment {

    @Bind(R.id.edtBusinessName)
    EditText edtBusinessName;
    @Bind(R.id.tvAddress)
    TextView tvAddress;
    @Bind(R.id.spCategory)
    Spinner spCategory;
    @Bind(R.id.edtPhone)
    EditText edtPhone;
    @Bind(R.id.edtTimings)
    EditText edtTimings;
    @Bind(R.id.cvUploadImages)
    CardView cvUploadImages;
    @Bind(R.id.cvImages)
    CardView cvImages;
    @Bind(R.id.ivUploadImage)
    ImageView ivUploadImage;
    @Bind(R.id.rvImages)
    RecyclerView rvImages;
    @Bind(R.id.edtNotes)
    EditText edtNotes;
    @Bind(R.id.cvNotes)
    CardView cvNotes;
    @Bind(R.id.svSuggestStore)
    ScrollView svSuggestStore;
    @Bind(R.id.llMain)
    LinearLayout llMain;

    private FreshActivity mActivity;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggest_store, container, false);
        mActivity.fragmentUISetup(this);
        ButterKnife.bind(this, rootView);

        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {

            @Override
            public void keyboardOpened() {
                if (!mActivity.isDeliveryOpenInBackground()) {
                    mActivity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.GONE);
                }
                mActivity.llPayViewContainer.setVisibility(View.GONE);

            }

            @Override
            public void keyBoardClosed() {
                if (!mActivity.isDeliveryOpenInBackground()) {
                    if (Prefs.with(mActivity).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1) {
                        mActivity.getFabViewTest().setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    }
                }
                mActivity.llPayViewContainer.setVisibility(View.VISIBLE);

            }
        };
        // register for keyboard event
        mActivity.registerForKeyBoardEvent(mKeyBoardStateHandler);

        return rootView;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivity = (FreshActivity) context;
    }
}
