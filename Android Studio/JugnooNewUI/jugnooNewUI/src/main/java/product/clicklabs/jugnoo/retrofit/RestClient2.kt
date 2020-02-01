package product.clicklabs.jugnoo.retrofit

import io.reactivex.schedulers.Schedulers
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import product.clicklabs.jugnoo.config.Config
import product.clicklabs.jugnoo.retrofit.apis.ApiService2
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


object RestClient2 {

    @JvmStatic lateinit var apiStreamService:ApiService2
    @JvmStatic lateinit var apiService:ApiService2


    private fun getOkHttpClient(retryOnConnectionFailure: Boolean, timeoutSeconds: Long,
                                loggingLevel: HttpLoggingInterceptor.Level): OkHttpClient { //loggingLevel BASIC is needed for streaming api to behave like chunk data events

        val protocolList = ArrayList<Protocol>()
        protocolList.add(Protocol.HTTP_2)
        protocolList.add(Protocol.SPDY_3)
        protocolList.add(Protocol.HTTP_1_1)

        val connectionPool = ConnectionPool(3, (5 * 60 * 1000).toLong(), TimeUnit.MILLISECONDS)

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = loggingLevel


        val builder = OkHttpClient.Builder()
        builder.connectionPool(connectionPool)
        builder.readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        builder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        builder.writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(retryOnConnectionFailure)
        builder.protocols(protocolList)
        builder.addInterceptor(httpLoggingInterceptor)

        return builder.build()
    }


    @JvmStatic
    fun initStreamApiService(){
        val restAdapter = Retrofit.Builder()
                .baseUrl(Config.getServerUrl())
                .client(getOkHttpClient(true, 5*60, HttpLoggingInterceptor.Level.BASIC))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        apiStreamService = restAdapter.create(ApiService2::class.java)
    }

    @JvmStatic
    fun initApiService(){
        val restAdapter = Retrofit.Builder()
                .baseUrl(Config.getServerUrl())
                .client(getOkHttpClient(true, 30, HttpLoggingInterceptor.Level.BODY))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        apiService = restAdapter.create(ApiService2::class.java)
    }


}