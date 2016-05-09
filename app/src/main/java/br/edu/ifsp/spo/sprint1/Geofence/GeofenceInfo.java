package br.edu.ifsp.spo.sprint1.Geofence;

import com.google.android.gms.location.Geofence;

public class GeofenceInfo {
    final String mId;
    public final double mLatitude;
    public final double mLongitude;
    public final float mRadius;
    long mExpirationDuration;
    int mTransitionType;
    public GeofenceInfo(String geofenceId, double latitude, double longitude,
                        float radius, long expiration, int transition) {
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }
    public Geofence getGeofence()
    {
        return new Geofence.Builder()
                .setRequestId(mId)
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(mLatitude, mLongitude, mRadius)
                .setExpirationDuration(mExpirationDuration)
                .build();
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public String getmId() {
        return mId;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public float getmRadius() {
        return mRadius;
    }

    public int getmTransitionType() {
        return mTransitionType;
    }

    public void setmTransitionType(int mTransitionType) {
        this.mTransitionType = mTransitionType;
    }

    public long getmExpirationDuration() {
        return mExpirationDuration;
    }

    public void setmExpirationDuration(long mExpirationDuration) {
        this.mExpirationDuration = mExpirationDuration;
    }
}
