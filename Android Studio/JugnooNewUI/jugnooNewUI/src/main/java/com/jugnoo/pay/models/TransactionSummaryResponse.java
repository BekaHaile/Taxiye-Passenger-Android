package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 12/8/16.
 */

public class TransactionSummaryResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("txn_details")
	@Expose
	private List<TxnDetail> txnDetails = null;

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
	 * The txnDetails
	 */
	public List<TxnDetail> getTxnDetails() {
		return txnDetails;
	}

	/**
	 *
	 * @param txnDetails
	 * The txn_details
	 */
	public void setTxnDetails(List<TxnDetail> txnDetails) {
		this.txnDetails = txnDetails;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public class TxnDetail {

		@SerializedName("amount")
		@Expose
		private Double amount;
		@SerializedName("date")
		@Expose
		private String date;
		@SerializedName("status")
		@Expose
		private String status;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;
		@SerializedName("vpa")
		@Expose
		private String vpa;
		@SerializedName("txn_string")
		@Expose
		private String txnString;
		@SerializedName("npciTxnId")
		@Expose
		private String npciTxnId;
		@SerializedName("bankRefId")
		@Expose
		private String bankRefId;
		@SerializedName("statusMessage")
		@Expose
		private String statusMessage;
		@SerializedName("message")
		@Expose
		private String message;

		public TxnDetail(Double amount, String date, String status, String name, String phoneNo, String vpa,
						 String txnString, String npciTxnId, String bankRefId, String statusMessage, String message) {
			this.amount = amount;
			this.date = date;
			this.status = status;
			this.name = name;
			this.phoneNo = phoneNo;
			this.vpa = vpa;
			this.txnString = txnString;
			this.npciTxnId = npciTxnId;
			this.bankRefId = bankRefId;
			this.statusMessage = statusMessage;
			this.message = message;
		}

		/**
		 *
		 * @return
		 * The amount
		 */
		public Double getAmount() {
			return amount;
		}

		/**
		 *
		 * @param amount
		 * The amount
		 */
		public void setAmount(Double amount) {
			this.amount = amount;
		}

		/**
		 *
		 * @return
		 * The date
		 */
		public String getDate() {
			return date;
		}

		/**
		 *
		 * @param date
		 * The date
		 */
		public void setDate(String date) {
			this.date = date;
		}

		/**
		 *
		 * @return
		 * The status
		 */
		public String getStatus() {
			return status;
		}

		/**
		 *
		 * @param status
		 * The status
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 *
		 * @return
		 * The name
		 */
		public String getName() {
			return name;
		}

		/**
		 *
		 * @param name
		 * The name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 *
		 * @return
		 * The txnString
		 */
		public String getTxnString() {
			return txnString;
		}

		/**
		 *
		 * @param txnString
		 * The txn_string
		 */
		public void setTxnString(String txnString) {
			this.txnString = txnString;
		}

		/**
		 *
		 * @return
		 * The npciTxnId
		 */
		public String getNpciTxnId() {
			return npciTxnId;
		}

		/**
		 *
		 * @param npciTxnId
		 * The npciTxnId
		 */
		public void setNpciTxnId(String npciTxnId) {
			this.npciTxnId = npciTxnId;
		}

		/**
		 *
		 * @return
		 * The bankRefId
		 */
		public String getBankRefId() {
			return bankRefId;
		}

		/**
		 *
		 * @param bankRefId
		 * The bankRefId
		 */
		public void setBankRefId(String bankRefId) {
			this.bankRefId = bankRefId;
		}

		/**
		 *
		 * @return
		 * The statusMessage
		 */
		public String getStatusMessage() {
			return statusMessage;
		}

		/**
		 *
		 * @param statusMessage
		 * The statusMessage
		 */
		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}

		public String getPhoneNo() {
			return phoneNo;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public String getVpa() {
			return vpa;
		}

		public void setVpa(String vpa) {
			this.vpa = vpa;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
