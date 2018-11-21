package product.clicklabs.jugnoo.home.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.CorporatesAdapter;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.model.Corporate;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.wallet.WalletCore;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by Ankit on 1/8/16.
 */
public class SlidingBottomCashFragment extends Fragment implements View.OnClickListener, GAAction, GACategory {

    private View rootView;
    private ScrollView linearLayoutRoot;
    private LinearLayout linearLayoutWalletContainer, linearLayoutCash, llOtherModesToPay, llCorporate;
    private ImageView imageViewRadioPaytm,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack,
            imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioCash, imageViewRadioFreeCharge,ivStripeCardIcon,
            ivAcceptCardIcon,ivPayStackIcon,ivOtherModesToPay, ivCorporate;
    private TextView textViewPaytm,textViewStripeCard,textViewAcceptCard,textViewPayStack, textViewPaytmValue, textViewMpesa,
            textViewMpesaValue, textViewMobikwik, textViewMobikwikValue, textViewFreeCharge, textViewFreeChargeValue,
            tvOtherModesToPay;
    private RelativeLayout relativeLayoutPaytm,relativeLayoutStripeCard,relativeLayoutAcceptCard,relativeLayoutPayStack,
            relativeLayoutMpesa, relativeLayoutMobikwik, relativeLayoutFreeCharge;
    private HomeActivity activity;
    private RecyclerView rvCorporates;
    private CorporatesAdapter corporatesAdapter;

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
        imageViewRadioStripeCard = (ImageView) rootView.findViewById(R.id.imageViewRadioStripeCard);
        imageViewRadioAcceptCard = (ImageView) rootView.findViewById(R.id.imageViewRadioAcceptCard);
        imageViewRadioPayStack = (ImageView) rootView.findViewById(R.id.imageViewRadioPayStack);
        imageViewRadioMpesa = (ImageView) rootView.findViewById(R.id.imageViewRadioMpesa);
        imageViewRadioMobikwik = (ImageView) rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewRadioCash = (ImageView) rootView.findViewById(R.id.imageViewRadioCash);
        imageViewRadioFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewRadioFreeCharge);
        ivOtherModesToPay = (ImageView) rootView.findViewById(R.id.ivOtherModesToPay);
        ivCorporate = rootView.findViewById(R.id.ivCorporate);
        rvCorporates = rootView.findViewById(R.id.rvCorporates);
        rvCorporates.setLayoutManager(new LinearLayoutManager(activity));
        rvCorporates.setHasFixedSize(false);
        textViewPaytmValue = (TextView) rootView.findViewById(R.id.textViewPaytmValue);
        textViewPaytmValue.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewPaytm = (TextView) rootView.findViewById(R.id.textViewPaytm);
        textViewPaytm.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewStripeCard = (TextView) rootView.findViewById(R.id.textViewStripeCard);
        textViewAcceptCard = (TextView) rootView.findViewById(R.id.textViewAcceptCard);
        textViewPayStack = (TextView) rootView.findViewById(R.id.textViewPayStack);
        ivStripeCardIcon = (ImageView) rootView.findViewById(R.id.ivStripeCardIcon);
        ivAcceptCardIcon = (ImageView) rootView.findViewById(R.id.ivAcceptCardIcon);
        ivPayStackIcon = (ImageView) rootView.findViewById(R.id.ivPayStackIcon);
        textViewStripeCard.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewAcceptCard.setTypeface(Fonts.mavenMedium(getActivity()));
        textViewPayStack.setTypeface(Fonts.mavenMedium(getActivity()));
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
        relativeLayoutStripeCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutStripeCard);
        relativeLayoutAcceptCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAcceptCard);
        relativeLayoutPayStack = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPayStack);
        relativeLayoutMpesa = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMpesa);
        relativeLayoutMobikwik = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMobikwik);
        linearLayoutCash = (LinearLayout) rootView.findViewById(R.id.linearLayoutCash);
        llOtherModesToPay = (LinearLayout) rootView.findViewById(R.id.llOtherModesToPay);
        llCorporate = rootView.findViewById(R.id.llCorporate);
        ((TextView) rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(getActivity()));
        relativeLayoutFreeCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFreeCharge);

        relativeLayoutPaytm.setOnClickListener(this);
        relativeLayoutStripeCard.setOnClickListener(this);
        relativeLayoutAcceptCard.setOnClickListener(this);
        relativeLayoutPayStack.setOnClickListener(this);
        relativeLayoutMpesa.setOnClickListener(this);
        relativeLayoutMobikwik.setOnClickListener(this);
        linearLayoutCash.setOnClickListener(this);
        relativeLayoutFreeCharge.setOnClickListener(this);
        llOtherModesToPay.setOnClickListener(this);
        llCorporate.setOnClickListener(this);


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
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAYTM, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.relativeLayoutMpesa:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MPESA, activity.getCallbackPaymentOptionSelector());
                    break;

                case R.id.relativeLayoutMobikwik:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MOBIKWIK, activity.getCallbackPaymentOptionSelector());
                    break;

                case R.id.linearLayoutCash:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CASH, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.llCorporate:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CORPORATE, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.llOtherModesToPay:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.RAZOR_PAY, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.relativeLayoutFreeCharge:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.relativeLayoutStripeCard:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.STRIPE_CARDS, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.relativeLayoutAcceptCard:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.ACCEPT_CARD, activity.getCallbackPaymentOptionSelector());
                    break;
                case R.id.relativeLayoutPayStack:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAY_STACK_CARD, activity.getCallbackPaymentOptionSelector());
                    break;
            }
            activity.showDriverMarkersAndPanMap(Data.autoData.getPickupLatLng(), activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected());
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
            if(corporatesAdapter!=null){
                corporatesAdapter.unSelectAll();
            }
            if (PaymentOption.PAYTM.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioPaytm, imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioCash,
                        imageViewRadioFreeCharge, ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.MOBIKWIK.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioMobikwik, imageViewRadioMpesa, imageViewRadioPaytm, imageViewRadioCash,
                        imageViewRadioFreeCharge, ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.MPESA.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash,
                        imageViewRadioFreeCharge, ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.FREECHARGE.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioFreeCharge, imageViewRadioMpesa, imageViewRadioMobikwik, imageViewRadioPaytm,
                        imageViewRadioCash, ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.RAZOR_PAY.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(ivOtherModesToPay, imageViewRadioMpesa, imageViewRadioFreeCharge, imageViewRadioMobikwik,
                        imageViewRadioPaytm, imageViewRadioCash,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            }else if (PaymentOption.STRIPE_CARDS.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioStripeCard,imageViewRadioAcceptCard, imageViewRadioMpesa, imageViewRadioFreeCharge,
                        imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash,ivOtherModesToPay,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.ACCEPT_CARD.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioAcceptCard,imageViewRadioStripeCard, imageViewRadioMpesa, imageViewRadioFreeCharge,
                        imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash,ivOtherModesToPay,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.PAY_STACK_CARD.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioPayStack,imageViewRadioStripeCard, imageViewRadioMpesa, imageViewRadioFreeCharge,
                        imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash,ivOtherModesToPay,imageViewRadioAcceptCard, ivCorporate);
            } else if (PaymentOption.CORPORATE.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(ivCorporate, imageViewRadioPayStack,imageViewRadioStripeCard, imageViewRadioMpesa, imageViewRadioFreeCharge,
                        imageViewRadioMobikwik, imageViewRadioPaytm, imageViewRadioCash,ivOtherModesToPay,imageViewRadioAcceptCard);
                if(corporatesAdapter!=null){
                    corporatesAdapter.selectDefault();
                }
            } else {
                paymentSelection(imageViewRadioCash, imageViewRadioMpesa, imageViewRadioPaytm, imageViewRadioMobikwik,
                        imageViewRadioFreeCharge, ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paymentSelection(ImageView selected, ImageView... unSelectedImageViews) {
        try {
            selected.setImageResource(R.drawable.ic_radio_button_selected);
            for(ImageView unselected: unSelectedImageViews){
                unselected.setImageResource(R.drawable.ic_radio_button_normal);
            }

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
                for (PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas) {
                    if (paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        }
                        else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MPESA.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMpesa);
                            textViewMpesa.setText(paymentModeConfigData.getDisplayName());
                        }
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
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CORPORATE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(llCorporate);
                            linearLayoutWalletContainer.addView(rvCorporates);
                            if (corporatesAdapter==null) {
                                corporatesAdapter = new CorporatesAdapter(paymentModeConfigData.getCorporates(),
                                        rvCorporates, new CorporatesAdapter.OnSelectedCallback() {
                                    @Override
                                    public void onItemSelected(@NotNull Corporate corporate) {
                                        if(Data.autoData.getPickupPaymentOption()!=PaymentOption.CORPORATE.getOrdinal()){
                                            onClick(llCorporate);
                                        }
                                    }
                                });
                            }
                            rvCorporates.setAdapter(corporatesAdapter);

                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutStripeCard);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                                WalletCore.getStripeCardDisplayString(getActivity(),paymentModeConfigData.getCardsData().get(0),textViewStripeCard,ivStripeCardIcon);
                            }else{
                                textViewStripeCard.setText(getString(R.string.add_card_payments));
                                ivStripeCardIcon.setImageResource(R.drawable.ic_card_default);
                            }
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.ACCEPT_CARD.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutAcceptCard);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                                WalletCore.getStripeCardDisplayString(getActivity(),paymentModeConfigData.getCardsData().get(0),textViewAcceptCard,ivAcceptCardIcon);
                            }else{
                                textViewAcceptCard.setText(getString(R.string.add_card_payments));
                                ivAcceptCardIcon.setImageResource(R.drawable.ic_card_default);
                            }
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAY_STACK_CARD.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPayStack);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                                WalletCore.getStripeCardDisplayString(getActivity(),paymentModeConfigData.getCardsData().get(0),textViewPayStack,ivPayStackIcon);
                            }else{
                                textViewPayStack.setText(getString(R.string.add_card_payments));
                                ivPayStackIcon.setImageResource(R.drawable.ic_card_default);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
