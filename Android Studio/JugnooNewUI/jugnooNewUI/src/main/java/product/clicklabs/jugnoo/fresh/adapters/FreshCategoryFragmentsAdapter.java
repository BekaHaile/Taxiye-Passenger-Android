package product.clicklabs.jugnoo.fresh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import product.clicklabs.jugnoo.fresh.fragments.FreshCategoryItemsFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class FreshCategoryFragmentsAdapter extends FragmentStatePagerAdapter {

	public FreshCategoryFragmentsAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return new FreshCategoryItemsFragment();
	}

	@Override
	public int getCount() {
		return 7;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Offers";
	}

}
