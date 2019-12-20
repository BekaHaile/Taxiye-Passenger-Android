package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.RideOrderShortView;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Prefs;

@SuppressLint("ValidFragment")
public class SupportRideIssuesFragment extends Fragment implements  Constants, GAAction, GACategory {

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
	private int autosStatus, supportCategory, productType;
	private String orderDate;


	private static final String ITEM_ARRAY = "itemArray", END_RIDE_DATA = "endRideData", RIDE_CANCELLED = "rideCancelled",
		AUTO_STATUS = "autosStatus", DATUM = "datum";

	public static SupportRideIssuesFragment newInstance(int engagementId, int orderId, EndRideData endRideData, ArrayList<ShowPanelResponse.Item> items,
														boolean rideCancelled, int autosStatus, HistoryResponse.Datum datum, int supportCategory, int productType, String orderDate){
		SupportRideIssuesFragment fragment = new SupportRideIssuesFragment();

		Gson gson = new Gson();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
		bundle.putInt(Constants.KEY_ORDER_ID, orderId);
		if(endRideData != null) {
			bundle.putString(END_RIDE_DATA, gson.toJson(endRideData, EndRideData.class));
		}
		if(items != null){
			JsonElement element = gson.toJsonTree(items, new TypeToken<List<ShowPanelResponse.Item>>() {}.getType());
			bundle.putString(ITEM_ARRAY, element.getAsJsonArray().toString());
		}
		bundle.putBoolean(RIDE_CANCELLED, rideCancelled);
		bundle.putInt(AUTO_STATUS, autosStatus);
		if(datum != null){
			bundle.putString(DATUM, gson.toJson(datum, HistoryResponse.Datum.class));
		}
		bundle.putInt(Constants.KEY_SUPPORT_CATEGORY, supportCategory);
		bundle.putInt(Constants.KEY_PRODUCT_TYPE, productType);
		bundle.putString(Constants.KEY_ORDER_DATE, orderDate);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void parseArguments(){
		Gson gson = new Gson();
		this.engagementId = getArguments().getInt(Constants.KEY_ENGAGEMENT_ID);
		this.orderId = getArguments().getInt(Constants.KEY_ORDER_ID);
		String endRideDataStr = getArguments().getString(END_RIDE_DATA, Constants.EMPTY_JSON_OBJECT);
		if(!Constants.EMPTY_JSON_OBJECT.equalsIgnoreCase(endRideDataStr)){
			endRideData = gson.fromJson(endRideDataStr, EndRideData.class);
		}
		String itemsArrayStr = getArguments().getString(ITEM_ARRAY, Constants.EMPTY_JSON_ARRAY);
		if(!Constants.EMPTY_JSON_ARRAY.equalsIgnoreCase(itemsArrayStr)){
			items = gson.fromJson(itemsArrayStr, new TypeToken<List<ShowPanelResponse.Item>>(){}.getType());
		}
		rideCancelled = getArguments().getBoolean(RIDE_CANCELLED);
		autosStatus = getArguments().getInt(AUTO_STATUS);
		String datumStr = getArguments().getString(DATUM, Constants.EMPTY_JSON_OBJECT);
		if(!Constants.EMPTY_JSON_OBJECT.equalsIgnoreCase(datumStr)){
			datum = gson.fromJson(datumStr, HistoryResponse.Datum.class);
		}
		supportCategory = getArguments().getInt(Constants.KEY_SUPPORT_CATEGORY, -1);
		productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE, -1);
		orderDate = getArguments().getString(Constants.KEY_ORDER_DATE, "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_support_ride_issues, container, false);

		activity = getActivity();
		parseArguments();
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

		cardViewRecycler = (CardView) root.findViewById(R.id.cvRoot);
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
										-1, "", endRideData.getSupportNumber(), ProductType.AUTO.getOrdinal());

							} else if (activity instanceof RideTransactionsActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((RideTransactionsActivity) activity).getContainer(),
										Integer.parseInt(endRideData.engagementId),
										endRideData.getEngagementDate(),
										activity.getResources().getString(R.string.support_main_title), item, endRideData.getPhoneNumber(),
										-1, "", endRideData.getSupportNumber(), ProductType.AUTO.getOrdinal());
							}
						} else if(datum != null){
							if (activity instanceof SupportActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((SupportActivity) activity).getContainer(),
										-1, "",
										activity.getResources().getString(R.string.support_main_title), item, datum.getPhoneNo(),
										datum.getOrderId(), DateOperations.convertDateViaFormat(DateOperations
												.utcToLocalTZ(datum.getOrderTime())), datum.getSupportNumber(), datum.getProductType());

							} else if (activity instanceof RideTransactionsActivity) {
								new TransactionUtils().openItemInFragment(activity,
										((RideTransactionsActivity) activity).getContainer(),
										-1, "",
										activity.getResources().getString(R.string.support_main_title), item, datum.getPhoneNo(),
										datum.getOrderId(), DateOperations.convertDateViaFormat(DateOperations
												.utcToLocalTZ(datum.getOrderTime())), datum.getSupportNumber(), datum.getProductType());
							}
						} else if(supportCategory != -1) {
							if(activity instanceof FreshActivity){
								new TransactionUtils().openItemInFragment(activity,
										((FreshActivity) activity).getRelativeLayoutContainer(),
										-1, "",
										activity.getResources().getString(R.string.support_main_title), item, "",
										orderId, orderDate, Config.getSupportNumber(activity), productType);
							}
						}
						GAUtils.event(SIDE_MENU, GAAction.SELECT_AN_ISSUE, item.getText());
					}
				});
		recyclerViewSupportFaq.setAdapter(supportFAQItemsAdapter);

		try {
			if (endRideData != null || datum != null) {
				linearLayoutRideShortInfo.setVisibility(View.VISIBLE);
				cardViewRecycler.setVisibility(View.VISIBLE);
				setRideData();
				if(items == null && datum != null){
					items = MyApplication.getInstance().getDatabase2().getSupportDataItems(datum.getSupportCategory());
					if(!Prefs.with(activity).getString(Constants.KEY_SP_TRANSACTION_SUPPORT_PANEL_VERSION, "-1").equalsIgnoreCase(Data.userData.getInAppSupportPanelVersion())){
						linearLayoutRideShortInfo.setVisibility(View.GONE);
						cardViewRecycler.setVisibility(View.GONE);
						int supportCategory = datum.getSupportCategory();
						if(datum.getProductType() == ProductType.FRESH.getOrdinal()){
							getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.FRESH);
						} else if (datum.getProductType() == ProductType.MEALS.getOrdinal()){
							getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.MEALS);
						} else if (datum.getProductType() == ProductType.GROCERY.getOrdinal()){
							getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.GROCERY);
						} else if (datum.getProductType() == ProductType.MENUS.getOrdinal()){
							getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.MENUS);
						} else if (datum.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()){
							getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.DELIVERY_CUSTOMER);
						} else if (datum.getProductType() == ProductType.PAY.getOrdinal()){
							getRideSummaryAPI(activity, engagementId, -1, supportCategory, true, ProductType.PAY);
						}
					}
				}
				updateIssuesList(items);
			}
			else if(supportCategory != -1){
				linearLayoutRideShortInfo.setVisibility(View.GONE);
				items = MyApplication.getInstance().getDatabase2().getSupportDataItems(supportCategory);
				updateIssuesList(items);
				if(items.size() == 1){
					goForwardToSingleItem();
				} else if(items.size() == 0) {
					cardViewRecycler.setVisibility(View.GONE);
					getRideSummaryAPI(activity, -1, -1, supportCategory, false, new HomeUtil().getProductType(productType));
				}
			}
			else {
				linearLayoutRideShortInfo.setVisibility(View.GONE);
				cardViewRecycler.setVisibility(View.GONE);
				getRideSummaryAPI(activity, engagementId, orderId, autosStatus, false, ProductType.AUTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			if(items != null && items.size() == 1){
				cardViewRecycler.setVisibility(View.GONE);
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						getActivity().onBackPressed();
					}
				});
				return;
			}
			setActivityTitle();
			if(Data.isOrderCancelled && datum != null) {
				int orderId = datum.getOrderId();
				int supportCategory = datum.getSupportCategory();
				if(datum.getProductType() == ProductType.FRESH.getOrdinal()
						|| datum.getProductType() == ProductType.MEALS.getOrdinal()
						|| datum.getProductType() == ProductType.GROCERY.getOrdinal()
						|| datum.getProductType() == ProductType.GROCERY.getOrdinal()
						|| datum.getProductType() == ProductType.MENUS.getOrdinal()
						|| datum.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()
						|| datum.getProductType() == ProductType.PAY.getOrdinal()){
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
						new TransactionUtils().openOrderStatusFragment(activity,
								((SupportActivity) activity).getContainer(), datum.getOrderId(), datum.getProductType(), 0,
								false, false);

					}
				}
			});
		} else if(activity instanceof FreshActivity){
			((FreshActivity)activity).fragmentUISetup(this);
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
			linearLayoutRideShortInfo.setVisibility(endRideData != null ? View.VISIBLE : View.GONE);
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
		try {
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
							if(items != null && items.size() == 1){
								goForwardToSingleItem();
							}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void performBackPress() {
		if (activity instanceof SupportActivity) {
			((SupportActivity) activity).performBackPressed();
		} else if (activity instanceof RideTransactionsActivity) {
			((RideTransactionsActivity) activity).performBackPressed();
		} else if (activity instanceof FreshActivity){
			((FreshActivity)activity).performBackPressed(false);
		}
	}

	private void goForwardToSingleItem(){
		try {
			new TransactionUtils().openItemInFragment(activity,
					((FreshActivity) activity).getRelativeLayoutContainer(),
					-1, "",
					activity.getResources().getString(R.string.order_is_late), items.get(0), "",
					orderId, orderDate, Config.getSupportNumber(activity), productType);
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					activity.getSupportFragmentManager().beginTransaction().remove(SupportRideIssuesFragment.this).commit();
//				}
//			}, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
