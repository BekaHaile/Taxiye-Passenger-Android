package com.sabkuchfresh.utils;

/**
 * Created by Gurmail S. Kang on 5/3/16.
 */
public class AppConstant {


    public static final String REVERSE_GEO_CODING_URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";

    public static interface AppType {
        public static final int AUTO = 0;
        public static final int FRESH = 1;
        public static final int MEALS = 2;

    }
    /**
     * Constant interface. DO NOT IMPLEMENT.
     *
     * @author Gurmail S. Kang
     */
    public static interface MenuClick {
        public static final int USERINFO = 0;
        public static final int WALLET = 1;
        public static final int HISTORY = 2;
        public static final int SUPPORT = 3;
        public static final int ABOUTUS = 4;
        public static final int NOTIFICATION_CENTER = 5;
        public static final int REFER = 6;
        public static final int FATAFAT = 11;
        public static final int MEALS = 12;
    }

    public static interface SupportType {
        public static final int SUPPORT = 0;
        public static final int HISTORY = 1;
        public static final int ABOUT = 2;
        public static final int NOTIFICATION = 3;
        public static final int SHARE = 4;
        public static final int FEED_BACK = 5;
        public static final int PROMO = 6;
    }

    public static interface EditAddressType {
        public static final int HOME = 2;
        public static final int WORK = 3;
        public static final int OTHER = 4;
    }

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
    }

    public static interface OrderType {
        public static final int FRESH = 0;
        public static final int MEALS = 1;
    }

    public static interface EventTracker {
        public static final int REVIEW_CART = 1;
        public static final int CHECKOUT = 2;
        public static final int PAYMENT = 3;
        public static final int ORDER_PLACED = 4;

    }

    public static interface DeepLinking {
        public static final int History_SCREEN = 1;
        public static final int NOTIFICATION_SCREEN = 2;
        public static final int SHARE_SCREEN = 3;
        public static final int Wallet_SCREEN = 4;
        public static final int PLAY_Store = 5;
        public static final int USER_PROFILE = 6;
//        public static final int PAYMENT = 7;
//        public static final int PAYMENT = 8;
//        public static final int PAYMENT = 9;
    }


}
