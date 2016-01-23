package product.clicklabs.jugnoo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class ShareActivityFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout linearLayoutRoot;

	private TextView textViewNumberOfDownloadsValue, textViewNumberOfFirstRidesValue,
			textViewMoneyEarnedValue, textViewDataEffective;

	private View rootView;
    private ShareActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(ShareActivityFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_activity, container, false);


        activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewNumberOfDownloads)).setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewNumberOfFirstRides)).setTypeface(Fonts.mavenLight(activity));
		((TextView)rootView.findViewById(R.id.textViewMoneyEarned)).setTypeface(Fonts.mavenLight(activity));

		textViewNumberOfDownloadsValue = (TextView)rootView.findViewById(R.id.textViewNumberOfDownloadsValue);
		textViewNumberOfDownloadsValue.setTypeface(Fonts.mavenLight(activity));
		textViewNumberOfFirstRidesValue = (TextView)rootView.findViewById(R.id.textViewNumberOfFirstRidesValue);
		textViewNumberOfFirstRidesValue.setTypeface(Fonts.mavenLight(activity));
		textViewMoneyEarnedValue = (TextView)rootView.findViewById(R.id.textViewMoneyEarnedValue);
		textViewMoneyEarnedValue.setTypeface(Fonts.mavenLight(activity));
		textViewDataEffective = (TextView)rootView.findViewById(R.id.textViewDataEffective);
		textViewDataEffective.setTypeface(Fonts.mavenLight(activity));



		try {
		} catch(Exception e){
			e.printStackTrace();
		}

		update();

		return rootView;
	}


	public void update(){
		try{
			if(activity.leaderboardActivityResponse != null){
				textViewNumberOfDownloadsValue.setText(""+activity.leaderboardActivityResponse.getNDownloads());
				textViewNumberOfFirstRidesValue.setText(""+activity.leaderboardActivityResponse.getNFirstRides());
				textViewMoneyEarnedValue.setText(""+activity.leaderboardActivityResponse.getNMoneyEarned());
				textViewDataEffective.setText(String.format(activity.getResources()
						.getString(R.string.data_effective_format), activity.leaderboardActivityResponse.getDate()));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}


}
