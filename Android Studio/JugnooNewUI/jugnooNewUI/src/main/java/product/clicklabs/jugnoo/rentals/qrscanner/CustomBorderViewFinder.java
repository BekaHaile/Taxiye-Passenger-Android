package product.clicklabs.jugnoo.rentals.qrscanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;

public class CustomBorderViewFinder extends ViewfinderView {

    private Paint borderPaint;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public CustomBorderViewFinder(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        Rect frame = framingRect;
        Rect previewFrame = previewFramingRect;

        int width = getWidth();
        int height = getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        //inside onDraw
        int distance = (frame.bottom - frame.top) / 4;
        int thickness = 10;

        //top left corner
        canvas.drawRect(frame.left - thickness, frame.top - thickness, distance + frame.left, frame.top, borderPaint);
        canvas.drawRect(frame.left - thickness, frame.top, frame.left, distance + frame.top, borderPaint);

        //top right corner
        canvas.drawRect(frame.right - distance, frame.top - thickness, frame.right + thickness, frame.top, borderPaint);
        canvas.drawRect(frame.right, frame.top, frame.right + thickness, distance + frame.top, borderPaint);

        //bottom left corner
        canvas.drawRect(frame.left - thickness, frame.bottom, distance + frame.left, frame.bottom + thickness, borderPaint);
        canvas.drawRect(frame.left - thickness, frame.bottom - distance, frame.left, frame.bottom, borderPaint);

        //bottom right corner
        canvas.drawRect(frame.right - distance, frame.bottom, frame.right + thickness, frame.bottom + thickness, borderPaint);
        canvas.drawRect(frame.right, frame.bottom - distance, frame.right + thickness, frame.bottom, borderPaint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint);
                }
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }
}
