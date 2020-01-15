package com.sabkuchfresh.pros.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.hippo.ChatByUniqueIdAttributes;
import com.hippo.HippoConfig;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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

	@BindView(R.id.tv2r)
	TextView tv2r;
	@BindView(R.id.tv3r)
	TextView tv3r;
	@BindView(R.id.ivDeliveryPlaceFeed)
	ImageView ivDeliveryPlaceFeed;
	@BindView(R.id.tvDeliveryPlace)
	TextView tvDeliveryPlace;
	@BindView(R.id.llDeliveryPlaceFeed)
	LinearLayout llDeliveryPlaceFeed;
	@BindView(R.id.tvDeliveryToValFeed)
	TextView tvDeliveryToValFeed;
	@BindView(R.id.tvAmountValue)
	TextView tvAmountValue;
	@BindView(R.id.ivPaidVia)
	ImageView ivPaidVia;
	@BindView(R.id.tvPaidViaValue)
	TextView tvPaidViaValue;
	@BindView(R.id.bNeedHelpFeed)
	Button bNeedHelpFeed;
	@BindView(R.id.bCancelOrder)
	Button bCancelOrder;
	@BindView(R.id.tv1r)
	TextView tv1r;
	@BindView(R.id.tv1l)
	TextView tv1l;
	@BindView(R.id.tv2l)
	TextView tv2l;
	@BindView(R.id.tv3l)
	TextView tv3l;
	@BindView(R.id.tv4l)
	TextView tv4l;
	@BindView(R.id.ivFromPlace)
	ImageView ivFromPlace;
	@BindView(R.id.tvFromPlace)
	TextView tvFromPlace;
	@BindView(R.id.llFromPlace)
	LinearLayout llFromPlace;
	@BindView(R.id.tvFromToVal)
	TextView tvFromToVal;
	@BindView(R.id.llFromAddress)
	LinearLayout llFromAddress;
	@BindView(R.id.tv5l)
	TextView tv5l;
	@BindView(R.id.tvTaskDetails)
	TextView tvTaskDetails;
	@BindView(R.id.llAmount)
	LinearLayout llAmount;
	@BindView(R.id.llPaidVia)
	LinearLayout llPaidVia;

	private Activity activity;
	private int jobId, feedOrderId, productType;
	private int supportCategory;
	private String date;
	private HistoryResponse.Datum datumFeed;


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
			feedOrderId = getArguments().getInt(Constants.KEY_ORDER_ID, -1);
		}
	}

	Unbinder unbinder;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pros_order_status, container, false);

		activity = getActivity();
		parseArguments();
		setActivityUI();


		unbinder = ButterKnife.bind(this, rootView);

		if (activity instanceof SupportActivity) {
			bNeedHelpFeed.setVisibility(View.GONE);
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
			bNeedHelpFeed.setText(R.string.need_help);

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
			bNeedHelpFeed.setText(R.string.chat_support);

			getFeedOrderData(activity);
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
		unbinder.unbind();
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
								productType == ProductType.PROS.getOrdinal() ? jobId : feedOrderId, supportCategory, date, productType);
					}
				} else if(productType == ProductType.FEED.getOrdinal()){
					if (datumFeed != null && !TextUtils.isEmpty(datumFeed.getFuguChannelId())) {
						ChatByUniqueIdAttributes chatAttr = new ChatByUniqueIdAttributes.Builder()
								.setTransactionId(datumFeed.getFuguChannelId())
								.setUserUniqueKey(String.valueOf(Data.getFuguUserData().getUserId()))
								.setChannelName(datumFeed.getFuguChannelName())
								.setTags(datumFeed.getFuguTags())
								.build();
						HippoConfig.getInstance().openChatByUniqueId(chatAttr);
					} else {
						HippoConfig.getInstance().openChat(getActivity(), Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
					}
				}
				break;

			case R.id.bCancelOrder:
				DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getString(R.string.are_you_sure_cancel_booking),
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
			title = activity.getString(R.string.order_hash_format, String.valueOf(feedOrderId));
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
			tv1r.setText(ProsCatalogueAdapter.getProsOrderState(activity, datum.getJobStatus()).second);
			tv1r.setTextColor(ContextCompat.getColor(activity, datum.getJobStatusColorRes()));

			tv3r.setText(DateOperations.convertDateTimeUSToInd(datum.getJobPickupDatetime().replace("\\", "")));
			SearchResult searchResult = homeUtil.getNearBySavedAddress(activity,
					new LatLng(datum.getJobLatitude(), datum.getJobLongitude()),
					false);
			if (searchResult != null && !TextUtils.isEmpty(searchResult.getName())) {
				llDeliveryPlaceFeed.setVisibility(View.VISIBLE);
				ivDeliveryPlaceFeed.setImageResource(homeUtil.getSavedLocationIcon(searchResult.getName()));
				tvDeliveryPlace.setText(searchResult.getName());
				tvDeliveryToValFeed.setText(searchResult.getAddress());
			} else {
				llDeliveryPlaceFeed.setVisibility(View.GONE);
				tvDeliveryToValFeed.setText(datum.getJobAddress());
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
				DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
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
											intent.putExtra("message", getString(R.string.order_cancelled_refresh_inventory));
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
								getFeedOrderData(activity);
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
								getFeedOrderData(activity);
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


	public void getFeedOrderData(final Activity activity) {
		try {
			if (MyApplication.getInstance().isOnline()) {

				DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ORDER_ID, "" + feedOrderId);
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
									datumFeed = historyResponse.getData().get(0);
									setFeedOrderData(datumFeed, activity);
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
			tv3r.setText(getString(R.string.asap));
		}

		SearchResult searchResultFrom = homeUtil.getNearBySavedAddress(activity,
				new LatLng(datum.getFromLatitude(), datum.getFromLongitude()),
				false);
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
				false);
		if (searchResultTo != null && !TextUtils.isEmpty(searchResultTo.getName())) {
			llDeliveryPlaceFeed.setVisibility(View.VISIBLE);
			ivDeliveryPlaceFeed.setImageResource(homeUtil.getSavedLocationIcon(searchResultTo.getName()));
			tvDeliveryPlace.setText(searchResultTo.getName());
			tvDeliveryToValFeed.setText(searchResultTo.getAddress());
		} else {
			llDeliveryPlaceFeed.setVisibility(View.GONE);
			tvDeliveryToValFeed.setText(datum.getToAddress());
		}
		tvTaskDetails.setText(datum.getDetails());
	}
}
