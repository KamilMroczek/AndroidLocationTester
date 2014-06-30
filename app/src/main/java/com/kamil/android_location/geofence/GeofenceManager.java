package com.kamil.android_location.geofence;

import android.app.PendingIntent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.kamil.android_location.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GeofenceManager implements IGeofenceManager {

    List<BasicGeofence> basicGeofences;

    public static final GeofenceManager INSTANCE = new GeofenceManager();

    public static GeofenceManager getInstance() {
        return INSTANCE;
    }

    private GeofenceManager() {
        fillList();
    }

    private void fillList() {
        basicGeofences = new ArrayList<BasicGeofence>();


        basicGeofences.add(new BasicGeofence("1", "Kamil Apartment", 34.0718550d, -118.3814818d, 20.0f, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT));
    }

    public BasicGeofence get(int i) {
        if(i < 1 || i > basicGeofences.size()) {
            return null;
        }

        // 1-based index
        return basicGeofences.get(i-1);
    }

    @Override
    public List<Geofence> buildGeofences() {

        Collection<Geofence> geofences = Collections2.transform(basicGeofences, new Function<BasicGeofence, Geofence>() {
            @Override
            public Geofence apply(BasicGeofence input) {
            return input.toGeofence();
            }
        });

        // TODO: For now until we know what we are going to do with them
        List<Geofence> returnList = new ArrayList<Geofence>();
        returnList.addAll(geofences);

        return returnList;
    }

    @Override
    public List<String> getGeofenceIds() {
        Collection<String> geofences = Collections2.transform(basicGeofences, new Function<BasicGeofence, String>() {
            @Override
            public String apply(BasicGeofence input) {
            return input.getId();
            }
        });

        // TODO: For now until we know what we are going to do with them
        List<String> returnList = new ArrayList<String>();
        returnList.addAll(geofences);

        return returnList;
    }

    @Override
    public void onAddGeofencesResult(int status, String[] strings) {
        switch(status) {
            case LocationStatusCodes.SUCCESS:
                Log.d(Constants.LOG_TAG, "Add geofences success. ");
                break;
            case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE:
                Log.d(Constants.LOG_TAG, "Unable to add geofences. Not available.");
                break;
            case LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                Log.d(Constants.LOG_TAG, "Unable to add geofences. Too many geofences. ");
                break;
            case LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                Log.d(Constants.LOG_TAG, "Unable to add geofences. Too many intents.");
                break;
            default:
                // mainly LocationStatusCodes.ERROR
                Log.d(Constants.LOG_TAG, "Add geofences error. (" + status + ")");
        }
        Log.d(Constants.LOG_TAG, Arrays.toString(strings));
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int status, String[] strings) {
        switch(status) {
            case LocationStatusCodes.SUCCESS:
                Log.d(Constants.LOG_TAG, "Remove geofences success. ");
                break;
            case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE:
                Log.d(Constants.LOG_TAG, "Unable to remove geofences. Not available.");
                break;
            default:
                // mainly LocationStatusCodes.ERROR
                Log.d(Constants.LOG_TAG, "Remove geofences error. (" + status + ")");
        }
        Log.d(Constants.LOG_TAG, Arrays.toString(strings));
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int status, PendingIntent pendingIntent) {
        Log.d(Constants.LOG_TAG, "Remove geofences pending intent result (" + status + ")");
    }
}
