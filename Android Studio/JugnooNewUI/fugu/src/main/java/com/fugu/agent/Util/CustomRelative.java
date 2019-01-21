package com.fugu.agent.Util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class CustomRelative extends RelativeLayout {
    private OnKeyboardOpened mOnKeyboardOpened;

    public CustomRelative(Context context) {
        super(context);
    }

    public CustomRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomRelative(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public interface OnKeyboardOpened {
        public boolean onKeyBoardStateChanged(boolean isVisible);
    }

    public void setOnKeyBoardStateChanged(OnKeyboardOpened onKeyboardOpened) {
        mOnKeyboardOpened = onKeyboardOpened;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();

            if (actualHeight > proposedheight) {
                // Keyboard is shown
                mOnKeyboardOpened.onKeyBoardStateChanged(true);
            } else {
                // Keyboard is hidden
                mOnKeyboardOpened.onKeyBoardStateChanged(false);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}

