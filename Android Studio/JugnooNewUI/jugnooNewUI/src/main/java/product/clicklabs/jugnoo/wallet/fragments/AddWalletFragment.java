package product.clicklabs.jugnoo.wallet.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
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
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class AddWalletFragment extends Fragment {

	private final String TAG = AddWalletFragment.class.getSimpleName();

	RelativeLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	ImageView imageViewWalletIcon;
	TextView textViewAddWalletHelp, textViewOTPMessage, textViewOTPNumber;

	LinearLayout linearLayoutOTP;
	EditText editTextOTP;

	Button buttonRequestOTP, buttonVerifyOTP, buttonResendOTP;

	View rootView;
	PaymentActivity paymentActivity;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;

	private int openWalletType;

	public AddWalletFragment(int openWalletType){
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

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_add_wallet, container, false);

		paymentActivity = (PaymentActivity) getActivity();


		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);


		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(paymentActivity));

		imageViewWalletIcon = (ImageView) rootView.findViewById(R.id.imageViewWalletIcon);
		textViewAddWalletHelp = (TextView) rootView.findViewById(R.id.textViewAddWalletHelp); textViewAddWalletHelp.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewOTPMessage = (TextView) rootView.findViewById(R.id.textViewOTPMessage); textViewOTPMessage.setTypeface(Fonts.mavenRegular(paymentActivity));
		textViewOTPNumber = (TextView) rootView.findViewById(R.id.textViewOTPNumber); textViewOTPNumber.setTypeface(Fonts.mavenMedium(paymentActivity));

		linearLayoutOTP = (LinearLayout) rootView.findViewById(R.id.linearLayoutOTP);
		editTextOTP = (EditText) rootView.findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.mavenRegular(paymentActivity));

		buttonRequestOTP = (Button) rootView.findViewById(R.id.buttonRequestOTP);	buttonRequestOTP.setTypeface(Fonts.mavenRegular(paymentActivity), Typeface.BOLD);
		buttonVerifyOTP = (Button) rootView.findViewById(R.id.buttonVerifyOTP);	buttonVerifyOTP.setTypeface(Fonts.mavenRegular(paymentActivity), Typeface.BOLD);
		buttonResendOTP = (Button) rootView.findViewById(R.id.buttonResendOTP);	buttonResendOTP.setTypeface(Fonts.mavenRegular(paymentActivity), Typeface.BOLD);

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

		if(openWalletType == PaymentOption.PAYTM.getOrdinal()){
			textViewTitle.setText(paymentActivity.getResources().getString(R.string.paytm_wallet));
			imageViewWalletIcon.setImageResource(R.drawable.ic_paytm_big);
		}
		else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
			textViewTitle.setText(paymentActivity.getResources().getString(R.string.mobikwik_wallet));
			imageViewWalletIcon.setImageResource(R.drawable.ic_mobikwik_big);
		}

		textViewTitle.getPaint().setShader(Utils.textColorGradient(getActivity(), textViewTitle));

		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(paymentActivity, editTextOTP);
                Bundle bundle = new Bundle();
                if(openWalletType == PaymentOption.PAYTM.getOrdinal()){
                    MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.BACK, bundle);
                }
                else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
                    MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.MOBIKWIK_WALLET+"_"+FirebaseEvents.BACK, bundle);
                }
				paymentActivity.goBack();
			}
		});

		buttonRequestOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.eventGA(Constants.REVENUE, "Paytm Wallet", "Request OTP");
                Bundle bundle = new Bundle();
                if(openWalletType == PaymentOption.PAYTM.getOrdinal()){
                    MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.PAYTM_WALLET+"_"+FirebaseEvents.REQUEST_OTP, bundle);
                }
                else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
                    MyApplication.getInstance().logEvent(FirebaseEvents.FB_REVENUE+"_"+FirebaseEvents.MOBIKWIK_WALLET+"_"+FirebaseEvents.REQUEST_OTP, bundle);
                }

				generateOTP(false);
			}
		});

		buttonVerifyOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String otp = editTextOTP.getText().toString().trim();
				if ("".equalsIgnoreCase(otp)) {
					editTextOTP.requestFocus();
					editTextOTP.setError("Please enter OTP");
				} else {
					sendOTP(editTextOTP.getText().toString().trim());
					Utils.hideSoftKeyboard(paymentActivity, editTextOTP);
				}
			}
		});

		linearLayoutOTP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextOTP.requestFocus();
				editTextOTP.setSelection(editTextOTP.getText().length());
				Utils.showSoftKeyboard(paymentActivity, editTextOTP);
				onClickListener.onClick(v);
			}
		});

		editTextOTP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				buttonVerifyOTP.performClick();
				return false;
			}
		});
		editTextOTP.setOnFocusChangeListener(onFocusChangeListener);
		editTextOTP.setOnClickListener(onClickListener);

		buttonResendOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				generateOTP(true);
			}
		});

		paymentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		try{
			textViewOTPNumber.setText(Data.userData.phoneNo);
		} catch(Exception e){
			e.printStackTrace();
		}

		setInitialUI();

		return rootView;
	}

	public void receiveOtp(String otp){
		try {
			if(buttonVerifyOTP.getVisibility() == View.VISIBLE) {
				editTextOTP.setText(otp);
				editTextOTP.setSelection(editTextOTP.getText().length());
				buttonVerifyOTP.performClick();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {
			if (hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, textViewScroll.getTop());
					}
				}, 200);
			} else {
				try {
					((EditText)v).setError(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				((EditText)v).setError(null);
			}
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					scrollView.smoothScrollTo(0, textViewScroll.getTop());
				}
			}, 200);
		}
	};

	private void setInitialUI(){
		if(openWalletType == PaymentOption.PAYTM.getOrdinal()) {
			textViewOTPMessage.setText(paymentActivity.getResources().getString(R.string.request_otp_message_paytm));
		}
		else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
			textViewOTPMessage.setText(paymentActivity.getResources().getString(R.string.request_otp_message_mobikwik));
		}
		linearLayoutOTP.setVisibility(View.GONE);

		buttonRequestOTP.setVisibility(View.VISIBLE);
		buttonVerifyOTP.setVisibility(View.GONE);
		buttonResendOTP.setVisibility(View.GONE);
	}


	private void setUIAfterRequest(){
		textViewOTPMessage.setText(paymentActivity.getResources().getString(R.string.sending_otp_to));
		linearLayoutOTP.setVisibility(View.VISIBLE);

		buttonRequestOTP.setVisibility(View.GONE);
		buttonVerifyOTP.setVisibility(View.VISIBLE);
		buttonResendOTP.setVisibility(View.VISIBLE);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	public void generateOTP(final boolean retry) {
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
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "requestOtp response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt(Constants.KEY_FLAG);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								setUIAfterRequest();
								if (retry) {
									DialogPopup.dialogBanner(paymentActivity, paymentActivity.getResources()
											.getString(R.string.otp_sent_successfully));
								}
							} else if (ApiResponseFlags.PAYTM_INVALID_EMAIL.getOrdinal() == flag) {
								DialogPopup.alertPopup(paymentActivity, "", message);
							} else {
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "requestOtp error="+error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				};

				if(openWalletType == PaymentOption.PAYTM.getOrdinal()) {
					RestClient.getApiServices().paytmRequestOtp(params, callback);
				}
				else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
					RestClient.getApiServices().mobikwikRequestOtp(params, callback);
				}
			} else{
				DialogPopup.dialogNoInternet(paymentActivity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								generateOTP(retry);
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

	private void sendOTP(final String otp) {
		try {
			if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
				DialogPopup.showLoadingDialog(paymentActivity, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
				params.put(Constants.KEY_OTP, otp);

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "loginWithOtp response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt(Constants.KEY_FLAG);
							if (ApiResponseFlags.PAYTM_LOGGED_IN.getOrdinal() == flag) {
								if (Data.userData != null && openWalletType == PaymentOption.PAYTM.getOrdinal()) {
									double balance = jObj.optDouble(Constants.KEY_BALANCE, -1);
									Data.userData.setPaytmBalance(balance);
									Data.userData.setPaytmEnabled(1);
									Prefs.with(paymentActivity).save(SPLabels.CHECK_BALANCE_LAST_TIME, System.currentTimeMillis());
									Prefs.with(paymentActivity).save(Constants.SP_LAST_ADDED_WALLET, PaymentOption.PAYTM.getOrdinal());
									paymentActivity.performGetBalanceSuccess(AddWalletFragment.class.getName());
								}
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if (Data.userData != null && openWalletType == PaymentOption.MOBIKWIK.getOrdinal()) {
									double balance = jObj.optDouble(Constants.KEY_BALANCE, -1);
									Data.userData.setMobikwikBalance(balance);
									Data.userData.setMobikwikEnabled(1);
									Prefs.with(paymentActivity).save(SPLabels.CHECK_BALANCE_LAST_TIME, System.currentTimeMillis());
									Prefs.with(paymentActivity).save(Constants.SP_LAST_ADDED_WALLET, PaymentOption.MOBIKWIK.getOrdinal());
									paymentActivity.performGetBalanceSuccess(AddWalletFragment.class.getName());
								}
							} else {
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "loginWithOtp error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
					}
				};

				if(openWalletType == PaymentOption.PAYTM.getOrdinal()) {
					RestClient.getApiServices().paytmLoginWithOtp(params, callback);
				}
				else if(openWalletType == PaymentOption.MOBIKWIK.getOrdinal()){
					RestClient.getApiServices().mobikwikLoginWithOtp(params, callback);
				}
			} else{
				DialogPopup.dialogNoInternet(paymentActivity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								sendOTP(otp);
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

}
