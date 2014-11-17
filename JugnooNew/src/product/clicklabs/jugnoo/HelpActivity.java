package product.clicklabs.jugnoo;

import java.text.DecimalFormat;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(HelpActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		about1Text = (TextView) findViewById(R.id.about1Text); about1Text.setTypeface(Data.regularFont(getApplicationContext()));
		about2Text = (TextView) findViewById(R.id.about2Text); about2Text.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		about3Text = (TextView) findViewById(R.id.about3Text); about3Text.setTypeface(Data.regularFont(getApplicationContext()));
		
		
		
		String str1 = getResources().getString(R.string.about_1_text) + "\nTo call a Jugnoo press the blue button on the map screen.";
		
		about1Text.setText(str1);
		
		
		//To call a Jugnoo press the blue button on the map screen
		
		
		String str3 = getResources().getString(R.string.about_2_text);
		
		about2Text.setText(str3);
		
		String str4 = getResources().getString(R.string.about_3_text);
		
		//First 2 kms: Rs.30\nAfter 2 kms: Rs. 10/km\nWaiting time: Rs.1.5/min after 15 minutes.
		//Operation Hours: 7 am - 9 pm currently
		try{
			DecimalFormat decimalFormat = new DecimalFormat("#");
			String str = "Operation Hours: 7 am - 9 pm currently \nFirst "+decimalFormat.format(HomeActivity.fareThresholdDistance)+" kms: Rs."+decimalFormat.format(HomeActivity.fareFixed)+"\nAfter "
			+decimalFormat.format(HomeActivity.fareThresholdDistance)+" kms:" + " Rs. "+decimalFormat.format(HomeActivity.farePerKm)+"/km\nWaiting time: Rs.1.5/min after 15 minutes.";
			about3Text.setText(str);
		} catch(Exception e){
			e.printStackTrace();
			about3Text.setText(str4);
		}
		
		
		
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
	}
	
	
	public void openMailIntentToSupport(){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@jugnoo.in" });
		email.putExtra(Intent.EXTRA_SUBJECT, "Jugnoo Support");
		email.putExtra(Intent.EXTRA_TEXT, "");
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client:"));
	}
	
	public void openCallIntent(String phoneNumber){
		Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
	}
	
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	

}
