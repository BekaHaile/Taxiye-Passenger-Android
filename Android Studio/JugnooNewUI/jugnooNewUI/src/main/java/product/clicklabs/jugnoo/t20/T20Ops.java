package product.clicklabs.jugnoo.t20;

import android.app.Activity;
import android.app.Dialog;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 3/5/16.
 */
public class T20Ops {

	private Dialog dialog;

	public T20Ops(){
		dialog = null;
	}

	public void openDialog(Activity activity, String engagementId, PassengerScreenMode passengerScreenMode,
						   T20Dialog.T20DialogCallback callback) {
		if(Data.userData != null && Data.userData.getT20WCEnable() == 1 && Data.assignedDriverInfo != null){
			if(PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
					|| PassengerScreenMode.P_IN_RIDE == passengerScreenMode){
				Schedule schedule = Data.assignedDriverInfo.getScheduleT20();
				if(schedule != null) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					if((PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
							&& Prefs.with(activity).getInt(Constants.SP_T20_DIALOG_BEFORE_START_CROSSED, 0) == 0)
							||
							(PassengerScreenMode.P_IN_RIDE == passengerScreenMode
									&& Prefs.with(activity).getInt(Constants.SP_T20_DIALOG_IN_RIDE_CROSSED, 0) == 0)){
						dialog = new T20Dialog(activity, engagementId, passengerScreenMode, schedule, callback).show();
					} else{
						callback.notShown();
					}
				} else{
					callback.notShown();
				}
			} else if (PassengerScreenMode.P_DRIVER_ARRIVED != passengerScreenMode){
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				callback.notShown();
			}
		} else{
			callback.notShown();
		}

	}

}
