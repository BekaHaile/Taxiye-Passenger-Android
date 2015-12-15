package product.clicklabs.jugnoo.sticky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Ankit on 12/9/15.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Prefs.with(context).save("remove_chat_head", "");
            Intent pushIntent = new Intent(context, WindowChangeDetectingService.class);
            context.startService(pushIntent);
        }
    }
}
