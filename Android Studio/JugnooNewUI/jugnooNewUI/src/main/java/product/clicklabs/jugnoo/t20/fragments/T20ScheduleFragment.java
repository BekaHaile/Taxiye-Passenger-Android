package product.clicklabs.jugnoo.t20.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFetchT20Schedule;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.t20.adapters.MatchScheduleAdapter;
import product.clicklabs.jugnoo.t20.models.MatchScheduleResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;

@SuppressLint("ValidFragment")
public class T20ScheduleFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = T20ScheduleFragment.class.getSimpleName();

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack, imageViewInfo;

	private RecyclerView recyclerViewSchedule;
	private MatchScheduleAdapter matchScheduleAdapter;
	private MatchScheduleResponse matchScheduleResponse;

	private View rootView;
    private FragmentActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(TAG + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_t20_schedule, container, false);

        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			if(relative != null) {
				new ASSL(activity, relative, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		imageViewInfo = (ImageView) rootView.findViewById(R.id.imageViewInfo);

		recyclerViewSchedule = (RecyclerView)rootView.findViewById(R.id.recyclerViewSchedule);
		recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewSchedule.setItemAnimator(new DefaultItemAnimator());
		recyclerViewSchedule.setHasFixedSize(false);

		matchScheduleAdapter = new MatchScheduleAdapter(null, activity);
		recyclerViewSchedule.setAdapter(matchScheduleAdapter);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){

					case R.id.imageViewBack:
						performBackPressed();
						break;

					case R.id.imageViewInfo:

						break;

				}
			}
		};
		imageViewBack.setOnClickListener(onClickListener);
		imageViewInfo.setOnClickListener(onClickListener);


		fetchT20Schedule();

		return rootView;
	}

	private void fetchT20Schedule(){
		new ApiFetchT20Schedule(activity, new ApiFetchT20Schedule.Callback() {
			@Override
			public void onSuccess(MatchScheduleResponse matchScheduleResponse) {
				T20ScheduleFragment.this.matchScheduleResponse = matchScheduleResponse;
				matchScheduleAdapter.setResults(T20ScheduleFragment.this.matchScheduleResponse);
			}

			@Override
			public void onFailure() {

			}

			@Override
			public void onRetry(View view) {

			}

			@Override
			public void onNoRetry(View view) {

			}
		}).fetchT20ScheduleAndUserSelections();
	}


	private void performBackPressed(){
		if(activity instanceof T20Activity){
			((T20Activity)activity).performBackPressed();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}
