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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.home.models.TrackingLogModeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MarkerAnimation {

    private static final String TAG = MarkerAnimation.class.getSimpleName();
    public static ArrayList<GetDirectionsAsync> getDirectionsAsyncs = new ArrayList<>();
    private static final double ANIMATION_TIME = 15000;

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
    public static void animateMarkerToICS(String engagementId, Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {

        try {
            if(MapUtils.distance(marker.getPosition(), finalPosition) < 80
					|| MapUtils.distance(marker.getPosition(), finalPosition) > 2000){
                //marker.setPosition(finalPosition);
                animationForShortDistance(engagementId, marker, finalPosition, latLngInterpolator);
			}
			else{
                getDirectionsAsyncs.add(new GetDirectionsAsync(engagementId, marker, finalPosition, latLngInterpolator));
                if(getDirectionsAsyncs.size() == 1){
                    getDirectionsAsyncs.get(0).execute();
                }
			}
        } catch (Exception e) {
            e.printStackTrace();
            try {
                marker.setPosition(finalPosition);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    private static void checkAndExecute(){
        if(getDirectionsAsyncs.size() > 0){
            getDirectionsAsyncs.get(0).execute();
        }
    }

    static class GetDirectionsAsync extends AsyncTask<String, String, String>{
        String engagementId;
        LatLng source, destination;
        Marker marker;
        LatLngInterpolator latLngInterpolator;

        public GetDirectionsAsync(String engagementId, Marker marker, LatLng destination, LatLngInterpolator latLngInterpolator){
            this.engagementId = engagementId;
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
                Response response = RestClient.getGoogleApiServices().getDirections(source.latitude + "," + source.longitude,
                        destination.latitude + "," + destination.longitude, false, "driving", false);
                String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                return responseStr;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jObj = new JSONObject(result);
                    double totalDistance = Double.parseDouble(jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                    final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
                    final ArrayList<Double> duration = new ArrayList<>();
                    for(int i=0; i<list.size(); i++){
                        if(i+1 < list.size()) {
                            double animDuration = (MapUtils.distance(list.get(i), list.get(i + 1))/totalDistance) * ANIMATION_TIME;
                            duration.add(animDuration);
                        }
                    }
                    if(list.size() > 0) {
                        list.remove(0);
                    }
                    animateMarkerToICSRecursive(engagementId, marker, list, latLngInterpolator, duration, true);
                }
                getDirectionsAsyncs.remove(0);
                checkAndExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animateMarkerToICSRecursive(final String engagementId, final Marker marker, final List<LatLng> list,
                                                   final LatLngInterpolator latLngInterpolator,
                                                   final List<Double> duration, final boolean rotation) {
        if(list.size() > 0) {
            final LatLng finalPosition = list.remove(0);
            final double finalDuration = duration.remove(0);
            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                @Override
                public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                    return latLngInterpolator.interpolate(fraction, startValue, endValue);
                }
            };
            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
            ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
            animator.setDuration((long) (finalDuration));
            //animator.setDuration((long) (30.0d * MapUtils.distance(marker.getPosition(), finalPosition)));
            //animator.setDuration(15000);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    marker.setPosition(finalPosition);
                    if (list.size() > 0) {
                        animateMarkerToICSRecursive(engagementId, marker, list, latLngInterpolator, duration, rotation);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            float bearing  = (float) MapUtils.getBearing(marker.getPosition(), finalPosition);
            if (rotation) {
                MapUtils.rotateMarker(marker, (float) MapUtils.getBearing(marker.getPosition(), finalPosition));
            }
            MyApplication.getInstance().getDatabase2().insertTrackingLogs(Integer.parseInt(engagementId),
                    finalPosition, bearing,
                    TrackingLogModeValue.MOVE.getOrdinal(),
                    marker.getPosition(), (long)finalDuration);

            animator.start();
        }
    }

    public static void animationForShortDistance(String engagementId, final Marker marker, LatLng latLng,
                                                 final LatLngInterpolator latLngInterpolator){
        if(MapUtils.distance(marker.getPosition(), latLng) >= 20) {
            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                @Override
                public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                    return latLngInterpolator.interpolate(fraction, startValue, endValue);
                }
            };
            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
            ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, latLng);
            animator.setDuration((long) ANIMATION_TIME);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
            float bearing = (float) MapUtils.getBearing(marker.getPosition(), latLng);
            MapUtils.rotateMarker(marker, bearing);
            MyApplication.getInstance().getDatabase2().insertTrackingLogs(Integer.parseInt(engagementId),
                    latLng, bearing,
                    TrackingLogModeValue.MOVE.getOrdinal(),
                    marker.getPosition(), (long) ANIMATION_TIME);
        }
    }




}
