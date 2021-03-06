package product.clicklabs.jugnoo.home.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomFareFragment;
import product.clicklabs.jugnoo.home.fragments.SlidingBottomOffersFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class SlidingBottomFragmentAdapter extends FragmentPagerAdapter {

	private Context context;
	private boolean isOffersEnabled;
	public SlidingBottomFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
		isOffersEnabled = Data.isMenuTagEnabled(MenuInfoTags.OFFERS);
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
			case 0:
				return new SlidingBottomCashFragment();

			case 1:
				return new SlidingBottomFareFragment();

			case 2:
				return new SlidingBottomOffersFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return isOffersEnabled?3:2;
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
