package com.jugnoo.pay.location;



import android.location.Location;

public interface LocationUpdate {
	public void onLocationChanged(Location location, int priority);
}
