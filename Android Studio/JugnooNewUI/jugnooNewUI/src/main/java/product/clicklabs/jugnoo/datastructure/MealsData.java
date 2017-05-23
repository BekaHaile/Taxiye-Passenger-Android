package product.clicklabs.jugnoo.datastructure;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by shankar on 8/17/16.
 */
public class MealsData {

	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	private String orderId;
	private int pendingFeedback;
	private double amount;
	private String feedbackDeliveryDate, rideEndGoodFeedbackText;
	private int feedbackViewType;
	private JSONArray negativeFeedbackReasons;
	private String feedbackOrderItems;
	private OfferStripMeals offerStripMeals;

	public MealsData(String orderId, int pendingFeedback, double amount, String feedbackDeliveryDate, int feedbackViewType,
					 String rideEndGoodFeedbackText, JSONArray negativeFeedbackReasons, String feedbackOrderItems,
			OfferStripMeals offerStripMeals) {
		this.orderId = orderId;
		this.pendingFeedback = pendingFeedback;
		this.amount = amount;
		this.feedbackDeliveryDate = feedbackDeliveryDate;
		this.feedbackViewType = feedbackViewType;
		this.rideEndGoodFeedbackText = rideEndGoodFeedbackText;
		this.negativeFeedbackReasons = negativeFeedbackReasons;
		this.feedbackOrderItems = feedbackOrderItems;
		this.offerStripMeals = offerStripMeals;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getPendingFeedback() {
		return pendingFeedback;
	}

	public void setPendingFeedback(int pendingFeedback) {
		this.pendingFeedback = pendingFeedback;
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

	public String getFeedbackOrderItems() {
		return feedbackOrderItems;
	}

	public void setFeedbackOrderItems(String feedbackOrderItems) {
		this.feedbackOrderItems = feedbackOrderItems;
	}

	public OfferStripMeals getOfferStripMeals() {
		return offerStripMeals;
	}

	public  class OfferStripMeals {

		@SerializedName("message")
		private String textToDisplay;

		@SerializedName("deep_index")
		private String deepIndex;


		public String getTextToDisplay() {
			return textToDisplay;
		}

		public String getDeepIndex() {
			return deepIndex;
		}
	}
}
