package product.clicklabs.jugnoo.support.fragments;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;

@SuppressLint("ValidFragment")
public class SupportFAQItemsListFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private CardView cardViewRecycler;
	private RecyclerView recyclerViewItems;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private FragmentActivity activity;

	private int engagementId, orderId, productType;
	private ShowPanelResponse.Item item;
	private String phoneNumber, rideDate, orderDate, supportNumber;

	public SupportFAQItemsListFragment(){}


	@Override
    public void onStart() {
        super.onStart();
//        FlurryAgent.init(activity, Config.getFlurryKey());
//        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//        FlurryAgent.onEvent(SupportFAQItemsListFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
//        FlurryAgent.onEndSession(activity);
    }

	public SupportFAQItemsListFragment(int engagementId, String rideDate, ShowPanelResponse.Item item, String phoneNumber,
									   int orderId, String orderDate, String supportNumber, int productType){
		this.engagementId = engagementId;
		this.item = item;
		this.phoneNumber = phoneNumber;
		this.rideDate = rideDate;
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.supportNumber = supportNumber;
		this.productType = productType;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_items_list, container, false);

        activity = getActivity();
		setActivityTitle();

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		cardViewRecycler = (CardView)rootView.findViewById(R.id.cardViewRecycler);
		recyclerViewItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewItems);
		recyclerViewItems.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity));
		recyclerViewItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewItems.setHasFixedSize(false);

		supportFAQItemsAdapter = new SupportFAQItemsAdapter((ArrayList<ShowPanelResponse.Item>) item.getItems(),
				activity, R.layout.list_item_support_faq,
				new SupportFAQItemsAdapter.Callback() {
					@Override
					public void onClick(int position, ShowPanelResponse.Item item) {
						if(activity instanceof SupportActivity){
							new TransactionUtils().openItemInFragment(activity,
									((SupportActivity)activity).getContainer(),
									engagementId, rideDate, SupportFAQItemsListFragment.this.item.getText(), item, phoneNumber,
									orderId, orderDate, supportNumber, productType);

						} else if(activity instanceof RideTransactionsActivity){
							new TransactionUtils().openItemInFragment(activity,
									((RideTransactionsActivity)activity).getContainer(),
									engagementId, rideDate, SupportFAQItemsListFragment.this.item.getText(), item, phoneNumber,
									orderId, orderDate, supportNumber, productType);
						}
						FlurryEventLogger.eventGA(Constants.ISSUES, SupportFAQItemsListFragment.this.item.getText(), item.getText());
					}
				});
		recyclerViewItems.setAdapter(supportFAQItemsAdapter);

		if(item != null && item.getItems() != null && item.getItems().size() > 0){
			cardViewRecycler.setVisibility(View.VISIBLE);
		} else{
			cardViewRecycler.setVisibility(View.GONE);
		}

		return rootView;
	}

	private void setActivityTitle(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(item.getText());
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(item.getText());
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			setActivityTitle();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
	}

}
