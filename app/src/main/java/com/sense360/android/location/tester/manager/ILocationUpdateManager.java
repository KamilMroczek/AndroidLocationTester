package com.sense360.android.location.tester.manager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public interface ILocationUpdateManager extends LocationListener {

    LocationRequest getLocationRequest();
}
