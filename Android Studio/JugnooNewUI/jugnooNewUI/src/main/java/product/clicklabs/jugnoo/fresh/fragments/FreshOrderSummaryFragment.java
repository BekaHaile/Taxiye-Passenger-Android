package product.clicklabs.jugnoo.fresh.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.fresh.adapters.FreshOrderItemAdapter;
import product.clicklabs.jugnoo.fresh.models.OrderHistory;
import product.clicklabs.jugnoo.fresh.models.OrderHistoryResponse;
import product.clicklabs.jugnoo.fresh.models.OrderItem;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshOrderSummaryFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = FreshOrderSummaryFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;
	private RecyclerView recyclerViewOrderItems;
	private FreshOrderItemAdapter freshOrderItemAdapter;

	private TextView textViewOrderIdValue, textViewOrderDeliveryDateValue, textViewOrderDeliverySlotValue,
			textViewOrderTimeValue, textViewOrderAddressValue,
			textViewTotalAmountValue, textViewDeliveryChargesValue, textViewAmountPayableValue,
			textViewPaymentMode, textViewPaymentModeValue;
	private Button buttonCancelOrder;


	private View rootView;
    private FreshActivity activity;

	public FreshOrderSummaryFragment(){
	}


    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshOrderSummaryFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_order_summary, container, false);

        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewOrderId)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOrderDeliveryDate)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOrderDeliverySlot)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewOrderReceipt)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewTotalAmount)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewDeliveryCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewAmountPayable)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewPaymentBy)).setTypeface(Fonts.mavenRegular(activity));

		textViewOrderIdValue = (TextView) rootView.findViewById(R.id.textViewOrderIdValue); textViewOrderIdValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewOrderDeliveryDateValue = (TextView) rootView.findViewById(R.id.textViewOrderDeliveryDateValue); textViewOrderDeliveryDateValue.setTypeface(Fonts.mavenRegular(activity));
		textViewOrderDeliverySlotValue = (TextView) rootView.findViewById(R.id.textViewOrderDeliverySlotValue); textViewOrderDeliverySlotValue.setTypeface(Fonts.mavenRegular(activity));

		textViewOrderTimeValue = (TextView) rootView.findViewById(R.id.textViewOrderTimeValue); textViewOrderTimeValue.setTypeface(Fonts.mavenRegular(activity));
		textViewOrderAddressValue = (TextView) rootView.findViewById(R.id.textViewOrderAddressValue); textViewOrderAddressValue.setTypeface(Fonts.mavenRegular(activity));

		textViewTotalAmountValue = (TextView) rootView.findViewById(R.id.textViewTotalAmountValue); textViewTotalAmountValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDeliveryChargesValue = (TextView) rootView.findViewById(R.id.textViewDeliveryChargesValue); textViewDeliveryChargesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewAmountPayableValue = (TextView) rootView.findViewById(R.id.textViewAmountPayableValue); textViewAmountPayableValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewPaymentMode = (TextView) rootView.findViewById(R.id.textViewPaymentMode); textViewPaymentMode.setTypeface(Fonts.mavenRegular(activity));
		textViewPaymentModeValue = (TextView) rootView.findViewById(R.id.textViewPaymentModeValue); textViewPaymentModeValue.setTypeface(Fonts.mavenRegular(activity));
		buttonCancelOrder = (Button) rootView.findViewById(R.id.buttonCancelOrder); buttonCancelOrder.setTypeface(Fonts.mavenRegular(activity));

		recyclerViewOrderItems = (RecyclerView) rootView.findViewById(R.id.recyclerViewOrderItems);
		recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewOrderItems.setHasFixedSize(false);

		try {
			if(activity.getOrderHistoryOpened() != null) {
				OrderHistory orderHistory = activity.getOrderHistoryOpened();
				freshOrderItemAdapter = new FreshOrderItemAdapter(activity,
						(ArrayList<OrderItem>) orderHistory.getOrderItems());
				recyclerViewOrderItems.setAdapter(freshOrderItemAdapter);

				textViewOrderIdValue.setText(String.valueOf(orderHistory.getOrderId()));

				textViewTotalAmountValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount()
								- orderHistory.getDeliveryCharges())));
				textViewDeliveryChargesValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getDeliveryCharges())));
				textViewAmountPayableValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));
				if(orderHistory.getPaymentMode().equals(PaymentOption.PAYTM.getOrdinal())){
					textViewPaymentMode.setText(activity.getResources().getString(R.string.paytm));
				} else{
					textViewPaymentMode.setText(activity.getResources().getString(R.string.cash));
				}
				textViewPaymentModeValue.setText(String.format(activity.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format(orderHistory.getOrderAmount())));

				if(orderHistory.getStartTime() != null && orderHistory.getEndTime() != null) {
					textViewOrderDeliverySlotValue.setText(DateOperations.convertDayTimeAPViaFormat(orderHistory.getStartTime())
							+ " - " + DateOperations.convertDayTimeAPViaFormat(orderHistory.getEndTime()));
				} else {
					textViewOrderDeliverySlotValue.setText("");
				}
				if(orderHistory.getExpectedDeliveryDate() != null){
					textViewOrderDeliveryDateValue.setText(DateOperations.getDate(orderHistory.getExpectedDeliveryDate()));
				} else {
					textViewOrderDeliveryDateValue.setText("");
				}


				textViewOrderTimeValue.setText(DateOperations.getDate(DateOperations.utcToLocalTZ(orderHistory.getOrderTime())));
				textViewOrderAddressValue.setText(orderHistory.getDeliveryAddress());

				if(orderHistory.getCancellable() == 1){
					buttonCancelOrder.setText(R.string.cancel_order);
				}
				else {
					buttonCancelOrder.setText(R.string.ok);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonCancelOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getOrderHistoryOpened().getCancellable() == 1){

					DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Are you sure you want to cancel this order?", getResources().getString(R.string.ok),
							getResources().getString(R.string.cancel), new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							cancelOrderApiCall(activity.getOrderHistoryOpened().getOrderId());
						}
					}, new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}, false, false);
				}
				else {
					activity.performBackPressed();
				}

			}
		});

		FlurryEventLogger.event(activity, FlurryEventNames.FRESH_ORDER_SUMMARY);

		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
		System.gc();
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			activity.fragmentUISetup(this);
		}
	}

	private void cancelOrderApiCall(int orderId){
		try {
			if(AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_FRESH_ORDER_ID, String.valueOf(orderId));


				RestClient.getFreshApiService().cancelOrder(params, new Callback<OrderHistoryResponse>() {
					@Override
					public void success(OrderHistoryResponse orderHistoryResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "Fresh order cancel response = " + responseStr);
						DialogPopup.dismissLoadingDialog();
						try {
							if(orderHistoryResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
								activity.performBackPressed();
								//activity.getFreshOrderHistoryFragment().getOrderHistoryResponse().getOrderHistory().remove(activity.getOrderHistoryOpenedPosition());
								activity.getFreshOrderHistoryFragment().getOrderHistory();
							}
							else{
								DialogPopup.alertPopupWithListener(activity, "", orderHistoryResponse.getMessage(), new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								});
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "Fresh Cancel Order error" + error.toString());
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
						cancelOrderApiCall(activity.getOrderHistoryOpened().getOrderId());
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}

}
