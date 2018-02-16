package product.clicklabs.jugnoo.base;

import android.annotation.TargetApi;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.LocationUpdate;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.PermissionCommon;

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

	public void requestLocationPermissionAndUpdates(){
		if(!PermissionCommon.hasPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
				&& shouldRequestLocationPermission()) {
			requestLocationPermissionExplicit();
		} else {
			getLocationFetcher().connect(this, 10000);
		}
	}

	public void requestLocationPermissionExplicit(){
		requestLocationPermissionExplicit(REQUEST_CODE_PERMISSION_LOCATION);
	}

	public void requestLocationPermissionExplicit(int requestCode){
		getPermissionCommon().getPermission(requestCode, true, true, android.Manifest.permission.ACCESS_FINE_LOCATION);
	}

	public void requestLocationUpdatesExplicit(){
		getLocationFetcher().connect(this, 10000);
	}

	@Override
	public void permissionGranted(int requestCode) {
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
			requestLocationUpdatesExplicit();
		}
	}

	@Override
	public void permissionDenied(int requestCode) {
		if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {

		}
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

	public boolean shouldRequestLocationPermission(){
		return false;
	}
}
