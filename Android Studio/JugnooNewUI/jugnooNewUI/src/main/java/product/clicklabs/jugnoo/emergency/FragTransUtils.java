package product.clicklabs.jugnoo.emergency;

import androidx.fragment.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.fragments.AddEmergencyContactsFragment;
import product.clicklabs.jugnoo.emergency.fragments.EmergencyContactOperationsFragment;

/**
 * For transacting fragments in containers
 *
 * Created by shankar on 2/23/16.
 */
public class FragTransUtils {

	public void openAddEmergencyContactsFragment(FragmentActivity activity, View container){
		activity.getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(container.getId(), new AddEmergencyContactsFragment(), AddEmergencyContactsFragment.class.getName())
				.addToBackStack(AddEmergencyContactsFragment.class.getName())
				.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
						.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

	public void openEmergencyContactsOperationsFragment(FragmentActivity activity, View container, String engagementId,
														ContactsListAdapter.ListMode listMode){
		if(ContactsListAdapter.ListMode.SEND_RIDE_STATUS == listMode){
			activity.getSupportFragmentManager().beginTransaction()
					.add(container.getId(),
							EmergencyContactOperationsFragment.newInstance(engagementId, listMode),
							EmergencyContactOperationsFragment.class.getName())
					.addToBackStack(EmergencyContactOperationsFragment.class.getName())
					.commitAllowingStateLoss();
		} else if(ContactsListAdapter.ListMode.CALL_CONTACTS == listMode){
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(),
							EmergencyContactOperationsFragment.newInstance(engagementId, listMode),
							EmergencyContactOperationsFragment.class.getName())
					.addToBackStack(EmergencyContactOperationsFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}

	}


}
