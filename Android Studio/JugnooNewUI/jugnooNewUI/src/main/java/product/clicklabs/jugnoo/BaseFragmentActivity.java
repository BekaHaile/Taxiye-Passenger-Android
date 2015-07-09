package product.clicklabs.jugnoo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseFragmentActivity extends FragmentActivity {

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
            startService(new Intent(BaseActivity.GENIE_SERVICE));

            Log.d("Service started", "");
        }
        newActivityStarted = false;

        activityFinished = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        stopService(new Intent(BaseActivity.GENIE_SERVICE));
        Log.d("Service stopped", "");
        super.onResume();
    }

    @Override
    public void finish() {
        activityFinished = true;
        super.finish();
    }
}
