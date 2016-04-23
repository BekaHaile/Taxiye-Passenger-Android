package product.clicklabs.jugnoo.fresh;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.fragments.FreshAddressFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCartItemsFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshCheckoutFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshOrderHistoryFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshOrderSummaryFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshPaymentFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshSearchFragment;
import product.clicklabs.jugnoo.fresh.fragments.FreshSupportFragment;

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

	public void openAddressFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshAddressFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshAddressFragment(),
							FreshAddressFragment.class.getName())
					.addToBackStack(FreshAddressFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openPaymentFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshPaymentFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshPaymentFragment(),
							FreshPaymentFragment.class.getName())
					.addToBackStack(FreshPaymentFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openOrderHistoryFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshOrderHistoryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshOrderHistoryFragment(),
							FreshOrderHistoryFragment.class.getName())
					.addToBackStack(FreshOrderHistoryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openOrderSummaryFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshOrderSummaryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshOrderSummaryFragment(),
							FreshOrderSummaryFragment.class.getName())
					.addToBackStack(FreshOrderSummaryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openSupportFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshSupportFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshSupportFragment(),
							FreshSupportFragment.class.getName())
					.addToBackStack(FreshSupportFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openSearchFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshSearchFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new FreshSearchFragment(),
							FreshSearchFragment.class.getName())
					.addToBackStack(FreshSearchFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}
