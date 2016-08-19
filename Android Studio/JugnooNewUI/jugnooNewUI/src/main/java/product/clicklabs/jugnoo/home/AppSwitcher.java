package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.apis.ApiLoginUsingAccessToken;
import product.clicklabs.jugnoo.apis.ApiUpdateClientId;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 8/19/16.
 */
public class AppSwitcher {

	private Context context;

	public AppSwitcher(Context context){
		this.context = context;
	}

	public void switchApp(final Activity activity, String clientId){

		if(clientId.equalsIgnoreCase(Config.getAutosClientId()) && !(activity instanceof HomeActivity)){

		}
		else if(clientId.equalsIgnoreCase(Config.getFreshClientId()) && !(activity instanceof FreshActivity)){
			if(Data.getFatafatData() == null){
				new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, Data.latitude, Data.longitude, clientId,
						new ApiLoginUsingAccessToken.Callback() {
							@Override
							public void noNet() {
								DialogPopup.alertPopup(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
							}

							@Override
							public void success() {
								Intent intent = new Intent(activity, FreshActivity.class);
								activity.startActivity(intent);
							}

							@Override
							public void failure() {

							}
						});
			} else {
				Intent intent = new Intent(activity, FreshActivity.class);
				activity.startActivity(intent);
				new ApiUpdateClientId().updateClientId(clientId);
				Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			}
		}

	}




}
