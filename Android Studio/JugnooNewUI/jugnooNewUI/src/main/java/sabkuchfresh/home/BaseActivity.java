package sabkuchfresh.home;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    public static final String GENIE_SERVICE = "com.jugnoo.genie.GENIE_SERVICE";

    private static boolean activityFinished = false;
    public boolean newActivityStarted = false;

    @Override
    public void startActivity(Intent intent) {
		try {
			newActivityStarted = true;
			super.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    protected void onPause() {

        if (!newActivityStarted && !activityFinished) {
//            startService(new Intent(GENIE_SERVICE));
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        newActivityStarted = false;
        activityFinished = false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
//        stopService(new Intent(GENIE_SERVICE));
        super.onResume();
        //HomeUtil.checkForAccessTokenChange(this);
    }

    @Override
    public void finish() {
        activityFinished = true;
        super.finish();
    }
}
