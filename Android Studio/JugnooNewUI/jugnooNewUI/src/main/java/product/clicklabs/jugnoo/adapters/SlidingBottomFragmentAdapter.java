package product.clicklabs.jugnoo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import product.clicklabs.jugnoo.fragments.SlidingBottomCashFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomFareFragment;
import product.clicklabs.jugnoo.fragments.SlidingBottomOffersFragment;
import product.clicklabs.jugnoo.home.fragments.BottomVehiclesFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class SlidingBottomFragmentAdapter extends FragmentStatePagerAdapter {

	private boolean showVehicles;
	public SlidingBottomFragmentAdapter(FragmentManager fm, boolean showVehicles) {
		super(fm);
		this.showVehicles = showVehicles;
	}

	public void setShowVehicles(boolean showVehicles){
		this.showVehicles = showVehicles;
		this.notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		if(showVehicles){
			if(position == 0){
				return new BottomVehiclesFragment();
			} else{
				position--;
			}
		}
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
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		if(showVehicles) {
			return 4;
		} else{
			return 3;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if(showVehicles) {
			if(position == 0){
				return "Vehicles";
			} else{
				position--;
			}
		}
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
