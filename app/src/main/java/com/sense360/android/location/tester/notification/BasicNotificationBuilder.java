package com.sense360.android.location.tester.notification;

import com.sense360.android.location.tester.R;

import android.app.Notification;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class BasicNotificationBuilder implements INotificationBuilder {

  private Context context;

  public BasicNotificationBuilder(Context context) {
    this.context = context;
  }

  @Override
  public Notification build(String title, String text) {
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(title)
        .setContentText(text)
        .setSound(alarmSound)
        .setDefaults(Notification.DEFAULT_VIBRATE);


    return mBuilder.build();
  }
}
