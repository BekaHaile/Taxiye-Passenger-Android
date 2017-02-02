package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 12/9/16.
 */

public class SendMoneyCallbackResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("npciTxnId")
	@Expose
	private String npciTxnId;
	@SerializedName("bankRefId")
	@Expose
	private String bankRefId;
	@SerializedName("date")
	@Expose
	private String date;
	@SerializedName("txn_message")
	@Expose
	private String txnMessage;

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

	public String getNpciTxnId() {
		return npciTxnId;
	}

	public void setNpciTxnId(String npciTxnId) {
		this.npciTxnId = npciTxnId;
	}

	public String getBankRefId() {
		return bankRefId;
	}

	public void setBankRefId(String bankRefId) {
		this.bankRefId = bankRefId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTxnMessage() {
		return txnMessage;
	}

	public void setTxnMessage(String txnMessage) {
		this.txnMessage = txnMessage;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
