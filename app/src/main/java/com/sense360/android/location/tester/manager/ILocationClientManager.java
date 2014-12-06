package com.sense360.android.location.tester.manager;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.sense360.android.location.tester.geofence.IGeofenceManager;

public interface ILocationClientManager extends GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    void connect(ILocationUpdateManager locationUpdateManager, IGeofenceManager geofenceManager, String type);

    void disconnect();
}
