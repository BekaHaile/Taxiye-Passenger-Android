package com.sabkuchfresh.home;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sabkuchfresh.fragments.AddAddressMapFragment;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.fragments.FreshFragment;
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.fragments.MealAddonItemsFragment;
import com.sabkuchfresh.fragments.MenusCheckoutMergedFragment;
import com.sabkuchfresh.fragments.MenusFilterCuisinesFragment;
import com.sabkuchfresh.fragments.MenusFilterFragment;
import com.sabkuchfresh.fragments.MenusItemCustomizeFragment;
import com.sabkuchfresh.fragments.MenusSearchFragment;
import com.sabkuchfresh.fragments.NewFeedbackFragment;
import com.sabkuchfresh.fragments.VendorMenuFragment;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

    public void addFreshFragment(FragmentActivity activity, View container, SuperCategoriesData.SuperCategory superCategory) {
        if (!checkIfFragmentAdded(activity, FreshFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), FreshFragment.newInstance(superCategory),
                            FreshFragment.class.getName())
                    .addToBackStack(FreshFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openCheckoutMergedFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, FreshCheckoutMergedFragment.class.getName())) {
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

    public void openMenusCheckoutMergedFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, MenusCheckoutMergedFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), new MenusCheckoutMergedFragment(),
                            MenusCheckoutMergedFragment.class.getName())
                    .addToBackStack(MenusCheckoutMergedFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openVendorMenuFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, VendorMenuFragment.class.getName())) {
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

    public void openMenusFilterFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, MenusFilterFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), new MenusFilterFragment(),
                            MenusFilterFragment.class.getName())
                    .addToBackStack(MenusFilterFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openMenusFilterCuisinesFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, MenusFilterCuisinesFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), new MenusFilterCuisinesFragment(),
                            MenusFilterCuisinesFragment.class.getName())
                    .addToBackStack(MenusFilterCuisinesFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openDeliveryAddressFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, DeliveryAddressesFragment.class.getName())) {
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), new DeliveryAddressesFragment(),
                            DeliveryAddressesFragment.class.getName())
                    .addToBackStack(DeliveryAddressesFragment.class.getName());

            if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                fragmentTransaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                        .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }


    public void openSearchFragment(FragmentActivity activity, View container, int superCategoryId, int cityId) {
        if (!checkIfFragmentAdded(activity, FreshSearchFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), FreshSearchFragment.newInstance(superCategoryId, cityId),
                            FreshSearchFragment.class.getName())
                    .addToBackStack(FreshSearchFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openMenusSearchFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, MenusSearchFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), new MenusSearchFragment(),
                            MenusSearchFragment.class.getName())
                    .addToBackStack(MenusSearchFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openMapFragment(FragmentActivity activity, View container, Bundle bundle) {
        AddAddressMapFragment addAddressMapFragment = new AddAddressMapFragment();
        if (bundle != null) {
            addAddressMapFragment.setArguments(bundle);
        }
        if (!checkIfFragmentAdded(activity, AddAddressMapFragment.class.getName())) {
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
        if (bundle != null) {
            addToAddressBookFragment.setArguments(bundle);
        }
        if (!checkIfFragmentAdded(activity, AddToAddressBookFragment.class.getName())) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), addToAddressBookFragment,
                            AddToAddressBookFragment.class.getName())
                    .addToBackStack(AddToAddressBookFragment.class.getName());
            if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                transaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                        .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
            }
            transaction.commitAllowingStateLoss();
        } else {
            addToAddressBookFragment = (AddToAddressBookFragment) activity.getSupportFragmentManager().findFragmentByTag(AddToAddressBookFragment.class.getName());
            if (addToAddressBookFragment != null && bundle != null) {
                activity.onBackPressed();
                addToAddressBookFragment.setNewArgumentsToUI(bundle);
            }
        }
    }

    public void openFeedback(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, FeedbackFragment.class.getName())) {
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

    public void openMenuFeedback(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, NewFeedbackFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,android.R.anim.fade_out)
                    .add(container.getId(), new NewFeedbackFragment(),
                            NewFeedbackFragment.class.getName())
                    .addToBackStack(NewFeedbackFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openMenusItemCustomizeFragment(FragmentActivity activity, View container, int categoryPos, int subCategoryPos, int itemPos) {
        if (!checkIfFragmentAdded(activity, MenusItemCustomizeFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), MenusItemCustomizeFragment.newInstance(categoryPos, subCategoryPos, itemPos),
                            MenusItemCustomizeFragment.class.getName())
                    .addToBackStack(MenusItemCustomizeFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }


    public boolean checkIfFragmentAdded(FragmentActivity activity, String tag) {
        return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
    }

    public void addMealAddonItemsFragment(FragmentActivity fragmentActivity, View container) {
        if (!checkIfFragmentAdded(fragmentActivity, MealAddonItemsFragment.class.getName())) {
            fragmentActivity.getSupportFragmentManager().beginTransaction()
                    .add(container.getId(), new MealAddonItemsFragment(),
                            MealAddonItemsFragment.class.getName())
                    .addToBackStack(MealAddonItemsFragment.class.getName())
                    .hide(fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentActivity.getSupportFragmentManager()
                            .getBackStackEntryAt(fragmentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

}

