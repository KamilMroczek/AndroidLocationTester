package com.kamil.android_location.manager;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.android.DeviceServices;
import com.kamil.android_location.http.HttpHelper;
import com.kamil.android_location.location.UserLocationBuilder;
import com.kamil.android_location.recorder.ILocationRecorder;
import com.kamil.android_location.recorder.LocationSender;

public class LocationUpdateManagerFactory {

    private HttpHelper httpHelper;
    private DeviceServices deviceServices;

    public LocationUpdateManagerFactory(HttpHelper httpHelper, DeviceServices deviceServices) {
        this.httpHelper = httpHelper;
        this.deviceServices = deviceServices;
    }

    public ILocationUpdateManager build(Intent intent) {
        int requestType = intent.getIntExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, LocationRequest.PRIORITY_HIGH_ACCURACY);
        String providerType = (requestType == LocationRequest.PRIORITY_HIGH_ACCURACY) ? Constants.HIGH_ACCURACY : Constants.BALANCED_POWER;

        String note = intent.getStringExtra(Constants.NOTE_EXTRA);

        int refreshIntervalSecs = intent.getIntExtra(Constants.REFRESH_INTERVAL_EXTRA, 10);

        Log.d(Constants.LOG_TAG, "Received start. Type=" + providerType + ", interval=" + refreshIntervalSecs);

        ILocationRecorder logRecorder = new LocationSender(httpHelper);
        UserLocationBuilder builder = new UserLocationBuilder(deviceServices, refreshIntervalSecs, providerType, note);

        return new LocationUpdateManager(logRecorder, builder, providerType, requestType, refreshIntervalSecs);
    }
}
