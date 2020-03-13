package product.clicklabs.jugnoo.support;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.fragments.RideSummaryFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.fragments.SupportMainFragment;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.support.models.SupportCategory;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class SupportActivity extends BaseAppCompatActivity {

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack, imageViewInvoice;
	
	private LinearLayout linearLayoutContainer;
	public int fromBadFeedback = 0;
	private EndRideData endRideData;
	private HistoryResponse.Datum datum;
	private ArrayList<ShowPanelResponse.Item> items;
	private int engagementId = -1, orderId = -1, productType = ProductType.NOT_SURE.getOrdinal();

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

		if(getIntent().hasExtra(Constants.INTENT_KEY_FROM_BAD)){
			fromBadFeedback = getIntent().getExtras().getInt(Constants.INTENT_KEY_FROM_BAD, -1);
			engagementId = getIntent().getExtras().getInt(Constants.KEY_ENGAGEMENT_ID, -1);
			orderId = getIntent().getExtras().getInt(Constants.KEY_ORDER_ID, -1);
			productType = getIntent().getExtras().getInt(Constants.KEY_PRODUCT_TYPE, ProductType.NOT_SURE.getOrdinal());
		}

		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		imageViewInvoice = (ImageView) findViewById(R.id.imageViewInvoice);

		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_SUPPORT);
		//textViewTitle.getPaint().setShader(FeedUtils.textColorGradient(this, textViewTitle));

		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		if(fromBadFeedback == 0) {
			getSupportFragmentManager().beginTransaction()
					.add(linearLayoutContainer.getId(), new SupportMainFragment(), SupportMainFragment.class.getName())
					.addToBackStack(SupportMainFragment.class.getName())
					.commitAllowingStateLoss();
		} else{
			ProductType pt = ProductType.NOT_SURE;
			int supportCategory = SupportCategory.NOT_SURE.getOrdinal();
			if(productType == ProductType.AUTO.getOrdinal()){
				pt = ProductType.AUTO;
				supportCategory = EngagementStatus.ENDED.getOrdinal();
			} else if(productType == ProductType.FRESH.getOrdinal()){
				pt = ProductType.FRESH;
			} else if(productType == ProductType.MEALS.getOrdinal()){
				pt = ProductType.MEALS;
			} else if(productType == ProductType.GROCERY.getOrdinal()){
				pt = ProductType.GROCERY;
			} else if(productType == ProductType.MENUS.getOrdinal()){
				pt = ProductType.MENUS;
			} else if(productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()){
				pt = ProductType.DELIVERY_CUSTOMER;
			} else if(productType == ProductType.PAY.getOrdinal()){
				pt = ProductType.PAY;
			}
			getRideSummaryAPI(this, pt, supportCategory); //for bad feedback case (thumbs down)
			setTitle(getResources().getString(R.string.support_ride_issues_title));
		}


	}

	
	public void performBackPressed(){
		Utils.hideSoftKeyboard(this, linearLayoutContainer);
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

	public SupportMainFragment getSupportMainFragment(){
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(SupportMainFragment.class.getName());
		if(fragment instanceof SupportMainFragment){
			return (SupportMainFragment) fragment;
		}
		return null;
	}

	public void openRideSummaryFragment(EndRideData endRideData, boolean rideCancelled, int autosStatus){
		if(!new TransactionUtils().checkIfFragmentAdded(this, RideSummaryFragment.class.getName())) {
			getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(getContainer().getId(),
							RideSummaryFragment.newInstance(Integer.parseInt(endRideData.engagementId), endRideData, rideCancelled, autosStatus),
							RideSummaryFragment.class.getName())
					.addToBackStack(RideSummaryFragment.class.getName())
					.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
							.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openSupportRideIssuesFragment(int autosStatus){
		try {
			int engagementId = -1;
			int orderId = -1;
			try{engagementId = Integer.parseInt(endRideData.engagementId);} catch (Exception e){}
			try{orderId = datum.getOrderId();} catch (Exception e){}
			if ((endRideData != null || datum != null) && items != null) {
				new TransactionUtils().openRideIssuesFragment(this,
						getContainer(),
						engagementId, orderId, endRideData, items, fromBadFeedback, false, autosStatus,
						datum, -1, -1, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	@Override
	protected void onDestroy() {
		if(relative != null) {
			ASSL.closeActivity(relative);
		}
        System.gc();
		super.onDestroy();
	}

	public void getRideSummaryAPI(final Activity activity, final ProductType productType, final int supportCategory) {
		try {
			new ApiGetRideSummary(activity, Data.userData.accessToken, engagementId, orderId, Data.autoData.getFareStructure().getFixedFare(),
					new ApiGetRideSummary.Callback() {
						@Override
						public void onSuccess(EndRideData endRideData, HistoryResponse.Datum datum, ArrayList<ShowPanelResponse.Item> items) {
							SupportActivity.this.endRideData = endRideData;
							SupportActivity.this.datum = datum;
							SupportActivity.this.items = items;

							if(fromBadFeedback == 1){
								openSupportRideIssuesFragment(EngagementStatus.ENDED.getOrdinal());
							} else{
								getSupportMainFragment().updateSuccess();
							}
						}

						@Override
						public boolean onActionFailed(String message) {
							if(fromBadFeedback != 1) {
								getSupportMainFragment().updateFail();
							}
							return false;
						}

						@Override
						public void onFailure() {
							if(fromBadFeedback != 1) {
								getSupportMainFragment().updateFail();
							}
						}

						@Override
						public void onRetry(View view) {
							getRideSummaryAPI(activity, productType, supportCategory);
						}

						@Override
						public void onNoRetry(View view) {

						}
					}).getRideSummaryAPI(supportCategory, productType, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public EndRideData getEndRideData() {
		return endRideData;
	}

	public HistoryResponse.Datum getDatum() {
		return datum;
	}

}
