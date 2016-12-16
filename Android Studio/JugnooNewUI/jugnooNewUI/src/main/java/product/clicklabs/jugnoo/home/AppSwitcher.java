package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.jugnoo.pay.activities.MainActivity;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiLoginUsingAccessToken;
import product.clicklabs.jugnoo.apis.ApiUpdateClientId;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 8/19/16.
 */
public class AppSwitcher {


	public AppSwitcher(){}

	/**
	 * From menu
	 * @param activity
	 * @param clientId client id for the app
	 * @param latLng
     */
	public void switchApp(Activity activity, String clientId, LatLng latLng, boolean clearActivityStack) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.KEY_INTERNAL_APP_SWITCH, true);
		switchApp(activity, clientId, null, latLng, bundle, clearActivityStack, false, false);
	}

	/**
	 * After splash
	 * @param activity
	 * @param clientId client id for the app
	 * @param data
     * @param latLng
     */
	public void switchApp(Activity activity, String clientId, Uri data, LatLng latLng, boolean openHomeSwitcher) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.KEY_INTERNAL_APP_SWITCH, false);
		switchApp(activity, clientId, data, latLng, bundle, false, openHomeSwitcher, false);
	}


	/**
	 * For reorder offering opening
	 * @param activity
	 * @param clientId
	 * @param data
	 * @param latLng
	 * @param bundle
	 */
	public void switchApp(Activity activity, String clientId, Uri data, LatLng latLng, Bundle bundle){
		switchApp(activity, clientId, data, latLng, bundle, false, false, false);
	}

	/**
	 *
	 * @param activity
	 * @param clientId client id for the app
	 * @param data
	 * @param latLng
     * @param bundle
     */
	public void switchApp(final Activity activity, final String clientId, final Uri data, final LatLng latLng, final Bundle bundle,
						  final boolean clearActivityStack, final boolean openHomeSwitcher, final boolean slowTransition){
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			final Intent intent = new Intent();
			if(clearActivityStack) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
			intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			intent.putExtra(Constants.KEY_APP_SWITCH_BUNDLE, bundle);
//			if(!(activity instanceof HomeActivity)) {
				intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
				intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);
//			}
			if (data != null) {
				intent.setData(data);
			}

			if(openHomeSwitcher){
				intent.setClass(activity, HomeSwitcherActivity.class);
				intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
				intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);
				activity.startActivity(intent);
				activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
				//activity.finish();
			} else {
				ApiLoginUsingAccessToken.Callback callback = new ApiLoginUsingAccessToken.Callback() {
					@Override
					public void noNet() {
						DialogPopup.alertPopup(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
					}

					@Override
					public void success(String clientId) {
						if (!intentSentAfterDataCheck(activity, clientId, data, bundle, clearActivityStack)) {
							intent.setClass(activity, FreshActivity.class);
							intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);
						}

					}

					@Override
					public void failure() {

					}

					@Override
					public void onRetry(View view) {
						switchApp(activity, clientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
					}

					@Override
					public void onNoRetry(View view) {

					}
				};

				if (clientId.equalsIgnoreCase(Config.getAutosClientId()) && !(activity instanceof HomeActivity)) {
					if (Data.autoData == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								new ApiLoginUsingAccessToken.Callback() {
									@Override
									public void noNet() {
										DialogPopup.alertPopup(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
									}

									@Override
									public void success(String clientId) {
										if (!intentSentAfterDataCheck(activity, clientId, data, bundle, clearActivityStack)) {
											intent.setClass(activity, HomeActivity.class);
											intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
											activity.startActivity(intent);
											activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
											ActivityCompat.finishAffinity(activity);
										}
									}

									@Override
									public void failure() {

									}

									@Override
									public void onRetry(View view) {
										switchApp(activity, clientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
									}

									@Override
									public void onNoRetry(View view) {

									}
								});
					} else {
						intent.setClass(activity, HomeActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
						ActivityCompat.finishAffinity(activity);
						new ApiUpdateClientId().updateClientId(clientId);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				} else if (clientId.equalsIgnoreCase(Config.getFreshClientId()) && !(activity instanceof FreshActivity)) {
					if (Data.getFreshData() == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								callback);
					} else {
						intent.setClass(activity, FreshActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
						ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				} else if (clientId.equalsIgnoreCase(Config.getMealsClientId()) && !(activity instanceof FreshActivity)) {
					if (Data.getMealsData() == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								callback);
					} else {
						intent.setClass(activity, FreshActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
						ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				} else if (clientId.equalsIgnoreCase(Config.getGroceryClientId()) && !(activity instanceof FreshActivity)) {
					if (Data.getGroceryData() == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								callback);
					} else {
						intent.setClass(activity, FreshActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
						ActivityCompat.finishAffinity(activity);

					new ApiUpdateClientId().updateClientId(clientId);
					Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				}
			} else if (clientId.equalsIgnoreCase(Config.getMenusClientId()) && !(activity instanceof FreshActivity)) {
				if (Data.getMenusData() == null) {
					new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
							callback);
				} else {
					intent.setClass(activity, FreshActivity.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					ActivityCompat.finishAffinity(activity);

					new ApiUpdateClientId().updateClientId(clientId);
					Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				}
			}
			else if (clientId.equalsIgnoreCase(Config.getPayClientId()) && !(activity instanceof MainActivity)) {
				if (Data.getPayData() == null) {
					new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
							new ApiLoginUsingAccessToken.Callback() {
								@Override
								public void noNet() {
									DialogPopup.alertPopup(activity, Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG);
								}

								@Override
								public void success(String clientId) {
									if (!intentSentAfterDataCheck(activity, clientId, data, bundle, clearActivityStack)) {
										intent.setClass(activity, MainActivity.class);
										intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
										activity.startActivity(intent);
										activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
										ActivityCompat.finishAffinity(activity);
									}
								}

								@Override
								public void failure() {

								}

								@Override
								public void onRetry(View view) {
									switchApp(activity, clientId, data, latLng, bundle);
								}

								@Override
								public void onNoRetry(View view) {

								}
							});
				} else {
					intent.setClass(activity, MainActivity.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					ActivityCompat.finishAffinity(activity);

					new ApiUpdateClientId().updateClientId(clientId);
					Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				}
			}
			else if (activity instanceof FreshActivity && !clientId.equalsIgnoreCase(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId()))) {
				if ((clientId.equalsIgnoreCase(Config.getFreshClientId()) && Data.getFreshData() == null)
						|| (clientId.equalsIgnoreCase(Config.getMealsClientId()) && Data.getMealsData() == null)
						|| (clientId.equalsIgnoreCase(Config.getGroceryClientId()) && Data.getGroceryData() == null)
						|| (clientId.equalsIgnoreCase(Config.getMenusClientId()) && Data.getMenusData() == null)) {
					new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
							callback);
				} else {
						intent.setClass(activity, FreshActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
						ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				}
			}
		} else {
			retryDialog(DialogErrorType.NO_NET, activity, clientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType, final Activity activity, final String clientId, final Uri data,
							 final LatLng latLng, final Bundle bundle, final boolean clearActivityStack, final boolean openHomeSwitcher,
							 final boolean slowTransition){
		DialogPopup.dialogNoInternet(activity,
				dialogErrorType,
				new Utils.AlertCallBackWithButtonsInterface() {
					@Override
					public void positiveClick(View view) {
						switchApp(activity, clientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
					}

					@Override
					public void neutralClick(View view) {

					}

					@Override
					public void negativeClick(View view) {
					}
				});
	}

	private boolean intentSentAfterDataCheck(Activity activity, String clientId, Uri data, Bundle bundle, boolean clearActivityStack){
		try {
			Intent intent = new Intent();
			if(clearActivityStack) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
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
			else if(clientId.equalsIgnoreCase(Config.getGroceryClientId()) && Data.getGroceryData() == null){
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
			else if(clientId.equalsIgnoreCase(Config.getMenusClientId()) && Data.getMenusData() == null){
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
			else if(clientId.equalsIgnoreCase(Config.getPayClientId()) && Data.getPayData() == null){
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

			Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);

			intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
			intent.putExtra(Constants.KEY_APP_SWITCH_BUNDLE, bundle);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.fade_in_slow, R.anim.fade_out_slow);
			ActivityCompat.finishAffinity(activity);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	private int getInAnim(boolean slowTransition){
		if(slowTransition){
			return R.anim.fade_in_slow;
		} else{
			return R.anim.fade_in;
		}
	}

	private int getOutAnim(boolean slowTransition){
		if(slowTransition){
			return R.anim.fade_out_slow;
		} else{
			return R.anim.fade_out;
		}
	}



}
