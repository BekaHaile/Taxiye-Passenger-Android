package product.clicklabs.jugnoo.apis;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.retrofit.OfferingsVisibilityResponse;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.Driver;
import product.clicklabs.jugnoo.retrofit.model.FareStructure;
import product.clicklabs.jugnoo.retrofit.model.FindADriverResponse;
import product.clicklabs.jugnoo.utils.DateOperations;
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
					Region regionSelected, final boolean beforeRequestRide, final boolean confirmedScreenOpened,
					final boolean savedAddressUsed){
		this.regionSelected = regionSelected;
		try {
			if(callback != null) {
				callback.onPre();
			}

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put(Constants.KEY_LATITUDE, String.valueOf(latLng.latitude));
			params.put(Constants.KEY_LONGITUDE, String.valueOf(latLng.longitude));
			if(Data.userData.getSubscriptionData().getUserSubscriptions() != null && Data.userData.getSubscriptionData().getUserSubscriptions().size() > 0) {
				params.put(Constants.KEY_AUTOS_BENEFIT_ID, String.valueOf(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getBenefitIdAutos()));
			}

			if (1 == showAllDrivers) {
				params.put("show_all", "1");
			}
			if(1 == showDriverInfo){
				params.put("show_phone_no", "1");
			}

			new HomeUtil().checkAndFillParamsForIgnoringAppOpen(activity, params);

			Log.i("params in find_a_driver", "=" + params);
			final long startTime = System.currentTimeMillis();

			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().findADriverCall(params, new retrofit.Callback<FindADriverResponse>() {
				@Override
				public void success(FindADriverResponse findADriverResponse, Response response) {
					try {
						String resp = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.e(TAG, "findADriverCall response=" + resp);

						double fareFactorOld = ApiFindADriver.this.regionSelected.getCustomerFareFactor();
						double driverFareFactorOld = ApiFindADriver.this.regionSelected.getDriverFareFactor();

						parseFindADriverResponse(findADriverResponse);

						Data.setLatLngOfJeanieLastShown(latLng);
						Data.autoData.setLastRefreshLatLng(latLng);
						refreshLatLng = latLng;
						refreshTime = System.currentTimeMillis();
						if(callback != null && !confirmedScreenOpened) {
							callback.onCompleteFindADriver();
						}

						if(beforeRequestRide){
							if(savedAddressUsed){
								callback.continueRequestRide(false, savedAddressUsed);
							} else {
								Region newRegion = null;
								for (Region region : Data.autoData.getRegions()) {
									if (region.getRegionId().equals(ApiFindADriver.this.regionSelected.getRegionId())) {
										newRegion = region;
									}
								}
								if (newRegion != null
										&& (Utils.compareDouble(fareFactorOld, newRegion.getCustomerFareFactor()) != 0
										|| Utils.compareDouble(driverFareFactorOld, newRegion.getDriverFareFactor()) != 0)) {
									callback.stopRequestRide(confirmedScreenOpened);
								} else {
									callback.continueRequestRide(confirmedScreenOpened, false);
								}
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
					String brandingStatus = driver.getBrandingStatus();
					Data.autoData.getDriverInfos().add(new DriverInfo(String.valueOf(driver.getUserId()), driver.getLatitude(), driver.getLongitude(), driver.getUserName(), "",
							"", driver.getPhoneNo(), String.valueOf(driver.getRating()), "", 0, bearing, vehicleType, (ArrayList<Integer>)driver.getRegionIds(), brandingStatus,
							driver.getOperatorId()));
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

			// using -1 as razorPayEnables does not come in getUserCurrentStatus api which uses this
			// same function to parse response
			if (findADriverResponse.getIsRazorpayEnabled()!=-1) {
				Data.autoData.setIsRazorpayEnabled(findADriverResponse.getIsRazorpayEnabled());
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

			try {
				if(Data.getDeliveryCustomerData() != null && Data.getDeliveryCustomerData().getPromoCoupons() == null){
					Data.getDeliveryCustomerData().setPromoCoupons(new ArrayList<PromoCoupon>());
				} else{
					Data.getDeliveryCustomerData().getPromoCoupons().clear();
				}
				if(findADriverResponse.getDeliveryCustomerPromotions() != null) {
					Data.getDeliveryCustomerData().getPromoCoupons().addAll(findADriverResponse.getDeliveryCustomerPromotions());
				}
				if(findADriverResponse.getDeliveryCustomerCoupons() != null) {
					Data.getDeliveryCustomerData().getPromoCoupons().addAll(findADriverResponse.getDeliveryCustomerCoupons());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if(Data.getPayData() != null && Data.getPayData().getPromoCoupons() == null){
					Data.getPayData().setPromoCoupons(new ArrayList<PromoCoupon>());
				} else{
					Data.getPayData().getPromoCoupons().clear();
				}
				if(findADriverResponse.getPayPromotions() != null) {
					Data.getPayData().getPromoCoupons().addAll(findADriverResponse.getPayPromotions());
				}
				if(findADriverResponse.getPayCoupons() != null) {
					Data.getPayData().getPromoCoupons().addAll(findADriverResponse.getPayCoupons());
				}
			} catch (Exception e) {
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
								fareStructure.getDisplayFareText(), fareStructure.getOperatorId());
						for (int i = 0; i < Data.autoData.getRegions().size(); i++) {
							try {
								if (Data.autoData.getRegions().get(i).getOperatorId() == fareStructure.getOperatorId()
										&& Data.autoData.getRegions().get(i).getVehicleType().equals(fareStructure.getVehicleType())
										&& Data.autoData.getRegions().get(i).getRideType().equals(fareStructure.getRideType())
										) {
									Data.autoData.getRegions().get(i).setFareStructure(fareStructure1);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (regionSelected.getOperatorId() == fareStructure.getOperatorId()
								&& regionSelected.getVehicleType().equals(fareStructure.getVehicleType())
								&& regionSelected.getRideType().equals(fareStructure.getRideType())
								) {
							Data.autoData.setFareStructure(fareStructure1);
						}
					}
				}
			}

			if(findADriverResponse.getTopupCardEnabled() != null){
				Data.userData.setTopupCardEnabled(findADriverResponse.getTopupCardEnabled());
			}

			if(!TextUtils.isEmpty(findADriverResponse.getGamePredictUrl())) {
				Data.userData.setGamePredictUrl(findADriverResponse.getGamePredictUrl());
			}

			parseResponseForOfferingsEnabled(findADriverResponse);

		} catch (Exception exception) {
			exception.printStackTrace();
		}


		try{
			if(findADriverResponse.getPointsOfInterestAddresses() != null){
				Data.userData.getPointsOfInterestAddresses().clear();
				Data.userData.getPointsOfInterestAddresses().addAll(findADriverResponse.getPointsOfInterestAddresses());
			}
		} catch (Exception e){}
	}

	public  static void parseResponseForOfferingsEnabled(OfferingsVisibilityResponse.OfferingsVisibilityData findADriverResponse) {
		if(findADriverResponse.getAutosEnabled()!= null) {
            Data.userData.setAutosEnabled(findADriverResponse.getAutosEnabled());
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
		if(findADriverResponse.getDeliveryCustomerEnabled() != null) {
            Data.userData.setDeliveryCustomerEnabled(findADriverResponse.getDeliveryCustomerEnabled());
        }
		if(findADriverResponse.getPayEnabled() != null) {
            Data.userData.setPayEnabled(findADriverResponse.getPayEnabled());
        }
		if(findADriverResponse.getIntegratedJugnooEnabled() != null){
            Data.userData.setIntegratedJugnooEnabled(findADriverResponse.getIntegratedJugnooEnabled());
        }


		if(findADriverResponse.getFeedEnabled() != null) {
            Data.userData.setFeedEnabled(findADriverResponse.getFeedEnabled());
        }
		if(findADriverResponse.getProsEnabled() != null) {
            Data.userData.setProsEnabled(findADriverResponse.getProsEnabled());
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
		void continueRequestRide(boolean confirmedScreenOpened, boolean savedAddressUsed);
		void stopRequestRide(boolean confirmedScreenOpened);
		void onFinish();
	}

}
