package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Utils;

@SuppressLint("ValidFragment")
public class SupportRideIssuesFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private LinearLayout linearLayoutRideShortInfo;
	private ImageView imageViewDriver;
	private TextView textViewDriverName, textViewDriverCarNumber, textViewTripTotalValue;
	private TextView textViewDate, textViewStart, textViewEnd, textViewStartValue, textViewEndValue;

	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private FragmentActivity activity;

	private int engagementId;
	private EndRideData endRideData;
	private GetRideSummaryResponse getRideSummaryResponse;

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


	public SupportRideIssuesFragment(int engagementId, EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse){
		this.engagementId = engagementId;
		this.endRideData = endRideData;
		this.getRideSummaryResponse = getRideSummaryResponse;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_ride_issues, container, false);

        activity = getActivity();

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setActivityTitle();


		linearLayoutRideShortInfo = (LinearLayout)rootView.findViewById(R.id.linearLayoutRideShortInfo);
		textViewDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName); textViewDriverName.setTypeface(Fonts.mavenLight(activity));
		textViewDriverCarNumber = (TextView)rootView.findViewById(R.id.textViewDriverCarNumber); textViewDriverCarNumber.setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewTripTotal)).setTypeface(Fonts.mavenLight(activity));
		textViewTripTotalValue = (TextView)rootView.findViewById(R.id.textViewTripTotalValue); textViewTripTotalValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		imageViewDriver = (ImageView) rootView.findViewById(R.id.imageViewDriver);
		textViewDate = (TextView)rootView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.mavenRegular(activity));
		textViewStart = (TextView)rootView.findViewById(R.id.textViewStart); textViewStart.setTypeface(Fonts.mavenRegular(activity));
		textViewEnd = (TextView)rootView.findViewById(R.id.textViewEnd); textViewEnd.setTypeface(Fonts.mavenRegular(activity));
		textViewStartValue = (TextView)rootView.findViewById(R.id.textViewStartValue); textViewStartValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndValue = (TextView)rootView.findViewById(R.id.textViewEndValue); textViewEndValue.setTypeface(Fonts.mavenLight(activity));

		recyclerViewSupportFaq = (RecyclerView)rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(null, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						if(activity instanceof SupportActivity){
							new TransactionUtils().openItemInFragment(activity,
									((SupportActivity)activity).getContainer(),
									Integer.parseInt(endRideData.engagementId),
									activity.getResources().getString(R.string.support_main_title), item, endRideData.getPhoneNumber());

						} else if(activity instanceof RideTransactionsActivity){
							new TransactionUtils().openItemInFragment(activity,
									((RideTransactionsActivity)activity).getContainer(),
									Integer.parseInt(endRideData.engagementId),
									activity.getResources().getString(R.string.support_main_title), item, endRideData.getPhoneNumber());
						}
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		if(activity instanceof SupportActivity){
			if(endRideData == null){
				linearLayoutRideShortInfo.setVisibility(View.GONE);
				recyclerViewSupportFaq.setVisibility(View.GONE);
				getRideSummaryAPI(activity, ""+engagementId);
			} else{
				linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
				recyclerViewSupportFaq.setVisibility(View.VISIBLE);
				setRideData();
				updateIssuesList((ArrayList<ShowPanelResponse.Item>) getRideSummaryResponse.getMenu());
			}
		} else if(activity instanceof RideTransactionsActivity){
			linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
			recyclerViewSupportFaq.setVisibility(View.VISIBLE);
			setRideData();
			updateIssuesList((ArrayList<ShowPanelResponse.Item>) getRideSummaryResponse.getMenu());
		}

		return rootView;
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
			((RideTransactionsActivity)activity).setTitle(activity.getResources().getString(R.string.support_ride_issues_title));
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(activity.getResources().getString(R.string.support_ride_issues_title));
			((SupportActivity)activity).setImageViewInvoiceVisibility(View.VISIBLE);
			((SupportActivity)activity).setImageViewInvoiceOnCLickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(activity instanceof SupportActivity && endRideData != null) {
						((SupportActivity) activity).openRideSummaryFragment(endRideData);
					}
				}
			});
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(root);
        System.gc();
	}




	private void updateIssuesList(ArrayList<ShowPanelResponse.Item> items){
		supportFAQItemsAdapter.setResults((ArrayList<ShowPanelResponse.Item>) items);
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

				if(!"".equalsIgnoreCase(endRideData.driverImage)){
					Picasso.with(activity).load(endRideData.driverImage).transform(new CircleTransform()).into(imageViewDriver);
				}

				textViewDate.setText(String.format(activity.getResources().getString(R.string.date_colon_format),
						endRideData.getRideDate()));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void getRideSummaryAPI(final Activity activity, final String engagementId) {
		new ApiGetRideSummary(activity, Data.userData.accessToken, Integer.parseInt(engagementId), Data.fareStructure.fixedFare,
				new ApiGetRideSummary.Callback() {
					@Override
					public void onSuccess(EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse) {
						SupportRideIssuesFragment.this.endRideData = endRideData;
						SupportRideIssuesFragment.this.getRideSummaryResponse = getRideSummaryResponse;
						setRideData();
						updateIssuesList((ArrayList<ShowPanelResponse.Item>) SupportRideIssuesFragment.this.getRideSummaryResponse.getMenu());
						linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
						recyclerViewSupportFaq.setVisibility(View.VISIBLE);
					}

					@Override
					public boolean onActionFailed(String message) {
						return true;
					}

					@Override
					public void onFailure() {
					}

					@Override
					public void onRetry(View view) {
						getRideSummaryAPI(activity, engagementId);
					}

					@Override
					public void onNoRetry(View view) {
						performBackPress();
					}
				}).getRideSummaryAPI();
	}


	public void performBackPress(){
		if(activity instanceof SupportActivity){
			((SupportActivity)activity).performBackPressed();
		} else if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).performBackPressed();
		}
	}

}
