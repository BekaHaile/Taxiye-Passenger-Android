package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
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
    private HomeActivity activity;
    private Callback callback;
    private Dialog dialog = null;
    private LinearLayout linearLayoutWalletContainer;
    private RelativeLayout relativeLayoutPaytm,relativeLayoutStripeCard;
    private RelativeLayout relativeLayoutMobikwik;
    private RelativeLayout relativeLayoutFreeCharge;
    private RelativeLayout relativeLayoutMpesa;
    private LinearLayout linearLayoutCash, llOtherModesToPay ;
    private ImageView radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash, imageViewRadioFreeCharge, ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard,ivStripeCardIcon;
    private TextView textViewPaytm,textViewStripeCard, textViewPaytmValue, textViewMobikwik, textViewMobikwikValue, textViewFreeCharge, textViewFreeChargeValue, tvOtherModesToPay, textViewMpesa,textViewMpesaValue;

    public PaymentOptionDialog(HomeActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public PaymentOptionDialog show() {
        try {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogScale;
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
            relativeLayoutStripeCard = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutStripeCard);
            relativeLayoutMobikwik = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutMobikwik);
            relativeLayoutFreeCharge = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutFreeCharge);
            linearLayoutCash = (LinearLayout) dialog.findViewById(R.id.linearLayoutCash);
            relativeLayoutMpesa = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutMpesa);
            llOtherModesToPay = (LinearLayout) dialog.findViewById(R.id.llOtherModesToPay);
            radioBtnPaytm = (ImageView) dialog.findViewById(R.id.radio_paytm);
            imageViewRadioMobikwik = (ImageView) dialog.findViewById(R.id.imageViewRadioMobikwik);
            imageViewRadioFreeCharge = (ImageView) dialog.findViewById(R.id.imageViewRadioFreeCharge);
            radioBtnCash = (ImageView) dialog.findViewById(R.id.radio_cash);
            imageViewRadioMpesa = (ImageView) dialog.findViewById(R.id.imageViewRadioMpesa);
            imageViewRadioStripeCard = (ImageView) dialog.findViewById(R.id.imageViewRadioStripeCard);
            ivStripeCardIcon = (ImageView) dialog.findViewById(R.id.ivStripeCardIcon);
            ivOtherModesToPay = (ImageView) dialog.findViewById(R.id.ivOtherModesToPay);


            textViewPaytmValue = (TextView) dialog.findViewById(R.id.textViewPaytmValue);
            textViewPaytmValue.setTypeface(Fonts.mavenLight(activity));
            textViewPaytm = (TextView) dialog.findViewById(R.id.textViewPaytm);
            textViewPaytm.setTypeface(Fonts.mavenLight(activity));
            textViewStripeCard = (TextView) dialog.findViewById(R.id.textViewStripeCard);
            textViewStripeCard.setTypeface(Fonts.mavenLight(activity));
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
            relativeLayoutMobikwik.setOnClickListener(this);
            relativeLayoutFreeCharge.setOnClickListener(this);
            linearLayoutCash.setOnClickListener(this);
            relativeLayoutMpesa.setOnClickListener(this);
            llOtherModesToPay.setOnClickListener(this);

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
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.PAYTM);
                    callback.onPaymentModeUpdated();
                    break;

                case R.id.relativeLayoutMobikwik:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.MOBIKWIK);
                    callback.onPaymentModeUpdated();
                    break;

                case R.id.linearLayoutCash:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.CASH);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutMpesa:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.MPESA);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.llOtherModesToPay:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.RAZOR_PAY);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutFreeCharge:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.FREECHARGE);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutStripeCard:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionBeforeRequestRide(activity, PaymentOption.STRIPE_CARDS);
                    callback.onPaymentModeUpdated();
                    break;
            }
            activity.showDriverMarkersAndPanMap(Data.autoData.getPickupLatLng(), activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedPaymentOptionUI() {
        try {
            Data.autoData.setPickupPaymentOption(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.autoData.getPickupPaymentOption()));
            if (PaymentOption.PAYTM.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash, imageViewRadioFreeCharge, ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard);
            } else if (PaymentOption.MOBIKWIK.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash, imageViewRadioFreeCharge, ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard);
            } else if (PaymentOption.FREECHARGE.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash, ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard);
            } else if (PaymentOption.MPESA.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash,imageViewRadioStripeCard);
            } else if (PaymentOption.RAZOR_PAY.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(ivOtherModesToPay, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash, imageViewRadioMpesa,imageViewRadioStripeCard);
            }else if (PaymentOption.STRIPE_CARDS.getOrdinal() == Data.autoData.getPickupPaymentOption()) {
                paymentSelection(imageViewRadioStripeCard, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash, imageViewRadioMpesa,ivOtherModesToPay);
            } else {
                paymentSelection(radioBtnCash, radioBtnPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, ivOtherModesToPay, imageViewRadioMpesa,imageViewRadioStripeCard);
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
            Data.autoData.setPickupPaymentOption(MyApplication.getInstance().getWalletCore()
                    .getPaymentOptionAccAvailability(Data.autoData.getPickupPaymentOption()));

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));

            textViewFreeChargeValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getFreeChargeBalanceStr()));
            textViewFreeChargeValue.setTextColor(Data.userData.getFreeChargeBalanceColor(activity));

//            textViewMpesaValue.setText(String.format(activity.getResources()
//                    .getString(R.string.rupees_value_format), Data.userData.getFreeChargeBalanceStr()));
//            textViewMpesaValue.setTextColor(Data.userData.getFreeChargeBalanceColor(activity));

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
//            if (Data.userData.getFreeChargeEnabled() == 1) {
//                textViewMpesaValue.setVisibility(View.VISIBLE);
//                textViewMpesa.setText(activity.getResources().getString(R.string.freecharge_wallet));
//            } else {
//                textViewMpesaValue.setVisibility(View.GONE);
//                textViewMpesa.setText(activity.getResources().getString(R.string.add_freecharge_wallet));
//            }

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
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
                                && Data.autoData != null && Data.autoData.isRazorpayEnabled()) {
                            linearLayoutWalletContainer.addView(llOtherModesToPay);
                            tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutStripeCard);
                            if(paymentModeConfigData.getCardsData()!=null && paymentModeConfigData.getCardsData().size()>0){
                               WalletCore.getStripeCardDisplayString(activity,paymentModeConfigData.getCardsData().get(0),textViewStripeCard,ivStripeCardIcon);
                            }else{
                                ivStripeCardIcon.setImageResource(R.drawable.ic_card_default);
                                textViewStripeCard.setText(activity.getString(R.string.add_card_payments));
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