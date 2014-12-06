package com.sense360.android.location.tester.recorder;

import android.util.Log;

import com.google.gson.Gson;
import com.sense360.android.location.tester.http.HttpHelper;
import com.sense360.android.location.tester.location.UserLocation;
import com.sense360.android.location.tester.background.SendUserLocationToServerTask;

public class LocationSender implements ILocationRecorder {

	private final Gson gson;
	private final HttpHelper http;
	
	public LocationSender(HttpHelper http) {
		this.http = http;
		gson = new Gson();
	}
	
	@Override
	public void record(UserLocation location) {
		Log.d("Location Sender", "Submitting location to server.");
		
		String json = gson.toJson(location);
		Log.d("Post", json);
		
		SendUserLocationToServerTask task = new SendUserLocationToServerTask(http);
		task.execute(new String[] { json });
	}
}
