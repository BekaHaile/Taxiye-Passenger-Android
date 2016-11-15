package com.sabkuchfresh.home;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sabkuchfresh.fragments.AddAddressMapFragment;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.fragments.FreshCartItemsFragment;
import com.sabkuchfresh.fragments.FreshCheckoutFragment;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.fragments.FreshOrderHistoryFragment;
import com.sabkuchfresh.fragments.FreshOrderSummaryFragment;
import com.sabkuchfresh.fragments.FreshPaymentFragment;
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.fragments.FreshSupportFragment;
import com.sabkuchfresh.fragments.VendorMenuFragment;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openCartFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshCartItemsFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
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
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FreshCheckoutFragment(),
							FreshCheckoutFragment.class.getName())
					.addToBackStack(FreshCheckoutFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openCheckoutMergedFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshCheckoutMergedFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FreshCheckoutMergedFragment(),
							FreshCheckoutMergedFragment.class.getName())
					.addToBackStack(FreshCheckoutMergedFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openVendorMenuFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, VendorMenuFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new VendorMenuFragment(),
							VendorMenuFragment.class.getName())
					.addToBackStack(VendorMenuFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openDeliveryAddressFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, DeliveryAddressesFragment.class.getName())) {
			FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new DeliveryAddressesFragment(),
							DeliveryAddressesFragment.class.getName())
					.addToBackStack(DeliveryAddressesFragment.class.getName());

			if(activity.getSupportFragmentManager().getBackStackEntryCount() > 0){
				fragmentTransaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
						.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
			}
			fragmentTransaction.commitAllowingStateLoss();
		}
	}

	public void openPaymentFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FreshPaymentFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
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
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FreshOrderHistoryFragment(),
							FreshOrderHistoryFragment.class.getName())
					.addToBackStack(FreshOrderHistoryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openOrderSummaryFragment(FragmentActivity activity, View container, HistoryResponse.Datum historyData) {
		if(!checkIfFragmentAdded(activity, FreshOrderSummaryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FreshOrderSummaryFragment(historyData),
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
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
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
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FreshSearchFragment(),
                            FreshSearchFragment.class.getName())
					.addToBackStack(FreshSearchFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

    public void openMapFragment(FragmentActivity activity, View container, Bundle bundle) {
		AddAddressMapFragment addAddressMapFragment = new AddAddressMapFragment();
		if(bundle != null) {
			addAddressMapFragment.setArguments(bundle);
		}
        if(!checkIfFragmentAdded(activity, AddAddressMapFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), addAddressMapFragment,
                            AddAddressMapFragment.class.getName())
                    .addToBackStack(AddAddressMapFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openAddToAddressFragment(FragmentActivity activity, View container, Bundle bundle) {
		AddToAddressBookFragment addToAddressBookFragment = new AddToAddressBookFragment();
		if(bundle != null) {
			addToAddressBookFragment.setArguments(bundle);
		}
        if(!checkIfFragmentAdded(activity, AddToAddressBookFragment.class.getName())) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), addToAddressBookFragment,
                            AddToAddressBookFragment.class.getName())
                    .addToBackStack(AddToAddressBookFragment.class.getName());
			if(activity.getSupportFragmentManager().getBackStackEntryCount() > 0){
				transaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
						.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
			}
			transaction.commitAllowingStateLoss();
        } else{
			addToAddressBookFragment = (AddToAddressBookFragment) activity.getSupportFragmentManager().findFragmentByTag(AddToAddressBookFragment.class.getName());
			if(addToAddressBookFragment != null && bundle != null) {
				activity.onBackPressed();
				addToAddressBookFragment.setNewArgumentsToUI(bundle);
			}
		}
    }

	public void openFeedback(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, FeedbackFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FeedbackFragment(),
							FeedbackFragment.class.getName())
					.addToBackStack(FeedbackFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

//	public void openFeedback() {
//		getSupportFragmentManager().beginTransaction()
//				.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//				.add(relativeLayoutContainer.getId(), new FeedbackFragment(),
//						FeedbackFragment.class.getName())
//				.addToBackStack(FeedbackFragment.class.getName())
//				.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
//						.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//				.commitAllowingStateLoss();
//	}

	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}

//.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
