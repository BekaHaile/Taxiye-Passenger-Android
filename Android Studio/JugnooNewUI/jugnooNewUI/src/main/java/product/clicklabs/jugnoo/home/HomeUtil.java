package product.clicklabs.jugnoo.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hippo.HippoConfig;
import com.hippo.HippoTicketAttributes;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import io.branch.referral.Branch;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.GCMIntentService;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.CorporatesAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.home.models.HippoTicketModel;
import product.clicklabs.jugnoo.home.models.HippoTicketRideModel;
import product.clicklabs.jugnoo.home.models.Region;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.retrofit.model.FetchUserAddressResponse;
import product.clicklabs.jugnoo.room.DBObject;
import product.clicklabs.jugnoo.room.apis.DBCoroutine;
import product.clicklabs.jugnoo.room.database.SearchLocationDB;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.models.ActionType;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedString;

/**
 * Created by shankar on 4/3/16.
 */
public class HomeUtil {

	public void checkAndFillParamsForIgnoringAppOpen(Context context, HashMap<String, String> params){
		long currentTime = System.currentTimeMillis();
		long lastPushReceivedTime = Prefs.with(context).getLong(Constants.SP_LAST_PUSH_RECEIVED_TIME, (currentTime + 1));
		long diff = currentTime - lastPushReceivedTime;
		params.put(Constants.KEY_LAST_PUSH_TIME_DIFF, String.valueOf(diff));
	}

	public static VehicleIconSet getVehicleIconSet(String name){
		if(VehicleIconSet.YELLOW_AUTO.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.YELLOW_AUTO;
		}
		else if(VehicleIconSet.RED_AUTO.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.RED_AUTO;
		}
		else if(VehicleIconSet.ORANGE_BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.ORANGE_BIKE;
		}
		else if(VehicleIconSet.YELLOW_BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.YELLOW_BIKE;
		}
		else if(VehicleIconSet.RED_BIKE.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.RED_BIKE;
		}
		else if(VehicleIconSet.ORANGE_CAR.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.ORANGE_CAR;
		}
		else if(VehicleIconSet.YELLOW_CAR.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.YELLOW_CAR;
		}
		else if(VehicleIconSet.RED_CAR.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.RED_CAR;
		}
		else if(VehicleIconSet.HELICOPTER.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.HELICOPTER;
		}
		else if(VehicleIconSet.ERICKSHAW.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.ERICKSHAW;
		}
		else if(VehicleIconSet.TRANSPORT.getName().equalsIgnoreCase(name)){
			return VehicleIconSet.TRANSPORT;
		}
		else{
			return VehicleIconSet.ORANGE_AUTO;
		}
	}

	public static SearchResult getNearBySavedAddress(Context context, LatLng latLng, boolean includeRecent){
		try {
			ArrayList<SearchResult> searchResults = new ArrayList<>();
			if (!Prefs.with(context).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
				String homeString = Prefs.with(context).getString(SPLabels.ADD_HOME, "");
				SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
				searchResults.add(searchResult);
			}
			if (!Prefs.with(context).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
				String workString = Prefs.with(context).getString(SPLabels.ADD_WORK, "");
				SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
				searchResults.add(searchResult);
			}
			searchResults.addAll(Data.userData.getSearchResults());
			double compareDistance = Constants.MAX_DISTANCE_TO_USE_SAVED_LOCATION;
			if(includeRecent) {
				searchResults.addAll(Data.userData.getSearchResultsRecent());
				if(Data.autoData.getUseRecentLocAtRequest() == 1){
					compareDistance = Data.autoData.getUseRecentLocAutoSnapMaxDistance();
					searchResults.addAll(Data.userData.getSearchResultsAdditional());
					for (FetchUserAddressResponse.Address address : Data.userData.getPointsOfInterestAddresses()) {
						SearchResult searchResult = new SearchResult("", "", "", address.getLat(), address.getLng());
						searchResults.add(searchResult);
					}
				}
			}

			double distance = Double.MAX_VALUE;
			SearchResult selectedNearByAddress = null;
			for(int i=0; i<searchResults.size(); i++){
				double fetchedDistance = MapUtils.distance(latLng, searchResults.get(i).getLatLng());
				if (fetchedDistance <= compareDistance && fetchedDistance < distance) {
					if (selectedNearByAddress == null
							|| TextUtils.isEmpty(selectedNearByAddress.getName())
							|| !TextUtils.isEmpty(searchResults.get(i).getName())) {
								distance = fetchedDistance;
								selectedNearByAddress = searchResults.get(i);
							}
				}
			}
			if(selectedNearByAddress != null
					&& Utils.compareDouble(selectedNearByAddress.getLatitude(), 0) != 0
					&& Utils.compareDouble(selectedNearByAddress.getLongitude(), 0) != 0){
				return selectedNearByAddress;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void logoutFunc(Activity activity, String message){
		try {
			PicassoTools.clearCache(Picasso.with(activity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Prefs.with(activity).save(Constants.FIRSTTIME_LOGIN,-1);
		FacebookLoginHelper.logoutFacebook();

		GCMIntentService.clearNotifications(activity);

		Data.clearDataOnLogout(activity);

		if(DBObject.INSTANCE.getInstance() != null) {
			DBCoroutine.Companion.deleteAll(DBObject.INSTANCE.getInstance());
			SearchLocationDB.Companion.clearInstance();
			DBObject.INSTANCE.clearInstance();
		}

		HomeActivity.userMode = UserMode.PASSENGER;
		HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;

		ActivityCompat.finishAffinity(activity);
		Intent intent = new Intent(activity, SplashNewActivity.class);
		if(message != null){
			intent.putExtra(Constants.KEY_LOGGED_OUT, 1);
			intent.putExtra(Constants.KEY_MESSAGE, message);
		}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);

		Branch.getInstance(activity).logout();
	}


	public void putDefaultParams(Map<String, String> params){
		addDefaultParams(params);
	}

	public static void addDefaultParams(Map<String, String> params){
		params.put(Constants.KEY_APP_VERSION, String.valueOf(MyApplication.getInstance().appVersion()));
		params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);
		params.put(Constants.KEY_LOGIN_TYPE, String.valueOf(0));
		params.put(Constants.KEY_OPERATOR_TOKEN, MyApplication.getInstance().getString(R.string.operator_token));
		params.put(Constants.KEY_CUSTOMER_PACKAGE_NAME, MyApplication.getInstance().getPackageName());
		params.put(Constants.KEY_LOCALE, LocaleHelper.getLanguage(MyApplication.getInstance()));
		Utils.logRequestBody(params);
	}

	public void putDefaultParamsMultipart(MultipartTypedOutput multipartTypedOutput){
		multipartTypedOutput.addPart(Constants.KEY_APP_VERSION, new TypedString(String.valueOf(MyApplication.getInstance().appVersion())));
		multipartTypedOutput.addPart(Constants.KEY_DEVICE_TYPE, new TypedString(String.valueOf(Data.DEVICE_TYPE)));
		multipartTypedOutput.addPart(Constants.KEY_LOGIN_TYPE, new TypedString(String.valueOf(0)));
		multipartTypedOutput.addPart(Constants.KEY_OPERATOR_TOKEN, new TypedString(MyApplication.getInstance().getString(R.string.operator_token)));
		multipartTypedOutput.addPart(Constants.KEY_CUSTOMER_PACKAGE_NAME, new TypedString(MyApplication.getInstance().getPackageName()));
		multipartTypedOutput.addPart(Constants.KEY_LOCALE, new TypedString(LocaleHelper.getLanguage(MyApplication.getInstance())));
		Utils.logRequestBody(multipartTypedOutput);
	}

	public ArrayList<SearchResult> getSavedPlacesWithHomeWork(Activity activity){
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
			String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
			SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
			searchResults.add(searchResult);
		}
		if (!Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
			String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
			SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
			searchResults.add(searchResult);
		}
		searchResults.addAll(Data.userData.getSearchResults());
		return searchResults;
	}

	public MarkerOptions getMarkerOptionsForSavedAddress(Activity activity, ASSL assl, SearchResult searchResult, boolean showAddress){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(TextUtils.isEmpty(searchResult.getName()) ? "recent" : searchResult.getName());
		markerOptions.title(TextUtils.isEmpty(searchResult.getAddress()) ? "poi" : markerOptions.getTitle());

		String addressName = "";
		int drawable = R.drawable.ic_saved_address_used;
		if(searchResult.getName() != null && !searchResult.getName().equalsIgnoreCase("")){
			addressName = searchResult.getName();

			if(addressName.equalsIgnoreCase(Constants.TYPE_HOME)){
				drawable = R.drawable.ic_saved_address_home;
			} else if(addressName.equalsIgnoreCase(Constants.TYPE_WORK)){
				drawable = R.drawable.ic_saved_address_work;
			} else if(TextUtils.isEmpty(addressName)){
				drawable = R.drawable.ic_saved_address_used;
			} else{
				drawable = R.drawable.ic_saved_address_other;
			}
		}

		if(!showAddress){
			addressName = "";
		}

		markerOptions.snippet(searchResult.getAddress());
		markerOptions.position(searchResult.getLatLng());
		if (!TextUtils.isEmpty(searchResult.getAddress())) {
			markerOptions.anchor(0.1f, 1f);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
					.getSavedAddressBitmap(activity, addressName, activity.getResources().getDimensionPixelSize(R.dimen.text_size_28),
							drawable, R.color.text_color)));
		} else {
			if(searchResult.getFreq() != null){
				addressName = String.valueOf(searchResult.getFreq());
			}
			markerOptions.anchor(0.1f, 1f);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
					.getSavedAddressBitmap(activity, addressName, activity.getResources().getDimensionPixelSize(R.dimen.text_size_24),
							R.drawable.ic_point_of_interest_marker, R.color.brown_marker_text)));
		}
		return markerOptions;
	}

	private ArrayList<Marker> markersSavedAddresses = new ArrayList<>();
	public void displaySavedAddressesAsFlags(Activity activity, ASSL assl, GoogleMap map, boolean showAddress, PassengerScreenMode passengerScreenMode){
		try {
			if(map != null){
				removeSavedAddress(map);
				if(passengerScreenMode == PassengerScreenMode.P_INITIAL && Data.autoData.getUseRecentLocAtRequest() == 1){
					if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
						String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
						SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
						markersSavedAddresses.add(map.addMarker(getMarkerOptionsForSavedAddress(activity, assl, searchResult, showAddress)));

					}
					if (!Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
						String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
						SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
						markersSavedAddresses.add(map.addMarker(getMarkerOptionsForSavedAddress(activity, assl, searchResult, showAddress)));
					}
					for(SearchResult searchResult : Data.userData.getSearchResults()){
						markersSavedAddresses.add(map.addMarker(getMarkerOptionsForSavedAddress(activity, assl, searchResult, showAddress)));
					}
					for(SearchResult searchResult : Data.userData.getSearchResultsRecent()){
						markersSavedAddresses.add(map.addMarker(getMarkerOptionsForSavedAddress(activity, assl, searchResult, showAddress)));
					}
					for(SearchResult searchResult : Data.userData.getSearchResultsAdditional()){
						markersSavedAddresses.add(map.addMarker(getMarkerOptionsForSavedAddress(activity, assl, searchResult, showAddress)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeSavedAddress(GoogleMap map){
		if(map != null) {
			if (markersSavedAddresses != null) {
				for (Marker marker : markersSavedAddresses) {
					marker.remove();
				}
				markersSavedAddresses.clear();
			}
		}
	}

	public enum SavedAddressState{

		MARKER_WITH_TEXT(0),
		MARKER(1),
		BLANK(2);

		private int ordinal;

		SavedAddressState(int ordinal) {
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}


	private ArrayList<Marker> markersPointsOfInterest = new ArrayList<>();
	public void displayPointOfInterestMarkers(Activity activity, ASSL assl, GoogleMap map, PassengerScreenMode passengerScreenMode){
		try {
			if (map != null) {
				removeMarkersPointsOfInterest(map);
				if (passengerScreenMode == PassengerScreenMode.P_INITIAL && Data.autoData.getUseRecentLocAtRequest() == 1) {
					final LatLng mapTarget = map.getCameraPosition().target;
					Collections.sort(Data.userData.getPointsOfInterestAddresses(), new Comparator<FetchUserAddressResponse.Address>() {
						@Override
						public int compare(FetchUserAddressResponse.Address lhs, FetchUserAddressResponse.Address rhs) {
							try {
								LatLng lhsLatLng = new LatLng(lhs.getLat(), lhs.getLng());
								LatLng rhsLatLng = new LatLng(rhs.getLat(), rhs.getLng());
								double distanceLhs = MapUtils.distance(mapTarget, lhsLatLng);
								double distanceRhs = MapUtils.distance(mapTarget, rhsLatLng);
								return (int) (distanceLhs - distanceRhs);
							} catch (Exception e) {
							}
							return 0;
						}
					});
					int size = Math.min(5, Data.userData.getPointsOfInterestAddresses().size());
					for (int i = 0; i < size; i++) {
						FetchUserAddressResponse.Address address = Data.userData.getPointsOfInterestAddresses().get(i);
						SearchResult searchResult = new SearchResult("", "", "", address.getLat(), address.getLng());
						searchResult.setFreq(address.getFreq());
						markersPointsOfInterest.add(map.addMarker(getMarkerOptionsForSavedAddress(activity, assl, searchResult, false)));
					}
				}
			}
		} catch (Exception e){}
	}

	public void removeMarkersPointsOfInterest(GoogleMap map){
		if(map != null) {
			if (markersPointsOfInterest != null) {
				for (Marker marker : markersPointsOfInterest) {
					marker.remove();
				}
				markersPointsOfInterest.clear();
			}
		}
	}

	public ProductType getProductType(int productType){
		if(productType == ProductType.AUTO.getOrdinal()){
			return ProductType.AUTO;
		} else if(productType == ProductType.FRESH.getOrdinal()){
			return ProductType.FRESH;
		} else if(productType == ProductType.MEALS.getOrdinal()){
			return ProductType.MEALS;
		} else if(productType == ProductType.GROCERY.getOrdinal()){
			return ProductType.GROCERY;
		} else if(productType == ProductType.MENUS.getOrdinal()){
			return ProductType.MENUS;
		} else if(productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()){
			return ProductType.DELIVERY_CUSTOMER;
		} else if(productType == ProductType.PAY.getOrdinal()){
			return ProductType.PAY;
		} else if(productType == ProductType.FEED.getOrdinal()){
			return ProductType.FEED;
		} else if(productType == ProductType.PROS.getOrdinal()){
			return ProductType.PROS;
		} else {
			return ProductType.NOT_SURE;
		}
	}

	public void openFuguOrSupport(FragmentActivity activity, View container, int orderId, int supportCategory, String deliveryDate, int productType){
		if (Data.isFuguChatEnabled()) {
			try {
				if(productType == ProductType.MENUS.getOrdinal()){
					HippoConfig.getInstance().openChat(activity, Data.CHANNEL_ID_FUGU_MENUS_DELIVERY_LATE());
				} else if(productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()){
					HippoConfig.getInstance().openChat(activity, Data.CHANNEL_ID_FUGU_DELIVERY_CUSTOMER_DELIVERY_LATE());
				} else {
					HippoConfig.getInstance().showConversations(activity,activity.getString(R.string.fugu_support_title));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Utils.showToast(activity, activity.getString(R.string.something_went_wrong));
			}

		} else {
			ArrayList<ShowPanelResponse.Item> items = MyApplication.getInstance().getDatabase2().getSupportDataItems(supportCategory);
			if(items.size() > 0) {
				ShowPanelResponse.Item item = new ShowPanelResponse.Item();
				item.setItems(items);
				item.setActionType(ActionType.NEXT_LEVEL.getOrdinal());
				item.setSupportId(supportCategory);
				item.setText(activity.getString(R.string.order_is_late));

				new TransactionUtils().openItemInFragment(activity, container, -1, "",
						activity.getResources().getString(R.string.support_main_title), item, "",
						orderId, deliveryDate,
						Config.getSupportNumber(activity), productType);
			} else {
				new TransactionUtils().openRideIssuesFragment(activity, container,
						-1, orderId, null, null, 0, false, 0, null,
						supportCategory, productType, deliveryDate);
			}
		}
	}

	public int getSavedLocationIcon(String name){
		int drawableRes = R.drawable.ic_loc_other;
		if(name.equalsIgnoreCase(Constants.TYPE_HOME)){
			drawableRes = R.drawable.ic_home;
		} else if(name.equalsIgnoreCase(Constants.TYPE_WORK)){
			drawableRes = R.drawable.ic_work;
		}
		return drawableRes;
	}

	/**
	 * Returns client id corresponding to deep index for offerings merged in Fafatat NEW
	 * @param deepIndex
	 * @return
	 */
	public static String getClientIdFromDeepIndexForMergedOfferings(int deepIndex){
		if(deepIndex == AppLinkIndex.FRESH_PAGE.getOrdinal()){
			return Config.getFreshClientId();
		} else if(deepIndex == AppLinkIndex.MEAL_PAGE.getOrdinal()){
			return Config.getMealsClientId();
		} else if(deepIndex == AppLinkIndex.MENUS_PAGE.getOrdinal()){
			return Config.getMenusClientId();
		} else if(deepIndex == AppLinkIndex.DELIVERY_CUSTOMER_PAGE.getOrdinal()){
			return Config.getDeliveryCustomerClientId();
		} else if(deepIndex == AppLinkIndex.FEED_PAGE.getOrdinal()){
			return Config.getFeedClientId();
		} else if(deepIndex == AppLinkIndex.PROS_PAGE.getOrdinal()){
			return Config.getProsClientId();
		}
		return "";
	}
	public void forceRTL(Activity activity){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (LocaleHelper.getLanguage(activity).equalsIgnoreCase("ar")) {
				activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
			} else {
				activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
			}
		}
	}

	public static void setVehicleIcon(Context context, ImageView imageView, String imageUrl, int resId, boolean loadingPlaceholder, Callback callback){
		if(!TextUtils.isEmpty(imageUrl)) {
			Picasso.with(context).load(imageUrl)
					.placeholder(loadingPlaceholder ? R.drawable.ic_vehicle_loader : resId)
					.error(resId)
					.into(imageView, callback);
		} else {
			imageView.setImageResource(resId);
		}
	}


    public static int chooseNextEligiblePaymentOption(int paymentOption, HomeActivity activity) {
        ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
        if (paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0) {
            List<Integer> restrictedPaymentMode = new ArrayList<>();
            List<Integer> restrictedCorporates = new ArrayList<>();
            ArrayList<Region> regions = Data.autoData.getRegions();
            if (regions.size() > 1) {
                restrictedPaymentMode = activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRestrictedPaymentModes();
            } else if (regions.size() > 0) {
                restrictedPaymentMode = regions.get(0).getRestrictedPaymentModes();
            }
            if (regions.size() > 1) {
                restrictedCorporates = activity.getSlidingBottomPanel().getRequestRideOptionsFragment().getRegionSelected().getRestrictedCorporates();
            } else if (regions.size() > 0) {
                restrictedCorporates = regions.get(0).getRestrictedCorporates();
            }
            for (PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas) {
                if (paymentModeConfigData.getEnabled() == 1) {
                    if ((restrictedPaymentMode.size() > 0 && !restrictedPaymentMode.contains(paymentModeConfigData.getPaymentOption())) || restrictedPaymentMode.size() == 0) {
						if(paymentModeConfigData.getPaymentOption() != PaymentOption.CORPORATE.getOrdinal() || restrictedCorporates.size() == 0
								|| !restrictedCorporates.contains(CorporatesAdapter.Companion.getSelectedCorporateBusinessId())){
							paymentOption = paymentModeConfigData.getPaymentOption();
							break;
						}
                    }
                }
            }

        }
        return paymentOption;
    }


	public static void openHippoTicketSupport(Context context){
		if(Data.userData != null) {
			HippoTicketModel hippoTicketModel = new HippoTicketModel(Integer.parseInt(Data.userData.getUserId()), Data.userData.getRegAs());
			HippoTicketAttributes.Builder builder = new HippoTicketAttributes.Builder();
			builder.setFaqName(Prefs.with(context).getString(Constants.HIPPO_SUPPORT_FAQ_NAME,
					context.getString(R.string.customer_hippo_support_faq_name)));
			HippoConfig.getInstance().showFAQSupport(builder.build(), hippoTicketModel);
		}
	}

	public static void openHippoTicketForRide(Context context, int engagementId, int driverId){
		if(Data.userData != null) {
			HippoTicketRideModel hippoTicketModel = new HippoTicketRideModel(Integer.parseInt(Data.userData.getUserId()), Data.userData.getRegAs(),
					driverId, engagementId);
			HippoTicketAttributes.Builder builder = new HippoTicketAttributes.Builder();
			builder.setFaqName(Prefs.with(context).getString(Constants.KEY_HIPPO_TICKET_RIDE_FAQ_NAME,
					context.getString(R.string.hippo_ticket_ride_faq_name)));
			HippoConfig.getInstance().showFAQSupport(builder.build(), hippoTicketModel);
		}
	}
}
