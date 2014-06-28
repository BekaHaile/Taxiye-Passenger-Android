//package product.clicklabs.jugnoo;
//
//import java.util.Arrays;
//
//import rmn.androidscreenlibrary.ASSL;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import com.facebook.FacebookException;
//import com.facebook.FacebookOperationCanceledException;
//import com.facebook.LoggingBehavior;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionLoginBehavior;
//import com.facebook.SessionState;
//import com.facebook.Settings;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.WebDialog;
//import com.facebook.widget.WebDialog.Builder;
//import com.facebook.widget.WebDialog.OnCompleteListener;
//
//public class SplashLogin1 extends Activity{
//	
//	Button facebookSignInBtn;
//	
//	LinearLayout relative;
//	
//
//	public static Session session;
//	
//	
//	public static boolean isSystemPackage(PackageInfo pkgInfo) {
//		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
//				: false;
//	}
//	
//	
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.splash_login);
//		
//		
//		relative = (LinearLayout) findViewById(R.id.relative);
//		new ASSL(SplashLogin1.this, relative, 1134, 720, false);
//		
//		facebookSignInBtn = (Button) findViewById(R.id.facebookSignInBtn); facebookSignInBtn.setTypeface(Data.regularFont(getApplicationContext()));
//		
//		
//		
//		facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				
//				if (!AppStatus.getInstance(SplashLogin1.this).isOnline(
//						SplashLogin1.this)) {
//					new DialogPopup().alertPopup(SplashLogin1.this, "", Data.CHECK_INTERNET_MSG);
//				} else {
//					Log.i(" connection", " connection");
//					session = new Session(SplashLogin1.this);
//					Session.setActiveSession(session);
//					Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
//
//					Session.OpenRequest openRequest = null;
//					openRequest = new Session.OpenRequest(SplashLogin1.this);
//					openRequest.setPermissions(Arrays.asList("email", "user_friends", "user_photos"));
//
//					try {
//						if (SplashLogin1.isSystemPackage(getPackageManager().getPackageInfo("com.facebook.katana", 0))) {
//							openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
//						} else {
//							openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
//						}
//					} catch (NameNotFoundException e) {
//						e.printStackTrace();
//					}
//
//					openRequest.setCallback(new Session.StatusCallback() {
//						@Override
//						public void call(Session session, SessionState state, Exception exception) {
//
//							if (session.isOpened()) {
//								Session.openActiveSession(SplashLogin1.this, true, new Session.StatusCallback() {
//											@Override
//											public void call(final Session session, SessionState state, Exception exception) {
//												Log.v("session.isOpened()", "" + session.isOpened());
//												Log.v("app id", "" + session.getApplicationId());
//												if (session.isOpened()) {
//													Log.e("heyyy", "Logged in..." + session.getAccessToken());
//
//													Data.fbAccessToken = session.getAccessToken();
//													Log.e("fbAccessToken===", "="+Data.fbAccessToken);
//											    	
//													
//													DialogPopup.showLoadingDialog(SplashLogin1.this, "Loading...");
//													
//
//													Request.executeMeRequestAsync(session,
//															new Request.GraphUserCallback() {
//																@Override
//																public void onCompleted(GraphUser user, Response response) { // fetching user data from FaceBook
//
//																	DialogPopup.dismissLoadingDialog();
//																	
//																	if (user != null) {
//																		Log.i("data", "username" + user.getName() + "fbid!" + user.getId() + " firstname "
//																						+ user.getFirstName() + " lastname " + user.getLastName() + "  ");
//																		Log.i("res", response.toString());
//																		Log.i("user", "User " + user);
//																		
//																		Data.fbId = user.getId();
//																		Data.fbFirstName = user.getFirstName();
//																		Data.fbLastName = user.getLastName();
//																		Data.fbUserName = user.getUsername();
//																		
//																		
//																		try {
//																			Data.fbUserEmail = ((String)user.asMap().get("email"));
//																			Log.e("Data.userEmail before","="+Data.fbUserEmail);
//																			if(Data.fbUserEmail == null && Data.fbUserName != null){
//																				Data.fbUserEmail = Data.fbUserName + "@facebook.com";
//																			}
//																		} catch (Exception e2) {
//																			e2.printStackTrace();
//																		}
//
//																		
//
//																		
//																		
//																		if(Data.fbUserName == null){
//																			Data.fbUserName = "";
//																		}
//																		
//																		if(Data.fbUserEmail == null){
//																			Data.fbUserEmail = "";
//																		}
//																		
//																		
//																		
//																		Log.e("Data.fbId","="+Data.fbId);
//																		Log.e("Data.fbFirstName","="+Data.fbFirstName);
//																		Log.e("Data.fbLastName","="+Data.fbLastName);
//																		Log.e("Data.fbUserName","="+Data.fbUserName);
//																		Log.e("Data.userEmail","="+Data.fbUserEmail);
//
//																		inviteFbFriend();
//																	}
//																	else{
//																		new DialogPopup().alertPopup(SplashLogin1.this, "Facebook Error", "Error in fetching information from Facebook.");
//																	}
//																	
//
//																}
//															});
//												}
//												else if (session.isClosed()) {
//													Log.e("heyy", "Logged out...");
//
//													DialogPopup.dismissLoadingDialog();
//												}
//											}
//										});
//
//							} else if (session.isClosed()) {
//								
//							}
//							
//							
//						}
//					});
//					session.openForRead(openRequest);
//				}
//				
//				
//				
//			}
//		});
//		
//		
//		
//		
//	}
//	
//public void inviteFbFriend(){
//		
//		Bundle parameters = new Bundle();
//		parameters.putString("message", "Download app now to get started. Available on Google Play Store and App Store");
//		parameters.putString("data", "Get from one place to another with ease.");
//		parameters.putString("link", "https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo");
//		
//
//		WebDialog.Builder builder = new Builder(this, Session.getActiveSession(), "apprequests", parameters);
//
//		builder.setOnCompleteListener(new OnCompleteListener() {
//
//		    @Override
//		    public void onComplete(Bundle values, FacebookException error) {
//		    	Log.e("values","="+values);
//		    	Log.e("error","="+error);
//		        if (error != null){
//		            if (error instanceof FacebookOperationCanceledException){
//		                Toast.makeText(SplashLogin1.this,"Request cancelled",Toast.LENGTH_SHORT).show();
//		            }
//		            else{
//		                Toast.makeText(SplashLogin1.this,"Network Error",Toast.LENGTH_SHORT).show();
//		            }
//		        }
//		        else{
//
//		            final String requestId = values.getString("request");
//		            if (requestId != null) {
//		            	new DialogPopup().alertPopup(SplashLogin1.this, "", "Friends invited successfully.");
//		            } 
//		            else {
//		                Toast.makeText(SplashLogin1.this,"Request cancelled",Toast.LENGTH_SHORT).show();
//		            }
//		        }                       
//		    }
//		});
//
//		WebDialog webDialog = builder.build();
//		webDialog.show();
//	        
//	        
//	}
//	
//	
//	
//	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		try {
//			super.onActivityResult(requestCode, resultCode, data);
//			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	
//}
