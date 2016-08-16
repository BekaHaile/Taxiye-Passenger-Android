package sabkuchfresh.wallet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.R;
import com.sabkuchfresh.SplashNewActivity;
import com.sabkuchfresh.TokenGenerator.HomeUtil;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.config.Config;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.TransactionType;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.SettleUserDebt;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
	ListView listViewTransactions;
	TransactionListAdapter transactionListAdapter;
	RelativeLayout relativeLayoutShowMore;
	TextView textViewShowMore;
	
	public double jugnooBalance = 0;
	public int totalTransactions = 0, pageSize = 0;
	ArrayList<TransactionInfo> transactionInfoList = new ArrayList<>();

	View rootView;
    private PaymentActivity paymentActivity;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
	
	@Override
	public void onResume() {
		super.onResume();
		HomeUtil.checkForAccessTokenChange(paymentActivity);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet_transactions, container, false);

        paymentActivity = (PaymentActivity) getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(paymentActivity));

		listViewTransactions = (ListView) rootView.findViewById(R.id.listViewTransactions);

		LinearLayout viewF = (LinearLayout) paymentActivity.getLayoutInflater().inflate(R.layout.list_item_show_more, null);
		listViewTransactions.addFooterView(viewF);
		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
        ASSL.DoMagic(viewF);
		relativeLayoutShowMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutShowMore);
		textViewShowMore = (TextView) viewF.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.latoLight(paymentActivity), Typeface.BOLD);
		relativeLayoutShowMore.setVisibility(View.GONE);
		
		transactionListAdapter = new TransactionListAdapter(paymentActivity);
		listViewTransactions.setAdapter(transactionListAdapter);


        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				performBackPressed();
            }
        });


		
		relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getTransactionInfoAsync(paymentActivity);
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
							performBackPressed();
						}
					}, false, false);
			
			transactionInfoList.clear();
			transactionListAdapter.notifyDataSetChanged();
			relativeLayoutShowMore.setVisibility(View.GONE);
		}
		else{
			if(transactionInfoList.size() == 0){
				relativeLayoutShowMore.setVisibility(View.GONE);
			}
			else{
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
			}
			transactionListAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderTransaction {
		TextView textViewTransactionDate, textViewTransactionAmount, textViewTransactionTime, textViewTransactionType, textViewTransactionMode;
		LinearLayout relative;
		int id;
	}

	class TransactionListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderTransaction holder;
		Context context;
		public TransactionListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return transactionInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderTransaction();
				convertView = mInflater.inflate(R.layout.list_item_trans_naw, null);
				
				holder.textViewTransactionDate = (TextView) convertView.findViewById(R.id.textViewTransactionDate); holder.textViewTransactionDate.setTypeface(Fonts.mavenLight(context));
				holder.textViewTransactionAmount = (TextView) convertView.findViewById(R.id.textViewTransactionAmount); holder.textViewTransactionAmount.setTypeface(Fonts.mavenLight(context));
				holder.textViewTransactionTime = (TextView) convertView.findViewById(R.id.textViewTransactionTime); holder.textViewTransactionTime.setTypeface(Fonts.mavenLight(context));
				holder.textViewTransactionType = (TextView) convertView.findViewById(R.id.textViewTransactionType); holder.textViewTransactionType.setTypeface(Fonts.mavenLight(context));
				holder.textViewTransactionMode = (TextView) convertView.findViewById(R.id.textViewTransactionMode); holder.textViewTransactionMode.setTypeface(Fonts.mavenLight(context));
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, 156));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderTransaction) convertView.getTag();
			}
			
			holder.id = position;
			
			TransactionInfo transactionInfo = transactionInfoList.get(position);
			
			holder.textViewTransactionDate.setText(transactionInfo.date);

			holder.textViewTransactionAmount.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(transactionInfo.amount)));
			holder.textViewTransactionTime.setText(transactionInfo.time);
			holder.textViewTransactionType.setText(transactionInfo.transactionText);
			
			if(TransactionType.CREDIT.getOrdinal() == transactionInfo.transactionType){
				holder.textViewTransactionType.setTextColor(getResources().getColor(R.color.green_transaction_type));
			}
			else{
				holder.textViewTransactionType.setTextColor(getResources().getColor(R.color.grey_dark));
			}

			if(transactionInfo.paytm == 1){
				holder.textViewTransactionMode.setVisibility(View.VISIBLE);
			}
			else{
				holder.textViewTransactionMode.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if(totalTransactions > transactionInfoList.size()){
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutShowMore.setVisibility(View.GONE);
			}
		}

	}

	
	
	public void getTransactionInfoAsync(final Activity activity) {
		relativeLayoutShowMore.setVisibility(View.GONE);
		if (AppStatus.getInstance(activity).isOnline(activity)) {
            DialogPopup.showLoadingDialog(activity, "Loading...");

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
			if(!HomeUtil.checkIfUserDataNull(activity)) {
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
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

										int paytm = jTransactionI.optInt("paytm", 0);

										transactionInfoList.add(new TransactionInfo(jTransactionI.getInt("txn_id"),
												jTransactionI.getInt("txn_type"),
												jTransactionI.getString("txn_time"),
												jTransactionI.getString("txn_date"),
												jTransactionI.getString("txn_text"),
												jTransactionI.getDouble("amount"),
												paytm));
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
                        DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "getTransactionHistory error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
						updateListData("Some error occurred", true);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Method used to remove fragment from stack
	 */
	public void performBackPressed() {
		try {
			paymentActivity.getSupportFragmentManager().popBackStack(WalletTransactionsFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
}
