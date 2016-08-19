package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

import product.clicklabs.jugnoo.retrofit.model.Campaigns;

/**
 * Created by gurmail on 18/08/16.
 */
public class AutoData {

    private String destinationHelpText, rideSummaryBadText, cancellationChargesPopupTextLine1, cancellationChargesPopupTextLine2, inRideSendInviteTextBold,
            inRideSendInviteTextNormal, confirmScreenFareEstimateEnable, poolDestinationPopupText1, poolDestinationPopupText2, poolDestinationPopupText3,
            rideEndGoodFeedbackText, baseFarePoolText;
    private int rideEndGoodFeedbackViewType;

    private int referAllStatus;
    private String referAllText, referAllTitle;
    private int referAllStatusLogin;
    private String referAllTextLogin, referAllTitleLogin;

    private ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
    private double fareFactor, driverFareFactor;
    private Campaigns campaigns;

    public AutoData(String destinationHelpText, String rideSummaryBadText, String cancellationChargesPopupTextLine1, String cancellationChargesPopupTextLine2,
                    String inRideSendInviteTextBold, String inRideSendInviteTextNormal, String confirmScreenFareEstimateEnable,
                    String poolDestinationPopupText1, String poolDestinationPopupText2, String poolDestinationPopupText3,
                    int rideEndGoodFeedbackViewType, String rideEndGoodFeedbackText, String baseFarePoolText,
                    int referAllStatus, String referAllText, String referAllTitle,
                    int referAllStatusLogin, String referAllTextLogin, String referAllTitleLogin) {
        this.destinationHelpText = destinationHelpText;
        this.rideSummaryBadText = rideSummaryBadText;
        this.cancellationChargesPopupTextLine1 = cancellationChargesPopupTextLine1;
        this.cancellationChargesPopupTextLine2 =cancellationChargesPopupTextLine2;
        this.inRideSendInviteTextBold = inRideSendInviteTextBold;
        this.inRideSendInviteTextNormal = inRideSendInviteTextNormal;
        this.confirmScreenFareEstimateEnable = confirmScreenFareEstimateEnable;
        this.poolDestinationPopupText1 = poolDestinationPopupText1;
        this.poolDestinationPopupText2 = poolDestinationPopupText2;
        this.poolDestinationPopupText3 = poolDestinationPopupText3;
        this.rideEndGoodFeedbackViewType = rideEndGoodFeedbackViewType;
        this.rideEndGoodFeedbackText = rideEndGoodFeedbackText;
        this.baseFarePoolText = baseFarePoolText;
        this.referAllStatus = referAllStatus;
        this.referAllText = referAllText;
        this.referAllTitle = referAllTitle;
        this.referAllStatusLogin = referAllStatusLogin;
        this.referAllTextLogin = referAllTextLogin;
        this.referAllTitleLogin = referAllTitleLogin;
    }

    public String getDestinationHelpText() {
        return destinationHelpText;
    }

    public void setDestinationHelpText(String destinationHelpText) {
        this.destinationHelpText = destinationHelpText;
    }

    public String getRideSummaryBadText() {
        return rideSummaryBadText;
    }

    public void setRideSummaryBadText(String rideSummaryBadText) {
        this.rideSummaryBadText = rideSummaryBadText;
    }

    public String getCancellationChargesPopupTextLine1() {
        return cancellationChargesPopupTextLine1;
    }

    public void setCancellationChargesPopupTextLine1(String cancellationChargesPopupTextLine1) {
        this.cancellationChargesPopupTextLine1 = cancellationChargesPopupTextLine1;
    }

    public String getCancellationChargesPopupTextLine2() {
        return cancellationChargesPopupTextLine2;
    }

    public String getInRideSendInviteTextBold() {
        return inRideSendInviteTextBold;
    }

    public void setInRideSendInviteTextBold(String inRideSendInviteTextBold) {
        this.inRideSendInviteTextBold = inRideSendInviteTextBold;
    }

    public void setCancellationChargesPopupTextLine2(String cancellationChargesPopupTextLine2) {
        this.cancellationChargesPopupTextLine2 = cancellationChargesPopupTextLine2;
    }

    public String getInRideSendInviteTextNormal() {
        return inRideSendInviteTextNormal;
    }

    public void setInRideSendInviteTextNormal(String inRideSendInviteTextNormal) {
        this.inRideSendInviteTextNormal = inRideSendInviteTextNormal;
    }

    public String getConfirmScreenFareEstimateEnable() {
        return confirmScreenFareEstimateEnable;
    }

    public void setConfirmScreenFareEstimateEnable(String confirmScreenFareEstimateEnable) {
        this.confirmScreenFareEstimateEnable = confirmScreenFareEstimateEnable;
    }

    public String getPoolDestinationPopupText1() {
        return poolDestinationPopupText1;
    }

    public void setPoolDestinationPopupText1(String poolDestinationPopupText1) {
        this.poolDestinationPopupText1 = poolDestinationPopupText1;
    }

    public String getPoolDestinationPopupText2() {
        return poolDestinationPopupText2;
    }

    public void setPoolDestinationPopupText2(String poolDestinationPopupText2) {
        this.poolDestinationPopupText2 = poolDestinationPopupText2;
    }

    public String getPoolDestinationPopupText3() {
        return poolDestinationPopupText3;
    }

    public void setPoolDestinationPopupText3(String poolDestinationPopupText3) {
        this.poolDestinationPopupText3 = poolDestinationPopupText3;
    }

    public int getRideEndGoodFeedbackViewType() {
        return rideEndGoodFeedbackViewType;
    }

    public void setRideEndGoodFeedbackViewType(int rideEndGoodFeedbackViewType) {
        this.rideEndGoodFeedbackViewType = rideEndGoodFeedbackViewType;
    }

    public String getRideEndGoodFeedbackText() {
        return rideEndGoodFeedbackText;
    }

    public void setRideEndGoodFeedbackText(String rideEndGoodFeedbackText) {
        this.rideEndGoodFeedbackText = rideEndGoodFeedbackText;
    }

    public String getBaseFarePoolText() {
        return baseFarePoolText;
    }

    public void setBaseFarePoolText(String baseFarePoolText) {
        this.baseFarePoolText = baseFarePoolText;
    }

    public int getReferAllStatus() {
        return referAllStatus;
    }

    public void setReferAllStatus(int referAllStatus) {
        this.referAllStatus = referAllStatus;
    }

    public String getReferAllText() {
        return referAllText;
    }

    public void setReferAllText(String referAllText) {
        this.referAllText = referAllText;
    }

    public String getReferAllTitle() {
        return referAllTitle;
    }

    public void setReferAllTitle(String referAllTitle) {
        this.referAllTitle = referAllTitle;
    }

    public int getReferAllStatusLogin() {
        return referAllStatusLogin;
    }

    public void setReferAllStatusLogin(int referAllStatusLogin) {
        this.referAllStatusLogin = referAllStatusLogin;
    }

    public String getReferAllTextLogin() {
        return referAllTextLogin;
    }

    public void setReferAllTextLogin(String referAllTextLogin) {
        this.referAllTextLogin = referAllTextLogin;
    }

    public String getReferAllTitleLogin() {
        return referAllTitleLogin;
    }

    public void setReferAllTitleLogin(String referAllTitleLogin) {
        this.referAllTitleLogin = referAllTitleLogin;
    }

    public ArrayList<DriverInfo> getDriverInfos() {
        return driverInfos;
    }

    public void setDriverInfos(ArrayList<DriverInfo> driverInfos) {
        this.driverInfos = driverInfos;
    }

    public double getFareFactor() {
        return fareFactor;
    }

    public void setFareFactor(double fareFactor) {
        this.fareFactor = fareFactor;
    }

    public double getDriverFareFactor() {
        return driverFareFactor;
    }

    public void setDriverFareFactor(double driverFareFactor) {
        this.driverFareFactor = driverFareFactor;
    }

    public Campaigns getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Campaigns campaigns) {
        this.campaigns = campaigns;
    }
}
