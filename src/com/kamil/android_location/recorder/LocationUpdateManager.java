package com.kamil.android_location.recorder;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;

public class LocationUpdateManager implements ILocationUpdateManager {

    private final String logTag;
    private boolean started;
    
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private int fusedProviderPriority;
    private int intervalBetweenUpdatesMillis;
    
    private ILocationRecorder locationRecorder;
    
    public LocationUpdateManager(ILocationRecorder recorder, String tag, int priority, int refreshIntervalSecs) {
    	locationRecorder = recorder;
    	logTag = tag;
    	started = false;
    	this.fusedProviderPriority = priority;
    	intervalBetweenUpdatesMillis = Constants.MILLIS_PER_SECOND * refreshIntervalSecs;
    }
    
    public void start(Context context) {
    	Log.d(logTag, "Starting Location Logger: " + logTag);

    	mLocationRequest = LocationRequest.create();
    	mLocationRequest.setFastestInterval(intervalBetweenUpdatesMillis);
		mLocationRequest.setPriority(this.fusedProviderPriority);
		mLocationRequest.setInterval(intervalBetweenUpdatesMillis);
		
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
			started = false;
		}
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		locationRecorder.record(newLocation);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(logTag, "Connection failed for Location Client");
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(logTag, "Connected Location Client. Requesting Location Updates. Interval=" + (intervalBetweenUpdatesMillis / 1000));
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
