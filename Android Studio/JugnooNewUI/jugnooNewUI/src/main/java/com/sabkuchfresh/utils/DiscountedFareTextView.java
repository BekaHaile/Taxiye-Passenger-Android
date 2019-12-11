package com.sabkuchfresh.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by Parminder Singh on 2/13/17.
 */

/**
 * Draws diagnol line across the textview
 */
public class DiscountedFareTextView extends AppCompatTextView {

    private Paint paint;

    public DiscountedFareTextView(Context context) {
        super(context);

    }

    public DiscountedFareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (paint==null) {
            paint = new Paint();
            paint.setColor(ContextCompat.getColor(getContext(), R.color.text_color_light));
            paint.setStyle(Paint.Style.FILL);
            paint.setStrikeThruText(true);
            paint.setStrokeWidth((int) (ASSL.Yscale() * 2.0f));
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, ((float)getHeight())* 0.5f, getWidth(), ((float)getHeight())* 0.5f, paint);
    }


}
