package com.kamil.android_location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationLogger implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	
	// A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = Constants.MILLIS_PER_SECOND * 10;
    private Logger LOCATION_LOG = LoggerFactory.getLogger(LocationLogger.class);
    private final String logTag;
    private boolean started;
    
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private int priority;
    private int intervalBetweenUpdates;
    
    public LocationLogger(String tag, int priority) { 
    	logTag = tag;
    	started = false;
    	this.priority = priority;
    	intervalBetweenUpdates = Constants.MILLIS_PER_SECOND * 10;
    }
    
    public void start(Context context) {
    	Log.d(logTag, "Starting Location Logger: " + logTag);

    	mLocationRequest = LocationRequest.create();
    	mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		mLocationRequest.setPriority(this.priority);
		mLocationRequest.setInterval(intervalBetweenUpdates);
		
    	mLocationClient = new LocationClient(context, this, this);
		mLocationClient.connect();
		
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
		locationLine += "," + location.getProvider() + "," + location.getTime() + "," + intervalBetweenUpdates; 
		locationLine += "," + location.getAccuracy() + "," + location.getLatitude() + "," + location.getLongitude();
		locationLine += "," + location.getSpeed() + "," + location.getBearing() + "," + location.getAltitude();
		
		Log.d(logTag, locationLine);
		LOCATION_LOG.info(locationLine);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(logTag, "Connection failed for Location Client");
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(logTag, "Connected Location Client. Requesting Location Updates");
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
