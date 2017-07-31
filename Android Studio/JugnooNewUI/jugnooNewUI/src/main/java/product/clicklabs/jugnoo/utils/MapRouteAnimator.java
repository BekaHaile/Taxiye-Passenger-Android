package product.clicklabs.jugnoo.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Thanks a lot bro for this class
 * Created by amal.chandran on 22/12/16.
 */

public class MapRouteAnimator {

    private static MapRouteAnimator mapRouteAnimator;

    private Polyline foregroundPolyline;

    private AnimatorSet firstRunAnimSet;
    private List<LatLng> latLngList;

    private MapRouteAnimator(){
    }

    public static MapRouteAnimator getInstance(){
        if(mapRouteAnimator == null) mapRouteAnimator = new MapRouteAnimator();
        return mapRouteAnimator;
    }


    public void animateRoute(GoogleMap googleMap, List<LatLng> latLngList, long duration, int pathResolvedColor, float pathWidth, LatLngInterpolator latLngInterpolator) {
        if (firstRunAnimSet == null){
            firstRunAnimSet = new AnimatorSet();
        } else {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();

            firstRunAnimSet = new AnimatorSet();
        }
        //Reset the polylines
        if (foregroundPolyline != null) foregroundPolyline.remove();

        this.latLngList = new ArrayList<>();
        this.latLngList.addAll(latLngList);

        PolylineOptions optionsForeground = new PolylineOptions().add(this.latLngList.get(0)).color(pathResolvedColor).width(pathWidth).geodesic(true);
        foregroundPolyline = googleMap.addPolyline(optionsForeground);


        ObjectAnimator foregroundRouteAnimator = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(latLngInterpolator), this.latLngList.toArray());
        foregroundRouteAnimator.setInterpolator(new LinearInterpolator());
        foregroundRouteAnimator.setDuration(duration);
        foregroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        firstRunAnimSet.play(foregroundRouteAnimator);
        firstRunAnimSet.start();
    }

    /**
     * This will be invoked by the ObjectAnimator multiple times. Mostly every 16ms.
     **/
    public void setRouteIncreaseForward(LatLng endLatLng) {
        List<LatLng> foregroundPoints = foregroundPolyline.getPoints();
        foregroundPoints.add(endLatLng);
        foregroundPolyline.setPoints(foregroundPoints);
    }

    private class RouteEvaluator implements TypeEvaluator<LatLng> {
        LatLngInterpolator latLngInterpolator;
        RouteEvaluator(LatLngInterpolator latLngInterpolator){
            this.latLngInterpolator = latLngInterpolator;
        }

        @Override
        public LatLng evaluate(float t, LatLng startPoint, LatLng endPoint) {
//            double lat = startPoint.latitude + t * (endPoint.latitude - startPoint.latitude);
//            double lng = startPoint.longitude + t * (endPoint.longitude - startPoint.longitude);
//            return new LatLng(lat,lng);
            return latLngInterpolator.interpolate(t, startPoint, endPoint);
        }
    }

}

