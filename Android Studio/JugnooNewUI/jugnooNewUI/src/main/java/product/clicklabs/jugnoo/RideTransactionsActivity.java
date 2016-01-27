package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.datastructure.UpdateRideTransaction;
import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;


public class RideTransactionsActivity extends BaseFragmentActivity implements UpdateRideTransaction, FlurryEventNames {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;

    RelativeLayout relativeLayoutContainer;

    public static UpdateRideTransaction updateRideTransaction;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides_transactions);

        updateRideTransaction = this;

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new RideTransactionsFragment(), RideTransactionsFragment.class.getName())
                .addToBackStack(RideTransactionsFragment.class.getName())
                .commitAllowingStateLoss();
		setTitle(getResources().getString(R.string.ride_history));

	}

	public RelativeLayout getContainer(){
		return relativeLayoutContainer;
	}

	public void openRideSummaryFragment(int engagementId){
		getSupportFragmentManager().beginTransaction()
				.add(relativeLayoutContainer.getId(),
						new RideSummaryFragment(engagementId, RideSummaryFragment.OpenMode.FROM_MENU),
						RideTransactionsFragment.class.getName())
				.addToBackStack(RideTransactionsFragment.class.getName())
				.commitAllowingStateLoss();
	}

	public void setTitle(String title){
		textViewTitle.setText(title);
	}
	
	public void performBackPressed(){
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		} else {
			super.onBackPressed();
			setTitle(getResources().getString(R.string.ride_history));
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
	
	

    @Override
    public void updateRideTransaction(int position) {
        try {
//            if (rideInfosList.size() > 0) {
//                rideInfosList.get(position).isRatedBefore = 1;
//            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	
}
