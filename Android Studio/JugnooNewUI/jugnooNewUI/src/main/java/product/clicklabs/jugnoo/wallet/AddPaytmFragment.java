package product.clicklabs.jugnoo.wallet;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


public class AddPaytmFragment extends Fragment {

	RelativeLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewAddWalletHelp, textViewOTPMessage, textViewOTPNumber;

	LinearLayout linearLayoutOTP;
	EditText editTextOTP;

	Button buttonRequestOTP, buttonVerifyOTP, buttonResendOTP;

	View rootView;
	PaymentActivity paymentActivity;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;


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
		rootView = inflater.inflate(R.layout.fragment_add_paytm, container, false);

		paymentActivity = (PaymentActivity) getActivity();


		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);


		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);

		textViewAddWalletHelp = (TextView) rootView.findViewById(R.id.textViewAddWalletHelp); textViewAddWalletHelp.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewOTPMessage = (TextView) rootView.findViewById(R.id.textViewOTPMessage); textViewOTPMessage.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewOTPNumber = (TextView) rootView.findViewById(R.id.textViewOTPNumber); textViewOTPNumber.setTypeface(Fonts.latoRegular(paymentActivity));

		linearLayoutOTP = (LinearLayout) rootView.findViewById(R.id.linearLayoutOTP);
		editTextOTP = (EditText) rootView.findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.latoRegular(paymentActivity));

		buttonRequestOTP = (Button) rootView.findViewById(R.id.buttonRequestOTP);	buttonRequestOTP.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonVerifyOTP = (Button) rootView.findViewById(R.id.buttonVerifyOTP);	buttonVerifyOTP.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonResendOTP = (Button) rootView.findViewById(R.id.buttonResendOTP);	buttonResendOTP.setTypeface(Fonts.latoRegular(paymentActivity));

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(paymentActivity, editTextOTP);
				performBackPressed();
			}
		});

		buttonRequestOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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

//		linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
//			@Override
//			public void keyboardOpened() {
//
//			}
//
//			@Override
//			public void keyBoardClosed() {
//
//			}
//		}));

		setInitialUI();


		return rootView;
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
		textViewOTPMessage.setText("Request an OTP to link Paytm Wallet to");
		linearLayoutOTP.setVisibility(View.GONE);

		buttonRequestOTP.setVisibility(View.VISIBLE);
		buttonVerifyOTP.setVisibility(View.GONE);
		buttonResendOTP.setVisibility(View.GONE);
	}


	private void setUIAfterRequest(){
		textViewOTPMessage.setText("Sending OTP To");
		linearLayoutOTP.setVisibility(View.VISIBLE);

		buttonRequestOTP.setVisibility(View.GONE);
		buttonVerifyOTP.setVisibility(View.VISIBLE);
		buttonResendOTP.setVisibility(View.VISIBLE);
	}


	/**
	 * Method used to remove fragment from stack
	 */
	public void performBackPressed() {
		getActivity().getSupportFragmentManager().popBackStack(AddPaytmFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
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
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");

				AsyncHttpClient client = Data.getClient();

				client.post(Config.getTXN_URL() + "/paytm/request_otp", params, new CustomAsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						Log.i("request succesfull", "response = " + response);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(response);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
								setUIAfterRequest();
								if(retry){
									DialogPopup.dialogBanner(paymentActivity, "OTP sent successfully");
								}
							}
							else if(ApiResponseFlags.PAYTM_INVALID_EMAIL.getOrdinal() == flag){
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
							else{
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
						} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendOTP(String otp) {
		try {
			if(AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
				DialogPopup.showLoadingDialog(paymentActivity, "Loading...");
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("client_id", Config.getClientId());
				params.put("is_access_token_new", "1");

				params.put("otp", "" + otp);

				AsyncHttpClient client = Data.getClient();

				client.post(Config.getTXN_URL() + "/paytm/login_with_otp", params, new CustomAsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						Log.i("request succesfull", "response = " + response);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(response);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if(ApiResponseFlags.PAYTM_OTP_ERROR.getOrdinal() == flag){
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
							else if(ApiResponseFlags.PAYTM_INACTIVE_LOGGED_IN.getOrdinal() == flag){
								DialogPopup.alertPopup(paymentActivity, "", message);
							}
							else if(ApiResponseFlags.PAYTM_LOGGED_IN.getOrdinal() == flag){
								if (Data.userData != null) {
									String balance = jObj.optString("balance", "0");
									Data.userData.setPaytmBalance(Double.parseDouble(balance));
									Data.userData.setPaytmStatus(Data.PAYTM_STATUS_ACTIVE);
									Data.userData.paytmEnabled = 1;

									Prefs.with(paymentActivity).save(SPLabels.PAYTM_CHECK_BALANCE_LAST_TIME, System.currentTimeMillis());
									paymentActivity.performGetBalanceSuccess(AddPaytmFragment.class.getName());
								}
							}

						} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
