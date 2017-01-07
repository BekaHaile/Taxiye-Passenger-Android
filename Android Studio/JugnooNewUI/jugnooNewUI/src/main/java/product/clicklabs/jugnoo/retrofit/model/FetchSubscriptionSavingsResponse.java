package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 1/5/16.
 */
public class FetchSubscriptionSavingsResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("total_savings")
	@Expose
	private Integer totalSavings;

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getTotalSavings() {
		return totalSavings;
	}

	public void setTotalSavings(Integer totalSavings) {
		this.totalSavings = totalSavings;
	}
}
