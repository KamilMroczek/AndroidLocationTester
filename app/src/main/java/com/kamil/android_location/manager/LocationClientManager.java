package com.kamil.android_location.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.android.GooglePlayHelper;
import com.kamil.android_location.geofence.GeofenceActionService;
import com.kamil.android_location.geofence.IGeofenceManager;

import java.util.List;

public class LocationClientManager implements ILocationClientManager {

    private final GooglePlayHelper mGooglePlayHelper;

    private Context context;
    private LocationClient mLocationClient;

    private ILocationUpdateManager mLocationUpdaterManager;
    private IGeofenceManager mGeofenceManager;

    private boolean requestedLocationUpdates;

    private String type = "geofence";


    public LocationClientManager(Context context) {
        mGooglePlayHelper = new GooglePlayHelper();
        this.context = context;

        requestedLocationUpdates = false;
    }

    @Override
    public void connect(ILocationUpdateManager locationUpdateManager, IGeofenceManager geofenceManager, String type) {
        Log.d(Constants.LOG_TAG, "Trying to connect location client. " + type);

        this.type = type;

        mLocationUpdaterManager = locationUpdateManager;
        mGeofenceManager = geofenceManager;

        boolean servicesConnected = mGooglePlayHelper.servicesConnected(context);

        if(servicesConnected) {
            // TODO: if a location client is already connected, we should remove this one and
            // reconnect. Or at least re-add the new locationUpdateManager and geofenceManager
            mLocationClient = new LocationClient(context, this, this);
            mLocationClient.connect();
        }
    }

    @Override
    public void disconnect() {
        Log.d(Constants.LOG_TAG, "Disconnecting location client.");

        if (mLocationClient != null && mLocationClient.isConnected()) {
            if(mLocationUpdaterManager != null) {
                mLocationClient.removeLocationUpdates(mLocationUpdaterManager);
            }
            if(mGeofenceManager != null) {
                mLocationClient.removeGeofences(mGeofenceManager.getGeofenceIds(), mGeofenceManager);
            }
            requestedLocationUpdates = false;

            mLocationClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constants.LOG_TAG, "Connected location client.");

        if (!requestedLocationUpdates) {
            if(mLocationUpdaterManager != null && type.equals("location")) {
                LocationRequest locationRequest = mLocationUpdaterManager.getLocationRequest();
                mLocationClient.requestLocationUpdates(locationRequest, mLocationUpdaterManager);
            }

            if(mGeofenceManager != null && type.equals("geofence")) {
                List<Geofence> geofences = mGeofenceManager.buildGeofences();

                Intent intent = new Intent(context, GeofenceActionService.class);

                PendingIntent pendingIntent =
                        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                mLocationClient.addGeofences(geofences, pendingIntent, mGeofenceManager);

            }

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
