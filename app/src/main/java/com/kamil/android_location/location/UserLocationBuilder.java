package com.kamil.android_location.location;

import android.location.Location;

import com.kamil.android_location.android.DeviceServices;
import com.kamil.android_location.geofence.GeofenceManager;

public class UserLocationBuilder {

	private int intervalRefreshSecs;
	private String providerType;
	private String note;
	private DeviceServices deviceServices;
	
	public UserLocationBuilder(DeviceServices services, int refreshIntervalSecs, String providerType, String note) {
		this.intervalRefreshSecs = refreshIntervalSecs;
		this.providerType = providerType;
		this.note = note;
		this.deviceServices = services;
	}
	
	public UserLocation build(Location location) {
    GeofenceManager manager = GeofenceManager.getInstance();
    
		return new UserLocation(location, deviceServices, intervalRefreshSecs, providerType, note + " (" + manager.getGeofenceTag() + ")");
	}
	
}
