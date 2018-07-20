package product.clicklabs.jugnoo.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.R;

/**
 * Created by socomo-46 on 22/09/16.
 */


public final class PermissionCommon {

    private static final int REQUEST_CODE = 0x8;
    private static final int REQUEST_CODE_RATIONAL = 0x9;
    private int requestCodeInitiated;
    private String[] permissionsInitiated;
    private String appName;
    private Activity activity;
    private PermissionListener permissionListener;
    private HashMap<String, String> rationalMessageMap;
    private boolean isFragment;
    private Fragment fragment;


    @SuppressWarnings("unused")
    private PermissionCommon() {

    }


    /**
     * @param context context of activity or fragment
     */
    public <Instance extends PermissionListener> PermissionCommon(Instance context) {
        permissionListener = context;
        if (context instanceof Activity)
            activity = (Activity) context;
        else if (context instanceof Fragment) {
            activity = ((Fragment) context).getActivity();
            isFragment = true;
            this.fragment = (Fragment) context;
        }

    }


    /**
     * @param context            context of activity or fragment
     * @param rationalMessageMap An explanation to the user, on why you are using the features
     *                           This can be made like HashMap<String,String> hashMap = new HashMap<>();
     *                           hashMap.put("Manifest.permission.CAMERA","This app requires camera access to use face recognition");
     *                           hashMap.put("Manifest.permission.READ_EXTERNAL_STORAGE","This app requires storage access to display gallery images");
     *                           Alternatively, you can declare the permission reason in getRationalMessage() method which would be global for the application if not passed in method
     */
    public <Instance extends PermissionListener> PermissionCommon(Instance context, @NonNull HashMap<String, String> rationalMessageMap) {
        permissionListener = context;
        if (context instanceof Activity)
            activity = (Activity) context;
        else if (context instanceof Fragment) {
            activity = ((Fragment) context).getActivity();
            isFragment = true;
            this.fragment = (Fragment) context;
        }
        this.rationalMessageMap = rationalMessageMap;
    }

    public final void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE:


                int permissionsGranted = 0;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_GRANTED)
                        permissionsGranted++;
                }


                if (grantResults.length == permissionsGranted) {
                    // this means all the permissions have been granted and we are ready to GO!
                    permissionListener.permissionGranted(requestCodeInitiated);


                } else {

                    for (String permission : permissions) {

                        if (!isGranted(permission) && !shouldShowRationalPermission(permission)) {


                            String messageToShow = activity.getString(R.string.you_have_disabled_permission_format, getPermissionLabel(permission));
                            showPermissionDenied(messageToShow);
                            //this means the user has blocked a permission and chosen "Never ask again" for the that permission.


                            permissionListener.permissionDenied(requestCodeInitiated);

                            return;

                        }

                    }

                    //the user has denied permission normally.Wait for user's action to ask for permission again
                    permissionListener.permissionDenied(requestCodeInitiated);


                }


                break;


            case REQUEST_CODE_RATIONAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getPermission(requestCodeInitiated, true, permissionsInitiated); //continues to check again if all permissions have been granted or there is still a rational permission pending
                else {
                    if (shouldShowRationalPermission(permissions[0])) {

//                        String messageToShow = activity.getString(R.string.you_have_disabled_permission_format, getPermissionLabel(permissions[0]));
//                        showPermissionDenied(messageToShow);
                        //activity means the user has blocked a permission and chosen "Never ask again" for the same
                        permissionListener.permissionDenied(requestCodeInitiated);

                        return;

                    }

                    permissionListener.permissionDenied(requestCodeInitiated);

                }

                break;
            default:
                break;
        }
    }

    /**
     * @param permission permission for which the rational message is needed
     * @return A message for the permission explaining why the feature is required
     * This is displayed as per the hashMap if not passed feel free to declare the reasons below that would be global for the app
     */
    private String getRationalMessage(String permission) {


        if (rationalMessageMap != null && rationalMessageMap.containsKey(permission))
            return rationalMessageMap.get(permission);


        if (appName == null)
            appName = activity.getString(activity.getApplicationInfo().labelRes);

        String permissionName = permission;
        if(permission.length() > 0){
            String[] arr = permission.split("\\.");
            permissionName = arr[arr.length-1];
        }
        switch (permission) {
            case Manifest.permission.CAMERA:
                return activity.getString(R.string.need_permission_camera_format, appName);
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return activity.getString(R.string.need_permission_location_format, appName);
            case Manifest.permission.READ_CONTACTS:
                return activity.getString(R.string.need_permission_contact_format, appName);
            case Manifest.permission.READ_PHONE_STATE:
                return activity.getString(R.string.need_permission_phone_state_format, appName);
            default:
                return activity.getString(R.string.need_permission_default_format, appName, permissionName);
        }

    }


    /**
     * @param requestCode     request code corresponding to set of permissions that would be returned onGranted() or onDenied()
     * @param showRationale
     * @param permissionArray set of permissions needed for the feature
     */

    public void getPermission(int requestCode, boolean showRationale, final String... permissionArray) {


        // Android code Build is lower than Marshmallow no need to check for permissions

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionListener.permissionGranted(requestCode);

            return;
        }


        //creates an arrayList of all the permissions that need to be  asked for
        ArrayList<String> permissionsToAsk = new ArrayList<>();
        for (String permission : permissionArray) {

            if (!isGranted(permission)) {
                permissionsToAsk.add(permission);
            }

        }

        //if all permissions are granted already calls the permissionsGrantedMethod
        if (permissionsToAsk.size() < 1) {
            permissionListener.permissionGranted(requestCode);

            return;
        }

        permissionsInitiated = permissionArray;
        requestCodeInitiated = requestCode;


        //this loop checks for any rationalPermission and stops the process un till the user reacts to the first rational permission if found
        //if granted the process continues again from onRequestPermissionResult() if not granted we wait for user's further input
        for (final String permission : permissionsToAsk) {


            if (shouldShowRationalPermission(permission)) {
                if(showRationale) {
                    showDialog1Button(activity, getRationalMessage(permission), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, REQUEST_CODE_RATIONAL);
                        }
                    });
                }
                else {
                    requestPermissions(new String[]{permission}, REQUEST_CODE_RATIONAL);
                }
                return;
            }


        }
        //At activity point if shouldAsk is true there is no rational Permission that exists and  No explanation needed, we can request for  the permissions.
        requestPermissions(permissionsToAsk.toArray(new String[permissionsToAsk.size()]), REQUEST_CODE);


    }



    private void requestPermissions(String[] strings, int requestCode) {

        if (isFragment) {
            fragment.requestPermissions(strings, requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, strings, requestCode);
        }
    }

    private boolean shouldShowRationalPermission(String permission) {
        if (isFragment) {
            return fragment.shouldShowRequestPermissionRationale(permission);
        } else {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

    }


    /**
     * @param permission permission to check
     * @return returns true if permission has been granted
     */

    public boolean isGranted(String permission) {

        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param permission permission to check
     * @return returns human readable format of the permission
     */

    private CharSequence getPermissionLabel(String permission) {
        try {

            PermissionInfo permissionInfo = activity.getPackageManager().getPermissionInfo(permission, 0);
            return permissionInfo.loadLabel(activity.getPackageManager());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Shows a snackBar to inform the user that he needs to go to settings to enable permission
     *
     * @param message The message that needs to be shown to the user for the corresponding permission
     */
    private void showPermissionDenied(String message) {
        showDialog2Button(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    public interface PermissionListener {
        /**
         * @param requestCode requestCode used to ask for specific set of permissions
         *                    activity method is called if all the permissions have been granted
         */

        void permissionGranted(int requestCode);

        /**
         * @param requestCode requestCode used to ask for specific set of permissions
         *                    activity method is called if any of the permission has been denied
         */
        void permissionDenied(int requestCode);


    }

    public static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private AlertDialog alertDialog;
    private void showDialog2Button(Activity activity, String message, DialogInterface.OnClickListener positiveListener) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);

        builder.setPositiveButton(activity.getString(R.string.settings), positiveListener);
        builder.setNegativeButton(activity.getString(R.string.cancel), null);

        builder.setCancelable(false);
        alertDialog = builder.show();
    }

    private void showDialog1Button(Activity activity, String message, DialogInterface.OnClickListener positiveListener) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);

        builder.setPositiveButton(activity.getString(R.string.ok), positiveListener);
        builder.setNegativeButton(activity.getString(R.string.cancel), null);

        builder.setCancelable(true);
        alertDialog = builder.show();
    }

}
