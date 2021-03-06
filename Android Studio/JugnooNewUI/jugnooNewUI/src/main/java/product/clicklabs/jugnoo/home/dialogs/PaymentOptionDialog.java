package product.clicklabs.jugnoo.home.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.CorporatesAdapter;
import product.clicklabs.jugnoo.adapters.StripeCardAdapter;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.model.Corporate;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
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
    private LinearLayout relativeLayoutAcceptCard, relativeLayoutPayStack;
    private RelativeLayout relativeLayoutMobikwik;
    private RelativeLayout relativeLayoutFreeCharge;
    private RelativeLayout relativeLayoutMpesa;
    private LinearLayout linearLayoutCash, llOtherModesToPay, llCorporate, llPos;
    private ImageView radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash, imageViewRadioFreeCharge, ivOtherModesToPay,
            imageViewRadioMpesa, imageViewRadioStripeCard, ivStripeCardIcon, imageViewRadioAcceptCard, ivAcceptCardIcon,
            imageViewRadioPayStack, ivPayStackIcon, ivCorporate, ivPos;

    private TextView textViewPayForRides, textViewPaytm, textViewStripeCard, textViewAcceptCard, textViewPayStack, textViewPaytmValue, textViewMobikwik,
            textViewMobikwikValue, textViewFreeCharge, textViewFreeChargeValue, tvOtherModesToPay, textViewMpesa,
            textViewMpesaValue;
    private RecyclerView rvCorporates, rvStripeCards,rvPayStack;
    private CorporatesAdapter corporatesAdapter;
    private StripeCardAdapter stripeCardAdapter,paystackCardAdapter;

    public PaymentOptionDialog(Activity activity, CallbackPaymentOptionSelector callbackPaymentOptionSelector, Callback callback) {
        this.activity = activity;
        this.callback = callback;
        this.callbackPaymentOptionSelector = callbackPaymentOptionSelector;
    }


    private Activity handleActivityCasting(Activity activity) {

        if(activity instanceof HomeActivity){
            return (HomeActivity)activity;
        }
        if(activity instanceof FreshActivity) {
            return (FreshActivity)activity;
        }
        else return activity;

    }


    public PaymentOptionDialog show(int paymentOption, String title) {
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
            textViewPayForRides = (TextView) dialog.findViewById(R.id.textViewPayForRides);
            textViewPayForRides.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            if(!TextUtils.isEmpty(title)){
                textViewPayForRides.setText(title);
            }

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
            llPos = dialog.findViewById(R.id.llPos);
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
            ivPos = dialog.findViewById(R.id.ivPos);
            rvCorporates = dialog.findViewById(R.id.rvCorporates);
            rvCorporates.setLayoutManager(new LinearLayoutManager(activity));
            rvCorporates.setHasFixedSize(false);
            rvStripeCards = dialog.findViewById(R.id.rvStripeCards);
            rvPayStack = dialog.findViewById(R.id.rvPayStack);
            rvStripeCards.setLayoutManager(new LinearLayoutManager(activity));
            rvStripeCards.setHasFixedSize(false);
            rvPayStack.setLayoutManager(new LinearLayoutManager(activity));
            rvPayStack.setHasFixedSize(false);


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
            llPos.setOnClickListener(this);

            orderPaymentModes(paymentOption);

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
                case R.id.llPos:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.POS, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutFreeCharge:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE, callbackPaymentOptionSelector);
                    callback.onPaymentModeUpdated();
                    break;
                case R.id.relativeLayoutStripeCard:
                    MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.STRIPE_CARDS, callbackPaymentOptionSelector);
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
            int selectedPaymentOption = callbackPaymentOptionSelector.getSelectedPaymentOption();
            if(activity instanceof HomeActivity) {
				ArrayList<Region> regions = Data.autoData.getRegions();
                Region region = (regions.size() > 1) ? ((HomeActivity) activity).slidingBottomPanel.getRequestRideOptionsFragment().getRegionSelected()
                        : (regions.size() > 0 ? regions.get(0) : null);
                if (region != null &&(region.getRestrictedPaymentModes().size() > 0||region.getRestrictedCorporates().size() > 0)) {
                    if (region.getRestrictedPaymentModes().contains(selectedPaymentOption)) {
                        callbackPaymentOptionSelector.setSelectedPaymentOption(HomeUtil.chooseNextEligiblePaymentOption(callbackPaymentOptionSelector.getSelectedPaymentOption(), (HomeActivity) activity));
                    }else if(selectedPaymentOption==PaymentOption.CORPORATE.getOrdinal()&&region.getRestrictedCorporates().contains(CorporatesAdapter.Companion.getSelectedCorporateBusinessId())){
                        callbackPaymentOptionSelector.setSelectedPaymentOption(HomeUtil.chooseNextEligiblePaymentOption(callbackPaymentOptionSelector.getSelectedPaymentOption(), (HomeActivity) activity));
                    }
                    else {
                        callbackPaymentOptionSelector.setSelectedPaymentOption(selectedPaymentOption);
                    }
                } else {
                    callbackPaymentOptionSelector.setSelectedPaymentOption(selectedPaymentOption);
                }
            } else {
                callbackPaymentOptionSelector.setSelectedPaymentOption(selectedPaymentOption);
            }




            if (corporatesAdapter != null) {
                corporatesAdapter.unSelectAll();
            }
            if (stripeCardAdapter != null) {
                stripeCardAdapter.unSelectAll();
            }

            if (PaymentOption.PAYTM.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(radioBtnPaytm, imageViewRadioMobikwik, radioBtnCash, imageViewRadioFreeCharge,
                        ivOtherModesToPay, imageViewRadioMpesa, imageViewRadioStripeCard, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
            } else if (PaymentOption.MOBIKWIK.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash, imageViewRadioFreeCharge,
                        ivOtherModesToPay, imageViewRadioMpesa, imageViewRadioStripeCard, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
            } else if (PaymentOption.FREECHARGE.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm, radioBtnCash,
                        ivOtherModesToPay, imageViewRadioMpesa, imageViewRadioStripeCard, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
            } else if (PaymentOption.MPESA.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioFreeCharge, imageViewRadioMobikwik,
                        radioBtnPaytm, radioBtnCash, imageViewRadioStripeCard, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
            } else if (PaymentOption.RAZOR_PAY.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(ivOtherModesToPay, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa, imageViewRadioStripeCard, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
            } else if (PaymentOption.STRIPE_CARDS.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentClearSelection(imageViewRadioStripeCard, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
                if (stripeCardAdapter != null) {
                    stripeCardAdapter.selectDefault();
                }
            } else if (PaymentOption.ACCEPT_CARD.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioAcceptCard, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioStripeCard, imageViewRadioPayStack, ivCorporate, ivPos);
            } else if (PaymentOption.PAY_STACK_CARD.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(imageViewRadioPayStack, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioStripeCard, imageViewRadioAcceptCard, ivCorporate, ivPos);
            } else if (PaymentOption.POS.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(ivPos, imageViewRadioPayStack, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioStripeCard, imageViewRadioAcceptCard, ivCorporate);
            } else if (PaymentOption.CORPORATE.getOrdinal() == callbackPaymentOptionSelector.getSelectedPaymentOption()) {
                paymentSelection(ivCorporate, imageViewRadioPayStack, imageViewRadioFreeCharge, imageViewRadioMobikwik, radioBtnPaytm,
                        radioBtnCash, imageViewRadioMpesa, ivOtherModesToPay, imageViewRadioStripeCard, imageViewRadioAcceptCard, ivPos);
                if (corporatesAdapter != null) {
                    corporatesAdapter.selectDefault();
                }
            } else {
                paymentSelection(radioBtnCash, radioBtnPaytm, imageViewRadioMobikwik, imageViewRadioFreeCharge, ivOtherModesToPay,
                        imageViewRadioMpesa, imageViewRadioStripeCard, imageViewRadioAcceptCard, imageViewRadioPayStack, ivCorporate, ivPos);
            }
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paymentSelection(ImageView selected, ImageView... unSelectedImageViews) {
        try {
            selected.setImageResource(R.drawable.ic_radio_button_selected);
            for (ImageView unselected : unSelectedImageViews) {
                unselected.setImageResource(R.drawable.ic_radio_button_normal);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void paymentClearSelection(ImageView... unSelectedImageViews) {
        try {
            for (ImageView unselected : unSelectedImageViews) {
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

    public void orderPaymentModes(int paymentOption) {
        try {
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
            if (paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0) {
                linearLayoutWalletContainer.removeAllViews();
                List<Integer> restrictedPaymentMode = new ArrayList<>();
                List<Integer> restrictedCorporates = new ArrayList<>();

                if(activity instanceof HomeActivity) {
					ArrayList<Region> regions = Data.autoData.getRegions();
                    if (regions.size() > 1) {
                        restrictedPaymentMode = ((HomeActivity) activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRestrictedPaymentModes();
                    } else if (regions.size() > 0) {
                        restrictedPaymentMode = regions.get(0).getRestrictedPaymentModes();
                    }

                    if (regions.size() > 1) {
                        restrictedCorporates = ((HomeActivity) activity).getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRestrictedCorporates();
                    } else if (regions.size() > 0) {
                        restrictedCorporates = regions.get(0).getRestrictedCorporates();
                    }
                }
                for (PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas) {
                    if (paymentModeConfigData.getEnabled() == 1) {

                        if ((restrictedPaymentMode.size() > 0 && !restrictedPaymentMode.contains(paymentModeConfigData.getPaymentOption())) || restrictedPaymentMode.size() == 0) {

                            if (paymentOption != -1 && paymentOption != paymentModeConfigData.getPaymentOption()) {
                                continue;
                            }

                            if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                                linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                                linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                                linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MPESA.getOrdinal()) {
                                linearLayoutWalletContainer.addView(relativeLayoutMpesa);
                                textViewMpesa.setText(paymentModeConfigData.getDisplayName());
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
                                linearLayoutWalletContainer.addView(linearLayoutCash);
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.POS.getOrdinal()) {
                                linearLayoutWalletContainer.addView(llPos);
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CORPORATE.getOrdinal()) {



                                if ( paymentModeConfigData.getCorporates() != null) {
                                    ArrayList<Corporate> allowedCorporates = new ArrayList<>();

                                    for (int i = 0; i <paymentModeConfigData.getCorporates().size() ; i++) {
                                        if(!restrictedCorporates.contains(paymentModeConfigData.getCorporates().get(i).getBusinessId())){
                                            allowedCorporates.add(paymentModeConfigData.getCorporates().get(i));
                                        }
                                    }

                                    if(allowedCorporates.size()>0) {
                                        linearLayoutWalletContainer.addView(llCorporate);
                                        linearLayoutWalletContainer.addView(rvCorporates);
//                                        if(corporatesAdapter == null) {
                                            corporatesAdapter = new CorporatesAdapter(allowedCorporates,
                                                    rvCorporates, Fonts.mavenLight(activity), new CorporatesAdapter.OnSelectedCallback() {
                                                @Override
                                                public void onItemSelected(@NotNull Corporate corporate) {
                                                    if (Data.autoData.getPickupPaymentOption() != PaymentOption.CORPORATE.getOrdinal()) {
                                                        onClick(llCorporate);
                                                    } else {
                                                        dismiss();
                                                    }
                                                }
                                            });
                                            rvCorporates.setAdapter(corporatesAdapter);
//                                        }
//                                        else {
//                                            corporatesAdapter.setList(allowedCorporates);
//                                        }

                                    }
                                }


                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()
                                    && callbackPaymentOptionSelector.isRazorpayEnabled()) {
                                linearLayoutWalletContainer.addView(llOtherModesToPay);
                                tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());

                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
                                if (paymentModeConfigData.getCardsData() != null) {
                                    linearLayoutWalletContainer.addView(rvStripeCards);
                                    stripeCardAdapter = new StripeCardAdapter(paymentModeConfigData.getCardsData(),
                                            rvStripeCards, Fonts.mavenLight(activity), new StripeCardAdapter.OnSelectedCallback() {
                                        @Override
                                        public void onItemSelected(@NotNull StripeCardData stripeCards, int pos) {
                                            if (dialog != null && dialog.isShowing()) {
                                                dialog.dismiss();
                                                Prefs.with(activity).save(Constants.STRIPE_SELECTED_POS, stripeCards.getCardId());
                                                callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.STRIPE_CARDS);
                                                callback.onPaymentModeUpdated();
                                            }
                                            Log.e("check", stripeCards.getCardId());
                                        }
                                    }, activity);
                                    rvStripeCards.setAdapter(stripeCardAdapter);
                                    stripeCardAdapter.selectDefault();
                                }
                                linearLayoutWalletContainer.addView(relativeLayoutStripeCard);
                                ivStripeCardIcon.setImageResource(R.drawable.ic_card_default);
                                textViewStripeCard.setText(activity.getString(R.string.action_add_card_stripe));
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.ACCEPT_CARD.getOrdinal()) {
                                linearLayoutWalletContainer.addView(relativeLayoutAcceptCard);
                                if (paymentModeConfigData.getCardsData() != null && paymentModeConfigData.getCardsData().size() > 0) {
                                    WalletCore.getStripeCardDisplayString(activity, paymentModeConfigData.getCardsData().get(0), textViewAcceptCard, ivAcceptCardIcon);
                                } else {
                                    ivAcceptCardIcon.setImageResource(R.drawable.ic_card_default);
                                    textViewAcceptCard.setText(activity.getString(R.string.action_add_card_accept_card));
                                }
                            } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAY_STACK_CARD.getOrdinal()) {
                                if (paymentModeConfigData.getCardsData() != null) {
                                    linearLayoutWalletContainer.addView(rvPayStack);
                                    paystackCardAdapter = new StripeCardAdapter(paymentModeConfigData.getCardsData(),
                                            rvPayStack, Fonts.mavenLight(activity), new StripeCardAdapter.OnSelectedCallback() {
                                        @Override
                                        public void onItemSelected(@NotNull StripeCardData stripeCards, int pos) {
                                            if (dialog != null && dialog.isShowing()) {
                                                dialog.dismiss();
                                                Prefs.with(activity).save(Constants.STRIPE_SELECTED_POS, stripeCards.getCardId());
                                                callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.PAY_STACK_CARD);
                                                callback.onPaymentModeUpdated();
                                            }
                                            Log.e("check", stripeCards.getCardId());
                                        }
                                    }, activity);
                                    rvPayStack.setAdapter(paystackCardAdapter);
                                    paystackCardAdapter.selectDefault();
                                }
                                linearLayoutWalletContainer.addView(relativeLayoutPayStack);
                                ivPayStackIcon.setImageResource(R.drawable.ic_card_default);
                                textViewPayStack.setText(activity.getString(R.string.action_add_card_stripe));
                            }

                            if (paymentOption == paymentModeConfigData.getPaymentOption()) {
                                break;
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
        void getSelectedPaymentOption();

    }

}