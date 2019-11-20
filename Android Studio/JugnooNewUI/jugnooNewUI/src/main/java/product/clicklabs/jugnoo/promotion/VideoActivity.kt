package product.clicklabs.jugnoo.promotion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_video.*
import product.clicklabs.jugnoo.BaseFragmentActivity
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Prefs
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class VideoActivity : BaseFragmentActivity() {

    private val INIT_DELAY: Long = 3000
    private val DELAY: Long = 1000
    private val PERCENTAGE_CONSTANT = 100
    private var mCurrentposition: Int = 0
    private var updateCurrentPosition: Runnable? = null
    private val mediaController: MediaController? = null
    private val isVideoViewSet = true
    private var duration: Int = 0
    private var watchedVideo = 0
    private var mScheduledExecutorService: ScheduledExecutorService? = null
    private val RESULT_PAUSE = 5
    private var isVideoPaused : Boolean? = null
    private var fileName:String? = null

    companion object {
        fun callingIntent(context : Context, url : String, isVideoPaused : Boolean) : Intent {
            val arr = url.split("/")
            val fileName = arr[arr.size-1].split(".")[0]
            return Intent(context, VideoActivity::class.java)
                    .putExtra("url", url)
                    .putExtra("file_name", fileName)
                    .putExtra("isVideoPaused", isVideoPaused)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_video)

        val url = intent?.getStringExtra("url")
        isVideoPaused = intent?.getBooleanExtra("isVideoPaused", false)
        fileName = intent?.getStringExtra("file_name")

        updateCurrentPosition = Runnable { mCurrentposition = simpleVideoView.currentPosition }
        mScheduledExecutorService = Executors.newScheduledThreadPool(1)

        mScheduledExecutorService?.scheduleWithFixedDelay(Runnable { simpleVideoView.post(updateCurrentPosition) }, INIT_DELAY, DELAY, TimeUnit.MILLISECONDS)


        simpleVideoView.setSecure(true)

        simpleVideoView.setVideoURI(Uri.parse(url))


        val mediaController = MediaController(ContextThemeWrapper(this@VideoActivity, R.style.MediaController), false)
        mediaController.setAnchorView(simpleVideoView)
        //Setting MediaController and URI, then starting the videoView
        simpleVideoView.setMediaController(mediaController)
        simpleVideoView.requestFocus()
        simpleVideoView.start()

        progressWheel?.visibility = View.VISIBLE
        simpleVideoView.setOnPreparedListener { mp ->
            Handler().postDelayed({
                duration = TimeUnit.MILLISECONDS.toMillis(simpleVideoView.duration.toLong()).toInt()
                val multi = (watchedVideo * duration / PERCENTAGE_CONSTANT).toLong()
                simpleVideoView.seekTo(multi.toInt())
            }, 400)
            val viewGroupLevel1 = mediaController.getChildAt(0) as LinearLayout
            //Set your color with desired transparency here:
            viewGroupLevel1.setBackgroundColor(resources.getColor(R.color.transparent))

            mp.setOnSeekCompleteListener { mp1 ->
                Prefs.with(this@VideoActivity).save(getVideoTimeKey(), 0)
                progressWheel?.visibility = View.GONE
                if(isVideoPaused!!) {
                    mp1.pause()
                    isVideoPaused = false;
                } else {
                    mp1.start()
                }
            }
        }

        simpleVideoView.setOnCompletionListener { simpleVideoView.seekTo(0) }

        ivMinimize.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getVideoTimeKey() : String {
        if(!TextUtils.isEmpty(fileName)){
            return fileName.plus("_videoTime")
        } else {
            return "videoTime"
        }
    }

    override fun onBackPressed() {
        if (simpleVideoView != null && isVideoViewSet && duration > 0) {
            Prefs.with(this).save(getVideoTimeKey(), simpleVideoView.currentPosition * PERCENTAGE_CONSTANT / duration)
        }
        if(!simpleVideoView!!.isPlaying) {
            setResult(RESULT_PAUSE)
        }
        super.onBackPressed()
    }

    public override fun onPause() {
        if (simpleVideoView != null && isVideoViewSet && duration > 0) {
            Prefs.with(this).save(getVideoTimeKey(), simpleVideoView.currentPosition * PERCENTAGE_CONSTANT / duration)
        }
        super.onPause()
    }


    public override fun onResume() {
        super.onResume()
        if (isVideoViewSet && simpleVideoView != null) {
            progressWheel.visibility = View.VISIBLE
            watchedVideo = Prefs.with(this).getInt(getVideoTimeKey(), 0)
            simpleVideoView.start()
        }
    }

}