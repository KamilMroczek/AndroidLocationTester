package com.sense360.android.location.tester.recorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sense360.android.location.tester.location.UserLocation;
import com.sense360.android.location.tester.android.DeviceServices;

import android.content.Context;
import android.util.Log;

public class LocationLogger implements ILocationRecorder {

	private Logger LOCATION_LOG = LoggerFactory.getLogger(LocationLogger.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	
	private DeviceServices deviceServices;
	
	public LocationLogger(Context context) {
		deviceServices = new DeviceServices(context);
	}
	
	@Override
	public void record(UserLocation update) {
		String locationLine = update.getProviderType();
		Date date = new Date(update.getTime());
		
		locationLine += "," + update.getTime() + "," + DATE_FORMAT.format(date);
		locationLine += "," + update.getIntervalRefreshSecs();
		locationLine += "," + update.getAccuracy() + "," + update.getLatitude() + "," + update.getLongitude();
		locationLine += "," + update.getSpeed() + "," + update.getBearing() + "," + update.getAltitude();
		locationLine += "," + deviceServices.isGpsOn() + "," + deviceServices.isNetworkOn() + "," + update.getNote();
		locationLine += "," + deviceServices.getAndroidId();
		
		Log.d("Location Log", locationLine);
		LOCATION_LOG.info(locationLine);
	}

}
