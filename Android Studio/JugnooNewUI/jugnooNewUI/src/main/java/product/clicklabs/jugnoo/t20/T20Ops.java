package product.clicklabs.jugnoo.t20;

import android.app.Activity;
import android.app.Dialog;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.t20.models.Schedule;

/**
 * Created by shankar on 3/5/16.
 */
public class T20Ops {

	private Dialog dialog;

	public T20Ops(){
		dialog = null;
	}

	public void openDialog(Activity activity, String engagementId, PassengerScreenMode passengerScreenMode) {
		if(Data.userData != null && Data.userData.getT20WCEnable() == 1 && Data.assignedDriverInfo != null){
			if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
					|| PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
				Schedule schedule = Data.assignedDriverInfo.getScheduleT20();
				if(schedule != null) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					dialog = new T20Dialog(activity, engagementId, passengerScreenMode, schedule).show();

				}
			}
		}
	}

}
