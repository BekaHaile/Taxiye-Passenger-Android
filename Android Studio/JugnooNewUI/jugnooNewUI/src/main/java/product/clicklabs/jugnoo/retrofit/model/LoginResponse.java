package product.clicklabs.jugnoo.retrofit.model;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.datastructure.FatafatTutorialData;
import com.sabkuchfresh.datastructure.PopupData;
import com.sabkuchfresh.datastructure.SearchSuggestion;
import com.sabkuchfresh.retrofit.model.PaymentGatewayModeConfig;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.Store;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.SignupTutorial;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.models.JeanieIntroDialogContent;
import product.clicklabs.jugnoo.home.models.MenuInfo;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;

/**
 * Created by shankar on 1/5/16.
 */
public class LoginResponse {
	public Pay getPay() {
		return pay;
	}

	public void setPay(Pay pay) {
		this.pay = pay;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public Pros getPros() {
		return pros;
	}

	public void setPros(Pros pros) {
		this.pros = pros;
	}

	public DeliveryCustomer getDeliveryCustomer() {
		return deliveryCustomer;
	}

	public void setDeliveryCustomer(DeliveryCustomer deliveryCustomer) {
		this.deliveryCustomer = deliveryCustomer;
	}

	public class Cancellation {

		@SerializedName("message")
		@Expose
		private String message;
		@SerializedName("reasons")
		@Expose
		private List<String> reasons = new ArrayList<String>();
		@SerializedName("addn_reason")
		@Expose
		private String addnReason;

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
		 * @return The reasons
		 */
		public List<String> getReasons() {
			return reasons;
		}

		/**
		 * @param reasons The reasons
		 */
		public void setReasons(List<String> reasons) {
			this.reasons = reasons;
		}

		/**
		 * @return The addnReason
		 */
		public String getAddnReason() {
			return addnReason;
		}

		/**
		 * @param addnReason The addn_reason
		 */
		public void setAddnReason(String addnReason) {
			this.addnReason = addnReason;
		}

	}

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("drivers")
	@Expose
	private List<Driver> drivers = new ArrayList<>();
	@SerializedName("autos")
	@Expose
	private Autos autos;
	@SerializedName("user_data")
	@Expose
	private UserData userData;
	@SerializedName("fresh")
	@Expose
	private Menus fresh;
	@SerializedName("meals")
	@Expose
	private Meals meals;
	@SerializedName("grocery")
	@Expose
	private Menus grocery;
	@SerializedName("menus")
	@Expose
	private Menus menus;
	@SerializedName("delivery_customer")
	@Expose
	private DeliveryCustomer deliveryCustomer;
	@SerializedName("pay")
	@Expose
	private Pay pay;
	@SerializedName("feed")
	@Expose
	private Feed feed;
	@SerializedName("delivery")
	@Expose
	private Delivery delivery;
	@SerializedName("pros")
	@Expose
	private Pros pros;
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
	 * @return The drivers
	 */
	public List<Driver> getDrivers() {
		return drivers;
	}

	/**
	 * @param drivers The drivers
	 */
	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	public Autos getAutos() {
		return autos;
	}

	public void setAutos(Autos autos) {
		this.autos = autos;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public Menus getFresh() {
		return fresh;
	}

	public void setFresh(Menus fresh) {
		this.fresh = fresh;
	}

	public Meals getMeals() {
		return meals;
	}

	public void setMeals(Meals meals) {
		this.meals = meals;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Menus getGrocery() {
		return grocery;
	}

	public void setGrocery(Menus grocery) {
		this.grocery = grocery;
	}

	public Menus getMenus() {
		return menus;
	}

	public void setMenus(Menus menus) {
		this.menus = menus;
	}

	public class Delivery{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}
	}

	public class Meals extends Menus{

		@SerializedName("offers_strip_meals")
		private OfferStripMeals offerStripMeals;
		@SerializedName("meals_favorite_feature")
		private MealsFavouriteFeature mealsFavouriteFeature;
		@SerializedName("feedback_order_items")
		private String feedbackOrderItems;



		public class OfferStripMeals {

			@SerializedName("message")
			private String textToDisplay;

			@SerializedName("deepindex")
			private String deepIndex;


			public String getTextToDisplay() {
				return textToDisplay;
			}

			public String getDeepIndex() {
				return deepIndex;
			}
		}

		public class MealsFavouriteFeature {
			@SerializedName("is_enabled")
			private int isEnabled;

			public boolean getIsEnabled() {
				return isEnabled>0;
			}
		}

		public OfferStripMeals getOfferStripMeals() {
			return offerStripMeals;
		}

		public MealsFavouriteFeature getMealsFavouriteFeature() {
			return mealsFavouriteFeature;
		}

		public boolean isMealsFavEnabled(){
			return mealsFavouriteFeature!=null && mealsFavouriteFeature.getIsEnabled();
		}

		public String getFeedbackOrderItems() {
			return feedbackOrderItems;
		}
	}





	public class Menus extends FeedbackData{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();

		@SerializedName(Constants.KEY_FATAFAT_ENABLED)
		int isFatafatEnabled = 1;

		@SerializedName("popup_data")
		private PopupData popupData;

		@SerializedName("stores")
		private ArrayList<Store> stores;



		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromoCoupons(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}

		public int getIsFatafatEnabled() {
			return isFatafatEnabled;
		}



		public PopupData getPopupData() {
			return popupData;
		}

		public ArrayList<Store> getStores() {
			return stores;
		}



		public void setIsFatafatEnabled(int isFatafatEnabled) {
			this.isFatafatEnabled = isFatafatEnabled;
		}

		private ArrayList<PromoCoupon> promoCoupons;

		public ArrayList<PromoCoupon> getPromoCoupons() {
			return promoCoupons;
		}

		public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
			this.promoCoupons = promoCoupons;
		}

	}

	public class DeliveryCustomer extends Menus{

		@SerializedName(Constants.SHOW_ADD_STORE)
		private int showAddStore;

		@SerializedName(Constants.ADD_STORE_IMAGES_LIMIT)
		private int addStoreImagesLimit;

		@SerializedName(Constants.KEY_MERCHANT_CATEGORY_ID)
		private int merchantCategoryId;

		@SerializedName(Constants.MERCHANT_CATEGORIES)
		private ArrayList<MenusResponse.Category> merchantCategories;

		@SerializedName("suggestions")
		private ArrayList<SearchSuggestion> recentSearches;


		public ArrayList<MenusResponse.Category> getMerchantCategoriesList() {
			return merchantCategories;
		}

		public ArrayList<SearchSuggestion> getRecentSearches() {
			return recentSearches;
		}

		public void setRecentSearches(final ArrayList<SearchSuggestion> recentSearches) {
			this.recentSearches = recentSearches;
		}

		public boolean getShowAddStore() {
			return showAddStore==1;
		}

		public int getAddStoreImages() {
			return addStoreImagesLimit;
		}

		public int getMerchantCategoryId() {
			return merchantCategoryId;
		}

	}

	public class Feed extends FeedbackData{
		@SerializedName("contacts_synced")
		@Expose
		private Integer contactsSynced;



		@SerializedName("review_placeholder")
		@Expose
		private String feedAddReviewHint;

		@SerializedName("ask_something_placeholder")
		@Expose
		private String feedAddPostHInt;

		@SerializedName("max_upload_images")
		@Expose
		private Integer maxUploadImagesFeed;

		@SerializedName("feed_reg_string")
		@Expose
		private String feedIntroString;


		@SerializedName("feed_active")
		@Expose
		private int feedActive;
		@SerializedName("feed_rank")
		@Expose
		private Long feedRank;
		@SerializedName("user_count")
		@Expose
		private long usersCount;

		@SerializedName("early_access_text")
		@Expose
		private String earlyAccessText;
		@SerializedName("early_access_share_title")
		@Expose
		private String earlyAccessShareTitle;
		@SerializedName("early_access_share_desc")
		@Expose
		private String earlyAccessShareDesc;
		@SerializedName("early_access_share_image")
		@Expose
		private String earlyAccessShareImage;
		@SerializedName("show_create_handle")
		@Expose
		private int showCreateHandle;


		@SerializedName("anonymous_functionality_enabled")
		@Expose
		private int anonymousFunctionalityEnabled = 1;

		@SerializedName("handle_name")
		private String handleName;

		@SerializedName("feed_name")
		@Expose
		private String feedName;



        @SerializedName("has_handle")
        private int hasHandle;

		@SerializedName("show_promo_box")
		private int showPromoBox;

		@SerializedName("count_notification_polling_interval")
		@Expose
		private Integer countNotificationPollingInterval;

		@SerializedName("bottom_strip")
		private Meals.OfferStripMeals bottomStrip;

		@SerializedName("how_it_works")
		private ArrayList<FatafatTutorialData> fatafatTutorialData;

		@SerializedName("upload_image_info")
		private FatafatUploadImageInfo fatafatUploadImageInfo;




		public FatafatUploadImageInfo getFatafatUploadImageInfo() {
			return fatafatUploadImageInfo;
		}

		public ArrayList<FatafatTutorialData> getFatafatTutorialData() {
			return fatafatTutorialData;
		}

		public Meals.OfferStripMeals getBottomStrip() {
			return bottomStrip;
		}

		public String getFeedIntroString() {
			return feedIntroString;
		}

		public long getUsersCount() {
			return usersCount;
		}

		public boolean getFeedActive() {
			return feedActive==1;
		}

		public Long getFeedRank() {
			return feedRank;
		}

		public String getFeedAddReviewHint() {
			return feedAddReviewHint;
		}

		public String getFeedAddPostHInt() {
			return feedAddPostHInt;
		}

		public Integer getMaxUploadImagesFeed() {
			return maxUploadImagesFeed;
		}

		public Integer getContactsSynced() {
			return contactsSynced;
		}

		public void setContactsSynced(Integer contactsSynced) {
			this.contactsSynced = contactsSynced;
		}

		public void setFeedRank(Long feedRank) {
			this.feedRank = feedRank;
		}

		public void incrementUserCount(){
			usersCount++;
		}

		public String getEarlyAccessText() {
			return earlyAccessText;
		}

		public void setEarlyAccessText(String earlyAccessText) {
			this.earlyAccessText = earlyAccessText;
		}

		public String getEarlyAccessShareTitle() {
			return earlyAccessShareTitle;
		}

		public void setEarlyAccessShareTitle(String earlyAccessShareTitle) {
			this.earlyAccessShareTitle = earlyAccessShareTitle;
		}

		public String getEarlyAccessShareDesc() {
			return earlyAccessShareDesc;
		}

		public void setEarlyAccessShareDesc(String earlyAccessShareDesc) {
			this.earlyAccessShareDesc = earlyAccessShareDesc;
		}

		public String getEarlyAccessShareImage() {
			return earlyAccessShareImage;
		}

		public void setEarlyAccessShareImage(String earlyAccessShareImage) {
			this.earlyAccessShareImage = earlyAccessShareImage;
		}

		public boolean showCreateHandle() {
			return showCreateHandle == 1;
		}

		public void setShowCreateHandle(int showCreateHandle) {
			this.showCreateHandle = showCreateHandle;
		}

		public int getAnonymousFunctionalityEnabled() {
			return anonymousFunctionalityEnabled;
		}

		public void setAnonymousFunctionalityEnabled(int anonymousFunctionalityEnabled) {
			this.anonymousFunctionalityEnabled = anonymousFunctionalityEnabled;
		}

		public String getHandleName() {
			return handleName;
		}

		public void setHandleName(String handleName) {
			this.handleName = handleName;
		}

		public String getFeedName() {
			if(feedName == null){
				feedName = "AskLocal";
			}
			return feedName;
		}

		public void setFeedName(String feedName) {
			this.feedName = feedName;
		}

        public boolean getHasHandle() {
            return hasHandle==1;
        }

        public void setHasHandle(int hasHandle) {
            this.hasHandle = hasHandle;
        }

		public int getCountNotificationPollingInterval() {
			if(countNotificationPollingInterval == null){
				countNotificationPollingInterval = 15;
			}
			return countNotificationPollingInterval*1000;
		}

		public void setCountNotificationPollingInterval(int countNotificationPollingInterval) {
			this.countNotificationPollingInterval = countNotificationPollingInterval;
		}

		public boolean showPromoBox() {
			return showPromoBox==1;
		}
    }

	public class Pay{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();
		@SerializedName("mid")
		@Expose
		private String mid;
		@SerializedName("mkey")
		@Expose
		private String mkey;
		@SerializedName("token")
		@Expose
		private String token;
		@SerializedName("faq_link")
		@Expose
		private String faqLink;
		@SerializedName("support_link")
		@Expose
		private String supportLink;
		@SerializedName("about_us")
		@Expose
		private String aboutUs;
		@SerializedName("has_vpa")
		@Expose
		private Integer hasVpa;

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}

		public String getMid() {
			return mid;
		}

		public void setMid(String mid) {
			this.mid = mid;
		}

		public String getMkey() {
			return mkey;
		}

		public void setMkey(String mkey) {
			this.mkey = mkey;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getFaqLink() {
			return faqLink;
		}

		public void setFaqLink(String faqLink) {
			this.faqLink = faqLink;
		}

		public String getSupportLink() {
			return supportLink;
		}

		public void setSupportLink(String supportLink) {
			this.supportLink = supportLink;
		}

		public String getAboutUs() {
			return aboutUs;
		}

		public void setAboutUs(String aboutUs) {
			this.aboutUs = aboutUs;
		}

		public Integer getHasVpa() {
			if(hasVpa == null){
				return 0;
			}
			return hasVpa;
		}

		public void setHasVpa(Integer hasVpa) {
			this.hasVpa = hasVpa;
		}
	}

	public class Autos{
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();
		@SerializedName("drivers")
		@Expose
		private List<Driver> drivers = new ArrayList<>();
		@SerializedName("regions")
		@Expose
		private List<Region> regions = new ArrayList<>();

		@SerializedName("priority_tip_category")
		@Expose
		private Integer priorityTipCategory;
		@SerializedName("fare_factor")
		@Expose
		private Double fareFactor;
		@SerializedName("driver_fare_factor")
		@Expose
		private Double driverFareFactor;
		@SerializedName("far_away_city")
		@Expose
		private String farAwayCity;
		@SerializedName("campaigns")
		@Expose
		private Campaigns campaigns;
		@SerializedName("fare_structure")
		@Expose
		private List<FareStructure> fareStructure = new ArrayList<>();
		@SerializedName("current_user_status")
		@Expose
		private Integer currentUserStatus;
		@SerializedName("cancellation")
		@Expose
		private Cancellation cancellation;
		@SerializedName("bad_rating_reasons")
		@Expose
		private List<String> badRatingReasons = new ArrayList<>();
		@SerializedName("nearbyPickupRegions")
		@Expose
		private NearbyPickupRegions nearbyPickupRegions;
		@SerializedName("use_recent_loc_at_request")
		@Expose
		private Integer useRecentLocAtRequest;
		@SerializedName("use_recent_loc_auto_snap_min_distance")
		@Expose
		private Double useRecentLocAutoSnapMinDistance;
		@SerializedName("use_recent_loc_auto_snap_max_distance")
		@Expose
		private Double useRecentLocAutoSnapMaxDistance;
		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("points_of_interest")
		@Expose
		private List<FetchUserAddressResponse.Address> pointsOfInterestAddresses = new ArrayList<FetchUserAddressResponse.Address>();
		@SerializedName("referral_popup_content")
		@Expose
		private PlaceOrderResponse.ReferralPopupContent referralPopupContent;
		@SerializedName("currency")
		@Expose
		private String currency;
		@SerializedName("distance_unit")
		@Expose
		private String distanceUnit;
		@SerializedName("show_region_specific_fare")
		@Expose
		private int showRegionSpecificFare;
		@SerializedName("services")
		@Expose
		private ArrayList<ServiceType> serviceTypes;
		@SerializedName("fault_conditions")
		@Expose
		private List<String> faultConditions;

		public List<Driver> getDrivers() {
			return drivers;
		}

		public void setDrivers(List<Driver> drivers) {
			this.drivers = drivers;
		}

		public List<Region> getRegions() {
			return regions;
		}

		public void setRegions(List<Region> regions) {
			this.regions = regions;
		}

		public Integer getPriorityTipCategory() {
			return priorityTipCategory;
		}

		public void setPriorityTipCategory(Integer priorityTipCategory) {
			this.priorityTipCategory = priorityTipCategory;
		}

		public Double getFareFactor() {
			return fareFactor;
		}

		public void setFareFactor(Double fareFactor) {
			this.fareFactor = fareFactor;
		}

		public Double getDriverFareFactor() {
			return driverFareFactor;
		}

		public void setDriverFareFactor(Double driverFareFactor) {
			this.driverFareFactor = driverFareFactor;
		}

		public String getFarAwayCity() {
			return farAwayCity;
		}

		public void setFarAwayCity(String farAwayCity) {
			this.farAwayCity = farAwayCity;
		}

		public Campaigns getCampaigns() {
			return campaigns;
		}

		public void setCampaigns(Campaigns campaigns) {
			this.campaigns = campaigns;
		}

		public List<FareStructure> getFareStructure() {
			return fareStructure;
		}

		public void setFareStructure(List<FareStructure> fareStructure) {
			this.fareStructure = fareStructure;
		}

		public Integer getCurrentUserStatus() {
			return currentUserStatus;
		}

		public void setCurrentUserStatus(Integer currentUserStatus) {
			this.currentUserStatus = currentUserStatus;
		}

		public Cancellation getCancellation() {
			return cancellation;
		}

		public void setCancellation(Cancellation cancellation) {
			this.cancellation = cancellation;
		}

		public List<String> getBadRatingReasons() {
			return badRatingReasons;
		}

		public void setBadRatingReasons(List<String> badRatingReasons) {
			this.badRatingReasons = badRatingReasons;
		}

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}

		public NearbyPickupRegions getNearbyPickupRegions() {
			return nearbyPickupRegions;
		}

		public void setNearbyPickupRegions(NearbyPickupRegions nearbyPickupRegions) {
			this.nearbyPickupRegions = nearbyPickupRegions;
		}

		public Integer getUseRecentLocAtRequest() {
			if(useRecentLocAtRequest == null){
				return 0;
			}
			return useRecentLocAtRequest;
		}

		public void setUseRecentLocAtRequest(Integer useRecentLocAtRequest) {
			this.useRecentLocAtRequest = useRecentLocAtRequest;
		}

		public Double getUseRecentLocAutoSnapMinDistance() {
			return useRecentLocAutoSnapMinDistance;
		}

		public void setUseRecentLocAutoSnapMinDistance(Double useRecentLocAutoSnapMinDistance) {
			this.useRecentLocAutoSnapMinDistance = useRecentLocAutoSnapMinDistance;
		}

		public Double getUseRecentLocAutoSnapMaxDistance() {
			return useRecentLocAutoSnapMaxDistance;
		}

		public void setUseRecentLocAutoSnapMaxDistance(Double useRecentLocAutoSnapMaxDistance) {
			this.useRecentLocAutoSnapMaxDistance = useRecentLocAutoSnapMaxDistance;
		}

		public Integer getCityId() {
			return cityId;
		}

		public void setCityId(Integer cityId) {
			this.cityId = cityId;
		}

		public List<FetchUserAddressResponse.Address> getPointsOfInterestAddresses() {
			return pointsOfInterestAddresses;
		}

		public void setPointsOfInterestAddresses(List<FetchUserAddressResponse.Address> pointsOfInterestAddresses) {
			this.pointsOfInterestAddresses = pointsOfInterestAddresses;
		}

		public PlaceOrderResponse.ReferralPopupContent getReferralPopupContent() {
			return referralPopupContent;
		}

		public void setReferralPopupContent(PlaceOrderResponse.ReferralPopupContent referralPopupContent) {
			this.referralPopupContent = referralPopupContent;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getDistanceUnit() {
			return distanceUnit;
		}

		public void setDistanceUnit(String distanceUnit) {
			this.distanceUnit = distanceUnit;
		}

		public int getShowRegionSpecificFare() {
			return showRegionSpecificFare;
		}

		public ArrayList<ServiceType> getServiceTypes() {
			return serviceTypes;
		}

		public void setServiceTypes(ArrayList<ServiceType> serviceTypes) {
			this.serviceTypes = serviceTypes;
		}

		public List<String> getFaultConditions() {
			return faultConditions;
		}

		public void setFaultConditions(List<String> faultConditions) {
			this.faultConditions = faultConditions;
		}
	}

	public class UserData{
		@SerializedName("menu")
		@Expose
		private List<MenuInfo> menuInfoList;
		@SerializedName("promotions")
		@Expose
		private List<PromotionInfo> promotions = new ArrayList<>();
		@SerializedName("coupons")
		@Expose
		private List<CouponInfo> coupons = new ArrayList<>();
		@SerializedName("city_id")
		@Expose
		private Integer cityId;
		@SerializedName("support_number")
		@Expose
		private String supportNumber;
		@SerializedName("meals_enabled")
		@Expose
		private Integer mealsEnabled;
		@SerializedName("fresh_enabled")
		@Expose
		private Integer freshEnabled;
		@SerializedName("delivery_enabled")
		@Expose
		private Integer deliveryEnabled;
		@SerializedName("pros_enabled")
		@Expose
		private Integer prosEnabled;
		@SerializedName("referral_message")
		@Expose
		private String referralMessage;
		@SerializedName("fb_share_caption")
		@Expose
		private String fbShareCaption;
		@SerializedName("fb_share_description")
		@Expose
		private String fbShareDescription;
		@SerializedName("referral_caption")
		@Expose
		private String referralCaption;
		@SerializedName("referral_email_subject")
		@Expose
		private String referralEmailSubject;
		@SerializedName("referral_popup_text")
		@Expose
		private String referralPopupText;
		@SerializedName("invite_earn_short_msg")
		@Expose
		private String inviteEarnShortMsg;
		@SerializedName("invite_earn_more_info")
		@Expose
		private String inviteEarnMoreInfo;
		@SerializedName("referral_sharing_message")
		@Expose
		private String referralSharingMessage;
		@SerializedName("sharing_og_title")
		@Expose
		private String sharingOgTitle;
		@SerializedName("jeanie_intro_dialog_content")
		@Expose
		private JeanieIntroDialogContent jeanieIntroDialogContent;

		@SerializedName("subscription_data")
		@Expose
		private SubscriptionData subscriptionData;
		@SerializedName("signup_tutorial")
		@Expose
		private SignupTutorial signupTutorial;

		@SerializedName("payment_gateway_mode_config_data")
		@Expose
		private List<PaymentGatewayModeConfig> paymentGatewayModeConfigData;

		@SerializedName("expand_jeanie")
		@Expose
		private int expandJeanie;
		@SerializedName("expanded_genie_text")
		@Expose
		private String expandedGenieText;

		@SerializedName("show_home")
		@Expose
		private int showHome;


		@SerializedName("show_jstar_in_account")
		@Expose
		private int showJugnooStarInAccount;

		public List<MenuInfo> getMenuInfoList() {
			return menuInfoList;
		}

		public void setMenuInfoList(List<MenuInfo> menuInfoList) {
			this.menuInfoList = menuInfoList;
		}

		public List<PromotionInfo> getPromotions() {
			return promotions;
		}

		public void setPromotions(List<PromotionInfo> promotions) {
			this.promotions = promotions;
		}

		public List<CouponInfo> getCoupons() {
			return coupons;
		}

		public void setCoupons(List<CouponInfo> coupons) {
			this.coupons = coupons;
		}

		public Integer getCityId() {
			return cityId;
		}

		public void setCityId(Integer cityId) {
			this.cityId = cityId;
		}

		public String getSupportNumber() {
			return supportNumber;
		}

		public void setSupportNumber(String supportNumber) {
			this.supportNumber = supportNumber;
		}

		public Integer getMealsEnabled() {
			return mealsEnabled;
		}

		public void setMealsEnabled(Integer mealsEnabled) {
			this.mealsEnabled = mealsEnabled;
		}

		public Integer getFreshEnabled() {
			return freshEnabled;
		}

		public void setFreshEnabled(Integer freshEnabled) {
			this.freshEnabled = freshEnabled;
		}

		public Integer getDeliveryEnabled() {
			return deliveryEnabled;
		}

		public void setDeliveryEnabled(Integer deliveryEnabled) {
			this.deliveryEnabled = deliveryEnabled;
		}

		public String getReferralMessage() {
			return referralMessage;
		}

		public void setReferralMessage(String referralMessage) {
			this.referralMessage = referralMessage;
		}

		public String getFbShareCaption() {
			return fbShareCaption;
		}

		public void setFbShareCaption(String fbShareCaption) {
			this.fbShareCaption = fbShareCaption;
		}

		public String getFbShareDescription() {
			return fbShareDescription;
		}

		public void setFbShareDescription(String fbShareDescription) {
			this.fbShareDescription = fbShareDescription;
		}

		public String getReferralCaption() {
			return referralCaption;
		}

		public void setReferralCaption(String referralCaption) {
			this.referralCaption = referralCaption;
		}

		public String getReferralEmailSubject() {
			return referralEmailSubject;
		}

		public void setReferralEmailSubject(String referralEmailSubject) {
			this.referralEmailSubject = referralEmailSubject;
		}

		public String getReferralPopupText() {
			return referralPopupText;
		}

		public void setReferralPopupText(String referralPopupText) {
			this.referralPopupText = referralPopupText;
		}

		public String getInviteEarnShortMsg() {
			return inviteEarnShortMsg;
		}

		public void setInviteEarnShortMsg(String inviteEarnShortMsg) {
			this.inviteEarnShortMsg = inviteEarnShortMsg;
		}

		public String getInviteEarnMoreInfo() {
			return inviteEarnMoreInfo;
		}

		public void setInviteEarnMoreInfo(String inviteEarnMoreInfo) {
			this.inviteEarnMoreInfo = inviteEarnMoreInfo;
		}

		public String getReferralSharingMessage() {
			return referralSharingMessage;
		}

		public void setReferralSharingMessage(String referralSharingMessage) {
			this.referralSharingMessage = referralSharingMessage;
		}

		public JeanieIntroDialogContent getJeanieIntroDialogContent() {
			return jeanieIntroDialogContent;
		}

		public void setJeanieIntroDialogContent(JeanieIntroDialogContent jeanieIntroDialogContent) {
			this.jeanieIntroDialogContent = jeanieIntroDialogContent;
		}

		public String getSharingOgTitle() {
			return sharingOgTitle;
		}

		public void setSharingOgTitle(String sharingOgTitle) {
			this.sharingOgTitle = sharingOgTitle;
		}

		public SubscriptionData getSubscriptionData() {
			return subscriptionData;
		}

		public void setSubscriptionData(SubscriptionData subscriptionData) {
			this.subscriptionData = subscriptionData;
		}

		public SignupTutorial getSignUpTutorial() {
			return signupTutorial;
		}

		public void setSignUpTutorial(SignupTutorial signUpTutorial) {
			this.signupTutorial = signUpTutorial;
		}

		public List<PaymentGatewayModeConfig> getPaymentGatewayModeConfigData() {
			return paymentGatewayModeConfigData;
		}

		public void setPaymentGatewayModeConfigData(List<PaymentGatewayModeConfig> paymentGatewayModeConfigData) {
			this.paymentGatewayModeConfigData = paymentGatewayModeConfigData;
		}

		public int getExpandJeanie() {
			return expandJeanie;
		}

		public void setExpandJeanie(int expandJeanie) {
			this.expandJeanie = expandJeanie;
		}

		public String getExpandedGenieText() {
			return expandedGenieText;
		}

		public void setExpandedGenieText(String expandedGenieText) {
			this.expandedGenieText = expandedGenieText;
		}
		public int getShowJugnooStarInAccount() {
			return showJugnooStarInAccount;
		}

		public Integer getProsEnabled() {
			return prosEnabled;
		}

		public void setProsEnabled(Integer prosEnabled) {
			this.prosEnabled = prosEnabled;
		}

		public int getShowHome() {
			return showHome;
		}

		public void setShowHome(int showHome) {
			this.showHome = showHome;
		}
	}

	public class Pros{

		@SerializedName("job_id")
		@Expose
		private int jobId;

		public int getJobId() {
			return jobId;
		}

		public void setJobId(int jobId) {
			this.jobId = jobId;
		}
	}


	public class FeedbackData {


		@SerializedName(Constants.KEY_FEEDBACK_ORDER_ID)
		private String orderId = "";

		@SerializedName(Constants.KEY_RESTAURANT_NAME)
		private String restaurantName = "";

		@SerializedName(Constants.QUESTION)
		private String question = "";

		@SerializedName(Constants.KEY_QUESTION_TYPE)
		private int questionType;

		@SerializedName(Constants.KEY_PENDING_FEEDBACK)
		private int pendingFeedback;

		@SerializedName(Constants.KEY_FEEDBACK_AMOUNT)
		private double amount;

		@SerializedName(Constants.KEY_FEEDBACK_DATE)
		private String feedbackDeliveryDate = "";

		@SerializedName(Constants.KEY_FEEDBACK_VIEW_TYPE)
		private Integer feedbackViewType;

		@SerializedName(Constants.KEY_RIDE_END_GOOD_FEEDBACK_TEXT)
		private String rideEndGoodFeedbackText;
		@SerializedName(Constants.KEY_FEEDBACK_ORDER_CURRENCY_CODE)
		private String feedbackCurrencyCode;
		@SerializedName(Constants.KEY_FEEDBACK_ORDER_CURRENCY)
		private String feedbackCurrency;


		@SerializedName(Constants.KEY_NEGATIVE_FEEDBACK_REASONS)
		ArrayList<String> negativeFeedbackReasons;

		@SerializedName(Constants.KEY_POSITIVE_FEEDBACK_REASONS)
		ArrayList<String> positiveFeedbackReasons;

		public Integer getFeedbackViewType() {
			return feedbackViewType==null? RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal():feedbackViewType;
		}

		public String getRideEndGoodFeedbackText(Context context) {
			return rideEndGoodFeedbackText==null? context.getResources().getString(R.string.end_ride_with_image_text, context.getString(R.string.app_name)):rideEndGoodFeedbackText;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getRestaurantName() {
			return restaurantName;
		}

		public String getQuestion() {
			return question;
		}

		public int getQuestionType() {
			return questionType;
		}

		public int getPendingFeedback() {
			return pendingFeedback;
		}

		public double getAmount() {
			return amount;
		}

		public String getFeedbackDeliveryDate() {
			return feedbackDeliveryDate;
		}

		public String getRideEndGoodFeedbackText() {
			return rideEndGoodFeedbackText;
		}

		public ArrayList<String> getNegativeFeedbackReasons() {
			return negativeFeedbackReasons;
		}

		public ArrayList<String> getPositiveFeedbackReasons() {
			return positiveFeedbackReasons;
		}

		public void setPendingFeedback(int pendingFeedback) {
			this.pendingFeedback = pendingFeedback;
		}

		public void setFeedbackViewType(Integer feedbackViewType) {
			this.feedbackViewType = feedbackViewType;
		}

		public String getFeedbackCurrencyCode() {
			return feedbackCurrencyCode;
		}

		public String getFeedbackCurrency() {
			return feedbackCurrency;
		}
	}

}
