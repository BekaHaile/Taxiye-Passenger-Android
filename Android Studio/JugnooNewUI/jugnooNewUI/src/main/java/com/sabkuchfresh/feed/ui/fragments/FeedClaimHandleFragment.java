package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.feed.ui.textwatchers.HandleTextWatcher;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public final class FeedClaimHandleFragment extends FeedBaseFragment implements GAAction {

    @Bind(R.id.edt_claim_handle)
    EditText edtClaimHandle;
    @Bind(R.id.btn_reserve_spot)
    Button btnReserveSpot;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.tvClaimHandle)
    TextView tvClaimHandle;
    @Bind(R.id.sv)
    ScrollView sv;
    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_claim_handle, container, false);
        ButterKnife.bind(this, rootView);
        edtClaimHandle.setEnabled(false);
        edtClaimHandle.addTextChangedListener(new HandleTextWatcher() {
            @Override
            public void enableSubmitButton(boolean isEnable) {
                btnReserveSpot.setEnabled(isEnable);
            }

            @Override
            public void afterTextChange() {
                if (edtClaimHandle.isSelected()) {
                    edtClaimHandle.setSelected(false);
                    tvError.setVisibility(View.INVISIBLE);
                }
            }
        });

        tvClaimHandle.setText(activity.getString(R.string.feed_claim_handle_description_format, Data.getFeedName(activity)));

        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(llMain, null, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                sv.fullScroll(View.FOCUS_DOWN);
                activity.getFabViewTest().relativeLayoutFABTest.setVisibility(View.GONE);
               activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);

            }

            @Override
            public void keyBoardClosed() {
                activity.getFabViewTest().relativeLayoutFABTest.setVisibility(View.VISIBLE);
                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);



            }
        });
        llMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        GAUtils.trackScreenView(FEED+HANDLE_INPUT);
        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_reserve_spot)
    public void onClick() {

        if(edtClaimHandle.getText().toString().trim().length()<=0){
            Toast.makeText(activity, "Please enter a handle name.", Toast.LENGTH_SHORT).show();
            return;
        }


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
                    Data.getFeedData().setHasHandle(1);


                    edtClaimHandle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_done_green_vector,0);
                    edtClaimHandle.setActivated(true);
                    tvError.setVisibility(View.INVISIBLE);

                    activity.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.performSuperBackPress();
                            activity.addFeedFragment();

                        }
                    },1000);

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
