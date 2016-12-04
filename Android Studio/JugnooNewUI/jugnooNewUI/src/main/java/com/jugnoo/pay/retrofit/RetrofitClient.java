package com.jugnoo.pay.retrofit;

import com.jakewharton.retrofit.Ok3Client;
import com.jugnoo.pay.config.Config;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;


/**
 * Created by cl-macmini-38 on 18/05/16.
 */
public class RetrofitClient {

    private static final int CONNECT_TIMEOUT_MILLIS = 120 * 1000; // 60s
    private static final int READ_TIMEOUT_MILLIS = 60 * 1000; // 60s

    private static RestAdapter adapter;

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();


    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(Config.getBaseURL())
            //.setLog(fooLog)
            .setLogLevel(RestAdapter.LogLevel.FULL);
//            .setClient(new OkClient(new OkHttpClient()));


    public static <S> S createService(Class<S> serviceClass)
    {
        okHttpClient.readTimeout(60, TimeUnit.SECONDS);
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
        builder.setClient(new Ok3Client(okHttpClient.build()));
        if (adapter == null)
            adapter = builder.build();

        return adapter.create(serviceClass);
    }


//    private static Retrofit mRetrofit;
//
//    public static WebApi setUpRetrofitClient(){
//
//
//
//        if (mRetrofit == null){
//            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
////            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//// set your desired log level
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            httpClient.addInterceptor(logging);
//            mRetrofit = new Retrofit.Builder()
//                    .baseUrl(Config.getBaseURL())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(httpClient.build())
//                    .build();
//            System.out.println(mRetrofit.baseUrl().toString());
//        }
//        return mRetrofit.create(WebApi.class);
//    }


}

