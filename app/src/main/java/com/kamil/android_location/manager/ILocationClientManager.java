package com.kamil.android_location.manager;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.kamil.android_location.geofence.IGeofenceManager;

public interface ILocationClientManager extends GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    void connect(ILocationUpdateManager locationUpdateManager, IGeofenceManager geofenceManager, String type);

    void disconnect();
}
