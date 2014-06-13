package product.clicklabs.jugnoo;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		
		
		String s1 = "Jugnoo";
		String s2 = " helps you provide a friendly and ";
		String s3 = "affordable";
		String s4 = " ride within the ";
		String s5 = "Chandigarh";
		String s6 = " tricity area. \nPeople with cars can offer ";
		String s7 = "rides";
		String s8 = " to users who find ";
		String s9 = "taxis";
		String s10 = " a bit too expensive. \nThe rates are affordable, and definitely a good deal when you get them for a friendly ride.";
		
		
		
		
		Spannable span1 = new SpannableString(s1);
		span1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		Spannable span3 = new SpannableString(s3);
		span3.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		Spannable span5 = new SpannableString(s5);
		span5.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		Spannable span7 = new SpannableString(s7);
		span7.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span7.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		Spannable span9 = new SpannableString(s9);
		span9.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span9.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		about1Text.setText("");
		
		about1Text.append(span1);
		about1Text.append(s2);
		about1Text.append(span3);
		about1Text.append(s4);
		about1Text.append(span5);
		about1Text.append(s6);
		about1Text.append(span7);
		about1Text.append(s8);
		about1Text.append(span9);
		about1Text.append(s10);
		
		
		
		
//		Jugnoo helps you provide a friendly and affordable ride within the Chandigarh tricity area.
//	    \n\nPeople with cars can offer rides to users who find taxis a bit too expensive.
//	    \n\nThe rates are affordable, and definitely a good deal when you get them for a friendly ride.\n
		
		
		
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
