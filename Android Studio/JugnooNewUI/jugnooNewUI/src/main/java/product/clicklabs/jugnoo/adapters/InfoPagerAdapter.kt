package product.clicklabs.jugnoo.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.PagerInfo

/**
 * Created by Shankar on 13/08/19.
 */

class InfoPagerAdapter(internal var mContext: Context, private var infoList: List<PagerInfo>?) : PagerAdapter() {
    internal var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return infoList!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    @Synchronized
    fun setList(mResources: List<PagerInfo>) {
        this.infoList = mResources
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.info_pager_item, container, false)

        val info = infoList!![position]

        val iv = itemView.findViewById(R.id.iv) as ImageView
        val tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
        val tvMessage = itemView.findViewById(R.id.tvMessage) as TextView
        if(!TextUtils.isEmpty(info.image)){
            Picasso.with(mContext).load(info.image)
                    .placeholder(R.drawable.ic_fresh_new_placeholder)
                    .error(R.drawable.ic_fresh_new_placeholder)
                    .fit()
                    .into(iv)
        }

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
