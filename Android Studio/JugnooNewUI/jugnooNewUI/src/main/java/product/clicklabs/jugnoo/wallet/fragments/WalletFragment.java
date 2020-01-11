package product.clicklabs.jugnoo.wallet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HelpParticularActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.stripe.StripeAddCardFragment;
import product.clicklabs.jugnoo.stripe.StripeViewCardFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;


public class WalletFragment extends Fragment implements GAAction, GACategory {
	
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
    TextView textViewFreeCharge, textViewFreeChargeBalanceValue,textViewStripeCard,textViewAcceptCard,textViewPayStackCard;

    RelativeLayout relativeLayoutWalletTransactions, relativeLayoutPayTransactions;
	private PaymentModeConfigData stripeConfigData;
	private PaymentModeConfigData acceptCardConfigData;
	private PaymentModeConfigData payStackConfigData;


    View rootView;
    private PaymentActivity paymentActivity;
	private RelativeLayout relativeLayoutStripe,relativeLayoutAcceptCard,relativeLayoutPayStack;
	private Handler handler = new Handler();
	private WalletFragmentListener walletFragmentListener;

	private Runnable enableStripeRunnable = new Runnable() {
		@Override
		public void run() {
			relativeLayoutStripe.setEnabled(true);
		}
	};
	private Runnable enableAcceptCardRunnable = new Runnable() {
		@Override
		public void run() {
			relativeLayoutAcceptCard.setEnabled(true);
		}
	};private Runnable enablePayStackRunnable = new Runnable() {
		@Override
		public void run() {
			relativeLayoutPayStack.setEnabled(true);
		}
	};
	public static WalletFragment newInstance(){
		WalletFragment fragment = new WalletFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	public interface WalletFragmentListener{

		void openPayStackAddCardFragment();
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof WalletFragmentListener){
			walletFragmentListener = (WalletFragmentListener) context;
		}else{
			throw new IllegalArgumentException("Fragment Listener not implemented");
		}
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        paymentActivity = (PaymentActivity) getActivity();

		GAUtils.trackScreenView(WALLET);

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);

		textViewStripeCard= (TextView) rootView.findViewById(R.id.textViewCardsStripe);
		textViewAcceptCard= (TextView) rootView.findViewById(R.id.textViewCardsAcceptCard);
		textViewPayStackCard= (TextView) rootView.findViewById(R.id.textViewCardsPayStack);
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(paymentActivity));
		
		textViewPromotion = (TextView) rootView.findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Fonts.mavenMedium(paymentActivity));

		linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
		relativeLayoutJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWallet);
		((TextView)rootView.findViewById(R.id.textViewJugnooCashBalance)).setTypeface(Fonts.mavenRegular(paymentActivity));
		((TextView)rootView.findViewById(R.id.textViewJugnooCashBalance)).setText(getString(R.string.jugnoo_cash, getString(R.string.app_name)));
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

		relativeLayoutPayTransactions = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPayTransactions);
		relativeLayoutStripe = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutStripe);
		relativeLayoutAcceptCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAcceptCard);
		relativeLayoutPayStack = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPayStack);
		((TextView) rootView.findViewById(R.id.textViewPayTransactions)).setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewStripeCard.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewAcceptCard.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewPayStackCard.setTypeface(Fonts.mavenRegular(paymentActivity));
        relativeLayoutWalletTransactions = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWalletTransactions);
		((TextView) rootView.findViewById(R.id.textViewWalletTransactions)).setTypeface(Fonts.mavenRegular(paymentActivity));
		relativeLayoutPayTransactions.setVisibility(View.GONE);

		textViewPromotion.setVisibility(View.GONE);

		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_WALLET);
		//textViewTitle.getPaint().setShader(FeedUtils.textColorGradient(getActivity(), textViewTitle));


        imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				paymentActivity.finish();
				paymentActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});

		relativeLayoutJugnooCash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(!HomeActivity.checkIfUserDataNull(paymentActivity)) {
                        if(Data.userData.getTopupCardEnabled() == 1) {
                            paymentActivity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                    .add(R.id.fragLayout, new WalletTopupFragment(), WalletTopupFragment.class.getName())
                                    .addToBackStack(WalletTopupFragment.class.getName())
                                    .hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
                                            .getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                                    .commit();
                        } else{
                                DialogPopup.alertPopupLeftOriented(paymentActivity, "", Data.userData.getJugnooCashTNC(), true, false, false);
                        }
						GAUtils.event(SIDE_MENU, GAAction.WALLET, JUGNOO+GAAction.CASH+TOPUP);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		relativeLayoutPaytm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				walletOnClick(PaymentOption.PAYTM.getOrdinal());
				GAUtils.event(SIDE_MENU, GAAction.WALLET+GAAction.WALLET+SELECTED, GAAction.PAYTM);
			}
		});

		relativeLayoutMobikwik.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				walletOnClick(PaymentOption.MOBIKWIK.getOrdinal());
				GAUtils.event(SIDE_MENU, GAAction.WALLET+GAAction.WALLET+SELECTED, GAAction.MOBIKWIK);
			}
		});

        relativeLayoutFreeCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				walletOnClick(PaymentOption.FREECHARGE.getOrdinal());
				GAUtils.event(SIDE_MENU, GAAction.WALLET+GAAction.WALLET+SELECTED, GAAction.FREECHARGE);
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
						.add(R.id.fragLayout, WalletTransactionsFragment.newInstance(0), WalletTransactionsFragment.class.getName())
						.addToBackStack(WalletTransactionsFragment.class.getName())
						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commit();
				GAUtils.event(SIDE_MENU, GAAction.WALLET, GAAction.WALLET+TRANSACTIONS+CLICKED);
			}
		});

		relativeLayoutPayTransactions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paymentActivity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.add(R.id.fragLayout, WalletTransactionsFragment.newInstance(1), WalletTransactionsFragment.class.getName())
						.addToBackStack(WalletTransactionsFragment.class.getName())
						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commit();
			}
		});

		View.OnClickListener cardsClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				view.setEnabled(false);
				PaymentModeConfigData configData;
				PaymentOption paymentOption;
				Runnable runnable;
				if(view.getId()==R.id.relativeLayoutStripe){
					runnable= enableStripeRunnable;
					configData= WalletFragment.this.stripeConfigData;
					paymentOption = PaymentOption.STRIPE_CARDS;

				}else if(view.getId() == R.id.relativeLayoutAcceptCard){
					runnable = enableAcceptCardRunnable;
					configData= WalletFragment.this.acceptCardConfigData;
					paymentOption = PaymentOption.ACCEPT_CARD;

				}else{
					runnable = enablePayStackRunnable;
					configData= WalletFragment.this.payStackConfigData;
					paymentOption = PaymentOption.PAY_STACK_CARD;

				}
				handler.postDelayed(runnable,300);


				if(configData==null)return;
				boolean openViewCardScreen = configData.getCardsData()!=null && configData.getCardsData().size()>0;
				if(!openViewCardScreen){

					if(paymentOption==PaymentOption.PAY_STACK_CARD){
						walletFragmentListener.openPayStackAddCardFragment();
						return;
					}

					paymentActivity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(R.id.fragLayout,  StripeAddCardFragment.newInstance(paymentOption), StripeAddCardFragment.class.getName())
							.addToBackStack(StripeAddCardFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
				}else{
					paymentActivity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(R.id.fragLayout,  StripeViewCardFragment.newInstance(configData.getCardsData(),paymentOption),
									StripeViewCardFragment.class.getName()).addToBackStack(StripeViewCardFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
				}

			}
		};
		relativeLayoutStripe.setOnClickListener(cardsClickListener);
		relativeLayoutAcceptCard.setOnClickListener(cardsClickListener);
		relativeLayoutPayStack.setOnClickListener(cardsClickListener);


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
							.add(R.id.fragLayout, WalletRechargeFragment.newInstance(paymentOption), WalletRechargeFragment.class.getName())
							.addToBackStack(WalletRechargeFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
				} else{
					paymentActivity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(R.id.fragLayout, AddWalletFragment.newInstance(paymentOption), AddWalletFragment.class.getName())
							.addToBackStack(AddWalletFragment.class.getName())
							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commit();
				}

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
				textViewJugnooCashBalanceValue.setText(!getResources().getBoolean(R.bool.wallet_amount_sidemenu)?Utils.getMoneyDecimalFormat().format(Data.userData.getJugnooBalance()):String.format(getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(Data.userData.getJugnooBalance())));
				textViewJugnooCashBalanceValue.setTextColor(Data.userData.getJugnooBalanceColor(paymentActivity));

				if(Data.userData.getPaytmEnabled() == 1){
					textViewPaytmBalance.setText(getResources().getString(R.string.paytm_wallet));
					textViewPaytmBalanceValue.setVisibility(View.VISIBLE);
					textViewPaytmBalanceValue.setText(String.format(paymentActivity.getResources()
							.getString(R.string.rupees_value_format), Data.userData.getPaytmBalanceStr()));
					textViewPaytmBalanceValue.setTextColor(Data.userData.getPaytmBalanceColor(paymentActivity));
				} else{
					textViewPaytmBalance.setText(getResources().getString(R.string.nl_add_paytm_wallet));
					textViewPaytmBalanceValue.setVisibility(View.GONE);
				}

				if(Data.userData.getMobikwikEnabled() == 1){
					textViewMobiKwik.setText(getResources().getString(R.string.mobikwik_wallet));
					textViewMobiKwikBalanceValue.setVisibility(View.VISIBLE);
					textViewMobiKwikBalanceValue.setText(String.format(paymentActivity.getResources()
							.getString(R.string.rupees_value_format), Data.userData.getMobikwikBalanceStr()));
					textViewMobiKwikBalanceValue.setTextColor(Data.userData.getMobikwikBalanceColor(paymentActivity));
				} else{
					textViewMobiKwik.setText(getResources().getString(R.string.add_mobikwik_wallet));
					textViewMobiKwikBalanceValue.setVisibility(View.GONE);
				}

                if(Data.userData.getFreeChargeEnabled() == 1){
                    textViewFreeCharge.setText(getResources().getString(R.string.freecharge_wallet));
                    textViewFreeChargeBalanceValue.setVisibility(View.VISIBLE);
                    textViewFreeChargeBalanceValue.setText(String.format(paymentActivity.getResources()
                            .getString(R.string.rupees_value_format), Data.userData.getFreeChargeBalanceStr()));
                    textViewFreeChargeBalanceValue.setTextColor(Data.userData.getFreeChargeBalanceColor(paymentActivity));
                } else{
                    textViewFreeCharge.setText(getResources().getString(R.string.add_freecharge_wallet));
                    textViewFreeChargeBalanceValue.setVisibility(View.GONE);
                }

			}
		} catch(Exception e){
			e.printStackTrace();
		}
		try{
			if(Data.userData.getPayEnabled() == 1
					&& Data.getPayData() != null
					&& Data.getPayData().getPay() != null
					&& Data.getPayData().getPay().getHasVpa() == 1){
				relativeLayoutPayTransactions.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutPayTransactions.setVisibility(View.GONE);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		relativeLayoutWalletTransactions.setVisibility(Prefs.with(paymentActivity).getInt(Constants.KEY_CUSTOMER_SHOW_WALLET_TRANSACTIONS, 1) == 1 ? View.VISIBLE : View.GONE);
		relativeLayoutJugnooCash.setVisibility(Prefs.with(paymentActivity).getInt(Constants.KEY_CUSTOMER_SHOW_WALLET_CASH, 1) == 1 ? View.VISIBLE : View.GONE);
	}


	private void orderPaymentModes(){
		try{
			ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
			if(paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0){
				linearLayoutWalletContainer.removeAllViews();
				for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
					if(paymentModeConfigData.getEnabled() == 1) {
						if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
							linearLayoutWalletContainer.addView(relativeLayoutPaytm);
						} else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
							linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
						} else if(paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        }else if(paymentModeConfigData.getPaymentOption()==PaymentOption.STRIPE_CARDS.getOrdinal()){
							linearLayoutWalletContainer.addView(relativeLayoutStripe);
							setCardsPaymentUI(paymentModeConfigData);

						}else if(paymentModeConfigData.getPaymentOption()==PaymentOption.ACCEPT_CARD.getOrdinal()){
							linearLayoutWalletContainer.addView(relativeLayoutAcceptCard);
							setCardsPaymentUI(paymentModeConfigData);

						}else if(paymentModeConfigData.getPaymentOption()==PaymentOption.PAY_STACK_CARD.getOrdinal()){
							linearLayoutWalletContainer.addView(relativeLayoutPayStack);
							setCardsPaymentUI(paymentModeConfigData);

						} else if(paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()){
							if(Prefs.with(paymentActivity).getInt(Constants.KEY_SHOW_JUGNOO_CASH_IN_WALLET, 1) == 1) {
								linearLayoutWalletContainer.addView(relativeLayoutJugnooCash);
							}
						}
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	public void setCardsPaymentUI(PaymentModeConfigData paymentModeConfigData) {

		if (paymentModeConfigData.getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
			this.stripeConfigData = paymentModeConfigData;

			if (paymentModeConfigData.getCardsData() != null && paymentModeConfigData.getCardsData().size() > 0) {
				textViewStripeCard.setText(getString(R.string.view_cards));
			} else {
				textViewStripeCard.setText(getString(R.string.action_add_card_stripe));
			}
		} else if(paymentModeConfigData.getPaymentOption() == PaymentOption.ACCEPT_CARD.getOrdinal()){
			this.acceptCardConfigData = paymentModeConfigData;
			if (paymentModeConfigData.getCardsData() != null && paymentModeConfigData.getCardsData().size() > 0) {
				textViewAcceptCard.setText(getString(R.string.view_card_wallet));
			} else {
				textViewAcceptCard.setText(getString(R.string.action_add_card_accept_card));
			}

		}else {
			this.payStackConfigData = paymentModeConfigData;
			if (paymentModeConfigData.getCardsData() != null && paymentModeConfigData.getCardsData().size() > 0) {
				textViewPayStackCard.setText(getString(R.string.view_card_wallet));
			} else {
				textViewPayStackCard.setText(getString(R.string.action_add_card_pay_stack));
			}

		}

	}

	@Override
	public void onDetach() {
		super.onDetach();
		walletFragmentListener = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}



}
