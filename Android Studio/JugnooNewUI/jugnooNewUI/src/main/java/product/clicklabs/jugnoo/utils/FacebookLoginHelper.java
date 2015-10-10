package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.net.Uri;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by socomo20 on 7/20/15.
 */
public class FacebookLoginHelper {

    private AccessToken accessToken;
    private Activity activity;
    private FacebookLoginCallback facebookLoginCallback;
    private FacebookUserData facebookUserData;

    public FacebookLoginHelper(Activity activity, CallbackManager callbackManager, FacebookLoginCallback facebookLoginCallback) {
        facebookUserData = null;
        this.activity = activity;
        this.facebookLoginCallback = facebookLoginCallback;

        FacebookSdk.sdkInitialize(activity);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				accessToken = loginResult.getAccessToken();
				if (accessToken != null) {
					if (accessToken.isExpired()) {
						callOpenActiveSession();
					} else {
						AccessToken.setCurrentAccessToken(accessToken);
						requestMe(accessToken);
					}
				}
			}

			@Override
			public void onCancel() {
				FacebookLoginHelper.this.facebookLoginCallback.facebookLoginError("Login cancelled");
			}

			@Override
			public void onError(FacebookException exception) {
				FacebookLoginHelper.this.facebookLoginCallback.facebookLoginError("Error in fetching information from Facebook\n" + exception.toString());
			}
		});
    }


    public void openFacebookSession() {
        accessToken = AccessToken.getCurrentAccessToken();
        Log.i("accessToken", "=" + accessToken);
        if (accessToken == null) {
            callOpenActiveSession();
        } else {
            if (accessToken.isExpired()) {
                callOpenActiveSession();
            } else {
                requestMe(accessToken);
            }
        }
    }


    private void callOpenActiveSession() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email"));
    }


    private void requestMe(final AccessToken accessToken) {
		DialogPopup.showLoadingDialog(activity, "Loading...");
        GraphRequest request = GraphRequest.newMeRequest(
            accessToken,
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                    JSONObject object,
                    GraphResponse response) {
					DialogPopup.dismissLoadingDialog();

                    Log.e("object", "=" + object);
//                    {
//                        "id": "270452659807369",
//                        "first_name": "Shankar",
//                        "timezone": 5.5,
//                        "email": "singhshankar771@gmail.com",
//                        "verified": true,
//                        "name": "Shankar Singh",
//                        "locale": "en_US",
//                        "link": "https://www.facebook.com/app_scoped_user_id/270452659807369/",
//                        "last_name": "Singh",
//                        "gender": "male",
//                        "updated_time": "2014-04-25T04:25:00+0000"
//                    }

                    if (object != null) {
                        String fbId = object.optString("id");
                        String firstName = object.optString("first_name");
                        String lastName = object.optString("last_name");
                        String userName = object.optString("user_name");

                        String userEmail = object.optString("email");
                        if(userEmail == null || "".equalsIgnoreCase(userEmail)){
                            if(userName != null && !"".equalsIgnoreCase(userName)){
                                userEmail = userName + "@facebook.com";
                            }
                            else{
                                userEmail = fbId + "@facebook.com";
                            }
                        }

                        facebookUserData = new FacebookUserData(accessToken.getToken(), fbId, firstName, lastName, userName, userEmail);
                        Log.e("facebookUserData", "=" + facebookUserData);
                        facebookLoginCallback.facebookLoginDone(facebookUserData);
                    }
                    else{
                        facebookLoginCallback.facebookLoginError("Error in fetching information from Facebook");
                    }
                }
            });
        request.executeAsync();
    }


    public void publishFeedDialog(String name, String caption, String description, String link, String pictureLink){
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(name)
                .setContentDescription(caption + " " + description)
                .setContentUrl(Uri.parse(link))
                .setImageUrl(Uri.parse(pictureLink))
                .build();

            ShareDialog shareDialog = new ShareDialog(activity);
            shareDialog.show(linkContent);
        }
    }


    public void appInviteDialog(){
        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo";
        previewImageUrl = "http://graph.facebook.com/717496164959213/picture?width=160&height=160";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                .setApplinkUrl(appLinkUrl)
                .setPreviewImageUrl(previewImageUrl)
                .build();
            AppInviteDialog.show(activity, content);
        }
    }





    public static void logoutFacebook(){
        LoginManager.getInstance().logOut();
    }

}
