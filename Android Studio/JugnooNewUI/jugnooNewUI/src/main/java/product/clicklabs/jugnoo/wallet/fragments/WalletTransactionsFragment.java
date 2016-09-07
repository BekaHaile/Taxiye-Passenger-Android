package product.clicklabs.jugnoo.wallet.fragments;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.TransactionInfo;
import product.clicklabs.jugnoo.wallet.adapters.WalletTransactionsAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class WalletTransactionsFragment extends Fragment implements FlurryEventNames {

	private final String TAG = WalletTransactionsFragment.class.getSimpleName();

	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	//Transactions List vars
	RecyclerView recyclerViewWalletTransactions;
	WalletTransactionsAdapter walletTransactionsAdapter;

	LinearLayout linearLayoutNoItems;
	
	public double jugnooBalance = 0;
	public int totalTransactions = 0, pageSize = 0;
	ArrayList<TransactionInfo> transactionInfoList = new ArrayList<>();

	View rootView;
    private PaymentActivity paymentActivity;
	private ImageView imageViewJugnooAnimation;
	private AnimationDrawable jugnooAnimation;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(paymentActivity, Config.getFlurryKey());
        FlurryAgent.onStartSession(paymentActivity, Config.getFlurryKey());
        FlurryAgent.onEvent("WalletTransactions started");
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
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet_transactions, container, false);

        paymentActivity = (PaymentActivity) getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(paymentActivity));

		recyclerViewWalletTransactions = (RecyclerView) rootView.findViewById(R.id.recyclerViewWalletTransactions);
		recyclerViewWalletTransactions.setLayoutManager(new LinearLayoutManager(paymentActivity));
		recyclerViewWalletTransactions.setItemAnimator(new DefaultItemAnimator());
		recyclerViewWalletTransactions.setHasFixedSize(false);

		linearLayoutNoItems = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoItems);
		((TextView)rootView.findViewById(R.id.textViewNoItems)).setTypeface(Fonts.mavenRegular(paymentActivity));
		linearLayoutNoItems.setVisibility(View.GONE);

		walletTransactionsAdapter = new WalletTransactionsAdapter(paymentActivity, transactionInfoList,
				totalTransactions, new WalletTransactionsAdapter.Callback() {
			@Override
			public void onShowMoreClick() {
				getTransactionInfoAsync(paymentActivity);
				FlurryEventLogger.event(RECENT_TRANSACTION_LOOK_UP);
			}
		});
		recyclerViewWalletTransactions.setAdapter(walletTransactionsAdapter);

		imageViewJugnooAnimation = (ImageView)rootView.findViewById(R.id.imageViewJugnooAnimation);
		jugnooAnimation = (AnimationDrawable) imageViewJugnooAnimation.getBackground();

		textViewTitle.getPaint().setShader(Utils.textColorGradient(getActivity(), textViewTitle));

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				paymentActivity.goBack();
            }
        });


		
		jugnooBalance = 0;
		totalTransactions = 0;
		pageSize = 0;

		if(transactionInfoList == null){
			transactionInfoList = new ArrayList<>();
		}
		transactionInfoList.clear();
		getTransactionInfoAsync(paymentActivity);

        return rootView;
	}



    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
        Log.e("errorOccurred", "errorOccurred = " + errorOccurred);
		if(errorOccurred){
			DialogPopup.alertPopupTwoButtonsWithListeners(paymentActivity, "", message, "Retry", "Cancel",
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							paymentActivity.goBack();
						}
					}, false, false);
			
			transactionInfoList.clear();
			walletTransactionsAdapter.notifyList(totalTransactions);
			linearLayoutNoItems.setVisibility(View.GONE);
		} else{
			if(transactionInfoList.size() == 0){
				linearLayoutNoItems.setVisibility(View.VISIBLE);
			} else{
				linearLayoutNoItems.setVisibility(View.GONE);
			}
			walletTransactionsAdapter.notifyList(totalTransactions);
		}
	}
	
	public void getTransactionInfoAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			imageViewJugnooAnimation.setVisibility(View.VISIBLE);
			jugnooAnimation.start();
			callRefreshAPI(activity);
		} else {
			DialogPopup.dialogNoInternet(paymentActivity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View view) {
							getTransactionInfoAsync(activity);
						}

						@Override
						public void neutralClick(View view) {

						}

						@Override
						public void negativeClick(View view) {

						}
					});
		}
	}


    public void callRefreshAPI(final Activity activity){
		try {
			if(!HomeActivity.checkIfUserDataNull(activity)) {
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getAutosClientId());
				params.put("is_access_token_new", "1");
				params.put("start_from", "" + transactionInfoList.size());

				RestClient.getApiServices().getTransactionHistory(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						Log.e(TAG, "getTransactionHistory response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
									String error = jObj.getString("error");
									updateListData(error, true);
								} else if (ApiResponseFlags.TRANSACTION_HISTORY.getOrdinal() == flag) {

									jugnooBalance = jObj.getDouble("balance");
									totalTransactions = jObj.getInt("num_txns");
									pageSize = jObj.getInt("page_size");

									JSONArray jTransactions = jObj.getJSONArray("transactions");
									for (int i = 0; i < jTransactions.length(); i++) {
										JSONObject jTransactionI = jTransactions.getJSONObject(i);

										int paytm = jTransactionI.optInt(Constants.KEY_PAYTM, 0);
										int mobikwik = jTransactionI.optInt(Constants.KEY_MOBIKWIK, 0);

										transactionInfoList.add(new TransactionInfo(jTransactionI.getInt("txn_id"),
												jTransactionI.getInt("txn_type"),
												jTransactionI.getString("txn_time"),
												jTransactionI.getString("txn_date"),
												jTransactionI.getString("txn_text"),
												jTransactionI.getDouble("amount"),
												paytm, mobikwik));
									}

									if (Data.userData != null) {
										Data.userData.setJugnooBalance(jugnooBalance);
									}
									paymentActivity.updateWalletFragment();

									updateListData("No transactions currently", false);
								} else {
									updateListData("Some error occurred", true);
								}
							} else {
								updateListData("Some error occurred", true);
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							updateListData("Some error occurred", true);
						}
						imageViewJugnooAnimation.setVisibility(View.GONE);
						jugnooAnimation.stop();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "getTransactionHistory error="+error.toString());
						imageViewJugnooAnimation.setVisibility(View.GONE);
						jugnooAnimation.stop();
						updateListData("Some error occurred", true);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	
}