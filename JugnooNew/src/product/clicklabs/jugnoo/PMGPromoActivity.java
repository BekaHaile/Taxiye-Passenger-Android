package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.utils.CustomAppLauncher;
import rmn.androidscreenlibrary.ASSL;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

public class PMGPromoActivity extends FragmentActivity{
	
	LinearLayout relative;
	
	Button backBtn;
	
	Button proceedBtn;
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pmg_promo);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(PMGPromoActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		
		proceedBtn = (Button) findViewById(R.id.proceedBtn); proceedBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		proceedBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				CustomAppLauncher.launchApp(PMGPromoActivity.this, "product.clicklabs.postmygreetings");
			}
		});
		
		
	}
	
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}

