package product.clicklabs.jugnoo.wallet.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class WalletTopupFragment extends Fragment {

	private final String TAG = WalletTopupFragment.class.getSimpleName();

	RelativeLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle, textViewTNC;

	TextView textViewCurrentBalance, textViewCurrentBalanceValue;

	TextView textViewRechargeInfo;
	EditText editTextTopupCardCode;
	Button buttonRecharge;

	View rootView;
	PaymentActivity activity;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_wallet_topup, container, false);
		activity = (PaymentActivity) getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);

		new ASSL(activity, relative, 1134, 720, false);

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(activity));
		textViewTitle.getPaint().setShader(Utils.textColorGradient(activity, textViewTitle));
		textViewTitle.setText(getString(R.string.jugnoo_cash, getString(R.string.app_name)));
		textViewTNC = (TextView) rootView.findViewById(R.id.textViewTNC); textViewTNC.setTypeface(Fonts.mavenMedium(activity));

		((TextView) rootView.findViewById(R.id.textViewAddCashHelp)).setTypeface(Fonts.mavenRegular(activity));

		textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance);	textViewCurrentBalance.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.mavenRegular(activity));

		textViewRechargeInfo = (TextView) rootView.findViewById(R.id.textViewRechargeInfo); textViewRechargeInfo.setTypeface(Fonts.mavenMedium(activity));
		textViewRechargeInfo.setText(getString(R.string.recharge_jc_topup_info, getString(R.string.app_name)));
		editTextTopupCardCode = (EditText) rootView.findViewById(R.id.editTextTopupCardCode); editTextTopupCardCode.setTypeface(Fonts.mavenRegular(activity));

		buttonRecharge = (Button) rootView.findViewById(R.id.buttonRecharge); buttonRecharge.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);




		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(activity, editTextTopupCardCode);
				performBackPressed();
			}
		});

		editTextTopupCardCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if(actionId == EditorInfo.IME_ACTION_SEND){
					buttonRecharge.performClick();
					handled = true;
				}
				return false;
			}
		});


		buttonRecharge.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String code = editTextTopupCardCode.getText().toString().trim();
				if ("".equalsIgnoreCase(code)) {
					editTextTopupCardCode.requestFocus();
					editTextTopupCardCode.setError(getResources().getString(R.string.topup_code_empty));
				} else {
					if (Data.userData != null) {
						topupJCAPI(editTextTopupCardCode.getText().toString().trim());
					}
				}
			}
		});

		textViewTNC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					DialogPopup.alertPopupLeftOriented(activity, "", Data.userData.getJugnooCashTNC(), true, false, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		return rootView;
	}

	private void updateWalletBalance(){
		try{
			if(Data.userData != null){
				textViewCurrentBalanceValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormatWithoutFloat().format(Data.userData.getJugnooBalance())));
				textViewCurrentBalanceValue.setTextColor(activity.getResources().getColor(R.color.theme_green_color));
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


	public void performBackPressed() {
		activity.goBack();
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	private void topupJCAPI(final String code) {
		try {
			if(MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, activity.getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
				params.put(Constants.KEY_TOPUP_CARD_CODE, code);

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().topupCustomerJC(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt(Constants.KEY_FLAG);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(activity, message);
								performBackPressed();
								activity.getBalance("", PaymentOption.CASH.getOrdinal());
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAddMoney error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
					}
				});
			}
			else{
				DialogPopup.dialogNoInternet(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
						new Utils.AlertCallBackWithButtonsInterface() {
							@Override
							public void positiveClick(View view) {
								topupJCAPI(code);
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
