package product.clicklabs.jugnoo.utils


import android.content.Context
import android.media.MediaPlayer

object SoundMediaPlayer {

    var mediaPlayer: MediaPlayer? = null
    var loopCount: Int = 0
    var runCount: Int = 0

    fun startSound(context: Context, soundFileId: Int, loopCount: Int) {
        try {
            stopSound()
            SoundMediaPlayer.loopCount = loopCount
            SoundMediaPlayer.runCount = 0
            mediaPlayer = MediaPlayer.create(context, soundFileId)
            mediaPlayer!!.setOnCompletionListener {
                try {
                    SoundMediaPlayer.runCount++
                    if (SoundMediaPlayer.runCount >= SoundMediaPlayer.loopCount) {
                        stopSound()
                    } else {
                        mediaPlayer!!.start()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mediaPlayer!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun stopSound() {
        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
            }
            SoundMediaPlayer.loopCount = 0
            SoundMediaPlayer.runCount = 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }
}
