package product.clicklabs.jugnoo.newui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.Group
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
import java.text.SimpleDateFormat
import java.util.*

class RewardsAdapter(val recyclerView:RecyclerView, val promoList: ArrayList<Promo>, private val rewardCardListener: RewardCardListener) :
        RecyclerView.Adapter<RewardsAdapter.ViewHolderService>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderService {
        return ViewHolderService(LayoutInflater.from(parent.context).
                inflate(R.layout.item_view_rewards, parent, false))
    }

    override fun getItemCount(): Int {
        return promoList.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolderService, position: Int) {
        val params = holder.clRewards.layoutParams as RecyclerView.LayoutParams
        params.width = getItemWidth(holder.clRewards.context)
        params.topMargin = 12
        params.bottomMargin = 12
        holder.clRewards.layoutParams = params
//        holder.tvRewardInfo.text = promoList.get(position).name
//        holder.tvAmount.text = promoList.get(position).promoCoupon.title
        if(promoList[position].couponCardType == 1 && !promoList[position].isScratched){
            holder.groupScratch.visibility = View.VISIBLE
            holder.ivGift.visibility = View.VISIBLE
        } else {
            holder.groupScratch.visibility = View.GONE
            holder.ivGift.visibility = View.GONE
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date1 = sdf.parse("2019-10-24")
        val date2 = sdf.parse(sdf.format(Calendar.getInstance().time))
        if(date1 > date2) {
            holder.ivScratch.setBackgroundResource(R.drawable.ic_rewards_diwali)
            holder.ivGift.visibility = View.GONE
        } else {
            holder.ivGift.visibility = View.VISIBLE
            if (promoList[position].promoCoupon.benefitType() == 3) {
                holder.ivScratch.setBackgroundResource(R.drawable.ic_scratch_bg_cashback)
                holder.ivGift.setImageResource(R.drawable.ic_scratch_diamond)
            } else {
                holder.ivScratch.setBackgroundResource(R.drawable.ic_scratch_bg)
                holder.ivGift.setImageResource(R.drawable.ic_scratch_gift)
            }
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
        var ivGift : AppCompatImageView= view.findViewById(R.id.ivGift)
        var groupScratch : Group= view.findViewById(R.id.groupScratch)
    }

    interface RewardCardListener{
        fun onCardScratched()
        fun onCardClicked(promo: Promo, index: Int)
    }

}