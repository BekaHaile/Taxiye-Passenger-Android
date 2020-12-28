package product.clicklabs.jugnoo.config;


import android.content.Context;
import android.text.TextUtils;

import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;


public class Config {

    public static final String AUTOS_CLIENT_ID = "EEBUOvQq7RRJBxJm";
    public static final String FRESH_CLIENT_ID = "FHkmrtv6zn0KuGcW";
    public static final String MEALS_CLIENT_ID = "MbxtwTaDolAD7cow";
    public static final String GROCERY_CLIENT_ID = "GhQXJfcgeY8zmZT8";
    public static final String MENUS_CLIENT_ID = "MEgLeJgyr1gwfv1D";
    public static final String PAY_CLIENT_ID = "PkjfJf27lkUvjKT9";
    public static final String DELIVERY_CLIENT_ID = "<needed from server>";
    public static final String FEED_CLIENT_ID = "F20A9fb009e282F1";
    public static final String PROS_CLIENT_ID = "PR03274bbf955caF";
    public static final String DELIVERY_CUSTOMER_CLIENT_ID = "DC311uhPSZV6tKjT";




    private static final String DEBUG_PASSWORD = BuildConfig.DEBUG_CODE;

    private static final String DOWNLOAD_SOURCE = "playstore";



    private static final String CLIENT_SHARED_SECRET = "nEkVmQh2771MvzGAsKxUUbISgHuCz1zZWfKFywMXCCUt7";



    private static final String STATIC_FLURRY_KEY = "";



    private static final String DEV_SERVER_URL = ":8012";
    private static final String LIVE_SERVER_URL = BuildConfig.LIVE_URL;

    private static final String LEGACY_SERVER_URL = ":4012";

    private static final String DEV_1_SERVER_URL = ":8013";
    private static final String DEV_2_SERVER_URL = ":8014";
    private static final String DEV_3_SERVER_URL = ":8015";


    private static final String MAPS_CACHING_SERVER_URL = BuildConfig.MAPS_CACHING_SERVER_URL;
    private static final String JUNGLE_MAPS_SERVER_URL = "";

    private static final String DEFAULT_SERVER_URL = LIVE_SERVER_URL;

    private static ConfigMode configMode = ConfigMode.LIVE;



	public static String CUSTOM_SERVER_URL = "",
    FRESH_SERVER_URL = "", CHAT_SERVER_URL = "", MENUS_SERVER_URL = "", PAY_SERVER_URL = "", FATAFAT_SERVER_URL = "";


    // modifiable static variables
    private static String SERVER_URL = DEFAULT_SERVER_URL;


    private static int MAX_AMOUNT = 5000;
    private static int MIN_AMOUNT = 100;


    private static final String FRESH_DEV_SERVER_URL = "8060";
    private static final String FRESH_LIVE_SERVER_URL = "4040";

    private static final String CHAT_DEV_SERVER_URL = "8095";
    private static final String CHAT_LIVE_SERVER_URL = "4010";

    private static final String MENUS_DEV_SERVER_URL = BuildConfig.MENUS_DEV_SERVER_URL;
    private static final String MENUS_LIVE_SERVER_URL = BuildConfig.MENUS_LIVE_SERVER_URL;

    private static final String FATAFAT_DEV_SERVER_URL = "8100";
    private static final String FATAFAT_LIVE_SERVER_URL = "4030";

    private static final String PAY_DEV_SERVER_URL = "";
    private static final String PAY_LIVE_SERVER_URL = "";

    private static final String FEED_DEV_SERVER_URL = ":8094";
    private static final String FEED_LIVE_SERVER_URL = ":8094";

    private static final String PROS_DEV_SERVER_URL = ":8061";
    private static final String PROS_LIVE_SERVER_URL = ":4034";
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

                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;


            case LIVE:

                SERVER_URL = LIVE_SERVER_URL;
                Log.PRINT = BuildConfig.DEBUG;


                MAX_AMOUNT = 5000;
                MIN_AMOUNT = 1;

                break;

			case CUSTOM:

				SERVER_URL = CUSTOM_SERVER_URL;
				Log.PRINT = true;


				MAX_AMOUNT = 5000;
				MIN_AMOUNT = 1;

				break;

        }


    }

    public static String getMapsCachingServerUrl() {
       return MAPS_CACHING_SERVER_URL;
    }

    public static String getJungleMapsServerUrl() {
       return JUNGLE_MAPS_SERVER_URL;
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


        return Prefs.with(context).getString(SPLabels.SUPPORT_CONTACT, "");

    }

	public static void saveSupportNumber(Context context, String supportContact){
		Prefs.with(context).save(SPLabels.SUPPORT_CONTACT, supportContact);
	}

    public static String getDebugPassword() {
        return DEBUG_PASSWORD;
    }

    public static String getAutosClientId() {
        return AUTOS_CLIENT_ID;
    }

    public static String getClientSharedSecret() {
        return CLIENT_SHARED_SECRET;
    }


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

    public static String getDownloadSource() {
        return DOWNLOAD_SOURCE;
    }

    public static String getFreshLiveServerUrl(){
        return FRESH_LIVE_SERVER_URL;
    }

    public static String getFreshDevServerUrl() {
        return FRESH_DEV_SERVER_URL;
    }

    public static String getFreshServerUrl() {
        if(TextUtils.isEmpty(FRESH_SERVER_URL)) {
            if (configMode == ConfigMode.LIVE) {
                return FRESH_LIVE_SERVER_URL;
            } else {
                return FRESH_DEV_SERVER_URL;
            }
        } else{
            return FRESH_SERVER_URL;
        }
    }

    public static String getChatServerUrl() {
        if(TextUtils.isEmpty(CHAT_SERVER_URL)) {
            if (configMode == ConfigMode.LIVE) {
                return CHAT_LIVE_SERVER_URL;
            } else {
                return CHAT_DEV_SERVER_URL;
            }
        } else{
            return CHAT_SERVER_URL;
        }
    }

    public static String getFreshDefaultServerUrl() {
        if(configMode == ConfigMode.LIVE){
            return FRESH_LIVE_SERVER_URL;
        } else{
            return FRESH_DEV_SERVER_URL;
        }
    }

    public static String getFreshServerUrlName() {
        if(getFreshServerUrl().equalsIgnoreCase(getFreshLiveServerUrl())){
            return "LIVE";
        }
        else if(getFreshServerUrl().equalsIgnoreCase(getFreshDevServerUrl())){
            return "DEV";
        }
        else{
            return "CUSTOM";
        }
    }

    public static String getFreshClientId() {
        return FRESH_CLIENT_ID;
    }

    public static String getMealsClientId() {
        return MEALS_CLIENT_ID;
    }

    public static String getDeliveryClientId() {
        return DELIVERY_CLIENT_ID;
    }

    public static String getGroceryClientId(){
        return GROCERY_CLIENT_ID;
    }

    public static String getMenusClientId(){
        return MENUS_CLIENT_ID;
    }

    public static String getPayClientId(){
        return PAY_CLIENT_ID;
    }
    public static String getDeliveryCustomerClientId(){
        return DELIVERY_CUSTOMER_CLIENT_ID;
    }





    public static String getMenusLiveServerUrl(){
        return MENUS_LIVE_SERVER_URL;
    }

    public static String getMenusDevServerUrl() {
        return MENUS_DEV_SERVER_URL;
    }

    public static String getMenusServerUrl() {
        if(TextUtils.isEmpty(MENUS_SERVER_URL)) {
            if (configMode == ConfigMode.LIVE) {
                return MENUS_LIVE_SERVER_URL;
            } else {
                return MENUS_DEV_SERVER_URL;
            }
        } else{
            return MENUS_SERVER_URL;
        }
    }

    public static String getMenusDefaultServerUrl() {
        if(configMode == ConfigMode.LIVE){
            return MENUS_LIVE_SERVER_URL;
        } else{
            return MENUS_DEV_SERVER_URL;
        }
    }

    public static String getMenusServerUrlName() {
        if(getMenusServerUrl().equalsIgnoreCase(getMenusLiveServerUrl())){
            return "LIVE";
        }
        else if(getMenusServerUrl().equalsIgnoreCase(getMenusDevServerUrl())){
            return "DEV";
        }
        else{
            return "CUSTOM";
        }
    }




    public static String getFatafatLiveServerUrl(){
        return FATAFAT_LIVE_SERVER_URL;
    }

    public static String getFatafatDevServerUrl() {
        return FATAFAT_DEV_SERVER_URL;
    }

    public static String getFatafatServerUrl() {
        if(TextUtils.isEmpty(FATAFAT_SERVER_URL)) {
            if (configMode == ConfigMode.LIVE) {
                return FATAFAT_LIVE_SERVER_URL;
            } else {
                return FATAFAT_DEV_SERVER_URL;
            }
        } else{
            return FATAFAT_SERVER_URL;
        }
    }

    public static String getFatafatDefaultServerUrl() {
        if(configMode == ConfigMode.LIVE){
            return FATAFAT_LIVE_SERVER_URL;
        } else{
            return FATAFAT_DEV_SERVER_URL;
        }
    }

    public static String getFatafatServerUrlName() {
        if(getFatafatServerUrl().equalsIgnoreCase(getFatafatLiveServerUrl())){
            return "LIVE";
        }
        else if(getFatafatServerUrl().equalsIgnoreCase(getFatafatDevServerUrl())){
            return "DEV";
        }
        else{
            return "CUSTOM";
        }
    }





    public static String getPayServerUrl() {
        if(TextUtils.isEmpty(PAY_SERVER_URL)) {
            if (configMode == ConfigMode.LIVE) {
                return PAY_LIVE_SERVER_URL;
            } else {
                // return PAY_DEV_SERVER_URL;
                return PAY_LIVE_SERVER_URL;
            }
        } else{
            return PAY_SERVER_URL;
        }
    }

    public static String getPayDefaultServerUrl() {
        if(configMode == ConfigMode.LIVE){
            return PAY_LIVE_SERVER_URL;
        } else{
            return PAY_DEV_SERVER_URL;
        }
    }


    public static String getFeedClientId() {
        return FEED_CLIENT_ID;
    }

    public static String getFeedServerUrl() {
        if (configMode == ConfigMode.LIVE) {
            return FEED_LIVE_SERVER_URL;
        } else {
            return FEED_DEV_SERVER_URL;
        }
    }

    public static String getProsClientId() {
        return PROS_CLIENT_ID;
    }

    public static String getProsServerUrl() {
        if (configMode == ConfigMode.LIVE) {
            return PROS_LIVE_SERVER_URL;
        } else {
            return PROS_DEV_SERVER_URL;
        }
    }


    public static String getLastOpenedClientId(Context context){
        return Prefs.with(context).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getAutosClientId());
    }
    public static void setLastOpenedClientId(Context context, String clientId){
        Prefs.with(context).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, clientId);
    }




}