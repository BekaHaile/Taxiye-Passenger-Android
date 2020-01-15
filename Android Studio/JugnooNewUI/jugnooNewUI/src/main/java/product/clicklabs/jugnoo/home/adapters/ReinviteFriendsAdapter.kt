package product.clicklabs.jugnoo.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.emergency.models.ContactBean

class ReinviteFriendsAdapter(val rv:RecyclerView, val contactBeans:MutableList<ContactBean>) : RecyclerView.Adapter<ReinviteFriendsAdapter.ViewHolderFriend>() {

    override fun getItemCount(): Int {
        return contactBeans.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriend {
        return ViewHolderFriend(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderFriend, position: Int) {
        holder.bind(position)
    }



    class ViewHolderFriend(view: View) : RecyclerView.ViewHolder(view){
        init {

        }

        fun bind(position:Int){

        }
    }


    interface Callback{
        fun onContactSelected(contactBean: ContactBean)
    }
}