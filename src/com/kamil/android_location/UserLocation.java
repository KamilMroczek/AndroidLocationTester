package com.kamil.android_location;

import android.location.Location;

public class UserLocation {
	
	private final int intervalRefreshSecs;
	private final String providerType;
	private final String note;
	private final long time;
	private final float accuracy;
	private final double latitude;
	private final double longitude;
	private final float speed;
	private final float bearing;
	private final double altitude;	
	
	public UserLocation(Location location, int intervalSecs, String provider, String note) {
		this.intervalRefreshSecs = intervalSecs;
		this.note = note;
		this.providerType = provider;
		
		this.time = location.getTime();
		this.accuracy = location.getAccuracy();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.speed = location.getSpeed();
		this.bearing = location.getBearing();
		this.altitude = location.getAltitude();
	}

	public int getIntervalRefreshSecs() {
		return intervalRefreshSecs;
	}

	public String getProviderType() {
		return providerType;
	}

	public String getNote() {
		return note;
	}

	public long getTime() {
		return time;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getSpeed() {
		return speed;
	}

	public float getBearing() {
		return bearing;
	}

	public double getAltitude() {
		return altitude;
	}
}
