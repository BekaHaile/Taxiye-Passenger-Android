package product.clicklabs.jugnoo.adapters

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.adapters.ItemListener
import kotlinx.android.synthetic.main.list_item_corporate.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.Corporate

class CorporatesAdapter(private var corporates: ArrayList<Corporate>, val recyclerView:RecyclerView
                        , private val typeface: Typeface?, private  val onSelectedCallback: OnSelectedCallback) :
        RecyclerView.Adapter<CorporatesAdapter.ViewHolderCorporate>(),ItemListener {


    companion object {
        var selectedCorporateBusinessId :Int = -1
    }
    fun setList(corporates: ArrayList<Corporate>){
        this.corporates = corporates
        notifyDataSetChanged()
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val pos = recyclerView.getChildLayoutPosition(parentView!!)
        if(pos != RecyclerView.NO_POSITION){
            for(corp in corporates){
                corp.selected = false
            }
            corporates[pos].selected = true
            selectedCorporateBusinessId = corporates[pos].businessId
            onSelectedCallback.onItemSelected(corporates[pos])
            notifyDataSetChanged()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CorporatesAdapter.ViewHolderCorporate {
        return ViewHolderCorporate(LayoutInflater.from(parent.context).
                inflate(R.layout.list_item_corporate, parent, false), this)
    }

    override fun getItemCount(): Int {
        return corporates.size
    }

    override fun onBindViewHolder(holder: CorporatesAdapter.ViewHolderCorporate, position: Int) {
        holder.bind(corporates[position].partnerName, corporates[position].selected, position)
    }

    fun unSelectAll() {
        for(corporate in corporates){
            corporate.selected = false
        }
        notifyDataSetChanged()
    }

    fun selectDefault(){
        for(i  in  0 until  corporates.size){
            corporates[i].selected = corporates[i].businessId == selectedCorporateBusinessId
        }
        notifyDataSetChanged()
    }

    inner class ViewHolderCorporate(view : View, listener:ItemListener) : RecyclerView.ViewHolder(view){
        init{
            if(typeface != null) {
                itemView.tvCorporateName.typeface = typeface
            }
            itemView.linear.setOnClickListener{it->
                listener.onClickItem(it, itemView)
            }
        }
        fun bind(businessName : String, selected:Boolean, position: Int){
            itemView.tvCorporateName.text = businessName
            itemView.tvCorporateName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (selected) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked,
                    0, 0, 0)
        }
    }

    interface OnSelectedCallback{
        fun onItemSelected(corporate: Corporate)
    }

}