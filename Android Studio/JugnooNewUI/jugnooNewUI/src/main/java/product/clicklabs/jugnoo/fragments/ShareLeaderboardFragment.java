package product.clicklabs.jugnoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.ShareActivity;
import product.clicklabs.jugnoo.adapters.LeaderboardItemsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.retrofit.model.Ranklist;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class ShareLeaderboardFragment extends Fragment implements FlurryEventNames, Constants {

	private LinearLayout linearLayoutRoot;

	private Button buttonLocal, buttonGlobal;
	private TextView textViewDaily, textViewWeekly;
	private RecyclerView recyclerViewLb;
	private LeaderboardItemsAdapter leaderboardItemsAdapter;
	private ArrayList<Ranklist> leaderboardItems;

	private View rootView;
    private ShareActivity activity;

	private LBLocationType lbLocationType;
	private LBTimeType lbTimeType;

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
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonLocal = (Button)rootView.findViewById(R.id.buttonLocal);
		buttonLocal.setTypeface(Fonts.latoRegular(activity));
		buttonGlobal = (Button)rootView.findViewById(R.id.buttonGlobal);
		buttonGlobal.setTypeface(Fonts.latoRegular(activity));

		textViewDaily = (TextView)rootView.findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Fonts.latoRegular(activity));
		textViewWeekly = (TextView)rootView.findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Fonts.latoRegular(activity));

		recyclerViewLb = (RecyclerView)rootView.findViewById(R.id.recyclerViewLb);
		recyclerViewLb.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewLb.setItemAnimator(new DefaultItemAnimator());
		recyclerViewLb.setHasFixedSize(false);

		leaderboardItems = new ArrayList<>();
		leaderboardItemsAdapter = new LeaderboardItemsAdapter(leaderboardItems, activity, R.layout.list_item_lb_entry);
		recyclerViewLb.setAdapter(leaderboardItemsAdapter);

		buttonLocal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(LBLocationType.LOCAL, lbTimeType);
			}
		});

		buttonGlobal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(LBLocationType.GLOBAL, lbTimeType);
			}
		});

		textViewDaily.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(lbLocationType, LBTimeType.DAILY);
			}
		});

		textViewWeekly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateList(lbLocationType, LBTimeType.WEEKLY);
			}
		});


		try {
		} catch(Exception e){
			e.printStackTrace();
		}

		updateList(LBLocationType.LOCAL, LBTimeType.DAILY);

		return rootView;
	}

	public void update(){
		updateList(lbLocationType, lbTimeType);
	}


	private void updateList(LBLocationType lbLocationType, LBTimeType lbTimeType){
		try {
			leaderboardItems.clear();

			if(LBTimeType.DAILY == lbTimeType){
				if(this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.drawable.background_yellow);
					textViewDaily.setTextColor(getResources().getColor(R.color.white));
					textViewWeekly.setBackgroundResource(R.drawable.background_white_bordered_yellow_selector);
					textViewWeekly.setTextColor(getResources().getColorStateList(R.color.text_color_grey_dark_white_selector));
				}
			}
			else if(LBTimeType.WEEKLY == lbTimeType){
				if(this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.drawable.background_white_bordered_yellow_selector);
					textViewDaily.setTextColor(getResources().getColorStateList(R.color.text_color_grey_dark_white_selector));
					textViewWeekly.setBackgroundResource(R.drawable.background_yellow);
					textViewWeekly.setTextColor(getResources().getColor(R.color.white));
				}
			}

			if(LBLocationType.LOCAL == lbLocationType){
				if(this.lbLocationType != lbLocationType){
					buttonLocal.setBackgroundResource(R.drawable.button_yellow_normal);
					buttonLocal.setTextColor(getResources().getColor(R.color.white));
					buttonGlobal.setBackgroundResource(R.drawable.background_white_bordered_yellow_rounded_selector);
					buttonGlobal.setTextColor(getResources().getColorStateList(R.color.text_color_grey_dark_white_selector));
				}
				if(LBTimeType.DAILY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getLocal().getDaily().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getLocal().getDaily().getUserinfo();
					fillUserInfo(userInfo);
				}
				else if(LBTimeType.WEEKLY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getLocal().getWeekly().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getLocal().getWeekly().getUserinfo();
					fillUserInfo(userInfo);
				}
			}
			else if(LBLocationType.GLOBAL == lbLocationType){
				if(this.lbLocationType != lbLocationType) {
					buttonLocal.setBackgroundResource(R.drawable.background_white_bordered_yellow_rounded_selector);
					buttonLocal.setTextColor(getResources().getColorStateList(R.color.text_color_grey_dark_white_selector));
					buttonGlobal.setBackgroundResource(R.drawable.button_yellow_normal);
					buttonGlobal.setTextColor(getResources().getColor(R.color.white));
				}
				if(LBTimeType.DAILY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getGlobal().getDaily().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getGlobal().getDaily().getUserinfo();
					fillUserInfo(userInfo);
				}
				else if(LBTimeType.WEEKLY == lbTimeType){
					leaderboardItems.addAll(activity.leaderboardResponse.getGlobal().getWeekly().getRanklist());
					LeaderboardResponse.Userinfo userInfo = activity.leaderboardResponse.getGlobal().getWeekly().getUserinfo();
					fillUserInfo(userInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			this.lbLocationType = lbLocationType;
			this.lbTimeType = lbTimeType;

			leaderboardItemsAdapter.notifyDataSetChanged();
		}
	}

	private void fillUserInfo(LeaderboardResponse.Userinfo userInfo){
		if(userInfo != null && userInfo.getRank() != null){
			if(userInfo.getRank() > 5){
				leaderboardItems.add(new Ranklist(userInfo.getRank(),
						userInfo.getDownloads(), Data.userData.userName, true));
			}
			else if(userInfo.getRank() < 0){
				leaderboardItems.add(new Ranklist(userInfo.getRank(),
						userInfo.getDownloads(), Data.userData.userName, true));
			}
			else {
				for (int i=0; i<leaderboardItems.size(); i++) {
					Ranklist ranklist = leaderboardItems.get(i);
					if (ranklist.getRank().equals(userInfo.getRank())) {
						leaderboardItems.get(i).setIsUser(true);
						break;
					}
				}
			}
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}

	enum LBLocationType{
		LOCAL, GLOBAL
	}

	enum LBTimeType{
		DAILY, WEEKLY
	}


}
