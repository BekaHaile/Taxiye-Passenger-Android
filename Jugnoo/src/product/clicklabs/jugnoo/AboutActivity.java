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
		String s2 = " आप प्रदान में मदद करता है a friendly and ";
		String s3 = "affordable";
		String s4 = " ride within the ";
		String s5 = "Chandigarh";
		String s6 = " tricity area.";
		String s10 = "\nआप एक अनुकूल सवारी के लिए उन्हें मिलता है जब दरें सस्ती है, और निश्चित रूप से एक अच्छा सौदा कर रहे हैं.";
		
		
		
		
		Spannable span1 = new SpannableString(s1);
		span1.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		Spannable span3 = new SpannableString(s3);
		span3.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		Spannable span5 = new SpannableString(s5);
		span5.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, span5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		about1Text.setText("");
		
		about1Text.append(span1);
		about1Text.append(s2);
		about1Text.append(span3);
		about1Text.append(s4);
		about1Text.append(span5);
		about1Text.append(s6);
		about1Text.append(s10);
		
		String str1 = getResources().getString(R.string.about_1_text);
		
		about1Text.setText(str1);
		
		String str3 = getResources().getString(R.string.about_2_text);
		
		about2Text.setText(str3);
		
		String str4 = getResources().getString(R.string.about_3_text);
		
		about3Text.setText(str4);
		
//		Jugnoo helps you provide a friendly and affordable ride within the Chandigarh tricity area.
//	    \nThe rates are affordable, and definitely a good deal when you get them for a friendly ride.\n
		
		//First 2 kms: Rs.30\nAfter 2 kms: Rs. 10/km\nWaiting time: Rs.1.5/min after 15 minutes.
		
		
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
