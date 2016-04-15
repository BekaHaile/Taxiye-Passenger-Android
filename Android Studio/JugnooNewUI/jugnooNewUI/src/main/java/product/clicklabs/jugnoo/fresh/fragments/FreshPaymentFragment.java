package product.clicklabs.jugnoo.fresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiPaytmCheckBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.FreshOrderCompleteDialog;
import product.clicklabs.jugnoo.fresh.FreshPaytmBalanceLowDialog;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.fresh.models.PlaceOrderResponse;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshPaymentFragment extends Fragment {

	private final String TAG = FreshPaymentFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;

	private LinearLayout linearLayoutCash;
	private ImageView imageViewCashRadio;

	private RelativeLayout relativeLayoutPaytm;
	private ImageView imageViewPaytmRadio;
	private TextView textViewPaytm, textViewPaytmValue;
	private ProgressWheel progressBarPaytm;
	private Button buttonPlaceOrder;

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
		buttonPlaceOrder = (Button) rootView.findViewById(R.id.buttonPlaceOrder); buttonPlaceOrder.setTypeface(Fonts.mavenRegular(activity));

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
					if(Data.userData.getPaytmBalance() >= getTotalPriceWithDeliveryCharges()) {
						activity.setPaymentOption(PaymentOption.PAYTM);
						setPaymentOptionUI();

					} else if(Data.userData.getPaytmError() == 1){
						DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));

					} else {
						showPaytmBalanceLowDialog();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				placeOrder();
			}
		});

		getBalance();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setPaymentOptionUI();
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

	private void placeOrder(){
		boolean goAhead = true;
		if(activity.getPaymentOption().getOrdinal() == PaymentOption.PAYTM.getOrdinal()) {
			if(Data.userData.getPaytmBalance() < getTotalPriceWithDeliveryCharges()) {
				if (Data.userData.getPaytmError() == 1) {
					DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
				} else {
					showPaytmBalanceLowDialog();
				}
				goAhead = false;
			}
		}
		if(goAhead) {
			DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
					activity.getResources().getString(R.string.place_order_confirmation),
					activity.getResources().getString(R.string.ok),
					activity.getResources().getString(R.string.cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							FlurryEventLogger.event(activity, FlurryEventNames.FRESH_PLACING_THE_ORDER);
							placeOrderApi();
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}, false, false);
		}
	}


	public void placeOrderApi() {
		try {
			if(AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

				params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(activity.getPaymentOption().getOrdinal()));
				params.put(Constants.KEY_DELIVERY_SLOT_ID, String.valueOf(activity.getSlotSelected().getDeliverySlotId()));
				params.put(Constants.KEY_DELIVERY_ADDRESS, String.valueOf(activity.getSelectedAddress()));

				JSONArray jCart = new JSONArray();
				if(activity.getProductsResponse() != null
						&& activity.getProductsResponse().getCategories() != null) {
					for (Category category : activity.getProductsResponse().getCategories()) {
						for (SubItem subItem : category.getSubItems()) {
							if (subItem.getSubItemQuantitySelected() > 0) {
								try {
									JSONObject jItem = new JSONObject();
									jItem.put(Constants.KEY_SUB_ITEM_ID, subItem.getSubItemId());
									jItem.put(Constants.KEY_QUANTITY, subItem.getSubItemQuantitySelected());
									jCart.put(jItem);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				params.put(Constants.KEY_CART, jCart.toString());



				Log.i(TAG, "getAllProducts params=" + params.toString());

				RestClient.getFreshApiService().placeOrder(params, new Callback<PlaceOrderResponse>() {
					@Override
					public void success(PlaceOrderResponse placeOrderResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
								if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
									new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
										@Override
										public void onDismiss() {
											activity.orderComplete();
										}
									}).show(String.valueOf(placeOrderResponse.getOrderId()),
											DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getStartTime())
													+ " - " + DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getEndTime()),
											activity.getSlotSelected().getDayName());

								} else{
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialog(DialogErrorType.CONNECTION_LOST);
					}
				});
			}
			else {
				retryDialog(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						placeOrderApi();
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}


	private void showPaytmBalanceLowDialog(){
		try {
			if(Data.userData.paytmEnabled == 1
					&& Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
				String amount = Utils.getMoneyDecimalFormat().format(Data.userData.getPaytmBalance() - getTotalPriceWithDeliveryCharges());
				new FreshPaytmBalanceLowDialog(activity, amount, new FreshPaytmBalanceLowDialog.Callback() {
					@Override
					public void onRechargeNowClicked() {
						intentToPaytm();
					}

					@Override
					public void onPayByCashClicked() {
						linearLayoutCash.performClick();
					}
				}).show();
			} else{
				intentToPaytm();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void intentToPaytm(){
		try {
			Intent intent = new Intent(activity, PaymentActivity.class);
			if (Data.userData.paytmEnabled == 1
					&& Data.userData.getPaytmStatus().equalsIgnoreCase(Data.PAYTM_STATUS_ACTIVE)) {
				intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.PAYTM_RECHARGE.getOrdinal());
				intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
						Utils.getMoneyDecimalFormat().format(getTotalPriceWithDeliveryCharges()
								- Data.userData.getPaytmBalance()));
			} else {
				intent.putExtra(Constants.KEY_ADD_PAYMENT_PATH, AddPaymentPath.ADD_PAYTM.getOrdinal());
			}
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double getTotalPriceWithDeliveryCharges(){
		return activity.updateCartValuesGetTotalPrice().first + activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();
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
