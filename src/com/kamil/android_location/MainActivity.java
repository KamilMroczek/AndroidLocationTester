package com.kamil.android_location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final String LOG_TAG = "Main Activity";
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = Constants.MILLIS_PER_SECOND * 10;
    private static final long FASTEST_INTERVAL = Constants.MILLIS_PER_SECOND * 10;

    private GooglePlayHelper mGooglePlayHelper;
    private boolean mServicesConnected;
    
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    
    TextView txtTime;
    TextView txtAccuracy;
    TextView txtProvider;
    TextView txtLatitude;
    TextView txtLongitude;
    TextView txtBearing;
    TextView txtSpeed;
    TextView txtAltitude;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupControls();

        mGooglePlayHelper = new GooglePlayHelper();
        
        mServicesConnected = mGooglePlayHelper.servicesConnected(this);
        if (mServicesConnected) {
			mLocationClient = new LocationClient(this, this, this);
			
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setInterval(UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		}
    }
    
    private void setupControls() {
    	txtTime = (TextView) findViewById(R.id.txtTime);
        txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
        txtProvider = (TextView) findViewById(R.id.txtProvider);
        
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        
        txtBearing = (TextView) findViewById(R.id.txtBearing);
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        txtAltitude = (TextView) findViewById(R.id.txtAltitude);
        
        Button btnStartBackground = (Button) findViewById(R.id.btnStartBackground);
        btnStartBackground.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startService(new Intent(v.getContext(),LocationBackgroundService.class));
			}
		});
        
        Button btnStopBackground = (Button) findViewById(R.id.btnStopBackground);
        btnStopBackground.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopService(new Intent(v.getContext(),LocationBackgroundService.class));
			}
		});
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

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // when disconnected nothing will not be pulling location, unless
    	// another app is pulling, so when you reconnect it could get an old location
		if (mServicesConnected) {
			if (mLocationClient.isConnected()) {
				mLocationClient.removeLocationUpdates(this);
			}

			mLocationClient.disconnect();
		}
        super.onStop();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
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

		Log.d(LOG_TAG, "Requesting Location Updates");
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
            Toast.makeText(this, "Location Service connection failed: " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
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
		
		txtProvider.setText(location.getProvider());
		txtAccuracy.setText(Float.toString(location.getAccuracy()));
		
		txtLatitude.setText(Double.toString(location.getLatitude()));
		txtLongitude.setText(Double.toString(location.getLongitude()));
		
		txtBearing.setText(Float.toString(location.getBearing()));
		txtSpeed.setText(Float.toString(location.getSpeed()));
		txtAltitude.setText(Double.toString(location.getAltitude()));
	}
}
