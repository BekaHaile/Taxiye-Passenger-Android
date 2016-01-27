package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;


public class RideSummaryActivity extends BaseFragmentActivity implements FlurryEventNames, Constants {

    RelativeLayout relative;
	RelativeLayout relativeLayoutContainer;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_summary);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

		relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

		try {
			Intent intent = getIntent();
			if(intent.hasExtra("engagement_id")){
				int engagementId = intent.getIntExtra("engagement_id", 0);
				if(0 == engagementId){
					throw new Exception();
				}
				else{
					getSupportFragmentManager().beginTransaction()
							.add(relativeLayoutContainer.getId(),
									new RideSummaryFragment(engagementId, RideSummaryFragment.OpenMode.FROM_MENU),
									RideSummaryFragment.class.getName())
							.addToBackStack(RideSummaryFragment.class.getName())
							.commitAllowingStateLoss();
				}
			}
			else if(intent.hasExtra(KEY_END_RIDE_DATA)){
				getSupportFragmentManager().beginTransaction()
						.add(relativeLayoutContainer.getId(),
								new RideSummaryFragment(-1, RideSummaryFragment.OpenMode.FROM_MENU),
								RideSummaryFragment.class.getName())
						.addToBackStack(RideSummaryFragment.class.getName())
						.commitAllowingStateLoss();
			}
			else{
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}


	}


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
