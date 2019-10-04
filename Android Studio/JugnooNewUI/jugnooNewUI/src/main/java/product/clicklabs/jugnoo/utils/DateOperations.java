package product.clicklabs.jugnoo.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * date and time related operations functions
 * @author shankar
 *
 */
@SuppressLint("SimpleDateFormat")
public class DateOperations {

	private static Calendar indianTimeCalendar = Calendar.getInstance();
	private static final DateFormat FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
	/*private static final DateFormat FORMAT_UTC_LOCAL = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

	static {
		FORMAT_UTC_LOCAL.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
	}
	public static Calendar getIndianTimeCalendar(){
		indianTimeCalendar = Calendar.getInstance();
		indianTimeCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		indianTimeCalendar.setTimeInMillis(System.currentTimeMillis());
		return indianTimeCalendar;

	}*/



	public static Date getDateFromString(String dfWithTimeZone)  {
		try {
			return FORMAT_UTC.parse(dfWithTimeZone);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}



	public DateOperations(){
		
	}

	/**
	 * Converts UTC time to local time
	 * @param utcTime UTC time String
	 * @return Local time String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String utcToLocal(String utcTime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date myDate = simpleDateFormat.parse(utcTime);
			String localDate = sdf.format(myDate);
			return localDate;
		} catch (Exception e1) {
			e1.printStackTrace();
			return utcTime;
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String localToUTC(String localTime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date myDate = simpleDateFormat.parse(localTime);
			String localDate = sdf.format(myDate);
			return localDate;
		} catch (Exception e1) {
			e1.printStackTrace();
			return localTime;
		}
	}

	//2015-05-08T10:29:52.000Z
	public static String utcToLocalTZ(String utcTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			utcTime = utcTime.replace("T", " ");
			utcTime = utcTime.split("\\.")[0];
			Date myDate = simpleDateFormat.parse(utcTime);
			String localDate = sdf.format(myDate);
			return localDate;
		} catch (Exception e1) {
			e1.printStackTrace();
			return utcTime;
		}
	}

	
	public static Calendar getCalendarFromTimeStamp(String timeStamp){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(timeStamp);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			return calendar;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts date string from 2014-01-12 00:00 to 12 Jan, 2014 12:00 AM
	 * @param dateTime 
	 * @return
	 */
	public static String convertDate(String dateTime){
		try{
		String date = dateTime.split(" ")[0];
		
		String year = date.split("-")[0];
		String month = date.split("-")[1];
		String day = date.split("-")[2];
		
		
		if("01".equalsIgnoreCase(month)){
			month = "Jan";
		}
		else if("02".equalsIgnoreCase(month)){
			month = "Feb";
		}
		else if("03".equalsIgnoreCase(month)){
			month = "Mar";
		}
		else if("04".equalsIgnoreCase(month)){
			month = "Apr";
		}
		else if("05".equalsIgnoreCase(month)){
			month = "May";
		}
		else if("06".equalsIgnoreCase(month)){
			month = "Jun";
		}
		else if("07".equalsIgnoreCase(month)){
			month = "Jul";
		}
		else if("08".equalsIgnoreCase(month)){
			month = "Aug";
		}
		else if("09".equalsIgnoreCase(month)){
			month = "Sept";
		}
		else if("10".equalsIgnoreCase(month)){
			month = "Oct";
		}
		else if("11".equalsIgnoreCase(month)){
			month = "Nov";
		}
		else if("12".equalsIgnoreCase(month)){
			month = "Dec";
		}
		
		
		String time = dateTime.split(" ")[1];
		
		String hour = time.split(":")[0];
		String min = time.split(":")[1];
		
		String amOrpm = Integer.parseInt(hour)>=12?"PM":"AM";
		String newHour = "";
		
		if(Integer.parseInt(hour)==0){
			newHour = "12";
		}
		else if(Integer.parseInt(hour)<=12){
			newHour = hour;
		}
		else{
			newHour = ""+(Integer.parseInt(hour) - 12);
		}
		
		String finalTime = ""+ newHour + ":" + min + " " + amOrpm; 
		
		
		String finalDate = ""+ day + " " + month + ", " + year + " " + finalTime; 
		
		return finalDate;
		}
		catch(Exception e){
			e.printStackTrace();
			return dateTime;
		}
	}

	/**
	 * Converts date string from 2014-01-12 00:00 to 12 Jan, 2014 12:00 AM
	 * @param dateTime
	 * @return
	 */
	public static String convertDateViaFormat(String dateTime) {

		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy h:mm a");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return convertDate(dateTime);
		}
	}

	public static String convertDateViaFormatTZ(String dateTime) {

		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy h:mm a");
		try {
			dateTime = dateTime.replace("T", " ").split("\\.")[0];
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return convertDate(dateTime);
		}
	}

	public static String convertDateViaFormatOnlyTime(String dateTime) {

		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("hh:mm a");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return convertDate(dateTime);
		}
	}

	public static String convertDateOnlyViaFormat(String dateTime) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return convertDate(dateTime);
		}
	}

	public static String convertDateOnlyViaFormatMonthFull(String dateTime) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMMM, yyyy");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return convertDate(dateTime);
		}
	}

	public static String convertDateOnlyViaFormatSlash(String dateTime) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return convertDate(dateTime);
		}
	}
	
	public static String getTimeAMPM(String dateTime){
		try{
            String time = dateTime.split(" ")[1];

            String hour = time.split(":")[0];
            String min = time.split(":")[1];

            String amOrpm = Integer.parseInt(hour)>=12?"PM":"AM";
            String newHour = "";

            if(Integer.parseInt(hour)==0){
                newHour = "12";
            }
            else if(Integer.parseInt(hour)<=12){
                newHour = hour;
            }
            else{
                newHour = ""+(Integer.parseInt(hour) - 12);
            }

            String finalTime = ""+ newHour + ":" + min + " " + amOrpm;

            return finalTime;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	

	
	public static String getCurrentTime() {
	    long foo = System.currentTimeMillis();
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	public static String getCurrentTimeInUTC() {
	    long foo = System.currentTimeMillis();
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	    return formatter.format(date);
	}
	
	
	public static String getTimeStampfromCalendar(Calendar calendar) {
	    long foo = calendar.getTimeInMillis();
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	
	
	
	
	
	
	public String getSixtySecAfterCurrentTime() {
	    long foo = System.currentTimeMillis() + 60000;
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	
	public static long getTimeDifference(String time1, String time2){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(time1);
			Date date2 = format.parse(time2);
			long millis = date1.getTime() - date2.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 60000;
	}

	public static long getTimeDifferenceInHHMM(String time1, String time2){
		try {
			SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
			Date date1 = format.parse(time1);
			Date date2 = format.parse(time2);
			long millis = date1.getTime() - date2.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 60000;
	}

	/*public static long getTimeInMillis(String time){
		long millis;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return millis;
	}*/

    public static long getTimeDifference(long time1, long time2){
        try {
            Date date1 = new Date(time1);
            Date date2 = new Date(time2);
            long millis = date1.getTime() - date2.getTime();
            return millis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 60000;
    }
	
	public static long getMilliseconds(String time1){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(time1);
			long millis = date1.getTime();
			return millis;
		} catch (Exception e) {
			try {
				time1 = time1.replace("T", " ");
				time1 = time1.split("\\.")[0];
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date1 = format.parse(time1);
				long millis = date1.getTime();
				return millis;
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		return 60000;
	}


    public static String getUTCTimeInLocalTimeStamp(String utcTime){
        try {
            long foo = System.currentTimeMillis();
            Date date = new Date(foo);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcDate = formatter.format(date);

            String utcTimeStamp = utcDate + " " + utcTime;
            String localTimeStamp = utcToLocal(utcTimeStamp);
            return localTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            long foo = System.currentTimeMillis();
            Date date = new Date(foo);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcDate = formatter.format(date);
            String utcTimeStamp = utcDate + " " + utcTime;
            return utcTimeStamp;
        }
    }

    public static String getUTCTimeInLocalTime(String utcTime){
        try {
            long foo = System.currentTimeMillis();
            Date date = new Date(foo);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcDate = formatter.format(date);

            String utcTimeStamp = utcDate + " " + utcTime;
            String localTimeStamp = utcToLocal(utcTimeStamp);
            return getTimeAMPM(localTimeStamp);
        } catch (Exception e) {
            e.printStackTrace();
            return utcTime;
        }
    }




	@SuppressLint("SimpleDateFormat")
	public static String utcToLocalWithTZFallback(String utcTime) {
		if(utcTime.contains("T")){
			return utcToLocalTZ(utcTime);
		} else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				Date myDate = simpleDateFormat.parse(utcTime);
				String localDate = sdf.format(myDate);
				return localDate;
			} catch (Exception e1) {
				e1.printStackTrace();
				return utcToLocalTZ(utcTime);
			}
		}
	}


	public static String getTimeStampUTCFromMillis(long millis){
		Date date = new Date(millis);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.format(date);
	}

	public static long getDayTimeSeconds(String hrTime) {
		try{
			String[] arr = hrTime.split(":");
			if(arr.length >= 3){
				long hrSec = Long.parseLong(arr[0]) * 60l * 60l;
				long minSec = Long.parseLong(arr[1]) * 60;
				long sec = Long.parseLong(arr[2]);
				return hrSec + minSec + sec;
			} else{
				throw new Exception();
			}
		} catch(Exception e){
			e.printStackTrace();
			return 24l * 60l * 60l;
		}
	}

	public static String convertDayTimeAPViaFormat(String dateTime, boolean showFullHours) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdfTo = new SimpleDateFormat((showFullHours?"h":"") + "h:mm a");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return dateTime;
		}
	}

public static String getAmPmFromServerDateFormat(String dateTime) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat sdfTo = new SimpleDateFormat("h:mm a");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return dateTime;
		}
	}




	public static long getTimeDifferenceInHHmmss(String time1, String time2){
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			Date date1 = format.parse(time1);
			Date date2 = format.parse(time2);
			long millis = date1.getTime() - date2.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 60000;
	}

	public static int getCurrentDayInt(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK)-1;
	}

	public static long getCurrentDayTimeSeconds(){
		Calendar calendar = Calendar.getInstance();
		long hrSec = ((long)calendar.get(Calendar.HOUR_OF_DAY)) * 60l * 60l;
		long minSec = ((long)calendar.get(Calendar.MINUTE)) * 60l;
		return hrSec + minSec + ((long)calendar.get(Calendar.SECOND));
	}

	public static String getDateFormatted(String dateYYYYMMDD) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy");
		try {
			Date myDate = sdfFrom.parse(dateYYYYMMDD);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return dateYYYYMMDD;
		}
	}

	public static String addCalendarFieldValueToDateTime(String dateTime, int addition, int calendarField){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(dateTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			calendar.add(calendarField, addition);
			return format.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return dateTime;
		}
	}

	public static int getTimezoneDiffWithUTC(){
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		return tz.getOffset(now.getTime()) / 60000;
	}

	public static String convertDateTimeUSToInd(String dateTime) {
		SimpleDateFormat sdfFrom = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat sdfTo = new SimpleDateFormat("dd MMM, yyyy h:mm a");
		try {
			Date myDate = sdfFrom.parse(dateTime);
			return sdfTo.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			return dateTime;
		}
	}

	public static String getDaysAheadTime(String dateTime, int additionOfDays){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(dateTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			calendar.add(Calendar.DAY_OF_MONTH, additionOfDays);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);

			return format.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return dateTime;
		}
	}
	public static String getDaysAheadTimeMinute(String dateTime, int additionOfDays){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(dateTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.add(Calendar.MINUTE, 30);

			return format.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return dateTime;
		}
	}
}
