package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.jugnoo.pay.activities.MainActivity;
import com.jugnoo.pay.activities.PayTutorial;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiLoginUsingAccessToken;
import product.clicklabs.jugnoo.apis.ApiUpdateClientId;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 8/19/16.
 */
public class AppSwitcher {


	public AppSwitcher(){}

	/**
	 * From fab, menu and notification deep linking
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
	 * For reorderMenus offering opening
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
	public void switchApp(final Activity activity, String clientId, final Uri data, final LatLng latLng, final Bundle bundle,
						  final boolean clearActivityStack, final boolean openHomeSwitcher, final boolean slowTransition){
		try {
			if (MyApplication.getInstance().isOnline()) {
				final Intent intent = new Intent();
				if(clearActivityStack) {
					//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				}

				boolean noOfferingEnabledForHomeScreen = false, isOnlyFatafatNewEnabled = false;
				// to check id Data.userData's key of <offering>_enabled is 1 for the client_id to open
				if(Data.userData != null){
					isOnlyFatafatNewEnabled = Data.userData.isRidesAndFatafatEnabled();

				/*
					 //commented because rides may not be available at every location & rather keep the user at the same offering, than switching to rides
				if(Data.userData.getIntegratedJugnooEnabled() == 0 || (!isOnlyFatafatNewEnabled
							&& ((clientId.equalsIgnoreCase(Config.getFreshClientId()) && Data.userData.getFreshEnabled() != 1)
							|| (clientId.equalsIgnoreCase(Config.getMealsClientId()) && Data.userData.getMealsEnabled() != 1)
							|| (clientId.equalsIgnoreCase(Config.getMenusClientId()) && Data.userData.getMenusEnabled() != 1)
							|| (clientId.equalsIgnoreCase(Config.getPayClientId()) && Data.userData.getPayEnabled() != 1)
							|| (clientId.equalsIgnoreCase(Config.getFeedClientId()) && Data.userData.getFeedEnabled() != 1)
							|| (clientId.equalsIgnoreCase(Config.getProsClientId()) && Data.userData.getProsEnabled() != 1)
							|| (clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId()) && Data.userData.getDeliveryCustomerEnabled() != 1))
					)){
						clientId = Config.getAutosClientId();
					}*/

					//if only delivery customer enabled and client id to open is other than that
					if(isOnlyFatafatNewEnabled
							&& (clientId.equalsIgnoreCase(Config.getFreshClientId())
									|| clientId.equalsIgnoreCase(Config.getMealsClientId())
									|| clientId.equalsIgnoreCase(Config.getMenusClientId())
									|| clientId.equalsIgnoreCase(Config.getFeedClientId())
									|| clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId())
									|| clientId.equalsIgnoreCase(Config.getProsClientId()))){
						Prefs.with(activity).save(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, clientId);
						clientId = Config.getDeliveryCustomerClientId();
					}

					//if only delivery customer enabled and deepindex is for other than delivery customer
					String clientIdFromDeepLink = HomeUtil.getClientIdFromDeepIndexForMergedOfferings(Data.deepLinkIndex);
					if(isOnlyFatafatNewEnabled
							&& Data.deepLinkIndex != AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal()
							&& !TextUtils.isEmpty(clientIdFromDeepLink)){
						Prefs.with(activity).save(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, clientIdFromDeepLink);
						Data.deepLinkIndex = -1;
					}

					if (Data.userData.getMealsEnabled() == 0
							&& Data.userData.getFreshEnabled() == 0
							&& Data.userData.getMenusEnabled() == 0
							&& Data.userData.getFeedEnabled() == 0
							&& Data.userData.getDeliveryCustomerEnabled()==0) {
						noOfferingEnabledForHomeScreen = true;
					}
				}


				intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
				intent.putExtra(Constants.KEY_APP_SWITCH_BUNDLE, bundle);
				intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
				intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);
				if (data != null) {
					intent.setData(data);
				}
				final String finalClientId = clientId;
				if(openHomeSwitcher && !noOfferingEnabledForHomeScreen){
					intent.setClass(activity, HomeSwitcherActivity.class);
					intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
					intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);
					activity.startActivity(intent);
					activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
					ActivityCompat.finishAffinity(activity);
				} else {

					ApiLoginUsingAccessToken.Callback callback = new ApiLoginUsingAccessToken.Callback() {
						@Override
						public void noNet() {
							DialogPopup.alertPopup(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc));
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
						public void failure(boolean onboardingFlow, String response, LoginResponse loginResponse) {

						}

						@Override
						public void onRetry(View view) {
							switchApp(activity, finalClientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
						}

						@Override
						public void onNoRetry(View view) {

						}
					};

					if (clientId.equalsIgnoreCase(Config.getAutosClientId()) && !(activity instanceof HomeActivity)) {
						if (Data.autoData == null) {
							new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
									true, new ApiLoginUsingAccessToken.Callback() {
										@Override
										public void noNet() {
											DialogPopup.alertPopup(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc));
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
										public void failure(boolean onboardingFlow, String response, LoginResponse loginResponse) {

										}

										@Override
										public void onRetry(View view) {
											switchApp(activity, finalClientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
										}

										@Override
										public void onNoRetry(View view) {

										}
									}, false);
						} else {
							intent.setClass(activity, HomeActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);
							new ApiUpdateClientId().updateClientId(clientId, latLng);
							Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
						}
					} else if (clientId.equalsIgnoreCase(Config.getFreshClientId()) && !(activity instanceof FreshActivity)) {
						if (Data.getFreshData() == null) {
							new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
									true, callback, false);
						} else {
							intent.setClass(activity, FreshActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);

							new ApiUpdateClientId().updateClientId(clientId, latLng);
							Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
						}
					} else if (clientId.equalsIgnoreCase(Config.getMealsClientId()) && !(activity instanceof FreshActivity)) {
						if (Data.getMealsData() == null) {
							new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
									true, callback, false);
						} else {
							intent.setClass(activity, FreshActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);

							new ApiUpdateClientId().updateClientId(clientId, latLng);
							Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
						}
					} else if (clientId.equalsIgnoreCase(Config.getGroceryClientId()) && !(activity instanceof FreshActivity)) {
						if (Data.getGroceryData() == null) {
							new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
									true, callback, false);
						} else {
							intent.setClass(activity, FreshActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId, latLng);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				} else if (clientId.equalsIgnoreCase(Config.getMenusClientId()) && !(activity instanceof FreshActivity)) {
					if (Data.getMenusData() == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								true, callback, false);
					} else {
						intent.setClass(activity, FreshActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
						ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId, latLng);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				}else if (clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId()) && !(activity instanceof FreshActivity)) {
					if (Data.getDeliveryCustomerData() == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								true, callback, false);
					} else {
						intent.setClass(activity, FreshActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
						ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId, latLng);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				}
				else if (clientId.equalsIgnoreCase(Config.getPayClientId()) && !(activity instanceof MainActivity)) {
					if (Data.getPayData() == null) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								true, new ApiLoginUsingAccessToken.Callback() {
									@Override
									public void noNet() {
										DialogPopup.alertPopup(activity, activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc));
									}

									@Override
									public void success(String clientId) {
										if (!intentSentAfterDataCheck(activity, clientId, data, bundle, clearActivityStack)) {
											// intent.setClass(activity, MainActivity.class);
											intent.setClass(activity, PayTutorial.class);

											intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
											intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);

											intent.putExtra(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
											activity.startActivity(intent);
											activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
											ActivityCompat.finishAffinity(activity);
										}
									}

									@Override
									public void failure(boolean onboardingFlow, String response, LoginResponse loginResponse) {

									}

									@Override
									public void onRetry(View view) {
										switchApp(activity, finalClientId, data, latLng, bundle);
									}

									@Override
									public void onNoRetry(View view) {

									}
								}, false);
					} else {
						// intent.setClass(activity, MainActivity.class);
						intent.setClass(activity, PayTutorial.class);

						intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
						intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);

						activity.startActivity(intent);
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
						ActivityCompat.finishAffinity(activity);

						new ApiUpdateClientId().updateClientId(clientId, latLng);
						Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
					}
				}
				else if (clientId.equalsIgnoreCase(Config.getFeedClientId()) && !(activity instanceof FreshActivity)) {
						if (Data.getFeedData() == null) {
							new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
									true, callback, false);
						} else {
							intent.setClass(activity, FreshActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);

							new ApiUpdateClientId().updateClientId(clientId, latLng);
							Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
						}
					}
					else if (clientId.equalsIgnoreCase(Config.getProsClientId()) && !(activity instanceof FreshActivity)) {
						if (Data.getProsData() == null) {
							new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
									true, callback, false);
						} else {
							intent.setClass(activity, FreshActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(getInAnim(slowTransition), getOutAnim(slowTransition));
							ActivityCompat.finishAffinity(activity);

							new ApiUpdateClientId().updateClientId(clientId, latLng);
							Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
						}
					}
				else if (activity instanceof FreshActivity && !clientId.equalsIgnoreCase(((FreshActivity)activity).currentOpenClientIdForFab())) {
						if(isOnlyFatafatNewEnabled && ((FreshActivity)activity).currentOpenClientIdForFab().equals(Config.getDeliveryCustomerClientId())){
							Intent broadcastIntent = new Intent(Data.LOCAL_BROADCAST);
							broadcastIntent.putExtra(Constants.KEY_FLAG, Constants.OPEN_APP_CLIENT_ID);
							broadcastIntent.putExtra(Constants.KEY_CLIENT_ID, clientId);
							LocalBroadcastManager.getInstance(activity).sendBroadcast(broadcastIntent);
							return;
						}
						if ((clientId.equalsIgnoreCase(Config.getFreshClientId()) && Data.getFreshData() == null)
							|| (clientId.equalsIgnoreCase(Config.getMealsClientId()) && Data.getMealsData() == null)
							|| (clientId.equalsIgnoreCase(Config.getGroceryClientId()) && Data.getGroceryData() == null)
							|| (clientId.equalsIgnoreCase(Config.getMenusClientId()) && Data.getMenusData() == null)
							|| (clientId.equalsIgnoreCase(Config.getFeedClientId()) && Data.getFeedData() == null)
							|| (clientId.equalsIgnoreCase(Config.getProsClientId()) && Data.getProsData() == null)
							|| (clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId()) && Data.getDeliveryCustomerData() == null)
							) {
						new ApiLoginUsingAccessToken(activity).hit(Data.userData.accessToken, latLng.latitude, latLng.longitude, clientId,
								true, callback, false);
					} else {
							intent.setClass(activity, FreshActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
							ActivityCompat.finishAffinity(activity);
							new ApiUpdateClientId().updateClientId(clientId, latLng);
							Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
						}
					} else if(activity instanceof FreshActivity
							&& clientId.equalsIgnoreCase(((FreshActivity)activity).currentOpenClientIdForFab())
							&& isOnlyFatafatNewEnabled
							&& !"".equalsIgnoreCase(Prefs.with(activity).getString(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, ""))){
							Intent broadcastIntent = new Intent(Data.LOCAL_BROADCAST);
							broadcastIntent.putExtra(Constants.KEY_FLAG, Constants.OPEN_APP_CLIENT_ID);
							broadcastIntent.putExtra(Constants.KEY_CLIENT_ID, Prefs.with(activity).getString(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, ""));
							LocalBroadcastManager.getInstance(activity).sendBroadcast(broadcastIntent);
					}
				}
			} else {
				retryDialog(DialogErrorType.NO_NET, activity, clientId, data, latLng, bundle, clearActivityStack, openHomeSwitcher, slowTransition);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			}else if(clientId.equalsIgnoreCase(Config.getDeliveryCustomerClientId()) && Data.getDeliveryCustomerData() == null){
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
			else if(clientId.equalsIgnoreCase(Config.getFeedClientId()) && Data.getFeedData() == null){
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
			else if(clientId.equalsIgnoreCase(Config.getProsClientId()) && Data.getProsData() == null){
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
			Prefs.with(activity).save(Constants.SP_CLIENT_ID_VIA_DEEP_LINK, "");

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
