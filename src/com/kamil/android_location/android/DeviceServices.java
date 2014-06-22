package com.kamil.android_location.android;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

public class DeviceServices {

	private LocationManager locationManager;
	
	private static final String LOG_TAG = "Device Services";
	
	public DeviceServices(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public int isGpsOn() {
		if(locationManager == null) {
			return 0;
		}
		
	    try {
	      return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? 1 : 0;
	    } catch (Exception ex) {
	    	Log.d(LOG_TAG, "Error checking for GPS. " + ex.getMessage());
	    	return 0;
	    }
	}
	
	public int isNetworkOn() {
		if(locationManager == null) {
			return 0;
		}

	    try {
	      return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? 1 : 0;
	    } catch (Exception ex) {
	    	Log.d(LOG_TAG, "Error checking for Network. " + ex.getMessage());
	    	return 0;
	    }
	}

}
