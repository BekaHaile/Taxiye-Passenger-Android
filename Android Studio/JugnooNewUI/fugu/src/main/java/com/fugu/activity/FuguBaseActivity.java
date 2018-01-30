package com.fugu.activity;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fugu.BuildConfig;
import com.fugu.FuguConfig;
import com.fugu.R;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.receiver.FuguNetworkStateReceiver;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonResponse;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Created by rajatdhamija  14/12/17.
 */

public class FuguBaseActivity extends AppCompatActivity implements FuguAppConstant {
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uncaughtExceptionError();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                registerReceiver(new FuguNetworkStateReceiver(),
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                FuguLog.e(TAG, "Error in broadcasting");
            }
        }
    }

    /**
     * Uncaught Exception encountered
     */
    private void uncaughtExceptionError() {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                        //Do your own error handling here
                        FuguLog.e("unCaughtException paramThread", "---> " + paramThread.toString());
                        FuguLog.e("unCaughtException paramThrowable", "---> " + paramThrowable.toString());
                        StringWriter stackTrace = new StringWriter();
                        paramThrowable.printStackTrace(new PrintWriter(stackTrace));
                        FuguLog.e("unCaughtException stackTrace", "---> " + stackTrace);
                        System.err.println(stackTrace);
                        apiSendError(stackTrace.toString());
                    }
                });
    }

    /**
     * APi to send error messages to server
     *
     * @param logs log to be sent
     */
    public void apiSendError(String logs) {
        if (isNetworkAvailable()) {
            PackageInfo pInfo = null;
            try {
                pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            JSONObject error = new JSONObject();
            try {
                error.put("log", logs);
                if (pInfo != null) {
                    error.put("version", pInfo.versionCode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put(FuguAppConstant.APP_SECRET_KEY, FuguConfig.getInstance().getAppKey());
            params.put(FuguAppConstant.DEVICE_TYPE, ANDROID_USER);
            params.put(APP_VERSION, BuildConfig.VERSION_NAME);
            params.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(FuguBaseActivity.this));
            params.put(FuguAppConstant.ERROR, error.toString());

            RestClient.getApiInterface().sendError(params)
                    .enqueue(new ResponseResolver<CommonResponse>(FuguBaseActivity.this, false, true) {
                        @Override
                        public void success(CommonResponse commonResponse) {
                            FuguLog.v("success", commonResponse.toString());
                        }

                        @Override
                        public void failure(APIError error) {
                            FuguLog.v("failure", error.toString());
                        }
                    });
        }
    }

    /**
     * Check Network Connection
     *
     * @return boolean
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            // close this context and return to preview context (if there is any)
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Set toolbar data
     *
     * @param toolbar  toolbar instance
     * @param title    title to be displayed
     * @param subTitle subtitle to be displayed
     * @return action bar
     */
    public ActionBar setToolbar(Toolbar toolbar, String title, String subTitle) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(CommonData.getColorConfig().getFuguActionBarBg()));
            toolbar.setTitleTextColor(CommonData.getColorConfig().getFuguActionBarText());
            toolbar.setSubtitleTextColor(CommonData.getColorConfig().getFuguActionBarText());
            if (FuguConfig.getInstance().getHomeUpIndicatorDrawableId() != -1)
                ab.setHomeAsUpIndicator(FuguConfig.getInstance().getHomeUpIndicatorDrawableId());

            ab.setTitle(title);
            ab.setSubtitle(subTitle);

        }
        return getSupportActionBar();
    }

    /**
     * Set toolbar data
     *
     * @param toolbar toolbar instance
     * @param title   title to be displayed
     * @return action bar
     */
    public ActionBar setToolbar(Toolbar toolbar, String title) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(CommonData.getColorConfig().getFuguActionBarBg()));

            if (FuguConfig.getInstance().getHomeUpIndicatorDrawableId() != -1)
                ab.setHomeAsUpIndicator(FuguConfig.getInstance().getHomeUpIndicatorDrawableId());

            ab.setTitle("");

            toolbar.setTitleTextColor(CommonData.getColorConfig().getFuguActionBarText());

            ((TextView) toolbar.findViewById(R.id.tv_toolbar_name)).setText(title);
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_name)).setTextColor(CommonData.getColorConfig().getFuguActionBarText());
        }
        return getSupportActionBar();
    }

}
