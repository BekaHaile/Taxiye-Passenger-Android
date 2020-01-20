package product.clicklabs.jugnoo.youtube

import android.os.Bundle
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.Prefs

class YoutubeVideoFragment : YouTubePlayerFragment(), YouTubePlayer.OnInitializedListener {

    private var player: YouTubePlayer? = null
    private var videoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize(Prefs.with(activity)
                .getString(Constants.KEY_CUSTOMER_YOUTUBE_API_KEY, getString(R.string.youtube_api_key)),
                this)
    }

    override fun onDestroy() {
        if (player != null) {
            player!!.release()
        }
        super.onDestroy()
    }

    fun setVideoId(videoId: String?) {
        if (videoId != null && videoId != this.videoId) {
            this.videoId = videoId
            if (player != null) {
                player!!.cueVideo(videoId)
            }
        }
    }

    fun pause() {
        if (player != null) {
            player!!.pause()
        }
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, restored: Boolean) {
        this.player = player
        if (!restored && videoId != null) {
            player.cueVideo(videoId)
            player.setShowFullscreenButton(false)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, result: YouTubeInitializationResult) {
        this.player = null
    }


    companion object {

        fun newInstance(): YoutubeVideoFragment {
            return YoutubeVideoFragment()
        }
    }

}