package product.clicklabs.jugnoo.home.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sabkuchfresh.adapters.ItemListener
import com.sabkuchfresh.utils.Utils
import com.squareup.picasso.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_friend.view.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.FilteredUserDatum
import product.clicklabs.jugnoo.utils.Fonts

class ReinviteFriendsAdapter(val rv:RecyclerView, val filteredUserDatums:MutableList<FilteredUserDatum>, val callback: Callback)
    : RecyclerView.Adapter<ReinviteFriendsAdapter.ViewHolderFriend>(), ItemListener {

    val imageSize:Int by lazy{
        Utils.dpToPx(rv.context, 55)
    }

    override fun getItemCount(): Int {
        return filteredUserDatums.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriend {
        return ViewHolderFriend(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend, parent, false), this)
    }

    override fun onBindViewHolder(holder: ViewHolderFriend, position: Int) {
        holder.bind(filteredUserDatums[position])
    }

    override fun onClickItem(viewClicked: View?, parentView: View?) {
        val position = rv.getChildLayoutPosition(parentView!!)
        filteredUserDatums[position].isSelected = !filteredUserDatums[position].isSelected
        callback.onContactSelected(position, filteredUserDatums[position])
        notifyDataSetChanged()
    }


    inner class ViewHolderFriend(view: View, itemListener: ItemListener) : RecyclerView.ViewHolder(view){
        init {
            itemView.tvUserName.typeface = Fonts.mavenMedium(itemView.context)
            itemView.tvUserPhoneNo.typeface = Fonts.mavenRegular(itemView.context)
            itemView.setOnClickListener{
                itemListener.onClickItem(itemView, itemView)
            }
        }

        fun bind(userDatum: FilteredUserDatum){
            itemView.tvUserName.text = userDatum.userName
            itemView.tvUserPhoneNo.text = userDatum.userPhoneNo

            val picasso = Picasso.with(itemView.ivUserImage.context)
            val requestCreator = if(!TextUtils.isEmpty(userDatum.userImage)){
                picasso.load(userDatum.userImage)
            }
            else if(userDatum.imageUri != null){
                picasso.load(userDatum.imageUri)
            }
            else {
                picasso.load(R.drawable.icon_user)
            }
            requestCreator.resize(imageSize, imageSize)
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(itemView.ivUserImage)

            itemView.groupSelected.visibility = if(userDatum.isSelected) View.VISIBLE else View.GONE
        }
    }


    interface Callback{
        fun onContactSelected(position:Int, userDatum: FilteredUserDatum)
    }
}