package product.clicklabs.jugnoo.promotion.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.analytics.GAAction
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.fragment_referral_txn.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.DialogErrorType
import product.clicklabs.jugnoo.home.HomeActivity
import product.clicklabs.jugnoo.promotion.adapters.ReferralTxnAdapter
import product.clicklabs.jugnoo.promotion.models.ReferralTxnResponse
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Utils
import retrofit.RetrofitError
import java.util.*


class ReferralTxnFragment : Fragment(), GAAction {

    private val TAG = ReferralTxnFragment::class.java.simpleName

    var adapter : ReferralTxnAdapter? = null

    var totalTransactions = 0
    var pageSize = 0
    internal var transactionInfoList: MutableList<Any> = mutableListOf()

    private var state:Int = STATE_REFERRALS

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_referral_txn, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        state = arguments?.getInt(KEY_STATE, STATE_REFERRALS) ?: STATE_REFERRALS

        rvTransactions.layoutManager = LinearLayoutManager(requireActivity())

        groupNoData.visibility = View.GONE

        if(Data.autoData != null) {
            adapter = ReferralTxnAdapter(requireActivity(), Data.autoData.currency, object : ReferralTxnAdapter.Callback {
                override fun onShowMoreClick() {
                    getTransactionInfoAsync(requireActivity())
                }
            })
            rvTransactions.adapter = adapter


            totalTransactions = 0
            pageSize = 0

            transactionInfoList.clear()
            getTransactionInfoAsync(requireActivity())
        }
    }
    


    fun updateListData(message: String, errorOccurred: Boolean) {
        Log.e("errorOccurred", "errorOccurred = $errorOccurred")
        if (errorOccurred) {
            DialogPopup.alertPopupTwoButtonsWithListeners(requireActivity(), "", message, getString(R.string.retry), getString(R.string.cancel),
                    { getTransactionInfoAsync(requireActivity()) },
                    { requireActivity().onBackPressed() }, false, false)

            transactionInfoList.clear()
            adapter!!.notifyList(transactionInfoList, totalTransactions)
            groupNoData.visibility = View.GONE
        } else {
            if (transactionInfoList.size == 0) {
                groupNoData.visibility = View.VISIBLE
            } else {
                groupNoData.visibility = View.GONE
            }
            adapter!!.notifyList(transactionInfoList, totalTransactions)
        }
    }

    fun getTransactionInfoAsync(activity: Activity?) {
        if (MyApplication.getInstance().isOnline) {
            DialogPopup.showLoadingDialog(activity, getString(R.string.loading))
            callRefreshAPI(activity)
        } else {
            retryDialog(DialogErrorType.NO_NET)
        }
    }


    private fun retryDialog(dialogErrorType: DialogErrorType) {
        DialogPopup.dialogNoInternet(requireActivity(),
                dialogErrorType,
                object : Utils.AlertCallBackWithButtonsInterface {
                    override fun positiveClick(view: View) {
                        getTransactionInfoAsync(requireActivity())
                    }

                    override fun neutralClick(view: View) {}

                    override fun negativeClick(view: View) {
                        requireActivity().onBackPressed()
                    }
                })
    }


    fun callRefreshAPI(activity: Activity?) {
        try {
            if (!HomeActivity.checkIfUserDataNull(activity)) {
                val params = HashMap<String, String>()
                params["start_from"] = transactionInfoList.size.toString()
                params[KEY_STATE] = state.toString()

                ApiCommon<ReferralTxnResponse>(requireActivity()).showLoader(true)
                        .execute(params, ApiName.REFERRAL_TXNS, object:APICommonCallback<ReferralTxnResponse>(){
                            override fun onSuccess(t: ReferralTxnResponse?, message: String?, flag: Int) {
                                if(state == STATE_REFERRALS
                                        && t != null && t.referralData != null && t.referralData!!.referrals != null){
                                    transactionInfoList.addAll(t.referralData!!.referrals!!)
                                }
                                else if(state == STATE_TOTAL
                                        && t != null && t.referralData != null && t.referralData!!.txns != null){
                                    transactionInfoList.addAll(t.referralData!!.txns!!)
                                }
                                else if(state == STATE_TODAY
                                        && t != null && t.referralData != null && t.referralData!!.txns != null){
                                    val currentDate = DateOperations.getCurrentDate()
                                    val currentDateItems = t.referralData!!.txns!!.filter {
                                        it.creditedOn?.contains(currentDate) ?: false
                                    }
                                    transactionInfoList.addAll(currentDateItems)
                                }
                                updateListData("", false)
                            }

                            override fun onError(t: ReferralTxnResponse?, message: String?, flag: Int): Boolean {
                                updateListData("", false)
                                return false
                            }

                            override fun onFailure(error: RetrofitError?): Boolean {
                                updateListData("", false)
                                return super.onFailure(error)
                            }

                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        const val KEY_STATE = "state"
        const val STATE_REFERRALS = 0
        const val STATE_TOTAL = 1
        const val STATE_TODAY = 2

        @JvmStatic
        fun newInstance(state:Int = 0): ReferralTxnFragment {
            val fragment = ReferralTxnFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_STATE, state)
            fragment.arguments = bundle
            return fragment
        }
    }


}
