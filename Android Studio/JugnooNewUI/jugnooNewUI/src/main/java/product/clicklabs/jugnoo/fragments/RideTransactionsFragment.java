package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FeedbackActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideSummaryActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.RideTransactionsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.RideInfo;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;


public class RideTransactionsFragment extends Fragment implements FlurryEventNames, Constants {

	private RelativeLayout relativeLayoutRoot;
	private RecyclerView recyclerViewRideTransactions;
	private TextView textViewInfo;
	private Button buttonGetRide;

	private RideTransactionsAdapter rideTransactionsAdapter;

	ArrayList<RideInfo> rideInfosList = new ArrayList<>();
	int totalRides = 0;

	private View rootView;
    private FragmentActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(RideTransactionsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
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
		textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.mavenLight(activity));
		buttonGetRide = (Button) rootView.findViewById(R.id.buttonGetRide); buttonGetRide.setTypeface(Fonts.mavenRegular(activity));
		textViewInfo.setVisibility(View.GONE);
		buttonGetRide.setVisibility(View.GONE);

		rideTransactionsAdapter = new RideTransactionsAdapter(rideInfosList, activity, R.layout.list_item_ride_transaction,
				new RideTransactionsAdapter.Callback() {
					@Override
					public void onClick(int position, RideInfo rideInfo) {
						try {
							if (0 == rideInfo.isCancelledRide) {
								if (AppStatus.getInstance(activity).isOnline(activity)) {
									Intent intent = new Intent(activity, RideSummaryActivity.class);
									intent.putExtra(Constants.KEY_ENGAGEMENT_ID, rideInfo.engagementId);
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
								} else {
									DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
								}
								FlurryEventLogger.event(FlurryEventNames.RIDE_SUMMARY_CHECKED_LATER);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onRateRideClick(int position, RideInfo rideInfo) {
						try {
							Intent intent = new Intent(activity, FeedbackActivity.class);
							intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.PAST_RIDE.getOrdinal());
							intent.putExtra("position", position);
							intent.putExtra(Constants.KEY_DRIVER_ID, rideInfo.driverId);
							intent.putExtra(Constants.KEY_ENGAGEMENT_ID, rideInfo.engagementId);
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
							FlurryEventLogger.event(FlurryEventNames.RIDE_RATED_ON_RIDE_HISTORY);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onShowMoreClick() {
						getRecentRidesAPI(activity, false);
					}
				});
		recyclerViewRideTransactions.setAdapter(rideTransactionsAdapter);


		textViewInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getRecentRidesAPI(activity, true);
			}
		});

		getRecentRidesAPI(activity, true);


		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
        System.gc();
	}

	public void getRecentRidesAPI(final Activity activity, final boolean refresh) {
		try {
			DialogPopup.dismissLoadingDialog();
			if(AppStatus.getInstance(activity).isOnline(activity)) {

				if(refresh){
					rideInfosList.clear();
				}

				DialogPopup.showLoadingDialog(activity, "Loading...");
				textViewInfo.setVisibility(View.GONE);

				RequestParams params = new RequestParams();

				params.put("access_token", Data.userData.accessToken);
				params.put("start_from", "" + rideInfosList.size());

				AsyncHttpClient client = Data.getClient();
				client.post(Config.getServerUrl() + "/get_recent_rides", params,
						new CustomAsyncHttpResponseHandler() {
							private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								updateListData("Some error occurred, tap to retry", true);
								DialogPopup.dismissLoadingDialog();
							}

							@Override
							public void onSuccess(String response) {
								Log.i("Server response", "response = " + response);
								try {

									jObj = new JSONObject(response);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
										int flag = jObj.getInt("flag");
										if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {

											totalRides = jObj.getInt("num_rides");

											if (jObj.has("rides")) {
												JSONArray jRidesArr = jObj.getJSONArray("rides");
												for (int i = 0; i < jRidesArr.length(); i++) {
													JSONObject jRide = jRidesArr.getJSONObject(i);
													int isRatedBefore = 1;
													if (jRide.has("is_rated_before")) {
														isRatedBefore = jRide.getInt("is_rated_before");
													}

													int driverId = 0;
													if (jRide.has("driver_id")) {
														driverId = jRide.getInt("driver_id");
													}

													int engagementId = 0;
													if (jRide.has("engagement_id")) {
														engagementId = jRide.getInt("engagement_id");
													}

													double waitTime = -1;
													if(jRide.has("wait_time")){
														waitTime = jRide.getDouble("wait_time");
													}

													int isCancelledRide = 0;
													if(jRide.has("is_cancelled_ride")){
														isCancelledRide = jRide.getInt("is_cancelled_ride");
													}

													rideInfosList.add(new RideInfo(jRide.getString("pickup_address"),
															jRide.getString("drop_address"),
															jRide.getDouble("amount"),
															jRide.getDouble("distance"),
															jRide.getDouble("ride_time"), waitTime,
															jRide.getString("date"), isRatedBefore, driverId, engagementId, isCancelledRide));
												}
											}

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
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			buttonGetRide.setVisibility(View.GONE);

			rideInfosList.clear();
			rideTransactionsAdapter.notifyDataSetChanged();
		}
		else{
			if(rideInfosList.size() == 0){
				textViewInfo.setVisibility(View.VISIBLE);
				textViewInfo.setText(message);
				buttonGetRide.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
				buttonGetRide.setVisibility(View.GONE);
			}
			rideTransactionsAdapter.notifyDataSetChanged();
		}
	}

}
