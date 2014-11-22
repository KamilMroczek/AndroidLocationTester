package com.kamil.android_location;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.android.GooglePlayHelper;
import com.kamil.android_location.background.LocationBackgroundService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements
                                           GooglePlayServicesClient.ConnectionCallbacks,
                                           GooglePlayServicesClient.OnConnectionFailedListener,
                                           LocationListener {

  public static final String LOG_TAG = "Main Activity";
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  // Update frequency in milliseconds
  private static final long UPDATE_INTERVAL = Constants.MILLIS_PER_SECOND * Constants.DEFAULT_REFRESH_INTERVAL_SECS;
  TextView txtTime;
  TextView txtAccuracy;
  TextView txtLatitude;
  TextView txtLongitude;
  TextView txtSpeed;
  TextView txtAltitude;
  TextView txtType;
  EditText txtRefreshInterval;
  EditText txtNote;
  RadioGroup radioGroupFusedProviderType;
  RadioGroup radioGroupBackgroundType;
  private GooglePlayHelper mGooglePlayHelper;
  private boolean mServicesConnected;
  private LocationClient mLocationClient;
  private LocationRequest mLocationRequest;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setupControls();

    mGooglePlayHelper = new GooglePlayHelper();

    //        connectLocationClient(LocationRequest.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL, false);
  }

  /*
   * Called when the Activity becomes visible.
   */
  @Override
  protected void onStart() {
    super.onStart();
    if (mServicesConnected) {
      mLocationClient.connect();
    }
  }

  @Override
  protected void onResume() {
    GooglePlayHelper helper = new GooglePlayHelper();
    if (!helper.servicesConnected(this)) {
      GooglePlayServicesUtil.getErrorDialog(helper.servicesResult(this), this, 0).show();
    }
    super.onResume();

  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  /*
   * Called when the Activity is no longer visible.
   */
  @Override
  protected void onStop() {
    disconnectLocationClient();
    super.onStop();
  }

  private void setupControls() {
    txtTime = (TextView) findViewById(R.id.txtTime);
    txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);

    txtLatitude = (TextView) findViewById(R.id.txtLatitude);
    txtLongitude = (TextView) findViewById(R.id.txtLongitude);

    txtSpeed = (TextView) findViewById(R.id.txtSpeed);
    txtAltitude = (TextView) findViewById(R.id.txtAltitude);

    txtAltitude = (TextView) findViewById(R.id.txtAltitude);

    txtRefreshInterval = (EditText) findViewById(R.id.txtRefreshInterval);
    txtRefreshInterval.setText(Integer.toString(Constants.DEFAULT_REFRESH_INTERVAL_SECS));

    txtType = (TextView) findViewById(R.id.txtType);
    txtType.setText(Constants.HIGH_ACCURACY);

    txtNote = (EditText) findViewById(R.id.txtNote);

    radioGroupFusedProviderType = (RadioGroup) findViewById(R.id.radioFusedProviderType);
    radioGroupFusedProviderType.check(R.id.radioHighAccuracy);

    radioGroupBackgroundType = (RadioGroup) findViewById(R.id.radioGroupBackgroundType);

    Button btnStartBackground = (Button) findViewById(R.id.btnStartBackground);
    btnStartBackground.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // stop the service first if running. Then start a new one
        stopService(new Intent(v.getContext(), LocationBackgroundService.class));

        int refreshInterval = Integer.valueOf(txtRefreshInterval.getText().toString());

        int checkedButton = radioGroupFusedProviderType.getCheckedRadioButtonId();

        int fusedProviderType;
        switch (checkedButton) {
          case R.id.radioBalancedPower:
            fusedProviderType = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            txtType.setText(Constants.BALANCED_POWER);
            break;

          case R.id.radioLowPower:
            fusedProviderType = LocationRequest.PRIORITY_LOW_POWER;
            txtType.setText(Constants.LOW_POWER);
            break;

          case R.id.radioNoPower:
            fusedProviderType = LocationRequest.PRIORITY_NO_POWER;
            txtType.setText(Constants.NO_POWER);
            break;

          default: // high power
            fusedProviderType = LocationRequest.PRIORITY_HIGH_ACCURACY;
            txtType.setText(Constants.HIGH_ACCURACY);
            break;
        }

        String note = txtNote.getText().toString();

        int checkedBackgroundButton = radioGroupBackgroundType.getCheckedRadioButtonId();
        String background_type = "geofence";
        if (checkedBackgroundButton == R.id.radioLocation) {
          background_type = "location";
        }

        Intent startIntent = new Intent(v.getContext(), LocationBackgroundService.class);
        startIntent.putExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, fusedProviderType);
        startIntent.putExtra(Constants.REFRESH_INTERVAL_EXTRA, refreshInterval);
        startIntent.putExtra(Constants.NOTE_EXTRA, note);
        startIntent.putExtra(Constants.BACKGROUND_TYPE_EXTRA, background_type);

        startService(startIntent);

        restartUILocationUpdates(fusedProviderType, refreshInterval);
      }
    });

    Button btnStopBackground = (Button) findViewById(R.id.btnStopBackground);
    btnStopBackground.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        disconnectLocationClient();
        stopService(new Intent(v.getContext(), LocationBackgroundService.class));
      }
    });
  }

  private void restartUILocationUpdates(int fusedProviderType, int refreshIntervalSecs) {
    disconnectLocationClient();
    connectLocationClient(fusedProviderType, (long) refreshIntervalSecs, true);
  }

  /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
  @Override
  public void onConnected(Bundle dataBundle) {
    // Display the connection status
    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    Log.d(LOG_TAG, "Connected Location Client");

    Log.d(LOG_TAG, "Requesting Location Updates.");
    mLocationClient.requestLocationUpdates(mLocationRequest, this);
  }

  /*
   * Called by Location Services if the connection to the
   * location client drops because of an error.
   */
  @Override
  public void onDisconnected() {
    // Display the connection status
    Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    Log.d(LOG_TAG, "Disconnected Location Client");
  }

  /*
   * Called by Location Services if the attempt to
   * Location Services fails.
   */
  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
    Log.d(LOG_TAG, "Connection to Location Client FAILED!");
    if (connectionResult.hasResolution()) {
      try {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
      } catch (IntentSender.SendIntentException e) {
        // Log the error
        e.printStackTrace();
      }
    } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
      Toast.makeText(this, "Location Service connection failed: " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT)
           .show();
    }
  }

  @Override
  public void onLocationChanged(Location newLocation) {
    Log.d(LOG_TAG, "Received Location Update");
    if (newLocation != null) {
      Toast.makeText(this, "New Location", Toast.LENGTH_SHORT).show();
      updateView(newLocation);
    } else {
      Toast.makeText(this, "Empty Location", Toast.LENGTH_SHORT).show();
    }
  }

  private void updateView(Location location) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    Date date = new Date(location.getTime());
    txtTime.setText(sdf.format(date));

    txtAccuracy.setText(Float.toString(location.getAccuracy()));

    txtLatitude.setText(Double.toString(location.getLatitude()));
    txtLongitude.setText(Double.toString(location.getLongitude()));

    txtSpeed.setText(Float.toString(location.getSpeed()));
    txtAltitude.setText(Double.toString(location.getAltitude()));
  }

  private void disconnectLocationClient() {
    // when disconnected nothing will not be pulling location, unless
    // another app is pulling, so when you reconnect it could get an old location
    if (mServicesConnected) {
      if (mLocationClient.isConnected()) {
        mLocationClient.removeLocationUpdates(this);
      }

      mLocationClient.disconnect();
    }
  }

  private void connectLocationClient(int fusedLocationPriority, long refreshInterval, boolean connectImmediately) {
    mServicesConnected = mGooglePlayHelper.servicesConnected(this);
    if (mServicesConnected) {
      mLocationClient = new LocationClient(this, this, this);

      mLocationRequest = LocationRequest.create();
      mLocationRequest.setPriority(fusedLocationPriority);
      mLocationRequest.setInterval(refreshInterval * Constants.MILLIS_PER_SECOND);
      mLocationRequest.setFastestInterval(refreshInterval * Constants.MILLIS_PER_SECOND);

      if (connectImmediately) {
        mLocationClient.connect();
      }
    }
  }

  // Define a DialogFragment that displays the error dialog
  public static class ErrorDialogFragment extends DialogFragment {

    // Global field to contain the error dialog
    private Dialog mDialog;

    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment() {
      super();
      mDialog = null;
    }

    // Set the dialog to display
    public void setDialog(Dialog dialog) {
      mDialog = dialog;
    }

    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return mDialog;
    }
  }
}
