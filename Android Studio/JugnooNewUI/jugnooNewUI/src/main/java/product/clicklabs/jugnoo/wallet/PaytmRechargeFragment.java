package product.clicklabs.jugnoo.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaytmPaymentState;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class PaytmRechargeFragment extends Fragment {

	private final String TAG = PaytmRechargeFragment.class.getSimpleName();

	LinearLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle, textViewTitleEdit;
	TextView textViewAddCashHelp;

	TextView textViewCurrentBalance, textViewCurrentBalanceValue;

	TextView textViewAddCash;
	EditText editTextAmount;
	Button buttonAmount1, buttonAmount2, buttonAmount3, buttonAddMoney, buttonRemoveWallet;

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

	public int getButtonRemoveWalletVisiblity(){
		return buttonRemoveWallet.getVisibility();
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
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewTitleEdit = (TextView) rootView.findViewById(R.id.textViewTitleEdit); textViewTitleEdit.setTypeface(Fonts.mavenRegular(paymentActivity));

		textViewAddCashHelp = (TextView) rootView.findViewById(R.id.textViewAddCashHelp); textViewAddCashHelp.setTypeface(Fonts.mavenLight(paymentActivity));

		textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance);	textViewCurrentBalance.setTypeface(Fonts.mavenLight(paymentActivity));
		textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.mavenLight(paymentActivity));

		textViewAddCash = (TextView) rootView.findViewById(R.id.textViewAddCash); textViewAddCash.setTypeface(Fonts.mavenLight(paymentActivity));

		editTextAmount = (EditText) rootView.findViewById(R.id.editTextAmount);	editTextAmount.setTypeface(Fonts.mavenLight(paymentActivity));

		buttonAmount1 = (Button) rootView.findViewById(R.id.buttonAmount1);	buttonAmount1.setTypeface(Fonts.mavenLight(paymentActivity));
		buttonAmount2 = (Button) rootView.findViewById(R.id.buttonAmount2);	buttonAmount2.setTypeface(Fonts.mavenLight(paymentActivity));
		buttonAmount3 = (Button) rootView.findViewById(R.id.buttonAmount3);	buttonAmount3.setTypeface(Fonts.mavenLight(paymentActivity));
		buttonAddMoney = (Button) rootView.findViewById(R.id.buttonAddMoney); buttonAddMoney.setTypeface(Fonts.mavenRegular(paymentActivity));
		buttonRemoveWallet = (Button) rootView.findViewById(R.id.buttonRemoveWallet);	buttonRemoveWallet.setTypeface(Fonts.mavenRegular(paymentActivity));


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


		buttonAmount1.setText(String.format(paymentActivity.getResources().getString(R.string.rupees_value_format_without_space), amount1));
		buttonAmount2.setText(String.format(paymentActivity.getResources().getString(R.string.rupees_value_format_without_space), amount2));
		buttonAmount3.setText(String.format(paymentActivity.getResources().getString(R.string.rupees_value_format_without_space), amount3));

		buttonAmount1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonBackground(buttonAmount1);
				editTextAmount.setText(amount1);
				editTextAmount.setSelection(editTextAmount.getText().length());
			}
		});

		buttonAmount2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonBackground(buttonAmount2);
				editTextAmount.setText(amount2);
				editTextAmount.setSelection(editTextAmount.getText().length());
			}
		});

		buttonAmount3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonBackground(buttonAmount3);
				editTextAmount.setText(amount3);
				editTextAmount.setSelection(editTextAmount.getText().length());
			}
		});

		buttonAddMoney.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String amount = editTextAmount.getText().toString().trim();
					if ("".equalsIgnoreCase(amount)) {
						DialogPopup.dialogBanner(paymentActivity, "" + getResources().getString(R.string.amount_range));
					} else {
						int amountInt = Integer.parseInt(amount);
						if (amountInt < Config.getMinAmount() || amountInt > Config.getMaxAmount()) {
							DialogPopup.dialogBanner(paymentActivity, "" + getResources().getString(R.string.amount_range));
						} else {
							if (Data.userData != null) {
								addBalance(editTextAmount.getText().toString().trim());
							}
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
				textViewTitleEdit.setVisibility(View.GONE);
				textViewAddCashHelp.setTextColor(paymentActivity.getResources().getColor(R.color.white_light_grey));
			}
		});

		buttonRemoveWallet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(PassengerScreenMode.P_INITIAL == HomeActivity.passengerScreenMode
					|| PassengerScreenMode.P_SEARCH == HomeActivity.passengerScreenMode) {
					DialogPopup.alertPopupTwoButtonsWithListeners(paymentActivity, "",
						paymentActivity.getResources().getString(R.string.paytm_remove_alert),
						paymentActivity.getResources().getString(R.string.remove),
						paymentActivity.getResources().getString(R.string.cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								removeWallet();
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}, false, false);
				}
			}
		});


		editTextAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.v("Amount value is", "--> " + s.toString());
				if (s.toString().equals(amount1)) {
					setButtonBackground(buttonAmount1);
				} else if (s.toString().equals(amount2)) {
					setButtonBackground(buttonAmount2);
				} else if (s.toString().equals(amount3)) {
					setButtonBackground(buttonAmount3);
				} else {
					setButtonBackground(null);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
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
		});

		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
		keyboardLayoutListener.setResizeTextView(false);

		paymentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		updatePaytmBalance();


		if(PassengerScreenMode.P_INITIAL == HomeActivity.passengerScreenMode
			|| PassengerScreenMode.P_SEARCH == HomeActivity.passengerScreenMode) {
			textViewTitleEdit.setVisibility(View.VISIBLE);
		}
		else{
			textViewTitleEdit.setVisibility(View.GONE);
		}
		buttonRemoveWallet.setVisibility(View.GONE);


		return rootView;
	}

	private void setButtonBackground(Button selected){
		buttonAmount1.setBackgroundResource(R.drawable.background_wallet_border);
		buttonAmount2.setBackgroundResource(R.drawable.background_wallet_border);
		buttonAmount3.setBackgroundResource(R.drawable.background_wallet_border);
		buttonAmount1.setTextColor(getResources().getColor(R.color.text_color));
		buttonAmount2.setTextColor(getResources().getColor(R.color.text_color));
		buttonAmount3.setTextColor(getResources().getColor(R.color.text_color));

		if(selected != null) {
			selected.setBackgroundResource(R.drawable.button_white_grey_theme_border_selector);
			selected.setTextColor(getResources().getColor(R.color.theme_color));
		}
	}

	private void updatePaytmBalance(){
		try{
			if(Data.userData != null){
				textViewCurrentBalanceValue.setText(paymentActivity.getResources().getString(R.string.rupee)+" "+Data.userData.getPaytmBalanceStr());
				textViewCurrentBalanceValue.setTextColor(Data.userData.getPaytmBalanceColor(paymentActivity));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updatePaytmBalance();
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

		if(buttonRemoveWallet.getVisibility() == View.VISIBLE){
			linearLayoutInner.setVisibility(View.VISIBLE);
			buttonRemoveWallet.setVisibility(View.GONE);
			textViewTitleEdit.setVisibility(View.VISIBLE);
			textViewAddCashHelp.setTextColor(paymentActivity.getResources().getColor(R.color.grey_dark_more));
		} else {
			paymentActivity.getSupportFragmentManager()
					.popBackStack(PaytmRechargeFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
			if(AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == paymentActivity.addPaymentPathInt){
				paymentActivity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.add(R.id.fragLayout, new WalletFragment(), WalletFragment.class.getName())
						.addToBackStack(WalletFragment.class.getName())
						.commitAllowingStateLoss();
			}
		}

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}






	private void addBalance(final String amount) {
		try {
			if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
				DialogPopup.showLoadingDialog(paymentActivity, "Adding Balance...");
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");
				params.put("amount", amount);

				RestClient.getStringRestClient().paytmAddMoney(params, new Callback<String>() {
					@Override
					public void success(String settleUserDebt, Response response) {
						Log.i(TAG, "paytmAddMoney settleUserDebt = " + settleUserDebt);
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "paytmAddMoney response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							openWebView(responseStr);
						} catch (Exception e) {
							DialogPopup.dismissLoadingDialog();
							e.printStackTrace();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAddMoney error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				});
			}
			else{
				DialogPopup.dialogNoInternet(paymentActivity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								addBalance(amount);
							}

							@Override
							public void neutralClick(View view) {

							}

							@Override
							public void negativeClick(View view) {

							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void removeWallet() {
		try {
			if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
				DialogPopup.showLoadingDialog(paymentActivity, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");

				RestClient.getApiServices().paytmDeletePaytm(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						Log.i(TAG, "paytmDeletePaytm response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(paymentActivity, message);
								Data.userData.deletePaytm();
								performBackPressed();
								performBackPressed();
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
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmDeletePaytm error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				});
			} else{
				DialogPopup.dialogNoInternet(paymentActivity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								removeWallet();
							}

							@Override
							public void neutralClick(View view) {

							}

							@Override
							public void negativeClick(View view) {

							}
						});
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
					if(AddPaymentPath.PAYTM_RECHARGE.getOrdinal() == paymentActivity.addPaymentPathInt){
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