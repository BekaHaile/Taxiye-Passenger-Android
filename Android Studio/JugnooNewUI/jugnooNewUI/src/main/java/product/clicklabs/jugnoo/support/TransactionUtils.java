package product.clicklabs.jugnoo.support;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemFragment;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemsListFragment;
import product.clicklabs.jugnoo.support.fragments.SupportRideIssuesFragment;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openItemInFragment(FragmentActivity activity, View container,
								   int engagementId, String rideDate, String parentName, ShowPanelResponse.Item item, String phoneNumber){
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
									new SupportFAQItemsListFragment(engagementId, rideDate, item, phoneNumber),
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

		if(singleItemToOpen != null && singleItemParentName != null){
			if(!checkIfFragmentAdded(activity, SupportFAQItemFragment.class.getName())) {
				activity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.add(container.getId(),
								new SupportFAQItemFragment(engagementId, rideDate, singleItemParentName, singleItemToOpen, phoneNumber),
								SupportFAQItemFragment.class.getName())
						.addToBackStack(SupportFAQItemFragment.class.getName())
						.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
								.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commitAllowingStateLoss();
				FlurryEventLogger.event(activity, FlurryEventNames.CLICKS_ON_SUPPORT_ISSUES);
			}
		}
	}


	public void openRideIssuesFragment(FragmentActivity activity, View container, int engagementId,
									   EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse, int fromBadFeedback) {
		if(!checkIfFragmentAdded(activity, SupportRideIssuesFragment.class.getName())) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(container.getId(),
					new SupportRideIssuesFragment(engagementId, endRideData, getRideSummaryResponse),
					SupportRideIssuesFragment.class.getName())
					.addToBackStack(SupportRideIssuesFragment.class.getName());
			if(fromBadFeedback == 0){
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
				fragmentTransaction.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
						.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
			}
			fragmentTransaction.commitAllowingStateLoss();
			FlurryEventLogger.event(FlurryEventNames.SUPPORT_ISSUE_WITH_RECENT_RIDE);
		}
	}



	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}
