package product.clicklabs.jugnoo;

/**
 * Created by socomo20 on 12/11/15.
 */
public interface Constants {

	String KEY_FLAG = "flag";
	String KEY_MESSAGE = "message";
	String KEY_TITLE = "title";
	String KEY_ERROR = "error";

	String KEY_ACCESS_TOKEN = "access_token";
	String KEY_ENGAGEMENT_ID = "engagement_id";
	String KEY_EMAIL = "email";
	String KEY_USER_NAME = "user_name";
	String KEY_USER_ID = "user_id";
	String KEY_DRIVER_ID = "driver_id";
	String KEY_PHONE_NO = "phone_no";
	String KEY_PUSH_CALL_DRIVER = "push_call_driver";
	String KEY_DRIVER_CAR_NO = "driver_car_no";
	String KEY_END_RIDE_DATA = "end_ride_data";
	String KEY_APP_MONITORING_TIME_TO_SAVE = "app_monitoring_time";
	String KEY_GOOGLE_NAME = "google_name";
	String KEY_GOOGLE_EMAIL = "google_email";
	String KEY_GOOGLE_ID = "google_id";
	String KEY_GOOGLE_IMAGE = "google_image";
	String KEY_GOOGLE_PARCEL = "google_parcel";
	String KEY_ETA = "eta";
	int ADD_HOME = 2, ADD_WORK = 3;
	String KEY_LATITUDE = "latitude";
	String KEY_LONGITUDE = "longitude";

	String KEY_ALERT_TYPE = "alert_type";
	String KEY_ID = "id";

	String KEY_SEARCH_FIELD_TEXT = "search_field_text";
	String KEY_SEARCH_FIELD_HINT = "search_field_hint";
	String KEY_SEARCH_MODE = "search_mode";

	String KEY_OTP_VIA_CALL_ENABLED = "otp_via_call_enabled";

	String KEY_PROMO_SUCCESS = "promo_success";
	String KEY_PROMO_MESSAGE = "promo_message";

	String ACTION_LOADING_COMPLETE = "product.clicklabs.jugnoo.ACTION_LOADING_COMPLETE";

	String KEY_SPLASH_STATE = "splash_state";
	String KEY_FORGOT_LOGIN_EMAIL = "forgot_login_email";
	String KEY_PREVIOUS_LOGIN_EMAIL = "previous_login_email";
	String KEY_BACK_FROM_OTP = "back_from_otp";
	String KEY_ADD_PAYMENT_PATH = "addPaymentPath";
	String KEY_PAYMENT_RECHARGE_VALUE = "payment_recharge_value";

	String POST_DATA = "post_data";


	String KEY_USER_DEBT = "user_debt";
	String KEY_CLIENT_ID = "client_id";
	String KEY_IS_ACCESS_TOKEN_NEW = "is_access_token_new";
	String KEY_IP_ADDRESS= "ip_addr";


	String KEY_OTP = "otp";

	String KEY_BRANCH_REFERRING_USER_IDENTIFIER = "referring_user_identifier";
	String KEY_DEEPINDEX = "deepindex";
	String KEY_REFERRAL_CODE = "referral_code";
	String KEY_REFERRAL_CODE_ENTERED = "referral_code_entered";
	String KEY_BRANCH_REFERRING_USER_NAME = "referring_user_name";
	String FB_LINK_SHARE_NAME = "Jugnoo";
//	String BRANCH_END_LINK = "https://get.jugnoo.in/#/register";


	String DOMAIN_SHARE_JUGNOO_IN = "share.jugnoo.in";

	String KEY_BRANCH_DESKTOP_URL = "branch_desktop_url";
	String KEY_BRANCH_ANDROID_URL = "branch_android_url";
	String KEY_BRANCH_IOS_URL = "branch_ios_url";
	String KEY_BRANCH_FALLBACK_URL = "branch_fallback_url";

	String KEY_JUGNOO_CASH_TNC = "jugnoo_cash_tnc";

	String KEY_ALREADY_VERIFIED_EMAIL = "already_verified_email";
	String KEY_ALREADY_REGISTERED_EMAIL = "already_registered_email";
	String KEY_TUTORIAL_NO_OF_PAGES = "NumOfPages";

	String KEY_SAVE_NOTIFICATION = "save_notification";
	String KEY_NOTIFICATION_ID = "notification_id";

	String LINKED_WALLET = "linked_wallet";
	String LINKED_WALLET_MESSAGE = "linked_wallet_message";

	String SERVER_TIMEOUT = "SERVER_TIMEOUT";

	String SP_ANALYTICS_LAST_MESSAGE_READ_TIME = "sp_analytics_last_message_read_time";


	String FIRST_TIME_DB= "first_time_db";
	String SECOND_TIME_DB= "second_time_db";


	String KEY_SHARE_ACTIVITY_FROM_DEEP_LINK = "share_activity_from_deep_link";

	String SP_USER_ID = "sp_user_id";



	String SP_EMERGENCY_MODE_ENABLED = "sp_emergency_mode_enabled";

	String KEY_SUPPORT_FEEDBACK_TEXT = "support_feedback_text";
	String KEY_SUPPORT_ID = "support_id";
	String KEY_SUPPORT_ISSUE_TITLE = "issue_title";

	String KEY_RIDE_DATE = "ride_date";

	String KEY_SP_IN_APP_SUPPORT_PANEL_VERSION = "in_app_support_panel_version";
	String SP_IN_APP_RIDE_SUPPORT_PANEL_VERSION = "in_app_ride_support_panel_version";
	String KEY_SHOW_RIDE_MENU = "show_ride_menu";


	String KEY_GET_GOGU = "get_gogu";
	String KEY_INVITE_EARN_SCREEN_IMAGE_ANDROID = "invite_earn_screen_image_android";

	String KEY_EMERGENCY_ACTIVITY_MODE = "emergency_activity_mode";
	String KEY_EMERGENCY_CONTACTS = "emergency_contacts";
	String KEY_NAME = "name";
	String KEY_TRIP_TOTAL = "trip_total";


	String KEY_T20_WC_ENABLE = "t20_wc_enable";
	String KEY_SP_T20_WC_SCHEDULE_VERSION = "t20_wc_schedule_version";
	String KEY_T20_SCHEDULE = "t20_schedule";
	String KEY_SCHEDULE_ID = "schedule_id";
	String KEY_TEAM_ID = "team_id";
	String KEY_TEAM_1 = "team_1";
	String KEY_TEAM_2 = "team_2";
	String KEY_MATCH_TIME = "match_time";
	String KEY_TEAM_NAME = "name";
	String KEY_TEAM_SHORT_NAME = "short_name";
	String KEY_TEAM_FLAG_IMAGE_URL = "flag_image_url";
	String KEY_T20_WC_INFO_TEXT = "t20_wc_info_text";
	String SP_T20_DIALOG_BEFORE_START_CROSSED = "sp_t20_dialog_before_start_crossed";
	String SP_T20_DIALOG_IN_RIDE_CROSSED = "sp_t20_dialog_in_ride_crossed";
	String KEY_PUBLIC_ACCESS_TOKEN = "public_access_token";

	String KEY_GAME_PREDICT_ENABLE = "game_predict_enable";
	String KEY_GAME_PREDICT_URL = "game_predict_url";
	String KEY_GAME_PREDICT_VIEW_DATA = "game_predict_view_data";
	String VIEW_DATA_SPLITTER = ";;;";


	String KEY_PAYTM_TRANSFER_DATA = "paytm_transfer_data";
	String KEY_TRANSFER_ID = "transfer_id";
	String KEY_TRANSFER_AMOUNT = "transfer_amount";
	String KEY_TRANSFER_SENDER_NAME = "transfer_sender_name";
	String KEY_TRANSFER_PHONE = "transfer_phone";
	String KEY_USER_AMOUNT = "user_amount";

	String KEY_CUSTOMER_FARE_FACTOR = "customer_fare_factor";

	String SP_LAST_DEVICE_TOKEN_REFRESH_TIME = "last_device_token_refresh_time";
	String KEY_SP_DEVICE_TOKEN_REFRESH_INTERVAL = "device_token_refresh_interval";

	String KEY_START_TIME = "start_time";
	String KEY_END_TIME = "end_time";

	String KEY_SP_CUSTOMER_LOCATION_UPDATE_INTERVAL = "customer_location_update_interval";
	String SP_CURRENT_STATE = "current_state";
	String ACTION_LOCATION_UPDATE = "jugnoo.ACTION_LOCATION_UPDATE";
	String KEY_ONE_SHOT = "one_shot";
	String KEY_EMERGENCY_LOC = "emergency_loc";
	String SP_CURRENT_ENGAGEMENT_ID = "current_engagement_id";

	String KEY_URL = "url";
	String SP_LAST_PUSH_RECEIVED_TIME = "last_push_received_time";
	String KEY_LAST_PUSH_TIME_DIFF = "last_push_time_diff";
	String KEY_PUSH_CLICKED = "push_clicked";



	long SECOND_MILLIS = 1000;
	long MINUTE_MILLIS = 60 * SECOND_MILLIS;
	long HOUR_MILLIS = 60 * MINUTE_MILLIS;
	long DAY_MILLIS = 24 * HOUR_MILLIS;

	long DEFAULT_DEVICE_TOKEN_REFRESH_INTERVAL = DAY_MILLIS;
	long LOCATION_UPDATE_INTERVAL = MINUTE_MILLIS;


	String SP_FIRST_OPEN_TIME = "sp_first_open_time";
	String SP_FIRST_LOGIN_COMPLETE = "sp_first_login_complete";
	String KEY_TIME_DIFF_SEC = "time_diff_sec";

	String SP_APP_DOWNLOAD_SOURCE_SENT = "sp_app_download_source_sent";
	String KEY_SOURCE = "source";
	String KEY_DOWNLOAD_SOURCE = "download_source";

	String KEY_PLAY_SOUND = "play_sound";

	String KEY_JUGNOO_BALANCE = "jugnoo_balance";
	String KEY_PAYTM_BALANCE = "paytm_balance";

	String SP_INSTALL_REFERRER_CONTENT = "sp_install_referrer_content";
	String KEY_REFERRER = "referrer";

	String KEY_STATUS = "status";
	String KEY_USER_APP_MONITORING = "user_app_monitoring";
	String KEY_USER_APP_MONITORING_DURATION = "user_app_monitoring_duration";

	String KEY_VEHICLE_TYPE = "vehicle_type";
	String KEY_REGION_ID = "region_id";
	String KEY_ICON_SET = "icon_set";

	int VEHICLE_AUTO = 1;

	String KEY_GIVEN_RATING = "given_rating";
	String KEY_COUPONS = "coupons";

	String KEY_SIGNED_UP_AT = "signed_up_at";
	String KEY_CONTACTS = "contacts";
	String KEY_CONTACT_TYPE = "contact_type";
	String KEY_CONTACT_VALUE = "contact_value";
	String KEY_SUBSCRIPTION_STATUS = "subscription_status";
	String KEY_ACTIVE = "active";
	String KEY_PHONE = "phone";

	String KEY_PAYMENT_MODE = "payment_mode";
	String KEY_DELIVERY_SLOT_ID = "delivery_slot_id";
	String KEY_DELIVERY_ADDRESS = "delivery_address";
	String KEY_CART = "cart";
	String KEY_SUB_ITEM_ID = "sub_item_id";
	String KEY_QUANTITY = "quantity";
	String SP_FRESH_INTRO_SHOWN = "fresh_intro_shown";
	String SP_FRESH_CART= "fresh_cart";
	String EMPTY_JSON_OBJECT = "{}";
	String KEY_CATEGORY_POSITION = "category_position";

	String KEY_REFER_ALL_STATUS_LOGIN = "refer_all_status_login";
	String KEY_REFER_ALL_TEXT_LOGIN = "refer_all_text_login";
	String KEY_REFER_ALL_TITLE_LOGIN = "refer_all_title_login";
	String KEY_USER_RESPONSE = "user_response";
	String KEY_IS_LOGIN_POPUP = "is_login_popup";

	String KEY_FRESH_ORDER_ID = "order_id";

	String KEY_REFER_DRIVER_NAME = "driver_name";
	String KEY_REFER_DRIVER_PHONE_NO = "driver_phone_no";


	String SP_REFERRAL_CODE = "sp_referral_code";
	String KEY_COUPON_SELECTED = "coupon_selected";

	String SP_PUSH_DIALOG_CONTENT = "push_dialog_content";
	String KEY_SHOW_DIALOG = "show_dialog";
	String KEY_PICTURE = "picture";
	String KEY_IMAGE = "image";
	String KEY_BUTTON_TEXT = "button_text";
	String KEY_SHOW_PUSH = "show_push";

	String KEY_BRANCH_REFERRING_LINKS = "branch_referring_links";
	String KEY_SP_FUGU_CAMPAIGN_NAME = "fugu_campaign_name";

	String KEY_WIDTH = "width";
	String KEY_HEIGHT = "height";
	String KEY_CAMPAIGN_ID = "campaign_id";
	String KEY_DATA = "data";
	String VALUE_CAMPAIGN_DATA_REQUEST = "1";
	String VALUE_CAMPAIGN_DATA_CANCEL = "-1";

	String KEY_CITY = "city";
	String KEY_CITY_REG = "city_reg";
	String KEY_FARE_VALUE = "fare_value";
	String KEY_FARE_TO_PAY = "fare_to_pay";
	String KEY_PAID_RIDE = "paid_ride";
	String KEY_OP_DROP_LATITUDE = "op_drop_latitude";
	String KEY_OP_DROP_LONGITUDE = "op_drop_longitude";

	String KEY_REFERRAL_LEADERBOARD_ENABLED = "referral_leaderboard_enabled";
	String KEY_REFERRAL_ACTIVITY_ENABLED = "referral_activity_enabled";
	String KEY_CODE = "code";

	String ACQUISITION = "Acquisition";
	String CAMPAIGNS = "Campaigns";
	String ACTIVATION = "Activation";
	String REVENUE = "Revenue";
	String RETENTION = "Retention";
	String HELP = "Help";
	String INFORMATIVE = "Informative";
	String REFERRAL = "Referral";
	String ISSUES = "Issues";
	String SLASH = "/";

	String KEY_UPDATED_USER_NAME = "updated_user_name";
	String KEY_UPDATED_USER_EMAIL = "updated_user_email";
	String KEY_UPDATED_PHONE_NO = "updated_phone_no";
	String KEY_OLD_PASSWORD = "old_password";
	String KEY_NEW_PASSWORD = "new_password";

}
