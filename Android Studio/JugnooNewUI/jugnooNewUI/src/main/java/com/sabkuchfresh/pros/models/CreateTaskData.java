package com.sabkuchfresh.pros.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 22/06/17.
 */

public class CreateTaskData {

	@SerializedName("flag")
	@Expose
	private int flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("data")
	@Expose
	private Data data;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
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

	public class Data {

		@SerializedName("job_id")
		@Expose
		private int jobId;
		@SerializedName("job_hash")
		@Expose
		private String jobHash;
		@SerializedName("customer_name")
		@Expose
		private String customerName;
		@SerializedName("customer_address")
		@Expose
		private String customerAddress;
		@SerializedName("job_token")
		@Expose
		private String jobToken;
		@SerializedName("tracking_link")
		@Expose
		private String trackingLink;

		public int getJobId() {
			return jobId;
		}

		public void setJobId(int jobId) {
			this.jobId = jobId;
		}

		public String getJobHash() {
			return jobHash;
		}

		public void setJobHash(String jobHash) {
			this.jobHash = jobHash;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public String getCustomerAddress() {
			return customerAddress;
		}

		public void setCustomerAddress(String customerAddress) {
			this.customerAddress = customerAddress;
		}

		public String getJobToken() {
			return jobToken;
		}

		public void setJobToken(String jobToken) {
			this.jobToken = jobToken;
		}

		public String getTrackingLink() {
			return trackingLink;
		}

		public void setTrackingLink(String trackingLink) {
			this.trackingLink = trackingLink;
		}

	}

}
