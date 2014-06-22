package com.kamil.android_location.recorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kamil.android_location.DeviceServices;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class LocationLogger implements ILocationRecorder {

	private Logger LOCATION_LOG = LoggerFactory.getLogger(LocationUpdateManager.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	
	private DeviceServices deviceServices;
	
	private int refreshIntervalSecs;
	private String providerType;
	private String note;
	
	public LocationLogger(Context context, int intervalBetweenUpdatesSecs, String providerType, String note) {
		deviceServices = new DeviceServices(context);
		refreshIntervalSecs = intervalBetweenUpdatesSecs;
		this.providerType = providerType;
		this.note = note;
	}
	
	@Override
	public void record(Location location) {
		Log.d(providerType, "Received Location Update");
		
		String locationLine = providerType; 
		Date date = new Date(location.getTime());
		locationLine += "," + location.getProvider() + "," + location.getTime() + "," + DATE_FORMAT.format(date);
		locationLine += "," + refreshIntervalSecs;
		locationLine += "," + location.getAccuracy() + "," + location.getLatitude() + "," + location.getLongitude();
		locationLine += "," + location.getSpeed() + "," + location.getBearing() + "," + location.getAltitude();
		locationLine += "," + deviceServices.isGpsOn() + "," + deviceServices.isNetworkOn() + "," + note;
		
		Log.d(providerType, locationLine);
		LOCATION_LOG.info(locationLine);
	}

}
