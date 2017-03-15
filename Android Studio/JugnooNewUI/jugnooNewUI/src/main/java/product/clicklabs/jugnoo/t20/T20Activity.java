package product.clicklabs.jugnoo.t20;

import android.os.Bundle;
import android.widget.LinearLayout;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.t20.fragments.GamePredictWebViewFragment;
import product.clicklabs.jugnoo.utils.ASSL;


public class T20Activity extends BaseFragmentActivity  {

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

		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		new ASSL(this, linearLayoutContainer, 1134, 720, false);


//		getSupportFragmentManager().beginTransaction()
//				.add(linearLayoutContainer.getId(), new T20ScheduleFragment(),
//						T20ScheduleFragment.class.getName())
//				.addToBackStack(T20ScheduleFragment.class.getName())
//				.commitAllowingStateLoss();

		getSupportFragmentManager().beginTransaction()
				.add(linearLayoutContainer.getId(), new GamePredictWebViewFragment(),
						GamePredictWebViewFragment.class.getName())
				.addToBackStack(GamePredictWebViewFragment.class.getName())
				.commitAllowingStateLoss();


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
//		DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
//				getResources().getString(R.string.quit_jugnoo_t20),
//				getResources().getString(R.string.ok),
//				getResources().getString(R.string.cancel),
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						performBackPressed();
//					}
//				},
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//
//					}
//				}, false, false);
		performBackPressed();
	}

	public LinearLayout getContainer(){
		return linearLayoutContainer;
	}


	@Override
	protected void onDestroy() {
        ASSL.closeActivity(linearLayoutContainer);
        System.gc();
		super.onDestroy();
	}
	
}
