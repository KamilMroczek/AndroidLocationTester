package com.kamil.android_location.background;

import android.os.AsyncTask;

import com.kamil.android_location.http.HttpHelper;

public class SendUserLocationToServerTask extends AsyncTask<String, Void, Boolean> {

  private final String serviceUrl = "https://fierce-eyrie-4789.herokuapp.com/user_location/create";
  private final HttpHelper http;

  public SendUserLocationToServerTask(HttpHelper http) {
    this.http = http;
  }

  @Override
  protected Boolean doInBackground(String... params) {
    String json = params[0];

    return http.postJsonData(serviceUrl, json);
  }
}
