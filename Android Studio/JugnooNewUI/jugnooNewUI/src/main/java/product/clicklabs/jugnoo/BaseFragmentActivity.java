package product.clicklabs.jugnoo;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseFragmentActivity extends FragmentActivity {

    private static boolean activityFinished = false;
    boolean newActivityStarted = false;

    @Override
    public void startActivity(Intent intent) {
		try {
			newActivityStarted = true;
			super.startActivity(intent);
		} catch(Exception e){
			e.printStackTrace();
			try {
				if(intent.getData().equals(Uri.parse("market://details?id=com.google.android.gms"))) {
					Intent intent1 = new Intent(Intent.ACTION_VIEW);
					intent1.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
					ComponentName componentName = intent.resolveActivity(getPackageManager());
					if (componentName != null) {
						startActivity(intent1);
					}
				}
			} catch(Exception e1){
				e1.printStackTrace();
			}
		}
    }

    @Override
    protected void onPause() {

        if (!newActivityStarted && !activityFinished) {
//            startService(new Intent(BaseActivity.GENIE_SERVICE));
        }
        newActivityStarted = false;

        activityFinished = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
//        stopService(new Intent(BaseActivity.GENIE_SERVICE));
        super.onResume();
    }

    @Override
    public void finish() {
        activityFinished = true;
        super.finish();
    }
}
