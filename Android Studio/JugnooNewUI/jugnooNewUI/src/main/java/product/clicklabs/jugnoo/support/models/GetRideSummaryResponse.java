package product.clicklabs.jugnoo.support.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 1/27/16.
 */
public class GetRideSummaryResponse {

	@SerializedName("user_id")
	@Expose
	private Integer userId;
	@SerializedName("driver_id")
	@Expose
	private Integer driverId;
	@SerializedName("pickup_address")
	@Expose
	private String pickupAddress;
	@SerializedName("drop_address")
	@Expose
	private String dropAddress;
	@SerializedName("pickup_time")
	@Expose
	private String pickupTime;
	@SerializedName("drop_time")
	@Expose
	private String dropTime;
	@SerializedName("fare")
	@Expose
	private Integer fare;
	@SerializedName("paid_using_wallet")
	@Expose
	private Integer paidUsingWallet;
	@SerializedName("paid_using_paytm")
	@Expose
	private Integer paidUsingPaytm;
	@SerializedName("to_pay")
	@Expose
	private Integer toPay;
	@SerializedName("distance")
	@Expose
	private Integer distance;
	@SerializedName("wait_time")
	@Expose
	private Integer waitTime;
	@SerializedName("ride_time")
	@Expose
	private Integer rideTime;
	@SerializedName("total_luggage_charges")
	@Expose
	private Integer totalLuggageCharges;
	@SerializedName("waiting_charges_applicable")
	@Expose
	private Integer waitingChargesApplicable;
	@SerializedName("convenience_charge")
	@Expose
	private Integer convenienceCharge;
	@SerializedName("driver_name")
	@Expose
	private String driverName;
	@SerializedName("driver_car_no")
	@Expose
	private String driverCarNo;
	@SerializedName("discount")
	@Expose
	private List<Discount> discount = new ArrayList<Discount>();
	@SerializedName("base_fare")
	@Expose
	private Integer baseFare;
	@SerializedName("fare_factor")
	@Expose
	private Integer fareFactor;
	@SerializedName("jugnoo_balance")
	@Expose
	private Integer jugnooBalance;
	@SerializedName("paytm_balance")
	@Expose
	private Integer paytmBalance;
	@SerializedName("rate_app")
	@Expose
	private Integer rateApp;
	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("banner")
	@Expose
	private String banner;
	@SerializedName("menu")
	@Expose
	private List<ShowPanelResponse.Item> menu = new ArrayList<ShowPanelResponse.Item>();

	/**
	 *
	 * @return
	 * The userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 *
	 * @param userId
	 * The user_id
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 *
	 * @return
	 * The driverId
	 */
	public Integer getDriverId() {
		return driverId;
	}

	/**
	 *
	 * @param driverId
	 * The driver_id
	 */
	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	/**
	 *
	 * @return
	 * The pickupAddress
	 */
	public String getPickupAddress() {
		return pickupAddress;
	}

	/**
	 *
	 * @param pickupAddress
	 * The pickup_address
	 */
	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	/**
	 *
	 * @return
	 * The dropAddress
	 */
	public String getDropAddress() {
		return dropAddress;
	}

	/**
	 *
	 * @param dropAddress
	 * The drop_address
	 */
	public void setDropAddress(String dropAddress) {
		this.dropAddress = dropAddress;
	}

	/**
	 *
	 * @return
	 * The pickupTime
	 */
	public String getPickupTime() {
		return pickupTime;
	}

	/**
	 *
	 * @param pickupTime
	 * The pickup_time
	 */
	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	/**
	 *
	 * @return
	 * The dropTime
	 */
	public String getDropTime() {
		return dropTime;
	}

	/**
	 *
	 * @param dropTime
	 * The drop_time
	 */
	public void setDropTime(String dropTime) {
		this.dropTime = dropTime;
	}

	/**
	 *
	 * @return
	 * The fare
	 */
	public Integer getFare() {
		return fare;
	}

	/**
	 *
	 * @param fare
	 * The fare
	 */
	public void setFare(Integer fare) {
		this.fare = fare;
	}

	/**
	 *
	 * @return
	 * The paidUsingWallet
	 */
	public Integer getPaidUsingWallet() {
		return paidUsingWallet;
	}

	/**
	 *
	 * @param paidUsingWallet
	 * The paid_using_wallet
	 */
	public void setPaidUsingWallet(Integer paidUsingWallet) {
		this.paidUsingWallet = paidUsingWallet;
	}

	/**
	 *
	 * @return
	 * The paidUsingPaytm
	 */
	public Integer getPaidUsingPaytm() {
		return paidUsingPaytm;
	}

	/**
	 *
	 * @param paidUsingPaytm
	 * The paid_using_paytm
	 */
	public void setPaidUsingPaytm(Integer paidUsingPaytm) {
		this.paidUsingPaytm = paidUsingPaytm;
	}

	/**
	 *
	 * @return
	 * The toPay
	 */
	public Integer getToPay() {
		return toPay;
	}

	/**
	 *
	 * @param toPay
	 * The to_pay
	 */
	public void setToPay(Integer toPay) {
		this.toPay = toPay;
	}

	/**
	 *
	 * @return
	 * The distance
	 */
	public Integer getDistance() {
		return distance;
	}

	/**
	 *
	 * @param distance
	 * The distance
	 */
	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	/**
	 *
	 * @return
	 * The waitTime
	 */
	public Integer getWaitTime() {
		return waitTime;
	}

	/**
	 *
	 * @param waitTime
	 * The wait_time
	 */
	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 *
	 * @return
	 * The rideTime
	 */
	public Integer getRideTime() {
		return rideTime;
	}

	/**
	 *
	 * @param rideTime
	 * The ride_time
	 */
	public void setRideTime(Integer rideTime) {
		this.rideTime = rideTime;
	}

	/**
	 *
	 * @return
	 * The totalLuggageCharges
	 */
	public Integer getTotalLuggageCharges() {
		return totalLuggageCharges;
	}

	/**
	 *
	 * @param totalLuggageCharges
	 * The total_luggage_charges
	 */
	public void setTotalLuggageCharges(Integer totalLuggageCharges) {
		this.totalLuggageCharges = totalLuggageCharges;
	}

	/**
	 *
	 * @return
	 * The waitingChargesApplicable
	 */
	public Integer getWaitingChargesApplicable() {
		return waitingChargesApplicable;
	}

	/**
	 *
	 * @param waitingChargesApplicable
	 * The waiting_charges_applicable
	 */
	public void setWaitingChargesApplicable(Integer waitingChargesApplicable) {
		this.waitingChargesApplicable = waitingChargesApplicable;
	}

	/**
	 *
	 * @return
	 * The convenienceCharge
	 */
	public Integer getConvenienceCharge() {
		return convenienceCharge;
	}

	/**
	 *
	 * @param convenienceCharge
	 * The convenience_charge
	 */
	public void setConvenienceCharge(Integer convenienceCharge) {
		this.convenienceCharge = convenienceCharge;
	}

	/**
	 *
	 * @return
	 * The driverName
	 */
	public String getDriverName() {
		return driverName;
	}

	/**
	 *
	 * @param driverName
	 * The driver_name
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 *
	 * @return
	 * The driverCarNo
	 */
	public String getDriverCarNo() {
		return driverCarNo;
	}

	/**
	 *
	 * @param driverCarNo
	 * The driver_car_no
	 */
	public void setDriverCarNo(String driverCarNo) {
		this.driverCarNo = driverCarNo;
	}

	/**
	 *
	 * @return
	 * The discount
	 */
	public List<Discount> getDiscount() {
		return discount;
	}

	/**
	 *
	 * @param discount
	 * The discount
	 */
	public void setDiscount(List<Discount> discount) {
		this.discount = discount;
	}

	/**
	 *
	 * @return
	 * The baseFare
	 */
	public Integer getBaseFare() {
		return baseFare;
	}

	/**
	 *
	 * @param baseFare
	 * The base_fare
	 */
	public void setBaseFare(Integer baseFare) {
		this.baseFare = baseFare;
	}

	/**
	 *
	 * @return
	 * The fareFactor
	 */
	public Integer getFareFactor() {
		return fareFactor;
	}

	/**
	 *
	 * @param fareFactor
	 * The fare_factor
	 */
	public void setFareFactor(Integer fareFactor) {
		this.fareFactor = fareFactor;
	}

	/**
	 *
	 * @return
	 * The jugnooBalance
	 */
	public Integer getJugnooBalance() {
		return jugnooBalance;
	}

	/**
	 *
	 * @param jugnooBalance
	 * The jugnoo_balance
	 */
	public void setJugnooBalance(Integer jugnooBalance) {
		this.jugnooBalance = jugnooBalance;
	}

	/**
	 *
	 * @return
	 * The paytmBalance
	 */
	public Integer getPaytmBalance() {
		return paytmBalance;
	}

	/**
	 *
	 * @param paytmBalance
	 * The paytm_balance
	 */
	public void setPaytmBalance(Integer paytmBalance) {
		this.paytmBalance = paytmBalance;
	}

	/**
	 *
	 * @return
	 * The rateApp
	 */
	public Integer getRateApp() {
		return rateApp;
	}

	/**
	 *
	 * @param rateApp
	 * The rate_app
	 */
	public void setRateApp(Integer rateApp) {
		this.rateApp = rateApp;
	}

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
	 * The banner
	 */
	public String getBanner() {
		return banner;
	}

	/**
	 *
	 * @param banner
	 * The banner
	 */
	public void setBanner(String banner) {
		this.banner = banner;
	}

	/**
	 *
	 * @return
	 * The menu
	 */
	public List<ShowPanelResponse.Item> getMenu() {
		return menu;
	}

	/**
	 *
	 * @param menu
	 * The menu
	 */
	public void setMenu(List<ShowPanelResponse.Item> menu) {
		this.menu = menu;
	}



	public class Discount {

		@SerializedName("key")
		@Expose
		private String key;
		@SerializedName("value")
		@Expose
		private Integer value;

		/**
		 *
		 * @return
		 * The key
		 */
		public String getKey() {
			return key;
		}

		/**
		 *
		 * @param key
		 * The key
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 *
		 * @return
		 * The value
		 */
		public Integer getValue() {
			return value;
		}

		/**
		 *
		 * @param value
		 * The value
		 */
		public void setValue(Integer value) {
			this.value = value;
		}

	}

}
