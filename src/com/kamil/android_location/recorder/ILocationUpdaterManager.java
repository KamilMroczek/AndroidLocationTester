package com.kamil.android_location.recorder;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationListener;

import android.content.Context;

public interface ILocationUpdaterManager extends GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	void start(Context context);

	void stop();

	boolean isStarted();
}
