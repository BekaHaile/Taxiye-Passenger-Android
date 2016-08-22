package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FreshOrderHistoryAdapter;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.model.OrderHistory;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshOrderHistoryFragment extends Fragment implements FlurryEventNames, Constants, View.OnClickListener {

	private final String TAG = FreshOrderHistoryFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;
	private RecyclerView recyclerViewRideOrderHistory;
	private FreshOrderHistoryAdapter freshOrderHistoryAdapter;
	private ImageView imageViewNoItems;

	private OrderHistoryResponse orderHistoryResponse;
    private View mEmptyView;
	private View rootView;
    private TextView placeOrder;
    private SupportActivity activity;

	public FreshOrderHistoryFragment(){
	}


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
		super.onStop();
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_order_history, container, false);

        activity = (SupportActivity) getActivity();
		activity.fragmentUISetup(this);

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewRideOrderHistory = (RecyclerView) rootView.findViewById(R.id.recyclerViewRideOrderHistory);
		recyclerViewRideOrderHistory.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewRideOrderHistory.setItemAnimator(new DefaultItemAnimator());
		recyclerViewRideOrderHistory.setHasFixedSize(false);

        mEmptyView = rootView.findViewById(R.id.order_empty_view);
        mEmptyView.setVisibility(View.GONE);

        placeOrder = (TextView) mEmptyView.findViewById(R.id.place_order);
        placeOrder.setOnClickListener(this);

		freshOrderHistoryAdapter = new FreshOrderHistoryAdapter(activity, null,
				new FreshOrderHistoryAdapter.Callback() {
					@Override
					public void onClick(int position, OrderHistory orderHistory) {

						activity.setOrderHistoryOpened(position, orderHistory);
						activity.getTransactionUtils().openOrderSummaryFragment(activity,
								activity.getRelativeLayoutContainer());
					}

					@Override
					public void onShowMoreClick() {
						getOrderHistory();
					}
				}, getOrdersTotalCount());
		recyclerViewRideOrderHistory.setAdapter(freshOrderHistoryAdapter);


		getOrderHistory();


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
			freshOrderHistoryAdapter.setTotalOrders(getOrdersTotalCount());


			//freshOrderHistoryAdapter.notifyItemRemoved(activity.getOrderHistoryOpenedPosition());
		}
	}

	private int getOrdersTotalCount(){
		return orderHistoryResponse == null ? 0 : orderHistoryResponse.getOrderHistory() == null ? 0 : orderHistoryResponse.getOrderHistory().size();
	}

	public void getOrderHistory() {
		try {
			if(AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

				RestClient.getFreshApiService().orderHistory(params, new Callback<OrderHistoryResponse>() {
					@Override
					public void success(OrderHistoryResponse orderHistoryResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "getAllProducts response = " + responseStr);

						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt(Constants.KEY_FLAG);
								FreshOrderHistoryFragment.this.orderHistoryResponse = orderHistoryResponse;
								freshOrderHistoryAdapter.notifyList(getOrdersTotalCount(),
										(ArrayList<OrderHistory>) orderHistoryResponse.getOrderHistory());
								if(orderHistoryResponse.getOrderHistory() != null
									&& orderHistoryResponse.getOrderHistory().size() == 0){
//									imageViewNoItems.setVisibility(View.VISIBLE);
                                    mEmptyView.setVisibility(View.VISIBLE);
								} else{
                                    mEmptyView.setVisibility(View.GONE);
//									imageViewNoItems.setVisibility(View.GONE);
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
						getOrderHistory();
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}

	public OrderHistoryResponse getOrderHistoryResponse() {
		return orderHistoryResponse;
	}

	public void setOrderHistoryResponse(OrderHistoryResponse orderHistoryResponse) {
		this.orderHistoryResponse = orderHistoryResponse;
	}

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.place_order) {
            getActivity().finish();
        }
    }
}
