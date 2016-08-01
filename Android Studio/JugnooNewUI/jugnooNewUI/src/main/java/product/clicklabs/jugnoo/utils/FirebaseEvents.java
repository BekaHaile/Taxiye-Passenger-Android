package product.clicklabs.jugnoo.utils;

/**
 * Created by gurmail on 28/07/16.
 */
public interface FirebaseEvents {

    //Categories
    String TRANSACTION = "Transaction";
    String INFORMATIVE = "Informative";
//    String REVENUE = "Revenue";      // get from Constant interface
    String ISSUES = "Issues";
//    String HELP = "Help";    // get from Constant interface
    String REFERRAL = "Referral";
    String ACQUISTION = "Acquistion";
//    String CAMPAIGNS = "Campaigns";   // get from Constant interface
    String ACTIVATION_RETENTION = "Activation_Retention";


    // Labels
    String HOME_SCREEN = "Home_Screen";
    String RIDE_COMPLETED = "ride_completed";
    String RIDE_START = "Ride_Start";
    String REQUEST_RIDE = "request_ride";
    String ACCEPT_RIDE = "accept_ride";
    String AUTO_POOL = "auto_pool";
    String LOGIN_PAGE = "Login_Page";
    String REFER_A_DRIVER = "Refer_a_driver";
    String POOL = "pool";
    String AUTO = "auto";
    String S_PAYMENT_MODE = "s_payment_mode";
    String VIEW_ACCOUNT = "View_Account";
    String SPLASH_SCREEN = "Splash_Screen";



    // Events
    String MENU = "menu";
    String DIFFERENT_PICKUP_LOCATION_POPUP = "different_pickup_location_popup";
    String GET_FARE_ESTIMATE = "get_fare_estimate";
    String OFFER_SELECTED = "offer_selected";
    String OFFER_T_N_C = "offer_tnc";
    String FRESH = "fresh";
    String REQUEST_RIDE_L1_AUTO = "request_ride_l1_auto";
    String REQUEST_RIDE_L1_AUTO_POOL = "request_ride_l1_auto_pool";
    String B_PAYMENT_MODE = "b_payment_mode";
    String FARE_POPUP = "fare_popup";
    String B_OFFER = "b_offer";
    String RATING_5 = "rating_5";
    String VIEW_INVOICE = "view_invoice";
    String RATING_1 = "rating_1";
    String CALL_DRIVER = "Call_Driver";
    String ENTER_DESTINATION = "enter_destination";
    String SEND_INVITES = "send_invites";
    String CANCEL_RIDE = "cancel_ride";
    String REQUEST_RIDE_L2 = "request_ride_l2";
    String LOGIN = "Login";
    String BACK = "Back";
    String FORGET_PASSWORD = "Forget_password";
    String LOGIN_WITH_GOOGLE = "Login_with_Google";
    String LOGIN_WITH_FACEBOOK = "Login_with_facebook";
    String OK = "ok";
    String CANCEL = "cancel";
    String GET_RIDE = "get_ride";
    String REQUEST_RIDE_L2_AUTO_POOL = "request_ride_l2_auto_pool";

    String INBOX = "inbox";
    String PROMOTION = "promotion";
    String WALLET = "wallet";
    String FREE_RIDES = "Free_rides";
    String GAME = "game";
    String GET_A_RIDE = "Get_a_Ride";
    String ABOUT = "About";

    String LOGOUT = "Logout";
    String ADD_HOME = "Add_Home";
    String EMERGENCY_CONTACTS = "Emergency_contacts";
    String CHANGE_PASSWORD = "Change_Password";
    String ADD_WORK = "Add_Work";
    String ABOUT_JUGNOO = "About_Jugnoo";
    String TERMS_AND_CONDITION = "Terms_and_Condition";
    String PLAYSTORE_RATING = "Playstore_rating";
    String PRIVACY_POLICY = "Privacy_Policy";
    String FACEBOOK_LIKE = "Facebook_Like";
    String NEED_HELP_ON_A_RIDE = "Need_help_on_a_ride";

    String JUGNOO_CASH = "Jugnoo_Cash";
    String PAYTM_WALLET = "Paytm_Wallet";
    String MOBIKWIK_WALLET = "Mobikwik_Wallet";
    String VIEW_RECENT_TRANSACTION = "View_Recent_Transaction";
    String REQUEST_OTP = "Request_OTP";
    String EDIT = "Edit";
    String REMOVE_WALLET = "Remove_Wallet";
    String ADD_PAYTM_CASH = "Add_Paytm_Cash";
    String ADD_MOBIKWIK_CASH = "Add_Mobikwik_Cash"

    String CUSTOMER_SUPPORT = "Customer_Support";
    String SELECT_AN_ISSUE = "Select_An_Issue";
    String ISSUE_WITH_FARE = "Issue_with_fare";
    String PROMOTIONAL_AND_OFFERS = "Promotional_and_Offers";
    String REQUESTING_A_RIDE = "Requesting_a_ride";
    String GENERAL_QUESTIONS = "General_questions";
    String ISSUE_WITH_DRIVER = "Issue_with_driver";
    String RELATED_TO_PICKUPS = "Related_to_pickups";
    String PAYING_FOR_THE_TRIP = "Paying_for_the_trip";
    String ISSUE_WITH_PICKUP = "Issue_with_pickup";
    String WE_ARE_SORRY_TO_HEAR_THAT_YOU_HAVE_LOST_AN_ITEM = "We_are_sorry_to_hear_that_you_have_lost_an_item";

    String HELP_POP_UP = "help_pop_up";
    String CALL_YOUR_CONTACTS = "call_your_contacts";
    String SEND_RIDE_STATUS_SCREEN = "send_ride_status_screen";
    String EMERGENCY_MODE_SCREEN = "emergency_mode_screen";


    String CONTINUE = "continue";
    String SKIP = "skip";
    String INVITE_FRIENDS = "invite_friends";
    String PAYTM = "paytm";
    String CASH = "cash";
    String SKIP_RIDE_IMAGE_END = "skip_ride_image_end";
    String AUTO_CANCEL_RIDE = "auto_cancel_ride";
    String POOL_CANCEL_RIDE = "pool_cancel_ride";
    String REFER = "refer";
    String DRIVER_ARRIVED = "driver_arrived";
    String DEEP_INDEX = "Deep_Index_";

    String OTP_SCREEN = "OTP_screen";
    String SIGN_UP_PAGE = "Sign_up_Page";
    String VERIFY_ME = "Verify_me";
    String GIVE_A_MISS_CALL = "Give_a_miss_call";
    String EDIT_PHONE_NUMBER = "Edit_phone_number";
    String SIGN_UP = "Sign_up";
    String SIGN_UP_WITH_GOOGLE = "Sign_up_with_Google";
    String SIGN_UP_WITH_FACEBOOK = "Sign_up_with_Facebook";
    String TERMS_OF_USE = "Terms_of_use";



    String PRIORITY_TIP_POP_UP = "Priority_tip_pop_up";
    String CBCD_POP_UP = "CBCD_pop_up";
    String CBCR_POP_UP = "CBCR_pop_up";
    String PROMOTIONS = "Promotions";
    String PROMOTIONAL_POP_UP = "Promotional_pop_up";
    String YES = "Yes";
    String NO_THANKS = "No_thanks";
    String APPLY = "Apply";
    String WANT_FREE_RIDES = "Want_free_rides";
    String COUPON_PROMOTION = "Coupon_promotion";
    //*State of user at time of pop up (eg ride start)
    String GENERAL_BUTTON_OF_YES = "General_button_of_yes";

    String SUPPORT = "Support";

    String RIDE_HISTORY = "Ride_history";
    String ISSUE_WITH_RECENT_RIDE = "Issue_with_recent_ride";
    String PROMOTIONS_AND_OFFERS = "Promotions_and_offers";
    String PAYMENTS = "Payments";
    String USING_JUGNOO = "Using_jugnoo";
    String FORGOT_AN_ITEM = "Forgot_an_item";
    String CALL_JUGNOO = "Call_jugnoo";
    String MOBIKWIK  = "mobikwik";


    String OUTSIDE_FB_MESSENGER  = "Outside_fb_messenger";
    String OUTSIDE_WHATSAPP = "Outside_whatsapp";
    String OUTSIDE_MOBILE_SMS = "Outside_mobile_sms";
    String THREEDOTS = "3_dots";
    String DETAILS = "Details";
    String DIALOG_WHATSUPP = "Dialog_whatsapp";
    String DIALOG_FB_MESSENGER = "Dialog_fb_messenger";
    String DIALOG_FB_APP = "Dialog_fb_app";
    String DIALOG_GMAIL = "Dialog_gmail";
    String DIALOG_TWITTER = "Dialog_twitter";
    String DIALOG_MOBILE_SMS = "Dialog_mobile_sms";
    String DIALOG_OTHERS = "Dialog_others";

}
