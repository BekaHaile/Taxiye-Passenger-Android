package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.RideTypeValue;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
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

    public void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng, final int isPooled, final boolean callFareEstimate) {
        try {
            if (AppStatus.getInstance(context).isOnline(context)) {
                if (sourceLatLng != null && destLatLng != null) {
                    DialogPopup.showLoadingDialog(context, "Loading...");
                    RestClient.getGoogleApiServices().getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
                            destLatLng.latitude + "," + destLatLng.longitude, false, "driving", false, new retrofit.Callback<SettleUserDebt>() {
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
                                            callback.onSuccess(list, startAddress, endAddress, distanceText, timeText, distanceValue, timeValue);
                                            if(callFareEstimate) {
                                                getFareEstimate((Activity) context, sourceLatLng, destLatLng, distanceValue / 1000d, timeValue / 60d, isPooled);
                                            } else{
                                                DialogPopup.dismissLoadingDialog();
                                            }
                                        } else {
                                            FlurryEventLogger.event(FlurryEventNames.GOOGLE_API_DIRECTIONS_FAILURE);
                                            DialogPopup.alertPopup((Activity) context, "", "Fare could not be estimated between the selected pickup and drop location");
                                            DialogPopup.dismissLoadingDialog();
                                            callback.onFailure();
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        callback.onFailure();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    callback.onFailure();
                                }
                            });
                }
            } else {
                DialogPopup.alertPopup((Activity) context, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback{
        void onSuccess(List<LatLng> list, String startAddress, String endAddress, String distanceText, String timeText, double distanceValue, double timeValue);
        void onFailure();
        void onRetry();
        void onNoRetry();
        void onFareEstimateSuccess(String minFare, String maxFare, double convenienceCharge);
        void onPoolSuccess(double fare, double rideDistance, String rideDistanceUnit,
                           double rideTime, String rideTimeUnit, int poolFareId, double convenienceCharge,
                           String text);
    }

    /**
     * ASync for calculating fare estimate from server
     */
    public void getFareEstimate(final Activity activity, final LatLng sourceLatLng, final LatLng desLatLng, final double distanceValue, final double timeValue, final int isPooled) {
        if (!HomeActivity.checkIfUserDataNull(activity)) {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                params.put("start_latitude", "" + sourceLatLng.latitude);
                params.put("start_longitude", "" + sourceLatLng.longitude);
                params.put("drop_latitude", ""+desLatLng.latitude);
                params.put("drop_longitude", ""+desLatLng.longitude);
                params.put("ride_distance", "" + distanceValue);
                params.put("ride_time", "" + timeValue);
                params.put(Constants.KEY_IS_POOLED, "" + isPooled);

                RestClient.getApiServices().getFareEstimate(params, new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.e("response", "getFareEstimate response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);

                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    if(jObj.has("pool_fare_id")){
                                        callback.onPoolSuccess(jObj.optDouble("fare", 0), jObj.optDouble("ride_distance", 0),
                                                jObj.optString("ride_distance_unit"), jObj.optDouble("ride_time", 0),
                                                jObj.optString("ride_time_unit"), jObj.optInt("pool_fare_id", 0),
                                                jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0), jObj.optString("text", ""));
                                    } else{
                                        String minFare = jObj.getString("min_fare");
                                        String maxFare = jObj.getString("max_fare");
                                        double convenienceCharge = jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0);
                                        callback.onFareEstimateSuccess(minFare, maxFare, convenienceCharge);
                                    }


                                } else {
                                    retryDialog(activity, message, sourceLatLng, desLatLng, distanceValue, timeValue, isPooled);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(activity, Data.SERVER_ERROR_MSG, sourceLatLng, desLatLng, distanceValue, timeValue, isPooled);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("response", "getFareEstimate error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(activity, Data.SERVER_NOT_RESOPNDING_MSG, sourceLatLng, desLatLng, distanceValue, timeValue, isPooled);
                    }
                });

            } else {
                retryDialog(activity, Data.CHECK_INTERNET_MSG, sourceLatLng, desLatLng, distanceValue, timeValue, isPooled);
                DialogPopup.dismissLoadingDialog();
            }
        } else{
            DialogPopup.dismissLoadingDialog();
        }
    }

    private void retryDialog(final Activity activity, final String message, final LatLng sourceLatLng, final LatLng destLatLng, final double distanceValue, final double timeValue, final int isPooled){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, "Retry", "Cancel",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFareEstimate(activity, sourceLatLng, destLatLng, distanceValue, timeValue, isPooled);
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
