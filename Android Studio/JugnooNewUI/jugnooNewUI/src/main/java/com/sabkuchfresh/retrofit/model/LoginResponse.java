package com.sabkuchfresh.retrofit.model;

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

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("login")
	@Expose
	private Login login;

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


		@SerializedName("far_away_city")
		@Expose
		private String farAwayCity;
		@SerializedName("fare_factor")
		@Expose
		private Double fareFactor;

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
	}
}
