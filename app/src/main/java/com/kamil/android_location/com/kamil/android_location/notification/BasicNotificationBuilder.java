package com.kamil.android_location.com.kamil.android_location.notification;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.kamil.android_location.R;

public class BasicNotificationBuilder implements INotificationBuilder {

    private Context context;

    public BasicNotificationBuilder(Context context) {
        this.context = context;
    }

    @Override
    public Notification build(String title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text);

        return mBuilder.build();
    }
}
