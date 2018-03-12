package com.sabkuchfresh.retrofit.model.menus;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.retrofit.model.RecentOrder;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.OfferingsVisibilityResponse;
import product.clicklabs.jugnoo.utils.DateOperations;

/**
 * Created by shankar on 11/15/16.
 */
public class MenusResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("support_contact")
	@Expose
	private String supportContact;
	@SerializedName("recent_orders")
	@Expose
	private List<RecentOrder> recentOrders = new ArrayList<RecentOrder>();
	@SerializedName("recent_orders_possible_status")
	@Expose
	private List<String> recentOrdersPossibleStatus = new ArrayList<String>();
	@SerializedName("recent_orders_possible_meals_status")
	@Expose
	private List<String> recentOrdersPossibleMealsStatus = new ArrayList<String>();
	@SerializedName("recent_orders_possible_fatafat_status")
	@Expose
	private List<String> recentOrdersPossibleFatafatStatus = new ArrayList<String>();

	public List<String> getRecentOrdersPossibleFatafatStatus() {
		return recentOrdersPossibleFatafatStatus;
	}

	@SerializedName("banner_info")
	@Expose
	private List<BannerInfo> bannerInfos;
	@SerializedName("show_banner")
	@Expose
	private boolean showBanner;
	@SerializedName("strip_info")
	@Expose
	private StripInfo stripInfo;
	@SerializedName("is_complete")
	private int isPageLengthComplete;
	@SerializedName("service_unavailable")
	private int serviceUnavailable;

    @SerializedName("chat_available")
	private int chatAvailable;


	@SerializedName("category_map")
	@Expose
	private List<Category> categories;
	@SerializedName("filters_generic")
	@Expose
	private List<KeyValuePair> filters;
	@SerializedName("sort_generic")
	@Expose
	private List<KeyValuePair> sorting;
	@SerializedName("vendors")
	@Expose
	private List<Vendor> vendors = new ArrayList<Vendor>();

	@SerializedName("suggestions")
	private List<VendorDirectSearch> directSearchVendors;

	@SerializedName("is_open_merchant_info")
	@Expose
	private int isOpenMerchantInfo;

	@SerializedName("region_data")
	@Expose
	private OfferingsVisibilityResponse.OfferingsVisibilityData offeringsVisibilityData ;

	public OfferingsVisibilityResponse.OfferingsVisibilityData getOfferingsVisibilityData() {
		return offeringsVisibilityData;
	}

	public List<VendorDirectSearch> getDirectSearchVendors() {
		return directSearchVendors;
	}

	public void setDirectSearchVendors(final List<VendorDirectSearch> directSearchVendors) {
		this.directSearchVendors = directSearchVendors;
	}

	public int getChatAvailable() {
		return chatAvailable;
	}

	/**
	 * @return The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return The supportContact
	 */
	public String getSupportContact() {
		return supportContact;
	}

	/**
	 * @param supportContact The support_contact
	 */
	public void setSupportContact(String supportContact) {
		this.supportContact = supportContact;
	}

	public List<RecentOrder> getRecentOrders() {
		if (recentOrders == null) {
			recentOrders = new ArrayList<>();
		}
		return recentOrders;
	}

	public void setRecentOrders(List<RecentOrder> recentOrders) {
		this.recentOrders = recentOrders;
	}

	public List<String> getRecentOrdersPossibleMealsStatus() {
		return recentOrdersPossibleMealsStatus;
	}

	public List<String> getRecentOrdersPossibleStatus() {
		return recentOrdersPossibleStatus;
	}

	public void setRecentOrdersPossibleStatus(List<String> recentOrdersPossibleStatus) {
		this.recentOrdersPossibleStatus = recentOrdersPossibleStatus;
	}

	public List<BannerInfo> getBannerInfos() {
		return bannerInfos;
	}

	public boolean getShowBanner() {
		return showBanner;
	}

	public StripInfo getStripInfo() {
		return stripInfo;
	}

	public boolean isPageLengthComplete() {
		return isPageLengthComplete == 1;
	}

	public int getServiceUnavailable() {
		return serviceUnavailable;
	}

	public void setServiceUnavailable(int serviceUnavailable) {
		this.serviceUnavailable = serviceUnavailable;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<KeyValuePair> getFilters() {
		if(filters == null){
			filters = new ArrayList<>();
		}
		return filters;
	}

	public void setFilters(List<KeyValuePair> filters) {
		this.filters = filters;
	}

	public List<KeyValuePair> getSorting() {
		if(sorting == null){
			sorting = new ArrayList<>();
		}
		return sorting;
	}

	public void setSorting(List<KeyValuePair> sorting) {
		this.sorting = sorting;
	}

	public boolean isOpenMerchantInfo() {
		return isOpenMerchantInfo == 1;
	}

	public void setIsOpenMerchantInfo(int isOpenMerchantInfo) {
		this.isOpenMerchantInfo = isOpenMerchantInfo;
	}

	// todo dummy method remove
	public ArrayList<VendorDirectSearch> getDummyDirectSearchVendors(){
		ArrayList<VendorDirectSearch> vendors = new ArrayList<>();
		for(int i=0;i<4;i++){
			VendorDirectSearch vendorDirectSearch = new VendorDirectSearch();
			vendorDirectSearch.setLine1("Query:"+String.valueOf(i+1));
			vendorDirectSearch.setLine2Start("in");
			vendorDirectSearch.setLine2End("Restaurent:"+String.valueOf(i+1));
			vendorDirectSearch.setLine1Color("#000000");
			vendorDirectSearch.setLine2StartColor("#000000");
			vendorDirectSearch.setLine2EndColor("#000000");
			vendorDirectSearch.setVendorId(332);
			vendorDirectSearch.setCategoryId(298);
			//vendorDirectSearch.setSubcategoryId(166);
			vendorDirectSearch.setItemId(54691);
			vendors.add(vendorDirectSearch);
		}
		return vendors;
	}

	public class VendorDirectSearch{

		@SerializedName("line_1")
		private String line1;

		@SerializedName("line_1_color")
		private String line1Color;

		@SerializedName("line_2_start")
		private String line2Start;

		@SerializedName("line_2_start_color")
		private String line2StartColor;

		@SerializedName("line_2_end")
		private String line2End;

		@SerializedName("line_2_end_color")
		private String line2EndColor;

		@SerializedName("restaurant_id")
		private int vendorId;

		@SerializedName("category_id")
		private int categoryId;

		@SerializedName("sub_category_id")
		private int subcategoryId;

		@SerializedName("restaurant_item_id")
		private int itemId;

		public String getLine1() {
			return line1;
		}

		public String getLine1Color() {
			return line1Color;
		}

		public String getLine2Start() {
			return line2Start;
		}

		public String getLine2StartColor() {
			return line2StartColor;
		}

		public String getLine2End() {
			return line2End;
		}

		public String getLine2EndColor() {
			return line2EndColor;
		}

		public void setLine1(final String line1) {
			this.line1 = line1;
		}

		public void setLine1Color(final String line1Color) {
			this.line1Color = line1Color;
		}

		public void setLine2Start(final String line2Start) {
			this.line2Start = line2Start;
		}

		public void setLine2StartColor(final String line2StartColor) {
			this.line2StartColor = line2StartColor;
		}

		public void setLine2End(final String line2End) {
			this.line2End = line2End;
		}

		public void setLine2EndColor(final String line2EndColor) {
			this.line2EndColor = line2EndColor;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(final int itemId) {
			this.itemId = itemId;
		}

		public int getVendorId() {
			return vendorId;
		}

		public void setVendorId(final int vendorId) {
			this.vendorId = vendorId;
		}

		public int getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(final int categoryId) {
			this.categoryId = categoryId;
		}

		public int getSubcategoryId() {
			return subcategoryId;
		}

		public void setSubcategoryId(final int subcategoryId) {
			this.subcategoryId = subcategoryId;
		}
	}



	public class Vendor {

		@SerializedName("restaurant_id")
		@Expose
		private Integer restaurantId;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("image")
		@Expose
		private String image;
		@SerializedName("minimum_order_amount")
		@Expose
		private double minimumOrderAmount;
		@SerializedName("delivery_amount_threshold")
		@Expose
		private Double deliveryAmountThreshold;
		@SerializedName("show_free_delivery_text")
		@Expose
		private Integer showFreeDeliveryText;
		@SerializedName("is_closed")
		@Expose
		private Integer isClosed;

		@SerializedName("is_available")
		@Expose
		private Integer isAvailable;

		@SerializedName("distance")
		@Expose
		private Double distance;
		@SerializedName("cuisines")
		@Expose
		private List<String> cuisines = new ArrayList<String>();
		@SerializedName("price_range")
		@Expose
		private Integer priceRange;
		@SerializedName("delivery_time")
		@Expose
		private Integer deliveryTime;
		@SerializedName("min_delivery_time")
		@Expose
		private Integer minDeliveryTime;
		@SerializedName("opens_at")
		@Expose
		private String opensAt;
		@SerializedName("popularity")
		@Expose
		private Integer popularity;
		@SerializedName("applicable_payment_mode")
		@Expose
		private Integer applicablePaymentMode;
		@SerializedName("service_tax")
		@Expose
		private Double serviceTax;
		@SerializedName("value_added_tax")
		@Expose
		private Double valueAddedTax;
		@SerializedName("packing_charges")
		@Expose
		private Double packingCharges;
		@SerializedName("packing_charges_percent")
		@Expose
		private Double packagingChargesInPercent;


		@SerializedName("pure_veg")
		@Expose
		private Integer pureVegetarian;
		@SerializedName("offer_discount")
		@Expose
		private Integer offersDiscounts;
		@SerializedName("free_delivery")
		@Expose
		private Integer freeDelivery;

		@SerializedName("close_at")
		@Expose
		private String closeIn;
		@SerializedName("display_address")
		@Expose
		private String displayAddress;
		@SerializedName("address")
		@Expose
		private String address;

		//		@SerializedName("buffer_time")
		@SerializedName("close_in_buffer")
		@Expose
		private Long bufferTime;


		@SerializedName("rating")
		private Double rating;

		@SerializedName("review_count")
		private long reviewCount;
		@SerializedName("delivery_time_text")
		@Expose
		private String deliveryTimeText;
		@SerializedName("min_order_text")
		@Expose
		private String minOrderText;

		@SerializedName("item_inactive_alert_text")
		@Expose
		private String itemInactiveAlertText;

		public Double getRating() {
			return rating == null ? 4D : Math.round(rating * 10.0) / 10.0;
		}

		public long getReviewCount() {
			return reviewCount;
		}

		@SerializedName("rating_color")
		private String colorCode;


		@SerializedName("offer_text")
		private String offerText;

		@SerializedName("next_slot")
		@Expose
		private String next_slot_time;
		@SerializedName("next_open_text")
		@Expose
		private String nextOpenText;

		@SerializedName("contact_list")
		@Expose
		private String contactList;
		@SerializedName("calling_number")
		@Expose
		private String callingNumber;
		@SerializedName("email")
		@Expose
		private String email;
		@SerializedName("chat_mode")
		@Expose
		private int chatMode;
		@SerializedName("order_mode")
		@Expose
		private int orderMode;
		@SerializedName("pay_mode")
		@Expose
		private int payMode;
		@SerializedName("restaurant_timings")
		@Expose
		private List<RestaurantTiming> restaurantTimings;
		@SerializedName("latitude")
		@Expose
		private double latitude;
		@SerializedName("longitude")
		@Expose
		private double longitude;

		@SerializedName("out_of_radius")
		private Object outOfRadius;

		@SerializedName("out_of_radius_strip")
		private boolean outOfRadiusStrip;

		private boolean userJustEditedReview;

		public boolean isUserJustEditedReview() {
			return userJustEditedReview;
		}

		public void setUserJustEditedReview(final boolean userJustEditedReview) {
			this.userJustEditedReview = userJustEditedReview;
		}

		public boolean isOutOfRadiusStrip() {
			return outOfRadiusStrip;
		}

		public int getOutOfRadius() {
			if(outOfRadius instanceof Integer || outOfRadius instanceof Double)
				return (int) outOfRadius;
			else if(outOfRadius instanceof Boolean)
			{
				return (boolean)outOfRadius?1:0;
			}

			return 0;
		}

		private boolean hasRated;
		public boolean isChatModeEnabled() {
			return chatMode == 1;
		}

		public int getOrderMode() {
			return orderMode;
		}

		public boolean getPayModeEnabled() {
			return payMode==1;
		}

		public String getNext_slot_time() {
			return next_slot_time;
		}

		public String getOfferText() {
			return offerText;
		}

		public String getColorCode() {
			return colorCode;
		}

		public void setDisplayAddress(String displayAddress) {
			this.displayAddress = displayAddress;
		}

		public void setRating(Double rating) {
			this.rating = rating;
		}

		public void setReviewCount(long reviewCount) {
			this.reviewCount = reviewCount;
		}

		public void setColorCode(String colorCode) {
			this.colorCode = colorCode;
		}

		/**
		 * @return The restaurantId
		 */
		public Integer getRestaurantId() {
			return restaurantId;
		}

		/**
		 * @param restaurantId The restaurantId
		 */
		public void setRestaurantId(Integer restaurantId) {
			this.restaurantId = restaurantId;
		}

		/**
		 * @return The name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name The vendor_name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return The image
		 */
		public String getImage() {
			return image;
		}

		/**
		 * @param image The image
		 */
		public void setImage(String image) {
			this.image = image;
		}

		/**
		 * @return The minimumOrderAmount
		 */
		public double getMinimumOrderAmount() {
			return minimumOrderAmount;
		}



		/**
		 * @param minimumOrderAmount The minimum_order_amount
		 */
		public void setMinimumOrderAmount(Double minimumOrderAmount) {
			this.minimumOrderAmount = minimumOrderAmount;
		}

		/**
		 * @return The isClosed
		 */
		public Integer getIsClosed() {
			return isClosed;
		}

		/**
		 * @param isClosed The is_closed
		 */
		public void setIsClosed(Integer isClosed) {
			this.isClosed = isClosed;
		}


		public Integer getIsAvailable() {
			return isAvailable;
		}

		/**
		 * @param isAvailable The is_closed
		 */
		public void setIsAvailable(Integer isAvailable) {
			this.isAvailable = isAvailable;
		}

		public boolean isClosed(){
			return (getIsClosed() == 1 || getIsAvailable() == 0);
		}

		/**
		 * @return The distance
		 */
		public Double getDistance() {
			return distance;
		}

		/**
		 * @param distance The distance
		 */
		public void setDistance(Double distance) {
			this.distance = distance;
		}

		/**
		 * @return The cuisines
		 */
		public List<String> getCuisines() {
			return cuisines;
		}

		/**
		 * @param cuisines The cuisines
		 */
		public void setCuisines(List<String> cuisines) {
			this.cuisines = cuisines;
		}

		public Integer getPriceRange() {
			return priceRange;
		}

		public void setPriceRange(Integer priceRange) {
			this.priceRange = priceRange;
		}

		public Integer getDeliveryTime() {
			return deliveryTime;
		}

		public void setDeliveryTime(Integer deliveryTime) {
			this.deliveryTime = deliveryTime;
		}

		public String getOpensAt() {
			return opensAt;
		}

		public void setOpensAt(String opensAt) {
			this.opensAt = opensAt;
		}

		public Integer getPopularity() {
			return popularity;
		}

		public void setPopularity(Integer popularity) {
			this.popularity = popularity;
		}

		public Integer getMinDeliveryTime() {
			return minDeliveryTime;
		}

		public void setMinDeliveryTime(Integer minDeliveryTime) {
			this.minDeliveryTime = minDeliveryTime;
		}

		public Integer getApplicablePaymentMode() {
			return applicablePaymentMode;
		}

		public void setApplicablePaymentMode(Integer applicablePaymentMode) {
			this.applicablePaymentMode = applicablePaymentMode;
		}

		public Double getServiceTax() {
			return serviceTax;
		}

		public void setServiceTax(Double serviceTax) {
			this.serviceTax = serviceTax;
		}

		public Double getValueAddedTax() {
			return valueAddedTax;
		}

		public void setValueAddedTax(Double vat) {
			this.valueAddedTax = vat;
		}

		public Double getPackingCharges() {
			return packingCharges;
		}

		public void setPackingCharges(Double packingCharges) {
			this.packingCharges = packingCharges;
		}

		public Double getPackagingChargesInPercent() {
			return packagingChargesInPercent;
		}

		public void setPackagingChargesInPercent(Double packagingChargesInPercent) {
			this.packagingChargesInPercent = packagingChargesInPercent;
		}


		public Integer getPureVegetarian() {
			if (pureVegetarian != null) {
				return pureVegetarian;
			} else {
				return 0;
			}
			//	return pureVegetarian;
		}

		public void setPureVegetarian(Integer pureVegetarian) {
			this.pureVegetarian = pureVegetarian;
		}

		public Integer getOffersDiscounts() {
			if (offersDiscounts != null) {
				return offersDiscounts;
			} else {
				return 0;
			}
			//	return offersDiscounts;
		}

		public void setOffersDiscounts(Integer offersDiscounts) {
			this.offersDiscounts = offersDiscounts;
		}

		public Integer getFreeDelivery() {
			if (freeDelivery != null) {
				return freeDelivery;
			} else {
				return 0;
			}
		}

		public void setFreeDelivery(Integer freeDelivery) {
			this.freeDelivery = freeDelivery;
		}

		public String getDisplayAddress() {
			return displayAddress;
		}

		public String getCloseIn() {
			return closeIn;
		}

		public void setCloseIn(String closeIn) {
			this.closeIn = closeIn;
		}

		public Long getBufferTime() {
			return bufferTime;
		}

		public void setBufferTime(Long bufferTime) {
			this.bufferTime = bufferTime;
		}

		public Double getDeliveryAmountThreshold() {
			if (deliveryAmountThreshold == null) {
				deliveryAmountThreshold = 0D;
			}
			return deliveryAmountThreshold;
		}

		public void setDeliveryAmountThreshold(Double deliveryAmountThreshold) {
			this.deliveryAmountThreshold = deliveryAmountThreshold;
		}

		public Integer getShowFreeDeliveryText() {
			if (showFreeDeliveryText == null) {
				showFreeDeliveryText = 0;
			}
			return showFreeDeliveryText;
		}

		public void setShowFreeDeliveryText(Integer showFreeDeliveryText) {
			this.showFreeDeliveryText = showFreeDeliveryText;
		}

		public String getDeliveryTimeText() {
			return deliveryTimeText;
		}

		public void setDeliveryTimeText(String deliveryTimeText) {
			this.deliveryTimeText = deliveryTimeText;
		}

		public String getMinOrderText() {
			return minOrderText;
		}

		public void setMinOrderText(String minOrderText) {
			this.minOrderText = minOrderText;
		}

		public String getItemInactiveAlertText(Context context) {
			if (itemInactiveAlertText == null) {
				return context.getString(R.string.item_not_available);
			} else {
				return itemInactiveAlertText;
			}
		}

		public void setItemInactiveAlertText(String itemInactiveAlertText) {
			this.itemInactiveAlertText = itemInactiveAlertText;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getContactList() {
			return contactList;
		}

		public void setContactList(String contactList) {
			this.contactList = contactList;
		}

		public String getCallingNumber() {
			return callingNumber;
		}

		public void setCallingNumber(String callingNumber) {
			this.callingNumber = callingNumber;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public List<RestaurantTiming> getRestaurantTimings() {
			return restaurantTimings;
		}

		public void setRestaurantTimings(List<RestaurantTiming> restaurantTimings) {
			this.restaurantTimings = restaurantTimings;
		}

		public String getRestaurantTimingsStr(){
			StringBuilder sb = new StringBuilder();
			if(restaurantTimings != null){
				for(RestaurantTiming timing : restaurantTimings){
					sb.append(DateOperations.convertDayTimeAPViaFormat(timing.getStartTime(), true))
							.append(" - ")
							.append(DateOperations.convertDayTimeAPViaFormat(timing.getEndTime(), true))
							.append("\n");
				}
			}
			if(sb.length() > 1){
				return sb.toString().substring(0, sb.length()-1);
			}
			return sb.toString();
		}

		public LatLng getLatLng() {
			return new LatLng(latitude, longitude);
		}

		public String getNextOpenText() {
			return nextOpenText;
		}

		public void setNextOpenText(String nextOpenText) {
			this.nextOpenText = nextOpenText;
		}

		public void setHasRated(boolean hasRated) {
			this.hasRated = hasRated;
		}

		public boolean isHasRated() {
			return hasRated;
		}
	}


	public class BannerInfo {
		@SerializedName("image_link")
		@Expose
		private String imageLink;
		@SerializedName("restaurant_id")
		@Expose
		private Integer restaurantId;
		@SerializedName("deep_index")
		@Expose
		private Integer deepIndex;

		public String getImageLink() {
			return imageLink;
		}

		public Integer getRestaurantId() {
			if (restaurantId == null) {
				restaurantId = -1;
			}
			return restaurantId;
		}

		public Integer getDeepIndex() {
			if (deepIndex == null) {
				deepIndex = -1;
			}
			return deepIndex;
		}
	}

	public class StripInfo {
		@SerializedName("text")
		@Expose
		private String text;
		@SerializedName("restaurant_id")
		@Expose
		private Integer restaurantId;
		@SerializedName("deep_index")
		@Expose
		private Integer deepIndex;

		public Integer getRestaurantId() {
			if (restaurantId == null) {
				restaurantId = -1;
			}
			return restaurantId;
		}

		public Integer getDeepIndex() {
			if (deepIndex == null) {
				deepIndex = -1;
			}
			return deepIndex;
		}

		public String getText() {
			return text;
		}
	}


	public static class Category {
		@SerializedName("id")
		@Expose
		private int id;
		@SerializedName("category")
		@Expose
		private String categoryName;
		@SerializedName("image")
		@Expose
		private String image;
		@SerializedName("tags")
		@Expose
		private String tagsName;
		@SerializedName("count")
		@Expose
		private int count;
		@SerializedName("filters")
		@Expose
		private List<KeyValuePair> filters;
		@SerializedName("sorting")
		@Expose
		private List<KeyValuePair> sorting;
		@SerializedName("client_id")
		@Expose
		private String clientId;

		public String getClientId() {
			return clientId;
		}

		private boolean isTypeOrder;

		public Category(boolean isTypeOrder) {
			this.isTypeOrder = isTypeOrder;
		}

		public Category(int id) {
			this.id = id;
		}

		public Category(int id, String categoryName) {
			this.id = id;
			this.categoryName = categoryName;
		}

		public Category(String image, String categoryName) {
			this.image = image;
			this.categoryName = categoryName;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public boolean isTypeOrder() {
			return isTypeOrder;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getTagsName() {
			return tagsName;
		}

		public void setTagsName(String tagsName) {
			this.tagsName = tagsName;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Category && ((Category)obj).getId() == getId();
		}

		public List<KeyValuePair> getSorting() {
			return sorting;
		}

		public void setSorting(List<KeyValuePair> sorting) {
			this.sorting = sorting;
		}

		public List<KeyValuePair> getFilters() {
			return filters;
		}

		public void setFilters(List<KeyValuePair> filters) {
			this.filters = filters;
		}
	}

	public static class KeyValuePair{
		@SerializedName("key")
		@Expose
		private String key;
		@SerializedName("value")
		@Expose
		private String value;

		public KeyValuePair(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof KeyValuePair && ((KeyValuePair)obj).getKey().equals(getKey());
		}
	}

	public static class RestaurantTiming{
		@SerializedName("start_time")
		@Expose
		private String startTime;
		@SerializedName("end_time")
		@Expose
		private String endTime;

		public String getStartTime() {
			return startTime;
		}

		public String getEndTime() {
			return endTime;
		}
	}

	public List<Vendor> getVendors() {
		return vendors;
	}

	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}

}
