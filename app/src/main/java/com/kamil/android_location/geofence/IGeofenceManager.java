package com.kamil.android_location.geofence;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

public interface IGeofenceManager extends
        LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener {

    List<Geofence> buildGeofences();

    List<String> getGeofenceIds();
}
