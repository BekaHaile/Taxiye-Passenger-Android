package product.clicklabs.jugnoo;

import java.util.Arrays;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.Builder;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookLogin {

	private static Session session;
	
	public void openFacebookSession1(final Activity activity, final FacebookLoginCallback facebookLoginCallback){
		
		if (!AppStatus.getInstance(activity).isOnline(activity)) {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		} else {
			session = new Session(activity);
			Session.setActiveSession(session);
			Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);

			Session.OpenRequest openRequest = null;
			openRequest = new Session.OpenRequest(activity);
			openRequest.setPermissions(Arrays.asList("email", "user_friends", "user_photos"));

			try {
				if (SplashLogin.isSystemPackage(activity.getPackageManager().getPackageInfo("com.facebook.katana", 0))) {
					openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
				} else {
					openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			openRequest.setCallback(new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					Log.i("openRequest session", "="+session);
					Log.i("openRequest state", "="+state);
					Log.e("openRequest exception", "="+exception);
						Session.openActiveSession(activity, true, new Session.StatusCallback() {
									@SuppressWarnings("deprecation")
									@Override
									public void call(final Session session, SessionState state, Exception exception) {
										Log.i("openActiveSession session", "="+session);
										Log.i("openActiveSession state", "="+state);
										Log.e("openActiveSession exception", "="+exception);
										if (session.isOpened()) {
											Data.fbAccessToken = session.getAccessToken();
											Log.e("fbAccessToken===", "="+Data.fbAccessToken);
											DialogPopup.showLoadingDialog(activity, "Loading...");
											Request.executeMeRequestAsync(session,
													new Request.GraphUserCallback() {
														@Override
														public void onCompleted(GraphUser user, Response response) { // fetching user data from FaceBook
															DialogPopup.dismissLoadingDialog();
															if (user != null) {
																Log.i("res", "="+response);
																Log.i("user", "=" + user);
																
																Data.fbId = user.getId();
																Data.fbFirstName = user.getFirstName();
																Data.fbLastName = user.getLastName();
																Data.fbUserName = user.getUsername();
																
																try {
																	Data.fbUserEmail = ((String)user.asMap().get("email"));
																	Log.e("Data.userEmail before","="+Data.fbUserEmail);
																	if(Data.fbUserEmail == null && Data.fbUserName != null){
																		Data.fbUserEmail = Data.fbUserName + "@facebook.com";
																	}
																} catch (Exception e2) {
																	e2.printStackTrace();
																}
																
																if(Data.fbUserName == null){
																	Data.fbUserName = "";
																}
																
																if(Data.fbUserEmail == null){
																	Data.fbUserEmail = "";
																}
																
																facebookLoginCallback.facebookLoginDone();
															}
															else{
																new DialogPopup().alertPopup(activity, "Facebook Error", "Error in fetching information from Facebook.");
															}
														}
													});
										}
										else if (session.isClosed()) {
											Log.e("heyy", "Logged out...");
										}
									}
								});
				}
			});
			session.openForRead(openRequest);
		}
	
	}
	
	public void openFacebookSession(final Activity activity, final FacebookLoginCallback facebookLoginCallback){
		if (!AppStatus.getInstance(activity).isOnline(activity)) {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		} else {
			session = Session.getActiveSession();
			Log.i("session", "="+session);
			if(session == null){
				callOpenActiveSession(activity, facebookLoginCallback);
			}
			else{
				if(session.getState() == SessionState.OPENED || session.getState() == SessionState.OPENED_TOKEN_UPDATED){
					callRequestMeAsync(session, activity, facebookLoginCallback);
				}
				else{
					Session.setActiveSession(session);	
					session.closeAndClearTokenInformation();	
					callOpenActiveSession(activity, facebookLoginCallback);
				}
			}
			
			
		}
	
	}
	
	
	public void callOpenActiveSession(final Activity activity, final FacebookLoginCallback facebookLoginCallback){
		Session.openActiveSession(activity, true, new Session.StatusCallback() {
			@Override
			public void call(final Session session, SessionState state, Exception exception) {
				Log.i("openActiveSession session", "="+session);
				Log.i("openActiveSession state", "="+state);
				Log.e("openActiveSession exception", "="+exception);
				if(session.isOpened()){
					FacebookLogin.session = session;
					Session.setActiveSession(session);
					callRequestMeAsync(session, activity, facebookLoginCallback);
				}
			}
		});
	}
	
	public void callRequestMeAsync(Session session, final Activity activity, final FacebookLoginCallback facebookLoginCallback){
			Data.fbAccessToken = session.getAccessToken();
			Log.e("fbAccessToken===", "="+Data.fbAccessToken);
			DialogPopup.showLoadingDialog(activity, "Loading...");
			Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) { // fetching user data from FaceBook
						DialogPopup.dismissLoadingDialog();
						if (user != null) {
							Log.i("res", "="+response);
							Log.i("user", "=" + user);
							
							Data.fbId = user.getId();
							Data.fbFirstName = user.getFirstName();
							Data.fbLastName = user.getLastName();
							Data.fbUserName = user.getUsername();
							
							try {
								Data.fbUserEmail = ((String)user.asMap().get("email"));
								Log.e("Data.userEmail before","="+Data.fbUserEmail);
								if(Data.fbUserEmail == null && Data.fbUserName != null){
									Data.fbUserEmail = Data.fbUserName + "@facebook.com";
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							
							if(Data.fbUserName == null){
								Data.fbUserName = "";
							}
							
							if(Data.fbUserEmail == null){
								Data.fbUserEmail = "";
							}
							
							facebookLoginCallback.facebookLoginDone();
						}
						else{
							new DialogPopup().alertPopup(activity, "Facebook Error", "Error in fetching information from Facebook.");
						}
					}
				}).executeAsync();
		
	}
	
	
	
	public void openAppInviteDialog(final Activity activity){
		
		Bundle parameters = new Bundle();
		parameters.putString("message", "Download app now to get started. Available on Google Play Store and App Store");
		parameters.putString("data", "Get from one place to another with ease.");
		parameters.putString("link", "https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo");

		WebDialog.Builder builder = new Builder(activity, Session.getActiveSession(), "apprequests", parameters);

		builder.setOnCompleteListener(new OnCompleteListener() {

		    @Override
		    public void onComplete(Bundle values, FacebookException error) {
		    	Log.e("values","="+values);
		    	Log.e("error","="+error);
		        if (error != null){
		        }
		        else{
		            final String requestId = values.getString("request");
		            if (requestId != null) {
		            	Toast.makeText(activity, "Friends invited successfully.", Toast.LENGTH_SHORT).show();
		            } 
		            else {
		                Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
		            }
		        }                       
		    }
		});

		WebDialog webDialog = builder.build();
		webDialog.show();
	        
	}
	
	
}


interface FacebookLoginCallback{
	public void facebookLoginDone();
}
