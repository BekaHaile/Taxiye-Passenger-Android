package com.sabkuchfresh.home;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import com.sabkuchfresh.feed.ui.fragments.FeedAddPostFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedChangeCityFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedNotificationsFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedPostDetailFragment;
import com.sabkuchfresh.feed.ui.fragments.FeedSpotReservedSharingFragment;
import com.sabkuchfresh.fragments.AddToAddressBookFragment;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.fragments.DeliveryStoresFragment;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.fragments.FreshFragment;
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.fragments.MealAddonItemsFragment;
import com.sabkuchfresh.fragments.MealsBulkOrderFragment;
import com.sabkuchfresh.fragments.MenusItemCustomizeFragment;
import com.sabkuchfresh.fragments.MenusSearchFragment;
import com.sabkuchfresh.fragments.MerchantInfoFragment;
import com.sabkuchfresh.fragments.NewFeedbackFragment;
import com.sabkuchfresh.fragments.RestaurantAddReviewFragment;
import com.sabkuchfresh.fragments.RestaurantImageFragment;
import com.sabkuchfresh.fragments.RestaurantReviewsListFragment;
import com.sabkuchfresh.fragments.SuggestStoreFragment;
import com.sabkuchfresh.fragments.TabbedSearchFragment;
import com.sabkuchfresh.fragments.VendorMenuFragment;
import com.sabkuchfresh.pros.models.ProsCatalogueData;
import com.sabkuchfresh.pros.models.ProsProductData;
import com.sabkuchfresh.pros.ui.fragments.ProsCheckoutFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsOrderStatusFragment;
import com.sabkuchfresh.pros.ui.fragments.ProsProductsFragment;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.tutorials.NewUserCompleteProfileFragment;
import product.clicklabs.jugnoo.tutorials.NewUserReferralFragment;
import product.clicklabs.jugnoo.tutorials.NewUserWalletFragment;
import product.clicklabs.jugnoo.tutorials.SignUpTutorial;

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

    public void openDeliveryStoresFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, DeliveryStoresFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), DeliveryStoresFragment.newInstance(),
                            DeliveryStoresFragment.class.getName())
                    .addToBackStack(DeliveryStoresFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openCheckoutMergedFragment(FragmentActivity activity, View container) {
        if (!activity.isFinishing() && !checkIfFragmentAdded(activity, FreshCheckoutMergedFragment.class.getName())) {
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

    public void openVendorMenuFragment(FragmentActivity activity, View container, Bundle args) {
        if (!checkIfFragmentAdded(activity, VendorMenuFragment.class.getName())) {
            VendorMenuFragment vendorMenuFragment = new VendorMenuFragment();
            if(args!=null){
                vendorMenuFragment.setArguments(args);
            }
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(),vendorMenuFragment, VendorMenuFragment.class.getName())
                    .addToBackStack(VendorMenuFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openVendorMenuFragment(FragmentActivity activity, View container) {
        openVendorMenuFragment(activity,container,null);
    }

    public void openMerchantInfoFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, MerchantInfoFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), new MerchantInfoFragment(),
                            MerchantInfoFragment.class.getName())
                    .addToBackStack(MerchantInfoFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }


    public void openTabbedSearchFragment(FragmentActivity activity, View container,Bundle args) {
        if (!checkIfFragmentAdded(activity, TabbedSearchFragment.class.getName())) {

            TabbedSearchFragment tabbedSearchFragment = new TabbedSearchFragment();
            if(args!=null){
                tabbedSearchFragment.setArguments(args);
            }
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), tabbedSearchFragment,
                            TabbedSearchFragment.class.getName())
                    .addToBackStack(TabbedSearchFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }


    public void openRestaurantImageFragment(FragmentActivity activity, View container) {


        if (!checkIfFragmentAdded(activity, RestaurantImageFragment.class.getName())) {

            RestaurantImageFragment restaurantImageFragment = RestaurantImageFragment.newInstance();
           /* VendorMenuFragment vendorMenuFragment = (VendorMenuFragment) activity.getSupportFragmentManager().findFragmentByTag(VendorMenuFragment.class.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && vendorMenuFragment != null) {

                Transition changeTransform = TransitionInflater.from(activity).
                        inflateTransition(R.transition.change_image_transform);
                Transition explodeTransform = TransitionInflater.from(activity).
                        inflateTransition(android.R.transition.explode);


                // Setup exit transition on first fragment
                vendorMenuFragment.setSharedElementReturnTransition(changeTransform);
                vendorMenuFragment.setExitTransition(explodeTransform);

                // Setup enter transition on second fragment
                restaurantImageFragment.setSharedElementEnterTransition(changeTransform);
                restaurantImageFragment.setEnterTransition(explodeTransform);

                restaurantImageFragment.setAllowEnterTransitionOverlap(true);
                restaurantImageFragment.setAllowReturnTransitionOverlap(true);


            }*/
            FreshActivity freshActivity = (FreshActivity) activity;
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in,0)
                    .add(container.getId(), restaurantImageFragment,
                            RestaurantImageFragment.class.getName())
                    .addToBackStack(RestaurantImageFragment.class.getName())
                   //  .addSharedElement(freshActivity.ivCollapseRestImage, activity.getString(R.string.zoom_view))
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openDeliveryAddressFragment(FragmentActivity activity, View container, boolean isUnsavedAddressSelectionOn) {
        if (!checkIfFragmentAdded(activity, DeliveryAddressesFragment.class.getName())) {
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(),  DeliveryAddressesFragment.newInstance(isUnsavedAddressSelectionOn),
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

    public void openFeedback(FragmentActivity activity, View container,String clientId) {
        if (!activity.isFinishing() && !checkIfFragmentAdded(activity, FeedbackFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(),  FeedbackFragment.newInstance(clientId),
                            FeedbackFragment.class.getName())
                    .addToBackStack(FeedbackFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openMenuFeedback(FragmentActivity activity, View container) {
        if (!activity.isFinishing() && !checkIfFragmentAdded(activity, NewFeedbackFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
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

    public void openRestaurantReviewsListFragment(FragmentActivity activity, View container, MenusResponse.Vendor vendor) {
        if (!checkIfFragmentAdded(activity, RestaurantReviewsListFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in,0)
                    .add(container.getId(), RestaurantReviewsListFragment.newInstance(vendor),
                            RestaurantReviewsListFragment.class.getName())
                    .addToBackStack(RestaurantReviewsListFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }


    public void openRestaurantAddReviewFragment(FragmentActivity activity, View container, int restaurantId, Float rating) {
        if (!checkIfFragmentAdded(activity, RestaurantAddReviewFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in,0)
                    .add(container.getId(), RestaurantAddReviewFragment.newInstance(restaurantId,rating),
                            RestaurantAddReviewFragment.class.getName())
                    .addToBackStack(RestaurantAddReviewFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openSignUpTutorialFragment(FragmentActivity activity, View container, int numOfPages) {
        if (!checkIfFragmentAdded(activity, SignUpTutorial.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, 0)
                    .replace(container.getId(), SignUpTutorial.newInstance(numOfPages),
                            SignUpTutorial.class.getName())
                    .addToBackStack(SignUpTutorial.class.getName())
                    .commitAllowingStateLoss();
        }
    }

    public void openFeedAddPostFragment(FragmentActivity activity, View container, FeedDetail feedDetail) {
        if (!checkIfFragmentAdded(activity, FeedAddPostFragment.class.getName())) {


            FeedAddPostFragment feedAddPostFragment;
            if(feedDetail==null){
                feedAddPostFragment = new FeedAddPostFragment();
            }
            else{
                feedAddPostFragment = FeedAddPostFragment.newInstance(feedDetail);
            }



            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.bottom_in, 0)
                    .add(container.getId(), feedAddPostFragment,
                            FeedAddPostFragment.class.getName())
                    .addToBackStack(FeedAddPostFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openNewUserReferralFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, NewUserReferralFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, 0)
                    .replace(container.getId(), NewUserReferralFragment.newInstance(),
                            NewUserReferralFragment.class.getName())
                    .commitAllowingStateLoss();
        }
    }

    public void openNewUserCompleteProfileFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, NewUserCompleteProfileFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, 0)
                    .replace(container.getId(), NewUserCompleteProfileFragment.newInstance(),
                            NewUserCompleteProfileFragment.class.getName())
                    .commitAllowingStateLoss();
        }
    }

    public void openNewUserWalletFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, NewUserWalletFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, 0)
                    .replace(container.getId(), NewUserWalletFragment.newInstance(),
                            NewUserWalletFragment.class.getName())
                    .commitAllowingStateLoss();
        }
    }

    public void openFeedCommentsFragment(FragmentActivity activity, View container, FeedDetail feedDetail, int positionInOriginalList, boolean openKeyboardOnLoad, int postNotificationId) {
        if (!checkIfFragmentAdded(activity, FeedPostDetailFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0)
                    .add(container.getId(), FeedPostDetailFragment.newInstance(feedDetail,positionInOriginalList,openKeyboardOnLoad, postNotificationId),
                            FeedPostDetailFragment.class.getName())
                    .addToBackStack(FeedPostDetailFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openFeedSpotReservedSharingFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, FeedSpotReservedSharingFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0)
                    .add(container.getId(), new FeedSpotReservedSharingFragment(),
                            FeedSpotReservedSharingFragment.class.getName())
                    .addToBackStack(FeedSpotReservedSharingFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openFeedNotificationsFragment(FragmentActivity activity, View container) {
        if (!checkIfFragmentAdded(activity, FeedNotificationsFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0)
                    .add(container.getId(), new FeedNotificationsFragment(),
                            FeedNotificationsFragment.class.getName())
                    .addToBackStack(FeedNotificationsFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openChangeFeedCityFragment(FreshActivity activity, RelativeLayout relativeLayoutContainer) {
        if (!checkIfFragmentAdded(activity, FeedChangeCityFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0)
                    .add(relativeLayoutContainer.getId(), new FeedChangeCityFragment(),
                            FeedNotificationsFragment.class.getName())
                    .addToBackStack(FeedChangeCityFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void openMealsBulkOrderFragment(FreshActivity activity, RelativeLayout relativeLayoutContainer, String url) {
        if (!checkIfFragmentAdded(activity, MealsBulkOrderFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), MealsBulkOrderFragment.newInstance(url),
                            MealsBulkOrderFragment.class.getName())
                    .addToBackStack(MealsBulkOrderFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void addProsProductsFragment(FragmentActivity activity, View container, ProsCatalogueData.ProsCatalogueDatum prosCatalogueDatum) {
        if (!checkIfFragmentAdded(activity, ProsProductsFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(container.getId(), ProsProductsFragment.newInstance(prosCatalogueDatum),
                            ProsProductsFragment.class.getName())
                    .addToBackStack(ProsProductsFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void addProsCheckoutFragment(FragmentActivity activity, View container, ProsProductData.ProsProductDatum prosProductDatum) {
        if (!checkIfFragmentAdded(activity, ProsCheckoutFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(container.getId(), ProsCheckoutFragment.newInstance(prosProductDatum),
                            ProsCheckoutFragment.class.getName())
                    .addToBackStack(ProsCheckoutFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void addProsOrderStatusFragment(FragmentActivity activity, View container, int jobId, int productType) {
        if (!checkIfFragmentAdded(activity, ProsOrderStatusFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(container.getId(), ProsOrderStatusFragment.newInstance(jobId, productType),
                            ProsOrderStatusFragment.class.getName())
                    .addToBackStack(ProsOrderStatusFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

    public void addSuggestStoreFragment(FragmentActivity activity,View relativeLayoutContainer){
        if (!activity.isFinishing() && !checkIfFragmentAdded(activity, SuggestStoreFragment.class.getName())) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(relativeLayoutContainer.getId(), new SuggestStoreFragment(),
                            SuggestStoreFragment.class.getName())
                    .addToBackStack(SuggestStoreFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                    .commitAllowingStateLoss();
        }
    }

}

