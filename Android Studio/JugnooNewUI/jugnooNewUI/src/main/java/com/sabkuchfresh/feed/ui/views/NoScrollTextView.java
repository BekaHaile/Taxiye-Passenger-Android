package com.sabkuchfresh.feed.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Parminder Saini on 07/06/17.
 */

public class NoScrollTextView extends TextView {
    public NoScrollTextView(Context context) {
        super(context);
    }

    public NoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void scrollTo(int x, int y) {
        //do nothing
    }
}