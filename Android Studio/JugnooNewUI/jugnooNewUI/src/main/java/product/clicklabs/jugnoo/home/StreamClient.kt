package product.clicklabs.jugnoo.home

import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okio.BufferedSource
import product.clicklabs.jugnoo.retrofit.RestClient2
import product.clicklabs.jugnoo.utils.Log
import java.util.*


class StreamClient {
    private val TAG = StreamClient::class.java.simpleName

    private val disposables: MutableList<Disposable> by lazy {
        mutableListOf<Disposable>()
    }

    private fun startClient(id:Int) {
        disposables.add(RestClient2.apiStreamService
                .createUserStream(id)
                .observeOn(Schedulers.io())
                .flatMap { responseBody -> events(responseBody.source()) }
                .subscribe({ t ->
                    Log.i(TAG, "onNext t=$t")
                }, { e ->
                    Log.i(TAG, "onError e=$e")
                }, {
                    Log.i(TAG, "onFinish")
                }))

    }

    private fun stopClient() {
        disposables.forEach {
            it.dispose()
        }
        disposables.clear()
    }


    private var locationStreamDisposable:Disposable? = null

    private val handler: Handler by lazy{ Handler(Looper.getMainLooper()) }

    fun startLocationStream(params: HashMap<String, String>, callback:LocationStreamCallback){
        if(locationStreamDisposable == null) {
            locationStreamDisposable = RestClient2.apiStreamService
                    .getDriverCurrentLocation(params)
                    .observeOn(Schedulers.io())
                    .flatMap { responseBody -> events(responseBody.source()) }
                    .subscribe({ t ->
                        Log.d(TAG, "onSuccess t=$t")
                        if(locationStreamDisposable != null && !locationStreamDisposable!!.isDisposed) {
                            Log.d(TAG, "onSuccess isDisposed=not")
                            callback.onResponse(t)
                        }
                    }, { e ->
                        Log.e(TAG, "onError e=$e")
                        reconnectWithDelay(callback)
                    }, {
                        Log.i(TAG, "onFinish")
                        if(locationStreamDisposable != null && !locationStreamDisposable!!.isDisposed) {
                            stopLocationStream()
                        }
                    })
        }
    }

    fun stopLocationStream(){
        if(locationStreamDisposable != null){
            if(!locationStreamDisposable!!.isDisposed) {
                locationStreamDisposable!!.dispose()
            }
            locationStreamDisposable = null
        }
    }


    private fun reconnectWithDelay(callback:LocationStreamCallback){
        Log.e(TAG, "reconnectWithDelay callback=$callback")
        stopLocationStream()
        handler.postDelayed({
            Log.e(TAG, "reconnect startLocationStream callback=$callback")
            startLocationStream(callback.getParams(), callback)
        }, 5000)
    }


    interface LocationStreamCallback {
        fun getParams(): HashMap<String, String>
        fun onResponse(response:String)
    }



    private fun events(source: BufferedSource): Observable<String> {
        return Observable.create { emitter ->
            var isCompleted = false
            try {
                while (!source.exhausted()) {
                    if(!emitter.isDisposed) {
                        emitter.onNext(source.readUtf8Line()!!)
                    }
                }
                if(!emitter.isDisposed) {
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception e=$e")
                e.printStackTrace()
                if (e.message == "Socket closed") {
                    isCompleted = true
                    if(!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                } else if(!emitter.isDisposed) {
                    emitter.onError(e)
                }
            }
            //if response end we get here
            if (!isCompleted && !emitter.isDisposed) {
                emitter.onComplete()
            }
        }
    }


}