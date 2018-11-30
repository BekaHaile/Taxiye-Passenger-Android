package com.fugu.agent;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugu.R;
import com.fugu.agent.fragment.BroadcastFragment;
import com.fugu.agent.helper.BroadcastListenerHelper;
import com.fugu.agent.listeners.BroadcastListener;

/**
 * Created by gurmail on 20/07/18.
 *
 * @author gurmail
 */

public class AgentBroadcastActivity extends AgentBaseActivity {

    private static final String TAG = AgentBroadcastActivity.class.getSimpleName();
    private Toolbar myToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_broadcast);
        initView();
    }

    private void initView() {

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar(myToolbar, "Select Teams");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_layout, new BroadcastFragment(), BroadcastFragment.class.getName())
                .addToBackStack(BroadcastFragment.class.getName())
                .commitAllowingStateLoss();

        LinearLayout layout = findViewById(R.id.llRoot);
        setupUI(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            //        e.printStackTrace();
        }
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

    public void updateToolBar(String title) {
        ((TextView) myToolbar.findViewById(R.id.tv_toolbar_name)).setText(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public BroadcastFragment getBroadcastFragment() {
        return (BroadcastFragment) getSupportFragmentManager().findFragmentByTag(BroadcastFragment.class.getName());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Method used to hide keyboard if outside touched.
     *
     * @param view
     */
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        //        e.printStackTrace();
                    }
                    return false;
                }

            });
        }
        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}
