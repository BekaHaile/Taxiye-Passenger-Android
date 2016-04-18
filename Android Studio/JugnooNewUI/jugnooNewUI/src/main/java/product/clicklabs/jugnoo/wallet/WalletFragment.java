package product.clicklabs.jugnoo.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HelpParticularActivity;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;


public class WalletFragment extends Fragment implements FlurryEventNames {
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewPromotion;

	RelativeLayout relativeLayoutJugnooCash;
	TextView textViewJugnooCashBalanceValue;

	RelativeLayout relativeLayoutPaytm;
	TextView textViewPaytmBalance, textViewPaytmBalanceValue;

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
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(paymentActivity));
		
		textViewPromotion = (TextView) rootView.findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Fonts.latoRegular(paymentActivity));

		relativeLayoutJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWallet);
		((TextView)rootView.findViewById(R.id.textViewJugnooCashBalance)).setTypeface(Fonts.mavenLight(paymentActivity));
		((TextView)rootView.findViewById(R.id.textViewJugnooCashTNC)).setTypeface(Fonts.mavenLight(paymentActivity));
		textViewJugnooCashBalanceValue = (TextView) rootView.findViewById(R.id.textViewJugnooCashBalanceValue);
		textViewJugnooCashBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));

		relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
		textViewPaytmBalance = (TextView)rootView.findViewById(R.id.textViewPaytmBalance); textViewPaytmBalance.setTypeface(Fonts.mavenLight(paymentActivity));
		textViewPaytmBalanceValue = (TextView) rootView.findViewById(R.id.textViewPaytmBalanceValue);
		textViewPaytmBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));


		relativeLayoutWalletTransactions = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWalletTransactions);
		((TextView) rootView.findViewById(R.id.textViewWalletTransactions)).setTypeface(Fonts.mavenLight(paymentActivity));


		textViewPromotion.setVisibility(View.GONE);


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

//				paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//						.add(R.id.fragLayout, new WalletAddPaymentFragment(), "WalletAddPaymentFragment").addToBackStack("WalletAddPaymentFragment")
//						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
//								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName())).commit();
//				FlurryEventLogger.event(ADDING_JUGNOO_CASH);

				if(!HomeActivity.checkIfUserDataNull(paymentActivity)) {
					DialogPopup.alertPopupLeftOriented(paymentActivity, "", Data.userData.getJugnooCashTNC());
					FlurryEventLogger.event(JUGNOO_CASH_CHECKED);
				}
			}
		});

		relativeLayoutPaytm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!HomeActivity.checkIfUserDataNull(paymentActivity)) {
					if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
						paymentActivity.getSupportFragmentManager().beginTransaction()
								.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
								.add(R.id.fragLayout, new PaytmRechargeFragment(""), PaytmRechargeFragment.class.getName())
								.addToBackStack(PaytmRechargeFragment.class.getName())
								.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
										.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
								.commit();
						FlurryEventLogger.event(PAYTM_WALLET_OPENED);
						FlurryEventLogger.event(paymentActivity, CLICKS_ON_PAYTM_WALLET);
					} else {
						paymentActivity.getSupportFragmentManager().beginTransaction()
								.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
								.add(R.id.fragLayout, new AddPaytmFragment(), AddPaytmFragment.class.getName())
								.addToBackStack(AddPaytmFragment.class.getName())
								.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
										.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
								.commit();
						FlurryEventLogger.event(PAYTM_WALLET_ADD_CLICKED);
					}
					NudgeClient.trackEventUserId(paymentActivity, FlurryEventNames.NUDGE_PAYMENT_WALLET_CLICKED, null);
				}
			}
		});

//		relativeLayoutAddPaytm.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(!Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
//					paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//							.add(R.id.fragLayout, new AddPaytmFragment(), AddPaytmFragment.class.getName())
//							.addToBackStack(AddPaytmFragment.class.getName())
//							.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
//									.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//							.commit();
//					FlurryEventLogger.event(PAYTM_WALLET_ADD_CLICKED);
//				}
//			}
//		});


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
			}
		});


		setUserWalletInfo();

        return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkIfUserDataNull(paymentActivity);
		HomeActivity.checkForAccessTokenChange(paymentActivity);
		setUserWalletInfo();
	}

	private void setUserWalletInfo(){
		try{
			if(Data.userData != null){
				textViewJugnooCashBalanceValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(Data.userData.getJugnooBalance())));
				textViewPaytmBalanceValue.setText(String.format(paymentActivity.getResources().getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
				if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
					showPaytmActiveUI();
				} else if(Data.userData.getPaytmStatus().equalsIgnoreCase("")){
					showPaytmActiveUI();
					textViewPaytmBalanceValue.setText(String.format(paymentActivity.getResources().getString(R.string.rupee), "--"));
				} else{
					showPaytmInactiveUI();
				}

				Spannable spanJ = new SpannableString(textViewJugnooCashBalanceValue.getText());
				spanJ.setSpan(new RelativeSizeSpan(0.8f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				//textViewJugnooCashBalanceValue.setText(spanJ);

				Spannable spanP = new SpannableString(textViewPaytmBalanceValue.getText());
				spanP.setSpan(new RelativeSizeSpan(0.8f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				//textViewPaytmBalanceValue.setText(spanP);
				textViewJugnooCashBalanceValue.setTextColor(Data.userData.getJugnooBalanceColor(paymentActivity));
				textViewPaytmBalanceValue.setTextColor(Data.userData.getPaytmBalanceColor(paymentActivity));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	private void showPaytmActiveUI(){
		textViewPaytmBalance.setText(getResources().getString(R.string.nl_paytm_wallet));
		textViewPaytmBalanceValue.setVisibility(View.VISIBLE);
	}

	private void showPaytmInactiveUI(){
		textViewPaytmBalance.setText(getResources().getString(R.string.nl_add_paytm_wallet));
		textViewPaytmBalanceValue.setVisibility(View.GONE);
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}


    public void updateStatus(String status, String amountValue) {
        try {
			String payment = status;
			if ("success".equalsIgnoreCase(payment)) {
				String amount = amountValue;
				new DialogPopup().dialogBanner(paymentActivity, "Payment successful, Added Rs. " + amount);
			}
		} catch(Exception e){
            e.printStackTrace();
        }
    }

}
