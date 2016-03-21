package product.clicklabs.jugnoo.apis;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.PriorityTipCategory;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.CheckForAppOpen;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Coupon;
import product.clicklabs.jugnoo.retrofit.model.Driver;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.retrofit.model.Promotion;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 3/19/16.
 */
public class ApiFindADriver {

	private final String TAG = ApiFindADriver.class.getSimpleName();

	private Activity activity;
	private Callback callback;

	public ApiFindADriver(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void hit(String accessToken, LatLng latLng, final int showAllDrivers, int showDriverInfo){
		try {
			if(callback != null) {
				callback.onPre();
			}

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
			params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));

			if (1 == showAllDrivers) {
				params.put("show_all", "1");
			}
			if(1 == showDriverInfo){
				params.put("show_phone_no", "1");
			}

			new CheckForAppOpen().checkAndFillParamsForIgnoringAppOpen(activity, params);

			Log.i("params in find_a_driver", "=" + params);
			final long startTime = System.currentTimeMillis();
			RestClient.getApiServices().findADriverCall(params, new retrofit.Callback<FindADriverResponse>() {
				@Override
				public void success(FindADriverResponse findADriverResponse, Response response) {
					try {
						FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_FIND_A_DRIVER, startTime);
						Log.e(TAG, "findADriverCall response=" + new String(((TypedByteArray) response.getBody()).getBytes()));
						parseFindADriverResponse(findADriverResponse);
						if(callback != null) {
							callback.onComplete();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "findADriverCall error=" + error.toString());
					if(callback != null) {
						callback.onFailure();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void parseFindADriverResponse(FindADriverResponse findADriverResponse){
		try {
			Data.driverInfos.clear();
			for (Driver driver : findADriverResponse.getDrivers()) {
				double bearing = 0;
				if (driver.getBearing() != null) {
					bearing = driver.getBearing();
				}
				Data.driverInfos.add(new DriverInfo(String.valueOf(driver.getUserId()), driver.getLatitude(), driver.getLongitude(), driver.getUserName(), "",
						"", driver.getPhoneNo(), String.valueOf(driver.getRating()), "", 0, bearing));
			}
			DecimalFormat df = new DecimalFormat("#");
			Data.etaMinutes = df.format(findADriverResponse.getEta());
			Data.priorityTipCategory = PriorityTipCategory.NO_PRIORITY_DIALOG.getOrdinal();
			if (findADriverResponse.getPriorityTipCategory() != null) {
				Data.priorityTipCategory = findADriverResponse.getPriorityTipCategory();
			}

			Data.userData.fareFactor = findADriverResponse.getFareFactor();
			Data.farAwayCity = "";
			if (findADriverResponse.getFarAwayCity() == null) {
				Data.farAwayCity = "";
			} else {
				Data.farAwayCity = findADriverResponse.getFarAwayCity();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if(Data.promoCoupons == null){
				Data.promoCoupons = new ArrayList<>();
			} else{
				Data.promoCoupons.clear();
			}
			for (Coupon coupon : findADriverResponse.getCoupons()) {
				Data.promoCoupons.add(new CouponInfo(coupon.getAccountId(),
						coupon.getCouponType(),
						coupon.getStatus(),
						coupon.getTitle(),
						coupon.getSubtitle(),
						coupon.getDescription(),
						coupon.getImage(),
						coupon.getRedeemedOn(),
						coupon.getExpiryDate(), "", ""));
			}
			for (Promotion promotion : findADriverResponse.getPromotions()) {
				Data.promoCoupons.add(new PromotionInfo(promotion.getPromoId(),
						promotion.getTitle(),
						promotion.getTermsNConds()));
			}

			for (FareStructure fareStructure : findADriverResponse.getFareStructure()) {
				String startTime = fareStructure.getStartTime();
				String endTime = fareStructure.getEndTime();
				String localStartTime = DateOperations.getUTCTimeInLocalTimeStamp(startTime);
				String localEndTime = DateOperations.getUTCTimeInLocalTimeStamp(endTime);
				long diffStart = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), localStartTime);
				long diffEnd = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), localEndTime);
				double convenienceCharges = 0;
				if (fareStructure.getConvenienceCharge() != null) {
					convenienceCharges = fareStructure.getConvenienceCharge();
				}
				if (diffStart >= 0 && diffEnd <= 0) {
					Data.fareStructure = new product.clicklabs.jugnoo.datastructure.FareStructure(fareStructure.getFareFixed(),
							fareStructure.getFareThresholdDistance(),
							fareStructure.getFarePerKm(),
							fareStructure.getFarePerMin(),
							fareStructure.getFareThresholdTime(),
							fareStructure.getFarePerWaitingMin(),
							fareStructure.getFareThresholdWaitingTime(), convenienceCharges, true);
					break;
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}


	public interface Callback{
		void onPre();
		void onFailure();
		void onComplete();
	}

}
