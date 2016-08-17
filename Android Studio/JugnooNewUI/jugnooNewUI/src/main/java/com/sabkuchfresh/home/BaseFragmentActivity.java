package com.sabkuchfresh.home;

import android.support.v4.app.FragmentActivity;

/**
 * Created by clicklabs on 7/3/15.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

//    /**
//     * this holds the reference for the Otto Bus which we declared in LavocalApplication
//     */
//    protected Bus mBus;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mBus = ((MyApplication) getApplication()).getBus();
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeUtil.checkForAccessTokenChange(this);
    }

    @Override
    public void finish() {
        super.finish();
    }


}
