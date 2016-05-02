package com.squareup.picasso;

import android.graphics.Bitmap;
import android.os.Looper;

import java.io.IOException;

public class PicassoTools {
    public static void clearCache (Picasso p) {
        p.cache.clear();
    }

    public static void into(RequestCreator requestCreator, Target target) {
        boolean mainThread = Looper.myLooper() == Looper.getMainLooper();
        if (mainThread) {
            requestCreator.into(target);
        } else {
            try {
                Bitmap bitmap = requestCreator.get();
                if (target != null) {
                    target.onBitmapLoaded(bitmap, Picasso.LoadedFrom.MEMORY);
                }
            } catch (IOException e) {
                if (target != null) {
                    target.onBitmapFailed(null);
                }
            }
        }
    }
}