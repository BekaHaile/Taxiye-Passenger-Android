package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 4/9/16.
 */
public class UserCheckoutResponse{

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("checkoutData")
	@Expose
	private CheckoutData checkoutData;

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
	 * The checkoutData
	 */
	public CheckoutData getCheckoutData() {
		return checkoutData;
	}

	/**
	 *
	 * @param checkoutData
	 * The checkoutData
	 */
	public void setCheckoutData(CheckoutData checkoutData) {
		this.checkoutData = checkoutData;
	}

}
