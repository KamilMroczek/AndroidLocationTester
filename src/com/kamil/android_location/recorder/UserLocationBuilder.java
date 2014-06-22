package com.kamil.android_location.recorder;

import android.location.Location;

import com.kamil.android_location.UserLocation;

public class UserLocationBuilder {

	private int intervalRefreshSecs;
	private String providerType;
	private String note;
	
	public UserLocationBuilder(int refreshIntervalSecs, String providerType, String note) {
		this.intervalRefreshSecs = refreshIntervalSecs;
		this.providerType = providerType;
		this.note = note;
	}
	
	public UserLocation build(Location location) {
		return new UserLocation(location, intervalRefreshSecs, providerType, note);
	}
	
}
