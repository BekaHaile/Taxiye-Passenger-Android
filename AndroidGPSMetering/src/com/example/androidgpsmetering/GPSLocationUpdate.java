package com.example.androidgpsmetering;

import android.location.Location;

public interface GPSLocationUpdate {
	public void onGPSLocationChanged(Location location);
}
