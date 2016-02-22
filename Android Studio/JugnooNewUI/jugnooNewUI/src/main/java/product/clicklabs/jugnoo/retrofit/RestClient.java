package product.clicklabs.jugnoo.retrofit;

import com.jakewharton.retrofit.Ok3Client;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import product.clicklabs.jugnoo.config.Config;
import retrofit.RestAdapter;

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


    public static void setupRestClient() {
        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override
            public void log(String message) {
            }
        };

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new Ok3Client(getOkHttpClient()))
                .setLog(fooLog)
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
                .setClient(new Ok3Client(getOkHttpClient()))
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
                .setClient(new Ok3Client(getOkHttpClient()))
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
