package com.sense360.android.location.tester.geofence;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

public interface IGeofenceManager extends
        LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener {

    List<Geofence> buildGeofences();

    List<String> getGeofenceIds();
}
