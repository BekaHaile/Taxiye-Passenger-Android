package product.clicklabs.jugnoo.support;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;

import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.OrderStatusFragment;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemFragment;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemsListFragment;
import product.clicklabs.jugnoo.support.fragments.SupportRideIssuesFragment;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openItemInFragment(FragmentActivity activity, View container,
								   int engagementId, String rideDate, String parentName, ShowPanelResponse.Item item, String phoneNumber,
								   int orderId, String orderDate, String supportNumber, int productType){
		ShowPanelResponse.Item singleItemToOpen = null;
		String singleItemParentName = null;
		if(ActionType.OPEN_RIDE_HISTORY.getOrdinal() == item.getActionType()){
			if(!checkIfFragmentAdded(activity, RideTransactionsFragment.class.getName())) {
				activity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.add(container.getId(),
								new RideTransactionsFragment(),
								RideTransactionsFragment.class.getName())
						.addToBackStack(RideTransactionsFragment.class.getName())
						.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
								.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commitAllowingStateLoss();
			}
		}
		else if(ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal() == item.getActionType()
				|| ActionType.INAPP_CALL.getOrdinal() == item.getActionType()
				|| ActionType.TEXT_ONLY.getOrdinal() == item.getActionType()) {
			singleItemToOpen = item;
			singleItemParentName = parentName;
		}
		else if(ActionType.NEXT_LEVEL.getOrdinal() == item.getActionType()) {
			if(item.getItems() != null && item.getItems().size() > 1){
				if(!checkIfFragmentAdded(activity, SupportFAQItemsListFragment.class.getName()+item.getSupportId())) {
					FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
					if(!(activity instanceof FreshActivity)){
						fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
					}
					fragmentTransaction.add(container.getId(),
									SupportFAQItemsListFragment.newInstance(engagementId, rideDate, item, phoneNumber, orderId, orderDate, supportNumber, productType),
									SupportFAQItemsListFragment.class.getName()+item.getSupportId())
							.addToBackStack(SupportFAQItemsListFragment.class.getName()+item.getSupportId())
							.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
									.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commitAllowingStateLoss();
				}
			} else if(item.getItems() != null && item.getItems().size() == 1){
				singleItemToOpen = item.getItems().get(0);
				singleItemParentName = item.getText();
			} else{
				singleItemToOpen = item;
				singleItemParentName = parentName;
			}
		}

		if(singleItemToOpen != null && singleItemParentName != null && (singleItemToOpen.getActionType() != ActionType.NEXT_LEVEL.getOrdinal()
				|| singleItemToOpen.getItems() == null)){
			if(!checkIfFragmentAdded(activity, SupportFAQItemFragment.class.getName())) {
				FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
				if(!(activity instanceof FreshActivity)){
					fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
				}
				fragmentTransaction.add(container.getId(),
								SupportFAQItemFragment.newInstance(engagementId, rideDate, singleItemParentName, singleItemToOpen, phoneNumber, orderId, orderDate, supportNumber, productType),
								SupportFAQItemFragment.class.getName())
						.addToBackStack(SupportFAQItemFragment.class.getName())
						.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
								.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commitAllowingStateLoss();
			}
		} else if(singleItemToOpen != null && singleItemParentName != null && singleItemToOpen.getActionType() == ActionType.NEXT_LEVEL.getOrdinal()){
			if(singleItemToOpen.getItems() != null && singleItemToOpen.getItems().size() == 1){
				singleItemParentName = singleItemToOpen.getText();
				singleItemToOpen = singleItemToOpen.getItems().get(0);
				openItemInFragment(activity, container, engagementId, rideDate, singleItemParentName, singleItemToOpen, phoneNumber, orderId, orderDate, supportNumber, productType);
			}
		}
	}


	public void openRideIssuesFragment(FragmentActivity activity, View container, int engagementId, int orderId,
									   EndRideData endRideData, ArrayList<ShowPanelResponse.Item> items,
									   int fromBadFeedback, boolean rideCancelled, int autosStatus,
									   HistoryResponse.Datum datum, int supportCategory, int productType, String orderDate) {
		if(!checkIfFragmentAdded(activity, SupportRideIssuesFragment.class.getName())) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			if(fromBadFeedback == 0){
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
			}
			fragmentTransaction.add(container.getId(),
					SupportRideIssuesFragment.newInstance(engagementId, orderId, endRideData, items, rideCancelled,
							autosStatus, datum, supportCategory, productType, orderDate),
					SupportRideIssuesFragment.class.getName())
					.addToBackStack(SupportRideIssuesFragment.class.getName());
			if(fromBadFeedback == 0 && fragmentManager.getBackStackEntryCount() > 0){
				fragmentTransaction.hide(fragmentManager.findFragmentByTag(fragmentManager
						.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName()));
			}
			fragmentTransaction.commitAllowingStateLoss();
		}
	}



	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

	public void openRideSummaryFragmentWithRideCancelledFlag(FragmentActivity activity, View container, int engagementId,
															 boolean rideCancelled, int autosStatus){
		if(!checkIfFragmentAdded(activity, RideSummaryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(),
							RideSummaryFragment.newInstance(engagementId, null, rideCancelled, autosStatus),
							RideSummaryFragment.class.getName())
					.addToBackStack(RideSummaryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openOrderStatusFragment(FragmentActivity activity, View container, int orderId, int productType, int openLiveTracking) {
		if(!checkIfFragmentAdded(activity, OrderStatusFragment.class.getName())) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
			OrderStatusFragment orderStatusFragment = new OrderStatusFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.KEY_ORDER_ID, orderId);
			bundle.putInt(Constants.KEY_PRODUCT_TYPE, productType);
			bundle.putInt(Constants.KEY_OPEN_LIVE_TRACKING, openLiveTracking);

			orderStatusFragment.setArguments(bundle);
			fragmentTransaction.add(container.getId(),
					orderStatusFragment,
					OrderStatusFragment.class.getName())
					.addToBackStack(OrderStatusFragment.class.getName());
			if(fragmentManager.getBackStackEntryCount() > 0){
				fragmentTransaction.hide(fragmentManager.findFragmentByTag(fragmentManager
						.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName()));
			}
			fragmentTransaction.commitAllowingStateLoss();
		}
	}

}
