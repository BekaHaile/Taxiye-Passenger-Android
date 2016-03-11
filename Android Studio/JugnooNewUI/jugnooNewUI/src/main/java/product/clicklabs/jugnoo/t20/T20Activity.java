package product.clicklabs.jugnoo.t20;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.t20.fragments.T20ScheduleFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;


public class T20Activity extends BaseFragmentActivity implements FlurryEventNames {

	private RelativeLayout relative;

	private LinearLayout linearLayoutContainer;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_t20);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		

		getSupportFragmentManager().beginTransaction()
				.add(linearLayoutContainer.getId(), new T20ScheduleFragment(),
						T20ScheduleFragment.class.getName())
				.addToBackStack(T20ScheduleFragment.class.getName())
				.commitAllowingStateLoss();


		FlurryEventLogger.event(SUPPORT_MAIN_OPENED);
	}

	
	public void performBackPressed(){
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

	public LinearLayout getContainer(){
		return linearLayoutContainer;
	}


	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
