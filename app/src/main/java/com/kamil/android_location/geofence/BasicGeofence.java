package com.kamil.android_location.geofence;

import com.google.android.gms.location.Geofence;
import com.kamil.android_location.Constants;

public class BasicGeofence {

    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
//    private long mExpirationDuration;
    private int mTransitionType;
    private String description;

    public BasicGeofence(String geofenceId, String desc, double latitude, double longitude, float radius, int transition) {
        this.mId = geofenceId;
        this.description = desc;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
//        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }

    public Geofence toGeofence() {
        Geofence.Builder g =  new Geofence.Builder()
                .setRequestId(getId())
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(Geofence.NEVER_EXPIRE);

        if((mTransitionType & Geofence.GEOFENCE_TRANSITION_DWELL) == Geofence.GEOFENCE_TRANSITION_DWELL) {
            g.setLoiteringDelay(Constants.MILLIS_PER_SECOND * 10);
        }

        return g.build();
    }

    public String getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getRadius() {
        return mRadius;
    }

//    public long getExpirationDuration() {
//        return mExpirationDuration;
//    }

    public int getTransitionType() {
        return mTransitionType;
    }

    public String getDescription() {
        return description;
    }
}
