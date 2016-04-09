package product.clicklabs.jugnoo.fresh;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.fragments.FreshCartItemsFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCheckoutFragment;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openCartFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshCartItemsFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshCartItemsFragment(),
							FreshCartItemsFragment.class.getName())
					.addToBackStack(FreshCartItemsFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openCheckoutFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshCheckoutFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshCheckoutFragment(),
							FreshCheckoutFragment.class.getName())
					.addToBackStack(FreshCheckoutFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}


	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}
