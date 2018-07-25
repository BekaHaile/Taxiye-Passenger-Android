package product.clicklabs.jugnoo.base;

import android.annotation.TargetApi;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.sabkuchfresh.fragments.TrackOrderFragment;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.permission.PermissionCommon;

/**
 * Created by shankar on 21/03/18.
 */

public class BaseFragment extends Fragment implements PermissionCommon.PermissionListener, LocationUpdate {
	public static final int REQUEST_CODE_PERMISSION_LOCATION = 1011;

	@Override
	public void onPause() {
		super.onPause();
		getLocationFetcher().destroy();
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

	private PermissionCommon permissionCommon;
	private PermissionCommon getPermissionCommon() {
		if (permissionCommon == null) {
			permissionCommon = new PermissionCommon(this);
		}
		return permissionCommon;
	}



	public void requestLocationPermissionExplicit(){
		getPermissionCommon().getPermission(REQUEST_CODE_PERMISSION_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);
	}


	@Override
	public void permissionGranted(int requestCode) {
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
			getLocationFetcher().connect(this, 10000);
		}
	}

	@Override
	public boolean permissionDenied(int requestCode, boolean neverAsk) {
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

	private LocationFetcher locationFetcher;
	public LocationFetcher getLocationFetcher(){
		if(locationFetcher == null){
			locationFetcher = new LocationFetcher(getActivity());
		}
		return locationFetcher;
	}

	public void locationChanged(Location location){}


}
