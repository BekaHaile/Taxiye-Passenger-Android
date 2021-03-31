package product.clicklabs.jugnoo.datastructure;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.model.Campaigns;
import product.clicklabs.jugnoo.home.models.SafetyInfoData;
import product.clicklabs.jugnoo.retrofit.model.Corporate;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
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
    private SearchResult searchResultPickup;
    private LatLng dropLatLng;
    private String dropAddress = "";
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
    private double noDriverFoundTip;
    private ArrayList<FindADriverResponse.RequestLevels> requestLevels;

    // RENTAL

    private List<String> FaultConditions;

    private int newBottomRequestUIEnabled;
    private double initialBidValue, changedBidValue;
    private int customerVerificationStatus = 0;
    private boolean multiDestAllowed;

	private SafetyInfoData safetyInfoData;

    private int poolSeatsSelected = 1;

    public AutoData(String destinationHelpText, String rideSummaryBadText, String cancellationChargesPopupTextLine1, String cancellationChargesPopupTextLine2,
                    String inRideSendInviteTextBold, String inRideSendInviteTextNormal, String confirmScreenFareEstimateEnable,
                    String poolDestinationPopupText1, String poolDestinationPopupText2, String poolDestinationPopupText3,
                    int rideEndGoodFeedbackViewType, String rideEndGoodFeedbackText, String baseFarePoolText, int referAllStatus, String referAllText,
                    String referAllTitle, int referAllStatusLogin, String referAllTextLogin, String referAllTitleLogin,
                    NearbyPickupRegions nearbyPickupRegionses, String inRideSendInviteTextBoldV2, String inRideSendInviteTextNormalV2,
                    int rideStartInviteTextDeepIndexV2, int isRazorpayEnabled,int isTipEnabled, int showRegionSpecificFare, int resendEmailInvoiceEnabled,int bluetoothEnabled,int customerVerificationStatus) {
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
		this.customerVerificationStatus = customerVerificationStatus;
	}

	public void defaultServiceType() {
		ArrayList<Integer> rideTypes = new ArrayList<>();
		ArrayList<Integer> regionIds = new ArrayList<>();
        regionIds.add(-1);
		rideTypes.add(ServiceTypeValue.NORMAL.getType());
		rideTypes.add(ServiceTypeValue.POOL.getType());
		rideTypes.add(ServiceTypeValue.BIKE_RENTAL.getType());
		int scheduleRideEnabled = Prefs.with(MyApplication.getInstance()).getBoolean(Constants.SCHEDULE_RIDE_ENABLED, false) ? 1 : 0;
		serviceTypeSelected = new ServiceType("On Demand", "", "", 1, rideTypes, regionIds, null, "", scheduleRideEnabled, true);

		getServiceTypes().add(serviceTypeSelected);


        ArrayList<Integer> rideTypesRental = new ArrayList<>();
        ArrayList<Integer> regionIdsRental = new ArrayList<>();
        regionIdsRental.add(-1);
        rideTypesRental.add(ServiceTypeValue.RENTAL.getType());
		getServiceTypes().add(new ServiceType("Rental", "", "", 1, rideTypesRental, regionIdsRental, null, "", scheduleRideEnabled, false));

        ArrayList<Integer> rideTypesOutStation = new ArrayList<>();
        ArrayList<Integer> regionIdsOutStation = new ArrayList<>();
        regionIdsOutStation.add(-1);
        rideTypesOutStation.add(ServiceTypeValue.OUTSTATION.getType());
		getServiceTypes().add(new ServiceType("Out Station", "", "", 1, rideTypesOutStation, regionIdsOutStation, null, "", scheduleRideEnabled, false));
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

    public void clearRegionFares(){
    	if(regions != null){
    		for(Region region : regions){
    			region.setRegionFare(null);
			}
		}
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

		boolean hideRegionsWithNoDrivers = Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_HIDE_REGIONS_WITH_NO_DRIVERS, 0) == 1;

        if(getServiceTypeSelected().getSupportedRideTypes() != null && getServiceTypeSelected().getSupportedRideTypes().size() > 0) {
            for (Region region : regions){
                if(getServiceTypeSelected().getSupportedRideTypes().contains(region.getRideType())
						&& (!hideRegionsWithNoDrivers || !region.getEta().equalsIgnoreCase("-"))){
                    regionsTemp.add(region);
                }
            }
			if(regionsTemp.size() == 0){
				for (Region region : regions){
					if(getServiceTypeSelected().getSupportedRideTypes().contains(region.getRideType())){
						regionsTemp.add(region);
					}
				}
			}

        } else {
			for (Region region : regions){
				if(!hideRegionsWithNoDrivers || !region.getEta().equalsIgnoreCase("-")){
					regionsTemp.add(region);
				}
			}
			if(regionsTemp.size() == 0){
				regionsTemp.addAll(regions);
			}
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
        return searchResultPickup != null ? searchResultPickup.getLatLng() : null;
    }


    public LatLng getDropLatLng() {
        return dropLatLng;
    }

    public void setDropLatLng(LatLng dropLatLng) {
        this.dropLatLng = dropLatLng;
        if(dropLatLng==null)
            multiDestList.clear();
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
		if(searchResultPickup == null || searchResultPickup.getAddress().equalsIgnoreCase(Constants.UNNAMED)){
			return "";
		}
		if(latLng != null && MapUtils.distance(searchResultPickup.getLatLng(), latLng) > Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION){
			return "";
		}
		return searchResultPickup != null ? searchResultPickup.getAddress() : "";
    }
    public SearchResult getPickupSearchResult() {
		return searchResultPickup;
    }

    public void setPickupSearchResult(SearchResult searchResult) {
    	if(searchResult != null && TextUtils.isEmpty(searchResult.getName())){
    		searchResult.setName(searchResult.getAddress());
		}
    	this.searchResultPickup = searchResult;
    }
    public void setPickupSearchResult(String address, LatLng latLng) {
		searchResultPickup = new SearchResult(address, address, "", latLng.latitude, latLng.longitude);
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
        return getNewUIFlag() || showRegionSpecificFare == 1;
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
        if(serviceTypes!=null&&serviceTypes.size()>0) {
            this.serviceTypes = serviceTypes;
        }
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

    public static ArrayList<MultiDestData> parseMultiDestList(JSONArray multidest){

//For login using access token


        ArrayList<AutoData.MultiDestData> multiDestData=new ArrayList<>();
        if(multidest.length()<=0)return null;
        for(int i=0;i<multidest.length();i++){
            LatLng latLng=new LatLng(multidest.optJSONObject(i).optDouble("chosen_drop_latitude"),multidest.optJSONObject(i).optDouble("chosen_drop_longitude"));
            String stopAddress=multidest.optJSONObject(i).optString("chosen_address");
            int stopReachedStatus=multidest.optJSONObject(i).optInt("stop_status");
            int stopId=multidest.optJSONObject(i).optInt("stop_id");
            int orderId=multidest.optJSONObject(i).optInt("order_id");

            multiDestData.add(Data.autoData.new MultiDestData(latLng,stopAddress,0,stopReachedStatus,stopId,orderId));
        }
        return multiDestData;

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

	public double getInitialBidValue() {
		return initialBidValue;
	}

	public void setInitialBidValue(double initialBidValue) {
		this.initialBidValue = initialBidValue;
		setChangedBidValue(initialBidValue);
	}

	public double getChangedBidValue() {
		return changedBidValue;
	}

	public void setChangedBidValue(double changedBidValue) {
		this.changedBidValue = changedBidValue;
	}

    public double getNoDriverFoundTip() {
        return noDriverFoundTip;
    }

    public void setNoDriverFoundTip(double noDriverFoundTip) {
        this.noDriverFoundTip = noDriverFoundTip;
    }

    public ArrayList<FindADriverResponse.RequestLevels> getRequestLevels() {
        if(requestLevels == null) {
            FindADriverResponse.RequestLevels level = new FindADriverResponse().new RequestLevels();
            level.setLevel(0);
            level.setTipEnabled(0);
            level.setEnabled(1);
            requestLevels = new ArrayList<>();
        }
        return requestLevels;
    }

    public void setRequestLevels(ArrayList<FindADriverResponse.RequestLevels> requestLevels) {
        this.requestLevels = requestLevels;
    }

    public int getCustomerVerificationStatus() {
        return customerVerificationStatus;
    }

    public void setCustomerVerificationStatus(int customerVerificationStatus) {
        this.customerVerificationStatus = customerVerificationStatus;
    }

    public boolean getMultiDestAllowed(){
        return multiDestAllowed;
    }

    public void setMultiDestAllowed(boolean multiDestAllowed){
        this.multiDestAllowed=multiDestAllowed;
    }

    public ArrayList<MultiDestData> getMultiDestList() {
        return multiDestList;
    }

    public void addMultiDestLatlng(MultiDestData multiDest) {
        this.multiDestList.add(multiDest);
    }

    private ArrayList<MultiDestData> multiDestList=new ArrayList<>();

    public class MultiDestData{

        private LatLng latlng;
        private String dropAddress;
        private int dropAddressId;
        private int stopReachStatus;
        private int stopId;
        private int orderId;
        private String dropAddressForName="";

        public int getStopId() {
            return stopId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setStopId(int stopId) {
            this.stopId = stopId;
        }

        public LatLng getLatlng() {
            return latlng;
        }

        public String getDropAddress() {
            return dropAddress;
        }

        public String getDropAddressForName() { return dropAddressForName; }

        public void setDropAddressForName(String dropAddressForName) { this.dropAddressForName = dropAddressForName; }

        public int getDropAddressId() {
            return dropAddressId;
        }
        public int getStopReachStatus(){return stopReachStatus; }
        public void setStopReachStatus(int stopReachStatus){this.stopReachStatus=stopReachStatus;}

        public MultiDestData(LatLng latLng, String dropAddress, int dropAddressId,int stopReachStatus){
            this.dropAddress=dropAddress;
            this.latlng=latLng;
            this.dropAddressId=dropAddressId;
            this.stopReachStatus=stopReachStatus;


        }
        public MultiDestData(LatLng latLng, String dropAddress, int dropAddressId,int stopReachStatus,int stopId,int orderId){
            this.dropAddress=dropAddress;
            this.latlng=latLng;
            this.dropAddressId=dropAddressId;
            this.stopReachStatus=stopReachStatus;
            this.orderId=orderId;
            this.stopId=stopId;


        }

    }

	public SafetyInfoData getSafetyInfoData() {
		return safetyInfoData;
	}

	public void setSafetyInfoData(SafetyInfoData safetyInfoData) {
		this.safetyInfoData = safetyInfoData;
	}


    public int getPoolSeatsSelected() {
        return poolSeatsSelected;
    }

    public void setPoolSeatsSelected(int poolSeatsSelected) {
        this.poolSeatsSelected = poolSeatsSelected;
    }

}
