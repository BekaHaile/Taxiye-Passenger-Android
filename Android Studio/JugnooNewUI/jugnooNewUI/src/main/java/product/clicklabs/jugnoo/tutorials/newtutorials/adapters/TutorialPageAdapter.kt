package product.clicklabs.jugnoo.tutorials.newtutorials.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import product.clicklabs.jugnoo.R

class TutorialPageAdapter(private var dataObjectList: ArrayList<TutorialDAO>) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(collection.context).inflate(
                R.layout.tutorial_fragment,
                collection,
                false
        ) as ViewGroup
        setDataToView(
                collection.context,
                position,
                layout
        )
        collection.addView(layout)
        return layout
    }

    private fun setDataToView(pContext: Context, pPosition: Int, pLayout: View) {
        Picasso.with(pContext).load(dataObjectList[pPosition].pImageUrl)
                .placeholder(R.drawable.ic_fresh_item_placeholder).fit()
                .into(pLayout.findViewById(R.id.iv_tut) as ImageView)

    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return dataObjectList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}