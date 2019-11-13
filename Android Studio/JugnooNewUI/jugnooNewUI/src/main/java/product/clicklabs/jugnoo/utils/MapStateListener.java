package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

public abstract class MapStateListener {

    private boolean mMapTouched = false;
    private boolean mMapSettled = false;
//    private Timer mTimer;
    private Handler handler;
    private static final int SETTLE_TIME = 500;

    private GoogleMap mMap;
    private CameraPosition mLastPosition;
    private Activity mActivity;

    public MapStateListener(GoogleMap map, TouchableMapFragment touchableMapFragment, Activity activity) {
        this.mMap = map;
        this.mActivity = activity;
        handler = new Handler();
        
        
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                onCameraPositionChanged(cameraPosition);
                unsettleMap();
                if(!mMapTouched) {
                    runSettleTimer();
                }
            }
        });

        
        touchableMapFragment.setTouchListener(new TouchableWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                touchMap();
                unsettleMap();
            }

            @Override
            public void onRelease() {
                releaseMap();
                runSettleTimer();
            }

            @Override
            public void onCancel() {
                mMapTouched = false;
                mMapSettled = true;
            }

            @Override
            public void onDoubleTap() {
                moveMap();
            	mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }

            @Override
            public void onMoveMap() {
                moveMap();
            }

            @Override
            public void onTwoFingerDoubleTap() {
                moveMap();
            	mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
            
            @Override
            public void pinchIn() {
            	mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom-0.1f));
            }
            
            @Override
            public void pinchOut() {
            	mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom+0.1f));
            }
            
        });
        
        
    }

    private void updateLastPosition() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLastPosition = MapStateListener.this.mMap.getCameraPosition();
            }
        });
    }

    private void runSettleTimer() {
        updateLastPosition();

        if(handler != null) {
            cancelRunnable = true;
            handler.removeCallbacks(runnable);
        }
        cancelRunnable = false;
        handler.postDelayed(runnable, SETTLE_TIME);
    }

    private boolean cancelRunnable = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(cancelRunnable){
                cancelRunnable = false;
                return;
            }
            CameraPosition currentPosition = MapStateListener.this.mMap.getCameraPosition();
            if (currentPosition.equals(mLastPosition)) {
                settleMap();
            }
        }
    };

    private synchronized void releaseMap() {
        if(mMapTouched) {
            mMapTouched = false;
            onMapReleased();
        }
    }

    public void touchMapExplicit(){
        onMapTouched();
        onMapReleased();
    }

    private void touchMap() {
        if(!mMapTouched) {
            if(handler != null) {
                cancelRunnable = true;
                handler.removeCallbacks(runnable);
            }
            mMapTouched = true;
            onMapTouched();
        }
    }

    public void unsettleMap() {
        if(mMapSettled) {
            if(handler != null) {
                cancelRunnable = true;
                handler.removeCallbacks(runnable);
            }
            mMapSettled = false;
            mLastPosition = null;
            onMapUnsettled();
        }
    }

    public void settleMap() {
        if(!mMapSettled) {
            mMapSettled = true;
            onMapSettled();
        }
    }

    public boolean isMapSettled(){
        return mMapSettled;
    }

    public abstract void onMapTouched();
    public abstract void onMapReleased();
    public abstract void onMapUnsettled();
    public abstract void moveMap();
    public abstract void onMapSettled();
    public abstract void onCameraPositionChanged(CameraPosition cameraPosition);
}
