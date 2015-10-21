package product.clicklabs.jugnoo.wallet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.payu.sdk.Constants;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class AddPaytmFragment extends Fragment {

	RelativeLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewAddWalletHelp, textViewOTPMessage, textViewOTPNumber;

	LinearLayout linearLayoutOTP;
	TextView textViewEnterOTP;
	EditText editTextOTP;

	Button buttonRequestOTP, buttonVerifyOTP, buttonResendOTP;

	View rootView;
	PaymentActivity paymentActivity;


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
		textViewEnterOTP = (TextView) rootView.findViewById(R.id.textViewEnterOTP); textViewEnterOTP.setTypeface(Fonts.latoRegular(paymentActivity));
		editTextOTP = (EditText) rootView.findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Fonts.latoRegular(paymentActivity));

		buttonRequestOTP = (Button) rootView.findViewById(R.id.buttonRequestOTP);	buttonRequestOTP.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonVerifyOTP = (Button) rootView.findViewById(R.id.buttonVerifyOTP);	buttonVerifyOTP.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonResendOTP = (Button) rootView.findViewById(R.id.buttonResendOTP);	buttonResendOTP.setTypeface(Fonts.latoRegular(paymentActivity));


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonRequestOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				generateOTP();
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
				}
			}
		});

		buttonResendOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				generateOTP();
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



	private void setInitialUI(){
		textViewOTPMessage.setText("Request an OTP to link PayTM Wallet to");
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
		getActivity().getSupportFragmentManager().popBackStack("AddPaytmFragment", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	public void generateOTP() {
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
						setUIAfterRequest();
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

						paymentActivity.getBalance(AddPaytmFragment.class.getName());

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



	private void openWebView(String jsonData) {
		String jData = "JsonData=\"" + jsonData.toString() + "\"";
		Log.e("jData", "jData = " + jData);

		Intent intent = new Intent(paymentActivity, PaytmRechargeWebViewActivity.class);
		intent.putExtra(Constants.POST_DATA, jsonData);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}


}
