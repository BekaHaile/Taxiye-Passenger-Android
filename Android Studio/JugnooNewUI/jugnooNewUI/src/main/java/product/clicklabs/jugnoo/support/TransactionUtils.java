package product.clicklabs.jugnoo.support;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sabkuchfresh.fragments.FreshOrderSummaryFragment;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.OrderStatusActivity;
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
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openItemInFragment(FragmentActivity activity, View container,
								   int engagementId, String rideDate, String parentName, ShowPanelResponse.Item item, String phoneNumber,
								   int orderId, String orderDate, String supportNumber){
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
				FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_RIDE_HISTORY);
				FlurryEventLogger.eventGA(Constants.ISSUES, "Customer Support", item.getText());
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
					activity.getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
							.add(container.getId(),
									new SupportFAQItemsListFragment(engagementId, rideDate, item, phoneNumber, orderId, orderDate, supportNumber),
									SupportFAQItemsListFragment.class.getName()+item.getSupportId())
							.addToBackStack(SupportFAQItemsListFragment.class.getName()+item.getSupportId())
							.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
									.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
							.commitAllowingStateLoss();
					FlurryEventLogger.event(FlurryEventNames.SUPPORT_NEXT_LEVEL_OPENED);
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
				activity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.add(container.getId(),
								new SupportFAQItemFragment(engagementId, rideDate, singleItemParentName, singleItemToOpen, phoneNumber, orderId, orderDate, supportNumber),
								SupportFAQItemFragment.class.getName())
						.addToBackStack(SupportFAQItemFragment.class.getName())
						.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
								.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commitAllowingStateLoss();
				FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_SUPPORT_ISSUES);
			}
		} else if(singleItemToOpen != null && singleItemParentName != null && singleItemToOpen.getActionType() == ActionType.NEXT_LEVEL.getOrdinal()){
			if(singleItemToOpen.getItems() != null && singleItemToOpen.getItems().size() == 1){
				singleItemParentName = singleItemToOpen.getText();
				singleItemToOpen = singleItemToOpen.getItems().get(0);
				openItemInFragment(activity, container, engagementId, rideDate, singleItemParentName, singleItemToOpen, phoneNumber, orderId, orderDate, supportNumber);
			}
		}
	}


	public void openRideIssuesFragment(FragmentActivity activity, View container, int engagementId, int orderId,
									   EndRideData endRideData, ArrayList<ShowPanelResponse.Item> items,
									   int fromBadFeedback, boolean rideCancelled, int autosStatus,
									   HistoryResponse.Datum datum) {
		if(!checkIfFragmentAdded(activity, SupportRideIssuesFragment.class.getName())) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			if(fromBadFeedback == 0){
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
			}
			fragmentTransaction.add(container.getId(),
					new SupportRideIssuesFragment(engagementId, orderId, endRideData, items, rideCancelled, autosStatus, datum),
					SupportRideIssuesFragment.class.getName())
					.addToBackStack(SupportRideIssuesFragment.class.getName());
			if(fromBadFeedback == 0 && fragmentManager.getBackStackEntryCount() > 0){
				fragmentTransaction.hide(fragmentManager.findFragmentByTag(fragmentManager
						.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName()));
			}
			fragmentTransaction.commitAllowingStateLoss();
			FlurryEventLogger.event(FlurryEventNames.SUPPORT_ISSUE_WITH_RECENT_RIDE);
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
							new RideSummaryFragment(engagementId, rideCancelled, autosStatus),
							RideSummaryFragment.class.getName())
					.addToBackStack(RideSummaryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openOrderSummaryFragment(FragmentActivity activity, View container, HistoryResponse.Datum datum) {
		if(!checkIfFragmentAdded(activity, FreshOrderSummaryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
					.add(container.getId(), new FreshOrderSummaryFragment(datum),
							FreshOrderSummaryFragment.class.getName())
					.addToBackStack(FreshOrderSummaryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openOrderStatusFragment(FragmentActivity activity, View container, int orderId) {
		if(!checkIfFragmentAdded(activity, OrderStatusActivity.class.getName())) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
			OrderStatusActivity orderStatusActivity = new OrderStatusActivity();
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.KEY_ORDER_ID, orderId);
			orderStatusActivity.setArguments(bundle);
			fragmentTransaction.add(container.getId(),
					orderStatusActivity,
					OrderStatusActivity.class.getName())
					.addToBackStack(OrderStatusActivity.class.getName());
			if(fragmentManager.getBackStackEntryCount() > 0){
				fragmentTransaction.hide(fragmentManager.findFragmentByTag(fragmentManager
						.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName()));
			}
			fragmentTransaction.commitAllowingStateLoss();
		}
	}

}
