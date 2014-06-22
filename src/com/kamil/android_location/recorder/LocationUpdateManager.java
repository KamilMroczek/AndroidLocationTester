package com.kamil.android_location.recorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.DeviceServices;

public class LocationUpdateManager implements ILocationUpdaterManager {

    private Logger LOCATION_LOG = LoggerFactory.getLogger(LocationUpdateManager.class);
    private final String logTag;
    private boolean started;
    
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private int fusedProviderPriority;
    private int intervalBetweenUpdates;
    
    private DeviceServices deviceServices;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    
    public LocationUpdateManager(String tag, int priority, int refreshIntervalSecs) { 
    	logTag = tag;
    	started = false;
    	this.fusedProviderPriority = priority;
    	intervalBetweenUpdates = Constants.MILLIS_PER_SECOND * refreshIntervalSecs;
    }
    
    public void start(Context context) {
    	Log.d(logTag, "Starting Location Logger: " + logTag);

    	mLocationRequest = LocationRequest.create();
    	mLocationRequest.setFastestInterval(intervalBetweenUpdates);
		mLocationRequest.setPriority(this.fusedProviderPriority);
		mLocationRequest.setInterval(intervalBetweenUpdates);
		
    	mLocationClient = new LocationClient(context, this, this);
		mLocationClient.connect();
		
		deviceServices = new DeviceServices(context);
		
		started = true;
    }
    
	public void stop() {
		Log.d(logTag, "Stopping Location Logger: " + logTag);
		if (mLocationClient != null) {
			if (mLocationClient.isConnected()) {
				Log.d(logTag, "Stopping location updates.");
				mLocationClient.removeLocationUpdates(this);
			}

			mLocationClient.disconnect();
		}
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		logLocation(newLocation);
	}
	
	private void logLocation(Location location) {
		Log.d(logTag, "Received Location Update");
		
		String locationLine = logTag; 
		Date date = new Date(location.getTime());
		locationLine += "," + location.getProvider() + "," + location.getTime() + "," + DATE_FORMAT.format(date);
		locationLine += "," + (intervalBetweenUpdates / 1000); 
		locationLine += "," + location.getAccuracy() + "," + location.getLatitude() + "," + location.getLongitude();
		locationLine += "," + location.getSpeed() + "," + location.getBearing() + "," + location.getAltitude();
		locationLine += "," + deviceServices.isGpsOn() + "," + deviceServices.isNetworkOn();
		
		Log.d(logTag, locationLine);
		LOCATION_LOG.info(locationLine);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(logTag, "Connection failed for Location Client");
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(logTag, "Connected Location Client. Requesting Location Updates. Interval=" + (intervalBetweenUpdates / 1000));
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		Log.d(logTag, "Disconnected Location Client");
	}
		
    public boolean isStarted() {
    	return started;
    }
}