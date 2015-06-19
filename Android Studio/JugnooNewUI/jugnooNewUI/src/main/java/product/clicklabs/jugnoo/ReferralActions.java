package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by socomo20 on 6/19/15.
 */
public class ReferralActions {



    public static void shareToFacebook(final Activity activity){
        new FacebookLoginHelper().openFacebookSession(activity, new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone() {
                if(Data.userData != null){
                    new FacebookLoginHelper().publishFeedDialog(activity,
                        "Jugnoo Autos - Autos on demand",
                        Data.referralMessages.fbShareCaption,
                        Data.referralMessages.fbShareDescription,
                        "https://jugnoo.in",
                        Data.userData.jugnooFbBanner);
                }
            }
        }, false);
    }


    public static void shareToWhatsapp(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = Data.referralMessages.referralSharingMessage;

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            Log.d("info", "=" + info);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            activity.startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }


    public static void sendSMSIntent(Activity activity){
        try {
            Uri sms_uri = Uri.parse("smsto:");
            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sms_intent.putExtra("sms_body", Data.referralMessages.referralSharingMessage);
            activity.startActivity(sms_intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openMailIntent(Activity activity){
        try {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
            email.putExtra(Intent.EXTRA_SUBJECT, Data.referralMessages.referralEmailSubject);
            email.putExtra(Intent.EXTRA_TEXT, Data.referralMessages.referralSharingMessage);
            email.setType("message/rfc822");
            activity.startActivity(Intent.createChooser(email, "Choose an Email client:"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
