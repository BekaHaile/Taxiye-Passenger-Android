package product.clicklabs.jugnoo.t20;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.t20.fragments.T20WebViewFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
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
		

//		getSupportFragmentManager().beginTransaction()
//				.add(linearLayoutContainer.getId(), new T20ScheduleFragment(),
//						T20ScheduleFragment.class.getName())
//				.addToBackStack(T20ScheduleFragment.class.getName())
//				.commitAllowingStateLoss();

		getSupportFragmentManager().beginTransaction()
				.add(linearLayoutContainer.getId(), new T20WebViewFragment(),
						T20WebViewFragment.class.getName())
				.addToBackStack(T20WebViewFragment.class.getName())
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
		DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
				getResources().getString(R.string.quit_jugnoo_t20),
				getResources().getString(R.string.ok),
				getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						performBackPressed();
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, false, false);
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
