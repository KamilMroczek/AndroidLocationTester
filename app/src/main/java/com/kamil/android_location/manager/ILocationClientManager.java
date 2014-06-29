package com.kamil.android_location.manager;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesClient;

public interface ILocationClientManager extends GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    void connect(Context context, ILocationUpdateManager locationUpdateManager);

    void disconnect();
}
