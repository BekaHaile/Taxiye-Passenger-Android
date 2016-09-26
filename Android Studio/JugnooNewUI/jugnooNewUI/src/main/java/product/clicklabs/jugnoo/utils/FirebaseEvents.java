package product.clicklabs.jugnoo.utils;

/**
 * Created by gurmail on 28/07/16.
 */
public interface FirebaseEvents {

    //Categories
    String TRANSACTION = "Tr";
    String INFORMATIVE = "In";
    String FB_REVENUE = "Rv";      // get from Constant interface
    String ISSUES = "Issues";
//    String HELP = "Help";    // get from Constant interface
    String REFERRAL = "Referral";
    String FB_ACQUISITION = "Ac";
    String FB_CAMPAIGNS = "Ca";   // get from Constant interface



    // Labels
    String HOME_SCREEN = "H_S";
    String RIDE_COMPLETED = "Rid_Cm";
    String RIDE_START = "Rid_St";
    String REQUEST_RIDE = "req_ride";
    String ACCEPT_RIDE = "acc_ride";
    String AUTO_POOL = "auto_pool";
    String LOGIN_PAGE = "Lg_Pg";
    String REFER_A_DRIVER = "Ref_Dr";
    String POOL = "pool";
    String AUTO = "auto";
    String S_PAYMENT_MODE = "s_pay_m";
    String VIEW_ACCOUNT = "V_A";
    String SPLASH_SCREEN = "S_S";
    String B_PAYMENT_MODE = "b_pay_m";
    String MEALS_PAYMENT_MODE = "m_pay_mode";
    String FRESH_PAYMENT_MODE = "f_pay_mode";


    // Events
    String MENU = "menu";
    String DIFFERENT_PICKUP_LOCATION_POPUP = "diff_pick_loc_pop";
    String GET_FARE_ESTIMATE = "get_fare_esti";
    String OFFER_SELECTED = "offer_selected";
    String OFFER_T_N_C = "offer_tnc";
    String FRESH = "fresh";
    String GROCERY = "grocery";
    String REQUEST_RIDE_L1_AUTO = "req_ride_l1_auto";
    String REQUEST_RIDE_L1_AUTO_POOL = "req_ride_l1_auto_pool";

    String FARE_POPUP = "fare_popup";
    String B_OFFER = "b_off";
    String RATING_5 = "rating_5";
    String VIEW_INVOICE = "view_invoice";
    String RATING_1 = "rating_1";
    String CALL_DRIVER = "Call_Driver";
    String ENTER_DESTINATION = "enter_destination";
    String SEND_INVITES = "send_inv_frnds_sc";
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
    String REQUEST_RIDE_L2_AUTO_POOL = "req_ride_l2_auto_pool";

    String INBOX = "inbox";
    String PROMOTION = "promotion";
    String WALLET = "wal";
    String FREE_RIDES = "F_rid_send_inv_frnds_sc";
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
    String PAYTM_WALLET = "P_wal";
    String MOBIKWIK_WALLET = "M_wal";
    String FREECHARGE_WALLET = "F_Wal";
    String VIEW_RECENT_TRANSACTION = "View_Recent_Transaction";
    String REQUEST_OTP = "Request_OTP";
    String EDIT = "Edit";
    String REMOVE_WALLET = "Remove_Wallet";
    String ADD_PAYTM_CASH = "Add_Paytm_Cash";
    String ADD_MOBIKWIK_CASH = "Add_Mobikwik_Cash";
    String ADD_FREECHARGE_CASH = "Add_Freecharge_Cash";
    String ADD_AMOUNT = "add_amount";

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
    String INVITE_FRIENDS = "inv_frnds_sc";
    String PAYTM = "paytm";
    String CASH = "cash";
    String FREECHARGE = "freecharge";
    String SKIP_RIDE_IMAGE_END = "skip_ride_image_end";
    String AUTO_CANCEL_RIDE = "auto_cancel_ride";
    String POOL_CANCEL_RIDE = "pool_cancel_ride";
    String REFER = "refer";
    String DRIVER_ARRIVED = "driver_arrived";
    String DEEP_INDEX = "Deep_Index_";

    String OTP_SCREEN = "OTP_S";
    String SIGN_UP_PAGE = "Si_up_Pg";
    String VERIFY_ME = "Verify_me";
    String GIVE_A_MISS_CALL = "Give_a_miss_call";
    String EDIT_PHONE_NUMBER = "Edit_phone_number";
    String SIGN_UP = "Sign_up";
    String SIGN_UP_WITH_GOOGLE = "Sign_up_with_Goo";
    String SIGN_UP_WITH_FACEBOOK = "Sign_up_with_FB";
    String TERMS_OF_USE = "Terms_of_use";



    String PRIORITY_TIP_POP_UP = "surge";
    String CBCD_POP_UP = "CBCD_up";
    String CBCR_POP_UP = "CBCR_up";
    String PROMOTIONS = "Promo";
    String PROMOTIONAL_POP_UP = "Promo_up";
    String YES = "Yes";
    String NO_THANKS = "No_thanks";
    String APPLY = "Apply";
    String WANT_FREE_RIDES = "Want_inv_frnds_sc";
    String COUPON_PROMOTION = "Coupon_promotion";
    //*State of user at time of pop up (eg ride start)
    String GENERAL_BUTTON_OF_YES = "General_button_of_yes";

    String SUPPORT = "Support";

    String RIDE_HISTORY = "Rid_H";
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
    String OUTSIDE_EMAIL = "Outside_email";
    String THREEDOTS = "3_dots";
    String DETAILS = "Details";
    String DIALOG_WHATSUPP = "Dialog_whatsapp";
    String DIALOG_FB_MESSENGER = "Dialog_fb_messenger";
    String DIALOG_FB_APP = "Dialog_fb_app";
    String DIALOG_GMAIL = "Dialog_gmail";
    String DIALOG_TWITTER = "Dialog_twitter";
    String DIALOG_MOBILE_SMS = "Dialog_mobile_sms";
    String DIALOG_OTHERS = "Dialog_others";


    String M_CART = "m_cart";
    String TRASH="trash";
    String BUTTON = "button";
    String MEALS = "meals";
    String M_ADD = "m_add";
    String M_SORT = "m_sort";
    String A_Z = "a-z";
    String M_PAY = "m_pay";
    String POPULARITY = "popularity";
    String LOW_TO_HIGH_PRICE = "low_to_high_price";
    String HIGH_TO_LOW_PRICE = "high_to_low_price";
    String NO = "No";
    String CHECKOUT = "checkout";
    String SLOT1 = "slot1";
    String SLOT2 = "slot2";
    String SLOT3 = "slot3";
    String PAY = "pay";
    String ADD_PROMO = "add_promo";
    String FRUITS = "fruits";
    String VEGETABLES = "vegetables";
    String ICE_CREAM = "ice_cream";
    String PREMIUM = "pre";
    String DRY = "dry";
    String BASKETS = "baskets";
    String F_SORT = "f_sort";
    String F_ADD = "f_add";
    String F_SEARCH_CART = "f_search_cart";
    String F_SEARCH_GO = "f_search_go";
    String F_CART = "f_cart";
    String F_PAY = "f_pay";
    String PLACE_ORDER = "place_order";
    String BUTTON_GENIE = "button_genie";
    String FREECHARGE_SHORT = "FC";

    String MENU_CATEGORIES = "menu_categories";
    String MENU_CATEGORIES_AUTOS = "menu_categories_autos";
    String MENU_CATEGORIES_FRESH = "menu_categories_fresh";
    String MENU_CATEGORIES_MEALS = "menu_categories_meals";

    String G_ADD = "g_add";

}
