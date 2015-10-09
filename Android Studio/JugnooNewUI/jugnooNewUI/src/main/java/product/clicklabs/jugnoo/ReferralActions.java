package product.clicklabs.jugnoo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;

import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.BranchMetricsEventHandler;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo20 on 6/19/15.
 */
public class ReferralActions implements FlurryEventNames {


    public static boolean showReferralDialog(final Activity activity, final CallbackManager callbackManager){
        try{
            boolean showDialog = false;
            long minus1 = -1l;
            long lastOpenDate = Prefs.with(activity).getLong(SPLabels.REFERRAL_OPEN_DATE_MILLIS, minus1);
            long oneDayMillis = 24 * 60 * 60 * 1000;
            long dateDiff = DateOperations.getTimeDifference(System.currentTimeMillis(), lastOpenDate);

            int lastAppOpen = Prefs.with(activity).getInt(SPLabels.REFERRAL_APP_OPEN_COUNT, 0);
            int lastTransactionCount = Prefs.with(activity).getInt(SPLabels.REFERRAL_TRANSACTION_COUNT, 0);

            if(dateDiff >= oneDayMillis){
                if((lastTransactionCount > 0) && (lastTransactionCount % 2 == 0)){
                    showDialog = true;
                }
                else if((lastAppOpen > 0) && (lastAppOpen % 5 == 0)){
                    showDialog = true;
                }
                else{
                    showDialog = false;
                }
            }
            else{
                showDialog = false;
            }

            if(showDialog) {
                final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
                dialog.setContentView(R.layout.dialog_referral);

                RelativeLayout rv = (RelativeLayout) dialog.findViewById(R.id.rv);
                new ASSL(activity, rv, 1134, 720, true);

                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.dimAmount = 0.6f;
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                ((TextView) dialog.findViewById(R.id.textViewGiftGet)).setTypeface(Fonts.latoRegular(activity));
                ((TextView) dialog.findViewById(R.id.textViewInviteFriends)).setTypeface(Fonts.latoRegular(activity));
                TextView textViewAmount = (TextView) dialog.findViewById(R.id.textViewAmount);
                textViewAmount.setTypeface(Fonts.latoRegular(activity));
                ((TextView) dialog.findViewById(R.id.textViewShareCodeToday)).setTypeface(Fonts.latoRegular(activity));


                (dialog.findViewById(R.id.imageViewFacebook)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToFacebook(activity, callbackManager);
                        FlurryEventLogger.event(REFERRAL_POPUP_FACEBOOK);
						dialog.dismiss();
                    }
                });

                (dialog.findViewById(R.id.imageViewWhatsapp)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareToWhatsapp(activity);
						FlurryEventLogger.event(REFERRAL_POPUP_WHATSAPP);
						dialog.dismiss();
                    }
                });

                (dialog.findViewById(R.id.imageViewSMS)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendSMSIntent(activity);
						FlurryEventLogger.event(REFERRAL_POPUP_MESSAGE);
						dialog.dismiss();
                    }
                });

                (dialog.findViewById(R.id.imageViewEmail)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMailIntent(activity);
						FlurryEventLogger.event(REFERRAL_POPUP_EMAIL);
						dialog.dismiss();
                    }
                });

                (dialog.findViewById(R.id.imageViewCross)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
						dialog.dismiss();
						FlurryEventLogger.event(REFERRAL_POPUP_CLOSE);
                    }
                });

                dialog.show();
                resetAppOpen(activity);
                resetTransactionCount(activity);
                updateOpenDate(activity);
            }
            return showDialog;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public static FacebookLoginHelper facebookLoginHelper;
    public static void shareToFacebook(final Activity activity, CallbackManager callbackManager){
        facebookLoginHelper = new FacebookLoginHelper(activity, callbackManager, new FacebookLoginCallback() {
            @Override
            public void facebookLoginDone(FacebookUserData facebookUserData) {
                try {
                    if(Data.userData != null){
                        new BranchMetricsUtils(activity, new BranchMetricsEventHandler() {
                            @Override
                            public void onBranchLinkCreated(String link) {
                                facebookLoginHelper.publishFeedDialog("Jugnoo Autos - Autos on demand",
                                    Data.referralMessages.fbShareCaption,
                                    Data.referralMessages.fbShareDescription,
                                    link+"?deepindex=0",
                                    Data.userData.jugnooFbBanner);

								//http://share.jugnoo.in/m/7MPH22Lyln?deepindex=0
                            }

                            @Override
                            public void onBranchError(String error) {
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                            }
                        }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_FACEBOOK, SPLabels.BRANCH_FACEBOOK_LINK, Data.userData.userIdentifier);
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
        facebookLoginHelper.openFacebookSession();
    }


    public static void shareToWhatsapp(final Activity activity) {

        try {
            new BranchMetricsUtils(activity, new BranchMetricsEventHandler() {
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

                        waIntent.putExtra(Intent.EXTRA_TEXT, text + "\n" + link+"?deepindex=0");
                        activity.startActivity(Intent.createChooser(waIntent, "Share with"));
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(activity, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_WHATSAPP, SPLabels.BRANCH_WHATSAPP_LINK, Data.userData.userIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void sendSMSIntent(final Activity activity){
        try {
            new BranchMetricsUtils(activity, new BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    Uri sms_uri = Uri.parse("smsto:");
                    Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                    sms_intent.putExtra("sms_body", Data.referralMessages.referralSharingMessage + "\n" + link+"?deepindex=0");
                    activity.startActivity(sms_intent);
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_SMS, SPLabels.BRANCH_SMS_LINK, Data.userData.userIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openMailIntent(final Activity activity){
        try {

            new BranchMetricsUtils(activity, new BranchMetricsEventHandler() {
                @Override
                public void onBranchLinkCreated(String link) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                    email.putExtra(Intent.EXTRA_SUBJECT, Data.referralMessages.referralEmailSubject);
                    email.putExtra(Intent.EXTRA_TEXT, Data.referralMessages.referralSharingMessage + "\n" + link+"?deepindex=0"); //
                    email.setType("message/rfc822");
                    activity.startActivity(Intent.createChooser(email, "Choose an Email client:"));
                }

                @Override
                public void onBranchError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }).getBranchLinkForChannel(BranchMetricsUtils.BRANCH_CHANNEL_EMAIL, SPLabels.BRANCH_EMAIL_LINK, Data.userData.userIdentifier);
        } catch (Exception e) {
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
