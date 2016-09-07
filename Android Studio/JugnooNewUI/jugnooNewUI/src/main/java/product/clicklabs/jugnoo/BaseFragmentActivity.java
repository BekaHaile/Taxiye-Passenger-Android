package product.clicklabs.jugnoo;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.sabkuchfresh.home.HomeUtil;

import product.clicklabs.jugnoo.home.HomeActivity;

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
	protected void onResume() {
		super.onResume();
		if(!HomeActivity.checkIfUserDataNull(this)){
			HomeUtil.checkForAccessTokenChange(this);
		}
	}
}
