package product.clicklabs.jugnoo.adapters

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.adapters.ItemListener
import kotlinx.android.synthetic.main.list_item_ride_type.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.ServiceType
import product.clicklabs.jugnoo.utils.ASSL

class RideTypesAdapter(private var serviceTypes: ArrayList<ServiceType>, val recyclerView: androidx.recyclerview.widget.RecyclerView
                       , private val typeface: Typeface?, private  val onSelectedCallback: OnSelectedCallback) :
        androidx.recyclerview.widget.RecyclerView.Adapter<RideTypesAdapter.ViewHolderService>(),ItemListener {

    init{
        recyclerView.visibility = if(serviceTypes.size == 0||serviceTypes.size == 1) View.GONE else View.VISIBLE
    }

    fun setList(corporates: ArrayList<ServiceType>){
        this.serviceTypes = corporates
        recyclerView.visibility = if(serviceTypes.size == 0||serviceTypes.size == 1) View.GONE else View.VISIBLE
        notifyDataSetChanged()
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val pos = recyclerView.getChildLayoutPosition(parentView!!)
        if(pos != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
            for(corp in serviceTypes){
                corp.selected = false
            }
            if(!serviceTypes.isNullOrEmpty()) {
                serviceTypes[pos].selected = true
                onSelectedCallback.onServiceTypeSelected(serviceTypes[pos])
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideTypesAdapter.ViewHolderService {
        return ViewHolderService(LayoutInflater.from(parent.context).
                inflate(R.layout.list_item_ride_type, parent, false), this)
    }

    override fun getItemCount(): Int {
        return serviceTypes.size
    }

    override fun onBindViewHolder(holder: RideTypesAdapter.ViewHolderService, position: Int) {
        holder.bind(serviceTypes[position].name, serviceTypes[position].selected, position)


        val params = holder.itemView.relative.getLayoutParams() as androidx.recyclerview.widget.RecyclerView.LayoutParams
        params.width = getItemWidth()
        holder.itemView.relative.setLayoutParams(params)
    }

    inner class ViewHolderService(view : View, listener:ItemListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        init{
            if(typeface != null) {
                itemView.tvRideType.typeface = typeface
            }
            itemView.relative.setOnClickListener{it->
                listener.onClickItem(it, itemView)
            }
        }
        fun bind(name : String, selected:Boolean, position: Int){
            itemView.tvRideType.text = name
            itemView.imageViewSelected.setBackgroundColor(ContextCompat.getColor(itemView.context, if(selected) R.color.theme_color else R.color.white))
        }
    }

    private fun getItemWidth(): Int {
        val width = ((if (itemCount > 3) 700f else 720f) / (if (itemCount > 3) 3 else itemCount) * ASSL.Xscale()).toInt()
        val minWidth = (100f * ASSL.Xscale()).toInt()
        return if (width >= minWidth) width else minWidth
    }

    interface OnSelectedCallback{
        fun onServiceTypeSelected(serviceType: ServiceType)
    }

}