package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.CancelOption;

/**
 * Created by shankar on 16/03/17.
 */

public class OrderCancelReasonsResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("cancel_options")
	@Expose
	private List<String> cancelOptions;
	@SerializedName("cancel_info")
	@Expose
	private String cancelInfo;
	@SerializedName("additional_reasons")
	@Expose
	private String additionalReason;
	@SerializedName("comment_placeholder")
	@Expose
	private String commentPlaceholder;


	public ArrayList<CancelOption> getCancelOptions() {
		ArrayList<CancelOption> cancelOptionsAL = new ArrayList<>();
		for(String option : cancelOptions){
			cancelOptionsAL.add(new CancelOption(option));
		}
		return cancelOptionsAL;
	}

	public void setCancelOptions(List<String> cancelOptions) {
		this.cancelOptions = cancelOptions;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAdditionalReason() {
		return additionalReason;
	}

	public void setAdditionalReason(String additionalReason) {
		this.additionalReason = additionalReason;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getCancelInfo() {
		return cancelInfo;
	}

	public void setCancelInfo(String cancelInfo) {
		this.cancelInfo = cancelInfo;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCommentPlaceholder() {
		return commentPlaceholder;
	}

	public void setCommentPlaceholder(String commentPlaceholder) {
		this.commentPlaceholder = commentPlaceholder;
	}
}
