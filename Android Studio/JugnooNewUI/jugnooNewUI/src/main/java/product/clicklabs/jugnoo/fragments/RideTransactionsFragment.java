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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FeedbackActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.RideTransactionsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.RideInfo;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
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

	ArrayList<RideInfo> rideInfosList = new ArrayList<>();
	int totalRides = 0;

	private View rootView;
    private FragmentActivity activity;

	public RideTransactionsFragment(){
	}


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
		linearLayoutNoRides = (LinearLayout)rootView.findViewById(R.id.linearLayoutNoRides);
		((TextView) rootView.findViewById(R.id.textViewNoRides)).setTypeface(Fonts.mavenLight(activity));
		buttonGetRide = (Button) rootView.findViewById(R.id.buttonGetRide); buttonGetRide.setTypeface(Fonts.mavenRegular(activity));
		linearLayoutNoRides.setVisibility(View.GONE);
		buttonGetRide.setVisibility(View.GONE);

		rideTransactionsAdapter = new RideTransactionsAdapter(rideInfosList, activity, R.layout.list_item_ride_transaction,
				new RideTransactionsAdapter.Callback() {
					@Override
					public void onClick(int position, RideInfo rideInfo) {
						try {
							if (0 == rideInfo.isCancelledRide) {
								if (AppStatus.getInstance(activity).isOnline(activity)) {
									if(activity instanceof RideTransactionsActivity){
										((RideTransactionsActivity)activity).openRideSummaryFragment(rideInfo.engagementId);
									} else if(activity instanceof SupportActivity){
										new TransactionUtils().openRideIssuesFragment(activity,
												((SupportActivity) activity).getContainer(),
												rideInfo.engagementId, null, null);
									}
									FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_RIDE_SUMMARY);
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
		}
	}

	private void setActivityTitle(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(MyApplication.getInstance().ACTIVITY_NAME_HISTORY);
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(activity.getResources().getString(R.string.ride_history));
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
				buttonGetRide.setVisibility(View.GONE);

				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("start_from", "" + rideInfosList.size());

				RestClient.getApiServices().getRecentRides(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						Log.i("Server response", "response = " + responseStr);
						try {

							JSONObject jObj = new JSONObject(responseStr);
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
				buttonGetRide.setVisibility(View.VISIBLE);
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
					buttonGetRide.setVisibility(View.VISIBLE);
				} else{
					buttonGetRide.setVisibility(View.GONE);
				}
			}
			else{
				linearLayoutNoRides.setVisibility(View.GONE);
				buttonGetRide.setVisibility(View.GONE);
			}
			rideTransactionsAdapter.notifyList(totalRides);
		}
	}


}
