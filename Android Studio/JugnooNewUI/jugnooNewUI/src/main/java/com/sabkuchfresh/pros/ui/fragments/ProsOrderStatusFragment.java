package com.sabkuchfresh.pros.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import com.fugu.FuguConfig;
import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.api.ApiProsOrderStatus;
import com.sabkuchfresh.pros.models.ProsOrderStatus;
import com.sabkuchfresh.pros.models.ProsOrderStatusResponse;
import com.sabkuchfresh.pros.ui.adapters.ProsCatalogueAdapter;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
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
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.MyApplication.getInstance;

/**
 * Created by shankar on 19/06/17.
 */

public class ProsOrderStatusFragment extends Fragment implements GAAction, GACategory {

	@Bind(R.id.tv2r)
	TextView tv2r;
	@Bind(R.id.tv3r)
	TextView tv3r;
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
	@Bind(R.id.tv1r)
	TextView tv1r;
	@Bind(R.id.tv1l)
	TextView tv1l;
	@Bind(R.id.tv2l)
	TextView tv2l;
	@Bind(R.id.tv3l)
	TextView tv3l;
	@Bind(R.id.tv4l)
	TextView tv4l;
	@Bind(R.id.ivFromPlace)
	ImageView ivFromPlace;
	@Bind(R.id.tvFromPlace)
	TextView tvFromPlace;
	@Bind(R.id.llFromPlace)
	LinearLayout llFromPlace;
	@Bind(R.id.tvFromToVal)
	TextView tvFromToVal;
	@Bind(R.id.llFromAddress)
	LinearLayout llFromAddress;
	@Bind(R.id.tv5l)
	TextView tv5l;
	@Bind(R.id.tvTaskDetails)
	TextView tvTaskDetails;
	@Bind(R.id.llAmount)
	LinearLayout llAmount;
	@Bind(R.id.llPaidVia)
	LinearLayout llPaidVia;

	private Activity activity;
	private int jobId, orderId, productType;
	private int supportCategory;
	private String date;
	private HistoryResponse.Datum datum;


	public static ProsOrderStatusFragment newInstance(int jobId, int productType) {
		ProsOrderStatusFragment fragment = new ProsOrderStatusFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_PRODUCT_TYPE, productType);
		if(productType == ProductType.PROS.getOrdinal()) {
			bundle.putInt(Constants.KEY_JOB_ID, jobId);
		} else if(productType == ProductType.FEED.getOrdinal()) {
			bundle.putInt(Constants.KEY_ORDER_ID, jobId);
		}
		fragment.setArguments(bundle);
		return fragment;
	}

	private void parseArguments() {
		productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE, ProductType.PROS.getOrdinal());
		if(productType == ProductType.PROS.getOrdinal()) {
			jobId = getArguments().getInt(Constants.KEY_JOB_ID, -1);
		} else if(productType == ProductType.FEED.getOrdinal()) {
			orderId = getArguments().getInt(Constants.KEY_ORDER_ID, -1);
		}
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

		if(productType == ProductType.PROS.getOrdinal()) {
			tv1l.setText(R.string.service_status_colon);
			tv2l.setText(R.string.service_type_colon);
			tv3l.setText(R.string.service_time_colon);
			llFromAddress.setVisibility(View.GONE);
			tv5l.setText(R.string.service_address_colon);
			tvTaskDetails.setVisibility(View.GONE);
			llAmount.setVisibility(View.VISIBLE);
			llPaidVia.setVisibility(View.VISIBLE);
			bNeedHelp.setText(R.string.need_help);

			getApiProsOrderStatus().getOrderData(activity, jobId);
		} else if(productType == ProductType.FEED.getOrdinal()) {
			tv1l.setText(R.string.status_colon);
			tv2l.setText(R.string.order_time_colon);
			tv3l.setText(R.string.delivery_time_colon);
			llFromAddress.setVisibility(View.VISIBLE);
			tv5l.setText(R.string.to_colon);
			tvTaskDetails.setVisibility(View.VISIBLE);
			llAmount.setVisibility(View.GONE);
			llPaidVia.setVisibility(View.GONE);
			bNeedHelp.setText(R.string.chat_support);

			getOrderData(activity);
		}

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
				if(productType == ProductType.PROS.getOrdinal()) {
					View container = null;
					if (activity instanceof FreshActivity) {
						container = ((FreshActivity) activity).getRelativeLayoutContainer();
					} else if (activity instanceof RideTransactionsActivity) {
						container = ((RideTransactionsActivity) activity).getContainer();
					} else if (activity instanceof SupportActivity) {
						container = ((SupportActivity) activity).getContainer();
					}
					if (container != null) {
						homeUtil.openFuguOrSupport((FragmentActivity) activity, container,
								productType == ProductType.PROS.getOrdinal() ? jobId : orderId, supportCategory, date, productType);
					}
				} else if(productType == ProductType.FEED.getOrdinal()){
					if (datum != null && !TextUtils.isEmpty(datum.getFuguChannelId())) {
						FuguConfig.getInstance().openChatByTransactionId(datum.getFuguChannelId(), String.valueOf(Data.getFuguUserData().getUserId()),
								datum.getFuguChannelName(), datum.getFuguTags());
					} else {
						FuguConfig.getInstance().openChat(getActivity(), Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
					}
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
									GAUtils.event(((FreshActivity) activity).getGaCategory(), PROS + ORDER_STATUS, PROS + ORDER + CANCELLED);
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

	private void setActivityUI() {
		String title = "";
		if(productType == ProductType.PROS.getOrdinal()){
			title = activity.getString(R.string.service_hash_format, String.valueOf(jobId));
		} else if(productType == ProductType.FEED.getOrdinal()){
			title = activity.getString(R.string.order_hash_format, String.valueOf(orderId));
		}
		if (activity instanceof FreshActivity) {
			((FreshActivity) activity).fragmentUISetup(this);
			((FreshActivity) activity).getTopBar().title.setText(title);
		} else if (activity instanceof RideTransactionsActivity) {
			((RideTransactionsActivity) activity).setTitle(title);
		} else if (activity instanceof SupportActivity) {
			((SupportActivity) activity).setTitle(title);
		}
	}

	private ApiProsOrderStatus apiProsOrderStatus;
	private ApiProsOrderStatus getApiProsOrderStatus() {
		if (apiProsOrderStatus == null) {
			apiProsOrderStatus = new ApiProsOrderStatus(new ApiProsOrderStatus.Callback() {
				@Override
				public void onNoRetry() {
					activity.onBackPressed();
				}

				@Override
				public void onSuccess(ProsOrderStatusResponse orderStatusResponse) {
					setDataToUI(orderStatusResponse);
				}
			});
		}
		return apiProsOrderStatus;
	}

	private void setDataToUI(ProsOrderStatusResponse orderStatusResponse) {
		tvAmountValue.setText(R.string.upon_inspection);
		if (orderStatusResponse != null && orderStatusResponse.getData() != null && orderStatusResponse.getData().size() > 0) {
			ProsOrderStatusResponse.Datum datum = orderStatusResponse.getData().get(0);
			Pair<String, String> pair = datum.getProductNameAndJobAmount();
			tv2r.setText(pair.first);
			if (datum.getJobStatus() == ProsOrderStatus.ENDED.getOrdinal()
					|| datum.getJobStatus() == ProsOrderStatus.FAILED.getOrdinal()) {
				if (!TextUtils.isEmpty(pair.second)) {
					tvAmountValue.setText(activity.getString(R.string.rupees_value_format, pair.second));
				} else {
					tvAmountValue.setText(R.string.to_be_confirmed);
				}
			}
			tv1r.setText(ProsCatalogueAdapter.getProsOrderState(datum.getJobStatus()).second);
			tv1r.setTextColor(ContextCompat.getColor(activity, datum.getJobStatusColorRes()));

			tv3r.setText(DateOperations.convertDateTimeUSToInd(datum.getJobPickupDatetime().replace("\\", "")));
			SearchResult searchResult = homeUtil.getNearBySavedAddress(activity,
					new LatLng(datum.getJobLatitude(), datum.getJobLongitude()),
					Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
			if (searchResult != null && !TextUtils.isEmpty(searchResult.getName())) {
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
							if (TextUtils.isEmpty(orderStatusResponse.getMessage())) {
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
									retryDialogCancelOrderOrOrderStatus(orderStatusResponse.getMessage(), DialogErrorType.SERVER_ERROR);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogCancelOrderOrOrderStatus("", DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("TAG", "cancelBooking error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialogCancelOrderOrOrderStatus("", DialogErrorType.CONNECTION_LOST);
					}
				};

				new HomeUtil().putDefaultParams(params);
				RestClient.getProsApiService().cancelBooking(params, callback);
			} else {
				retryDialogCancelOrderOrOrderStatus("", DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialogCancelOrderOrOrderStatus(String message, DialogErrorType dialogErrorType) {
		if (TextUtils.isEmpty(message)) {
			DialogPopup.dialogNoInternet(activity,
					dialogErrorType,
					new Utils.AlertCallBackWithButtonsInterface() {
						@Override
						public void positiveClick(View view) {
							if(productType == ProductType.PROS.getOrdinal()) {
								cancelOrderApi(activity);
							} else {
								getOrderData(activity);
							}
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
							if(productType == ProductType.PROS.getOrdinal()) {
								cancelOrderApi(activity);
							} else {
								getOrderData(activity);
							}
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


	public void getOrderData(final Activity activity) {
		try {
			if (MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ORDER_ID, "" + orderId);
				params.put(Constants.KEY_PRODUCT_TYPE, "" + productType);
				params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
				params.put(Constants.INTERATED, "1");

				Callback<HistoryResponse> callback = new Callback<HistoryResponse>() {
					@Override
					public void success(HistoryResponse historyResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Server response", "response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt("flag");
								String message = JSONParser.getServerMessage(jObj);
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									datum = historyResponse.getData().get(0);
									setFeedOrderData(datum, activity);
								} else {
									retryDialogCancelOrderOrOrderStatus(message, DialogErrorType.SERVER_ERROR);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogCancelOrderOrOrderStatus("", DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
						DialogPopup.dismissLoadingDialog();
						retryDialogCancelOrderOrOrderStatus("", DialogErrorType.CONNECTION_LOST);
					}
				};
				new HomeUtil().putDefaultParams(params);
				RestClient.getFatafatApiService().getCustomOrderHistory(params, callback);
			} else {
				retryDialogCancelOrderOrOrderStatus("", DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setFeedOrderData(HistoryResponse.Datum datum, Activity activity) {
		tv1r.setText(datum.getOrderStatus());
		try{
			tv1r.setTextColor(Color.parseColor(datum.getOrderStatusColor()));
		} catch (Exception e){
			tv1r.setTextColor(ContextCompat.getColor(activity, R.color.green_status));
		}
		tv2r.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(datum.getCreatedAt())));
		if(!TextUtils.isEmpty(datum.getDeliveryTime())){
			tv3r.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(datum.getDeliveryTime())));
		} else {
			tv3r.setText("ASAP");
		}

		SearchResult searchResultFrom = homeUtil.getNearBySavedAddress(activity,
				new LatLng(datum.getFromLatitude(), datum.getFromLongitude()),
				Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
		if (searchResultFrom != null && !TextUtils.isEmpty(searchResultFrom.getName())) {
			llFromPlace.setVisibility(View.VISIBLE);
			ivFromPlace.setImageResource(homeUtil.getSavedLocationIcon(searchResultFrom.getName()));
			tvFromPlace.setText(searchResultFrom.getName());
			tvFromToVal.setText(searchResultFrom.getAddress());
		} else {
			llFromPlace.setVisibility(View.GONE);
			tvFromToVal.setText(datum.getFromAddress());
		}

		SearchResult searchResultTo = homeUtil.getNearBySavedAddress(activity,
				new LatLng(datum.getToLatitude(), datum.getToLongitude()),
				Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION, false);
		if (searchResultTo != null && !TextUtils.isEmpty(searchResultTo.getName())) {
			llDeliveryPlace.setVisibility(View.VISIBLE);
			ivDeliveryPlace.setImageResource(homeUtil.getSavedLocationIcon(searchResultTo.getName()));
			tvDeliveryPlace.setText(searchResultTo.getName());
			tvDeliveryToVal.setText(searchResultTo.getAddress());
		} else {
			llDeliveryPlace.setVisibility(View.GONE);
			tvDeliveryToVal.setText(datum.getToAddress());
		}
		tvTaskDetails.setText(datum.getDetails());
	}
}