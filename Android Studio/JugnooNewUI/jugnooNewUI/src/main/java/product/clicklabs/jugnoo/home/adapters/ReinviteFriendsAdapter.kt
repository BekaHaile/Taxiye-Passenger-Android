package product.clicklabs.jugnoo.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sabkuchfresh.adapters.ItemListener
import kotlinx.android.synthetic.main.list_item_friend.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.emergency.models.ContactBean
import product.clicklabs.jugnoo.utils.Fonts

class ReinviteFriendsAdapter(val rv:RecyclerView, val contactBeans:MutableList<ContactBean>, val callback: Callback)
    : RecyclerView.Adapter<ReinviteFriendsAdapter.ViewHolderFriend>(), ItemListener {

    override fun getItemCount(): Int {
        return contactBeans.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriend {
        return ViewHolderFriend(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend, parent, false), this)
    }

    override fun onBindViewHolder(holder: ViewHolderFriend, position: Int) {
        holder.bind(contactBeans[position])
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val position = rv.getChildLayoutPosition(parentView!!)
        contactBeans[position].isSelected = !contactBeans[position].isSelected
        callback.onContactSelected(position, contactBeans[position])
        notifyDataSetChanged()
    }


    class ViewHolderFriend(view: View, itemListener: ItemListener) : RecyclerView.ViewHolder(view){
        init {
            itemView.tvUserName.typeface = Fonts.mavenMedium(itemView.context)
            itemView.tvUserPhoneNo.typeface = Fonts.mavenRegular(itemView.context)
            itemView.setOnClickListener{
                itemListener.onClickItem(itemView, itemView)
            }
        }

        fun bind(contactBean: ContactBean){
            itemView.tvUserName.text = contactBean.name
            itemView.tvUserPhoneNo.text = contactBean.phoneNo
            if(contactBean.imageBitmap != null){
                itemView.ivUserImage.setImageBitmap(contactBean.imageBitmap)
            } else {
                itemView.ivUserImage.setImageResource(R.drawable.icon_user)
            }
            itemView.groupSelected.visibility = if(contactBean.isSelected) View.VISIBLE else View.GONE
        }
    }


    interface Callback{
        fun onContactSelected(position:Int, contactBean: ContactBean)
    }
}