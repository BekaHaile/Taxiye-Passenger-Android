package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.CallbackPaymentOptionSelector;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.CorporatesAdapter;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.retrofit.model.Corporate;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.wallet.WalletCore;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by shankar on 5/2/16.
 */
public class PaymentOptionDialog implements View.OnClickListener {

    private final String TAG = PaymentOptionDialog.class.getSimpleName();
    Bundle bundle = new Bundle();
    private Activity activity;
    private Callback callback;
    private CallbackPaymentOptionSelector callbackPaymentOptionSelector;
    private Dialog dialog = null;
    private LinearLayout linearLayoutWalletContainer;
    private RelativeLayout relativeLayoutPaytm;
    private LinearLayout relativeLayoutStripeCard;
    private LinearLayout relativeLayoutAcceptCard,relativeLayoutPayStack;
    private RelativeLayout relativeLayoutMobikwik;
    private RelativeLayout relativeLayoutFreeCharge;
    private RelativeLayout relativeLayoutMpesa;
    private LinearLayout linearLayoutCash, llOtherModesToPay, llCorporate;
    private ImageView radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash, imageViewRadioFreeCharge, ivOtherModesToPay,
            imageViewRadioMpesa,imageViewRadioStripeCard,ivStripeCardIcon, imageViewRadioAcceptCard,ivAcceptCardIcon ,
            imageViewRadioPayStack,ivPayStackIcon, ivCorporate;

    private TextView textViewPaytm,textViewStripeCard, textViewAcceptCard,textViewPayStack,textViewPaytmValue, textViewMobikwik,
            textViewMobikwikValue, textViewFreeCharge, textViewFreeChargeValue, tvOtherModesToPay, textViewMpesa,
            textViewMpesaValue;
    private RecyclerView rvCorporates;
    private CorporatesAdapter corporatesAdapter;

    public PaymentOptionDialog(Activity activity, CallbackPaymentOptionSelector callbackPaymentOptionSelector, Callback callback) {
        this.activity = activity;
        this.callback = callback;
        this.callbackPaymentOptionSelector = callbackPaymentOptionSelector;
    }

    public PaymentOptionDialog show() {
        try {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_payment_option);

            RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative);
            new ASSL(activity, relative, 1134, 720, false);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            LinearLayout linearLayoutInner = (LinearLayout) dialog.findViewById(R.id.linearLayoutInner);
            ((TextView) dialog.findViewById(R.id.textViewPayForRides)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

            linearLayoutWalletContainer = (LinearLayout) dialog.findViewById(R.id.linearLayoutWalletContainer);
            relativeLayoutPaytm = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutPaytm);
            relativeLayoutStripeCard = (LinearLayout) dialog.findViewById(R.id.relativeLayoutStripeCard);
            relativeLayoutAcceptCard = (LinearLayout) dialog.findViewById(R.id.relativeLayoutAcceptCard);
            relativeLayoutPayStack = (LinearLayout) dialog.findViewById(R.id.relativeLayoutPayStack);
            relativeLayoutMobikwik = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutMobikwik);
            relativeLayoutFreeCharge = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutFreeCharge);
            linearLayoutCash = (LinearLayout) dialog.findViewById(R.id.linearLayoutCash);
            relativeLayoutMpesa = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutMpesa);
            llOtherModesToPay = (LinearLayout) dialog.findViewById(R.id.llOtherModesToPay);
            llCorporate = dialog.findViewById(R.id.llCorporate);
            radioBtnPaytm = (ImageView) dialog.findViewById(R.id.radio_paytm);
            imageViewRadioMobikwik = (ImageView) dialog.findViewById(R.id.imageViewRadioMobikwik);
            imageViewRadioFreeCharge = (ImageView) dialog.findViewById(R.id.imageViewRadioFreeCharge);
            radioBtnCash = (ImageView) dialog.findViewById(R.id.radio_cash);
            imageViewRadioMpesa = (ImageView) dialog.findViewById(R.id.imageViewRadioMpesa);
            imageViewRadioStripeCard = (ImageView) dialog.findViewById(R.id.imageViewRadioStripeCard);
            imageViewRadioAcceptCard = (ImageView) dialog.findViewById(R.id.imageViewRadioAcceptCard);
            imageViewRadioPayStack = (ImageView) dialog.findViewById(R.id.imageViewRadioPayStack);
            ivStripeCardIcon = (ImageView) dialog.findViewById(R.id.ivStripeCardIcon);
            ivAcceptCardIcon = (ImageView) dialog.findViewById(R.id.ivAcceptCardIcon);
            ivPayStackIcon = (ImageView) dialog.findViewById(R.id.ivPayStackIcon);
            ivOtherModesToPay = (ImageView) dialog.findViewById(R.id.ivOtherModesToPay);
            ivCorporate = dialog.findViewById(R.id.ivCorporate);
            rvCorporates = dialog.findViewById(R.id.rvCorporates);
            rvCorporates.setLayoutManager(new LinearLayoutManager(activity));
            rvCorporates.setHasFixedSize(false);



            textViewPaytmValue = (TextView) dialog.findViewById(R.id.textViewPaytmValue);
            textViewPaytmValue.setTypeface(Fonts.mavenLight(activity));
            textViewPaytm = (TextView) dialog.findViewById(R.id.textViewPaytm);
            textViewPaytm.setTypeface(Fonts.mavenLight(activity));
            textViewStripeCard = (TextView) dialog.findViewById(R.id.textViewStripeCard);
            textViewAcceptCard = (TextView) dialog.findViewById(R.id.textViewAcceptCard);
            textViewPayStack = (TextView) dialog.findViewById(R.id.textViewPayStack);
            textViewStripeCard.setTypeface(Fonts.mavenLight(activity));
            textViewAcceptCard.setTypeface(Fonts.mavenLight(activity));
            textViewPayStack.setTypeface(Fonts.mavenLight(activity));
            textViewMobikwikValue = (TextView) dialog.findViewById(R.id.textViewMobikwikValue);
            textViewMobikwikValue.setTypeface(Fonts.mavenLight(activity));
            textViewMobikwik = (TextView) dialog.findViewById(R.id.textViewMobikwik);
            textViewMobikwik.setTypeface(Fonts.mavenLight(activity));
            ((TextView) dialog.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(activity));
            textViewFreeCharge = (TextView) dialog.findViewById(R.id.textViewFreeCharge);
            textViewFreeCharge.setTypeface(Fonts.mavenLight(activity));
            textViewFreeChargeValue = (TextView) dialog.findViewById(R.id.textViewFreeChargeValue);
            textViewFreeChargeValue.setTypeface(Fonts.mavenLight(activity));
            textViewMpesa = (TextView) dialog.findViewById(R.id.textViewMpesa);
            textViewMpesa.setTypeface(Fonts.mavenLight(activity));
            textViewMpesaValue = (TextView) dialog.findViewById(R.id.textViewMpesaValue);
            textViewMpesaValue.setTypeface(Fonts.mavenLight(activity));
            tvOtherModesToPay = (TextView) dialog.findViewById(R.id.tvOtherModesToPay);

            relativeLayoutPaytm.setOnClickListener(this);
            relativeLayoutStripeCard.setOnClickListener(this);
            relativeLayoutAcceptCard.setOnClickListener(this);
            relativeLayoutPayStack.setOnClickListener(this);
            relativeLayoutMobikwik.setOnClickListener(this);
            relativeLayoutFreeCharge.setOnClickListener(this);
            linearLayoutCash.setOnClickListener(this);
            relativeLayoutMpesa.setOnClickListener(this);
            llOtherModesToPay.setOnClickListener(this);
            llCorporate.setOnClickListener(this);

            orderPaymentModes();

            updatePreferredPaymentOptionUI();

            ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);
            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            linearLayoutInner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    callback.onDialogDismiss();
                }
            });


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.relativeLayoutPaytm:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAYTM, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;

                case R.id.relativeLayoutMobikwik:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MOBIKWIK, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;

                case R.id.linearLayoutCash:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CASH, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutMpesa:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MPESA, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.llOtherModesToPay:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.RAZOR_PAY, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.llCorporate:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CORPORATE, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutFreeCharge:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutStripeCard:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.STRIPE_CARDS, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutAcceptCard:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.ACCEPT_CARD, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutPayStack:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAY_STACK_CARD, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
            }
            callbackPaymentOptionSelector.onWalletOptionClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedPaymentOptionUI() {
        try {
            callbackPaymentOptionSelector.setSelectedPaymentOption(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(callbackPaymentOptionSelector.getSelectedPaymentOption()));

            if(corporatesAdapter!=null){
                corporatesAdapter.unSelectAll();
            }

            if (PaymentOption.PAYTM.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash, imageViewRadioFreeCharge,
                        ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.MOBIKWIK.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash, imageViewRadioFreeCharge,
                        ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.FREECHARGE.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash,
                        ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.MPESA.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioFreeCharge, imageViewRadioMobikwik,
                        radioBtnPaytm, radioBtnCash,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.RAZOR_PAY.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(ivOtherModesToPay, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            }else if (PaymentOption.STRIPE_CARDS.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioStripeCard, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa,ivOtherModesToPay,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            } else if (PaymentOption.ACCEPT_CARD.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioAcceptCard, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa,ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioPayStack, ivCorporate);
            }else if (PaymentOption.PAY_STACK_CARD.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioPayStack, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa,ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard, ivCorporate);
            } else if (PaymentOption.CORPORATE.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(ivCorporate, imageViewRadioPayStack, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa,ivOtherModesToPay,imageViewRadioStripeCard,imageViewRadioAcceptCard);
                if(corporatesAdapter!=null){
                    corporatesAdapter.selectDefault();
                }
           } else {
                paymentSelection(radioBtnCash, radioBtnPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, ivOtherModesToPay,
                        imageViewRadioMpesa,imageViewRadioStripeCard,imageViewRadioAcceptCard,imageViewRadioPayStack, ivCorporate);
            }
            dialog.dismiss();
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
            callbackPaymentOptionSelector.setSelectedPaymentOption(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(callbackPaymentOptionSelector.getSelectedPaymentOption()));

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

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
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        }
                        else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MPESA.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMpesa);
                            textViewMpesa.setText(paymentModeConfigData.getDisplayName());
                        }
                        else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
                            linearLayoutWalletContainer.addView(linearLayoutCash);
                        }
                        else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CORPORATE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(llCorporate);
                            linearLayoutWalletContainer.addView(rvCorporates);

                            if(corporatesAdapter==null && paymentModeConfigData.getCorporates()!=null){
                                corporatesAdapter = new CorporatesAdapter(paymentModeConfigData.getCorporates(),
                                        rvCorporates, new CorporatesAdapter.OnSelectedCallback() {
                                    @Override
                                    public void onItemSelected(@NotNull Corporate corporate) {
                                        if(Data.autoData.getPickupPaymentOption()!=PaymentOption.CORPORATE.getOrdinal()){
                                            onClick(llCorporate);
                                        }else{
                                            dismiss();
                                        }
                                    }
                                });
                            }
                            rvCorporates.setAdapter(corporatesAdapter);

                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
                                && callbackPaymentOptionSelector.isRazorpayEnabled()) {
                            linearLayoutWalletContainer.addView(llOtherModesToPay);
                            tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutStripeCard);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                               WalletCore.getStripeCardDisplayString(activity,paymentModeConfigData.getCardsData().get(0),textViewStripeCard,ivStripeCardIcon);
                            }else{
                                ivStripeCardIcon.setImageResource(R.drawable.ic_card_default);
                                textViewStripeCard.setText(activity.getString(R.string.action_add_card_stripe));
                            }
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.ACCEPT_CARD.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutAcceptCard);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                               WalletCore.getStripeCardDisplayString(activity,paymentModeConfigData.getCardsData().get(0),textViewAcceptCard,ivAcceptCardIcon);
                            }else{
                                ivAcceptCardIcon.setImageResource(R.drawable.ic_card_default);
                                textViewAcceptCard.setText(activity.getString(R.string.action_add_card_accept_card));
                            }
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAY_STACK_CARD.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPayStack);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                               WalletCore.getStripeCardDisplayString(activity,paymentModeConfigData.getCardsData().get(0),textViewPayStack,ivPayStackIcon);
                            }else{
                                ivPayStackIcon.setImageResource(R.drawable.ic_card_default);
                                textViewPayStack.setText(activity.getString(R.string.action_add_card_pay_stack));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface Callback {
        void onDialogDismiss();

        void onPaymentModeUpdated();
    }

}