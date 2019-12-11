package product.clicklabs.jugnoo.adapters

import android.content.Context
import android.graphics.Typeface
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
import product.clicklabs.jugnoo.utils.Utils
import java.util.*



/**
 * Created by Parminder Saini on 13/06/17.
 */

class BidsPlacedAdapter(private val context: Context,
                        private val recyclerView: androidx.recyclerview.widget.RecyclerView,
                        private val callback: Callback) : androidx.recyclerview.widget.RecyclerView.Adapter<BidsPlacedAdapter.MyViewHolder>(), ItemListener {
    private val layoutInflater: LayoutInflater
    private var bidInfos: ArrayList<BidInfo>? = null
    private val dp2: Int
    private var maxTimeDiff: Long = 60000L
    private var vehicleName:String = ""

    init {
        layoutInflater = LayoutInflater.from(context)
        dp2 = context.resources.getDimensionPixelSize(R.dimen.dp_5)
    }

    private val mBoundViewHolders = HashSet<MyViewHolder>()

    fun setList(bidInfos: ArrayList<BidInfo>?, totalBidTime: Long, vehicleName:String) {
        this.bidInfos = bidInfos
        maxTimeDiff = if (totalBidTime > 0) totalBidTime else 60000L
        this.vehicleName = vehicleName
        mBoundViewHolders.clear()
        notifyDataSetChanged()
        recyclerView.visibility = if (itemCount == 0) View.GONE else View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val convertView = layoutInflater.inflate(R.layout.list_item_bid_request, parent, false)

        val params = convertView.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
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
        holder.tvDriverName.text = if(TextUtils.isEmpty(bidInfo.driverName)) context.getString(R.string.driver) else bidInfo.driverName
        holder.tvVehicleName.text = vehicleName


        holder.tvEta.visibility = if(TextUtils.isEmpty(bidInfo.eta)) View.GONE else View.VISIBLE
        holder.tvEta.text = bidInfo.eta+context.getString(R.string.min)
        holder.tvDistance.text = bidInfo.acceptDistanceText
        holder.bidInfo = bidInfo

        mBoundViewHolders.add(holder)



    }

    override fun onViewRecycled(holder: MyViewHolder) {
        super.onViewRecycled(holder)
        mBoundViewHolders.remove(holder)
    }

    fun updateProgress() {
        for (holder in mBoundViewHolders) {
            if(holder.bidInfo != null) {
                val diff = (System.currentTimeMillis() - DateOperations.getMilliseconds(DateOperations.utcToLocalWithTZFallback(holder.bidInfo!!.createdAt)))
                val params = holder.vProgressLeft.layoutParams
                params.width = (holder.cvRoot.measuredWidth.toDouble() * ((maxTimeDiff - diff).toDouble() / maxTimeDiff.toDouble())).toInt()
                holder.vProgressLeft.layoutParams = params
                if(!callback.isBidCancelling(holder.bidInfo!!.engagementId) && (maxTimeDiff - diff) <= 1000){
                    callback.onBidCancelled(holder.bidInfo!!, false)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (bidInfos == null) 0 else bidInfos!!.size
    }

    override fun onClickItem(viewClicked: View, parentView: View) {
        val position = recyclerView.getChildAdapterPosition(parentView)
        if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
            when (viewClicked.id) {
                R.id.bAccept -> callback.onBidAccepted(bidInfos!![position])

                R.id.bCancel -> callback.onBidCancelled(bidInfos!![position], true)
            }
        }
    }


    class MyViewHolder(itemView: View, itemListener: ItemListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val cvRoot: androidx.cardview.widget.CardView
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
        var bidInfo:BidInfo?

        init {
            cvRoot = itemView.findViewById(R.id.cvRoot)
            cvRoot.measuredWidth
            vProgressLeft = itemView.findViewById(R.id.vProgressLeft)
            ivDriver = itemView.findViewById(R.id.ivDriver)
            textViewDriverRating = itemView.findViewById(R.id.textViewDriverRating)
            tvBidValue = itemView.findViewById(R.id.tvBidValue)
            tvBidValue.setTypeface(tvBidValue.typeface, Typeface.BOLD)
            tvDriverName = itemView.findViewById(R.id.tvDriverName)
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName)
            tvEta = itemView.findViewById(R.id.tvEta)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            bCancel = itemView.findViewById(R.id.bCancel)
            bAccept = itemView.findViewById(R.id.bAccept)
            bidInfo = null

            bAccept.setOnClickListener {
                itemListener.onClickItem(bAccept, itemView)
            }
            bCancel.setOnClickListener {
                itemListener.onClickItem(bCancel, itemView)
            }
        }
    }

    interface Callback {
        fun onBidAccepted(bidInfo: BidInfo)
        fun onBidCancelled(bidInfo: BidInfo, showDialog:Boolean)
        fun isBidCancelling(engagementId:Int) : Boolean
    }

}
