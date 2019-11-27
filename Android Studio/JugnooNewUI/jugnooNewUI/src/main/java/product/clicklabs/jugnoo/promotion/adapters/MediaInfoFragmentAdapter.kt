package product.clicklabs.jugnoo.promotion.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import product.clicklabs.jugnoo.promotion.fragments.MediaInfoFragment
import product.clicklabs.jugnoo.retrofit.model.MediaInfo

class MediaInfoFragmentAdapter(fm: FragmentManager, var mediaInfos: MutableList<MediaInfo>? = null) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return MediaInfoFragment.newInstance(mediaInfos!![position])
    }

    override fun getCount(): Int {
        return if (mediaInfos == null) 0 else mediaInfos!!.size
    }

}
