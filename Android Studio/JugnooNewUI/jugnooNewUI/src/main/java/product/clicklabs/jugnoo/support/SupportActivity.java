package product.clicklabs.jugnoo.support;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.BaseFragmentActivity;
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
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class SupportActivity extends BaseFragmentActivity implements FlurryEventNames {

	private RelativeLayout relative;

	private TextView textViewTitle;
	private ImageView imageViewBack, imageViewInvoice;
	
	private LinearLayout linearLayoutContainer;
	public int fromBadFeedback = 0;
	private EndRideData endRideData;
	private HistoryResponse.Datum datum;
	private ArrayList<ShowPanelResponse.Item> items;

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
			fromBadFeedback = getIntent().getExtras().getInt(Constants.INTENT_KEY_FROM_BAD);
		}

		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		imageViewInvoice = (ImageView) findViewById(R.id.imageViewInvoice);

		textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_SUPPORT);
		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

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
			getRideSummaryAPI(this);
			setTitle(getResources().getString(R.string.support_ride_issues_title));
		}


		FlurryEventLogger.event(SUPPORT_MAIN_OPENED);
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
							new RideSummaryFragment(endRideData, rideCancelled, autosStatus),
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
			try{engagementId = Integer.parseInt(endRideData.engagementId);} catch (Exception e){}
			if ((endRideData != null || datum != null) && items != null) {
				new TransactionUtils().openRideIssuesFragment(this,
						getContainer(),
						engagementId, endRideData, items, fromBadFeedback, false, autosStatus,
						datum);
				FlurryEventLogger.eventGA(Constants.ISSUES, "Customer Support", "Issue with Ride");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}

	public void getRideSummaryAPI(final Activity activity) {
		try {
			new ApiGetRideSummary(activity, Data.userData.accessToken, -1, Data.autoData.getFareStructure().getFixedFare(),
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
							getRideSummaryAPI(activity);
						}

						@Override
						public void onNoRetry(View view) {

						}
					}).getRideSummaryAPI(EngagementStatus.ENDED.getOrdinal(), ProductType.NOT_SURE, false);
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
