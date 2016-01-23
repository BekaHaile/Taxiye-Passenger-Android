package com.squareup.picasso;

/**
 * Picasso's(ImageLoaderLibrary) class to crop bitmap in circular shape
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

//public class RoundBorderTransform implements Transformation {
//
//	private int radius;
//
//	public RoundBorderTransform(int radius){
//		this.radius = radius;
//	}
//
//	@Override
//	public Bitmap transform(Bitmap source) {
//		Bitmap source1 = source;
//		try{
//			int sizeX = source.getWidth();
//			int sizeY = source.getHeight();
//
//			Bitmap bitmap = Bitmap.createBitmap(sizeX, sizeY, source.getConfig());
//
//			Canvas canvas = new Canvas(bitmap);
//			Paint paint = new Paint();
//			BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
//			paint.setShader(shader);
//			paint.setAntiAlias(true);
//
//			final Rect rect = new Rect(0, 0, sizeX, sizeY);
//		    final RectF rectF = new RectF(rect);
//		    final float roundPx = radius;
//
//		    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//			source.recycle();
//
//			return bitmap;
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		return source1;
//	}
//
//	@Override
//	public String key() {
//		return "circle";
//	}
//}

// enables hardware accelerated rounded corners
// original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
public class RoundBorderTransform implements com.squareup.picasso.Transformation {
	private final int radius;
	private final int margin;  // dp

	// radius is corner radii in dp
	// margin is the board in dp
	public RoundBorderTransform(final int radius, final int margin) {
		this.radius = radius;
		this.margin = margin;
	}

	@Override
	public Bitmap transform(final Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

		if (source != output) {
			source.recycle();
		}

		return output;
	}

	@Override
	public String key() {
		return "rounded";
	}
}