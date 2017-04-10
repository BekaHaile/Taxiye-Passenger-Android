package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public final class FeedClaimHandleFragment extends FeedBaseFragment {

    @Bind(R.id.edt_claim_handle)
    EditText edtClaimHandle;
    @Bind(R.id.btn_reserve_spot)
    Button btnReserveSpot;
    @Bind(R.id.tv_error)
    TextView tvError;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_claim_handle, container, false);
        ButterKnife.bind(this, rootView);
        edtClaimHandle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtClaimHandle.isSelected()) {
                    edtClaimHandle.setSelected(false);
                    tvError.setVisibility(View.INVISIBLE);
                }

            }
        });

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_reserve_spot)
    public void onClick() {

        if(edtClaimHandle.getText().toString().trim().length()>0){
            HashMap<String,String> params = new HashMap<>();
            params.put(Constants.KEY_HANDLE,edtClaimHandle.getText().toString().trim());
            new ApiCommon<FeedCommonResponse>(activity).putAccessToken(true).execute(params, ApiName.SET_HANDLE_API, new APICommonCallback<FeedCommonResponse>() {
                @Override
                public boolean onNotConnected() {
                    return false;
                }

                @Override
                public boolean onException(Exception e) {
                    return false;
                }

                @Override
                public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {
                    Utils.hideKeyboard(activity);
                    Data.getFeedData().setShowCreateHandle(1);
                    activity.performSuperBackPress();
                    activity.addFeedFragment();
                }

                @Override
                public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                    tvError.setText(message);
                    tvError.setVisibility(View.VISIBLE);
                    edtClaimHandle.setSelected(true);
                    return true;
                }

                @Override
                public boolean onFailure(RetrofitError error) {
                    return false;
                }

                @Override
                public void onNegativeClick() {

                }
            });

        }


    }
}
