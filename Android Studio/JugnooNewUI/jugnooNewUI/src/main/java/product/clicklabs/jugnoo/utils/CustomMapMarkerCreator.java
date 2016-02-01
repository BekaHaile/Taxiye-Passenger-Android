package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import product.clicklabs.jugnoo.R;


@SuppressWarnings("static-access")
public class CustomMapMarkerCreator {
	
	public static Bitmap createCarMarkerBitmap(Activity activity, ASSL assl){
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int)(35.0f * scale);
		int height = (int)(54.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(R.drawable.auto_top);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
	public static Bitmap createPinMarkerBitmap(Activity activity, ASSL assl){
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int)(47.0f * scale);
		int height = (int)(79.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}


    public static Bitmap createPinMarkerBitmapEnd(Activity activity, ASSL assl){
        float scale = Math.min(assl.Xscale(), assl.Yscale());
        int width = (int)(47.0f * scale);
        int height = (int)(79.0f * scale);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball_end);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }

    public static Bitmap createPinMarkerBitmapStart(Activity activity, ASSL assl){
        float scale = Math.min(assl.Xscale(), assl.Yscale());
        int width = (int)(47.0f * scale);
        int height = (int)(79.0f * scale);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball_start);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }


    public static Bitmap createSmallPinMarkerBitmap(Activity activity, ASSL assl, int drawableId){
        float scale = Math.min(assl.Xscale(), assl.Yscale());
        int width = (int)(24.0f * scale);
        int height = (int)(41.0f * scale);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(drawableId);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }


	public static Bitmap getTextBitmap(final Context context, ASSL assl, final String text, final int fontSize) {
// ic_centre_pin_big

		final TextView textView = new TextView(context);
		textView.setText(text);
		textView.setTextSize(fontSize);

		final Rect boundsText = new Rect();

		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int)(77.0f * 0.85 * scale);
		int height = (int)(130.0f * 0.85 * scale);

		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

		final Paint paint = textView.getPaint();
		paint.getTextBounds(text, 0, textView.length(), boundsText);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);

		final Canvas canvasText = new Canvas(bmpText);

		Drawable shape = context.getResources().getDrawable(R.drawable.ic_centre_pin_big);
		shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
		shape.draw(canvasText);

		canvasText.drawText(text, canvasText.getWidth() / 2, (26f*assl.Yscale()), paint);
		canvasText.drawText("MIN", canvasText.getWidth() / 2, (int)(30f*assl.Yscale()) + boundsText.height(), paint);


		return bmpText;
	}

	public static Marker addTextMarkerToMap(final Context context, final GoogleMap map,
											final LatLng location, final String text, final int padding,
											final int fontSize) {
		Marker marker = null;

		if (context == null || map == null || location == null || text == null
				|| fontSize <= 0) {
			return marker;
		}

		final TextView textView = new TextView(context);
		final TextView textView2 = new TextView(context);
		textView.setText(text);
		textView.setTextSize(fontSize);
		textView2.setText(text);
		textView2.setTextSize(fontSize);

		final Rect boundsText = new Rect();

		final Paint paintText = textView.getPaint();
		paintText.getTextBounds(text, 0, textView.length(), boundsText);
		paintText.setTextAlign(Paint.Align.CENTER);
		paintText.setStyle(Paint.Style.STROKE);
		paintText.setStrokeWidth(4);
		paintText.setColor(Color.WHITE);


		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
				* padding, boundsText.height() + 2 * padding, conf);


		final Paint paintText2 = textView2.getPaint();
		paintText2.getTextBounds(text, 0, textView2.length(), boundsText);
		paintText2.setTextAlign(Paint.Align.CENTER);
		paintText2.setColor(Color.BLACK);

		final Canvas canvasText = new Canvas(bmpText);

		canvasText.drawText(text, canvasText.getWidth() / 2,
				canvasText.getHeight() - padding, paintText);
		canvasText.drawText(text, canvasText.getWidth() / 2,
				canvasText.getHeight() - padding, paintText2);

		final MarkerOptions markerOptions = new MarkerOptions()
				.position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
				.anchor(0.5f, 1);

		marker = map.addMarker(markerOptions);
		marker.setTitle("");

		return marker;
	}

}
