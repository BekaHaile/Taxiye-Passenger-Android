package product.clicklabs.jugnoo.fresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.fresh.FreshActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class FreshSupportFragment extends Fragment {

	private final String TAG = FreshSupportFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;
	private LinearLayout linearLayoutCallSupport;

	private View rootView;
    private FreshActivity activity;

	public FreshSupportFragment(){}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(FreshSupportFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_support, container, false);


        activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewInCase)).setTypeface(Fonts.mavenRegular(activity));
		((TextView)rootView.findViewById(R.id.textViewJustContact)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewCallSupport)).setTypeface(Fonts.mavenRegular(activity));

		linearLayoutCallSupport = (LinearLayout) rootView.findViewById(R.id.linearLayoutCallSupport);

		linearLayoutCallSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity.getProductsResponse() != null
						&& activity.getProductsResponse().getSupportContact() != null){
					Utils.openCallIntent(activity, activity.getProductsResponse().getSupportContact());
				} else{
					Utils.openCallIntent(activity, Config.getSupportNumber(activity));
				}

			}
		});

		FlurryEventLogger.event(activity, FlurryEventNames.FRESH_SUPPORT);

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden){
			activity.fragmentUISetup(this);
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
