package com.sense360.android.location.tester.manager;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.sense360.android.location.tester.Constants;
import com.sense360.android.location.tester.location.UserLocationBuilder;
import com.sense360.android.location.tester.recorder.ILocationRecorder;

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
    int intervalBetweenUpdatesMillis = intervalBetweenUpdatesSecs * Constants.SECOND;

    LocationRequest locationRequest = LocationRequest.create();
    locationRequest.setFastestInterval(intervalBetweenUpdatesMillis);
    locationRequest.setPriority(this.fusedProviderPriority);
    locationRequest.setInterval(intervalBetweenUpdatesMillis);
    locationRequest.setSmallestDisplacement(0);

    return locationRequest;
  }

  @Override
  public void onLocationChanged(Location newLocation) {
    Log.d(providerType, "Received Location Update.");
    locationRecorder.record(userLocationBuilder.build(newLocation));
  }
}
