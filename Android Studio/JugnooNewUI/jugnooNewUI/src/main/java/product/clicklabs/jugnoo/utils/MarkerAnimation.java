package product.clicklabs.jugnoo.utils;

/*
 * Created by socomo on 10/23/15.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogModeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MarkerAnimation {

    private static ArrayList<GetDirectionsAsync> getDirectionsAsyncs = new ArrayList<>();
    private static final double ANIMATION_TIME = 14000;
    private static final double MIN_DISTANCE = 80;
    private static final double MAX_DISTANCE = 4000;
    private static final double MAX_DISTANCE_FACTOR_GAPI = 2;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
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
    public static void animateMarkerToICS(String engagementId, Marker marker, LatLng finalPosition,
                                          final LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim,
                                          boolean animateRoute, GoogleMap googleMap, int pathResolvedColor,
                                          int untrackedPathColor, float pathWidth, boolean ignoreDistanceCheck) {

        try {
            if(ignoreDistanceCheck || MapUtils.distance(marker.getPosition(), finalPosition) < MIN_DISTANCE
					|| MapUtils.distance(marker.getPosition(), finalPosition) > MAX_DISTANCE){
                animationForShortDistance(engagementId, marker, finalPosition, latLngInterpolator, callbackAnim);
                clearPolylines();
                if(animateRoute && googleMap != null){
                    List<LatLng> list = new ArrayList<>();
                    list.add(finalPosition);
                    List<Double> durationList = new ArrayList<>();
                    durationList.add(ANIMATION_TIME);

                    PolylineOptions polylineOptions = new PolylineOptions().color(untrackedPathColor).width(pathWidth)
                            .geodesic(true).add(marker.getPosition()).add(finalPosition);
                    polylineOptions.zIndex(0);
                    polylinesUnTracked.add(googleMap.addPolyline(polylineOptions));
                    animatePath(marker.getPosition(), googleMap, list, durationList, pathResolvedColor, ASSL.Xscale() * 7f, latLngInterpolator);
                }
			}
			else{
                getDirectionsAsyncs.add(new GetDirectionsAsync(engagementId, marker, finalPosition, latLngInterpolator,
                        callbackAnim, animateRoute, googleMap, pathResolvedColor, untrackedPathColor, pathWidth));
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


    public static void animateMarkerOnList(Marker marker, List<LatLng> list, final LatLngInterpolator latLngInterpolator,
                                           GoogleMap googleMap, int pathResolvedColor, int untrackedPathColor, float pathWidth){
        getDirectionsAsyncs.add(new GetDirectionsAsync("-1", marker, latLngInterpolator, null, list, true,
                googleMap, pathResolvedColor, untrackedPathColor, pathWidth));
        Log.e("getDirectionsAsyncs.size", "="+getDirectionsAsyncs.size());
        //true, googleMap,ContextCompat.getColor(TrackOrderActivity.this,R.color.theme_color)
        if(getDirectionsAsyncs.size() == 1){
            getDirectionsAsyncs.get(0).execute();
        }
    }


    private static void checkAndExecute(){
        try {
            getDirectionsAsyncs.remove(0);
            if(getDirectionsAsyncs.size() > 0){
				getDirectionsAsyncs.get(0).execute();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class GetDirectionsAsync extends AsyncTask<String, String, String>{
        String engagementId;
        LatLng source, destination;
        Marker marker;
        LatLngInterpolator latLngInterpolator;
		CallbackAnim callbackAnim;
        List<LatLng> list;
        double totalDistance;
        boolean animateRoute;
        GoogleMap googleMap;
        int pathResolvedColor, untrackedPathColor;
        float pathWidth;

        GetDirectionsAsync(String engagementId, Marker marker, LatLng destination,
                           LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim,
                           boolean animateRoute, GoogleMap googleMap, int pathResolvedColor,
                           int untrackedPathColor, float pathWidth){
            this.engagementId = engagementId;
            this.source = marker.getPosition();
            this.destination = destination;
            this.marker = marker;
            this.latLngInterpolator = latLngInterpolator;
			this.callbackAnim = callbackAnim;
            this.animateRoute = animateRoute;
            this.googleMap = googleMap;
            this.pathResolvedColor = pathResolvedColor;
            this.untrackedPathColor = untrackedPathColor;
            this.pathWidth = pathWidth;
        }

        GetDirectionsAsync(String engagementId, Marker marker, LatLngInterpolator latLngInterpolator,
                           CallbackAnim callbackAnim, List<LatLng> list, boolean animateRoute,
                           GoogleMap googleMap, int pathResolvedColor, int untrackedPathColor, float pathWidth){
            this.engagementId = engagementId;
            this.source = marker.getPosition();
            this.marker = marker;
            this.latLngInterpolator = latLngInterpolator;
            this.callbackAnim = callbackAnim;
            this.list = list;
            this.animateRoute = animateRoute;
            this.googleMap = googleMap;
            this.pathResolvedColor = pathResolvedColor;
            this.untrackedPathColor = untrackedPathColor;
            this.pathWidth = pathWidth;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                stopCurrentAsync = false;
                if(list == null || list.size() == 0) {
                    Response response = RestClient.getGoogleApiService().getDirections(source.latitude + "," + source.longitude,
                            destination.latitude + "," + destination.longitude, false, "driving", false);
                    return new String(((TypedByteArray) response.getBody()).getBytes());
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
            if(!stopCurrentAsync) {
                try {
                    if (result != null) {
                        clearPolylines();
                        if (list == null && !TextUtils.isEmpty(result)) {
                            JSONObject jObj = new JSONObject(result);
                            totalDistance = Double.parseDouble(jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("value"));
                            if(totalDistance > MapUtils.distance(source, destination) * MAX_DISTANCE_FACTOR_GAPI){
                                list = new ArrayList<>();
                                list.add(source);
                                list.add(destination);
                                totalDistance = MapUtils.distance(source, destination);
                            } else {
                                list = MapUtils.getLatLngListFromPath(result);
                            }
                        }

                        ArrayList<Double> duration = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (i + 1 < list.size()) {
                                double animDuration = (MapUtils.distance(list.get(i), list.get(i + 1)) / totalDistance) * ANIMATION_TIME;
                                duration.add(animDuration);
                            }
                        }
                        if (list.size() > 1) {
                            LatLng lastLatLng = list.remove(0);
                            if (animateRoute && googleMap != null) {
                                List<LatLng> latLngList = new ArrayList<>();
                                latLngList.addAll(list);
                                List<Double> durationList = new ArrayList<>();
                                durationList.addAll(duration);

                                if (latLngList.size() > 0) {
                                    plotPolylinesUntracked(lastLatLng, latLngList, untrackedPathColor, pathWidth, googleMap);
                                    animatePath(lastLatLng, googleMap, latLngList, durationList, pathResolvedColor, pathWidth, latLngInterpolator);
                                }
                            }

                            if (callbackAnim != null) {
                                callbackAnim.onPathFound(list);
                            }
                            animateMarkerToICSRecursive(engagementId, marker, list, latLngInterpolator, duration, true, callbackAnim);
                        } else {
                            throw new Exception();
                        }
                    } else {
                        throw new Exception();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    checkAndExecute();
                    if (callbackAnim != null) {
                        callbackAnim.onAnimNotDone();
                    }
                }
            }
            stopCurrentAsync = false;

        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void animateMarkerToICSRecursive(final String engagementId, final Marker marker, final List<LatLng> list,
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
            objectAnimator = animator;
        }
    }

    private static void animationForShortDistance(String engagementId, final Marker marker, LatLng latLng,
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
            objectAnimator = animator;
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


    interface CallbackAnim {
		void onPathFound(List<LatLng> latLngs);
        void onAnimComplete();
        void onAnimNotDone();
	}


    private static ArrayList<Polyline> polylines = new ArrayList<>();
    private static ArrayList<Polyline> polylinesUnTracked = new ArrayList<>();
    public static void clearPolylines(){
        if(polylines != null){
            for(Polyline polyline : polylines){
                polyline.remove();
            }
            polylines.clear();
        }
        clearPolylinesUnTracked();
    }

    private static void clearPolylinesUnTracked(){
        if(polylinesUnTracked != null){
            for(Polyline polyline : polylinesUnTracked){
                polyline.remove();
            }
            polylinesUnTracked.clear();
        }
    }

    private static void animatePath(final LatLng lastLatLng, final GoogleMap googleMap, final List<LatLng> latLngList, final List<Double> durationList,
                                    final int pathResolvedColor, final float pathWidth, final LatLngInterpolator latLngInterpolator){
        if(latLngList.size() > 0 && durationList.size() > 0){
            final LatLng currLatLng = latLngList.remove(0);
            List<LatLng> latLngs = new ArrayList<>();
            latLngs.add(lastLatLng);
            latLngs.add(currLatLng);
            double duration = durationList.remove(0);
            animatorSet = new MapRouteAnimator().animateRoute(googleMap, latLngs, (long) duration, pathResolvedColor, pathWidth, latLngInterpolator,
                    new MapRouteAnimator.Callback() {
                        @Override
                        public void onAnimationEnd(Polyline foregroundPolyline) {
                            polylines.add(foregroundPolyline);
                            if(latLngList.size() > 0){
                                animatePath(currLatLng, googleMap, latLngList, durationList, pathResolvedColor, pathWidth, latLngInterpolator);
                            } else {
                                clearPolylinesUnTracked();
                            }
                        }
                    });
        }

    }

    private static void plotPolylinesUntracked(LatLng lastLatLng, List<LatLng> latLngList, int untrackedPathColor, float pathWidth, GoogleMap googleMap){
        LatLng pointer = lastLatLng;
        for(LatLng currLatLng : latLngList){
            PolylineOptions polylineOptions = new PolylineOptions().color(untrackedPathColor).width(pathWidth).geodesic(true).add(pointer).add(currLatLng);
            polylineOptions.zIndex(0);
            polylinesUnTracked.add(googleMap.addPolyline(polylineOptions));
            pointer = currLatLng;
        }
    }


    private static AnimatorSet animatorSet;
    private static ObjectAnimator objectAnimator;
    private static boolean stopCurrentAsync;
    public static void clearAsyncList(){
        getDirectionsAsyncs.clear();
        if(animatorSet != null){
            animatorSet.cancel();
        }
        if(objectAnimator != null){
            objectAnimator.cancel();
        }
        stopCurrentAsync = true;
    }

}
