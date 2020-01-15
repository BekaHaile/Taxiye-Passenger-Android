package product.clicklabs.jugnoo.tutorials;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.WalletCore;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserWalletFragment extends Fragment {

    private View root;
    private RelativeLayout rlRoot;
    private Button bFinish;
    private NewUserFlow activity;
    private ImageView ivAddPaytm, ivAddMobikwik, ivAddFreecharge;
    private TextView tvPaytmBalanceValue, tvMobikwikBalanceValue, tvFreeChargeBalanceValue, tvPaytmOffer, tvMobikwikOffer, tvFreeChargeOffer;
    private WalletCore walletCore;
    private String paytmOfferText, mobikwikOfferText, freechargeOfferText;
    private RelativeLayout rlPaytm, rlMobikwik, rlFreeCharge;
    private LinearLayout llWalletContainer;

    public static NewUserWalletFragment newInstance() {
        Bundle bundle = new Bundle();
        NewUserWalletFragment fragment = new NewUserWalletFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_new_user_wallet, container, false);
        rlRoot = (RelativeLayout) root.findViewById(R.id.rlRoot);
        activity = (NewUserFlow) getActivity();

        try {
            if (rlRoot != null) {
                new ASSL(getActivity(), rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bFinish = (Button) root.findViewById(R.id.bFinish);
        llWalletContainer = (LinearLayout) root.findViewById(R.id.llWalletContainer);
        rlPaytm = (RelativeLayout) root.findViewById(R.id.rlPaytm);
        rlMobikwik = (RelativeLayout) root.findViewById(R.id.rlMobikwik);
        rlFreeCharge = (RelativeLayout) root.findViewById(R.id.rlFreeCharge);
        ivAddPaytm = (ImageView) root.findViewById(R.id.ivAddPaytm);
        ivAddMobikwik = (ImageView) root.findViewById(R.id.ivAddMobikwik);
        ivAddFreecharge = (ImageView) root.findViewById(R.id.ivAddFreecharge);
        tvPaytmBalanceValue = (TextView) root.findViewById(R.id.tvPaytmBalanceValue);
        tvMobikwikBalanceValue = (TextView) root.findViewById(R.id.tvMobikwikBalanceValue);
        tvFreeChargeBalanceValue = (TextView) root.findViewById(R.id.tvFreeChargeBalanceValue);
        tvPaytmOffer = (TextView) root.findViewById(R.id.tvPaytmOffer);
        tvMobikwikOffer = (TextView) root.findViewById(R.id.tvMobikwikOffer);
        tvFreeChargeOffer = (TextView) root.findViewById(R.id.tvFreeChargeOffer);
        walletCore = new WalletCore(activity);

        bFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performBackPressed();
            }
        });

        ivAddPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletCore.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.PAYTM.getOrdinal());
            }
        });

        ivAddMobikwik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletCore.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.MOBIKWIK.getOrdinal());
            }
        });

        ivAddFreecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletCore.openPaymentActivityInCaseOfWalletNotAdded(activity, PaymentOption.FREECHARGE.getOrdinal());
            }
        });


        activity.getTvTitle().setText(activity.getResources().getString(R.string.connect_wallets));
        activity.setTickLineView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setWalletStatus();
    }

    private void setWalletStatus(){
        try{
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
            if(paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0){
                llWalletContainer.removeAllViews();
                for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
                    if(paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            paytmOfferText = paymentModeConfigData.getOfferText();
                            llWalletContainer.addView(rlPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            mobikwikOfferText = paymentModeConfigData.getOfferText();
                            llWalletContainer.addView(rlMobikwik);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            freechargeOfferText = paymentModeConfigData.getOfferText();
                            llWalletContainer.addView(rlFreeCharge);
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if(Data.userData.getPaytmEnabled() == 1){
            activity.getIvTickWallet().setImageResource(R.drawable.ic_bar_check);
            ivAddPaytm.setVisibility(View.GONE);
            tvPaytmBalanceValue.setVisibility(View.VISIBLE);
            tvPaytmBalanceValue.setText(getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(Data.userData.getPaytmBalance())));
        } else{
            ivAddPaytm.setVisibility(View.VISIBLE);
            tvPaytmBalanceValue.setVisibility(View.GONE);
        }
        if(paytmOfferText != null && !paytmOfferText.equalsIgnoreCase("")){
            tvPaytmOffer.setVisibility(View.VISIBLE);
            tvPaytmOffer.setText(paytmOfferText);
        } else{
            tvPaytmOffer.setVisibility(View.GONE);
        }

        if(Data.userData.getMobikwikEnabled() == 1){
            activity.getIvTickWallet().setImageResource(R.drawable.ic_bar_check);
            ivAddMobikwik.setVisibility(View.GONE);
            tvMobikwikBalanceValue.setVisibility(View.VISIBLE);
            tvMobikwikBalanceValue.setText(getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(Data.userData.getMobikwikBalance())));
        } else{
            ivAddMobikwik.setVisibility(View.VISIBLE);
            tvMobikwikBalanceValue.setVisibility(View.GONE);
        }
        if(mobikwikOfferText != null && !mobikwikOfferText.equalsIgnoreCase("")){
            tvMobikwikOffer.setVisibility(View.VISIBLE);
            tvMobikwikOffer.setText(mobikwikOfferText);
        } else{
            tvMobikwikOffer.setVisibility(View.GONE);
        }

        if(Data.userData.getFreeChargeEnabled() == 1){
            activity.getIvTickWallet().setImageResource(R.drawable.ic_bar_check);
            ivAddFreecharge.setVisibility(View.GONE);
            tvFreeChargeBalanceValue.setVisibility(View.VISIBLE);
            tvFreeChargeBalanceValue.setText(getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(Data.userData.getFreeChargeBalance())));
        } else{
            ivAddFreecharge.setVisibility(View.VISIBLE);
            tvFreeChargeBalanceValue.setVisibility(View.GONE);
        }
        if(freechargeOfferText != null && !freechargeOfferText.equalsIgnoreCase("")){
            tvFreeChargeOffer.setVisibility(View.VISIBLE);
            tvFreeChargeOffer.setText(freechargeOfferText);
        } else{
            tvFreeChargeOffer.setVisibility(View.GONE);
        }
    }
}
