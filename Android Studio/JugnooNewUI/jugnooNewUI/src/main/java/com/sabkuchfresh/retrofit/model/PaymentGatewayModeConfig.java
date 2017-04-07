package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 23/03/17.
 */

public class PaymentGatewayModeConfig {
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("enabled")
	@Expose
	private Integer enabled;
	@SerializedName("display_name")
	@Expose
	private String displayName;
	@SerializedName("display_name_upi")
	@Expose
	private String displayNameUpi;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayNameUpi() {
		return displayNameUpi;
	}

	public void setDisplayNameUpi(String displayNameUpi) {
		this.displayNameUpi = displayNameUpi;
	}
}
