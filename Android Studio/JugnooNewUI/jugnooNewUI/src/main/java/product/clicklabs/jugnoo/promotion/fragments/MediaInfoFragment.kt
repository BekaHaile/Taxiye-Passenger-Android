package product.clicklabs.jugnoo.promotion.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_media_info.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.promotion.VideoActivity
import product.clicklabs.jugnoo.retrofit.model.MediaInfo
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Prefs
import product.clicklabs.jugnoo.youtube.YoutubeVideoFragment
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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

    private val INIT_DELAY: Long = 3000
    private val DELAY: Long = 1000
    private val PERCENTAGE_CONSTANT = 100
    private val REQ_CODE_VIDEO = 9112
    private val RESULT_PAUSE = 5

    private var mScheduledExecutorService: ScheduledExecutorService? = null
    private var mCurrentposition: Int = 0
    private var updateCurrentPosition: Runnable? = null
    private val mediaController: MediaController? = null
    private val isVideoViewSet = true
    private var isVideoPaused:Boolean = false
    private var duration: Int = 0
    private var watchedVideo = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if(arguments != null) {
            mediaInfo = gson.fromJson(arguments!!.getString(DATUM), MediaInfo::class.java)
        }

        return inflater.inflate(R.layout.fragment_media_info, container, false)
    }

    private fun getVideoTimeKey() : String {
        if(::mediaInfo.isInitialized){
            return mediaInfo.getFileName().plus("_videoTime")
        } else {
            return "videoTime"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            if (::mediaInfo.isInitialized) {
                if(mediaInfo.isVideo == 1){

                    rlVideoView.visibility = View.VISIBLE
                    rlYoutubeContainer.visibility = View.GONE
                    imageView.visibility = View.GONE


                    Prefs.with(activity).save(getVideoTimeKey(), 0)
                    val url = mediaInfo.url

                    ivZoom.setOnClickListener { startActivityForResult(VideoActivity.callingIntent(requireActivity(), url!!, !simpleVideoView.isPlaying), REQ_CODE_VIDEO) }



                    updateCurrentPosition = Runnable {
                        if(simpleVideoView != null) {
                            mCurrentposition = simpleVideoView.currentPosition
                        }
                    }
                    mScheduledExecutorService = Executors.newScheduledThreadPool(1)

                    mScheduledExecutorService!!.scheduleWithFixedDelay(Runnable { simpleVideoView.post(updateCurrentPosition) }, INIT_DELAY, DELAY, TimeUnit.MILLISECONDS)


                    simpleVideoView.setVideoURI(Uri.parse(url))


                    val mediaController = MediaController(ContextThemeWrapper(activity, R.style.MediaController), false)
                    mediaController.setAnchorView(simpleVideoView)
                    //Setting MediaController and URI, then starting the videoView
                    simpleVideoView.setMediaController(mediaController)
                    simpleVideoView.requestFocus()
                    simpleVideoView.start()

                    progressWheel.visibility = View.VISIBLE
                    simpleVideoView.setOnPreparedListener { mp ->
                        Handler().postDelayed({
                            duration = TimeUnit.MILLISECONDS.toMillis(simpleVideoView.duration.toLong()).toInt()
                            val multi = (watchedVideo * duration / PERCENTAGE_CONSTANT).toLong()
                            simpleVideoView.seekTo(multi.toInt())
                        }, 400)
                        val viewGroupLevel1 = mediaController.getChildAt(0) as LinearLayout
                        //Set your color with desired transparency here:
                        viewGroupLevel1.setBackgroundColor(resources.getColor(R.color.transparent))

                        mp.setOnSeekCompleteListener { mp ->
                            Prefs.with(activity).save(getVideoTimeKey(), 0)
                            progressWheel.visibility = View.GONE
                            if (isVideoPaused) {
                                mp.pause()
                                isVideoPaused = false
                            } else {
                                mp.start()
                            }
                        }
                    }

                    simpleVideoView.setOnCompletionListener { mediaPlayer -> simpleVideoView.seekTo(0) }


                }
                else if(mediaInfo.checkIsYoutubeVideo()){
                    Log.e(MediaInfoFragment::class.java.simpleName, "mediaInfo="+mediaInfo.url)
                    Log.e(MediaInfoFragment::class.java.simpleName, "mediaInfo="+mediaInfo.checkIsYoutubeVideo())
                    Log.e(MediaInfoFragment::class.java.simpleName, "mediaInfo="+mediaInfo.getYoutubeId())

                    rlVideoView.visibility = View.GONE
                    rlYoutubeContainer.visibility = View.VISIBLE
                    imageView.visibility = View.GONE

                    val youtubeVideoFragment = YoutubeVideoFragment()
                    requireActivity().fragmentManager.beginTransaction()
                            .replace(rlYoutubeContainer.id, youtubeVideoFragment)
                            .commit()

                    val videoId = mediaInfo.getYoutubeId()
                    youtubeVideoFragment.setVideoId(videoId)

                }
                else {

                    rlVideoView.visibility = View.GONE
                    rlYoutubeContainer.visibility = View.GONE
                    imageView.visibility = View.VISIBLE

                    Picasso.with(activity)
                            .load(mediaInfo.url)
                            .placeholder(R.drawable.ic_fresh_new_placeholder)
                            .error(R.drawable.ic_fresh_new_placeholder)
                            .into(imageView)
                }
            } else {
                rlVideoView.visibility = View.GONE
                imageView.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_PAUSE && requestCode == REQ_CODE_VIDEO) {
            if (simpleVideoView != null) {
                simpleVideoView.pause()
                isVideoPaused = true
            }
        }
    }

    override fun onPause() {
        if (::mediaInfo.isInitialized && mediaInfo.isVideo == 1 && simpleVideoView != null && isVideoViewSet && duration > 0) {
            Prefs.with(activity).save(getVideoTimeKey(), simpleVideoView.currentPosition * PERCENTAGE_CONSTANT / duration)
        }
        super.onPause()
    }


    override fun onResume() {
        super.onResume()
        if (::mediaInfo.isInitialized && mediaInfo.isVideo == 1 && isVideoViewSet && simpleVideoView != null) {
            progressWheel.visibility = View.VISIBLE
            watchedVideo = Prefs.with(activity).getInt(getVideoTimeKey(), 0)
            simpleVideoView.start()
        }
    }


}