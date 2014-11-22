package com.kamil.android_location.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.kamil.android_location.notification.BasicNotificationBuilder;
import com.kamil.android_location.notification.INotificationBuilder;

import java.util.List;

public class GeofenceActionService extends IntentService {

    private static final String LOG_TAG = "GeofenceActionService";

    public GeofenceActionService() {
        super("GeofenceActionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            // Get the error code with a static method
            int errorCode = LocationClient.getErrorCode(intent);
            // Log the error
            Log.e(LOG_TAG, "Location Services error: " + errorCode);
        } else {

            List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);

            int transitionType = LocationClient.getGeofenceTransition(intent);

            sendNotification(transitionType, geofences.get(0));
        }
    }

    private void sendNotification(int transitionType, Geofence geofence) {
        GeofenceManager manager = GeofenceManager.getInstance();
        BasicGeofence basicGeofence = manager.get(Integer.parseInt(geofence.getRequestId()));

        String transitionText = null;
        switch(transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionText = "Entered " + basicGeofence.getDescription();
                manager.setGeofenceTag(basicGeofence.getDescription());
                Log.d(LOG_TAG, "Received geofence ENTER event");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionText = "Exited " + basicGeofence.getDescription();
                manager.setGeofenceTag("");
                Log.d(LOG_TAG, "Received geofence EXIT event");
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                transitionText = "Dwelled in " + basicGeofence.getDescription();
                Log.d(LOG_TAG, "Received geofence DWELL event");
                break;
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        INotificationBuilder builder = new BasicNotificationBuilder(getApplicationContext());
        mNotificationManager.notify(1, builder.build(transitionText, "yes he did"));
    }
}
