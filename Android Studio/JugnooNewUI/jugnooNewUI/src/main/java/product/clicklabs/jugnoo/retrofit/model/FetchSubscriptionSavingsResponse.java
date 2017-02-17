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

	public class UpgradeArray {

		@SerializedName("upgradable_to")
		@Expose
		private Integer upgradableTo;
		@SerializedName("upgrade_amount")
		@Expose
		private Integer upgradeAmount;

		public Integer getUpgradableTo() {
			return upgradableTo;
		}

		public void setUpgradableTo(Integer upgradableTo) {
			this.upgradableTo = upgradableTo;
		}

		public Integer getUpgradeAmount() {
			return upgradeAmount;
		}

		public void setUpgradeAmount(Integer upgradeAmount) {
			this.upgradeAmount = upgradeAmount;
		}

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
		@SerializedName("upgrade_plan")
		@Expose
		private UpgradePlan upgradePlan;
		@SerializedName("renew_plan")
		@Expose
		private RenewPlan renewPlan;

		public Warning getWarning() {
			return warning;
		}

		public void setWarning(Warning warning) {
			this.warning = warning;
		}

		public UpgradePlan getUpgradePlan() {
			return upgradePlan;
		}

		public void setUpgradePlan(UpgradePlan upgradePlan) {
			this.upgradePlan = upgradePlan;
		}

		public RenewPlan getRenewPlan() {
			return renewPlan;
		}

		public void setRenewPlan(RenewPlan renewPlan) {
			this.renewPlan = renewPlan;
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

	public class RenewPlan {

		@SerializedName("title")
		@Expose
		private String title;
		@SerializedName("description")
		@Expose
		private String description;
		@SerializedName("cross_text")
		@Expose
		private String crossText;
		@SerializedName("renew_data")
		@Expose
		private RenewData renewData;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getCrossText() {
			return crossText;
		}

		public void setCrossText(String crossText) {
			this.crossText = crossText;
		}

		public RenewData getRenewData() {
			return renewData;
		}

		public void setRenewData(RenewData renewData) {
			this.renewData = renewData;
		}

	}

	public class RenewData {

		@SerializedName("sub_id")
		@Expose
		private Integer subId;
		@SerializedName("amount")
		@Expose
		private Integer amount;

		public Integer getSubId() {
			return subId;
		}

		public void setSubId(Integer subId) {
			this.subId = subId;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}

	}

	public class UpgradePlan {

		@SerializedName("title")
		@Expose
		private String title;
		@SerializedName("description")
		@Expose
		private String description;
		@SerializedName("cross_text")
		@Expose
		private String crossText;
		@SerializedName("upgrade_data")
		@Expose
		private List<UpgradeDatum> upgradeData = null;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getCrossText() {
			return crossText;
		}

		public void setCrossText(String crossText) {
			this.crossText = crossText;
		}

		public List<UpgradeDatum> getUpgradeData() {
			return upgradeData;
		}

		public void setUpgradeData(List<UpgradeDatum> upgradeData) {
			this.upgradeData = upgradeData;
		}

	}
}

