package product.clicklabs.jugnoo.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.sabkuchfresh.adapters.ItemListener
import com.squareup.picasso.CircleTransform
import com.squareup.picasso.Picasso
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.BidInfo
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Utils
import java.util.*

/**
 * Created by Parminder Saini on 13/06/17.
 */

class BidsPlacedAdapter(private val context: Context,
                        private val recyclerView: RecyclerView,
                        private val callback: Callback) : RecyclerView.Adapter<BidsPlacedAdapter.MyViewHolder>(), ItemListener {
    private val layoutInflater: LayoutInflater
    private var bidInfos: ArrayList<BidInfo>? = null
    private val dp2: Int
    private var maxTimeDiff:Long = 60L

    init {
        layoutInflater = LayoutInflater.from(context)
        dp2 = context.resources.getDimensionPixelSize(R.dimen.dp_5)
    }

    fun setList(bidInfos: ArrayList<BidInfo>?, totalBidTime:Long) {
        this.bidInfos = bidInfos
        maxTimeDiff = if(totalBidTime > 0) totalBidTime else 60L
        notifyDataSetChanged()
        recyclerView.visibility = if (itemCount == 0) View.GONE else View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val convertView = layoutInflater.inflate(R.layout.list_item_bid_request, parent, false)

        val params = convertView.layoutParams as RecyclerView.LayoutParams
        params.setMargins(0, dp2, 0, dp2)
        convertView.layoutParams = params

        return MyViewHolder(convertView, this)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bidInfo = bidInfos!![position]

        val dp45 = Utils.dpToPx(context, 45f)
        if (!TextUtils.isEmpty(bidInfo.driverImage)) {
            Picasso.with(context).load(bidInfo.driverImage)
                    .placeholder(R.drawable.ic_driver_placeholder)
                    .transform(CircleTransform())
                    .resize(dp45, dp45).centerCrop().into(holder.ivDriver)
        } else {
            holder.ivDriver.setImageResource(R.drawable.ic_driver_placeholder)
        }
        holder.textViewDriverRating.text = Utils.getDecimalFormat1Decimal().format(bidInfo.rating)
        holder.tvBidValue.text = Utils.formatCurrencyValue(bidInfo.currency, bidInfo.bidValue)
        holder.tvDriverName.text = bidInfo.driverName
        holder.tvVehicleName.text = bidInfo.vehicleName

        val diff = (System.currentTimeMillis() - DateOperations.getMilliseconds(DateOperations.utcToLocalWithTZFallback(bidInfo.createdAt)))/1000
        Log.w("BidPlacedAdapter", "diff="+diff)

        holder.tvEta.text = (maxTimeDiff - diff).toString()
        holder.tvDistance.text = bidInfo.acceptDistanceText

        val params = holder.vProgressLeft.layoutParams
        params.width = (holder.cvRoot.measuredWidth.toDouble()*((maxTimeDiff - diff).toDouble()/maxTimeDiff.toDouble())).toInt()
        holder.vProgressLeft.layoutParams = params

    }

    override fun getItemCount(): Int {
        return if (bidInfos == null) 0 else bidInfos!!.size
    }

    override fun onClickItem(viewClicked: View, parentView: View) {
        if (viewClicked.id == R.id.llRoot) {
            val position = recyclerView.getChildAdapterPosition(parentView)
            if (position != RecyclerView.NO_POSITION) {
                when (viewClicked.id) {
                    R.id.bAccept -> callback.onBidAccepted(bidInfos!![position])

                    R.id.bCancel -> callback.onBidCancelled(bidInfos!![position])
                }
            }
        }
    }


    class MyViewHolder(itemView: View, itemListener: ItemListener) : RecyclerView.ViewHolder(itemView) {

        val cvRoot: CardView
        val vProgressLeft: View
        val ivDriver: ImageView
        val textViewDriverRating: TextView
        val tvBidValue: TextView
        val tvDriverName: TextView
        val tvVehicleName: TextView
        val tvEta: TextView
        val tvDistance: TextView
        val bCancel: Button
        val bAccept: Button

        init {
            cvRoot = itemView.findViewById(R.id.cvRoot)
            cvRoot.measuredWidth
            vProgressLeft = itemView.findViewById(R.id.vProgressLeft)
            ivDriver = itemView.findViewById(R.id.ivDriver)
            textViewDriverRating = itemView.findViewById(R.id.textViewDriverRating)
            tvBidValue = itemView.findViewById(R.id.tvBidValue)
            tvDriverName = itemView.findViewById(R.id.tvDriverName)
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName)
            tvEta = itemView.findViewById(R.id.tvEta)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            bCancel = itemView.findViewById(R.id.bCancel)
            bAccept = itemView.findViewById(R.id.bAccept)

            bCancel.setOnClickListener { itemListener.onClickItem(bCancel, itemView) }
            bAccept.setOnClickListener { itemListener.onClickItem(bAccept, itemView) }
        }
    }

    interface Callback {
        fun onBidAccepted(bidInfo: BidInfo)
        fun onBidCancelled(bidInfo: BidInfo)
    }

}
