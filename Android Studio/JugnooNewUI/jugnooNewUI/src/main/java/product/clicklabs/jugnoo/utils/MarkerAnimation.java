package product.clicklabs.jugnoo.utils;

/**
 * Created by socomo on 10/23/15.
 */
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MarkerAnimation {
    public static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void animateMarkerToHC(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, finalPosition);
                marker.setPosition(newPosition);
            }
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animateMarkerToICS(Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {

//        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
//            @Override
//            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
//                return latLngInterpolator.interpolate(fraction, startValue, endValue);
//            }
//        };
//        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
//        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
//        animator.setDuration((long) (10.0d * MapUtils.distance(marker.getPosition(), finalPosition)));
//        animator.start();
//
//
//
//        animator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });

        if(MapUtils.distance(marker.getPosition(), finalPosition) < 200){
            List<LatLng> list = new ArrayList<>();
            list.add(finalPosition);
            animateMarkerToICSRecursive(marker, list, latLngInterpolator, false);
        }
        else{
            new GetDirectionsAsync(marker, finalPosition, latLngInterpolator).execute();
        }

    }

    static class GetDirectionsAsync extends AsyncTask<String, String, String>{

        LatLng source, destination;
        Marker marker;
        LatLngInterpolator latLngInterpolator;

        public GetDirectionsAsync(Marker marker, LatLng destination, LatLngInterpolator latLngInterpolator){
            this.source = marker.getPosition();
            this.destination = destination;
            this.marker = marker;
            this.latLngInterpolator = latLngInterpolator;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = MapUtils.makeDirectionsURL(source, destination);
                String result = new HttpRequester().getJSONFromUrl(url);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
                animateMarkerToICSRecursive(marker, list, latLngInterpolator, true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animateMarkerToICSRecursive(final Marker marker, final List<LatLng> list, final LatLngInterpolator latLngInterpolator, final boolean rotation) {
        if(list.size() > 0) {
            final LatLng finalPosition = list.remove(0);
            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                @Override
                public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                    return latLngInterpolator.interpolate(fraction, startValue, endValue);
                }
            };
            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
            ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
            animator.setDuration((long) (10.0d * MapUtils.distance(marker.getPosition(), finalPosition)));
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    marker.setPosition(finalPosition);
                    if (list.size() > 0) {
                        animateMarkerToICSRecursive(marker, list, latLngInterpolator, rotation);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            if(rotation) {
                //marker.setRotation((float) MapUtils.getBearing(marker.getPosition(), finalPosition));
                MapUtils.rotateMarker(marker, (float) MapUtils.getBearing(marker.getPosition(), finalPosition));
            }
            animator.start();
        }
    }




}
