package com.sabkuchfresh.analytics;

import android.content.Context;

import product.clicklabs.jugnoo.MyApplication;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Log;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import product.clicklabs.jugnoo.R;

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
    private Branch branch;

    public BranchMetricsUtils(Context context, BranchMetricsEventHandler branchMetricsEventHandler) {
        this.context = context;
        this.branchMetricsEventHandler = branchMetricsEventHandler;
        this.branch = MyApplication.getInstance().branch;
    }


    public void getBranchLinkForChannel(String channel, final String spKey, final String userIdentifier,
                                        final String referralCode, final String referringUserName,
                                        final String fbShareDescription, final String jugnooFbBanner,
                                        String branchDesktopUrl, String branchAndroidUrl,
                                        String branchIosUrl, String branchFallbackUrl) {

        DialogPopup.showLoadingDialog(context, context.getResources().getString(R.string.loading));

        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()

                // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
                .setCanonicalIdentifier("item/12345")

                // The canonical URL for SEO purposes (optional)
                .setCanonicalUrl("https://lofatafat.com")

                // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                .setTitle(Data.userData.referralShareTitle)
                .setContentDescription(Data.userData.referralShareText)
                .setContentImageUrl(Data.userData.getReferralShareImage())

                // You use this to specify whether this content can be discovered publicly - default is public
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                    .addContentMetadata(Constants.KEY_BRANCH_REFERRING_USER_IDENTIFIER, Data.userData.getUserId())
//                    .addContentMetadata(Constants.KEY_DEEPINDEX, "0")
                .addContentMetadata(Constants.KEY_REFERRAL_CODE, Data.userData.referralCode)
//                    .addContentMetadata(Constants.KEY_BRANCH_REFERRING_USER_NAME, referringUserName)
                ;
        LinkProperties linkProperties = new LinkProperties()
                .setChannel(channel)
                .setFeature(Branch.FEATURE_TAG_SHARE);

        Log.e(TAG, "linkProperties.getControlParams=>" + linkProperties.getControlParams());


        Log.i(TAG, "branchUniversalObject=>" + branchUniversalObject.convertToJson());

        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                DialogPopup.dismissLoadingDialog();
                Log.i(TAG, "url=>" + url);
                Log.e(TAG, "error=>" + error);
                if (error == null) {
                    branchMetricsEventHandler.onBranchLinkCreated(url);
                } else {
                    branchMetricsEventHandler.onBranchError(error.getMessage());
                }
            }
        });
//        }
//        else{
//            branchMetricsEventHandler.onBranchLinkCreated(existingUrl);
//        }

    }

    public interface BranchMetricsEventHandler {
        void onBranchLinkCreated(String link);
        void onBranchError(String error);
    }


}