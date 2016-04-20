package product.clicklabs.jugnoo.utils;

/**
 * Created by socomo20 on 7/28/15.
 */
public interface FlurryEventNames {

    String

			SIGNUP_THROUGH_REFERRAL = "Sign-up through Referral Deep link",
        SIGNUP = "Sign-up",
        SIGNUP_VIA_FACEBOOK = "Sign-up via Facebook",
	SIGNUP_VIA_GOOGLE = "Signup via google",
        SIGNUP_FINAL = "Sign-up Submit",
        OTP_VERIFIED_WITH_SMS = "OTP verified with SMS",
        CHANGE_PHONE_OTP_NOT_RECEIVED = "Changed phone number_OTP Not received",
        CALL_ME_OTP = "OTP not received so call received",
		GIVE_MISSED_CALL = "Give missed call",
        LOGIN_OPTION_MAIN = "Login option main",
        LOGIN_VIA_FACEBOOK = "Login via Facebook",
        LOGIN_VIA_GOOGLE = "Login via Google",
        LOGIN_VIA_EMAIL = "Login via E-mail",
        FORGOT_PASSWORD = "forgot password",
        PASSWORD_RETRIEVED = "password retrieved",
        REFERRAL_POPUP_CLOSE = "Referral_Popup_Close",
        REFERRAL_POPUP_FACEBOOK = "Referral_popup_Facebook",
        REFERRAL_POPUP_WHATSAPP = "Referral popup Whatsapp",
        REFERRAL_POPUP_MESSAGE = "Referral popup messaging",
        REFERRAL_POPUP_EMAIL = "Referral popup E-mail",
        REFERRAL_GIFT_ICON = "Referral_Gift_icon",
        INVITE_FACEBOOK = "Invite&Earn_ Facebook",
        INVITE_WHATSAPP = "Invite&Earn_ Whatsapp",
            INVITE_GENERIC = "Invite&Earn Generic",
        INVITE_MESSAGE = "Invite&Earn_ Messaging",
        INVITE_EMAIL = "Invite&Earn_ Email",
        PICKUP_LOCATION_SET = "Pick up location set",
        PICKUP_LOCATION_NOT_SET = "pickup location not set",
        NAVIGATION_TO_CURRENT_LOC = "Navigation to current location",
        JUGNOO_GENIE = "Jugnoo Jeanie",
        JUGNOO_GENIE_MEALS = "JugnooJeanie_meals",
        JUGNOO_GENIE_FATAFAT = "JugnooJeanie_Fatafat",
        AUTO_RIDE_ICON = "Auto icon ride screen",
        FARE_ESTIMATE = "Fare estimate",
        FARE_ESTIMATE_CALCULATED = "Fare estimate calculated",
        RATE_CARD = "Rate card",
        FINAL_RIDE_CALL_MADE = "Final call ride made",
        FINAL_CALL_RIDE = "Final call ride",
        COUPON_SELECTION_MADE = "Coupon selection made",
        COUPON_SELECTION_NOT_MADE = "Coupon selection not made",
        DROP_LOCATION_USED_FINIDING_DRIVER = "drop location_used_finding driver",
        DROP_LOCATION_OPENED_NOT_USED_FINDING_DRIVER = "drop location_opened not used_finding driver",
        REQUEST_CANCELLED_FINDING_DRIVER = "Request cancelled_finding driver",
        DROP_LOCATION_USED_RIDE_ACCEPTED = "drop location used_ride accepted",
        DROP_LOCATION_OPENED_BUT_NOT_USED_RIDE_ACCEPTED = "drop location opened but not used_ride accepted",
        CALL_TO_DRIVER_MADE_WHEN_NOT_ARRIVED = "Call to driver made when not arrived",
        SOS_ALERT_USED = "sos alert used",
        SOS_ALERT_CANCELLED = "SOS alert cancelled",
        EMERGENCY_CONTACT_TO_BE_ADDED = "Emergency contact to be added",
			FAVORITE_LOCATION_TO_BE_ADDED = "Favorite location to be added",
        EMERGENCY_CONTACT_ADDED = "Emergency contact added",
        SOS_SMS_TO_EMERGENCY_CONTACT = "SOS SMS to Emergency contact",
        SOS_CALL_TO_EMERGENCY_CONTACT = "SOS call to Emergency Contact",
        RIDE_CANCELLED_NOT_COMPLETE = "Ride cancelled_not complete",
        RIDE_CANCELLED_COMPLETE = "Ride cancelled_complete",
        CALL_TO_DRIVER_MADE_WHEN_ARRIVED = "call to driver made when arrived",
        JUGNOO_CASH_ADDED_WHEN_DRIVER_ARRIVED = "jugnoo cash added when driver arrived",
        JUGNOO_CASH_ADDED_WHEN_RIDE_IN_PROGRESS = "Jugnoo cash added when ride in progress",
        JUGNOO_CASH_ADDED_WHEN_ALERT_GENERATED = "Jugnoo cash added when alert generated",
        FARE_RECEIPT_CHECKED = "Fare receipt checked",
        FEEDBACK_AFTER_RIDE_NO = "Feedback after ride_NO",
        FEEDBACK_AFTER_RIDE_YES = "Feedback after ride_YES",
        FEEDBACK_WITH_COMMENTS = "Feedback with comments",
        RATE_US_NOW_POP_RATED = "Rate us now pop rated",
        RATE_US_NOW_POP_NOT_RATED = "Rate us now pop not rated",
        MENU_LOOKUP = "Menu lookup",
        INVITE_EARN_MENU = "Invite&Earn_Menu",
		JUGNOO_LINE_CLICK = "Jugnoo line opened",
		AUTO_ID_ENTERED_LINE = "Auto ID entered_Line",
		LINE_PAYED_VIA_CASH = "Line_payed via cash",
		JUGNOO_LINE_PAYMENT = "Line_payed via JC",
        JUGNOO_CASH_MENU = "Jugnoo Cash_Menu",
            USER_DEBT_MAKE_PAYMENT = "User Debt make payment",
			WORLD_CUP_MENU = "World cup menu",
            WALLET_MENU = "Wallet Menu",
			WALLET_BEFORE_REQUEST_RIDE = "Wallet before request ride",
			WALLET_VIA_TUTORIAL = "Wallet opened via Tutorial",
        RECENT_TRANSACTION_LOOK_UP = "Recent transaction look up",
        INVITE_EARN_JUGNOO_CASH = "Invite&Earn_Jugnoo Cash",
            INVITE_EARN_MORE_INFO = "Invite&Earn more info",
        ADDING_JUGNOO_CASH = "Adding Jugnoo Cash",
        ADD_JUGNOO_CASH_WITH_GIVEN_AMOUNT = "Add Jugnoo cash with Given amount",
        ADD_JUGNOO_CASH_MANUALLY = "Add Jugnoo cash Manually",
        ADDING_JUGNOO_CASH_FOR_FINAL_PAYMENT = "Adding jugnoo cash for final payment",
        PAYMENT_BY_DEBIT_CARD = "Payment by debit card",
        PAYMENT_BY_CREDIT_CARD = "Payment by credit card",
        PAYMENT_BY_NET_BANKING = "Payment by net banking",
        PAYMENT_MADE_FOR_JUGNOO_CASH = "Payment made for Jugnoo cash",
        PROMOTIONS_CHECKED = "Promotions checked",
        PROMO_CODE_ENTERED = "Promo code entered",
        PROMO_CODE_APPLIED = "Promo code applied",
        ONGOING_OFFERS_CHECKED = "ongoing offers checked",
        INVITE_EARN_PROMOTIONS = "Invite&Earn_Promotions",
        RIDE_HISTORY = "Ride history",
        RIDE_RATED_ON_RIDE_HISTORY = "Ride rated on ride history",
        FARE_DETAILS = "Fare details",
        SUPPORT_OPTIONS = "Support options",
            SUPPORT_OPTIONS_THROUGH_EMERGENCY = "Support options through Emergency",
        SEND_EMAIL_SUPPORT = "Send email_support",
        CALL_SUPPORT = "call_support",
        FAQS_SUPPORT = "FAQS_Support",
        FEEDBACK_SUPPORT = "Feedback_support",
        FEEDBACK_COMMENTS_PROVIDED = "Feedback_comments provided",
        FEEDBACK_SUBMITTED = "Feedback submitted",
        RATING_ON_PLAYSTORE_ABOUT = "Rating on playstore_about",
        LIKING_ON_FACEBOOK_ABOUT = "Liking on facebook_about",
        TERMS_AND_CONDITIONS = "terms and conditions",
        PRIVACY_POLICY = "Privacy policy",
        ABOUT_JUGOO_AUTOS = "About Jugoo Autos",
	NOTIFICATION_ICON = "Notification Icon",
	NOTIFICATION_CENTER_DRAWER = "Notification center drawer",

			CALL_WHEN_NO_INTERNET =	"Call when no internet",
			COUPONS_SELECTED =	"Coupons selected",
			COUPON_NOT_SELECTED =	"Coupon not selected",
			RECENT_TRANSACTIONS =	"Recent transactions",
			JUGNOO_CASH_CHECKED =	"Jugnoo cash checked",
			PAYTM_WALLET_ADD_CLICKED =	"paytm wallet add clicked",
			PAYTM_WALLET_OPENED =	"paytm wallet opened",
			NOTIFICATION_CENTER_MENU =	"notification center menu",
			RIDE_SUMMARY_CHECKED_LATER =	"Ride summary checked later",
			CASH_SELECTED_WHEN_REQUESTING =	"cash selected when requesting",
			PAYTM_SELECTED_WHEN_REQUESTING =	"Paytm selected when requesting",

	GENERIC_FACEBOOK = "Generic Facebook share",
	GENERIC_EMAIL = "Generic Email share",
	GENERIC_WHATSAPP = "Generic Whatsapp share",
	GENERIC_SMS_OTHER = "Generic SMS and other share",

	JUGNOO_STICKY_OPENED = "Jugnoo Sticky opened",
	JUGNOO_STICKY_EXPANDED = "Jugnoo Sticky expanded",
	JUGNOO_STICKY_TRANSFER_TO_APP = "Jugnoo Sticky transfer to app",
	JUGNOO_STICKY_RIDE_CONFIRMATION = "Jugnoo Sticky Ride confirmation"


			;



	String BRANCH_EVENT_REQUEST_RIDE = "request_ride",
			BRANCH_EVENT_RIDE_COMPLETED = "ride_completed",
			BRANCH_EVENT_REGISTRATION = "registration"

			;

    String FB_EVENT_REQUEST_RIDE = "request_ride",
        FB_EVENT_RIDE_COMPLETED = "ride_completed",
		FB_EVENT_REGISTRATION = "registration"

            ;



    String INVITE_PUSH_RECEIVED = "invite_push_received",
            INVITE_SCREEN_THROUGH_PUSH = "invite_screen_through_push",
            INVITE_SHARE_GENERIC_THROUGH_PUSH = "invite_share_generic_through_push"

                    ;

    String SUPPORT_MAIN_OPENED = "support_main_opened",
            SUPPORT_ISSUE_WITH_RECENT_RIDE = "support_issue_with_recent_ride",
            SUPPORT_ISSUE_OPENED = "support_issue_opened",
            SUPPORT_ISSUE_FEEDBACK_SUBMITTED = "support_issue_feedback_submitted",
            SUPPORT_RIDE_HISTORY_OPENED = "support_ride_history_opened",
            SUPPORT_NEXT_LEVEL_OPENED = "support_next_level_opened",
            SUPPORT_ISSUE_CALL_DRIVER = "support_issue_call_driver",
            SUPPORT_ISSUE_CALL_JUGNOO = "support_issue_call_jugnoo",
            RIDE_SUMMARY_NEED_HELP = "ride_summary_need_help"

            ;

    String API_GET_ONGOING_RIDE_PATH = "api_get_ongoing_ride_path",
            API_GET_DRIVER_CURRENT_LOCATION = "api_get_driver_current_location",
            API_FIND_A_DRIVER = "api_find_a_driver",
            API_SHOW_AVAILABLE_PROMOTIONS = "api_show_available_promotions",
            API_LOGIN_USING_ACCESS_TOKEN = "api_login_using_access_token",
            API_GET_CURRENT_USER_STATUS = "api_get_current_user_status",
            API_REQUEST_RIDE = "api_request_ride",
            API_PAYTM_CHECK_BALANCE = "api_paytm_check_balance"

                    ;

    String EMERGENCY_MODE_ENABLED = "emergency_mode_enabled"

            ;


    String WHO_VISITED_FREE_RIDE_SCREEN = "who_visited_free_ride_screen",
            WHO_CLICKED_ON_INVITE_FRIENDS = "who_clicked_on_invite_friends",
            WHO_CLICKED_ON_LEADERBOARD = "who_clicked_on_leaderboard",
            WHO_CLICKED_ON_ACTIVITY = "who_clicked_on_activity",
            WHO_CLICKED_ON_WHATSAPP = "who_clicked_on_whatsapp",
            WHO_CLICKED_ON_FACEBOOK = "who_clicked_on_facebook",
            WHO_CLICKED_ON_TWITTER = "who_clicked_on_twitter",
            WHO_CLICKED_ON_EMAIL = "who_clicked_on_email",
            WHO_CLICKED_ON_SMS = "who_clicked_on_sms",
            WHO_CLICKED_ON_OTHERS = "who_clicked_on_others",
            TO_WHOM_A_PUSH_WAS_DELIVERED = "to_whom_a_push_was_delivered",
            WHO_CLICKED_THE_PUSH = "who_clicked_the_push",
            WHO_VISITED_THE_NOTIFICATION_SCREEN = "who_visited_the_notification_screen",
            CLICKS_ON_GET_A_RIDE = "clicks_on_get_a_ride",
            CLICKS_ON_OFFERS = "clicks_on_offers",
            CLICKS_ON_MIN_FARE = "clicks_on_min_fare",
            CLICKS_ON_GET_FARE_ESTIMATE = "clicks_on_get_fare_estimate",
            CLICKS_ON_PAYTM = "clicks_on_paytm",
            CHANGED_MODE_FROM_PAYTM_TO_CASH = "changed_mode_from_paytm_to_cash",
            CLICKS_ON_WALLET = "clicks_on_wallet",
            CLICKS_ON_PAYTM_WALLET = "clicks_on_paytm_wallet",
            CLICKS_ON_REMOVE_WALLET = "clicks_on_remove_wallet",
            WHO_VISITED_T20_WORLD_CUP_SCREEN = "who_visited_T20_World_Cup_screen";
    

    String ERROR_CONNECTION_TIMEOUT = "error_connection_timeout",
            ERROR_SOCKET_TIMEOUT = "error_socket_timeout",
            ERROR_NO_INTERNET = "error_no_internet"
    ;


    String CAMPAIGN_ = "campaign_",
            LOGIN_SINCE_FIRST_APP_OPEN_DIFF = "login_since_first_app_open_diff",
            APP_DOWNLOAD_SOURCE = "app_download_source";

    String CLICKS_ON_PROMOTIONS_SCREEN = "clicks_on_promotions_screen",
            ENTERED_PROMO_CODE = "entered_promo_code",
            CLICKS_ON_APPLY = "clicks_on_apply",
            CLICKS_ON_INVITE = "clicks_on_invite_on_promotions_screen",
            TNC_VIEWS = "coupon_tnc_views",
            TNC_VIEWS_PROMO = "promotion_tnc_views",
            HOW_MANY_USERS_ADDED_ADD_HOME = "how_many_users_added_add_home",
            HOW_MANY_USERS_ADDED_ADD_WORK = "how_many_users_added_add_work",
            CLICKS_ON_LOGOUT = "clicks_on_logout",
            CLICKS_ON_EMERGENCY_CONTACTS = "clicks_on_emergency_contacts",
            CLICKS_ON_ADDED_EMERGENCY_CONTACTS = "clicks_on_added_emergency_contacts",
            CLICKS_ON_RIDE_SUMMARY = "clicks_on_ride_summary",
            CLICKS_ON_NEED_HELP = "clicks_on_need_help",
            CLICKS_ON_SUPPORT_ISSUES = "clicks_on_support_issues",
            CLICKS_ON_SUPPORT = "clicks_on_support",
            CLICKS_ON_RIDE_HISTORY = "clicks_on_ride_history",
            CLICKS_ON_ACCOUNT = "clicks_on_account";

    String SURGE_NOT_ACCEPTED = "surge_not_accepted";


    String NUDGE_REQUEST_RIDE = "request_ride",
            NUDGE_NO_COUPONS = "no_coupons",
            NUDGE_COUPON_AVAILABLE = "coupon_available",
            NUDGE_INVITE_FRIENDS = "invite_friends",
            NUDGE_DRIVER_NOT_ASSIGNED = "driver_not_assigned",
            NUDGE_RIDE_COMPLETED = "ride_completed",
            NUDGE_FEEDBACK = "feedback",
            NUDGE_CANCEL_REQUEST = "cancel_request",
            NUDGE_CANCEL_RIDE = "cancel_ride",
            NUDGE_APP_OPEN = "app_open",
            NUDGE_SIGNUP = "signup",
            NUDGE_SIGNUP_VERIFIED = "signup_verified";

    String FRESH_MAIN = "jugnoo_fresh_main";
    String FRESH_CART = "jugnoo_fresh_cart";
    String FRESH_CHECKOUT = "jugnoo_fresh_checkout";
    String FRESH_PAYMENT = "jugnoo_fresh_payment";
    String FRESH_ORDER_PLACED = "jugnoo_fresh_order_placed";
    String FRESH_ORDER_HISTORY = "jugnoo_fresh_order_history";
    String FRESH_SUPPORT = "jugnoo_fresh_support";
    String FRESH_ORDER_SUMMARY = "jugnoo_fresh_order_summary";


    String NUDGE_PAYTM_COUPON_AVAILABLE = "paytm_coupon_available",
            NUDGE_PAYTM_WALLET_REMOVED = "paytm_wallet_removed",
            NUDGE_INVITE_VIA_FACEBOOK = "invite_via_facebook",
            NUDGE_INVITE_VIA_WHATSAPP = "invite_via_whatsapp",
            NUDGE_INVITE_VIA_TWITTER = "invite_via_twitter",
            NUDGE_INVITE_VIA_EMAIL = "invite_via_email",
            NUDGE_INVITE_VIA_SMS = "invite_via_sms",
            NUDGE_INVITE_VIA_OTHER = "invite_via_other",
            NUDGE_OFFERS_TAB_CLICKED = "offers_tab_clicked",
            NUDGE_OFFER_SELECTED = "offer_selected",
            NUDGE_NOTIFICATION_CHECKED = "notification_checked",
            NUDGE_RIDE_ACCEPTED = "ride_accepted",
    NUDGE_CALL_DRIVER = "call_driver",
    NUDGE_RIDE_START = "ride_start",
    NUDGE_FARE_TAB_CLICKED = "fare_tab_clicked",
    NUDGE_FARE_ESTIMATE_CLICKED = "fare_estimate_clicked",
    NUDGE_PAYMENT_TAB_CLICKED = "payment_tab_clicked",
    NUDGE_PAYTM_METHOD_SELECTED = "paytm_method_selected",
    NUDGE_CASH_METHOD_SELECTED = "cash_method_selected",
    NUDGE_JUGNOO_FRESH_CLICKED = "jugnoo_fresh_clicked",
    NUDGE_JUGNOO_FRESH_ORDER_PLACED = "jugnoo_fresh_order_placed",
    NUDGE_MENU_CLICKED = "menu_clicked",
    NUDGE_GAME_CLICKED = "game_clicked",
    NUDGE_FREE_RIDES_CLICKED = "free_rides_clicked",
    NUDGE_WALLET_CLICKED = "wallet_clicked",
    NUDGE_PAYMENT_WALLET_CLICKED = "payment_wallet_clicked",
    NUDGE_ADD_MONEY_CLICKED = "add_money_clicked",
    NUDGE_EDIT_PAYTM_CLICKED = "edit_paytm_clicked",
            NUDGE_FRESH_CATEGORY_CLICKED_FORMAT = "fresh_category_%s_clicked",
            NUDGE_FRESH_CART_CLICKED = "fresh_cart_clicked",
            NUDGE_FRESH_ITEMS_IN_CART = "fresh_items_in_cart",
            NUDGE_FRESH_CHECKOUT_CLICKED = "fresh_checkout_clicked",
            NUDGE_FRESH_CART_DELETE_CLICKED = "fresh_cart_delete_clicked",
            NUDGE_FRESH_CART_DELETE_CANCEL_CLICKED = "fresh_cart_delete_cancel_clicked",
            NUDGE_FRESH_ADDRESS_CLICKED = "fresh_address_clicked",
            NUDGE_FRESH_DATE_TIME_CLICKED = "fresh_date_time_clicked",
            NUDGE_FRESH_PROCEED_TO_PAYMENT_CLICKED = "fresh_proceed_to_payment_clicked",
            NUDGE_FRESH_PAYTM_CLICKED = "fresh_paytm_clicked",
            NUDGE_FRESH_CASH_CLICKED = "fresh_cash_clicked",
            NUDGE_FRESH_BACK_ON_PAYMENT_CLICKED = "fresh_back_on_payment_clicked",
            NUDGE_FRESH_PLACE_ORDER_CLICKED = "fresh_place_order_clicked",
            NUDGE_FRESH_ORDER_PLACED = "fresh_order_placed",
            NUDGE_FRESH_BACK_TO_JUGNOO = "fresh_back_to_jugnoo"
            ;

}
