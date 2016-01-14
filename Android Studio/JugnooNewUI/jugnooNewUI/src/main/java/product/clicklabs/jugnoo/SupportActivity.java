package product.clicklabs.jugnoo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;


public class SupportActivity extends BaseActivity implements FlurryEventNames {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	RelativeLayout relativeLayoutSendUsEmail, relativeLayoutCallUs, relativeLayoutFAQ, relativeLayoutFeedback;
	TextView textViewSendUsEmail, textViewCallUs, textViewFAQ, textViewFeedback;
	
	
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
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		relativeLayoutSendUsEmail = (RelativeLayout) findViewById(R.id.relativeLayoutSendUsEmail);
		relativeLayoutCallUs = (RelativeLayout) findViewById(R.id.relativeLayoutCallUs);
		relativeLayoutFAQ = (RelativeLayout) findViewById(R.id.relativeLayoutFAQ);
		relativeLayoutFeedback = (RelativeLayout) findViewById(R.id.relativeLayoutFeedback);
		
		textViewSendUsEmail = (TextView) findViewById(R.id.textViewSendUsEmail); textViewSendUsEmail.setTypeface(Fonts.mavenLight(this));
		textViewCallUs = (TextView) findViewById(R.id.textViewCallUs); textViewCallUs.setTypeface(Fonts.mavenLight(this));
		textViewFAQ = (TextView) findViewById(R.id.textViewFAQ); textViewFAQ.setTypeface(Fonts.mavenLight(this));
		textViewFeedback = (TextView) findViewById(R.id.textViewFeedback); textViewFeedback.setTypeface(Fonts.mavenLight(this));

		
		relativeLayoutSendUsEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@jugnoo.in" });
				email.putExtra(Intent.EXTRA_SUBJECT, "Jugnoo Support");
				email.putExtra(Intent.EXTRA_TEXT, "");
				email.setType("message/rfc822");
				try {
					startActivity(Intent.createChooser(email, "Choose an Email client:"));
                    FlurryEventLogger.event(SEND_EMAIL_SUPPORT);
					Log.i("Finished sending email...", "");
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		relativeLayoutCallUs.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        Utils.openCallIntent(SupportActivity.this, Config.getSupportNumber(SupportActivity.this));
                FlurryEventLogger.event(CALL_SUPPORT);
			}
		});
		
		relativeLayoutFAQ.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HelpParticularActivity.helpSection = HelpSection.FAQ;
				startActivity(new Intent(SupportActivity.this, HelpParticularActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FAQS_SUPPORT);
			}
		});

		relativeLayoutFeedback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SupportActivity.this, FeedbackActivity.class);
				intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.SUPPORT.getOrdinal());
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FEEDBACK_SUPPORT);
			}
		});
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
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
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
