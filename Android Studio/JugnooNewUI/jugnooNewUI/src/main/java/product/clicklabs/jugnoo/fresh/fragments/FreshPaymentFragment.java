package product.clicklabs.jugnoo.fresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiPaytmCheckBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.wallet.PaymentActivity;


public class FreshPaymentFragment extends Fragment {

	private final String TAG = FreshPaymentFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;

	private LinearLayout linearLayoutCash;
	private ImageView imageViewCashRadio;

	private RelativeLayout relativeLayoutPaytm;
	private ImageView imageViewPaytmRadio;
	private TextView textViewPaytm, textViewPaytmValue;
	private ProgressWheel progressBarPaytm;

	private View rootView;
    private FreshActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshPaymentFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_payment, container, false);


        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewPayForItems)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenLight(activity));

		linearLayoutCash = (LinearLayout) rootView.findViewById(R.id.linearLayoutCash);
		imageViewCashRadio = (ImageView) rootView.findViewById(R.id.imageViewCashRadio);

		relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
		imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
		textViewPaytm = (TextView) rootView.findViewById(R.id.textViewPaytm); textViewPaytm.setTypeface(Fonts.mavenLight(activity));
		textViewPaytmValue = (TextView) rootView.findViewById(R.id.textViewPaytmValue); textViewPaytmValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		progressBarPaytm = (ProgressWheel) rootView.findViewById(R.id.progressBarPaytm);
		progressBarPaytm.setVisibility(View.GONE);

		linearLayoutCash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.setPaymentOption(PaymentOption.CASH);
				setPaymentOptionUI();
			}
		});

		relativeLayoutPaytm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(Data.userData.getPaytmBalance() >= activity.updateCartValuesGetTotalPrice().first) {
						activity.setPaymentOption(PaymentOption.PAYTM);
						setPaymentOptionUI();

					} else if(Data.userData.getPaytmError() == 1){
						DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));

					} else {
						DialogPopup.alertPopupWithListener(activity, "",
								activity.getResources().getString(R.string.paytm_no_cash),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(activity, PaymentActivity.class);
										if (Data.userData.paytmEnabled == 1
												&& Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
											intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
										} else {
											intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
										}
										activity.startActivity(intent);
										activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									}
								});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		setPaymentOptionUI();
		getBalance();

		return rootView;
	}

	private ApiPaytmCheckBalance apiPaytmCheckBalance;
	public ApiPaytmCheckBalance getApiPaytmCheckBalance() {
		if (apiPaytmCheckBalance == null) {
			apiPaytmCheckBalance = new ApiPaytmCheckBalance(activity, new ApiPaytmCheckBalance.Callback() {
				@Override
				public void onSuccess() {
					activity.setPaymentOption(PaymentOption.PAYTM);
					setPaymentOptionUI();
				}

				@Override
				public void onFailure() {
					activity.setPaymentOption(PaymentOption.CASH);
					setPaymentOptionUI();
				}

				@Override
				public void onFinish() {

				}

				@Override
				public void onRetry(View view) {
					getBalance();
				}

				@Override
				public void onNoRetry(View view) {

				}
			});
		}
		return apiPaytmCheckBalance;
	}

	private void getBalance(){
		try {
			getApiPaytmCheckBalance().getBalance(Data.userData.paytmEnabled, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setPaymentOptionUI(){
		try {
			if(PaymentOption.PAYTM == activity.getPaymentOption()){
				if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
					progressBarPaytm.setVisibility(View.GONE);
				} else if(Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_INACTIVE)){
					activity.setPaymentOption(PaymentOption.CASH);
					progressBarPaytm.setVisibility(View.GONE);
				} else{
					activity.setPaymentOption(PaymentOption.CASH);
					progressBarPaytm.setVisibility(View.VISIBLE);
				}
			} else{
				activity.setPaymentOption(PaymentOption.CASH);
				progressBarPaytm.setVisibility(View.GONE);
			}
			textViewPaytmValue.setText(String.format(activity.getResources()
					.getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
			textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));

			if(Data.userData.paytmEnabled == 1 && Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)){
				textViewPaytmValue.setVisibility(View.VISIBLE);
				textViewPaytm.setText(activity.getResources().getString(R.string.nl_paytm_wallet));
			} else{
				textViewPaytmValue.setVisibility(View.GONE);
				textViewPaytm.setText(activity.getResources().getString(R.string.nl_add_paytm_wallet));
			}

			if(Data.userData.getPaytmError() == 1){
				activity.setPaymentOption(PaymentOption.CASH);
				relativeLayoutPaytm.setVisibility(View.GONE);
			} else{
				relativeLayoutPaytm.setVisibility(View.VISIBLE);
			}

			if(activity.getPaymentOption() == null
					|| activity.getPaymentOption() == PaymentOption.CASH){
				imageViewCashRadio.setImageResource(R.drawable.radio_selected_icon);
				imageViewPaytmRadio.setImageResource(R.drawable.radio_unselected_icon);
			} else{
				imageViewCashRadio.setImageResource(R.drawable.radio_unselected_icon);
				imageViewPaytmRadio.setImageResource(R.drawable.radio_selected_icon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden){
			activity.fragmentUISetup(this);
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
