package product.clicklabs.jugnoo.sticky;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;

import java.util.logging.LogRecord;

import product.clicklabs.jugnoo.AccessTokenGenerator;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by socomo on 12/10/15.
 */
public class WindowChangeDetectingService extends AccessibilityService {

    String selectedPackageName = "";
    Handler handler;
    String packageName;

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
            handler = new Handler();
            Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(this);

                packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";
                String activityName = event.getClassName() != null ? event.getClassName().toString() : "";

                Log.d("packageName is", "---> " + packageName);
                Log.d("saved packageName is", "---> " + Prefs.with(this).getString("remove_chat_head", ""));
                if((!packageName.equalsIgnoreCase(Prefs.with(this).getString("remove_chat_head", "")))){
                    Prefs.with(this).save("remove_chat_head", "");
                    Log.v("other app is", "other app is " + Prefs.with(this).getString("remove_chat_head", ""));
                    selectedPackageName = packageName;
                }else{
                    Log.v("other app in else", "other app in else " + Prefs.with(this).getString("remove_chat_head", ""));
                }


                if(((packageName.equalsIgnoreCase("com.ubercab")) || (packageName.equalsIgnoreCase("com.olacabs.customer"))
                        || (packageName.equalsIgnoreCase("com.winit.merucab")) || (packageName.equalsIgnoreCase("com.autoncab.customer"))
                        || (packageName.equalsIgnoreCase("com.gcs.telerickshaw")) || (packageName.equalsIgnoreCase("com.tfs.consumer"))
                        || (packageName.equalsIgnoreCase("com.dimts.delhiautojunction")))
                        && (!"".equalsIgnoreCase(pair.first))
                        && (Prefs.with(this).getBoolean(SPLabels.JUGNOO_JEANIE_STATE, false) == true)
                        && (Prefs.with(this).getInt(SPLabels.SHOW_JUGNOO_JEANIE, 0) == 1)
                        && (!Prefs.with(this).getString("remove_chat_head", "").equalsIgnoreCase(packageName))
                   ){
                    Log.v("start service in accessibility", "start service in accessibility");
                    selectedPackageName = packageName;
                    Intent intent = new Intent(this, GenieService.class);
                    intent.putExtra("package_name", packageName);
                    startService(intent);
                    //startService(new Intent(this, GenieService.class));
                }else {
                    Log.v("stop service", "stop service");
                    stopService(new Intent(this, GenieService.class));
                    Prefs.with(this).save("remove_chat_head", packageName);
                    Log.v("stop service package Name", "stop service " + Prefs.with(this).getString("remove_chat_head", ""));

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Prefs.with(WindowChangeDetectingService.this).save("remove_chat_head", packageName);
                        }
                    }, 1000);
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
