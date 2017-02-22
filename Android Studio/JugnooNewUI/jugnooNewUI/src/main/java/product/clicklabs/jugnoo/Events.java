package product.clicklabs.jugnoo;

/**
 * Created by gurmail on 02/09/16.
 */
public interface Events {

    String SIGNED_UP = "Signed Up";
    String RIDE_REQUESTED = "Ride Requested";
    String RIDE_CONFIRMED = "Ride Confirmed";
    String RIDE_COMPLETED = "Ride Completed";

    String MEALS_ADDED_TO_CART = "Meals - Added to Cart";
    String MEALS_CHARGED = "Meals - Charged";
    String FRESH_ADDED_TO_CART = "Fresh - Added to Cart";
    String FRESH_CHARGED = "Fresh - Charged";
    String GROCERY_ADDED_TO_CART = "Grocery - Added to Cart";
    String MENUS_ADDED_TO_CART = "Menus - Added to Cart";

    String PROMO_CODE = "Promo Code";
    String CHANNEL = "Channel";
    String WALLET = "Wallet";

    String VEHICLE_TYPE = "vehicle type";
    String OFFER_CODE = "Offer Code";
    String ETA = "nearest driver ETA";
    String SURGE = "Surge";

    String PRODUCT_NAME = "Product Name";
    String PRODUCT_ID = "Product ID";
    String TOTAL_AMOUNT = "Total Amount";

    String QUANTITY = "Quantity";
    String DISCOUNT_AMOUNT = "Discount Amount";
    String TYPE = "Type";
    String START_TIME = "Start Time";
    String END_TIME = "End Time";
    String CHARGED_ID = "Charged ID";
    String PROMOCODE = "Promocode";


    // for users
    String USER_ID = "Identity";
    String NAME = "Name";
    String EMAIL = "Email";
    String PHONE = "Phone";
    String SOURCE = "Source";
    String OS_VERSION = "os_version";
    String REFERRAL_CODE = "User's Referral Code";
    String COUPONS = "coupons";
    String JUGNOO_CASH = "Jugnoo Cash";
    String COUPONS_USED = "coupons_used";
    String CAN_REQUEST = "can_request";
    String IS_VERIFIED = "is_verified";
    String HOME = "Home";
    String WORK = "Work";
    String IS_PUSH_MEALS_ENABLED = "is_push_meals_enabled";
    String IS_PUSH_AUTOS_ENABLED = "is_push_autos_enabled";
    String IS_PUSH_FRESH_ENABLED = "is_push_fresh_enabled";
    String IS_PUSH_GROCERY_ENABLED = "is_push_grocery_enabled";
    String IS_PUSH_MENUS_ENABLED = "is_push_menus_enabled";
    String IS_PUSH_PAY_ENABLED = "is_push_pay_enabled";

    String TRAIN_TIME = "train_time";

    String MAX_COUPON_VALUE = "max_coupon_value";
    String TIMING = "timing";
    String CITY = "city";
    String REGISTERED_CITY = "Registered city";
    String PROMO_CODE_USED = "PromoCodeUsed";


    /**
     Edited by Parminder Singh on 2/15/17 at 4:00 PM
     **/

    String MENUS = "Menus";
    String SECOND_RESTAURANT_ORDER = "Second Restaurant Order";
    String POPUP_ORDER = "Popup Order";
    String FEEDBACK = "Feedback";
    String COMMENT_ADDED = "Comment Added";
    java.lang.String RATING = "Rating";
    String REVIEW = "Review";
    String PLUS_BUTTON = "Plus Button";
    String SUBMITTED = "MenuFeedSubmitted";
    String CLICKED = "Clicked";
    String SEARCH = "Search";
    String NOT_SEARCHED = "Not Searched";
    String SEARCH_MATCHED = "Click on Search button on home screen of menus";
    String ADD_RESTRO = "Add Restro";
    String INFORMATION = "Information";
    String CLAIM_GIFT = "Claim Gift";
    String FRESH_SCREEN = "Fresh Screen";
    String MEALS_SCREEN = "Meals Screen";
    String MENUS_SCREEN = "Menus Screen";
    String SIDE_MENU_CATEGORIES = "Side Menu Categories";
    String RIDES = "Rides";
    String FRESH = "Fresh";
    String MEALS = "Meals";
    String GROCERY = "Grocery";
    String PAY = "Pay";
    String GENIE = "Genie";
    String SUBMIT_FEED = "Submit Feed";
    String EDIT_FEED = "Edit Feed";
    String FEED_EDITED = "MenuFeedEdited";
    String ADD_TEXT = "Add Text";
    String JUGNOO_ADD_TEXT = "JugnoAddText";
    String ADDS_IMAGES = "Adds Images";
    String JUGNOO_ADD_IMAGES = "JugnooAddImage";
    String USER_CLICK_ON_CAMERA = "User click on camera button";
    String JUGNOO_OPEN_CAMERA = "JugnooOpenCamera";
    String FILTERS = "Filters";
    String MENU_FILTERS = "Menu Filters";
    String MENU_APPLY_FILTER = "MenuApplyFilter";
    String APPLY_FILTERS = "Apply Filters";
    String CHANGE_ADDRESS = "Change Address";
    String MENU_CHANGE_ADDRESS = "MenuChangeAddress";
    String SELECT_RESTAURANT = "Select Restaurant";
    String MENU_SELECT_RESTAURANT = "MenuSelectRestaurant";
    String CLICK_ON_PLUS_BUTTON = "Click on + Button";
    String MENU_ADD_FEED = "MenuAddFeed";
    String CLICK_ON_RATING_BUTTON_RESTRO = "Click on Rating Button on the Restro Image";
    String MENU_RESTRO_RATING_CLICK = "MenuRestroRatingClick";
    String CLICK_SEARCH_BUTTON_MENUS = "Click on Search button on home screen of menus";
    String MENU_SEARCH = "MenuSearch";
    String CLICK_ADD_BUTTON_ITEM = "Click on add button of item";
    String MENU_ADD_ITEM = "MenuAddItem";
    String CLICK_PAY_BUTTON = "Cick on pay button";
    String MENU_BILL_PAY = "MenuBillPay";
    String CLICK_CART_BUTTON = "Click on cart button";
    String MENU_CART_VIEW = "MenuCartView";
    String CART_ITEM_EDIT = "Cart Item Edit";
    String MENU_CART_EDIT = "MenuCartEdit";
    String MENU_SEARCH_MATCH = "MenuSearchMatch";
    String FEEDBACK_COMMENTS = "Feedback Page: Comments";
    String MENU_FEEDBACK_COMMENTS = "MenuFeedbackComments";
    String FEEDBACK_TAGS = "Feedback page: Tags";
    String MENU_FEEDBACK_TAGS = "MenuFeedbackTags- ";
    String FEEDBACK_STAR = "Feedback page: Star Icon";
    String STAR_ICON = "MenuStarIcon";
    String MENU_FEEDBACK_SUBMIT = "MenuFeedbackSubmit";
    String FEEDBACK_SUBMIT = "Feedback page: Submit button";
}
