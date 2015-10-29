package com.squareup.picasso;

/**
 * Picasso's(ImageLoaderLibrary) class to crop bitmap in circular shape
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
 
public class RoundBorderTransform implements Transformation {

	private int radius;

	public RoundBorderTransform(int radius){
		this.radius = radius;
	}

	@Override
	public Bitmap transform(Bitmap source) {
		Bitmap source1 = source;
		try{
			int sizeX = source.getWidth();
			int sizeY = source.getHeight();

	 
			Bitmap bitmap = Bitmap.createBitmap(sizeX, sizeY, source.getConfig());
	 
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setAntiAlias(true);
	 
			final Rect rect = new Rect(0, 0, sizeX, sizeY);
		    final RectF rectF = new RectF(rect);
		    final float roundPx = radius;
			
		    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			source.recycle();
	 
			return bitmap;
		} catch(Exception e){
			e.printStackTrace();
		}
		return source1;
	}
 
	@Override
	public String key() {
		return "circle";
	}
}