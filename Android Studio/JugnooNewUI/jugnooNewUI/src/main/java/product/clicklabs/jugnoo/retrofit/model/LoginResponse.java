package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 1/5/16.
 */
public class LoginResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;


	@SerializedName("login")
	@Expose
	private FindADriverResponse findADriverResponse;

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

	public FindADriverResponse getFindADriverResponse() {
		return findADriverResponse;
	}

	public void setFindADriverResponse(FindADriverResponse findADriverResponse) {
		this.findADriverResponse = findADriverResponse;
	}
}
