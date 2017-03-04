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

import com.google.gson.Gson;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;

@SuppressLint("ValidFragment")
public class SupportFAQItemsListFragment extends Fragment implements  Constants {

	private LinearLayout root;

	private CardView cardViewRecycler;
	private RecyclerView recyclerViewItems;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private FragmentActivity activity;

	private int engagementId, orderId, productType;
	private ShowPanelResponse.Item item;
	private String phoneNumber, rideDate, orderDate, supportNumber;


	@Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
		super.onStop();
    }

	private static final String ITEM = "item";

	public static SupportFAQItemsListFragment newInstance(int engagementId, String rideDate,
													 ShowPanelResponse.Item item, String phoneNumber,
													 int orderId, String orderDate, String supportNumber, int productType){
		SupportFAQItemsListFragment fragment = new SupportFAQItemsListFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
		bundle.putString(Constants.KEY_RIDE_DATE, rideDate);
		bundle.putString(ITEM, new Gson().toJson(item, ShowPanelResponse.Item.class));
		bundle.putString(Constants.KEY_PHONE_NO, phoneNumber);
		bundle.putInt(Constants.KEY_ORDER_ID, orderId);
		bundle.putString(Constants.KEY_ORDER_DATE, orderDate);
		bundle.putString(Constants.KEY_SUPPORT_NUMBER, supportNumber);
		bundle.putInt(Constants.KEY_PRODUCT_TYPE, productType);
		fragment.setArguments(bundle);

		return fragment;
	}

	private void parseArguments(){
		this.engagementId = getArguments().getInt(Constants.KEY_ENGAGEMENT_ID);
		this.rideDate = getArguments().getString(Constants.KEY_RIDE_DATE);
		this.item = new Gson().fromJson(getArguments().getString(ITEM), ShowPanelResponse.Item.class);
		this.phoneNumber = getArguments().getString(Constants.KEY_PHONE_NO);
		this.orderId = getArguments().getInt(Constants.KEY_ORDER_ID);
		this.orderDate = getArguments().getString(Constants.KEY_ORDER_DATE);
		this.supportNumber = getArguments().getString(Constants.KEY_SUPPORT_NUMBER);
		this.productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_items_list, container, false);

        activity = getActivity();
		setActivityTitle();
		parseArguments();

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		cardViewRecycler = (CardView)rootView.findViewById(R.id.cvRoot);
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
		try {
			if(activity instanceof RideTransactionsActivity){
				((RideTransactionsActivity)activity).setTitle(item.getText());
			} else if(activity instanceof SupportActivity){
				((SupportActivity)activity).setTitle(item.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
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
