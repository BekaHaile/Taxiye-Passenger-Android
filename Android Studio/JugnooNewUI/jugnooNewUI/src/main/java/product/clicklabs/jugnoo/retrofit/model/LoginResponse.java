package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

	public class EmergencyContact {

		@SerializedName("id")
		@Expose
		private Integer id;
		@SerializedName("user_id")
		@Expose
		private Integer userId;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("email")
		@Expose
		private String email;
		@SerializedName("verification_status")
		@Expose
		private Integer verificationStatus;
		@SerializedName("user_verification_token")
		@Expose
		private String userVerificationToken;
		@SerializedName("contact_verification_token")
		@Expose
		private String contactVerificationToken;
		@SerializedName("requests_made")
		@Expose
		private Integer requestsMade;
		@SerializedName("requested_on")
		@Expose
		private String requestedOn;
		@SerializedName("verified_on")
		@Expose
		private String verifiedOn;

		/**
		 * @return The id
		 */
		public Integer getId() {
			return id;
		}

		/**
		 * @param id The id
		 */
		public void setId(Integer id) {
			this.id = id;
		}

		/**
		 * @return The userId
		 */
		public Integer getUserId() {
			return userId;
		}

		/**
		 * @param userId The user_id
		 */
		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		/**
		 * @return The phoneNo
		 */
		public String getPhoneNo() {
			return phoneNo;
		}

		/**
		 * @param phoneNo The phone_no
		 */
		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		/**
		 * @return The name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name The name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return The email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * @param email The email
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * @return The verificationStatus
		 */
		public Integer getVerificationStatus() {
			return verificationStatus;
		}

		/**
		 * @param verificationStatus The verification_status
		 */
		public void setVerificationStatus(Integer verificationStatus) {
			this.verificationStatus = verificationStatus;
		}

		/**
		 * @return The userVerificationToken
		 */
		public String getUserVerificationToken() {
			return userVerificationToken;
		}

		/**
		 * @param userVerificationToken The user_verification_token
		 */
		public void setUserVerificationToken(String userVerificationToken) {
			this.userVerificationToken = userVerificationToken;
		}

		/**
		 * @return The contactVerificationToken
		 */
		public String getContactVerificationToken() {
			return contactVerificationToken;
		}

		/**
		 * @param contactVerificationToken The contact_verification_token
		 */
		public void setContactVerificationToken(String contactVerificationToken) {
			this.contactVerificationToken = contactVerificationToken;
		}

		/**
		 * @return The requestsMade
		 */
		public Integer getRequestsMade() {
			return requestsMade;
		}

		/**
		 * @param requestsMade The requests_made
		 */
		public void setRequestsMade(Integer requestsMade) {
			this.requestsMade = requestsMade;
		}

		/**
		 * @return The requestedOn
		 */
		public String getRequestedOn() {
			return requestedOn;
		}

		/**
		 * @param requestedOn The requested_on
		 */
		public void setRequestedOn(String requestedOn) {
			this.requestedOn = requestedOn;
		}

		/**
		 * @return The verifiedOn
		 */
		public String getVerifiedOn() {
			return verifiedOn;
		}

		/**
		 * @param verifiedOn The verified_on
		 */
		public void setVerifiedOn(String verifiedOn) {
			this.verifiedOn = verifiedOn;
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
	private List<Driver> drivers = new ArrayList<Driver>();
	@SerializedName("eta")
	@Expose
	private Integer eta;
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
	private List<String> badRatingReasons = new ArrayList<String>();
	@SerializedName("last_ride")
	@Expose
	private Object lastRide;
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
	 * @return The eta
	 */
	public Integer getEta() {
		return eta;
	}

	/**
	 * @param eta The eta
	 */
	public void setEta(Integer eta) {
		this.eta = eta;
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
	 * @return The lastRide
	 */
	public Object getLastRide() {
		return lastRide;
	}

	/**
	 * @param lastRide The last_ride
	 */
	public void setLastRide(Object lastRide) {
		this.lastRide = lastRide;
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

		@SerializedName("flag")
		@Expose
		private Integer flag;
		@SerializedName("user_name")
		@Expose
		private String userName;
		@SerializedName("user_image")
		@Expose
		private String userImage;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;
		@SerializedName("user_email")
		@Expose
		private String userEmail;
		@SerializedName("access_token")
		@Expose
		private String accessToken;
		@SerializedName("current_user_status")
		@Expose
		private Integer currentUserStatus;
		@SerializedName("popup")
		@Expose
		private Integer popup;
		@SerializedName("can_change_location")
		@Expose
		private Integer canChangeLocation;
		@SerializedName("can_schedule")
		@Expose
		private Integer canSchedule;
		@SerializedName("scheduling_limit")
		@Expose
		private Integer schedulingLimit;
		@SerializedName("gcm_intent")
		@Expose
		private Integer gcmIntent;
		@SerializedName("christmas_icon_enable")
		@Expose
		private Integer christmasIconEnable;
		@SerializedName("user_id")
		@Expose
		private Integer userId;
		@SerializedName("referral_code")
		@Expose
		private String referralCode;
		@SerializedName("user_identifier")
		@Expose
		private String userIdentifier;
		@SerializedName("email_verification_status")
		@Expose
		private Integer emailVerificationStatus;
		@SerializedName("auth_key")
		@Expose
		private String authKey;
		@SerializedName("jugnoo_balance")
		@Expose
		private Integer jugnooBalance;
		@SerializedName("paytm_enabled")
		@Expose
		private Integer paytmEnabled;
		@SerializedName("num_coupons_available")
		@Expose
		private Integer numCouponsAvailable;
		@SerializedName("support_number")
		@Expose
		private String supportNumber;
		@SerializedName("emergency_contacts")
		@Expose
		private List<EmergencyContact> emergencyContacts = new ArrayList<EmergencyContact>();
		@SerializedName("refer_all_status")
		@Expose
		private Integer referAllStatus;
		@SerializedName("user_app_monitoring")
		@Expose
		private String userAppMonitoring;
		@SerializedName("jugnoo_cash_tnc")
		@Expose
		private String jugnooCashTnc;
		@SerializedName("refer_all_text")
		@Expose
		private String referAllText;
		@SerializedName("refer_all_title")
		@Expose
		private String referAllTitle;
		@SerializedName("jugnoo_sticky")
		@Expose
		private Integer jugnooSticky;
		@SerializedName("jugnoo_fb_banner")
		@Expose
		private String jugnooFbBanner;
		@SerializedName("user_app_monitoring_duration")
		@Expose
		private String userAppMonitoringDuration;
		@SerializedName("in_app_support_panel_version")
		@Expose
		private String inAppSupportPanelVersion;
		@SerializedName("get_gogu")
		@Expose
		private String getGogu;
		@SerializedName("invite_earn_screen_image_ios")
		@Expose
		private String inviteEarnScreenImageIos;
		@SerializedName("invite_earn_screen_image_android")
		@Expose
		private String inviteEarnScreenImageAndroid;
		@SerializedName("public_access_token")
		@Expose
		private String publicAccessToken;
		@SerializedName("t20_wc_enable")
		@Expose
		private String t20WcEnable;
		@SerializedName("t20_wc_schedule_version")
		@Expose
		private String t20WcScheduleVersion;
		@SerializedName("t20_wc_info_text")
		@Expose
		private String t20WcInfoText;
		@SerializedName("game_predict_enable")
		@Expose
		private String gamePredictEnable;
		@SerializedName("game_predict_url")
		@Expose
		private String gamePredictUrl;
		@SerializedName("game_predict_view_data")
		@Expose
		private String gamePredictViewData;
		@SerializedName("customer_location_update_interval")
		@Expose
		private String customerLocationUpdateInterval;
		@SerializedName("fare_factor")
		@Expose
		private Integer fareFactor;
		@SerializedName("priority_tip_category")
		@Expose
		private Integer priorityTipCategory;
		@SerializedName("promotions")
		@Expose
		private List<Promotion> promotions = new ArrayList<Promotion>();
		@SerializedName("coupons")
		@Expose
		private List<Coupon> coupons = new ArrayList<Coupon>();
		@SerializedName("fare_structure")
		@Expose
		private List<FareStructure> fareStructure = new ArrayList<FareStructure>();
		@SerializedName("far_away_city")
		@Expose
		private String farAwayCity;

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
		 * @return The userName
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * @param userName The user_name
		 */
		public void setUserName(String userName) {
			this.userName = userName;
		}

		/**
		 * @return The userImage
		 */
		public String getUserImage() {
			return userImage;
		}

		/**
		 * @param userImage The user_image
		 */
		public void setUserImage(String userImage) {
			this.userImage = userImage;
		}

		/**
		 * @return The phoneNo
		 */
		public String getPhoneNo() {
			return phoneNo;
		}

		/**
		 * @param phoneNo The phone_no
		 */
		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		/**
		 * @return The userEmail
		 */
		public String getUserEmail() {
			return userEmail;
		}

		/**
		 * @param userEmail The user_email
		 */
		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

		/**
		 * @return The accessToken
		 */
		public String getAccessToken() {
			return accessToken;
		}

		/**
		 * @param accessToken The access_token
		 */
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
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
		 * @return The popup
		 */
		public Integer getPopup() {
			return popup;
		}

		/**
		 * @param popup The popup
		 */
		public void setPopup(Integer popup) {
			this.popup = popup;
		}

		/**
		 * @return The canChangeLocation
		 */
		public Integer getCanChangeLocation() {
			return canChangeLocation;
		}

		/**
		 * @param canChangeLocation The can_change_location
		 */
		public void setCanChangeLocation(Integer canChangeLocation) {
			this.canChangeLocation = canChangeLocation;
		}

		/**
		 * @return The canSchedule
		 */
		public Integer getCanSchedule() {
			return canSchedule;
		}

		/**
		 * @param canSchedule The can_schedule
		 */
		public void setCanSchedule(Integer canSchedule) {
			this.canSchedule = canSchedule;
		}

		/**
		 * @return The schedulingLimit
		 */
		public Integer getSchedulingLimit() {
			return schedulingLimit;
		}

		/**
		 * @param schedulingLimit The scheduling_limit
		 */
		public void setSchedulingLimit(Integer schedulingLimit) {
			this.schedulingLimit = schedulingLimit;
		}

		/**
		 * @return The gcmIntent
		 */
		public Integer getGcmIntent() {
			return gcmIntent;
		}

		/**
		 * @param gcmIntent The gcm_intent
		 */
		public void setGcmIntent(Integer gcmIntent) {
			this.gcmIntent = gcmIntent;
		}

		/**
		 * @return The christmasIconEnable
		 */
		public Integer getChristmasIconEnable() {
			return christmasIconEnable;
		}

		/**
		 * @param christmasIconEnable The christmas_icon_enable
		 */
		public void setChristmasIconEnable(Integer christmasIconEnable) {
			this.christmasIconEnable = christmasIconEnable;
		}

		/**
		 * @return The userId
		 */
		public Integer getUserId() {
			return userId;
		}

		/**
		 * @param userId The user_id
		 */
		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		/**
		 * @return The referralCode
		 */
		public String getReferralCode() {
			return referralCode;
		}

		/**
		 * @param referralCode The referral_code
		 */
		public void setReferralCode(String referralCode) {
			this.referralCode = referralCode;
		}

		/**
		 * @return The userIdentifier
		 */
		public String getUserIdentifier() {
			return userIdentifier;
		}

		/**
		 * @param userIdentifier The user_identifier
		 */
		public void setUserIdentifier(String userIdentifier) {
			this.userIdentifier = userIdentifier;
		}

		/**
		 * @return The emailVerificationStatus
		 */
		public Integer getEmailVerificationStatus() {
			return emailVerificationStatus;
		}

		/**
		 * @param emailVerificationStatus The email_verification_status
		 */
		public void setEmailVerificationStatus(Integer emailVerificationStatus) {
			this.emailVerificationStatus = emailVerificationStatus;
		}

		/**
		 * @return The authKey
		 */
		public String getAuthKey() {
			return authKey;
		}

		/**
		 * @param authKey The auth_key
		 */
		public void setAuthKey(String authKey) {
			this.authKey = authKey;
		}

		/**
		 * @return The jugnooBalance
		 */
		public Integer getJugnooBalance() {
			return jugnooBalance;
		}

		/**
		 * @param jugnooBalance The jugnoo_balance
		 */
		public void setJugnooBalance(Integer jugnooBalance) {
			this.jugnooBalance = jugnooBalance;
		}

		/**
		 * @return The paytmEnabled
		 */
		public Integer getPaytmEnabled() {
			return paytmEnabled;
		}

		/**
		 * @param paytmEnabled The paytm_enabled
		 */
		public void setPaytmEnabled(Integer paytmEnabled) {
			this.paytmEnabled = paytmEnabled;
		}

		/**
		 * @return The numCouponsAvailable
		 */
		public Integer getNumCouponsAvailable() {
			return numCouponsAvailable;
		}

		/**
		 * @param numCouponsAvailable The num_coupons_available
		 */
		public void setNumCouponsAvailable(Integer numCouponsAvailable) {
			this.numCouponsAvailable = numCouponsAvailable;
		}

		/**
		 * @return The supportNumber
		 */
		public String getSupportNumber() {
			return supportNumber;
		}

		/**
		 * @param supportNumber The support_number
		 */
		public void setSupportNumber(String supportNumber) {
			this.supportNumber = supportNumber;
		}

		/**
		 * @return The emergencyContacts
		 */
		public List<EmergencyContact> getEmergencyContacts() {
			return emergencyContacts;
		}

		/**
		 * @param emergencyContacts The emergency_contacts
		 */
		public void setEmergencyContacts(List<EmergencyContact> emergencyContacts) {
			this.emergencyContacts = emergencyContacts;
		}

		/**
		 * @return The referAllStatus
		 */
		public Integer getReferAllStatus() {
			return referAllStatus;
		}

		/**
		 * @param referAllStatus The refer_all_status
		 */
		public void setReferAllStatus(Integer referAllStatus) {
			this.referAllStatus = referAllStatus;
		}

		/**
		 * @return The userAppMonitoring
		 */
		public String getUserAppMonitoring() {
			return userAppMonitoring;
		}

		/**
		 * @param userAppMonitoring The user_app_monitoring
		 */
		public void setUserAppMonitoring(String userAppMonitoring) {
			this.userAppMonitoring = userAppMonitoring;
		}

		/**
		 * @return The jugnooCashTnc
		 */
		public String getJugnooCashTnc() {
			return jugnooCashTnc;
		}

		/**
		 * @param jugnooCashTnc The jugnoo_cash_tnc
		 */
		public void setJugnooCashTnc(String jugnooCashTnc) {
			this.jugnooCashTnc = jugnooCashTnc;
		}

		/**
		 * @return The referAllText
		 */
		public String getReferAllText() {
			return referAllText;
		}

		/**
		 * @param referAllText The refer_all_text
		 */
		public void setReferAllText(String referAllText) {
			this.referAllText = referAllText;
		}

		/**
		 * @return The referAllTitle
		 */
		public String getReferAllTitle() {
			return referAllTitle;
		}

		/**
		 * @param referAllTitle The refer_all_title
		 */
		public void setReferAllTitle(String referAllTitle) {
			this.referAllTitle = referAllTitle;
		}

		/**
		 * @return The jugnooSticky
		 */
		public Integer getJugnooSticky() {
			return jugnooSticky;
		}

		/**
		 * @param jugnooSticky The jugnoo_sticky
		 */
		public void setJugnooSticky(Integer jugnooSticky) {
			this.jugnooSticky = jugnooSticky;
		}

		/**
		 * @return The jugnooFbBanner
		 */
		public String getJugnooFbBanner() {
			return jugnooFbBanner;
		}

		/**
		 * @param jugnooFbBanner The jugnoo_fb_banner
		 */
		public void setJugnooFbBanner(String jugnooFbBanner) {
			this.jugnooFbBanner = jugnooFbBanner;
		}

		/**
		 * @return The userAppMonitoringDuration
		 */
		public String getUserAppMonitoringDuration() {
			return userAppMonitoringDuration;
		}

		/**
		 * @param userAppMonitoringDuration The user_app_monitoring_duration
		 */
		public void setUserAppMonitoringDuration(String userAppMonitoringDuration) {
			this.userAppMonitoringDuration = userAppMonitoringDuration;
		}

		/**
		 * @return The inAppSupportPanelVersion
		 */
		public String getInAppSupportPanelVersion() {
			return inAppSupportPanelVersion;
		}

		/**
		 * @param inAppSupportPanelVersion The in_app_support_panel_version
		 */
		public void setInAppSupportPanelVersion(String inAppSupportPanelVersion) {
			this.inAppSupportPanelVersion = inAppSupportPanelVersion;
		}

		/**
		 * @return The getGogu
		 */
		public String getGetGogu() {
			return getGogu;
		}

		/**
		 * @param getGogu The get_gogu
		 */
		public void setGetGogu(String getGogu) {
			this.getGogu = getGogu;
		}

		/**
		 * @return The inviteEarnScreenImageIos
		 */
		public String getInviteEarnScreenImageIos() {
			return inviteEarnScreenImageIos;
		}

		/**
		 * @param inviteEarnScreenImageIos The invite_earn_screen_image_ios
		 */
		public void setInviteEarnScreenImageIos(String inviteEarnScreenImageIos) {
			this.inviteEarnScreenImageIos = inviteEarnScreenImageIos;
		}

		/**
		 * @return The inviteEarnScreenImageAndroid
		 */
		public String getInviteEarnScreenImageAndroid() {
			return inviteEarnScreenImageAndroid;
		}

		/**
		 * @param inviteEarnScreenImageAndroid The invite_earn_screen_image_android
		 */
		public void setInviteEarnScreenImageAndroid(String inviteEarnScreenImageAndroid) {
			this.inviteEarnScreenImageAndroid = inviteEarnScreenImageAndroid;
		}

		/**
		 * @return The publicAccessToken
		 */
		public String getPublicAccessToken() {
			return publicAccessToken;
		}

		/**
		 * @param publicAccessToken The public_access_token
		 */
		public void setPublicAccessToken(String publicAccessToken) {
			this.publicAccessToken = publicAccessToken;
		}

		/**
		 * @return The t20WcEnable
		 */
		public String getT20WcEnable() {
			return t20WcEnable;
		}

		/**
		 * @param t20WcEnable The t20_wc_enable
		 */
		public void setT20WcEnable(String t20WcEnable) {
			this.t20WcEnable = t20WcEnable;
		}

		/**
		 * @return The t20WcScheduleVersion
		 */
		public String getT20WcScheduleVersion() {
			return t20WcScheduleVersion;
		}

		/**
		 * @param t20WcScheduleVersion The t20_wc_schedule_version
		 */
		public void setT20WcScheduleVersion(String t20WcScheduleVersion) {
			this.t20WcScheduleVersion = t20WcScheduleVersion;
		}

		/**
		 * @return The t20WcInfoText
		 */
		public String getT20WcInfoText() {
			return t20WcInfoText;
		}

		/**
		 * @param t20WcInfoText The t20_wc_info_text
		 */
		public void setT20WcInfoText(String t20WcInfoText) {
			this.t20WcInfoText = t20WcInfoText;
		}

		/**
		 * @return The gamePredictEnable
		 */
		public String getGamePredictEnable() {
			return gamePredictEnable;
		}

		/**
		 * @param gamePredictEnable The game_predict_enable
		 */
		public void setGamePredictEnable(String gamePredictEnable) {
			this.gamePredictEnable = gamePredictEnable;
		}

		/**
		 * @return The gamePredictUrl
		 */
		public String getGamePredictUrl() {
			return gamePredictUrl;
		}

		/**
		 * @param gamePredictUrl The game_predict_url
		 */
		public void setGamePredictUrl(String gamePredictUrl) {
			this.gamePredictUrl = gamePredictUrl;
		}

		/**
		 * @return The gamePredictViewData
		 */
		public String getGamePredictViewData() {
			return gamePredictViewData;
		}

		/**
		 * @param gamePredictViewData The game_predict_view_data
		 */
		public void setGamePredictViewData(String gamePredictViewData) {
			this.gamePredictViewData = gamePredictViewData;
		}

		/**
		 * @return The customerLocationUpdateInterval
		 */
		public String getCustomerLocationUpdateInterval() {
			return customerLocationUpdateInterval;
		}

		/**
		 * @param customerLocationUpdateInterval The customer_location_update_interval
		 */
		public void setCustomerLocationUpdateInterval(String customerLocationUpdateInterval) {
			this.customerLocationUpdateInterval = customerLocationUpdateInterval;
		}

		/**
		 * @return The fareFactor
		 */
		public Integer getFareFactor() {
			return fareFactor;
		}

		/**
		 * @param fareFactor The fare_factor
		 */
		public void setFareFactor(Integer fareFactor) {
			this.fareFactor = fareFactor;
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
	}
}
