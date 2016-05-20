package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import com.facebook.CallbackManager;

import java.util.List;

import product.clicklabs.jugnoo.adapters.ShareIntentListAdapter;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Prefs;


/**
 * Created by socomo20 on 6/19/15.
 */
public class ReferralActions {

    public static FacebookLoginHelper facebookLoginHelper;
    public static void shareToFacebook(final Activity activity, CallbackManager callbackManager){
        facebookLoginHelper = new FacebookLoginHelper(activity, callbackManager, new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone(FacebookUserData facebookUserData) {
                try {
                    if(Data.userData != null){
                        new BranchMetricsUtils(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                            @Override
                            public void onBranchLinkCreated(String link) {
                                if(Data.userData != null) {
                                    facebookLoginHelper.publishFeedDialog("Jugnoo Autos - Autos on demand",
                                            Data.referralMessages.fbShareCaption,
                                            Data.referralMessages.fbShareDescription,
                                            link,
                                            Data.userData.jugnooFbBanner);

                                    //30.707810, 76.761957
                                    // ?pickup_lat=30.707810&pickup_lng=76.761957
                                    //http://share.jugnoo.in/m/7MPH22Lyln?deepindex=0
                                }
                            }

                            @Override
                            public void onBranchError(String error) {
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                            }
                        }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_FACEBOOK,
                                SPLabels.BRANCH_FACEBOOK_LINK,
                                Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                                Data.referralMessages.fbShareDescription, Data.userData.jugnooFbBanner,
                                Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                                Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void facebookLoginError(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
        facebookLoginHelper.openFacebookSession(false);
    }

    public static void shareToFacebookBasic(final Activity activity, CallbackManager callbackManager, final String link){
        facebookLoginHelper = new FacebookLoginHelper(activity, callbackManager, new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone(FacebookUserData facebookUserData) {
            }

            @Override
            public void facebookLoginError(String message) {
            }
        });
		if(Data.userData != null && Data.referralMessages != null) {
			facebookLoginHelper.publishFeedDialog("Jugnoo Autos - Autos on demand",
					Data.referralMessages.fbShareCaption,
					Data.referralMessages.fbShareDescription,
					link,
					Data.userData.jugnooFbBanner);
		}
    }


    public static void shareToWhatsapp(final Activity activity) {

        try {
            new BranchMetricsUtils(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    PackageManager pm = activity.getPackageManager();
                    try {
                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        String text = Data.referralMessages.referralSharingMessage;

                        PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                        Log.d("info", "=" + info);
                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, text + "\n"
                                + link);
                        activity.startActivity(Intent.createChooser(waIntent, "Share with"));
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(activity, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_WHATSAPP,
                    SPLabels.BRANCH_WHATSAPP_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.referralMessages.fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void sendSMSIntent(final Activity activity){
        try {
            new BranchMetricsUtils(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    Uri sms_uri = Uri.parse("smsto:");
                    Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                    sms_intent.putExtra("sms_body", Data.referralMessages.referralSharingMessage + "\n"
                            + link);
                    activity.startActivity(sms_intent);
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_SMS,
                    SPLabels.BRANCH_SMS_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.referralMessages.fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openMailIntent(final Activity activity){
        try {

            new BranchMetricsUtils(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                    email.putExtra(Intent.EXTRA_SUBJECT, Data.referralMessages.referralEmailSubject);
                    email.putExtra(Intent.EXTRA_TEXT, Data.referralMessages.referralSharingMessage + "\n"
                            + link); //
                    email.setType("message/rfc822");
                    activity.startActivity(Intent.createChooser(email, "Choose an Email client:"));
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_EMAIL,
                    SPLabels.BRANCH_EMAIL_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.referralMessages.fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openGenericShareIntent(final Activity activity, final CallbackManager callbackManager){
        try {
            new BranchMetricsUtils(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {

                    shareGeneric(activity, callbackManager,
                            Data.referralMessages.referralEmailSubject,
                            Data.referralMessages.referralSharingMessage + "\n" + link,
                            link);
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_GENERIC,
                    SPLabels.BRANCH_GENERIC_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.referralMessages.fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareGeneric(final Activity activity, final CallbackManager callbackManager,
                             final String subject, final String body, final String link) {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List<ResolveInfo> activities = activity.getPackageManager().queryIntentActivities(sendIntent, 0);
        sortApps(activities);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Share with...");
        final ShareIntentListAdapter adapter = new ShareIntentListAdapter(activity,
                R.layout.list_item_share_intent, activities.toArray());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResolveInfo info = (ResolveInfo) adapter.getItem(which);
                if (info.activityInfo.packageName.contains("com.facebook.katana")) {
                    shareToFacebookBasic(activity, callbackManager, link);
					FlurryEventLogger.event(activity, FlurryEventNames.WHO_CLICKED_ON_FACEBOOK);
                    FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up ", "Facebook");
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_VIA_FACEBOOK, null);
                }
				else if(info.activityInfo.packageName.contains("com.google.android.gm")
						|| info.activityInfo.packageName.contains("com.yahoo.mobile.client.android.mail")
						|| info.activityInfo.packageName.contains("com.microsoft.office.outlook")
						|| info.activityInfo.packageName.contains("com.google.android.apps.inbox")){
					Intent intent = new Intent(android.content.Intent.ACTION_SEND);
					intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.putExtra(Intent.EXTRA_TEXT, body);
					activity.startActivity(intent);
					FlurryEventLogger.event(activity, FlurryEventNames.WHO_CLICKED_ON_EMAIL);
                    FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up ", "Gmail");
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_VIA_EMAIL, null);
				}
				else if(info.activityInfo.packageName.contains("com.whatsapp")){
					Intent intent = new Intent(android.content.Intent.ACTION_SEND);
					intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, body);
					activity.startActivity(intent);
					FlurryEventLogger.event(activity, FlurryEventNames.WHO_CLICKED_ON_WHATSAPP);
                    FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up ", "WhatsApp");
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_VIA_WHATSAPP, null);
				}
				else {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    activity.startActivity(intent);
                    if(info.activityInfo.packageName.contains("com.twitter.android")){
                        FlurryEventLogger.event(activity, FlurryEventNames.WHO_CLICKED_ON_TWITTER);
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_VIA_TWITTER, null);
                    } else if(info.activityInfo.packageName.contains("com.android.mms")){
                        FlurryEventLogger.event(activity, FlurryEventNames.WHO_CLICKED_ON_SMS);
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_VIA_SMS, null);
                        FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up ", "SMS");
                    } else{
                        FlurryEventLogger.event(activity, FlurryEventNames.WHO_CLICKED_ON_OTHERS);
                        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_INVITE_VIA_OTHER, null);
                        FlurryEventLogger.eventGA(Constants.REFERRAL, "invite friends pop up ", "Other");
                    }
                }
            }
        });
        builder.create().show();
    }


    private static void sortApps(List<ResolveInfo> infos){
        try{
            //com.facebook.orca
            //com.twitter.android

            ResolveInfo fb = null, whatsapp = null, sms = null, mail = null, fbMsgr = null, twitter = null;
            for(int i=0; i<infos.size(); i++){
                ResolveInfo info = infos.get(i);
                if (info.activityInfo.packageName.contains("com.facebook.katana") && fb == null) {
                    fb = infos.remove(i);
                    i--;
                }
                else if((info.activityInfo.packageName.contains("com.google.android.gm")
                    || info.activityInfo.packageName.contains("com.yahoo.mobile.client.android.mail")
                    || info.activityInfo.packageName.contains("com.microsoft.office.outlook")
                    || info.activityInfo.packageName.contains("com.google.android.apps.inbox")) && mail == null){
                    mail = infos.remove(i);
                    i--;
                }
                else if(info.activityInfo.packageName.contains("com.whatsapp") && whatsapp == null){
                    whatsapp = infos.remove(i);
                    i--;
                }
                else if(info.activityInfo.packageName.contains("com.android.mms") && sms == null){
                    sms = infos.remove(i);
                    i--;
                }

                else if(info.activityInfo.packageName.contains("com.facebook.orca") && fbMsgr == null){
                    fbMsgr = infos.remove(i);
                    i--;
                }
                else if(info.activityInfo.packageName.contains("com.twitter.android") && twitter == null){
                    twitter = infos.remove(i);
                    i--;
                }
            }

            if(twitter != null){
                infos.add(0, twitter);
            }
            if(mail != null){
                infos.add(0, mail);
            }
            if(sms != null){
                infos.add(0, sms);
            }
            if(fbMsgr != null){
                infos.add(0, fbMsgr);
            }
            if(fb != null){
                infos.add(0, fb);
            }
            if(whatsapp != null){
                infos.add(0, whatsapp);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void incrementAppOpen(Context context){
        int lastAppOpen = Prefs.with(context).getInt(SPLabels.REFERRAL_APP_OPEN_COUNT, 0);
        if(lastAppOpen >= 100){
            lastAppOpen = 0;
        }
        lastAppOpen = lastAppOpen + 1;
        Prefs.with(context).save(SPLabels.REFERRAL_APP_OPEN_COUNT, lastAppOpen);
    }

    public static void resetAppOpen(Context context){
        Prefs.with(context).save(SPLabels.REFERRAL_APP_OPEN_COUNT, 0);
    }

    public static void incrementTransactionCount(Context context){
        int lastTransactionCount = Prefs.with(context).getInt(SPLabels.REFERRAL_TRANSACTION_COUNT, 0);
        if(lastTransactionCount >= 100){
            lastTransactionCount = 0;
        }
        lastTransactionCount = lastTransactionCount + 1;
        Prefs.with(context).save(SPLabels.REFERRAL_TRANSACTION_COUNT, lastTransactionCount);
    }

    public static void resetTransactionCount(Context context){
        Prefs.with(context).save(SPLabels.REFERRAL_TRANSACTION_COUNT, 0);
    }

    public static void updateOpenDate(Context context){
        long dateMillis = System.currentTimeMillis();
        Prefs.with(context).save(SPLabels.REFERRAL_OPEN_DATE_MILLIS, dateMillis);
    }

}
