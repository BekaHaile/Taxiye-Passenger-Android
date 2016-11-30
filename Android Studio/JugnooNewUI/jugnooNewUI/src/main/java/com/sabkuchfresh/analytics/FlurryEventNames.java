package com.sabkuchfresh.analytics;

/**
 * Created by socomo20 on 7/28/15.
 */
public interface FlurryEventNames {

    String USER_ACCOUNT = "User Account",
            INTERACTIONS = "Interactions",
            HOME_SCREEN = "Home screen",
            SEARCH_SCREEN = "Search Screen",
            REVIEW_CART = "Review Cart",
            CHECKOUT = "Checkout",
            ADDRESS_SCREEN = "Address Screen",
            PAYMENT_SCREEN = "Payment Screen",
            ECOMMERCE_EVENTS = "Ecommerce events",
            FEEDBACK_SCREEN = "Feedback Screen",
            REFER_SCREEN = "REFERRAL SCREEN",
            Support_SCREEN = "Support SCREEN",
                    FRESH_FRAGMENT = "Fresh Screen",
            MEALS_FRAGMENT = "Meals Screen",
            GROCERY_FRAGMENT = "Grocery Screen"
    ;


    String SIGNUP = "Signup",
            LOGIN = "Login",
            PAYTM_SIGNUP = "Paytm_Signup",
            FORGOT_PASSWORD = "Forgot Password",
            CATEGORY_CHANGE = "Category Change",
            CART = "Cart",
            SORT = "Sort",
            ADD_PRODUCT = "Add Product",
            DELETE_PRODUCT = "Delete product",
            PRODUCT_SEARCH = "Product Search",
            DELETE = "Delete",
            ADD = "Add",
            TIMESLOT_CHANGED = "Timeslot Changed",
            SCREEN_TRANSITION = "Screen Transition",
            CHANGE_ADDRESS = "Change Address",
            PAYMENT_METHOD = "Payment method",
            METHOD_CHANGED = "Method changed",
            ADD_PAYTM_WALLET = "Add Paytm Wallet",
            REQUEST_OTP_PAYTM = "Request OTP PAYTM",
            ORDER_PLACED = "Order Placed",
            SUBMIT_FEEDBACK = "Submit Feedback",
            SKIP_FEEDBACK = "Skip Feedback",
            INVITE = "Invite",
            SUPPORT_QUERY_TICKET = "Support Query Ticket"
                    ;


    String EMAIL = "Email",
            FB = "FB",
            GOOGLE = "Google",
            YES = "YES",
            NO = "No",
            TAP = "Tap",
            SWIPE = "Swipe",
            BOTTOM_ICON = "Bottom Icon",
            POPULARITY = "Popularity",
            PRICE_LOW_TO_HIGH = "Price -Low to high",
            PRICE_HIGH_TO_LOW = "Price High to Low",
            CANCEL = "Cancel",
            FLOATING_ICON = "Floating Icon",
            A_Z = "A-Z",

    ALL = "All",
            CHECKOUT_SCREEN = "Checkout screen",
            FIRSTST = "1st",
            SECOND = "2nd",
            THIRD = "3rd",
            Payment_screen = "Payment screen",
            Address_Screen = "Address Screen",
            REVIEW_CART_SCREEN = "Review cart Screen",
            ADD_NEW_ADDRESS = "Add new Address",

    CASH = "Cash",
            RECHARGE = "Recharge",
            PAYTM = "Paytm",
            PAY_VIA_CASH = "Pay via Cash";


    String NUDGE_SIGNUP = "signup",
            NUDGE_SIGNUP_VERIFIED = "signup_verified";


    String NUDGE_PAYTM_WALLET_REMOVED = "paytm_wallet_removed",
            NUDGE_JUGNOO_FRESH_ORDER_PLACED = "jugnoo_fresh_order_placed",
            NUDGE_MENU_CLICKED = "menu_clicked",
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
            NUDGE_FRESH_ORDER_PLACED = "fresh_order_placed";

}
