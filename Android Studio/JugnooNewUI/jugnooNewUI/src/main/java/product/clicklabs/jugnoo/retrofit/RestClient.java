package product.clicklabs.jugnoo.retrofit;

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

    static {
        setupRestClient();
    }

    public static void setupRestClient() {
       RestAdapter.Log fooLog = new RestAdapter.Log() {
            @Override
            public void log(String message) {
            }
        };

        ArrayList<Protocol> protocolList = new ArrayList<Protocol>();
        protocolList.add(Protocol.HTTP_2);
        protocolList.add(Protocol.SPDY_3);
        protocolList.add(Protocol.HTTP_1_1);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setRetryOnConnectionFailure(false);
        okHttpClient.setProtocols(protocolList);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Config.getServerUrl())
                .setClient(new OkClient(okHttpClient))
                .setLog(fooLog)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        API_SERVICES = restAdapter.create(ApiService.class);
    }

    public static ApiService getApiServiceForLink(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setRetryOnConnectionFailure(false);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(url)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL);
        RestAdapter restAdapter = builder.build();
        return restAdapter.create(ApiService.class);
    }

    public static ApiService getApiServices() {
        return API_SERVICES;
    }
}
