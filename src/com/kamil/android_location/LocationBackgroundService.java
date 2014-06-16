package com.kamil.android_location;

import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocationBackgroundService extends Service {

	private GooglePlayHelper mGooglePlayHelper;
	private boolean mServicesConnected;
	private LocationLogger mLocationLoggerHighAccuracy;
	private LocationLogger mLocationLoggerBalanced;
	
	@Override
    public void onCreate() {
		mGooglePlayHelper = new GooglePlayHelper();
		mLocationLoggerHighAccuracy = new LocationLogger("HighAccuracy", LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationLoggerBalanced = new LocationLogger("BalancedAccuracy", LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d("Location Background Service", "Start service.");
		
		mServicesConnected = mGooglePlayHelper.servicesConnected(this);
        if (mServicesConnected) {
        	if(!mLocationLoggerHighAccuracy.isStarted()) {
        		mLocationLoggerHighAccuracy.start(this.getApplicationContext());
        	}
        	if(!mLocationLoggerBalanced.isStarted()) {
        		mLocationLoggerBalanced.start(this.getApplicationContext());
        	}
		}
        return START_REDELIVER_INTENT;
    }

    @Override
	public void onDestroy() {
		Log.d("Location Background Service", "Stop service.");
		
		mLocationLoggerHighAccuracy.stop();
		mLocationLoggerBalanced.stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
