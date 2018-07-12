package com.jugnoo.pay.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;


/**
 * Created by cl-macmini-38 on 18/05/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!HomeActivity.checkIfUserDataNull(this)){
            HomeActivity.checkForAccessTokenChange(this);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(LocaleHelper.onAttach(newBase,
                Prefs.with(newBase).getString(Constants.KEY_DEFAULT_LANG, newBase.getString(R.string.default_lang)))));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

}
