package product.clicklabs.jugnoo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.flurry.android.FlurryAgent;

import java.util.List;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class ShareActivity extends BaseActivity implements FlurryEventNames {
	
	RelativeLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;

	ImageView imageViewFacebook, imageViewWhatsapp, imageViewSMS, imageViewEmail;
	TextView textViewReferralCode, textViewReferralCaption, textViewCode, textViewMoreInfo, textViewDesc;
    WebView webViewReferralCaption;
	private Button buttonInvite;
    CallbackManager callbackManager;


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


        callbackManager = CallbackManager.Factory.create();
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);

		imageViewFacebook = (ImageView) findViewById(R.id.imageViewFacebook);
		imageViewWhatsapp = (ImageView) findViewById(R.id.imageViewWhatsapp);
		imageViewSMS = (ImageView) findViewById(R.id.imageViewSMS);
		imageViewEmail = (ImageView) findViewById(R.id.imageViewEmail);

        webViewReferralCaption = (WebView) findViewById(R.id.webViewReferralCaption);
		textViewReferralCode = (TextView) findViewById(R.id.textViewReferralCode); textViewReferralCode.setTypeface(Fonts.latoRegular(this));
        textViewReferralCaption = (TextView) findViewById(R.id.textViewReferralCaption); textViewReferralCaption.setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewShare)).setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		textViewDesc = (TextView)findViewById(R.id.textViewDesc);textViewDesc.setTypeface(Fonts.latoLight(this));
		textViewMoreInfo = (TextView)findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.latoRegular(this));

		textViewCode = (TextView)findViewById(R.id.textViewCode);textViewCode.setTypeface(Fonts.latoRegular(this));
		buttonInvite = (Button)findViewById(R.id.buttonInvite);

        webViewReferralCaption.getSettings().setJavaScriptEnabled(true);
        webViewReferralCaption.getSettings().setDomStorageEnabled(true);
        webViewReferralCaption.getSettings().setDatabaseEnabled(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW, R.id.imageViewLogo);
        params.setMargins((int) (ASSL.Xscale() * 20.0f), (int) (ASSL.Yscale() * 20.0f), (int) (ASSL.Xscale() * 20.0f), (int) (ASSL.Yscale() * 20.0f));


		
		try {
			if(Data.referralMessages.referralMessage.contains(Data.userData.referralCode)){
				String strPre = Data.referralMessages.referralMessage.split(Data.userData.referralCode)[0];
				String strPost = Data.referralMessages.referralMessage.split(Data.userData.referralCode)[1];
				textViewCode.setText(Data.userData.referralCode);
				textViewDesc.setText(Data.referralMessages.referralShortMessage);
				Log.v("length of short message", "--> "+textViewDesc.getText().length());
				
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

			try {
				Log.e("Data.userData.jugnooFbBanner=", "="+Data.userData.jugnooFbBanner);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogPopup.alertPopupWithListener(ShareActivity.this, "", Data.referralMessages.referralMessage, new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
			}
		});
		
		
		buttonInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*try {
					Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Content to share");
					PackageManager pm = getApplicationContext().getPackageManager();
					List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
					for (final ResolveInfo app : activityList) {
						if ((app.activityInfo.name).contains("facebook")) {
							final ActivityInfo activity = app.activityInfo;
							final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
							shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
							shareIntent.setComponent(name);
							startActivity(shareIntent);
							break;
						}
						else{
							Intent share = new Intent(Intent.ACTION_SEND);
							share.setType("text/plain");
							share.putExtra(Intent.EXTRA_TEXT, Data.userData.referralCode);
							//share.putExtra(Intent.EXTRA_TEXT, "http://www.jugnoo.in");
							startActivity(Intent.createChooser(share, "Share Text"));
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}*/

//				String shareBody = "app string text more text! Get the app at http://jugnoo.in";
				String shareBody = Data.userData.referralCode + " http://share.jugnoo.in";
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				PackageManager pm = view.getContext().getPackageManager();
				List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, 0);
				for(final ResolveInfo app : activityList) {
					Log.i("ShareActivity", "app.actinfo.name: " + app.activityInfo.name);
					//if((app.activityInfo.name).contains("facebook")) {
					if("com.facebook.katana.ShareLinkActivity".equals(app.activityInfo.name)) {
						Log.v("facebook","facebook sdk called");
						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, Data.userData.referralCode);
						sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
						//startActivity(Intent.createChooser(sharingIntent, "Share idea"));
						break;
					} else {
						sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "app name");
						sharingIntent.putExtra(android.content.Intent.EXTRA_TITLE, "JUgnoo");
						sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
						//startActivity(Intent.createChooser(sharingIntent, "Share"));
						//break;
					}
				}
				startActivity(Intent.createChooser(sharingIntent, "Share"));
			}
		});
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});


		
		imageViewFacebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(AppStatus.getInstance(ShareActivity.this).isOnline(ShareActivity.this)) {
					ReferralActions.shareToFacebook(ShareActivity.this, callbackManager);
					FlurryEventLogger.event(INVITE_FACEBOOK);
				} else{
					DialogPopup.alertPopup(ShareActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});
		
		
		imageViewWhatsapp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(AppStatus.getInstance(ShareActivity.this).isOnline(ShareActivity.this)) {
					ReferralActions.shareToWhatsapp(ShareActivity.this);
					FlurryEventLogger.event(INVITE_WHATSAPP);
				} else{
					DialogPopup.alertPopup(ShareActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});
		
		
		imageViewSMS.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(AppStatus.getInstance(ShareActivity.this).isOnline(ShareActivity.this)) {
					ReferralActions.sendSMSIntent(ShareActivity.this);
					FlurryEventLogger.event(INVITE_MESSAGE);
				} else{
					DialogPopup.alertPopup(ShareActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});

		imageViewEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(AppStatus.getInstance(ShareActivity.this).isOnline(ShareActivity.this)) {
					ReferralActions.openMailIntent(ShareActivity.this);
					FlurryEventLogger.event(INVITE_EMAIL);
				} else{
					DialogPopup.alertPopup(ShareActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});

		
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
            callbackManager.onActivityResult(requestCode, resultCode, data);
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
