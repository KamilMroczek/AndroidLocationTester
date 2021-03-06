package com.sense360.android.location.tester.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sense360.android.location.tester.Constants;
import com.sense360.android.location.tester.android.DeviceServices;
import com.sense360.android.location.tester.geofence.GeofenceManager;
import com.sense360.android.location.tester.geofence.IGeofenceManager;
import com.sense360.android.location.tester.http.HttpHelper;
import com.sense360.android.location.tester.manager.ILocationClientManager;
import com.sense360.android.location.tester.manager.ILocationUpdateManager;
import com.sense360.android.location.tester.manager.LocationClientManager;
import com.sense360.android.location.tester.manager.LocationUpdateManagerFactory;

public class LocationBackgroundService extends Service {

  private static final String LOG_TAG = "Location Background Service";
  private ILocationClientManager locationClientManager;

  @Override
  public void onCreate() {
    locationClientManager = new LocationClientManager(getApplicationContext());
  }

  // TODO: add ability for only one instance of service to be running
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(LOG_TAG, "Start service.");

    LocationUpdateManagerFactory locationUpdateManagerFactory =
        new LocationUpdateManagerFactory(new HttpHelper(), new DeviceServices(getApplicationContext()));
    if (intent == null) {
      Log.d(LOG_TAG, "Intent is null");
    }
    ILocationUpdateManager locationUpdateManager = locationUpdateManagerFactory.build(intent);

    IGeofenceManager geofenceManager = GeofenceManager.getInstance();

    String background_type = intent.getStringExtra(Constants.BACKGROUND_TYPE_EXTRA);

    locationClientManager.connect(locationUpdateManager, geofenceManager, background_type);

    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    Log.d(LOG_TAG, "Stop service.");

    locationClientManager.disconnect();

    super.onDestroy();
  }

  // TODO: Implement to save power
  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return null;
  }
}
