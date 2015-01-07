package product.clicklabs.jugnoo.driver.utils;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Sounds Class plays sounds
 * @author shankar
 *
 */
@SuppressLint("UseSparseArrays")
public class SoundEffects {

	private static final SoundEffects INSTANCE = new SoundEffects();

	public static final int BELL_SOUND = 1; // sound constants declared

	private SoundEffects() {

	}

	public static SoundEffects getInstance() {
		return INSTANCE;
	}

	public static SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	int priority = 1;
	int no_loop = 0;
	private int volume;
	float normal_playback_rate = 1f;

	public void init(Context context) {

		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();

		soundPoolMap.put(BELL_SOUND,
				soundPool.load(context, R.raw.bell_sound, 1)); // sound raw
																// files added
																// to soundPool
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void playSound(int soundId) {
		try{
			soundPool.play(soundId, volume, volume, priority, no_loop, normal_playback_rate);
		} catch(Exception e){
		}
	}

}
