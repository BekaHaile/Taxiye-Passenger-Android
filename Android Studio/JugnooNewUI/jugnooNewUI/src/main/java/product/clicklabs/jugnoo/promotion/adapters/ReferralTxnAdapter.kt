package product.clicklabs.jugnoo.promotion.adapters

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_view_referral_earnings.view.*
import kotlinx.android.synthetic.main.list_item_show_more.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.promotion.models.ReferralTxn
import product.clicklabs.jugnoo.promotion.models.ReferralUser
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils


class ReferralTxnAdapter(private val context: Context, private val currency:String,
                         private val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var transactionInfoList: MutableList<Any>? = null
    private var totalTransactions: Int? = null

    fun notifyList(transactionInfoList: MutableList<Any>?, totalTransactions: Int) {
        this.transactionInfoList = transactionInfoList
        this.totalTransactions = totalTransactions
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_FOOTER) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_show_more, parent, false)
            return ViewFooterHolder(v, context)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_view_referral_earnings, parent, false)
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
            transactionInfoList!!.size
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
            itemView.tvTime.typeface = Fonts.mavenRegular(context)
            itemView.tvAmount.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD)
            itemView.tvDate.typeface = Fonts.mavenRegular(context)
            itemView.tvInfo.typeface = Fonts.mavenRegular(context)
        }

        fun bind(referralTxn: Any){
            if(referralTxn is ReferralTxn){
                itemView.tvTime.text = referralTxn.text
                itemView.tvInfo.text = DateOperations.convertDateViaFormatOnlyTime(DateOperations.utcToLocalWithTZFallback(referralTxn.creditedOn))
                itemView.tvDate.text = DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(referralTxn.creditedOn))
                itemView.tvAmount.text = Utils.formatCurrencyValue(currency, referralTxn.amount!!)
            }
            else if(referralTxn is ReferralUser){
                itemView.tvTime.text = referralTxn.userName
                if(referralTxn.referredOn != null) {
                    itemView.tvDate.text = DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(referralTxn.referredOn))
                    itemView.tvInfo.text =  DateOperations.convertDateViaFormatOnlyTime(DateOperations.utcToLocalWithTZFallback(referralTxn.referredOn))
                }
                itemView.tvAmount.text = Utils.formatCurrencyValue(currency, referralTxn.amount!!)

            }

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
