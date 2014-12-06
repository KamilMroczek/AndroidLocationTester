package com.sense360.android.location.tester.background.place;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.sense360.android.location.tester.Constants;
import com.sense360.android.location.tester.http.HttpHelper;
import com.sense360.android.location.tester.location.UserLocation;
import com.sense360.android.location.tester.location.UserLocationBuilder;
import com.sense360.android.location.tester.recorder.ILocationRecorder;
import com.sense360.android.location.tester.recorder.LocationSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Class is responsible for detecting whether you are inside a place.
 * kamilm (12/04/2014)
 */
public class InsidePlaceDetector implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
                                            GooglePlayServicesClient.OnConnectionFailedListener {

  private Logger LOCATION_LOG = LoggerFactory.getLogger(FarPlaceDetector.class);
  private NotificationManager mNotificationManager;
  private Random mRandom;
  private NotificationCompat.Builder mBuilder;
  private Context mContext;
  private List<Place> mPlaceList;
  private LocationClient mLocationClient;
  private long mPullDelay;
  private float mDetectionRadius;
  private Set<Place> mNotifiedPlaces;
  private UserLocationBuilder mUserLocationBuilder;
  private ILocationRecorder mLocationRecorder;
  private Location mLastLocationInside;
  private Long mLastLocationInsideTime;

  public InsidePlaceDetector(Context context, List<Place> places, long pullDelay, float detectionRadius,
                             UserLocationBuilder userLocationBuilder) {
    mContext = context;
    mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    mRandom = new Random(System.currentTimeMillis());
    mBuilder = new NotificationCompat.Builder(context);
    mPlaceList = places;
    mPullDelay = pullDelay;
    mDetectionRadius = detectionRadius;
    mNotifiedPlaces = new HashSet<Place>(3);
    mUserLocationBuilder = userLocationBuilder;
    mLocationRecorder = new LocationSender(new HttpHelper());
  }

  public void start() {
    //    log("start");

    if (isConnected()) {
      log("Not connecting because already connected");
      return;
    }

    mLocationClient = new LocationClient(mContext, this, this);
    mLocationClient.connect();
    mNotifiedPlaces.clear();
  }

  public void stop() {
    //    log("stop");

    if (isConnected()) {
      //      log("removing location updates");
      mLocationClient.removeLocationUpdates(this);
      mLocationClient.disconnect();
      mLocationClient = null;
    }
    mLastLocationInside = null;
    mLastLocationInsideTime = null;
  }

  public boolean isConnected() {
    return mLocationClient != null && (mLocationClient.isConnected() || mLocationClient.isConnecting());
  }

  @Override
  public void onConnected(Bundle bundle) {
    //    log("close connected");

    LocationRequest locationRequest = new LocationRequest();
    locationRequest.setSmallestDisplacement(0f);
    locationRequest.setFastestInterval(mPullDelay);
    locationRequest.setInterval(mPullDelay);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    mLocationClient.requestLocationUpdates(locationRequest, this);
  }

  @Override
  public void onDisconnected() {
    //    log("close disconnected");
  }

  @Override
  public void onLocationChanged(Location location) {
    //    log("close location received");

    UserLocation userLocation = mUserLocationBuilder.build(location);
    mLocationRecorder.record(userLocation);

    for (Place place : mPlaceList) {
      float distance = location.distanceTo(place.getLocation());
      if (distance < mDetectionRadius) {
        if (!mNotifiedPlaces.contains(place)) {
          sendNotification(place);
          mNotifiedPlaces.add(place);
        }

        String distanceStr = "Inside " + place.mName + " (" + String.format("%.1f", distance) + "m)";
        if (mLastLocationInside != null) {
          float diffFromLast = mLastLocationInside.distanceTo(location);
          float speed = diffFromLast / ((location.getTime() - mLastLocationInsideTime) / Constants.SECOND);
          distanceStr += " (Diff=" + String.format("%.1f", diffFromLast) + ",Speed= " + String
              .format("%.1f", speed) + ")";
        }

        log(distanceStr);

        mLastLocationInside = location;
        mLastLocationInsideTime = location.getTime();
      }
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    log("Location client failure - code " + connectionResult.getErrorCode());
  }

  private void sendNotification(Place place) {
    String text = "Inside " + place.mName;

    Notification notification = mBuilder
        .setTicker(text)
        .setContentTitle(text)
        .setContentText(text)
        .setSmallIcon(mContext.getApplicationInfo().icon)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND).build();

    mNotificationManager.notify(mRandom.nextInt(), notification);
  }

  private void log(String s) {
    Log.i("ClosePlaceDetector", s);
    LOCATION_LOG.info(s);
  }
}
