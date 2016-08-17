package product.clicklabs.jugnoo.datastructure;

import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.retrofit.model.Store;

import java.util.ArrayList;

/**
 * Created by shankar on 8/17/16.
 */
public class FatafatUserData {
	public String shareImage, shareName;
	public String referralBanner, referralShortDesc, referralDescription, referralShareTitle,
			referralShareText, referralShareImage, question, orderId, referralShareMessage;
	public int questionType;
	public int pendingFeedback;
	public ArrayList<Store> stores;
	private PopupData popupData;

	public FatafatUserData(String shareImage, String shareName, String referralBanner, String referralShortDesc,
						   String referralDescription, String referralShareTitle, String referralShareText, String referralShareImage,
						   String question, String orderId, String referralShareMessage, int questionType, int pendingFeedback,
						   ArrayList<Store> stores, PopupData popupData) {
		this.shareImage = shareImage;
		this.shareName = shareName;
		this.referralBanner = referralBanner;
		this.referralShortDesc = referralShortDesc;
		this.referralDescription = referralDescription;
		this.referralShareTitle = referralShareTitle;
		this.referralShareText = referralShareText;
		this.referralShareImage = referralShareImage;
		this.question = question;
		this.orderId = orderId;
		this.referralShareMessage = referralShareMessage;
		this.questionType = questionType;
		this.pendingFeedback = pendingFeedback;
		this.stores = stores;
		this.popupData = popupData;
	}

	public String getShareImage() {
		return shareImage;
	}

	public void setShareImage(String shareImage) {
		this.shareImage = shareImage;
	}

	public String getShareName() {
		return shareName;
	}

	public void setShareName(String shareName) {
		this.shareName = shareName;
	}

	public String getReferralBanner() {
		return referralBanner;
	}

	public void setReferralBanner(String referralBanner) {
		this.referralBanner = referralBanner;
	}

	public String getReferralShortDesc() {
		return referralShortDesc;
	}

	public void setReferralShortDesc(String referralShortDesc) {
		this.referralShortDesc = referralShortDesc;
	}

	public String getReferralDescription() {
		return referralDescription;
	}

	public void setReferralDescription(String referralDescription) {
		this.referralDescription = referralDescription;
	}

	public String getReferralShareTitle() {
		return referralShareTitle;
	}

	public void setReferralShareTitle(String referralShareTitle) {
		this.referralShareTitle = referralShareTitle;
	}

	public String getReferralShareText() {
		return referralShareText;
	}

	public void setReferralShareText(String referralShareText) {
		this.referralShareText = referralShareText;
	}

	public String getReferralShareImage() {
		return referralShareImage;
	}

	public void setReferralShareImage(String referralShareImage) {
		this.referralShareImage = referralShareImage;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getReferralShareMessage() {
		return referralShareMessage;
	}

	public void setReferralShareMessage(String referralShareMessage) {
		this.referralShareMessage = referralShareMessage;
	}

	public int getQuestionType() {
		return questionType;
	}

	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}

	public int getPendingFeedback() {
		return pendingFeedback;
	}

	public void setPendingFeedback(int pendingFeedback) {
		this.pendingFeedback = pendingFeedback;
	}

	public ArrayList<Store> getStores() {
		return stores;
	}

	public void setStores(ArrayList<Store> stores) {
		this.stores = stores;
	}

	public PopupData getPopupData() {
		return popupData;
	}

	public void setPopupData(PopupData popupData) {
		this.popupData = popupData;
	}
}
