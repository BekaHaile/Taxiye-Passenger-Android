package product.clicklabs.jugnoo.utils;

/*
 * Created by socomo on 10/23/15.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Property;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.MapsApiSources;
import product.clicklabs.jugnoo.directions.JungleApisImpl;
import product.clicklabs.jugnoo.home.trackinglog.TrackingLogModeValue;

public class MarkerAnimation {

    private static ArrayList<GetDirectionsAsync> getDirectionsAsyncs = new ArrayList<>();
    private static final double ANIMATION_TIME = 8000;
    private static final double FAST_ANIMATION_TIME = 1500;
    public static final double MIN_DISTANCE = 40;
    public static final double MAX_DISTANCE = 1000;
    public static final double MAX_DISTANCE_FACTOR_GAPI = 1.8;


	public static void animateMarkerToICS(String engagementId, Marker marker, LatLng finalPosition,
										  final LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim, boolean ignoreDistanceCheck) {
		animateMarkerToICS(engagementId, marker, finalPosition, latLngInterpolator, callbackAnim, false, null, 0, 0, 0, ignoreDistanceCheck);
	}

    public static void animateMarkerToICS(String engagementId, Marker marker, LatLng finalPosition,
                                          final LatLngInterpolator latLngInterpolator, CallbackAnim callbackAnim,
                                          boolean animateRoute, GoogleMap googleMap, int pathResolvedColor,
                                          int untrackedPathColor, float pathWidth, boolean ignoreDistanceCheck) {

        try {
            if(!ignoreDistanceCheck && MapUtils.distance(marker.getPosition(), finalPosition) < MIN_DISTANCE){
                return;
            }
            if(ignoreDistanceCheck || MapUtils.distance(marker.getPosition(), finalPosition) > MAX_DISTANCE){
                double duration = (ignoreDistanceCheck ? FAST_ANIMATION_TIME : ANIMATION_TIME);
                animationForShortDistance(engagementId, marker, finalPosition, latLngInterpolator, callbackAnim,
                        (long) duration);
                clearPolylines();
                if(animateRoute && googleMap != null){
                    List<LatLng> list = new ArrayList<>();
                    list.add(finalPosition);
                    List<Double> durationList = new ArrayList<>();
                    durationList.add(duration);

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


    public static void animateMarkerOnList(Marker marker, List<LatLng> list, final LatLngInterpolator latLngInterpolator, boolean animateRoute,
                                           GoogleMap googleMap, int pathResolvedColor, int untrackedPathColor, float pathWidth, CallbackAnim callback, boolean fastDuration){
        getDirectionsAsyncs.add(new GetDirectionsAsync("-1", marker, latLngInterpolator, callback, list, animateRoute,
                googleMap, pathResolvedColor, untrackedPathColor, pathWidth, fastDuration));
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
        boolean fastDuration;

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
            fastDuration = false;
        }

        GetDirectionsAsync(String engagementId, Marker marker, LatLngInterpolator latLngInterpolator,
                           CallbackAnim callbackAnim, List<LatLng> list, boolean animateRoute,
                           GoogleMap googleMap, int pathResolvedColor, int untrackedPathColor, float pathWidth, boolean fastDuration){
            this.engagementId = engagementId;
            this.source = marker.getPosition();
            this.destination = (list != null && list.size() > 0) ? list.get(list.size()-1) : null;
            this.marker = marker;
            this.latLngInterpolator = latLngInterpolator;
            this.callbackAnim = callbackAnim;
            this.list = list;
            this.animateRoute = animateRoute;
            this.googleMap = googleMap;
            this.pathResolvedColor = pathResolvedColor;
            this.untrackedPathColor = untrackedPathColor;
            this.pathWidth = pathWidth;
            this.fastDuration = fastDuration;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                stopCurrentAsync = false;
                if(Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_DIRECTIONS_FOR_DRIVER_ENROUTE, 1) == 0){
                    straightLineCase();
                }
                else if(list == null || list.size() == 0) {
					JungleApisImpl.DirectionsResult result = JungleApisImpl.INSTANCE.getDirectionsPathSync(source, destination, "metric", MapsApiSources.CUSTOMER_MARKER_ANIMATION, false);
					if(result != null){
						list = result.getLatLngs();
						totalDistance = result.getPath().getDistance();
					} else {
						straightLineCase();
					}
                    return "";
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
                straightLineCase();
            }
            return null;
        }

        private void straightLineCase() {
            fastDuration = true;
            list = new ArrayList<>();
            list.add(source);
            list.add(destination);
            totalDistance = MapUtils.distance(source, destination);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!stopCurrentAsync) {
                try {
                        clearPolylines();
                        if(list != null && destination != null
                                && totalDistance > MapUtils.distance(source, destination) * MAX_DISTANCE_FACTOR_GAPI){
                            straightLineCase();
                        } else if(list == null){
							straightLineCase();
						}

                        ArrayList<Double> duration = new ArrayList<>();
						double totalDuration = (fastDuration ? FAST_ANIMATION_TIME : ANIMATION_TIME);
                        for (int i = 0; i < list.size(); i++) {
                            if (i + 1 < list.size()) {
                                double animDuration = (MapUtils.distance(list.get(i), list.get(i + 1)) / totalDistance) * totalDuration;
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

            if(callbackAnim != null){
                callbackAnim.onTranslate(finalPosition, finalDuration);
            }
            animator.start();
            objectAnimator = animator;
        }
    }

    private static void animationForShortDistance(String engagementId, final Marker marker, LatLng latLng,
                                                  final LatLngInterpolator latLngInterpolator, final CallbackAnim callbackAnim, long duration){
        if(MapUtils.distance(marker.getPosition(), latLng) >= 20) {
            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                @Override
                public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                    return latLngInterpolator.interpolate(fraction, startValue, endValue);
                }
            };
            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
            ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, latLng);
            animator.setDuration(duration);
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
                        marker.getPosition(), duration);
            }


            if(callbackAnim != null) {
                List<LatLng> latLngs = new ArrayList<>();
                latLngs.add(marker.getPosition());
                latLngs.add(latLng);
                callbackAnim.onPathFound(latLngs);
                callbackAnim.onTranslate(latLng, duration);
            }
        } else {
            if(callbackAnim != null){
                callbackAnim.onAnimNotDone();
            }
        }
    }


    public interface CallbackAnim {
		void onPathFound(List<LatLng> latLngs);
        void onTranslate(LatLng latLng, double duration);
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
