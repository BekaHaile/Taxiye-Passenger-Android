package product.clicklabs.jugnoo.utils;

/**
 * Created by socomo20 on 7/29/15.
 */
public interface BranchMetricsEventHandler {
    void onBranchLinkCreated(String link);
    void onBranchError(String error);
}
