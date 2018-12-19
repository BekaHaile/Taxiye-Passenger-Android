package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.GoogleRestApis;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 5/26/16.
 */
public class ApiFareEstimate {

    private Context context;
    private Callback callback;
    private String startAddress, endAddress, distanceText, timeText;
    private double distanceValue, timeValue;

    public ApiFareEstimate(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng, final int isPooled, final boolean callFareEstimate, final Region region, final PromoCoupon promoCoupon) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                if (sourceLatLng != null && destLatLng != null) {
                    DialogPopup.showLoadingDialog(context, context.getString(R.string.loading));
                    GoogleRestApis.INSTANCE.getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
                            destLatLng.latitude + "," + destLatLng.longitude, false, "driving", false, getDistanceUnit(), new retrofit.Callback<SettleUserDebt>() {
                                @Override
                                public void success(SettleUserDebt settleUserDebt, Response response) {
                                    try {
                                        String result = new String(((TypedByteArray) response.getBody()).getBytes());
                                        Log.i("result", "=" + result);
                                        JSONObject jObj = new JSONObject(result);
                                        final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
                                        if (jObj.getString("status").equalsIgnoreCase("OK") && list.size() > 0) {

                                            startAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address");
                                            endAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address");

                                            distanceText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                                            timeText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

                                            distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value");
                                            timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value");
                                            callback.onSuccess(list, startAddress, endAddress, distanceText, timeText,
                                                    distanceValue, timeValue, promoCoupon);
                                            if(callFareEstimate) {
                                                getFareEstimate((Activity) context, sourceLatLng, destLatLng,
                                                        distanceValue / 1000d, timeValue / 60d, isPooled, region, promoCoupon,
                                                        list);
                                            } else{
                                                DialogPopup.dismissLoadingDialog();
                                            }
                                        } else {
                                            DialogPopup.dismissLoadingDialog();
                                            retryDialogDirections(context.getString(R.string.fare_could_not_be_estimated_between_pickup_drop), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon);
                                            callback.onDirectionsFailure();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        DialogPopup.dismissLoadingDialog();
                                        retryDialogDirections(context.getString(R.string.connection_lost_please_try_again), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon);
                                        callback.onDirectionsFailure();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    DialogPopup.dismissLoadingDialog();
                                    retryDialogDirections(context.getString(R.string.connection_lost_please_try_again), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon);
                                    callback.onDirectionsFailure();
                                }
                            });
                } else {
                    callback.onDirectionsFailure();
                }
            } else {
                retryDialogDirections(context.getString(R.string.connection_lost_desc), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon);
                callback.onDirectionsFailure();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDirectionsFailure();
        }
    }

    @NonNull
    private String getDistanceUnit() {
        String units = "metric";
        if(Data.autoData != null
                && !TextUtils.isEmpty(Data.autoData.getDistanceUnit())
                && Data.autoData.getDistanceUnit().contains("mile")){
			units = "imperial";
		}
        return units;
    }

    public interface Callback{
        void onSuccess(List<LatLng> list, String startAddress, String endAddress, String distanceText, String timeText,
                       double distanceValue, double timeValue, PromoCoupon promoCoupon);
        void onRetry();
        void onNoRetry();
        void onFareEstimateSuccess(String currency, String minFare, String maxFare, double convenienceCharge, double tollCharge);
        void onPoolSuccess(String currency, double fare, double rideDistance, String rideDistanceUnit,
                           double rideTime, String rideTimeUnit, int poolFareId, double convenienceCharge,
                           String text, double tollCharge);
        void onDirectionsFailure();
        void onFareEstimateFailure();
    }

    /**
     * ASync for calculating fare estimate from server
     */
    public void getFareEstimate(final Activity activity, final LatLng sourceLatLng, final LatLng desLatLng,
                                final double distanceValue, final double timeValue, final int isPooled, final Region region, final PromoCoupon promoCoupon,
                                final List<LatLng> latLngs) {
        if (!HomeActivity.checkIfUserDataNull(activity)) {
            if (MyApplication.getInstance().isOnline()) {
                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                params.put("start_latitude", "" + sourceLatLng.latitude);
                params.put("start_longitude", "" + sourceLatLng.longitude);
                params.put("drop_latitude", ""+desLatLng.latitude);
                params.put("drop_longitude", ""+desLatLng.longitude);
                params.put(Constants.KEY_RIDE_DISTANCE, "" + distanceValue);
                params.put(Constants.KEY_RIDE_TIME, "" + timeValue);
                params.put(Constants.KEY_IS_POOLED, "" + isPooled);
                params.put(Constants.KEY_VEHICLE_TYPE, String.valueOf(region.getVehicleType()));
                params.put(Constants.KEY_RIDE_TYPE, String.valueOf(region.getRideType()));
                params.put(Constants.KEY_REGION_ID, String.valueOf(region.getRegionId()));
                if(promoCoupon!=null && promoCoupon.getId()!=-1){
                    params.put(promoCoupon instanceof CouponInfo?Constants.KEY_COUPON_ID:Constants.KEY_PROMO_ID, String.valueOf(promoCoupon.getId()));
                }

                if(latLngs != null && latLngs.size() > 0){
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (LatLng latLng : latLngs) {
                            JSONArray latLngArr = new JSONArray();
                            latLngArr.put(latLng.latitude);
                            latLngArr.put(latLng.longitude);
                            jsonArray.put(latLngArr);
                        }
                        params.put(Constants.KEY_PATH_LAT_LONGS, jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().getFareEstimate(params, new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.e("response", "getFareEstimate response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);

                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    String currency = jObj.optString(Constants.KEY_CURRENCY);
                                    double tollCharge = jObj.optDouble(Constants.KEY_TOLL_CHARGE, 0);
                                    if(jObj.has("pool_fare_id")){
                                        callback.onPoolSuccess(currency, jObj.optDouble("fare", 0), jObj.optDouble(Constants.KEY_RIDE_DISTANCE, 0),
                                                jObj.optString("ride_distance_unit"), jObj.optDouble(Constants.KEY_RIDE_TIME, 0),
                                                jObj.optString("ride_time_unit"), jObj.optInt("pool_fare_id", 0),
                                                jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0), jObj.optString("text", ""), tollCharge);
                                    } else{
                                        String minFare = jObj.getString("min_fare");
                                        String maxFare = jObj.getString("max_fare");
                                        double convenienceCharge = jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0);
                                        callback.onFareEstimateSuccess(currency, minFare, maxFare, convenienceCharge, tollCharge);
                                    }


                                } else {
                                    retryDialog(activity, message, sourceLatLng, desLatLng, distanceValue, timeValue, isPooled,
                                            region, promoCoupon, latLngs);
                                    callback.onFareEstimateFailure();
                                }
                            } else {
                                callback.onFareEstimateFailure();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(activity, activity.getString(R.string.connection_lost_please_try_again), sourceLatLng,
                                    desLatLng, distanceValue, timeValue, isPooled, region, promoCoupon, latLngs);
                            callback.onFareEstimateFailure();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("response", "getFareEstimate error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(activity, activity.getString(R.string.connection_lost_please_try_again), sourceLatLng,
                                desLatLng, distanceValue, timeValue, isPooled, region, promoCoupon, latLngs);
                        callback.onFareEstimateFailure();
                    }
                });

            } else {
                retryDialog(activity, activity.getString(R.string.connection_lost_desc), sourceLatLng, desLatLng, distanceValue,
                        timeValue, isPooled, region, promoCoupon, latLngs);
                DialogPopup.dismissLoadingDialog();
                callback.onFareEstimateFailure();
            }
        } else{
            DialogPopup.dismissLoadingDialog();
            callback.onFareEstimateFailure();
        }
    }

    private void retryDialog(final Activity activity, final String message, final LatLng sourceLatLng, final LatLng destLatLng,
                             final double distanceValue, final double timeValue, final int isPooled, final Region region,
                             final PromoCoupon promoCoupon, final List<LatLng> latLngs){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, activity.getString(R.string.retry), activity.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFareEstimate(activity, sourceLatLng, destLatLng, distanceValue, timeValue, isPooled, region, promoCoupon, latLngs);
                        callback.onRetry();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onNoRetry();
                    }
                }, false, false);
    }


    private void retryDialogDirections(String message, final LatLng sourceLatLng, final LatLng destLatLng, final int isPooled, final boolean callFareEstimate, final Region region,final PromoCoupon promoCoupon){
        DialogPopup.alertPopupTwoButtonsWithListeners((Activity) context, "", message, context.getString(R.string.retry), context.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDirectionsAndComputeFare(sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon);
                        callback.onRetry();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onNoRetry();
                    }
                }, false, false);
    }

}
