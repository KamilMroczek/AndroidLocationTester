package com.sense360.android.location.tester.manager;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.sense360.android.location.tester.Constants;
import com.sense360.android.location.tester.android.DeviceServices;
import com.sense360.android.location.tester.http.HttpHelper;
import com.sense360.android.location.tester.location.UserLocationBuilder;
import com.sense360.android.location.tester.recorder.ILocationRecorder;
import com.sense360.android.location.tester.recorder.LocationSender;

public class LocationUpdateManagerFactory {

  private HttpHelper httpHelper;
  private DeviceServices deviceServices;

  public LocationUpdateManagerFactory(HttpHelper httpHelper, DeviceServices deviceServices) {
    this.httpHelper = httpHelper;
    this.deviceServices = deviceServices;
  }

  public ILocationUpdateManager build(Intent intent) {
    if (intent != null) {
      return buildFromIntent(intent);
    } else {
      return buildDefault();
    }
  }

  private ILocationUpdateManager buildFromIntent(Intent intent) {
    int requestType = intent.getIntExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, LocationRequest.PRIORITY_HIGH_ACCURACY);

    String providerType;
    switch (requestType) {
      case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
        providerType = Constants.BALANCED_POWER;
        break;
      case LocationRequest.PRIORITY_LOW_POWER:
        providerType = Constants.LOW_POWER;
        break;
      case LocationRequest.PRIORITY_NO_POWER:
        providerType = Constants.NO_POWER;
        break;
      default:
        providerType = Constants.HIGH_ACCURACY;
        break;
    }

    String note = intent.getStringExtra(Constants.NOTE_EXTRA);

    int refreshIntervalSecs = intent.getIntExtra(Constants.REFRESH_INTERVAL_EXTRA, 10);

    Log.d(Constants.LOG_TAG, "Received start. Type=" + providerType + ", interval=" + refreshIntervalSecs);

    ILocationRecorder logRecorder = new LocationSender(httpHelper);
    UserLocationBuilder builder = new UserLocationBuilder(deviceServices, refreshIntervalSecs, providerType, note);

    return new LocationUpdateManager(logRecorder, builder, providerType, requestType, refreshIntervalSecs);
  }

  private ILocationUpdateManager buildDefault() {
    int requestType = LocationRequest.PRIORITY_HIGH_ACCURACY;
    String providerType = Constants.HIGH_ACCURACY;

    String note = "";

    int refreshIntervalSecs = 10;

    Log.d(Constants.LOG_TAG, "Received start. Type=" + providerType + ", interval=" + refreshIntervalSecs);

    ILocationRecorder logRecorder = new LocationSender(httpHelper);
    UserLocationBuilder builder = new UserLocationBuilder(deviceServices, refreshIntervalSecs, providerType, note);

    return new LocationUpdateManager(logRecorder, builder, providerType, requestType, refreshIntervalSecs);
  }
}
