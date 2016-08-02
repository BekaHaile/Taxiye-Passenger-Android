package product.clicklabs.jugnoo.wallet.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.WalletRechargeWebViewActivity;
import product.clicklabs.jugnoo.wallet.models.WalletAddMoneyState;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class WalletRechargeFragment extends Fragment {

	private final String TAG = WalletRechargeFragment.class.getSimpleName();

	RelativeLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle, textViewTitleEdit;
	TextView textViewAddCashHelp;

	ImageView imageViewWalletIcon;
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
	private int openWalletType;

	public WalletRechargeFragment(int openWalletType){
		this.openWalletType = openWalletType;
	}


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
		rootView = inflater.inflate(R.layout.fragment_wallet_recharge, container, false);
		paymentActivity = (PaymentActivity) getActivity();

		paymentActivity.setWalletAddMoneyState(WalletAddMoneyState.INIT);


		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		linearLayoutInner = (LinearLayout) rootView.findViewById(R.id.linearLayoutInner);

		new ASSL(paymentActivity, relative, 1134, 720, false);

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(paymentActivity));
		textViewTitleEdit = (TextView) rootView.findViewById(R.id.textViewTitleEdit); textViewTitleEdit.setTypeface(Fonts.mavenRegular(paymentActivity));

		textViewAddCashHelp = (TextView) rootView.findViewById(R.id.textViewAddCashHelp); textViewAddCashHelp.setTypeface(Fonts.mavenRegular(paymentActivity));

		imageViewWalletIcon = (ImageView) rootView.findViewById(R.id.imageViewWalletIcon);
		textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance);	textViewCurrentBalance.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.mavenRegular(paymentActivity));

		textViewAddCash = (TextView) rootView.findViewById(R.id.textViewAddCash); textViewAddCash.setTypeface(Fonts.mavenMedium(paymentActivity));

		editTextAmount = (EditText) rootView.findViewById(R.id.editTextAmount);	editTextAmount.setTypeface(Fonts.mavenRegular(paymentActivity));
		try {
			editTextAmount.setText(paymentActivity.amountToPreFill);
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonAmount1 = (Button) rootView.findViewById(R.id.buttonAmount1);	buttonAmount1.setTypeface(Fonts.mavenRegular(paymentActivity));
		buttonAmount2 = (Button) rootView.findViewById(R.id.buttonAmount2);	buttonAmount2.setTypeface(Fonts.mavenRegular(paymentActivity));
		buttonAmount3 = (Button) rootView.findViewById(R.id.buttonAmount3);	buttonAmount3.setTypeface(Fonts.mavenRegular(paymentActivity));
		buttonAddMoney = (Button) rootView.findViewById(R.id.buttonAddMoney); buttonAddMoney.setTypeface(Fonts.mavenRegular(paymentActivity), Typeface.BOLD);
		buttonRemoveWallet = (Button) rootView.findViewById(R.id.buttonRemoveWallet); buttonRemoveWallet.setTypeface(Fonts.mavenRegular(paymentActivity), Typeface.BOLD);


		scrolled = false;
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);


		if(openWalletType == PaymentOption.PAYTM.getOrdinal()){
			textViewTitle.setText(paymentActivity.getResources().getString(R.string.paytm_wallet));
			imageViewWalletIcon.setImageResource(R.drawable.ic_paytm_big);
			buttonAddMoney.setText(paymentActivity.getResources().getString(R.string.add_paytm_cash));
		} else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
			textViewTitle.setText(paymentActivity.getResources().getString(R.string.mobikwik_wallet));
			imageViewWalletIcon.setImageResource(R.drawable.ic_mobikwik_big);
			buttonAddMoney.setText(paymentActivity.getResources().getString(R.string.add_mobikwik_cash));
		} else if(openWalletType == PaymentOption.FREECHARGE.getOrdinal()) {
            textViewTitle.setText(paymentActivity.getResources().getString(R.string.freecharge_wallet));
            // TODO: 02/08/16 change icon here
            imageViewWalletIcon.setImageResource(R.drawable.ic_mobikwik_big);
            buttonAddMoney.setText(paymentActivity.getResources().getString(R.string.add_freecharge_cash));
        }

		textViewTitle.getPaint().setShader(Utils.textColorGradient(paymentActivity, textViewTitle));

		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(paymentActivity, editTextAmount);
				FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Back");
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
                fireBaseEvent(amount1);
			}
		});

		buttonAmount2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonBackground(buttonAmount2);
				editTextAmount.setText(amount2);
				editTextAmount.setSelection(editTextAmount.getText().length());
                fireBaseEvent(amount2);
			}
		});

		buttonAmount3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonBackground(buttonAmount3);
				editTextAmount.setText(amount3);
				editTextAmount.setSelection(editTextAmount.getText().length());
                fireBaseEvent(amount3);
			}
		});

		buttonAddMoney.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String amount = editTextAmount.getText().toString().trim();
					if ("".equalsIgnoreCase(amount)) {
						DialogPopup.dialogBanner(paymentActivity, getResources().getString(R.string.amount_range));
					} else {
						int amountInt = Integer.parseInt(amount);
						if (amountInt < Config.getMinAmount() || amountInt > Config.getMaxAmount()) {
							DialogPopup.dialogBanner(paymentActivity, getResources().getString(R.string.amount_range));
						} else {
							if (Data.userData != null) {
								addBalance(editTextAmount.getText().toString().trim());
								MyApplication.getInstance().getWalletCore().addMoneyFlurryEvent(openWalletType, editTextAmount.getText().toString().trim());
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
				MyApplication.getInstance().getWalletCore().editWalletFlurryEvent(openWalletType);
			}
		});

		buttonRemoveWallet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PassengerScreenMode.P_INITIAL == HomeActivity.passengerScreenMode
						|| PassengerScreenMode.P_SEARCH == HomeActivity.passengerScreenMode) {
					DialogPopup.alertPopupTwoButtonsWithListeners(paymentActivity, "",
							paymentActivity.getResources().getString(R.string.wallet_remove_alert),
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
				MyApplication.getInstance().getWalletCore().removeWalletFlurryEvent(openWalletType);
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


		updateWalletBalance();


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

    private void fireBaseEvent(String amount) {
        Bundle bundle = new Bundle();
        bundle.putString("amount", amount);
        if(openWalletType == PaymentOption.PAYTM.getOrdinal()){
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.ADD_AMOUNT, bundle);
        }
        else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
            MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.MOBIKWIK_WALLET+"_"+FirebaseEvents.ADD_AMOUNT, bundle);
        }
    }
	private void setButtonBackground(Button selected){
		buttonAmount1.setBackgroundResource(R.drawable.background_white_grey_theme_rb_selector);
		buttonAmount2.setBackgroundResource(R.drawable.background_white_grey_theme_rb_selector);
		buttonAmount3.setBackgroundResource(R.drawable.background_white_grey_theme_rb_selector);
		buttonAmount1.setTextColor(getResources().getColorStateList(R.color.text_color_theme_color_selector));
		buttonAmount2.setTextColor(getResources().getColorStateList(R.color.text_color_theme_color_selector));
		buttonAmount3.setTextColor(getResources().getColorStateList(R.color.text_color_theme_color_selector));

		if(selected != null) {
			selected.setBackgroundResource(R.drawable.background_white_theme_color_rounded_bordered);
			selected.setTextColor(getResources().getColor(R.color.theme_color));
		}
	}

	private void updateWalletBalance(){
		try{
			if(Data.userData != null){
				textViewCurrentBalanceValue.setText(String.format(paymentActivity.getResources().getString(R.string.rupees_value_format),
						MyApplication.getInstance().getWalletCore().getWalletBalanceStr(openWalletType)));
				textViewCurrentBalanceValue.setTextColor(MyApplication.getInstance().getWalletCore()
						.getWalletBalanceColor(openWalletType));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateWalletBalance();
	}


	/**
	 * Method used to remove fragment from stack
	 */
	public void performBackPressed() {

        Bundle bundle = new Bundle();
        MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+ FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.BACK, bundle);
		if(buttonRemoveWallet.getVisibility() == View.VISIBLE){
			linearLayoutInner.setVisibility(View.VISIBLE);
			buttonRemoveWallet.setVisibility(View.GONE);
			textViewTitleEdit.setVisibility(View.VISIBLE);
		} else {
			paymentActivity.goBack();
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
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
				params.put(Constants.KEY_AMOUNT, amount);

				if(openWalletType == PaymentOption.PAYTM.getOrdinal()) {
					RestClient.getStringRestClient().paytmAddMoney(params, new Callback<String>() {
						@Override
						public void success(String settleUserDebt, Response response) {
							Log.i(TAG, "paytmAddMoney settleUserDebt = " + settleUserDebt);
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "paytmAddMoney response = " + responseStr);
							DialogPopup.dismissLoadingDialog();
							try {
								openWebView(responseStr, openWalletType);
							} catch (Exception e) {
								DialogPopup.dismissLoadingDialog();
								e.printStackTrace();
								DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
							}
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "paytmAddMoney error=" + error.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
					});
				} else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
					RestClient.getApiServices().mobikwikAddMoney(params, new Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "mobikwikAddMoney response = " + responseStr);
							DialogPopup.dismissLoadingDialog();
							try {
								JSONObject jObj = new JSONObject(responseStr);
								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
								String message = JSONParser.getServerMessage(jObj);
								if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
									String url = jObj.optString(Constants.KEY_ADD_MONEY_URL, "");
									openWebView(url, openWalletType);
								} else{
									DialogPopup.alertPopup(paymentActivity, "", message);
								}
							} catch (Exception e) {
								DialogPopup.dismissLoadingDialog();
								e.printStackTrace();
								DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
							}
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "mobikwikAddMoney error=" + error.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
					});
				} else if(openWalletType == PaymentOption.FREECHARGE.getOrdinal()) {
                    RestClient.getApiServices().freechargeAddMoney(params, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            Log.i(TAG, "freechargeAddMoney response = " + responseStr);
                            DialogPopup.dismissLoadingDialog();
                            try {
                                JSONObject jObj = new JSONObject(responseStr);
                                int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                                String message = JSONParser.getServerMessage(jObj);
                                if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                    String url = jObj.optString(Constants.KEY_ADD_MONEY_URL, "");
                                    openWebView(url, openWalletType);
                                } else{
                                    DialogPopup.alertPopup(paymentActivity, "", message);
                                }
                            } catch (Exception e) {
                                DialogPopup.dismissLoadingDialog();
                                e.printStackTrace();
                                DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "freechargeAddMoney error=" + error.toString());
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
                        }
                    });
                }
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
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						Log.i(TAG, "deleteWallet response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt(Constants.KEY_FLAG);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(paymentActivity, message);
								MyApplication.getInstance().getWalletCore().deleteWallet(openWalletType);
								if(openWalletType == Prefs.with(paymentActivity).getInt(Constants.SP_LAST_ADDED_WALLET, 0)){
									Prefs.with(paymentActivity).save(Constants.SP_LAST_ADDED_WALLET, 0);
								}
								if(openWalletType == Prefs.with(paymentActivity).getInt(Constants.SP_LAST_USED_WALLET, 0)){
									Prefs.with(paymentActivity).save(Constants.SP_LAST_USED_WALLET, 0);
								}
								if(openWalletType == Prefs.with(paymentActivity).getInt(Constants.SP_LAST_MONEY_ADDED_WALLET, 0)){
									Prefs.with(paymentActivity).save(Constants.SP_LAST_MONEY_ADDED_WALLET, 0);
								}
								MyApplication.getInstance().getWalletCore().setDefaultPaymentOption();
								performBackPressed();
								performBackPressed();
								paymentActivity.performGetBalanceSuccess("");
								MyApplication.getInstance().getWalletCore().deleteWalletFlurryEvent(openWalletType);
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
						Log.e(TAG, "deleteWallet error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				};

				if(openWalletType == PaymentOption.PAYTM.getOrdinal()) {
					RestClient.getApiServices().paytmDeletePaytm(params, callback);
				}
				else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
					RestClient.getApiServices().mobikwikUnlink(params, callback);
				} else if(openWalletType == PaymentOption.FREECHARGE.getOrdinal()) {
                    RestClient.getApiServices().freechargeUnlink(params, callback);
                }
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

	private void openWebView(String data, int walletType) {
		paymentActivity.setWalletAddMoneyState(WalletAddMoneyState.INIT);
		Intent intent = new Intent(paymentActivity, WalletRechargeWebViewActivity.class);
		if(walletType == PaymentOption.PAYTM.getOrdinal()) {
			intent.putExtra(Constants.POST_DATA, data);
		}
		else if(walletType == PaymentOption.MOBIKWIK.getOrdinal()) {
			intent.putExtra(Constants.KEY_URL, data);
		} else if(walletType == PaymentOption.FREECHARGE.getOrdinal()) {
            //ToDo: set freechage code here
        }
		intent.putExtra(Constants.KEY_WALLET_TYPE, walletType);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, rechargeRequestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == rechargeRequestCode) {
			if(resultCode == WalletAddMoneyState.SUCCESS.getOrdinal()){
				paymentActivity.setWalletAddMoneyState(WalletAddMoneyState.SUCCESS);
			}
			else if (resultCode == WalletAddMoneyState.FAILURE.getOrdinal()) {
				paymentActivity.setWalletAddMoneyState(WalletAddMoneyState.FAILURE);
			}
			else{
				DialogPopup.dialogBanner(paymentActivity, paymentActivity.getResources()
						.getString(R.string.transaction_cancelled));
			}

			try{
				if(paymentActivity.getWalletAddMoneyState() == WalletAddMoneyState.SUCCESS) {
					DialogPopup.dialogBanner(paymentActivity, paymentActivity.getResources()
							.getString(R.string.transaction_successful));
					Prefs.with(paymentActivity).save(Constants.SP_LAST_MONEY_ADDED_WALLET, openWalletType);
					paymentActivity.getBalance(WalletRechargeFragment.class.getName());
				}
				else if(paymentActivity.getWalletAddMoneyState() == WalletAddMoneyState.FAILURE){
					DialogPopup.dialogBanner(paymentActivity, paymentActivity.getResources()
							.getString(R.string.transaction_failed));
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
