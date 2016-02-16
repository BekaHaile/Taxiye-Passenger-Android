package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shankar on 2/10/16.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            /* Setting the alarm here */
//			ScheduleAlarmForGCM scheduleAlarmForGCM = new ScheduleAlarmForGCM(context);
//			scheduleAlarmForGCM.start();
		}
	}
}
