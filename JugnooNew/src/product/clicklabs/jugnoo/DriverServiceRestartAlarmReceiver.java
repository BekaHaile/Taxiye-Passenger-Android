package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DriverServiceRestartAlarmReceiver extends BroadcastReceiver {

	private static final String START_SERVICE = "product.clicklabs.jugnoo.START_SERVICE";
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (START_SERVICE.equals(action)) {
			new DriverServiceOperations().startDriverService(context);
		}
	}
	
}