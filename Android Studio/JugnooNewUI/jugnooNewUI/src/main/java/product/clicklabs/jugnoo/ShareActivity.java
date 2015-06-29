package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class ShareActivity extends Activity{
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	ImageView imageViewFacebook, imageViewWhatsapp, imageViewSMS, imageViewEmail;
	TextView textViewReferralCode, textViewReferralCaption;
    WebView webViewReferralCaption;


	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(ShareActivity.this, relative, 1134, 720, false);
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		imageViewFacebook = (ImageView) findViewById(R.id.imageViewFacebook);
		imageViewWhatsapp = (ImageView) findViewById(R.id.imageViewWhatsapp);
		imageViewSMS = (ImageView) findViewById(R.id.imageViewSMS);
		imageViewEmail = (ImageView) findViewById(R.id.imageViewEmail);

        webViewReferralCaption = (WebView) findViewById(R.id.webViewReferralCaption);
		textViewReferralCode = (TextView) findViewById(R.id.textViewReferralCode); textViewReferralCode.setTypeface(Fonts.latoRegular(this));
        textViewReferralCaption = (TextView) findViewById(R.id.textViewReferralCaption); textViewReferralCaption.setTypeface(Fonts.latoRegular(this));

        webViewReferralCaption.getSettings().setJavaScriptEnabled(true);
        webViewReferralCaption.getSettings().setDomStorageEnabled(true);
        webViewReferralCaption.getSettings().setDatabaseEnabled(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW, R.id.imageViewLogo);
        params.setMargins((int)(ASSL.Xscale()*20.0f), (int)(ASSL.Yscale()*20.0f), (int)(ASSL.Xscale()*20.0f), (int)(ASSL.Yscale()*20.0f));


		
		try {
			if(Data.referralMessages.referralMessage.contains(Data.userData.referralCode)){
				String strPre = Data.referralMessages.referralMessage.split(Data.userData.referralCode)[0];
				String strPost = Data.referralMessages.referralMessage.split(Data.userData.referralCode)[1];
				
				SpannableString sstr = new SpannableString(Data.userData.referralCode);
				final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
				final ForegroundColorSpan clrs = new ForegroundColorSpan(Color.parseColor("#FAA31C"));
				sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				sstr.setSpan(clrs, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				textViewReferralCode.setText("");
				textViewReferralCode.append(strPre);
				textViewReferralCode.append(sstr);
				textViewReferralCode.append(strPost);


                if(1 == Data.referralMessages.referralCaptionEnabled) {
                    webViewReferralCaption.setVisibility(View.VISIBLE);
                    textViewReferralCaption.setVisibility(View.GONE);
                    loadHTMLContent(Data.referralMessages.referralCaption);
                }
                else{
                    webViewReferralCaption.setVisibility(View.GONE);
                    textViewReferralCaption.setVisibility(View.VISIBLE);

                    SpannableString spanFriends = new SpannableString("friends");
                    spanFriends.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spanFriends.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString spanFreeRides = new SpannableString("Jugnoo Cash");
                    spanFreeRides.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spanFreeRides.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    textViewReferralCaption.setText("");
                    textViewReferralCaption.append("Invite ");
                    textViewReferralCaption.append(spanFriends);
                    textViewReferralCaption.append(" and\nearn ");
                    textViewReferralCaption.append(spanFreeRides);

                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});


		
		imageViewFacebook.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ReferralActions.shareToFacebook(ShareActivity.this);
				FlurryEventLogger.sharedViaFacebook(Data.userData.accessToken);
			}
		});
		
		
		imageViewWhatsapp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ReferralActions.shareToWhatsapp(ShareActivity.this);
				FlurryEventLogger.sharedViaWhatsapp(Data.userData.accessToken);
			}
		});
		
		
		imageViewSMS.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ReferralActions.sendSMSIntent(ShareActivity.this);
				FlurryEventLogger.sharedViaSMS(Data.userData.accessToken);
			}
		});

		imageViewEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ReferralActions.openMailIntent(ShareActivity.this);
				FlurryEventLogger.sharedViaEmail(Data.userData.accessToken);
			}
		});
		
		Log.e("Data.userData.jugnooFbBanner=", "="+Data.userData.jugnooFbBanner);
		
	}

    public void loadHTMLContent(String data) {
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webViewReferralCaption.loadDataWithBaseURL("", data, mimeType, encoding, "");
    }


	

	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void performbackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performbackPressed();
		super.onBackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}
