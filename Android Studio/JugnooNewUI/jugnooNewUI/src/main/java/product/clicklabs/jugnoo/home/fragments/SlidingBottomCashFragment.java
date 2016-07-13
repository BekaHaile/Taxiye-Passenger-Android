package product.clicklabs.jugnoo.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomCashFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private LinearLayout linearLayoutRoot, linearLayoutWalletContainer, linearLayoutCash;
    private ImageView imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioCash;
    private TextView textViewPaytm, textViewPaytmValue, textViewMobikwik, textViewMobikwikValue;
    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik;
    private HomeActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sliding_bottom_cash, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if(linearLayoutRoot != null) {
                new ASSL(getActivity(), linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayoutWalletContainer = (LinearLayout)rootView.findViewById(R.id.linearLayoutWalletContainer);

        imageViewRadioPaytm = (ImageView)rootView.findViewById(R.id.imageViewRadioPaytm);
        imageViewRadioMobikwik = (ImageView)rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewRadioCash = (ImageView)rootView.findViewById(R.id.imageViewRadioCash);

        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewPaytm = (TextView)rootView.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewMobikwik = (TextView)rootView.findViewById(R.id.textViewMobikwik);textViewMobikwik.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewMobikwikValue = (TextView)rootView.findViewById(R.id.textViewMobikwikValue); textViewMobikwikValue.setTypeface(Fonts.mavenMedium(getActivity()));

        relativeLayoutPaytm = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutMobikwik = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutMobikwik);
        linearLayoutCash = (LinearLayout)rootView.findViewById(R.id.linearLayoutCash);
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(getActivity()));

        relativeLayoutPaytm.setOnClickListener(this);
        relativeLayoutMobikwik.setOnClickListener(this);
        linearLayoutCash.setOnClickListener(this);

        orderPaymentModes();

        updatePreferredPaymentOptionUI();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferredPaymentOptionUI();
    }

    private void paymentSelection(ImageView selected, ImageView unSelected){
        try {
            selected.setImageResource(R.drawable.ic_radio_button_selected);
            unSelected.setImageResource(R.drawable.ic_radio_button_normal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()){
                case R.id.relativeLayoutPaytm:
                    if(Data.userData.getPaytmBalance() > 0) {
                        Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                        activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYTM_METHOD_SELECTED, null);
                    }
                    else if(Data.userData.getPaytmBalance() < 0){
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                    } else{
                        if(Data.userData.getPaytmEnabled() == 1) {
                            DialogPopup.alertPopupWithListener(activity, "",
                                    activity.getResources().getString(R.string.paytm_no_cash),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(activity, PaymentActivity.class);
                                            intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
                                            intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.PAYTM.getOrdinal());
                                            activity.startActivity(intent);
                                            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                        }
                                    });
                        }
                        else{
                            MyApplication.getInstance().getWalletCore()
                                    .openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.PAYTM.getOrdinal());
                        }
                    }
                    break;

                case R.id.relativeLayoutMobikwik:
                    if(Data.userData.getMobikwikBalance() > 0) {
                        Data.pickupPaymentOption = PaymentOption.MOBIKWIK.getOrdinal();
                        activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_MOBIKWIK_METHOD_SELECTED, null);
                    }
                    else if(Data.userData.getMobikwikBalance() < 0){
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.mobikwik_error_select_cash));
                    } else{
                        if(Data.userData.getMobikwikEnabled() == 1) {
                            DialogPopup.alertPopupWithListener(activity, "",
                                    activity.getResources().getString(R.string.mobikwik_no_cash),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(activity, PaymentActivity.class);
                                            intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal());
                                            intent.putExtra(Constants.KEY_WALLET_TYPE, PaymentOption.MOBIKWIK.getOrdinal());
                                            activity.startActivity(intent);
                                            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                        }
                                    });
                        }
                        else{
                            MyApplication.getInstance().getWalletCore()
                                    .openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.MOBIKWIK.getOrdinal());
                        }
                    }
                    break;

                case R.id.linearLayoutCash:
                    if(Data.pickupPaymentOption == PaymentOption.PAYTM.getOrdinal()){
                        FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
                    }
                    Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                    activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_CASH_METHOD_SELECTED, null);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedPaymentOptionUI(){
        try {
            Data.pickupPaymentOption = MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.pickupPaymentOption);
            if(PaymentOption.PAYTM.getOrdinal() == Data.pickupPaymentOption){
                paymentSelection(imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioCash);
            } else if(PaymentOption.MOBIKWIK.getOrdinal() == Data.pickupPaymentOption){
                paymentSelection(imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash);
            } else{
                paymentSelection(imageViewRadioCash, imageViewRadioPaytm, imageViewRadioMobikwik);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paymentSelection(ImageView selected, ImageView unSelected, ImageView unSelected2){
        try {
            selected.setImageResource(R.drawable.ic_radio_button_selected);
            unSelected.setImageResource(R.drawable.ic_radio_button_normal);
            unSelected2.setImageResource(R.drawable.ic_radio_button_normal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePreferredPaymentOptionUI(){
        try{
            Data.pickupPaymentOption = MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.pickupPaymentOption);

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));

            if(Data.userData.getPaytmEnabled() == 1){
                textViewPaytmValue.setVisibility(View.VISIBLE);
                textViewPaytm.setText(activity.getResources().getString(R.string.paytm_wallet));
            } else{
                textViewPaytmValue.setVisibility(View.GONE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
            }
            if(Data.userData.getMobikwikEnabled() == 1){
                textViewMobikwikValue.setVisibility(View.VISIBLE);
                textViewMobikwik.setText(activity.getResources().getString(R.string.mobikwik_wallet));
            } else{
                textViewMobikwikValue.setVisibility(View.GONE);
                textViewMobikwik.setText(activity.getResources().getString(R.string.add_mobikwik_wallet));
            }

            setSelectedPaymentOptionUI();

        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private void orderPaymentModes(){
        try{
            if(MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas() != null
                    && MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas().size() > 0){
                linearLayoutWalletContainer.removeAllViews();
                for(PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore()
                        .getPaymentModeConfigDatas()){
                    if(paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
                                && Data.userData.getPaytmEnabled() == 1) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
                                && Data.userData.getMobikwikEnabled() == 1) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        }
                    }
                }

                for(PaymentModeConfigData paymentModeConfigData : MyApplication.getInstance().getWalletCore()
                        .getPaymentModeConfigDatas()){
                    if(paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()
                                && Data.userData.getPaytmEnabled() != 1) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()
                                && Data.userData.getMobikwikEnabled() != 1) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
