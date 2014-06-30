package com.kamil.android_location.manager;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.location.UserLocationBuilder;
import com.kamil.android_location.recorder.ILocationRecorder;

public class LocationUpdateManager implements ILocationUpdateManager {

    private final String providerType;

    private int fusedProviderPriority;
    private int intervalBetweenUpdatesSecs;
    
    private ILocationRecorder locationRecorder;
    private UserLocationBuilder userLocationBuilder;
    
	public LocationUpdateManager(ILocationRecorder recorder, UserLocationBuilder builder, String type, int priority,
			int refreshIntervalSecs) {
		locationRecorder = recorder;
		userLocationBuilder = builder;
		
		this.providerType = type;
		this.fusedProviderPriority = priority;
		this.intervalBetweenUpdatesSecs = refreshIntervalSecs;
	}
    
    public LocationRequest getLocationRequest() {
    	int intervalBetweenUpdatesMillis = intervalBetweenUpdatesSecs * Constants.MILLIS_PER_SECOND;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(intervalBetweenUpdatesMillis);
        locationRequest.setPriority(this.fusedProviderPriority);
        locationRequest.setInterval(intervalBetweenUpdatesMillis);

        return locationRequest;
    }

	@Override
	public void onLocationChanged(Location newLocation) {
		Log.d(providerType, "Received Location Update.");
		locationRecorder.record(userLocationBuilder.build(newLocation));
	}
}
