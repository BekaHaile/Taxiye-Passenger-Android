package com.sabkuchfresh.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @Author Parminder Singh  on 12/08/16.
 */


public final class DateParser {

    private static final DateFormat FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private static final DateFormat FORMAT_UTC_WITHOUT_TIMEZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final DateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    public static final DateFormat DISPLAY_DATE_FORMAT_JUGNOO = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final Calendar cal = Calendar.getInstance();

    static {
        DISPLAY_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    /**
     * @param utcDate utc String Date in Format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     * @return local date String in format Aug 16, 2016
     * Example
     * UTC date 2016-08-17T22:44:28.669Z
     * return Value is Aug 18, 2016 Assuming Local time is India i.e offset +05.30
     */
    public static String getLocalDateString(String utcDate) {


        try {
            return DISPLAY_DATE_FORMAT.format(FORMAT_UTC.parse(utcDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }
    public static String getLocalDateStringFatfat(String utcDate){
        try {
            return DISPLAY_DATE_FORMAT_JUGNOO.format(FORMAT_UTC.parse(utcDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Used to to display date from reading as set in  History class object after sorting
     *
     * @param utcDate String utc date as yyyy-MM-dd'T'HH:mm:ss.SSS'Z
     * @return just converts the string into date object without changing time as per offset
     */


    public static String getLocalDateString(Date utcDate) {

        return getLocalDateString(FORMAT_UTC_WITHOUT_TIMEZONE.format(utcDate));
    }


    /**
     * @param utcDate utcDate that is to be converted to local date
     * @return returns local Date
     */
    private static Date getLocalDate(String utcDate) {


        try {
            return DISPLAY_DATE_FORMAT.parse(getLocalDateString(utcDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * @param utcDate format yyyy-MM-dd'T'HH:mm:ss.SSS'Z
     * @return String in dd-MM-yyyy format
     */
    public static String getVerificationDateString(String utcDate) {


        try {
            return DISPLAY_DATE_FORMAT.format(SIMPLE_DATE_FORMAT.parse(utcDate.substring(0, 10)));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param monthsToAdd         months to add to calendar to get the future date
     * @param lastCalibrationDate date in UTCFormat()
     */
    @SuppressWarnings("ConstantConditions")
    public static String getNextCalibrationDate(int monthsToAdd, String lastCalibrationDate) {

        if (getLocalDate(lastCalibrationDate) != null) {
            resetCalendarDate();
            cal.setTimeInMillis(getLocalDate(lastCalibrationDate).getTime());
            cal.add(Calendar.MONTH, monthsToAdd);
            return parseTimeToDisplayDate(cal.getTimeInMillis());
        }

        return null;

    }


    /**
     * @param monthsToAdd          months to add to calendar to get the future date
     * @param lastVerificationDate date in UTCFormat() which will be split and offset would be ignored
     */
    public static String getNextVerificationDate(int monthsToAdd, String lastVerificationDate) {

        try {
            resetCalendarDate();
            cal.setTimeInMillis(SIMPLE_DATE_FORMAT.parse(lastVerificationDate.substring(0, 10)).getTime());
            cal.add(Calendar.MONTH, monthsToAdd);
            return parseTimeToDisplayDate(cal.getTimeInMillis());
        } catch (ParseException e) {
            return null;
        }


    }


    /**
     * Used to set Date to History class object to sort according to dates
     *
     * @param utcDate String utc date as yyyy-MM-dd'T'HH:mm:ss.SSS'Z
     * @return just converts the string into date object without changing time as per offset
     */

    public static Date getUtcDate(String utcDate) {

        try {
            return FORMAT_UTC_WITHOUT_TIMEZONE.parse(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getUtcDateWithTimeZone(String utcDate) {

        try {
            return FORMAT_UTC.parse(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This method is specifically used to create date object by ignoring the offset
     * which is to be set in History Object
     */
    public static Date getVerificationDateO(String utcDate) {

        try {
            return DISPLAY_DATE_FORMAT.parse(DISPLAY_DATE_FORMAT.format(getUtcDate(utcDate)));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is specifically used to get String date of the date objet
     * which was set in history object in DISPLAY_DATE_FORMAT
     */
    public static String setVerificationDate(Date displayDate) {


        return DISPLAY_DATE_FORMAT.format(displayDate);
    }



    /*
        Converts Time To Date.Used to get dates from calendar after manipulation of days,months etc
     */

    private static String parseTimeToDisplayDate(long timeInMilliSec) {

        return DISPLAY_DATE_FORMAT.format(timeInMilliSec);
    }

    /**
     * resets the calendar's date to current time
     */

    private static void resetCalendarDate() {

        cal.setTimeInMillis(System.currentTimeMillis());

    }

}
