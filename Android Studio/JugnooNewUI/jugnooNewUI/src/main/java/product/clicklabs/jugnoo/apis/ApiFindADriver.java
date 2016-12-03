package product.clicklabs.jugnoo.apis;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Driver;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
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
			params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);

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
							for (Region region : Data.autoData.getRegions()) {
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
			Data.autoData.getDriverInfos().clear();
			if(findADriverResponse.getDrivers() != null) {
				for (Driver driver : findADriverResponse.getDrivers()) {
					double bearing = 0;
					if (driver.getBearing() != null) {
						bearing = driver.getBearing();
					}
					int vehicleType = driver.getVehicleType() == null ? Constants.VEHICLE_AUTO : driver.getVehicleType();
					Data.autoData.getDriverInfos().add(new DriverInfo(String.valueOf(driver.getUserId()), driver.getLatitude(), driver.getLongitude(), driver.getUserName(), "",
							"", driver.getPhoneNo(), String.valueOf(driver.getRating()), "", 0, bearing, vehicleType, (ArrayList<Integer>)driver.getRegionIds()));
				}
			}

			Data.autoData.setFareFactor(1);
			if(findADriverResponse.getFareFactor() != null) {
				Data.autoData.setFareFactor(findADriverResponse.getFareFactor());
			}
			Data.autoData.setDriverFareFactor(1);
			if(findADriverResponse.getDriverFareFactor() != null) {
				Data.autoData.setDriverFareFactor(findADriverResponse.getDriverFareFactor());
			}

			Data.autoData.setFarAwayCity("");
			if (findADriverResponse.getFarAwayCity() == null) {
				Data.autoData.setFarAwayCity("");
			} else {
				Data.autoData.setFarAwayCity(findADriverResponse.getFarAwayCity());
			}

			Data.autoData.setCampaigns(findADriverResponse.getCampaigns());

            if(findADriverResponse.getCityId() != null){
                Data.userData.setCurrentCity(findADriverResponse.getCityId());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(findADriverResponse.getNearbyPickupRegions() != null){
			Data.autoData.setNearbyPickupRegionses(findADriverResponse.getNearbyPickupRegions());
		}

		try{
			if(Data.autoData.getRegions() == null){
				Data.autoData.setRegions(new ArrayList<Region>());
			} else{
				Data.autoData.getRegions().clear();
			}
			if(findADriverResponse.getRegions() != null) {
				HomeUtil homeUtil = new HomeUtil();
				for (Region region : findADriverResponse.getRegions()) {
					region.setVehicleIconSet(homeUtil.getVehicleIconSet(region.getIconSet()));
					region.setIsDefault(false);
					Data.autoData.getRegions().add(region);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		try {
			//Common promo and Coupon
			try {
				if(Data.userData != null && Data.userData.getPromoCoupons() == null){
                    Data.userData.setPromoCoupons(new ArrayList<PromoCoupon>());
                } else{
                    Data.userData.getPromoCoupons().clear();
                }
				if(findADriverResponse.getCommonPromotions() != null) {
                    Data.userData.getPromoCoupons().addAll(findADriverResponse.getCommonPromotions());
                }
				if(findADriverResponse.getCommonCoupons() != null) {
                    Data.userData.getPromoCoupons().addAll(findADriverResponse.getCommonCoupons());
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			// for Auto promo and coupons
			try {
				if(Data.autoData != null && Data.autoData.getPromoCoupons() == null){
                    Data.autoData.setPromoCoupons(new ArrayList<PromoCoupon>());
                } else{
                    Data.autoData.getPromoCoupons().clear();
                }
				if(findADriverResponse.getAutosPromotions() != null) {
                    Data.autoData.getPromoCoupons().addAll(findADriverResponse.getAutosPromotions());
                }
				if(findADriverResponse.getAutosCoupons() != null) {
                    Data.autoData.getPromoCoupons().addAll(findADriverResponse.getAutosCoupons());
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			// for Fresh promo and coupons
			try {
				if(Data.getFreshData() != null && Data.getFreshData().getPromoCoupons() == null){
                    Data.getFreshData().setPromoCoupons(new ArrayList<PromoCoupon>());
                } else{
                    Data.getFreshData().getPromoCoupons().clear();
                }
				if(findADriverResponse.getFreshPromotions() != null) {
                    Data.getFreshData().getPromoCoupons().addAll(findADriverResponse.getFreshPromotions());
                }
				if(findADriverResponse.getFreshCoupons() != null) {
                    Data.getFreshData().getPromoCoupons().addAll(findADriverResponse.getFreshCoupons());
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			// for Meals promo and coupons
			try {
				if(Data.getMealsData() != null && Data.getMealsData().getPromoCoupons() == null){
                    Data.getMealsData().setPromoCoupons(new ArrayList<PromoCoupon>());
                } else{
                    Data.getMealsData().getPromoCoupons().clear();
                }
				if(findADriverResponse.getMealsPromotions() != null) {
                    Data.getMealsData().getPromoCoupons().addAll(findADriverResponse.getMealsPromotions());
                }
				if(findADriverResponse.getMealsCoupons() != null) {
                    Data.getMealsData().getPromoCoupons().addAll(findADriverResponse.getMealsCoupons());
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if(Data.getGroceryData() != null && Data.getGroceryData().getPromoCoupons() == null){
					Data.getGroceryData().setPromoCoupons(new ArrayList<PromoCoupon>());
				} else{
					Data.getGroceryData().getPromoCoupons().clear();
				}
				if(findADriverResponse.getGroceryPromotions() != null) {
					Data.getGroceryData().getPromoCoupons().addAll(findADriverResponse.getGroceryPromotions());
				}
				if(findADriverResponse.getGroceryCoupons() != null) {
					Data.getGroceryData().getPromoCoupons().addAll(findADriverResponse.getGroceryCoupons());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if(Data.getMenusData() != null && Data.getMenusData().getPromoCoupons() == null){
					Data.getMenusData().setPromoCoupons(new ArrayList<PromoCoupon>());
				} else{
					Data.getMenusData().getPromoCoupons().clear();
				}
				if(findADriverResponse.getMenusPromotions() != null) {
					Data.getMenusData().getPromoCoupons().addAll(findADriverResponse.getMenusPromotions());
				}
				if(findADriverResponse.getMenusCoupons() != null) {
					Data.getMenusData().getPromoCoupons().addAll(findADriverResponse.getMenusCoupons());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// for Dodo promo and coupons
			try {
				if(Data.getDeliveryData() != null && Data.getDeliveryData().getPromoCoupons() == null){
                    Data.getDeliveryData().setPromoCoupons(new ArrayList<PromoCoupon>());
                } else{
                    Data.getDeliveryData().getPromoCoupons().clear();
                }
				if(findADriverResponse.getDeliveryPromotions() != null) {
                    Data.getDeliveryData().getPromoCoupons().addAll(findADriverResponse.getDeliveryPromotions());
                }
				if(findADriverResponse.getDeliveryCoupons() != null) {
                    Data.getDeliveryData().getPromoCoupons().addAll(findADriverResponse.getDeliveryCoupons());
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				MyApplication.getInstance().getCleverTapUtils().setCoupons();
			} catch (Exception e) {
				e.printStackTrace();
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
								fareStructure.getFareThresholdWaitingTime(), convenienceCharges, true,
								fareStructure.getDisplayBaseFare(),
								fareStructure.getDisplayFareText());
						for (int i = 0; i < Data.autoData.getRegions().size(); i++) {
							try {
								if (Data.autoData.getRegions().get(i).getVehicleType().equals(fareStructure.getVehicleType())
										&& Data.autoData.getRegions().get(i).getRideType().equals(fareStructure.getRideType())) {
									Data.autoData.getRegions().get(i).setFareStructure(fareStructure1);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (regionSelected.getVehicleType().equals(fareStructure.getVehicleType())
								&& regionSelected.getRideType().equals(fareStructure.getRideType())
								) {
							Data.autoData.setFareStructure(fareStructure1);
						}
					}
				}
			}

			if(findADriverResponse.getFreshEnabled() != null) {
				Data.userData.setFreshEnabled(findADriverResponse.getFreshEnabled());
			}
			if(findADriverResponse.getMealsEnabled() != null) {
				Data.userData.setMealsEnabled(findADriverResponse.getMealsEnabled());
			}
			if(findADriverResponse.getDeliveryEnabled() != null) {
				Data.userData.setDeliveryEnabled(findADriverResponse.getDeliveryEnabled());
			}
			if(findADriverResponse.getGroceryEnabled() != null) {
				Data.userData.setGroceryEnabled(findADriverResponse.getGroceryEnabled());
			}
			if(findADriverResponse.getMenusEnabled() != null) {
				Data.userData.setMenusEnabled(findADriverResponse.getMenusEnabled());
			}
			if(findADriverResponse.getIntegratedJugnooEnabled() != null){
				Data.userData.setIntegratedJugnooEnabled(findADriverResponse.getIntegratedJugnooEnabled());
			}
			if(!TextUtils.isEmpty(findADriverResponse.getGamePredictUrl())) {
				Data.userData.setGamePredictUrl(findADriverResponse.getGamePredictUrl());
			}
			if(findADriverResponse.getTopupCardEnabled() != null){
				Data.userData.setTopupCardEnabled(findADriverResponse.getTopupCardEnabled());
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

	public void setRefreshLatLng(LatLng refreshLatLng){
		this.refreshLatLng = refreshLatLng;
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
