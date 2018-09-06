package product.clicklabs.jugnoo;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fugu.FuguConfig;

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.permission.PermissionCommon;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseAppCompatActivity extends AppCompatActivity implements PermissionCommon.PermissionListener, LocationUpdate {
	public static final int REQUEST_CODE_PERMISSION_LOCATION = 1011;
	public static final int REQUEST_CODE_PERMISSION_STORAGE = 1013;
	public static final int REQUEST_CODE_PERMISSION_PHONE = 1014;

	private PermissionCommon permissionCommon;
	private LocationFetcher locationFetcher;
	private Handler handler = new Handler();
	private boolean openingFugu;

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
		getLocationFetcher().destroy();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(LocaleHelper.onAttach(newBase, LocaleHelper.getLanguage(newBase))));
	}

	public boolean checkOfAT(){
    	return true;
	}

	@TargetApi(23)
	@SuppressWarnings("unused")
	@Override
	public void onRequestPermissionsResult(final int requestCode,
										   final @NonNull String permissions[],
										   final @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		getPermissionCommon().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public PermissionCommon getPermissionCommon() {
		if (permissionCommon == null) {
			permissionCommon = new PermissionCommon(this).setCallback(this);
		}
		return permissionCommon;
	}

	public void requestLocationPermissionExplicit(){
		getPermissionCommon().getPermission(REQUEST_CODE_PERMISSION_LOCATION,  android.Manifest.permission.ACCESS_FINE_LOCATION);
	}


	public void requestLocationUpdatesExplicit(){
		getLocationFetcher().connect(this, 10000);
	}

	@Override
	public void permissionGranted(int requestCode) {
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
			requestLocationUpdatesExplicit();
		} else if(requestCode == REQUEST_CODE_PERMISSION_PHONE){
			fuguShowConversations();
		}
	}

	private void fuguShowConversations() {
		FuguConfig.getInstance().showConversations(this, getString(R.string.fugu_support_title));
	}

	@Override
	public boolean permissionDenied(int requestCode, boolean neverAsk) {
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {

		}
		return true;
	}

	@Override
	public void onRationalRequestIntercepted(int requestCode) {

	}

	@Override
	public void onLocationChanged(Location location) {
		HomeActivity.myLocation = location;
		Data.loginLatitude = location.getLatitude();
		Data.loginLongitude = location.getLongitude();
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}

	public LocationFetcher getLocationFetcher(){
		if(locationFetcher == null){
			locationFetcher = new LocationFetcher(this);
		}
		return locationFetcher;
	}

	public void locationChanged(Location location){}

	public boolean shouldRequestLocationPermission(){
		return false;
	}

	public void requestExternalStoragePermission(){
//		getPermissionCommon().getPermission(REQUEST_CODE_PERMISSION_STORAGE, false, Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	public void openFugu() {
		if(!openingFugu) {
//		if(PermissionCommon.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
			fuguShowConversations();
//		} else {
//			getPermissionCommon().getPermission(REQUEST_CODE_PERMISSION_PHONE, false, true, Manifest.permission.READ_PHONE_STATE);
//		}
			openingFugu = true;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					openingFugu = false;
				}
			}, 1000);
		}
	}

	public void attachBaseContextWithoutTypekit(Context newBase){
		super.attachBaseContext(LocaleHelper.onAttach(newBase, LocaleHelper.getLanguage(newBase)));
	}
}
