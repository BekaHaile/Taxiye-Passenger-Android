package com.sabkuchfresh.pros.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProsOrderStatusResponse {

	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("error")
	@Expose
	private String error;
	@SerializedName("flag")
	@Expose
	private int flag;
	@SerializedName("data")
	@Expose
	private List<Datum> data = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public List<Datum> getData() {
		return data;
	}

	public void setData(List<Datum> data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public class Fields {

		@SerializedName("custom_field")
		@Expose
		private List<CustomField> customField = null;

		public List<CustomField> getCustomField() {
			return customField;
		}

		public void setCustomField(List<CustomField> customField) {
			this.customField = customField;
		}

	}

	public class Datum {

		@SerializedName("job_latitude")
		@Expose
		private double jobLatitude;
		@SerializedName("job_longitude")
		@Expose
		private double jobLongitude;
		@SerializedName("job_address")
		@Expose
		private String jobAddress;
		@SerializedName("job_status")
		@Expose
		private int jobStatus;
		@SerializedName("job_description")
		@Expose
		private String jobDescription;
		@SerializedName("job_pickup_datetime")
		@Expose
		private String jobPickupDatetime;
		@SerializedName("job_id")
		@Expose
		private int jobId;
		@SerializedName("job_delivery_datetime")
		@Expose
		private String jobDeliveryDatetime;
		@SerializedName("job_time")
		@Expose
		private String jobTime;
		@SerializedName("fields")
		@Expose
		private Fields fields;
		@SerializedName("support_category")
		@Expose
		private int supportCategory;


		public String getJobAddress() {
			return jobAddress;
		}

		public void setJobAddress(String jobAddress) {
			this.jobAddress = jobAddress;
		}

		public int getJobStatus() {
			return jobStatus;
		}

		public void setJobStatus(int jobStatus) {
			this.jobStatus = jobStatus;
		}

		public String getJobDescription() {
			return jobDescription;
		}

		public void setJobDescription(String jobDescription) {
			this.jobDescription = jobDescription;
		}

		public String getJobPickupDatetime() {
			return jobPickupDatetime;
		}

		public void setJobPickupDatetime(String jobPickupDatetime) {
			this.jobPickupDatetime = jobPickupDatetime;
		}

		public int getJobId() {
			return jobId;
		}

		public void setJobId(int jobId) {
			this.jobId = jobId;
		}

		public String getJobDeliveryDatetime() {
			return jobDeliveryDatetime;
		}

		public void setJobDeliveryDatetime(String jobDeliveryDatetime) {
			this.jobDeliveryDatetime = jobDeliveryDatetime;
		}

		public String getJobTime() {
			return jobTime;
		}

		public void setJobTime(String jobTime) {
			this.jobTime = jobTime;
		}

		public Fields getFields() {
			return fields;
		}

		public void setFields(Fields fields) {
			this.fields = fields;
		}

		public double getJobLatitude() {
			return jobLatitude;
		}

		public void setJobLatitude(double jobLatitude) {
			this.jobLatitude = jobLatitude;
		}

		public double getJobLongitude() {
			return jobLongitude;
		}

		public void setJobLongitude(double jobLongitude) {
			this.jobLongitude = jobLongitude;
		}

		public int getSupportCategory() {
			return supportCategory;
		}

		public void setSupportCategory(int supportCategory) {
			this.supportCategory = supportCategory;
		}
	}


	public class CustomField {

		@SerializedName("label")
		@Expose
		private String label;
		@SerializedName("data")
		@Expose
		private String data;
		@SerializedName("fleet_data")
		@Expose
		private String fleetData;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getFleetData() {
			return fleetData;
		}

		public void setFleetData(String fleetData) {
			this.fleetData = fleetData;
		}
	}
}
