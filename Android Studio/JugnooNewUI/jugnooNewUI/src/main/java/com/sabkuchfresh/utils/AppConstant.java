package com.sabkuchfresh.utils;

/**
 * Created by Gurmail S. Kang on 5/3/16.
 */
public class AppConstant {

    public interface ListType {
        public static final int HOME = 0;
        public static final int OTHER = 1;
    }

    public interface IsFatafatEnabled {
        public static final int NOT_ENABLED = 0;
        public static final int ENABLED = 1;
    }

    public static interface ApplicationType {
        public static final int FRESH = 1;
        public static final int MEALS = 2;
        public static final int GROCERY = 3;
        int MENUS = 4;
        int PAY = 5;
        int FEED =  6;
        int PROS =  7;
        int DELIVERY_CUSTOMER =  8;
    }

    public static interface EventTracker {
        public static final int REVIEW_CART = 1;
        public static final int CHECKOUT = 2;
        public static final int PAYMENT = 3;
        public static final int ORDER_PLACED = 4;

    }

    public static String SENT_TOKEN_TO_SERVER = "SendTokenToServer";
    public static String CONTACT_DATA = "contact_data";
    public static String REQUEST_STATUS = "request_status";
    public static String REQUEST_STATUS_CONFIRMATION = "request_status_confirmation";
    public static String TRANSACTION_DATA = "transc_data";
    public static String SEND_TRANSACTION_DATA = "send_transc_data";
    public static String PENDING_TRANSACTION_STATUS= "pending_transc_status";
    public static String ORDER_ID = "OrderId";
    public static String TRANSACTION_STATUS = "transaction_status";
    public static String URL = "url";

    // custom dialog strings

}
