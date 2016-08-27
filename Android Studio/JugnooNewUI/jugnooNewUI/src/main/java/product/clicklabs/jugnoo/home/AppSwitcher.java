package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
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


	public AppSwitcher(){}

	public void switchApp(final Activity activity, final String clientId, LatLng latLng) {
		switchApp(activity, clientId, null, latLng);
	}

	public void switchApp(final Activity activity, final String clientId, final Uri data, final LatLng latLng){

		ApiLoginUsingAccessToken.Callback callback = new ApiLoginUsingAccessToken.Callback() {
			@Override
			public void noNet() {
				DialogPopup.alertPopup(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
			}

			@Override
			public void success(String clientId) {
				if(!intentSentAfterDataCheck(activity, clientId, data)) {
					Intent intent = new Intent(activity, FreshActivity.class);
					intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					activity.startActivity(intent);
					activity.finish();
				}

			}

			@Override
			public void failure() {

			}
		};

		if(clientId.equalsIgnoreCase(Config.getAutosClientId()) && !(activity instanceof HomeActivity)){
			if(Data.autoData == null){
				new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
						new ApiLoginUsingAccessToken.Callback() {
							@Override
							public void noNet() {
								DialogPopup.alertPopup(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
							}

							@Override
							public void success(String clientId) {
								if(!intentSentAfterDataCheck(activity, clientId, data)) {
									Intent intent = new Intent(activity, HomeActivity.class);
									intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
									if (data != null) {
										intent.setData(data);
									}
									activity.startActivity(intent);
									activity.finish();
								}
							}

							@Override
							public void failure() {

							}
						});
			} else {
				Intent intent = new Intent(activity, HomeActivity.class);
				intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				if(data != null){
					intent.setData(data);
				}
				activity.startActivity(intent);
				activity.finish();
				new ApiUpdateClientId().updateClientId(clientId);
				Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			}
		}
		else if(clientId.equalsIgnoreCase(Config.getFreshClientId()) && !(activity instanceof FreshActivity)){
			if(Data.getFreshData() == null){
				new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
						callback);
			} else {
				Intent intent = new Intent(activity, FreshActivity.class);
				intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				activity.startActivity(intent);
				activity.finish();

				new ApiUpdateClientId().updateClientId(clientId);
				Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			}
		}
		else if(clientId.equalsIgnoreCase(Config.getMealsClientId()) && !(activity instanceof FreshActivity)){
			if(Data.getFreshData() == null){
				new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
						callback);
			} else {
				Intent intent = new Intent(activity, FreshActivity.class);
				intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				activity.startActivity(intent);
				activity.finish();

				new ApiUpdateClientId().updateClientId(clientId);
				Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			}
		}
		else if(activity instanceof FreshActivity && !clientId.equalsIgnoreCase(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()))){
			if(Data.getFreshData() == null){
				new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
						callback);
			} else {
				Intent intent = new Intent(activity, FreshActivity.class);
				intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				activity.startActivity(intent);
				activity.finish();

				new ApiUpdateClientId().updateClientId(clientId);
				Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			}
		}

	}

	private boolean intentSentAfterDataCheck(Activity activity, String clientId, Uri data){
		try {
			Intent intent = new Intent();
			if(clientId.equalsIgnoreCase(Config.getAutosClientId()) && Data.autoData == null) {
				if(Data.getFreshData() != null){
					clientId = Config.getFreshClientId();
				}
				else if(Data.getMealsData() != null){
					clientId = Config.getMealsClientId();
				}
				intent.setClass(activity, FreshActivity.class);
			}
			else if(clientId.equalsIgnoreCase(Config.getFreshClientId()) && Data.getFreshData() == null){
				if(Data.autoData != null) {
					intent.setClass(activity, HomeActivity.class);
					clientId = Config.getAutosClientId();
					if (data != null) {
						intent.setData(data);
					}
				}
				else if(Data.getMealsData() != null){
					intent.setClass(activity, FreshActivity.class);
					clientId = Config.getMealsClientId();
				}
			}
			else if(clientId.equalsIgnoreCase(Config.getMealsClientId()) && Data.getMealsData() == null){
				if(Data.autoData != null) {
					intent.setClass(activity, HomeActivity.class);
					clientId = Config.getAutosClientId();
					if (data != null) {
						intent.setData(data);
					}
				}
				else if(Data.getFreshData() != null){
					intent.setClass(activity, FreshActivity.class);
					clientId = Config.getFreshClientId();
				}
			}
			else{
				return false;
			}

			intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			activity.startActivity(intent);
			activity.finish();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}




}
