package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
		@SerializedName("upgradeArray")
		@Expose
		private List<UpgradeArray> upgradeArray = null;

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

		public List<UpgradeArray> getUpgradeArray() {
			return upgradeArray;
		}

		public void setUpgradeArray(List<UpgradeArray> upgradeArray) {
			this.upgradeArray = upgradeArray;
		}

	}
}

