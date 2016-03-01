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

    String API_RESPONSE_TIME_LOG = "api_response_time_log";

    String API_GET_ONGOING_RIDE_PATH = "/get_ongoing_ride_path",
            API_GET_DRIVER_CURRENT_LOCATION = "/get_driver_current_location",
            API_FIND_A_DRIVER = "/find_a_driver",
            API_SHOW_AVAILABLE_PROMOTIONS = "/show_available_promotions",
            API_LOGIN_USING_ACCESS_TOKEN = "/login_using_access_token",
            API_GET_CURRENT_USER_STATUS = "/get_current_user_status",
            API_REQUEST_RIDE = "/request_ride",
            API_PAYTM_CHECK_BALANCE = "/paytm/check_balance"

                    ;

}