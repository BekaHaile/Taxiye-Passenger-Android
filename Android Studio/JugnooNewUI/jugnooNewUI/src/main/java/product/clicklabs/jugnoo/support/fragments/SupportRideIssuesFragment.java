package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Database2;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.RideOrderShortView;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Prefs;

@SuppressLint("ValidFragment")
public class SupportRideIssuesFragment extends Fragment implements FlurryEventNames, Constants, FirebaseEvents {

	private LinearLayout root;

	private LinearLayout linearLayoutRideShortInfo;
	private RideOrderShortView rideOrderShortView;

	private CardView cardViewRecycler;
	private RecyclerView recyclerViewSupportFaq;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
	private FragmentActivity activity;

	private int engagementId, orderId;
	private EndRideData endRideData;
	private HistoryResponse.Datum datum;
	private ArrayList<ShowPanelResponse.Item> items;
	private boolean rideCancelled;
	private int autosStatus;

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

	public SupportRideIssuesFragment(){}

	public SupportRideIssuesFragment(int engagementId, int orderId, EndRideData endRideData, ArrayList<ShowPanelResponse.Item> items,
									 boolean rideCancelled, int autosStatus, HistoryResponse.Datum datum) {
		this.engagementId = engagementId;
		this.orderId = orderId;
		this.endRideData = endRideData;
		this.items = items;
		this.rideCancelled = rideCancelled;
		this.autosStatus = autosStatus;
		this.datum = datum;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_support_ride_issues, container, false);

		activity = getActivity();

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if (root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setActivityTitle();


		linearLayoutRideShortInfo = (LinearLayout) rootView.findViewById(R.id.linearLayoutRideShortInfo);
		rideOrderShortView = new RideOrderShortView(activity, rootView, false);

		cardViewRecycler = (CardView) root.findViewById(R.id.cardViewRecycler);
		recyclerViewSupportFaq = (RecyclerView) rootView.findViewById(R.id.recyclerViewSupportFaq);
		recyclerViewSupportFaq.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
		recyclerViewSupportFaq.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSupportFaq.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter(null, activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						if(endRideData != null) {
							if (activity instanceof SupportActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((SupportActivity) activity).getContainer(),
										Integer.parseInt(endRideData.engagementId),
										endRideData.getEngagementDate(),
										activity.getResources().getString(R.string.support_main_title), item, endRideData.getPhoneNumber(),
										-1, "", endRideData.getSupportNumber());

							} else if (activity instanceof RideTransactionsActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((RideTransactionsActivity) activity).getContainer(),
										Integer.parseInt(endRideData.engagementId),
										endRideData.getEngagementDate(),
										activity.getResources().getString(R.string.support_main_title), item, endRideData.getPhoneNumber(),
										-1, "", endRideData.getSupportNumber());
							}
						} else if(datum != null){
							if (activity instanceof SupportActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((SupportActivity) activity).getContainer(),
										-1, "",
										activity.getResources().getString(R.string.support_main_title), item, datum.getPhoneNo(),
										datum.getOrderId(), DateOperations.convertDateViaFormat(DateOperations
												.utcToLocalTZ(datum.getOrderTime())), datum.getSupportNumber());

							} else if (activity instanceof RideTransactionsActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((RideTransactionsActivity) activity).getContainer(),
										-1, "",
										activity.getResources().getString(R.string.support_main_title), item, datum.getPhoneNo(),
										datum.getOrderId(), DateOperations.convertDateViaFormat(DateOperations
												.utcToLocalTZ(datum.getOrderTime())), datum.getSupportNumber());
							}
						}
						Bundle bundle = new Bundle();
						String label = item.getText().replaceAll("\\W", "_");
						MyApplication.getInstance().logEvent(FirebaseEvents.ISSUES + "_" + FirebaseEvents.ISSUE_WITH_RECENT_RIDE + "_" + label, bundle);
						FlurryEventLogger.eventGA(Constants.ISSUES, "Select An Issue", item.getText());
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		if (endRideData != null || datum != null) {
			linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
			cardViewRecycler.setVisibility(View.VISIBLE);
			setRideData();
			if(items == null && datum != null){
				items = Database2.getInstance(activity).getSupportDataItems(datum.getSupportCategory());
				if(!Prefs.with(activity).getString(Constants.KEY_SP_TRANSACTION_SUPPORT_PANEL_VERSION, "-1").equalsIgnoreCase(Data.userData.getInAppSupportPanelVersion())){
					linearLayoutRideShortInfo.setVisibility(View.GONE);
					cardViewRecycler.setVisibility(View.GONE);
					int supportCategory = datum.getSupportCategory();
					if(datum.getProductType() == ProductType.FRESH.getOrdinal()){
						getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.FRESH);
					} else if (datum.getProductType() == ProductType.MEALS.getOrdinal()){
						getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.MEALS);
					}
				}
			}
			updateIssuesList(items);
		} else {
			linearLayoutRideShortInfo.setVisibility(View.GONE);
			cardViewRecycler.setVisibility(View.GONE);
			getRideSummaryAPI(activity, engagementId, orderId, autosStatus, false, ProductType.AUTO);
		}

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			setActivityTitle();
			if(Data.isOrderCancelled) {
				int orderId = datum.getOrderId();
				int supportCategory = datum.getSupportCategory();
				if(datum.getProductType() == ProductType.FRESH.getOrdinal()){
					getRideSummaryAPI(activity, engagementId, orderId, supportCategory, false, ProductType.NOT_SURE);
				} else if (datum.getProductType() == ProductType.MEALS.getOrdinal()){
					getRideSummaryAPI(activity, engagementId, orderId, supportCategory, false, ProductType.NOT_SURE);
				}
				Data.isSupportRideIssueUpdated = true;
				Data.isOrderCancelled = false;
			}
		}
	}

	private void setActivityTitle() {
		if (activity instanceof RideTransactionsActivity) {
			((RideTransactionsActivity) activity).setTitle(activity.getResources().getString(R.string.support_ride_issues_title));
		} else if (activity instanceof SupportActivity) {
			((SupportActivity) activity).setTitle(activity.getResources().getString(R.string.support_ride_issues_title));
			((SupportActivity) activity).setImageViewInvoiceVisibility(View.VISIBLE);
			((SupportActivity) activity).setImageViewInvoiceOnCLickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (activity instanceof SupportActivity && endRideData != null) {
						((SupportActivity) activity).openRideSummaryFragment(endRideData, rideCancelled, autosStatus);
					}
					else if(activity instanceof SupportActivity && datum != null){
						new TransactionUtils().openOrderSummaryFragment(activity,
								((SupportActivity) activity).getContainer(), datum);
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


	private void updateIssuesList(ArrayList<ShowPanelResponse.Item> items) {
		if(items != null && items.size() > 0){
			cardViewRecycler.setVisibility(View.VISIBLE);
		} else{
			cardViewRecycler.setVisibility(View.GONE);
		}
		supportFAQItemsAdapter.setResults(items);
	}

	private void setRideData() {
		try {
			try {
				rideOrderShortView.updateData(endRideData, datum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getRideSummaryAPI(final Activity activity, final int engagementId, final int orderId, final int supportCategory,
								  final boolean fromOrderHistory, final ProductType productType) {
		new ApiGetRideSummary(activity, Data.userData.accessToken, engagementId, orderId, Data.autoData.getFareStructure().getFixedFare(),
				new ApiGetRideSummary.Callback() {
					@Override
					public void onSuccess(EndRideData endRideData, HistoryResponse.Datum datum, ArrayList<ShowPanelResponse.Item> items) {
						if(endRideData != null && endRideData.driverName != null) {
							SupportRideIssuesFragment.this.endRideData = endRideData;
						}
						if(datum != null && datum.getOrderId() != null) {
							SupportRideIssuesFragment.this.datum = datum;
						}
						if(items != null) {
							SupportRideIssuesFragment.this.items = items;
						}
						setRideData();
						updateIssuesList(items);
						linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
						cardViewRecycler.setVisibility(View.VISIBLE);
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
						getRideSummaryAPI(activity, engagementId, orderId, supportCategory, fromOrderHistory, productType);
					}

					@Override
					public void onNoRetry(View view) {
						performBackPress();
					}
				}).getRideSummaryAPI(supportCategory, productType, fromOrderHistory);
	}


	public void performBackPress() {
		if (activity instanceof SupportActivity) {
			((SupportActivity) activity).performBackPressed();
		} else if (activity instanceof RideTransactionsActivity) {
			((RideTransactionsActivity) activity).performBackPressed();
		}
	}

}
