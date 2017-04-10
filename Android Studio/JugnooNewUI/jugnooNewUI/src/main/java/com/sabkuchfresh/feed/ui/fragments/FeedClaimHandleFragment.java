package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public final class FeedClaimHandleFragment extends FeedBaseFragment {

    @Bind(R.id.edt_claim_handle)
    EditText edtClaimHandle;
    @Bind(R.id.til_claim_hanlde)
    TextInputLayout tilClaimHanlde;
    @Bind(R.id.btn_reserve_spot)
    Button btnReserveSpot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_claim_handle, container, false);
        ButterKnife.bind(this, rootView);
        tilClaimHanlde.setError("Dsa");
        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
