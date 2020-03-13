package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.commoncalls.ApiCancelOrder;
import com.sabkuchfresh.retrofit.model.OrderCancelReasonsResponse;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class OrderCancelReasonsFragment extends Fragment implements GAAction, GACategory {

	private CancelOptionsListAdapter cancelOptionsListAdapter;

	private RelativeLayout relativeLayoutOtherCancelOptionInner;
	private TextView textViewOtherCancelOption;
	private ImageView imageViewOtherCancelOptionCheck;
	private EditText editTextOtherCancelOption;
	private TextView textViewOtherError;
	private boolean otherChecked = false;
	private TextView textViewCancelInfo;
	private ScrollView scrollView;
	private Button buttonCancelRide;

	private Activity activity;
	private int orderId, productType, storeId;
	private String clientId;
	private ArrayList<CancelOption> cancelOptions;
	private OrderCancelReasonsResponse reasonsResponse;

	public OrderCancelReasonsFragment() {
	}

	public static OrderCancelReasonsFragment newInstance(int orderId, int productType, int storeId, String clientId) {
		OrderCancelReasonsFragment fragment = new OrderCancelReasonsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_ORDER_ID, orderId);
		bundle.putInt(Constants.KEY_PRODUCT_TYPE, productType);
		bundle.putString(Constants.KEY_CLIENT_ID, clientId);
		bundle.putInt(Constants.STORE_ID, storeId);
		fragment.setArguments(bundle);
		return fragment;
	}


	private void parseArguments() {
		orderId = getArguments().getInt(Constants.KEY_ORDER_ID);
		productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE);
		clientId = getArguments().getString(Constants.KEY_CLIENT_ID);
		storeId = getArguments().getInt(Constants.STORE_ID);
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_order_cancel_reasons, container, false);
		parseArguments();
		cancelOptions = new ArrayList<>();

		NonScrollListView listViewCancelOptions = (NonScrollListView) rootView.findViewById(R.id.listViewCancelOptions);
		cancelOptionsListAdapter = new CancelOptionsListAdapter(activity);
		listViewCancelOptions.setAdapter(cancelOptionsListAdapter);

		relativeLayoutOtherCancelOptionInner = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutOtherCancelOptionInner);
		textViewOtherCancelOption = (TextView) rootView.findViewById(R.id.textViewOtherCancelOption);
		imageViewOtherCancelOptionCheck = (ImageView) rootView.findViewById(R.id.imageViewOtherCancelOptionCheck);
		editTextOtherCancelOption = (EditText) rootView.findViewById(R.id.editTextOtherCancelOption);
		textViewOtherError = (TextView) rootView.findViewById(R.id.textViewOtherError);
		textViewOtherError.setVisibility(View.GONE);

		buttonCancelRide = (Button) rootView.findViewById(R.id.buttonCancelRide);
		textViewCancelInfo = (TextView) rootView.findViewById(R.id.textViewCancelInfo);

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

		editTextOtherCancelOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, editTextOtherCancelOption.getTop());
					}
				}, 200);
			}
		});

		editTextOtherCancelOption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollView.smoothScrollTo(0, editTextOtherCancelOption.getTop());
					}
				}, 200);
			}
		});

		editTextOtherCancelOption.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					textViewOtherError.setVisibility(View.GONE);
				}
			}
		});


		buttonCancelRide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (reasonsResponse != null) {
					String cancelReasonsStr = "";

					if ("".equalsIgnoreCase(reasonsResponse.getAdditionalReasons())) {
						otherChecked = false;
					}

					if (otherChecked) {
						cancelReasonsStr = editTextOtherCancelOption.getText().toString().trim();
						if ("".equalsIgnoreCase(cancelReasonsStr)) {
							textViewOtherError.setVisibility(View.VISIBLE);
						} else {
							cancelOrderApi(reasonsResponse.getAdditionalReasons(), cancelReasonsStr);
						}
					} else {
						for (int i = 0; i < cancelOptions.size(); i++) {
							if (cancelOptions.get(i).checked) {
								cancelReasonsStr = cancelOptions.get(i).name;
								break;
							}
						}
						if ("".equalsIgnoreCase(cancelReasonsStr)) {
							DialogPopup.alertPopup(activity, "", activity.getString(R.string.please_select_one_reason));
						} else {
							cancelOrderApi(cancelReasonsStr, "");
						}
					}
				}
			}
		});

		relativeLayoutOtherCancelOptionInner.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (reasonsResponse != null && !TextUtils.isEmpty(reasonsResponse.getAdditionalReasons())) {
					otherChecked = true;
					updateCheckBoxes();
				}
			}
		});


		fetchCancelReasonsApi();

		return rootView;
	}


	private void setCancelOptionsDataToUI() {
		try {
			if (reasonsResponse != null) {
				for (int i = 0; i < cancelOptions.size(); i++) {
					cancelOptions.get(i).checked = false;
				}

				if (TextUtils.isEmpty(reasonsResponse.getCancelInfo())) {
					textViewCancelInfo.setVisibility(View.GONE);
				} else {
					textViewCancelInfo.setVisibility(View.VISIBLE);
					textViewCancelInfo.setText(reasonsResponse.getCancelInfo());
				}

				if (TextUtils.isEmpty(reasonsResponse.getAdditionalReasons())) {
					relativeLayoutOtherCancelOptionInner.setVisibility(View.GONE);
					editTextOtherCancelOption.setVisibility(View.GONE);
				} else {
					relativeLayoutOtherCancelOptionInner.setVisibility(View.VISIBLE);
					editTextOtherCancelOption.setVisibility(View.VISIBLE);
					textViewOtherCancelOption.setText(reasonsResponse.getAdditionalReasons());
					editTextOtherCancelOption.setHint(TextUtils.isEmpty(reasonsResponse.getCommentPlaceholder()) ?
							"" : reasonsResponse.getCommentPlaceholder());
				}

				otherChecked = false;
				updateCheckBoxes();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateCheckBoxes() {
		if (otherChecked) {
			for (int i = 0; i < cancelOptions.size(); i++) {
				cancelOptions.get(i).checked = false;
			}
			relativeLayoutOtherCancelOptionInner.setBackgroundColor(Color.WHITE);
			imageViewOtherCancelOptionCheck.setImageResource(R.drawable.check_box_checked);
			editTextOtherCancelOption.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutOtherCancelOptionInner.setBackgroundColor(Color.TRANSPARENT);
			imageViewOtherCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked);
			editTextOtherCancelOption.setVisibility(View.GONE);
		}
		cancelOptionsListAdapter.notifyDataSetChanged();
	}


	private class ViewHolderCancelOption {
		TextView textViewCancelOption;
		ImageView imageViewCancelOptionCheck;
		RelativeLayout relative;
		int id;
	}

	private class CancelOptionsListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderCancelOption holder;
		Context context;

		CancelOptionsListAdapter(Context context) {
			this.context = context;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return cancelOptions == null ? 0 : cancelOptions.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderCancelOption();
				convertView = mInflater.inflate(R.layout.list_item_order_cancel_option, null);

				holder.textViewCancelOption = (TextView) convertView.findViewById(R.id.textViewCancelOption);
				holder.imageViewCancelOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewCancelOptionCheck);

				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderCancelOption) convertView.getTag();
			}

			holder.id = position;

			CancelOption cancelOption = cancelOptions.get(position);
			holder.textViewCancelOption.setText(cancelOption.name);

			if (cancelOption.checked) {
				holder.relative.setBackgroundColor(Color.WHITE);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_checked);
			} else {
				holder.relative.setBackgroundColor(Color.TRANSPARENT);
				holder.imageViewCancelOptionCheck.setImageResource(R.drawable.check_box_unchecked);
			}

			holder.relative.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderCancelOption) v.getTag();
					for (int i = 0; i < cancelOptions.size(); i++) {
						cancelOptions.get(i).checked = holder.id == i;
					}
					otherChecked = false;

					if (textViewOtherError.getVisibility() == View.VISIBLE) {
						textViewOtherError.setVisibility(View.GONE);
					}
					updateCheckBoxes();
				}
			});

			return convertView;
		}
	}



	public void fetchCancelReasonsApi() {
		try {
			if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ORDER_ID, String.valueOf(orderId));
				params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(productType));

				retrofit.Callback<OrderCancelReasonsResponse> callback = new retrofit.Callback<OrderCancelReasonsResponse>() {
					@Override
					public void success(OrderCancelReasonsResponse reasonsResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						try {
							String message = reasonsResponse.getMessage();
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, reasonsResponse.getFlag(),
									reasonsResponse.getError(), reasonsResponse.getMessage())) {
								if (reasonsResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
									OrderCancelReasonsFragment.this.reasonsResponse = reasonsResponse;
									cancelOptions.clear();
									cancelOptions.addAll(reasonsResponse.getCancelOptions());
									buttonCancelRide.setVisibility(View.VISIBLE);
									setCancelOptionsDataToUI();
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							retryDialogFetchCancelReasons(DialogErrorType.SERVER_ERROR);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						retryDialogFetchCancelReasons(DialogErrorType.CONNECTION_LOST);
					}
				};

				new HomeUtil().putDefaultParams(params);
				if (productType == ProductType.MENUS.getOrdinal() || productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
					RestClient.getMenusApiService().fetchCancellationReasons(params, callback);
				} else {
					RestClient.getFreshApiService().fetchCancellationReasons(params, callback);
				}

			} else {
				retryDialogFetchCancelReasons(DialogErrorType.NO_NET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialogFetchCancelReasons(DialogErrorType dialogErrorType) {
		DialogPopup.dialogNoInternet(activity, dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						fetchCancelReasonsApi();
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {

					}
				});
	}


	private ApiCancelOrder apiCancelOrder;
	private void cancelOrderApi(final String reasons, final String addnReasons){
		if(apiCancelOrder == null){
			apiCancelOrder = new ApiCancelOrder(activity, new ApiCancelOrder.Callback() {
				@Override
				public void onSuccess(String message) {
					DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Data.isOrderCancelled = true;

							Intent intent = new Intent(Data.LOCAL_BROADCAST);
							intent.putExtra("message", getString(R.string.order_cancelled_refresh_inventory));
							intent.putExtra("open_type", 10);
							LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

							activity.onBackPressed();
							activity.onBackPressed();
						}
					});
				}

				@Override
				public void onFailure() {

				}

				@Override
				public void onRetry(View view) {
					cancelOrderApi(reasons, addnReasons);
				}

				@Override
				public void onNoRetry(View view) {

				}
			});
		}
		apiCancelOrder.hit(orderId, clientId, storeId, productType, reasons, addnReasons);
	}

}
