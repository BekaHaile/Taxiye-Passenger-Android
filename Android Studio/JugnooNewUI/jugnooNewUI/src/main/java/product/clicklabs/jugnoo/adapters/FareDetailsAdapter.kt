package product.clicklabs.jugnoo.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_fare_detail_extra.view.*
import kotlinx.android.synthetic.main.list_item_fare_detail_item.view.*
import kotlinx.android.synthetic.main.list_item_fare_detail_vehicle.view.*
import product.clicklabs.jugnoo.R

class FareDetailsAdapter(val details: ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            VIEW_TYPE_VEHICLE -> {
                ViewHolderVehicle(LayoutInflater.from(parent.context).inflate(R.layout.list_item_fare_detail_vehicle, parent, false))
            }
            VIEW_TYPE_FARE_ITEM -> {
                ViewHolderFareItem(LayoutInflater.from(parent.context).inflate(R.layout.list_item_fare_detail_item, parent, false))
            }
            else -> {
                ViewHolderFareExtra(LayoutInflater.from(parent.context).inflate(R.layout.list_item_fare_detail_extra, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolderVehicle -> holder.bind((details[position] as FareVehicle).vehicle)
            is ViewHolderFareItem -> holder.bind((details[position] as FareItem).name, (details[position] as FareItem).value)
            is ViewHolderFareExtra -> holder.bind((details[position] as FareExtra).extra)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when(details.get(position)){
            is FareVehicle -> VIEW_TYPE_VEHICLE
            is FareItem -> VIEW_TYPE_FARE_ITEM
            else -> VIEW_TYPE_FARE_EXTRA
        }
    }

    inner class ViewHolderVehicle(view : View) : RecyclerView.ViewHolder(view){
        fun bind(vehicleName : String){
            itemView.tvHeader.text = vehicleName
        }
    }
    inner class ViewHolderFareItem(view : View) : RecyclerView.ViewHolder(view){
        fun bind(fareItem : String, fareValue : String){
            itemView.tvFareItem.text = fareItem
            itemView.tvFareValue.text = fareValue
        }
    }
    inner class ViewHolderFareExtra(view : View) : RecyclerView.ViewHolder(view){
        fun bind(extraDetails : String){
            itemView.tvExtra.text = extraDetails
        }
    }

    private val VIEW_TYPE_VEHICLE = 0;
    private val VIEW_TYPE_FARE_ITEM = 1;
    private val VIEW_TYPE_FARE_EXTRA = 2;

}

class FareVehicle(var vehicle:String)
class FareItem(var name:String, var value: String)
class FareExtra(var extra:String)