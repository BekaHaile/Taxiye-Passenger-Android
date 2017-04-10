package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;

import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.fragments.RideTransactionsFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class RideTransactionsActivity extends BaseFragmentActivity implements GAAction {

    private final String TAG = RideTransactionsActivity.class.getSimpleName();

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack, imageViewInvoice;

    RelativeLayout relativeLayoutContainer;

	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
    }

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides_transactions);

		GAUtils.trackScreenView(HISTORY);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);

		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_HISTORY);
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		imageViewBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if(getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0) != 0){
			new TransactionUtils().openOrderStatusFragment(this, relativeLayoutContainer, getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0),
					getIntent().getIntExtra(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal()));
		} else {
			getSupportFragmentManager().beginTransaction()
					.add(relativeLayoutContainer.getId(), new RideTransactionsFragment(), RideTransactionsFragment.class.getName())
					.addToBackStack(RideTransactionsFragment.class.getName())
					.commitAllowingStateLoss();
		}



	}

	public void setTitle(String title){
		textViewTitle.setText(title);
	}

	public RelativeLayout getContainer(){
		return relativeLayoutContainer;
	}


	public void performBackPressed(){
		Utils.hideSoftKeyboard(this, relativeLayoutContainer);
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
		Data.isOrderCancelled = false;
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}


}
