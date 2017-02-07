package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.commoncalls.SendFeedbackQuery;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.CustomRatingBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.dialogs.RateAppDialog;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by Parminder Singh on 1/30/17.
 */

public class NewFeedbackFragment extends Fragment {


    @Bind(R.id.tv_view_invoice)
    TextView tvViewInvoice;
    @Bind(R.id.tv_rate_experience)
    TextView tvRateExperience;
    @Bind(R.id.rating_bar)
    CustomRatingBar ratingBar;
    @Bind(R.id.edt_feedback_title)
    EditText edtFeedbackTitle;
    @Bind(R.id.edt_feedback_descc)
    EditText edtFeedbackDescc;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    private FreshActivity activity;
    private int viewType;
    private String dateValue;
    private double orderAmount;
    private String orderId;
    private String endRideGoodFeedbackText;
    private ProductType productType;
    private ArrayList<FeedbackReason> reasons = new ArrayList<>();
    private int rateApp;
    private RateAppDialog rateAppDialog;
    private RateAppDialogContent rateAppDialogContent;
    private LinearLayout mainLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_rate_experience, container, false);
        ButterKnife.bind(this, rootView);
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);


        mainLayout = (LinearLayout) rootView.findViewById(R.id.root_layout);
        new ASSL(activity, mainLayout, 1134, 720, false);
/*

        try {
            rateApp = Data.userData.getCustomerRateAppFlag();
            rateAppDialogContent = Data.userData.getRateAppDialogContent();
            if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                viewType = Data.getMenusData().getFeedbackViewType();
                dateValue = Data.getMenusData().getFeedbackDeliveryDate();
                orderAmount = Data.getMenusData().getAmount();
                orderId = Data.getMenusData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.menus));
                endRideGoodFeedbackText = Data.getMenusData().getRideEndGoodFeedbackText();
                productType = ProductType.MENUS;
                reasons.clear();
                for (int i = 0; i < Data.getMenusData().getNegativeFeedbackReasons().length(); i++) {
                    reasons.add(new FeedbackReason(Data.getMenusData().getNegativeFeedbackReasons().get(i).toString()));
                }
            } else {
                activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


        return rootView;
    }

    private void sendFeedback(int rating, String feedbackTitle, String negativeReasons) {


        SendFeedbackQuery.sendQuery(orderId, productType, rating, feedbackTitle, negativeReasons, activity, new SendFeedbackQuery.FeedbackResultListener() {
            @Override
            public void onSendFeedbackResult(boolean isSuccess, int rating) {
                if (isSuccess) {
                    backPressed(rating);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_view_invoice, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_view_invoice:
                break;
            case R.id.btn_submit:
                sendFeedback(Math.round(ratingBar.getScore()), edtFeedbackTitle.getText().toString().trim(), edtFeedbackDescc.getText().toString().trim());
                break;
        }
    }

    private void backPressed(int rating) {
        activity.setRefreshCart(true);
        try {
            activity.getSupportFragmentManager().popBackStack(FeedbackFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
            activity.getSupportFragmentManager().popBackStackImmediate();
        }

        if (rateApp == 1 && rating > 3) {
            getRateAppDialog().show(rateAppDialogContent);
        }
    }

    private RateAppDialog getRateAppDialog() {
        if (rateAppDialog == null) {
            rateAppDialog = new RateAppDialog(activity);
        }
        return rateAppDialog;
    }

}
