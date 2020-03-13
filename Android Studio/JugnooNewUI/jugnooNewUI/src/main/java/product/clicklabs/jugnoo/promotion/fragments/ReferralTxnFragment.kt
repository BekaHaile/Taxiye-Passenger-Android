package product.clicklabs.jugnoo.promotion.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sabkuchfresh.analytics.GAAction
import kotlinx.android.synthetic.main.fragment_referral_txn.*
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.promotion.adapters.ReferralTxnAdapter
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Log


class ReferralTxnFragment : Fragment(), GAAction {

    private val TAG = ReferralTxnFragment::class.java.simpleName

    var adapter : ReferralTxnAdapter? = null

    var totalTransactions = 0
    var pageSize = 0
    internal var transactionInfoList: MutableList<Any> = mutableListOf()

    private var state:Int = STATE_REFERRALS

    private var callback:Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Callback){
            callback = context
        }
    }

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
                }
            })
            rvTransactions.adapter = adapter


            totalTransactions = 0
            pageSize = 0

            transactionInfoList.clear()
            val listFromReferralFragment = callback?.getReferralTxnList(state)
            if(listFromReferralFragment != null){
                transactionInfoList.addAll(listFromReferralFragment)
            }
            updateListData("", false)

        }
    }
    


    fun updateListData(message: String, errorOccurred: Boolean) {
        Log.e("errorOccurred", "errorOccurred = $errorOccurred")
        if (errorOccurred) {
            DialogPopup.alertPopupWithListener(requireActivity(), "", message,
                    { requireActivity().onBackPressed() })

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


    interface Callback{
        fun getReferralTxnList(state:Int):MutableList<Any>?
    }

}
