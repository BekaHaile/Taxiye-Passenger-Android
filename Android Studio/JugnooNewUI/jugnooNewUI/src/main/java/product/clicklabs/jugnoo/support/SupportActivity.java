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
import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.support.fragments.SupportMainFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class SupportActivity extends BaseFragmentActivity implements FlurryEventNames {

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack, imageViewInvoice;
	
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
		imageViewInvoice = (ImageView) findViewById(R.id.imageViewInvoice);


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
		imageViewInvoice.setVisibility(View.GONE);
	}

	public void setImageViewInvoiceVisibility(int visibility){
		imageViewInvoice.setVisibility(visibility);
	}

	public void setImageViewInvoiceOnCLickListener(OnClickListener onCLickListener){
		imageViewInvoice.setOnClickListener(onCLickListener);
	}



	public LinearLayout getContainer(){
		return linearLayoutContainer;
	}

	public void openRideSummaryFragment(EndRideData endRideData){
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(getContainer().getId(),
						new RideSummaryFragment(endRideData),
						RideTransactionsFragment.class.getName())
				.addToBackStack(RideTransactionsFragment.class.getName())
				.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
						.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commitAllowingStateLoss();
	}


	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
