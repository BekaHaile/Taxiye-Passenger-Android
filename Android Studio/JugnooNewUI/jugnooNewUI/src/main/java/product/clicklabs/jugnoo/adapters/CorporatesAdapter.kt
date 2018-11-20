package product.clicklabs.jugnoo.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.adapters.ItemListener
import kotlinx.android.synthetic.main.list_item_corporate.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.Corporate

class CorporatesAdapter(private var corporates: ArrayList<Corporate>, val recyclerView:RecyclerView) :
        RecyclerView.Adapter<CorporatesAdapter.ViewHolderCorporate>(),ItemListener {

    fun setList(corporates: ArrayList<Corporate>){
        this.corporates = corporates
        notifyDataSetChanged()
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val pos = recyclerView.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            for(corp in corporates){
                corp.selected = false
            }
            corporates[pos].selected = true
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CorporatesAdapter.ViewHolderCorporate {
        return ViewHolderCorporate(LayoutInflater.from(parent.context).inflate(R.layout.list_item_corporate, parent, false), this)
    }

    override fun getItemCount(): Int {
        return corporates.size
    }

    override fun onBindViewHolder(holder: CorporatesAdapter.ViewHolderCorporate, position: Int) {
        holder.bind(corporates[position].partnerName, corporates[position].selected, position)
    }

    inner class ViewHolderCorporate(view : View, listener:ItemListener) : RecyclerView.ViewHolder(view){
        init{
            itemView.linear.setOnClickListener{it->
                listener.onClickItem(it, itemView)
            }
        }
        fun bind(businessName : String, selected:Boolean, position: Int){
            itemView.tvCorporateName.text = businessName
            itemView.tvCorporateName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (selected) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked,
                    0, 0, 0)
            itemView.viewDivider.visibility = if(position == corporates.size-1) View.GONE else View.VISIBLE
        }
    }

}