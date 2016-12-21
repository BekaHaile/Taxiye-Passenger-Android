package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/5/16.
 */
public class FetchPayDataResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;


	@SerializedName("vpa")
	@Expose
	private String vpa;
	@SerializedName("transaction")
	@Expose
	private List<TransacHistoryResponse.TransactionHistory> transaction = new ArrayList<TransacHistoryResponse.TransactionHistory>();
	@SerializedName("share_button_text")
	@Expose
	private String shareButtonText;

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

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public List<TransacHistoryResponse.TransactionHistory> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<TransacHistoryResponse.TransactionHistory> transaction) {
		this.transaction = transaction;
	}

	public String getShareButtonText() {
		return shareButtonText;
	}

	public void setShareButtonText(String shareButtonText) {
		this.shareButtonText = shareButtonText;
	}
}
