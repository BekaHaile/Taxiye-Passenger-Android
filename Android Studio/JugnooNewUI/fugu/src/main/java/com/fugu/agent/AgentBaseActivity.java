package com.fugu.agent;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.fugu.R;
import com.fugu.database.CommonData;

/**
 * Created by gurmail on 18/06/18.
 *
 * @author gurmail
 */

public class AgentBaseActivity extends AppCompatActivity {

    private static final String TAG = AgentBaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                registerReceiver(new FuguNetworkStateReceiver(),
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                FuguLog.e(TAG, "Error in broadcasting");
            }
        }*/
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
