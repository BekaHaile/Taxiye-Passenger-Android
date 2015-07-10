package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    public static final String GENIE_SERVICE = "com.jugnoo.genie.GENIE_SERVICE";

    private static boolean activityFinished = false;
    boolean newActivityStarted = false;

    @Override
    public void startActivity(Intent intent) {
        newActivityStarted = true;
        super.startActivity(intent);
    }

    @Override
    protected void onPause() {

        if (!newActivityStarted && !activityFinished) {
//            startService(new Intent(GENIE_SERVICE));

            Log.d("Service started", "");
        }
        newActivityStarted = false;

        activityFinished = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
//        stopService(new Intent(GENIE_SERVICE));
        Log.d("Service stopped", "");
        super.onResume();
    }

    @Override
    public void finish() {
        activityFinished = true;
        super.finish();
    }
}
