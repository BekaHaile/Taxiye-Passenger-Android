package product.clicklabs.jugnoo.home.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomFareFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class SlidingBottomFragmentAdapter extends FragmentPagerAdapter {

	private Context context;
	public SlidingBottomFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
			case 0:
				return new SlidingBottomCashFragment();

			case 1:
				return new SlidingBottomFareFragment();

//			case 2:
//				return new SlidingBottomOffersFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
//		return 3;
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return context.getString(R.string.cash);
			case 1:
				return context.getString(R.string.fare);
			case 2:
				return context.getString(R.string.offers);
		}
		return null;
	}

}
