package product.clicklabs.jugnoo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.location.Location;
import android.support.annotation.NonNull;

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.PermissionCommon;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity implements PermissionCommon.PermissionListener, LocationUpdate {
	public static final int REQUEST_CODE_PERMISSION_LOCATION = 1011;
	private static final int REQUEST_CODE_PERMISSION_RECEIVE_SMS = 1012;

    @Override
    public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new HomeUtil().forceRTL(this);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(LocaleHelper.onAttach(newBase, LocaleHelper.getLanguage(newBase))));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Data.activityResumed = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Data.activityResumed = false;
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
	public PermissionCommon getPermissionCommon() {
		if (permissionCommon == null) {
			permissionCommon = new PermissionCommon(this);
		}
		return permissionCommon;
	}

	public void requestLocationPermissionAndUpdates(){
		if(!PermissionCommon.hasPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
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
		getPermissionCommon().getPermission(requestCode, true, android.Manifest.permission.ACCESS_FINE_LOCATION);
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
			locationFetcher = new LocationFetcher(this);
		}
		return locationFetcher;
	}

	public void locationChanged(Location location){}

	public boolean shouldRequestLocationPermission(){
		return false;
	}

	public void requestReceiveSMSPermission(){
		if(!PermissionCommon.hasPermission(this, android.Manifest.permission.RECEIVE_SMS)){
			getPermissionCommon().getPermission(REQUEST_CODE_PERMISSION_RECEIVE_SMS, true, Manifest.permission.RECEIVE_SMS);
		}
	}

	private static final int WIDTH_PX = 200;
	private static final int HEIGHT_PX = 80;

	public static boolean isLocaleSupported(Context context, String text) {
		int w = WIDTH_PX, h = HEIGHT_PX;
		Resources resources = context.getResources();
		float scale = resources.getDisplayMetrics().density;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Bitmap orig = bitmap.copy(conf, false);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setTextSize((int) (14 * scale));

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;

		canvas.drawText(text, x, y, paint);
		boolean res = !orig.sameAs(bitmap);
		orig.recycle();
		bitmap.recycle();
		return res;
	}
}
