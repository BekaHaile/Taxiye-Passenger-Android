package product.clicklabs.jugnoo.promotion.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.fragments.ReferralActivityFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralLeaderboardFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralsFragment;

/**
 * Created by shankar on 12/29/15.
 */
public class PromotionsFragmentAdapter extends FragmentPagerAdapter {

	private Context context;

	public PromotionsFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
			case 0:
				return new ReferralsFragment();

			case 1:
				if(Data.userData != null && Data.userData.getReferralLeaderboardEnabled() == 1) {
					return new ReferralLeaderboardFragment();
				}

			case 2:
				if(Data.userData != null && Data.userData.getReferralActivityEnabled() == 1) {
					return new ReferralActivityFragment();
				}
		}

		return null;
	}

	@Override
	public int getCount() {
		int count = 1;
		if(Data.userData != null && Data.userData.getReferralLeaderboardEnabled() == 1){
			count = count + 1;
		}
		if(Data.userData != null && Data.userData.getReferralActivityEnabled() == 1){
			count = count + 1;
		}
		return count;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return context.getResources().getString(R.string.referrals);
			case 1:
				if(Data.userData != null && Data.userData.getReferralLeaderboardEnabled() == 1) {
					return context.getResources().getString(R.string.leaderboard);
				}
			case 2:
				if(Data.userData != null && Data.userData.getReferralActivityEnabled() == 1) {
					return context.getResources().getString(R.string.activity);
				}
		}
		return "";
	}

}
