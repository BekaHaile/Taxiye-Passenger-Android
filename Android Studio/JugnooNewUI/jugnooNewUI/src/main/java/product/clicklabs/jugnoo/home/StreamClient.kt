package product.clicklabs.jugnoo.home

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okio.BufferedSource
import product.clicklabs.jugnoo.retrofit.RestClient2
import product.clicklabs.jugnoo.utils.Log
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


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

    fun startLocationStream(params: HashMap<String, String>, callback:LocationStreamCallback){
        if(locationStreamDisposable == null) {
            locationStreamDisposable = RestClient2.apiStreamService
                    .getDriverCurrentLocation(params)
                    .observeOn(Schedulers.io())
                    .flatMap { responseBody -> events(responseBody.source()) }
                    .subscribe({ t ->
                        Log.i(TAG, "onNext t=$t")
                        callback.onResponse(t)
                    }, { e ->
                        Log.i(TAG, "onError e=$e")
                        reconnectWithDelay(callback)
                    }, {
                        Log.i(TAG, "onFinish")
                        stopLocationStream()
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
        val disposable = Observable.create<String> { emitter ->
            Log.d("DelayExample", "Create")
            stopLocationStream()
            emitter.onNext("MindOrks")
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("DelayExample", "onNext it=$it")
                    startLocationStream(callback.getParams(), callback)
                }
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
                    emitter.onNext(source.readUtf8Line()!!)
                }
                emitter.onComplete()
            } catch (e: IOException) {
                e.printStackTrace()
                if (e.message == "Socket closed") {
                    isCompleted = true
                    emitter.onComplete()
                } else {
                    emitter.onError(e)
                }
            }
            //if response end we get here
            if (!isCompleted) {
                emitter.onComplete()
            }
        }
    }


}