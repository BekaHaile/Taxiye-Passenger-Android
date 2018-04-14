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

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

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
public class SlidingBottomCashFragment extends Fragment implements View.OnClickListener, GAAction, GACategory {

    private View rootView;
    private ScrollView linearLayoutRoot;
    private LinearLayout linearLayoutWalletContainer, linearLayoutCash, llOtherModesToPay;
    private ImageView imageViewRadioPaytm, imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioCash, imageViewRadioFreeCharge, ivOtherModesToPay;
    private TextView textViewPaytm, textViewPaytmValue, textViewMpesa, textViewMpesaValue, textViewMobikwik, textViewMobikwikValue, textViewFreeCharge, textViewFreeChargeValue, tvOtherModesToPay;
    private RelativeLayout relativeLayoutPaytm, relativeLayoutMpesa, relativeLayoutMobikwik, relativeLayoutFreeCharge;
    private HomeActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sliding_bottom_cash, container, false);
        activity = (HomeActivity) getActivity();
        linearLayoutRoot = (ScrollView) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(getActivity(), linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);

        imageViewRadioPaytm = (ImageView) rootView.findViewById(R.id.imageViewRadioPaytm);
        imageViewRadioMpesa = (ImageView) rootView.findViewById(R.id.imageViewRadioMpesa);
        imageViewRadioMobikwik = (ImageView) rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewRadioCash = (ImageView) rootView.findViewById(R.id.imageViewRadioCash);
        imageViewRadioFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewRadioFreeCharge);
        ivOtherModesToPay = (ImageView) rootView.findViewById(R.id.ivOtherModesToPay);

        textViewPaytmValue = (TextView) rootView.findViewById(R.id.textViewPaytmValue);
        textViewPaytmValue.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewPaytm = (TextView) rootView.findViewById(R.id.textViewPaytm);
        textViewPaytm.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewMpesaValue = (TextView) rootView.findViewById(R.id.textViewMpesaValue);
        textViewMpesaValue.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewMpesa = (TextView) rootView.findViewById(R.id.textViewMpesa);
        textViewMpesa.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewMobikwik = (TextView) rootView.findViewById(R.id.textViewMobikwik);
        textViewMobikwik.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewMobikwikValue = (TextView) rootView.findViewById(R.id.textViewMobikwikValue);
        textViewMobikwikValue.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewFreeCharge = (TextView) rootView.findViewById(R.id.textViewFreeCharge);
        textViewFreeCharge.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewFreeChargeValue = (TextView) rootView.findViewById(R.id.textViewFreeChargeValue);
        textViewFreeChargeValue.setTypeface(Fonts.mavenMedium(getActivity()));
        tvOtherModesToPay = (TextView) rootView.findViewById(R.id.tvOtherModesToPay);

        relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutMpesa = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMpesa);
        relativeLayoutMobikwik = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMobikwik);
        linearLayoutCash = (LinearLayout) rootView.findViewById(R.id.linearLayoutCash);
        llOtherModesToPay = (LinearLayout) rootView.findViewById(R.id.llOtherModesToPay);
        ((TextView) rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(getActivity()));
        relativeLayoutFreeCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFreeCharge);

        relativeLayoutPaytm.setOnClickListener(this);
        relativeLayoutMpesa.setOnClickListener(this);
        relativeLayoutMobikwik.setOnClickListener(this);
        linearLayoutCash.setOnClickListener(this);
        relativeLayoutFreeCharge.setOnClickListener(this);
        llOtherModesToPay.setOnClickListener(this);


        activity.getSlidingBottomPanel().getSlidingUpPanelLayout().setScrollableView(linearLayoutRoot);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        orderPaymentModes();
        updatePreferredPaymentOptionUI();
    }

    Bundle bundle = new Bundle();

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.relativeLayoutPaytm:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.PAYTM);
                    break;
                case R.id.relativeLayoutMpesa:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.MPESA);
                    break;

                case R.id.relativeLayoutMobikwik:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.MOBIKWIK);
                    break;

                case R.id.linearLayoutCash:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.CASH);
                    break;
                case R.id.llOtherModesToPay:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.RAZOR_PAY);
                    break;
                case R.id.relativeLayoutFreeCharge:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.FREECHARGE);
                    break;
            }
            try {
                GAUtils.event(RIDES, HOME + WALLET + SELECTED, MyApplication.getInstance().getWalletCore()
                        .getPaymentOptionName(Data.autoData.getPickupPaymentOption()));
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setSelectedPaymentOptionUI() {
        try {
            Data.autoData.setPickupPaymentOption(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.autoData.getPickupPaymentOption()));
            if (PaymentOption.PAYTM.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioPaytm, imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioCash, imageViewRadioFreeCharge, ivOtherModesToPay);
            } else if (PaymentOption.MOBIKWIK.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioMobikwik, imageViewRadioMpesa, imageViewRadioPaytm, imageViewRadioCash, imageViewRadioFreeCharge, ivOtherModesToPay);
            } else if (PaymentOption.MPESA.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash, imageViewRadioFreeCharge, ivOtherModesToPay);
            } else if (PaymentOption.FREECHARGE.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioFreeCharge, imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash, ivOtherModesToPay);
            } else if (PaymentOption.RAZOR_PAY.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(ivOtherModesToPay, imageViewRadioMpesa, imageViewRadioFreeCharge, imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash);
            } else {
                paymentSelection(imageViewRadioCash, imageViewRadioMpesa, imageViewRadioPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, ivOtherModesToPay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paymentSelection(ImageView selected, ImageView unSelected, ImageView unSelected2, ImageView unSelected3, ImageView unSelected4, ImageView unSelected5) {
        try {
            selected.setImageResource(R.drawable.ic_radio_button_selected);
            unSelected.setImageResource(R.drawable.ic_radio_button_normal);
            unSelected2.setImageResource(R.drawable.ic_radio_button_normal);
            unSelected3.setImageResource(R.drawable.ic_radio_button_normal);
            unSelected4.setImageResource(R.drawable.ic_radio_button_normal);
            unSelected5.setImageResource(R.drawable.ic_radio_button_normal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePreferredPaymentOptionUI() {
        try {
            Data.autoData.setPickupPaymentOption(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.autoData.getPickupPaymentOption()));

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

//            textViewMpesaValue.setText(String.format(activity.getResources()
//                    .getString(R.string.rupees_value_format), Data.userData.getMpesaBalanceStr()));
//            textViewMpesaValue.setTextColor(Data.userData.getMpesaBalanceColor(activity));

            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));

            textViewFreeChargeValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getFreeChargeBalanceStr()));
            textViewFreeChargeValue.setTextColor(Data.userData.getFreeChargeBalanceColor(activity));

            if (Data.userData.getPaytmEnabled() == 1) {
                textViewPaytmValue.setVisibility(View.VISIBLE);
                textViewPaytm.setText(activity.getResources().getString(R.string.paytm_wallet));
            } else {
                textViewPaytmValue.setVisibility(View.GONE);
                textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
            }

//            if (Data.userData.getMpesaEnabled() == 1) {
//                textViewMpesaValue.setVisibility(View.VISIBLE);
//                textViewMpesa.setText(activity.getResources().getString(R.string.mpesa_wallet));
//            } else {
//                textViewMpesaValue.setVisibility(View.GONE);
//                textViewMpesa.setText(activity.getResources().getString(R.string.nl_add_mpesa_wallet));
//            }

            if (Data.userData.getMobikwikEnabled() == 1) {
                textViewMobikwikValue.setVisibility(View.VISIBLE);
                textViewMobikwik.setText(activity.getResources().getString(R.string.mobikwik_wallet));
            } else {
                textViewMobikwikValue.setVisibility(View.GONE);
                textViewMobikwik.setText(activity.getResources().getString(R.string.add_mobikwik_wallet));
            }
            if (Data.userData.getFreeChargeEnabled() == 1) {
                textViewFreeChargeValue.setVisibility(View.VISIBLE);
                textViewFreeCharge.setText(activity.getResources().getString(R.string.freecharge_wallet));
            } else {
                textViewFreeChargeValue.setVisibility(View.GONE);
                textViewFreeCharge.setText(activity.getResources().getString(R.string.add_freecharge_wallet));
            }

            setSelectedPaymentOptionUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void orderPaymentModes() {
        try {
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
            if (paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0) {
                linearLayoutWalletContainer.removeAllViews();
                // TODO: 14/04/18 remove this
                linearLayoutWalletContainer.addView(relativeLayoutMpesa);
                for (PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas) {
                    if (paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        }
                        // TODO: 14/04/18 uncomment this check when server starts sending mpesa config
//                        else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MPESA.getOrdinal()) {
//                            linearLayoutWalletContainer.addView(relativeLayoutMpesa);
//                        }
                        else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
                            linearLayoutWalletContainer.addView(linearLayoutCash);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
                                && Data.autoData != null && Data.autoData.isRazorpayEnabled()) {
                            linearLayoutWalletContainer.addView(llOtherModesToPay);
                            tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
