package product.clicklabs.jugnoo.datastructure;

import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.retrofit.model.Store;

import java.util.ArrayList;

/**
 * Created by shankar on 8/17/16.
 */
public class FreshData {
	public String question, orderId;
	public int questionType;
	public int pendingFeedback;
	public ArrayList<Store> stores;
	private PopupData popupData;

	public FreshData(String question, String orderId, int questionType, int pendingFeedback,
					 ArrayList<Store> stores, PopupData popupData) {
		this.question = question;
		this.orderId = orderId;
		this.questionType = questionType;
		this.pendingFeedback = pendingFeedback;
		this.stores = stores;
		this.popupData = popupData;
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
}
