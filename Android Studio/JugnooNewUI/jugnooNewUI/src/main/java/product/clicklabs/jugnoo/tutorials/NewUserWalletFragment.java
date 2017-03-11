package product.clicklabs.jugnoo.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.wallet.WalletCore;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserWalletFragment extends Fragment {

    private View root;
    private RelativeLayout rlRoot;
    private Button bFinish;
    private NewUserChutiyapaa activity;
    private ImageView ivAddPaytm, ivAddMobikwik, ivAddFreecharge;
    private TextView tvPaytmBalanceValue, tvMobikwikBalanceValue, tvFreeChargeBalanceValue;
    private WalletCore walletCore;

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
        activity = (NewUserChutiyapaa) getActivity();

        try {
            if (rlRoot != null) {
                new ASSL(getActivity(), rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bFinish = (Button) root.findViewById(R.id.bFinish);
        ivAddPaytm = (ImageView) root.findViewById(R.id.ivAddPaytm);
        ivAddMobikwik = (ImageView) root.findViewById(R.id.ivAddMobikwik);
        ivAddFreecharge = (ImageView) root.findViewById(R.id.ivAddFreecharge);
        tvPaytmBalanceValue = (TextView) root.findViewById(R.id.tvPaytmBalanceValue);
        tvMobikwikBalanceValue = (TextView) root.findViewById(R.id.tvMobikwikBalanceValue);
        tvFreeChargeBalanceValue = (TextView) root.findViewById(R.id.tvFreeChargeBalanceValue);
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

        activity.getTvTitle().setText(activity.getResources().getString(R.string.connect_wallet));
        activity.setTickLineView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setWalletStatus();
    }

    private void setWalletStatus(){
        if(Data.userData.getPaytmEnabled() == 1){
            ivAddPaytm.setVisibility(View.GONE);
            tvPaytmBalanceValue.setVisibility(View.VISIBLE);
            tvPaytmBalanceValue.setText(getString(R.string.rupees_minus_value_format_without_space, String.valueOf(Data.userData.getPaytmBalance())));
        } else{
            ivAddPaytm.setVisibility(View.VISIBLE);
            tvPaytmBalanceValue.setVisibility(View.GONE);
        }
        if(Data.userData.getMobikwikEnabled() == 1){
            ivAddMobikwik.setVisibility(View.GONE);
            tvMobikwikBalanceValue.setVisibility(View.VISIBLE);
            tvMobikwikBalanceValue.setText(getString(R.string.rupees_minus_value_format_without_space, String.valueOf(Data.userData.getMobikwikBalance())));
        } else{
            ivAddMobikwik.setVisibility(View.VISIBLE);
            tvMobikwikBalanceValue.setVisibility(View.GONE);
        }
        if(Data.userData.getFreeChargeEnabled() == 1){
            ivAddFreecharge.setVisibility(View.GONE);
            tvFreeChargeBalanceValue.setVisibility(View.VISIBLE);
            tvFreeChargeBalanceValue.setText(getString(R.string.rupees_minus_value_format_without_space, String.valueOf(Data.userData.getFreeChargeBalance())));
        } else{
            ivAddFreecharge.setVisibility(View.VISIBLE);
            tvFreeChargeBalanceValue.setVisibility(View.GONE);
        }
    }
}
