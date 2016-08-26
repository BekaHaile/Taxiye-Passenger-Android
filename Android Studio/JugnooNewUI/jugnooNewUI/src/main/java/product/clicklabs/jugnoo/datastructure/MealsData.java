package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

/**
 * Created by shankar on 8/17/16.
 */
public class MealsData {

	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	private String orderId;
	private int pendingFeedback;
	private double amount;
	private String feedbackDeliveryDate;
	private int feedbackViewType;
	public MealsData(String orderId, int pendingFeedback, double amount, String feedbackDeliveryDate, int feedbackViewType) {
		this.orderId = orderId;
		this.pendingFeedback = pendingFeedback;
		this.amount = amount;
		this.feedbackDeliveryDate = feedbackDeliveryDate;
		this.feedbackViewType = feedbackViewType;
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
}
