package product.clicklabs.jugnoo.support;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemFragment;
import product.clicklabs.jugnoo.support.fragments.SupportFAQItemsListFragment;
import product.clicklabs.jugnoo.support.fragments.SupportRideIssuesFragment;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openItemInFragment(FragmentActivity activity, View container,
								   int engagementId, String parentName, ShowPanelResponse.Item item){
		if(ActionType.GENERATE_FRESHDESK_TICKET.getOrdinal() == item.getActionType()
				|| ActionType.INAPP_CALL.getOrdinal() == item.getActionType()
				|| ActionType.TEXT_ONLY.getOrdinal() == item.getActionType()) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(),
							new SupportFAQItemFragment(engagementId, parentName, item), SupportFAQItemFragment.class.getName())
					.addToBackStack(SupportFAQItemFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
		else if(ActionType.NEXT_LEVEL.getOrdinal() == item.getActionType()) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(),
							new SupportFAQItemsListFragment(engagementId, item), SupportFAQItemsListFragment.class.getName())
					.addToBackStack(SupportFAQItemsListFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}


	public void openRideIssuesFragment(FragmentActivity activity, View container, int engagementId,
									   EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse) {
		activity.getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(container.getId(),
						new SupportRideIssuesFragment(engagementId, endRideData, getRideSummaryResponse),
						SupportRideIssuesFragment.class.getName())
				.addToBackStack(SupportRideIssuesFragment.class.getName())
				.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
						.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commitAllowingStateLoss();
	}

}
