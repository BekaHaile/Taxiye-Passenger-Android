package product.clicklabs.jugnoo.support.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.adapters.SupportFAQItemsAdapter;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.LinearLayoutManager;


public class SupportFAQItemsListFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout root;

	private RecyclerView recyclerViewItems;
	private SupportFAQItemsAdapter supportFAQItemsAdapter;

	private View rootView;
    private FragmentActivity activity;

	private int engagementId;
	private ShowPanelResponse.Item item;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(SupportFAQItemsListFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

	public SupportFAQItemsListFragment(int engagementId, ShowPanelResponse.Item item){
		this.engagementId = engagementId;
		this.item = item;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_support_items_list, container, false);

        activity = getActivity();

		activity.setTitle(item.getText());

		root = (LinearLayout) rootView.findViewById(R.id.root);
		try {
			if(root != null) {
				new ASSL(activity, root, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewItems);
		recyclerViewItems.setLayoutManager(new LinearLayoutManager(activity));
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
									engagementId, SupportFAQItemsListFragment.this.item.getText(), item);

						} else if(activity instanceof RideTransactionsActivity){
							new TransactionUtils().openItemInFragment(activity,
									((RideTransactionsActivity)activity).getContainer(),
									engagementId, SupportFAQItemsListFragment.this.item.getText(), item);
						}
					}
				});
		recyclerViewItems.setAdapter(supportFAQItemsAdapter);

		return rootView;
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(root);
        System.gc();
	}

}
