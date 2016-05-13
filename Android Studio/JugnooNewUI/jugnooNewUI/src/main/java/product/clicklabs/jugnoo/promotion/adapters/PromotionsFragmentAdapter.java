package product.clicklabs.jugnoo.promotion.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.promotion.fragments.ReferralActivityFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralsFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralLeaderboardFragment;

/**
 * Created by shankar on 12/29/15.
 */
public class PromotionsFragmentAdapter extends FragmentPagerAdapter {

	public PromotionsFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new ReferralsFragment();
				break;

			case 1:
				fragment = new ReferralLeaderboardFragment();
				break;

			case 2:
				fragment = new ReferralActivityFragment();
				break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Earn";
			case 1:
				return "Leaderboard";
			case 2:
				return "Activity";
		}

		return null;
	}

}
