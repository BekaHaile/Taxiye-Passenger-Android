package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.content.IntentSender;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by shankar on 5/18/15.
 */
public class LocationInit {

    private static final String TAG = LocationInit.class.getSimpleName();

    public static final int LOCATION_REQUEST_CODE = 1000;

    public static void showLocationAlertDialog(final Activity activity){
        int sdkInt = android.os.Build.VERSION.SDK_INT;
        if(sdkInt >= 19) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(30*1000);
            mLocationRequest.setFastestInterval(30*1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest mLocationSettingsRequest = builder.build();

            SettingsClient mSettingsClient = LocationServices.getSettingsClient(activity);

            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.i(TAG, "All location settings are satisfied.");
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                            "location settings ");
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(activity, LOCATION_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);
                                    Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            DialogPopup.showLocationSettingsAlert(activity);
        }
    }


}
