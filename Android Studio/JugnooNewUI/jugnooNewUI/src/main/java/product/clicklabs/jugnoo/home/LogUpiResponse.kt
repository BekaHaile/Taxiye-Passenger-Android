package product.clicklabs.jugnoo.home

import android.app.Activity
import android.os.Bundle
import com.sabkuchfresh.feed.models.FeedCommonResponse
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import org.json.JSONObject
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.utils.DialogPopup



object LogUpiResponse {

    fun api(activity: Activity, engagementId:String, bundle: Bundle?, callback: Callback?){

        bundle?.run{
            val params = hashMapOf<String, String>()
            val jsonObject = JSONObject()

            keySet()?.let{
                it.forEach{ key->
                    if(key.equals(Constants.KEY_UPI_RESPONSE, true)){
                        val responseVal = get(key).toString()
                        val responseMap = getQueryMap(responseVal)
                        for(inKey in responseMap.keys){
                            jsonObject.put(inKey, responseMap.get(inKey))
                        }
                        return@forEach
                    }
                }
            }

            params[Constants.KEY_ENGAGEMENT_ID] = engagementId
            params[Constants.KEY_PAYMENT_RESPONSE] = jsonObject.toString()

            ApiCommon<FeedCommonResponse>(activity).execute(params, ApiName.UPDATE_PAYMENT_TO_UPI,
                    object: APICommonCallback<FeedCommonResponse>(){
                        override fun onSuccess(t: FeedCommonResponse?, message: String?, flag: Int) {
                            callback?.onSuccess(engagementId)
                            DialogPopup.alertPopupWithListener(activity, "", message) {
                            }
                        }

                        override fun onError(t: FeedCommonResponse?, message: String?, flag: Int): Boolean {
                            return false
                        }

                    })
        }

    }

    private fun getQueryMap(query: String): Map<String, String> {
        val params = query.split("&".toRegex())
        val map = HashMap<String, String>()
        for (param in params) {
            val name = param.split("=".toRegex())[0]
            val value = param.split("=".toRegex())[1]
            map[name] = value
        }
        return map
    }


    interface Callback{
        fun onSuccess(engagementId:String)
    }


}