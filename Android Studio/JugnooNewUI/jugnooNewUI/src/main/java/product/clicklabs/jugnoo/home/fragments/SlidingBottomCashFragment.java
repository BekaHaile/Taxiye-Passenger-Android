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
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.WalletType;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomCashFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private LinearLayout linearLayoutRoot, linearLayoutCash;
    private ImageView radioBtnPaytm, radioBtnCash;
    private TextView textViewPaytm, textViewPaytmValue;
    private RelativeLayout relativeLayoutPaytm;
    private ProgressWheel progressBarPaytm;
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

        relativeLayoutPaytm = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutPaytm);
        linearLayoutCash = (LinearLayout)rootView.findViewById(R.id.linearLayoutCash);
        radioBtnPaytm = (ImageView)rootView.findViewById(R.id.radio_paytm);
        radioBtnCash = (ImageView)rootView.findViewById(R.id.radio_cash);
        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewPaytm = (TextView)rootView.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenMedium(getActivity()));
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(getActivity()));
        progressBarPaytm = (ProgressWheel) rootView.findViewById(R.id.progressBarPaytm);


        relativeLayoutPaytm.setOnClickListener(this);
        linearLayoutCash.setOnClickListener(this);

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
        switch (v.getId()){
            case R.id.relativeLayoutPaytm:
                if(Data.userData.getPaytmBalance() > 0) {
                    Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                    setSelectedPaymentOptionUI(Data.pickupPaymentOption);
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_PAYTM_METHOD_SELECTED, null);

                } else if(Data.userData.getPaytmBalance() < 0){
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
                                        intent.putExtra(Constants.KEY_WALLET_TYPE, WalletType.PAYTM.getOrdinal());
                                        activity.startActivity(intent);
                                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                    }
                                });
                    }
                    else{
                        activity.getSlidingBottomPanel().getRequestRideOptionsFragment().openPaymentActivityInCaseOfPaytmNotAdded();
                    }
                }
                break;

            case R.id.linearLayoutCash:
                if(Data.pickupPaymentOption == PaymentOption.PAYTM.getOrdinal()){
                    FlurryEventLogger.event(activity, FlurryEventNames.CHANGED_MODE_FROM_PAYTM_TO_CASH);
                }
                Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                setSelectedPaymentOptionUI(Data.pickupPaymentOption);
                NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_CASH_METHOD_SELECTED, null);
                break;
        }
    }

    public void updatePreferredPaymentOptionUI(){
        try{
            int preferredPaymentOption = Data.pickupPaymentOption;
            if(PaymentOption.PAYTM.getOrdinal() == preferredPaymentOption){
                if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() > -1){
                    Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                    progressBarPaytm.setVisibility(View.GONE);
                }
                else{
                    Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                    progressBarPaytm.setVisibility(View.VISIBLE);
                }
            }
            else{
                Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                progressBarPaytm.setVisibility(View.GONE);
            }

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));

            if(Data.userData.getPaytmEnabled() == 1 && Data.userData.getPaytmBalance() > -1){
                textViewPaytmValue.setVisibility(View.VISIBLE);
                textViewPaytm.setText(activity.getResources().getString(R.string.paytm_wallet));
            }
            else{
                textViewPaytmValue.setVisibility(View.GONE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
            }

            if(Data.userData.getPaytmBalance() < 0){
                Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                relativeLayoutPaytm.setVisibility(View.GONE);
            }
            else{
                relativeLayoutPaytm.setVisibility(View.VISIBLE);
            }

            setSelectedPaymentOptionUI(Data.pickupPaymentOption);

            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setPaytmLoadingVisiblity(int visiblity){
        try {
            progressBarPaytm.setVisibility(visiblity);
            if(visiblity == View.VISIBLE) {
				textViewPaytmValue.setVisibility(View.GONE);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedPaymentOptionUI(int pickupPaymentOption){
        try {
            if(PaymentOption.PAYTM.getOrdinal() == pickupPaymentOption){
				paymentSelection(radioBtnPaytm, radioBtnCash);
			} else{
				paymentSelection(radioBtnCash, radioBtnPaytm);
			}
            activity.getSlidingBottomPanel().getRequestRideOptionsFragment().updatePaymentOption();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
