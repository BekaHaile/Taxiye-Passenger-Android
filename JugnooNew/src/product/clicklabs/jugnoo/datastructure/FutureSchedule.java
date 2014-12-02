package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class FutureSchedule {
	
	public String scheduleId, pickupAddress, pickupTime, scheduleStatusStr;
	public LatLng pickupLatLng;
	public int isChangeable, scheduleStatusFlag;
	
	
	public FutureSchedule(String scheduleId, String pickupAddress, String pickupTime, String scheduleStatusStr, LatLng pickupLatLng, int isChangeable, int scheduleStatusFlag){
		this.scheduleId = scheduleId;
		this.pickupAddress = pickupAddress;
		this.pickupTime = pickupTime;
		this.scheduleStatusStr = scheduleStatusStr;
		this.pickupLatLng = pickupLatLng;
		this.isChangeable = isChangeable;
		this.scheduleStatusFlag = scheduleStatusFlag;
	}
	
	@Override
	public String toString() {
		return scheduleId + " " + pickupLatLng + " " + pickupTime + " " + scheduleStatusStr + " " + isChangeable;
	}
	
}
