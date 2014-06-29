package com.kamil.android_location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.kamil.android_location.Constants;
import com.kamil.android_location.background.LocationBackgroundService;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Boot Service", "Received intent=" + intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Intent startIntent = new Intent(context, LocationBackgroundService.class);
            startIntent.putExtra(Constants.FUSED_PROVIDER_TYPE_EXTRA, LocationRequest.PRIORITY_HIGH_ACCURACY);
            startIntent.putExtra(Constants.REFRESH_INTERVAL_EXTRA, 10);
            startIntent.putExtra(Constants.NOTE_EXTRA, "");

            context.startService(startIntent);
        }
    }
}
