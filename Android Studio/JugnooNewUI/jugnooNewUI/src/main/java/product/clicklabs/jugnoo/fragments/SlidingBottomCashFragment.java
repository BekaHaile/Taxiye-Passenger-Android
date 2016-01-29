package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.graphics.Typeface;
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
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.wallet.PaymentActivity;

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
        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenRegular(getActivity()));
        textViewPaytm = (TextView)rootView.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenLight(getActivity()));
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(getActivity()));
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
        selected.setImageResource(R.drawable.radio_selected_icon);
        unSelected.setImageResource(R.drawable.radio_unselected_icon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relativeLayoutPaytm:
                if(Data.userData.getPaytmBalance() > 0) {
                    Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                    setSelectedPaymentOptionUI(Data.pickupPaymentOption);
                } else if(Data.userData.getPaytmError() == 1){
                    DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                } else{
                    if(Data.userData.paytmEnabled == 1
                            && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
                        DialogPopup.alertPopupWithListener(activity, "",
                                activity.getResources().getString(R.string.paytm_no_cash),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, PaymentActivity.class);
                                        if(Data.userData.paytmEnabled == 1) {
                                            intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
                                        } else {
                                            intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
                                        }
                                        activity.startActivity(intent);
                                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                    }
                                });
                    }
                    else{
                        activity.getSlidingBottomPanel().openPaymentActivityInCaseOfPaytmNotAdded();
                    }
                }
                break;

            case R.id.linearLayoutCash:
                Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                setSelectedPaymentOptionUI(Data.pickupPaymentOption);
                break;
        }
    }

    public void updatePreferredPaymentOptionUI(){
        try{
            int preferredPaymentOption = Data.pickupPaymentOption;
            if(PaymentOption.PAYTM.getOrdinal() == preferredPaymentOption){
                if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
                    Data.pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
                    progressBarPaytm.setVisibility(View.GONE);
                }
                else if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)){
                    Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
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

            if(Data.userData.paytmEnabled == 1 && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
                textViewPaytmValue.setVisibility(View.VISIBLE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_paytm_wallet));
            }
            else{
                textViewPaytmValue.setVisibility(View.GONE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
            }

            if(Data.userData.getPaytmError() == 1){
                Data.pickupPaymentOption = PaymentOption.CASH.getOrdinal();
                relativeLayoutPaytm.setVisibility(View.GONE);
            }
            else{
                relativeLayoutPaytm.setVisibility(View.VISIBLE);
            }

            setSelectedPaymentOptionUI(Data.pickupPaymentOption);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setPaytmLoadingVisiblity(int visiblity){
        progressBarPaytm.setVisibility(visiblity);
        if(visiblity == View.VISIBLE) {
            textViewPaytmValue.setVisibility(View.GONE);
        }
    }

    private void setSelectedPaymentOptionUI(int pickupPaymentOption){
        if(PaymentOption.PAYTM.getOrdinal() == pickupPaymentOption){
            paymentSelection(radioBtnPaytm, radioBtnCash);
        } else{
            paymentSelection(radioBtnCash, radioBtnPaytm);
        }
        activity.getSlidingBottomPanel().updatePaymentOption();
    }





}
