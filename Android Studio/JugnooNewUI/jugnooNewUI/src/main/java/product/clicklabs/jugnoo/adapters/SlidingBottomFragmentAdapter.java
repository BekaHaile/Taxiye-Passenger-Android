package product.clicklabs.jugnoo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.fragments.SlidingBottomCashFragment;
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
		switch(position){
			case 0:
				return new SlidingBottomCashFragment();

			case 1:
				return new SlidingBottomOffersFragment();

			case 2:
				return new SlidingBottomOffersFragment();
		}
		return null;
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
