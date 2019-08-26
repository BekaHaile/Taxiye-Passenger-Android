package product.clicklabs.jugnoo.apis

import android.app.Activity
import android.view.View
import org.json.JSONObject
import product.clicklabs.jugnoo.*
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Utils
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray

object ApiCancelRequest {

    const val TAG:String = "ApiCancelRequest"

    fun cancelCustomerRequestAsync(activity: Activity, callback: Callback?) {
        if (MyApplication.getInstance().isOnline) {

            DialogPopup.showLoadingDialog(activity, activity.getString(R.string.loading))

            val params = hashMapOf<String, String>()

            params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
            params[Constants.KEY_SESSION_ID] = Data.autoData.getcSessionId()


            HomeUtil().putDefaultParams(params)
            RestClient.getApiService().cancelTheRequest(params, object : retrofit.Callback<SettleUserDebt> {
                override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                    val responseStr = String((response.body as TypedByteArray).bytes)
                    DialogPopup.dismissLoadingDialog()
                    try {
                        val jObj = JSONObject(responseStr)
                        val message = JSONParser.getServerMessage(jObj)
                        val flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.REQUEST_CANCELLED.getOrdinal())
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if(flag == ApiResponseFlags.REQUEST_CANCELLED.getOrdinal()){
                                callback?.onSuccess()
                            } else {
                                DialogPopup.alertPopupWithListener(activity, "", message
                                ) { callback?.onFailure() }
                            }
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        DialogPopup.alertPopupWithListener(activity, "", activity.getString(R.string.connection_lost_please_try_again))
                        { callback?.onFailure() }
                    }

                }

                override fun failure(error: RetrofitError) {
                    Log.e(TAG, "cancelTheRequest error=$error")
                    DialogPopup.dismissLoadingDialog()
                    DialogPopup.alertPopupWithListener(activity, "", activity.getString(R.string.connection_lost_please_try_again))
                    { callback?.onFailure() }
                }
            })
        } else {
            DialogPopup.dialogNoInternet(activity, activity.getString(R.string.connection_lost_title),
                    activity.getString(R.string.connection_lost_desc),
                    object : Utils.AlertCallBackWithButtonsInterface {
                override fun positiveClick(v: View) {
                    cancelCustomerRequestAsync(activity, callback)
                }

                override fun neutralClick(v: View) {

                }

                override fun negativeClick(v: View) {

                }
            })
        }
    }

    interface Callback{
        fun onSuccess()
        fun onFailure()
    }

}