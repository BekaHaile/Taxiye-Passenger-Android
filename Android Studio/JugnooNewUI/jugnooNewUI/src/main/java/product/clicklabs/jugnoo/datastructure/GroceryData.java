package product.clicklabs.jugnoo.datastructure;

import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.retrofit.model.Store;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by shankar on 8/17/16.
 */
public class GroceryData {
	public String question, orderId;
	public int questionType;
	public int pendingFeedback;
	public ArrayList<Store> stores;
	private PopupData popupData;
	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	private double amount;
	private String feedbackDeliveryDate, rideEndGoodFeedbackText;
	private int feedbackViewType;
	private int isFatafatEnabled;
	private JSONArray negativeFeedbackReasons;


	public GroceryData(String question, String orderId, int questionType, int pendingFeedback,
					   ArrayList<Store> stores, PopupData popupData, double amount, String feedbackDeliveryDate,
					   int feedbackViewType, int isFatafatEnabled, String rideEndGoodFeedbackText, JSONArray negativeFeedbackReasons) {
		this.question = question;
		this.orderId = orderId;
		this.questionType = questionType;
		this.pendingFeedback = pendingFeedback;
		this.stores = stores;
		this.popupData = popupData;
		this.amount = amount;
		this.feedbackDeliveryDate = feedbackDeliveryDate;
		this.feedbackViewType = feedbackViewType;
		this.isFatafatEnabled = isFatafatEnabled;
		this.rideEndGoodFeedbackText = rideEndGoodFeedbackText;
		this.negativeFeedbackReasons = negativeFeedbackReasons;
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

	public ArrayList<PromoCoupon> getPromoCoupons() {
		return promoCoupons;
	}

	public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
		this.promoCoupons = promoCoupons;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getFeedbackDeliveryDate() {
		return feedbackDeliveryDate;
	}

	public void setFeedbackDeliveryDate(String feedbackDeliveryDate) {
		this.feedbackDeliveryDate = feedbackDeliveryDate;
	}

	public int getFeedbackViewType() {
		return feedbackViewType;
	}

	public void setFeedbackViewType(int feedbackViewType) {
		this.feedbackViewType = feedbackViewType;
	}

	public int getIsFatafatEnabled() {
		return isFatafatEnabled;
	}

	public void setIsFatafatEnabled(int isFatafatEnabled) {
		this.isFatafatEnabled = isFatafatEnabled;
	}

	public String getRideEndGoodFeedbackText() {
		return rideEndGoodFeedbackText;
	}

	public void setRideEndGoodFeedbackText(String rideEndGoodFeedbackText) {
		this.rideEndGoodFeedbackText = rideEndGoodFeedbackText;
	}

	public JSONArray getNegativeFeedbackReasons() {
		return negativeFeedbackReasons;
	}

	public void setNegativeFeedbackReasons(JSONArray negativeFeedbackReasons) {
		this.negativeFeedbackReasons = negativeFeedbackReasons;
	}
}
