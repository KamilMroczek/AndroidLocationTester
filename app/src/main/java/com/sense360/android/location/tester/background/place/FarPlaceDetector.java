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
import com.sense360.android.location.tester.MainActivity;
import com.sense360.android.location.tester.http.HttpHelper;
import com.sense360.android.location.tester.location.UserLocation;
import com.sense360.android.location.tester.location.UserLocationBuilder;
import com.sense360.android.location.tester.recorder.ILocationRecorder;
import com.sense360.android.location.tester.recorder.LocationSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class is responsible for detecting presence within a big radius of any place.
 * kamilm (12/04/2014)
 */
public class FarPlaceDetector implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
                                         GooglePlayServicesClient.OnConnectionFailedListener {

  private Logger LOCATION_LOG = LoggerFactory.getLogger(FarPlaceDetector.class);
  private List<Place> mPlaceList;
  private LocationClient mLocationClient;
  private Context mContext;
  private float mLargeRadiusRange;
  private InsidePlaceDetector mInsidePlaceDetector;
  private UserLocationBuilder mUserLocationBuilder;
  private ILocationRecorder mLocationRecorder;
  private RotationHandler mRotationHandler;
  private NotificationManager mNotificationManager;
  private NotificationCompat.Builder mBuilder;
  private Random mRandom;

  public FarPlaceDetector(Context context, float largeRadiusRange, float smallRadiusRange, long closePullDelay,
                          UserLocationBuilder userLocationBuilder, UserLocationBuilder userLocationBuilder2) {
    setPlaces();

    mLargeRadiusRange = largeRadiusRange;
    mContext = context;

    mInsidePlaceDetector = new InsidePlaceDetector(context, mPlaceList, closePullDelay, smallRadiusRange,
                                                   userLocationBuilder2);
    mUserLocationBuilder = userLocationBuilder;
    mLocationRecorder = new LocationSender(new HttpHelper());

    mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    mBuilder = new NotificationCompat.Builder(context);
    mRandom = new Random(System.currentTimeMillis());
  }

  public void start() {
    //    log("start");
    if (mLocationClient == null) {
      mLocationClient = new LocationClient(mContext, this, this);
    }

    if (!mLocationClient.isConnected()) {
      //      log("connecting...");
      mLocationClient.connect();
    }
  }

  public void stop() {
    //    log("stop");
    if (mLocationClient != null) {
      //      log("removing location updates");
      mLocationClient.removeLocationUpdates(this);
      mLocationClient.disconnect();
    }

    mLocationClient = null;

    if (mInsidePlaceDetector.isConnected()) {
      mInsidePlaceDetector.stop();
    }

    if (mRotationHandler != null) {
      mRotationHandler.finishMonitoring();
    }
  }

  public boolean isConnected() {
    return mLocationClient != null && (mLocationClient.isConnected() || mLocationClient.isConnecting());
  }

  private void setPlaces() {
    mPlaceList = new ArrayList<Place>();

    mPlaceList.add(new Place("ns-1", "Shell on Culver", 34.016604d, -118.399765d));
    mPlaceList.add(new Place("ns-2", "Unalocal 76", 34.014013d, -118.402258d));
    mPlaceList.add(new Place("ns-3", "Shell on Venice", 34.018546d, -118.406475d));
    mPlaceList.add(new Place("ns-4", "Arco on Motor", 34.026545d, -118.408937d));
    mPlaceList.add(new Place("ns-5", "Valero", 34.031579d, -118.391182d));
    mPlaceList.add(new Place("ns-6", "Chevron Robertson", 34.032806d, -118.390472d));
    mPlaceList.add(new Place("ns-7", "76 @ Airdrome", 34.048719d, -118.385992d));
    mPlaceList.add(new Place("ns-8", "Mobil @ Cashio", 34.051984d, -118.383908d));
    mPlaceList.add(new Place("ns-9", "Futuristic Arco", 34.059194d, -118.383319d));
    mPlaceList.add(new Place("ns-10", "Mobile @ Wilshire", 34.065884d, -118.378063d));
    mPlaceList.add(new Place("ns-11", "76 Robertson & 3rd", 34.07385d, -118.383455d));
    mPlaceList.add(new Place("ns-12", "Shell @ Robertson", 34.059675d, -118.383851d));

    //    mPlaceList.add(new Place("ns-13", "NextSpace", 34.024275d, -118.394594d));
  }


  @Override
  public void onLocationChanged(Location location) {
    //    log("far location received");

    boolean placesWithinLargeRadius = anyPlacesWithinLargeRadius(location);

    UserLocation userLocation = mUserLocationBuilder.build(location);
    mLocationRecorder.record(userLocation);

    if (mInsidePlaceDetector.isConnected()) {
      if (!placesWithinLargeRadius) {
        log("Outside of big radius");
        sendNotification("outside big radius");
        mInsidePlaceDetector.stop();
        mRotationHandler.finishMonitoring();
      }
    } else {
      if (placesWithinLargeRadius) {
        //        log("Inside big radius");
        sendNotification("inside big radius");
        mInsidePlaceDetector.start();
        mRotationHandler = new RotationHandler(mContext);
        mRotationHandler.startMonitoring();
      }
    }
  }

  private void sendNotification(String text) {
    Notification notification = mBuilder
        .setTicker(text)
        .setContentTitle(text)
        .setContentText(text)
        .setSmallIcon(mContext.getApplicationInfo().icon)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND).build();

    mNotificationManager.notify(mRandom.nextInt(), notification);
  }

  private boolean anyPlacesWithinLargeRadius(Location location) {
    for (Place place : mPlaceList) {
      float distanceFromPlace = location.distanceTo(place.getLocation());
      if (distanceFromPlace < mLargeRadiusRange) {
        log("-- Close " + place.mName + "(" + String.format("%.1f", distanceFromPlace) + "m away) --");
        return true;
      }
    }

    return false;
  }

  @Override
  public void onConnected(Bundle bundle) {
    //    log("far connected");

    LocationRequest locationRequest = new LocationRequest();
    locationRequest.setSmallestDisplacement(MainActivity.BIG_RADIUS_INTERVAL_B);
    locationRequest.setFastestInterval(0);
    locationRequest.setInterval(0);
    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    mLocationClient.requestLocationUpdates(locationRequest, this);
  }

  @Override
  public void onDisconnected() {
    log("far disconnected");
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    log("Location client failure - code " + connectionResult.getErrorCode());
  }

  private void log(String s) {
    Log.i("FarPlaceDetector", s);
    LOCATION_LOG.info(s);
  }
}
