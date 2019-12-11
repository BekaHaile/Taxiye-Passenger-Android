package com.sabkuchfresh.feed.utils;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.ContextCompat;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;


public class BadgeDrawable extends Drawable {
 
	private Paint mBadgePaint;
	private Paint mBadgePaint1;
	private Paint mTextPaint;
	private Rect mTxtRect = new Rect();
 
	private String mCount = "";
	private boolean mWillDraw;
 
	public BadgeDrawable(Context context) {
		float mTextSize = context.getResources().getDimension(R.dimen.feed_badge_text_size);
 
		mBadgePaint = new Paint();
		mBadgePaint.setColor(ContextCompat.getColor(context,R.color.feed_color_orange));
		mBadgePaint.setAntiAlias(true);
		mBadgePaint.setStyle(Paint.Style.FILL);
		mBadgePaint1 = new Paint();
		mBadgePaint1.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.white));
		mBadgePaint1.setAntiAlias(true);
		mBadgePaint1.setStyle(Paint.Style.FILL);
 
		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);
		Typeface mavenBold = Typeface.create(Fonts.mavenRegular(context),Typeface.BOLD);
		mTextPaint.setTypeface(mavenBold);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
	}
 
	@Override
	public void draw(Canvas canvas) {
 
 
 
		if (!mWillDraw) {
			return;
		}
		Rect bounds = getBounds();
		float width = bounds.right - bounds.left;
		float height = bounds.bottom - bounds.top;
 
		// Position the badge in the top-right quadrant of the icon.
 
	        /*Using Math.max rather than Math.min */
 
		float radius = ((Math.max(width, height) / 2)) / 2;
//		float centerX = (width - radius - 1) +10;
		float centerX = width-5;
		float centerY = radius -5;
		if(mCount.length() <= 2){
			// Draw badge circle.
			canvas.drawCircle(centerX, centerY, (int)(radius+4.5), mBadgePaint1);
			canvas.drawCircle(centerX, centerY, (int)(radius+2.5), mBadgePaint);
		}
		else{
			canvas.drawCircle(centerX, centerY, (int)(radius+5.5), mBadgePaint1);
			canvas.drawCircle(centerX, centerY, (int)(radius+3.5), mBadgePaint);
//	        	canvas.drawRoundRect(radius, radius, radius, radius, 10, 10, mBadgePaint);
		}
		// Draw badge count text inside the circle.
		mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
		float textHeight = mTxtRect.bottom - mTxtRect.top;
		float textY = centerY + (textHeight / 2f);
		if(mCount.length() > 2)
			canvas.drawText("99+", centerX, textY, mTextPaint);
		else
			canvas.drawText(mCount, centerX, textY, mTextPaint);
	}
 
	/*
    Sets the count (i.e notifications) to display.
     */
	public void setCount(String count) {
		mCount = count;
 
		// Only draw a badge if there are notifications.
		mWillDraw = !count.equalsIgnoreCase("0");
		invalidateSelf();
	}
 
	@Override
	public void setAlpha(int alpha) {
		// do nothing
	}
 
	@Override
	public void setColorFilter(ColorFilter cf) {
		// do nothing
	}
 
	@Override
	public int getOpacity() {
		return PixelFormat.UNKNOWN;
	}

	public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

		BadgeDrawable badge;

		// Reuse drawable if possible
		Drawable reuse = icon.findDrawableByLayerId(R.id.item_badge);
		if (reuse != null && reuse instanceof BadgeDrawable) {
			badge = (BadgeDrawable) reuse;
		} else {
			badge = new BadgeDrawable(context);
		}
		badge.setCount(count);
		icon.mutate();
		icon.setDrawableByLayerId(R.id.item_badge, badge);
	}

}