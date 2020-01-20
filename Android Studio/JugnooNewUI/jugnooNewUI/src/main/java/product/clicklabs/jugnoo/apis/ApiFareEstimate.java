package product.clicklabs.jugnoo.apis;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
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
import product.clicklabs.jugnoo.directions.JungleApisImpl;
import product.clicklabs.jugnoo.directions.room.model.Path;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Package;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
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
    private String distanceText, timeText;
    private List<LatLng> list;
    private double distanceValue, timeValue;

    private LatLng sourceLatLng, destLatLng;

    public ApiFareEstimate(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng, final int isPooled,
											final boolean callFareEstimate, final Region region, final PromoCoupon promoCoupon,
											final Package selectedPackage, final String source) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                if (sourceLatLng != null && destLatLng != null) {
                	if(this.sourceLatLng != null && this.destLatLng != null
								&& MapUtils.distance(this.sourceLatLng, sourceLatLng) < Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION
							&& MapUtils.distance(this.destLatLng, destLatLng) < Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION
							&& list != null && distanceValue > 0){
						directionsSuccess(promoCoupon, callFareEstimate, sourceLatLng, destLatLng, isPooled, region, selectedPackage);
						return;
					}
                    DialogPopup.showLoadingDialog(context, context.getString(R.string.loading));
					JungleApisImpl.INSTANCE.getDirectionsPath(sourceLatLng, destLatLng, getDistanceUnit(), source, new JungleApisImpl.Callback() {
								@Override
								public void onSuccess(@NotNull List<LatLng> latLngs, @NotNull Path path) {
									try {
										list = latLngs;
										if (list.size() > 0) {
											ApiFareEstimate.this.sourceLatLng = sourceLatLng;
											ApiFareEstimate.this.destLatLng = destLatLng;

											distanceText = path.getDistanceText();
											timeText = path.getTimeText();

											distanceValue = path.getDistance();
											timeValue = path.getTime();
											directionsSuccess(promoCoupon, callFareEstimate, sourceLatLng, destLatLng, isPooled, region, selectedPackage);
										} else {
											DialogPopup.dismissLoadingDialog();
											retryDialogDirections(context.getString(R.string.please_select_appropriate_pickup_and_drop_location_to_continue), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon, selectedPackage, source);
											callback.onDirectionsFailure();
										}

									} catch (Exception e) {
										e.printStackTrace();
										DialogPopup.dismissLoadingDialog();
										retryDialogDirections(context.getString(R.string.please_select_appropriate_pickup_and_drop_location_to_continue), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon, selectedPackage, source);
										callback.onDirectionsFailure();
									}
								}

								@Override
								public void onFailure() {
									DialogPopup.dismissLoadingDialog();
									retryDialogDirections(context.getString(R.string.please_select_appropriate_pickup_and_drop_location_to_continue), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon, selectedPackage, source);
									callback.onDirectionsFailure();
								}
							});
                } else {
                    callback.onDirectionsFailure();
                }
            } else {
                retryDialogDirections(context.getString(R.string.connection_lost_desc), sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon, selectedPackage, source);
                callback.onDirectionsFailure();
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDirectionsFailure();
        }
    }

	public void directionsSuccess(PromoCoupon promoCoupon, boolean callFareEstimate, LatLng sourceLatLng, LatLng destLatLng, int isPooled, Region region, final Package selectedPackage) {
		callback.onSuccess(list, distanceText, timeText,
				distanceValue, timeValue, promoCoupon);
		if(callFareEstimate) {
			getFareEstimate((Activity) context, sourceLatLng, destLatLng,
					distanceValue / 1000d, timeValue / 60d, isPooled, region, promoCoupon,
					list, selectedPackage);
		} else {
			DialogPopup.dismissLoadingDialog();
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
        void onSuccess(List<LatLng> list, String distanceText, String timeText,
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
                                final List<LatLng> latLngs, final Package selectedPackage) {
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

                if(selectedPackage != null) {
                    params.put(Constants.KEY_PACKAGE_ID, String.valueOf(selectedPackage.getPackageId()));
                    params.put(Constants.KEY_IS_ROUND_TRIP, String.valueOf(selectedPackage.isRoundTrip()));
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
                                    if(jObj.has(Constants.KEY_POOL_FARE_ID)){
                                        callback.onPoolSuccess(currency, jObj.optDouble("fare", 0), jObj.optDouble(Constants.KEY_RIDE_DISTANCE, 0),
                                                jObj.optString("ride_distance_unit"), jObj.optDouble(Constants.KEY_RIDE_TIME, 0),
                                                jObj.optString("ride_time_unit"), jObj.optInt(Constants.KEY_POOL_FARE_ID, 0),
                                                jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0), jObj.optString("text", ""), tollCharge);
                                    } else{
                                        String minFare = jObj.getString("min_fare");
                                        String maxFare = jObj.getString("max_fare");
                                        double convenienceCharge = jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0);
                                        callback.onFareEstimateSuccess(currency, minFare, maxFare, convenienceCharge, tollCharge);
                                    }


                                } else {
                                    retryDialog(activity, message, sourceLatLng, desLatLng, distanceValue, timeValue, isPooled,
                                            region, promoCoupon, latLngs, selectedPackage);
                                    callback.onFareEstimateFailure();
                                }
                            } else {
                                callback.onFareEstimateFailure();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(activity, activity.getString(R.string.connection_lost_please_try_again), sourceLatLng,
                                    desLatLng, distanceValue, timeValue, isPooled, region, promoCoupon, latLngs, selectedPackage);
                            callback.onFareEstimateFailure();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("response", "getFareEstimate error="+error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(activity, activity.getString(R.string.connection_lost_please_try_again), sourceLatLng,
                                desLatLng, distanceValue, timeValue, isPooled, region, promoCoupon, latLngs, selectedPackage);
                        callback.onFareEstimateFailure();
                    }
                });

            } else {
                retryDialog(activity, activity.getString(R.string.connection_lost_desc), sourceLatLng, desLatLng, distanceValue,
                        timeValue, isPooled, region, promoCoupon, latLngs, selectedPackage);
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
                             final PromoCoupon promoCoupon, final List<LatLng> latLngs, final Package selectedPackage){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, activity.getString(R.string.retry), activity.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFareEstimate(activity, sourceLatLng, destLatLng, distanceValue, timeValue, isPooled, region, promoCoupon, latLngs, selectedPackage);
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


    private void retryDialogDirections(String message, final LatLng sourceLatLng, final LatLng destLatLng, final int isPooled, final boolean callFareEstimate,
									   final Region region, final PromoCoupon promoCoupon, final Package selectedPackage, final String source){
    	DialogPopup.alertPopup((Activity) context, "", message);
//        DialogPopup.alertPopupTwoButtonsWithListeners((Activity) context, "", message, context.getString(R.string.retry), context.getString(R.string.cancel),
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getDirectionsAndComputeFare(sourceLatLng, destLatLng, isPooled, callFareEstimate, region,promoCoupon, selectedPackage, source);
//                        callback.onRetry();
//                    }
//                },
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        callback.onNoRetry();
//                    }
//                }, false, false);
    }

}
