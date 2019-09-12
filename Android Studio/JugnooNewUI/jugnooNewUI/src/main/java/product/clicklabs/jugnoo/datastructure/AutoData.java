package product.clicklabs.jugnoo.datastructure;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.model.Campaigns;
import product.clicklabs.jugnoo.retrofit.model.Corporate;
import product.clicklabs.jugnoo.retrofit.model.NearbyPickupRegions;
import product.clicklabs.jugnoo.retrofit.model.Package;
import product.clicklabs.jugnoo.retrofit.model.ServiceType;
import product.clicklabs.jugnoo.retrofit.model.ServiceTypeValue;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by gurmail on 18/08/16.
 */
public class AutoData {


    private ArrayList<FeedBackInfo> feedBackInfoRatingData=new ArrayList<>();

    public void setFeedBackInfoRatingData(ArrayList<FeedBackInfo> feedBackInfoRateData) {
        this.feedBackInfoRatingData = feedBackInfoRateData;
    }

    public ArrayList<FeedBackInfo> getFeedBackInfoRatingData() {
        return feedBackInfoRatingData;
    }

    private int isReverseBid;
	private long bidRequestRideTimeout, bidTimeout;
	private String fuguChannelId;
    private String fuguChannelName;
    private ArrayList<String> fuguTags;
    private String destinationHelpText, rideSummaryBadText, cancellationChargesPopupTextLine1, cancellationChargesPopupTextLine2, inRideSendInviteTextBold,
            inRideSendInviteTextNormal, confirmScreenFareEstimateEnable, poolDestinationPopupText1, poolDestinationPopupText2, poolDestinationPopupText3,
            rideEndGoodFeedbackText, baseFarePoolText, inRideSendInviteTextBoldV2, inRideSendInviteTextNormalV2;
    private int rideEndGoodFeedbackViewType, rideStartInviteTextDeepIndexV2;

    private int referAllStatus;
    private String referAllText, referAllTitle;
    private int referAllStatusLogin;
    private String referAllTextLogin, referAllTitleLogin;

    private ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
    private double fareFactor, driverFareFactor;
    private Campaigns campaigns;
    private FareStructure fareStructure;
    private ArrayList<Region> regions;
    private ArrayList<Region> regionsTemp;
    private String farAwayCity = "";
    private int isRazorpayEnabled;
    private String cEngagementId = "", cDriverId = "", cSessionId = "";
    private DriverInfo assignedDriverInfo;
    private EndRideData endRideData;
    private LatLng pickupLatLng, dropLatLng;
    private String pickupAddress = "", dropAddress = "";
    private LatLng pickupAddressMappedLatLng;
    private int dropAddressId;
    private int pickupPaymentOption = PaymentOption.PAYTM.getOrdinal();
    private CancelOptionsList cancelOptionsList;
    private ArrayList<FeedbackReason> feedbackReasons = new ArrayList<>();
    private boolean supportFeedbackSubmitted = false;
    private LatLng lastRefreshLatLng;
    NearbyPickupRegions nearbyPickupRegionses;

    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    private Integer useRecentLocAtRequest;
    private Double useRecentLocAutoSnapMinDistance;
    private Double useRecentLocAutoSnapMaxDistance;
    private PlaceOrderResponse.ReferralPopupContent referralPopupContent;
    private ArrayList<BidInfo> bidInfos;
    private String distanceUnit;
    private int isTipEnabled;
    private int showRegionSpecificFare;

    private ArrayList<Corporate> corporatesFetched;
    private ArrayList<ServiceType> serviceTypes;
    private ServiceType serviceTypeSelected;
    private Package selectedPackage;
    private String currency;
    private boolean isServiceAv;
    private int lock = 0;
    private int bluetoothEnabled =0;
    private String  previousSelService = "";
    private int resendEmailInvoiceEnabled;

    // RENTAL

    private List<String> FaultConditions;

    private int newBottomRequestUIEnabled;


    public AutoData(String destinationHelpText, String rideSummaryBadText, String cancellationChargesPopupTextLine1, String cancellationChargesPopupTextLine2,
                    String inRideSendInviteTextBold, String inRideSendInviteTextNormal, String confirmScreenFareEstimateEnable,
                    String poolDestinationPopupText1, String poolDestinationPopupText2, String poolDestinationPopupText3,
                    int rideEndGoodFeedbackViewType, String rideEndGoodFeedbackText, String baseFarePoolText, int referAllStatus, String referAllText,
                    String referAllTitle, int referAllStatusLogin, String referAllTextLogin, String referAllTitleLogin,
                    NearbyPickupRegions nearbyPickupRegionses, String inRideSendInviteTextBoldV2, String inRideSendInviteTextNormalV2,
                    int rideStartInviteTextDeepIndexV2, int isRazorpayEnabled,int isTipEnabled, int showRegionSpecificFare, int resendEmailInvoiceEnabled,int bluetoothEnabled) {
        this.bluetoothEnabled = bluetoothEnabled;
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
        this.nearbyPickupRegionses = nearbyPickupRegionses;
        this.inRideSendInviteTextBoldV2 = inRideSendInviteTextBoldV2;
        this.inRideSendInviteTextNormalV2 = inRideSendInviteTextNormalV2;
        this.rideStartInviteTextDeepIndexV2 = rideStartInviteTextDeepIndexV2;
        this.isRazorpayEnabled = isRazorpayEnabled;
        this.isTipEnabled = isTipEnabled;
        this.showRegionSpecificFare = showRegionSpecificFare;
        this.resendEmailInvoiceEnabled = resendEmailInvoiceEnabled;
		defaultServiceType();
	}

	public void defaultServiceType() {
		ArrayList<Integer> rideTypes = new ArrayList<>();
		ArrayList<Integer> regionIds = new ArrayList<>();
		rideTypes.add(ServiceTypeValue.NORMAL.getType());
		rideTypes.add(ServiceTypeValue.POOL.getType());
		rideTypes.add(ServiceTypeValue.BIKE_RENTAL.getType());
		int scheduleRideEnabled = Prefs.with(MyApplication.getInstance()).getBoolean(Constants.SCHEDULE_RIDE_ENABLED, false) ? 1 : 0;
		serviceTypeSelected = new ServiceType("On Demand", "", "", 1, rideTypes, regionIds, null, "", scheduleRideEnabled, true);
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

    public int getBluetoothEnabled() {
        return bluetoothEnabled;
    }

    public void setBluetoothEnabled(int bluetoothEnabled) {
        this.bluetoothEnabled = bluetoothEnabled;
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

    public FareStructure getFareStructure() {
        return fareStructure;
    }

    public void setFareStructure(FareStructure fareStructure) {
        this.fareStructure = fareStructure;
    }

    public boolean getNewUIFlag(){
        boolean setNew = getNewBottomRequestUIEnabled() == 1;
        for (Region region : Data.autoData.getRegions()) {
            if (region.getReverseBid() == 1) {
                setNew = true;
            }
        }
        return setNew;
    }

    public ArrayList<Region> getRegions() {
        if(regionsTemp == null){
            regionsTemp = new ArrayList<>();
        }
        if(regions == null){
            regions = new ArrayList<>();
        }
        regionsTemp.clear();

//  ***********************************************************************************************************************
//            if last selected service type is Rental or Outstation and if
//            app location refreshed from An available service type(like rental or outstation)location to
//            An unavailable service type location where these service types are not available.
//            In this, we have to check for the selected service type is available for the new location or not
//
//            if yes, then used selected ServiceType  otherwise  set selected ServiceType to the 0th serviceType

//              lock = logic only works on find a driver response

        if(lock == 1) {
            boolean isSelectedTypeAvailable = false;
            if (Data.autoData != null && Data.autoData.getServiceTypes() != null && Data.autoData.getServiceTypes().size() > 0) {

                if (getServiceTypeSelected().getSupportedRideTypes() != null && getServiceTypeSelected().getSupportedRideTypes().size() > 0) {

                    for (int i = 0; i < Data.autoData.getServiceTypes().size(); i++) {
                        List<Integer> allServiceType = Data.autoData.getServiceTypes().get(i).getSupportedRideTypes();

                        if (allServiceType != null && allServiceType.size() > 0) {
                            List<Integer> selectedServiceType = getServiceTypeSelected().getSupportedRideTypes();

                            for (int j = 0; j < selectedServiceType.size(); j++) {
                                int selRideType = selectedServiceType.get(j);
                                if (allServiceType.contains(selRideType)) {
                                    isSelectedTypeAvailable = true;
                                    break;
                                }
                            }

                            if (isSelectedTypeAvailable) {
                                break;
                            }
                        }
                    }
                }
            }
            if(!isSelectedTypeAvailable && Data.autoData != null && (Data.autoData.getServiceTypes() == null || Data.autoData.getServiceTypes().isEmpty())) {
                isSelectedTypeAvailable = true;
				defaultServiceType();
            }
            if (!isSelectedTypeAvailable) {
                if (getServiceTypes() != null && getServiceTypes().size() > 0) {
                    previousSelService = "" + getServiceTypeSelected().getName();
                    setServiceTypeSelected(getServiceTypes().get(0));
                } else {
//                    getServiceTypeSelected().setSupportedRideTypes(null);
                }
            }
            setIsServiceAvailable(isSelectedTypeAvailable);
        }
// *****************************************************************************************************************************

        if(getServiceTypeSelected().getSupportedRideTypes() != null && getServiceTypeSelected().getSupportedRideTypes().size() > 0) {
            for (Region region : regions){
                if(getServiceTypeSelected().getSupportedRideTypes().contains(region.getRideType())){
                    regionsTemp.add(region);
                }
            }
        } else {
            regionsTemp.addAll(regions);
        }
        return regionsTemp;
    }

    public String getPreviousSelService(){
        return previousSelService;
    }
    public void addRegion(Region region){
        if(regions == null){
            regions = new ArrayList<>();
        }
        regions.add(region);
    }
    public void clearRegions(){
        if(regions == null){
            regions = new ArrayList<>();
        }
        regions.clear();
    }

    private void setIsServiceAvailable(final boolean isServiceAv){
        if(lock == 1) {
            this.isServiceAv = isServiceAv;
            setLock(0);
        }
    }
    public boolean getIsServiceAvailable(){
        return  isServiceAv;
    }

    public void setLock(final int lockVal){
        this.lock = lockVal;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }

    public String getFarAwayCity() {
        return farAwayCity;
    }

    public void setFarAwayCity(String farAwayCity) {
        this.farAwayCity = farAwayCity;
    }

    public String getcEngagementId() {
        return cEngagementId;
    }

    public void setcEngagementId(String cEngagementId) {
        this.cEngagementId = cEngagementId;
    }

    public String getcDriverId() {
        return cDriverId;
    }

    public void setcDriverId(String cDriverId) {
        this.cDriverId = cDriverId;
    }

    public String getcSessionId() {
        return cSessionId;
    }

    public void setcSessionId(String cSessionId) {
        this.cSessionId = cSessionId;
    }

    public DriverInfo getAssignedDriverInfo() {
        return assignedDriverInfo;
    }

    public void setAssignedDriverInfo(DriverInfo assignedDriverInfo) {
        this.assignedDriverInfo = assignedDriverInfo;
    }

    public EndRideData getEndRideData() {
        return endRideData;
    }

    public void setEndRideData(EndRideData endRideData) {
        this.endRideData = endRideData;
    }

    public LatLng getPickupLatLng() {
        return pickupLatLng;
    }

    public void setPickupLatLng(LatLng pickupLatLng) {
        this.pickupLatLng = pickupLatLng;
    }

    public LatLng getDropLatLng() {
        return dropLatLng;
    }

    public void setDropLatLng(LatLng dropLatLng) {
        this.dropLatLng = dropLatLng;
    }

    public int getPickupPaymentOption() {
        return pickupPaymentOption;
    }

    public void setPickupPaymentOption(int pickupPaymentOption) {
        this.pickupPaymentOption = MyApplication.getInstance().getWalletCore().validatePaymentOptionForRidesOffering(pickupPaymentOption);
    }

    public CancelOptionsList getCancelOptionsList() {
        return cancelOptionsList;
    }

    public void setCancelOptionsList(CancelOptionsList cancelOptionsList) {
        this.cancelOptionsList = cancelOptionsList;
    }

    public ArrayList<FeedbackReason> getFeedbackReasons() {
        return feedbackReasons;
    }

    public void setFeedbackReasons(ArrayList<FeedbackReason> feedbackReasons) {
        this.feedbackReasons = feedbackReasons;
    }

    public boolean isSupportFeedbackSubmitted() {
        return supportFeedbackSubmitted;
    }

    public void setSupportFeedbackSubmitted(boolean supportFeedbackSubmitted) {
        this.supportFeedbackSubmitted = supportFeedbackSubmitted;
    }

    public LatLng getLastRefreshLatLng() {
        return lastRefreshLatLng;
    }

    public void setLastRefreshLatLng(LatLng lastRefreshLatLng) {
        this.lastRefreshLatLng = lastRefreshLatLng;
    }

    public ArrayList<PromoCoupon> getPromoCoupons() {
        return promoCoupons;
    }

    public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
        this.promoCoupons = promoCoupons;
    }

    public String getPickupAddress(LatLng latLng) {
        if(pickupAddressMappedLatLng == null || latLng == null){
            return "";
        }
        if(MapUtils.distance(pickupAddressMappedLatLng, latLng) > 50){
            return "";
        }
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress, LatLng latLng) {
        if(latLng == null || pickupAddressMappedLatLng == null
                || MapUtils.distance(pickupAddressMappedLatLng, latLng) > 50){
            this.pickupAddress = pickupAddress;
            this.pickupAddressMappedLatLng = latLng;
        }
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public NearbyPickupRegions getNearbyPickupRegionses() {
        return nearbyPickupRegionses;
    }

    public void setNearbyPickupRegionses(NearbyPickupRegions nearbyPickupRegionses) {
        this.nearbyPickupRegionses = nearbyPickupRegionses;
    }

    public String getInRideSendInviteTextBoldV2() {
        return inRideSendInviteTextBoldV2;
    }

    public void setInRideSendInviteTextBoldV2(String inRideSendInviteTextBoldV2) {
        this.inRideSendInviteTextBoldV2 = inRideSendInviteTextBoldV2;
    }

    public String getInRideSendInviteTextNormalV2() {
        return inRideSendInviteTextNormalV2;
    }

    public void setInRideSendInviteTextNormalV2(String inRideSendInviteTextNormalV2) {
        this.inRideSendInviteTextNormalV2 = inRideSendInviteTextNormalV2;
    }

    public int getRideStartInviteTextDeepIndexV2() {
        return rideStartInviteTextDeepIndexV2;
    }

    public void setRideStartInviteTextDeepIndexV2(int rideStartInviteTextDeepIndexV2) {
        this.rideStartInviteTextDeepIndexV2 = rideStartInviteTextDeepIndexV2;
    }

    public Integer getUseRecentLocAtRequest() {
        return useRecentLocAtRequest;
    }

    public void setUseRecentLocAtRequest(Integer useRecentLocAtRequest) {
        this.useRecentLocAtRequest = useRecentLocAtRequest;
    }

    public Double getUseRecentLocAutoSnapMinDistance() {
        return useRecentLocAutoSnapMinDistance;
    }

    public void setUseRecentLocAutoSnapMinDistance(Double useRecentLocAutoSnapMinDistance) {
        this.useRecentLocAutoSnapMinDistance = useRecentLocAutoSnapMinDistance;
    }

    public Double getUseRecentLocAutoSnapMaxDistance() {
        return useRecentLocAutoSnapMaxDistance;
    }

    public void setUseRecentLocAutoSnapMaxDistance(Double useRecentLocAutoSnapMaxDistance) {
        this.useRecentLocAutoSnapMaxDistance = useRecentLocAutoSnapMaxDistance;
    }

    public PlaceOrderResponse.ReferralPopupContent getReferralPopupContent() {
        return referralPopupContent;
    }

    public void setReferralPopupContent(PlaceOrderResponse.ReferralPopupContent referralPopupContent) {
        this.referralPopupContent = referralPopupContent;
    }

    public int getDropAddressId() {
        return dropAddressId;
    }

    public void setDropAddressId(int dropAddressId) {
        this.dropAddressId = dropAddressId;
    }

    public String getFuguChannelId() {
        return fuguChannelId;
    }

    public void setFuguChannelId(String fuguChannelId) {
        this.fuguChannelId = fuguChannelId;
    }

    public String getFuguChannelName() {
        return fuguChannelName;
    }

    public void setFuguChannelName(String fuguChannelName) {
        this.fuguChannelName = fuguChannelName;
    }

    public ArrayList<String> getFuguTags() {
        return fuguTags;
    }

    public void setFuguTags(ArrayList<String> fuguTags) {
        this.fuguTags = fuguTags;
    }

    public boolean isRazorpayEnabled() {
        return isRazorpayEnabled == 1;
    }
    public int getIsRazorpayEnabled() {
        return isRazorpayEnabled;
    }

    public void setIsRazorpayEnabled(int isRazorpayEnabled) {
        this.isRazorpayEnabled = isRazorpayEnabled;
    }

    public ArrayList<BidInfo> getBidInfos() {
        return bidInfos;
    }

    public void setBidInfos(ArrayList<BidInfo> bidInfos) {
        this.bidInfos = bidInfos;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public boolean getIsTipEnabled() {
        return isTipEnabled==1;
    }

    public boolean showRegionSpecificFare() {
        return showRegionSpecificFare == 1;
    }

    public void setShowRegionSpecificFare(int showRegionSpecificFare) {
        this.showRegionSpecificFare = showRegionSpecificFare;
    }

    public ArrayList<Corporate> getCorporatesFetched() {
        if(corporatesFetched == null){
            corporatesFetched = new ArrayList<>();
        }
        return corporatesFetched;
    }

    public void setCorporatesFetched(ArrayList<Corporate> corporates){
        this.corporatesFetched = corporates;
    }

    public ArrayList<ServiceType> getServiceTypes() {
        if(serviceTypes == null){
            serviceTypes = new ArrayList<>();
        }
        return serviceTypes;
    }

    public void setServiceTypes(ArrayList<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public ServiceType getServiceTypeSelected() {
        return serviceTypeSelected;
    }

    public void setServiceTypeSelected(ServiceType serviceTypeSelected) {
        this.serviceTypeSelected = serviceTypeSelected;
    }

    public Package getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(Package selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getResendEmailInvoiceEnabled() {
        return resendEmailInvoiceEnabled;
    }

    public void setResendEmailInvoiceEnabled(int resendEmailInvoiceEnabled) {
        this.resendEmailInvoiceEnabled = resendEmailInvoiceEnabled;
    }


    public List<String> getFaultConditions() {
        return FaultConditions;
    }

    public void setFaultConditions(List<String> faultConditions) {
        FaultConditions = faultConditions;
    }

	public int getIsReverseBid() {
		return isReverseBid;
	}

	public void setIsReverseBid(int isReverseBid) {
		this.isReverseBid = isReverseBid;
	}

	public long getBidTimeout() {
		return bidTimeout;
	}

	public void setBidTimeout(long bidTimeout) {
		this.bidTimeout = bidTimeout;
	}

	public long getBidRequestRideTimeout() {
		return bidRequestRideTimeout;
	}

	public void setBidRequestRideTimeout(long bidRequestRideTimeout) {
		this.bidRequestRideTimeout = bidRequestRideTimeout;
	}

	public int getNewBottomRequestUIEnabled() {
		return newBottomRequestUIEnabled;
	}

	public void setNewBottomRequestUIEnabled(int newBottomRequestUIEnabled) {
		this.newBottomRequestUIEnabled = newBottomRequestUIEnabled;
	}
}
