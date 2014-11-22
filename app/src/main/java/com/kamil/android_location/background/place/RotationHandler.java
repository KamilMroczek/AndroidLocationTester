package com.kamil.android_location.background.place;

/**
 * Class is responsible for
 * kamilm (12/04/2014)
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kamil.android_location.Constants;

import java.util.Arrays;

public class RotationHandler implements SensorEventListener {

  private static final long TURN_DETECTION_TIME = Constants.SECOND * 6;
  private final long MIN_ROTATION_GAP = Constants.SECOND / 2;
  private final long MIN_ACC_GAP = Constants.SECOND / 10;
  private Context mContext;
  private SensorManager mSensorManager;
  private Sensor mRotationSensor;
  private Sensor mAccelerometerSensor;
  private TurnDetector mTurnDetector;
  private long mLastRotationEvent;
  private long mLastAccelerometerEvent;
  private float[] mPreviousRotationMatrix;
  private float[] mCurrentRotationMatrix;
  private float[] mAngleChange;
  private int mTurningAxisIndex;

  public RotationHandler(Context context) {
    mContext = context;
    mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    mTurnDetector = new TurnDetector((int) TURN_DETECTION_TIME, (int) MIN_ROTATION_GAP);
    mCurrentRotationMatrix = new float[9];
    mAngleChange = new float[3];
  }

  public void startMonitoring() {
    reset();

    mSensorManager.registerListener(this, mRotationSensor, (int) MIN_ROTATION_GAP);
    mSensorManager.registerListener(this, mAccelerometerSensor, (int) MIN_ACC_GAP);
  }

  public void finishMonitoring() {
    mSensorManager.unregisterListener(this, mRotationSensor);
    mSensorManager.unregisterListener(this, mAccelerometerSensor);
  }

  private void reset() {
    Arrays.fill(mCurrentRotationMatrix, 0f);
    mPreviousRotationMatrix = null;
    Arrays.fill(mAngleChange, 0f);
    mLastRotationEvent = System.currentTimeMillis();
    mLastAccelerometerEvent = System.currentTimeMillis();
    mTurnDetector.reset();
    mTurningAxisIndex = 0;
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    switch (sensorEvent.sensor.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
        handleAccelerometer(sensorEvent.values);
        break;
      case Sensor.TYPE_ROTATION_VECTOR:
        if (mTurningAxisIndex > -1) {
          handleRotation(sensorEvent.values);
        }
        break;
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  private void handleAccelerometer(float[] values) {
    long now = System.currentTimeMillis();

    if (accelerometerEventTooQuick(now)) {
      return;
    }

    float largestAxis = Math.max(Math.max(Math.abs(values[0]), Math.abs(values[1])), Math.abs(values[2]));
    int newTurningAxisIndex = mTurningAxisIndex;

    // index into the rotation vector is z,x,y.
    if (largestAxis == Math.abs(values[0])) {
      newTurningAxisIndex = 1;
    } else if (largestAxis == Math.abs(values[1])) {
      newTurningAxisIndex = 2;
    } else if (largestAxis == Math.abs(values[2])) {
      newTurningAxisIndex = 0;
    }

    if (newTurningAxisIndex != mTurningAxisIndex) {
      mTurningAxisIndex = newTurningAxisIndex;
      mPreviousRotationMatrix = null;
      //      Log.d("TAG", "Change axis reset");
      mTurnDetector.reset();
    }
  }

  private void handleRotation(float[] values) {
    long now = System.currentTimeMillis();
    if (rotationEventTooQuick(now)) {
      return;
    }

    mLastRotationEvent = now;
    calculateRotation(values);

    if (mTurnDetector.hasTurned()) {
      //      Log.d("TAG", "Detected turn reset");
      mTurnDetector.reset();
    }
  }

  private void calculateRotation(float[] newValues) {
    SensorManager.getRotationMatrixFromVector(mCurrentRotationMatrix, newValues);
    if (mPreviousRotationMatrix != null) {
      SensorManager.getAngleChange(mAngleChange, mCurrentRotationMatrix, mPreviousRotationMatrix);

      int rotation = (int) Math.round(Math.toDegrees(mAngleChange[mTurningAxisIndex]));
      //      Log.d("TAG", "W=" + Math.round(rotation) + "[" + mTurningAxisIndex + "]");
      mTurnDetector.recordNewValue(Math.round(rotation));
    }

    mPreviousRotationMatrix = mCurrentRotationMatrix.clone();
  }

  private boolean rotationEventTooQuick(long sensorTime) {
    long diff = sensorTime - mLastRotationEvent;
    return diff < MIN_ROTATION_GAP;
  }

  private boolean accelerometerEventTooQuick(long sensorTime) {
    long diff = sensorTime - mLastAccelerometerEvent;
    return diff < MIN_ACC_GAP;
  }
}
