package product.clicklabs.jugnoo.datastructure;

import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.retrofit.model.Store;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 8/17/16.
 */
public class MenusData {
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
	private JSONArray positiveFeedbackReasons;
	private List<MenusResponse.Category> merchantCategoriesList = new ArrayList<>();
	private String restaurantName;
	private int merchantCategoryId;
	private int addStoreImages;
	private int showAddStore;
	private boolean isFeedOrder;

	public JSONArray getPositiveFeedbackReasons() {
		return positiveFeedbackReasons;
	}

	public MenusData(String question, String orderId, int questionType, int pendingFeedback, ArrayList<Store> stores, PopupData popupData, double amount, String feedbackDeliveryDate,
					 int feedbackViewType, int isFatafatEnabled, String rideEndGoodFeedbackText, JSONArray negativeFeedbackReasons, JSONArray positiveFeedbackReasons,
					 String restaurantName, int category,int addStoreImages,int showAddStore,JSONArray merchantCategories,boolean isFeedOrder) {
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
		this.positiveFeedbackReasons = positiveFeedbackReasons;
		this.restaurantName = restaurantName;
		this.merchantCategoryId = category;
		this.addStoreImages = addStoreImages;
		this.showAddStore = showAddStore;
		this.isFeedOrder = isFeedOrder;
		if(merchantCategories!=null){
			for(int i = 0;i<merchantCategories.length();i++){
				try {
					JSONObject jsonObject = (JSONObject) merchantCategories.getJSONObject(i);
					merchantCategoriesList.add(i,new MenusResponse.Category(jsonObject.optInt("id",0),jsonObject.optString("category",null)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


		}
	}

	public int getMerchantCategoryId() {
		return merchantCategoryId;
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

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public int getAddStoreImages() {
		return addStoreImages;
	}

	public boolean getShowAddStore() {
		return showAddStore==1;
	}

	public List<MenusResponse.Category> getMerchantCategoriesList() {
		return merchantCategoriesList;
	}

	public boolean isFeedOrder() {
		return isFeedOrder;
	}
}
