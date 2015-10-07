package product.clicklabs.jugnoo.utils;

import android.content.Context;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import product.clicklabs.jugnoo.datastructure.SPLabels;

/**
 * Created by socomo20 on 7/29/15.
 */
public class BranchMetricsUtils {

    public static final String BRANCH_CHANNEL_SMS = "SMS";
    public static final String BRANCH_CHANNEL_WHATSAPP = "Whatsapp";
    public static final String BRANCH_CHANNEL_FACEBOOK = "Facebook";
    public static final String BRANCH_CHANNEL_EMAIL = "Email";

    private Context context;
    private BranchMetricsEventHandler branchMetricsEventHandler;

    public BranchMetricsUtils(Context context, BranchMetricsEventHandler branchMetricsEventHandler){
        this.context = context;
        this.branchMetricsEventHandler = branchMetricsEventHandler;
    }

    public void getBranchLinkForChannel(String channel, final String spKey, final String userIdentifier){
        String existingUrl = Prefs.with(context).getString(spKey, "");
        String existingUserIdentifier = Prefs.with(context).getString(SPLabels.USER_IDENTIFIER, "");
        if(!userIdentifier.equalsIgnoreCase(existingUserIdentifier)){
            existingUrl = "";
        }
        if("".equalsIgnoreCase(existingUrl)){
            DialogPopup.showLoadingDialog(context, "Loading...");
            JSONObject params = new JSONObject();
            try {
                params.put("referring_user_identifier", userIdentifier);
				params.put("deepindex", "0");
            } catch (Exception ex) { }

            Branch branch = Branch.getInstance(context);
            branch.getShortUrl(channel, Branch.FEATURE_TAG_SHARE, null, params, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    DialogPopup.dismissLoadingDialog();
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

}
