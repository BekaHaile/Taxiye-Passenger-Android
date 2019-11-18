package product.clicklabs.jugnoo.promotion.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_media_info.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.MediaInfo

class MediaInfoFragment:Fragment(){

    companion object{
        const val DATUM = "media_info_datum"
        val gson: Gson = Gson()

        fun newInstance(mediaInfo: MediaInfo) : MediaInfoFragment{
            val fragment = MediaInfoFragment();
            val bundle = Bundle()
            bundle.putString(DATUM, gson.toJson(mediaInfo, MediaInfo::class.java))
            fragment.arguments = bundle
            return fragment
        }

    }

    lateinit var mediaInfo: MediaInfo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if(arguments != null) {
            mediaInfo = gson.fromJson(arguments!!.getString(DATUM), MediaInfo::class.java)
        }

        return inflater.inflate(R.layout.fragment_media_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (::mediaInfo.isInitialized) {
            Picasso.with(activity)
                    .load(mediaInfo.url)
                    .placeholder(R.drawable.ic_fresh_new_placeholder)
                    .error(R.drawable.ic_fresh_new_placeholder)
                    .into(imageView)
        }

    }


}