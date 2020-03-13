package product.clicklabs.jugnoo.promotion.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.promotion.adapters.LeaderboardItemsAdapter;
import product.clicklabs.jugnoo.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.retrofit.model.Ranklist;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


public class ReferralLeaderboardFragment extends Fragment implements  Constants {

	private LinearLayout linearLayoutRoot;

	private TextView textViewDaily, textViewWeekly;
	private RecyclerView recyclerViewLb;
	private LeaderboardItemsAdapter leaderboardItemsAdapter;
	private ArrayList<Ranklist> leaderboardItems;

	private View rootView;
    private ShareActivity activity;

	private LBLocationType lbLocationType;
	private LBTimeType lbTimeType;
	private ImageView imageViewBack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_referral_leaderboard, container, false);


        activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



		textViewDaily = (TextView)rootView.findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Fonts.mavenMedium(activity));
		textViewWeekly = (TextView)rootView.findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Fonts.mavenMedium(activity));
		imageViewBack = (ImageView)rootView.findViewById(R.id.imageViewBack);

		((TextView)rootView.findViewById(R.id.textViewRank)).setTypeface(Fonts.mavenMedium(activity));
		((TextView)rootView.findViewById(R.id.textViewName)).setTypeface(Fonts.mavenMedium(activity));
		((TextView)rootView.findViewById(R.id.textViewNoOfDownloads)).setTypeface(Fonts.mavenMedium(activity));

		recyclerViewLb = (RecyclerView)rootView.findViewById(R.id.recyclerViewLb);
		recyclerViewLb.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewLb.setItemAnimator(new DefaultItemAnimator());
		recyclerViewLb.setHasFixedSize(false);

		leaderboardItems = new ArrayList<>();
		leaderboardItemsAdapter = new LeaderboardItemsAdapter(leaderboardItems, activity, R.layout.list_item_lb_entry);
		recyclerViewLb.setAdapter(leaderboardItemsAdapter);


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

		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.performBackPressed();
			}
		});


		updateList(LBLocationType.GLOBAL, LBTimeType.DAILY);

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
					textViewDaily.setBackgroundResource(R.drawable.bg_white_theme_color_bb);
					textViewDaily.setTextColor(getResources().getColor(R.color.theme_color));
					textViewWeekly.setTextColor(getResources().getColor(R.color.text_color_light));
					textViewWeekly.setBackgroundResource(R.color.white);
				}
			}
			else if(LBTimeType.WEEKLY == lbTimeType){
				if(this.lbTimeType != lbTimeType) {
					textViewDaily.setBackgroundResource(R.color.white);
					textViewWeekly.setBackgroundResource(R.drawable.bg_white_theme_color_bb);
					textViewWeekly.setTextColor(getResources().getColor(R.color.theme_color));
					textViewDaily.setTextColor(getResources().getColor(R.color.text_color_light));
				}
			}

			if(LBLocationType.LOCAL == lbLocationType){
				if(this.lbLocationType != lbLocationType){
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
