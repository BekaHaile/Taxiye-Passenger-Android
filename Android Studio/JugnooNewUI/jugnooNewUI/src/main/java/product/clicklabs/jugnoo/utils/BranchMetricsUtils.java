package product.clicklabs.jugnoo.utils;

import android.content.Context;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SPLabels;

/**
 * Created by socomo20 on 7/29/15.
 */
public class BranchMetricsUtils {

    private final String TAG = BranchMetricsUtils.class.getSimpleName();

    public static final String BRANCH_CHANNEL_SMS = "SMS";
    public static final String BRANCH_CHANNEL_WHATSAPP = "Whatsapp";
    public static final String BRANCH_CHANNEL_FACEBOOK = "Facebook";
    public static final String BRANCH_CHANNEL_FACEBOOK_MESSENGER = "Facebook Messenger";
    public static final String BRANCH_CHANNEL_EMAIL = "Email";
    public static final String BRANCH_CHANNEL_GENERIC = "Generic";

    private Context context;
    private BranchMetricsEventHandler branchMetricsEventHandler;

    public BranchMetricsUtils(Context context, BranchMetricsEventHandler branchMetricsEventHandler){
        this.context = context;
        this.branchMetricsEventHandler = branchMetricsEventHandler;
    }

    public void getBranchLinkForChannel(String channel, final String spKey, final String userIdentifier,
                                        final String referralCode, final String referringUserName,
                                        final String fbShareDescription, final String jugnooFbBanner,
                                        String branchDesktopUrl, String branchAndroidUrl,
                                        String branchIosUrl, String branchFallbackUrl){

        String existingUrl = Prefs.with(context).getString(spKey, "");
        String existingUserIdentifier = Prefs.with(context).getString(SPLabels.USER_IDENTIFIER, "");

        String existingLinkDescription = Prefs.with(context).getString(SPLabels.BRANCH_LINK_DESCRIPTION, "");
        String existingLinkImage = Prefs.with(context).getString(SPLabels.BRANCH_LINK_IMAGE, "");

        String existingBranchDesktopUrl = Prefs.with(context).getString(SPLabels.BRANCH_DESKTOP_URL, "");
        String existingBranchAndroidUrl = Prefs.with(context).getString(SPLabels.BRANCH_ANDROID_URL, "");
        String existingBranchIosUrl = Prefs.with(context).getString(SPLabels.BRANCH_IOS_URL, "");
        String existingBranchFallbackUrl = Prefs.with(context).getString(SPLabels.BRANCH_FALLBACK_URL, "");

        if(!userIdentifier.equalsIgnoreCase(existingUserIdentifier)){
            existingUrl = "";
        }
        if(!existingBranchDesktopUrl.equalsIgnoreCase(branchDesktopUrl)
                    || !existingBranchAndroidUrl.equalsIgnoreCase(branchAndroidUrl)
                    || !existingBranchIosUrl.equalsIgnoreCase(branchIosUrl)
                    || !existingBranchFallbackUrl.equalsIgnoreCase(branchFallbackUrl)){
            existingUrl = "";
            Prefs.with(context).save(SPLabels.BRANCH_DESKTOP_URL, branchDesktopUrl);
            Prefs.with(context).save(SPLabels.BRANCH_ANDROID_URL, branchAndroidUrl);
            Prefs.with(context).save(SPLabels.BRANCH_IOS_URL, branchIosUrl);
            Prefs.with(context).save(SPLabels.BRANCH_FALLBACK_URL, branchFallbackUrl);
        }
        if(!fbShareDescription.equalsIgnoreCase(existingLinkDescription)){
            existingUrl = "";
        }
        if(!jugnooFbBanner.equalsIgnoreCase(existingLinkImage)){
            existingUrl = "";
        }

        if("".equalsIgnoreCase(existingUrl)){
            DialogPopup.showLoadingDialog(context, context.getResources().getString(R.string.loading));

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()

                    // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
                    .setCanonicalIdentifier("item/12345")

                            // The canonical URL for SEO purposes (optional)
                    .setCanonicalUrl("https://jugnoo.in")

                            // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                    .setTitle(Constants.FB_LINK_SHARE_NAME)
                    .setContentDescription(fbShareDescription)
                    .setContentImageUrl(jugnooFbBanner)

                            // You use this to specify whether this content can be discovered publicly - default is public
                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

                    .addContentMetadata(Constants.KEY_BRANCH_REFERRING_USER_IDENTIFIER, userIdentifier)
                    .addContentMetadata(Constants.KEY_DEEPINDEX, "0")
                    .addContentMetadata(Constants.KEY_REFERRAL_CODE, referralCode)
                    .addContentMetadata(Constants.KEY_BRANCH_REFERRING_USER_NAME, referringUserName)
                    ;
            LinkProperties linkProperties = new LinkProperties()
                    .setChannel(channel)
                    .setFeature(Branch.FEATURE_TAG_SHARE);

            if(!"".equalsIgnoreCase(branchDesktopUrl)){
                linkProperties.addControlParameter("$desktop_url", branchDesktopUrl);
            }
            if(!"".equalsIgnoreCase(branchAndroidUrl)){
                linkProperties.addControlParameter("$android_url", branchAndroidUrl);
            }
            if(!"".equalsIgnoreCase(branchIosUrl)){
                linkProperties.addControlParameter("$ios_url", branchIosUrl);
            }
            if(!"".equalsIgnoreCase(branchFallbackUrl)){
                linkProperties.addControlParameter("$fallback_url", branchFallbackUrl);
            }
            Log.e(TAG, "linkProperties.getControlParams=>"+linkProperties.getControlParams());
//                    .addControlParameter("$desktop_url", Constants.BRANCH_END_LINK)
//                    .addControlParameter("$android_url", Constants.BRANCH_END_LINK)
//                    .addControlParameter("$ios_url", Constants.BRANCH_END_LINK)
//                    .addControlParameter("$fallback_url", Constants.BRANCH_END_LINK)


            Log.i(TAG, "branchUniversalObject=>"+branchUniversalObject.convertToJson());

            branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    DialogPopup.dismissLoadingDialog();
                    Log.i(TAG, "url=>" + url);
                    Log.e(TAG, "error=>" + error);
                    if(error == null){
                        Prefs.with(context).save(spKey, url);
                        Prefs.with(context).save(SPLabels.USER_IDENTIFIER, userIdentifier);
                        Prefs.with(context).save(SPLabels.BRANCH_LINK_DESCRIPTION, fbShareDescription);
                        Prefs.with(context).save(SPLabels.BRANCH_LINK_IMAGE, jugnooFbBanner);
                        branchMetricsEventHandler.onBranchLinkCreated(url);
                    }
                    else{
                        branchMetricsEventHandler.onBranchError(error.getMessage());
                    }
                }
            });
        }
        else{
            branchMetricsEventHandler.onBranchLinkCreated(existingUrl);
        }

    }

	public interface BranchMetricsEventHandler {
		void onBranchLinkCreated(String link);
		void onBranchError(String error);
	}


	public static void logEvent(Context context, String eventName, boolean oneTime){
		if(oneTime) {
			String spPrefix = "branch_";
			if (Prefs.with(context).getInt(spPrefix+eventName, 0) == 0) {
				Branch branch = Branch.getInstance(context);
				branch.userCompletedAction(eventName);
				Prefs.with(context).save(spPrefix+eventName, 1);
			}
		}
		else{
			Branch branch = Branch.getInstance(context);
			branch.userCompletedAction(eventName);
		}
	}

}
