package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import product.clicklabs.jugnoo.R;


@SuppressWarnings("static-access")
public class CustomMapMarkerCreator {

	public static Bitmap createMarkerBitmapForResource(Activity activity, int resourceId){
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		int width = (int)(49.0f * scale);
		int height = (int)(62.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(resourceId);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}

    public static Bitmap createMarkerBitmapForResource(Activity activity, int resourceId, float originalWidth, float originalHeight){
        float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
        int width = (int)(originalWidth * scale);
        int height = (int)(originalHeight * scale);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(resourceId);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }


    public static Bitmap createPinMarkerBitmapEnd(Activity activity){
        float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
        int width = (int)(46.0f * scale * 0.85f);
        int height = (int)(94.0f * scale * 0.85f);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball_end);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }

    public static Bitmap createPinMarkerBitmapStart(Activity activity){
        float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
        int width = (int)(46.0f * scale * 0.85f);
        int height = (int)(94.0f * scale * 0.85f);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball_start);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }


    public static Bitmap createSmallPinMarkerBitmap(Activity activity, int drawableId){
        float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
        int width = (int)(46.0f * scale * 0.65f);
        int height = (int)(94.0f * scale * 0.65f);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(drawableId);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }


	public static Bitmap getTextBitmap(final Context context, String text, final int fontSize) {
// ic_centre_pin_big
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		final TextView textView = new TextView(context);

		String textToWrite = text==null?"--":text;
		textView.setText(textToWrite);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(scale * (float)fontSize));

		final Rect boundsText = new Rect();


		int width = (int)(95.0f * 0.85 * scale);
		int height = (int)(166.0f * 0.85 * scale);

		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

		final Paint paint = textView.getPaint();
		paint.getTextBounds(textToWrite, 0, textView.length(), boundsText);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);

		final Canvas canvasText = new Canvas(bmpText);

		Drawable shape = context.getResources().getDrawable(R.drawable.ic_centre_pin_big);
		shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
		shape.draw(canvasText);
		if(text!=null){
			String minsText = context.getString(R.string.min).toUpperCase();
			try{
				minsText = Math.round(Double.parseDouble(text)) > 1 ? context.getString(R.string.mins).toUpperCase() : context.getString(R.string.min).toUpperCase();
			} catch (Exception e){}
			int extraMargin = text.equalsIgnoreCase("-") ? (int)(10f* ASSL.Yscale()):0;
			canvasText.drawText(minsText, canvasText.getWidth() / 2, (int)(37f*ASSL.Yscale()) + boundsText.height() + extraMargin, paint);
			canvasText.drawText(textToWrite, canvasText.getWidth() / 2, (31f*ASSL.Yscale()), paint);

		}else{
			canvasText.drawText(textToWrite, canvasText.getWidth() / 2, (31f*ASSL.Yscale()), paint);

		}




		return bmpText;
	}
	public static Bitmap getTextBitmap(final Context context, String value, String suffix, final int fontSize) {
// ic_centre_pin_big
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		final TextView textView = new TextView(context);

		String textToWrite = value==null?"--":value;
		textView.setText(textToWrite);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(scale * (float)fontSize));

		final Rect boundsText = new Rect();


		int width = (int)(95.0f * 0.85 * scale);
		int height = (int)(160.0f * 0.85 * scale);

		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

		final Paint paint = textView.getPaint();
		paint.getTextBounds(textToWrite, 0, textView.length(), boundsText);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);

		final Canvas canvasText = new Canvas(bmpText);

		Drawable shape = context.getResources().getDrawable(R.drawable.ic_centre_pin_big);
		shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
		shape.draw(canvasText);
		if(value!=null){
			canvasText.drawText(suffix, canvasText.getWidth() / 2, (int)(37f*ASSL.Yscale()) + boundsText.height(), paint);
			canvasText.drawText(textToWrite, canvasText.getWidth() / 2, (31f*ASSL.Yscale()), paint);

		}else{
			canvasText.drawText(textToWrite, canvasText.getWidth() / 2, (31f*ASSL.Yscale()), paint);

		}




		return bmpText;
	}


	public static Bitmap getSavedAddressBitmap(final Context context, String text, final int fontSize, int icon,
											   int colorResId) {
// ic_centre_pin_big
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		final TextView textView = new TextView(context);
		textView.setText(text);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(scale * (float)fontSize));
		textView.setTypeface(Fonts.mavenMedium(context));

		final Rect boundsText = new Rect();


		int width = (int)(200.0f * scale);
		int height = (int)(49.0f * scale);

		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

		final Paint paint = textView.getPaint();
		paint.getTextBounds(text, 0, textView.length(), boundsText);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setColor(context.getResources().getColor(colorResId));

		final Canvas canvasText = new Canvas(bmpText);

		Drawable shape = context.getResources().getDrawable(icon);
		shape.setBounds(0, 0, (int)(42.0f * scale), bmpText.getHeight());
		shape.draw(canvasText);

		canvasText.drawText(text, (ASSL.Xscale() * 42f), (31f*ASSL.Yscale()), paint);


		return bmpText;
	}

	public static Bitmap getTextAssignBitmap(final Context context, String text, final int fontSize) {
// ic_centre_pin_big
		float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
		final TextView textView = new TextView(context);
		textView.setText(text);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (scale * (float) fontSize));
		textView.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);

		final TextView textView1 = new TextView(context);
		textView1.setText(text);
		textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (scale * (float) fontSize));
		textView1.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);

		final Rect boundsText = new Rect();


		int width = (int)(186.0f * scale * 0.85f);
		int height = (int)(173.0f * scale * 0.85f);

		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

		final Paint paint = textView.getPaint();
		paint.getTextBounds(text, 0, textView.length(), boundsText);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(context.getResources().getColor(R.color.text_color));

		final Paint paint1 = textView1.getPaint();
		paint1.getTextBounds(text, 0, textView1.length(), boundsText);
		paint1.setTextAlign(Paint.Align.CENTER);
		paint1.setColor(context.getResources().getColor(R.color.theme_color));

		final Canvas canvasText = new Canvas(bmpText);

		Drawable shape = context.getResources().getDrawable(R.drawable.ic_eta_callout);
		shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
		shape.draw(canvasText);

		canvasText.drawText(context.getString(R.string.eta)+": ", (45f * ASSL.Xscale()), (34f * ASSL.Yscale()), paint);
		canvasText.drawText(text+" "+context.getString(R.string.min).toUpperCase(), (105f * ASSL.Xscale()), (int)(34f*ASSL.Yscale()), paint1);


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
