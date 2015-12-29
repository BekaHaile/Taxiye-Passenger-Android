package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class ShareLeaderboardFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout linearLayoutRoot;

	private RelativeLayout relativeLayoutLocal, relativeLayoutDaily, relativeLayoutWeekly;
	private TextView textViewLocal, textViewDaily, textViewWeekly;

	private View rootView;
    private ShareActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(ShareLeaderboardFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_leaderboard, container, false);


        activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);

		relativeLayoutLocal = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutLocal);
		relativeLayoutDaily = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutDaily);
		relativeLayoutWeekly = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutWeekly);

		textViewLocal = (TextView)rootView.findViewById(R.id.textViewLocal);
		textViewLocal.setTypeface(Fonts.latoRegular(activity));
		textViewDaily = (TextView)rootView.findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Fonts.latoRegular(activity));
		textViewWeekly = (TextView)rootView.findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Fonts.latoRegular(activity));

		relativeLayoutLocal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		relativeLayoutDaily.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		relativeLayoutWeekly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		try {
		} catch(Exception e){
			e.printStackTrace();
		}


		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
