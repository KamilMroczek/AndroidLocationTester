package com.kamil.android_location.android;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GooglePlayHelper {
	
	public GooglePlayHelper() {
		
	}
	
	public boolean servicesConnected(Context context) {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Google Services", "Google Play services is available.");
			return true;
		} else { // Google Play services was not available for some reason
			Log.d("Google Services", "Google Play services is NOT available.");
			return false;
		}
	}

}
