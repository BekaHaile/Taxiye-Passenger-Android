package product.clicklabs.jugnoo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.activities.MainActivity;

import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


public class AboutActivity extends BaseActivity implements FlurryEventNames, FirebaseEvents {

    RelativeLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    RelativeLayout relativeLayoutRateUs, relativeLayoutLikeUs, relativeLayoutTNC, relativeLayoutPrivacy, relativeLayoutAbout;
    TextView textViewRateUs, textViewLikeUs, textViewTNC, textViewPrivacy, textViewAbout;


    String facebookPageId = "252184564966458";
//    String facebookPageName = "ridejugnoo";
    String facebookPageName = "jugnoose";
    private final String  TAG = "About";
    Bundle bundle;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, (ViewGroup) relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        relativeLayoutRateUs = (RelativeLayout) findViewById(R.id.relativeLayoutRateUs);
        relativeLayoutLikeUs = (RelativeLayout) findViewById(R.id.relativeLayoutLikeUs);
        relativeLayoutTNC = (RelativeLayout) findViewById(R.id.relativeLayoutTNC);
        relativeLayoutPrivacy = (RelativeLayout) findViewById(R.id.relativeLayoutPrivacy);
        relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutAbout);

        textViewRateUs = (TextView) findViewById(R.id.textViewRateUs);
        textViewRateUs.setTypeface(Fonts.mavenLight(this));
        textViewLikeUs = (TextView) findViewById(R.id.textViewLikeUs);
        textViewLikeUs.setTypeface(Fonts.mavenLight(this));
        textViewTNC = (TextView) findViewById(R.id.textViewTNC);
        textViewTNC.setTypeface(Fonts.mavenLight(this));
        textViewPrivacy = (TextView) findViewById(R.id.textViewPrivacy);
        textViewPrivacy.setTypeface(Fonts.mavenLight(this));
        textViewAbout = (TextView) findViewById(R.id.textViewAbout);
        textViewAbout.setTypeface(Fonts.mavenLight(this));

        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_ABOUT);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        relativeLayoutRateUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo"));
//                startActivity(intent);
//                FlurryEventLogger.event(RATING_ON_PLAYSTORE_ABOUT);
//                bundle = new Bundle();
//                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+ABOUT+"_"+PLAYSTORE_RATING, bundle);
//                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Playstore rating");

                startActivity(new Intent(AboutActivity.this, MainActivity.class));

            }
        });

        relativeLayoutLikeUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent;
                    if (Utils.appInstalledOrNot(AboutActivity.this, "com.facebook.katana")) {
                        try {
                            getPackageManager().getPackageInfo("com.facebook.katana", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + facebookPageId));
                        } catch (Exception e) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebookPageName));
                        }
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.facebook.com/" + facebookPageName));
                    }
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast(AboutActivity.this, "Facebook app not enabled");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.facebook.com/" + facebookPageName));
                    startActivity(intent);
                }
                FlurryEventLogger.event(LIKING_ON_FACEBOOK_ABOUT);
                bundle = new Bundle();
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+ABOUT+"_"+FACEBOOK_LIKE, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Facebook Like");
            }
        });

        relativeLayoutTNC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.TERMS;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(TERMS_AND_CONDITIONS);
                bundle = new Bundle();
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+ABOUT+"_"+TERMS_AND_CONDITION, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Terms and Condition");
            }
        });

        relativeLayoutPrivacy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.PRIVACY;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(FlurryEventNames.PRIVACY_POLICY);
                bundle = new Bundle();
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+ABOUT+"_"+FirebaseEvents.PRIVACY_POLICY, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Privacy Policy");
            }
        });

        relativeLayoutAbout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HelpParticularActivity.helpSection = HelpSection.ABOUT;
                startActivity(new Intent(AboutActivity.this, HelpParticularActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                FlurryEventLogger.event(ABOUT_JUGOO_AUTOS);
                bundle = new Bundle();
                MyApplication.getInstance().logEvent(INFORMATIVE+"_"+ABOUT+"_"+ABOUT_JUGNOO, bundle);
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "About Jugnoo");
            }
        });


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Back");
                performBackPressed();
            }
        });
    }


    public void performBackPressed() {
        bundle = new Bundle();
        MyApplication.getInstance().logEvent(INFORMATIVE+"_"+ABOUT+"_"+BACK, bundle);

        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

}
