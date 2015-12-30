package product.clicklabs.jugnoo.retrofit;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.DataLoader;
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
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        API_SERVICES = restAdapter.create(ApiService.class);
    }

    public static ApiService getApiServices() {
        return API_SERVICES;
    }
}
