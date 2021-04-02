package product.clicklabs.jugnoo.credits.fragments

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.sabkuchfresh.feed.models.FeedCommonResponse
import kotlinx.coroutines.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.credits.UserType
import product.clicklabs.jugnoo.datastructure.DialogErrorType
import product.clicklabs.jugnoo.datastructure.TutorialDataResponse
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Utils
import retrofit.mime.TypedByteArray
import java.util.*
import kotlin.coroutines.CoroutineContext

class SendCreditsToCustomerViewModel : ViewModel(), CoroutineScope {

    private var mShareCreditLiveData = MutableLiveData<FeedCommonResponse>()
    var mJob = Job()
    private val uiScope = CoroutineScope(coroutineContext)

    fun getShareCreditsLiveData(): MutableLiveData<FeedCommonResponse> {
        return mShareCreditLiveData
    }

    fun shareCreditsToUser(
            pPhoneNo: String,
            pCountryCode: String,
            pAmount: String,
            pTransferTo: Int
    ) {
        uiScope.launch {
            val response = withContext(Dispatchers.Default) { getServerReponse(pPhoneNo, pCountryCode, pAmount, pTransferTo) }
            when (response != null) {
                true ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is FeedCommonResponse -> mShareCreditLiveData.postValue(response)
                            is JsonParseException -> DialogPopup.alertPopup(MyApplication.getInstance().getmActivity(), "", response.localizedMessage)
                        }
                    }
                false -> {
                    uiScope.launch {
                        withContext(Dispatchers.Main) {
                            DialogPopup.dialogNoInternet(
                                    MyApplication.getInstance().getmActivity(),
                                    DialogErrorType.CONNECTION_LOST,
                                    object : Utils.AlertCallBackWithButtonsInterface {
                                        override fun positiveClick(view: View) {
                                            MyApplication.getInstance().getmActivity().finish()
                                        }

                                        override fun neutralClick(view: View) {
                                            MyApplication.getInstance().getmActivity().finish()
                                        }

                                        override fun negativeClick(view: View) {
                                            MyApplication.getInstance().getmActivity().finish()
                                        }
                                    })
                        }
                    }
                }
            }
        }
    }

    private fun getServerReponse(pPhoneNo: String,
                                 pCountryCode: String,
                                 pAmount: String,
                                 pTransferTo: Int
    ): Any? {
        if (MyApplication.getInstance().isOnline) {
            MyApplication.getInstance().getmActivity().runOnUiThread {
                DialogPopup.showLoadingDialog(MyApplication.getInstance().getmActivity(), "Loading")
            }
            val nameValuePairs = HashMap<String, String>()
            nameValuePairs["access_token"] = Data.userData.accessToken
            nameValuePairs["engagement_id"] = "" + Data.autoData.getcEngagementId()
            nameValuePairs["latitude"] = "" + Data.latitude
            nameValuePairs["longitude"] = "" + Data.longitude
            nameValuePairs["phone_no"] = "" + pCountryCode + pPhoneNo
            nameValuePairs["country_code"] = "" + pCountryCode
            nameValuePairs["amount"] = "" + pAmount
            nameValuePairs["login_type"] = "" + UserType.CUSTOMER.ordinal
            nameValuePairs["receiver_type"] = "" + pTransferTo
            HomeUtil().putDefaultParams(nameValuePairs)
            val mFeedCommonResponse = RestClient.getApiService().shareCredits(nameValuePairs)
            DialogPopup.dismissLoadingDialog()
            val responseStr = String((mFeedCommonResponse.body as TypedByteArray).bytes)
            return when (responseStr.isEmpty()) {
                true -> JsonParseException("Server Error:".plus(mFeedCommonResponse.status))
                false -> Gson().fromJson(responseStr, TutorialDataResponse::class.java)
            }
        }
        return null
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mJob
}
