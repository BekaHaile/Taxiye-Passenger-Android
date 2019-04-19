package product.clicklabs.jugnoo.adapters

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sabkuchfresh.adapters.ItemListener
import kotlinx.android.synthetic.main.list_item_rental_package.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.Package

class RentalPackagesAdapter(private var context:Context, private var packages: ArrayList<Package>?,
                            private var currency:String?, private var distanceUnit:String?,
                            val recyclerView:RecyclerView
                            , private val typeface: Typeface?, private  val onSelectedCallback: OnSelectedCallback) :
        RecyclerView.Adapter<RentalPackagesAdapter.ViewHolderCorporate>(),ItemListener {


    companion object {
        var selectedPackageId :Int? = -1
    }
    fun setList(packages: ArrayList<Package>, currency:String?, distanceUnit:String?){
        this.packages = packages
        this.currency = currency
        this.distanceUnit = distanceUnit
        notifyDataSetChanged()
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val pos = recyclerView.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            for(corp in packages!!){
                corp.selected = false
            }
            packages!![pos].selected = true
            selectedPackageId = packages!![pos].packageId
            onSelectedCallback.onItemSelected(packages!![pos])
            notifyDataSetChanged()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalPackagesAdapter.ViewHolderCorporate {
        val dp5 = context.resources.getDimensionPixelSize(R.dimen.dp_5)
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rental_package, parent, false);
        val params:RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams
        params.setMargins(0, dp5, 0, dp5)
        view.layoutParams = params
        return ViewHolderCorporate(view, this)
    }

    override fun getItemCount(): Int {
        return packages?.size ?: 0
    }

    override fun onBindViewHolder(holder: RentalPackagesAdapter.ViewHolderCorporate, position: Int) {
        holder.bind(packages!![position].getPackageName(context, currency, distanceUnit), packages!![position].selected, position)
    }

    inner class ViewHolderCorporate(view : View, listener:ItemListener) : RecyclerView.ViewHolder(view){
        init{
            if(typeface != null) {
                itemView.tvPackageName.typeface = typeface
            }
            itemView.linear.setOnClickListener{it->
                listener.onClickItem(it, itemView)
            }
        }
        fun bind(businessName : String, selected:Boolean, position: Int){
            itemView.tvPackageName.text = businessName
            itemView.tvPackageName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (selected) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked,
                    0, 0, 0)
        }
    }

    interface OnSelectedCallback{
        fun onItemSelected(selectedPackage: Package)
    }

}