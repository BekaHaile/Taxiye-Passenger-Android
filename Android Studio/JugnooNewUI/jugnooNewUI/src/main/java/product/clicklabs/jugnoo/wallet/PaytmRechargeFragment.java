package product.clicklabs.jugnoo.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.EmergencyContactsActivity;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PaytmPaymentState;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class PaytmRechargeFragment extends Fragment {

	LinearLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle, textViewTitleEdit;
	TextView textViewAddCashHelp;

	TextView textViewCurrentBalance, textViewCurrentBalanceValue;

	TextView textViewAddCash;
	EditText editTextAmount;
	Button buttonAmount1, buttonAmount2, buttonAmount3, buttonAddMoney;
	Button buttonMakePayment, buttonMakePaymentOTP, buttonWithdrawMoney, buttonRemoveWallet;

	View rootView;
	PaymentActivity paymentActivity;

	ScrollView scrollView;
	TextView textViewScroll;
	LinearLayout linearLayoutMain, linearLayoutInner;
	boolean scrolled = false;

	String amount1 = "500", amount2 = "1000", amount3 = "2000";


	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(getActivity(), Config.getFlurryKey());
		FlurryAgent.onStartSession(getActivity(), Config.getFlurryKey());
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_paytm_recharge, container, false);

		Data.paytmPaymentState = PaytmPaymentState.INIT;

		paymentActivity = (PaymentActivity) getActivity();


		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		linearLayoutInner = (LinearLayout) rootView.findViewById(R.id.linearLayoutInner);

		new ASSL(paymentActivity, relative, 1134, 720, false);

//		setupUI(rootView.findViewById(R.id.relative));

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);
		textViewTitleEdit = (TextView) rootView.findViewById(R.id.textViewTitleEdit); textViewTitleEdit.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);

		textViewAddCashHelp = (TextView) rootView.findViewById(R.id.textViewAddCashHelp); textViewAddCashHelp.setTypeface(Fonts.latoRegular(paymentActivity));

		textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance);	textViewCurrentBalance.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.latoRegular(paymentActivity));

		textViewAddCash = (TextView) rootView.findViewById(R.id.textViewAddCash); textViewAddCash.setTypeface(Fonts.latoLight(paymentActivity));

		editTextAmount = (EditText) rootView.findViewById(R.id.editTextAmount);	editTextAmount.setTypeface(Fonts.latoRegular(paymentActivity));

		buttonAmount1 = (Button) rootView.findViewById(R.id.buttonAmount1);	buttonAmount1.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonAmount2 = (Button) rootView.findViewById(R.id.buttonAmount2);	buttonAmount2.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonAmount3 = (Button) rootView.findViewById(R.id.buttonAmount3);	buttonAmount3.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonAddMoney = (Button) rootView.findViewById(R.id.buttonAddMoney); buttonAddMoney.setTypeface(Fonts.latoRegular(paymentActivity));

		buttonMakePaymentOTP = (Button) rootView.findViewById(R.id.buttonMakePaymentOTP); buttonMakePaymentOTP.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonMakePayment = (Button) rootView.findViewById(R.id.buttonMakePayment);	buttonMakePayment.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonWithdrawMoney = (Button) rootView.findViewById(R.id.buttonWithdrawMoney);	buttonWithdrawMoney.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonRemoveWallet = (Button) rootView.findViewById(R.id.buttonRemoveWallet);	buttonRemoveWallet.setTypeface(Fonts.latoRegular(paymentActivity));


		scrolled = false;
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(paymentActivity, editTextAmount);
				performBackPressed();
			}
		});


		buttonAmount1.setText(paymentActivity.getResources().getString(R.string.rupee) + " " + amount1);
		buttonAmount2.setText(paymentActivity.getResources().getString(R.string.rupee) + " " + amount2);
		buttonAmount3.setText(paymentActivity.getResources().getString(R.string.rupee) + " " + amount3);

		buttonAmount1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextAmount.setText(amount1);
			}
		});

		buttonAmount2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextAmount.setText(amount2);
			}
		});

		buttonAmount3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextAmount.setText(amount3);
			}
		});

		buttonAddMoney.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String amount = editTextAmount.getText().toString().trim();
					if("".equalsIgnoreCase(amount)){
						DialogPopup.dialogBanner(paymentActivity, "" + getResources().getString(R.string.amount_range));
					}
					else{
						int amountInt = Integer.parseInt(amount);
						if(amountInt < Config.getMinAmount() || amountInt > Config.getMaxAmount()) {
							DialogPopup.dialogBanner(paymentActivity, "" + getResources().getString(R.string.amount_range));
						}
						else{
							addBalance(editTextAmount.getText().toString().trim());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					editTextAmount.setError("Enter valid amount");
				}
			}
		});

		textViewTitleEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				linearLayoutInner.setVisibility(View.GONE);
				buttonRemoveWallet.setVisibility(View.VISIBLE);


			}
		});

		buttonRemoveWallet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogPopup.alertPopupTwoButtonsWithListeners(paymentActivity, "", "Are you sure you want to remove this payment method?", "Cancel", "Remove",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								removeWallet();
							}
						}, false, false);
			}
		});



		buttonMakePayment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				genrateOTP();
			}
		});

		buttonMakePaymentOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendOTP(editTextAmount.getText().toString().trim());
			}
		});

		buttonWithdrawMoney.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				withdrawMoney(editTextAmount.getText().toString().trim());
			}
		});

		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {
				if (!scrolled) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							scrollView.smoothScrollTo(0, buttonAddMoney.getTop());
						}
					}, 100);
					scrolled = true;
				}
			}

			@Override
			public void keyBoardClosed() {
				scrolled = false;
			}
		}));

		paymentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		try{
			if(Data.userData != null){
				textViewCurrentBalanceValue.setText(paymentActivity.getResources().getString(R.string.rupee)+" "+Data.userData.getPaytmBalanceStr());
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		return rootView;
	}


	@Override
	public void onResume() {
		super.onResume();
		try{
			if(Data.userData != null){
				textViewCurrentBalanceValue.setText(paymentActivity.getResources().getString(R.string.rupee)+" "+Data.userData.getPaytmBalanceStr());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Method used to hide keyboard if outside touched.
	 *
	 * @param view
	 */
	private void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					try {
						if (paymentActivity.getCurrentFocus() != null) {
							InputMethodManager inputMethodManager = (InputMethodManager) paymentActivity.getSystemService(paymentActivity.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(paymentActivity.getCurrentFocus().getWindowToken(), 0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}

			});
		}
		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}


	/**
	 * Method used to remove fragment from stack
	 */
	public void performBackPressed() {
		getActivity().getSupportFragmentManager().popBackStack("PaytmRechargeFragment", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	public void genrateOTP() {
		if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
			DialogPopup.showLoadingDialog(paymentActivity, "Generating OTP...");
			RequestParams params = new RequestParams();
			params.put("access_token", Data.userData.accessToken);
			params.put("client_id", Config.getClientId());
			params.put("is_access_token_new", "1");

			AsyncHttpClient client = Data.getClient();

			client.post(Config.getTXN_URL() + "paytm/login/request_otp", params, new CustomAsyncHttpResponseHandler() {

				@Override
				public void onSuccess(String response) {
					Log.i("request succesfull", "response = " + response);
					try {
						Toast.makeText(paymentActivity, "res = " + response, Toast.LENGTH_SHORT).show();
						JSONObject res = new JSONObject(response.toString());

					} catch (Exception e) {
						DialogPopup.dismissLoadingDialog();
						e.printStackTrace();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();

				}

				@Override
				public void onFailure(Throwable arg0) {
					Log.e("request fail", arg0.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
				}
			});
		}
	}

	private void sendOTP(String otp) {
		if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
			DialogPopup.showLoadingDialog(paymentActivity, "Sending OTP...");
			RequestParams params = new RequestParams();
			params.put("access_token", Data.userData.accessToken);
			params.put("client_id", Config.getClientId());
			params.put("is_access_token_new", "1");

			params.put("otp", "" + otp);

			AsyncHttpClient client = Data.getClient();

			client.post(Config.getTXN_URL() + "paytm/wallet/login_with_otp", params, new CustomAsyncHttpResponseHandler() {

				@Override
				public void onSuccess(String response) {
					Log.i("request succesfull", "response = " + response);
					try {
						Toast.makeText(paymentActivity, "res = " + response, Toast.LENGTH_SHORT).show();

						JSONObject res = new JSONObject(response.toString());

					} catch (Exception e) {
						DialogPopup.dismissLoadingDialog();
						e.printStackTrace();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();

				}

				@Override
				public void onFailure(Throwable arg0) {
					Log.e("request fail", arg0.toString());
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
				}
			});
		}
	}



	private void addBalance(String amount) {
		try {
			if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
				DialogPopup.showLoadingDialog(paymentActivity, "Adding Balance...");
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");

				params.put("amount", "" + amount);

				AsyncHttpClient client = Data.getClient();
				client.post(Config.getTXN_URL() + "/paytm/add_money", params, new CustomAsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						Log.i("request succesfull", "response = " + response);
						DialogPopup.dismissLoadingDialog();
						try {
							openWebView(response);
						} catch (Exception e) {
							DialogPopup.dismissLoadingDialog();
							e.printStackTrace();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void onFailure(Throwable arg0) {
						Log.e("request fail", arg0.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				});
			}
			else{
				DialogPopup.alertPopup(paymentActivity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void withdrawMoney(String amount) {
		DialogPopup.showLoadingDialog(paymentActivity, "Withdrawing money...");
		RequestParams params = new RequestParams();
		params.put("access_token", Data.userData.accessToken);
		params.put("client_id", Config.getClientId());
		params.put("is_access_token_new", "1");

		params.put("amount", "" + amount);
		params.put("appIP", "" + Utils.getLocalIpAddress());

		AsyncHttpClient client = Data.getClient();

		client.post(Config.getTXN_URL() + "paytm/wallet/withdraw", params, new CustomAsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Log.i("request succesfull", "response = " + response);
				DialogPopup.dismissLoadingDialog();
				try {
					Toast.makeText(paymentActivity, "res = " + response, Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					e.printStackTrace();
					DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
				}
				DialogPopup.dismissLoadingDialog();
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.e("request fail", arg0.toString());
				DialogPopup.dismissLoadingDialog();
				DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
			}
		});
	}

	private void removeWallet() {
		try {
			if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
				DialogPopup.showLoadingDialog(paymentActivity, "Loading...");
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");

				AsyncHttpClient client = Data.getClient();

				client.post(Config.getTXN_URL() + "/paytm/delete_paytm", params, new CustomAsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						Log.i("request succesfull", "response = " + response);
						try {
							JSONObject jObj = new JSONObject(response);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(paymentActivity, message);
								Data.userData.deletePaytm();
								imageViewBack.performClick();
								paymentActivity.performGetBalanceSuccess("");
							} else {
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void onFailure(Throwable arg0) {
						Log.e("request fail", arg0.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				});
			} else{
				DialogPopup.alertPopup(paymentActivity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int rechargeRequestCode = 1;

	private void openWebView(String jsonData) {
		Data.paytmPaymentState = PaytmPaymentState.INIT;
		Intent intent = new Intent(paymentActivity, PaytmRechargeWebViewActivity.class);
		intent.putExtra(Constants.POST_DATA, jsonData);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, rechargeRequestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == rechargeRequestCode) {
			if(resultCode == PaytmPaymentState.SUCCESS.getOrdinal()){
				Data.paytmPaymentState = PaytmPaymentState.SUCCESS;
			}
			else if (resultCode == PaytmPaymentState.FAILURE.getOrdinal()) {
				Data.paytmPaymentState = PaytmPaymentState.FAILURE;
			}
			else{
				DialogPopup.dialogBanner(paymentActivity, "Transaction cancelled");
			}

			try{
				if(Data.paytmPaymentState == PaytmPaymentState.SUCCESS) {
					if(AddPaymentPath.FROM_IN_RIDE == PaymentActivity.addPaymentPath){
						HomeActivity.rechargedOnce = true;
					}
					DialogPopup.dialogBanner(paymentActivity, "Transaction Successful");
					paymentActivity.getBalance(PaytmRechargeFragment.class.getName());
				}
				else if(Data.paytmPaymentState == PaytmPaymentState.FAILURE){
					DialogPopup.dialogBanner(paymentActivity, "Transaction failed");
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			Data.paytmPaymentState = PaytmPaymentState.INIT;
		}
	}
}
