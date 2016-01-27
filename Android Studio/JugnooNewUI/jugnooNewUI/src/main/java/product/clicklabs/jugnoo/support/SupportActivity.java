package product.clicklabs.jugnoo.support;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.support.fragments.SupportMainFragment;
import product.clicklabs.jugnoo.support.fragments.SupportRideIssuesFragment;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class SupportActivity extends BaseFragmentActivity implements FlurryEventNames {

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack;
	
	private LinearLayout linearLayoutContainer;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		

		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		getSupportFragmentManager().beginTransaction()
				.add(linearLayoutContainer.getId(), new SupportMainFragment(), SupportMainFragment.class.getName())
				.addToBackStack(SupportMainFragment.class.getName())
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
		performBackPressed();
	}

	public void setTitle(String title){
		textViewTitle.setText(title);
	}

	public LinearLayout getContainer(){
		return linearLayoutContainer;
	}

	public void openRideIssuesFragment(int engagementId, EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse) {
		getSupportFragmentManager().beginTransaction()
				.add(getContainer().getId(),
						new SupportRideIssuesFragment(engagementId, endRideData, getRideSummaryResponse),
						SupportRideIssuesFragment.class.getName())
				.addToBackStack(SupportRideIssuesFragment.class.getName())
				.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
						.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commitAllowingStateLoss();
	}

	public void openRideTransactionsFragment(){
		getSupportFragmentManager().beginTransaction()
				.add(getContainer().getId(),
						new RideTransactionsFragment(),
						RideTransactionsFragment.class.getName())
				.addToBackStack(RideTransactionsFragment.class.getName())
				.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
						.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commitAllowingStateLoss();
		setTitle(getResources().getString(R.string.support_select_a_ride_title));
	}


	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
