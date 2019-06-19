package product.clicklabs.jugnoo.adapters

import android.app.Activity
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.adapters.ItemListener
import kotlinx.android.synthetic.main.list_item_stripe_card.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.stripe.model.StripeCardData
import product.clicklabs.jugnoo.wallet.WalletCore


class StripeCardAdapter (private var stripeData: ArrayList<StripeCardData>?, val recyclerView: RecyclerView
                         , private val typeface: Typeface?, private  val onSelectedCallback: OnSelectedCallback,private val activity : Activity) :
        RecyclerView.Adapter<StripeCardAdapter.ViewHolderStripeCards>(), ItemListener {


    companion object {
        var selectedStripeCardId :Int = -1
    }
    fun setList(stripeCards: ArrayList<StripeCardData >){
        this.stripeData = stripeCards
        notifyDataSetChanged()
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val pos = recyclerView.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            for(corp in stripeData!!){
                corp.selected = false
            }
            stripeData!![pos].selected = true
            selectedStripeCardId = Integer.parseInt(stripeData!![pos].cardId);
            onSelectedCallback.onItemSelected(stripeData!![pos],pos)
            notifyDataSetChanged()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StripeCardAdapter.ViewHolderStripeCards {
        return ViewHolderStripeCards(LayoutInflater.from(parent.context).
                inflate(R.layout.list_item_stripe_card, parent, false), this)
    }

    override fun getItemCount(): Int {
        return if(stripeData == null) 0 else stripeData!!.size
    }

    override fun onBindViewHolder(holder: StripeCardAdapter.ViewHolderStripeCards, position: Int) {
        holder.bind(stripeData!![position].last4, stripeData!![position].selected, position)
    }

    fun unSelectAll() {
        for(corporate in stripeData!!){
            corporate.selected = false
        }
        notifyDataSetChanged()
    }

    fun selectDefault(){
        if(stripeData != null) {
            for (i in 0 until stripeData!!.size) {
                stripeData!![i].selected = Integer.parseInt(stripeData!![i].cardId) == selectedStripeCardId
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolderStripeCards(view : View, listener: ItemListener) : RecyclerView.ViewHolder(view){
        init{
            if(typeface != null) {
                itemView.tvStripeCardName.typeface= typeface
            }
            itemView.linearStripe.setOnClickListener{it->
                listener.onClickItem(it, itemView)
            }
        }
        fun bind(lastFour : String, selected:Boolean, position: Int){
            itemView.tvStripeCardName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (selected) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked,
                    0, 0, 0)
            itemView.tvStripeCardNumber.setCompoundDrawablesWithIntrinsicBounds(WalletCore.getBrandImage(stripeData!!.get(position).getBrand()), 0, 0, 0);
            itemView.tvStripeCardNumber.text = WalletCore.getStripeCardDisplayString(activity, lastFour)
        }
    }

    interface OnSelectedCallback{
        fun onItemSelected(stripeCards: StripeCardData ,pos : Int)
    }

}