package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.Region;

/**
 * Created by shankar on 1/5/16.
 */
public class LoginResponse {
	public class Cancellation {

		@SerializedName("message")
		@Expose
		private String message;
		@SerializedName("reasons")
		@Expose
		private List<String> reasons = new ArrayList<String>();
		@SerializedName("addn_reason")
		@Expose
		private String addnReason;

		/**
		 * @return The message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message The message
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * @return The reasons
		 */
		public List<String> getReasons() {
			return reasons;
		}

		/**
		 * @param reasons The reasons
		 */
		public void setReasons(List<String> reasons) {
			this.reasons = reasons;
		}

		/**
		 * @return The addnReason
		 */
		public String getAddnReason() {
			return addnReason;
		}

		/**
		 * @param addnReason The addn_reason
		 */
		public void setAddnReason(String addnReason) {
			this.addnReason = addnReason;
		}

	}

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("drivers")
	@Expose
	private List<Driver> drivers = new ArrayList<>();
	@SerializedName("referral_message")
	@Expose
	private String referralMessage;
	@SerializedName("fb_share_caption")
	@Expose
	private String fbShareCaption;
	@SerializedName("fb_share_description")
	@Expose
	private String fbShareDescription;
	@SerializedName("referral_caption")
	@Expose
	private String referralCaption;
	@SerializedName("referral_email_subject")
	@Expose
	private String referralEmailSubject;
	@SerializedName("referral_popup_text")
	@Expose
	private String referralPopupText;
	@SerializedName("invite_earn_short_msg")
	@Expose
	private String inviteEarnShortMsg;
	@SerializedName("invite_earn_more_info")
	@Expose
	private String inviteEarnMoreInfo;
	@SerializedName("referral_sharing_message")
	@Expose
	private String referralSharingMessage;
	@SerializedName("autos")
	@Expose
	private Autos autos;
	@SerializedName("user_data")
	@Expose
	private UserData userData;

	/**
	 * @return The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 * @return The drivers
	 */
	public List<Driver> getDrivers() {
		return drivers;
	}

	/**
	 * @param drivers The drivers
	 */
	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	/**
	 * @return The referralMessage
	 */
	public String getReferralMessage() {
		return referralMessage;
	}

	/**
	 * @param referralMessage The referral_message
	 */
	public void setReferralMessage(String referralMessage) {
		this.referralMessage = referralMessage;
	}

	/**
	 * @return The fbShareCaption
	 */
	public String getFbShareCaption() {
		return fbShareCaption;
	}

	/**
	 * @param fbShareCaption The fb_share_caption
	 */
	public void setFbShareCaption(String fbShareCaption) {
		this.fbShareCaption = fbShareCaption;
	}

	/**
	 * @return The fbShareDescription
	 */
	public String getFbShareDescription() {
		return fbShareDescription;
	}

	/**
	 * @param fbShareDescription The fb_share_description
	 */
	public void setFbShareDescription(String fbShareDescription) {
		this.fbShareDescription = fbShareDescription;
	}

	/**
	 * @return The referralCaption
	 */
	public String getReferralCaption() {
		return referralCaption;
	}

	/**
	 * @param referralCaption The referral_caption
	 */
	public void setReferralCaption(String referralCaption) {
		this.referralCaption = referralCaption;
	}

	/**
	 * @return The referralEmailSubject
	 */
	public String getReferralEmailSubject() {
		return referralEmailSubject;
	}

	/**
	 * @param referralEmailSubject The referral_email_subject
	 */
	public void setReferralEmailSubject(String referralEmailSubject) {
		this.referralEmailSubject = referralEmailSubject;
	}

	/**
	 * @return The referralPopupText
	 */
	public String getReferralPopupText() {
		return referralPopupText;
	}

	/**
	 * @param referralPopupText The referral_popup_text
	 */
	public void setReferralPopupText(String referralPopupText) {
		this.referralPopupText = referralPopupText;
	}

	/**
	 * @return The inviteEarnShortMsg
	 */
	public String getInviteEarnShortMsg() {
		return inviteEarnShortMsg;
	}

	/**
	 * @param inviteEarnShortMsg The invite_earn_short_msg
	 */
	public void setInviteEarnShortMsg(String inviteEarnShortMsg) {
		this.inviteEarnShortMsg = inviteEarnShortMsg;
	}

	/**
	 * @return The inviteEarnMoreInfo
	 */
	public String getInviteEarnMoreInfo() {
		return inviteEarnMoreInfo;
	}

	/**
	 * @param inviteEarnMoreInfo The invite_earn_more_info
	 */
	public void setInviteEarnMoreInfo(String inviteEarnMoreInfo) {
		this.inviteEarnMoreInfo = inviteEarnMoreInfo;
	}

	/**
	 * @return The referralSharingMessage
	 */
	public String getReferralSharingMessage() {
		return referralSharingMessage;
	}

	/**
	 * @param referralSharingMessage The referral_sharing_message
	 */
	public void setReferralSharingMessage(String referralSharingMessage) {
		this.referralSharingMessage = referralSharingMessage;
	}

	public Autos getAutos() {
		return autos;
	}

	public void setAutos(Autos autos) {
		this.autos = autos;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public class Autos{
		@SerializedName("drivers")
		@Expose
		private List<Driver> drivers = new ArrayList<>();
		@SerializedName("regions")
		@Expose
		private List<Region> regions = new ArrayList<>();

		@SerializedName("priority_tip_category")
		@Expose
		private Integer priorityTipCategory;
		@SerializedName("fare_factor")
		@Expose
		private Double fareFactor;
		@SerializedName("driver_fare_factor")
		@Expose
		private Double driverFareFactor;
		@SerializedName("far_away_city")
		@Expose
		private String farAwayCity;
		@SerializedName("campaigns")
		@Expose
		private Campaigns campaigns;
		@SerializedName("fare_structure")
		@Expose
		private List<FareStructure> fareStructure = new ArrayList<>();
		@SerializedName("current_user_status")
		@Expose
		private Integer currentUserStatus;
		@SerializedName("cancellation")
		@Expose
		private Cancellation cancellation;
		@SerializedName("bad_rating_reasons")
		@Expose
		private List<String> badRatingReasons = new ArrayList<>();

		public List<Driver> getDrivers() {
			return drivers;
		}

		public void setDrivers(List<Driver> drivers) {
			this.drivers = drivers;
		}

		public List<Region> getRegions() {
			return regions;
		}

		public void setRegions(List<Region> regions) {
			this.regions = regions;
		}

		public Integer getPriorityTipCategory() {
			return priorityTipCategory;
		}

		public void setPriorityTipCategory(Integer priorityTipCategory) {
			this.priorityTipCategory = priorityTipCategory;
		}

		public Double getFareFactor() {
			return fareFactor;
		}

		public void setFareFactor(Double fareFactor) {
			this.fareFactor = fareFactor;
		}

		public Double getDriverFareFactor() {
			return driverFareFactor;
		}

		public void setDriverFareFactor(Double driverFareFactor) {
			this.driverFareFactor = driverFareFactor;
		}

		public String getFarAwayCity() {
			return farAwayCity;
		}

		public void setFarAwayCity(String farAwayCity) {
			this.farAwayCity = farAwayCity;
		}

		public Campaigns getCampaigns() {
			return campaigns;
		}

		public void setCampaigns(Campaigns campaigns) {
			this.campaigns = campaigns;
		}

		public List<FareStructure> getFareStructure() {
			return fareStructure;
		}

		public void setFareStructure(List<FareStructure> fareStructure) {
			this.fareStructure = fareStructure;
		}

		public Integer getCurrentUserStatus() {
			return currentUserStatus;
		}

		public void setCurrentUserStatus(Integer currentUserStatus) {
			this.currentUserStatus = currentUserStatus;
		}

		public Cancellation getCancellation() {
			return cancellation;
		}

		public void setCancellation(Cancellation cancellation) {
			this.cancellation = cancellation;
		}

		public List<String> getBadRatingReasons() {
			return badRatingReasons;
		}

		public void setBadRatingReasons(List<String> badRatingReasons) {
			this.badRatingReasons = badRatingReasons;
		}
	}

	public class UserData{
		@SerializedName("menu")
		@Expose
		private List<MenuInfo> menuInfoList;
		@SerializedName("promotions")
		@Expose
		private List<Promotion> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<Coupon> coupons = new ArrayList<>();
		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("support_number")
		@Expose
		private String supportNumber;
		@SerializedName("meals_enabled")
		@Expose
		private Integer mealsEnabled;
		@SerializedName("fresh_enabled")
		@Expose
		private Integer freshEnabled;
		@SerializedName("delivery_enabled")
		@Expose
		private Integer deliveryEnabled;

		public List<MenuInfo> getMenuInfoList() {
			return menuInfoList;
		}

		public void setMenuInfoList(List<MenuInfo> menuInfoList) {
			this.menuInfoList = menuInfoList;
		}

		public List<Promotion> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<Promotion> promotions) {
			this.promotions = promotions;
		}

		public List<Coupon> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<Coupon> coupons) {
			this.coupons = coupons;
		}

		public Integer getCityId() {
			return cityId;
		}

		public void setCityId(Integer cityId) {
			this.cityId = cityId;
		}

		public String getSupportNumber() {
			return supportNumber;
		}

		public void setSupportNumber(String supportNumber) {
			this.supportNumber = supportNumber;
		}

		public Integer getMealsEnabled() {
			return mealsEnabled;
		}

		public void setMealsEnabled(Integer mealsEnabled) {
			this.mealsEnabled = mealsEnabled;
		}

		public Integer getFreshEnabled() {
			return freshEnabled;
		}

		public void setFreshEnabled(Integer freshEnabled) {
			this.freshEnabled = freshEnabled;
		}

		public Integer getDeliveryEnabled() {
			return deliveryEnabled;
		}

		public void setDeliveryEnabled(Integer deliveryEnabled) {
			this.deliveryEnabled = deliveryEnabled;
		}
	}

}
