package com.sense360.android.location.tester.background.place;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class TurnDetector {

  private final int DEGREE_THRESHOLD = 50;
  private Logger LOCATION_LOG = LoggerFactory.getLogger(FarPlaceDetector.class);

  private int mTrailingWindowSize;
  private Queue<Integer> mDegrees;
  private int lastAngle;
  private Integer[] mAngleArray;

  public TurnDetector(int maxTimeWindow, int delay) {
    mTrailingWindowSize = maxTimeWindow / delay;
    mDegrees = new ArrayBlockingQueue<Integer>(mTrailingWindowSize);
    mAngleArray = new Integer[mTrailingWindowSize];
    reset();
  }

  public void reset() {
    mDegrees.clear();
    lastAngle = 0;
  }

  public void recordNewValue(Integer angle) {
    // if angles are different direction, restart
    if (lastAngle * angle < 0) {
      Log.d("TAG", "Change of direction reset");
      reset();
    } else if (mDegrees.size() == mTrailingWindowSize) {
      mDegrees.remove();
    }

    mDegrees.add(angle);
    if (angle != 0) {
      lastAngle = angle.intValue();
    }
  }

  public boolean hasTurned() {
    Arrays.fill(mAngleArray, 0);
    mAngleArray = mDegrees.toArray(mAngleArray);

    Log.d("TAG", Arrays.toString(mAngleArray));

    int sumAngle = 0;
    for (int i = 0; i < mDegrees.size(); i++) {
      sumAngle += mAngleArray[i];
      if (Math.abs(sumAngle) > DEGREE_THRESHOLD) {
        if (i > 3) {
          float time = (i + 1) * 0.5f;
          LOCATION_LOG.info("Turn Detected " + Math.abs(sumAngle) + " degrees (" + Float.toString(time) + "s)");
          Log.d("Turn Detector", "Turn Detected " + Math.abs(sumAngle) + " degrees (" + Float.toString(time) + "s)");
          return true;
        } else {
          // too quick of a turn
          Log.d("Turn", "Too quick reset");
          reset();
        }
      }
    }

    return false;
  }
}
