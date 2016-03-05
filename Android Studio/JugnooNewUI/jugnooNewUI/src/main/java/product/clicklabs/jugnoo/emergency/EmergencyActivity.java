package product.clicklabs.jugnoo.emergency;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.fragments.EmergencyContactsFragment;
import product.clicklabs.jugnoo.emergency.fragments.EmergencyModeEnabledFragment;
import product.clicklabs.jugnoo.utils.ASSL;


public class EmergencyActivity extends BaseFragmentActivity {

    private final String TAG = EmergencyActivity.class.getSimpleName();

    public static final  int MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS = 5;
    public static final  int MAX_EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 3;
    public static int EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 3;

    RelativeLayout relative, relativeLayoutContainer;
    int mode;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());
        FlurryAgent.onEvent(EmergencyActivity.class.getSimpleName() + " started");
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

        mode = getIntent().getIntExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal());

        if(mode == EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal()) {
            String engagementId = getIntent().getStringExtra(Constants.KEY_ENGAGEMENT_ID);
            String driverId = getIntent().getStringExtra(Constants.KEY_DRIVER_ID);
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new EmergencyModeEnabledFragment(driverId, engagementId),
                            EmergencyModeEnabledFragment.class.getName())
                    .addToBackStack(EmergencyModeEnabledFragment.class.getName())
                    .commitAllowingStateLoss();
        }
        else if(mode == EmergencyActivityMode.EMERGENCY_CONTACTS.getOrdinal()){
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), new EmergencyContactsFragment(),
                            EmergencyContactsFragment.class.getName())
                    .addToBackStack(EmergencyContactsFragment.class.getName())
                    .commitAllowingStateLoss();
        }
        else if(mode == EmergencyActivityMode.SEND_RIDE_STATUS.getOrdinal()){
            String engagementId = getIntent().getStringExtra(Constants.KEY_ENGAGEMENT_ID);
            new FragTransUtils().openEmergencyContactsOperationsFragment(this, relativeLayoutContainer, engagementId,
                    ContactsListAdapter.ListMode.SEND_RIDE_STATUS);
        }

        setEmergencyContactsAllowedToAdd();


    }

    public static void setEmergencyContactsAllowedToAdd(){
        if(Data.emergencyContactsList != null){
            EMERGENCY_CONTACTS_ALLOWED_TO_ADD = MAX_EMERGENCY_CONTACTS_ALLOWED_TO_ADD - Data.emergencyContactsList.size();
            if(EMERGENCY_CONTACTS_ALLOWED_TO_ADD < 0){
                EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 0;
            }
        }
    }

    public RelativeLayout getContainer(){
        return relativeLayoutContainer;
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


    public enum EmergencyActivityMode{
        EMERGENCY_ACTIVATE(0),
        EMERGENCY_CONTACTS(1),
        SEND_RIDE_STATUS(2),
        CALL_CONTACTS(3)

        ;

        private int ordinal;

        EmergencyActivityMode(int ordinal){
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }
    }

}
