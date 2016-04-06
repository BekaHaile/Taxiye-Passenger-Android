package product.clicklabs.jugnoo.fresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.fresh.adapters.FreshCategoryFragmentsAdapter;
import product.clicklabs.jugnoo.fresh.widgets.PagerSlidingTabStrip;
import product.clicklabs.jugnoo.utils.ASSL;


public class FreshFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private PagerSlidingTabStrip tabs;
	private ViewPager viewPager;
	private FreshCategoryFragmentsAdapter freshCategoryFragmentsAdapter;

	private View rootView;
    private FragmentActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh, container, false);


        activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
		freshCategoryFragmentsAdapter = new FreshCategoryFragmentsAdapter(getChildFragmentManager());
		viewPager.setAdapter(freshCategoryFragmentsAdapter);

		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		tabs.setTextColorResource(R.color.theme_color, R.color.grey_dark);
		tabs.setViewPager(viewPager);


		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
