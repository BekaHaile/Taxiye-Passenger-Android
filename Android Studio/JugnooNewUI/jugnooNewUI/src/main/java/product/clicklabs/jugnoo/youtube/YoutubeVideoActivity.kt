package product.clicklabs.jugnoo.youtube

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_youtube_player.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Prefs


class YoutubeVideoActivity : YouTubeFailureRecoveryActivity(){

    companion object{

        const val VIDEO_ID = "VIDEO_ID"

        @JvmStatic
        fun createIntent(context: Context, videoId:String): Intent {
            return Intent(context, YoutubeVideoActivity::class.java)
                    .putExtra(VIDEO_ID, videoId)
        }

    }

    private var videoId:String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.hasExtra(VIDEO_ID)) {
            videoId = intent.getStringExtra(VIDEO_ID)
        }

        setContentView(R.layout.activity_youtube_player)

        youtube_view.initialize(Prefs.with(this)
                .getString(Constants.KEY_CUSTOMER_YOUTUBE_API_KEY, getString(R.string.youtube_api_key)), this)

        tvSkip.setOnClickListener{
            Prefs.with(this).save(Constants.SP_YOUTUBE_TUTORIAL_SKIPPED, true)
            finish()
        }
        val content = SpannableString(tvSkip.text)
        content.setSpan(UnderlineSpan(), 0, content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSkip.text = content
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer,
                                         wasRestored: Boolean) {
        if (!wasRestored && !TextUtils.isEmpty(videoId)) {
            player.cueVideo(videoId)
            player.setPlayerStateChangeListener(object:YouTubePlayer.PlayerStateChangeListener{
                override fun onAdStarted() {
                }

                override fun onLoading() {
                }

                override fun onVideoStarted() {
                }

                override fun onLoaded(p0: String?) {
                }

                override fun onVideoEnded() {
                    tvSkip.postDelayed({ tvSkip.performClick() }, 1500)
                }

                override fun onError(p0: YouTubePlayer.ErrorReason?) {
                }

            })
        }
    }

    override fun getYouTubePlayerProvider(): YouTubePlayer.Provider {
        return youtube_view
    }

}