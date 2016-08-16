package com.sabkuchfresh.config;


import android.content.Context;

import com.sabkuchfresh.datastructure.SPLabels;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Prefs;


public class Config {

    public static final String GOOGLE_GEOCODE_API_BASEURL = "https://maps.googleapis.com/maps/api/";

    // final variables
    private static String SUPPORT_NUMBER = "+919023121121";
    private static final String DEBUG_PASSWORD = "098673";

    private static final String DOWNLOAD_SOURCE = "playstore";

    private static final String FABRIC_APIKEY = "f96b4d9d9886e413041e7b14fa52434b87709356";

    private static final String CLIENT_ID = "FHkmrtv6zn0KuGcW";
//    private static final String CLIENT_SHARED_SECRET = "fMxVVNgUl5XV9bzr12T1xtiQfxaAblXf";

//    private static final String AUTO_CLIENT_ID = "EEBUOvQq7RRJBxJm";
    private static final String CLIENT_SHARED_SECRET = "nqaK7HTwDT3epcpR5JuMWwojFv0KJnIv";

    private static final String GOOGLE_PROJECT_ID = "805009535154";
    private static final String MAPS_BROWSER_KEY = "AIzaSyCQ2JkACvuWeMK0P10V0S05-okfmveKEis";
//    private static final String FACEBOOK_APP_ID = "779502785483550";

    private static final String NEW_RELIC_KEY = "AA75d379d0492f03b9f38463843130f74895c9ca7c";



    private static final String STATIC_FLURRY_KEY = "H8Y94ND8GPQTKKG5R2VY";





    private static final String LEGACY_SERVER_URL = "https://api-autos.jugnoo.in:4012";
    // https://prod-autos-api.jugnoo.in
    // https://api-autos.jugnoo.in:4012

    private static final String DEV_1_SERVER_URL = "https://test.jugnoo.in:8013";
    private static final String DEV_2_SERVER_URL = "https://test.jugnoo.in:8014";
    private static final String DEV_3_SERVER_URL = "https://test.jugnoo.in:8015";
	//https://172.168.1.14:8012





    private static final String FRESH_DEV_SERVER_URL = "https://test.jugnoo.in:8060";
    private static final String FRESH_LIVE_SERVER_URL = "https://prod-fresh-api.jugnoo.in:4009";


    private static final String DEV_SERVER_URL = "https://test.jugnoo.in:8012"; //8060, 8012
    private static final String LIVE_SERVER_URL = "https://prod-autos-api.jugnoo.in";




    private static final String DEFAULT_SERVER_URL = LIVE_SERVER_URL ;

    private static ConfigMode configMode = ConfigMode.LIVE;



	public static String CUSTOM_SERVER_URL = "";


    // modifiable static variables
    private static String SERVER_URL = DEFAULT_SERVER_URL;

    private static String PAYMENT_URL = "";
    private static String TXN_URL = "";
    private static String SURL = "";
    private static String FURL = "";

    private static int MAX_AMOUNT = 5000;
    private static int MIN_AMOUNT = 100;


    /**
     * Initialize all the variable in this method
     *
     * @param appMode
     */
    public static void init(ConfigMode appMode) {

        switch (appMode) {
            case DEV:

                SERVER_URL = DEV_SERVER_URL;
                Log.PRINT = true;

                PAYMENT_URL = "https://test.jugnoo.in/jugnoo/wallet/payments_seamless.php";
				TXN_URL = SERVER_URL;
                SURL = "https://test.jugnoo.in/jugnoo/wallet/success.php";
                FURL = "https://test.jugnoo.in/jugnoo/wallet/failure.php";

                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;


            case LIVE:

                SERVER_URL = LIVE_SERVER_URL;
                Log.PRINT = false;

                PAYMENT_URL = "https://dev.jugnoo.in/jugnoo-phpfiles/wallet/payments.php";
				TXN_URL = SERVER_URL;
                SURL = "https://dev.jugnoo.in/jugnoo-phpfiles/wallet/success.php";
                FURL = "https://dev.jugnoo.in/jugnoo-phpfiles/wallet/failure.php";

                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;


            case DEV_1:

                SERVER_URL = DEV_1_SERVER_URL;
                Log.PRINT = true;

                PAYMENT_URL = "https://test.jugnoo.in/jugnoo/wallet/payments_seamless.php";
				TXN_URL = SERVER_URL;
                SURL = "https://test.jugnoo.in/jugnoo/wallet/success.php";
                FURL = "https://test.jugnoo.in/jugnoo/wallet/failure.php";

                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;

            case DEV_2:

                SERVER_URL = DEV_2_SERVER_URL;
                Log.PRINT = true;

                PAYMENT_URL = "https://test.jugnoo.in/jugnoo/wallet/payments_seamless.php";
				TXN_URL = SERVER_URL;
                SURL = "https://test.jugnoo.in/jugnoo/wallet/success.php";
                FURL = "https://test.jugnoo.in/jugnoo/wallet/failure.php";

                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;

            case DEV_3:

                SERVER_URL = DEV_3_SERVER_URL;
                Log.PRINT = true;

                PAYMENT_URL = "https://test.jugnoo.in/jugnoo/wallet/payments_seamless.php";
				TXN_URL = SERVER_URL;
                SURL = "https://test.jugnoo.in/jugnoo/wallet/success.php";
                FURL = "https://test.jugnoo.in/jugnoo/wallet/failure.php";

                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;

			case CUSTOM:

				SERVER_URL = CUSTOM_SERVER_URL;
				Log.PRINT = true;

				PAYMENT_URL = "https://test.jugnoo.in/jugnoo/wallet/payments_seamless.php";
				TXN_URL = SERVER_URL;
				SURL = "https://test.jugnoo.in/jugnoo/wallet/success.php";
				FURL = "https://test.jugnoo.in/jugnoo/wallet/failure.php";

				MAX_AMOUNT = 5000;
				MIN_AMOUNT = 1;

				break;

        }


    }




    // modifiable fields

    public static ConfigMode getConfigMode() {
        return configMode;
    }

    public static void setConfigMode(ConfigMode configMode) {
        Config.configMode = configMode;
        init(configMode);
    }

    public static String getFlurryKey() {
        return STATIC_FLURRY_KEY;
    }

    public static String getServerUrl() {
        init(configMode);
        return SERVER_URL;
    }




    public static String getSURL() {
        init(configMode);
        return SURL;
    }

    public static String getFURL() {
        init(configMode);
        return FURL;
    }

    public static String getPAYMENT_URL() {
        init(configMode);
        return PAYMENT_URL;
    }

    public static String getTXN_URL() {
        init(configMode);
        return TXN_URL;
    }


    public static int getMinAmount() {
        init(configMode);
        return MIN_AMOUNT;
    }


    public static int getMaxAmount() {
        init(configMode);
        return MAX_AMOUNT;
    }



    public static String getServerUrlName() {
        init(configMode);
        if(SERVER_URL.equalsIgnoreCase(LIVE_SERVER_URL)){
            return "LIVE";
        }
        else if(SERVER_URL.equalsIgnoreCase(DEV_1_SERVER_URL)){
            return "DEV_1";
        }
        else if(SERVER_URL.equalsIgnoreCase(DEV_2_SERVER_URL)){
            return "DEV_2";
        }
        else if(SERVER_URL.equalsIgnoreCase(DEV_3_SERVER_URL)){
            return "DEV_3";
        }
		else if(SERVER_URL.equalsIgnoreCase(DEV_SERVER_URL)){
			return "DEV";
		}
        else{
            return "CUSTOM";
        }
    }












    // final feilds

    public static String getSupportNumber(Context context) {
		SUPPORT_NUMBER = Prefs.with(context).getString(SPLabels.SUPPORT_CONTACT, SUPPORT_NUMBER);
        return SUPPORT_NUMBER;
    }

	public static void saveSupportNumber(Context context, String supportContact){
		Prefs.with(context).save(SPLabels.SUPPORT_CONTACT, supportContact);
		SUPPORT_NUMBER = supportContact;
	}

    public static String getDebugPassword() {
        return DEBUG_PASSWORD;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getClientSharedSecret() {
        return CLIENT_SHARED_SECRET;
    }

    public static String getGoogleProjectId() {
        return GOOGLE_PROJECT_ID;
    }

//    public static String getMapsBrowserKey() {
//        return MAPS_BROWSER_KEY;
//    }

//    public static String getFacebookAppId() {
//        return FACEBOOK_APP_ID;
//    }


    public static String getDevServerUrl() {
        return DEV_SERVER_URL;
    }

    public static String getDev1ServerUrl() {
        return DEV_1_SERVER_URL;
    }

    public static String getDev2ServerUrl() {
        return DEV_2_SERVER_URL;
    }

    public static String getDev3ServerUrl() {
        return DEV_3_SERVER_URL;
    }

    public static String getLiveServerUrl() {
        return LIVE_SERVER_URL;
    }

    public static String getLegacyServerUrl() {
        return LEGACY_SERVER_URL;
    }

    public static String getDefaultServerUrl() {
        return DEFAULT_SERVER_URL;
    }

    public static String getNewRelicKey() {
        return NEW_RELIC_KEY;
    }

    public static String getDownloadSource() {
        return DOWNLOAD_SOURCE;
    }

    public static String getFreshServerUrl() {
        if(configMode == ConfigMode.LIVE){
            return FRESH_LIVE_SERVER_URL;
        } else{
            return FRESH_DEV_SERVER_URL;
        }
    }
}