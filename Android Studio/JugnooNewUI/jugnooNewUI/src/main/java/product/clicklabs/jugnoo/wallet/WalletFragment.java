package product.clicklabs.jugnoo.wallet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HelpParticularActivity;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class WalletFragment extends Fragment implements FlurryEventNames {
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewPromotion;

	RelativeLayout relativeLayoutJugnooCash;
	TextView textViewJugnooCashBalanceValue;

	RelativeLayout relativeLayoutPaytm;
	TextView textViewPaytmBalanceValue;
	ProgressWheel progressBarWallet;

	RelativeLayout relativeLayoutAddPaytm;

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
	public void onResume() {
		super.onResume();
        HomeActivity.checkForAccessTokenChange(paymentActivity);
		startBalanceUpdater();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopBalanceUpdater();
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        paymentActivity = (PaymentActivity) getActivity();


		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);
		
		textViewPromotion = (TextView) rootView.findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Fonts.latoRegular(paymentActivity));

		relativeLayoutJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWallet);
		((TextView)rootView.findViewById(R.id.textViewJugnooCashBalance)).setTypeface(Fonts.latoRegular(paymentActivity));
		textViewJugnooCashBalanceValue = (TextView) rootView.findViewById(R.id.textViewJugnooCashBalanceValue);
		textViewJugnooCashBalanceValue.setTypeface(Fonts.latoRegular(paymentActivity));

		relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
		((TextView)rootView.findViewById(R.id.textViewPaytmBalance)).setTypeface(Fonts.latoRegular(paymentActivity));
		textViewPaytmBalanceValue = (TextView) rootView.findViewById(R.id.textViewPaytmBalanceValue);
		textViewPaytmBalanceValue.setTypeface(Fonts.latoRegular(paymentActivity));
		progressBarWallet = (ProgressWheel) rootView.findViewById(R.id.progressBarWallet);
		progressBarWallet.setVisibility(View.GONE);

		((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.latoRegular(paymentActivity));

		relativeLayoutAddPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddPaytm);
		((TextView)rootView.findViewById(R.id.textViewAddPaytm)).setTypeface(Fonts.latoRegular(paymentActivity));

		relativeLayoutWalletTransactions = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWalletTransactions);
		((TextView) rootView.findViewById(R.id.textViewWalletTransactions)).setTypeface(Fonts.latoRegular(paymentActivity));


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

				paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
						.add(R.id.fragLayout, new WalletAddPaymentFragment(), "WalletAddPaymentFragment").addToBackStack("WalletAddPaymentFragment")
						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName())).commit();
				FlurryEventLogger.event(ADDING_JUGNOO_CASH);

			}
		});

		relativeLayoutPaytm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
						.add(R.id.fragLayout, new PaytmRechargeFragment(), "PaytmRechargeFragment").addToBackStack("PaytmRechargeFragment")
						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commit();

			}
		});

		relativeLayoutAddPaytm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

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

				paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
						.add(R.id.fragLayout, new WalletTransactionsFragment(), "WalletTransactionsFragment").addToBackStack("WalletTransactionsFragment")
						.hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
								.getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commit();

			}
		});


		try{
			if(Data.userData != null){
				textViewJugnooCashBalanceValue.setText(getResources().getString(R.string.rupee)+" "+Utils.getMoneyDecimalFormat().format(Data.userData.jugnooBalance));
			}
		} catch(Exception e){
			e.printStackTrace();
		}

        return rootView;
	}



    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}


    public void updateStatus(String status, String amountValue) {
        try{
                String payment = status;
                if("success".equalsIgnoreCase(payment)){
                    String amount = amountValue;
					new DialogPopup().dialogBanner(paymentActivity, "Payment successful, Added Rs. " + amount);
                }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

	private void getBalance() {
		if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
			progressBarWallet.setVisibility(View.VISIBLE);
			RequestParams params = new RequestParams();
			params.put("access_token", Data.userData.accessToken);
			params.put("client_id", Config.getClientId());
			params.put("is_access_token_new", "1");

			AsyncHttpClient client = Data.getClient();

			client.post(Config.getTXN_URL() + "paytm/wallet/check_balance", params, new CustomAsyncHttpResponseHandler() {

				@Override
				public void onSuccess(String response) {
					Log.i("request succesfull", "response = " + response);
					try {
						JSONObject res = new JSONObject(response.toString());
						String balance = res.optString("WALLETBALANCE", "0");
						if(Data.userData != null){
							Data.userData.paytmBalance = Double.parseDouble(balance);
						}
						textViewPaytmBalanceValue.setText(paymentActivity.getResources().getString(R.string.rupee)+" "+balance);
						Toast.makeText(paymentActivity, res.toString(), Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
					progressBarWallet.setVisibility(View.GONE);
				}

				@Override
				public void onFailure(Throwable arg0) {
					Log.e("request fail", arg0.toString());
					progressBarWallet.setVisibility(View.GONE);
				}
			});
		}
	}


	private int balanceUpdaterRunning = 0;
	private Handler handlerBalanceUpdater = new Handler();
	private Runnable runnableBalanceUpdater = new Runnable() {
		@Override
		public void run() {
			try {
				if(balanceUpdaterRunning == 1){
					getBalance();
					handlerBalanceUpdater.postDelayed(runnableBalanceUpdater, 20000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void startBalanceUpdater(){
		try {
			balanceUpdaterRunning = 1;
			handlerBalanceUpdater.postDelayed(runnableBalanceUpdater, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void stopBalanceUpdater(){
		try {
			balanceUpdaterRunning = 0;
			handlerBalanceUpdater.removeCallbacks(runnableBalanceUpdater);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	
}
