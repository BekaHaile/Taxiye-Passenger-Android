package product.clicklabs.jugnoo.retrofit;

import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.config.Config;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Rest client
 */
public class RestClient {
    private static ApiService API_SERVICES;
    private static GoogleAPIServices GOOGLE_API_SERVICES;

    static {
        setupRestClient();
        setupGoogleAPIRestClient();
    }

    private static OkHttpClient getOkHttpClient(){

        ArrayList<Protocol> protocolList = new ArrayList<>();
        protocolList.add(Protocol.HTTP_2);
        protocolList.add(Protocol.SPDY_3);
        protocolList.add(Protocol.HTTP_1_1);

        ConnectionPool connectionPool = new ConnectionPool(3, 5 * 60 * 1000);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectionPool(connectionPool);
        okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setRetryOnConnectionFailure(false);
        okHttpClient.setProtocols(protocolList);

        return okHttpClient;
    }


    public static void setupRestClient() {
        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override
            public void log(String message) {
            }
        };

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new OkClient(getOkHttpClient()))
//                .setLog(fooLog)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        API_SERVICES = restAdapter.create(ApiService.class);
    }

    public static ApiService getApiServices() {
        return API_SERVICES;
    }


    public static StringAPIService getStringRestClient() {
        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override
            public void log(String message) {
            }
        };

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new OkClient(getOkHttpClient()))
                .setConverter(new StringConverter())
                .setLog(fooLog)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        return restAdapter.create(StringAPIService.class);
    }



    public static void setupGoogleAPIRestClient() {

        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override public void log(String message) {
            }
        };

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("http://maps.googleapis.com/maps/api")
                .setClient(new OkClient(getOkHttpClient()))
                .setLog(fooLog)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                ;

        RestAdapter restAdapter = builder.build();
        GOOGLE_API_SERVICES = restAdapter.create(GoogleAPIServices.class);
    }

    public static GoogleAPIServices getGoogleApiServices() {
        return GOOGLE_API_SERVICES;
    }
}
