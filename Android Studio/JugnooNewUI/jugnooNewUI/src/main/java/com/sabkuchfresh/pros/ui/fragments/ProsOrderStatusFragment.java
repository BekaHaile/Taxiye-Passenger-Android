package com.sabkuchfresh.pros.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.models.ProsOrderStatus;
import com.sabkuchfresh.pros.models.ProsOrderStatusResponse;
import com.sabkuchfresh.pros.ui.adapters.ProsCatalogueAdapter;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.MyApplication.getInstance;

/**
 * Created by shankar on 19/06/17.
 */

public class ProsOrderStatusFragment extends Fragment implements GAAction, GACategory{

	@Bind(R.id.tvServiceType)
	TextView tvServiceType;
	@Bind(R.id.tvServiceTime)
	TextView tvServiceTime;
	@Bind(R.id.ivDeliveryPlace)
	ImageView ivDeliveryPlace;
	@Bind(R.id.tvDeliveryPlace)
	TextView tvDeliveryPlace;
	@Bind(R.id.llDeliveryPlace)
	LinearLayout llDeliveryPlace;
	@Bind(R.id.tvDeliveryToVal)
	TextView tvDeliveryToVal;
	@Bind(R.id.tvAmountValue)
	TextView tvAmountValue;
	@Bind(R.id.ivPaidVia)
	ImageView ivPaidVia;
	@Bind(R.id.tvPaidViaValue)
	TextView tvPaidViaValue;
	@Bind(R.id.bNeedHelp)
	Button bNeedHelp;
	@Bind(R.id.bCancelOrder)
	Button bCancelOrder;
	@Bind(R.id.tvOrderStatus)
	TextView tvOrderStatus;

	private Activity activity;
	private int jobId;
	private int supportCategory;
	private String date;


	public static ProsOrderStatusFragment newInstance(int jobId) {
		ProsOrderStatusFragment fragment = new ProsOrderStatusFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_JOB_ID, jobId);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments() {
		jobId = getArguments().getInt(Constants.KEY_JOB_ID, -1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pros_order_status, container, false);

		activity = getActivity();
		parseArguments();
		setActivityUI();


		ButterKnife.bind(this, rootView);

		if (activity instanceof SupportActivity) {
			bNeedHelp.setVisibility(View.GONE);
		}

		getOrderData(activity);

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			setActivityUI();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick({R.id.bNeedHelp, R.id.bCancelOrder})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.bNeedHelp:
				View container = null;
				if(activity instanceof FreshActivity) {
					container = ((FreshActivity)activity).getRelativeLayoutContainer();
				} else if (activity instanceof RideTransactionsActivity) {
					container = ((RideTransactionsActivity) activity).getContainer();
				} else if (activity instanceof SupportActivity) {
					container = ((SupportActivity) activity).getContainer();
				}
				if(container != null) {
					homeUtil.openFuguOrSupport((FragmentActivity) activity, container,
							jobId, supportCategory, date, ProductType.PROS.getOrdinal());
				}
				break;

			case R.id.bCancelOrder:
				DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", "Are you sure you want to cancel this booking?",
						getResources().getString(R.string.ok),
						getResources().getString(R.string.cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								cancelOrderApi(activity);
								if (activity instanceof FreshActivity) {
									GAUtils.event(((FreshActivity) activity).getGaCategory(), PROS+ORDER_STATUS, PROS+ORDER+CANCELLED);
								}
							}
						}, new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}, false, false);

				break;
		}
	}

	private void setActivityUI(){
		if(activity instanceof FreshActivity) {
			((FreshActivity)activity).fragmentUISetup(this);
			((FreshActivity)activity).getTopBar().title.setText(activity.getString(R.string.service_hash_format, String.valueOf(jobId)));
		} else if (activity instanceof RideTransactionsActivity) {
			((RideTransactionsActivity) activity).setTitle(activity.getString(R.string.service_hash_format, String.valueOf(jobId)));
		} else if (activity instanceof SupportActivity) {
			((SupportActivity) activity).setTitle(activity.getString(R.string.service_hash_format, String.valueOf(jobId)));
		}
	}

	public void getOrderData(final Activity activity) {
		try {
			if (getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_JOB_ID, String.valueOf(jobId));
				params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(ProductType.PROS.getOrdinal()));
				params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

				Callback<ProsOrderStatusResponse> callback = new Callback<ProsOrderStatusResponse>() {
					@Override
					public void success(ProsOrderStatusResponse orderStatusResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Server response", "response = " + responseStr);
						try {
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, orderStatusResponse.getFlag(), orderStatusResponse.getError(), orderStatusResponse.getMessage())) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == orderStatusResponse.getFlag()) {
									setDataToUI(orderStatusResponse);
								} else {
									retryDialogOrderData(orderStatusResponse.getMessage(), DialogErrorType.SERVER_ERROR);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogOrderData("", DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialogOrderData("", DialogErrorType.CONNECTION_LOST);
					}
				};

				new HomeUtil().putDefaultParams(params);
				RestClient.getProsApiService().orderHistory(params, callback);
			} else {
				retryDialogOrderData("", DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialogOrderData(String message, DialogErrorType dialogErrorType) {
		if (TextUtils.isEmpty(message)) {
			DialogPopup.dialogNoInternet(activity,
					dialogErrorType,
					new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View view) {
							getOrderData(activity);
						}

						@Override
						public void neutralClick(View view) {

						}

						@Override
						public void negativeClick(View view) {
						}
					});
		} else {
			DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
					activity.getString(R.string.retry), activity.getString(R.string.cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							getOrderData(activity);
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							activity.onBackPressed();
						}
					}, false, false);
		}
	}


	private void setDataToUI(ProsOrderStatusResponse orderStatusResponse){
		tvAmountValue.setText(R.string.upon_inspection);
		if(orderStatusResponse != null && orderStatusResponse.getData() != null && orderStatusResponse.getData().size() > 0){
			ProsOrderStatusResponse.Datum datum = orderStatusResponse.getData().get(0);
			Pair<String, String> pair = datum.getProductNameAndJobAmount();
			tvServiceType.setText(pair.first);
			if(datum.getJobStatus() == ProsOrderStatus.ENDED.getOrdinal()
					|| datum.getJobStatus() == ProsOrderStatus.FAILED.getOrdinal()) {
				if (!TextUtils.isEmpty(pair.second)) {
					tvAmountValue.setText(activity.getString(R.string.rupees_value_format, pair.second));
				} else {
					tvAmountValue.setText(R.string.to_be_confirmed);
				}
			}
			tvOrderStatus.setText(ProsCatalogueAdapter.getProsOrderState(datum.getJobStatus()).second);
			tvOrderStatus.setTextColor(ContextCompat.getColor(activity, datum.getJobStatusColorRes()));

			tvServiceTime.setText(DateOperations.convertDateTimeUSToInd(datum.getJobPickupDatetime().replace("\\", "")));
			SearchResult searchResult = homeUtil.getNearBySavedAddress(activity,
					new LatLng(datum.getJobLatitude(), datum.getJobLongitude()),
					Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
			if(searchResult != null && !TextUtils.isEmpty(searchResult.getName())){
				llDeliveryPlace.setVisibility(View.VISIBLE);
				ivDeliveryPlace.setImageResource(homeUtil.getSavedLocationIcon(searchResult.getName()));
				tvDeliveryPlace.setText(searchResult.getName());
				tvDeliveryToVal.setText(searchResult.getAddress());
			} else {
				llDeliveryPlace.setVisibility(View.GONE);
				tvDeliveryToVal.setText(datum.getJobAddress());
			}
			date = datum.getJobPickupDatetime();
			supportCategory = datum.getSupportCategory();
			bCancelOrder.setVisibility(datum.getJobStatus() == ProsOrderStatus.UNASSIGNED.getOrdinal() ? View.VISIBLE : View.GONE);
		}
	}

	private HomeUtil homeUtil = new HomeUtil();


	public void cancelOrderApi(final Activity activity) {
		try {
			if (getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_JOB_ID, String.valueOf(jobId));
				params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(ProductType.PROS.getOrdinal()));
				params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

				Callback<SettleUserDebt> callback = new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt orderStatusResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Server response cancelBooking", "response = " + responseStr);
						try {
							if(TextUtils.isEmpty(orderStatusResponse.getMessage())){
								orderStatusResponse.setMessage(activity.getString(R.string.booking_cancelled_successfully));
							}
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, orderStatusResponse.getFlag(), orderStatusResponse.getError(), orderStatusResponse.getMessage())) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == orderStatusResponse.getFlag()) {
									DialogPopup.alertPopupWithListener(activity, "", orderStatusResponse.getMessage(), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											Data.isOrderCancelled = true;

											Intent intent = new Intent(Data.LOCAL_BROADCAST);
											intent.putExtra("message", "Order cancelled, refresh inventory");
											intent.putExtra("open_type", 10);
											LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

											activity.onBackPressed();
										}
									});
								} else {
									retryDialogCancelOrderApi(orderStatusResponse.getMessage(), DialogErrorType.SERVER_ERROR);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogCancelOrderApi("", DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("TAG", "cancelBooking error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialogCancelOrderApi("", DialogErrorType.CONNECTION_LOST);
					}
				};

				new HomeUtil().putDefaultParams(params);
				RestClient.getProsApiService().cancelBooking(params, callback);
			} else {
				retryDialogCancelOrderApi("", DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialogCancelOrderApi(String message, DialogErrorType dialogErrorType) {
		if (TextUtils.isEmpty(message)) {
			DialogPopup.dialogNoInternet(activity,
					dialogErrorType,
					new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View view) {
							cancelOrderApi(activity);
						}

						@Override
						public void neutralClick(View view) {

						}

						@Override
						public void negativeClick(View view) {
						}
					});
		} else {
			DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
					activity.getString(R.string.retry), activity.getString(R.string.cancel),
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							cancelOrderApi(activity);
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							activity.onBackPressed();
						}
					}, false, false);
		}
	}
}
