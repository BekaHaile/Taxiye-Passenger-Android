package com.sabkuchfresh.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Parminder Singh on 29/09/16.
 */
public final class ImageCompression extends AsyncTask<String, Void, ImageCompression.CompressedImageModel[]> {
    private AsyncResponse asyncResponse;
    private Context context;
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;
    private static final int COMPRESSION_QUALITY = 80;
    public interface AsyncResponse {
        void processFinish(CompressedImageModel[] output);

        void onError();
    }
    @SuppressWarnings("unused")
    private ImageCompression() {
    }
    public ImageCompression(AsyncResponse asyncResponse, Context context) {
        this.asyncResponse = asyncResponse;
        this.context = context;
    }
    @Override
    protected CompressedImageModel[] doInBackground(String... strings) {
        if (strings.length == 0 || strings[0] == null)
            return null;
        CompressedImageModel[] compressedFiles = new CompressedImageModel[strings.length];
        for (int i = 0; i < strings.length; i++) {
            CompressedImageModel compressImagePath = compressImage(strings[i]);
            if (compressImagePath != null) {
                compressedFiles[i] = compressImagePath ;
            }
        }
        return compressedFiles;
    }
    @Override
    protected void onPostExecute(CompressedImageModel[] files) {
        super.onPostExecute(files);
        if (!isCancelled()){

            if(files!=null && files.length!=0)
            {
                asyncResponse.processFinish(files);
            }
            else{
                asyncResponse.onError();
            }

        }

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    @SuppressWarnings("deprecation")
    private CompressedImageModel compressImage(String imagePath) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError|Exception exception) {
            exception.printStackTrace();
            return null;
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError|Exception exception) {
            exception.printStackTrace();
            return null;
        }
        if (scaledBitmap == null || bmp == null)
            return null;
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        bmp.recycle();
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        FileOutputStream out;
        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new CompressedImageModel(new File(filepath),scaledBitmap.getHeight(),scaledBitmap.getWidth());
        } catch (Exception|OutOfMemoryError e) {
            e.printStackTrace();
            return null;

        }
    }
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
    private String getFilename() {
        File mediaStorageDir = new File(context.getCacheDir() + File.separator + context.getApplicationContext().getPackageName()
                + "/Compressed");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        return (mediaStorageDir.getAbsolutePath() + "/" + mImageName);
    }
   /* public File getCompressedImageFile(String filePath) {
        if (filePath != null) {
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            File imageFile = new File(filePath);
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(imageFile);
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                android.util.Log.e("Converters", "Bitmap.CompressFormat.JPEG/FileOutputStream:ParcelDetail", e);
            }
            return imageFile;
        }
        return null;
    }*/

   public class CompressedImageModel{
       public File file ;
       public  int size;
       public int width;

       public CompressedImageModel(File file, int size, int width) {
           this.file = file;
           this.size = size;
           this.width = width;
       }

       public File getFile() {
           return file;
       }

       public void setFile(File file) {
           this.file = file;
       }

       public int getSize() {
           return size;
       }

       public void setSize(int size) {
           this.size = size;
       }

       public int getWidth() {
           return width;
       }

       public void setWidth(int width) {
           this.width = width;
       }
   }
}