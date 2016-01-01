package product.clicklabs.jugnoo.retrofit;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.config.Config;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Rest client
 */
public class RestClient {
    private static ApiService API_SERVICES;

    static {
        setupRestClient();
    }

    public static void setupRestClient() {
        RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override public void log(String message) {
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setRetryOnConnectionFailure(false);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new OkClient(okHttpClient))
                .setLog(fooLog)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        API_SERVICES = restAdapter.create(ApiService.class);
    }

    public static ApiService getApiServices() {
        return API_SERVICES;
    }
}
