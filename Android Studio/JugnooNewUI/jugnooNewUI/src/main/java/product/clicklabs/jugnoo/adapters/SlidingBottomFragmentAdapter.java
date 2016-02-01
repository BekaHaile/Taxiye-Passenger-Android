package product.clicklabs.jugnoo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import product.clicklabs.jugnoo.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomFareFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomOffersFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class SlidingBottomFragmentAdapter extends FragmentPagerAdapter {

	public SlidingBottomFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new SlidingBottomCashFragment();
				break;

			case 1:
				fragment = new SlidingBottomFareFragment();
				break;

			case 2:
				fragment = new SlidingBottomOffersFragment();
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
				return "Cash";
			case 1:
				return "Fare";
			case 2:
				return "Offers";
		}

		return null;
	}

}
