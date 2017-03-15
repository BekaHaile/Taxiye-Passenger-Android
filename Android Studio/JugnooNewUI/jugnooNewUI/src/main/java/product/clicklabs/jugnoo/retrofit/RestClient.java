package product.clicklabs.jugnoo.retrofit;

import com.jakewharton.retrofit.Ok3Client;
import com.jugnoo.pay.retrofit.PayApiService;
import com.sabkuchfresh.apis.FeedApiService;
import com.sabkuchfresh.apis.FreshApiService;
import com.sabkuchfresh.apis.MenusApiService;

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
    private static PayApiService PAY_API_SERVICE = null;
    private static FeedApiService FEED_API_SERVICE = null;

    static {
        setupRestClient();
        setupGoogleAPIRestClient();
        setupFreshApiRestClient();
        setupChatApiRestClient();
        setupMenusApiRestClient();
        setupPayApiRestClient();
        setupFeedApiRestClient();
    }

    private static OkHttpClient getOkHttpClient(){

        ArrayList<Protocol> protocolList = new ArrayList<>();
        protocolList.add(Protocol.HTTP_2);
        protocolList.add(Protocol.SPDY_3);
        protocolList.add(Protocol.HTTP_1_1);

        ConnectionPool connectionPool = new ConnectionPool(3, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool(connectionPool);
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(false);
        builder.protocols(protocolList);

        return builder.build();
    }

    private static void setLogger(RestAdapter.Builder builder){
        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override
            public void log(String message) {
            }
        };
        if(!BuildConfig.DEBUG_MODE) {
            builder.setLog(fooLog);
        }
    }


    public static void setupRestClient() {
        if(API_SERVICES == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient()))
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
        PAY_API_SERVICE = null;
        FEED_API_SERVICE = null;
    }

    public static void setupAllClients(){
        setupRestClient();
        setupFreshApiRestClient();
        setupChatApiRestClient();
        setupMenusApiRestClient();
        setupPayApiRestClient();
        setupFeedApiRestClient();
    }


    public static StringAPIService getStringRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new Ok3Client(getOkHttpClient()))
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
                    .setEndpoint("http://maps.googleapis.com/maps/api")
                    .setClient(new Ok3Client(getOkHttpClient()))
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
                    .setClient(new Ok3Client(getOkHttpClient()))
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
                    .setClient(new Ok3Client(getOkHttpClient()))
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
                    .setClient(new Ok3Client(getOkHttpClient()))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            MENUS_API_SERVICE = restAdapter.create(MenusApiService.class);
        }
    }

    public static MenusApiService getMenusApiService() {
        return MENUS_API_SERVICE;
    }





    public static void setupPayApiRestClient() {
        if(PAY_API_SERVICE == null) {
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(Config.getPayServerUrl())
                    .setClient(new Ok3Client(getOkHttpClient()))
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
                    .setClient(new Ok3Client(getOkHttpClient()))
                    .setLogLevel(RestAdapter.LogLevel.FULL);
            setLogger(builder);

            RestAdapter restAdapter = builder.build();
            FEED_API_SERVICE = restAdapter.create(FeedApiService.class);
        }
    }

    public static FeedApiService getFeedApiService() {
        return FEED_API_SERVICE;
    }
}
