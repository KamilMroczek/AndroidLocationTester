package com.sense360.android.location.tester.background.place;

import android.location.Location;

/**
 * Class is responsible for
 * kamilm (12/01/2014)
 */
public class Place {

  public final String mId;
  public final Location mLocation;
  public final String mName;

  public Place(String id, String name, Double latitude, Double longitude) {
    mLocation = new Location("Kamil");
    mLocation.setLatitude(latitude);
    mLocation.setLongitude(longitude);

    mName = name;
    mId = id;
  }

  public Location getLocation() {
    return mLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Place)) {
      return false;
    }

    Place place = (Place) o;

    if (mId != null ? !mId.equals(place.mId) : place.mId != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return mId != null ? mId.hashCode() : 0;
  }
}
