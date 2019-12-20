package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.R;

/**
 * Created by socomo20 on 7/20/15.
 */
public class FacebookLoginHelper {

    private AccessToken accessToken;
    private Activity activity;
    private FacebookLoginCallback facebookLoginCallback;
    private FacebookUserData facebookUserData;
    private boolean fetchData = true;

    public FacebookLoginHelper(final Activity activity, CallbackManager callbackManager, FacebookLoginCallback facebookLoginCallback) {
        facebookUserData = null;
        this.activity = activity;
        this.facebookLoginCallback = facebookLoginCallback;


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                if (accessToken != null) {
                    if (accessToken.isExpired()) {
                        callOpenActiveSession();
                    } else {
                        AccessToken.setCurrentAccessToken(accessToken);
                        requestMe(accessToken, fetchData);
                    }
                }
            }

            @Override
            public void onCancel() {
                FacebookLoginHelper.this.facebookLoginCallback.facebookLoginError(activity.getString(R.string.login_cancelled));
            }

            @Override
            public void onError(FacebookException exception) {
                FacebookLoginHelper.this.facebookLoginCallback.facebookLoginError(activity.getString(R.string.error_in_fetching_facebook_info)+"\n" + exception.toString());
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
                requestMe(accessToken, true);
            }
        }
    }

    public void openFacebookSession(boolean fetchData) {
        this.fetchData = fetchData;
        accessToken = AccessToken.getCurrentAccessToken();
        calculateHashKey(BuildConfig.APPLICATION_ID);
        Log.i("accessToken", "=" + accessToken);
        if (accessToken == null) {
            callOpenActiveSession();
        } else {
            if (accessToken.isExpired()) {
                callOpenActiveSession();
            } else {
                requestMe(accessToken, fetchData);
            }
        }
    }


    private void callOpenActiveSession() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }


    private void requestMe(final AccessToken accessToken, boolean fetchData) {
        if(fetchData) {
            DialogPopup.showLoadingDialog(activity, activity.getString(R.string.loading));
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
                                String firstName;
                                if(object.has("name")){
                                    firstName = object.optString("name");
                                } else {
                                    firstName = object.optString("first_name");
                                }
                                String lastName = object.optString("last_name");
                                String userName = object.optString("user_name");

                                String userEmail = object.optString("email");
                                if (userEmail == null || "".equalsIgnoreCase(userEmail)) {
                                    if (userName != null && !"".equalsIgnoreCase(userName)) {
                                        userEmail = userName + "@facebook.com";
                                    } else {
                                        userEmail = fbId + "@facebook.com";
                                    }
                                }

                                facebookUserData = new FacebookUserData(accessToken.getToken(), fbId, firstName, lastName, userName, userEmail);
                                Log.e("facebookUserData", "=" + facebookUserData);
                                facebookLoginCallback.facebookLoginDone(facebookUserData);
                            } else {
                                facebookLoginCallback.facebookLoginError(activity.getString(R.string.error_in_fetching_facebook_info));
                            }
                        }
                    });
            request.executeAsync();
        }
        else{
            facebookUserData = new FacebookUserData(accessToken.getToken(), "", "", "", "", "");
            facebookLoginCallback.facebookLoginDone(facebookUserData);
        }
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

    private void calculateHashKey(String yourPackageName) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    yourPackageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }







    public static void logoutFacebook(){
        LoginManager.getInstance().logOut();
    }

}
