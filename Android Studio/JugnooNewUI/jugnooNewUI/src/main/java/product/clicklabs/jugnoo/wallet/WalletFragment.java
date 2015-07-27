package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HelpParticularActivity;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.TransactionType;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class WalletFragment extends Fragment {
	
	private boolean firstTimeFlag = false;
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewPromotion, textViewAccountBalance, textViewAccountBalanceValue, textViewRecentTransactions;
	Button buttonAddPayment;
	
	TextView textViewInfo;
	ProgressBar progressBar;

    ScrollView scrollView;
	
	ListView listViewTransactions;
	TransactionListAdapter transactionListAdapter;
	
	RelativeLayout relativeLayoutShowMore;
	TextView textViewShowMore;
	
	AsyncHttpClient fetchTransactionInfoClient;
    AnimationDrawable frameAnimation;
    ImageView view;

	public double jugnooBalance = 0;
	public int totalTransactions = 0, pageSize = 0;
	public String promoBanner = "";
	ArrayList<TransactionInfo> transactionInfoList = new ArrayList<TransactionInfo>();

	DecimalFormat df = new DecimalFormat("#");

    View rootView;
    public PaymentActivity paymentActivity;

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
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        paymentActivity = (PaymentActivity) getActivity();


		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);
		
		textViewPromotion = (TextView) rootView.findViewById(R.id.textViewPromotion); textViewPromotion.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewAccountBalance = (TextView) rootView.findViewById(R.id.textViewAccountBalance); textViewAccountBalance.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);
		textViewAccountBalanceValue = (TextView) rootView.findViewById(R.id.textViewAccountBalanceValue); textViewAccountBalanceValue.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewRecentTransactions = (TextView) rootView.findViewById(R.id.textViewRecentTransactions); textViewRecentTransactions.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewRecentTransactions.setText("");
		textViewPromotion.setVisibility(View.GONE);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		
		buttonAddPayment = (Button) rootView.findViewById(R.id.buttonAddPayment); buttonAddPayment.setTypeface(Fonts.latoRegular(paymentActivity));
		
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
		
		textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(paymentActivity));
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		
		textViewInfo.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);


        view = (ImageView) rootView.findViewById(R.id.logoAnimView);
        view.setBackgroundResource(R.drawable.animation_list);

        frameAnimation = (AnimationDrawable) view.getBackground();
        frameAnimation.start();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FlurryEventLogger.screenOpened("Gift Button Pressed");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startActivity(new Intent(paymentActivity, ShareActivity.class));
                paymentActivity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.shareScreenOpened(Data.userData.accessToken);
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paymentActivity.finish();

            }
        });

        textViewInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getTransactionInfoAsync(paymentActivity);
            }
        });

        buttonAddPayment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)

                    .add(R.id.fragLayout, new WalletAddPaymentFragment(), "WalletAddPaymentFragment").addToBackStack("WalletAddPaymentFragment")
                    .hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
                        .getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commit();

                try {
                    listViewTransactions.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, 0);
                        }
                    }, 100);

            }catch(Exception e) {}



            }
        });


        textViewPromotion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                HelpParticularActivity.helpSection = HelpSection.WALLET_PROMOTIONS;
				startActivity(new Intent(paymentActivity, HelpParticularActivity.class));
                paymentActivity.overridePendingTransition(R.anim.right_in, R.anim.right_out);

                try {
                    listViewTransactions.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, 0);
                        }
                    }, 100);

                }catch(Exception e) {}


            }
        });


		
		relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fetchTransactionInfoClient = null;
                if(firstTimeFlag) {
                    firstTimeFlag = false;
                    updateListData("", false);
                } else {
                    getTransactionInfoAsync(paymentActivity);
                }
			}
		});
		

		
		jugnooBalance = 0;
		totalTransactions = 0;
		pageSize = 0;
		
		fetchTransactionInfoClient = null;
		
		if(transactionInfoList == null){
			transactionInfoList = new ArrayList<TransactionInfo>();
		}
		transactionInfoList.clear();
        firstTimeFlag = true;
		
		try{
			if(Data.userData != null){
				textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee)+" "+df.format(Data.userData.jugnooBalance));
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		getTransactionInfoAsync(paymentActivity);
        return rootView;
	}



    @Override
	public void onDestroy() {
		if(fetchTransactionInfoClient != null){
			fetchTransactionInfoClient.cancelRequests(paymentActivity, true);
		}
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void updateListData(String message, boolean errorOccurred){
        Log.e("errorOccurred", "errorOccurred = "+errorOccurred);
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			transactionInfoList.clear();
			transactionListAdapter.notifyDataSetChanged();
			relativeLayoutShowMore.setVisibility(View.GONE);
		}
		else{
			if(transactionInfoList.size() == 0){
				textViewRecentTransactions.setText("");
				relativeLayoutShowMore.setVisibility(View.GONE);
				textViewInfo.setVisibility(View.VISIBLE);
				textViewInfo.setText(message);
			}
			else{
				textViewRecentTransactions.setText("Recent Transactions");
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);
			}
			transactionListAdapter.notifyDataSetChanged();
			textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee)+" "+df.format(jugnooBalance));
		}

		Utils.expandListForVariableHeight(listViewTransactions);
	}
	
	
	class ViewHolderTransaction {
		TextView textViewTransactionDate, textViewTransactionAmount, textViewTransactionTime, textViewTransactionType;
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
            if(firstTimeFlag && transactionInfoList.size()>4) {
                return 4;
            }
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
				
				holder.textViewTransactionDate = (TextView) convertView.findViewById(R.id.textViewTransactionDate); holder.textViewTransactionDate.setTypeface(Fonts.latoRegular(context));
				holder.textViewTransactionAmount = (TextView) convertView.findViewById(R.id.textViewTransactionAmount); holder.textViewTransactionAmount.setTypeface(Fonts.latoRegular(context));
				holder.textViewTransactionTime = (TextView) convertView.findViewById(R.id.textViewTransactionTime); holder.textViewTransactionTime.setTypeface(Fonts.latoLight(context));
				holder.textViewTransactionType = (TextView) convertView.findViewById(R.id.textViewTransactionType); holder.textViewTransactionType.setTypeface(Fonts.latoLight(context));
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, 108));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderTransaction) convertView.getTag();
			}
			
			holder.id = position;
			
			TransactionInfo transactionInfo = transactionInfoList.get(position);
			
			holder.textViewTransactionDate.setText(transactionInfo.date);

			holder.textViewTransactionAmount.setText(getResources().getString(R.string.rupee)+" "+df.format(transactionInfo.amount));
			holder.textViewTransactionTime.setText(transactionInfo.time);
			holder.textViewTransactionType.setText(transactionInfo.transactionText);
			
			if(TransactionType.CREDIT.getOrdinal() == transactionInfo.transactionType){
				holder.textViewTransactionType.setTextColor(getResources().getColor(R.color.green_transaction_type));
			}
			else{
				holder.textViewTransactionType.setTextColor(getResources().getColor(R.color.grey_dark));
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
		if(fetchTransactionInfoClient == null){
            if (AppStatus.getInstance(activity).isOnline(activity)) {
				
				progressBar.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);

                if(AddPaymentPath.FROM_WALLET == PaymentActivity.addPaymentPath) {
                    callRefreshAPI(activity);
                }
                else{
                    transactionInfoList.clear();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callRefreshAPI(activity);
                        }
                    }, 5000);
                }
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}
	}


    public void callRefreshAPI(final Activity activity){
        RequestParams params = new RequestParams();
        params.put("access_token", Data.userData.accessToken);
        params.put("client_id", Config.getClientId());
        params.put("is_access_token_new", "1");
        params.put("start_from", ""+transactionInfoList.size());


        fetchTransactionInfoClient = Data.getClient();
        fetchTransactionInfoClient.post(Config.getServerUrl() + "/get_transaction_history", params,
            new CustomAsyncHttpResponseHandler() {
                private JSONObject jObj;

                @Override
                public void onFailure(Throwable arg3) {
                    Log.e("request fail", arg3.toString());
                    progressBar.setVisibility(View.GONE);
                    updateListData("Some error occurred. Tap to retry", true);
                }

                @Override
                public void onSuccess(String response) {
                    Log.e("Server response", "response = " + response);
                    try {
                        jObj = new JSONObject(response);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                updateListData(error, true);
                            } else if (ApiResponseFlags.TRANSACTION_HISTORY.getOrdinal() == flag) {

//											{
//											    "flag": 423,
//											    "banner": "Get Jugnoo Cash at 50% discount!!!",
//											    "balance": 1,
//											    "num_txns": 26,
//											    "page_size": 10,
//											    "transactions": [
//											        {
//											            "txn_id": 72,
//											            "txn_type": 1,
//											            "amount": 1,
//											            "txn_date": "February 27th, 2015",
//											            "txn_time": "12:58 PM",
//											            "txn_text": "Cash Added"
//											        }
//											    ]
//											}

                                jugnooBalance = jObj.getDouble("balance");
                                totalTransactions = jObj.getInt("num_txns");
                                pageSize = jObj.getInt("page_size");

                                promoBanner = "";
                                if (jObj.has("banner")) {
                                    promoBanner = jObj.getString("banner");
                                }


                                JSONArray jTransactions = jObj.getJSONArray("transactions");
                                for (int i = 0; i < jTransactions.length(); i++) {
                                    JSONObject jTransactionI = jTransactions.getJSONObject(i);
                                    transactionInfoList.add(new TransactionInfo(jTransactionI.getInt("txn_id"),
                                        jTransactionI.getInt("txn_type"),
                                        jTransactionI.getString("txn_time"),
                                        jTransactionI.getString("txn_date"),
                                        jTransactionI.getString("txn_text"),
                                        jTransactionI.getDouble("amount")));
                                }

                                if (Data.userData != null) {
                                    Data.userData.jugnooBalance = jugnooBalance;

                                }

                                try {
                                    textViewAccountBalanceValue.setText(getResources().getString(R.string.rupee) + " " + df.format(jugnooBalance));
                                }catch(Exception e){}

                                showPromoBanner();
                                updateListData("No transactions currently", false);
                            } else {
                                updateListData("Some error occurred. Tap to retry", true);
                            }
                        } else {
                            updateListData("Some error occurred. Tap to retry", true);
                        }

                    } catch (Exception exception) {
                        exception.printStackTrace();
                        updateListData("Some error occurred. Tap to retry", true);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    fetchTransactionInfoClient = null;
                    super.onFinish();
                }

            });
    }



	
	public void showPromoBanner(){
		if(!"".equalsIgnoreCase(promoBanner)){
			textViewPromotion.setVisibility(View.VISIBLE);
			textViewPromotion.setText(promoBanner);
		}
		else{
			textViewPromotion.setVisibility(View.GONE);
		}
	}

    public void updateStatus(String status, String amountValue) {
        try{
                String payment = status;
                if("success".equalsIgnoreCase(payment)){
                    String amount = amountValue;
                    new DialogPopup().dialogBanner(paymentActivity, "Payment successful, Added Rs. " + amount);
                    transactionInfoList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    textViewInfo.setVisibility(View.GONE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstTimeFlag = true;
                            fetchTransactionInfoClient = null;
                            getTransactionInfoAsync(paymentActivity);
                        }
                    }, 5000);
                    scrollView.smoothScrollTo(0, 0);
                }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


	
	
}
