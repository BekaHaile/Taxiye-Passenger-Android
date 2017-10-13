package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.feed.models.FeedCommonResponse;

/**
 * Created by shankar on 1/5/16.
 */
public class SettleUserDebt extends FeedCommonResponse {

	@SerializedName("toast_message")
	@Expose String toastMessage;

	@SerializedName("flag_toast_message")
	@Expose int showToastMessage;



	public String getToastMessage() {
		return toastMessage;
	}

	public int getFlag() {
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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean getShowToastMessage() {
		return showToastMessage!=0;
	}
}
