package com.jugnoo.pay.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jugnoo.pay.R;
import com.jugnoo.pay.services.RegistrationIntentService;
import com.jugnoo.pay.utils.AppConstants;


/**
 * Created by cl-macmini-38 on 18/05/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Dialog dialog;
    private AppCompatActivity appCompatActivity;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9001;
    BroadcastReceiver broadcastReceiver;
    int tokens;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appCompatActivity = this;
        broadCastReceiverSetup();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (isNetworkConnected()) {
            if (dialog != null)
                dialog.dismiss();

//            if (isGpsEnable()) {


//                if (dialog != null)
//                    dialog.dismiss();


//            } else {

//                showAlert(appCompatActivity, "Your GPS seems to be disabled ,please enable it ", "Ok", "gps");
//            }


        } else {

            showAlertNoInternet(appCompatActivity);

        }
     }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        CommonMethods.clearNotifications(this);
        Log.d("OnResumeBase", "no");

//        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
//                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        if (isNetworkConnected()) {


            if (dialog != null)
                dialog.dismiss();


        } else {

            showAlertNoInternet(appCompatActivity);

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showAlertNoInternet(final Activity context) {
        Button btnNoInternet;
        Toolbar mToolBar;
        TextView toolbarTitleTxt;
        ImageButton backBtn;

        try {
            if (dialog == null) {
                dialog = new Dialog(context, R.style.AppTheme);
                dialog.setContentView(R.layout.layout_no_internet_connection);
                //        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
//        dialog.getWindow().setAttributes(lp);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);


                btnNoInternet = (Button) dialog.findViewById(R.id.refresh_btn);


                btnNoInternet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isNetworkConnected()) {

                            showAlertNoInternet(context);

                        } else {

                            if (dialog != null) {
                                dialog.dismiss();

                            }
                        }


                    }
                });
            }


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setupParent(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView);
            }
        }
    }


    protected void hideSoftKeyboard() {

        try {

            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

            Log.d("tag", "exception");
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    private void broadCastReceiverSetup() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(AppConstants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    // textView.setText(getString(R.string.gcm_send_message));
                    Log.d("BaseA", "Main token" + sentToken);
                } else {
                    //  textView.setText(getString(R.string.token_error_message));


                }

            }
        };

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            Log.d("Main", "check google play");
            startService(intent);
        }

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d("BaseActivity", "This device is not supported.");

            }
            return false;
        }
        return true;
    }






}
