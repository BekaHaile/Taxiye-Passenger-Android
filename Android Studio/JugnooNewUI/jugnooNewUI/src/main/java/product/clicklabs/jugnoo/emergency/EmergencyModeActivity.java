package product.clicklabs.jugnoo.emergency;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.emergency.fragments.EmergencyModeEnabledFragment;
import product.clicklabs.jugnoo.utils.ASSL;


public class EmergencyModeActivity extends BaseFragmentActivity {

    RelativeLayout relative, relativeLayoutContainer;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());
        FlurryAgent.onEvent(EmergencyModeActivity.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_mode);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new EmergencyModeEnabledFragment(),
                        EmergencyModeEnabledFragment.class.getName())
                .addToBackStack(EmergencyModeEnabledFragment.class.getName())
                .commitAllowingStateLoss();

    }


    public void performBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

}
