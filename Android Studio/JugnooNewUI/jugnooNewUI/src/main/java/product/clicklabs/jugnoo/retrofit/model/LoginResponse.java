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
	@SerializedName("login")
	@Expose
	private Login login;
	@SerializedName("drivers")
	@Expose
	private List<Driver> drivers = new ArrayList<>();
	@SerializedName("cancellation")
	@Expose
	private Cancellation cancellation;
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
	@SerializedName("bad_rating_reasons")
	@Expose
	private List<String> badRatingReasons = new ArrayList<>();
	@SerializedName("referral_sharing_message")
	@Expose
	private String referralSharingMessage;

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
	 * @return The login
	 */
	public Login getLogin() {
		return login;
	}

	/**
	 * @param login The login
	 */
	public void setLogin(Login login) {
		this.login = login;
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
	 * @return The cancellation
	 */
	public Cancellation getCancellation() {
		return cancellation;
	}

	/**
	 * @param cancellation The cancellation
	 */
	public void setCancellation(Cancellation cancellation) {
		this.cancellation = cancellation;
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
	 * @return The badRatingReasons
	 */
	public List<String> getBadRatingReasons() {
		return badRatingReasons;
	}

	/**
	 * @param badRatingReasons The bad_rating_reasons
	 */
	public void setBadRatingReasons(List<String> badRatingReasons) {
		this.badRatingReasons = badRatingReasons;
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

	public class Login {

		@SerializedName("current_user_status")
		@Expose
		private Integer currentUserStatus;
		@SerializedName("support_number")
		@Expose
		private String supportNumber;
		@SerializedName("priority_tip_category")
		@Expose
		private Integer priorityTipCategory;
		@SerializedName("promotions")
		@Expose
		private List<Promotion> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<Coupon> coupons = new ArrayList<>();
		@SerializedName("fare_structure")
		@Expose
		private List<FareStructure> fareStructure = new ArrayList<>();
		@SerializedName("far_away_city")
		@Expose
		private String farAwayCity;
		@SerializedName("fare_factor")
		@Expose
		private Double fareFactor;
		@SerializedName("regions")
		@Expose
		private List<Region> regions = new ArrayList<>();
		@SerializedName("fresh_available")
		@Expose
		private Integer freshAvailable;
		@SerializedName("is_pool_enabled")
		@Expose
		private Integer isPoolEnabled;
		@SerializedName("menu")
		@Expose
		private List<MenuInfo> menuInfoList;



		@SerializedName("campaigns")
		@Expose
		private Campaigns campaigns;

		/**
		 *
		 * @return
		 * The campaigns
		 */
		public Campaigns getCampaigns() {
			return campaigns;
		}

		/**
		 *
		 * @param campaigns
		 * The campaigns
		 */
		public void setCampaigns(Campaigns campaigns) {
			this.campaigns = campaigns;
		}

		/**
		 * @return The currentUserStatus
		 */
		public Integer getCurrentUserStatus() {
			return currentUserStatus;
		}

		/**
		 * @param currentUserStatus The current_user_status
		 */
		public void setCurrentUserStatus(Integer currentUserStatus) {
			this.currentUserStatus = currentUserStatus;
		}

		/**
		 * @return The priorityTipCategory
		 */
		public Integer getPriorityTipCategory() {
			return priorityTipCategory;
		}

		/**
		 * @param priorityTipCategory The priority_tip_category
		 */
		public void setPriorityTipCategory(Integer priorityTipCategory) {
			this.priorityTipCategory = priorityTipCategory;
		}

		/**
		 * @return The promotions
		 */
		public List<Promotion> getPromotions() {
			return promotions;
		}

		/**
		 * @param promotions The promotions
		 */
		public void setPromotions(List<Promotion> promotions) {
			this.promotions = promotions;
		}

		/**
		 * @return The coupons
		 */
		public List<Coupon> getCoupons() {
			return coupons;
		}

		/**
		 * @param coupons The coupons
		 */
		public void setCoupons(List<Coupon> coupons) {
			this.coupons = coupons;
		}

		/**
		 * @return The fareStructure
		 */
		public List<FareStructure> getFareStructure() {
			return fareStructure;
		}

		/**
		 * @param fareStructure The fare_structure
		 */
		public void setFareStructure(List<FareStructure> fareStructure) {
			this.fareStructure = fareStructure;
		}

		public String getFarAwayCity() {
			return farAwayCity;
		}

		public void setFarAwayCity(String farAwayCity) {
			this.farAwayCity = farAwayCity;
		}

		public String getSupportNumber() {
			return supportNumber;
		}

		public void setSupportNumber(String supportNumber) {
			this.supportNumber = supportNumber;
		}

		public Double getFareFactor() {
			return fareFactor;
		}

		public void setFareFactor(Double fareFactor) {
			this.fareFactor = fareFactor;
		}

		public List<Region> getRegions() {
			return regions;
		}

		public void setRegions(List<Region> regions) {
			this.regions = regions;
		}

		public Integer getFreshAvailable() {
			return freshAvailable;
		}

		public void setFreshAvailable(Integer freshAvailable) {
			this.freshAvailable = freshAvailable;
		}

		public List<MenuInfo> getMenuInfoList() {
			return menuInfoList;
		}

		public void setMenuInfoList(List<MenuInfo> menuInfoList) {
			this.menuInfoList = menuInfoList;
		}

		public Integer getIsPoolEnabled() {
			return isPoolEnabled;
		}

		public void setIsPoolEnabled(Integer isPoolEnabled) {
			this.isPoolEnabled = isPoolEnabled;
		}
	}
}
