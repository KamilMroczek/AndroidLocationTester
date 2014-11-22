package com.kamil.android_location;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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
  private LocationClient mLocationClient;
  private LocationRequest mLocationRequest;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setupControls();

    mGooglePlayHelper = new GooglePlayHelper();
  }

  /*
   * Called when the Activity becomes visible.
   */
  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    GooglePlayHelper helper = new GooglePlayHelper();
    if (!helper.servicesConnected(this)) {
      GooglePlayServicesUtil.getErrorDialog(helper.servicesResult(this), this, 0).show();
    }

    connectLocationClient();

    super.onResume();

  }

  @Override
  protected void onPause() {
    disconnectLocationClient();

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
        setFusedProviderTextBox();

        // stop the service first if running. Then start a new one
        stopService(new Intent(v.getContext(), LocationBackgroundService.class));

        int refreshInterval = Integer.valueOf(txtRefreshInterval.getText().toString());
        int fusedProviderType = getFusedProviderType();
        startBackgroundService(v.getContext(), refreshInterval, fusedProviderType);

        restartUILocationUpdates();
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

  private void startBackgroundService(Context context, int refreshInterval, int fusedProviderType) {
    String note = txtNote.getText().toString();

    int checkedBackgroundButton = radioGroupBackgroundType.getCheckedRadioButtonId();
    String background_type = "geofence";
    if (checkedBackgroundButton == R.id.radioLocation) {
      background_type = "location";
    }

    Intent startIntent = new Intent(context, LocationBackgroundService.class);
    startIntent.putExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, fusedProviderType);
    startIntent.putExtra(Constants.REFRESH_INTERVAL_EXTRA, refreshInterval);
    startIntent.putExtra(Constants.NOTE_EXTRA, note);
    startIntent.putExtra(Constants.BACKGROUND_TYPE_EXTRA, background_type);

    startService(startIntent);
  }

  private int getFusedProviderType() {
    int checkedButton = radioGroupFusedProviderType.getCheckedRadioButtonId();

    switch (checkedButton) {
      case R.id.radioBalancedPower:
        return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
      case R.id.radioLowPower:
        return LocationRequest.PRIORITY_LOW_POWER;
      case R.id.radioNoPower:
        return LocationRequest.PRIORITY_NO_POWER;
      default: // high power
        return LocationRequest.PRIORITY_HIGH_ACCURACY;
    }
  }

  private void setFusedProviderTextBox() {
    int checkedButton = radioGroupFusedProviderType.getCheckedRadioButtonId();

    switch (checkedButton) {
      case R.id.radioBalancedPower:
        txtType.setText(Constants.BALANCED_POWER);
        break;
      case R.id.radioLowPower:
        txtType.setText(Constants.LOW_POWER);
        break;

      case R.id.radioNoPower:
        txtType.setText(Constants.NO_POWER);
        break;

      default: // high power
        txtType.setText(Constants.HIGH_ACCURACY);
        break;
    }
  }

  private void restartUILocationUpdates() {
    disconnectLocationClient();
    connectLocationClient();
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

  private void disconnectLocationClient() {
    // when disconnected nothing will not be pulling location, unless
    // another app is pulling, so when you reconnect it could get an old location
    if (locationClientConnected()) {
      mLocationClient.removeLocationUpdates(this);
      mLocationClient.disconnect();
    }

    mLocationClient = null;
  }

  private void connectLocationClient() {

    if (locationClientConnected()) {
      Log.d(LOG_TAG, "No Connecting location client because already connected");
      return;
    }

    boolean servicesConnected = mGooglePlayHelper.servicesConnected(this);
    if (servicesConnected) {
      int refreshInterval = Integer.valueOf(txtRefreshInterval.getText().toString());
      int fusedProviderType = getFusedProviderType();

      mLocationRequest = LocationRequest.create();
      mLocationRequest.setPriority(fusedProviderType);
      mLocationRequest.setInterval(refreshInterval * Constants.MILLIS_PER_SECOND);
      mLocationRequest.setFastestInterval(refreshInterval * Constants.MILLIS_PER_SECOND);

      mLocationClient = new LocationClient(this, this, this);
      mLocationClient.connect();
    }
  }

  private boolean locationClientConnected() {
    return mLocationClient != null && (mLocationClient.isConnected() || mLocationClient.isConnecting());
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

  // Define a DialogFragment that displays the error dialog
  public static class ErrorDialogFragment extends DialogFragment {

    // Global field to contain the error dialog
    private Dialog mDialog;

    public ErrorDialogFragment() {
      super();
      mDialog = null;
    }

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
