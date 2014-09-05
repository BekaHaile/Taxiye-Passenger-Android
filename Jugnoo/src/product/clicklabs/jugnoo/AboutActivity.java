package product.clicklabs.jugnoo;

import java.text.DecimalFormat;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class AboutActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView about1Text, about2Text, about3Text;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(AboutActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		about1Text = (TextView) findViewById(R.id.about1Text); about1Text.setTypeface(Data.regularFont(getApplicationContext()));
		about2Text = (TextView) findViewById(R.id.about2Text); about2Text.setTypeface(Data.regularFont(getApplicationContext()), Typeface.BOLD);
		about3Text = (TextView) findViewById(R.id.about3Text); about3Text.setTypeface(Data.regularFont(getApplicationContext()));
		
		
		
		String str1 = getResources().getString(R.string.about_1_text);
		
		about1Text.setText(str1);
		
		String str3 = getResources().getString(R.string.about_2_text);
		
		about2Text.setText(str3);
		
		String str4 = getResources().getString(R.string.about_3_text);
		
		//First 2 kms: Rs.30\nAfter 2 kms: Rs. 10/km\nWaiting time: Rs.1.5/min after 15 minutes.
		
		try{
			DecimalFormat decimalFormat = new DecimalFormat("#");
			String str = "First "+decimalFormat.format(HomeActivity.fareThresholdDistance)+" kms: Rs."+decimalFormat.format(HomeActivity.fareFixed)+"\nAfter "
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
