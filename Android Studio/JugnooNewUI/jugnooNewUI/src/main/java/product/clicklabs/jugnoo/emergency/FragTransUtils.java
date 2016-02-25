package product.clicklabs.jugnoo.emergency;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.fragments.AddEmergencyContactsFragment;

/**
 * For transacting fragments in containers
 *
 * Created by shankar on 2/23/16.
 */
public class FragTransUtils {

	public void openAddEmergencyContactsFragment(FragmentActivity activity, View container){
		activity.getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
				.add(container.getId(), new AddEmergencyContactsFragment(), AddEmergencyContactsFragment.class.getName())
				.addToBackStack(AddEmergencyContactsFragment.class.getName())
				.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
						.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

}
