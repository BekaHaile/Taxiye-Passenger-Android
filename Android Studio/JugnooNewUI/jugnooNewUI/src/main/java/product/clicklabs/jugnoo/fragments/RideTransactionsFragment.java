package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.RideTransactionsAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class RideTransactionsFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = RideTransactionsFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;
	private RecyclerView recyclerViewRideTransactions;
	private LinearLayout linearLayoutNoRides;
	private Button buttonGetRide;

	private RideTransactionsAdapter rideTransactionsAdapter;

	ArrayList<HistoryResponse.Datum> rideInfosList = new ArrayList<>();
	int totalRides = 0;

	private View rootView;
    private FragmentActivity activity;

	public RideTransactionsFragment(){
	}


    @Override
    public void onStart() {
        super.onStart();
//        FlurryAgent.init(activity, Config.getFlurryKey());
//        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//        FlurryAgent.onEvent(RideTransactionsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
//        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ride_transactions, container, false);


        activity = getActivity();

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rideInfosList = new ArrayList<>();
		totalRides = 0;

		recyclerViewRideTransactions = (RecyclerView) rootView.findViewById(R.id.recyclerViewRideTransactions);
		recyclerViewRideTransactions.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewRideTransactions.setItemAnimator(new DefaultItemAnimator());
		recyclerViewRideTransactions.setHasFixedSize(false);
		linearLayoutNoRides = (LinearLayout)rootView.findViewById(R.id.linearLayoutNoRides);
		((TextView) rootView.findViewById(R.id.textViewNoRides)).setTypeface(Fonts.mavenLight(activity));
		buttonGetRide = (Button) rootView.findViewById(R.id.buttonGetRide); buttonGetRide.setTypeface(Fonts.mavenRegular(activity));
		linearLayoutNoRides.setVisibility(View.GONE);

		rideTransactionsAdapter = new RideTransactionsAdapter(rideInfosList, activity, R.layout.list_item_ride_transaction,
				new RideTransactionsAdapter.Callback() {
					@Override
					public void onClick(int position, HistoryResponse.Datum historyData) {
						try {
							if (historyData.getProductType() == ProductType.AUTO.getOrdinal()) {
								if (AppStatus.getInstance(activity).isOnline(activity)) {
									if (activity instanceof RideTransactionsActivity) {
										new TransactionUtils().openRideSummaryFragmentWithRideCancelledFlag(activity, ((RideTransactionsActivity) activity).getContainer(),
												historyData.getEngagementId(), historyData.getIsCancelledRide() == 1, historyData.getAutosStatus());
									} else if (activity instanceof SupportActivity) {
										new TransactionUtils().openRideIssuesFragment(activity,
												((SupportActivity) activity).getContainer(),
												historyData.getEngagementId(), -1, null, null, 0, historyData.getIsCancelledRide() == 1, historyData.getAutosStatus(),
												null);
									}
								} else {
									DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
								}
							} else if (historyData.getProductType() == ProductType.FRESH.getOrdinal()
									|| historyData.getProductType() == ProductType.MEALS.getOrdinal()
									|| historyData.getProductType() == ProductType.GROCERY.getOrdinal()
									|| historyData.getProductType() == ProductType.MENUS.getOrdinal()
									|| historyData.getProductType() == ProductType.PAY.getOrdinal()) {
								if (activity instanceof RideTransactionsActivity) {
										new TransactionUtils().openOrderStatusFragment(activity, ((RideTransactionsActivity) activity).getContainer(),
												historyData.getOrderId(), historyData.getProductType());
								} else if (activity instanceof SupportActivity) {
									new TransactionUtils().openRideIssuesFragment(activity,
											((SupportActivity) activity).getContainer(),
											-1, historyData.getOrderId(), null, null, 0, false, 0,
											historyData);
								}
							}

							FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_RIDE_SUMMARY);
							FlurryEventLogger.event(FlurryEventNames.RIDE_SUMMARY_CHECKED_LATER);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onShowMoreClick() {
						getRecentRidesAPI(activity, false);
					}
				}, totalRides);
		recyclerViewRideTransactions.setAdapter(rideTransactionsAdapter);

		buttonGetRide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof RideTransactionsActivity) {
					((RideTransactionsActivity)activity).performBackPressed();
				} else{
					buttonGetRide.setVisibility(View.GONE);
				}
			}
		});

		updateGetRideButton();

		getRecentRidesAPI(activity, true);
		setActivityTitle();


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
			setActivityTitle();
			if(Data.isOrderCancelled || Data.isSupportRideIssueUpdated) {
				Data.isOrderCancelled = false;
				getRecentRidesAPI(activity, true);
			}
		}
	}

	private void updateGetRideButton(){
		try {
			if(Data.userData.getMealsEnabled() == 0 && Data.userData.getFreshEnabled() == 0 && Data.userData.getDeliveryEnabled() == 0){
				buttonGetRide.setVisibility(View.VISIBLE);
			} else{
				buttonGetRide.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setActivityTitle(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(MyApplication.getInstance().ACTIVITY_NAME_HISTORY);
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(activity.getResources().getString(R.string.history));
		}
	}

	public void getRecentRidesAPI(final Activity activity, final boolean refresh) {
		try {
			DialogPopup.dismissLoadingDialog();
			if(AppStatus.getInstance(activity).isOnline(activity)) {

				if(refresh){
					rideInfosList.clear();
				}

				DialogPopup.showLoadingDialog(activity, "Loading...");
				linearLayoutNoRides.setVisibility(View.GONE);

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_START_FROM, "" + rideInfosList.size());

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().getRecentRides(params, new Callback<HistoryResponse>() {
					@Override
					public void success(HistoryResponse historyResponse, Response response) {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						Log.i("Server response", "response = " + responseStr);
						try {

							JSONObject jObj = new JSONObject(responseStr);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
									totalRides = jObj.getInt("history_size");
									rideInfosList.addAll(historyResponse.getData());

									updateListData("You haven't tried Jugnoo yet.", false);

								} else {
									updateListData("Some error occurred, tap to retry", true);
								}
							} else {
								updateListData("Some error occurred, tap to retry", true);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							updateListData("Some error occurred, tap to retry", true);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e(TAG, "getRecentRidesAPI error="+error.toString());
						updateListData("Some error occurred, tap to retry", true);
						DialogPopup.dismissLoadingDialog();
					}
				});
			}
			else {
				updateListData("No internet connection, tap to retry", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			linearLayoutNoRides.setVisibility(View.VISIBLE);
			if(activity instanceof RideTransactionsActivity) {
				updateGetRideButton();
			} else{
				buttonGetRide.setVisibility(View.GONE);
			}

			rideInfosList.clear();
			rideTransactionsAdapter.notifyList(totalRides);
		}
		else{
			if(rideInfosList.size() == 0){
				linearLayoutNoRides.setVisibility(View.VISIBLE);
				if(activity instanceof RideTransactionsActivity) {
					updateGetRideButton();
				} else{
					buttonGetRide.setVisibility(View.GONE);
				}
			} else{
				linearLayoutNoRides.setVisibility(View.GONE);
				buttonGetRide.setVisibility(View.GONE);
			}
			rideTransactionsAdapter.notifyList(totalRides);
		}
	}


}
