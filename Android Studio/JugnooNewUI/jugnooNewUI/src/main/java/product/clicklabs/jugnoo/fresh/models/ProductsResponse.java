package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/7/16.
 */
public class ProductsResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("categories")
	@Expose
	private List<Category> categories = new ArrayList<>();
	@SerializedName("delivery_info")
	@Expose
	private DeliveryInfo deliveryInfo;
	@SerializedName("show_message")
	@Expose
	private Integer showMessage;


	/**
	 *
	 * @return
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 * The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 *
	 * @return
	 * The categories
	 */
	public List<Category> getCategories() {
		return categories;
	}

	/**
	 *
	 * @param categories
	 * The categories
	 */
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public DeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
	}

	public Integer getShowMessage() {
		return showMessage;
	}

	public void setShowMessage(Integer showMessage) {
		this.showMessage = showMessage;
	}
}
