package product.clicklabs.jugnoo.sticky;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by socomo on 12/10/15.
 */
public class WindowChangeDetectingService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;// | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;// |  AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";
            String activityName = event.getClassName() != null ? event.getClassName().toString() : "";
            Log.d("packageName is","---> "+packageName);
            if((packageName.equalsIgnoreCase("com.ubercab")) || (packageName.equalsIgnoreCase("com.olacabs.customer"))){
                startService(new Intent(this, GenieService.class));
            }else {
                stopService(new Intent(this, GenieService.class));
            }
            // do something with the information
            // ...
        }
        else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        {
            Log.d("this", "event: " + event.toString());
        }
    }

    @Override
    public void onInterrupt() {}
}
