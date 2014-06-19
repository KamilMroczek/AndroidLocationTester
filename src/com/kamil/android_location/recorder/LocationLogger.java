package com.kamil.android_location.recorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;

public class LocationLogger implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private Logger LOCATION_LOG = LoggerFactory.getLogger(LocationLogger.class);
    private final String logTag;
    private boolean started;
    
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private int fusedProviderPriority;
    private int intervalBetweenUpdates;
    private LocationManager locationManager;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    
    public LocationLogger(String tag, int priority, int refreshIntervalSecs) { 
    	logTag = tag;
    	started = false;
    	this.fusedProviderPriority = priority;
    	intervalBetweenUpdates = Constants.MILLIS_PER_SECOND * refreshIntervalSecs;
    }
    
    public void start(Context context) {
    	Log.d(logTag, "Starting Location Logger: " + logTag);

    	mLocationRequest = LocationRequest.create();
    	mLocationRequest.setFastestInterval(intervalBetweenUpdates);
		mLocationRequest.setPriority(this.fusedProviderPriority);
		mLocationRequest.setInterval(intervalBetweenUpdates);
		
    	mLocationClient = new LocationClient(context, this, this);
		mLocationClient.connect();
		
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		started = true;
    }
    
	public void stop() {
		Log.d(logTag, "Stopping Location Logger: " + logTag);
		if (mLocationClient != null) {
			if (mLocationClient.isConnected()) {
				Log.d(logTag, "Stopping location updates.");
				mLocationClient.removeLocationUpdates(this);
			}

			mLocationClient.disconnect();
		}
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		logLocation(newLocation);
	}
	
	private void logLocation(Location location) {
		Log.d(logTag, "Received Location Update");
		
		String locationLine = logTag; 
		Date date = new Date(location.getTime());
		locationLine += "," + location.getProvider() + "," + location.getTime() + "," + DATE_FORMAT.format(date);
		locationLine += "," + (intervalBetweenUpdates / 1000); 
		locationLine += "," + location.getAccuracy() + "," + location.getLatitude() + "," + location.getLongitude();
		locationLine += "," + location.getSpeed() + "," + location.getBearing() + "," + location.getAltitude();
		locationLine += "," + isGpsOn() + "," + isNetworkOn();
		
		Log.d(logTag, locationLine);
		LOCATION_LOG.info(locationLine);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(logTag, "Connection failed for Location Client");
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d(logTag, "Connected Location Client. Requesting Location Updates. Interval=" + (intervalBetweenUpdates / 1000));
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		Log.d(logTag, "Disconnected Location Client");
	}
	
	private int isGpsOn() {
		if(locationManager == null) {
			return 0;
		}
		
	    try {
	      return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? 1 : 0;
	    } catch (Exception ex) {
	    	Log.d(logTag, "Error checking for GPS. " + ex.getMessage());
	    	return 0;
	    }
	}
	
	private int isNetworkOn() {
		if(locationManager == null) {
			return 0;
		}

	    try {
	      return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? 1 : 0;
	    } catch (Exception ex) {
	    	Log.d(logTag, "Error checking for Network. " + ex.getMessage());
	    	return 0;
	    }
	}
	
    public boolean isStarted() {
    	return started;
    }
}
