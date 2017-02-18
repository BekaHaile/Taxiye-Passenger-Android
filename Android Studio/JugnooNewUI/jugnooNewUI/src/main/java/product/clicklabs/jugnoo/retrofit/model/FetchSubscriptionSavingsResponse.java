package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import product.clicklabs.jugnoo.datastructure.SubscriptionData;

/**
 * Created by shankar on 1/5/16.
 */
public class FetchSubscriptionSavingsResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("savings")
	@Expose
	private Integer totalSavings;
	@SerializedName("upgrade_data")
	@Expose
	private List<UpgradeDatum> upgradeData = null;
	@SerializedName("renewal_data")
	@Expose
	private RenewalData renewalData;

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

	public Integer getTotalSavings() {
		return totalSavings;
	}

	public void setTotalSavings(Integer totalSavings) {
		this.totalSavings = totalSavings;
	}

	public List<UpgradeDatum> getUpgradeData() {
		return upgradeData;
	}

	public void setUpgradeData(List<UpgradeDatum> upgradeData) {
		this.upgradeData = upgradeData;
	}

	public RenewalData getRenewalData() {
		return renewalData;
	}

	public void setRenewalData(RenewalData renewalData) {
		this.renewalData = renewalData;
	}

	public class UpgradeDatum {

		@SerializedName("id")
		@Expose
		private Integer id;
		@SerializedName("upgrading_text")
		@Expose
		private String upgradingText;
		@SerializedName("upgrade_array")
		@Expose
		private List<SubscriptionData.Subscription> upgradeArray = null;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getUpgradingText() {
			return upgradingText;
		}

		public void setUpgradingText(String upgradingText) {
			this.upgradingText = upgradingText;
		}

		public List<SubscriptionData.Subscription> getUpgradeArray() {
			return upgradeArray;
		}

		public void setUpgradeArray(List<SubscriptionData.Subscription> upgradeArray) {
			this.upgradeArray = upgradeArray;
		}
	}

	public class RenewalData {

		@SerializedName("warning")
		@Expose
		private Warning warning;
		@SerializedName("renew_plan")
		@Expose
		private SubscriptionData.Subscription renewPlan;
		@SerializedName("upgrade_plan")
		@Expose
		private List<UpgradeDatum> upgradePlan = null;

		public Warning getWarning() {
			return warning;
		}

		public void setWarning(Warning warning) {
			this.warning = warning;
		}

		public SubscriptionData.Subscription getRenewPlan() {
			return renewPlan;
		}

		public void setRenewPlan(SubscriptionData.Subscription renewPlan) {
			this.renewPlan = renewPlan;
		}

		public List<UpgradeDatum> getUpgradePlan() {
			return upgradePlan;
		}

		public void setUpgradePlan(List<UpgradeDatum> upgradePlan) {
			this.upgradePlan = upgradePlan;
		}

	}

	public class Warning {

		@SerializedName("text")
		@Expose
		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}
}

