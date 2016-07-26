package product.clicklabs.jugnoo.home.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomCashFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private ScrollView linearLayoutRoot;
    private LinearLayout linearLayoutWalletContainer, linearLayoutCash;
    private ImageView imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioCash;
    private TextView textViewPaytm, textViewPaytmValue, textViewMobikwik, textViewMobikwikValue;
    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik;
    private HomeActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sliding_bottom_cash, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (ScrollView) rootView.findViewById(R.id.linearLayoutRoot);
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

        activity.getSlidingBottomPanel().getSlidingUpPanelLayout().setScrollableView(linearLayoutRoot);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferredPaymentOptionUI();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()){
                case R.id.relativeLayoutPaytm:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.PAYTM);
                    break;

                case R.id.relativeLayoutMobikwik:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.MOBIKWIK);
                    break;

                case R.id.linearLayoutCash:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.CASH);
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
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas(Data.userData);
            if(paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0){
                linearLayoutWalletContainer.removeAllViews();
                for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
                    if(paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        }
                    }
                }
                linearLayoutWalletContainer.addView(linearLayoutCash);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
