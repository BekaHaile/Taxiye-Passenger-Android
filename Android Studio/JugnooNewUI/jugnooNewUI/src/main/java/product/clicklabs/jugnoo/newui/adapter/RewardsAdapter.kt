package product.clicklabs.jugnoo.newui.adapter

import android.app.Activity
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.utils.Utils
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.promotion.models.Promo
import java.util.ArrayList

class RewardsAdapter(val recyclerView:RecyclerView, val promoList: ArrayList<Promo>, private val rewardCardListener: RewardCardListener) :
        RecyclerView.Adapter<RewardsAdapter.ViewHolderService>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderService {
        return ViewHolderService(LayoutInflater.from(parent.context).
                inflate(R.layout.item_view_rewards, parent, false))
    }

    override fun getItemCount(): Int {
        return promoList.size
    }

    override fun onBindViewHolder(holder: ViewHolderService, position: Int) {
        val params = holder.clRewards.layoutParams as RecyclerView.LayoutParams
        params.width = getItemWidth(holder.clRewards.context)
        params.topMargin = 12
        params.bottomMargin = 12
        holder.clRewards.layoutParams = params
//        holder.tvRewardInfo.text = promoList.get(position).name
//        holder.tvAmount.text = promoList.get(position).promoCoupon.title
        if(promoList[position].couponCardType == 1 && !promoList[position].isScratched){
            holder.ivScratch.visibility = View.VISIBLE
        } else {
            holder.ivScratch.visibility = View.GONE
        }

        holder.clRewards.setOnClickListener {
//            if(promoList[position].couponCardType == 1 && !promoList[position].isScratched) {
                rewardCardListener.onCardClicked(promoList[position], position)
//            }
        }
    }

    private fun getItemWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels - Utils.dpToPx(context, 50)
        return  width / 2
    }

    inner class ViewHolderService(view : View) : RecyclerView.ViewHolder(view){
        var clRewards : ConstraintLayout= view.findViewById(R.id.clRewards)
        var tvRewardInfo : AppCompatTextView= view.findViewById(R.id.tvRewardInfo)
        var tvAmount : AppCompatTextView= view.findViewById(R.id.tvAmount)
        var ivScratch : AppCompatImageView= view.findViewById(R.id.ivScratch)
    }

    interface RewardCardListener{
        fun onCardScratched()
        fun onCardClicked(promo: Promo, index: Int)
    }

}