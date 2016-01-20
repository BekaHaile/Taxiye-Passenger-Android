package product.clicklabs.jugnoo.utils;

import android.content.Context;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
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
    public static final String BRANCH_CHANNEL_EMAIL = "Email";
    public static final String BRANCH_CHANNEL_GENERIC = "Generic";

    private Context context;
    private BranchMetricsEventHandler branchMetricsEventHandler;

    public BranchMetricsUtils(Context context, BranchMetricsEventHandler branchMetricsEventHandler){
        this.context = context;
        this.branchMetricsEventHandler = branchMetricsEventHandler;
    }

    public void getBranchLinkForChannel(String channel, final String spKey, final String userIdentifier,
                                        final String referralCode, final String referringUserName){
        String existingUrl = Prefs.with(context).getString(spKey, "");
        String existingUserIdentifier = Prefs.with(context).getString(SPLabels.USER_IDENTIFIER, "");
        if(!userIdentifier.equalsIgnoreCase(existingUserIdentifier)){
            existingUrl = "";
        }
        if("".equalsIgnoreCase(existingUrl)){
            DialogPopup.showLoadingDialog(context, context.getResources().getString(R.string.loading));

//            JSONObject params = new JSONObject();
//            try {
//                params.put("referring_user_identifier", userIdentifier);
//				params.put("deepindex", "0");
//                params.put("referral_code", referralCode);
//                params.put("referring_user_name", referringUserName);
//                Log.e("branch link params", ""+params.toString());
//            } catch (Exception ex) { }
//
//            Branch branch = Branch.getInstance(context);
//            branch.getShortUrl(channel, Branch.FEATURE_TAG_SHARE, null, params, new Branch.BranchLinkCreateListener() {
//                @Override
//                public void onLinkCreate(String url, BranchError error) {
//                    DialogPopup.dismissLoadingDialog();
//                    if(error == null){
//                        Prefs.with(context).save(spKey, url);
//                        Prefs.with(context).save(SPLabels.USER_IDENTIFIER, userIdentifier);
//                        branchMetricsEventHandler.onBranchLinkCreated(url);
//                    }
//                    else{
//                        branchMetricsEventHandler.onBranchError(error.getMessage());
//                    }
//                }
//            });

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()

                    // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
                    .setCanonicalIdentifier("item/12345")

                            // The canonical URL for SEO purposes (optional)
                    .setCanonicalUrl("https://jugnoo.in")

                            // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                    .setTitle(Constants.FB_LINK_SHARE_NAME)
                    .setContentDescription(Data.referralMessages.fbShareDescription)
                    .setContentImageUrl(Data.userData.jugnooFbBanner)

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
//                    .addControlParameter("$desktop_url", Constants.BRANCH_END_LINK)
//                    .addControlParameter("$android_url", Constants.BRANCH_END_LINK)
//                    .addControlParameter("$ios_url", Constants.BRANCH_END_LINK)
//                    .addControlParameter("$fallback_url", Constants.BRANCH_END_LINK)


            Log.i(TAG, "branchUniversalObject=>"+branchUniversalObject.convertToJson());
            Log.i(TAG, "linkProperties=>"+linkProperties.toString());

            branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    DialogPopup.dismissLoadingDialog();
                    Log.i(TAG, "url=>" + url);
                    Log.e(TAG, "error=>" + error);
                    if(error == null){
                        Prefs.with(context).save(spKey, url);
                        Prefs.with(context).save(SPLabels.USER_IDENTIFIER, userIdentifier);
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
