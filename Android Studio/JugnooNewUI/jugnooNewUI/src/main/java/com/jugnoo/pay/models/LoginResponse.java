package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;


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
	@SerializedName("autos")
	@Expose
	private Autos autos;
	@SerializedName("user_data")
	@Expose
	private UserData userData;
	@SerializedName("fresh")
	@Expose
	private Fresh fresh;
	@SerializedName("meals")
	@Expose
	private Meals meals;
	@SerializedName("grocery")
	@Expose
	private Grocery grocery;
	@SerializedName("delivery")
	@Expose
	private Delivery delivery;
	@SerializedName("mid")
	@Expose
	private String mid;
	@SerializedName("mkey")
	@Expose
	private String mkey;
	@SerializedName("token")
	@Expose
	private String token;
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

	public Fresh getFresh() {
		return fresh;
	}

	public void setFresh(Fresh fresh) {
		this.fresh = fresh;
	}

	public Meals getMeals() {
		return meals;
	}

	public void setMeals(Meals meals) {
		this.meals = meals;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Grocery getGrocery() {
		return grocery;
	}

	public void setGrocery(Grocery grocery) {
		this.grocery = grocery;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getMkey() {
		return mkey;
	}

	public void setMkey(String mkey) {
		this.mkey = mkey;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public class Delivery{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}
	}

	public class Meals{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}
	}

	public class Fresh{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}
	}

	public class Grocery{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}
	}

	public class Autos{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();
		@SerializedName("drivers")
		@Expose
		private List<Driver> drivers = new ArrayList<>();
		@SerializedName("regions")
		@Expose
		private List<Region> regions = new ArrayList<>();

		@SerializedName("priority_tip_category")
		@Expose
		private Integer priorityTipCategory;

		public Integer getBluetooth_tracker_enabled() {
			return bluetooth_tracker_enabled;
		}

		public void setBluetooth_tracker_enabled(Integer bluetooth_tracker_enabled) {
			this.bluetooth_tracker_enabled = bluetooth_tracker_enabled;
		}

		@SerializedName("bluetooth_tracker_enabled")
		@Expose
		private Integer bluetooth_tracker_enabled;

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

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}
	}

	public class UserData{
		@SerializedName("auth_key")
		@Expose
		private String authKey;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;
		@SerializedName("menu")
		@Expose
		private List<MenuInfo> menuInfoList;
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();
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
		@SerializedName("sharing_og_title")
		@Expose
		private String sharingOgTitle;
		@SerializedName("jeanie_intro_dialog_content")
		@Expose
		private JeanieIntroDialogContent jeanieIntroDialogContent;


		public List<MenuInfo> getMenuInfoList() {
			return menuInfoList;
		}

		public void setMenuInfoList(List<MenuInfo> menuInfoList) {
			this.menuInfoList = menuInfoList;
		}

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
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

		public String getReferralMessage() {
			return referralMessage;
		}

		public void setReferralMessage(String referralMessage) {
			this.referralMessage = referralMessage;
		}

		public String getFbShareCaption() {
			return fbShareCaption;
		}

		public void setFbShareCaption(String fbShareCaption) {
			this.fbShareCaption = fbShareCaption;
		}

		public String getFbShareDescription() {
			return fbShareDescription;
		}

		public void setFbShareDescription(String fbShareDescription) {
			this.fbShareDescription = fbShareDescription;
		}

		public String getReferralCaption() {
			return referralCaption;
		}

		public void setReferralCaption(String referralCaption) {
			this.referralCaption = referralCaption;
		}

		public String getReferralEmailSubject() {
			return referralEmailSubject;
		}

		public void setReferralEmailSubject(String referralEmailSubject) {
			this.referralEmailSubject = referralEmailSubject;
		}

		public String getReferralPopupText() {
			return referralPopupText;
		}

		public void setReferralPopupText(String referralPopupText) {
			this.referralPopupText = referralPopupText;
		}

		public String getInviteEarnShortMsg() {
			return inviteEarnShortMsg;
		}

		public void setInviteEarnShortMsg(String inviteEarnShortMsg) {
			this.inviteEarnShortMsg = inviteEarnShortMsg;
		}

		public String getInviteEarnMoreInfo() {
			return inviteEarnMoreInfo;
		}

		public void setInviteEarnMoreInfo(String inviteEarnMoreInfo) {
			this.inviteEarnMoreInfo = inviteEarnMoreInfo;
		}

		public String getReferralSharingMessage() {
			return referralSharingMessage;
		}

		public void setReferralSharingMessage(String referralSharingMessage) {
			this.referralSharingMessage = referralSharingMessage;
		}

		public JeanieIntroDialogContent getJeanieIntroDialogContent() {
			return jeanieIntroDialogContent;
		}

		public void setJeanieIntroDialogContent(JeanieIntroDialogContent jeanieIntroDialogContent) {
			this.jeanieIntroDialogContent = jeanieIntroDialogContent;
		}

		public String getSharingOgTitle() {
			return sharingOgTitle;
		}

		public void setSharingOgTitle(String sharingOgTitle) {
			this.sharingOgTitle = sharingOgTitle;
		}


		public String getAuthKey() {
			return authKey;
		}

		public void setAuthKey(String authKey) {
			this.authKey = authKey;
		}

		public String getPhoneNo() {
			return phoneNo;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}
	}

}
