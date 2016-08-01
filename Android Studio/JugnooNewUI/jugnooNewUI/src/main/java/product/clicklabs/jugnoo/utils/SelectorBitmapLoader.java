package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by ankit on 6/13/16.
 */
public class SelectorBitmapLoader {

    private Context context;
    private ImageView imageView;
    private Bitmap bitmapNormal, bitmapPressed;
    private Callback callback;
    private Target targetNormal, targetPressed;
    private boolean setDrawable;

    public SelectorBitmapLoader(Context context) {
        this.context = context;
        this.bitmapNormal = null;
        this.bitmapPressed = null;
        this.targetNormal = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                bitmapNormal = bitmap;
                checkAndSetSelector();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };
        this.targetPressed = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                bitmapPressed = bitmap;
                checkAndSetSelector();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };
    }

    public void loadSelector(ImageView imageView, String urlNormal, String urlPressed, Callback callback, boolean setDrawable) {
        this.setDrawable = setDrawable;
        this.imageView = imageView;
        this.bitmapNormal = null;
        this.bitmapPressed = null;
        this.callback = callback;

        Picasso.with(context).cancelRequest(targetNormal);
        Picasso.with(context).cancelRequest(targetPressed);

        Picasso.with(context).load(urlNormal)
                .into(targetNormal);

        Picasso.with(context).load(urlPressed)
                .into(targetPressed);
    }

    private void checkAndSetSelector() {
        if (bitmapNormal != null && bitmapPressed != null) {
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{android.R.attr.state_pressed},
                    new BitmapDrawable(context.getResources(), bitmapPressed));
            states.addState(new int[]{},
                    new BitmapDrawable(context.getResources(), bitmapNormal));
            if (imageView != null && setDrawable) {
                imageView.setImageDrawable(states);
            }
            if (callback != null) {
                callback.onSuccess(states);
            }
        }
    }

    public interface Callback {
        void onSuccess(Drawable drawable);
    }

}