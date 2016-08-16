package sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 3/19/16.
 */
public class Coupon {

	@SerializedName("coupon_id")
	@Expose
	private Integer couponId;
	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("subtitle")
	@Expose
	private String subtitle;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("coupon_type")
	@Expose
	private Integer couponType;
	@SerializedName("type")
	@Expose
	private Integer type;
	@SerializedName("discount_percentage")
	@Expose
	private Double discountPercentage;
	@SerializedName("discount_maximum")
	@Expose
	private Double discountMaximum;
	@SerializedName("discount")
	@Expose
	private Double discount;
	@SerializedName("maximum")
	@Expose
	private Double maximum;
	@SerializedName("start_time")
	@Expose
	private String startTime;
	@SerializedName("end_time")
	@Expose
	private String endTime;
	@SerializedName("image")
	@Expose
	private String image;
	@SerializedName("account_id")
	@Expose
	private Integer accountId;
	@SerializedName("redeemed_on")
	@Expose
	private String redeemedOn;
	@SerializedName("status")
	@Expose
	private Integer status;
	@SerializedName("expiry_date")
	@Expose
	private String expiryDate;

	/**
	 *
	 * @return
	 * The couponId
	 */
	public Integer getCouponId() {
		return couponId;
	}

	/**
	 *
	 * @param couponId
	 * The coupon_id
	 */
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	/**
	 *
	 * @return
	 * The title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 *
	 * @param title
	 * The title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 *
	 * @return
	 * The subtitle
	 */
	public String getSubtitle() {
		return subtitle;
	}

	/**
	 *
	 * @param subtitle
	 * The subtitle
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	/**
	 *
	 * @return
	 * The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @param description
	 * The description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 *
	 * @return
	 * The couponType
	 */
	public Integer getCouponType() {
		return couponType;
	}

	/**
	 *
	 * @param couponType
	 * The coupon_type
	 */
	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	/**
	 *
	 * @return
	 * The type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 *
	 * @param type
	 * The type
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 *
	 * @return
	 * The discountPercentage
	 */
	public Double getDiscountPercentage() {
		return discountPercentage;
	}

	/**
	 *
	 * @param discountPercentage
	 * The discount_percentage
	 */
	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	/**
	 *
	 * @return
	 * The discountMaximum
	 */
	public Double getDiscountMaximum() {
		return discountMaximum;
	}

	/**
	 *
	 * @param discountMaximum
	 * The discount_maximum
	 */
	public void setDiscountMaximum(Double discountMaximum) {
		this.discountMaximum = discountMaximum;
	}

	/**
	 *
	 * @return
	 * The discount
	 */
	public Double getDiscount() {
		return discount;
	}

	/**
	 *
	 * @param discount
	 * The discount
	 */
	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	/**
	 *
	 * @return
	 * The maximum
	 */
	public Double getMaximum() {
		return maximum;
	}

	/**
	 *
	 * @param maximum
	 * The maximum
	 */
	public void setMaximum(Double maximum) {
		this.maximum = maximum;
	}

	/**
	 *
	 * @return
	 * The startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 *
	 * @param startTime
	 * The start_time
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 *
	 * @return
	 * The endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 *
	 * @param endTime
	 * The end_time
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 *
	 * @return
	 * The image
	 */
	public String getImage() {
		return image;
	}

	/**
	 *
	 * @param image
	 * The image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 *
	 * @return
	 * The accountId
	 */
	public Integer getAccountId() {
		return accountId;
	}

	/**
	 *
	 * @param accountId
	 * The account_id
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	/**
	 *
	 * @return
	 * The redeemedOn
	 */
	public String getRedeemedOn() {
		return redeemedOn;
	}

	/**
	 *
	 * @param redeemedOn
	 * The redeemed_on
	 */
	public void setRedeemedOn(String redeemedOn) {
		this.redeemedOn = redeemedOn;
	}

	/**
	 *
	 * @return
	 * The status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 *
	 * @param status
	 * The status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 *
	 * @return
	 * The expiryDate
	 */
	public String getExpiryDate() {
		return expiryDate;
	}

	/**
	 *
	 * @param expiryDate
	 * The expiry_date
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

}