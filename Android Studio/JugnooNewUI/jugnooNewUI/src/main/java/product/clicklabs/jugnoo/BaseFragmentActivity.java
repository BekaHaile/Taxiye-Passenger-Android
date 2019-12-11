package product.clicklabs.jugnoo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseFragmentActivity extends FragmentActivity {

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
		if(!HomeActivity.checkIfUserDataNull(this)){
			HomeActivity.checkForAccessTokenChange(this);
		} else {
			return;
		}
		new HomeUtil().forceRTL(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Data.activityResumed = true;
		if(!HomeActivity.checkIfUserDataNull(this)){
			HomeActivity.checkForAccessTokenChange(this);
		} else {
			return;
		}

		if(BaseFragmentActivity.this instanceof ChatActivity){
			if(Prefs.with(this).getBoolean(Constants.SP_CHAT_CLOSE, false)) {
				Prefs.with(this).save(Constants.SP_CHAT_CLOSE, false);
				finish();
			} else{
				LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_CHAT_CLOSE));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Data.activityResumed = false;
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
	}


	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(BaseFragmentActivity.this instanceof ChatActivity){
				Prefs.with(context).save(Constants.SP_CHAT_CLOSE, false);
				finish();
			}
		}
	};
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(LocaleHelper.onAttach(newBase, LocaleHelper.getLanguage(newBase))));
	}
}
