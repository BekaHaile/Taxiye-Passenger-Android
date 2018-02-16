package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import product.clicklabs.jugnoo.R;

/**
 * Created by ankit on 04/04/17.
 */

public class FBAccountKit {
    private Activity activity;
    private static final int FRAMEWORK_REQUEST_CODE = 1;
    private int nextPermissionsRequestCode = 4000;
    private OnCompleteListener completeListener;

    public FBAccountKit(Activity activity) {
        this.activity = activity;
    }

    private interface OnCompleteListener {
        void onComplete();
    }

    public void startFbAccountKit(PhoneNumber phoneNumber){
        onLogin(LoginType.PHONE, phoneNumber);
    }

    private void onLogin(final LoginType loginType, PhoneNumber phoneNumber) {
        final Intent intent = new Intent(activity, AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType,
                AccountKitActivity.ResponseType.CODE);
        configurationBuilder.setTheme(R.style.AppLoginTheme_Salmon);
        configurationBuilder.setTitleType(AccountKitActivity.TitleType.LOGIN);
        configurationBuilder.setDefaultCountryCode(Utils.getCountryCode(activity));
        if(phoneNumber != null && !phoneNumber.toString().equalsIgnoreCase("")) {
            configurationBuilder.setInitialPhoneNumber(phoneNumber);
        }
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration);
        completeListener = new OnCompleteListener() {
            @Override
            public void onComplete() {
                activity.startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
            }
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            completeListener.onComplete();
            return;
        }

        String[] permissions = { android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.RECEIVE_SMS };
        int permissionsGranted = 0, rationalePermissions = 0;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted++;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                rationalePermissions++;
            }
        }
        if (permissionsGranted == permissions.length) {
            completeListener.onComplete();
            return;
        } else if(rationalePermissions == permissions.length) {
            completeListener.onComplete();
            return;
        } else {
            ActivityCompat.requestPermissions(activity, permissions, nextPermissionsRequestCode);
        }
    }

    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String permissions[],
                                           final @NonNull int[] grantResults) {
        if (completeListener != null) {
            completeListener.onComplete();
        }
    }
}
