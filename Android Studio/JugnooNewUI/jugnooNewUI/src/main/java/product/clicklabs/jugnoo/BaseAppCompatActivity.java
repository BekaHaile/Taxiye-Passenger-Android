package product.clicklabs.jugnoo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    public void startActivity(Intent intent) {
		try {
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
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(checkOfAT() && !HomeActivity.checkIfUserDataNull(this)){
			HomeActivity.checkForAccessTokenChange(this);
		}
		new HomeUtil().forceRTL(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Data.activityResumed = true;
		if(checkOfAT() && !HomeActivity.checkIfUserDataNull(this)){
			HomeActivity.checkForAccessTokenChange(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Data.activityResumed = false;
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(LocaleHelper.onAttach(newBase,
				Prefs.with(newBase).getString(Constants.KEY_DEFAULT_LANG, newBase.getString(R.string.default_lang)))));
	}

	public boolean checkOfAT(){
    	return true;
	}
}
