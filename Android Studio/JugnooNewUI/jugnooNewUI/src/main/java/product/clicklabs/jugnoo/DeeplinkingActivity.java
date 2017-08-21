package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

public class DeeplinkingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Uri data = getIntent().getData();
		String clientId = Prefs.with(this).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
		String restaurantId = "-1";
		try {
			if(data != null){
				Log.e("data", "=" + data);
				if(data.getPath().toLowerCase().contains(Constants.KEY_RIDES)){
					clientId = Config.getAutosClientId();
				} else if(data.getPath().toLowerCase().contains(Constants.KEY_FATAFAT)){
					clientId = Config.getFreshClientId();
				} else if(data.getPath().toLowerCase().contains(Constants.KEY_MEALS)){
					clientId = Config.getMealsClientId();
				} else if(data.getPath().toLowerCase().contains(Constants.KEY_MENUS)){
					clientId = Config.getMenusClientId();
					if(data.getPathSegments().size()>3) {
						restaurantId = data.getPathSegments().get(3);
					}
				} else if(data.getPath().toLowerCase().contains(Constants.KEY_ASKLOCAL)){
					clientId = Config.getFeedClientId();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Prefs.with(this).save(Constants.SP_RESTAURANT_ID_TO_DEEP_LINK, restaurantId);
		if(Data.userData != null){
			MyApplication.getInstance().getAppSwitcher().switchApp(this, clientId,
					new LatLng(Data.latitude, Data.longitude), false);
		} else {
			Prefs.with(this).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			startActivity(new Intent(this, SplashNewActivity.class));
			finish();
		}

	}
}
