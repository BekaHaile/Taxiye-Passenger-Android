package product.clicklabs.jugnoo.promotion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AppPackage;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.promotion.adapters.ShareIntentListAdapter;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by socomo20 on 6/19/15.
 */
public class ReferralActions  {

    public static FacebookLoginHelper facebookLoginHelper;
    public static void shareToFacebook(final Activity activity, final boolean isMessenger, final CallbackManager callbackManager){
        try {
            if(Data.userData != null){
                String channel = isMessenger ? BranchMetricsUtils.BRANCH_CHANNEL_FACEBOOK_MESSENGER : BranchMetricsUtils.BRANCH_CHANNEL_FACEBOOK;
                String channelLinkSP = isMessenger ? SPLabels.BRANCH_FACEBOOK_MESSENGER_LINK : SPLabels.BRANCH_FACEBOOK_LINK;
                BranchMetricsUtils.getBranchLinkForChannel(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                    @Override
                    public void onBranchLinkCreated(final String link) {
                        if(Data.userData != null) {
                            ArrayList<AppPackage> appPackages = new ArrayList<>();
                            appPackages.add(new AppPackage(0, "com.facebook.orca"));
                            Utils.checkAppsArrayInstall(activity, appPackages);
                            if (isMessenger && appPackages.get(0).getInstalled() == 1) {
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setPackage("com.facebook.orca");
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, Data.userData.getReferralMessages().referralSharingMessage + " " + link);
                                activity.startActivity(intent);
                            } else {

                                facebookLoginHelper = new FacebookLoginHelper(activity, callbackManager, new FacebookLoginCallback() {
                                    @Override
                                    public void facebookLoginDone(FacebookUserData facebookUserData) {
                                        facebookLoginHelper.publishFeedDialog(activity.getString(R.string.app_name),
                                                Data.userData.getReferralMessages().fbShareCaption,
                                                Data.userData.getReferralMessages().fbShareDescription,
                                                link,
                                                Data.userData.jugnooFbBanner);
                                    }

                                    @Override
                                    public void facebookLoginError(String message) {
                                        Utils.showToast(activity, message);
                                    }
                                });
                                facebookLoginHelper.openFacebookSession(false);


                            }
                        }
                    }

                    @Override
                    public void onBranchError(String error) {
                        Utils.showToast(activity, error);
                    }
                }, channel,
                        channelLinkSP,
                        Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                        Data.userData.getReferralMessages().getTitle(),
                        Data.userData.getReferralMessages().fbShareDescription, Data.userData.jugnooFbBanner,
                        Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                        Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl(), Data.userData.getCity(),
                        Data.userData.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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
		if(Data.userData != null && Data.userData.getReferralMessages() != null) {
			facebookLoginHelper.publishFeedDialog(activity.getString(R.string.app_name),
					Data.userData.getReferralMessages().fbShareCaption,
					Data.userData.getReferralMessages().fbShareDescription,
					link,
					Data.userData.jugnooFbBanner);
		}
    }


    public static void shareToWhatsapp(final Activity activity) {

        try {
            BranchMetricsUtils.getBranchLinkForChannel(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    try {
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        List<ResolveInfo> activities = activity.getPackageManager().queryIntentActivities(intent, 0);
                        for(ResolveInfo info : activities){
                            if(info.activityInfo.packageName.contains("com.whatsapp")){
                                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                                intent.putExtra(Intent.EXTRA_TEXT, Data.userData.getReferralMessages().referralSharingMessage + " "
                                        + link);
                                activity.startActivity(intent);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Utils.showToast(activity, activity.getString(R.string.whatsapp_not_installed));
                    }
                }

                @Override
                public void onBranchError(String error) {
                    Utils.showToast(activity, error);
                }
            }, BranchMetricsUtils.BRANCH_CHANNEL_WHATSAPP,
                    SPLabels.BRANCH_WHATSAPP_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.userData.getReferralMessages().getTitle(),
                    Data.userData.getReferralMessages().fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl(), Data.userData.getCity(),
                    Data.userData.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void sendSMSIntent(final Activity activity){
        try {
            BranchMetricsUtils.getBranchLinkForChannel(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    Uri sms_uri = Uri.parse("smsto:");
                    Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                    sms_intent.putExtra("sms_body", Data.userData.getReferralMessages().referralSharingMessage + " "
                            + link);
                    activity.startActivity(sms_intent);
                }

                @Override
                public void onBranchError(String error) {
                    Utils.showToast(activity, error);
                }
            }, BranchMetricsUtils.BRANCH_CHANNEL_SMS,
                    SPLabels.BRANCH_SMS_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.userData.getReferralMessages().getTitle(),
                    Data.userData.getReferralMessages().fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl(), Data.userData.getCity(),
                    Data.userData.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openMailIntent(final Activity activity){
        try {

            BranchMetricsUtils.getBranchLinkForChannel(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                    email.putExtra(Intent.EXTRA_SUBJECT, Data.userData.getReferralMessages().referralEmailSubject);
                    email.putExtra(Intent.EXTRA_TEXT, Data.userData.getReferralMessages().referralSharingMessage + " "
                            + link); //
                    email.setType("message/rfc822");
                    activity.startActivity(Intent.createChooser(email, activity.getString(R.string.choose_email_client)));
                }

                @Override
                public void onBranchError(String error) {
                    Utils.showToast(activity, error);
                }
            }, BranchMetricsUtils.BRANCH_CHANNEL_EMAIL,
                    SPLabels.BRANCH_EMAIL_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.userData.getReferralMessages().getTitle(),
                    Data.userData.getReferralMessages().fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl(), Data.userData.getCity(),
                    Data.userData.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openGenericShareIntent(final Activity activity, final CallbackManager callbackManager){
        try {
            BranchMetricsUtils.getBranchLinkForChannel(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    genericShareDialog(activity, callbackManager,
                            Data.userData.getReferralMessages().referralEmailSubject,
                            Data.userData.getReferralMessages().referralSharingMessage + " " + link,
                            link);
                }

                @Override
                public void onBranchError(String error) {
                    Utils.showToast(activity, error);
                }
            }, BranchMetricsUtils.BRANCH_CHANNEL_GENERIC,
                    SPLabels.BRANCH_GENERIC_LINK,
                    Data.userData.userIdentifier, Data.userData.referralCode, Data.userData.userName,
                    Data.userData.getReferralMessages().getTitle(),
                    Data.userData.getReferralMessages().fbShareDescription, Data.userData.jugnooFbBanner,
                    Data.userData.getBranchDesktopUrl(), Data.userData.getBranchAndroidUrl(),
                    Data.userData.getBranchIosUrl(), Data.userData.getBranchFallbackUrl(), Data.userData.getCity(),
                    Data.userData.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void genericShareDialog(final Activity activity, final CallbackManager callbackManager,
                                          final String subject, final String body, final String link){
        genericShareDialog(activity, callbackManager, subject, body, link, "", false, null, false);
    }

    public static void genericShareDialog(final Activity activity, final CallbackManager callbackManager,
                                          final String subject, final String body, final String link, final String imageUrl,
                                          final boolean showCustomDialog,
                                          final ShareDialogCallback callback,
                                          final boolean hitAnalyticEvents) {
        if(!showCustomDialog) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            activity.startActivity(Intent.createChooser(intent, activity.getResources().getText(R.string.send_via)));
        }
        else {
            Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            List<ResolveInfo> activities = activity.getPackageManager().queryIntentActivities(sendIntent, 0);
            sortAppsInGenericShareDialog(activities);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
            builder.setTitle(activity.getString(R.string.share_with));
            final ShareIntentListAdapter adapter = new ShareIntentListAdapter(activity,
                    R.layout.list_item_share_intent, activities.toArray());
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        ResolveInfo info = (ResolveInfo) adapter.getItem(which);
                        if(info != null) {
                            if ((info.activityInfo.packageName.equalsIgnoreCase("com.facebook.katana")
                                    || info.activityInfo.packageName.equalsIgnoreCase("com.facebook.work"))
                                    && ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareDialog shareDialog = new ShareDialog(activity);
                                ShareLinkContent.Builder builder1 = new ShareLinkContent.Builder();
                                if (!TextUtils.isEmpty(subject)) {
                                    builder1.setContentTitle(subject);
                                } else {
                                    builder1.setContentTitle(activity.getString(R.string.app_name));
                                }
                                builder1.setContentDescription(body);
                                if (!TextUtils.isEmpty(link)) {
                                    builder1.setContentUrl(Uri.parse(link));
                                }
                                if (!TextUtils.isEmpty(imageUrl)) {
                                    builder1.setImageUrl(Uri.parse(imageUrl));
                                }
                                ShareLinkContent linkContent = builder1.build();
                                shareDialog.show(linkContent);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                intent.putExtra(Intent.EXTRA_TEXT, body);
                                activity.startActivity(intent);
                            }

                            try {
                                if(callback != null){
									callback.onShareClicked(info.activityInfo.applicationInfo.loadLabel(activity.getPackageManager()).toString());
								}
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
            builder.create().show();
        }
    }


    private static void sortAppsInGenericShareDialog(List<ResolveInfo> infos){
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

    public static interface ShareDialogCallback{
        void onShareClicked(String appName);
    }

    public static void shareIntent(Activity context, String subject, String message, String appType){
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
            ResolveInfo resolveInfo = null;
            for(ResolveInfo info : activities){
                if(appType.equalsIgnoreCase(EMAIL) && (info.activityInfo.packageName.contains("com.google.android.gm")
                        || info.activityInfo.packageName.contains("com.yahoo.mobile.client.android.mail")
                        || info.activityInfo.packageName.contains("com.microsoft.office.outlook")
                        || info.activityInfo.packageName.contains("com.google.android.apps.inbox"))){
                    resolveInfo = info;
                    break;
                } else if(appType.equalsIgnoreCase(TWITTER) && info.activityInfo.packageName.contains("com.twitter.android")){
                    resolveInfo = info;
                    break;
                } else if(appType.equalsIgnoreCase(WHATSAPP) && info.activityInfo.packageName.contains("com.whatsapp")){
                    resolveInfo = info;
                    break;
                }
            }

            if(resolveInfo != null) {
                intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                context.startActivity(intent);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.send_via)));
        }
    }

    public static void shareToFacebookDialog(Activity context, String title, String desc, String link, String imageUrl){
        if(ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog shareDialog = new ShareDialog(context);
            ShareLinkContent.Builder builder1 = new ShareLinkContent.Builder();
            if (!TextUtils.isEmpty(title)) {
                builder1.setContentTitle(title);
            } else {
                builder1.setContentTitle(context.getString(R.string.app_name));
            }
            builder1.setContentDescription(desc);
            if (!TextUtils.isEmpty(link)) {
                builder1.setContentUrl(Uri.parse(link));
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                builder1.setImageUrl(Uri.parse(imageUrl));
            }
            ShareLinkContent linkContent = builder1.build();
            shareDialog.show(linkContent);
        } else {
            shareIntent(context, title, desc+" "+link, "");
        }
    }

    public static final String WHATSAPP = "Whatsapp", TWITTER = "Twitter", EMAIL = "Email";

}
