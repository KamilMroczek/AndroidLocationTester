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

    private final String providerType;
    private boolean started;
    
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private int fusedProviderPriority;
    private int intervalBetweenUpdatesSecs;
    
    private ILocationRecorder locationRecorder;
    private UserLocationBuilder userLocationBuilder;
    
	public LocationUpdateManager(ILocationRecorder recorder, UserLocationBuilder builder, String type, int priority,
			int refreshIntervalSecs) {
		locationRecorder = recorder;
		userLocationBuilder = builder;
		
		this.providerType = type;
		this.started = false;
		this.fusedProviderPriority = priority;
		this.intervalBetweenUpdatesSecs = refreshIntervalSecs;
		
	}
    
    public void start(Context context) {
    	Log.d(providerType, "Starting Location Logger: " + providerType);

    	int intervalBetweenUpdatesMillis = intervalBetweenUpdatesSecs * Constants.MILLIS_PER_SECOND; 
    	mLocationRequest = LocationRequest.create();
    	mLocationRequest.setFastestInterval(intervalBetweenUpdatesMillis);
		mLocationRequest.setPriority(this.fusedProviderPriority);
		mLocationRequest.setInterval(intervalBetweenUpdatesMillis);
		
    	mLocationClient = new LocationClient(context, this, this);
		mLocationClient.connect();
		
		started = true;
    }
    
	public void stop() {
		Log.d(providerType, "Stopping Location Logger: " + providerType);
		if (mLocationClient != null) {
			if (mLocationClient.isConnected()) {
				Log.d(providerType, "Stopping location updates.");
				mLocationClient.removeLocationUpdates(this);
			}

			mLocationClient.disconnect();
			started = false;
		}
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		Log.d(providerType, "Received Location Update");
		locationRecorder.record(userLocationBuilder.build(newLocation));
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(providerType, "Connection failed for Location Client");
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(providerType, "Connected Location Client. Requesting Location Updates. Interval=" + intervalBetweenUpdatesSecs);
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		Log.d(providerType, "Disconnected Location Client");
	}
		
    public boolean isStarted() {
    	return started;
    }
}
