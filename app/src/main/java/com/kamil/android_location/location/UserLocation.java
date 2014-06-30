package com.kamil.android_location.location;

import com.google.gson.annotations.SerializedName;
import com.kamil.android_location.android.DeviceServices;

import android.location.Location;

public class UserLocation {
	
	@SerializedName("refresh_secs")
	private final int intervalRefreshSecs;
	@SerializedName("provider_type")
	private final String providerType;
	private final String note;
	@SerializedName("time")
	private final long time;
	private final float accuracy;
	private final double latitude;
	private final double longitude;
	private final float speed;
	private final float bearing;
	private final double altitude;
	@SerializedName("gps_on")
	private final int gpsOn;
	@SerializedName("network_on")
	private final int networkOn;
	private final String device;
	
	public UserLocation(Location location, DeviceServices deviceServices, int intervalSecs, String provider, String note) {
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
		
		this.gpsOn = deviceServices.isGpsOn();
		this.networkOn = deviceServices.isNetworkOn();
		this.device = deviceServices.getAndroidId();
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

	public int getGpsOn() {
		return gpsOn;
	}

	public int getNetworkOn() {
		return networkOn;
	}
	
	public String getDevice() {
		return device;
	}
}
