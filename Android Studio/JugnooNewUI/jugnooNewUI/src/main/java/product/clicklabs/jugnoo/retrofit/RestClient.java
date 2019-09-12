package product.clicklabs.jugnoo.retrofit;

import com.jakewharton.retrofit.Ok3Client;
import com.jugnoo.pay.retrofit.PayApiService;
import com.sabkuchfresh.apis.FatafatApiService;
import com.sabkuchfresh.apis.FeedApiService;
import com.sabkuchfresh.apis.FreshApiService;
import com.sabkuchfresh.apis.MenusApiService;
import com.sabkuchfresh.pros.api.ProsApi;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import product.clicklabs.jugnoo.BuildConfig;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.retrofit.model.ChatApiService;
import retrofit.RestAdapter;

/**
 * Rest client
 */
public class RestClient {
    private static ApiService API_SERVICES = null;
    private static GoogleAPIServices GOOGLE_API_SERVICES = null;
    private static FreshApiService FRESH_API_SERVICE = null;
    private static ChatApiService CHAT_API_SERVICE = null;
    private static MenusApiService MENUS_API_SERVICE = null;
    private static FatafatApiService FATAFAT_API_SERVICE = null;
    private static PayApiService PAY_API_SERVICE = null;
    private static FeedApiService FEED_API_SERVICE = null;
    private static ProsApi PROS_API = null;
    private static MapsCachingApiService MAPS_CACHING_API = null;

    static {
        setupRestClient();
        setupGoogleAPIRestClient();
        setupFreshApiRestClient();
        setupChatApiRestClient();
        setupMenusApiRestClient();
        setupFatafatApiRestClient();
        setupPayApiRestClient();
        setupFeedApiRestClient();
        setupProsApiRestClient();
        setupMapsCachingRestClient();
    }

	private static OkHttpClient getOkHttpClient(boolean retryOnConnectionFailure){
    	return getOkHttpClient(retryOnConnectionFailure, 30);
	}
    private static OkHttpClient getOkHttpClient(boolean retryOnConnectionFailure, long timeoutSeconds){

        ArrayList<Protocol> protocolList = new ArrayList<>();
        protocolList.add(Protocol.HTTP_2);
        protocolList.add(Protocol.SPDY_3);
        protocolList.add(Protocol.HTTP_1_1);

        ConnectionPool connectionPool = new ConnectionPool(3, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool(connectionPool);
        builder.readTimeout(timeoutSeconds, TimeUnit.SECONDS);
        builder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);
        builder.writeTimeout(timeoutSeconds, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(retryOnConnectionFailure);
        builder.protocols(protocolList);

        return builder.build();
    }

    private static void setLogger(RestAdapter.Builder builder){

        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override
            public void log(String message) {
            }
        };
        if(!BuildConfig.DEBUG) {
            builder.setLog(fooLog);
        }
    }


    public static void setupRestClient() {
        if(API_SERVICES == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            API_SERVICES = restAdapter.create(ApiService.class);
        }
    }

    public static ApiService getApiService() {
        return API_SERVICES;
    }

    public static void clearRestClients(){
        API_SERVICES = null;
        FRESH_API_SERVICE = null;
        CHAT_API_SERVICE = null;
        MENUS_API_SERVICE = null;
        FATAFAT_API_SERVICE = null;
        PAY_API_SERVICE = null;
        FEED_API_SERVICE = null;
        PROS_API = null;
    }

    public static void setupAllClients(){
        setupRestClient();
        setupFreshApiRestClient();
        setupChatApiRestClient();
        setupMenusApiRestClient();
        setupFatafatApiRestClient();
        setupPayApiRestClient();
        setupFeedApiRestClient();
        setupProsApiRestClient();
        setupMapsCachingRestClient();
    }


    public static StringAPIService getStringRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new Ok3Client(getOkHttpClient(false)))
                .setConverter(new StringConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL);
        setLogger(builder);

        RestAdapter restAdapter = builder.build();
        return restAdapter.create(StringAPIService.class);
    }



    public static void setupGoogleAPIRestClient() {
        if(GOOGLE_API_SERVICES == null) {
            RestAdapter.Log fooLog = new RestAdapter.Log() {
                @Override
                public void log(String message) {
                }
            };

            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint("https://maps.googleapis.com")
                    .setClient(new Ok3Client(getOkHttpClient(true)))
                    .setLog(fooLog)
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            RestAdapter restAdapter = builder.build();
            GOOGLE_API_SERVICES = restAdapter.create(GoogleAPIServices.class);
        }
    }

    public static GoogleAPIServices getGoogleApiService() {
        return GOOGLE_API_SERVICES;
    }



    public static void setupFreshApiRestClient() {
        if(FRESH_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getFreshServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            FRESH_API_SERVICE = restAdapter.create(FreshApiService.class);
        }
    }

    public static FreshApiService getFreshApiService() {
        return FRESH_API_SERVICE;
    }



    public static void setupChatApiRestClient() {
        if(CHAT_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getChatServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            CHAT_API_SERVICE = restAdapter.create(ChatApiService.class);
        }
    }

    public static ChatApiService getChatApiService() {
        return CHAT_API_SERVICE;
    }



    public static void setupMenusApiRestClient() {
        if(MENUS_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getMenusServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            MENUS_API_SERVICE = restAdapter.create(MenusApiService.class);
        }
    }

    public static MenusApiService getMenusApiService() {
        return MENUS_API_SERVICE;
    }



    public static void setupFatafatApiRestClient() {
        if(FATAFAT_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getFatafatServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(true)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            FATAFAT_API_SERVICE = restAdapter.create(FatafatApiService.class);
        }
    }

    public static FatafatApiService getFatafatApiService() {
        return FATAFAT_API_SERVICE;
    }





    public static void setupPayApiRestClient() {
        if(PAY_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getPayServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            PAY_API_SERVICE = restAdapter.create(PayApiService.class);
        }
    }

    public static PayApiService getPayApiService() {
        return PAY_API_SERVICE;
    }


    private static void setupFeedApiRestClient() {
        if(FEED_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getFeedServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            FEED_API_SERVICE = restAdapter.create(FeedApiService.class);
        }
    }

    public static FeedApiService getFeedApiService() {
        return FEED_API_SERVICE;
    }



    private static void setupProsApiRestClient() {
        if(PROS_API == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getProsServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(false)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            PROS_API = restAdapter.create(ProsApi.class);
        }
    }

    public static ProsApi getProsApiService() {
        return PROS_API;
    }

    public static void setupMapsCachingRestClient() {
        if(MAPS_CACHING_API == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getMapsCachingServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient(true, 3)))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            MAPS_CACHING_API = restAdapter.create(MapsCachingApiService.class);
        }
    }

    public static MapsCachingApiService getMapsCachingService() {
        return MAPS_CACHING_API;
    }


}
