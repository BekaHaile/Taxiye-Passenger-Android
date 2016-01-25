package product.clicklabs.jugnoo.support.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SupportRideIssuesFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private LinearLayout linearLayoutRideShortInfo;
	private RelativeLayout relativeLayoutIssueWithRide;
	private TextView textViewDriverName, textViewDriverCarNumber, textViewTripTotalValue;
	private TextView textViewDate, textViewStart, textViewEnd, textViewStartValue, textViewEndValue;

	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private SupportActivity activity;

	private int showPanelState = 0;
	private EndRideData endRideData;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportRideIssuesFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }


	public SupportRideIssuesFragment(EndRideData endRideData){
		this.endRideData = endRideData;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_ride_issues, container, false);

        activity = (SupportActivity) getActivity();
		activity.setTitle(activity.getResources().getString(R.string.support_ride_issues_title));

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showPanelState = 0;

		linearLayoutRideShortInfo = (LinearLayout)rootView.findViewById(R.id.linearLayoutRideShortInfo);
		relativeLayoutIssueWithRide = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutIssueWithRide);
		((TextView)rootView.findViewById(R.id.textViewIssueWithRide)).setTypeface(Fonts.mavenRegular(activity));
		textViewDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.mavenLight(activity));
		textViewDriverCarNumber = (TextView)rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewTripTotal)).setTypeface(Fonts.mavenLight(activity));
		textViewTripTotalValue = (TextView)rootView.findViewById(R.id.textViewTripTotalValue); textViewTripTotalValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		textViewDate = (TextView)rootView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.mavenRegular(activity));
		textViewStart = (TextView)rootView.findViewById(R.id.textViewStart); textViewStart.setTypeface(Fonts.mavenRegular(activity));
		textViewEnd = (TextView)rootView.findViewById(R.id.textViewEnd); textViewEnd.setTypeface(Fonts.mavenRegular(activity));
		textViewStartValue = (TextView)rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndValue = (TextView)rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.mavenLight(activity));

		recyclerViewSupportFaq = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(null, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						activity.openItemInFragment(Integer.parseInt(endRideData.engagementId), activity.getResources().getString(R.string.support_main_title), item);
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
		relativeLayoutIssueWithRide.setVisibility(View.GONE);
		recyclerViewSupportFaq.setVisibility(View.GONE);
		setRideData();
		showPanel();


		return rootView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
        System.gc();
	}


	private void showPanel() {
		if(!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, "");

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

			RestClient.getApiServices().showPanel(params,
					new Callback<ShowPanelResponse>() {
						@Override
						public void success(ShowPanelResponse showPanelResponse, Response response) {
							DialogPopup.dismissLoadingDialog();
							try {
								recyclerViewSupportFaq.setVisibility(View.VISIBLE);
								showPanelState = 1;
								update(showPanelResponse);
							} catch (Exception exception) {
								exception.printStackTrace();
								retryDialog(DialogErrorType.SERVER_ERROR);
							}
						}

						@Override
						public void failure(RetrofitError error) {
							DialogPopup.dismissLoadingDialog();
							recyclerViewSupportFaq.setVisibility(View.GONE);
							showPanelState = -1;
							retryDialog(DialogErrorType.CONNECTION_LOST);
						}
					});
		} else{
			retryDialog(DialogErrorType.NO_NET);
		}
	}



	private void retryDialog(DialogErrorType dialogErrorType){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick() {
						if(showPanelState != 1) {
							showPanel();
						}
					}

					@Override
					public void neutralClick() {

					}

					@Override
					public void negativeClick() {

					}
				});
	}


	private void update(ShowPanelResponse showPanelResponse){
		supportFAQItemsAdapter.setResults((ArrayList<ShowPanelResponse.Item>) showPanelResponse.getMenu());
	}

	private void setRideData(){
		try{
			if(endRideData != null){
				textViewDriverName.setText(endRideData.driverName);
				textViewDriverCarNumber.setText(endRideData.driverCarNumber);

				textViewStartValue.setText(endRideData.pickupAddress);
				textViewEndValue.setText(endRideData.dropAddress);

				textViewStart.append(" " + endRideData.pickupTime);
				textViewEnd.append(" " + endRideData.dropTime);

				textViewTripTotalValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.fare));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}



}
