package product.clicklabs.jugnoo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class SlideButtonInvert extends SeekBar {

    private Drawable thumb;
    private SlideButtonInvertListener listener;

    public SlideButtonInvert(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProgress(100);
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        this.thumb = thumb;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (thumb.getBounds().contains((int) event.getX(), (int) event.getY())) {
                super.onTouchEvent(event);
            } else
                return false;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getProgress() < 35){
                handleSlide();
            }
            else{
            	setProgress(100);
            }
        } else
            super.onTouchEvent(event);

        return true;
    }

    private void handleSlide() {
    	setProgress(0);
        listener.handleSlide();
    }

    public void setSlideButtonListener(SlideButtonInvertListener listener) {
        this.listener = listener;
    }   
}

interface SlideButtonInvertListener {
    public void handleSlide();
}


