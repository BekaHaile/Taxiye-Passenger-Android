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
    String TRAIN_TIME = "train_time";

    String MAX_COUPON_VALUE = "max_coupon_value";
    String TIMING = "timing";

}
