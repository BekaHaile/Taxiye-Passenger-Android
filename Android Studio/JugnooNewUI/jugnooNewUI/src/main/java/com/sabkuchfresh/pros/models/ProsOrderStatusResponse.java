package com.sabkuchfresh.pros.models;

import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;

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
		@SerializedName("fleet_name")
		@Expose
		private String fleetName;
		@SerializedName("job_hash")
		@Expose
		private String jobHash;


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

		public int getJobStatusColorRes() {
			if(jobStatus == ProsOrderStatus.FAILED.getOrdinal()
					|| jobStatus == ProsOrderStatus.CANCEL.getOrdinal()
					|| jobStatus == ProsOrderStatus.DELETED.getOrdinal()
					|| jobStatus == ProsOrderStatus.IGNORED.getOrdinal()){
				return R.color.red_status;
			} else {
				return R.color.green_status;
			}
		}

		public Pair<String, String> getProductNameAndJobAmount(){
			String productName = getJobNameSplitted(), jobAmount = "";
			if(getFields() != null) {
				for (ProsOrderStatusResponse.CustomField customField : getFields().getCustomField()) {
					if (customField.getLabel().equalsIgnoreCase(Constants.KEY_PRODUCT_NAME)) {
						productName = customField.getData();
					} else if (customField.getLabel().equalsIgnoreCase(Constants.KEY_JOB_AMOUNT)
							&& !TextUtils.isEmpty(customField.getFleetData())) {
						jobAmount = customField.getFleetData();
					}
				}
			}
			return new Pair<>(productName, jobAmount);
		}

		public String getJobNameSplitted() {
			String[] arr = jobDescription.split("\\:\\ ");
			return arr[0];
		}

		public String getFleetName() {
			return fleetName;
		}

		public void setFleetName(String fleetName) {
			this.fleetName = fleetName;
		}

		public String getJobHash() {
			return jobHash;
		}

		public void setJobHash(String jobHash) {
			this.jobHash = jobHash;
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
