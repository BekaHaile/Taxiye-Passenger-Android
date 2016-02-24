package product.clicklabs.jugnoo.support;

import android.support.v4.app.FragmentActivity;
import android.view.View;

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
								   int engagementId, String parentName, ShowPanelResponse.Item item, String phoneNumber){
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
				FlurryEventLogger.event(FlurryEventNames.SUPPORT_RIDE_HISTORY_OPENED);
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
									new SupportFAQItemsListFragment(engagementId, item, phoneNumber),
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
								new SupportFAQItemFragment(engagementId, singleItemParentName, singleItemToOpen, phoneNumber),
								SupportFAQItemFragment.class.getName())
						.addToBackStack(SupportFAQItemFragment.class.getName())
						.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
								.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commitAllowingStateLoss();
				FlurryEventLogger.event(FlurryEventNames.SUPPORT_ISSUE_OPENED);
			}
		}
	}


	public void openRideIssuesFragment(FragmentActivity activity, View container, int engagementId,
									   EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse) {
		if(!checkIfFragmentAdded(activity, SupportRideIssuesFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(),
							new SupportRideIssuesFragment(engagementId, endRideData, getRideSummaryResponse),
							SupportRideIssuesFragment.class.getName())
					.addToBackStack(SupportRideIssuesFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
			FlurryEventLogger.event(FlurryEventNames.SUPPORT_ISSUE_WITH_RECENT_RIDE);
		}
	}



	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}
