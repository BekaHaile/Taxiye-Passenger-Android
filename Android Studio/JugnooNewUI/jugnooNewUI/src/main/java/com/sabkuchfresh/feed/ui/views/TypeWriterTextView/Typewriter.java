package com.sabkuchfresh.feed.ui.views.TypeWriterTextView;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.AttributeSet;

public class Typewriter extends androidx.appcompat.widget.AppCompatTextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay =75; //Default 500ms delay
    private AudioManager am ;
    private boolean playSound;

    public Typewriter(Context context) {
        super(context);
        am= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

    }

    public Typewriter(Context context, AttributeSet attrs) {
        super(context, attrs);
        am= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            am.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.5f);
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    public void animateText(CharSequence text) {
        mText = text;
        if (playSound) {
            mIndex = 0;
            setText("");
            mHandler.removeCallbacks(characterAdder);
            mHandler.postDelayed(characterAdder, mDelay);
        }else{
            setText(text);
        }
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    public void clearAnimAndSetText(){
        if (playSound) {
            if(mText!=null) {
                mHandler.removeCallbacksAndMessages(null);
                setText(mText);
            }
        }else{
            setText(mText);
        }
    }

    public void setmText(CharSequence text){
        mText = text;
        setText(mText);
    }
}
