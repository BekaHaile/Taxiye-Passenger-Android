package product.clicklabs.jugnoo.home

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okio.BufferedSource
import product.clicklabs.jugnoo.retrofit.RestClient2
import product.clicklabs.jugnoo.utils.Log
import java.io.IOException


class StreamClient {
    private val TAG = StreamClient::class.java.simpleName

    val disposables: MutableList<Disposable> by lazy {
        mutableListOf<Disposable>()
    }

    fun startClient(id:Int) {
        disposables.add(RestClient2.apiService
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

    fun stopClient() {
        disposables.forEach {
            it.dispose()
        }
        disposables.clear()
    }


    fun events(source: BufferedSource): Observable<String> {
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