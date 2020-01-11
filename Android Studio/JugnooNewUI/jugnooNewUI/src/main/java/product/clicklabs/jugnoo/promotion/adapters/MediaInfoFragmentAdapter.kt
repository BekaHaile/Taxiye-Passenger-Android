package product.clicklabs.jugnoo.promotion.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import product.clicklabs.jugnoo.promotion.fragments.MediaInfoFragment
import product.clicklabs.jugnoo.retrofit.model.MediaInfo

class MediaInfoFragmentAdapter(fm: androidx.fragment.app.FragmentManager, var mediaInfos: MutableList<MediaInfo>? = null) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return MediaInfoFragment.newInstance(mediaInfos!![position])
    }

    override fun getCount(): Int {
        return if (mediaInfos == null) 0 else mediaInfos!!.size
    }

}
