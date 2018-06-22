package product.clicklabs.jugnoo.emergency;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiEmergencyDisable;
import product.clicklabs.jugnoo.emergency.adapters.ContactsListAdapter;
import product.clicklabs.jugnoo.emergency.fragments.EmergencyContactsFragment;
import product.clicklabs.jugnoo.emergency.fragments.EmergencyModeEnabledFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;


public class EmergencyActivity extends BaseAppCompatActivity {

    private final String TAG = EmergencyActivity.class.getSimpleName();

    public static final  int MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS = 5;
    public static final  int MAX_EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 3;
    public static int EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 3;

    RelativeLayout relativeLayoutContainer;
    int mode;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
        new ASSL(this, relativeLayoutContainer, 1134, 720, false);

        mode = getIntent().getIntExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal());

        if(mode == EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal()) {
            String engagementId = getIntent().getStringExtra(Constants.KEY_ENGAGEMENT_ID);
            String driverId = getIntent().getStringExtra(Constants.KEY_DRIVER_ID);
            getSupportFragmentManager().beginTransaction()
                    .add(relativeLayoutContainer.getId(), EmergencyModeEnabledFragment.newInstance(driverId, engagementId),
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
        if(Data.userData.getEmergencyContactsList() != null){
            EMERGENCY_CONTACTS_ALLOWED_TO_ADD = MAX_EMERGENCY_CONTACTS_ALLOWED_TO_ADD - Data.userData.getEmergencyContactsList().size();
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
            if(mode == EmergencyActivityMode.EMERGENCY_ACTIVATE.getOrdinal()
                    && Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0) == 1){
                DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                        getString(R.string.are_you_sure_want_to_disable_emergency),
                        "", "", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String engagementId = getIntent().getStringExtra(Constants.KEY_ENGAGEMENT_ID);
                                disableEmergencyMode(engagementId);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }, false, false);
            } else {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
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
        ASSL.closeActivity(relativeLayoutContainer);
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

    public void disableEmergencyMode(final String engagementId){
        new ApiEmergencyDisable(this, new ApiEmergencyDisable.Callback() {
            @Override
            public void onSuccess() {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void onRetry(View view) {
                disableEmergencyMode(engagementId);
            }

            @Override
            public void onNoRetry(View view) {

            }
        }).emergencyDisable(engagementId);
    }

}
