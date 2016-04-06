package product.clicklabs.jugnoo.fresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.fresh.adapters.FreshCategoryItemsAdapter;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.utils.ASSL;


public class FreshCategoryItemsFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private RecyclerView recyclerViewCategoryItems;
	private FreshCategoryItemsAdapter freshCategoryItemsAdapter;

	private View rootView;
    private FragmentActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshCategoryItemsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_category_items, container, false);


        activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewCategoryItems = (RecyclerView)rootView.findViewById(R.id.recyclerViewCategoryItems);
		recyclerViewCategoryItems.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewCategoryItems.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCategoryItems.setHasFixedSize(false);

		ArrayList<SubItem> subItems = new ArrayList<>();
		subItems.add(new SubItem("Orange", "500gms", 20, 20, "http://i67.tinypic.com/2vumvzl.png"));

		freshCategoryItemsAdapter = new FreshCategoryItemsAdapter(activity, subItems);
		recyclerViewCategoryItems.setAdapter(freshCategoryItemsAdapter);



		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
