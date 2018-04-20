package com.jugnoo.pay.utils;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;


/**
 * Created by root on 19/4/16.
 */
public class CommonMethods {
    public static boolean isInternetAvailable(Context  context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                    && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
            } else {
                return false;
            }
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
    }


    public static String getDeviceID(Context context)
    {
//        return  Settings.Secure.getString(context.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
        return Prefs.with(context).getString(SharedPreferencesName.DEVICE_TOKEN, "");
    }




    public static void hideKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(
                        context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (((Activity) context).getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(((Activity) context)
                        .getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
    public static void startFragmentTransaction(AppCompatActivity appCompatActivity, Fragment fragment, String tag, int container) {
        try {
            FragmentManager mFragmentManager = appCompatActivity.getSupportFragmentManager();
            Fragment fragmentFromBackStack = mFragmentManager.findFragmentByTag(tag);
            if (fragmentFromBackStack == null) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(container, fragment, tag);
//                fragmentTransaction.addToBackStack(tag);
                fragmentTransaction.commit();
            } else {
                mFragmentManager.popBackStack(tag, 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void displaySnakbar(RelativeLayout layout, String message){
//        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar
                .make(layout,message, Snackbar.LENGTH_LONG);
//        View view = snackbar.getView();
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
//        params.gravity = Gravity.BOTTOM;
//        view.setLayoutParams(params);
        snackbar.show();




    }


    public static void displayToast(Context ctx, String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();

    }

    // Check permissions

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(context.getString(R.string.permission_necessary));
                    alertBuilder.setMessage(context.getString(R.string.external_storage_permission_is_necessary));
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        }
                    });

                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }






    }



//    // get Country Code
//    public static String getCountryCode(Activity ctx)
//    {
//        try {
//            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
//            String countryCodeValue = tm.getNetworkCountryIso();
//            if (countryCodeValue == null || countryCodeValue.length() == 0) {
////                countryCodeValue = ctx.getResources().getConfiguration().locale.getCountry();
//               return "+1";
//            }
//            else {
//                System.out.println("code== " + countryCodeValue);
////                Integer countryCode[]=new Integer[Countries.COUNTRIES.size()];
////                for(int l=0;l<Countries.COUNTRIES.size();l++)
////                {
////
////                     countryCode[l] = Countries.COUNTRIES.get(0).getDialCode();
////                }
//
////                String countryCode[] = ctx.getResources().getStringArray(R.array.CountryCodes);
//                String code = null;
//                for (int i = 0; i < Countries.COUNTRIES.size(); i++) {
////                    String arr[] = countryCode[i].split(",");
//                    if (countryCodeValue.equalsIgnoreCase(Countries.COUNTRIES.get(i).getCode())) {
//                        code = "+" + Integer.toString(Countries.COUNTRIES.get(i).getDialCode());
//                        break;
//                    }
//                }
//
//                if (code == null)
//                    return "+1";
//                else
//                    return code;
//            }
//        }
//        catch (Exception e)
//        {
//            return "+1";
//        }
//    }


    // get address from latitude and longitude

    public static Address getAddressFromLatLng(AppCompatActivity ctx, double lat, double lng)
    {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(ctx, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (Exception e) {

            return null;

        }


    }


//    public static String capEachWord(String source) {
//        String result = "";
//        Log.d("Tag", "dsf");
//        String[] splitString = source.split(" ");
//        for (String target : splitString) {
//            result += Character.toUpperCase(target.charAt(0))
//                    + target.substring(1).toLowerCase() + " ";
//        }
//        return result.trim();
//    }
    public static String capEachWord(String source) {
        try {
            source.trim();
            if (source.length() > 0) {

                String result = "";
                Log.d("Tag", "dsf");
                String[] splitString = source.split(" ");
//                if (splitString.length > 1) {
                for (String target : splitString) {
                    if(target!=null&&!target.equals("")){

                        result += Character.toUpperCase(target.charAt(0))
                                + target.substring(1).toLowerCase() + " ";
                    }

                }
                return result.trim();
//                } else
//                    return "" + Character.toUpperCase(source.charAt(0));
            }
            return "";
        }
        catch (Exception e)
        {
            System.out.println("Exc== "+e.toString());
            return ""+source;
        }
    }



    public static void checkGPSStatus(final Activity ctx) {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if ( locationManager == null ) {
            locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex){}
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex){}
        if ( !gps_enabled && !network_enabled ){
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
            dialog.setMessage(ctx.getString(R.string.turn_on_location_service));
            dialog.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ctx.startActivity(intent);
                }
            });
            dialog.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ctx.onBackPressed();
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }


    public static Date parseDate(String d)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date result;
        try {
            result = df.parse(d.replaceAll("Z$", "+0000"));
            System.out.println("date:"+result); //prints date in current locale
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            System.out.println("date== "+result.getTime());

            System.out.println(sdf.format(result)); //prints date in the format sdf
            return  result;

        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return null;

        }

    }


    public static String parseOnlyDate(String d)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date result;
        try {
            result = df.parse(d.replaceAll("Z$", "+0000"));
            System.out.println("date:"+result); //prints date in current locale
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            System.out.println("date== "+result.getTime());

            System.out.println(sdf.format(result)); //prints date in the format sdf
            return  sdf.format(result);

        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return null;

        }

    }



    public static String getDate(Date result)
    {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(result);

        if(date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMM yyyy");
        else if(date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMM yyyy");
        else if(date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMM yyyy");
        else
            format = new SimpleDateFormat("d'th' MMM yyyy");

        String yourDate = format.format(result);
        System.out.println("your date== "+yourDate);


        return  yourDate;
    }


    public static String getDatePickerTypeDate(Date result)
    {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(result);

        if(date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("EEE, d'st' MMM yyyy");
        else if(date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("EEE, d'nd' MMM yyyy");
        else if(date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("EEE, d'rd' MMM yyyy");
        else
            format = new SimpleDateFormat("EEE, d'th' MMM yyyy");

        String yourDate = format.format(result);
        System.out.println("your date picker type == "+yourDate);


        return  yourDate;
    }


   public static String getTime(Date result)
    {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        sdf1.setTimeZone(TimeZone.getDefault());

        System.out.println("date== "+result.getTime());

        System.out.println(sdf1.format(result));
        StringTokenizer tk = new StringTokenizer(sdf1.format(result));
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa");
        Date dt;
        try {
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return    sdfs.format(dt);
    }



    public static String formatDate(String date) {

        Date tmpDate = null;
        try {
            SimpleDateFormat formatFrom;
            if(date.contains("1st")||date.contains("21st")||date.contains("31st"))
                formatFrom = new SimpleDateFormat("hh:mm a, EEE, d'st' MMM yyyy");
            else if(date.contains("2nd")||date.contains("22nd"))
                formatFrom = new SimpleDateFormat("hh:mm a, EEE, d'nd' MMM yyyy");
                else if (date.contains("3rd")||date.contains("23rd"))
                formatFrom = new SimpleDateFormat("hh:mm a, EEE, d'rd' MMM yyyy");
                else
             formatFrom = new SimpleDateFormat("hh:mm a, EEE, d'th' MMM yyyy");

            tmpDate = formatFrom.parse(date);
            SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return formatTo.format(tmpDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }


    public static String getTimeWithoutSec(String oldDate)
    {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String d = oldDate.replace("T"," ");
        StringTokenizer tk = new StringTokenizer(d);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm");
        Date dt;
        try {
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        return    (sdfs.format(dt)).trim().replace("PM","");
        return    (sdfs.format(dt).replace(":","."));
    }

    public static String getTimeAmFormat(String oldDate)
    {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String d = oldDate.replace("T"," ");
        StringTokenizer tk = new StringTokenizer(d);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa");
        Date dt;
        try {
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        return    (sdfs.format(dt)).trim().replace("PM","");
        return    (sdfs.format(dt));
    }



    public static String getRoundOfTime(String startTime, int addTime){
        String dd = startTime.replace("T"," ");
        StringTokenizer tk = new StringTokenizer(dd);
        String date = tk.nextToken();
        String time = tk.nextToken();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm:ss");
        Date dt;
        try {
            dt = sdf.parse(time);
            //System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
            Calendar cal = Calendar.getInstance();
            //cal.add(Calendar.MINUTE, mod < 8 ? -mod : (15-mod));
            cal.setTime(dt);

            int unroundedMinutes = cal.get(Calendar.MINUTE);
            int mod = unroundedMinutes % 15;
            //cal.set(Calendar.MINUTE, unroundedMinutes + mod);
            //cal.add(Calendar.MINUTE, 30);
//            cal.setTimeZone(TimeZone.getDefault());
            cal.add(Calendar.MINUTE, mod < 8 ? -mod : (15-mod));
            cal.add(Calendar.MINUTE,addTime);
            dt = cal.getTime();
           Log.e("nowwwww","==="+dt);
            return date+"T"+sdfs.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDateFromUTC(String OurDate)
    {



        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy, hh:mm aa"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            //Log.d("OurDate", OurDate);
        }
        catch (Exception e)
        {
            OurDate = "00/00/0000, 00:00";
        }
        return OurDate;
    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void clearSingleNotifications(Context context,int number) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(number);
    }


    public static void callingBadToken(final AppCompatActivity ctx, int statusCode,String msg)
    {
       final int  AUTH_DUPLICATE_REGISTRATIONS =  400,
                AUTH_REGISTRATION_SUCCESSFUL=401,
                AUTH_REGISTRATION_FAILURE=     402,
                AUTH_ALREADY_REGISTERED=       403,
                AUTH_NOT_REGISTERED=           404,
                AUTH_VERIFICATION_REQUIRED=    405,
                AUTH_VERIFICATION_FAILURE=     406,
                AUTH_LOGIN_SUCCESSFUL=         407,
                AUTH_LOGIN_FAILURE=            408,
                AUTH_LOGOUT_SUCCESSFUL=        409,
                AUTH_LOGOUT_FAILURE=           410,
                AUTH_REFERRAL_UNSUCCESSFUL=    411,
                AUTH_REFERRAL_SUCCESSFUL=      412,
                AUTH_PASSWORD_RESET_FAILURE=   413,
                AUTH_PASSWORD_RESET_SUCCESS=   414,
                PROFILE_INFORMATION=           416,
                AUTH_USER_MAPPING_ID=          417,
                PAYMENT_INFORMATION_WRONG=     420,
                PAYMENT_UPDATE_COMPLETE=       421,
                PAYMENT_UPDATE_FAILURE=        422,
                TRANSACTION_HISTORY=           423,
                TRANSACTION_FAILED=            424,
                TRANSACTION_COMPLETE=          425,
                WALLET_BALANCE=                426,
                INSUFFICIENT_FUNDS=            427,
                INCOMPLETE_REGISTRATION=       430,
                INVALID_ACCESS_TOKEN =         101,
                DUPLICATE_EMAIL         =      802,

                PAY_REGISTRATION_SUCCSSFUL= 800,
                TOKEN_EXPIRED = 803,
                JUGNOO_PAY_VERIFICATION_REQUIRED= 432,

                TXN_ALREADY_EXISTS= 301,
                TXN_INITIATED= 302,

                SILENT_PUSH = 51;

        try{

            switch (statusCode)
            {
                case INVALID_ACCESS_TOKEN:
                    // ctx.startActivity(new Intent(ctx, SignInActivity.class));
                    ctx.startActivity(new Intent(ctx, SplashNewActivity.class));
                        ctx.finish();
                    break;

                case TOKEN_EXPIRED:AUTH_ALREADY_REGISTERED:AUTH_LOGIN_FAILURE:
                    DialogPopup.alertPopupWithListener(ctx, "", msg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ctx.finish();
                        }
                    });
                    break;
                case AUTH_NOT_REGISTERED:
                    DialogPopup.alertPopupTwoButtonsWithListeners(ctx, "", msg,
                            ctx.getString(R.string.register),
                            ctx.getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new HomeUtil().logoutFunc(ctx, "");
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, true, false);
                        break;
                case AUTH_ALREADY_REGISTERED:
                    DialogPopup.alertPopupWithListener(ctx, "", msg, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ctx.finish();
                        }
                    });

                    break;

                  default:
                    DialogPopup.alertPopup(ctx, "", msg);
                    break;

            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void  openUrl(Context ctx, String mUri){
        try {
            Uri uri = Uri.parse(mUri); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUniqueDeviceId(Context ctx)
    {
         String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }


    public  static String extractNumber(final String str) {

        if(str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for(char c : str.toCharArray()){
            if(Character.isDigit(c) || c == '+'){
                sb.append(c);
                found = true;
            } else if(found){
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }

        return sb.toString();
    }



}

