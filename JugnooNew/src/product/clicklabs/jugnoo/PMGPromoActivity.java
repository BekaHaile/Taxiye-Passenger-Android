package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import rmn.androidscreenlibrary.ASSL;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class PMGPromoActivity extends FragmentActivity{
	
	LinearLayout relative;
	
	ImageView backBtn;
	
	TextView promoText, promoDeliverText, tncText, tncDescText;
	
	Button continueBtn;
	
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
		
		
		backBtn = (ImageView) findViewById(R.id.backBtn);
		
		continueBtn = (Button) findViewById(R.id.continueBtn); continueBtn.setTypeface(Data.regularFont(getApplicationContext()));
		
		promoText = (TextView) findViewById(R.id.promoText); promoText.setTypeface(Data.regularFont(getApplicationContext()));
		promoDeliverText = (TextView) findViewById(R.id.promoDeliverText); promoDeliverText.setTypeface(Data.regularFont(getApplicationContext()));
		
		tncText = (TextView) findViewById(R.id.tncText); tncText.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		tncDescText = (TextView) findViewById(R.id.tncDescText); tncDescText.setTypeface(Data.regularFont(getApplicationContext()));
		
		
		promoText.setText("It's vacation time. Show your love for your near and dear ones by gifting them a cake and a greeting card. Sit back and relax while Jugnoo delivers your order the very same day.");
		promoDeliverText.setText("\"To ab deliver karwao, Jugnoo Se!\"");
		
		tncText.setText("Terms and Conditions:");
		tncDescText.setText("1. Same day delivery valid only for delivery addresses in Chandigarh, Panchkula and Mohali." +
				"\n2. Same day delivery valid for orders received before 3 pm. Order after 3 pm would be delivered the next day.");
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		continueBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				CustomAppLauncher.launchApp(PMGPromoActivity.this, "product.clicklabs.postmygreetings");
				FlurryEventLogger.christmasScreenContinuePressed(Data.userData.accessToken);
			}
		});
		
		
	}
	
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
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

