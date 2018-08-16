package product.clicklabs.jugnoo.datastructure;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleIconSet;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.utils.BitmapUtils;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.Utils;

public class DriverInfo {
	
	public String userId, name, image, carImage, phoneNumber, rating, carNumber, brandingStatus;
	public LatLng latLng;
	public int freeRide;
	
	public String promoName = Data.NO_PROMO_APPLIED, cancelRideThrashHoldTime, poolRideStatusString;
	private String eta = "10";
	private double fareFixed;
	private int preferredPaymentMode, isPooledRide, chatEnabled;
	private double bearing;

	private Schedule scheduleT20;

	private int vehicleType, cancellationCharges;
	private ArrayList<Integer> regionIds = new ArrayList<>();
	private VehicleIconSet vehicleIconSet;
	private ArrayList<String> fellowRiders = new ArrayList<>();
	private int operatorId;
	private String currency;
	private String markerUrl;
	private int paymentMethod;
	private Double tipAmount;
	public DriverInfo(String userId){
		this.userId = userId;
	}

	//for drivers to show in free state
	public DriverInfo(String userId, double latitude, double longitude, 
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, double bearing, int vehicleType, ArrayList<Integer> regionIds, String brandingStatus, int operatorId,
					  int paymentMethod){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = freeRide;
		this.bearing = bearing;
		this.vehicleType = vehicleType;
		this.regionIds = regionIds;
		this.brandingStatus = brandingStatus;
		this.operatorId = operatorId;
		this.paymentMethod = paymentMethod;
	}

	//for engagement
	public DriverInfo(String userId, double latitude, double longitude,
			String name, String image, String carImage, String phoneNumber, String rating, String carNumber, 
			int freeRide, String promoName, String eta, double fareFixed, int preferredPaymentMode, Schedule scheduleT20,
					  int vehicleType, String iconSet, String cancelRideThrashHoldTime, int cancellationCharges, int isPooledRide,
					  String poolRideStatusString, ArrayList<String> fellowRiders, double bearing, int chatEnabled, int operatorId,
					  String currency, String markerUrl,Double tipAmount){
		this.userId = userId;
		this.latLng = new LatLng(latitude, longitude);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = phoneNumber;
		this.rating = rating;
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = freeRide;
		if(!"".equalsIgnoreCase(promoName)){
			this.promoName = promoName;
		}
		if(!"".equalsIgnoreCase(eta)){
			setEta(eta);
		}
		this.fareFixed = fareFixed;
		this.preferredPaymentMode = preferredPaymentMode;
		this.scheduleT20 = scheduleT20;
		this.vehicleType = vehicleType;
		this.vehicleIconSet = new HomeUtil().getVehicleIconSet(iconSet);
		this.cancelRideThrashHoldTime = cancelRideThrashHoldTime;
		this.cancellationCharges = cancellationCharges;
		this.isPooledRide = isPooledRide;
		this.poolRideStatusString = poolRideStatusString;
		this.fellowRiders = fellowRiders;
		this.bearing = bearing;
		this.chatEnabled = chatEnabled;
		this.operatorId = operatorId;
		this.currency = currency;
		this.markerUrl = markerUrl;
		this.tipAmount = tipAmount;
	}

	//for last ride data
	public DriverInfo(String userId, String name, String image, String carImage, String carNumber, int operatorId){
		this.userId = userId;
		this.latLng = new LatLng(0, 0);
		this.name = name;
		this.image = image;
		this.carImage = carImage;
		this.phoneNumber = "";
		this.rating = "4";
		this.carNumber = carNumber.toUpperCase(Locale.ENGLISH);
		this.freeRide = 0;
		this.operatorId = operatorId;
	}

	public void setEta(String eta){
        this.eta = eta;
	}

	public String getEta(){
		return this.eta;
	}



	@Override
	public String toString() {
		return  "Id: " + userId + "\n" +
				"Branding Status: "+ brandingStatus;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((DriverInfo)o).userId.equalsIgnoreCase(this.userId)){
				return true;
			}
		} catch(Exception e){
		}
		return false;
	}


	public double getFareFixed() {
		return fareFixed;
	}

	public String getFareFixedStr(){
		return Utils.getMoneyDecimalFormat().format(getFareFixed());
	}

	public int getPreferredPaymentMode() {
		return preferredPaymentMode;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public Schedule getScheduleT20() {
		return scheduleT20;
	}

	public void setScheduleT20(Schedule scheduleT20) {
		this.scheduleT20 = scheduleT20;
	}

	public int getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}

	public VehicleIconSet getVehicleIconSet() {
		return vehicleIconSet;
	}

	public void setVehicleIconSet(String iconSet) {
		this.vehicleIconSet = new HomeUtil().getVehicleIconSet(iconSet);
	}

	public ArrayList<Integer> getRegionIds() {
		return regionIds;
	}

	public void setRegionIds(ArrayList<Integer> regionIds) {
		this.regionIds = regionIds;
	}

	public String getCancelRideThrashHoldTime() {
		return cancelRideThrashHoldTime;
	}

	public void setCancelRideThrashHoldTime(String cancelRideThrashHoldTime) {
		this.cancelRideThrashHoldTime = cancelRideThrashHoldTime;
	}

	public int getCancellationCharges() {
		return cancellationCharges;
	}

	public void setCancellationCharges(int cancellationCharges) {
		this.cancellationCharges = cancellationCharges;
	}

	public int getIsPooledRide() {
		return isPooledRide;
	}

	public void setIsPooledRide(int isPooledRide) {
		this.isPooledRide = isPooledRide;
	}

	public String getPoolRideStatusString() {
		return poolRideStatusString;
	}

	public void setPoolRideStatusString(String poolRideStatusString) {
		this.poolRideStatusString = poolRideStatusString;
	}

	public ArrayList<String> getFellowRiders() {
		return fellowRiders;
	}

	public void setFellowRiders(ArrayList<String> fellowRiders) {
		this.fellowRiders = fellowRiders;
	}


	public int getChatEnabled() {
		return chatEnabled;
	}

	public void setChatEnabled(int chatEnabled) {
		this.chatEnabled = chatEnabled;
	}

	private Bitmap getMarkerBitmap(Activity activity, boolean loader){
		if(loader) {
			return CustomMapMarkerCreator.createMarkerBitmapForResource(activity,
					R.drawable.ic_vehicle_loader, 51f, 71f);
		} else {
			if (vehicleIconSet == VehicleIconSet.ERICKSHAW) {
				return CustomMapMarkerCreator.createMarkerBitmapForResource(activity,
						vehicleIconSet.getIconMarker(), 34f, 52f);
			} else {
				return CustomMapMarkerCreator
						.createMarkerBitmapForResource(activity, vehicleIconSet.getIconMarker());
			}
		}
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(int paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getMarkerUrl() {
		return markerUrl;
	}

	public void setMarkerUrl(String markerUrl) {
		this.markerUrl = markerUrl;
	}

	public enum PaymentMethod{
		CASH(1),
		BOTH(2); // default

		private int ordinal;
		PaymentMethod(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

	// dont remove this, it is for disabling GC for Picasso targets
	private Target target;

	public Marker addMarkerToMap(String markerUrl, final Activity context, final GoogleMap map, final MarkerOptions markerOptions){
		if(!TextUtils.isEmpty(markerUrl)){
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(context, true)));
			final Marker marker = map.addMarker(markerOptions);
			RequestCreator requestCreator = MyApplication.getPicasso(context).load(markerUrl)
					.resize(Utils.dpToPx(context, 27), Utils.dpToPx(context, 34));
			target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
					try {
						if(marker != null && marker.isVisible()) {
							marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledDownBitmap(bitmap,
									context.getResources().getDimensionPixelSize(R.dimen.dp_38), true)));
						}
					} catch (Exception ignored) {
					}
				}

				@Override
				public void onBitmapFailed(Drawable drawable) {
				}

				@Override
				public void onPrepareLoad(Drawable drawable) {
				}
			};
			PicassoTools.into(requestCreator, target);
			return marker;
		} else {
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(context, false)));
			return map.addMarker(markerOptions);
		}
	}

	public Double getTipAmount() {
		return  tipAmount!=null && tipAmount>0?tipAmount:null;
	}

	public void setTipAmount(Double tipAmount) {
		this.tipAmount = tipAmount;
	}

}
