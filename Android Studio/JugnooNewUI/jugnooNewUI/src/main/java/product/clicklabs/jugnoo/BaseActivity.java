package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;

import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.typekit.TypekitContextWrapper;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    @Override
    public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new HomeUtil().forceRTL(this);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(LocaleHelper.onAttach(newBase, LocaleHelper.getLanguage(newBase))));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Data.activityResumed = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Data.activityResumed = false;
	}

	private static final int WIDTH_PX = 200;
	private static final int HEIGHT_PX = 80;

	public static boolean isLocaleSupported(Context context, String text) {
		int w = WIDTH_PX, h = HEIGHT_PX;
		Resources resources = context.getResources();
		float scale = resources.getDisplayMetrics().density;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Bitmap orig = bitmap.copy(conf, false);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setTextSize((int) (14 * scale));

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;

		canvas.drawText(text, x, y, paint);
		boolean res = !orig.sameAs(bitmap);
		orig.recycle();
		bitmap.recycle();
		return res;
	}
}
