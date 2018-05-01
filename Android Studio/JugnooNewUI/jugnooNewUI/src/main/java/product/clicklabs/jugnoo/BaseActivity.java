package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    @Override
    public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(LocaleHelper.onAttach(TypekitContextWrapper.wrap(newBase), "en"));
//		super.attachBaseContext();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Data.activityResumed = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Data.activityResumed = false;
	}
}
