package com.kamil.android_location.background;

import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.GooglePlayHelper;
import com.kamil.android_location.recorder.LocationUpdateManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocationBackgroundService extends Service {

	private GooglePlayHelper mGooglePlayHelper;
	private boolean mServicesConnected;
	private LocationUpdateManager mLocationLogger;
	
	private static final String LOG_TAG = "Location Background Service";
	
	@Override
    public void onCreate() {
		mGooglePlayHelper = new GooglePlayHelper();
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(LOG_TAG, "Start service.");
		
    	mLocationLogger = parseIntentAndCreateLogger(intent);
		
		mServicesConnected = mGooglePlayHelper.servicesConnected(this);
        if (mServicesConnected && mLocationLogger != null) {
        	if(!mLocationLogger.isStarted()) {
        		mLocationLogger.start(this.getApplicationContext());
        	}
		}
        return START_REDELIVER_INTENT;
    }
    
    private LocationUpdateManager parseIntentAndCreateLogger(Intent intent) {
    	int requestType = intent.getIntExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, LocationRequest.PRIORITY_HIGH_ACCURACY); 
    	String providerType = (requestType == LocationRequest.PRIORITY_HIGH_ACCURACY) ? Constants.HIGH_ACCURACY : Constants.BALANCED_POWER;
    	
    	int refreshIntervalSecs = intent.getIntExtra(Constants.REFRESH_INTERVAL_EXTRA, 10);
    	
    	Log.d(LOG_TAG, "Received start. Type=" + providerType + ", interval=" + refreshIntervalSecs);
    	
    	return new LocationUpdateManager(providerType, requestType, refreshIntervalSecs);
    }

    @Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Stop service.");
		
		mLocationLogger.stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
