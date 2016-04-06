package product.clicklabs.jugnoo.fresh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.fresh.fragments.FreshCategoryItemsFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class FreshCategoryFragmentsAdapter extends FragmentPagerAdapter {

	public FreshCategoryFragmentsAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return new FreshCategoryItemsFragment();
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
