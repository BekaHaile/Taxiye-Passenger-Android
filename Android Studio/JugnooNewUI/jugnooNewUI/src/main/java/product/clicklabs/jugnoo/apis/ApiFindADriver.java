package product.clicklabs.jugnoo.apis;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.PriorityTipCategory;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
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
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 3/19/16.
 */
public class ApiFindADriver {

	private final String TAG = ApiFindADriver.class.getSimpleName();
	private final double MIN_DISTANCE_FOR_FIND_A_DRIVER_REFRESH_ON_REQUEST_RIDE = 50; // in meters
	private final long MIN_TIME_FOR_FIND_A_DRIVER_REFRESH_ON_REQUEST_RIDE = 2 * 60 * 1000; // in millis


	private HomeActivity activity;
	private Callback callback;
	private Region regionSelected;
	private LatLng refreshLatLng;
	private long refreshTime;

	public ApiFindADriver(HomeActivity activity, Region regionSelected, Callback callback){
		this.activity = activity;
		this.regionSelected = regionSelected;
		this.callback = callback;
	}

	public void hit(String accessToken, final LatLng latLng, final int showAllDrivers, int showDriverInfo,
					Region regionSelected, final boolean beforeRequestRide, final boolean confirmedScreenOpened){
		this.regionSelected = regionSelected;
		try {
			if(callback != null) {
				callback.onPre();
			}

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
			params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
			params.put("device_type", Data.DEVICE_TYPE);

			if (1 == showAllDrivers) {
				params.put("show_all", "1");
			}
			if(1 == showDriverInfo){
				params.put("show_phone_no", "1");
			}

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(activity, params);

			Log.i("params in find_a_driver", "=" + params);
			final long startTime = System.currentTimeMillis();
			RestClient.getApiServices().findADriverCall(params, new retrofit.Callback<FindADriverResponse>() {
				@Override
				public void success(FindADriverResponse findADriverResponse, Response response) {
					try {
						FlurryEventLogger.eventApiResponseTime(FlurryEventNames.API_FIND_A_DRIVER, startTime);
						String resp = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.e(TAG, "findADriverCall response=" + resp);

						double fareFactorOld = ApiFindADriver.this.regionSelected.getCustomerFareFactor();
						double driverFareFactorOld = ApiFindADriver.this.regionSelected.getDriverFareFactor();

						parseFindADriverResponse(findADriverResponse);

						refreshLatLng = latLng;
						refreshTime = System.currentTimeMillis();
						if(callback != null && !confirmedScreenOpened) {
							callback.onCompleteFindADriver();
						}

						if(beforeRequestRide){
							Region newRegion = null;
							for (Region region : Data.regions) {
								if(region.getRegionId() == ApiFindADriver.this.regionSelected.getRegionId()){
									newRegion = region;
								}
							}
							if(newRegion != null
									&& (Utils.compareDouble(fareFactorOld, newRegion.getCustomerFareFactor()) != 0
									|| Utils.compareDouble(driverFareFactorOld, newRegion.getDriverFareFactor()) != 0)){
								callback.stopRequestRide(confirmedScreenOpened);
							} else{
								callback.continueRequestRide(confirmedScreenOpened);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					if(callback != null) {
						callback.onFinish();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e(TAG, "findADriverCall error=" + error.toString());
					if(callback != null) {
						callback.onFailure();
						callback.onFinish();
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
			if(findADriverResponse.getDrivers() != null) {
				for (Driver driver : findADriverResponse.getDrivers()) {
					double bearing = 0;
					if (driver.getBearing() != null) {
						bearing = driver.getBearing();
					}
					int vehicleType = driver.getVehicleType() == null ? Constants.VEHICLE_AUTO : driver.getVehicleType();
					Data.driverInfos.add(new DriverInfo(String.valueOf(driver.getUserId()), driver.getLatitude(), driver.getLongitude(), driver.getUserName(), "",
							"", driver.getPhoneNo(), String.valueOf(driver.getRating()), "", 0, bearing, vehicleType, (ArrayList<Integer>)driver.getRegionIds()));
				}
			}
			Data.priorityTipCategory = PriorityTipCategory.NO_PRIORITY_DIALOG.getOrdinal();
			if (findADriverResponse.getPriorityTipCategory() != null) {
				Data.priorityTipCategory = findADriverResponse.getPriorityTipCategory();
			}

			Data.userData.fareFactor = 1d;
			if(findADriverResponse.getFareFactor() != null) {
				Data.userData.fareFactor = findADriverResponse.getFareFactor();
			}
			if(findADriverResponse.getDriverFareFactor() != null) {
				Data.userData.setDriverFareFactor(findADriverResponse.getDriverFareFactor());
			} else{
				Data.userData.setDriverFareFactor(1);
			}
			Data.farAwayCity = "";
			if (findADriverResponse.getFarAwayCity() == null) {
				Data.farAwayCity = "";
			} else {
				Data.farAwayCity = findADriverResponse.getFarAwayCity();
			}

			if (findADriverResponse.getFreshAvailable() == null) {
				Data.freshAvailable = 0;
			} else {
				Data.freshAvailable = findADriverResponse.getFreshAvailable();
			}

			Data.campaigns = findADriverResponse.getCampaigns();
			Data.userData.setIsPoolEnabled(findADriverResponse.getIsPoolEnabled()==null ? 0 : findADriverResponse.getIsPoolEnabled());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
			if(Data.regions == null){
				Data.regions = new ArrayList<>();
			} else{
				Data.regions.clear();
			}
			if(findADriverResponse.getRegions() != null) {
				HomeUtil homeUtil = new HomeUtil();
				for (Region region : findADriverResponse.getRegions()) {
					region.setVehicleIconSet(homeUtil.getVehicleIconSet(region.getIconSet()));
					Data.regions.add(region);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		try {
			if(Data.promoCoupons == null){
				Data.promoCoupons = new ArrayList<>();
			} else{
				Data.promoCoupons.clear();
			}
			if(findADriverResponse.getCoupons() != null) {
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
			}
			if(findADriverResponse.getPromotions() != null) {
				for (Promotion promotion : findADriverResponse.getPromotions()) {
					Data.promoCoupons.add(new PromotionInfo(promotion.getPromoId(),
							promotion.getTitle(),
							promotion.getTermsNConds()));
				}
			}

			if(findADriverResponse.getFareStructure() != null) {
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
						product.clicklabs.jugnoo.datastructure.FareStructure fareStructure1 = new product.clicklabs.jugnoo.datastructure.FareStructure(fareStructure.getFareFixed(),
								fareStructure.getFareThresholdDistance(),
								fareStructure.getFarePerKm(),
								fareStructure.getFarePerMin(),
								fareStructure.getFareThresholdTime(),
								fareStructure.getFarePerWaitingMin(),
								fareStructure.getFareThresholdWaitingTime(), convenienceCharges, true);
						for (int i = 0; i < Data.regions.size(); i++) {
							try {if (Data.regions.get(i).getVehicleType().equals(fareStructure.getVehicleType())) {
									Data.regions.get(i).setFareStructure(fareStructure1);
								}} catch (Exception e) {e.printStackTrace();}
						}
						if(regionSelected.getVehicleType().equals(fareStructure.getVehicleType())){
							Data.fareStructure = fareStructure1;
						}
					}
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean findADriverNeeded(LatLng pickupLatLng){
		double distance = MapUtils.distance(refreshLatLng, pickupLatLng);
		long timeDiff = System.currentTimeMillis() - refreshTime;
		return (distance > MIN_DISTANCE_FOR_FIND_A_DRIVER_REFRESH_ON_REQUEST_RIDE
				|| timeDiff > MIN_TIME_FOR_FIND_A_DRIVER_REFRESH_ON_REQUEST_RIDE);
	}


	public interface Callback{
		void onPre();
		void onFailure();
		void onCompleteFindADriver();
		void continueRequestRide(boolean confirmedScreenOpened);
		void stopRequestRide(boolean confirmedScreenOpened);
		void onFinish();
	}

}
