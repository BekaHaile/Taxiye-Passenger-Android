package product.clicklabs.jugnoo.promotion.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_view_referral_earnings.view.*
import kotlinx.android.synthetic.main.list_item_show_more.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.promotion.models.ReferralTxn
import product.clicklabs.jugnoo.utils.ASSL
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils
import java.util.*


class ReferralTxnAdapter(private val context: Context, private val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var transactionInfoList: ArrayList<ReferralTxn>? = null
    private var totalTransactions: Int? = null

    fun notifyList(transactionInfoList: ArrayList<ReferralTxn>?, totalTransactions: Int) {
        this.transactionInfoList = transactionInfoList
        this.totalTransactions = totalTransactions
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_FOOTER) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_show_more, parent, false)

            val layoutParams = RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT)
            v.layoutParams = layoutParams

            ASSL.DoMagic(v)
            return ViewFooterHolder(v, context)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_view_referral_earnings, parent, false)

            val layoutParams = RecyclerView.LayoutParams(720, 156)
            v.layoutParams = layoutParams

            ASSL.DoMagic(v)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        if (viewholder is ViewHolder) {
            val transactionInfo = transactionInfoList!![position]
            viewholder.bind(transactionInfo)

        } else if (viewholder is ViewFooterHolder) {
            viewholder.bind()
        }

    }

    override fun getItemCount(): Int {
        return if (transactionInfoList == null || transactionInfoList!!.size == 0) {
            0
        } else {
            if (totalTransactions!! > transactionInfoList!!.size) {
                transactionInfoList!!.size + 1
            } else {
                transactionInfoList!!.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionFooter(position)) {
            TYPE_FOOTER
        } else TYPE_ITEM
    }

    private fun isPositionFooter(position: Int): Boolean {
        return position == transactionInfoList!!.size
    }



    inner class ViewHolder(convertView: View, context: Context) : RecyclerView.ViewHolder(convertView) {
        init {
            itemView.tvDate.typeface = Fonts.mavenRegular(context)
            itemView.tvAmount.typeface = Fonts.mavenRegular(context)
            itemView.tvTime.typeface = Fonts.mavenRegular(context)
            itemView.tvInfo.typeface = Fonts.mavenRegular(context)
        }

        fun bind(referralTxn: ReferralTxn){
            itemView.tvDate.text = DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(referralTxn.date))
            itemView.tvTime.text = DateOperations.convertDateViaFormatOnlyTime(DateOperations.utcToLocalWithTZFallback(referralTxn.date))
            itemView.tvAmount.text = Utils.formatCurrencyValue("INR", referralTxn.amount!!)
            itemView.tvInfo.text = referralTxn.message
        }
    }


    inner class ViewFooterHolder(convertView: View, context: Context) : RecyclerView.ViewHolder(convertView) {

        init {
            itemView.textViewShowMore.typeface = Fonts.mavenLight(context)
            itemView.textViewShowMore.text = context.resources.getString(R.string.show_more)
        }

        fun bind(){
            itemView.relativeLayoutShowMore.setOnClickListener { callback.onShowMoreClick() }
        }
    }

    interface Callback {
        fun onShowMoreClick()
    }

    companion object {

        private val TYPE_FOOTER = 2
        private val TYPE_ITEM = 1
    }

}
