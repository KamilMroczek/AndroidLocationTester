package com.kamil.android_location.recorder;

import android.util.Log;

import com.google.gson.Gson;
import com.kamil.android_location.HttpHelper;
import com.kamil.android_location.UserLocation;
import com.kamil.android_location.background.SendUserLocationToServerTask;

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
