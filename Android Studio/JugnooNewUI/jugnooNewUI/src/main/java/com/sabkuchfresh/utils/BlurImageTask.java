package com.sabkuchfresh.utils;

/**
 * Created by Parminder Singh on 2/6/17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Pair;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class BlurImageTask extends AsyncTask<String, Void, Pair<Bitmap, Bitmap>> {


    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }


    private Activity context;
    private OnExecution onExecution;



    public BlurImageTask(Activity context, OnExecution onExecution) {
        this.context = context;
        this.onExecution = onExecution;
    }

    @Override
    protected Pair<Bitmap, Bitmap> doInBackground(String... params) {
        if (params != null && params[0] != null) {
            try {
                Bitmap originalBitmap = Picasso.with(context).load(params[0]).get();
                Bitmap blurredBitmap = blurRenderScript(context, originalBitmap, 25);

                return new Pair<>(originalBitmap, blurredBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else
            return null;

    }

    @Override
    protected void onPostExecute(Pair<Bitmap, Bitmap> bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && !isCancelled() && !context.isFinishing()) {

            onExecution.onLoad(bitmap.first, bitmap.second);
        }
    }


    public interface OnExecution {


        void onLoad(Bitmap originalBitmap, Bitmap blurredBitmap);

    }
}



