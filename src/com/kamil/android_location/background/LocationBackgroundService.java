package com.kamil.android_location.background;

import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.GooglePlayHelper;
import com.kamil.android_location.recorder.ILocationRecorder;
import com.kamil.android_location.recorder.ILocationUpdateManager;
import com.kamil.android_location.recorder.LocationLogger;
import com.kamil.android_location.recorder.LocationUpdateManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocationBackgroundService extends Service {

	private GooglePlayHelper mGooglePlayHelper;
	private boolean mServicesConnected;
	private ILocationUpdateManager mLocationUpdaterManager;
	
	private static final String LOG_TAG = "Location Background Service";
	
	@Override
    public void onCreate() {
		mGooglePlayHelper = new GooglePlayHelper();
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(LOG_TAG, "Start service.");
		
    	mLocationUpdaterManager = parseIntentAndCreateManager(intent);
		
		mServicesConnected = mGooglePlayHelper.servicesConnected(this);
        if (mServicesConnected && mLocationUpdaterManager != null) {
        	if(!mLocationUpdaterManager.isStarted()) {
        		mLocationUpdaterManager.start(this.getApplicationContext());
        	}
		}
        return START_REDELIVER_INTENT;
    }
    
    private LocationUpdateManager parseIntentAndCreateManager(Intent intent) {
    	int requestType = intent.getIntExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, LocationRequest.PRIORITY_HIGH_ACCURACY); 
    	String providerType = (requestType == LocationRequest.PRIORITY_HIGH_ACCURACY) ? Constants.HIGH_ACCURACY : Constants.BALANCED_POWER;
    	
    	int refreshIntervalSecs = intent.getIntExtra(Constants.REFRESH_INTERVAL_EXTRA, 10);
    	
    	Log.d(LOG_TAG, "Received start. Type=" + providerType + ", interval=" + refreshIntervalSecs);
    	
    	ILocationRecorder logRecorder = new LocationLogger(this, refreshIntervalSecs, providerType);
    	return new LocationUpdateManager(logRecorder, providerType, requestType, refreshIntervalSecs);
    }

    @Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Stop service.");
		
		mLocationUpdaterManager.stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
