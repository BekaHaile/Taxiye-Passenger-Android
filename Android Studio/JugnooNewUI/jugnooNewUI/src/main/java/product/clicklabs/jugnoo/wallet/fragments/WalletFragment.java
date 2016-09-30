package product.clicklabs.jugnoo.wallet.fragments;

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

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HelpParticularActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;


public class WalletFragment extends Fragment implements FlurryEventNames, FirebaseEvents {
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewPromotion;

	LinearLayout linearLayoutWalletContainer;
	RelativeLayout relativeLayoutJugnooCash;
	TextView textViewJugnooCashBalanceValue;
	RelativeLayout relativeLayoutPaytm;
	TextView textViewPaytmBalance, textViewPaytmBalanceValue;
	RelativeLayout relativeLayoutMobikwik;
	TextView textViewMobiKwik, textViewMobiKwikBalanceValue;

    RelativeLayout relativeLayoutFreeCharge;
    TextView textViewFreeCharge, textViewFreeChargeBalanceValue;

    RelativeLayout relativeLayoutWalletTransactions;


    View rootView;
    private PaymentActivity paymentActivity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(paymentActivity, Config.getFlurryKey());
        FlurryAgent.onStartSession(paymentActivity, Config.getFlurryKey());
        FlurryAgent.onEvent("Register started");
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(paymentActivity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        paymentActivity = (PaymentActivity) getActivity();


		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(paymentActivity));
		
		textViewPromotion = (TextView) rootView.findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Fonts.mavenMedium(paymentActivity));

		linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
		relativeLayoutJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWallet);
		((TextView)rootView.findViewById(R.id.textViewJugnooCashBalance)).setTypeface(Fonts.mavenRegular(paymentActivity));
		((TextView)rootView.findViewById(R.id.textViewJugnooCashTNC)).setTypeface(Fonts.mavenLight(paymentActivity));
		textViewJugnooCashBalanceValue = (TextView) rootView.findViewById(R.id.textViewJugnooCashBalanceValue);
		textViewJugnooCashBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));

		relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
		textViewPaytmBalance = (TextView)rootView.findViewById(R.id.textViewPaytmBalance); textViewPaytmBalance.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewPaytmBalanceValue = (TextView) rootView.findViewById(R.id.textViewPaytmBalanceValue); textViewPaytmBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));

		relativeLayoutMobikwik = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMobikwik);
		textViewMobiKwik = (TextView) rootView.findViewById(R.id.textViewMobikwik); textViewMobiKwik.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewMobiKwikBalanceValue = (TextView) rootView.findViewById(R.id.textViewMobikwikBalanceValue); textViewMobiKwikBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));

        relativeLayoutFreeCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFreeCharge);
        textViewFreeCharge = (TextView) rootView.findViewById(R.id.textViewFreeCharge); textViewFreeCharge.setTypeface(Fonts.mavenRegular(paymentActivity));
        textViewFreeChargeBalanceValue = (TextView) rootView.findViewById(R.id.textViewFreeChargeBalanceValue); textViewFreeChargeBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));


        relativeLayoutWalletTransactions = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWalletTransactions);
		((TextView) rootView.findViewById(R.id.textViewWalletTransactions)).setTypeface(Fonts.mavenRegular(paymentActivity));


		textViewPromotion.setVisibility(View.GONE);

		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_WALLET);
		textViewTitle.getPaint().setShader(Utils.textColorGradient(getActivity(), textViewTitle));


        imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.eventGA(Constants.REVENUE, "Wallet", "Back");
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+WALLET+"_"+BACK, bundle);
				paymentActivity.finish();
				paymentActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});

		relativeLayoutJugnooCash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!HomeActivity.checkIfUserDataNull(paymentActivity)) {
					paymentActivity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(R.id.fragLayout, new WalletTopupFragment(), WalletTopupFragment.class.getName())
							.addToBackStack(WalletTopupFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
					FlurryEventLogger.event(JUGNOO_CASH_CHECKED);
					Bundle bundle = new Bundle();
					MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+WALLET+"_"+JUGNOO_CASH, bundle);
					FlurryEventLogger.eventGA(Constants.REVENUE, "Wallet", "Jugnoo Cash");
				}
			}
		});

		relativeLayoutPaytm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				walletOnClick(PaymentOption.PAYTM.getOrdinal());
			}
		});

		relativeLayoutMobikwik.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				walletOnClick(PaymentOption.MOBIKWIK.getOrdinal());
			}
		});

        relativeLayoutFreeCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				walletOnClick(PaymentOption.FREECHARGE.getOrdinal());
            }
        });


        textViewPromotion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.WALLET_PROMOTIONS;
				startActivity(new Intent(paymentActivity, HelpParticularActivity.class));
                paymentActivity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

		relativeLayoutWalletTransactions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paymentActivity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.add(R.id.fragLayout, new WalletTransactionsFragment(), WalletTransactionsFragment.class.getName())
						.addToBackStack(WalletTransactionsFragment.class.getName())
						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commit();
				FlurryEventLogger.event(RECENT_TRANSACTIONS);
                Bundle bundle = new Bundle();
                MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+WALLET+"_"+VIEW_RECENT_TRANSACTION, bundle);
				FlurryEventLogger.eventGA(Constants.REVENUE, "Wallet", "View Recent Transaction");
			}
		});



		setUserWalletInfo();

        return rootView;
	}

	private void walletOnClick(int paymentOption){
		try {
			if(!HomeActivity.checkIfUserDataNull(paymentActivity)) {

				boolean walletAdded = false;
				if((paymentOption == PaymentOption.PAYTM.getOrdinal() && Data.userData.getPaytmEnabled() == 1)
						|| (paymentOption == PaymentOption.MOBIKWIK.getOrdinal() && Data.userData.getMobikwikEnabled() == 1)
						|| (paymentOption == PaymentOption.FREECHARGE.getOrdinal() && Data.userData.getFreeChargeEnabled() == 1)){
					walletAdded = true;
				}

				if(walletAdded){
					paymentActivity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(R.id.fragLayout, new WalletRechargeFragment(paymentOption), WalletRechargeFragment.class.getName())
							.addToBackStack(WalletRechargeFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
					FlurryEventLogger.event(PAYTM_WALLET_OPENED);
					FlurryEventLogger.event(paymentActivity, CLICKS_ON_PAYTM_WALLET);
				} else{
					paymentActivity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(R.id.fragLayout, new AddWalletFragment(paymentOption), AddWalletFragment.class.getName())
							.addToBackStack(AddWalletFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
					FlurryEventLogger.event(PAYTM_WALLET_ADD_CLICKED);
				}

				String suffix = "";
				if(paymentOption == PaymentOption.PAYTM.getOrdinal()){
					suffix = FirebaseEvents.PAYTM_WALLET;
				} else if(paymentOption == PaymentOption.MOBIKWIK.getOrdinal()){
					suffix = FirebaseEvents.MOBIKWIK_WALLET;
				} else if(paymentOption == PaymentOption.FREECHARGE.getOrdinal()){
					suffix = FirebaseEvents.FREECHARGE_WALLET;
				}
				MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.WALLET+"_"+suffix, new Bundle());
				NudgeClient.trackEventUserId(paymentActivity, FlurryEventNames.NUDGE_PAYTM_WALLET_CLICKED, null);
				FlurryEventLogger.eventGA(Constants.REVENUE, "Wallet", "Paytm Wallet");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkIfUserDataNull(paymentActivity);
		HomeActivity.checkForAccessTokenChange(paymentActivity);
		setUserWalletInfo();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			orderPaymentModes();
		}
	}

	private void setUserWalletInfo(){
		try{
			orderPaymentModes();
			if(Data.userData != null){
				textViewJugnooCashBalanceValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(Data.userData.getJugnooBalance())));
				textViewJugnooCashBalanceValue.setTextColor(Data.userData.getJugnooBalanceColor(paymentActivity));

				if(Data.userData.getPaytmEnabled() == 1){
					textViewPaytmBalance.setText(getResources().getString(R.string.paytm_wallet));
					textViewPaytmBalanceValue.setVisibility(View.VISIBLE);
					textViewPaytmBalanceValue.setText(String.format(paymentActivity.getResources()
							.getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
					textViewPaytmBalanceValue.setTextColor(Data.userData.getPaytmBalanceColor(paymentActivity));
				} else{
					textViewPaytmBalance.setText(getResources().getString(R.string.nl_add_paytm_wallet));
					textViewPaytmBalanceValue.setVisibility(View.GONE);
				}

				if(Data.userData.getMobikwikEnabled() == 1){
					textViewMobiKwik.setText(getResources().getString(R.string.mobikwik_wallet));
					textViewMobiKwikBalanceValue.setVisibility(View.VISIBLE);
					textViewMobiKwikBalanceValue.setText(String.format(paymentActivity.getResources()
							.getString(R.string.rupees_value_format_without_space), Data.userData.getMobikwikBalanceStr()));
					textViewMobiKwikBalanceValue.setTextColor(Data.userData.getMobikwikBalanceColor(paymentActivity));
				} else{
					textViewMobiKwik.setText(getResources().getString(R.string.add_mobikwik_wallet));
					textViewMobiKwikBalanceValue.setVisibility(View.GONE);
				}

                if(Data.userData.getFreeChargeEnabled() == 1){
                    textViewFreeCharge.setText(getResources().getString(R.string.freecharge_wallet));
                    textViewFreeChargeBalanceValue.setVisibility(View.VISIBLE);
                    textViewFreeChargeBalanceValue.setText(String.format(paymentActivity.getResources()
                            .getString(R.string.rupees_value_format_without_space), Data.userData.getFreeChargeBalanceStr()));
                    textViewFreeChargeBalanceValue.setTextColor(Data.userData.getFreeChargeBalanceColor(paymentActivity));
                } else{
                    textViewFreeCharge.setText(getResources().getString(R.string.add_freecharge_wallet));
                    textViewFreeChargeBalanceValue.setVisibility(View.GONE);
                }

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	private void orderPaymentModes(){
		try{
			ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas(Data.userData);
			if(paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0){
				linearLayoutWalletContainer.removeAllViews();
				linearLayoutWalletContainer.addView(relativeLayoutJugnooCash);
				for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
					if(paymentModeConfigData.getEnabled() == 1) {
						if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
							linearLayoutWalletContainer.addView(relativeLayoutPaytm);
						} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
							linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
						} else if(paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        }
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}
