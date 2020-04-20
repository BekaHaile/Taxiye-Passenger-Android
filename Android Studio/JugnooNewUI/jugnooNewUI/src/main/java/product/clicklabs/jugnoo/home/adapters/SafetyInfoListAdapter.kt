package product.clicklabs.jugnoo.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.textview_safety_info.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.models.SafetyPoint
import product.clicklabs.jugnoo.utils.Fonts
import java.util.*

/**
 * Created by shankar on 20/04/20.
 */
class SafetyInfoListAdapter(private var infoData: ArrayList<SafetyPoint>)
    : RecyclerView.Adapter<SafetyInfoListAdapter.ViewHolderSafetyInfo>() {


    fun setList(infoData: ArrayList<SafetyPoint>) {
        this.infoData = infoData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return infoData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSafetyInfo {
        return ViewHolderSafetyInfo(LayoutInflater.from(parent.context).inflate(R.layout.textview_safety_info, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderSafetyInfo, position: Int) {
        holder.bind(infoData[position])
    }


    inner class ViewHolderSafetyInfo(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.textView.typeface = Fonts.mavenRegular(itemView.context)
        }

        fun bind(info:SafetyPoint){
            itemView.textView.text = info.text
        }
    }

}
