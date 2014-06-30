package com.kamil.android_location.manager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.android.GooglePlayHelper;

public class LocationClientManager implements ILocationClientManager {

    private final GooglePlayHelper mGooglePlayHelper;

    private ILocationUpdateManager mLocationUpdaterManager;
    private LocationClient mLocationClient;

    private boolean requestedLocationUpdates;


    public LocationClientManager() {
        mGooglePlayHelper = new GooglePlayHelper();

        requestedLocationUpdates = false;
    }

    @Override
    public void connect(Context context, ILocationUpdateManager locationUpdateManager) {
        Log.d(Constants.LOG_TAG, "Trying to connect location client.");

        boolean servicesConnected = mGooglePlayHelper.servicesConnected(context);

        if(servicesConnected) {
            // TODO: if a location client is already connected, we should remove this one and
            // reconnect. Or at least re-add the new locationUpdateManager
            mLocationClient = new LocationClient(context, this, this);
            mLocationClient.connect();
        }

        mLocationUpdaterManager = locationUpdateManager;
    }

    @Override
    public void disconnect() {
        Log.d(Constants.LOG_TAG, "Disconnecting location client.");

        if (mLocationClient != null && mLocationClient.isConnected()) {
            if(mLocationUpdaterManager != null) {
                mLocationClient.removeLocationUpdates(mLocationUpdaterManager);
            }
            requestedLocationUpdates = false;

            mLocationClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constants.LOG_TAG, "Connected location client.");

        if (!requestedLocationUpdates && mLocationUpdaterManager != null) {
            LocationRequest locationRequest = mLocationUpdaterManager.getLocationRequest();
            mLocationClient.requestLocationUpdates(locationRequest, mLocationUpdaterManager);

            requestedLocationUpdates = true;
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(Constants.LOG_TAG, "Disconnected location client.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Constants.LOG_TAG, "Connection failed for location client.");
    }
}
