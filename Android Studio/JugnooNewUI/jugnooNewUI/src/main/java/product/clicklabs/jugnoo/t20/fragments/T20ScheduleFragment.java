package product.clicklabs.jugnoo.t20.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiFetchT20Schedule;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.t20.adapters.MatchScheduleAdapter;
import product.clicklabs.jugnoo.t20.models.MatchScheduleResponse;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.t20.models.Selection;
import product.clicklabs.jugnoo.t20.models.T20DataType;
import product.clicklabs.jugnoo.t20.models.Team;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

@SuppressLint("ValidFragment")
public class T20ScheduleFragment extends Fragment implements Constants {

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
//        FlurryAgent.init(activity, Config.getFlurryKey());
//        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//		FlurryAgent.onEvent(TAG + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
//        FlurryAgent.onEndSession(activity);
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

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(activity));
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
						if(Data.userData != null && !"".equalsIgnoreCase(Data.userData.getT20WCInfoText())){
							DialogPopup.alertPopup(activity, "", Data.userData.getT20WCInfoText());
						}
						break;

				}
			}
		};
		imageViewBack.setOnClickListener(onClickListener);
		imageViewInfo.setOnClickListener(onClickListener);
		if(Data.userData != null && !"".equalsIgnoreCase(Data.userData.getT20WCInfoText())){
			imageViewInfo.setVisibility(View.VISIBLE);
		} else{
			imageViewInfo.setVisibility(View.GONE);
		}


		fetchT20Schedule();

		return rootView;
	}

	private void fetchT20Schedule(){
		String savedScheduleVersion = Prefs.with(activity).getString(Constants.KEY_SP_T20_WC_SCHEDULE_VERSION, "-1");
		if(savedScheduleVersion.equalsIgnoreCase(Data.userData.getT20WCScheduleVersion())){

			List<Schedule> schedules = MyApplication.getInstance().getDatabase2().getT20DataItems(T20DataType.SCHEDULE.getOrdinal());
			List<Selection> selections = MyApplication.getInstance().getDatabase2().getT20DataItems(T20DataType.SELECTION.getOrdinal());
			List<Team> teams = MyApplication.getInstance().getDatabase2().getT20DataItems(T20DataType.TEAM.getOrdinal());

			matchScheduleResponse = new MatchScheduleResponse(ApiResponseFlags.ACTION_COMPLETE.getOrdinal(),
					schedules, teams, selections, "");
			matchScheduleAdapter.setResults(T20ScheduleFragment.this.matchScheduleResponse);

		} else {
			new ApiFetchT20Schedule(activity, new ApiFetchT20Schedule.Callback() {
				@Override
				public void onSuccess(MatchScheduleResponse matchScheduleResponse) {
					T20ScheduleFragment.this.matchScheduleResponse = matchScheduleResponse;
					matchScheduleAdapter.setResults(T20ScheduleFragment.this.matchScheduleResponse);

					MyApplication.getInstance().getDatabase2()
							.insertUpdateT20Data(T20DataType.SCHEDULE.getOrdinal(), matchScheduleResponse.getSchedule());
					MyApplication.getInstance().getDatabase2()
							.insertUpdateT20Data(T20DataType.SELECTION.getOrdinal(), matchScheduleResponse.getSelections());
					MyApplication.getInstance().getDatabase2()
							.insertUpdateT20Data(T20DataType.TEAM.getOrdinal(), matchScheduleResponse.getTeams());

					Prefs.with(activity).save(Constants.KEY_SP_T20_WC_SCHEDULE_VERSION,
							Data.userData.getT20WCScheduleVersion());
				}

				@Override
				public void onFailure() {

				}

				@Override
				public void onRetry(View view) {
					fetchT20Schedule();
				}

				@Override
				public void onNoRetry(View view) {
					performBackPressed();
				}
			}).fetchT20ScheduleAndUserSelections();
		}
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
