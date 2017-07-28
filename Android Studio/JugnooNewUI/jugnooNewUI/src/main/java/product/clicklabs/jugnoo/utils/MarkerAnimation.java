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
import android.text.TextUtils;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogModeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MarkerAnimation {

    private static final String TAG = MarkerAnimation.class.getSimpleName();
    public static ArrayList<GetDirectionsAsync> getDirectionsAsyncs = new ArrayList<>();
    private static final double ANIMATION_TIME = 14000;

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
    public static void animateMarkerToICS(String engagementId, Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim) {

        try {
            if(MapUtils.distance(marker.getPosition(), finalPosition) < 80
					|| MapUtils.distance(marker.getPosition(), finalPosition) > 2000){
                //marker.setPosition(finalPosition);
                animationForShortDistance(engagementId, marker, finalPosition, latLngInterpolator, callbackAnim);
			}
			else{
                getDirectionsAsyncs.add(new GetDirectionsAsync(engagementId, marker, finalPosition, latLngInterpolator, callbackAnim));
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
            if(callbackAnim != null){
                callbackAnim.onAnimNotDone();
            }
        }

    }


    public static void animateMarkerOnList(Marker marker, List<LatLng> list, final LatLngInterpolator latLngInterpolator){
        getDirectionsAsyncs.add(new GetDirectionsAsync("-1", marker, latLngInterpolator, null, list));
        if(getDirectionsAsyncs.size() == 1){
            getDirectionsAsyncs.get(0).execute();
        }
    }


    private static void checkAndExecute(){
        getDirectionsAsyncs.remove(0);
        if(getDirectionsAsyncs.size() > 0){
            getDirectionsAsyncs.get(0).execute();
        }
    }

    static class GetDirectionsAsync extends AsyncTask<String, String, String>{
        String engagementId;
        LatLng source, destination;
        Marker marker;
        LatLngInterpolator latLngInterpolator;
		CallbackAnim callbackAnim;
        List<LatLng> list;
        double totalDistance;

        public GetDirectionsAsync(String engagementId, Marker marker, LatLng destination, LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim){
            this.engagementId = engagementId;
            this.source = marker.getPosition();
            this.destination = destination;
            this.marker = marker;
            this.latLngInterpolator = latLngInterpolator;
			this.callbackAnim = callbackAnim;
        }

        public GetDirectionsAsync(String engagementId, Marker marker, LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim, List<LatLng> list){
            this.engagementId = engagementId;
            this.source = marker.getPosition();
            this.marker = marker;
            this.latLngInterpolator = latLngInterpolator;
            this.callbackAnim = callbackAnim;
            this.list = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if(list == null || list.size() == 0) {
                    Response response = RestClient.getGoogleApiService().getDirections(source.latitude + "," + source.longitude,
                            destination.latitude + "," + destination.longitude, false, "driving", false);
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    return responseStr;
                } else {
                    LatLng first = list.get(0);
                    totalDistance = 0;
                    for(LatLng latLng : list){
                        totalDistance = totalDistance + MapUtils.distance(first, latLng);
                        first = latLng;
                    }
                    return "";
                }
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
                    if(list==null && !TextUtils.isEmpty(result)) {
                        JSONObject jObj = new JSONObject(result);
                        totalDistance = Double.parseDouble(jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                        list = MapUtils.getLatLngListFromPath(result);
                    }

                    ArrayList<Double> duration = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (i + 1 < list.size()) {
                            double animDuration = (MapUtils.distance(list.get(i), list.get(i + 1)) / totalDistance) * ANIMATION_TIME;
                            duration.add(animDuration);
                        }
                    }
                    if (list.size() > 0) {
                        list.remove(0);
                    } else {
                        throw new Exception();
                    }
                    if (callbackAnim != null) {
                        callbackAnim.onPathFound(list);
                    }

                    animateMarkerToICSRecursive(engagementId, marker, list, latLngInterpolator, duration, true, callbackAnim);
                } else {
                    throw new Exception();
                }

            } catch (Exception e) {
                e.printStackTrace();
                checkAndExecute();
                if(callbackAnim != null){
                    callbackAnim.onAnimNotDone();
                }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animateMarkerToICSRecursive(final String engagementId, final Marker marker, final List<LatLng> list,
                                                   final LatLngInterpolator latLngInterpolator,
                                                   final List<Double> duration, final boolean rotation, final CallbackAnim callbackAnim) {
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
                        animateMarkerToICSRecursive(engagementId, marker, list, latLngInterpolator, duration, rotation, callbackAnim);
                    } else {
                        checkAndExecute();
                        if(callbackAnim != null){
                            callbackAnim.onAnimComplete();
                        }
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
                MapUtils.rotateMarker(marker, bearing);
            }
            if(Integer.parseInt(engagementId) > 0) {
                MyApplication.getInstance().getDatabase2().insertTrackingLogs(Integer.parseInt(engagementId),
                        finalPosition, bearing,
                        TrackingLogModeValue.MOVE.getOrdinal(),
                        marker.getPosition(), (long) finalDuration);
            }

            animator.start();
        }
    }

    public static void animationForShortDistance(String engagementId, final Marker marker, LatLng latLng,
                                                 final LatLngInterpolator latLngInterpolator, final CallbackAnim callbackAnim){
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
                    if(callbackAnim != null){
                        callbackAnim.onAnimComplete();
                    }
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
            if(Integer.parseInt(engagementId) > 0) {
                MyApplication.getInstance().getDatabase2().insertTrackingLogs(Integer.parseInt(engagementId),
                        latLng, bearing,
                        TrackingLogModeValue.MOVE.getOrdinal(),
                        marker.getPosition(), (long) ANIMATION_TIME);
            }


            if(callbackAnim != null) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(marker.getPosition());
                latLngs.add(latLng);
                callbackAnim.onPathFound(latLngs);
            }
        } else {
            if(callbackAnim != null){
                callbackAnim.onAnimNotDone();
            }
        }
    }


    public interface CallbackAnim {
		void onPathFound(List<LatLng> latLngs);
        void onAnimComplete();
        void onAnimNotDone();
	}


}
