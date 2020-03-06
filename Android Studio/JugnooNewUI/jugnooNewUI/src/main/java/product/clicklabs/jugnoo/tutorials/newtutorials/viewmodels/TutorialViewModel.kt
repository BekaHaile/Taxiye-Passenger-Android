package product.clicklabs.jugnoo.tutorials.newtutorials.viewmodels

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.tutorials.newtutorials.adapters.TutorialDAO
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Utils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*

class TutorialViewModel : ViewModel() {

    private var pListOfTutorialImages = MutableLiveData<ArrayList<TutorialDAO>>()
    private lateinit var mParameters: HashMap<String, String>

    fun getTutorialData(): MutableLiveData<ArrayList<TutorialDAO>> {
        return this.pListOfTutorialImages
    }

    fun syncDataFromServer(pParameters: HashMap<String, String>) {
        mParameters = pParameters
        if (MyApplication.getInstance().isOnline) {
            HomeUtil().putDefaultParams(pParameters)
            DialogPopup.showLoadingDialog(
                    MyApplication.getInstance().getmCurrentActivity(),
                    MyApplication.getInstance().getmCurrentActivity().getResources().getString(R.string.loading)
            )
            RestClient.getApiService().getInformation(
                    pParameters,
                    object : Callback<SettleUserDebt?> {
                        override fun success(settleUserDebt: SettleUserDebt?, response: Response) {
                            DialogPopup.dismissLoadingDialog()
                            val responseStr = String((response.body as TypedByteArray).bytes)
                            try {
                                val jObj = JSONObject(responseStr)
                                val dataStrings = jObj.getString("data")
                                var listOfTutImages = Gson().fromJson(dataStrings, object : TypeToken<ArrayList<TutorialDAO?>?>() {}.getType()) as ArrayList<TutorialDAO>
                                pListOfTutorialImages.postValue(listOfTutImages)

                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }

                        override fun failure(error: RetrofitError) {
                            DialogPopup.dismissLoadingDialog()
                        }
                    }
            )
        } else {
            openRetryFunction(MyApplication.getInstance().getmCurrentActivity())
        }
    }

    fun openRetryFunction(pContext: Context) {
        DialogPopup.dialogNoInternet(pContext as Activity,
                pContext.getString(R.string.connection_lost_title),
                pContext.getString(R.string.connection_lost_desc),
                object : Utils.AlertCallBackWithButtonsInterface {
                    override fun positiveClick(v: View) {
                        if (::mParameters.isInitialized) {
                            syncDataFromServer(mParameters)
                        }
                    }

                    override fun neutralClick(v: View) {}
                    override fun negativeClick(v: View) {
                        pContext.finish()
                    }
                })
    }
}
