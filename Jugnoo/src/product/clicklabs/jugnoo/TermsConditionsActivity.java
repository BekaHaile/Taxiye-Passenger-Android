package product.clicklabs.jugnoo;

import org.apache.http.Header;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TermsConditionsActivity extends Activity{
	
	LinearLayout relative;
	
	TextView textViewTitle, textViewTermsAndConditions;
	Button buttonAgree, buttonCancel;
	
	boolean termsAgreedApiDone = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_conditions_screen);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(TermsConditionsActivity.this, relative, 1134, 720, false);
		
		termsAgreedApiDone = false;
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.regularFont(getApplicationContext()));
		textViewTermsAndConditions = (TextView) findViewById(R.id.textViewTermsAndConditions); textViewTermsAndConditions.setTypeface(Data.regularFont(getApplicationContext()));
		
		buttonAgree = (Button) findViewById(R.id.buttonAgree); buttonAgree.setTypeface(Data.regularFont(getApplicationContext()));
		buttonCancel = (Button) findViewById(R.id.buttonCancel); buttonCancel.setTypeface(Data.regularFont(getApplicationContext()));
		
		buttonAgree.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				termsAgreedAsync(TermsConditionsActivity.this);
				
				SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				Editor editor = pref.edit();
				editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
				editor.putString(Data.SP_ID_KEY, Data.userData.id);
				editor.commit();
				
				startActivity(new Intent(TermsConditionsActivity.this, HomeActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
			}
		});
		
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		textViewTermsAndConditions.setText("");
		
		
		
		
		StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str1 = new SpannableString("THIS FOLLOWING USER AGREEMENT DESCRIBES THE TERMS AND CONDITIONS ON WHICH JUGNOO, INC. " +
				"OFFERS YOU ACCESS TO THE JUGNOO PLATFORM.");
		str1.setSpan(boldStyleSpan, 0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str1);
		
		
		
		
		
		String str2 = "\n\n" + 
				"Welcome to the user agreement (the \"Agreement\" or \"User Agreement\" or \"Terms of Service\") for JUGNOO (the \"JUGNOO Platform\")," +
				" an application owned and operated by SoCoMo Inc., a San Fransisco corporation, whose principal office is located at 114 Sansome Street, " +
				"San Francisco, CA 94104 . This Agreement is a legally binding agreement made between you (\"You,\" \"Your,\" or \"Yourself\") and " +
				"JUGNOO, Inc. (\"JUGNOO,\" \"We,\" \"Us\" or \"Our\")." + "\n" +
				"JUGNOO is willing to license, not sell, the JUGNOO Platform to You only upon the condition that You accept all the terms contained " +
				"in this Agreement. By signing up with or by using the JUGNOO Platform, You indicate that You understand this Agreement and accept all " +
				"of its terms. If You do not accept all the terms of this Agreement, then JUGNOO is unwilling to license the " +
				"JUGNOO Platform to You." + "\n" +
				"This paragraph applies to any version of the JUGNOO Platform that you acquire from the Apple App Store. This Agreement is entered into " +
				"between You and JUGNOO. Apple, Inc. (\"Apple\") is not a party to this Agreement and shall have no obligations with respect to the " +
				"JUGNOO Platform. JUGNOO, not Apple, is solely responsible for the JUGNOO Platform and the content thereof as set forth hereunder. " +
				"However, Apple and Apple's subsidiaries are third party beneficiaries of this Agreement. Upon Your acceptance of this Agreement, " +
				"Apple shall have the right (and will be deemed to have accepted the right) to enforce this Agreement against You as a third party " +
				"beneficiary thereof. This Agreement incorporates by reference the Licensed Application End User License Agreement published by Apple, " +
				"for purposes of which, You are \"the end-user.\" In the event of a conflict in the terms of the Licensed Application End User License " +
				"Agreement and this Agreement, the terms of this Agreement shall control." + "\n" +
				"The JUGNOO Platform provides a means to enable persons who seek transportation to certain destinations (\"Riders\") to be matched with " +
				"persons driving to or through those destinations (\"Drivers\"). For purposes of this Agreement these services shall collectively be " +
				"defined as the \"Services\". This Agreement describes the terms and conditions that will govern Your use of and participation in the " +
				"JUGNOO Platform." + "\n" +
				"Please read this Agreement carefully before using the Services. You must read, agree with and accept all of the terms and conditions " +
				"contained in this Agreement, which includes those terms and conditions expressly set out below and those incorporated by reference, " +
				"before You use any of the Services. By using any of the Services, You become a Participant in JUGNOO and a User of Services available " +
				"on the JUGNOO Platform (\"Participant\" or \"User\") and You agree to be bound by the terms and conditions of this Agreement with respect " +
				"to such Services." + "\n";
		textViewTermsAndConditions.append(str2);
		
		
		
		
		
		
		
		StyleSpan boldStyleSpan3 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str3 = new SpannableString("IF YOU DO NOT AGREE TO BE BOUND BY THE TERMS AND CONDITIONS OF THIS AGREEMENT, " +
				"PLEASE DO NOT USE OR ACCESS JUGNOO OR REGISTER FOR THE SERVICES PROVIDED ON JUGNOO.");
		str3.setSpan(boldStyleSpan3, 0, str3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str3);
		
		
		
		
		
		String str4 = "\n"+"We may amend this Agreement at any time by posting the amended terms on the JUGNOO Platform. If We post amended terms on the " +
				"JUGNOO Platform, You may not use the Services without accepting them. Except as stated below, all amended terms shall automatically be " +
				"effective after they are posted on the JUGNOO Platform. This Agreement may not be otherwise amended except in writing signed by You and JUGNOO."+"\n";
		textViewTermsAndConditions.append(str4);
		
		
		
		
		
		
		StyleSpan boldStyleSpan5 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str5 = new SpannableString("JUGNOO DOES NOT PROVIDE TRANSPORTATION SERVICES, AND JUGNOO IS NOT A TRANSPORTATION CARRIER. " +
				"IT IS UP TO THE DRIVER OR VEHICLE OPERATOR TO DECIDE WHETHER OR NOT TO OFFER A RIDE TO A RIDER CONTACTED THROUGH THE JUGNOO PLATFORM, " +
				"AND IT IS UP THE RIDER TO DECIDE WHETHER OR NOT TO ACCEPT A RIDE FROM ANY DRIVER CONTACTED THROUGH THE JUGNOO PLATFORM. ANY DECISION BY " +
				"A USER TO OFFER OR ACCEPT TRANSPORTATION ONCE SUCH USER IS MATCHED THROUGH THE JUGNOO PLATFORM IS A DECISION MADE IN SUCH USER'S SOLE DISCRETION. " +
				"JUGNOO OFFERS INFORMATION AND A METHOD TO CONNECT DRIVERS AND RIDERS WITH EACH OTHER, BUT DOES NOT AND DOES NOT INTEND TO PROVIDE " +
				"TRANSPORTATION SERVICES OR ACT IN ANY MANNER AS A TRANSPORTATION CARRIER, AND HAS NO RESPONSIBILITY OR LIABILITY FOR ANY TRANSPORTATION " +
				"SERVICES VOLUNTARILY PROVIDED TO ANY RIDER BY ANY DRIVER USING THE JUGNOO PLATFORM."+"\n\n\n");
		str5.setSpan(boldStyleSpan5, 0, str5.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str5);
		
		
		
		
		
		
		
		StyleSpan boldStyleSpan6 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str6 = new SpannableString("PAYMENTS"+"\n\nCharges:");
		str6.setSpan(boldStyleSpan6, 0, str6.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str6);
		
		
		
		
		
		
		
		String str7 = " As a Rider in all other markets that are not Donation Markets, You agree that any mandatory amounts charged " +
				"following a ride (a \"Charge\") are due immediately. JUGNOO reserves the right to determine pricing. In all markets, no " +
				"Charge or Donation may exceed five hundred rupees (Rs.500)."+"\n\n";
		textViewTermsAndConditions.append(str7);
		
		
		
		
		
		
		
		StyleSpan boldStyleSpan8 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str8 = new SpannableString("Cash only:");
		str8.setSpan(boldStyleSpan8, 0, str8.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str8);
		
		
		
		
		
		String str9 = " All Donations and Payments made by Passengers to Drivers shall only be made through the Cash." + "\n\n\n";
		textViewTermsAndConditions.append(str9);
		
		
		
		
		
		
		
		StyleSpan boldStyleSpan10 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str10= new SpannableString("ELIGIBILITY"+"\n");
		str10.setSpan(boldStyleSpan10, 0, str10.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str10);
		
		
		
		
		
		
		String str11 = "Our Services are available only to, and may only be used by individuals who can form legally binding contracts under " +
				"applicable law. Without limiting the foregoing, Our Services are not available to children (persons under the age of 18) or to " +
				"temporarily or indefinitely terminated Participants. By becoming a Participant, You represent and warrant that You are at least " +
				"18 years old. By using the JUGNOO Platform or the Services, You represent and warrant that You have the right, authority and " +
				"capacity to enter into this Agreement and to abide by the terms and conditions of this Agreement."+"\n"+
				"You are the sole authorized user of Your account. You are responsible for maintaining the confidentiality of any password " +
				"provided by You or JUGNOO for accessing the Services. You are solely and fully responsible for all activities that occur " +
				"under Your password or account. JUGNOO has no control over the use of any User's account and expressly disclaims any liability " +
				"derived therefrom. Should You suspect that any unauthorized party may be using Your password or account or You suspect any " +
				"other breach of security, You will contact Us immediately."+"\n\n\n";
		textViewTermsAndConditions.append(str11);
		
		
		
		
		
		
		
		
		
		
		StyleSpan boldStyleSpan12 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str12= new SpannableString("TERM AND TERMINATION"+"\n");
		str12.setSpan(boldStyleSpan12, 0, str12.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str12);
		
		
		
		
		
		
		
		String str13 = "This Agreement is effective upon use of the JUGNOO Platform or the Services for new Users and upon the posting " +
				"dates of any subsequent amendments to this Agreement for all current Users. You may terminate Your participation in the " +
				"Services at any time, for any reason upon receipt by Us of Your written or email notice of termination. Either You or We " +
				"may terminate Your participation in the JUGNOO Platform by removing Your Information at any time, for any or no reason, " +
				"without explanation, effective upon sending written or email notice to the other party. Upon such termination, We will remove " +
				"all of Your information from Our servers, though We may retain an archived copy of records We have about You as required " +
				"by law or for legitimate business purposes. We maintain sole discretion to bar Your use of the Services in the future, for " +
				"any or no reason. Even after Your participation in the JUGNOO Platform is terminated, this Agreement will remain in effect." + "\n\n\n";
		textViewTermsAndConditions.append(str13);
		
		
		
		
		
		
		
		StyleSpan boldStyleSpan14 = new StyleSpan(android.graphics.Typeface.BOLD);
		SpannableString str14= new SpannableString("YOUR INFORMATION"+"\n");
		str14.setSpan(boldStyleSpan14, 0, str14.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textViewTermsAndConditions.append(str14);
		
		
		
		
		String str15 = "Your Information is any information You provide, publish or display (\"post\") to the JUGNOO Platform or " +
				"send to other Users in the registration or in any public message area (including, but not limited to the feedback " +
				"section) or through any email feature (\"Your Information\"). Your Information will be stored on computers. You " +
				"consent to Us using Your Information to create a User account that will allow You.";
		textViewTermsAndConditions.append(str15);
		
		
		
		
	}
	
	
	
	public void termsAgreedAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			Log.i("access_token", "=" + Data.userData.accessToken);
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/agree_terms", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String response = new String(arg2);
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									termsAgreedApiDone = true;
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && termsAgreedApiDone){
			
			SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			Editor editor = pref.edit();
			editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
			editor.putString(Data.SP_ID_KEY, Data.userData.id);
			editor.commit();
			
			startActivity(new Intent(TermsConditionsActivity.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
		
	}
	
	
}
