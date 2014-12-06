package com.sense360.android.location.tester.location;

import android.location.Location;

import com.sense360.android.location.tester.android.DeviceServices;
import com.sense360.android.location.tester.geofence.GeofenceManager;

public class UserLocationBuilder {

  private int intervalRefreshSecs;
  private String providerType;
  private String note;
  private DeviceServices deviceServices;

  public UserLocationBuilder(DeviceServices services, int refreshIntervalSecs, String providerType, String note) {
    this.intervalRefreshSecs = refreshIntervalSecs;
    this.providerType = providerType;
    this.note = note;
    this.deviceServices = services;
  }

  public UserLocation build(Location location) {
    GeofenceManager manager = GeofenceManager.getInstance();

    return new UserLocation(location, deviceServices, intervalRefreshSecs, providerType, note);
  }

}
