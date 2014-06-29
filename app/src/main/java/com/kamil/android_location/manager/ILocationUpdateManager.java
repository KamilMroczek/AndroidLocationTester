package com.kamil.android_location.manager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public interface ILocationUpdateManager extends LocationListener {

    LocationRequest getLocationRequest();
}
