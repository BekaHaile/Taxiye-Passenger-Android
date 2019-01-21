package com.fugu.utils;

import android.os.SystemClock;

import java.util.regex.Pattern;

/**
 * Created by gurmail on 08/05/18.
 *
 * @author gurmail
 */

public class Utils {

    /**
     * Method to check whether a String is number
     *
     * @param value
     */
    public static boolean isNumeric(String value) {

        if (value == null)
            return false;

        return Pattern.matches("-?\\d+(\\.\\d+)?", value);
    }

    /**
     * Validates the character sequence with email format
     *
     * @param email
     * @return true, if the string entered by user is syntactically correct as
     * email, false otherwise
     */
    public static boolean isEmailValid(String email) {

        // Check whether the Email is valid
        if (email == null) return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public final static boolean isValidPhoneNumber(CharSequence target) {
        if (target == null) {
            return false;
        } else {

            if (target.length() < 6 || target.length() > 14) {
                return false;
            } else {
                return android.util.Patterns.PHONE.matcher(target).matches();
            }
        }
    }


    private static final int    MULTIPLE_CLICK_THRESHOLD       = 2500; //in milli seconds
    // variable to track event time
    private static long mLastClickTime = 0;

    /**
     * Method to prevent multiple clicks
     */
    public static boolean preventMultipleClicks() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MULTIPLE_CLICK_THRESHOLD) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }
}
