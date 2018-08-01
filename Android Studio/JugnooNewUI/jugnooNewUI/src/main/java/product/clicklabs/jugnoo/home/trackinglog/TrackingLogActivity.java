package product.clicklabs.jugnoo.home.trackinglog;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LatLngInterpolator;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.MarkerAnimation;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class TrackingLogActivity extends BaseFragmentActivity {

    private final String TAG = TrackingLogActivity.class.getSimpleName();
    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    LinearLayout linearLayoutForm;
    EditText editTextEngagementId;
    Button buttonSubmit;
    RecyclerView recyclerViewLogs;
    TrackingLogDataAdapter trackingLogDataAdapter;

    RelativeLayout relativeLayoutMap;
    GoogleMap map;

    ArrayList<TrackingLogReponse.Datum> data = new ArrayList<>();
    ScreenState state;
    ASSL assl;
    Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
        enableMapMyLocation(map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_log);

        relative = (LinearLayout) findViewById(R.id.relative);
        assl = new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        linearLayoutForm = (LinearLayout) findViewById(R.id.linearLayoutForm);
        editTextEngagementId = (EditText) findViewById(R.id.editTextEngagementId);
        editTextEngagementId.setTypeface(Fonts.mavenMedium(this));
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

        recyclerViewLogs = (RecyclerView) findViewById(R.id.recyclerViewLogs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLogs.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLogs.setHasFixedSize(false);
        trackingLogDataAdapter = new TrackingLogDataAdapter(this, data, new TrackingLogDataAdapter.Callback() {
            @Override
            public void onItemClicked(TrackingLogReponse.Datum datum) {
                try {
                    JSONObject jsonObject = new JSONObject(datum.getData());
                    state = ScreenState.MAP;
                    switchScreenState(state);
                    displayDataOnMap(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerViewLogs.setAdapter(trackingLogDataAdapter);

        relativeLayoutMap = (RelativeLayout) findViewById(R.id.relativeLayoutMap);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (map != null) {
                    map.getUiSettings().setAllGesturesEnabled(true);
                    map.getUiSettings().setZoomControlsEnabled(true);
                    enableMapMyLocation(map);

                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        state = ScreenState.FORM;
        switchScreenState(state);

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String engagementId = editTextEngagementId.getText().toString().trim();
                if(TextUtils.isEmpty(engagementId)){
                    editTextEngagementId.requestFocus();
                    editTextEngagementId.setError(getString(R.string.empty_engagement_id));
                } else if(!Utils.checkIfOnlyDigits(engagementId)){
                    editTextEngagementId.requestFocus();
                    editTextEngagementId.setError(getString(R.string.invalid_engagement_id));
                } else{
                    Utils.hideSoftKeyboard(TrackingLogActivity.this, editTextEngagementId);
                    customerFetchRideLogAPI(TrackingLogActivity.this, engagementId);
                }
            }
        });

    }

    private void enableMapMyLocation(GoogleMap googleMap) {
        if(googleMap != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    public void performBackPressed() {
        if(state == ScreenState.MAP){
            state = ScreenState.RECYCLER;
            switchScreenState(state);
        }
        else if(state == ScreenState.RECYCLER){
            state = ScreenState.FORM;
            switchScreenState(state);
        }
        else {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

    private enum ScreenState{
        FORM, RECYCLER, MAP
    }

    private void switchScreenState(ScreenState state){

        switch(state){
            case FORM:
                linearLayoutForm.setVisibility(View.VISIBLE);
                recyclerViewLogs.setVisibility(View.GONE);
                relativeLayoutMap.setVisibility(View.GONE);
                data.clear();
                trackingLogDataAdapter.notifyDataSetChanged();
                break;

            case RECYCLER:
                linearLayoutForm.setVisibility(View.GONE);
                recyclerViewLogs.setVisibility(View.VISIBLE);
                relativeLayoutMap.setVisibility(View.GONE);
                break;

            case MAP:
                linearLayoutForm.setVisibility(View.GONE);
                recyclerViewLogs.setVisibility(View.GONE);
                relativeLayoutMap.setVisibility(View.VISIBLE);
                break;
        }

    }


    public void customerFetchRideLogAPI(final Activity activity, final String engagementId) {
        if(MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
            params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);

            new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().customerFetchRideLog(params, new Callback<TrackingLogReponse>() {
                @Override
                public void success(TrackingLogReponse trackingLogReponse, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "customerFetchRideLog response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                data.clear();

                                if(trackingLogReponse.getIosData() != null
                                        && trackingLogReponse.getIosData().size() > 0){
                                    TrackingLogReponse.Datum datum = trackingLogReponse.new Datum();
                                    datum.setLabel(getString(R.string.ios_data));
                                    datum.setViewType(TrackingLogReponse.ViewType.LABEL);
                                    data.add(datum);
                                    for(TrackingLogReponse.Datum datum1 : trackingLogReponse.getIosData()){
                                        datum1.setViewType(TrackingLogReponse.ViewType.DATA);
                                        data.add(datum1);
                                    }
                                }

                                if(trackingLogReponse.getAndroidData() != null
                                        && trackingLogReponse.getAndroidData().size() > 0){
                                    TrackingLogReponse.Datum datum = trackingLogReponse.new Datum();
                                    datum.setLabel(getString(R.string.android_data));
                                    datum.setViewType(TrackingLogReponse.ViewType.LABEL);
                                    data.add(datum);
                                    for(TrackingLogReponse.Datum datum1 : trackingLogReponse.getAndroidData()){
                                        datum1.setViewType(TrackingLogReponse.ViewType.DATA);
                                        data.add(datum1);
                                    }
                                }

                                if(data.size() > 0) {
                                    state = ScreenState.RECYCLER;
                                    switchScreenState(state);
                                    trackingLogDataAdapter.notifyDataSetChanged();
                                } else{
                                    DialogPopup.alertPopup(activity, "", getString(R.string.no_data_available));
                                }

                            } else {
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "customerFetchRideLog error=" + error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }


    private void displayDataOnMap(JSONObject jsonObject){
        try {
            if(map != null){
                map.clear();
				JSONArray jDriverLocations = jsonObject.getJSONArray(Constants.KEY_DRIVER_LOCATIONS);
                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                List<LatLng> driverLatLngs = new ArrayList<>();
	            for(int i=0; i<jDriverLocations.length(); i++){
                    LatLng latLng = new LatLng(jDriverLocations.getJSONObject(i).getDouble(Constants.KEY_LAT),
                            jDriverLocations.getJSONObject(i).getDouble(Constants.KEY_LONG));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(String.valueOf(i+1));
                    markerOptions.zIndex(0);
                    map.addMarker(markerOptions);
                    builder.include(latLng);
                    driverLatLngs.add(latLng);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            float ratio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), (int)(80.0f*ratio)), 500, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);


                JSONArray jTrackingLogs = jsonObject.getJSONArray(Constants.KEY_TRACKING_LOGS);
                if(jTrackingLogs.length() > 0){
                    JSONObject jTrackingLog0 = jTrackingLogs.getJSONObject(0);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(getString(R.string.driver));
                    markerOptions.position(new LatLng(jTrackingLog0.getDouble(Constants.KEY_LAT), jTrackingLog0.getDouble(Constants.KEY_LONG)));
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.zIndex(1);
                    markerOptions.rotation((float) jTrackingLog0.getDouble(Constants.KEY_BEARING));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
                            .createMarkerBitmapForResource(this, R.drawable.ic_auto_marker)));
                    final Marker driverMarker = map.addMarker(markerOptions);

                    Gson gson = new Gson();
                    TrackingLogData trackingLogData = gson.fromJson(jsonObject.toString(), TrackingLogData.class);
                    List<TrackingLogItem> trackingLogItems = trackingLogData.getTrackingLogs();



                    final List<TrackingLogItem> finalTrackingLogItems = new ArrayList<>();
                    for(LatLng latLng : driverLatLngs){
                        double dist = Double.MAX_VALUE;
                        TrackingLogItem itemMatched = null;
                        for(int i=0; i<trackingLogItems.size(); i++){
                            TrackingLogItem item = trackingLogItems.get(i);
                            double distI = MapUtils.distance(latLng, item.getLatLng());
                            if(distI <= dist){
                                dist = distI;
                                itemMatched = item;
                            }
                        }
                        if(itemMatched != null){
                            finalTrackingLogItems.add(itemMatched);
                        }
                    }

//                    animateMarkerICSRecursive(driverMarker, trackingLogItems, new LatLngInterpolator.Spherical());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(state == ScreenState.MAP) {
                                MarkerAnimation.clearAsyncList();
                                animateMarkerICSRecursive(driverMarker, finalTrackingLogItems);
                            }
                        }
                    }, 1100);

                }
			}
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast(this, getString(R.string.some_error_occured)+" "+e.getLocalizedMessage()+":"+e.getMessage()+":"+e.getCause());
        }
    }


    private void animateMarkerICSRecursive(final Marker marker, final List<TrackingLogItem> trackingLogItems){
        if(state == ScreenState.MAP && trackingLogItems.size() > 0) {
            final TrackingLogItem trackingLogItem = trackingLogItems.remove(0);
            MarkerAnimation.animateMarkerToICS("-1", marker, trackingLogItem.getLatLng(), new LatLngInterpolator.LinearFixed(),
                    new MarkerAnimation.CallbackAnim() {
                        @Override
                        public void onPathFound(List<LatLng> latLngs) {

                        }

                        @Override
                        public void onTranslate(LatLng latLng, double duration) {

                        }

                        @Override
                        public void onAnimComplete() {
                            if(state == ScreenState.MAP && trackingLogItems.size() > 0) {
                                animateMarkerICSRecursive(marker, trackingLogItems);
                            }
                        }

                        @Override
                        public void onAnimNotDone() {
							LatLng prevMarkerPos = marker.getPosition();
							marker.setPosition(trackingLogItem.getLatLng());
							marker.setRotation((float) MapUtils.getBearing(prevMarkerPos, trackingLogItem.getLatLng()));
                            this.onAnimComplete();
                        }
                    }, false, null, 0, 0, 0,
                    trackingLogItem.getMode().equalsIgnoreCase(TrackingLogModeValue.RESET.getOrdinal()));
        }

    }

    private void animateMarkerICSRecursive(final Marker marker, final List<TrackingLogItem> trackingLogItems,
                                                   final LatLngInterpolator latLngInterpolator) {
        if(marker.isVisible() && trackingLogItems.size() > 0) {
            TrackingLogItem trackingLogItem = trackingLogItems.remove(0);
            if(trackingLogItem.getMode().equalsIgnoreCase(TrackingLogModeValue.MOVE.getOrdinal())){
                marker.setPosition(new LatLng(trackingLogItem.getFromLat(), trackingLogItem.getFromLng()));
                final LatLng finalPosition = new LatLng(trackingLogItem.getLat(), trackingLogItem.getLng());
                final double finalDuration = trackingLogItem.getDuration() * 1000.0D;
                TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                    @Override
                    public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                        return latLngInterpolator.interpolate(fraction, startValue, endValue);
                    }
                };
                Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
                ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
                animator.setDuration((long) (finalDuration));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (trackingLogItems.size() > 0) {
                            animateMarkerICSRecursive(marker, trackingLogItems, latLngInterpolator);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

                MapUtils.rotateMarker(marker, trackingLogItem.getBearing());
                animator.start();

            } else if(trackingLogItem.getMode().equalsIgnoreCase(TrackingLogModeValue.RESET.getOrdinal())){
                marker.setRotation(trackingLogItem.getBearing());
                marker.setPosition(new LatLng(trackingLogItem.getLat(), trackingLogItem.getLng()));
                animateMarkerICSRecursive(marker, trackingLogItems, latLngInterpolator);
            }
        }
    }


    public class TrackingLogData{
        @SerializedName("tracking_logs")
        @Expose
        private List<TrackingLogItem> trackingLogs = new ArrayList<TrackingLogItem>();

        public List<TrackingLogItem> getTrackingLogs() {
            return trackingLogs;
        }

        public void setTrackingLogs(List<TrackingLogItem> trackingLogs) {
            this.trackingLogs = trackingLogs;
        }
    }

    public class TrackingLogItem{
        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("long")
        @Expose
        private Double lng;
        @SerializedName("bearing")
        @Expose
        private Float bearing;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("from_lat")
        @Expose
        private Double fromLat;
        @SerializedName("from_long")
        @Expose
        private Double fromLng;
        @SerializedName("duration")
        @Expose
        private Float duration;

        public Double getLat() {
            return lat;
        }

        public LatLng getLatLng(){
            return new LatLng(lat, lng);
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public Float getBearing() {
            return bearing;
        }

        public void setBearing(Float bearing) {
            this.bearing = bearing;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Double getFromLat() {
            return fromLat;
        }

        public void setFromLat(Double fromLat) {
            this.fromLat = fromLat;
        }

        public Double getFromLng() {
            return fromLng;
        }

        public void setFromLng(Double fromLng) {
            this.fromLng = fromLng;
        }

        public Float getDuration() {
            return duration;
        }

        public void setDuration(Float duration) {
            this.duration = duration;
        }
    }

}
